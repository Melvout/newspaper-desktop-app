/**
 * 
 */
package application.controllers;

import java.io.IOException;

import application.AppScenes;
import application.news.Article;
import application.news.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * @author ÁngelLucas
 *
 */
public class ArticleDetailsController {
	
	    private User usr;
		private Article article;

		private Pane root;

		@FXML
		private Label articleTitle;

		private final NewsReaderController newsReaderController;
		
		public ArticleDetailsController(NewsReaderController newsReaderController){

			this.newsReaderController = newsReaderController;
	
			try{
				FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.NEWS_DETAILS.getFxmlFile()));
				loader.setController(this);
				root = loader.load();
			}catch( IOException e){
				e.printStackTrace();
			}
		}

		public void initialize(){
			System.out.println("allo");
		}

		public Pane getContent(){
			return root;
		}

		@FXML
		public void backMainMenu(ActionEvent event){
			Button sourceButton = (Button)event.getSource();
			sourceButton.getScene().setRoot(newsReaderController.getContent());
		}

	    

		/**
		 * @param usr the usr to set
		 */
		void setUsr(User usr) {
			this.usr = usr;
			if (usr == null) {
				return; //Not logged user
			}
			//TODO Update UI information
		}

		/**
		 * @param article the article to set
		 */
		void setArticle(Article article) {
			this.article = article;

			
			
			//TODO complete this method

			System.out.println(">>> " + this.article.getBodyText());
			articleTitle.setText(this.article.getTitle());
		}
}
