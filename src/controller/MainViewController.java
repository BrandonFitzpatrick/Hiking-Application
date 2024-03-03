package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import model.Account;

public class MainViewController {

	@FXML
    private BorderPane borderPane;

	@FXML
	private Button notificationsBtn;

	@FXML
	private ImageView notificationsIcon;

	@FXML
	private Button postBtn;

	@FXML
	private ImageView postIcon;

	@FXML
	private Button profileBtn;

	@FXML
	private ImageView profileIcon;

	@FXML
	private Button searchBtn;

	@FXML
	private ImageView searchIcon;
	    
	private Account accountLoggedIn;

    public void init(Account accountLoggedIn) {   
    	this.accountLoggedIn = accountLoggedIn;
    	
    	Image profileImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/Images/profile_icon.png".replace(" ", "%20"));
    	profileIcon.setImage(profileImage);
    	
    	Image postImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/Images/create_post_icon.png".replace(" ", "%20"));
    	postIcon.setImage(postImage);
    	
    	Image notificationImage;
    	//change notification image depending on whether the user has new notifications
    	if(accountLoggedIn.hasUnviewedNotifications()) {
    		notificationImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/Images/bell_icon2.png".replace(" ", "%20"));
    	} else {
    		notificationImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/Images/bell_icon.png".replace(" ", "%20"));
    	}
    	notificationsIcon.setImage(notificationImage);
    	
    	Image searchImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/Images/search_icon.png".replace(" ", "%20"));
    	searchIcon.setImage(searchImage);
    	
    	//when a user first logs in, bring them to their profile view
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserProfileView.fxml"));
            Parent userProfileView = loader.load();
            UserProfileViewController userProfileViewController = loader.getController();
            userProfileViewController.init(this, accountLoggedIn, true);
            borderPane.setCenter(userProfileView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Account getAccountLoggedIn() {
    	return accountLoggedIn;
    }
    
    public BorderPane getBorderPane() {
		return borderPane;
	}

    @FXML
    void notificationsBtnClicked(ActionEvent event) {
    	try {
    		accountLoggedIn.setUnviewedNotifications(false);
    		Image notificationImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/Images/bell_icon.png".replace(" ", "%20"));
    		notificationsIcon.setImage(notificationImage);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AllNotificationsView.fxml"));
            Parent allNotificationsView = loader.load();
            AllNotificationsViewController allNotificationsViewController = loader.getController();
            allNotificationsViewController.init(this);
            borderPane.setCenter(allNotificationsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void postBtnClicked(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PostCreationView.fxml"));
            Parent postCreationView = loader.load();
            PostCreationViewController postCreationViewController = loader.getController();
            postCreationViewController.init(this, null);
            borderPane.setCenter(postCreationView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void profileBtnClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserProfileView.fxml"));
            Parent userProfileView = loader.load();
            UserProfileViewController userProfileViewController = loader.getController();
            userProfileViewController.init(this, accountLoggedIn, true);
            borderPane.setCenter(userProfileView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void searchBtnClicked(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserSearchView.fxml"));
            Parent userSearchView = loader.load();
            UserSearchViewController searchViewController = loader.getController();
            searchViewController.init(this);
            borderPane.setCenter(userSearchView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

