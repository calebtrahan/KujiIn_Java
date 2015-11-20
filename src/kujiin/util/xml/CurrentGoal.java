package kujiin.util.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class CurrentGoal {
    private Integer ID;
    private String Date_Set;
    private String Date_Due;
    private Integer Goal_Hours;

    public CurrentGoal() {}

// Getters And Setters
    public String getDate_Set() {return Date_Set;}
    public void setDate_Set(String date_Set) {Date_Set = date_Set;}
    public String getDate_Due() {return Date_Due;}
    public void setDate_Due(String date_Due) {Date_Due = date_Due;}
    public Integer getGoal_Hours() {return Goal_Hours;}
    public void setGoal_Hours(Integer goal_Hours) {Goal_Hours = goal_Hours;}
    public Integer getID() {return ID;}
    public void setID(Integer ID) {this.ID = ID;}

// Other Methods
    public boolean isCompleted(Integer currenthours) {
        try {return currenthours >= getGoal_Hours();}
        catch (NullPointerException | ArithmeticException ignored) {return false;}
    }
}
