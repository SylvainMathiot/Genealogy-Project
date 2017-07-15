package sylvain.thread.event;

import sylvain.model.Person;

/**
 * Folder scanned event.
 * 
 * @author Sylvain Mathiot
 */
public class FolderScannedEvent implements ScanningEventInterface {
  private Person person;

  public FolderScannedEvent(Person person) {
    this.person = person;
  }

  public Person getPerson() {
    return person;
  }
}
