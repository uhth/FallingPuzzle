package fallingpuzzle.controller.tile;

import fallingpuzzle.exceptions.TileException;
import fallingpuzzle.model.Row;
import fallingpuzzle.model.Tile;
import javafx.scene.Node;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TileDragController extends DragController
{

    public TileDragController(final Node target, final boolean isDraggable)
    {

        super(target, isDraggable);
    }

    @Override
    protected void createHandlers()
    {

        setAnchor = event ->
            {
                if (event.isPrimaryButtonDown())
                {

                    cycleStatus = ACTIVE;
                    anchorX = event.getSceneX();
                    mouseOffsetFromNodeZeroX = event.getX();
                }
                if (event.isSecondaryButtonDown())
                {
                    cycleStatus = INACTIVE;
                    target.setTranslateX(0);
                }
            };

        updatePositionOnDrag = event ->
            {
                if (cycleStatus != INACTIVE)
                {
                    final double newX = event.getSceneX() - anchorX;
                    if (!outSideParentBounds(target.getLayoutBounds(), target.getParent().getLayoutBounds(),
                            target.getLayoutX() + newX))
                    {
                        target.setTranslateX(newX);
                    }
                }
            };

        commitPositionOnRelease = event ->
            {

                if (cycleStatus != INACTIVE)
                {

                    final double translate = target.getTranslateX();
                    final double absTranslate = Math.abs(translate);
                    final boolean isTranslatePos = translate >= 0 ? true : false;
                    final Tile tile = (Tile) target;
                    final Row row = (Row) tile.getParent();
                    int oldIndex = tile.getFirstIndex();
                    int deltaIndex = 0;

                    if (absTranslate >= 35 && absTranslate < 106)
                    {
                        deltaIndex += 1;
                    }
                    else if (absTranslate >= 106 && absTranslate < 177)
                    {
                        deltaIndex += 2;
                    }
                    else if (absTranslate >= 177 && absTranslate < 248)
                    {
                        deltaIndex += 3;
                    }
                    else if (absTranslate >= 248 && absTranslate < 319)
                    {
                        deltaIndex += 4;
                    }
                    else if (absTranslate >= 319 && absTranslate < 390)
                    {
                        deltaIndex += 5;
                    }
                    else if (absTranslate >= 390 && absTranslate < 455)
                    {
                        deltaIndex += 6;
                    }
                    else if (absTranslate >= 455)
                    {
                        deltaIndex += 7;
                    }

                    deltaIndex *= isTranslatePos ? 1 : -1;
                    final int newIndex = oldIndex += deltaIndex;

                    target.setTranslateX(0);

                    final Tile targetTile = (Tile) target;

                    if (deltaIndex != 0)
                    {
                        try
                        {
                            targetTile.getParentRow().moveTile(targetTile, newIndex);
                        }
                        catch (final TileException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            };

    }

}
