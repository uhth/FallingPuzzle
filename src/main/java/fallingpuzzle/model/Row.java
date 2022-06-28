package fallingpuzzle.model;

import java.util.ArrayList;
import java.util.List;

import fallingpuzzle.controller.scene.GameController;
import fallingpuzzle.controller.scene.VBoxRow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Row extends Pane
{

    private GameController gameController;

    public Row()
    {
        setFocusTraversable(true);
    }

    /* Checks for each tile already in if the tested one has any index in common */
    public boolean collidesWithOtherTiles(final Tile tileToTest)
    {
        if (getChildren().isEmpty())
        {
            return false;
        }
        final ArrayList<Integer> unavailableIndexes = new ArrayList<Integer>();
        for (int i = 0; i < getChildren().size(); ++i)
        {
            final Tile tile = (Tile) getChildren().get(i);
            if (!tile.equals(tileToTest))
            {
                unavailableIndexes.addAll(tile.getIndexes());
            }
        }

        for (int i = 0; i < tileToTest.getIndexes().size(); ++i)
        {
            if (unavailableIndexes.contains(tileToTest.getIndexes().get(i)) || i < 0 || i > 7)
            {
                log.info("collides with : {} | tesing {} ", tileToTest.getIndexes().get(i), tileToTest.getIndexes());
                return true;
            }
        }
        return false;
    }

    /* Checks for each tile already in if the tested one has any index in common if you try to move it */
    public boolean collidesWithOtherTiles(final Tile tileToTest, final int mockFirstIndex)
    {
        final ArrayList<Integer> unavailableIndexes = new ArrayList<Integer>();
        for (int i = 0; i < getChildren().size(); ++i)
        {
            final Tile tile = (Tile) getChildren().get(i);
            if (!tile.equals(tileToTest))
            {
                unavailableIndexes.addAll(tile.getIndexes());
            }
        }

        final int trueFirstIndex = tileToTest.getFirstIndex();
        tileToTest.move(mockFirstIndex);

        for (int i = 0; i < tileToTest.getIndexes().size(); ++i)
        {
            if (unavailableIndexes.contains(tileToTest.getIndexes().get(i)) || i < 0 || i > 7)
            {
                tileToTest.move(trueFirstIndex);
                return true;
            }
        }
        tileToTest.move(trueFirstIndex);
        return false;
    }

    @Override
    public boolean equals(final Object row)
    {
        if (row instanceof Row)
        {
            return getChildren().containsAll(((Row) row).getChildren());
        }
        return false;
    }

    public void fitToParent()
    {
        final VBox parent = (VBoxRow) getParent();
        setMinWidth(parent.getWidth());
        setMaxWidth(parent.getWidth());
        setMaxHeight(parent.getHeight() / 10);
        setMinHeight(parent.getHeight() / 10);
        setWidth(parent.getWidth());
        setHeight(parent.getHeight() / 10);

        for (int i = 0; i < getChildren().size(); ++i)
        {
            final Tile tile = (Tile) getChildren().get(i);
            tile.updateTileSize(getWidth() / 8, getHeight());
        }

    }

    public GameController getGameController()
    {
        return gameController;
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

    /* Only inserts tiles which can fit inside this row */
    public void insert(final List<Tile> tilesToInsert)
    {
        for (int i = 0; i < tilesToInsert.size(); ++i)
        {
            final Tile tile = tilesToInsert.get(i);
            if (!collidesWithOtherTiles(tile))
            {
                getChildren().add(tile);
            }
        }
        updateTilesCoords();
    }

    /* Only inserts a tile which can fit inside this row */
    public void insert(final Tile tileToInsert, final boolean checkForCollision)
    {
        if (!checkForCollision || !collidesWithOtherTiles(tileToInsert))
        {
            getChildren().add(tileToInsert);
        }
        updateTilesCoords();
    }

    public boolean isFull()
    {
        int indexCount = 0;
        for (int i = 0; i < getChildren().size(); ++i)
        {
            indexCount += ((Tile) getChildren().get(i)).getIndexes().size();
        }
        if (indexCount == 8)
        {
            return true;
        }
        return false;
    }

    /* Used by controller to move a tile */
    public boolean moveTile(final Tile tile, final int index)
    {
        final int oldIndex = tile.getFirstIndex();
        if (tilesInBeetween(tile, index))
        {
            log.info("{}", "ILLEGAL MOVE");
            return false;
        }
        tile.move(index);
        if (collidesWithOtherTiles(tile))
        {
            tile.move(oldIndex);
            log.info("{}", "ILLEGAL MOVE");
            log.info("row: {} move is: {} to {}", gameController.getRowIndex(this), oldIndex, index);
            return false;
        }
        else
        {
            log.info("row: {} move is: {} to {}", gameController.getRowIndex(this), oldIndex, index);
            return true;
        }
    }

    public void remove(final Tile tile)
    {
        getChildren().remove(tile);
    }

    public void setGameController(final GameController gameController)
    {
        this.gameController = gameController;
    }

    //if unavailable indexes contains any index in between mock and real return false;
    public boolean tilesInBeetween(final Tile tileToTest, final int mockIndex)
    {
        final ArrayList<Integer> unavailableIndexes = new ArrayList<Integer>();
        final int realIndex = tileToTest.getFirstIndex();
        for (int i = 0; i < getChildren().size(); ++i)
        {
            final Tile tile = (Tile) getChildren().get(i);
            if (!tile.equals(tileToTest))
            {
                unavailableIndexes.addAll(tile.getIndexes());
            }
        }
        unavailableIndexes.sort((o1, o2) -> o1 > o2 ? o1 : o2);
        //case mockIndex < realIndex -> test mockIndex + tile size + 1
        if (mockIndex < realIndex)

        {
            for (int i = mockIndex + tileToTest.getIndexes().size() + 1; i < realIndex; ++i)
            {
                if (unavailableIndexes.contains(i))
                {
                    return true;
                }
            }
        }
        else if (mockIndex > realIndex)
        {
            for (int i = realIndex + 1; i < mockIndex; ++i)
            {
                if (unavailableIndexes.contains(i))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /* Updates tile's X for it to be correctly displayed on screen */
    public void updateTilesCoords()
    {
        for (int i = 0; i < getChildren().size(); ++i)
        {
            final Tile tile = (Tile) getChildren().get(i);
            tile.setX(tile.getFirstIndex() * (getWidth() / 8));
        }
    }

}
