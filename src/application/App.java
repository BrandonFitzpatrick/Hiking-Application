package application;

import java.io.EOFException;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.AccountList;
import model.InitialAdmin;
import model.TrailList;
import utils.Utilities;

public class App extends Application {

	public static void main(String[] args) {
		//if you immediately reopen the program after closing without giving images time to save,
		//the program will try to save and load images at the same time, resulting in serialization breaking
		//this try/catch block is a mechanism to prevent that from happening
		try {
			Utilities.restore();
			if(TrailList.getTrailList().isEmpty()) {
				Utilities.importTrails();
			}
			if(AccountList.getAccountList().isEmpty()) {
				InitialAdmin.createAndAdd();
			}
			launch(args);
			Utilities.backup();
		} catch (EOFException e) {
			System.out.println("Images are currently being serialized! Please wait to run the program.");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/view/LogInView.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}