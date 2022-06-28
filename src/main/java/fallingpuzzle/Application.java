package fallingpuzzle;

import fallingpuzzle.controller.scene.MainMenuController;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Application extends javafx.application.Application
{

    private static Stage primaryStage;

    public static void main(final String[] args)
    {
        launch(args);
    }

    public static void setScene(final Scene scene)
    {
        if (primaryStage == null)
        {
            return;
        }
        primaryStage.setScene(scene);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception
    {
        Application.primaryStage = primaryStage;
        primaryStage.setScene(MainMenuController.getScene());
        primaryStage.setTitle("Falling Puzzle");
        primaryStage.setWidth(600);
        primaryStage.setHeight(1000);
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event ->
            {
                Platform.exit();
                System.exit(0);
            });
    }

}
