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

@XmlRootElement(name = "CurrentGoals")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CurrentGoals {
    private List<CurrentGoal> RinGoals;
    private List<CurrentGoal> KyoGoals;
    private List<CurrentGoal> TohGoals;
    private List<CurrentGoal> ShaGoals;
    private List<CurrentGoal> KaiGoals;
    private List<CurrentGoal> JinGoals;
    private List<CurrentGoal> RetsuGoals;
    private List<CurrentGoal> ZaiGoals;
    private List<CurrentGoal> ZenGoals;
    private List<CurrentGoal> TotalGoals;

    public CurrentGoals() {}

// Getters And Setters
    public List<CurrentGoal> getRinGoals() {
    return RinGoals;
}
    public void setRinGoals(List<CurrentGoal> rinGoals) {
        RinGoals = rinGoals;
    }
    public List<CurrentGoal> getKyoGoals() {
        return KyoGoals;
    }
    public void setKyoGoals(List<CurrentGoal> kyoGoals) {
        KyoGoals = kyoGoals;
    }
    public List<CurrentGoal> getTohGoals() {
        return TohGoals;
    }
    public void setTohGoals(List<CurrentGoal> tohGoals) {
        TohGoals = tohGoals;
    }
    public List<CurrentGoal> getShaGoals() {
        return ShaGoals;
    }
    public void setShaGoals(List<CurrentGoal> shaGoals) {
        ShaGoals = shaGoals;
    }
    public List<CurrentGoal> getKaiGoals() {
        return KaiGoals;
    }
    public void setKaiGoals(List<CurrentGoal> kaiGoals) {
        KaiGoals = kaiGoals;
    }
    public List<CurrentGoal> getJinGoals() {
        return JinGoals;
    }
    public void setJinGoals(List<CurrentGoal> jinGoals) {
        JinGoals = jinGoals;
    }
    public List<CurrentGoal> getRetsuGoals() {
        return RetsuGoals;
    }
    public void setRetsuGoals(List<CurrentGoal> retsuGoals) {
        RetsuGoals = retsuGoals;
    }
    public List<CurrentGoal> getZaiGoals() {
        return ZaiGoals;
    }
    public void setZaiGoals(List<CurrentGoal> zaiGoals) {
        ZaiGoals = zaiGoals;
    }
    public List<CurrentGoal> getZenGoals() {
        return ZenGoals;
    }
    public void setZenGoals(List<CurrentGoal> zenGoals) {
        ZenGoals = zenGoals;
    }
    public List<CurrentGoal> getTotalGoals() {return TotalGoals;}
    public void setTotalGoals(List<CurrentGoal> totalGoals) {
        TotalGoals = totalGoals;}

// XML Processing
    public void unmarshall() throws JAXBException {
    if (This_Session.currentgoalsxmlfile.exists()) {
        JAXBContext context = JAXBContext.newInstance(CurrentGoals.class);
        Unmarshaller createMarshaller = context.createUnmarshaller();
        CurrentGoals currentGoals = (CurrentGoals) createMarshaller.unmarshal(This_Session.currentgoalsxmlfile);
        setRinGoals(currentGoals.getRinGoals());
        setKyoGoals(currentGoals.getKyoGoals());
        setTohGoals(currentGoals.getTohGoals());
        setShaGoals(currentGoals.getShaGoals());
        setKaiGoals(currentGoals.getKaiGoals());
        setJinGoals(currentGoals.getJinGoals());
        setRetsuGoals(currentGoals.getRetsuGoals());
        setZaiGoals(currentGoals.getZaiGoals());
        setZenGoals(currentGoals.getZenGoals());
        setTotalGoals(currentGoals.getTotalGoals());
    }
}
    public void marshall() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(CurrentGoals.class);
        Marshaller createMarshaller = context.createMarshaller();
        createMarshaller.marshal(this, This_Session.currentgoalsxmlfile);
    }
    public void add(int cutindex, CurrentGoal newgoal) throws JAXBException {
        List<CurrentGoal> newgoals = getallcutgoals(cutindex);
        int count = 0;
        if (newgoals != null && newgoals.size() > 0) {
            count = getgoal(cutindex, getallcutgoals(cutindex).size() - 1).getID();
        } else {newgoals = new ArrayList<>();}
        newgoal.setID(count + 1);
        newgoals.add(newgoal);
        update(sort(newgoals), cutindex);
        marshall();
    }
    public boolean delete(int cutindex, CurrentGoal currentGoal) {
        try {
            List<CurrentGoal> cutgoallist = getallcutgoals(cutindex);
            cutgoallist.remove(currentGoal);
            update(sort(cutgoallist), cutindex);
            marshall();
            return true;
        } catch (Exception e) {return false;}
    }
    public List<CurrentGoal> sort(List<CurrentGoal> goallist) {
        try {
            BeanComparator bc = new BeanComparator(CurrentGoal.class, "getGoal_Hours");
            Collections.sort(goallist, bc);
            int count = 1;
            for (CurrentGoal i : goallist) {i.setID(count); count++;}
            return goallist;
        } catch (Exception ignored) {return null;}
    }
    public boolean goaliscompleted(CurrentGoal currentGoal, double currentpracticedhours) {
        return currentGoal.getGoal_Hours() >= currentpracticedhours;
    }
    public void checkifgoalscompleted(int cutindex, double currentpracticedhours, CompletedGoals completedGoals) {
        List<CurrentGoal> cutgoals = getallcutgoals(cutindex);
        int oldsize = cutgoals.size();
        for (kujiin.xml.CurrentGoal i : cutgoals) {
            if (goaliscompleted(i, currentpracticedhours)) {
                if (! completedGoals.completegoal(cutindex, i)) {
                    Tools.showerrordialog("Error", "Cannot Complete This Goal", "Check File Permissions");
                } else {cutgoals.remove(i);}
            }
        }
        if (cutgoals.size() != oldsize) {
            update(sort(cutgoals), cutindex);
            try {marshall();}
            catch (JAXBException ignored) {Tools.showerrordialog("Error", "Cannot Write Current Goals To XML File", "Check File Permissions");}
        }
    }
    public List<CurrentGoal> getallcutgoals(int cutindex) {
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
    public void update(List<CurrentGoal> cutgoallist, int cutindex) {
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
    public CurrentGoal getgoal(int cutindex, Integer goalindex) {
        try {return getallcutgoals(cutindex).get(goalindex);}
        catch (ArrayIndexOutOfBoundsException | NullPointerException ignored) {return null;}
    }
    public boolean goalsexist(int cutindex) {
        return getallcutgoals(cutindex) != null && getallcutgoals(cutindex).size() > 0;
    }
}
