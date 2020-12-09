/**
 * 
 */
package application.controllers;

import java.io.IOException;
import application.news.Article;
import application.news.User;
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


	public NewsReaderController(){		
		//TODO
		//Uncomment next sentence to use data from server instead dummy data
		//newsReaderModel.setDummyDate(false);
		//Get text Label

		try{
			FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.READER.getFxmlFile()));
			loader.setController(this);
			root = loader.load();
		}catch( IOException e){
			e.printStackTrace();
		}
	}

	public void initialize(){
		getData();
	}

	public Pane getContent(){
		return root;
	}

	@FXML
	/* Method to go to the details page of a specific article */
	public void openDetailsPage(ActionEvent event){
		if(this.articlesList.getSelectionModel().getSelectedItem() != null){

			ArticleDetailsController articleDetailsController = new ArticleDetailsController(this);
			articleDetailsController.setArticle(this.articlesList.getSelectionModel().getSelectedItem());
			
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
			articleEditController.setArticle(this.articlesList.getSelectionModel().getSelectedItem());

			Button sourceButton = (Button)event.getSource();
			sourceButton.getScene().setRoot(articleEditController.getContent());
		}
	}


	@FXML
	/* Method called when the user click on one of the article in the list. */
	private void articleSelected(){
		Article articleSelected = this.articlesList.getSelectionModel().getSelectedItem();
		articleTitle.setText(articleSelected.getTitle());
		articleImage.setImage(articleSelected.getImageData());

		WebEngine webEngine = articleBody.getEngine();
		webEngine.loadContent(articleSelected.getBodyText());
	}

	private void getData() {
		//TODO retrieve data and update UI
		//The method newsReaderModel.retrieveData() can be used to retrieve data  
		newsReaderModel.retrieveData();
		articlesList.setItems(newsReaderModel.getArticles());
	}

	/**
	 * @return the usr
	 */
	public User getUsr() {
		return usr;
	}

	public void setConnectionManager (ConnectionManager connection){
		this.newsReaderModel.setDummyData(false); //System is connected so dummy data are not needed
		this.newsReaderModel.setConnectionManager(connection);
		this.getData();
	}
	
	/**
	 * @param usr the usr to set
	 */
	public void setUsr(User usr) {
		
		this.usr = usr;
		//Reload articles
		this.getData();
		//TODO Update UI
	}


}
