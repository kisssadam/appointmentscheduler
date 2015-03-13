package my.domain;

public class MyUnavailablePeriodPenalty {

	private User user;
	private MyPeriod period;
	
	public MyUnavailablePeriodPenalty() {
		super();
	}
	
	MyUnavailablePeriodPenalty(User user, MyPeriod period) {
		super();
		this.user = user;
		this.period = period;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public MyPeriod getPeriod() {
		return this.period;
	}

	public void setPeriod(MyPeriod period) {
		this.period = period;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.period == null) ? 0 : this.period.hashCode());
		result = prime * result
				+ ((this.user == null) ? 0 : this.user.hashCode());
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
		if (!(obj instanceof MyUnavailablePeriodPenalty)) {
			return false;
		}
		MyUnavailablePeriodPenalty other = (MyUnavailablePeriodPenalty) obj;
		if (this.period == null) {
			if (other.period != null) {
				return false;
			}
		} else if (!this.period.equals(other.period)) {
			return false;
		}
		if (this.user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!this.user.equals(other.user)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MyUnavailablePeriodPenalty [user=");
		builder.append(this.user);
		builder.append(", period=");
		builder.append(this.period);
		builder.append("]");
		return builder.toString();
	}
	
}
