package fallingpuzzle.controller.scene;

import fallingpuzzle.controller.Controller;
import fallingpuzzle.controller.ia.AIService;
import fallingpuzzle.controller.ia.DLVAdapter;
import fallingpuzzle.controller.ia.DLVController;
import fallingpuzzle.controller.scene.VBoxRow.ChildrenAddedEvent;
import fallingpuzzle.model.Row;
import fallingpuzzle.model.Tile;
import fallingpuzzle.model.TileGenerator;
import fallingpuzzle.model.TileMove;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class GameController extends Controller
{

    private static Tile selectedTile;

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

    private VBoxRow vboRows;

    private VBoxRow vboNextRow;

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

    private EventHandler<ChildrenAddedEvent> rowSlideUp;

    private TileGenerator tileGenerator;

    private BooleanProperty aiStateProperty;

    private Service<Object> aiService;

    private DLVAdapter dlvAdapter;

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
        dlvAdapter.streamGridIntoProcess(vboRows.getChildrenUnmodifiable(), dlvProcess);
        final TileMove tileMove = dlvController.getTileMoveFromOutput();
        if (tileMove != null)
        {
            final Row row = tileMove.getTile().getRow();
            final Integer newIndex = tileMove.getNewIndex();
            row.moveTile(tileMove.getTile(), newIndex);
            // rowUp.handle(null);
            updateRows();
        }
    }

    public BooleanProperty getAiStateProperty()
    {
        return aiStateProperty;
    }

    public Row getRowByIndex(final int index)
    {
        return (Row) vboRows.getChildren().get(index);
    }

    public int getRowIndex(final Row row)
    {
        return vboRows.getChildrenUnmodifiable().indexOf(row);
    }

    @FXML
    public void initialize()
    {
        dlvController = new DLVController(this);
        tileGenerator = new TileGenerator();
        dlvAdapter = new DLVAdapter();
        aiStateProperty = new SimpleBooleanProperty(false);
        aiService = new AIService(this);
        aiService.start();

        vboNextRow = new VBoxRow();
        vboNextRow.idProperty().setValue("vboNextRow");
        achBoard.getChildren().add(vboNextRow);
        vboNextRow.setAlignment(Pos.BASELINE_CENTER);
        vboNextRow.setFocusTraversable(true);
        vboNextRow.setSpacing(1.0);
        AnchorPane.setBottomAnchor(vboNextRow, 0.0);
        AnchorPane.setLeftAnchor(vboNextRow, 0.0);
        AnchorPane.setRightAnchor(vboNextRow, 0.0);
        AnchorPane.setTopAnchor(vboNextRow, 812.0);

        vboRows = new VBoxRow();
        vboRows.idProperty().setValue("vboRows");
        achBoard.getChildren().add(vboRows);
        vboRows.setAlignment(Pos.BOTTOM_CENTER);
        vboRows.setFillWidth(false);
        vboRows.setFocusTraversable(true);
        vboRows.setSpacing(1.0);
        AnchorPane.setBottomAnchor(vboRows, 140.0);
        AnchorPane.setLeftAnchor(vboRows, 0.0);
        AnchorPane.setRightAnchor(vboRows, 0.0);
        AnchorPane.setTopAnchor(vboRows, 15.0);

        createEvents();
        vboNextRow.addEventHandler(VBoxRow.CHILDREN_ADDED, rowSlideUp);
        tbnAiSwitch.setOnAction(iASwitch);
        mniRowUp.setOnAction(rowUp);
        mniInitBoard.setOnAction(initBoard);

        aiStateProperty.bindBidirectional(tbnAiSwitch.selectedProperty());

        //BG
        //cnvGameBG.getGraphicsContext2D().drawImage(new Image(this.getClass().getResourceAsStream("/images/bg4.png")), 0, 0);

        lblScore.setText("0");

    }

    public void moveTile(final Tile tile, final Integer newIndex)
    {
        final Row row = tile.getRow();
        row.moveTile(tile, newIndex);
    }

    public void reset()
    {
        vboNextRow.getChildren().clear();
        vboRows.getChildren().clear();
        lblScore.setText("0");
    }

    /* MAIN ALGORITHM */
    // 1 - while -> check each row for falling tiles ( starting from bottom ) returns true
    // 2 - if -> check for a full row ( starting from bottom )
    // 2a true -> remove it then go to step 1
    // 2b false -> end
    public void updateRows()
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

    private void createEvents()
    {
        iASwitch = event ->
            {
                if (!tbnAiSwitch.isSelected())
                {
                    tbnAiSwitch.setText("AI OFF");
                    return;
                }
                tbnAiSwitch.setText("AI ON");
            };

        rowSlideUp = event ->
            {
                if (vboNextRow.getChildrenUnmodifiable().size() > 1)
                {
                    final Row row = (Row) vboNextRow.getChildrenUnmodifiable().get(0);
                    vboRows.getChildren().add(vboRows.getChildren().size(), row);
                    vboNextRow.getChildren().remove(row);
                    row.fitToParent();
                    row.updateTilesCoords();
                    updateRows();
                }
            };

        initBoard = event ->
            {
                vboNextRow.getChildren().clear();
                vboRows.getChildren().clear();
                while (vboRows.getChildrenUnmodifiable().size() < 5)
                {
                    rowUp.handle(null);
                }

            };
        rowUp = event ->
            {
                final Row row = new Row();
                row.setGameController(this);
                tileGenerator.genTiles(row);
                vboNextRow.addChildren(row);
                row.updateTilesCoords();
                row.fitToParent();
            };
    }

    private boolean handleFallingTiles()
    {
        final ObservableList<Node> rows = vboRows.getChildren();
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
        final ObservableList<Node> rows = vboRows.getChildren();

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

}
