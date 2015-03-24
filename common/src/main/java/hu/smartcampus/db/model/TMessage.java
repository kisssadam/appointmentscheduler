package hu.smartcampus.db.model;

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

}