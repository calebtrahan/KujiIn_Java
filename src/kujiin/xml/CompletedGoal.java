package kujiin.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class CompletedGoal {
    private Integer ID;
    private Double Goal_Hours;
    private String Date_Completed;

    public CompletedGoal() {}

    // TODO Finish Refactoring Current And Completed Goals To Support All Cuts
        // Update Main UI So I Can Select Cuts
        // Create A Flow Chart Showing Each Cut Progress (In Relation To Total)
        // Totals Are The Mathmatical Total Of Rin-Zen's Highest Goal?
// Getters And Setters
    public Double getGoal_Hours() {return Goal_Hours;}
    public void setGoal_Hours(Double goal_Hours) {Goal_Hours = goal_Hours;}
    public String getDate_Completed() {return Date_Completed;}
    public void setDate_Completed(String date_Completed) {Date_Completed = date_Completed;}
    public Integer getID() {
        return ID;
    }
    public void setID(Integer ID) {
        this.ID = ID;
    }

}
