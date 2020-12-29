/**
 * 
 */
package application.controllers;

import java.io.File;
import java.io.IOException;

import javax.json.JsonObject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import javafx.stage.FileChooser;
import javafx.stage.Modality;
import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.utils.JsonArticle;
import application.utils.exceptions.ErrorMalFormedArticle;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import serverConection.ConnectionManager;
import application.AppScenes;
import application.models.NewsReaderModel;
import javafx.scene.Node;

/**
 * @author ÁngelLucas
 *
 */
public class NewsReaderController {

	@FXML
	private Label articleTitle;
	@FXML
	private ListView<Article> articlesList;
	@FXML
	private ImageView articleImage;
	@FXML
	private WebView articleBody;
	@FXML
	private JFXComboBox<Categories> categoryFilter;
	@FXML
	private JFXButton deleteArticle, editArticle, readFullArticle, loginButton;

	private NewsReaderModel newsReaderModel = new NewsReaderModel();
	private User usr = null;
	private Pane root;
	private Article articleSelected;
	private FilteredList<Article> filteredArticleList;

	public NewsReaderController(){		
		try{
			FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.READER.getFxmlFile()));
			loader.setController(this);
			root = loader.load();
		}catch( IOException e){
			e.printStackTrace();
		}
	}

	public void initialize(){
		this.categoryFilter.getItems().addAll(newsReaderModel.getCategories());
		this.categoryFilter.setValue(Categories.ALL);
		
		deleteArticle.setDisable(true);
		editArticle.setDisable(true);
		readFullArticle.setDisable(true);
	}

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
		if( articleSelected != null){

			readFullArticle.setDisable(false); // activate read article button

			/* Activate delete and edit button only if user is logged */
			if( this.usr != null ){
				deleteArticle.setDisable(false);
				editArticle.setDisable(false);
			}
			
			updateUI();

			/* if double click on article, goes to article details */
			if (event.getClickCount() >= 2){
				ArticleDetailsController articleDetailsController = new ArticleDetailsController(this);
				articleDetailsController.setArticle(getCurrentFullArticle());
				
				this.articleTitle.getScene().setRoot(articleDetailsController.getContent());
			}
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
		if( articleFile != null ){
			JsonObject articleJson = JsonArticle.readFile(articleFile.getPath());
			Article articleToload = null;
			try {
				articleToload = JsonArticle.jsonToArticle(articleJson);
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
	}
	
	@FXML
	/* Method to delete an article */
	private void deleteArticle(){
		if(this.articlesList.getSelectionModel().getSelectedItem() != null){
			newsReaderModel.deleteArticle(this.articlesList.getSelectionModel().getSelectedItem());
			clearUI();
		}
	}

	@FXML
	/* Method called to filter the articles by category */
	private void categoryFilter(){
		String filterValue = this.categoryFilter.getValue().toString();

		if(filterValue.equals("All")){
			this.filteredArticleList.setPredicate(null);
		}
		else{
			this.filteredArticleList.setPredicate( article -> article.getCategory().equals(this.categoryFilter.getValue().toString()) );
		}
		this.articlesList.setItems(filteredArticleList);
	}

	@FXML
	/* Method called to open the login view */
	private void openLoginView(ActionEvent event){
		/* if no user logged */
		if( this.usr == null ){
			Scene parentScene = ((Node) event.getSource()).getScene();
			FXMLLoader loader = null;
			try{
				loader = new FXMLLoader(getClass().getResource(AppScenes.LOGIN.getFxmlFile()));
				LoginController loginController = new LoginController(this);
				loginController.setConnectionManager(this.newsReaderModel.getConnectionManager());
				loader.setController(loginController);
				Pane root = loader.load();
				Scene scene = new Scene(root);
	
				scene.getStylesheets().add(getClass().getResource("/resources/application.css").toExternalForm());
				Window parentStage = parentScene.getWindow();
				Stage stage = new Stage();
				stage.initOwner(parentStage);
				stage.setScene(scene);
				stage.initStyle(StageStyle.UNDECORATED);
				stage.initModality(Modality.WINDOW_MODAL);
				stage.show();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		/* if user already logged */
		else{
			System.out.println("Closing app...");
			Stage stage =(Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.close();
		}
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
		this.filteredArticleList = new FilteredList<Article>(newsReaderModel.getArticles());
		articlesList.setItems(filteredArticleList);
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
		readFullArticle.setDisable(true);
		loginButton.setText("Quit");

		this.getData();
	}

	public void setConnectionManager (ConnectionManager connection){
		this.newsReaderModel.setDummyData(false); //System is connected so dummy data are not needed
		this.newsReaderModel.setConnectionManager(connection);
		this.getData();
	}
}
