package fallingpuzzle.controller.ia;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public class DLVAdapter implements DVLProgram
{

    public void streamGridIntoProcess(final ObservableList<Node> rows, final Process process)
    {
        try
        {
            final OutputStream out = process.getOutputStream();
            final OutputStreamWriter ow = new OutputStreamWriter(out);
            final String program = createProgram(rows);
            ow.write(program);
            ow.close();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

}
