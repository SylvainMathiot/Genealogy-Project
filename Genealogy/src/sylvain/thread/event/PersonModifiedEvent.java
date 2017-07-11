package sylvain.thread.event;

import sylvain.model.Person;

/*
 *
 * @author Sylvain Mathiot
 *
 */
public class PersonModifiedEvent implements MonitoringEventInterface {
  private Person person;

  public PersonModifiedEvent(Person person) {
    this.person = person;
  }

  public Person getPerson() {
    return person;
  }
}
