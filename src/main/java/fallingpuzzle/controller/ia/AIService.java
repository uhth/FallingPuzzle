package fallingpuzzle.controller.ia;

import fallingpuzzle.controller.scene.GameController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class AIService extends Service<Void>
{
    GameController gc;

    public AIService(final GameController gc)
    {
        this.gc = gc;
    }

    @Override
    protected Task<Void> createTask()
    {
        return new AITask(gc);
    }

}
