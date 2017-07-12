package sylvain.thread.container;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.swing.event.EventListenerList;

import org.apache.commons.lang3.StringUtils;

import sylvain.model.DataModel;
import sylvain.model.Person;
import sylvain.thread.event.FolderScannedEvent;
import sylvain.thread.event.ScanningEventInterface;
import sylvain.thread.listener.ScanningListener;

/*
 *
 * @author Sylvain Mathiot
 *
 */
public class ScanningContainer implements Serializable {
  private static final long serialVersionUID = 68785075004493324L;
  private transient EventListenerList listeners;
  private String target;

  /**
   * Constructor.
   * 
   * @param target The database folder
   */
  public ScanningContainer(String target) {
    this.target = target;
    readResolve();
  }

  public String getTarget() {
    return target;
  }

  private ScanningContainer readResolve() {
    listeners = new EventListenerList();
    return this;
  }

  private ScanningListener[] getScanningListeners() {
    return listeners.getListeners(ScanningListener.class);
  }

  public void addScanningListener(ScanningListener scanningListener) {
    listeners.add(ScanningListener.class, scanningListener);
  }

  protected void fireFolderScannedEvent(ScanningEventInterface event) {
    Arrays.asList(getScanningListeners()).stream()
        .filter(listener -> event instanceof FolderScannedEvent)
        .forEach(listener -> listener.folderScanned((FolderScannedEvent) event));
  }

  /**
   * Parse the database folder to initialize the data model.
   */
  public void scan() {
    File[] list = Paths.get(target).toFile().exists() ? Paths.get(target).toFile().listFiles()
        : new File[] {};
    Arrays.asList(list).stream()
        .filter(f -> f.isDirectory() && StringUtils.isNumeric(f.getName().subSequence(0, 4)))
        .forEach(f -> {
          Person person = DataModel.getInstance().add(f);
          fireFolderScannedEvent(new FolderScannedEvent(person));
        });
  }
}
