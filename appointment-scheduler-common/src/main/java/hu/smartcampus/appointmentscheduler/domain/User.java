package hu.smartcampus.appointmentscheduler.domain;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

/**
 * Describes a user.
 * 
 * @author adam
 */
public class User implements Cloneable, Comparable<User>, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * This {@link Collator} is used in the {@link #compareTo(User)} method. This makes it possible to order users
	 * using hungarian abc.
	 */
	private static final Collator hungarianCollator = Collator.getInstance(new Locale("hu", "HU"));

	/**
	 * The name of the {@link User}.
	 */
	private String displayName;

	/**
	 * The login name of the {@link User}.
	 */
	private String loginName;

	/**
	 * Indicates that is this user necessary to attend on the {@link Event} or not. If {@code true} then this
	 * {@link User} is not necessary to attend on the {@link Event}.
	 */
	private boolean skippable;

	/**
	 * Constructs an empty {@link User}.
	 */
	public User() {
		super();
	}

	/**
	 * Constructs a new {@link User} using the given arguments.
	 * 
	 * @param displayName the display name of the {@link User}
	 * @param loginName the login name of the {@link User}
	 * @param skippable Indicates that is this user necessary to attend on the {@link Event} or not. If {@code true}
	 *            then this {@link User} is not necessary to attend on the {@link Event}.
	 */
	public User(String displayName, String loginName, boolean skippable) {
		super();
		this.displayName = displayName;
		this.loginName = loginName;
		this.skippable = skippable;
	}

	/**
	 * Returns the display name of the {@link User}.
	 * 
	 * @return the display name of the {@link User}
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * Sets the display name of the {@link User}
	 * 
	 * @param displayName the new display name of the {@link User}
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Returns the login name of the {@link User}.
	 * 
	 * @return the login name of the {@link User}
	 */
	public String getLoginName() {
		return this.loginName;
	}

	/**
	 * Sets the login name of the {@link User}.
	 * 
	 * @param loginName the new login name of the {@link User}
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * Returns {@code true} if the {@link User} is not necessary to attend on the {@link Event}, {@code false}
	 * otherwise.
	 * 
	 * @return {@code true} if the {@link User} is not necessary to attend on the {@link Event}
	 */
	public boolean isSkippable() {
		return this.skippable;
	}

	/**
	 * Sets the {@link #skippable} field of the {@link User}.
	 * 
	 * @param skippable the new value of the {@link #skippable} field of the {@link User}
	 */
	public void setSkippable(boolean skippable) {
		this.skippable = skippable;
	}

	/**
	 * Returns the hash code value of the {@link User}.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.displayName == null) ? 0 : this.displayName.hashCode());
		result = prime * result + ((this.loginName == null) ? 0 : this.loginName.hashCode());
		return result;
	}

	/**
	 * Returns {@code true} if {@code obj} is equal to this {@link Period}. {@link User}s are equal if they have the
	 * same {@link #displayName}, {@link #loginName} and {@link #skippable} values.
	 */
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

	/**
	 * Returns the {@link String} representation of the {@link User}.
	 */
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

	/**
	 * Returns a shallow copy of this {@link User} instance.
	 */
	@Override
	protected User clone() {
		return new User(this.displayName, this.loginName, this.skippable);
	}

	/**
	 * Compares {@link User}s in ascending order using their {@link #displayName} and their {@link #loginName} fields.
	 */
	@Override
	public int compareTo(User otherUser) {
		int result = hungarianCollator.compare(this.displayName, otherUser.displayName);
		if (result == 0) {
			result = hungarianCollator.compare(this.loginName, otherUser.loginName);
		}
		return result;
	}

}
