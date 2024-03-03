package controller;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.Review;
import model.SerializableImage;
import model.Trail;

public class ReviewCreationViewController {

    @FXML
    private Button addImagesBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private ImageView deleteBtnIcon;

    @FXML
    private HBox imageHBox;

    @FXML
    private ImageView imageView;

    @FXML
    private Button postBtn;

    @FXML
    private TextArea reviewField;

    @FXML
    private ComboBox<Integer> ratingComboBox;

    @FXML
    private Button switchImagesBtn;

    @FXML
    private ImageView switchImagesBtnIcon;

    @FXML
    private Text writeAReview;
    
    @FXML
    private Text reviewCreationFailed;
    
    @FXML
    private Button backBtn;
    
    @FXML
    private ImageView backBtnIcon;
    
    private MainViewController mainViewController;
    
    private Trail trail;
    
    private List<Trail> trailList;
    
    private Review reviewEditing;
    
    private boolean isViewingFromTrailView;
    
    //this simply holds the images being displayed in the creation view
    private LinkedList<SerializableImage> images = new LinkedList<>();
    
    private int currentImageIndex;
    
    public void init(MainViewController mainViewController, Trail trail, List<Trail> trailList, Review reviewEditing, boolean isViewingFromTrailView) {
    	this.mainViewController = mainViewController;
    	this.trail = trail;
    	this.trailList = trailList;
    	this.reviewEditing = reviewEditing;
    	this.isViewingFromTrailView = isViewingFromTrailView;
    	
    	reviewField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //check if the length exceeds the maximum length
                if (newValue.length() > 925) {
                    reviewField.setText(oldValue);
                }
            }
        });
    	
    	Image switchImagesBtnImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/Images/forward_arrow.png".replace(" ", "%20"));
        switchImagesBtnIcon.setImage(switchImagesBtnImage);
        switchImagesBtn.setGraphic(switchImagesBtnIcon);
        
		Image deleteBtnImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/Images/trash_icon.png".replace(" ", "%20"));
		deleteBtnIcon.setImage(deleteBtnImage);
		deleteBtn.setGraphic(deleteBtnIcon);
		
		Image backBtnImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/Images/back_arrow.png".replace(" ", "%20"));
		backBtnIcon.setImage(backBtnImage);
		backBtn.setGraphic(backBtnIcon);
		
		Integer[] temp = {1, 2, 3, 4, 5};
		List<Integer> ratings = Arrays.asList(temp);
		ratingComboBox.setItems(FXCollections.observableArrayList(ratings));
		
		if(reviewEditing == null) {
			writeAReview.setText("Write a Review");
			postBtn.setText("Post");
		} else {
			writeAReview.setText("Edit Your Review");
			postBtn.setText("Edit");
			reviewField.setText(reviewEditing.getReviewText());
			ratingComboBox.setValue(reviewEditing.getRating());
			images.addAll(reviewEditing.getImages());
			currentImageIndex = 0;
			
			if(!images.isEmpty()) {
    			imageView.setImage(images.get(0).getImage());
    		}
		}
    }
    
    @FXML
    void postBtnClicked(ActionEvent event) {
    	try {
			String reviewText = reviewField.getText();
			int rating = ratingComboBox.getValue();
			
			if(reviewEditing == null) {
				Review review = new Review(mainViewController.getAccountLoggedIn(), trail, reviewText, rating, images);
				mainViewController.getAccountLoggedIn().addReview(review);
				trail.addReview(review);
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ExpandedTrailView.fxml"));
				Parent userProfileView = loader.load();
				ExpandedTrailViewController expandedTrailViewController = loader.getController();
	            expandedTrailViewController.init(mainViewController, trail, trailList);
				mainViewController.getBorderPane().setCenter(userProfileView);
			} else {
				if(isViewingFromTrailView) {
					reviewEditing.setReviewText(reviewText);
					reviewEditing.setRating(rating);
					reviewEditing.setImages(images);
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ExpandedTrailView.fxml"));
					Parent expandedTrailView = loader.load();
					ExpandedTrailViewController expandedTrailViewController = loader.getController();
		            expandedTrailViewController.init(mainViewController, trail, trailList);
					mainViewController.getBorderPane().setCenter(expandedTrailView);
				} else {
					System.out.println(trail.getReview(mainViewController.getAccountLoggedIn().getUsername()));
					reviewEditing.setReviewText(reviewText);
					reviewEditing.setRating(rating);
					reviewEditing.setImages(images);
					System.out.println(trail.getReview(mainViewController.getAccountLoggedIn().getUsername()));
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserProfileView.fxml"));
					Parent userProfileView = loader.load();
					UserProfileViewController userProfileViewController = loader.getController();
		            userProfileViewController.init(mainViewController, mainViewController.getAccountLoggedIn(), false);
					mainViewController.getBorderPane().setCenter(userProfileView);
				}
			}
		} catch (Exception e) {
			reviewCreationFailed.setText("Failure! One or more required fields are empty or invalid.");
			e.printStackTrace();
		}
    }
    
    @FXML
	void addImagesBtnClicked(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg",
				"*.jpeg", "*.gif", "*.bmp");
		fileChooser.getExtensionFilters().add(extFilter);
		List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
		currentImageIndex = 0;
		if (selectedFiles != null) {
			for (File selectedFile : selectedFiles) {
				String filePath = selectedFile.toURI().toString();
				Image image = new Image(filePath);
				images.addLast(new SerializableImage(image));
			}
			imageView.setImage(images.get(0).getImage());
		}
	}
	
	@FXML
	void switchImagesBtnClicked(ActionEvent event) {
		if (!images.isEmpty()) {
			currentImageIndex = (currentImageIndex + 1) % images.size();
			displayImage(currentImageIndex);
		}
	}

	@FXML
	void deleteBtnClicked(ActionEvent event) {
		if(!images.isEmpty()) {
			images.remove(currentImageIndex);
			if(currentImageIndex >= images.size()) {
				currentImageIndex = 0;
			}
			displayImage(currentImageIndex);
		}
		
		if(images.isEmpty()) {
			imageView.setImage(null);
		}
	}
	
	@FXML
	void backBtnClicked(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ExpandedTrailView.fxml"));
			Parent expandedTrailView = loader.load();
			ExpandedTrailViewController expandedTrailViewController = loader.getController();
	        expandedTrailViewController.init(mainViewController, trail, trailList);
			mainViewController.getBorderPane().setCenter(expandedTrailView);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void displayImage(int index) {
		if (index >= 0 && index < images.size()) {
			Image image = images.get(index).getImage();
			imageView.setImage(image);
		}
	}
}