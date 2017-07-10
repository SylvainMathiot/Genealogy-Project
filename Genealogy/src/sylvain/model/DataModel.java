package sylvain.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataModel {
	private static DataModel INSTANCE;
	private List<Person> persons;
	
	private DataModel(){
		persons = new ArrayList<Person>();
	}
	
	public static DataModel getInstance(){
		if(INSTANCE == null){
			INSTANCE = new DataModel();
		}
		return INSTANCE;
	}
	
	public List<Person> getPersons(){
		return persons;
	}
	
	public boolean contains(String id){
		return persons.stream().filter(p -> p.getId().equalsIgnoreCase(id)).findFirst().isPresent();
	}
	
	public boolean contains(Person person){
		return persons.contains(person);
	}
	
	public boolean contains(File folder){
		return contains(folder.getName().split("_")[0]);
	}
	
	public Optional<Person> get(String id){
		return persons.stream().filter(p -> p.getId().equalsIgnoreCase(id)).findFirst();
	}
	
	public Optional<Person> get(Person person){
		return persons.stream().filter(p -> p.equals(person)).findFirst();
	}
	
	public Optional<Person> get(File folder){
		return get(folder.getName().split("_")[0]);
	}
	
	public void add(Person person){
		if(!persons.contains(person))
			persons.add(person);
	}
	
	public Person add(File folder){
		Person person = new Person(folder);
		if(contains(folder))
			get(person).get().merge(person);
		add(person);
		
		return person;
	}
	
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("DataModel :");
		
		for(Person person : persons)
			str.append("\n").append(person);
		
		return str.toString();
	}
}
