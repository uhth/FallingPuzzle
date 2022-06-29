package fallingpuzzle.controller.ia;

import fallingpuzzle.controller.scene.GameController;
import javafx.application.Platform;
import javafx.concurrent.Task;

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
                    Platform.runLater(() -> gc.callAi());
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
