package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import kujiin.util.enums.ReferenceType;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;

import java.io.File;

public class Qi_Gong extends SessionPart {
    private String Summary;

    public Qi_Gong (int number, String name, This_Session thissession, ToggleButton aSwitch, TextField value) {
        super(number, name, thissession, aSwitch, value);
        if (thissession.Root.getOptions().getProgramOptions().getTooltips()) {
            if (number == 0) {Value.setTooltip(new Tooltip("Minutes You Want To Collect Qi/Prana Preceding The Session"));}
            else {Value.setTooltip(new Tooltip("Minutes You Want To Collect Qi/Prana Following The Session"));}
        } else {Value.setTooltip(null);}
    }

    @Override
    public String getNameForFiles() {return "qi";}
    @Override
    public String getNameForChart() {
        if (name.equals("Presession")) {return "Pre";}
        else return "Post";
    }

// Description Setters
    public void setSummary(String summary) {
        Summary = summary;
    }
    @Override
    public Tooltip getTooltip() {return new Tooltip(Summary);}

// Entrainment
    @Override
    public int entrainmentpartcount() {
        return 10;
    }

// Creation Methods
    @Override
    public boolean creation_buildEntrainment() {
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            int index = allsessionpartstoplay.indexOf(this);
            SessionPart parttotest;
            switch (number) {
                case 0:
                    parttotest = allsessionpartstoplay.get(index + 1);
                    break;
                case 15:
                    parttotest = allsessionpartstoplay.get(index - 1);
                    break;
                default:
                    parttotest = null;
            }
            SoundFile rampfile;
            if (parttotest instanceof  Qi_Gong || parttotest instanceof Element) {rampfile = entrainment.getFreq();}
            else {rampfile = entrainment.ramp_get(Options.CUTNAMES.indexOf(parttotest.name.toUpperCase()));}
            entrainment.setRampfile(rampfile);
            if (ramponly) {setDuration(Duration.millis(entrainment.getRampfile().getDuration()));}
            return super.creation_buildEntrainment() && entrainment.getRampfile().isValid();
        } else {return super.creation_buildEntrainment();}
    }

    @Override
    public File reference_getFile() {
        ReferenceType referenceType = thisession.Root.getOptions().getSessionOptions().getReferencetype();
        if (referenceType == null) {return null;}
        switch (referenceType) {
            case html: {
                String name = "Qi-Gong.html";
                return new File(Options.DIRECTORYREFERENCE, "html/" + name);
            }
            case txt: {
                String name = "Qi-Gong.txt";
                return new File(Options.DIRECTORYREFERENCE, "txt/" + name);
            }
            default:
                return null;
        }
    }
}
