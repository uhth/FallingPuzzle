package fallingpuzzle.controller.scene;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import fallingpuzzle.controller.Controller;
import fallingpuzzle.controller.ia.AIService;
import fallingpuzzle.controller.ia.DLVAdapter;
import fallingpuzzle.controller.ia.DLVController;
import fallingpuzzle.events.TileAMREvent;
import fallingpuzzle.exceptions.TileException;
import fallingpuzzle.model.Row;
import fallingpuzzle.model.Tile;
import fallingpuzzle.model.TileGenerator;
import fallingpuzzle.model.TileMove;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
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
        log.info("{}, {}, full={}", selectedTile.toString(), selectedTile.getParentRow().toString(),
                selectedTile.getParentRow().isFull());
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
            try
            {
                row.moveTile(tileMove.getTile(), newIndex);

            }
            catch (final TileException tileException)
            {
                tbnAiSwitch.fire();
                tileMove.getTile().setFill(Color.GREEN);
                final BackgroundFill[] bgFills = new BackgroundFill[1];
                bgFills[0] = new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY);
                final Background bg = new Background(bgFills);
                row.setBackground(bg);
                log.warn("{}", tileException.getMessage());
                tileException.printStackTrace();
            }
            catch (final Exception everyOtherException)
            {
                everyOtherException.printStackTrace();
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

        vboNextRow.addEventHandler(VBoxRow.CHILDREN_ADDED, event -> rowSlideUp());
        vboRows.addEventHandler(TileAMREvent.TILE_MOVE, event ->
            {
                updateRows();
                rowUp();
            });
        tbnAiSwitch.setOnAction(event ->
            {
                final String state = tbnAiSwitch.isSelected() ? "AI ON" : "AI OFF";
                tbnAiSwitch.setText(state);
            });
        mniRowUp.setOnAction(event -> rowUp());
        mniInitBoard.setOnAction(event -> initBoard());

        aiStateProperty.bindBidirectional(tbnAiSwitch.selectedProperty());

        //BG
        //cnvGameBG.getGraphicsContext2D().drawImage(new Image(this.getClass().getResourceAsStream("/images/bg4.png")), 0, 0);

        mniCallAi = new MenuItem();
        mnbOptions.getItems().add(mniCallAi);
        mniCallAi.setText("AI once");
        mniCallAi.setOnAction(event -> callAi());

        lblScore.setText("0");
        sldAiLantecy.valueProperty().addListener((a, b, c) -> lblSlider.setText("" + c.intValue()));
        lblSlider.setText("" + sldAiLantecy.valueProperty().intValue());

    }

    public void reset()
    {
        try
        {
            vboNextRow.getChildren().clear();
            vboRows.getChildren().clear();
            lblScore.setText("0");
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            aiService.restart();
            throw e;
        }
    }

    private boolean handleFallingTiles()
    {
        final AtomicBoolean falling = new AtomicBoolean(false);
        for (int i = vboRows.getChildren().size() - 2; i >= 0; --i)
        {
            final ArrayList<Node> tilesToMove = new ArrayList<>();
            final Row currentRow = (Row) vboRows.getChildren().get(i);
            final Row bottomRow = (Row) vboRows.getChildren().get(i + 1);
            currentRow.getChildren().forEach(tilesToMove::add);
            for (final Node tile : tilesToMove)
            {
                try
                {
                    bottomRow.addTile((Tile) tile);
                    falling.set(true);
                    currentRow.removeTile((Tile) tile);
                }
                catch (final TileException e)
                {
                }
            }
        }
        return falling.get();
    }

    private boolean handleFullRows()
    {
        for (int i = vboRows.getChildren().size() - 1; i >= 0; --i)
        {
            if (((Row) vboRows.getChildren().get(i)).isFull())
            {
                log.info("{} removed", vboRows.getChildren().get(i).toString());
                vboRows.getChildren().remove(i);
                addScore(1);
                return true;
            }
        }
        return false;
    }

    private void initBoard()
    {
        reset();
        while (vboRows.getChildrenUnmodifiable().size() < 5)
        {
            rowUp();
        }
        lblScore.setText("0");
    }

    private void rowSlideUp()
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
    }

    private void rowUp()
    {
        try
        {
            if (vboRows.getChildren().size() > 9)
            {
                initBoard();
            }
            final Row row = new Row();
            tileGenerator.genTiles(row);
            vboNextRow.addChildren(row);
            row.fitToParent(vboNextRow);
            row.updateTilesCoords();
            updateRows();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    /* MAIN ALGORITHM */
    // 1 - while -> check each row for falling tiles
    // 2 - if -> check for a full row ( starting from bottom )
    // 2a true -> remove it then go to step 1
    // 2b false -> end
    private void updateRows() // <-- TODO cycling must be corrected
    {
        boolean fullRows = true;
        boolean fallingTiles = true;
        do
        {
            fallingTiles = handleFallingTiles();
            vboRows.getChildren().forEach(node -> ((Row) node).updateTilesCoords());
        }
        while (fallingTiles);
        do
        {
            fullRows = handleFullRows();
            vboRows.getChildren().forEach(node -> ((Row) node).updateTilesCoords());
        }
        while (fullRows);
    }

}
