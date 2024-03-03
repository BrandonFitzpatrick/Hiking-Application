package controller;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Review;
import model.Trail;

public class ExpandedTrailViewController {

	@FXML
    private Button backBtn;

    @FXML
    private ImageView backBtnIcon;

    @FXML
    private VBox reviewsVBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private HBox trailHBox;
    
    @FXML
    private Button writeReviewBtn;
    
	private int initialReviews = 10;

	//holds reviews that are removed from the view as you scroll down the review
	//list, so they they can easily be re-added as you scroll up
	private Stack<Parent> reviewsRemovedFromTop = new Stack<>();

	//used for iterating through reviews as you're scrolling
	private ListIterator<Review> iter;
    
    private MainViewController mainViewController;
    
    private Trail trail;
    
    private List<Trail> trailList;
    
	public void init(MainViewController mainViewController, Trail trail, List<Trail> trailList) {
		try {
			this.mainViewController = mainViewController;
			this.trail = trail;
			this.trailList = trailList;
			if(trailList == null) {
				backBtn.setVisible(false);
			}
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TrailView.fxml"));
			Parent trailView = loader.load();
			TrailViewController trailViewController = loader.getController();
			trailViewController.init(mainViewController, trail, trailList);
			trailHBox.getChildren().add(trailView);
			iter = trail.getReviews().listIterator();

			Image backBtnImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/")
					+ "/Images/back_arrow.png".replace(" ", "%20"));
			backBtnIcon.setImage(backBtnImage);
			backBtn.setGraphic(backBtnIcon);
			
			if(mainViewController.getAccountLoggedIn().hasReviewed(trail) ) {
				writeReviewBtn.setVisible(false);
			}

			loadInitialReviews();
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
		double thresholdUpwards = 0.05;
		double thresholdDownwards = 0.95;

		if (event.getDeltaY() < 0) {
			// scrolling downwards
			if (scrollPane.getVvalue() >= thresholdDownwards) {
				loadNewReviewDownwards();
			}
		} else {
			// scrolling upwards
			if (scrollPane.getVvalue() <= thresholdUpwards) {
				loadNewReviewUpwards();
			}
		}
	}

	private void handleScrollBarDrag(double newValue) {
	    double thresholdUpwards = 0.20;
	    double thresholdDownwards = 0.80;

	    if (newValue <= thresholdUpwards) {
	        loadNewReviewUpwards();
	    } else if (newValue >= thresholdDownwards) {
	        loadNewReviewDownwards();
	    }
	}
	
	private void loadInitialReviews() {
        //load initial set of reviews
        for (int i = 0; i < initialReviews && iter.hasNext(); i++) {
        	Review next = iter.next();
        	//don't add review if review is from a blocked user
        	if(!mainViewController.getAccountLoggedIn().isBlocking(next.getReviewer())) {
        		reviewsVBox.getChildren().add(createReviewView(next));
        	}
        }
    }

	private void loadNewReviewDownwards() {
        if (iter.hasNext()) {
        	Review next = iter.next();
        	//don't add review if review is from a blocked user
        	if(!mainViewController.getAccountLoggedIn().isBlocking(next.getReviewer())) {
        		 //remove the top review
                reviewsRemovedFromTop.push((Parent)reviewsVBox.getChildren().remove(0));
                //add a new review at the bottom
                reviewsVBox.getChildren().add(createReviewView(next));
        	}
        }
    }

    private void loadNewReviewUpwards() {
        if (!reviewsRemovedFromTop.isEmpty()) {
            //add a new review at the top
            reviewsVBox.getChildren().add(0, reviewsRemovedFromTop.pop());
            //remove the bottom review
            reviewsVBox.getChildren().remove(reviewsVBox.getChildren().size() - 1);
            iter.previous();
        }
    }

    @FXML
    void backBtnClicked(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AllTrailsView.fxml"));
            Parent allTrailsView = loader.load();
            AllTrailsViewController allTrailsViewController = loader.getController();
            allTrailsViewController.init(mainViewController, trailList);
            mainViewController.getBorderPane().setCenter(allTrailsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void writeReviewBtnClicked(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReviewCreationView.fxml"));
            Parent reviewCreationView = loader.load();
            ReviewCreationViewController reviewCreationViewController = loader.getController();
            reviewCreationViewController.init(mainViewController, trail, trailList, null, true);
            mainViewController.getBorderPane().setCenter(reviewCreationView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //takes the information from a provided review, makes a view out of it, and adds that view
 	//to the expanded trail view
 	private Parent createReviewView(Review review) {
 		try {
 			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReviewView.fxml"));
 			Parent reviewView = loader.load(); 
 			ReviewViewController reviewViewController = loader.getController();
 			reviewViewController.init(mainViewController, trail, review, trailList, true);
 			return reviewView;
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
 		return null;
 	}
}