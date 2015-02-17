package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the T_USER database table.
 * 
 */
@Entity
@Table(name="T_USER")
@NamedQuery(name="TUser.findAll", query="SELECT t FROM TUser t")
public class TUser implements Serializable {
	private static final long serialVersionUID = 1L;
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
	@Column(name="USER_ID", unique=true, nullable=false, precision=10)
	public long getUserId() {
		return this.userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}


	@Column(name="DISPLAY_NAME", nullable=false, length=256)
	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	@Column(name="LOGIN_NAME", nullable=false, length=256)
	public String getLoginName() {
		return this.loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}


	@Column(name="SMART_PASSWORD", length=64)
	public String getSmartPassword() {
		return this.smartPassword;
	}

	public void setSmartPassword(String smartPassword) {
		this.smartPassword = smartPassword;
	}


	//bi-directional one-to-one association to TAdmin
	@OneToOne(mappedBy="TUser")
	public TAdmin getTAdmin() {
		return this.TAdmin;
	}

	public void setTAdmin(TAdmin TAdmin) {
		this.TAdmin = TAdmin;
	}


	//bi-directional many-to-many association to TEvent
	@ManyToMany
	@JoinTable(
		name="T_EVENT_PROVIDER"
		, joinColumns={
			@JoinColumn(name="USER_ID", nullable=false)
			}
		, inverseJoinColumns={
			@JoinColumn(name="EVENT_ID", nullable=false)
			}
		)
	public List<TEvent> getTEvents() {
		return this.TEvents;
	}

	public void setTEvents(List<TEvent> TEvents) {
		this.TEvents = TEvents;
	}


	//bi-directional many-to-one association to TMessage
	@OneToMany(mappedBy="TUser")
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

}