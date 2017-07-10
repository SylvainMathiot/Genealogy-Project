package sylvain.view;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * 
 * Listeners for making the nodes draggable via left mouse button
 * Considers if parent is zoomed
 * 
 * @author Sylvain Mathiot
 *
 */
public class NodeGestures {
	private DragContext nodeDragContext = new DragContext();
    private PannableCanvas canvas;

    public NodeGestures(PannableCanvas canvas) {
        this.canvas = canvas;
    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

        public void handle(MouseEvent event) {

            // Right mouse button => dragging
            if(!event.isSecondaryButtonDown())
                return;

            nodeDragContext.setMouseAnchorX(event.getSceneX());
            nodeDragContext.setMouseAnchorY(event.getSceneY());

            Node node = (Node) event.getSource();

            nodeDragContext.setTranslateAnchorX(node.getTranslateX());
            nodeDragContext.setTranslateAnchorY(node.getTranslateY());

        }

    };

    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {

            // Right mouse button => dragging
            if(!event.isSecondaryButtonDown())
                return;

            double scale = canvas.getScale();

            Node node = (Node)event.getSource();

            node.setTranslateX(nodeDragContext.getTranslateAnchorX() + ((event.getSceneX() - nodeDragContext.getMouseAnchorX()) / scale));
            node.setTranslateY(nodeDragContext.getTranslateAnchorY() + ((event.getSceneY() - nodeDragContext.getMouseAnchorY()) / scale));

            event.consume();
        }
    };
}
