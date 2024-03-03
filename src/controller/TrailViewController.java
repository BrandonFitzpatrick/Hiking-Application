package controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
import model.InitialAdmin;
import model.Trail;
import model.TrailList;

public class TrailViewController {

    @FXML
    private Text difficulty;

    @FXML
    private Text distance;

    @FXML
    private Text elevationGain;

    @FXML
    private Text endAddress;

    @FXML
    private Text name;

    @FXML
    private Button reviewsBtn;

    @FXML
    private Text routeType;

    @FXML
    private Text startAddress;
    
    @FXML
    private HBox endAddressHBox;
    
    @FXML
    private HBox editBtnHBox;
    
    private MainViewController mainViewController;
    
    private Trail trail;
    
    private List<Trail> trailList;
    
    public void init(MainViewController mainViewController, Trail trail, List<Trail> trailList) {
    	this.mainViewController = mainViewController;
    	this.trail = trail;
    	this.trailList = trailList;
    	
    	if(mainViewController.getAccountLoggedIn().isAdmin()) {
    		addEditTrailBtn();
    	}
    	
    	if(mainViewController.getAccountLoggedIn() instanceof InitialAdmin) {
    		addDeleteBtn();
    	}
    	
    	name.setText(trail.getName());
    	startAddress.setText("Start: " + trail.getStartAddress());
    	distance.setText("Distance: " + trail.getLength() + " Miles");
    	elevationGain.setText("Elevation Gain: " + trail.getElevation() + " Feet");
    	if(!trail.getStartAddress().equals(trail.getEndAddress()) && !trail.getEndAddress().equals("")) {
    		endAddress.setText("End: " + trail.getEndAddress());
    	} else {
    		endAddress.setText("");
    		endAddressHBox.setMaxSize(0, 0);
    	}
    	difficulty.setText("Difficulty: " + trail.getDifficulty().toString());
    	routeType.setText("Route Type: " + trail.getRouteType().toString());
    }

    @FXML
    void reviewsBtnClicked(ActionEvent event) {
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
    
    //add button for editing trails
  	private void addEditTrailBtn() {
  		Button editTrailBtn = new Button();
  		ImageView editTrailIcon = new ImageView();
  		editTrailIcon.setFitWidth(15);
  		editTrailIcon.setFitHeight(15);
  		Image editTrailImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/")
  				+ "/Images/edit_icon.png".replace(" ", "%20"));
  		editTrailIcon.setImage(editTrailImage);
  		editTrailBtn.setGraphic(editTrailIcon);
  		editTrailBtn.setOnAction(e -> {
  			try {
  				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TrailCreationView.fxml"));
  				Parent trailCreationView = loader.load();
  				TrailCreationViewController trailCreationViewController = loader.getController();
  				trailCreationViewController.init(mainViewController, trail);
  				mainViewController.getBorderPane().setCenter(trailCreationView);
  			} catch (IOException ex) {
  				ex.printStackTrace();
  			}
  		});
  		editBtnHBox.getChildren().add(editTrailBtn);
  	}
  	
  	//add button for deleting trails
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
                TrailList.getTrailList().delete(trail);
                trailList.remove(trail);
                //refresh the view after deleting a trail
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AllTrailsView.fxml"));
					Parent allTrailsView = loader.load();
					AllTrailsViewController allTrailsViewController = loader.getController();
					allTrailsViewController.init(mainViewController, trailList);
					mainViewController.getBorderPane().setCenter(allTrailsView);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            }
        });
        editBtnHBox.getChildren().add(deleteBtn);
    }
}
