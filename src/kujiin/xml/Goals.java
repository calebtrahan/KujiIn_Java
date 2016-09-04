package kujiin.xml;

import kujiin.MainController;
import kujiin.lib.BeanComparator;
import kujiin.util.Meditatable;
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
import java.util.Collections;
import java.util.List;

@XmlRootElement(name = "Goals")
@XmlAccessorType(XmlAccessType.FIELD)
public class Goals {
    private List<Goal> PresessionGoals;
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
    private List<Goal> PostsessionGoals;
    private List<Goal> TotalGoals;
    @XmlTransient
    private MainController Root;

    public Goals() {}
    public Goals(MainController root) {
        Root = root;
    }

// List Getters
    public List<Goal> getMeditatableGoalList(int meditatableindex) {
        switch (meditatableindex) {
            case 0: return PresessionGoals;
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
            case 15: return PostsessionGoals;
            case 16: return TotalGoals;
            default: return null;
        }
    }
    public void setMeditatableGoalList(int meditatableindex, List<Goal> goallist) {
        switch (meditatableindex) {
            case 0: PresessionGoals = goallist; return;
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
            case 15: PostsessionGoals = goallist; return;
            case 16: TotalGoals = goallist;
        }
    }

// XML Processing
    public void unmarshall() {
        if (Options.GOALSXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(Goals.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                Goals currentGoals = (Goals) createMarshaller.unmarshal(Options.GOALSXMLFILE);
                PresessionGoals = currentGoals.PresessionGoals;
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
                PostsessionGoals = currentGoals.PostsessionGoals;
                TotalGoals = currentGoals.TotalGoals;
            } catch (JAXBException e) {
                Root.dialog_Information("Information", "Couldn't Open Current Goals XML File", "Check Read File Permissions Of " + Options.GOALSXMLFILE.getAbsolutePath());
            }
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Goals.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Options.GOALSXMLFILE);
        } catch (JAXBException e) {
            Root.dialog_Information("Information", "Couldn't Save Current Goals XML File", "Check Write File Permissions Of " + Options.GOALSXMLFILE.getAbsolutePath());
        }
    }

// Utility
    public static List<Goal> sortgoalsbyHours(List<Goal> listtosort) {
        BeanComparator bc = new BeanComparator(kujiin.xml.Goals.Goal.class, "getGoal_Hours");
        Collections.sort(listtosort, bc);
        return listtosort;
    }
    public static List<Goal> sortgoalsbyDate(List<Goal> listtosort) {
        BeanComparator bc = new BeanComparator(kujiin.xml.Goals.Goal.class, "getDate_Set");
        Collections.sort(listtosort, bc);
        return listtosort;
    }

    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static class Goal {
        private Integer ID;
        private String Date_Set;
        private String Date_Due;
        private String Date_Completed;
        private Double Goal_Hours;
        private Boolean Completed;

        public Goal() {}

        public Goal(Double goalhours, Meditatable meditatable) {
            setGoal_Hours(goalhours);
            setDate_Set(Util.convert_localdatetostring(LocalDate.now()));
            setCompleted(false);
            setDate_Completed(null);
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
        public String getDate_Completed() {
            return Date_Completed;
        }
        public void setDate_Completed(String date_Completed) {
            Date_Completed = date_Completed;
        }
        public Boolean getCompleted() {
            return Completed;
        }
        public void setCompleted(Boolean completed) {
            Completed = completed;
        }

        // Other Methods
        public boolean isCompleted(Integer currenthours) {
            try {return currenthours >= getGoal_Hours();}
            catch (NullPointerException | ArithmeticException ignored) {return false;}
        }
        public String getpercentagecompleted(double currenthours) {
            float percent = (float) currenthours / getGoal_Hours().floatValue();
            percent *= 100;
            if (percent >= 100) {percent = 100;}
            return String.format("%.2f", percent) + "%";
        }

        @Override
        public String toString() {
            return String.format("Set Date: %s Goal Hours: %s Is Completed: %s Date Completed: %s", getDate_Set(), getGoal_Hours(), getCompleted(), getDate_Completed());
        }
    }

}


