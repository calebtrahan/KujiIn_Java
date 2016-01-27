package kujiin.xml;

import kujiin.Tools;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.time.LocalDate;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class CurrentGoal {
    private Integer ID;
    private String Date_Set;
    private String Date_Due;
    private double Goal_Hours;

    public CurrentGoal() {}

    public CurrentGoal(LocalDate duedate, Double goalhours) {
        setDate_Due(Tools.convertfromlocaldatetostring(duedate));
        setGoal_Hours(goalhours);
        setDate_Set(Tools.convertfromlocaldatetostring(LocalDate.now()));
    }

// Getters And Setters
    public String getDate_Set() {return Date_Set;}
    public void setDate_Set(String date_Set) {Date_Set = date_Set;}
    public String getDate_Due() {return Date_Due;}
    public void setDate_Due(String date_Due) {Date_Due = date_Due;}
    public Double getGoal_Hours() {return Goal_Hours;}
    public void setGoal_Hours(Double goal_Hours) {Goal_Hours = goal_Hours;}
    public Integer getID() {return ID;}
    public void setID(Integer ID) {this.ID = ID;}

// Other Methods
    public boolean isCompleted(Integer currenthours) {
        try {return currenthours >= getGoal_Hours();}
        catch (NullPointerException | ArithmeticException ignored) {return false;}
    }
    public String getpercentagecompleted(double currenthours) {
        float percent = (float) currenthours / getGoal_Hours().floatValue();
        percent *= 100;
//        return (float) practicedhours / (float) goalhours;
        return String.format("%.2f", percent) + "%";
    }

}
