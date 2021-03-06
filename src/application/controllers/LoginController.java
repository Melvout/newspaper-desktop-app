package application.controllers;

import application.news.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import serverConection.ConnectionManager;
import com.jfoenix.controls.JFXButton;
import application.models.LoginModel;

public class LoginController{

	@FXML
	private TextField usernameInput, passwordInput;
	@FXML
	private JFXButton submitButton;
	@FXML
	private Label feedbackLabel;

	private LoginModel loginModel = new LoginModel();
	private NewsReaderController newsReaderController;
	private User loggedUsr = null;

	public LoginController (NewsReaderController newsReaderController){

		this.newsReaderController = newsReaderController;
	}
	
	User getLoggedUsr(){
		return loggedUsr;
	}

	@FXML
	/* Function to submit the login form to the server */
	private void submitForm(ActionEvent action){
		
		if( !usernameInput.getText().equals("") && !passwordInput.getText().equals("") ){
			Stage stage = (Stage) ((Node) action.getSource()).getScene().getWindow();
			
			User user = this.loginModel.validateUser(usernameInput.getText(), passwordInput.getText());
			if( user != null ){
				this.newsReaderController.setUsr(user);
				stage.close();
			}
			else{
				System.out.println("Wrong password");
				this.feedbackLabel.setVisible(true); // Set visible the feedback label
			}
		}
	}

	@FXML
	private void onInput(){
		this.feedbackLabel.setVisible(false); // Set invisible the feedback label
	}

	@FXML
	private void cancel(ActionEvent action){
		Stage stage = (Stage) ((Node) action.getSource()).getScene().getWindow();
		stage.close();
	}
		
	void setConnectionManager (ConnectionManager connection){
		this.loginModel.setConnectionManager(connection);
	}
}