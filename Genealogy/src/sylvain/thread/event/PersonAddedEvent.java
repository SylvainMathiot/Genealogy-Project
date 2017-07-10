package sylvain.thread.event;

import sylvain.model.Person;

/**
 * 
 * @author Sylvain Mathiot
 *
 */
public class PersonAddedEvent implements MonitoringEventInterface{
	private Person person;
	
	public PersonAddedEvent(Person person){
		this.person = person;
	}
	
	public Person getPerson(){
		return person;
	}
}
