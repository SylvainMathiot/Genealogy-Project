package sylvain.view;

import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

/*
 *
 * @author Sylvain Mathiot
 *
 */
public class Person extends Rectangle {

  /**
   * Constructor.
   * 
   * @param nodeGestures The node gestures
   */
  public Person(NodeGestures nodeGestures) {
    setWidth(200);
    setHeight(100);
    setArcHeight(15);
    setArcWidth(15);
    addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
    addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
  }
}
