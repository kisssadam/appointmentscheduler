package hu.smartcampus.appointmentscheduler.domain;

import hu.smartcampus.db.model.TEvent;
import hu.smartcampus.db.model.TUser;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.solution.cloner.PlanningCloneable;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PlanningSolution
public class EventSchedule implements Solution<HardMediumSoftScore>, PlanningCloneable<EventSchedule> {

    /*
     * TODO at kellene alakitani LocalDateTime-ra az entitiket ez alapjan:
     * https://weblogs.java.net/blog/montanajava/archive/2014/06/17/using-java-8-datetime-classes-jpa
     */
    private static final Logger logger = LoggerFactory.getLogger(EventSchedule.class);
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY;
    private static final ZoneId BUDAPEST_ZONE_ID;
    private static final DateTimeFormatter DAY_DATE_TIME_FORMATTER;
    private static final DateTimeFormatter WEEK_DATE_TIME_FORMATER;
    private static final DateTimeFormatter YEAR_DATE_TIME_FORMATER;
    private static final DateTimeFormatter HOUR_DATE_TIME_FORMATTER;
    private static final DateTimeFormatter MINUTE_DATE_TIME_FORMATTER;

    private EntityManager entityManager;
    private List<String> requiredLoginNames;
    private List<String> skippableLoginNames;
    private List<String> mergedLoginNames;
    private List<DayOfWeek> daysOfWeek;
    private int year;
    private int weekOfYear;
    private int minHour;
    private int maxHour;
    private List<User> users;
    private List<Event> events;
    private HardMediumSoftScore score;

    static {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("SMARTCAMPUS");

        BUDAPEST_ZONE_ID = ZoneId.of("Europe/Budapest");

        DAY_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EEEE");
        WEEK_DATE_TIME_FORMATER = DateTimeFormatter.ofPattern("w");
        YEAR_DATE_TIME_FORMATER = DateTimeFormatter.ofPattern("yyyy");
        HOUR_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("H");
        MINUTE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("m");
    }

    public static EventSchedule createEventSchedule(String[] requiredLoginNames, String[] skippableLoginNames, DayOfWeek[] daysOfWeek, int year, int weekOfYear, int minHour, int maxHour) {
        return new EventSchedule(requiredLoginNames, skippableLoginNames, daysOfWeek, year, weekOfYear, minHour, maxHour);
    }

    private EventSchedule() {
        super();
    }

    private EventSchedule(String[] requiredLoginNames, String[] skippableLoginNames, DayOfWeek[] daysOfWeek, int year, int weekOfYear, int minHour, int maxHour) {
        this.entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();

        this.requiredLoginNames = Arrays.stream(requiredLoginNames).distinct().collect(Collectors.toList());
        this.skippableLoginNames = Arrays.stream(skippableLoginNames).filter(loginName -> !this.requiredLoginNames.contains(loginName)).distinct().collect(Collectors.toList());
        this.mergedLoginNames = Stream.concat(this.requiredLoginNames.stream(), this.skippableLoginNames.stream()).distinct().collect(Collectors.toList());

        this.daysOfWeek = Arrays.asList(daysOfWeek);
        this.year = year;
        this.weekOfYear = weekOfYear;
        this.minHour = minHour;
        this.maxHour = maxHour;

        List<TUser> queriedTUsers = queryTUsers(mergedLoginNames);
        this.users = createUsersFromTUsers(queriedTUsers);

        List<TEvent> everyTEvent = getEveryTEventFromTUsers(queriedTUsers);
        this.events = createEventsFromTEvents(everyTEvent);

        // Add conflicting events that should be moved by the algorithm
        DayOfWeek conflictingDay = this.daysOfWeek.isEmpty() ? DayOfWeek.MONDAY : this.daysOfWeek.get(0);
        Timeslot conflictingTimeslot = this.events.isEmpty() ? new Timeslot(this.minHour) : this.events.get(0).getPeriod().getTimeslot();
        Period conflictingPeriod = new Period(conflictingDay, conflictingTimeslot);
        boolean isLocked = false;

        this.events.add(new Event("Movable event", conflictingPeriod, this.users, isLocked));
    }

    private List<TUser> queryTUsers(List<String> loginNames) {
        TypedQuery<TUser> query = entityManager.createNamedQuery("TUser.findByLoginName", TUser.class);
        query.setParameter("loginNames", loginNames);
        
        return query.getResultList();
    }

    private List<TEvent> getEveryTEventFromTUsers(List<TUser> queriedTUsers) {
        return queriedTUsers.stream()
                            .flatMap(tUser -> new ArrayList<>(tUser.getTEvents()).stream())
                            .distinct()
                            .collect(Collectors.toList());
    }

    private List<User> createUsersFromTUsers(List<TUser> queriedTUsers) {
        return queriedTUsers.stream().map(tUser -> {
            boolean isSkippable = !this.requiredLoginNames.contains(tUser.getLoginName());
            String displayName = tUser.getDisplayName();
            String loginName = tUser.getLoginName();
            
            return new User(displayName, loginName, isSkippable);
        })
        .distinct()
        .sorted()
        .collect(Collectors.toList());
    }

    private List<Event> createEventsFromTEvents(List<TEvent> everyTEvent) {
        logger.trace("Queried TEvents are: {}.", everyTEvent);
        List<? super Period> possiblePeriods = getPossiblePeriods();
        List<Event> result = everyTEvent
                .stream()
                .filter(tEvent -> Integer.parseInt(tEvent.getEventStart().toInstant().atZone(BUDAPEST_ZONE_ID).format(YEAR_DATE_TIME_FORMATER)) == this.year)
                .filter(tEvent -> Integer.parseInt(tEvent.getEventStart().toInstant().atZone(BUDAPEST_ZONE_ID).format(WEEK_DATE_TIME_FORMATER)) == this.weekOfYear)
                .filter(tEvent -> this.daysOfWeek.contains(DayOfWeek.valueOf(tEvent.getEventStart().toInstant().atZone(BUDAPEST_ZONE_ID).format(DAY_DATE_TIME_FORMATTER).toUpperCase())))
                .flatMap(new Function<TEvent, Stream<? extends Event>>() {

                    @Override
                    public Stream<? extends Event> apply(TEvent tEvent) {
                        Timestamp eventStart = tEvent.getEventStart();
                        Timestamp eventEnd = tEvent.getEventEnd();

                        String title = tEvent.getTitle();
                        List<User> usersOfEvent = getRequiredUsersOfTEvent(tEvent);
                        boolean locked = true;

                        List<Period> periods = createPeriodsFromTimestamps(eventStart, eventEnd);
                        return periods.stream().map(period -> new Event(title, period, usersOfEvent, locked));
                    }
                })
                .filter(event -> possiblePeriods.contains(event.getPeriod()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        logger.trace("Created Events from TEvents are: {}", result);
        return result;
    }
    

    private List<User> getRequiredUsersOfTEvent(TEvent tEvent) {
        return new ArrayList<>(tEvent.getTUsers())
                .stream()
                .filter(tUser -> this.mergedLoginNames.contains(tUser.getLoginName()))
                .map(tUser -> getUserByLoginName(tUser.getLoginName()))
                .distinct()
                .collect(Collectors.toList());
    }
    

    private User getUserByLoginName(String loginName) {
        Optional<User> result = this.users.stream().filter(user -> user.getLoginName().equals(loginName)).findFirst();
        return result.isPresent() ? result.get() : null;
    }

    private List<Period> createPeriodsFromTimestamps(Timestamp eventStart, Timestamp eventEnd) {
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(eventStart.toInstant().atZone(BUDAPEST_ZONE_ID).format(DAY_DATE_TIME_FORMATTER).toUpperCase());

        int rangeMinValue = Integer.parseInt(eventStart.toInstant().atZone(BUDAPEST_ZONE_ID).format(HOUR_DATE_TIME_FORMATTER));
        int rangeMaxValue = Integer.parseInt(eventEnd.toInstant().atZone(BUDAPEST_ZONE_ID).format(HOUR_DATE_TIME_FORMATTER));
        
        int eventEndMinute = Integer.parseInt(eventStart.toInstant().atZone(BUDAPEST_ZONE_ID).format(MINUTE_DATE_TIME_FORMATTER));

        if (rangeMinValue == rangeMaxValue || eventEndMinute != 0) {
            rangeMaxValue++;
        }

        return IntStream.range(rangeMinValue, rangeMaxValue)
                .mapToObj(hour -> new Period(dayOfWeek, new Timeslot(hour)))
                .distinct()
                .collect(Collectors.toList());
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }
    
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public List<String> getRequiredLoginNames() {
        return this.requiredLoginNames;
    }

    public void setRequiredLoginNames(List<String> requiredLoginNames) {
        this.requiredLoginNames = requiredLoginNames;
    }

    public List<String> getSkippableLoginNames() {
        return this.skippableLoginNames;
    }

    public void setSkippableLoginNames(List<String> skippableLoginNames) {
        this.skippableLoginNames = skippableLoginNames;
    }

    public List<String> getMergedLoginNames() {
        return this.mergedLoginNames;
    }

    public void setMergedLoginNames(List<String> mergedLoginNames) {
        this.mergedLoginNames = mergedLoginNames;
    }

    public List<DayOfWeek> getDaysOfWeek() {
        return this.daysOfWeek;
    }

    public void setDaysOfWeek(List<DayOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getWeekOfYear() {
        return this.weekOfYear;
    }

    public void setWeekOfYear(int weekOfYear) {
        this.weekOfYear = weekOfYear;
    }

    public int getMinHour() {
        return this.minHour;
    }

    public void setMinHour(int minHour) {
        this.minHour = minHour;
    }

    public int getMaxHour() {
        return this.maxHour;
    }

    public void setMaxHour(int maxHour) {
        this.maxHour = maxHour;
    }

    public List<User> getUsers() {
        return this.users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
    
    @PlanningEntityCollectionProperty
    public List<Event> getEvents() {
        return this.events;
    }
    
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public HardMediumSoftScore getScore() {
        return this.score;
    }

    @Override
    public void setScore(HardMediumSoftScore score) {
        this.score = score;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<? extends Object> getProblemFacts() {
        return null;
    }

    @ValueRangeProvider(id = "periodRange")
    public List<Period> getPossiblePeriods() {
        List<Timeslot> possibleTimeslots = getPossibleTimeslots();
        List<Period> possiblePeriods = new ArrayList<>(daysOfWeek.size() * possibleTimeslots.size());
        
        this.daysOfWeek.forEach(dayOfWeek -> {
            possibleTimeslots.forEach(timeslot -> {
                possiblePeriods.add(new Period(dayOfWeek, timeslot));
            });
        });
        
        logger.trace("Possible periods are {}.", possiblePeriods);
        return possiblePeriods;
    }

    private List<Timeslot> getPossibleTimeslots() {
        return IntStream.rangeClosed(this.minHour, this.maxHour).mapToObj(hour -> new Timeslot(hour)).distinct().collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EventSchedule [events=");
        builder.append(this.events);
        builder.append(", score=");
        builder.append(this.score);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Clone will only deep copy the {@link #events}.
     */
    @Override
    public EventSchedule planningClone() {
        EventSchedule clone = new EventSchedule();
        
        clone.setRequiredLoginNames(this.requiredLoginNames);
        clone.setSkippableLoginNames(this.skippableLoginNames);
        clone.setMergedLoginNames(this.mergedLoginNames);
        clone.setDaysOfWeek(this.daysOfWeek);
        clone.setYear(this.year);
        clone.setWeekOfYear(this.weekOfYear);
        clone.setMinHour(this.minHour);
        clone.setMaxHour(this.maxHour);
        clone.setUsers(this.users);
        clone.setEvents(this.events.stream().map(event -> event.clone()).collect(Collectors.toList()));
        clone.setScore(this.score);

        return clone;
    }

}