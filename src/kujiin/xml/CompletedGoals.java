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
    private List<CompletedGoal> CompletedGoal;

    public CompletedGoals() {}

// Getters And Setters
    public List<kujiin.xml.CompletedGoal> getCompletedGoal() {return CompletedGoal;}
    public void setCompletedGoal(List<kujiin.xml.CompletedGoal> completedGoal) {CompletedGoal = completedGoal;}

// XML Processing
    public void unmarshall() throws JAXBException {
        if (This_Session.completedgoalsxmlfile.exists()) {
            JAXBContext context = JAXBContext.newInstance(CompletedGoals.class);
            Unmarshaller createMarshaller = context.createUnmarshaller();
            CompletedGoals completedGoals = (CompletedGoals) createMarshaller.unmarshal(This_Session.completedgoalsxmlfile);
            setCompletedGoal(completedGoals.getCompletedGoal());
        }
    }
    public void marshall() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(CompletedGoals.class);
        Marshaller createMarshaller = context.createMarshaller();
        createMarshaller.marshal(getCompletedGoal(), This_Session.completedgoalsxmlfile);
    }
    public void addgoal(CompletedGoal completedGoal) throws JAXBException {
        if (This_Session.completedgoalsxmlfile.exists()) {
            List<CompletedGoal> completedGoalsList = getCompletedGoal();
            if (completedGoalsList == null) {completedGoalsList = new ArrayList<>();}
            completedGoalsList.add(completedGoal);
            setCompletedGoal(completedGoalsList);
            JAXBContext context = JAXBContext.newInstance(CompletedGoals.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, This_Session.completedgoalsxmlfile);
        }
    }
    public void sortcompletedgoals() {
        try {
            BeanComparator bc = new BeanComparator(kujiin.xml.CompletedGoal.class, "getGoal_Hours");
            List<CompletedGoal> completedGoalList = getCompletedGoal();
            Collections.sort(completedGoalList, bc);
            setCompletedGoal(completedGoalList);
            int count = 1;
            for (kujiin.xml.CompletedGoal i : getCompletedGoal()) {i.setID(count); count++;}
        } catch (NullPointerException ignored) {}
    }
    public boolean goalcompleted(CurrentGoal currentGoal) {
        double hours = currentGoal.getGoal_Hours();
        CompletedGoal newcompletedgoal = new CompletedGoal();
        newcompletedgoal.setDate_Completed(Tools.gettodaysdate());
        newcompletedgoal.setGoal_Hours(hours);
        try {addgoal(newcompletedgoal); return true;}
        catch (JAXBException ignored) {return false;}
    }

// Goal Information Getters
    public CompletedGoal getgoalbyindex(Integer index) {
    try {
        if (getCompletedGoal() == null) {return null;}
        else {return getCompletedGoal().get(index);}
    } catch (ArrayIndexOutOfBoundsException ignored) {return null;}
}
    public List<CompletedGoal> getallgoals() {return getCompletedGoal();}
    public boolean goalsexist() {
        return getCompletedGoal() != null && getCompletedGoal().size() > 0;
    }
    public CompletedGoal getgoalbyid(Integer id) {
        if (getCompletedGoal() != null && getCompletedGoal().size() > 0) {
            for (kujiin.xml.CompletedGoal i : getCompletedGoal()) {
                if (i.getID().equals(id)) {return i;}
            }
            return null;
        } else {return null;}
    }

}
