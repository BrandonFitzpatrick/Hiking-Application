package model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Objects;

public class Post implements Serializable, Comparable<Post> {
	private Account userPosting;
	private String trail; //making this string for testing, should be a Trail object though
	private String caption;
	private LinkedList<SerializableImage> images;
	private LocalDate startDate;
	private LocalDate endDate; //could be null
	private LocalTime startTime;
	private LocalTime endTime;
	private Duration hikeDuration;
	private double distance;
	private double averagePace;
	private int likes;
	private LinkedList<Comment> comments;
	
	public Post(Account userPosting, String trail, String caption, LinkedList<SerializableImage> images, LocalDate startDate, LocalDate endDate,
			LocalTime startTime, LocalTime endTime, double distance) {
		this.userPosting = userPosting;
		this.trail = trail;
		this.caption = caption;
		this.images = images;
		this.startDate = startDate;
		this.endDate = endDate;
		this.startTime = startTime;
		hikeDuration = Duration.between(startTime,  endTime);
		this.endTime = endTime;
		this.distance = distance;
		averagePace = (double) Math.round((hikeDuration.toMinutes() / distance) * 10) / 10; // rounding to one decimal point
		comments = new LinkedList<>();
	}
	
	public void addComment(Comment comment) {
		comments.addLast(comment);
	}
	
	public void deleteComment(Comment comment) {
		comments.remove(comment);
	}
	
	public Account getUserPosting() {
		return userPosting;
	}
	
	public String getTrail() {
		return trail;
	}
	
	public String getCaption() {
		return caption;
	}

	public LinkedList<SerializableImage> getImages() {
		return images;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public Duration getHikeDuration() {
		return hikeDuration;
	}

	public double getDistance() {
		return distance;
	}

	public double getAveragePace() {
		return averagePace;
	}
	
	public int getLikes() {
		return likes;
	}

	public LinkedList<Comment> getComments() {
		return comments;
	}

	public void setTrail(String trail) {
		this.trail = trail;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public void setImages(LinkedList<SerializableImage> images) {
		this.images = images;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
		
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
		//update the duration and average pace each time the start and end times are updated
		hikeDuration = Duration.between(startTime,  endTime);
		averagePace = (double) Math.round((hikeDuration.toMinutes() / distance) * 10) / 10; // rounding to one decimal point
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
		//update the duration and average pace each time the start and end times are updated
		hikeDuration = Duration.between(startTime,  endTime);
		averagePace = (double) Math.round((hikeDuration.toMinutes() / distance) * 10) / 10; // rounding to one decimal point
	}

	public void setDistance(double distance) {
		this.distance = distance;
		//update the average pace each time the distance is updated
		averagePace = (double) Math.round((hikeDuration.toMinutes() / distance) * 10) / 10; // rounding to one decimal point
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(averagePace, caption, comments, distance, endDate, endTime, hikeDuration, images, likes,
				startDate, startTime, trail, userPosting);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Post other = (Post) obj;
		return Double.doubleToLongBits(averagePace) == Double.doubleToLongBits(other.averagePace)
				&& Objects.equals(caption, other.caption) && Objects.equals(comments, other.comments)
				&& Double.doubleToLongBits(distance) == Double.doubleToLongBits(other.distance)
				&& Objects.equals(endDate, other.endDate) && Objects.equals(endTime, other.endTime)
				&& Objects.equals(hikeDuration, other.hikeDuration) && Objects.equals(images, other.images)
				&& likes == other.likes && Objects.equals(startDate, other.startDate)
				&& Objects.equals(startTime, other.startTime) && Objects.equals(trail, other.trail)
				&& Objects.equals(userPosting, other.userPosting);
	}

	//for debugging
	@Override
	public String toString() {
		return "Post [trail=" + trail + ", caption=" + caption + ", images=" + images + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", startTime=" + startTime + ", endTime=" + endTime + ", hikeDuration="
				+ hikeDuration + ", distance=" + distance + ", averagePace=" + averagePace + ", likes=" + likes
				+ ", comments=" + comments + "]";
	}

	@Override
	public int compareTo(Post o) {
	    int dateComparison = this.startDate.compareTo(o.startDate);
	    if (dateComparison != 0) {
	        return dateComparison;
	    }

	    int timeComparison = this.startTime.compareTo(o.startTime);
	    if (timeComparison != 0) {
	        return timeComparison;
	    }

	    int trailComparison = this.trail.compareTo(o.trail);
	    if (trailComparison != 0) {
	        return trailComparison;
	    }

	    int distanceComparison = Double.compare(this.distance, o.distance);
	    if (distanceComparison != 0) {
	        return distanceComparison;
	    }

	    int paceComparison = Double.compare(this.averagePace, o.averagePace);
	    if (paceComparison != 0) {
	        return paceComparison;
	    }

	    int likesComparison = Integer.compare(this.likes, o.likes);
	    if (likesComparison != 0) {
	        return likesComparison;
	    }

	    int captionComparison = this.caption.compareTo(o.caption);
	    if (captionComparison != 0) {
	        return captionComparison;
	    }
	    return 0;
	}
}
