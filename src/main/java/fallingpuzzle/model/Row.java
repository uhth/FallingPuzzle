package fallingpuzzle.model;

import java.util.ArrayList;
import java.util.List;

import fallingpuzzle.controller.scene.VBoxRow;
import fallingpuzzle.events.TileAMREvent;
import fallingpuzzle.exceptions.DuplicateTileException;
import fallingpuzzle.exceptions.TileNotFoundException;
import fallingpuzzle.exceptions.UnavailableIndexException;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Row extends Pane
{

    private ArrayList<Integer> freeIndexes;

    private final ArrayList<Integer> occupiedIndexes;

    public Row()
    {
        freeIndexes = new ArrayList<>();
        occupiedIndexes = new ArrayList<>();
        addEventHandler(TileAMREvent.TILE_OPS, event -> updateIndexesLists());

        updateIndexesLists();
        setFocusTraversable(true);
    }

    //ADDS TILE TO THIS ROW
    public void addTile(final Tile tile) throws UnavailableIndexException, DuplicateTileException
    {
        if (tile == null)
        {
            throw new NullPointerException();
        }
        if (getChildren().contains(tile))
        {
            throw new DuplicateTileException(tile.toString() + " already belongs to this row");
        }
        if (isOutOfBorder(tile.getFirstIndex(), tile.getSize()))
        {
            throw new UnavailableIndexException(
                    "Tile would lay outside the grid: " + (tile.getFirstIndex() + tile.getSize() - 1));
        }
        for (final Integer index : tile.getIndexes())
        {
            if (!isIndexFree(index))
            {
                throw new UnavailableIndexException("Index: " + index + " is not available.");
            }
        }
        getChildren().add(tile);
        fireEvent(new TileAMREvent(TileAMREvent.TILE_ADD));
    }

    //CHECKS FOR INDEXES i between STARTING s and DESTINATION d :    s --- i? --- d  || d --- i? --- s ( s and d excluded )
    public boolean checkForTilesBetweenTwoIndexes(final int startingIndex, final int destinationIndex, final Tile tile)
            throws UnavailableIndexException
    {

        //log.info("starting index={} destination index={}", startingIndex, destinationIndex);

        if (startingIndex == destinationIndex)
        {
            log.info("Starting index and destination index are the same");
            return true;
        }

        if (startingIndex < 0 || startingIndex > 7 || destinationIndex < 0 || destinationIndex > 7)
        {
            throw new UnavailableIndexException("INVALID INDEX NUM: " + "s -> " + startingIndex + " d ->" + destinationIndex);
        }

        if (startingIndex < destinationIndex)
        {
            for (int i = startingIndex + 1; i < destinationIndex; ++i)
            {
                if (occupiedIndexes.contains(i) && !tile.getIndexes().contains(i))
                {
                    log.info("bad index={}", i);
                    return true;
                }
            }
        }
        else
        {
            for (int i = startingIndex - 1; i > destinationIndex; --i)
            {
                if (occupiedIndexes.contains(i) && !tile.getIndexes().contains(i))
                {
                    log.info("bad index={}", i);
                    return true;
                }
            }
        }

        return false;
    }

    public void fitToParent(final VBoxRow parent)
    {
        setMinWidth(parent.getWidth());
        setMaxWidth(parent.getWidth());
        setWidth(parent.getWidth());
        if (parent.getId().equals("vboNextRow"))
        {
            setMinHeight(parent.getHeight());
            setHeight(parent.getHeight());
            setMaxHeight(parent.getHeight());
        }
        else
        {
            setMinHeight(parent.getHeight() / 10);
            setHeight(parent.getHeight() / 10);
            setMaxHeight(parent.getHeight() / 10);
        }
        for (int i = 0; i < getChildren().size(); ++i)
        {
            final Tile tile = (Tile) getChildren().get(i);
            tile.updateTileSize(getWidth() / 8, getHeight());
        }

    }

    /* get Tile by Index */
    public Tile getTile(final int index)
    {
        for (int i = 0; i < getChildren().size(); ++i)
        {
            final Tile tile = (Tile) getChildren().get(i);
            if (tile.getFirstIndex() == index)
            {
                return tile;
            }
        }
        return null;
    }

    public boolean isFull()
    {
        return freeIndexes.isEmpty();
    }

    //check if index is free
    public boolean isIndexFree(final int index)
    {
        return freeIndexes.contains(index);
    }

    //check if index is free and doesn't belong to tile
    public boolean isIndexFree(final int index, final Tile tile)
    {
        return freeIndexes.contains(index) || tile.getIndexes().contains(index);

    }

    //check for out-of-borders
    public boolean isOutOfBorder(final int firstIndex, final int tileSize)
    {
        return firstIndex + tileSize - 1 > 7;
    }

    //MOVES TILE INSIDE THIS ROW
    public void moveTile(final Tile tile, final int destIndex) throws TileNotFoundException, UnavailableIndexException
    {
        if (tile == null)
        {
            throw new NullPointerException();
        }
        if (!getChildren().contains(tile))
        {
            throw new TileNotFoundException("Tile: " + tile.toString() + " doesn't belong to this row.");
        }

        //check if dest index is free and doesn't belong to this tile
        if (!isIndexFree(destIndex, tile))
        {
            throw new UnavailableIndexException("Index: " + destIndex + " is already occupied");
        }

        //check for out-of-border (only for the right side)
        if (isOutOfBorder(destIndex, tile.getSize()))
        {
            throw new UnavailableIndexException(
                    "Destination index would put the tile outside the grid: " + (destIndex + tile.getSize() - 1));
        }
        if (checkForTilesBetweenTwoIndexes(tile.getFirstIndex(), destIndex, tile))
        {
            throw new UnavailableIndexException(
                    "Tiles be sitting between the current index and the destination index " + occupiedIndexes);
        }
        tile.move(destIndex);
        tile.fireEvent(new TileAMREvent(TileAMREvent.TILE_MOVE));
    }

    //REMOVES TILE FROM THIS ROW
    public void removeTile(final Tile tile) throws TileNotFoundException
    {
        if (tile == null)
        {
            throw new NullPointerException();
        }
        if (!getChildren().contains(tile))
        {
            throw new TileNotFoundException("Unable to find: " + tile.toString() + " inside " + toString());
        }
        getChildren().remove(tile);
        fireEvent(new TileAMREvent(TileAMREvent.TILE_REMOVE));
    }

    @Override
    public String toString()
    {
        return "Row{index=" + ((VBoxRow) getParent()).getChildrenUnmodifiable().indexOf(this) + "}";
    }

    /* Updates tile's X for it to be correctly displayed on screen */
    public void updateTilesCoords()
    {
        updateIndexesLists();
        for (int i = 0; i < getChildren().size(); ++i)
        {
            final Tile tile = (Tile) getChildren().get(i);
            tile.setX(tile.getFirstIndex() * (getWidth() / 8));
            if (getParent() != null && ((VBoxRow) getParent()).getId().equals("vboRows"))
            {
                tile.setDraggable(true);
                tile.setSelectable(true);
            }
        }
    }

    //To be called whenever Tiles get ADDED, MOVED, REMOVED to/inside/from this list
    private void updateIndexesLists()
    {
        freeIndexes.clear();
        occupiedIndexes.clear();
        for (final Node tile : getChildren())
        {
            occupiedIndexes.addAll(((Tile) tile).getIndexes());
        }
        freeIndexes = new ArrayList<>();
        freeIndexes.addAll(List.of(0, 1, 2, 3, 4, 5, 6, 7));
        freeIndexes.removeAll(occupiedIndexes);
    }

}
