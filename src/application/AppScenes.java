/**
 * 
 */
package application;

/**
 * Contain all app scenes
 * @author ÁngelLucas
 *
 */
public enum AppScenes {
	LOGIN("/resources/views/Login.fxml"), 
	READER("/resources/views/NewsReader.fxml"), 
	NEWS_DETAILS ("/resources/views/ArticleDetails.fxml"),
	EDITOR("/resources/views/ArticleEdit.fxml"), 
	ADMIN("/resources/views/AdminNews.fxml"),
	IMAGE_PICKER("/resources/views/ImagePicker.fxml")
	/*,IMAGE_PICKER("/resources/views/ImagePickerMaterailDesign.fxml")*/; 
	
	private String fxmlFile;
	
	private AppScenes (String file){ this.fxmlFile = file; }
	 
	public String getFxmlFile(){ return this.fxmlFile; }
 
}
