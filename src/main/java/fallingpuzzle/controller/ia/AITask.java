package fallingpuzzle.controller.ia;

import fallingpuzzle.controller.scene.GameController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AITask extends Task<Object>
{

    GameController gc;

    public AITask(final GameController gc)
    {
        this.gc = gc;
    }

    @Override
    protected Object call() throws Exception
    {
        try
        {
            while (!Thread.currentThread().isInterrupted())
            {
                if (Boolean.TRUE.equals(gc.getAiStateProperty().get()))
                {
                    if (!gc.readyForAi.get())
                    {
                        continue;
                    }
                    gc.readyForAi.set(false);
                    log.info("{}", "Thread is calling CALLAI()");
                    Platform.runLater(() -> gc.callAi());
                    // Platform.runLater(() -> gc.initBoard());
                }
                Thread.sleep(gc.getAiSliderValueProperty().longValue());
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            throw e;
        }
        return null;
    }

}
