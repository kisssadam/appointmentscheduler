package hu.smartcampus.appointmentscheduler.entity;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The persistent class for the T_MESSAGE database table.
 * 
 */
@Entity
@Table(name = "T_MESSAGE")
@NamedQuery(name = "TMessage.findAll", query = "SELECT t FROM TMessage t")
public class TMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private long messageId;
	private String message;
	private TEvent TEvent;
	private TUser TUser;

	public TMessage() {
	}

	@Id
	@Column(name = "MESSAGE_ID", unique = true, nullable = false, precision = 10)
	public long getMessageId() {
		return this.messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

	@Column(name = "\"MESSAGE\"", length = 1000)
	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	// bi-directional many-to-one association to TEvent
	@ManyToOne
	@JoinColumn(name = "EVENT_ID")
	public TEvent getTEvent() {
		return this.TEvent;
	}

	public void setTEvent(TEvent TEvent) {
		this.TEvent = TEvent;
	}

	// bi-directional many-to-one association to TUser
	@ManyToOne
	@JoinColumn(name = "USER_ID", nullable = false)
	public TUser getTUser() {
		return this.TUser;
	}

	public void setTUser(TUser TUser) {
		this.TUser = TUser;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.TUser == null) ? 0 : this.TUser.hashCode());
		result = prime * result + ((this.message == null) ? 0 : this.message.hashCode());
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
		if (!(obj instanceof TMessage)) {
			return false;
		}
		TMessage other = (TMessage) obj;
		if (this.TUser == null) {
			if (other.TUser != null) {
				return false;
			}
		} else if (!this.TUser.equals(other.TUser)) {
			return false;
		}
		if (this.message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!this.message.equals(other.message)) {
			return false;
		}
		return true;
	}

}