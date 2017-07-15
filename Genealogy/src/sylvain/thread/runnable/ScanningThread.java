package sylvain.thread.runnable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sylvain.controller.LoggerSingleton;
import sylvain.thread.container.ScanningContainer;

/**
 * Scanning thread.
 * 
 * @author Sylvain Mathiot
 */
public class ScanningThread implements Runnable {
  private static final Logger logger =
      LogManager.getLogger(LoggerSingleton.getInstance().getLoggerName(ScanningThread.class));
  private ScanningContainer scanningContainer;

  public ScanningThread(ScanningContainer scanningContainer) {
    super();
    this.scanningContainer = scanningContainer;
  }

  /**
   * Start the thread.
   */
  public void run() {
    try {
      Thread.currentThread().setName(this.getClass().getSimpleName());

      scanningContainer.scan();
    } catch (Exception e) {
      logger.error(e);
    }
  }
}
