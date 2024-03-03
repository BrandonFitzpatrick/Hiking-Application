package controller;

import java.io.IOException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import model.Comment;
import model.Post;
import model.SerializableImage;

public class CommentViewController {

    @FXML
    private Text commentText;

    @FXML
    private HBox editBtnHBox;

    @FXML
    private ImageView profilePicture;

    @FXML
    private Text username;
    
    @FXML
    private HBox deleteBtnHBox;
    
    private MainViewController mainViewController;
    
    private Post post;
    
    public void init(MainViewController mainViewController, Post post, Comment comment) { 
		this.mainViewController = mainViewController;
		this.post = post;
		boolean isViewingOwnComment = mainViewController.getAccountLoggedIn().getUsername().equals(comment.getUserCommenting().getUsername());
    	//admins can delete the comments of non admin users
    	if(isViewingOwnComment || (mainViewController.getAccountLoggedIn().isAdmin() && !comment.getUserCommenting().isAdmin())) {
    		addDeleteBtn(comment);
    	}
    	SerializableImage profile = comment.getUserCommenting().getProfilePicture();
    	username.setText(comment.getUserCommenting().getUsername());
    	profilePicture.setImage(profile.getImage());
    	commentText.setText(comment.getComment());
	}
    
    //add button for deleting comments
    public void addDeleteBtn(Comment comment) {
        Button deleteBtn = new Button();
        ImageView deleteBtnIcon = new ImageView();
        deleteBtnIcon.setFitWidth(15);
        deleteBtnIcon.setFitHeight(15);
        Image deleteBtnImage = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/Images/trash_icon.png".replace(" ", "%20"));
        deleteBtnIcon.setImage(deleteBtnImage);
        deleteBtn.setGraphic(deleteBtnIcon);
        deleteBtn.setOnAction(e -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Delete Comment");
            confirmationAlert.setContentText("Are you sure you want to delete this comment?");
            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                post.deleteComment(comment);
                //refresh the view after deleting a comment
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ExpandedPostView.fxml"));
                    Parent expandedPostView = loader.load();
                    ExpandedPostViewController expandedPostViewController = loader.getController();
                    expandedPostViewController.init(mainViewController, post);
                    mainViewController.getBorderPane().setCenter(expandedPostView);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        deleteBtnHBox.getChildren().add(deleteBtn);
    }
}

