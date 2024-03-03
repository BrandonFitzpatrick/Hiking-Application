package model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

import javafx.scene.image.Image;

public class Account implements Serializable {
	private String username;
	private String password;
	private String phoneNumber;
	private SerializableImage profilePicture;
	private LinkedList<Post> postHistory;
	private TreeSet<Post> postsLiked;
	private TreeMap<String, Account> followers;
	private TreeMap<String, Account> following;
	private TreeMap<String, Account> usersBlocked;
	private LinkedList<Notification> notifications;
	private LinkedList<Review> userReviews;
	private boolean unviewedNotifications;
	private boolean isAdmin;
	
	public Account(String username, String password, String phoneNumber) {
		this.username = username;
		this.password = password;
		this.phoneNumber = phoneNumber;
		Image profile = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/Images/profile_icon.png".replace(" ", "%20"));
		profilePicture = new SerializableImage(profile);
		postHistory = new LinkedList<>();
		postsLiked = new TreeSet<>();
		followers = new TreeMap<>();
		following = new TreeMap<>();
		usersBlocked = new TreeMap<>();
		notifications = new LinkedList<>();
		userReviews = new LinkedList<>();
		unviewedNotifications = false;
		isAdmin = false;
	}
	
	public void addPost(Post post) {
		postHistory.addLast(post);
		Iterator<Map.Entry<String, Account>> iter = followers.entrySet().iterator();
		//each time a user makes a post, their followers will be alerted with a new notification
		while (iter.hasNext()) {
			Map.Entry<String, Account> next = iter.next();
			//most recent notifications are displayed first
			next.getValue().getNotifications().addFirst(new Notification(this, true));
			next.getValue().setUnviewedNotifications(true);
		}
	}
	
	public void deletePost(Post post) {
		postHistory.remove(post);
	}
	
	public void addReview(Review review) {
		userReviews.addLast(review);
		Iterator<Map.Entry<String, Account>> iter = followers.entrySet().iterator();
		//each time a user writes a review, their followers will be alerted with a new notification
		while (iter.hasNext()) {
			Map.Entry<String, Account> next = iter.next();
			//most recent notifications are displayed first
			next.getValue().getNotifications().addFirst(new Notification(this, false));
			next.getValue().setUnviewedNotifications(true);
		}
	}
	
	public void deleteReview(Review review) {
		userReviews.remove(review);
	}
	
	public void updateReview(Review originalReview, String reviewText, int rating, LinkedList<SerializableImage> images) {
		originalReview.setReviewText(reviewText);
		originalReview.setRating(rating);
		originalReview.setImages(images);
	}
	
	public void follow(Account account) {
		following.put(account.getUsername(), account);
		account.getFollowers().put(this.getUsername(), this);
	}
	
	public void unfollow(Account account) {
		following.remove(account.getUsername());
		account.getFollowers().remove(this.getUsername());
	}
	
	public void like(Post post) {
		postsLiked.add(post);
	}
	
	public void unlike(Post post) {
		postsLiked.remove(post);
	}
	
	public void block(Account account) {
		usersBlocked.put(account.getUsername(), account);
	}
	
	public void unblock(Account account) {
		usersBlocked.remove(account.getUsername());
	}
	
	public boolean isFollowing(Account account) {
		if (following.get(account.getUsername()) != null) {
			return true;
		}
		return false;
	}
	
	public boolean isBlocking(Account account) {
		if(usersBlocked.get(account.getUsername()) != null) {
			return true;
		}
		return false;
	}
	
	public boolean hasReviewed(Trail trail) {
		ListIterator<Review> iter = userReviews.listIterator();
		while(iter.hasNext()) {
			if(iter.next().getTrailReviewing().equals(trail)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasLiked(Post post) {
		if (postsLiked.contains(post)) {
			return true;
		}
		return false;
	}
	
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	private TreeMap<String, Account> getFollowers() {
		return followers;
	}

	public int getNumberOfFollowers() {
		return followers.size();
	}

	public int getNumberOfFollowing() {
		return following.size();
	}
	
	public int getNumberOfPosts() {
		return postHistory.size();
	}
	
	public LinkedList<Post> getPostHistory() {
		return postHistory;
	}

	public SerializableImage getProfilePicture() {
		return profilePicture;
	}

	public LinkedList<Notification> getNotifications() {
		return notifications;
	}

	public LinkedList<Review> getUserReviews() {
		return userReviews;
	}

	public boolean hasUnviewedNotifications() {
		return unviewedNotifications;
	}
	
	public boolean isAdmin() {
		return isAdmin;
	}

	public void setProfilePicture(SerializableImage profilePicture) {
		this.profilePicture = profilePicture;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setUnviewedNotifications(boolean unviewedNotifications) {
		this.unviewedNotifications = unviewedNotifications;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(followers, following, isAdmin, notifications, password, phoneNumber, postHistory,
				postsLiked, profilePicture, unviewedNotifications, userReviews, username, usersBlocked);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		return Objects.equals(followers, other.followers) && Objects.equals(following, other.following)
				&& isAdmin == other.isAdmin && Objects.equals(notifications, other.notifications)
				&& Objects.equals(password, other.password) && Objects.equals(phoneNumber, other.phoneNumber)
				&& Objects.equals(postHistory, other.postHistory) && Objects.equals(postsLiked, other.postsLiked)
				&& Objects.equals(profilePicture, other.profilePicture)
				&& unviewedNotifications == other.unviewedNotifications
				&& Objects.equals(userReviews, other.userReviews) && Objects.equals(username, other.username)
				&& Objects.equals(usersBlocked, other.usersBlocked);
	}

	//for debugging
	@Override
	public String toString() {
		return "Account [username=" + username + ", password=" + password + ", phoneNumber=" + phoneNumber
				+ ", profilePicture=" + profilePicture + ", postHistory=" + postHistory + ", postsLiked=" + postsLiked
				+ ", reviews=" + userReviews + "]";
	}
}
