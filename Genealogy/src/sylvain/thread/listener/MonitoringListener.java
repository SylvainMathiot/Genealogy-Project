package sylvain.thread.listener;

import java.util.EventListener;

import sylvain.thread.event.PersonAddedEvent;
import sylvain.thread.event.PersonDeletedEvent;
import sylvain.thread.event.PersonModifiedEvent;

/*
 *
 * @author Sylvain Mathiot
 *
 */
public interface MonitoringListener extends EventListener {
  void personAdded(PersonAddedEvent e);

  void personModified(PersonModifiedEvent e);

  void personDeleted(PersonDeletedEvent e);
}
