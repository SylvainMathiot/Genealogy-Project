package sylvain.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;

/*
 *
 * A zoomable and pannable canvas
 *
 * @author Sylvain Mathiot
 *
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

  /*
   * Add a grid to the canvas, send it to back
   */
  // public void addGrid() {
  // double w = getBoundsInLocal().getWidth();
  // double h = getBoundsInLocal().getHeight();
  //
  // Canvas grid = new Canvas(w, h);
  // grid.setMouseTransparent(true);
  //
  // GraphicsContext gc = grid.getGraphicsContext2D();
  // gc.setStroke(Color.GRAY);
  // gc.setLineWidth(1);
  //
  // // Draw grid lines
  // double offset = 50;
  // for(double i=offset; i < w; i+=offset) {
  // gc.strokeLine(i, 0, i, h);
  // gc.strokeLine(0, i, w, i);
  // }
  //
  // getChildren().add(grid);
  //
  // grid.toBack();
  // }

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
