package fallingpuzzle.events;

import javafx.event.Event;
import javafx.event.EventType;

public class TileAMREvent extends Event
{
    private static final long serialVersionUID = -6570534944365043778L;

    public static final EventType<TileAMREvent> TILE_OPS = new EventType<>("TILE_MOVE");

    public static final EventType<TileAMREvent> TILE_ADD = new EventType<>(TILE_OPS, "TILE_ADD");

    public static final EventType<TileAMREvent> TILE_REMOVE = new EventType<>(TILE_OPS, "TILE_REMOVE");

    public static final EventType<TileAMREvent> TILE_MOVE = new EventType<>(TILE_OPS, "TILE_MOVE");

    public TileAMREvent(final EventType<TileAMREvent> eventType)
    {
        super(eventType);
    }

}
