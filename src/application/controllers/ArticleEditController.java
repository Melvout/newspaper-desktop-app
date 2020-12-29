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
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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

	private boolean isEditHtmlMode = true;

	@FXML
	TextField titleInput, subtitleInput;
	@FXML
	JFXComboBox<Categories> categoryInput;
	@FXML
	HTMLEditor abstractInput, bodyInput;
	@FXML
	ImageView imageInput;
	@FXML
	JFXButton saveLocallyButton, sendButton, switchEditMode;
	@FXML
	TextArea bodyInputPlainText, abstractInputPlainText;
	@FXML
	Label exportFeedback;

	public ArticleEditController(NewsReaderController newsReaderController) {

		this.newsReaderController = newsReaderController;

		try{
			FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.EDITOR.getFxmlFile()));
			loader.setController(this);
			root = loader.load();
		} 
		catch (IOException e){
			e.printStackTrace();
		}
	}

	public void initialize(){
		System.out.println("Entering editArticle scene...");
		this.categoryInput.getItems().addAll(Categories.ECONOMY, Categories.INTERNATIONAL, Categories.NATIONAL, Categories.SPORTS, Categories.TECHNOLOGY);
		Image image = new Image("images/noImage.jpg", true);
		imageInput.setImage(image);
	}

	public Pane getContent(){
		return root;
	}

	@FXML
	/* Function to send the article to the server */
	public void saveArticleToServer(){

		saveDataToModel();
		
		this.editingArticle.commit();

		send();
		returnToMainMenu();
	}

	@FXML
	public void backMainMenu(){
		returnToMainMenu();
	}

	@FXML
	/* Function to save the article locally */ 
	private void saveLocally(){

		this.write();
		this.exportFeedback.setVisible(true);
	}

	@FXML
	/* Function to add an image */
	public void onImageClicked(MouseEvent event){
		if (event.getClickCount() >= 2) {
			Scene parentScene = ((Node) event.getSource()).getScene();
			FXMLLoader loader = null;
			try {
				loader = new FXMLLoader(getClass().getResource(AppScenes.IMAGE_PICKER.getFxmlFile()));
				Pane root = loader.load();
				// Scene scene = new Scene(root, 570, 420);
				Scene scene = new Scene(root);

				scene.getStylesheets().add(getClass().getResource("/resources/application.css").toExternalForm());
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
					imageInput.setImage(image);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	/* Function to switch between HTML or Plain text edition mode */
	private void switchEditMode(){
		
		/* If bodyInput is already visible */
		if( this.isEditHtmlMode ){
			this.isEditHtmlMode = false;
			this.switchEditMode.setText("HTML");

			/* For body part */
			this.bodyInput.setVisible(false);
			this.editingArticle.bodyTextProperty().set(this.bodyInput.getHtmlText());
			this.bodyInputPlainText.setText(this.editingArticle.getBodyText());
			this.bodyInputPlainText.setVisible(true);

			/* For abstract part */
			this.abstractInput.setVisible(false);
			this.editingArticle.abstractTextProperty().set(this.abstractInput.getHtmlText());
			this.abstractInputPlainText.setText(this.editingArticle.getAbstractText());
			this.abstractInputPlainText.setVisible(true);
		}
		else{
			this.isEditHtmlMode = true;
			this.switchEditMode.setText("Plain text");

			/* For body part */
			this.bodyInputPlainText.setVisible(false);
			this.editingArticle.bodyTextProperty().set(this.bodyInputPlainText.getText());
			this.bodyInput.setHtmlText(this.editingArticle.getBodyText());
			this.bodyInput.setVisible(true);

			/* For abstract part */
			this.abstractInputPlainText.setVisible(false);
			this.editingArticle.abstractTextProperty().set(this.abstractInputPlainText.getText());
			this.abstractInput.setHtmlText(this.editingArticle.getAbstractText());
			this.abstractInput.setVisible(true);
		}
	}

	@FXML
	/* Function to enable the send button only if title and category are provided */
	private void checkFormValidity(){
		if(!this.titleInput.getText().isEmpty() && this.categoryInput.getSelectionModel().getSelectedItem() != null && this.usr != null){
			this.sendButton.setDisable(false);
		}
		else{
			this.sendButton.setDisable(true);
		}
	}

	/* Save the data from the view to the model */
	private void saveDataToModel(){
		this.editingArticle.titleProperty().set(this.titleInput.getText().replaceAll("'", "\\\\'"));

		this.editingArticle.setCategory(this.categoryInput.getSelectionModel().getSelectedItem());
		this.editingArticle.subtitleProperty().set(this.subtitleInput.getText().replaceAll("'", "\\\\'"));
		if(this.isEditHtmlMode){
			this.editingArticle.bodyTextProperty().set(this.bodyInput.getHtmlText().replaceAll("'", "\\\\'"));
			this.editingArticle.abstractTextProperty().set(this.abstractInput.getHtmlText().replaceAll("'", "\\\\'"));
		}
		else{
			this.editingArticle.bodyTextProperty().set(this.bodyInputPlainText.getText().replaceAll("'", "\\\\'"));
			this.editingArticle.abstractTextProperty().set(this.abstractInputPlainText.getText().replaceAll("'", "\\\\'"));
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
	}

	/**
	 * 
	 * @param usr the usr to set
	 */
	public void setUsr(User usr){
		this.usr = usr;	
		if(this.usr == null ){
			this.sendButton.setDisable(true);
		}	
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

			if(article != null){ 
				updateUI(); 
				System.out.println("ARTICLE EDITION");
				this.titleInput.setDisable(true);
			}
		}
		else{
			System.out.println("ALLO");
			if(article != null){ 
				System.out.println(article.getTitle());
				this.editingArticle = new ArticleEditModel(article); 
				updateUI();
			}
			else{
				this.editingArticle = new ArticleEditModel(); // To be able to create an article and save it to a local file while not logged in.
			}
		}

		/* If creation mode we disable the send button at the beginning */
		if(this.editingArticle.getTitle().equals("")){
			this.sendButton.setDisable(true);
		}
	}

	/* Return to main menu */
	public void returnToMainMenu(){
		System.out.println("Leaving editArticle scene...");
		this.newsReaderController.getData();
		root.getScene().setRoot(newsReaderController.getContent());
	}

	/* Function to update the UI */
	public void updateUI(){
		this.titleInput.setText(this.editingArticle.getTitle());
		this.subtitleInput.setText(this.editingArticle.getSubtitle());
		this.categoryInput.getSelectionModel().select(this.editingArticle.getCategory());
		this.imageInput.setImage(this.editingArticle.getArticleOriginal().getImageData());
		this.bodyInput.setHtmlText(this.editingArticle.getBodyText());
		this.abstractInput.setHtmlText(this.editingArticle.getAbstractText());
	}
	

	/**
	 * Save an article to a file in a json format
	 * Article must have a title
	 */
	private void write() {

		saveDataToModel();	
		this.editingArticle.commit();
		//Removes special characters not allowed for filenames
		String name = this.getArticle().getTitle().replaceAll("\\||/|\\\\|:|\\?","");
		String fileName ="saveNews//"+name+".news";
		JsonObject data = JsonArticle.articleToJson(this.getArticle());
		try (FileWriter file = new FileWriter(fileName)){
			file.write(data.toString());
			file.flush();
	    }catch (IOException e){
			e.printStackTrace();
		}		
	}
}
