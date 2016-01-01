package kujiin.util.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class CompletedGoal {
    private Double Goal_Hours;
    private String Date_Completed;

    public CompletedGoal() {}

// Getters And Setters
    public Double getGoal_Hours() {return Goal_Hours;}
    public void setGoal_Hours(Double goal_Hours) {Goal_Hours = goal_Hours;}
    public String getDate_Completed() {return Date_Completed;}
    public void setDate_Completed(String date_Completed) {Date_Completed = date_Completed;}

// Other Methods

}
