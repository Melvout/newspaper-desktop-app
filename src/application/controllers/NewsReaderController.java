/**
 * 
 */
package application.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.function.Predicate;

import javax.imageio.ImageIO;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.utils.JsonArticle;
import application.utils.exceptions.ErrorMalFormedArticle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;
import serverConection.ConnectionManager;
import application.AppScenes;
import application.Main;
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

	//TODO add attributes and methods as needed

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

	public Pane getContent(){
		return root;
	}

	@FXML
	public void openDetailsPage(ActionEvent event){
		if(this.articlesList.getSelectionModel().getSelectedItem() != null){
			ArticleDetailsController articleDetailsController = new ArticleDetailsController(this);
			articleDetailsController.setArticle(this.articlesList.getSelectionModel().getSelectedItem());
			


			Button sourceButton = (Button)event.getSource();
			sourceButton.getScene().setRoot(articleDetailsController.getContent());
		}
	}

	public void initialize(){
		getData();
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
