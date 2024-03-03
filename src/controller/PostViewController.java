package controller;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import model.Post;

public class PostViewController {

	@FXML
	private ImageView profilePicture;

	@FXML
	private Text averagePace;

	@FXML
	private Text caption;

	@FXML
	private Text date;

	@FXML
	private ImageView postImageView;

	@FXML
	private Text time;

	@FXML
	private Text totalDistance;

	@FXML
	private Text totalTime;

	@FXML
	private Text trailName;

	@FXML
	private HBox imageHBox;

	@FXML
	private Text username;

	@FXML
	private Button likeBtn;

	@FXML
	private ImageView likeBtnIcon;

	@FXML
	private HBox editBtnHBox;

	@FXML
	private Button commentBtn;

	@FXML
	private ImageView commentBtnIcon;

	private Post post;

	private MainViewController mainViewController;

	private Image likeBtnImage1 = new Image("file:" + System.getProperty("user.dir").replace("\\", "/")
			+ "/Images/filled_heart.png".replace(" ", "%20"));
	private Image likeBtnImage2 = new Image("file:" + System.getProperty("user.dir").replace("\\", "/")
			+ "/Images/unfilled_heart.png".replace(" ", "%20"));

	public void init(MainViewController mainViewController) {
		this.mainViewController = mainViewController;
	}

	@FXML
	void likeBtnClicked(ActionEvent event) {
		if (!mainViewController.getAccountLoggedIn().hasLiked(post)) {
			post.setLikes(post.getLikes() + 1);
			mainViewController.getAccountLoggedIn().like(post);
			likeBtnIcon.setImage(likeBtnImage1);
		} else {
			post.setLikes(post.getLikes() - 1);
			mainViewController.getAccountLoggedIn().unlike(post);
			likeBtnIcon.setImage(likeBtnImage2);
		}
		likeBtn.setText(post.getLikes() + " Likes");
	}

	@FXML
	void commentBtnClicked(ActionEvent event) {
		try {
 	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ExpandedPostView.fxml"));
 	        Parent expandedPostView = loader.load();
 	        ExpandedPostViewController expandedPostViewController = loader.getController();
 	        expandedPostViewController.init(mainViewController, post);
 	        mainViewController.getBorderPane().setCenter(expandedPostView);
 	    } catch (IOException e) {
 	        e.printStackTrace();
 	    }
	}

	//takes the information from a provided post, and makes a view out of it
	public void makePost(Post post) {
		boolean isViewingOwnPost = mainViewController.getAccountLoggedIn().getUsername().equals(post.getUserPosting().getUsername());
		this.post = post;
		if (!post.getImages().isEmpty()) {
			postImageView.setImage(post.getImages().get(0).getImage());
			// allows for switching between photos if there are multiple
			if (post.getImages().size() > 1) {
				addSwitchImagesBtn();
			}
		} else {
			postImageView.setFitHeight(0);
			postImageView.setFitWidth(0);
		}
		profilePicture.setImage(post.getUserPosting().getProfilePicture().getImage());
		username.setText(post.getUserPosting().getUsername());
		String dateStr = post.getStartDate().toString();
		if (!post.getEndDate().equals(post.getStartDate())) {
			dateStr += " to " + post.getEndDate().toString();
		}
		date.setText(dateStr);
		caption.setText(post.getCaption());
		trailName.setText(post.getTrail());
		time.setText("From: " + post.getStartTime().toString().substring(0, 5) + " - "
				+ post.getEndTime().toString().substring(0, 5));
		totalTime.setText("Duration: " + String.format("%.1f", post.getHikeDuration().toMinutes() / 60.0) + " Hours");
		totalDistance.setText("Distance: " + String.valueOf(post.getDistance()) + " Miles");
		averagePace.setText("Average Pace: " + post.getAveragePace() + " Minutes/Mile");
		likeBtn.setText(post.getLikes() + " Likes");
		if (mainViewController.getAccountLoggedIn().hasLiked(post)) {
			likeBtnIcon.setImage(likeBtnImage1);
		} else {
			likeBtnIcon.setImage(likeBtnImage2);
		}
		Image commentImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/Images/speech_bubble_icon.png".replace(" ", "%20"));
		commentBtnIcon.setImage(commentImage);

		if (isViewingOwnPost) {
			addEditPostBtn();
		}
		
		//the option to delete posts will be added if the user is viewing their own post, OR
		//if an admin is viewing the post of a non admin
		if (isViewingOwnPost || (mainViewController.getAccountLoggedIn().isAdmin() && !post.getUserPosting().isAdmin())) {
			addDeletePostBtn();
		}
	}

	//add button for switching between images when there are multiple
	private void addSwitchImagesBtn() {
		//atomic integers can be modified within a lambda expression unlike regular integers
		AtomicInteger currentImageIndex = new AtomicInteger(0);
		Button switchImagesBtn = new Button();
		ImageView switchImagesBtnIcon = new ImageView();
		switchImagesBtnIcon.setFitWidth(25);
		switchImagesBtnIcon.setFitHeight(10);
		Image forwardImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/")
				+ "/Images/forward_arrow.png".replace(" ", "%20"));
		switchImagesBtnIcon.setImage(forwardImage);
		switchImagesBtn.setGraphic(switchImagesBtnIcon);
		switchImagesBtn.setOnAction(e -> {
			currentImageIndex.incrementAndGet();
			currentImageIndex.set(currentImageIndex.get() % post.getImages().size());
			displayImage(currentImageIndex.get());
		});
		imageHBox.getChildren().add(switchImagesBtn);
	}
	
	//add button for editing posts
	private void addEditPostBtn() {
		Button editPostBtn = new Button();
		ImageView editPostIcon = new ImageView();
		editPostIcon.setFitWidth(15);
		editPostIcon.setFitHeight(15);
		Image editPostImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/")
				+ "/Images/edit_icon.png".replace(" ", "%20"));
		editPostIcon.setImage(editPostImage);
		editPostBtn.setGraphic(editPostIcon);
		editPostBtn.setOnAction(e -> {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PostCreationView.fxml"));
				Parent postCreationView = loader.load();
				PostCreationViewController postCreationViewController = loader.getController();
				postCreationViewController.init(mainViewController, post);
				mainViewController.getBorderPane().setCenter(postCreationView);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
		editBtnHBox.getChildren().add(editPostBtn);
	}
	
	//add button for deleting posts
	private void addDeletePostBtn() {
	    Button deletePostBtn = new Button();
	    ImageView deletePostIcon = new ImageView();
	    deletePostIcon.setFitWidth(15);
	    deletePostIcon.setFitHeight(15);
	    Image deletePostImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/")
	            + "/Images/trash_icon.png".replace(" ", "%20"));
	    deletePostIcon.setImage(deletePostImage);
	    deletePostBtn.setGraphic(deletePostIcon);
	    deletePostBtn.setOnAction(e -> {
	        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
	        confirmationAlert.setTitle("Confirmation");
	        confirmationAlert.setHeaderText("Delete Post");
	        confirmationAlert.setContentText("Are you sure you want to delete this post?");
	        Optional<ButtonType> result = confirmationAlert.showAndWait();
	        if (result.isPresent() && result.get() == ButtonType.OK) {
	            post.getUserPosting().deletePost(post);
	            //reload the view after a post is deleted
	            try {
	                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserProfileView.fxml"));
	                Parent userProfileView = loader.load();
	                UserProfileViewController userProfileViewController = loader.getController();
	                userProfileViewController.init(mainViewController, post.getUserPosting(), true);
	                mainViewController.getBorderPane().setCenter(userProfileView);
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        }
	    });
	    editBtnHBox.getChildren().add(deletePostBtn);
	}

	private void displayImage(int index) {
		if (index >= 0 && index < post.getImages().size()) {
			Image image = post.getImages().get(index).getImage();
			postImageView.setImage(image);
		}
	}
}
