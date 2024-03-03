package model;

import java.io.Serializable;
import java.util.Objects;

public class Comment implements Serializable {
	private Account userCommenting;
	private String comment;
	
	public Comment(Account userCommenting, String comment) {
		this.userCommenting = userCommenting;
		this.comment = comment;
	}

	public Account getUserCommenting() {
		return userCommenting;
	}

	public String getComment() {
		return comment;
	}

	@Override
	public int hashCode() {
		return Objects.hash(comment, userCommenting);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Comment other = (Comment) obj;
		return Objects.equals(comment, other.comment) && Objects.equals(userCommenting, other.userCommenting);
	}
}
