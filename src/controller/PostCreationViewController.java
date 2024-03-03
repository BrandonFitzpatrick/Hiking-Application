package controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.Post;
import model.SerializableImage;
import model.TrailList;

public class PostCreationViewController {

	@FXML
    private Button addImagesBtn;

    @FXML
    private TextArea captionField;

    @FXML
    private Text createNewPost;

    @FXML
    private Button deleteBtn;

    @FXML
    private ImageView deleteBtnIcon;

    @FXML
    private TextField distanceField;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Spinner<LocalTime> endTimeSpinner;

    @FXML
    private HBox imageHBox;

    @FXML
    private ImageView imageView;

    @FXML
    private Button postBtn;

    @FXML
    private Text postCreationFailed;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private Spinner<LocalTime> startTimeSpinner;

    @FXML
    private Button switchImagesBtn;

    @FXML
    private ImageView switchImagesBtnIcon;

    @FXML
    private TextField trailNameField;
    
	@FXML
	private HBox hBox;
	
    @FXML
    private Text createTrail;
    
    SpinnerValueFactory<LocalTime> startTimeValueFactory;
    
    SpinnerValueFactory<LocalTime> endTimeValueFactory;
    
    //this simply holds the images being displayed in the creation view
    private LinkedList<SerializableImage> images = new LinkedList<>();
    
    private MainViewController mainViewController;
    
    private Post postEditing;
    
    private int currentImageIndex;
    
    public void init(MainViewController mainViewController, Post postEditing) {
    	this.mainViewController = mainViewController;
    	this.postEditing = postEditing;
    	
    	if(mainViewController.getAccountLoggedIn().isAdmin()) {
    		addCreateTrailBtn();
    	}
    	
    	startTimeValueFactory = new SpinnerValueFactory<>() {
            @Override
            public void decrement(int steps) {
                setValue(getValue().minusMinutes(steps)); 
            }

            @Override
            public void increment(int steps) {
                setValue(getValue().plusMinutes(steps)); 
            }
        };
        startTimeValueFactory.setValue(LocalTime.now()); 
        startTimeSpinner.setValueFactory(startTimeValueFactory);
        
        endTimeValueFactory = new SpinnerValueFactory<>() {
            @Override
            public void decrement(int steps) {
                setValue(getValue().minusMinutes(steps)); 
            }

            @Override
            public void increment(int steps) {
                setValue(getValue().plusMinutes(steps));
            }
        };
        endTimeValueFactory.setValue(LocalTime.now()); 
        endTimeSpinner.setValueFactory(endTimeValueFactory);
        
        Image switchImagesBtnImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/Images/forward_arrow.png".replace(" ", "%20"));
        switchImagesBtnIcon.setImage(switchImagesBtnImage);
        switchImagesBtn.setGraphic(switchImagesBtnIcon);
        
		Image deleteBtnImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/Images/trash_icon.png".replace(" ", "%20"));
		deleteBtnIcon.setImage(deleteBtnImage);
		deleteBtn.setGraphic(deleteBtnIcon);
        
		//if the user is making a new post rather than editing one
    	if(postEditing == null) {
    		createNewPost.setText("Create New Post");
    		postBtn.setText("Post");
    	} else {
    		createNewPost.setText("Edit Your Post");
    		postBtn.setText("Edit");
    		trailNameField.setText(postEditing.getTrail());
    		captionField.setText(postEditing.getCaption());
    		startDatePicker.setValue(postEditing.getStartDate());
    		endDatePicker.setValue(postEditing.getEndDate());
    		startTimeValueFactory.setValue(postEditing.getStartTime());
    		endTimeValueFactory.setValue(postEditing.getEndTime());
    		distanceField.setText(String.valueOf(postEditing.getDistance()));
    		images.addAll(postEditing.getImages());
    		currentImageIndex = 0;

    		if(!images.isEmpty()) {
    			imageView.setImage(images.get(0).getImage());
    		}
    	}
    }

	@FXML
	void postBtnClicked(ActionEvent event) {
		try {
			String trailName = trailNameField.getText();
			//the name used has to be the name of an existing trail
			if(TrailList.getTrailList().search(trailName, null, null, null, null, null, null) == null) {
				throw new IllegalArgumentException();
			}
			
			String caption = captionField.getText();
			
			LocalDate startDate = startDatePicker.getValue();
			LocalDate endDate = endDatePicker.getValue();
			LocalTime startTime = startTimeSpinner.getValue();
			LocalTime endTime = endTimeSpinner.getValue();
			if (startDate.isAfter(endDate) || startTime.isAfter(endTime)) {
				throw new IllegalArgumentException();
			}
			
			double distanceHiked = Double.valueOf(distanceField.getText());
		
			//if the user is making a new post rather than editing one
			if (postEditing == null) {
				Post post = new Post(mainViewController.getAccountLoggedIn(), trailName, caption, images, startDate,
						endDate, startTime, endTime, distanceHiked);
				mainViewController.getAccountLoggedIn().addPost(post);
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserProfileView.fxml"));
				Parent userProfileView = loader.load();
				UserProfileViewController userProfileViewController = loader.getController();
	            userProfileViewController.init(mainViewController, mainViewController.getAccountLoggedIn(), true);
				mainViewController.getBorderPane().setCenter(userProfileView);
			} else {
				postEditing.setTrail(trailName);
				postEditing.setCaption(caption);
				postEditing.setStartDate(startDate);
				postEditing.setEndDate(endDate);
				postEditing.setStartTime(startTime);
				postEditing.setEndTime(endTime);
				postEditing.setDistance(distanceHiked);
				postEditing.setImages(images);
				
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserProfileView.fxml"));
				Parent userProfileView = loader.load();
				UserProfileViewController userProfileViewController = loader.getController();
	            userProfileViewController.init(mainViewController, postEditing.getUserPosting(), true);
				mainViewController.getBorderPane().setCenter(userProfileView);
			}
		} catch (Exception e) {
			postCreationFailed.setText("Failure! One or more required fields are empty or invalid.");
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
	
	public void addCreateTrailBtn() {
		Button createTrailBtn = new Button("Create Trail");
		createTrailBtn.setMinWidth(90);
		createTrailBtn.setOnAction(e -> {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TrailCreationView.fxml"));
				Parent trailCreationView = loader.load();
				TrailCreationViewController trailCreationViewController = loader.getController();
				trailCreationViewController.init(mainViewController, null);
				mainViewController.getBorderPane().setCenter(trailCreationView);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
		hBox.getChildren().add(0, createTrailBtn);
	}

	private void displayImage(int index) {
		if (index >= 0 && index < images.size()) {
			Image image = images.get(index).getImage();
			imageView.setImage(image);
		}
	}
}
