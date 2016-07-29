package kujiin.xml;

import javafx.util.Duration;
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
import java.util.ArrayList;
import java.util.Arrays;
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
                Root.gui_showinformationdialog("Information", "Couldn't Open Current Goals XML File", "Check Read File Permissions Of " + Options.GOALSXMLFILE.getAbsolutePath());
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
            Root.gui_showinformationdialog("Information", "Couldn't Save Current Goals XML File", "Check Write File Permissions Of " + Options.GOALSXMLFILE.getAbsolutePath());
        }
    }
    // Add
    public boolean add(int meditatableindex, Goal newgoal) {
        try {
            List<Goal> allgoals = getAllGoals(meditatableindex);
            if (allgoals == null) {allgoals = new ArrayList<>();}
            allgoals.add(newgoal);
            update(allgoals, meditatableindex);
            sort(meditatableindex);
            marshall();
            return true;
        } catch (Exception ignored) {return false;}
    }
    // Deletennnn
    public boolean delete(int meditatableindex, Goal currentGoal) {
        try {
            getAllGoals(meditatableindex).remove(currentGoal);
            sort(meditatableindex);
            marshall();
            return true;
        } catch (Exception e) {return false;}
    }
    public boolean delete(int meditatableindex, int goalindex) {
        try {
            getAllGoals(meditatableindex).remove(goalindex);
            sort(meditatableindex);
            marshall();
            return true;
        } catch (Exception e) {return false;}
    }
    // Query
    public List<List<Goal>> getallMeditatableGoalLists() {
        return new ArrayList<>(Arrays.asList(PresessionGoals, RinGoals, KyoGoals, TohGoals, ShaGoals, KaiGoals, JinGoals, RetsuGoals, ZaiGoals, ZenGoals,
                EarthGoals, AirGoals, FireGoals, WaterGoals, VoidGoals, PostsessionGoals));
    }
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
    public Goal getCurrentGoal(int meditatableindex) {
        try {return getallMeditatableGoalLists().get(meditatableindex).get(getAllGoals(meditatableindex).size() - 1);}
        catch (IndexOutOfBoundsException | NullPointerException ignored) {return null;}
    }
    public List<Goal> getAllGoals(int meditatableindex) {
        return getallMeditatableGoalLists().get(meditatableindex);
    }
    public List<Goal> getCompletedGoals(int meditatableindex) {
        try {
            List<Goal> newgoallist = new ArrayList<>();
            for (Goal i : getallMeditatableGoalLists().get(meditatableindex)) {
                if (i.getCompleted() != null && i.getCompleted()) {newgoallist.add(i);}
            }
            return newgoallist;
        } catch (NullPointerException e) {return new ArrayList<>();}
    }
    public int count_completedgoals(int meditatableindex) {return getCompletedGoals(meditatableindex).size();}
    public int count_allgoals(int meditatableindex) {return getallMeditatableGoalLists().get(meditatableindex).size();}
    public List<Goal> getgoalsCompletedOn(int meditatableindex, LocalDate localDate) {
        try {
            List<Goal> goalslist = new ArrayList<>();
            for (Goal i : getAllGoals(meditatableindex)) {
                if (i.getDate_Completed() == null || ! i.getCompleted()) {continue;}
                if (Util.convert_stringtolocaldate(i.getDate_Completed()).equals(localDate)) {goalslist.add(i);}
            }
            return goalslist;
        } catch (Exception ignored) {return new ArrayList<Goal>();}
    }
    // Sort
    public void sort(int meditatableindex) {
        List<Goal> goallist = getallMeditatableGoalLists().get(meditatableindex);
        if (goallist != null && ! goallist.isEmpty()) {
            try {
                BeanComparator bc = new BeanComparator(Goal.class, "getGoal_Hours");
                Collections.sort(goallist, bc);
                int count = 1;
                for (Goal i : goallist) {
                    i.setID(count);
                    count++;
                }
                update(goallist, meditatableindex);
            } catch (Exception ignored) {}
        }
    }
    public void update(List<Goal> cutgoallist, int meditatableindex) {
        setMeditatableGoalList(meditatableindex, cutgoallist);
    }
    // Playback Utility
    public void completegoals(int meditatableindex, Duration currentpracticedhours) {
        try {
            List<Goal> newgoallist = getallMeditatableGoalLists().get(meditatableindex);
            for (Goal i : getallMeditatableGoalLists().get(meditatableindex)) {
                boolean completed = currentpracticedhours.greaterThanOrEqualTo(Duration.hours(i.getGoal_Hours()));
                boolean notcompletedbefore = i.getCompleted() != null && ! i.getCompleted();
                if (completed && notcompletedbefore) {
                    i.setCompleted(true);
                    i.setDate_Completed(Util.gettodaysdate());
                }
                newgoallist.add(i);
            }
            update(newgoallist, meditatableindex);
        } catch (Exception ignored) {}
    }
    public List<Goal> completegoalsandgetcompleted(int meditatableindex, Duration currentpracticedhours) {
        try {
            List<Goal> newgoallist = getallMeditatableGoalLists().get(meditatableindex);
            for (Goal i : getallMeditatableGoalLists().get(meditatableindex)) {
                boolean completed = currentpracticedhours.greaterThanOrEqualTo(Duration.hours(i.getGoal_Hours()));
                boolean notcompletedbefore = i.getCompleted() != null && ! i.getCompleted();
                if (completed && notcompletedbefore) {
                    i.setCompleted(true);
                    i.setDate_Completed(Util.gettodaysdate());
                }
                newgoallist.add(i);
            }
            update(newgoallist, meditatableindex);
            return newgoallist;
        } catch (Exception ignored) {return new ArrayList<Goal>();}
    }

//    public List<Goal> getgoalscompletedondate(int cutindex, LocalDate localDate) {
//       List<Goal> goalscompletedondate = new ArrayList<>();
//        for (Goal i : getallmeditatablegoals(cutindex, false)) {
//            LocalDate date = Util.convert_stringtolocaldate(i.getDate_Completed())
//        }
//    }
// Goal Getters
//    public boolean goalsexist(int cutorelementindex, boolean includecompleted) {
//        return getallmeditatablegoals(cutorelementindex, includecompleted) != null && ! getallmeditatablegoals(cutorelementindex, true).isEmpty();
//    }
//    public Goal getcurrentgoal(int cutorelementindex) {
//        try {return getallmeditatablegoals(cutorelementindex, false).get(0);}
//        catch (NullPointerException | IndexOutOfBoundsException ignored) {return null;}
//    }
//    public Goal getgoal(int cutorelementindex, Integer goalindex, boolean includecompleted) {
//        try {return getallmeditatablegoals(cutorelementindex, includecompleted).get(goalindex);}
//        catch (IndexOutOfBoundsException | NullPointerException ignored) {return null;}
//    }

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


