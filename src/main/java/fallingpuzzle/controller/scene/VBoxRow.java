package fallingpuzzle.controller.scene;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class VBoxRow extends VBox
{

    public class ChildrenAddedEvent extends Event
    {
        private static final long serialVersionUID = -1307457809601417798L;

        public ChildrenAddedEvent(final EventType<? extends Event> eventType)
        {
            super(eventType);
        }

    }

    public static final EventType<ChildrenAddedEvent> CHILDREN_ADDED = new EventType<>("CHILDREN_ADDED");

    public void addChildren(final Node node)
    {
        getChildren().add(node);
        fireEvent(new ChildrenAddedEvent(CHILDREN_ADDED));
    }
}
