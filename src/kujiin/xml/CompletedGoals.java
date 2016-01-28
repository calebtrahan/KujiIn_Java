package kujiin.xml;


import kujiin.This_Session;
import kujiin.Tools;
import kujiin.lib.BeanComparator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlRootElement(name = "Completed Goals")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CompletedGoals {
    private List<CompletedGoal> RinGoals;
    private List<CompletedGoal> KyoGoals;
    private List<CompletedGoal> TohGoals;
    private List<CompletedGoal> ShaGoals;
    private List<CompletedGoal> KaiGoals;
    private List<CompletedGoal> JinGoals;
    private List<CompletedGoal> RetsuGoals;
    private List<CompletedGoal> ZaiGoals;
    private List<CompletedGoal> ZenGoals;
    private List<CompletedGoal> TotalGoals;

    public CompletedGoals() {}

// Getters And Setters
    public List<CompletedGoal> getRinGoals() {
    return RinGoals;
}
    public void setRinGoals(List<CompletedGoal> rinGoals) {
        RinGoals = rinGoals;
    }
    public List<CompletedGoal> getKyoGoals() {
        return KyoGoals;
    }
    public void setKyoGoals(List<CompletedGoal> kyoGoals) {
        KyoGoals = kyoGoals;
    }
    public List<CompletedGoal> getTohGoals() {
        return TohGoals;
    }
    public void setTohGoals(List<CompletedGoal> tohGoals) {
        TohGoals = tohGoals;
    }
    public List<CompletedGoal> getShaGoals() {
        return ShaGoals;
    }
    public void setShaGoals(List<CompletedGoal> shaGoals) {
        ShaGoals = shaGoals;
    }
    public List<CompletedGoal> getKaiGoals() {
        return KaiGoals;
    }
    public void setKaiGoals(List<CompletedGoal> kaiGoals) {
        KaiGoals = kaiGoals;
    }
    public List<CompletedGoal> getJinGoals() {
        return JinGoals;
    }
    public void setJinGoals(List<CompletedGoal> jinGoals) {
        JinGoals = jinGoals;
    }
    public List<CompletedGoal> getRetsuGoals() {
        return RetsuGoals;
    }
    public void setRetsuGoals(List<CompletedGoal> retsuGoals) {
        RetsuGoals = retsuGoals;
    }
    public List<CompletedGoal> getZaiGoals() {
        return ZaiGoals;
    }
    public void setZaiGoals(List<CompletedGoal> zaiGoals) {
        ZaiGoals = zaiGoals;
    }
    public List<CompletedGoal> getZenGoals() {
        return ZenGoals;
    }
    public void setZenGoals(List<CompletedGoal> zenGoals) {
        ZenGoals = zenGoals;
    }
    public List<CompletedGoal> getTotalGoals() {
        return TotalGoals;
    }
    public void setTotalGoals(List<CompletedGoal> totalGoals) {
        TotalGoals = totalGoals;
    }

// XML Processing
    public void unmarshall() {
        if (This_Session.completedgoalsxmlfile.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(CompletedGoals.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                CompletedGoals completedGoals = (CompletedGoals) createMarshaller.unmarshal(This_Session.completedgoalsxmlfile);
                setRinGoals(completedGoals.getRinGoals());
                setKyoGoals(completedGoals.getKyoGoals());
                setTohGoals(completedGoals.getTohGoals());
                setShaGoals(completedGoals.getShaGoals());
                setKaiGoals(completedGoals.getKaiGoals());
                setJinGoals(completedGoals.getJinGoals());
                setRetsuGoals(completedGoals.getRetsuGoals());
                setZaiGoals(completedGoals.getZaiGoals());
                setZenGoals(completedGoals.getZenGoals());
                setTotalGoals(completedGoals.getTotalGoals());
            } catch (JAXBException e) {
                Tools.showinformationdialog("Information", "Couldn't Open Completed Goals XML File", "Check Read File Permissions Of " + This_Session.completedgoalsxmlfile.getAbsolutePath());
            }
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(CompletedGoals.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.marshal(this, This_Session.completedgoalsxmlfile);
        } catch (JAXBException e) {
            Tools.showinformationdialog("Information", "Couldn't Save Completed Goals XML File", "Check Write File Permissions Of " + This_Session.completedgoalsxmlfile.getAbsolutePath());
        }
    }
    public void add(int cutindex, CompletedGoal completedGoal) throws JAXBException {
        if (This_Session.completedgoalsxmlfile.exists()) {
            List<CompletedGoal> completedGoalsList = getallcutgoals(cutindex);
            if (completedGoalsList == null) {completedGoalsList = new ArrayList<>();}
            completedGoalsList.add(completedGoal);
            update(sort(completedGoalsList), cutindex);
            marshall();
        }
    }
    public List<CompletedGoal> sort(List<CompletedGoal> goallist) {
        try {
            BeanComparator bc = new BeanComparator(CompletedGoal.class, "getGoal_Hours");
            Collections.sort(goallist, bc);
            int count = 1;
            for (CompletedGoal i : goallist) {i.setID(count); count++;}
            return goallist;
        } catch (NullPointerException ignored) {return null;}
    }
    public boolean completegoal(int cutindex, CurrentGoals.CurrentGoal currentGoal) {
        double hours = currentGoal.getGoal_Hours();
        CompletedGoal newcompletedgoal = new CompletedGoal();
        newcompletedgoal.setDate_Completed(Tools.gettodaysdate());
        newcompletedgoal.setGoal_Hours(hours);
        try {
            add(cutindex, newcompletedgoal); return true;}
        catch (JAXBException ignored) {return false;}
    }
    public List<CompletedGoal> getallcutgoals(int cutindex) {
        if (cutindex == 0) return RinGoals;
        if (cutindex == 1) return KyoGoals;
        if (cutindex == 2) return TohGoals;
        if (cutindex == 3) return ShaGoals;
        if (cutindex == 4) return KaiGoals;
        if (cutindex == 5) return JinGoals;
        if (cutindex == 6) return RetsuGoals;
        if (cutindex == 7) return ZaiGoals;
        if (cutindex == 8) return ZenGoals;
        if (cutindex == 9) return TotalGoals;
        else return null;
    }
    public CompletedGoal getgoal(int cutindex, int goalindex) {
        try {return getallcutgoals(cutindex).get(goalindex);}
        catch (ArrayIndexOutOfBoundsException ignored) {return null;}
    }
    public void update(List<CompletedGoal> cutgoallist, int cutindex) {
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
    public boolean goalsexist(int cutindex) {
        return getallcutgoals(cutindex) != null && getallcutgoals(cutindex).size() > 0;
    }

    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static class CompletedGoal {
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
}
