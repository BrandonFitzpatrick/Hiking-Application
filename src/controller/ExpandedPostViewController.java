package controller;

import java.io.IOException;
import java.util.ListIterator;
import java.util.Stack;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Comment;
import model.Post;
import model.SerializableImage;

public class ExpandedPostViewController {

	@FXML
	private Button commentBtn;
	
	@FXML
	private ImageView commentBtnIcon;

	@FXML
	private TextField commentField;

	@FXML
	private VBox commentsVBox;

	@FXML
	private HBox postHBox;

	@FXML
	private ImageView profilePicture;
	
	@FXML
	private Button backBtn;

	@FXML
	private ImageView backBtnIcon;
	
	@FXML
	private ScrollPane scrollPane;
	
	private int initialComments = 10;
	
	//holds comments that are removed from the view as you scroll down the post list, so they they can 
	//easily be re-added as you scroll up
	private Stack<Parent> commentsRemovedFromTop = new Stack<>();
	
	//used for iterating through comments as you're scrolling
	private ListIterator<Comment> iter;
	
	private long lastScrollTime = 0;
	
	private Post post;
	
	private MainViewController mainViewController;

	public void init(MainViewController mainViewController, Post post) {
		try {
			this.mainViewController = mainViewController;
			this.post = post;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PostView.fxml"));
			Parent postView = loader.load(); 
			PostViewController postViewController = loader.getController(); 
			postViewController.init(mainViewController);
			postViewController.makePost(post);
			postHBox.getChildren().add(postView);
			iter = post.getComments().listIterator();
			SerializableImage profile = mainViewController.getAccountLoggedIn().getProfilePicture();
			profilePicture.setImage(profile.getImage());
	        commentField.textProperty().addListener(new ChangeListener<String>() {
	            @Override
	            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
	                //check if the length exceeds the maximum length
	                if (newValue.length() > 180) {
	                    commentField.setText(oldValue);
	                }
	            }
	        });
	        Image commentBtnImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/Images/up_arrow.png".replace(" ", "%20"));
	        commentBtnIcon.setImage(commentBtnImage);
	        commentBtn.setGraphic(commentBtnIcon);
	        Image backBtnImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/Images/back_arrow.png".replace(" ", "%20"));
	        backBtnIcon.setImage(backBtnImage);
	        backBtn.setGraphic(backBtnIcon);
	        
	        loadInitialComments();
	        scrollPane.setOnScroll(event -> {
	            handleScrollEvent(event);
	        });

	        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
	            handleScrollBarDrag(newValue.doubleValue());
	        });
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleScrollEvent(ScrollEvent event) {
	    //mechanism to prevent the user from scrolling too fast
	    //and not giving comments enough time to load in
	    if (System.currentTimeMillis() - lastScrollTime > 200) {
	        lastScrollTime = System.currentTimeMillis();

	        double thresholdUpwards = 0.05;
	        double thresholdDownwards = 0.95;

	        if (event.getDeltaY() < 0) {
	            //scrolling downwards
	            if (scrollPane.getVvalue() >= thresholdDownwards) {
	                loadNewCommentDownwards();
	            }
	        } else {
	            //scrolling upwards
	            if (scrollPane.getVvalue() <= thresholdUpwards) {
	                loadNewCommentUpwards();
	            }
	        }
	    }
	}

	private void handleScrollBarDrag(double newValue) {
	    double thresholdUpwards = 0.20;
	    double thresholdDownwards = 0.80;

	    if (newValue <= thresholdUpwards) {
	        loadNewCommentUpwards();
	    } else if (newValue >= thresholdDownwards) {
	        loadNewCommentDownwards();
	    }
	}
	
	private void loadInitialComments() {
        //load initial set of comments
        for (int i = 0; i < initialComments && iter.hasNext(); i++) {
        	Comment next = iter.next();
        	//don't add comment if the comment is from a blocked user
        	if(!mainViewController.getAccountLoggedIn().isBlocking(next.getUserCommenting())) {
        		 commentsVBox.getChildren().add(createCommentView(next));
        	}
        }
    }

	private void loadNewCommentDownwards() {
        if (iter.hasNext()) {
        	Comment next = iter.next();
        	//don't add comment if the comment is from a blocked user
        	if(!mainViewController.getAccountLoggedIn().isBlocking(next.getUserCommenting())) {
        		//remove the top comment
                commentsRemovedFromTop.push((Parent)commentsVBox.getChildren().remove(0));
                //add a new comment at the bottom
                commentsVBox.getChildren().add(createCommentView(next));
        	}
        }
    }

    private void loadNewCommentUpwards() {
        if (!commentsRemovedFromTop.isEmpty()) {
            //add a new comment at the top
            commentsVBox.getChildren().add(0, commentsRemovedFromTop.pop());
            //remove the bottom comment
            commentsVBox.getChildren().remove(commentsVBox.getChildren().size() - 1);
            iter.previous();
        }
    }
    
    @FXML
    void commentBtnClicked(ActionEvent event) {
		Comment comment = new Comment(mainViewController.getAccountLoggedIn(), commentField.getText());
		post.addComment(comment);
    	//refresh view after adding a comment
    	try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ExpandedPostView.fxml"));
			Parent expandedPostView = loader.load();
			ExpandedPostViewController expandedPostViewController = loader.getController();
			expandedPostViewController.init(mainViewController, post);
			mainViewController.getBorderPane().setCenter(expandedPostView);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
    }
    
    @FXML
    void backBtnClicked(ActionEvent event) {
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
    
	//takes the information from a provided comment, makes a view out of it, and adds that view
	//to the expanded post view
	private Parent createCommentView(Comment comment) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CommentView.fxml"));
			Parent commentView = loader.load(); 
			CommentViewController commentViewController = loader.getController();
			commentViewController.init(mainViewController, post, comment);
			return commentView;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

