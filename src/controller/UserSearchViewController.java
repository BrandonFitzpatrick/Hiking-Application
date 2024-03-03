package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.Account;
import model.AccountList;

public class UserSearchViewController {
	@FXML
	private TextField searchBar;
	
	@FXML
    private Button searchBtn;

	@FXML
	private Button trailSearchBtn;
	
	private MainViewController mainViewController;

	public void init(MainViewController mainViewController) {
		this.mainViewController = mainViewController;
	}
	
	@FXML
	void trailSearchBtnClicked(ActionEvent event) {
		try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TrailSearchView.fxml"));
            Parent trailSearchView = loader.load();
            TrailSearchViewController trailSearchViewController = loader.getController();
            trailSearchViewController.init(mainViewController);
            mainViewController.getBorderPane().setCenter(trailSearchView);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	@FXML
	void searchBtnClicked(ActionEvent event) {
		String username = searchBar.getText().toLowerCase();
		Account accountFound = AccountList.getAccountList().search(username);
		if(accountFound != null) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserProfileView.fxml"));
		        Parent userProfileView = loader.load();
		        UserProfileViewController userProfileViewController = loader.getController();
		        userProfileViewController.init(mainViewController, accountFound, true);
		        mainViewController.getBorderPane().setCenter(userProfileView);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
