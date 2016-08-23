package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;

import java.io.File;


public class Element extends Meditatable {

    public Element(int number, String name, int duration, String briefsummary, This_Session thissession, ToggleButton aSwitch, TextField value) {
        super(number, name, duration, briefsummary, thissession, aSwitch, value);
        if (thissession.Root.getOptions().getProgramOptions().getTooltips()) {Value.setTooltip(new Tooltip("Minutes You Want To Practice " + name));}
        else {Value.setTooltip(null);}
    }

// Entrainment
    @Override
    public void entrainment_populate() {
//        ArrayList<String> rampvariations = thisession.getallCuts().stream().map(i -> i.name).collect(Collectors.toCollection(ArrayList::new));
        int rampvariationcount = 0;

        super.entrainment_populate();
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

// Creation
    @Override
    public boolean creation_buildEntrainment() {
        int index = allmeditatablestoplay.indexOf(this);
        Meditatable meditatablebefore = null;
        Meditatable meditatableafter = null;
        if (index != 0) {meditatablebefore = allmeditatablestoplay.get(index - 1);}
        if (index != allmeditatablestoplay.size() - 1) {meditatableafter = allmeditatablestoplay.get(index + 1);}
        Duration timeleft = getduration();
        Duration freqlongduration = new Duration(entrainment.getFreqlong().getDuration());
        Duration freqshortduration = new Duration(entrainment.getFreqshort().getDuration());
        SoundFile rampinfile = null;
        SoundFile rampoutfile = null;
        if (meditatablebefore != null || meditatableafter != null) {
            if (meditatablebefore != null) {
               rampinfile = new SoundFile(new File(Options.DIRECTORYRAMP, "elementin" + meditatablebefore.name.toLowerCase() + ".mp3"));
                timeleft = timeleft.subtract(Duration.minutes(1));
            }
            if (meditatableafter != null) {
                rampoutfile = new SoundFile(new File(Options.DIRECTORYRAMP, "elementout" + meditatableafter.name.toLowerCase() + ".mp3"));
                timeleft = timeleft.subtract(Duration.minutes(1));
                rampenabled = true;
            } else {rampenabled = false;}
        }
        while (timeleft.greaterThan(freqlongduration)) {
            entrainment.created_add(entrainment.getFreqlong());
            timeleft = timeleft.subtract(freqlongduration);
        }
        while (timeleft.greaterThan(Duration.ZERO)) {
            entrainment.created_add(entrainment.getFreqshort());
            timeleft = timeleft.subtract(freqshortduration);
        }
        entrainment.shuffleCreated();
        if (rampinfile != null) {entrainment.created_add(0, rampinfile);}
        if (rampoutfile != null) {entrainment.created_add(rampoutfile);}
        return entrainment.created_getAll().size() > 0 && entrainment.gettotalCreatedDuration().greaterThanOrEqualTo(getduration());
    }

// Goals

// Export

}
