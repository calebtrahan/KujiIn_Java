package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import kujiin.ui.MainController;
import kujiin.xml.Preferences;
import kujiin.xml.SoundFile;


public class Element extends SessionPart {

    public Element(int number, String name, MainController Root, ToggleButton aSwitch, TextField value) {
        super(number, name, Root, aSwitch, value);
        if (Root.getPreferences().getUserInterfaceOptions().getTooltips()) {Value.setTooltip(new Tooltip("Minutes You Want To Practice " + name));}
        else {Value.setTooltip(null);}
    }

// Gettters And Setters
    @Override
    public Tooltip getTooltip() {
        return super.getTooltip();
    }
    @Override
    public String getNameForFiles() {return "qi";}

// Entrainment
    @Override
    public int startup_entrainmentpartcount() {
        return 10;
    }

// Creation
    @Override
    public boolean creation_buildEntrainment() {
        if (root.getPreferences().getSessionOptions().getRampenabled()) {
            try {
                int index = allsessionpartstoplay.indexOf(this);
                SessionPart parttotest = allsessionpartstoplay.get(index + 1);
                SoundFile rampfile;
                if (parttotest instanceof  Qi_Gong || parttotest instanceof Element) {rampfile = entrainment.getFreq();}
                else {rampfile = entrainment.ramp_get(Preferences.CUTNAMES.indexOf(parttotest.name.toUpperCase()));}
                entrainment.setRampfile(rampfile);
                if (ramponly) {setDuration(Duration.millis(entrainment.getRampfile().getDuration()));}
                return super.creation_buildEntrainment() && entrainment.getRampfile().isValid();
            } catch (IndexOutOfBoundsException ignored) {return super.creation_buildEntrainment();}
        } else {return super.creation_buildEntrainment();}
    }

}
