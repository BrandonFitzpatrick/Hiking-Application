package controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

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
import model.Review;
import model.SerializableImage;
import model.Trail;

public class ReviewViewController {

	@FXML
    private Text date;

    @FXML
    private HBox editBtnHBox;

    @FXML
    private HBox imageHBox;

    @FXML
    private ImageView postImageView;

    @FXML
    private ImageView profilePicture;

    @FXML
    private Text reviewScore;

    @FXML
    private Text reviewText;

    @FXML
    private Text time;
    
    @FXML
    private HBox followBtnHBox;

    @FXML
    private Text username;
    
    @FXML
    private Text trailName;
    
    private MainViewController mainViewController;
    
    private Trail trail;
    
    private Review review;
    
    private List<Trail> trailList;
    
    private boolean isViewingFromTrailView;
    
    public void init(MainViewController mainViewController, Trail trail, Review review, List<Trail> trailList, boolean isViewingFromTrailView) { 
    	boolean isViewingOwnReview = mainViewController.getAccountLoggedIn().getUsername().equals(review.getReviewer().getUsername());
		this.mainViewController = mainViewController;
		this.trail = trail;
		this.review = review;
		this.trailList = trailList;
		this.isViewingFromTrailView = isViewingFromTrailView;
		
		if (!review.getImages().isEmpty()) {
			postImageView.setImage(review.getImages().get(0).getImage());
			// allows for switching between photos if there are multiple
			if (review.getImages().size() > 1) {
				addSwitchImagesBtn();
			}
		} else {
			postImageView.setFitHeight(0);
			postImageView.setFitWidth(0);
		}
		
		//the option to delete reviews will be added if the user is viewing their own post, OR
		//if an admin is viewing the post of a non admin
    	if((isViewingOwnReview) || (mainViewController.getAccountLoggedIn().isAdmin() && !review.getReviewer().isAdmin())) {
    		addDeleteBtn();
    	}	
		
		if(isViewingOwnReview) {
			addEditReviewBtn();
		} else if(isViewingFromTrailView){
			addFollowBtn();
		}
		
    	SerializableImage profile = review.getReviewer().getProfilePicture();
    	username.setText(review.getReviewer().getUsername());
    	reviewScore.setText(String.valueOf(review.getRating()) + "/5");
    	date.setText("Date: " + review.getDatePosted().toString());
    	time.setText("Time: " + review.getTimePosted().toString().substring(0, 5));
    	profilePicture.setImage(profile.getImage());
    	reviewText.setText(review.getReviewText());
    	trailName.setText(review.getTrailReviewing().getName());
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
  			currentImageIndex.set(currentImageIndex.get() % review.getImages().size());
  			displayImage(currentImageIndex.get());
  		});
  		imageHBox.getChildren().add(switchImagesBtn);
  	}
  	
  	//add button for editing reviews
  	private void addEditReviewBtn() {
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
  				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReviewCreationView.fxml"));
  				Parent reviewCreationView = loader.load();
  				ReviewCreationViewController reviewCreationViewController = loader.getController();
  				reviewCreationViewController.init(mainViewController, trail, trailList, review, isViewingFromTrailView);
  				mainViewController.getBorderPane().setCenter(reviewCreationView);
  			} catch (IOException ex) {
  				ex.printStackTrace();
  			}
  		});
  		editBtnHBox.getChildren().add(editPostBtn);
  	}
    
    //add button for deleting reviews
    public void addDeleteBtn() {
        Button deleteBtn = new Button();
        ImageView deleteBtnIcon = new ImageView();
        deleteBtnIcon.setFitWidth(15);
        deleteBtnIcon.setFitHeight(15);
        Image deleteBtnImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/Images/trash_icon.png".replace(" ", "%20"));
        deleteBtnIcon.setImage(deleteBtnImage);
        deleteBtn.setGraphic(deleteBtnIcon);
        deleteBtn.setOnAction(e -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Delete Review");
            confirmationAlert.setContentText("Are you sure you want to delete this review");
            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                review.getReviewer().deleteReview(review);
                trail.deleteReview(review);
                //refresh the view after deleting a review
                if(isViewingFromTrailView) {
                	try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ExpandedTrailView.fxml"));
                        Parent expandedTrailView = loader.load();
                        ExpandedTrailViewController expandedTrailViewController = loader.getController();
                        expandedTrailViewController.init(mainViewController, trail, trailList);
                        mainViewController.getBorderPane().setCenter(expandedTrailView);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                	try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserProfileView.fxml"));
                        Parent userProfileView = loader.load();
                        UserProfileViewController userProfileViewController = loader.getController();
                        userProfileViewController.init(mainViewController, review.getReviewer(), false);
                        mainViewController.getBorderPane().setCenter(userProfileView);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        editBtnHBox.getChildren().add(deleteBtn);
    }
    
    public void addFollowBtn() {
    	Button followBtn = new Button();
    	followBtn.setMinWidth(75);
    	if(mainViewController.getAccountLoggedIn().isFollowing(review.getReviewer())) {
    		followBtn.setText("Unfollow");
    		followBtn.setOnAction(e -> {
    			mainViewController.getAccountLoggedIn().unfollow(review.getReviewer());
    			//refresh the view after unfollowing a user
    			try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ExpandedTrailView.fxml"));
                    Parent expandedTrailView = loader.load();
                    ExpandedTrailViewController expandedTrailViewController = loader.getController();
                    expandedTrailViewController.init(mainViewController, trail, trailList);
                    mainViewController.getBorderPane().setCenter(expandedTrailView);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
    		});
    	} else {
    		followBtn.setText("Follow");
    		followBtn.setOnAction(e -> {
    			mainViewController.getAccountLoggedIn().follow(review.getReviewer());
    			//refresh the view after following a user
    			try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ExpandedTrailView.fxml"));
                    Parent expandedTrailView = loader.load();
                    ExpandedTrailViewController expandedTrailViewController = loader.getController();
                    expandedTrailViewController.init(mainViewController, trail, trailList);
                    mainViewController.getBorderPane().setCenter(expandedTrailView);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
    		});
    	}
    	followBtnHBox.getChildren().add(followBtn);
    }
    
    private void displayImage(int index) {
		if (index >= 0 && index < review.getImages().size()) {
			Image image = review.getImages().get(index).getImage();
			postImageView.setImage(image);
		}
	}
}
