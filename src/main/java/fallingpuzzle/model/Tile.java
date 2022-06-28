package fallingpuzzle.model;

import java.util.ArrayList;

import fallingpuzzle.controller.tile.TileDragController;
import fallingpuzzle.controller.tile.TileSelectController;
import fallingpuzzle.model.utils.IndexChangeListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle
{

    private final ArrayList<Integer> indexes;

    private final IntegerProperty firstIndex;

    private int nCell;

    private final TileSelectController tileSelectController;

    private final TileDragController tileDragController;

    private double baseWidth = 0;

    private double baseHeight = 0;

    public Tile(final int firstIndex, final int nCell)
    {
        this(firstIndex, nCell, 0.0, 0.0);
    }

    public Tile(final int firstIndex, final int nCell, final double baseWidth, final double baseHeight)
    {

        tileSelectController = new TileSelectController(this, false);
        tileDragController = new TileDragController(this);
        tileDragController.isDraggableProperty().set(false);
        indexes = new ArrayList<Integer>();
        this.firstIndex = new SimpleIntegerProperty(10);

        //Updating first index will also update indexList
        this.firstIndex.addListener(new IndexChangeListener(this));

        this.nCell = nCell;
        this.baseHeight = baseHeight;
        this.baseWidth = baseWidth;
        resize();

        this.firstIndex.set(firstIndex);
    }

    public Integer getFirstIndex()
    {
        return firstIndex.get();
    }

    public ArrayList<Integer> getIndexes()
    {
        return indexes;
    }

    public int getNCell()
    {
        return nCell;
    }

    public void move(final int index)
    {
        firstIndex.set(index);
    }

    public void setDraggable(final boolean draggable)
    {
        tileDragController.isDraggableProperty().set(draggable);
    }

    public void setNCell(final int nCell)
    {
        this.nCell = nCell;
        updateIndexes(firstIndex.getValue());
        resize();
    }

    public void setSelectable(final boolean selectable)
    {
        tileSelectController.setSelectable(selectable);
    }

    //Used by listener to update tile indexes as the first index changes
    public void updateIndexes(final Number newValue)
    {
        indexes.clear();
        for (int i = 0; i < nCell; ++i)
        {
            indexes.add(newValue.intValue() + i);
        }
    }

    public void updateTileSize(final double baseWidth, final double baseHeight)
    {
        this.baseWidth = baseWidth;
        this.baseHeight = baseHeight;
        resize();
    }

    private void resize()
    {
        setWidth(baseWidth * nCell - 2);
        setHeight(baseHeight - 2);
    }

}
