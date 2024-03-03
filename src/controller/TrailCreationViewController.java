package controller;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import model.Difficulty;
import model.RouteType;
import model.Trail;
import model.TrailList;

public class TrailCreationViewController {

    @FXML
    private Button createBtn;

    @FXML
    private ComboBox<Difficulty> difficultyComboBox;

    @FXML
    private TextField elevationField;

    @FXML
    private TextField endAddressField;

    @FXML
    private HBox hBox;

    @FXML
    private TextField lengthField;

    @FXML
    private ComboBox<RouteType> routeTypeComboBox;

    @FXML
    private TextField startAddressField;

    @FXML
    private Text trailCreationFailed;

    @FXML
    private TextField trailNameField;
    
    @FXML
    private Text createTrail;
    
    private MainViewController mainViewController;
    
    private Trail trailEditing;
    
    public void init(MainViewController mainViewController, Trail trailEditing) {
    	this.mainViewController = mainViewController;
    	this.trailEditing = trailEditing;
    	difficultyComboBox.setItems(FXCollections.observableArrayList(Difficulty.values()));
    	routeTypeComboBox.setItems(FXCollections.observableArrayList(RouteType.values()));
    	if(trailEditing == null) {
        	createTrail.setText("Add Trail");
        	createBtn.setText("Create");	
        	difficultyComboBox.setValue(Difficulty.Easy);
        	routeTypeComboBox.setValue(RouteType.Loop_Trail);
    	} else {
    		createTrail.setText("Edit Trail");
    		createBtn.setText("Edit");
    		trailNameField.setText(trailEditing.getName());
    		startAddressField.setText(trailEditing.getStartAddress());
    		endAddressField.setText(trailEditing.getEndAddress());
    		lengthField.setText(String.valueOf(trailEditing.getLength()));
    		elevationField.setText(String.valueOf(trailEditing.getElevation()));
    		difficultyComboBox.setValue(trailEditing.getDifficulty());
    		routeTypeComboBox.setValue(trailEditing.getRouteType());
    	}
    }

    @FXML
    void createBtnClicked(ActionEvent event) {
    	try {
    		String trailName = trailNameField.getText();
        	String startAddress = startAddressField.getText();
        	String endAddress = endAddressField.getText();
        	double length = Double.parseDouble(lengthField.getText());
        	double elevation = Double.parseDouble(elevationField.getText());
        	Difficulty difficulty = difficultyComboBox.getValue();
        	RouteType routeType = routeTypeComboBox.getValue();
        	
        	if(trailEditing == null) {
        		Trail trail = new Trail(trailName, startAddress, endAddress, length, elevation, difficulty, routeType);
            	TrailList.getTrailList().add(trail);
            	try {
        			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ExpandedTrailView.fxml"));
        			Parent expandedTrailView = loader.load(); 
        			ExpandedTrailViewController expandedTrailViewController = loader.getController(); 
        			expandedTrailViewController.init(mainViewController, trail, null);
        			mainViewController.getBorderPane().setCenter(expandedTrailView);
        		} catch (IOException e) {
        			e.printStackTrace();
        		} 
        	} else {
        		TrailList.getTrailList().delete(trailEditing);
        		trailEditing.setName(trailName);
        		trailEditing.setStartAddress(startAddress);
        		trailEditing.setEndAddress(endAddress);
        		trailEditing.setLength(length);
        		trailEditing.setElevation(elevation);
        		trailEditing.setDifficulty(difficulty);
        		trailEditing.setRouteType(routeType);
        		TrailList.getTrailList().add(trailEditing);
        		try {
        			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ExpandedTrailView.fxml"));
        			Parent expandedTrailView = loader.load(); 
        			ExpandedTrailViewController expandedTrailViewController = loader.getController(); 
        			expandedTrailViewController.init(mainViewController, trailEditing, null);
        			mainViewController.getBorderPane().setCenter(expandedTrailView);
        		} catch (IOException e) {
        			e.printStackTrace();
        		} 
        	}
    	} catch(Exception e) {
    		trailCreationFailed.setText("\"Failure! One or more required fields are empty or invalid.");
    	}
    }
}
