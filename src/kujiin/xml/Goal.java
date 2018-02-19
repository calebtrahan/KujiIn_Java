package kujiin.xml;

import javafx.util.Duration;
import kujiin.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.time.LocalDate;

import static kujiin.util.Util.dateFormat;

@XmlAccessorType(XmlAccessType.FIELD)
public class Goal {
    private Integer ID;
    private Double Duration; // In Minutes
    private Boolean Completed;
    private String Date_Completed;

    public Goal() {
    }
    public Goal(javafx.util.Duration goalduration) {
        setDuration(goalduration);
        setCompleted(false);
    }

// Getters And Setters
    public Duration getDuration() {
        return javafx.util.Duration.minutes(Duration);
    }
    public void setDuration(Duration duration) {
        Duration = duration.toMinutes();
    }
    public Integer getID() {
        return ID;
    }
    public void setID(Integer ID) {
        this.ID = ID;
    }
    public LocalDate getDate_Completed() {
        if (Date_Completed == null) {return null;}
        else {return LocalDate.parse(Date_Completed, dateFormat);}
    }
    public void setDate_Completed(LocalDate date_Completed) {
        if (date_Completed != null) {Date_Completed = date_Completed.format(dateFormat);}
    }
    public Boolean getCompleted() {
        return Completed;
    }
    public void setCompleted(Boolean completed) {
        Completed = completed;
    }

// Other Methods
    public boolean isCompleted(Duration currentduration) {
        try {
            return currentduration.greaterThanOrEqualTo(getDuration());
        } catch (NullPointerException | ArithmeticException ignored) {
            return false;
        }
    }
    public String getpercentagecompleted(Duration currentduration) {
        Double percent = currentduration.toMillis() / getDuration().toMillis();
        percent *= 100.0;
        if (percent >= 100.0) {
            percent = 100.0;
        }
        return String.format("%.2f", percent) + "%";
    }

    @Override
    public String toString() {
        return String.format("Goal Hours: %s Is Completed: %s Date Completed: %s", getDuration(), getCompleted(), getDate_Completed());
    }
    public String getFormattedString(Duration timepracticed, boolean includepercentage, double maxchars) {
        if (getDuration().greaterThan(javafx.util.Duration.ZERO)) {
            StringBuilder text = new StringBuilder();
            if (includepercentage) {
                Double percentage = null;
                String percentagetext;
                try {
                    percentage = (timepracticed.toMillis() / getDuration().toMillis()) * 100;
                    percentagetext = " (" + percentage.intValue() + "%)";
                } catch (ArithmeticException e) {percentagetext = "(0%)";}
                text.append(text.append(Util.formatdurationtoStringSpelledOut(getDuration(), maxchars - percentagetext.length())));
                text.append(percentagetext);
            } else {text.append(Util.formatdurationtoStringSpelledOut(getDuration(), maxchars));}
            return text.toString();
        } else {return "Goal Not Set";}
    }

}