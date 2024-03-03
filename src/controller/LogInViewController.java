package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Account;
import model.AccountList;

public class LogInViewController {

    @FXML
    private Button createAccountBtn;

    @FXML
    private Button forgotPasswordBtn;

    @FXML
    private Button logInBtn;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;
    
    @FXML
    private Text invalidCredentials;

	public void initialize() {
		usernameField.setTextFormatter(new TextFormatter<>(change -> {
			if (change.isContentChange()) {
				int newLength = change.getControlNewText().length();
				if (newLength <= 15) {
					return change;
				} else {
					return null;
				}
			}
			return change;
		}));
	}
    
    @FXML
    void createAccountBtnClicked(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CreateAccountView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) createAccountBtn.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void forgotPasswordBtnClicked(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ForgotPasswordView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) forgotPasswordBtn.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void logInBtnClicked(ActionEvent event) {
    	String username = usernameField.getText().trim().toLowerCase();
    	String password = passwordField.getText().trim();
    	Account accountLoggedIn = AccountList.getAccountList().search(username);
    	if (accountLoggedIn != null && accountLoggedIn.getPassword().equals(password)) {
    		try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
                Parent root = loader.load();
                MainViewController mainViewController = loader.getController();
                mainViewController.init(accountLoggedIn);
                Scene scene = new Scene(root);
                Stage stage = (Stage) logInBtn.getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
    	} else {
    		invalidCredentials.setText("Invalid Credentials!");
    	}
    }
}

