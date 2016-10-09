package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import kujiin.ui.MainController;
import kujiin.util.enums.StartupCheckType;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;

import java.io.File;

import static kujiin.ui.MainController.getallCutNames;


public class Element extends SessionPart {

    public Element(int number, String name, MainController Root, ToggleButton aSwitch, TextField value) {
        super(number, name, Root, aSwitch, value);
        if (Root.getOptions().getProgramOptions().getTooltips()) {Value.setTooltip(new Tooltip("Minutes You Want To Practice " + name));}
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
    @Override
    public SoundFile startup_getnextentrainment() throws IndexOutOfBoundsException {
        SoundFile soundFile;
        File file;
        switch (startupchecks_entrainment_count) {
            case 0:
                soundFile = entrainment.getFreq();
                file = new File(kujiin.xml.Options.DIRECTORYENTRAINMENT, getNameForFiles().toUpperCase() + ".mp3");
                break;
            default:
                if (startupchecks_entrainment_count > startup_entrainmentpartcount()) {
                    startupCheckType = StartupCheckType.AMBIENCE;
                    System.out.println("Switched To Ambience At " + startupchecks_entrainment_count);
                    throw new IndexOutOfBoundsException();
                }
                soundFile = entrainment.ramp_get(startupchecks_entrainment_count);
                file = new File(kujiin.xml.Options.DIRECTORYENTRAINMENT, "ramp/" + getNameForFiles() + "to" + getallCutNames().get(startupchecks_entrainment_count - 1).toLowerCase() + ".mp3");
                break;
        }
        if (soundFile == null) {
            if (file.exists()) {
                soundFile = new SoundFile(file);}
            else {}
        }
        return soundFile;
    }

// Creation
    @Override
    public boolean creation_buildEntrainment() {
        if (root.getOptions().getSessionOptions().getRampenabled()) {
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
