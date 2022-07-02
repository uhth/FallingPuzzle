package fallingpuzzle.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import fallingpuzzle.controller.tile.TileDragController;
import fallingpuzzle.controller.tile.TileSelectController;
import fallingpuzzle.events.TileAMREvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle
{

    private static final HashMap<Integer, Tile> hashes = new HashMap<>();

    private final ArrayList<Integer> indexes;

    private final int size;

    private final TileSelectController tileSelectController;

    private final TileDragController tileDragController;

    private double baseWidth = 0;

    private double baseHeight = 0;

    private int hash;

    public Tile(final int firstIndex, final int size, final double baseWidth, final double baseHeight)
    {

        indexes = new ArrayList<>();
        indexes.add(firstIndex);
        tileSelectController = new TileSelectController(this, false);
        tileDragController = new TileDragController(this, false);

        this.size = size;
        this.baseHeight = baseHeight;
        this.baseWidth = baseWidth;
        resize();
        setColor();
        updateIndexesList();
        this.addEventHandler(TileAMREvent.TILE_MOVE, event -> updateIndexesList());

        //tile hash
        do
        {
            hash = RandomStringUtils.randomAlphabetic(30, 36).chars().reduce(0, (left, right) -> left + right);
        }
        while (hashes.putIfAbsent(hash, this) == null);

    }

    @Override
    public boolean equals(final Object obj)
    {
        return obj != null && obj.hashCode() == hashCode();
    }

    public int getFirstIndex()
    {
        return indexes.get(0);
    }

    public List<Integer> getIndexes()
    {
        return indexes;
    }

    public int getLastIndex()
    {
        return indexes.get(indexes.size() - 1);
    }

    public Row getParentRow()
    {
        if (parentProperty().get() == null)
        {
            throw new NullPointerException();
        }
        return (Row) parentProperty().get();
    }

    public int getSize()
    {
        return size;
    }

    @Override
    public int hashCode()
    {
        return hash;
    }

    public void move(final int newFirstIndex)
    {
        indexes.set(0, newFirstIndex);
    }

    public void setDraggable(final boolean draggable)
    {
        tileDragController.isDraggableProperty().set(draggable);
    }

    public void setSelectable(final boolean selectable)
    {
        tileSelectController.setSelectable(selectable);
    }

    @Override
    public String toString()
    {
        return "Tile{indexes=" + indexes + ", size=" + size + "}";
    }

    public void updateTileSize(final double baseWidth, final double baseHeight)
    {
        this.baseWidth = baseWidth;
        this.baseHeight = baseHeight;
        resize();
    }

    private void resize()
    {
        setWidth(baseWidth * size - 2);
        setHeight(baseHeight - 2);
    }

    private void setColor()
    {
        switch (size)
        {
            case 3:
                setFill(Color.RED);
                break;
            case 2:
                setFill(Color.BLUE);
                break;
            default:
                setFill(Color.BLACK);
                break;
        }
    }

    private void updateIndexesList()
    {
        final int firstIndex = indexes.get(0);
        indexes.clear();
        for (int i = 0; i < size; ++i)
        {
            indexes.add(firstIndex + i);
        }
    }

}
