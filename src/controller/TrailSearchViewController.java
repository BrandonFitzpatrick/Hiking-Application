package controller;

import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import model.Difficulty;
import model.RouteType;
import model.Trail;
import model.TrailList;

public class TrailSearchViewController {

	@FXML
	private ListView<Difficulty> difficultyView;

	@FXML
	private TextField maxElevationGainField;

	@FXML
	private TextField maxLengthField;

	@FXML
	private TextField minElevationGainField;

	@FXML
	private TextField minLengthField;

	@FXML
	private TextField nameField;

	@FXML
	private Button searchBtn;

	@FXML
	private ListView<RouteType> typeView;

	@FXML
	private Button userSearchBtn;
	
    @FXML
    private Text searchFailed;

    private MainViewController mainViewController;
    
    public void init(MainViewController mainViewController) {
    	this.mainViewController = mainViewController;
    	difficultyView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	typeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	difficultyView.setItems(FXCollections.observableArrayList(Difficulty.values()));
    	typeView.setItems(FXCollections.observableArrayList(RouteType.values()));
    }
    
	@FXML
	void searchBtnClicked(ActionEvent event) {
		try {
			Double minLength = null, maxLength = null, minElevationGain = null, maxElevationGain = null;
			if (minLengthField.getText() != "") {
				minLength = Double.parseDouble(minLengthField.getText());
				if(minLength < 0) {
					throw new IllegalArgumentException();
				}
			}
			if (maxLengthField.getText() != "") {
				maxLength = Double.parseDouble(maxLengthField.getText());
				if(maxLength < 0 || (minLength != null && minLength > maxLength)) {
					throw new IllegalArgumentException();
				}
			}
			if (minElevationGainField.getText() != "") {
				minElevationGain = Double.parseDouble(minElevationGainField.getText());
				if(minElevationGain < 0) {
					throw new IllegalArgumentException();
				}
			}
			if (maxElevationGainField.getText() != "") {
				maxElevationGain = Double.parseDouble(maxElevationGainField.getText());
				if(maxElevationGain < 0 || (minElevationGain != null && minElevationGain > maxElevationGain)) {
					throw new IllegalArgumentException();
				}
			}

			List<Trail> trailsFound = TrailList.getTrailList().search(nameField.getText(), minLength, maxLength,
					minElevationGain, maxElevationGain, difficultyView.getSelectionModel().getSelectedItems(),
					typeView.getSelectionModel().getSelectedItems());
			if (trailsFound != null) {
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AllTrailsView.fxml"));
					Parent allTrailsView = loader.load();
					AllTrailsViewController allTrailsViewController = loader.getController();
					allTrailsViewController.init(mainViewController, trailsFound);
					mainViewController.getBorderPane().setCenter(allTrailsView);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			searchFailed.setText("Search Failed!");
		}
	}
    
    @FXML
    void userSearchBtnClicked(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserSearchView.fxml"));
            Parent userSearchView = loader.load();
            UserSearchViewController searchViewController = loader.getController();
            searchViewController.init(mainViewController);
            mainViewController.getBorderPane().setCenter(userSearchView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
}
