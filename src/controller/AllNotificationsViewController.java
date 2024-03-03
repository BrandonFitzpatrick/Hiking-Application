package controller;

import java.io.IOException;
import java.util.ListIterator;
import java.util.Stack;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import model.Notification;

public class AllNotificationsViewController {

    @FXML
    private VBox notificationsVBox;
    
    @FXML
    private ScrollPane scrollPane;
    
    private int initialNotifications = 15;
    
    //holds notifications that are removed from the view as you scroll down the notification list, so they they can 
  	//easily be re-added as you scroll up
    private Stack<Parent> notificationsRemovedFromTop = new Stack<>();
    
    //used for iterating through notifications as you're scrolling
    ListIterator<Notification> iter;
    
    private MainViewController mainViewController;
    
	public void init(MainViewController mainViewController) {
		this.mainViewController = mainViewController;
		iter = mainViewController.getAccountLoggedIn().getNotifications().listIterator();
		loadInitialNotifications();
		scrollPane.setOnScroll(event -> {
            handleScrollEvent(event);
        });

        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            handleScrollBarDrag(newValue.doubleValue());
        });
	}
	
	private void handleScrollEvent(ScrollEvent event) {
		double thresholdUpwards = 0.05;
		double thresholdDownwards = 0.95;

		if (event.getDeltaY() < 0) {
			//scrolling downwards
			if (scrollPane.getVvalue() >= thresholdDownwards) {
				loadNewNotificationDownwards();
			}
		} else {
			//scrolling upwards
			if (scrollPane.getVvalue() <= thresholdUpwards) {
				loadNewNotificationUpwards();
			}
		}
	}

	private void handleScrollBarDrag(double newValue) {
	    double thresholdUpwards = 0.20;
	    double thresholdDownwards = 0.80;

	    if (newValue <= thresholdUpwards) {
	        loadNewNotificationUpwards();
	    } else if (newValue >= thresholdDownwards) {
	        loadNewNotificationDownwards();
	    }
	}
    
    private void loadInitialNotifications() {
        //load initial set of notifications
        for (int i = 0; i < initialNotifications && iter.hasNext(); i++) {
        	notificationsVBox.getChildren().add(createNotificationView(iter.next()));
        }
    }

	private void loadNewNotificationDownwards() {
        if (iter.hasNext()) {
            //remove the top notification
            notificationsRemovedFromTop.push((Parent)notificationsVBox.getChildren().remove(0));
            //add a new notification at the bottom
            notificationsVBox.getChildren().add(createNotificationView(iter.next()));
        }
    }

    private void loadNewNotificationUpwards() {
        if (!notificationsRemovedFromTop.isEmpty()) {
            //add a new notification at the top
            notificationsVBox.getChildren().add(0, notificationsRemovedFromTop.pop());
            //remove the bottom notification
            notificationsVBox.getChildren().remove(notificationsVBox.getChildren().size() - 1);
            iter.previous();
        }
    }
    
    private Parent createNotificationView(Notification notification) {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/NotificationView.fxml"));
	        Parent notificationView = loader.load(); 
	        NotificationViewController notificationViewController = loader.getController(); 
	        notificationViewController.setMainViewController(mainViewController);
	        notificationViewController.makeNotification(notification);
	        return notificationView;
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
}
