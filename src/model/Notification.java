package model;

import java.io.Serializable;
import java.util.Objects;

public class Notification implements Serializable {
	private Account userPosting;
	private boolean isPostNotification;

	public Notification(Account userPosting, boolean isPostNotification) {
		this.userPosting = userPosting;
		this.isPostNotification = isPostNotification;
	}

	public Account getUserPosting() {
		return userPosting;
	}

	public boolean isPostNotification() {
		return isPostNotification;
	}

	@Override
	public int hashCode() {
		return Objects.hash(isPostNotification, userPosting);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Notification other = (Notification) obj;
		return isPostNotification == other.isPostNotification && Objects.equals(userPosting, other.userPosting);
	}
}
