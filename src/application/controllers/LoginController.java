package application.controllers;

import application.news.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import serverConection.ConnectionManager;


import com.jfoenix.controls.JFXButton;

import application.models.LoginModel;



public class LoginController {

	private LoginModel loginModel = new LoginModel();
	private NewsReaderController newsReaderController;

	@FXML
	private TextField usernameInput;
	@FXML
	private TextField passwordInput;
	@FXML
	private JFXButton submitButton;
	
	private User loggedUsr = null;

	public LoginController (NewsReaderController newsReaderController){

		this.newsReaderController = newsReaderController;
	
		//Uncomment next sentence to use data from server instead dummy data
		//loginModel.setDummyData(false);
	}
	
	User getLoggedUsr() {
		return loggedUsr;
		
	}

	@FXML
	private void submitForm(ActionEvent action){
		
		if( !usernameInput.getText().equals("") && !passwordInput.getText().equals("") ){
			Stage stage = (Stage) ((Node) action.getSource()).getScene().getWindow();
			
			System.out.println("ALLO");
			User user = this.loginModel.validateUser(usernameInput.getText(), passwordInput.getText());
			if( user != null ){
				this.newsReaderController.setUsr(user);
				stage.close();
			}
		}
	}

	@FXML
	private void cancel(ActionEvent action){
		Stage stage = (Stage) ((Node) action.getSource()).getScene().getWindow();
		stage.close();
	}
		
	void setConnectionManager (ConnectionManager connection) {
		this.loginModel.setConnectionManager(connection);
	}
}