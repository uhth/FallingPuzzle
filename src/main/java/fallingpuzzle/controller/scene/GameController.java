package fallingpuzzle.controller.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
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
    private Slider sldAiLantecy;

    @FXML
    private MenuItem mniInitBoard;

    @FXML
    private MenuButton mnbOptions;

    @FXML
    private MenuItem mniRowUp;

    @FXML
    private Label lblSlider;

    @FXML
    private Label lblScore;

    @FXML
    private AnchorPane achRoot;

    private MenuItem mniCallAi;

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

    public AtomicBoolean readyForAi = new AtomicBoolean(true);

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
            final Row row = getRowByIndex(tileMove.getRowIndex());
            final Integer newIndex = tileMove.getNewIndex();
            if (row.moveTile(tileMove.getTile(), newIndex))
            {
                rowUp(true);
                updateRows(true);
            }
            else
            {
                tileMove.getTile().setFill(Color.GREEN);
                final BackgroundFill[] bgFills = new BackgroundFill[1];
                bgFills[0] = new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY);
                final Background bg = new Background(bgFills);
                row.setBackground(bg);
                log.warn("tile: {}, indexes: {}", tileMove.getTile(), tileMove.getTile().getIndexes());
                tbnAiSwitch.fire();
            }
        }
        readyForAi.set(true);
    }

    public DoubleProperty getAiSliderValueProperty()
    {
        return sldAiLantecy.valueProperty();
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

    public void initBoard()
    {
        initBoard.handle(null);
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

        //   achRoot.getStylesheets().add(getClass().getResource("/css/style.css").toString());

        vboNextRow = new VBoxRow();
        vboNextRow.idProperty().setValue("vboNextRow");
        achBoard.getChildren().add(vboNextRow);
        vboNextRow.setAlignment(Pos.BASELINE_CENTER);
        vboNextRow.setFocusTraversable(true);
        vboNextRow.setSpacing(1.0);
        vboNextRow.setMinHeight(73.0);
        vboNextRow.setMaxHeight(73.0);
        AnchorPane.setBottomAnchor(vboNextRow, 50.0);
        AnchorPane.setLeftAnchor(vboNextRow, 0.0);
        AnchorPane.setRightAnchor(vboNextRow, 0.0);
        AnchorPane.setTopAnchor(vboNextRow, 730.0); //812

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

        achBoard.setFocusTraversable(true);

        createEvents();
        vboNextRow.addEventHandler(VBoxRow.CHILDREN_ADDED, rowSlideUp);
        tbnAiSwitch.setOnAction(iASwitch);
        mniRowUp.setOnAction(rowUp);
        mniInitBoard.setOnAction(initBoard);

        aiStateProperty.bindBidirectional(tbnAiSwitch.selectedProperty());

        //BG
        //cnvGameBG.getGraphicsContext2D().drawImage(new Image(this.getClass().getResourceAsStream("/images/bg4.png")), 0, 0);

        mniCallAi = new MenuItem();
        mnbOptions.getItems().add(mniCallAi);
        mniCallAi.setText("AI once");
        mniCallAi.setOnAction(event -> callAi());

        lblScore.setText("0");
        vboRows.requestFocus();
        sldAiLantecy.valueProperty().addListener((a, b, c) -> lblSlider.setText("" + c.intValue()));
        lblSlider.setText("" + sldAiLantecy.valueProperty().intValue());

    }

    public void moveTile(final Tile tile, final Integer newIndex, final Row row)
    {
        row.moveTile(tile, newIndex);
    }

    public void reset()
    {
        try
        {
            vboNextRow.getChildren().clear();
            vboRows.getChildren().clear();
            initBoard();
            lblScore.setText("0");
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            aiService.restart();
            throw e;
        }
    }

    public void rowUp(final boolean score)
    {
        try
        {
            if (vboRows.getChildren().size() > 9)
            {
                reset();
            }
            final Row row = new Row();
            row.setGameController(this);
            tileGenerator.genTiles(row);
            vboNextRow.addChildren(row);
            row.updateTilesCoords();
            row.fitToParent(vboNextRow);
            row.updateTilesCoords();
            updateRows(score);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    /* MAIN ALGORITHM */
    // 1 - while -> check each row for falling tiles ( starting from bottom ) returns true
    // 2 - if -> check for a full row ( starting from bottom )
    // 2a true -> remove it then go to step 1
    // 2b false -> end
    public void updateRows(final boolean addScore)
    {
        int score = 0;

        boolean cycle = true;
        while (cycle)
        {
            cycle = false;
            //step 1
            boolean fallingTiles = false;
            do
            {
                fallingTiles = handleFallingTiles();
            }
            while (fallingTiles);

            //step 2
            if (handleFullRows())
            {
                cycle = true;
                ++score;
            }
        }
        if (addScore)
        {
            addScore(score);
        }

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
                    try
                    {
                        final Row row = (Row) vboNextRow.getChildrenUnmodifiable().get(0);
                        vboRows.getChildren().add(row);
                        vboNextRow.getChildren().remove(row);
                        row.fitToParent(vboRows);
                        row.updateTilesCoords();
                    }
                    catch (final Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            };

        initBoard = event ->
            {
                lblScore.setText("0");
                vboNextRow.getChildren().clear();
                vboRows.getChildren().clear();
                while (vboRows.getChildrenUnmodifiable().size() < 5)
                {
                    rowUp.handle(null);
                }

            };
        rowUp = event -> rowUp(false);
    }

    private boolean handleFallingTiles()
    {
        final ObservableList<Node> rows = vboRows.getChildren();
        final AtomicBoolean falling = new AtomicBoolean(false);
        for (int i = rows.size() - 2; i >= 0; --i)
        {
            final List<Node> tilesToInsert = new ArrayList<>();
            final Row currentRow = (Row) rows.get(i);
            final Row nextRow = (Row) rows.get(i + 1);
            currentRow.getChildren().forEach(node ->
                {
                    if (!nextRow.collidesWithOtherTiles((Tile) node))
                    {
                        tilesToInsert.add(node);
                        falling.set(true);
                    }
                });
            tilesToInsert.forEach(node ->
                {
                    nextRow.insert((Tile) node, false);
                    currentRow.remove((Tile) node);
                });
            tilesToInsert.clear();
        }
        return falling.get();
    }

    private boolean handleFullRows()
    {
        boolean value = false;
        try
        {
            final ObservableList<Node> rows = vboRows.getChildren();
            final List<Node> rowsToRemove = new ArrayList<>();
            for (int i = rows.size() - 1; i >= 0; --i)
            {
                final Row currentRow = (Row) rows.get(i);
                if (currentRow.isFull())
                {
                    rowsToRemove.add(currentRow);
                    value = true;
                }
            }
            rows.removeAll(rowsToRemove);
        }
        catch (

        final Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }

}
