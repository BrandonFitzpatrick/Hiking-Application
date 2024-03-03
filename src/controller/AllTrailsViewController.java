package controller;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import model.Trail;

public class AllTrailsViewController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox trailsVBox;
    
    private MainViewController mainViewController;
    
    private List<Trail> trailList;
    
    private int initialTrails = 6;
   
    //holds trails that are removed from the view as you scroll down the trail list, so they they can 
  	//easily be re-added as you scroll up
    private Stack<Parent> trailsRemovedFromTop = new Stack<>();
    
    //used for iterating through trails as you're scrolling
    ListIterator<Trail> iter;
    
    public void init(MainViewController mainViewController, List<Trail> trailList) {
    	this.mainViewController = mainViewController;
    	this.trailList = trailList;
		iter = trailList.listIterator();
		loadInitialTrails();
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
				loadNewTrailDownwards();
			}
		} else {
			//scrolling upwards
			if (scrollPane.getVvalue() <= thresholdUpwards) {
				loadNewTrailUpwards();
			}
		}
	}

	private void handleScrollBarDrag(double newValue) {
	    double thresholdUpwards = 0.20;
	    double thresholdDownwards = 0.80;

	    if (newValue <= thresholdUpwards) {
	        loadNewTrailUpwards();
	    } else if (newValue >= thresholdDownwards) {
	        loadNewTrailDownwards();
	    }
	}
    
    private void loadInitialTrails() {
        //load initial set of trails
        for (int i = 0; i < initialTrails && iter.hasNext(); i++) {
        	trailsVBox.getChildren().add(createTrailView(iter.next()));
        }
    }

	private void loadNewTrailDownwards() {
        if (iter.hasNext()) {
            //remove the top trail
            trailsRemovedFromTop.push((Parent)trailsVBox.getChildren().remove(0));
            //add a new trail at the bottom
            trailsVBox.getChildren().add(createTrailView(iter.next()));
        }
    }

    private void loadNewTrailUpwards() {
        if (!trailsRemovedFromTop.isEmpty()) {
            //add a new trail at the top
            trailsVBox.getChildren().add(0, trailsRemovedFromTop.pop());
            //remove the bottom trail
            trailsVBox.getChildren().remove(trailsVBox.getChildren().size() - 1);
            iter.previous();
        }
    }
    
    private Parent createTrailView(Trail trail) {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TrailView.fxml"));
	        Parent trailView = loader.load(); 
	        TrailViewController trailViewController = loader.getController(); 
	        trailViewController.init(mainViewController, trail, trailList);
	        return trailView;
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
}
