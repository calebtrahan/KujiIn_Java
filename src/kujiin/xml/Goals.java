package kujiin.xml;

import kujiin.ui.MainController;
import kujiin.ui.dialogs.alerts.InformationDialog;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

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
    public List<Goal> getplaybackItemGoals(int index) {
        List<Goal> goalstoreturn = null;
        switch (index) {
            case 0: goalstoreturn = QiGongGoals; break;
            case 1: goalstoreturn = RinGoals; break;
            case 2: goalstoreturn = KyoGoals; break;
            case 3: goalstoreturn = TohGoals; break;
            case 4: goalstoreturn = ShaGoals; break;
            case 5: goalstoreturn = KaiGoals; break;
            case 6: goalstoreturn = JinGoals; break;
            case 7: goalstoreturn = RetsuGoals; break;
            case 8: goalstoreturn = ZaiGoals; break;
            case 9: goalstoreturn = ZenGoals; break;
            case 10: goalstoreturn = EarthGoals; break;
            case 11: goalstoreturn = AirGoals; break;
            case 12: goalstoreturn = FireGoals; break;
            case 13: goalstoreturn = WaterGoals; break;
            case 14: goalstoreturn = VoidGoals; break;
            case 15: goalstoreturn = TotalGoals; break;
        }
        if (goalstoreturn != null) {return goalstoreturn;}
        else {return new ArrayList<>();}
    }
    public List<Goal> getGoalsForPlaybackItem(int playbackitemindex, boolean includecurrent, boolean includecompleted) {
        if (includecompleted && includecurrent) {return getplaybackItemGoals(playbackitemindex);}
        else {
            List<Goal> filteredgoals = new ArrayList<>();
            for (Goal i : getplaybackItemGoals(playbackitemindex)) {
                if (includecurrent && ! i.getCompleted()) {filteredgoals.add(i);}
                else if (includecompleted && i.getCompleted()) {filteredgoals.add(i);}
            }
            return filteredgoals;
        }
    }
    public Goal getCurrentGoal(int index) {
        List<Goal> goals = getplaybackItemGoals(index);
        if (goals != null) {
            for (Goal i : goals) {
                if (! i.getCompleted()) {return i;}
            }
        }
        return null;
    }
    public void set(int index, List<Goal> goallist) {
        switch (index) {
            case 0: QiGongGoals = goallist; break;
            case 1: RinGoals = goallist; break;
            case 2: KyoGoals = goallist; break;
            case 3: TohGoals = goallist; break;
            case 4: ShaGoals = goallist; break;
            case 5: KaiGoals = goallist; break;
            case 6: JinGoals = goallist; break;
            case 7: RetsuGoals = goallist; break;
            case 8: ZaiGoals = goallist; break;
            case 9: ZenGoals = goallist; break;
            case 10: EarthGoals = goallist; break;
            case 11: AirGoals = goallist; break;
            case 12: FireGoals = goallist; break;
            case 13: WaterGoals = goallist; break;
            case 14: VoidGoals = goallist; break;
            case 15: TotalGoals = goallist; break;
            default:
        }
        marshall();
    }
    public void remove(int playbackitemindex, Goal goal) {
        List<Goal> goalslist = getplaybackItemGoals(playbackitemindex);
        goalslist.remove(goal);
        set(playbackitemindex, goalslist);
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
                new InformationDialog(Root.getPreferences(), "Information", "Couldn't Open Current Goals XML File", "Check Read File Permissions Of " + Preferences.GOALSXMLFILE.getAbsolutePath(), false);
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
            e.printStackTrace();
            new InformationDialog(Root.getPreferences(), "Information", "Couldn't Save Current Goals XML File", "Check Write File Permissions Of " + Preferences.GOALSXMLFILE.getAbsolutePath(), false);
        }
    }

// Utility
    private void sort() {

    }

}