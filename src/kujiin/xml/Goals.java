package kujiin.xml;

import javafx.util.Duration;
import kujiin.ui.MainController;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.util.Util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static kujiin.util.Util.dateFormat;

@XmlRootElement(name = "Goals")
@XmlAccessorType(XmlAccessType.FIELD)
public class Goals {
    private List<Goal> QiGongGoals;
    private List<Goal> RinGoals;
    private List<Goal> KyoGoals;
    private List<Goal> TohGoals;
    private List<Goal> ShaGoals;
    private List<Goal> KaiGoals;
    private List<Goal> JinGoals;
    private List<Goal> RetsuGoals;
    private List<Goal> ZaiGoals;
    private List<Goal> ZenGoals;
    private List<Goal> EarthGoals;
    private List<Goal> AirGoals;
    private List<Goal> FireGoals;
    private List<Goal> WaterGoals;
    private List<Goal> VoidGoals;
    private List<Goal> TotalGoals;
    @XmlTransient
    private MainController Root;

    public Goals() {}
    public Goals(MainController root) {
        Root = root;
        unmarshall();
    }

// List Getters
    public List<List<Goal>> getAll() {
        return new ArrayList<>(Arrays.asList(QiGongGoals, RinGoals, KyoGoals, TohGoals, ShaGoals, KaiGoals, JinGoals, RetsuGoals, ZaiGoals, ZenGoals,
                EarthGoals, AirGoals, FireGoals, WaterGoals, VoidGoals, TotalGoals));
    }
    public List<Goal> get(int index) {
        switch (index) {
            case 0: return QiGongGoals;
            case 1: return RinGoals;
            case 2: return KyoGoals;
            case 3: return TohGoals;
            case 4: return ShaGoals;
            case 5: return KaiGoals;
            case 6: return JinGoals;
            case 7: return RetsuGoals;
            case 8: return ZaiGoals;
            case 9: return ZenGoals;
            case 10: return EarthGoals;
            case 11: return AirGoals;
            case 12: return FireGoals;
            case 13: return WaterGoals;
            case 14: return VoidGoals;
            case 15: return TotalGoals;
            default: return null;
        }
    }
    public Goal getCurrentGoal(int index) {
        for (Goal i : get(index)) {
            if (! i.getCompleted()) {return i;}
        }
        return null;
    }
    public void set(int index, List<Goal> goallist) {
        switch (index) {
            case 0: QiGongGoals = goallist; return;
            case 1: RinGoals = goallist; return;
            case 2: KyoGoals = goallist; return;
            case 3: TohGoals = goallist; return;
            case 4: ShaGoals = goallist; return;
            case 5: KaiGoals = goallist; return;
            case 6: JinGoals = goallist; return;
            case 7: RetsuGoals = goallist; return;
            case 8: ZaiGoals = goallist; return;
            case 9: ZenGoals = goallist; return;
            case 10: EarthGoals = goallist; return;
            case 11: AirGoals = goallist; return;
            case 12: FireGoals = goallist; return;
            case 13: WaterGoals = goallist; return;
            case 14: VoidGoals = goallist; return;
            case 15: TotalGoals = goallist; return;
            default:
        }
    }

// XML Processing
    public void unmarshall() {
        if (Preferences.GOALSXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(Goals.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                Goals currentGoals = (Goals) createMarshaller.unmarshal(Preferences.GOALSXMLFILE);
                QiGongGoals = currentGoals.QiGongGoals;
                RinGoals = currentGoals.RinGoals;
                KyoGoals = currentGoals.KyoGoals;
                TohGoals = currentGoals.TohGoals;
                ShaGoals = currentGoals.ShaGoals;
                KaiGoals = currentGoals.KaiGoals;
                JinGoals = currentGoals.JinGoals;
                RetsuGoals = currentGoals.RetsuGoals;
                ZaiGoals = currentGoals.ZaiGoals;
                ZenGoals = currentGoals.ZenGoals;
                EarthGoals = currentGoals.EarthGoals;
                AirGoals = currentGoals.AirGoals;
                FireGoals = currentGoals.FireGoals;
                WaterGoals = currentGoals.WaterGoals;
                VoidGoals = currentGoals.VoidGoals;
                TotalGoals = currentGoals.TotalGoals;
            } catch (JAXBException e) {
                new InformationDialog(Root.getPreferences(), "Information", "Couldn't Open Current Goals XML File", "Check Read File Permissions Of " + Preferences.GOALSXMLFILE.getAbsolutePath());
            }
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Goals.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Preferences.GOALSXMLFILE);
        } catch (JAXBException e) {
            new InformationDialog(Root.getPreferences(), "Information", "Couldn't Save Current Goals XML File", "Check Write File Permissions Of " + Preferences.GOALSXMLFILE.getAbsolutePath());
        }
    }

// Utility
    private void sort() {

    }

// Subclasses
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Goal {
        private Integer ID;
        private Double Duration;
        private Boolean Completed;
        private String Date_Completed;

        public Goal() {
        }
        public Goal(Duration goalduration) {
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
            return LocalDate.parse(Date_Completed, dateFormat);
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
}


