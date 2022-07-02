package fallingpuzzle.controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class Controller
{

    protected static Parent root;

    private static Scene load(final String path)
    {
        final FXMLLoader loader = new FXMLLoader(Controller.class.getResource(path));
        Scene scene = null;
        try
        {
            root = loader.load();
            scene = new Scene(root);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
            log.error("Can't figure out controller's path in FXML file.");
        }
        return scene;
    }

    //Creates a new scene from a FXML file
    protected static Scene getScene(final String path)
    {
        return load(path);
    }

}
