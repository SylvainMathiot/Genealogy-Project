package sylvain.thread.event;

import sylvain.model.Person;

/**
 * Person deleted event.
 * 
 * @author Sylvain Mathiot
 */
public class PersonDeletedEvent implements MonitoringEventInterface {
  private Person person;

  public PersonDeletedEvent(Person person) {
    this.person = person;
  }

  public Person getPerson() {
    return person;
  }
}
