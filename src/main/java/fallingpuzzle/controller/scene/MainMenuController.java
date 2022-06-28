package fallingpuzzle.controller.scene;

import fallingpuzzle.Application;
import fallingpuzzle.controller.Controller;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class MainMenuController extends Controller
{

    private static Scene scene;

    public static Scene getScene()
    {
        scene = getScene("/view/MainMenu.fxml");
        return scene;
    }

    //Components
    @FXML
    private Button btnPLAY;

    @FXML
    private Canvas cnvMenuBG;

    private EventHandler<ActionEvent> gameScene;

    @FXML
    public void initialize()
    {

        cnvMenuBG.widthProperty().bind(((AnchorPane) cnvMenuBG.getParent()).widthProperty());
        cnvMenuBG.heightProperty().bind(((AnchorPane) cnvMenuBG.getParent()).heightProperty());

        gameScene = event ->
            {
                Application.setScene(GameController.getScene());
            };
        btnPLAY.setOnAction(gameScene);

    }

}
