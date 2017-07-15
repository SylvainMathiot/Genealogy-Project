package sylvain.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import sylvain.controller.LoggerSingleton;
import sylvain.controller.PropertiesSingleton;
import sylvain.view.NodeGestures;

/**
 * Person model class.
 * 
 * @author Sylvain Mathiot
 */
public class Person {
  private static final Logger logger =
      LogManager.getLogger(LoggerSingleton.getInstance().getLoggerName(Person.class));
  private String id = "";
  private String lastName = "";
  private String firstNames = "";
  private String birthDate = "";
  private String birthPlace = "";
  private String deathDate = "";
  private String deathPlace = "";
  private List<Union> unions = new ArrayList<Union>();
  private Pane pane = new Pane();

  public Person(String id) {
    super();
    this.id = id;
  }

  /**
   * Constructor.
   * 
   * @param folder The Person folder
   */
  public Person(File folder) {
    super();
    if (folder != null && folder.isDirectory()) {
      id = folder.getName().split("_")[0];

      try {
        Optional<File> identity = Arrays.asList(folder.listFiles()).stream()
            .filter(f -> f.isFile() && f.getName()
                .equalsIgnoreCase(PropertiesSingleton.getInstance().get("IDENTITY.FILENAME")))
            .findFirst();

        if (identity.isPresent()) {
          Ini ini = new Ini(identity.get());
          final Section identitySection = ini.get("IDENTITY");
          final Section birthSection = ini.get("BIRTH");
          final Section deathSection = ini.get("DEATH");
          final Section childrenSection = ini.get("CHILDREN");

          if (identitySection != null) {
            lastName = identitySection.get("LASTNAME", "");
            firstNames = identitySection.get("FIRSTNAMES", "");
          }

          if (birthSection != null) {
            birthDate = birthSection.get("DATE", "");
            birthPlace = birthSection.get("PLACE", "");
          }

          if (deathSection != null) {
            deathDate = deathSection.get("DATE", "");
            deathPlace = deathSection.get("PLACE", "");
          }

          if (childrenSection != null) {
            int nbUnions = childrenSection.get("UNIONS", Integer.class, 0);

            Section unionSection;
            Union union;
            for (int i = 1; i <= nbUnions; i++) {
              unionSection = ini.get("UNION-" + i);
              if (unionSection != null) {
                union = new Union(this);
                union.setStartDate(unionSection.get("STARTDATE", ""));
                union.setEndDate(unionSection.get("ENDDATE", ""));
                union.setType(unionSection.get("TYPE", ""));
                union.setPartner(unionSection.get("PARTNER", ""));
                for (String child : childrenSection.getAll("UNION-" + i)) {
                  union.addChild(child);
                }

                unions.add(union);
              }
            }
          }
        } else {
          logger.warn("Identity file not found for Person " + id);
        }
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
    }
  }

  /**
   * Merge the person with the give one.
   * 
   * @param person The Person to merge from
   */
  public void merge(Person person) {
    lastName = person.lastName;
    firstNames = person.firstNames;
    birthDate = person.birthDate;
    birthPlace = person.birthPlace;
    deathDate = person.deathDate;
    deathPlace = person.deathPlace;
    unions = person.unions;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFirstNames() {
    return firstNames;
  }

  public void setFirstNames(String firstNames) {
    this.firstNames = firstNames;
  }

  public String getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }

  public String getBirthPlace() {
    return birthPlace;
  }

  public void setBirthPlace(String birthPlace) {
    this.birthPlace = birthPlace;
  }

  public String getDeathDate() {
    return deathDate;
  }

  public void setDeathDate(String deathDate) {
    this.deathDate = deathDate;
  }

  public String getDeathPlace() {
    return deathPlace;
  }

  public void setDeathPlace(String deathPlace) {
    this.deathPlace = deathPlace;
  }

  public List<Union> getUnions() {
    return unions;
  }

  public void setUnions(List<Union> unions) {
    this.unions = unions;
  }
  
  public Pane getPane() {
    return pane;
  }

  public void setPane(Pane pane) {
    this.pane = pane;
  }

  /**
   * Build the person representation shape.
   * 
   * @return The shape
   */
  public Pane build(NodeGestures nodeGestures) {
    Rectangle personShape = new Rectangle();
    personShape.getStyleClass().add("personShape");
    personShape.setWidth(Integer.parseInt(PropertiesSingleton.getInstance().get("PERSON.WIDTH")));
    personShape.setHeight(Integer.parseInt(PropertiesSingleton.getInstance().get("PERSON.HEIGHT")));
    personShape.setSmooth(true);
    
    Text name = new Text(firstNames + " " + lastName);
    name.getStyleClass().add("nameText");
    name.setWrappingWidth(personShape.getWidth());
    name.setTextAlignment(TextAlignment.CENTER);
    name.setFontSmoothingType(FontSmoothingType.GRAY);
    name.setSmooth(true);
    name.setY(20);
    
    pane = new Pane();
    pane.getChildren().addAll(personShape, name);

    if (nodeGestures != null) {
      pane.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
      pane.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
    }
    
    return pane;
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

    Person p = (Person) obj;
    return new EqualsBuilder().append(id, p.getId()).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(id).append(lastName).append(firstNames).toHashCode();
  }

  @Override
  public String toString() {
    return "Person [" + id + "] " + lastName + " " + firstNames;
  }
}
