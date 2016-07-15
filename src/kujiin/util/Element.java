package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import kujiin.xml.Goals;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;

import java.io.File;
import java.util.List;

// TODO Put Add A Japanese Character Symbol Picture (Representing Each Cut) To Creator Cut Labels (With Tooltips Displaying Names)
// TODO Add Tooltips To Cuts Saying A One Word Brief Summary (Rin -> Strength, Kyo -> Control, Toh->Harmony)
public class Element extends Meditatable {

    public Element(int number, String name, int duration, String briefsummary, This_Session thissession, ToggleButton aSwitch, TextField value) {
        super(number, name, duration, briefsummary, thissession, aSwitch, value);
        Value.setTooltip(new Tooltip("Minutes You Want To Practice " + name));
        if (entrainment.getFreqshort() == null) {entrainment.setFreqshort(new SoundFile(new File(Options.DIRECTORYENTRAINMENT, "entrainment/ELEMENT1.mp3"))); entrainment.calculateshortfreqduration();}
        if (entrainment.getFreqlong() == null) {entrainment.setFreqlong(new SoundFile(new File(Options.DIRECTORYENTRAINMENT, "entrainment/ELEMENT5.mp3"))); entrainment.calculatelongfreqduration();}
    }

// GUI
    public boolean hasValidValue() {
    return Switch.isSelected() && Integer.parseInt(Value.getText()) != 0;
}
    public void toggleswitch() {
        if (Switch.isSelected()) {
            Value.setText("0");
            Value.setDisable(false);
            Value.setTooltip(new Tooltip("Practice Time For " + name + " (In Minutes)"));
        } else {
            Value.setText("0");
            Value.setDisable(true);
            Value.setTooltip(new Tooltip(name + " Is Disabled. Click " + name + " Button Above To Enable"));
        }
    }
    public void changevalue(int newvalue) {
        if (newvalue == 0) {
            Switch.setSelected(false);
            toggleswitch();
        } else {
            Switch.setSelected(true);
            Value.setDisable(false);
            Value.setText(Integer.toString(newvalue));
            setDuration(newvalue);
        }
    }

// Creation
    @Override
    public boolean buildEntrainment() {
        int index = allmeditatablestoplay.indexOf(this);
        Meditatable cutorelementbefore = null;
        Meditatable cutorelementafter = null;
        if (index != 0) {cutorelementbefore = allmeditatablestoplay.get(index - 1);}
        if (index != allmeditatablestoplay.size() - 1) {cutorelementafter = allmeditatablestoplay.get(index + 1);}
        int durationinminutes = getdurationinminutes();
        SoundFile rampinfile = null;
        SoundFile rampoutfile = null;
        if (cutorelementbefore != null || cutorelementafter != null) {
            if (cutorelementbefore != null) {
               rampinfile = new SoundFile(new File(Options.DIRECTORYRAMP, "elementin" + cutorelementbefore.name.toLowerCase() + ".mp3"));
                durationinminutes -= 1;
            }
            if (cutorelementafter != null) {
                rampoutfile = new SoundFile(new File(Options.DIRECTORYRAMP, "elementout" + cutorelementafter.name.toLowerCase() + ".mp3"));
                durationinminutes -= 1;
            }
        }
        int fivetimes = (int) Math.ceil(durationinminutes / 5);
        int singletimes = (int) Math.ceil(durationinminutes % 5);
        for (int i = 0; i < fivetimes; i++) {entrainment.created_add(entrainment.getFreqlong());}
        for (int i = 0; i < singletimes; i++) {entrainment.created_add(entrainment.getFreqshort());}
        entrainment.shuffleCreated();
        if (rampinfile != null) {entrainment.created_add(0, rampinfile);}
        if (rampoutfile != null) {entrainment.created_add(rampoutfile);}
        return entrainment.created_getAll().size() > 0 && entrainment.gettotalCreatedDuration() / 1000 >= getdurationinseconds();
    }

// Goals
    @Override
    public void setGoals(List<Goals.Goal> goalslist) {
        GoalsController.update(goalslist, number);
    }
    @Override
    public List<Goals.Goal> getGoals(boolean includecompleted) {
        return GoalsController.getallcutgoals(number, includecompleted);
    }

// Export

}
