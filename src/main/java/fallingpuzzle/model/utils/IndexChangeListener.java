package fallingpuzzle.model.utils;

import fallingpuzzle.model.Tile;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class IndexChangeListener implements ChangeListener<Number>
{

    private final Tile tile;

    public IndexChangeListener(final Tile tile)
    {
        this.tile = tile;
    }

    @Override
    public void changed(final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue)
    {
        tile.updateIndexes(newValue);
    }

}
