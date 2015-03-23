package hu.smartcampus.db.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

/**
 * The persistent class for the T_LOCATION database table.
 * 
 */
@Entity
@Table(name = "T_LOCATION")
@NamedQuery(name = "TLocation.findAll", query = "SELECT t FROM TLocation t")
public class TLocation implements Serializable {
	private static final long serialVersionUID = 1L;
	private long locationId;
	private String locationName;
	private List<TEvent> TEvents;

	public TLocation() {
	}

	@Id
	@Column(name = "LOCATION_ID", unique = true, nullable = false, precision = 10)
	public long getLocationId() {
		return this.locationId;
	}

	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}

	@Column(name = "LOCATION_NAME", nullable = false, length = 200)
	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	// bi-directional many-to-many association to TEvent
	@ManyToMany
	@JoinTable(name = "T_EVENT_LOCATION", joinColumns = { @JoinColumn(name = "LOCATION_ID", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "EVENT_ID", nullable = false) })
	public List<TEvent> getTEvents() {
		return this.TEvents;
	}

	public void setTEvents(List<TEvent> TEvents) {
		this.TEvents = TEvents;
	}

}