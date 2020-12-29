package application;

import java.util.Properties;
import javafx.application.Application;
import javafx.stage.Stage;
import serverConection.ConnectionManager;
import serverConection.exceptions.AuthenticationError;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import application.controllers.NewsReaderController;

public class Main extends Application{

	@Override
	public void start(Stage primaryStage){

		NewsReaderController newsReaderController = new NewsReaderController();

		try{

		// Create properties for server connection
		Properties prop = buildServerProperties();
		ConnectionManager connection = new ConnectionManager(prop);
		connection.setAnonymousAPIKey("");
		newsReaderController.setConnectionManager(connection);
		} 
		catch (AuthenticationError e){
			e.printStackTrace();
		} 

		Pane root = newsReaderController.getContent();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("News article reader");
		primaryStage.show();
	}

	public static void main(String[] args){
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
