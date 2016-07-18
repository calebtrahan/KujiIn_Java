package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;

import java.io.File;

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
        Meditatable meditatablebefore = null;
        Meditatable meditatableafter = null;
        if (index != 0) {meditatablebefore = allmeditatablestoplay.get(index - 1);}
        if (index != allmeditatablestoplay.size() - 1) {meditatableafter = allmeditatablestoplay.get(index + 1);}
        int durationinminutes = getdurationinminutes();
        SoundFile rampinfile = null;
        SoundFile rampoutfile = null;
        if (meditatablebefore != null || meditatableafter != null) {
            if (meditatablebefore != null) {
               rampinfile = new SoundFile(new File(Options.DIRECTORYRAMP, "elementin" + meditatablebefore.name.toLowerCase() + ".mp3"));
                durationinminutes -= 1;
            }
            if (meditatableafter != null) {
                rampoutfile = new SoundFile(new File(Options.DIRECTORYRAMP, "elementout" + meditatableafter.name.toLowerCase() + ".mp3"));
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

// Export

}
