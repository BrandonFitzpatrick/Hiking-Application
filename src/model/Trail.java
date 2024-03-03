package model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;
import java.util.TreeMap;

public class Trail implements Serializable{
	private String name;
	private String startAddress;
	private String endAddress;
	private double length;
	private double elevation;
	private Difficulty difficulty;
	private RouteType routeType;
	private LinkedList<Review> reviews;
	
	public Trail(String name, String startAddress, String endAddress, double length, double elevation,
			Difficulty difficulty, RouteType routeType) {
		this.name = name;
		this.startAddress = startAddress;
		this.endAddress = endAddress;
		this.length = length;
		this.elevation = elevation;
		this.difficulty = difficulty;
		this.routeType = routeType;
		reviews = new LinkedList<>();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(difficulty, elevation, endAddress, length, name, reviews, routeType, startAddress);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trail other = (Trail) obj;
		return difficulty == other.difficulty
				&& Double.doubleToLongBits(elevation) == Double.doubleToLongBits(other.elevation)
				&& Objects.equals(endAddress, other.endAddress)
				&& Double.doubleToLongBits(length) == Double.doubleToLongBits(other.length)
				&& Objects.equals(name, other.name) && Objects.equals(reviews, other.reviews)
				&& routeType == other.routeType && Objects.equals(startAddress, other.startAddress);
	}
	
	public void addReview(Review review) {
		reviews.addLast(review);
	}
	
	public void deleteReview(Review review) {
		reviews.remove(review);
	}
	
	public Review getReview(String username) {
		ListIterator<Review> iter = reviews.listIterator();
		while(iter.hasNext()) {
			Review next = iter.next();
			if(next.getReviewer().getUsername().equals(username)) {
				return next;
			}
		}
		return null;
	}
	
	public String getName() {
		return name;
	}

	public double getLength() {
		return length;
	}

	public double getElevation() {
		return elevation;
	}

	public String getStartAddress() {
		return startAddress;
	}

	public String getEndAddress() {
		return endAddress;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public RouteType getRouteType() {
		return routeType;
	}

	public LinkedList<Review> getReviews() {
		return reviews;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setStartAddress(String startAddress) {
		this.startAddress = startAddress;
	}

	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	public void setRouteType(RouteType routeType) {
		this.routeType = routeType;
	}

	//for debugging
	@Override
	public String toString() {
		return "Trail [name=" + name + ", startAddress=" + startAddress + ", endAddress=" + endAddress + ", length="
				+ length + ", elevation=" + elevation + ", difficulty=" + difficulty + ", routeType=" + routeType
				+ ", reviews=" + reviews + "]";
	}
}
