package sylvain.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Union {
  private String startDate = "";
  private String endDate = "";
  private String type = "";
  private Person person = null;
  private Person partner = null;
  private List<Person> children = new ArrayList<Person>();

  public Union(Person person) {
    this.person = person;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  public Person getPartner() {
    return partner;
  }

  public void setPartner(Person partner) {
    this.partner = partner;
  }

  /**
   * Set the partner property.
   * 
   * @param id The partner's person id
   */
  public void setPartner(String id) {
    Person partner;
    if (DataModel.getInstance().contains(id)) {
      partner = DataModel.getInstance().get(id).get();
    } else {
      partner = new Person(id);
      DataModel.getInstance().add(partner);
    }

    this.partner = partner;
  }

  public List<Person> getChildren() {
    return children;
  }

  public void setChildren(List<Person> children) {
    this.children = children;
  }

  /**
   * Add a child to the union.
   * 
   * @param child The child person to add
   */
  public void addChild(Person child) {
    if (!children.contains(child)) {
      children.add(child);
    }
  }

  /**
   * Add a child to the union.
   * 
   * @param id The child person id to add
   */
  public void addChild(String id) {
    Person child;
    if (DataModel.getInstance().contains(id)) {
      child = DataModel.getInstance().get(id).get();
    } else {
      child = new Person(id);
      DataModel.getInstance().add(child);
    }

    addChild(child);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }

    Union u = (Union) obj;
    return new EqualsBuilder()
        .append(person, u.getPerson())
        .append(partner, u.getPartner())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(person).append(partner).toHashCode();
  }

  @Override
  public String toString() {
    return "Union [" + type + "] with " + partner.getId();
  }
}
