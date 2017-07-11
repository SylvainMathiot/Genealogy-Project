package sylvain.view;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/*
 *
 * Listeners for making the scene's canvas draggable and zoomable
 *
 * @author Sylvain Mathiot
 *
 */
public class SceneGestures {
  private static final double MAX_SCALE = 10.0d;
  private static final double MIN_SCALE = .1d;
  private DragContext sceneDragContext = new DragContext();
  private PannableCanvas canvas;

  public SceneGestures(PannableCanvas canvas) {
    this.canvas = canvas;
  }

  public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
    return onMousePressedEventHandler;
  }

  public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
    return onMouseDraggedEventHandler;
  }

  public EventHandler<ScrollEvent> getOnScrollEventHandler() {
    return onScrollEventHandler;
  }

  private EventHandler<MouseEvent> onMousePressedEventHandler =
      new EventHandler<MouseEvent>() {

        public void handle(MouseEvent event) {

          // Left mouse button => panning
          if (!event.isPrimaryButtonDown()) {
            return;
          }

          sceneDragContext.setMouseAnchorX(event.getSceneX());
          sceneDragContext.setMouseAnchorY(event.getSceneY());

          sceneDragContext.setTranslateAnchorX(canvas.getTranslateX());
          sceneDragContext.setTranslateAnchorY(canvas.getTranslateY());
        }
      };

  private EventHandler<MouseEvent> onMouseDraggedEventHandler =
      new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {

          // Left mouse button => panning
          if (!event.isPrimaryButtonDown()) {
            return;
          }

          canvas.setTranslateX(
              sceneDragContext.getTranslateAnchorX()
                  + event.getSceneX()
                  - sceneDragContext.getMouseAnchorX());
          canvas.setTranslateY(
              sceneDragContext.getTranslateAnchorY()
                  + event.getSceneY()
                  - sceneDragContext.getMouseAnchorY());

          event.consume();
        }
      };

  /*
   * Mouse wheel handler: zoom to pivot point
   */
  private EventHandler<ScrollEvent> onScrollEventHandler =
      new EventHandler<ScrollEvent>() {

        @Override
        public void handle(ScrollEvent event) {

          double delta = 1.2;

          double scale = canvas.getScale(); // currently we only use Y, same
          // value is used for X
          double oldScale = scale;

          if (event.getDeltaY() < 0) {
            scale /= delta;
          } else {
            scale *= delta;
          }

          scale = clamp(scale, MIN_SCALE, MAX_SCALE);

          double f = (scale / oldScale) - 1;

          double dx =
              (event.getSceneX()
                  - (canvas.getBoundsInParent().getWidth() / 2
                      + canvas.getBoundsInParent().getMinX()));
          double dy =
              (event.getSceneY()
                  - (canvas.getBoundsInParent().getHeight() / 2
                      + canvas.getBoundsInParent().getMinY()));

          canvas.setScale(scale);

          // note: pivot value must be untransformed, i. e. without scaling
          canvas.setPivot(f * dx, f * dy);

          event.consume();
        }
      };

  /**
   * Clamp value.
   * 
   * @param value The clamp value
   * @param min The min value
   * @param max The max value
   * @return The clamp value, min or max value
   */
  public static double clamp(double value, double min, double max) {

    if (Double.compare(value, min) < 0) {
      return min;
    }

    if (Double.compare(value, max) > 0) {
      return max;
    }

    return value;
  }
}
