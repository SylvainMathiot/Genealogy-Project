package sylvain.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sylvain.controller.LoggerSingleton;
import sylvain.controller.PropertiesSingleton;
import sylvain.thread.container.MonitoringContainer;
import sylvain.thread.container.ScanningContainer;
import sylvain.thread.event.FolderScannedEvent;
import sylvain.thread.event.PersonAddedEvent;
import sylvain.thread.event.PersonDeletedEvent;
import sylvain.thread.event.PersonModifiedEvent;
import sylvain.thread.listener.MonitoringListener;
import sylvain.thread.listener.ScanningListener;
import sylvain.thread.runnable.MonitoringThread;
import sylvain.thread.runnable.ScanningThread;

/**
 * Main class.
 *
 * @author Sylvain Mathiot
 */
public class Genealogy extends Application {
  private static final Logger logger =
      LogManager.getLogger(LoggerSingleton.getInstance().getLoggerName(Genealogy.class));
  private PannableCanvas canvas;
  private NodeGestures nodeGestures;

  /**
   * Main method.
   *
   * @param args The command line arguments array
   */
  public static void main(String[] args) {
    // Initialize logger
    System.setProperty("log4j.configurationFile", "log4j2.xml");

    // Load settings
    PropertiesSingleton.getInstance("settings.properties");

    // Initialize view
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle(PropertiesSingleton.getInstance().get("TITLE"));
    primaryStage.getIcons()
        .add(new Image(Genealogy.class.getResourceAsStream("graphic/image/genealogy128.png")));
    primaryStage.setResizable(true);
    primaryStage.centerOnScreen();

    canvas = new PannableCanvas();
    canvas.setTranslateX(0);
    canvas.setTranslateY(0);

    nodeGestures = new NodeGestures(canvas);

    Group root = new Group();
    root.getChildren().add(canvas);

    Scene scene = new Scene(root, Color.WHITE);
    scene.getStylesheets()
        .add(Genealogy.class.getResource("graphic/stylesheet.css").toExternalForm());

    SceneGestures sceneGestures = new SceneGestures(canvas);
    scene.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
    scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
    scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

    primaryStage.setOnCloseRequest(event -> {
      try {
        stop();
      } catch (Exception e) {
        logger.error(e);
      }
    });

    // Start database folder scanning thread
    Thread scanningThread = new Thread(new ScanningThread(scanningContainer()));
    scanningThread.setDaemon(false);
    scanningThread.start();
    scanningThread.join();

    // Start database folder monitoring thread
    Thread monitoringThread = new Thread(new MonitoringThread(monitoringContainer()));
    monitoringThread.setDaemon(true);
    monitoringThread.start();

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private ScanningContainer scanningContainer() {
    // Configure database folder scanning listener
    ScanningContainer databaseFolderScanning =
        new ScanningContainer(PropertiesSingleton.getInstance().get("DATABASE.FOLDER"));

    databaseFolderScanning.addScanningListener(new ScanningListener() {

      @Override
      public void folderScanned(FolderScannedEvent e) {
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            logger.info("Person added : " + e.getPerson());
            canvas.getChildren().add(e.getPerson().build(nodeGestures));
          }
        });
      }
    });

    return databaseFolderScanning;
  }

  private MonitoringContainer monitoringContainer() {
    // Configure database folder monitoring listener
    MonitoringContainer databaseFolderMonitoring = new MonitoringContainer();

    databaseFolderMonitoring.addMonitoringListener(new MonitoringListener() {

      @Override
      public void personAdded(PersonAddedEvent e) {
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            logger.info("Person added : " + e.getPerson());
            canvas.getChildren().add(e.getPerson().build(nodeGestures));
          }
        });
      }

      @Override
      public void personModified(PersonModifiedEvent e) {
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            logger.info("Person modified : " + e.getPerson());
            if (e.getPerson().getPane() != null
                && canvas.getChildren().contains(e.getPerson().getPane())) {
              canvas.getChildren().remove(e.getPerson().getPane());
              canvas.getChildren().add(e.getPerson().build(nodeGestures));
            }
          }
        });
      }

      @Override
      public void personDeleted(PersonDeletedEvent e) {
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            logger.info("Person deleted : " + e.getPerson());
            if (e.getPerson().getPane() != null
                && canvas.getChildren().contains(e.getPerson().getPane())) {
              canvas.getChildren().remove(e.getPerson().getPane());
            }
          }
        });
      }
    });

    return databaseFolderMonitoring;
  }
}
