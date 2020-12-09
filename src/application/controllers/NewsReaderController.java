/**
 * 
 */
package application.controllers;

import java.io.IOException;
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
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
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

	public void initialize(){
		//getData();
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
			
			//articleDetailsController.setArticle(this.articlesList.getSelectionModel().getSelectedItem());
			
			Button sourceButton = (Button)event.getSource();
			sourceButton.getScene().setRoot(articleDetailsController.getContent());
		}
	}

	@FXML
	public void editArticle(ActionEvent event){
		if(this.articlesList.getSelectionModel().getSelectedItem() != null){
			ArticleEditController articleEditController = new ArticleEditController(this);
			
			articleEditController.setConnectionMannager(this.newsReaderModel.getConnectionManager());
			articleEditController.setUsr(this.usr);

			articleEditController.setArticle(getCurrentFullArticle());

			clearUI();

			Button sourceButton = (Button)event.getSource();
			sourceButton.getScene().setRoot(articleEditController.getContent());
		}
	}

	@FXML
	/* Method called when the user click on one of the article in the list. */
	private void articleSelected(){
		this.articleSelected = this.articlesList.getSelectionModel().getSelectedItem();
		updateUI();
	}

	private void updateUI(){
		articleTitle.setText(articleSelected.getTitle());
		articleImage.setImage(articleSelected.getImageData());
		WebEngine webEngine = articleBody.getEngine();
		webEngine.loadContent(articleSelected.getAbstractText());
	}

	private void clearUI(){
		articleTitle.setText("");
		articleImage.setImage(null);
		WebEngine webEngine = articleBody.getEngine();
		webEngine.loadContent("");
	}

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

	public void addArticle(Article articleToAdd){
		this.newsReaderModel.addArticle(articleToAdd);
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
