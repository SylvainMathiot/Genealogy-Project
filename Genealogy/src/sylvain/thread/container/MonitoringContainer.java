package sylvain.thread.container;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.Arrays;

import javax.swing.event.EventListenerList;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import sylvain.controller.PropertiesSingleton;
import sylvain.controller.WatchServiceSingleton;
import sylvain.model.DataModel;
import sylvain.model.Person;
import sylvain.thread.event.MonitoringEventInterface;
import sylvain.thread.event.PersonAddedEvent;
import sylvain.thread.event.PersonDeletedEvent;
import sylvain.thread.event.PersonModifiedEvent;
import sylvain.thread.listener.MonitoringListener;

/**
 * Monitoring thread container.
 * 
 * @author Sylvain Mathiot
 */
public class MonitoringContainer implements Serializable {
  private static final long serialVersionUID = 4863785957098726655L;
  private transient EventListenerList listeners;

  public MonitoringContainer() {
    readResolve();
  }

  private MonitoringContainer readResolve() {
    listeners = new EventListenerList();
    return this;
  }

  private MonitoringListener[] getMonitoringListeners() {
    return listeners.getListeners(MonitoringListener.class);
  }

  public void addMonitoringListener(MonitoringListener monitoringListener) {
    listeners.add(MonitoringListener.class, monitoringListener);
  }

  protected void firePersonAddedEvent(MonitoringEventInterface event) {
    Arrays.asList(getMonitoringListeners()).stream()
        .filter(listener -> event instanceof PersonAddedEvent)
        .forEach(listener -> listener.personAdded((PersonAddedEvent) event));
  }

  protected void firePersonModifiedEvent(MonitoringEventInterface event) {
    Arrays.asList(getMonitoringListeners()).stream()
        .filter(listener -> event instanceof PersonModifiedEvent)
        .forEach(listener -> listener.personModified((PersonModifiedEvent) event));
  }

  protected void firePersonDeletedEvent(MonitoringEventInterface event) {
    Arrays.asList(getMonitoringListeners()).stream()
        .filter(listener -> event instanceof PersonDeletedEvent)
        .forEach(listener -> listener.personDeleted((PersonDeletedEvent) event));
  }

  /**
   * Monitor the database folder for create, modify and delete events.
   * 
   * @throws IOException File system exception
   * @throws InterruptedException Thread exception
   */
  public void monitor() throws IOException, InterruptedException {
    WatchKey watchKey;
    boolean valid = true;
    File rootFolder = new File(PropertiesSingleton.getInstance().get("DATABASE.FOLDER"));

    do {
      if (rootFolder == null || !rootFolder.exists() || !Files.isDirectory(rootFolder.toPath())) {
        continue;
      }
      watchKey = WatchServiceSingleton.getInstance().getWatchService().take();

      for (WatchEvent<?> event : watchKey.pollEvents()) {
        Thread.sleep(2000);

        if (OVERFLOW.equals(event.kind())) {
          continue;
        } else if (ENTRY_CREATE.equals(event.kind())) {
          doCreate(WatchServiceSingleton.getInstance().resolve(watchKey, event));
        } else if (ENTRY_MODIFY.equals(event.kind())) {
          doModify(WatchServiceSingleton.getInstance().resolve(watchKey, event));
        } else if (ENTRY_DELETE.equals(event.kind())) {
          doDelete((Path) event.context());
        }
      }

      valid = watchKey.reset();

    } while (valid);
  }

  private boolean isPersonDirectory(Path path) {
    return path != null
        && StringUtils
            .isNumeric(path.getName(path.getNameCount() - 1).toFile().getName().subSequence(0, 4))
        && FilenameUtils.getExtension(path.getName(path.getNameCount() - 1).toFile().getName())
            .isEmpty();
  }

  private boolean isRootDirectory(Path path) {
    return path != null && path.toAbsolutePath().toString()
        .equalsIgnoreCase(PropertiesSingleton.getInstance().get("DATABASE.FOLDER"));
  }

  private boolean isIdentityFile(Path path) {
    return path != null && Files.isRegularFile(path) && path.getName(path.getNameCount() - 1)
        .toString().equalsIgnoreCase(PropertiesSingleton.getInstance().get("IDENTITY.FILENAME"));
  }

  private void doCreate(Path child) {
    if (Files.isDirectory(child) && isRootDirectory(child.getParent())) {
      WatchServiceSingleton.getInstance().register(child);

      if (isPersonDirectory(child)) {
        Person person = DataModel.getInstance().add(child.toFile());
        firePersonAddedEvent(new PersonAddedEvent(person));
      }
    }

    if (isIdentityFile(child) && isPersonDirectory(child.getParent())
        && isRootDirectory(child.getParent().getParent())) {
      Person person = DataModel.getInstance().add(child.getParent().toFile());
      firePersonAddedEvent(new PersonAddedEvent(person));
    }
  }

  private void doModify(Path child) {
    if (isIdentityFile(child) && isPersonDirectory(child.getParent())
        && isRootDirectory(child.getParent().getParent())) {
      Person person = DataModel.getInstance().add(child.getParent().toFile());
      firePersonModifiedEvent(new PersonModifiedEvent(person));
    }
  }

  private void doDelete(Path child) {
    if (isPersonDirectory(child)) {
      Person person = DataModel.getInstance().delete(child.toFile());
      firePersonDeletedEvent(new PersonDeletedEvent(person));
    }
  }
}
