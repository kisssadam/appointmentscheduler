package hu.smartcampus.appointmentscheduler.domain;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

public class User implements Cloneable, Comparable<User>, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Collator hungarianCollator = Collator.getInstance(new Locale("hu", "HU"));
	private String displayName;
	private String loginName;
	private boolean skippable;
	
	public User() {
	}

	public User(String displayName, String loginName, boolean skippable) {
		super();
		this.displayName = displayName;
		this.loginName = loginName;
		this.skippable = skippable;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getLoginName() {
		return this.loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	public boolean isSkippable() {
		return this.skippable;
	}

	public void setSkippable(boolean skippable) {
		this.skippable = skippable;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.displayName == null) ? 0 : this.displayName.hashCode());
		result = prime * result + ((this.loginName == null) ? 0 : this.loginName.hashCode());
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
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
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
		builder.append("User [displayName=");
		builder.append(this.displayName);
		builder.append(", loginName=");
		builder.append(this.loginName);
		builder.append("]");
		return builder.toString();
	}

	@Override
	protected User clone() {
		return new User(this.displayName, this.loginName, this.skippable);
	}

	@Override
	public int compareTo(User otherUser) {
		int result = hungarianCollator.compare(this.displayName, otherUser.displayName);
		if (result == 0) {
			result = hungarianCollator.compare(this.loginName, otherUser.loginName);
		}
		return result;
	}

}
