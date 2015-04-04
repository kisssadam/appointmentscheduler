package hu.smartcampus.appointmentscheduler.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.List;

/**
 * The persistent class for the T_EVENT database table.
 * 
 */
@Entity
@Table(name = "T_EVENT")
@NamedQuery(name = "TEvent.findAll", query = "SELECT t FROM TEvent t")
public class TEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	private long eventId;
	private String description;
	private Timestamp eventEnd;
	private Timestamp eventStart;
	private String title;
	private List<TCategory> TCategories;
	private List<TAdmin> TAdmins;
	private List<TLocation> TLocations;
	private List<TUser> TUsers;
	private List<TMessage> TMessages;

	public TEvent() {
	}

	@Id
	@Column(name = "EVENT_ID", unique = true, nullable = false, precision = 10)
	public long getEventId() {
		return this.eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	@Column(nullable = false, length = 1000)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "EVENT_END", nullable = false)
	public Timestamp getEventEnd() {
		return this.eventEnd;
	}

	public void setEventEnd(Timestamp eventEnd) {
		this.eventEnd = eventEnd;
	}

	@Column(name = "EVENT_START", nullable = false)
	public Timestamp getEventStart() {
		return this.eventStart;
	}

	public void setEventStart(Timestamp eventStart) {
		this.eventStart = eventStart;
	}

	@Column(nullable = false, length = 200)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	// bi-directional many-to-many association to TCategory
	@ManyToMany
	@JoinTable(name = "T_EVENT_CATEGORY", joinColumns = { @JoinColumn(name = "EVENT_ID", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "CATEGORY_ID", nullable = false) })
	public List<TCategory> getTCategories() {
		return this.TCategories;
	}

	public void setTCategories(List<TCategory> TCategories) {
		this.TCategories = TCategories;
	}

	// bi-directional many-to-many association to TAdmin
	@ManyToMany
	@JoinTable(name = "T_EVENT_EDITOR", joinColumns = { @JoinColumn(name = "EVENT_ID", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "USER_ID", nullable = false) })
	public List<TAdmin> getTAdmins() {
		return this.TAdmins;
	}

	public void setTAdmins(List<TAdmin> TAdmins) {
		this.TAdmins = TAdmins;
	}

	// bi-directional many-to-many association to TLocation
	@ManyToMany(mappedBy = "TEvents")
	public List<TLocation> getTLocations() {
		return this.TLocations;
	}

	public void setTLocations(List<TLocation> TLocations) {
		this.TLocations = TLocations;
	}

	// bi-directional many-to-many association to TUser
	@ManyToMany(mappedBy = "TEvents")
	public List<TUser> getTUsers() {
		return this.TUsers;
	}

	public void setTUsers(List<TUser> TUsers) {
		this.TUsers = TUsers;
	}

	// bi-directional many-to-one association to TMessage
	@OneToMany(mappedBy = "TEvent")
	public List<TMessage> getTMessages() {
		return this.TMessages;
	}

	public void setTMessages(List<TMessage> TMessages) {
		this.TMessages = TMessages;
	}

	public TMessage addTMessage(TMessage TMessage) {
		getTMessages().add(TMessage);
		TMessage.setTEvent(this);

		return TMessage;
	}

	public TMessage removeTMessage(TMessage TMessage) {
		getTMessages().remove(TMessage);
		TMessage.setTEvent(null);

		return TMessage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
		result = prime * result + ((this.eventEnd == null) ? 0 : this.eventEnd.hashCode());
		result = prime * result + ((this.eventStart == null) ? 0 : this.eventStart.hashCode());
		result = prime * result + ((this.title == null) ? 0 : this.title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TEvent)) {
			return false;
		}
		TEvent other = (TEvent) obj;
		if (this.description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!this.description.equals(other.description)) {
			return false;
		}
		if (this.eventEnd == null) {
			if (other.eventEnd != null) {
				return false;
			}
		} else if (!this.eventEnd.equals(other.eventEnd)) {
			return false;
		}
		if (this.eventStart == null) {
			if (other.eventStart != null) {
				return false;
			}
		} else if (!this.eventStart.equals(other.eventStart)) {
			return false;
		}
		if (this.title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!this.title.equals(other.title)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TEvent [description=");
		builder.append(this.description);
		builder.append(", eventEnd=");
		builder.append(this.eventEnd);
		builder.append(", eventStart=");
		builder.append(this.eventStart);
		builder.append(", title=");
		builder.append(this.title);
		builder.append("]");
		return builder.toString();
	}

}