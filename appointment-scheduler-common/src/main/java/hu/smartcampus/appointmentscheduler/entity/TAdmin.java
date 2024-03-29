package hu.smartcampus.appointmentscheduler.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.util.List;

/**
 * The persistent class for the T_ADMIN database table.
 * 
 */
@Entity
@Table(name = "T_ADMIN")
@NamedQuery(name = "TAdmin.findAll", query = "SELECT t FROM TAdmin t")
public class TAdmin implements Serializable {
	private static final long serialVersionUID = 1L;
	private long userId;
	private TUser TUser;
	private List<TEvent> TEvents;
	private List<TGroup> TGroups;

	public TAdmin() {
	}

	@Id
	@Column(name = "USER_ID", unique = true, nullable = false, precision = 10)
	public long getUserId() {
		return this.userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	// bi-directional one-to-one association to TUser
	@OneToOne
	@JoinColumn(name = "USER_ID", nullable = false, insertable = false, updatable = false)
	public TUser getTUser() {
		return this.TUser;
	}

	public void setTUser(TUser TUser) {
		this.TUser = TUser;
	}

	// bi-directional many-to-many association to TEvent
	@ManyToMany(mappedBy = "TAdmins")
	public List<TEvent> getTEvents() {
		return this.TEvents;
	}

	public void setTEvents(List<TEvent> TEvents) {
		this.TEvents = TEvents;
	}

	// bi-directional many-to-many association to TGroup
	@ManyToMany(mappedBy = "TAdmins")
	public List<TGroup> getTGroups() {
		return this.TGroups;
	}

	public void setTGroups(List<TGroup> TGroups) {
		this.TGroups = TGroups;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.TUser == null) ? 0 : this.TUser.hashCode());
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
		if (!(obj instanceof TAdmin)) {
			return false;
		}
		TAdmin other = (TAdmin) obj;
		if (this.TUser == null) {
			if (other.TUser != null) {
				return false;
			}
		} else if (!this.TUser.equals(other.TUser)) {
			return false;
		}
		return true;
	}

}