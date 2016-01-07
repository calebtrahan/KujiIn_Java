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
    private List<CurrentGoal> CurrentGoal;

    public CurrentGoals() {}

// Getters And Setters
    public List<kujiin.xml.CurrentGoal> getCurrentGoal() {return CurrentGoal;}
    public void setCurrentGoal(List<kujiin.xml.CurrentGoal> currentGoal) {CurrentGoal = currentGoal;}

// Other Methods
    public void populatefromxml() throws JAXBException {
        if (This_Session.currentgoalsxmlfile.exists()) {
            JAXBContext context = JAXBContext.newInstance(CurrentGoals.class);
            Unmarshaller createMarshaller = context.createUnmarshaller();
            CurrentGoals currentGoals = (CurrentGoals) createMarshaller.unmarshal(This_Session.currentgoalsxmlfile);
            setCurrentGoal(currentGoals.getCurrentGoal());
        }
    }
    public void writecurrentgoalstoxml() throws JAXBException {
        if (This_Session.currentgoalsxmlfile.exists()) {
            JAXBContext context = JAXBContext.newInstance(CurrentGoals.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.marshal(getCurrentGoal(), This_Session.currentgoalsxmlfile);
        }
    }
    public void addnewgoal(CurrentGoal newgoal) throws JAXBException {
        if (This_Session.currentgoalsxmlfile.exists()) {populatefromxml();}
        List<CurrentGoal> newgoals = getCurrentGoal();
        int count = 0;
        if (newgoals != null && newgoals.size() > 0) {
            sortcurrentgoals();
            count = getlastcurrentgoal().getID();
        } else {newgoals = new ArrayList<>();}
        newgoal.setID(count + 1);
        newgoals.add(newgoal);
        setCurrentGoal(newgoals);
        JAXBContext context = JAXBContext.newInstance(CurrentGoals.class);
        Marshaller createMarshaller = context.createMarshaller();
        createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        createMarshaller.marshal(this, This_Session.currentgoalsxmlfile);
    }
    public CurrentGoal getfirstcurrentgoal() {
        if (getCurrentGoal() == null) {return null;}
        else {return getCurrentGoal().get(0);}
    }
    public CurrentGoal getlastcurrentgoal() {
        if (getCurrentGoal() == null) {return null;}
        else {return getCurrentGoal().get(getCurrentGoal().size() - 1);}
    }
    public List<CurrentGoal> getallcurrentgoals() {return getCurrentGoal();}
    public boolean goalsexist() {
        return getCurrentGoal() != null && getCurrentGoal().size() > 0;
    }
    public boolean deletegoal(Integer id) {
        if (goalsexist()) {
            List<CurrentGoal> templist = getCurrentGoal();
            for (kujiin.xml.CurrentGoal i : templist) {
                if (i.getID().equals(id)) {templist.remove(i); return true;}
            }
            return false;
        } else {return false;}
    }
    public void sortcurrentgoals() {
        if (getCurrentGoal() != null) {
            BeanComparator bc = new BeanComparator(kujiin.xml.CurrentGoal.class, "getGoal_Hours");
            List<CurrentGoal> currentGoalsList = getCurrentGoal();
            Collections.sort(currentGoalsList, bc);
            setCurrentGoal(currentGoalsList);
        }
        int count = 1;
        for (kujiin.xml.CurrentGoal i : getCurrentGoal()) {i.setID(count); count++;}
    }
    public CurrentGoal getgoal(Integer id) {
        if (getCurrentGoal() != null && getCurrentGoal().size() > 0) {
            for (kujiin.xml.CurrentGoal i : getCurrentGoal()) {
                if (i.getID().equals(id)) {return i;}
            }
            return null;
        } else {return null;}
    }
    public void checkforcompletedgoals(double currentpracticedhours, CompletedGoals completedGoals) {
        int oldsize = getCurrentGoal().size();
        List<CurrentGoal> newgoalslist = getCurrentGoal();
        for (kujiin.xml.CurrentGoal i : getCurrentGoal()) {
            if (currentpracticedhours >= i.getGoal_Hours()) {
                CompletedGoal completedGoal = new CompletedGoal();
                completedGoal.setDate_Completed(Tools.gettodaysdate());
                completedGoal.setGoal_Hours(i.getGoal_Hours());
                try {completedGoals.addcompletedgoal(completedGoal);}
                catch (JAXBException ignored) {
                    Tools.showerrordialog("Error", "Cannot Complete This Goal", "Check File Permissions");
                    return;
                }
                newgoalslist.remove(i);
            }
        }
        setCurrentGoal(newgoalslist);
        if (getCurrentGoal().size() != oldsize) {
            try {writecurrentgoalstoxml();}
            catch (JAXBException ignored) {Tools.showerrordialog("Error", "Cannot Write Current Goals To XML File", "Check File Permissions");}
        }
    }
}
