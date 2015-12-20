package kujiin.util.xml;


import kujiin.This_Session;
import kujiin.dialogs.DisplayCompletedGoalsDialog;
import kujiin.lib.BeanComparator;
import kujiin.util.lib.GuiUtils;
import kujiin.util.lib.TimeUtils;

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

@XmlRootElement(name = "Completed Goals")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CompletedGoals {
    private List<CompletedGoal> CompletedGoal;

    public CompletedGoals() {
        try {populatefromxml();}
        catch (JAXBException ignored) {}
    }

// Getters And Setters
    public List<kujiin.util.xml.CompletedGoal> getCompletedGoal() {return CompletedGoal;}
    public void setCompletedGoal(List<kujiin.util.xml.CompletedGoal> completedGoal) {CompletedGoal = completedGoal;}

// XML Interaction
    public void populatefromxml() throws JAXBException {
        if (This_Session.completedgoalsxmlfile.exists()) {
            JAXBContext context = JAXBContext.newInstance(CompletedGoals.class);
            Unmarshaller createMarshaller = context.createUnmarshaller();
            CompletedGoals completedGoals = (CompletedGoals) createMarshaller.unmarshal(This_Session.completedgoalsxmlfile);
            setCompletedGoal(completedGoals.getCompletedGoal());
        }
    }
    public void addcompletedgoal(CompletedGoal completedGoal) throws JAXBException {
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
    public boolean goalcompleted(CurrentGoal currentGoal) {
        double hours = currentGoal.getGoal_Hours();
        kujiin.util.xml.CompletedGoal newcompletedgoal = new CompletedGoal();
        newcompletedgoal.setDate_Completed(TimeUtils.convertfromlocaldatetostring(LocalDate.now()));
        newcompletedgoal.setGoal_Hours(hours);
        try {addcompletedgoal(newcompletedgoal); return true;}
        catch (JAXBException ignored) {return false;}
    }
    public void sortcompletedgoals() {
        if (getCompletedGoal() != null) {
            BeanComparator bc = new BeanComparator(kujiin.util.xml.CompletedGoal.class, "getGoal_Hours");
            List<CompletedGoal> completedGoalList = getCompletedGoal();
            Collections.sort(completedGoalList, bc);
            setCompletedGoal(completedGoalList);
        }
    }
    public void displaycompletedgoals() {
        if (getCompletedGoal() != null) {
            DisplayCompletedGoalsDialog dcg = new DisplayCompletedGoalsDialog(getCompletedGoal());
            dcg.showAndWait();
        } else {GuiUtils.showinformationdialog("Cannot Display", "Cannot Display", "No Goals Completed Yet");}
    }
}
