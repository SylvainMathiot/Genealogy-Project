package sylvain.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;

/**
 * A zoomable and pannable canvas.
 *
 * @author Sylvain Mathiot
 */
public class PannableCanvas extends Pane {
  private DoubleProperty myScale = new SimpleDoubleProperty(1.0);

  /**
   * Constructor.
   */
  public PannableCanvas() {
    // Add scale transform
    scaleXProperty().bind(myScale);
    scaleYProperty().bind(myScale);
  }

  public double getScale() {
    return myScale.get();
  }

  public void setScale(double scale) {
    myScale.set(scale);
  }

  public void setPivot(double x, double y) {
    setTranslateX(getTranslateX() - x);
    setTranslateY(getTranslateY() - y);
  }
}
