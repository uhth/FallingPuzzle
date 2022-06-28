package fallingpuzzle.controller.scene;

import java.io.File;
import fallingpuzzle.Application;
import fallingpuzzle.controller.Controller;
import fallingpuzzle.controller.data.Setting;
import fallingpuzzle.controller.data.SettingsDAO;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainMenuController extends Controller {
	
	private static Scene scene;
	
    //Components
    @FXML
    private Button btnPLAY;
    @FXML
    private Button btnDLVPATH;
    @FXML
    private Canvas cnvMenuBG;
    
    public static Scene getScene() {
    	scene = getScene("/view/MainMenu.fxml");
    	return scene;
	}
    
    private EventHandler<ActionEvent> gameScene;
    private EventHandler<ActionEvent> dlvPath;
    
    @FXML
    public void initialize() {
    	
    	cnvMenuBG.widthProperty().bind( ( (AnchorPane) cnvMenuBG.getParent() ).widthProperty() );
    	cnvMenuBG.heightProperty().bind( ( (AnchorPane) cnvMenuBG.getParent() ).heightProperty() );
    	
    	dlvPath = event -> { 
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Resource File");
			fileChooser.getExtensionFilters().addAll(
			        new ExtensionFilter("DLV", "*.exe") );
			File selectedFile = fileChooser.showOpenDialog( scene.getWindow() );
			if( selectedFile != null ) {
				Setting setting = new Setting( "DLV_PATH", selectedFile.getAbsolutePath() );
				SettingsDAO.insert( setting );
			 }
		};
    	btnDLVPATH.setOnAction( dlvPath );
    	
    	gameScene = event -> { Application.setScene( GameController.getScene() ); };
    	btnPLAY.setOnAction( gameScene );
    	
    }
       
    
}
