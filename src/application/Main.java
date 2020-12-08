package application;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.omg.CORBA.portable.InputStream;

import application.news.Article;
import application.news.Categories;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import serverConection.ConnectionManager;
import serverConection.exceptions.AuthenticationError;
import serverConection.exceptions.ServerCommunicationError;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import application.controllers.ArticleDetailsController;
import application.controllers.NewsReaderController;


public class Main extends Application {

	@Override
	public void start(Stage primaryStage){
	
		NewsReaderController newsReaderController = new NewsReaderController();
		
		//Create properties for server connection
		//Properties prop = buildServerProperties();
		//ConnectionManager connection = new ConnectionManager(prop);
		//Connecting as public (anonymous) for your group
		//connection.setAnonymousAPIKey(""/*Put your group API Key here*/);
		//newsReaderController.setConnectionManager(connection);	

		/* Login without login form: */
		//connection.login("Reader2", "reader2"); //User: Reader2 and password "reader2" 
		//User user = new User ("Reader2", 
		//Integer.parseInt(connection.getIdUser()));
		//controller.setUsr(user);

		Pane root = newsReaderController.getContent();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("News article reader");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	final static Properties buildServerProperties() {
		Properties prop = new Properties();
		prop.setProperty(ConnectionManager.ATTR_SERVICE_URL, "https://sanger.dia.fi.upm.es/pui-rest-news/");
		prop.setProperty(ConnectionManager.ATTR_REQUIRE_SELF_CERT, "TRUE");
		
		/* For http & https proxy 
		prop.setProperty(ConnectionManager.ATTR_PROXY_HOST, "http://proxy.fi.upm.es");
		prop.setProperty(ConnectionManager.ATTR_PROXY_PORT, "80");
		*/
		/* For proxy or apache password auth 
		prop.setProperty(ConnectionManager.ATTR_PROXY_USER, "...");
		prop.setProperty(ConnectionManager.ATTR_PROXY_PASS, "...");
		*/
		return prop;
	}
	
}
