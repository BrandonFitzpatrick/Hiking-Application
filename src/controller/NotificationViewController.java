package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import model.Notification;

public class NotificationViewController {

    @FXML
    private Text notificationText;

    @FXML
    private ImageView profilePicture;

    @FXML
    private Button viewAccountBtn;
    
    private Notification notification;
    
    private MainViewController mainViewController;
    
    public void setMainViewController(MainViewController mainViewController) {
    	this.mainViewController = mainViewController;
    }

    @FXML
    void viewAccountBtnClicked(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserProfileView.fxml"));
            Parent userProfileView = loader.load();
            UserProfileViewController userProfileViewController = loader.getController();
            userProfileViewController.init(mainViewController, notification.getUserPosting(), true);
            mainViewController.getBorderPane().setCenter(userProfileView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void makeNotification(Notification notification) {
    	this.notification = notification;
    	if(notification.isPostNotification()) {
    		notificationText.setText(notification.getUserPosting().getUsername() + " made a post!");
    	} else {
    		notificationText.setText(notification.getUserPosting().getUsername() + " wrote a review!");
    	}
    	profilePicture.setImage(notification.getUserPosting().getProfilePicture().getImage());
    }
}
