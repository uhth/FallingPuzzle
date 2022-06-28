package fallingpuzzle.controller.ia;

import fallingpuzzle.controller.scene.GameController;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ToggleButton;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AIService extends Service<Void>
{

    ToggleButton tbnAiSwitch;

    GameController gameController;

    public AIService(final ToggleButton tbnAiSwitch, final GameController gameController)
    {
        this.tbnAiSwitch = tbnAiSwitch;
        this.gameController = gameController;
    }

    @Override
    protected Task<Void> createTask()
    {

        return new Task<Void>()
            {
                @Override
                protected Void call() throws Exception
                {
                    log.info("{}", "help");
                    while (tbnAiSwitch.isSelected())
                    {
                        if (!gameController.isReady())
                        {
                            continue;
                        }
                        gameController.notReady();
                        Platform.runLater(() ->
                            {
                                gameController.callAi();
                            });
                        Thread.sleep(500);
                    }
                    log.info("AI STOP");
                    return null;
                }
            };

    }

}
