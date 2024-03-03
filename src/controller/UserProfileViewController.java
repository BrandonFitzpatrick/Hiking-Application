package controller;

import java.io.File;
import java.io.IOException;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Stack;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Account;
import model.InitialAdmin;
import model.Post;
import model.Review;
import model.SerializableImage;

public class UserProfileViewController {

	@FXML
	private ImageView profilePicture;

	@FXML
	private Text username;

	@FXML
	private Text followers;

	@FXML
	private Text following;

	@FXML
	private Text posts;

	@FXML
	private VBox postsVBox;

	@FXML
	private HBox updateProfilePictureHBox;

	@FXML
	private HBox accountButtonsHBox;

	@FXML
	private ImageView adminIndicator;
	
	@FXML
	private ScrollPane scrollPane;
	
	@FXML
	private Button trailSearchBtn;
	
    @FXML
    private Button viewBtn;
	
	private int initialPosts = 4; 
	
	private int initialReviews = 8;
	
	//holds posts that are removed from the view as you scroll down the post list, so they they can 
	//easily be re-added as you scroll up
	private Stack<Parent> postsRemovedFromTop = new Stack<>();
	
	//holds reviews that are removed from the view as you scroll down the review list, so they they can 
	//easily be re-added as you scroll up
	private Stack<Parent> reviewsRemovedFromTop = new Stack<>();
	
	//used for iterating through posts as you're scrolling
	private ListIterator<Post> postIter;
	
	//used for iterating through reviews as you're scrolling
	private ListIterator<Review> reviewIter;
	
	private long lastScrollTime = 0;
	
	private MainViewController mainViewController;

	private Account currentAccount;

	private boolean isOwnAccount;

	private boolean accountAccessNotBlocked;
	
	private boolean isViewingPosts;

	public void init(MainViewController mainViewController, Account currentAccount, boolean isViewingPosts) {
		this.mainViewController = mainViewController;
		this.currentAccount = currentAccount;
		this.isViewingPosts = isViewingPosts;
		
		accountAccessNotBlocked = (!mainViewController.getAccountLoggedIn().isBlocking(currentAccount) && !currentAccount.isBlocking(mainViewController.getAccountLoggedIn()) /* || (mainViewController.getAccountLoggedIn().isAdmin() && !currentAccount.isAdmin()) || (mainViewController.getAccountLoggedIn() instanceof InitialAdmin) */);
		if(accountAccessNotBlocked) {
			if(isViewingPosts) {
				postIter = currentAccount.getPostHistory().listIterator();
				viewBtn.setText("View Reviews");
				loadInitialPosts();
				scrollPane.setOnScroll(event -> {
		            handlePostScrollEvent(event);
		        });

		        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
		            handlePostScrollBarDrag(newValue.doubleValue());
		        });
			} else {
				reviewIter = currentAccount.getUserReviews().listIterator();
				viewBtn.setText("View Posts");
				loadInitialReviews();
				scrollPane.setOnScroll(event -> {
		            handleReviewScrollEvent(event);
		        });

		        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
		            handleReviewScrollBarDrag(newValue.doubleValue());
		        });
			}
		}
		setInitialInformation();
	}
	
	private void handlePostScrollEvent(ScrollEvent event) {
	    //mechanism to prevent the user from scrolling too fast
	    //and not giving posts enough time to load in
	    if (System.currentTimeMillis() - lastScrollTime > 200) {
	        lastScrollTime = System.currentTimeMillis();

	        double thresholdUpwards = 0.05;
	        double thresholdDownwards = 0.95;

	        if (event.getDeltaY() < 0) {
	            //scrolling downwards
	            if (scrollPane.getVvalue() >= thresholdDownwards) {
	                loadNewPostDownwards();
	            }
	        } else {
	            //scrolling upwards
	            if (scrollPane.getVvalue() <= thresholdUpwards) {
	                loadNewPostUpwards();
	            }
	        }
	    }
	}

	private void handlePostScrollBarDrag(double newValue) {
	    double thresholdUpwards = 0.20;
	    double thresholdDownwards = 0.80;

	    if (newValue <= thresholdUpwards) {
	        loadNewPostUpwards();
	    } else if (newValue >= thresholdDownwards) {
	        loadNewPostDownwards();
	    }
	}

	private void loadInitialPosts() {
        //load initial set of posts
        for (int i = 0; i < initialPosts && postIter.hasNext(); i++) {
            postsVBox.getChildren().add(createPostView(postIter.next()));
        }
    }

	private void loadNewPostDownwards() {
        if (postIter.hasNext()) {
            //remove the top post
            postsRemovedFromTop.push((Parent)postsVBox.getChildren().remove(0));
            //add a new post at the bottom
            postsVBox.getChildren().add(createPostView(postIter.next()));
        }
    }

    private void loadNewPostUpwards() {
        if (!postsRemovedFromTop.isEmpty()) {
            //add a new post at the top
            postsVBox.getChildren().add(0, postsRemovedFromTop.pop());
            //remove the bottom post
            postsVBox.getChildren().remove(postsVBox.getChildren().size() - 1);
            postIter.previous();
        }
    }

	private void setInitialInformation() {
		isOwnAccount = mainViewController.getAccountLoggedIn().equals(currentAccount);
		//add a star icon by username if the user is an admin
		if (currentAccount.isAdmin()) {
			Image adminImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/")
					+ "/Images/star_icon.png".replace(" ", "%20"));
			adminIndicator.setImage(adminImage);
		}

		if (isOwnAccount) {
			addUpdateProfilePictureBtn();
			addLogOutBtn();
			addChangePasswordBtn();
		} else {
			if (accountAccessNotBlocked) {
				addFollowBtn();
				//admins can promote other accounts
				if (mainViewController.getAccountLoggedIn().isAdmin()) {
					if (!currentAccount.isAdmin()) {
						addPromoteBtn();
					}
				}

				//the initial admin can demote other admins
				if (currentAccount.isAdmin() && mainViewController.getAccountLoggedIn() instanceof InitialAdmin) {
					addDemoteBtn();
				}
			} 
			
			//if the user is viewing an account that has them blocked, the block button should not be added
			//and admins cannot be blocked
			if(!currentAccount.isBlocking(mainViewController.getAccountLoggedIn()) && !currentAccount.isAdmin()) {
				addBlockBtn();
			}
		}
		//profile information will be set and added to the view
		if(accountAccessNotBlocked) {
			profilePicture.setImage(currentAccount.getProfilePicture().getImage());
			username.setText(currentAccount.getUsername());
			followers.setText(currentAccount.getNumberOfFollowers() + " Followers");
			following.setText(currentAccount.getNumberOfFollowing() + " Following");
			posts.setText(currentAccount.getNumberOfPosts() + " Posts");
		} else {
			username.setText("BLOCKED");
		}
	}

	//takes the information from a provided post, makes a view out of it, and adds that view
	//to the profile view
	private Parent createPostView(Post post) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PostView.fxml"));
			Parent postView = loader.load();
			PostViewController postViewController = loader.getController();
			postViewController.init(mainViewController);
			postViewController.makePost(post);
			return postView;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@FXML
	void viewBtnClicked(ActionEvent event) {
		//switches from viewing posts to viewing reviews, or vice versa
		try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserProfileView.fxml"));
            Parent userProfileView = loader.load();
            UserProfileViewController userProfileViewController = loader.getController();
            userProfileViewController.init(mainViewController, currentAccount, !isViewingPosts);
            mainViewController.getBorderPane().setCenter(userProfileView);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void loadInitialReviews() {
        //load initial set of reviews
        for (int i = 0; i < initialReviews && reviewIter.hasNext(); i++) {
            postsVBox.getChildren().add(createReviewView(reviewIter.next()));
        }
    }

	private void loadNewReviewDownwards() {
        if (reviewIter.hasNext()) {
            //remove the top review
            reviewsRemovedFromTop.push((Parent)postsVBox.getChildren().remove(0));
            //add a new review at the bottom
            postsVBox.getChildren().add(createReviewView(reviewIter.next()));
        }
    }

    private void loadNewReviewUpwards() {
        if (!reviewsRemovedFromTop.isEmpty()) {
            //add a new review at the top
            postsVBox.getChildren().add(0, reviewsRemovedFromTop.pop());
            //remove the bottom review
            postsVBox.getChildren().remove(postsVBox.getChildren().size() - 1);
            reviewIter.previous();
        }
    }
    
    //takes the information from a provided review, makes a view out of it, and adds that view
 	//to the profile view
 	private Parent createReviewView(Review review) {
 		try {
 			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReviewView.fxml"));
 			Parent reviewView = loader.load();
 			ReviewViewController reviewViewController = loader.getController();
 			reviewViewController.init(mainViewController, review.getTrailReviewing(), review, null, false);
 			return reviewView;
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
 		return null;
 	}
 	
 	private void handleReviewScrollEvent(ScrollEvent event) {
	    //mechanism to prevent the user from scrolling too fast
	    //and not giving reviews enough time to load in
	    if (System.currentTimeMillis() - lastScrollTime > 200) {
	        lastScrollTime = System.currentTimeMillis();

	        double thresholdUpwards = 0.05;
	        double thresholdDownwards = 0.95;

	        if (event.getDeltaY() < 0) {
	            //scrolling downwards
	            if (scrollPane.getVvalue() >= thresholdDownwards) {
	                loadNewReviewDownwards();
	            }
	        } else {
	            //scrolling upwards
	            if (scrollPane.getVvalue() <= thresholdUpwards) {
	                loadNewReviewUpwards();
	            }
	        }
	    }
	}

	private void handleReviewScrollBarDrag(double newValue) {
	    double thresholdUpwards = 0.20;
	    double thresholdDownwards = 0.80;

	    if (newValue <= thresholdUpwards) {
	        loadNewReviewUpwards();
	    } else if (newValue >= thresholdDownwards) {
	        loadNewReviewDownwards();
	    }
	}

	//adds button for changing profile picture
	private void addUpdateProfilePictureBtn() {
		Button updateProfilePictureBtn = new Button();
		updateProfilePictureBtn.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
			fileChooser.getExtensionFilters().add(extFilter);
			File selectedFile = fileChooser.showOpenDialog(null);
			if (selectedFile != null) {
				String filePath = selectedFile.toURI().toString();
				Image newProfilePicture = new Image(filePath);
				currentAccount.setProfilePicture(new SerializableImage(newProfilePicture));
				profilePicture.setImage(newProfilePicture);
			}
		});
		Image updateProfilePictureImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/")
				+ "/Images/edit_icon.png".replace(" ", "%20"));
		ImageView updateProfilePictureIcon = new ImageView();
		updateProfilePictureIcon.setFitWidth(15);
		updateProfilePictureIcon.setFitHeight(15);
		updateProfilePictureIcon.setImage(updateProfilePictureImage);
		updateProfilePictureBtn.setGraphic(updateProfilePictureIcon);
		updateProfilePictureHBox.getChildren().add(updateProfilePictureBtn);
	}

	//adds button for logging out
	private void addLogOutBtn() {
		Button logOutBtn = new Button("Log Out");
		logOutBtn.setPrefSize(110, 25);
		logOutBtn.setOnAction(e -> {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogInView.fxml"));
				Parent logInView = loader.load();
				Scene scene = new Scene(logInView);
				Stage stage = (Stage) logOutBtn.getScene().getWindow();
				stage.setScene(scene);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
		accountButtonsHBox.getChildren().add(logOutBtn);
	}

	//adds button for changing password
	private void addChangePasswordBtn() {
		Button changePasswordBtn = new Button("Change Password");
		changePasswordBtn.setPrefSize(110, 25);
		changePasswordBtn.setOnAction(e -> {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ChangePasswordView.fxml"));
				Parent changePasswordView = loader.load();
				ChangePasswordViewController changePasswordViewController = loader.getController();
				changePasswordViewController.setMainViewController(mainViewController);
				mainViewController.getBorderPane().setCenter(changePasswordView);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
		accountButtonsHBox.getChildren().add(changePasswordBtn);
	}

	//adds button for following/unfollowing accounts
	private void addFollowBtn() {
		Button followBtn = new Button();
		followBtn.setPrefSize(110, 25);
		if (!mainViewController.getAccountLoggedIn().isFollowing(currentAccount)) {
			followBtn.setText("Follow");
		} else {
			followBtn.setText("Unfollow");
		}

		followBtn.setOnAction(e -> {
			if (!mainViewController.getAccountLoggedIn().isFollowing(currentAccount)) {
				mainViewController.getAccountLoggedIn().follow(currentAccount);
				followBtn.setText("Unfollow");
			} else {
				mainViewController.getAccountLoggedIn().unfollow(currentAccount);
				followBtn.setText("Follow");
			}
			followers.setText(currentAccount.getNumberOfFollowers() + " Followers");
		});
		accountButtonsHBox.getChildren().add(followBtn);
	}

	//adds button for blocking/unblocking accounts
	private void addBlockBtn() {
	    Button blockBtn = new Button();
	    blockBtn.setPrefSize(110, 25);

	    if (!mainViewController.getAccountLoggedIn().isBlocking(currentAccount)) {
	        blockBtn.setText("Block");
	    } else {
	        blockBtn.setText("Unblock");
	    }

	    blockBtn.setOnAction(e -> {
	        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
	        confirmationAlert.setTitle("Confirmation");
	        if (!mainViewController.getAccountLoggedIn().isBlocking(currentAccount)) {
	            confirmationAlert.setHeaderText("Block User");
	            confirmationAlert.setContentText("Are you sure you want to block this user?");
	        } else {
	            confirmationAlert.setHeaderText("Unblock User");
	            confirmationAlert.setContentText("Are you sure you want to unblock this user?");
	        }
	        Optional<ButtonType> result = confirmationAlert.showAndWait();
	        if (result.isPresent() && result.get() == ButtonType.OK) {
	            if (!mainViewController.getAccountLoggedIn().isBlocking(currentAccount)) {
	                mainViewController.getAccountLoggedIn().block(currentAccount);
	                mainViewController.getAccountLoggedIn().unfollow(currentAccount);
	            } else {
	                mainViewController.getAccountLoggedIn().unblock(currentAccount);
	            }
	            try {
	                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserProfileView.fxml"));
	                Parent userProfileView = loader.load();
	                UserProfileViewController userProfileViewController = loader.getController();
	                userProfileViewController.init(mainViewController, currentAccount, true);
	                mainViewController.getBorderPane().setCenter(userProfileView);
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }

	            if (!mainViewController.getAccountLoggedIn().isBlocking(currentAccount)) {
	                blockBtn.setText("Unblock");
	            } else {
	                blockBtn.setText("Block");
	            }
	        }
	    });
	    accountButtonsHBox.getChildren().add(blockBtn);
	}

	//adds button for promoting accounts
	private void addPromoteBtn() {
		Button promoteBtn = new Button("Promote");
		promoteBtn.setPrefSize(110, 25);
		promoteBtn.setOnAction(e -> {
			currentAccount.setAdmin(true);
			// refresh the view after promoting
			// (essentially to get rid of the promote/delete account buttons, and add the admin indicator)
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserProfileView.fxml"));
				Parent userProfileView = loader.load();
				UserProfileViewController userProfileViewController = loader.getController();
				userProfileViewController.init(mainViewController, currentAccount, true);
				mainViewController.getBorderPane().setCenter(userProfileView);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
		accountButtonsHBox.getChildren().addAll(promoteBtn);
	}

	//adds button for demoting accounts
	private void addDemoteBtn() {
		Button demoteBtn = new Button("Demote");
		demoteBtn.setPrefSize(110, 25);
		demoteBtn.setOnAction(e -> {
			currentAccount.setAdmin(false);
			// refresh the view after demoting
			// (essentially to get rid of the demote account buttons, and remove the admin indicator)
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserProfileView.fxml"));
				Parent userProfileView = loader.load();
				UserProfileViewController userProfileViewController = loader.getController();
				userProfileViewController.init(mainViewController, currentAccount, true);
				mainViewController.getBorderPane().setCenter(userProfileView);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
		accountButtonsHBox.getChildren().add(demoteBtn);
	}
}