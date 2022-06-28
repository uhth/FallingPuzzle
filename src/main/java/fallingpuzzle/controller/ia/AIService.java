package fallingpuzzle.controller.ia;

import fallingpuzzle.controller.scene.GameController;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ToggleButton;

public class AIService extends Service<Void> {
	
	ToggleButton tbnAiSwitch;
	GameController gameController;
	
	public AIService( ToggleButton tbnAiSwitch, GameController gameController ) {
		this.tbnAiSwitch = tbnAiSwitch;
		this.gameController = gameController;
	}
	
	@Override
	protected Task<Void> createTask() {
		
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				System.out.println("AI RUNNING:");
				while( tbnAiSwitch.isSelected() ) {
					if( !gameController.isReady() ) continue;
				gameController.notReady();
				Platform.runLater( () -> { gameController.genDLVFile(); } );
					Thread.sleep( 500 );
				}
				System.out.println("AI STOP");
				return null;
			}
		};
		
	}

}
