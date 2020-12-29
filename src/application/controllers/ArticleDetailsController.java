/**
 * 
 */
package application.controllers;

import java.io.IOException;
import application.AppScenes;
import application.news.Article;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * @author ÁngelLucas
 *
 */
public class ArticleDetailsController{
	
		@FXML
		private Label articleTitle, articleSubtitle, articleCategory;
		@FXML
		private ImageView articleImage;
		@FXML
		private WebView articleAbstract, articleBody;

		private Article article;
		private Pane root;
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
		 * @param article the article to set
		 */
		void setArticle(Article article) {
			this.article = article;

			articleTitle.setText(this.article.getTitle());
			articleSubtitle.setText(this.article.getSubtitle());
			articleCategory.setText(this.article.getCategory());
			articleImage.setImage(this.article.getImageData());

			WebEngine webEngine = articleAbstract.getEngine();
			webEngine.loadContent(this.article.getAbstractText());

			webEngine = articleBody.getEngine();
			webEngine.loadContent(this.article.getBodyText());
		}
}
