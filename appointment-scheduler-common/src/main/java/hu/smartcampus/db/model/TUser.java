package hu.smartcampus.db.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * The persistent class for the T_USER database table.
 * 
 */
@Entity
@Table(name = "T_USER")
@NamedQueries({
		@NamedQuery(name = "TUser.findAll", query = "SELECT t FROM TUser t"),
		@NamedQuery(name = "TUser.findByLoginName", query = "SELECT t FROM TUser t WHERE t.loginName IN :loginNames") })
public class TUser implements Comparable<TUser>, Serializable {
	private static final long serialVersionUID = 1L;
	private static final Collator hungarianCollator = Collator.getInstance(new Locale("hu", "HU"));
	private long userId;
	private String displayName;
	private String loginName;
	private String smartPassword;
	private TAdmin TAdmin;
	private List<TEvent> TEvents;
	private List<TMessage> TMessages;

	public TUser() {
	}

	@Id
	@Column(name = "USER_ID", unique = true, nullable = false, precision = 10)
	public long getUserId() {
		return this.userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	@Column(name = "DISPLAY_NAME", nullable = false, length = 256)
	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Column(name = "LOGIN_NAME", nullable = false, length = 256)
	public String getLoginName() {
		return this.loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@Column(name = "SMART_PASSWORD", length = 64)
	public String getSmartPassword() {
		return this.smartPassword;
	}

	public void setSmartPassword(String smartPassword) {
		this.smartPassword = smartPassword;
	}

	// bi-directional one-to-one association to TAdmin
	@OneToOne(mappedBy = "TUser")
	public TAdmin getTAdmin() {
		return this.TAdmin;
	}

	public void setTAdmin(TAdmin TAdmin) {
		this.TAdmin = TAdmin;
	}

	// bi-directional many-to-many association to TEvent
	@ManyToMany
	@JoinTable(name = "T_EVENT_PROVIDER", joinColumns = { @JoinColumn(name = "USER_ID", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "EVENT_ID", nullable = false) })
	public List<TEvent> getTEvents() {
		return this.TEvents;
	}

	public void setTEvents(List<TEvent> TEvents) {
		this.TEvents = TEvents;
	}

	// bi-directional many-to-one association to TMessage
	@OneToMany(mappedBy = "TUser")
	public List<TMessage> getTMessages() {
		return this.TMessages;
	}

	public void setTMessages(List<TMessage> TMessages) {
		this.TMessages = TMessages;
	}

	public TMessage addTMessage(TMessage TMessage) {
		getTMessages().add(TMessage);
		TMessage.setTUser(this);

		return TMessage;
	}

	public TMessage removeTMessage(TMessage TMessage) {
		getTMessages().remove(TMessage);
		TMessage.setTUser(null);

		return TMessage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.displayName == null) ? 0 : this.displayName.hashCode());
		result = prime * result
				+ ((this.loginName == null) ? 0 : this.loginName.hashCode());
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
		if (!(obj instanceof TUser)) {
			return false;
		}
		TUser other = (TUser) obj;
		if (this.displayName == null) {
			if (other.displayName != null) {
				return false;
			}
		} else if (!this.displayName.equals(other.displayName)) {
			return false;
		}
		if (this.loginName == null) {
			if (other.loginName != null) {
				return false;
			}
		} else if (!this.loginName.equals(other.loginName)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TUser [displayName=");
		builder.append(this.displayName);
		builder.append(", loginName=");
		builder.append(this.loginName);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(TUser otherTUser) {
		int result = hungarianCollator.compare(this.displayName, otherTUser.displayName);
		if (result == 0) {
			result = hungarianCollator.compare(this.loginName, otherTUser.loginName);
		}
		return result;
	}

}