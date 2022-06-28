package fallingpuzzle.controller.scene;

import java.util.concurrent.atomic.AtomicBoolean;

import fallingpuzzle.controller.Controller;
import fallingpuzzle.controller.ia.AIService;
import fallingpuzzle.controller.ia.DLVAdapter;
import fallingpuzzle.controller.ia.DLVController;
import fallingpuzzle.model.Row;
import fallingpuzzle.model.Tile;
import fallingpuzzle.model.TileGenerator;
import fallingpuzzle.model.TileMove;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class GameController extends Controller
{

    private static Tile selectedTile;

    private static AIService AiCycle;

    public static Scene getScene()
    {
        return getScene("/view/Game.fxml");
    }

    public static void updateSelectedTile(final Tile newTile)
    {
        if (selectedTile != null)
        {
            selectedTile.setWidth(selectedTile.getWidth() + 2);
            selectedTile.setHeight(selectedTile.getHeight() + 2);
            selectedTile.setX(selectedTile.getX() - 1);
            selectedTile.setY(selectedTile.getY() - 1);
        }
        selectedTile = newTile;
        selectedTile.setWidth(selectedTile.getWidth() - 2);
        selectedTile.setHeight(selectedTile.getHeight() - 2);
        selectedTile.setX(selectedTile.getX() + 1);
        selectedTile.setY(selectedTile.getY() + 1);
    }

    @FXML
    private VBox vboRows;

    @FXML
    private VBox vboNextRow;

    @FXML
    private AnchorPane achBoard;

    @FXML
    private Label lblGameTitle;

    @FXML
    private Canvas cnvGameBG;

    @FXML
    private MenuItem mniInitBoard;

    @FXML
    private MenuItem mniRowUp;

    @FXML
    private Label lblScore;

    @FXML
    private ToggleButton tbnAiSwitch;

    private DLVController dlvController;

    private EventHandler<ActionEvent> iASwitch;

    private EventHandler<ActionEvent> rowUp;

    private EventHandler<ActionEvent> initBoard;

    private ObservableList<Node> rows;

    private final AtomicBoolean isReady = new AtomicBoolean(true);

    final DLVAdapter dlvAdapter = new DLVAdapter();

    //IA STUFF

    public void addScore(final int score)
    {
        int currentScore = Integer.parseInt(lblScore.getText());
        currentScore += score;
        final StringBuilder sb = new StringBuilder();
        sb.append(currentScore);
        lblScore.setText(sb.toString());
    }

    public void callAi()
    {
        final Process dlvProcess = dlvController.startProcess();
        dlvAdapter.streamGridIntoProcess(rows, dlvProcess);
        final TileMove tileMove = dlvController.getTileMoveFromOutput();
        if (tileMove != null)
        {
            log.info("{} {}", tileMove.getTile(), tileMove.getNewIndex());
            moveTile(tileMove.getTile(), tileMove.getNewIndex());
        }
        isReady.set(true);
    }

    public void genRow()
    {
        //add row to preview vbox
        createRow();
        //shift upper row to game vbox
        if (vboNextRow.getChildren().size() > 1)
        {
            final Row row1 = (Row) vboNextRow.getChildren().get(0);
            vboNextRow.getChildren().remove(row1);
            vboRows.getChildren().add(row1);

            row1.fitToParent(); //TODO ISSUES HERE Out Of Bounds somehow

            //add some features to tails
            for (int i = 0; i < row1.getChildren().size(); ++i)
            {
                final Tile tile = (Tile) row1.getChildren().get(i);
                tile.setSelectable(true);
                tile.setDraggable(true);
            }

            log.info("genrow");

        }

        //GameOver
        if (vboRows.getChildren().size() > 10)
        {
            System.out.println("GAME OVER");
            reset();
            mniInitBoard.fire();
        }
    }

    public Row getRow(final int rowIndex)
    {
        return (Row) vboRows.getChildren().get(rowIndex);
    }

    public int getRowPosition(final Row row)
    {
        for (int i = 0; i < rows.size(); ++i)
        {
            if (rows.get(i).equals(row))
            {
                return i;
            }
        }
        return 0;
    }

    @FXML
    public void initialize()
    {
        rows = vboRows.getChildren();
        dlvController = new DLVController(this);
        AiCycle = new AIService(tbnAiSwitch, this);

        cnvGameBG.getGraphicsContext2D().drawImage(new Image(this.getClass().getResourceAsStream("/images/bg4.png")), 0, 0);

        rowUp = event ->
            {
                genRow();
                update();
            };
        mniRowUp.setOnAction(rowUp);

        iASwitch = event ->
            {
                if (!tbnAiSwitch.isSelected())
                {
                    tbnAiSwitch.setText("AI OFF");
                    mniRowUp.setDisable(false);
                    mniInitBoard.setDisable(false);
                    return;
                }
                tbnAiSwitch.setText("AI ON");
                AiCycle.restart();
                mniRowUp.setDisable(true);
                mniInitBoard.setDisable(true);
            };
        tbnAiSwitch.setOnAction(iASwitch);

        initBoard = event ->
            {
                while (vboRows.getChildren().size() < 4)
                {
                    genRow();
                }
            };
        lblScore.setText("0");
        mniInitBoard.setOnAction(initBoard);

    }

    public boolean isReady()
    {
        return isReady.get();
    }

    public void moveTile(final Tile tile, final int index)
    {
        final Row row = (Row) tile.getParent();
        if (row.moveTile(tile, index))
        {
            log.info("got here");
            update();
            genRow();
            update();
        }
    }

    //ROW MANAGEMENT

    //THREADS STUFF
    public void notReady()
    {
        isReady.set(false);
    }

    public void removeRow(final Row row)
    {
        rows.remove(row);
    }

    /* MAIN ALGORITHM */
    // 1 - while -> check each row for falling tiles ( starting from bottom ) returns true
    // 2 - if -> check for a full row ( starting from bottom )
    // 2a true -> remove it then go to step 1
    // 2b false -> end
    public void update()
    {

        int score = 0;

        boolean cycle = true;
        while (cycle)
        {
            cycle = false;
            //step 1
            while (handleFallingTiles())
            {
            }

            //step 2
            if (handleFullRows())
            {
                cycle = true;
                ++score;
            }
            log.info("update");
        }
        addScore(score);

    }

    private Row createRow()
    {
        final Row row = new Row();
        row.setController(this);
        vboNextRow.getChildren().add(row);
        row.fitToParent();
        final TileGenerator tg = new TileGenerator();
        tg.genTiles(row);
        return row;
    }

    private boolean handleFallingTiles()
    {
        boolean falling = false;
        for (int i = rows.size() - 2; i >= 0; --i)
        {
            final Row currentRow = (Row) rows.get(i);
            final Row nextRow = (Row) rows.get(i + 1);
            for (int j = 0; j < currentRow.getChildren().size(); ++j)
            {
                final Tile tile = (Tile) currentRow.getChildren().get(j);
                if (!nextRow.collidesWithOtherTiles(tile))
                {
                    nextRow.insert(tile, false);
                    currentRow.remove(tile);
                    falling = true;
                }
            }
        }
        return falling;
    }

    private boolean handleFullRows()
    {
        for (int i = rows.size() - 1; i >= 0; --i)
        {
            final Row currentRow = (Row) rows.get(i);
            if (currentRow.isFull())
            {
                rows.remove(currentRow);
                return true;
            }
        }
        return false;
    }

    private void reset()
    {
        vboNextRow.getChildren().clear();
        vboRows.getChildren().clear();
        lblScore.setText("0");
    }

}
