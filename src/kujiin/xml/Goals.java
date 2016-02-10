package kujiin.xml;

import kujiin.Tools;
import kujiin.lib.BeanComparator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlRootElement(name = "Goals")
@XmlAccessorType(XmlAccessType.PROPERTY)
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
    private List<Goal> PostsessionGoals;
    private List<Goal> TotalGoals;

    // TODO Unify Completed And Current Goals With The IsCompleted() Boolean

    public Goals() {}

// Getters And Setters
    public List<Goal> getPresessionGoals() {
        return PresessionGoals;
    }
    public void setPresessionGoals(List<Goal> presessionGoals) {
        PresessionGoals = presessionGoals;
    }
    public List<Goal> getRinGoals() {
    return RinGoals;
}
    public void setRinGoals(List<Goal> rinGoals) {
        RinGoals = rinGoals;
    }
    public List<Goal> getKyoGoals() {
        return KyoGoals;
    }
    public void setKyoGoals(List<Goal> kyoGoals) {
        KyoGoals = kyoGoals;
    }
    public List<Goal> getTohGoals() {
        return TohGoals;
    }
    public void setTohGoals(List<Goal> tohGoals) {
        TohGoals = tohGoals;
    }
    public List<Goal> getShaGoals() {
        return ShaGoals;
    }
    public void setShaGoals(List<Goal> shaGoals) {
        ShaGoals = shaGoals;
    }
    public List<Goal> getKaiGoals() {
        return KaiGoals;
    }
    public void setKaiGoals(List<Goal> kaiGoals) {
        KaiGoals = kaiGoals;
    }
    public List<Goal> getJinGoals() {
        return JinGoals;
    }
    public void setJinGoals(List<Goal> jinGoals) {
        JinGoals = jinGoals;
    }
    public List<Goal> getRetsuGoals() {
        return RetsuGoals;
    }
    public void setRetsuGoals(List<Goal> retsuGoals) {
        RetsuGoals = retsuGoals;
    }
    public List<Goal> getZaiGoals() {
        return ZaiGoals;
    }
    public void setZaiGoals(List<Goal> zaiGoals) {
        ZaiGoals = zaiGoals;
    }
    public List<Goal> getZenGoals() {
        return ZenGoals;
    }
    public void setZenGoals(List<Goal> zenGoals) {
        ZenGoals = zenGoals;
    }
    public List<Goal> getPostsessionGoals() {
        return PostsessionGoals;
    }
    public void setPostsessionGoals(List<Goal> postsessionGoals) {
        PostsessionGoals = postsessionGoals;
    }
    public List<Goal> getTotalGoals() {return TotalGoals;}
    public void setTotalGoals(List<Goal> totalGoals) {
        TotalGoals = totalGoals;}

// XML Processing
    public void unmarshall() {
        if (Options.goalsxmlfile.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(Goals.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                Goals currentGoals = (Goals) createMarshaller.unmarshal(Options.goalsxmlfile);
                setPresessionGoals(currentGoals.getPresessionGoals());
                setRinGoals(currentGoals.getRinGoals());
                setKyoGoals(currentGoals.getKyoGoals());
                setTohGoals(currentGoals.getTohGoals());
                setShaGoals(currentGoals.getShaGoals());
                setKaiGoals(currentGoals.getKaiGoals());
                setJinGoals(currentGoals.getJinGoals());
                setRetsuGoals(currentGoals.getRetsuGoals());
                setZaiGoals(currentGoals.getZaiGoals());
                setZenGoals(currentGoals.getZenGoals());
                setPostsessionGoals(currentGoals.getPostsessionGoals());
                setTotalGoals(currentGoals.getTotalGoals());
            } catch (JAXBException e) {
                Tools.showinformationdialog("Information", "Couldn't Open Current Goals XML File", "Check Read File Permissions Of " + Options.goalsxmlfile.getAbsolutePath());
            }
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Goals.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Options.goalsxmlfile);
        } catch (JAXBException e) {
            Tools.showinformationdialog("Information", "Couldn't Save Current Goals XML File", "Check Write File Permissions Of " + Options.goalsxmlfile.getAbsolutePath());
        }
    }
    public void add(int cutindex, Goal newgoal) throws JAXBException {
        List<Goal> newgoals = getallcutgoals(cutindex, true);
        newgoals.add(newgoal);
        update(sort(newgoals), cutindex);
        marshall();
    }
    public boolean delete(int cutindex, Goal currentGoal) {
        try {
            List<Goal> cutgoallist = getallcutgoals(cutindex, true);
            cutgoallist.remove(currentGoal);
            update(sort(cutgoallist), cutindex);
            marshall();
            return true;
        } catch (Exception e) {return false;}
    }
    public List<Goal> sort(List<Goal> goallist) {
        try {
            BeanComparator bc = new BeanComparator(Goal.class, "getGoal_Hours");
            Collections.sort(goallist, bc);
            int count = 1;
            for (Goal i : goallist) {i.setID(count); count++;}
            return goallist;
        } catch (Exception ignored) {return null;}
    }
    public void update(List<Goal> cutgoallist, int cutindex) {
        if (cutindex == 0 || cutindex == 10) setPresessionGoals(cutgoallist);
        if (cutindex == 0) setRinGoals(cutgoallist);
        if (cutindex == 1) setKyoGoals(cutgoallist);
        if (cutindex == 2) setTohGoals(cutgoallist);
        if (cutindex == 3) setShaGoals(cutgoallist);
        if (cutindex == 4) setKaiGoals(cutgoallist);
        if (cutindex == 5) setJinGoals(cutgoallist);
        if (cutindex == 6) setRetsuGoals(cutgoallist);
        if (cutindex == 7) setZaiGoals(cutgoallist);
        if (cutindex == 8) setZenGoals(cutgoallist);
        if (cutindex == 9) setTotalGoals(cutgoallist);
    }


// Goal Getters
    public boolean goalsexist(int cutindex, boolean includecompleted) {
        return getallcutgoals(cutindex, includecompleted) != null && ! getallcutgoals(cutindex, true).isEmpty();
    }
    public Goal getgoal(int cutindex, Integer goalindex, boolean includecompleted) {
        try {return getallcutgoals(cutindex, includecompleted).get(goalindex);}
        catch (ArrayIndexOutOfBoundsException | NullPointerException ignored) {return null;}
    }
    public List<Goal> getallcutgoals(int cutindex, boolean includecompleted) {
        if (cutindex == 0) return filtergoals(PresessionGoals, includecompleted);
        if (cutindex == 1) return filtergoals(RinGoals, includecompleted);
        if (cutindex == 2) return filtergoals(KyoGoals, includecompleted);
        if (cutindex == 3) return filtergoals(TohGoals, includecompleted);
        if (cutindex == 4) return filtergoals(ShaGoals, includecompleted);
        if (cutindex == 5) return filtergoals(KaiGoals, includecompleted);
        if (cutindex == 6) return filtergoals(JinGoals, includecompleted);
        if (cutindex == 7) return filtergoals(RetsuGoals, includecompleted);
        if (cutindex == 8) return filtergoals(ZaiGoals, includecompleted);
        if (cutindex == 9) return filtergoals(ZenGoals, includecompleted);
        if (cutindex == 10) return filtergoals(PostsessionGoals, includecompleted);
        if (cutindex == 11) return filtergoals(TotalGoals, includecompleted);
        else return null;
    }
    public List<Goal> filtergoals(List<Goal> cutgoals, boolean includecompleted) {
        List<Goal> newgoallist = new ArrayList<>();
        for (Goal i : cutgoals) {
            if (includecompleted) {newgoallist.add(i);}
            else {
                if (!i.getCompleted()) {newgoallist.add(i);}
            }
        }
        return newgoallist;
    }

// Goal Completion Methods
    public void checkifgoalscompleted(int cutindex, double currentpracticedhours) {
        for (Goal i : getallcutgoals(cutindex, true)) {
            boolean completed = currentpracticedhours >= i.getGoal_Hours();
            i.setCompleted(completed);
            if (completed && i.getDate_Completed().isEmpty()) {
                i.setDate_Completed(Tools.gettodaysdate());
            }
        }
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

        public Goal(LocalDate duedate, Double goalhours) {
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
//        return (float) practicedhours / (float) goalhours;
            return String.format("%.2f", percent) + "%";
        }
    }

}


