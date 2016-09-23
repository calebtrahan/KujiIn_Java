package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;


public class Element extends SessionPart {

    public Element(int number, String name, This_Session thissession, ToggleButton aSwitch, TextField value) {
        super(number, name, thissession, aSwitch, value);
        if (thissession.Root.getOptions().getProgramOptions().getTooltips()) {Value.setTooltip(new Tooltip("Minutes You Want To Practice " + name));}
        else {Value.setTooltip(null);}
    }

    @Override
    public Tooltip getTooltip() {
        return super.getTooltip();
    }

    @Override
    public String getNameForFiles() {return "qi";}

// Entrainment
    @Override
    public int entrainmentpartcount() {
        return 10;
    }

// Creation Methods
    @Override
    public boolean creation_buildEntrainment() {
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            try {
                int index = allsessionpartstoplay.indexOf(this);
                SessionPart parttotest = allsessionpartstoplay.get(index + 1);
                SoundFile rampfile;
                if (parttotest instanceof  Qi_Gong || parttotest instanceof Element) {rampfile = entrainment.getFreq();}
                else {rampfile = entrainment.ramp_get(Options.CUTNAMES.indexOf(parttotest.name.toUpperCase()));}
                entrainment.setRampfile(rampfile);
                if (ramponly) {setDuration(Duration.millis(entrainment.getRampfile().getDuration()));}
                return super.creation_buildEntrainment() && entrainment.getRampfile().isValid();
            } catch (IndexOutOfBoundsException ignored) {return super.creation_buildEntrainment();}
        } else {return super.creation_buildEntrainment();}
    }

}
