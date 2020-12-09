/**
 * 
 */
package application.controllers;

import java.io.FileWriter;
import java.io.IOException;

import javax.json.JsonObject;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.utils.JsonArticle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import serverConection.ConnectionManager;
import serverConection.exceptions.ServerCommunicationError;
import application.models.ArticleEditModel;
import application.AppScenes;

/**
 * @author ÁngelLucas
 *
 */
public class ArticleEditController {
	private ConnectionManager connection;
	private ArticleEditModel editingArticle;
	private User usr;

	private Pane root;
	private final NewsReaderController newsReaderController;

	@FXML
	TextField titleInput;
	@FXML
	TextField subtitleInput;
	@FXML
	ComboBox<Categories> categoryInput;
	@FXML
	HTMLEditor abstractInput;
	@FXML
	HTMLEditor bodyInput;
	@FXML
	ImageView imageInput;

	public ArticleEditController(NewsReaderController newsReaderController) {

		this.newsReaderController = newsReaderController;

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.EDITOR.getFxmlFile()));
			loader.setController(this);
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initialize() {
		System.out.println("Entering editArticle scene...");
		this.categoryInput.getItems().addAll(Categories.ALL, Categories.ECONOMY, Categories.INTERNATIONAL, Categories.NATIONAL, Categories.SPORTS, Categories.TECHNOLOGY);
	}

	public Pane getContent() {
		return root;
	}

	@FXML
	public void saveArticleToServer(){
		this.editingArticle.setCategory(this.categoryInput.getSelectionModel().getSelectedItem());
		this.editingArticle.titleProperty().set("Test article");
		this.editingArticle.commit();
		send();
	}

	@FXML
	public void backMainMenu(ActionEvent event) {
		System.out.println("Leaving editArticle scene...");

		this.newsReaderController.getData();

		Button sourceButton = (Button) event.getSource();
		sourceButton.getScene().setRoot(newsReaderController.getContent());
	}

	@FXML
	public void onImageClicked(MouseEvent event) {
		if (event.getClickCount() >= 2) {
			Scene parentScene = ((Node) event.getSource()).getScene();
			FXMLLoader loader = null;
			try {
				loader = new FXMLLoader(getClass().getResource(AppScenes.IMAGE_PICKER.getFxmlFile()));
				Pane root = loader.load();
				// Scene scene = new Scene(root, 570, 420);
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				Window parentStage = parentScene.getWindow();
				Stage stage = new Stage();
				stage.initOwner(parentStage);
				stage.setScene(scene);
				stage.initStyle(StageStyle.UNDECORATED);
				stage.initModality(Modality.WINDOW_MODAL);
				stage.showAndWait();
				ImagePickerController controller = loader.<ImagePickerController>getController();
				Image image = controller.getImage();
				if (image != null) {
					editingArticle.setImage(image);
					// TODO Update image on UI
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Send an article to server, Title and category must be defined and category
	 * must be different to ALL
	 * 
	 * @return true if the article has been saved
	 */
	private boolean send() {
		String titleText = this.editingArticle.getTitle();
		Categories category = this.editingArticle.getCategory();

		if (titleText == null || category == null || titleText.equals("") || category == Categories.ALL) {
			Alert alert = new Alert(AlertType.ERROR, "Imposible send the article!! Title and categoy are mandatory", ButtonType.OK);
			alert.showAndWait();
			return false;
		}

		try{
			this.connection.saveArticle(this.editingArticle.getArticleOriginal());
		} catch (ServerCommunicationError e){
			
			e.printStackTrace();
		}
		return true;
	}
	
	public Article getArticle() {
		Article result = null;
		if (this.editingArticle != null) {
			result = this.editingArticle.getArticleOriginal();
		}
		return result;
	}

	/**
	 * This method is used to set the connection manager which is
	 * needed to save a news 
	 * @param connection connection manager
	 */
	public void setConnectionMannager(ConnectionManager connection) {
		this.connection = connection;
		//TODO enable send and back button
	}

	/**
	 * 
	 * @param usr the usr to set
	 */
	public void setUsr(User usr){
		this.usr = usr;		
	}

	/**
	 * PRE: User must be set
	 * 
	 * @param article
	 *            the article to set
	 */
	public void setArticle(Article article){
		if( this.usr != null ){
			this.editingArticle = (article != null) ? new ArticleEditModel(article) : new ArticleEditModel(usr);

			//TODO update UI
			this.titleInput.setText(this.editingArticle.getTitle());
			this.subtitleInput.setText(this.editingArticle.getSubtitle());
			this.categoryInput.getSelectionModel().select(this.editingArticle.getCategory());
			this.imageInput.setImage(this.editingArticle.getArticleOriginal().getImageData());
			this.bodyInput.setHtmlText(this.editingArticle.getBodyText());
			this.abstractInput.setHtmlText(this.editingArticle.getAbstractText());
		}
	}
	
	/**
	 * Save an article to a file in a json format
	 * Article must have a title
	 */
	private void write() {
		//TODO Consolidate all changes	
		this.editingArticle.commit();
		//Removes special characters not allowed for filenames
		String name = this.getArticle().getTitle().replaceAll("\\||/|\\\\|:|\\?","");
		String fileName ="saveNews//"+name+".news";
		JsonObject data = JsonArticle.articleToJson(this.getArticle());
		  try (FileWriter file = new FileWriter(fileName)) {
	            file.write(data.toString());
	            file.flush();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
}
