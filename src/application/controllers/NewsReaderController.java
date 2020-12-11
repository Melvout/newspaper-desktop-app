/**
 * 
 */
package application.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonObject;
import javax.swing.Action;

import javafx.stage.FileChooser;

import application.news.Article;
import application.news.User;
import application.utils.JsonArticle;
import application.utils.exceptions.ErrorMalFormedArticle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import serverConection.ConnectionManager;
import application.AppScenes;
import application.models.NewsReaderModel;

/**
 * @author ÁngelLucas
 *
 */
public class NewsReaderController {

	private NewsReaderModel newsReaderModel = new NewsReaderModel();
	private User usr;
	@FXML
	private Label articleTitle;
	@FXML
	private ListView<Article> articlesList;
	@FXML
	private ImageView articleImage;
	@FXML
	private WebView articleBody;
	private Pane root;
	private Article articleSelected;
	
	public NewsReaderController(){		
		try{
			FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.READER.getFxmlFile()));
			loader.setController(this);
			root = loader.load();
		}catch( IOException e){
			e.printStackTrace();
		}
	}

	public void initialize(){ }

	public Pane getContent(){
		return root;
	}

	@FXML
	/* Method to go to the details page of a specific article */
	public void openDetailsPage(ActionEvent event){
		if(this.articlesList.getSelectionModel().getSelectedItem() != null){

			ArticleDetailsController articleDetailsController = new ArticleDetailsController(this);
			articleDetailsController.setArticle(getCurrentFullArticle());
			
			/* Open details page */
			Button sourceButton = (Button)event.getSource();
			sourceButton.getScene().setRoot(articleDetailsController.getContent());
		}
	}

	@FXML
	/* Method called when the create button is pressed */
	private void createArticle(ActionEvent event){

		ArticleEditController articleEditController = new ArticleEditController(this);
		articleEditController.setConnectionMannager(this.newsReaderModel.getConnectionManager());
		articleEditController.setUsr(this.usr);
		articleEditController.setArticle(null);

		clearUI();

		/* Open creating scene */
		Button sourceButton = (Button)event.getSource();
		sourceButton.getScene().setRoot(articleEditController.getContent());

	}

	@FXML
	/* Method called when the edit button is pressed */
	private void editArticle(ActionEvent event){
		if(this.articlesList.getSelectionModel().getSelectedItem() != null){

			ArticleEditController articleEditController = new ArticleEditController(this);
			articleEditController.setConnectionMannager(this.newsReaderModel.getConnectionManager());
			articleEditController.setUsr(this.usr);
			articleEditController.setArticle(getCurrentFullArticle());

			clearUI();

			/* Open editing scene */
			Button sourceButton = (Button)event.getSource();
			sourceButton.getScene().setRoot(articleEditController.getContent());
		}
	}

	@FXML
	/* Method called when the user click on one of the article in the list. */
	private void articleSelected(MouseEvent event){
		this.articleSelected = this.articlesList.getSelectionModel().getSelectedItem();
		updateUI();
		/* if double click on article, goes to article details */
		if (event.getClickCount() >= 2){
			ArticleDetailsController articleDetailsController = new ArticleDetailsController(this);
			articleDetailsController.setArticle(getCurrentFullArticle());
			ListView<Article> sourceButton = (ListView<Article>)event.getSource(); // I'll try to fix this 
			sourceButton.getScene().setRoot(articleDetailsController.getContent());
		}
	}

	@FXML
	/* Method called when the load article button is pressed */
	private void loadArticle(ActionEvent event){

		/* Creating the fileChooser window and adding an extension filter */
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("NEWS files (*.news)", "*.news");
		fileChooser.getExtensionFilters().add(extensionFilter);

		/* Get the file and transform it into an article */
		File articleFile;
		articleFile = fileChooser.showOpenDialog(articleTitle.getScene().getWindow());
		JsonObject articleJson = JsonArticle.readFile(articleFile.getPath());
		Article articleToload = null;
		try {
			articleToload = JsonArticle.jsonToArticle(articleJson);
			System.out.println(">>> " + articleToload.getTitle());
		} catch (ErrorMalFormedArticle e) {
			e.printStackTrace();
		}

		/* Open editing scene */
		ArticleEditController articleEditController = new ArticleEditController(this);
		articleEditController.setConnectionMannager(this.newsReaderModel.getConnectionManager());
		articleEditController.setUsr(this.usr);
		articleEditController.setArticle(articleToload);
		clearUI();
		Button sourceButton = (Button)event.getSource();
		sourceButton.getScene().setRoot(articleEditController.getContent());
	}
	
	/* Method to update the UI of this page */
	private void updateUI(){
		articleTitle.setText(articleSelected.getTitle());
		articleImage.setImage(articleSelected.getImageData());
		WebEngine webEngine = articleBody.getEngine();
		webEngine.loadContent(articleSelected.getAbstractText());
	}

	/* Method to clear the UI of this page*/
	private void clearUI(){
		articleTitle.setText("");
		articleImage.setImage(null);
		WebEngine webEngine = articleBody.getEngine();
		webEngine.loadContent("");
	}

	/* Method to retrieve teh data from the server */
	public void getData(){
		newsReaderModel.retrieveData();
		articlesList.setItems(newsReaderModel.getArticles());
	}

	/* Method to get full details of the currently selected article */
	public Article getCurrentFullArticle(){
		String idArticleToDisplay = Integer.toString(articlesList.getSelectionModel().getSelectedItem().getIdArticle());
		Article articleToDisplay = null;
		try {
			articleToDisplay = JsonArticle.jsonToArticle(this.newsReaderModel.getConnectionManager().getFullArticle(idArticleToDisplay));
		} 
		catch (ErrorMalFormedArticle e){
			e.printStackTrace();
		}
		return articleToDisplay;		
	}

	/**
	 * @return the usr
	 */
	public User getUsr() {
		return usr;
	}

	/**
	 * @param usr the usr to set
	 */
	public void setUsr(User usr) {
		this.usr = usr;
		//Reload articles
		//TODO Update UI
	}

	public void setConnectionManager (ConnectionManager connection){
		this.newsReaderModel.setDummyData(false); //System is connected so dummy data are not needed
		this.newsReaderModel.setConnectionManager(connection);
		this.getData();
	}


}
