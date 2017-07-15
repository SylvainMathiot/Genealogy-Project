package sylvain.thread.event;

import sylvain.model.Person;

/**
 * Person added event.
 * 
 * @author Sylvain Mathiot
 */
public class PersonAddedEvent implements MonitoringEventInterface {
  private Person person;

  public PersonAddedEvent(Person person) {
    this.person = person;
  }

  public Person getPerson() {
    return person;
  }
}
