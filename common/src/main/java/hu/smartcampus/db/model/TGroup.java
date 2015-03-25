package hu.smartcampus.db.model;

import java.io.Serializable;

import javax.persistence.*;

import java.util.List;

/**
 * The persistent class for the T_GROUP database table.
 * 
 */
@Entity
@Table(name = "T_GROUP")
@NamedQuery(name = "TGroup.findAll", query = "SELECT t FROM TGroup t")
public class TGroup implements Serializable {
	private static final long serialVersionUID = 1L;
	private long groupId;
	private String groupName;
	private List<TAdmin> TAdmins;

	public TGroup() {
	}

	@Id
	@Column(name = "GROUP_ID", unique = true, nullable = false, precision = 10)
	public long getGroupId() {
		return this.groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	@Column(name = "GROUP_NAME", nullable = false, length = 100)
	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	// bi-directional many-to-many association to TAdmin
	@ManyToMany
	@JoinTable(name = "T_GROUP_MEMBER", joinColumns = { @JoinColumn(name = "GROUP_ID", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "USER_ID", nullable = false) })
	public List<TAdmin> getTAdmins() {
		return this.TAdmins;
	}

	public void setTAdmins(List<TAdmin> TAdmins) {
		this.TAdmins = TAdmins;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.groupName == null) ? 0 : this.groupName.hashCode());
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
		if (!(obj instanceof TGroup)) {
			return false;
		}
		TGroup other = (TGroup) obj;
		if (this.groupName == null) {
			if (other.groupName != null) {
				return false;
			}
		} else if (!this.groupName.equals(other.groupName)) {
			return false;
		}
		return true;
	}

}