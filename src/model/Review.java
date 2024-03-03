package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Objects;

public class Review implements Serializable {
	private Account reviewer;
	private Trail trailReviewing;
	private String reviewText;
	private LocalDate datePosted;
	private LocalTime timePosted;
	private int rating;
	private LinkedList<SerializableImage> images;
	
	public Review(Account reviewer, Trail trailReviewing, String reviewText, int rating, LinkedList<SerializableImage> images) {
		this.reviewer = reviewer;
		this.trailReviewing = trailReviewing;
		this.reviewText = reviewText;
		datePosted = LocalDate.now();
		timePosted = LocalTime.now();
		this.rating = rating;
		this.images = images;
	}
	
	public Account getReviewer() {
		return reviewer;
	}

	public Trail getTrailReviewing() {
		return trailReviewing;
	}

	public String getReviewText() {
		return reviewText;
	}

	public LocalDate getDatePosted() {
		return datePosted;
	}

	public LocalTime getTimePosted() {
		return timePosted;
	}

	public int getRating() {
		return rating;
	}
	
	public LinkedList<SerializableImage> getImages() {
		return images;
	}

	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public void setImages(LinkedList<SerializableImage> images) {
		this.images = images;
	}

	@Override
	public int hashCode() {
		return Objects.hash(datePosted, images, rating, reviewText, reviewer, timePosted, trailReviewing);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Review other = (Review) obj;
		return Objects.equals(datePosted, other.datePosted) && Objects.equals(images, other.images)
				&& rating == other.rating && Objects.equals(reviewText, other.reviewText)
				&& Objects.equals(reviewer, other.reviewer) && Objects.equals(timePosted, other.timePosted)
				&& Objects.equals(trailReviewing, other.trailReviewing);
	}

	//for debugging
	@Override
	public String toString() {
		return reviewText;
	}
}
