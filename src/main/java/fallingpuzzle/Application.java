package fallingpuzzle;
import fallingpuzzle.controller.scene.MainMenuController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Application extends javafx.application.Application {
	
	private static Stage primaryStage;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start( Stage primaryStage ) throws Exception {
		Application.primaryStage = primaryStage;
		primaryStage.setScene( MainMenuController.getScene() );
		primaryStage.setTitle( "Falling Puzzle" );
		primaryStage.setWidth( 600 );
		primaryStage.setHeight( 1000 );
		primaryStage.setResizable( false );
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		       @Override
		       public void handle(WindowEvent e) {
		          Platform.exit();
		          System.exit(0);		       
		       }
		    });
	}
		
	public static void setScene( Scene scene ) {
		if( primaryStage == null ) return;
		primaryStage.setScene( scene );
	}

}
