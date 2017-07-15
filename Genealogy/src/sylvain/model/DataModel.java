package sylvain.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data model class.
 * 
 * @author Sylvain Mathiot
 */
public class DataModel {
  private static DataModel INSTANCE;
  private List<Person> persons;

  private DataModel() {
    persons = new ArrayList<Person>();
  }

  /**
   * Get instance of singleton.
   * 
   * @return Instance of singleton
   */
  public static DataModel getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DataModel();
    }
    return INSTANCE;
  }

  public List<Person> getPersons() {
    return persons;
  }

  public boolean contains(String id) {
    return persons.stream().filter(p -> p.getId().equalsIgnoreCase(id)).findFirst().isPresent();
  }

  public boolean contains(Person person) {
    return persons.contains(person);
  }

  public boolean contains(File folder) {
    return contains(folder.getName().split("_")[0]);
  }

  public Optional<Person> get(String id) {
    return persons.stream().filter(p -> p.getId().equalsIgnoreCase(id)).findFirst();
  }

  public Optional<Person> get(Person person) {
    return persons.stream().filter(p -> p.equals(person)).findFirst();
  }

  public Optional<Person> get(File folder) {
    return get(folder.getName().split("_")[0]);
  }

  /**
   * Add a Person to the data model.
   * 
   * @param person The person to add
   * @return The added Person
   */
  public Person add(Person person) {
    if (!persons.contains(person)) {
      persons.add(person);
    }
    return person;
  }

  /**
   * Add a Person to the data model.
   * 
   * @param folder The person folder to add
   * @return The added Person
   */
  public Person add(File folder) {
    Person person = new Person(folder);
    if (contains(folder)) {
      get(person).get().merge(person);
    }
    return add(person);
  }

  /**
   * Delete a Person from the data model.
   * 
   * @param id The person id to delete
   * @return The deleted Person
   */
  public Person delete(String id) {
    Optional<Person> person = get(id);
    if (person.isPresent()) {
      delete(person.get());
    }

    return person.get();
  }

  /**
   * Delete a Person from the data model.
   * 
   * @param person The person to delete
   * @return The deleted Person
   */
  public Person delete(Person person) {
    if (contains(person)) {
      persons.remove(person);
    }

    return person;
  }

  public Person delete(File folder) {
    return delete(folder.getName().split("_")[0]);
  }

  @Override
  public String toString() {
    StringBuffer str = new StringBuffer("DataModel :");

    for (Person person : persons) {
      str.append("\n").append(person);
    }

    return str.toString();
  }
}
