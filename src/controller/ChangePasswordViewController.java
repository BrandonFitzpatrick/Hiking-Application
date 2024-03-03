package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;

public class ChangePasswordViewController {

    @FXML
    private Button changePasswordBtn;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Text passwordChangeFailed;
    
    private MainViewController mainViewController;

    public void setMainViewController(MainViewController mainViewController) {
		this.mainViewController = mainViewController;
	}
    
	@FXML
    void changePasswordBtnClicked(ActionEvent event) {
    	String password = passwordField.getText();
    	//password must be at least 8 characters long, contain a capital letter, a lowercase letter, 
        //punctuation, cannot contain any other characters, and cannot contain the username
    	if (isValidPassword(password, confirmPasswordField.getText(), mainViewController.getAccountLoggedIn().getUsername(), 8)) {
    		mainViewController.getAccountLoggedIn().setPassword(password);
    		try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserProfileView.fxml"));
                Parent userProfileView = loader.load();
                UserProfileViewController userProfileViewController = loader.getController();
                userProfileViewController.init(mainViewController, mainViewController.getAccountLoggedIn(), true);
                mainViewController.getBorderPane().setCenter(userProfileView);
            } catch (IOException e) {
                e.printStackTrace();
            }
    	} else {
    		passwordChangeFailed.setText("Password Does Not Meet The Criteria, Or Passwords Do Not Match!");
    	}
    }
	
	public static boolean passwordsMatch(String password1, String password2) {
		if (!password1.equals(password2)) {
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean passwordHasRightLength(String password, int min_length) {
		if (password.length() < min_length) {
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean passwordHasUppercase(String password) {
		if (password.equals(password.toLowerCase())) {
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean passwordHasDigit(String password) {
		for (int i = 0; i < 10; i++) {
			if (password.contains(String.valueOf(i))) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean passwordHasPunctuation(String password) {
		for (int i = 0; i < password.length(); i++) {
			char current = password.charAt(i);
			if ((current == 33) || (current == 34) || (current >= 39 && current <= 42) || (current >= 44 && current <= 47) || (current == 58) || (current == 59) || (current == 63) || (current == 64) || (current >= 91 && current <= 93) || (current == 95) || (current == 123) || (current == 125)) { //goes through the ascii values of every punctuation character
				return true;
			}
		}
		return false;
	}
	
	public static boolean passwordHasOnlyLettersDigitsAndPunctuation(String password) {
		for (int i = 0; i < password.length(); i++) {
			char current = password.charAt(i);
			if ((current == 9) || (current == 32) || (current >= 35 && current <= 38) || (current == 43) || (current >= 60 && current <= 62) || (current == 94) || (current == 96) || (current == 124) || (current == 126)) { //goes through the ascii values of every character that isn't a letter, digit, or punctuation
				return false;
			}
		}
		return true;
	}
	
	public static boolean passwordHasUsername(String password, String username) {
		if (password.toLowerCase().contains(username.toLowerCase())) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean passwordIsNotBlank(String password) {
		return !password.isBlank();
	}
	
	public static boolean isValidPassword(String password, String password2, String username, int min_length) {
		if (passwordsMatch(password, password2) && passwordHasRightLength(password, min_length) && passwordIsNotBlank(password) && passwordHasUppercase(password) && passwordHasDigit(password) && passwordHasPunctuation(password) && passwordHasOnlyLettersDigitsAndPunctuation(password) && !passwordHasUsername(password, username)) {
			return true;
		} else {
			return false;
		}
	}
}