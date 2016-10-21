package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import kujiin.ui.MainController;
import kujiin.util.enums.ReferenceType;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;

import java.io.File;

public class Qi_Gong extends SessionPart {
    private String Summary;

    public Qi_Gong (int number, String name, MainController Root,  ToggleButton aSwitch, TextField value) {
        super(number, name, Root, aSwitch, value);
        if (Root.getOptions().getUserInterfaceOptions().getTooltips()) {
            if (number == 0) {Value.setTooltip(new Tooltip("Minutes You Want To Collect Qi/Prana Preceding The Session"));}
            else {Value.setTooltip(new Tooltip("Minutes You Want To Collect Qi/Prana Following The Session"));}
        } else {Value.setTooltip(null);}
    }

// Getters And Setters
    @Override
    public String getNameForFiles() {return "qi";}
    @Override
    public String getNameForChart() {
        if (name.equals("Presession")) {return "Pre";}
        else return "Post";
    }
    @Override
    public String getNameForReference() {
        return "Qi-Gong";
    }

// Description Setters
    public void setSummary(String summary) {
        Summary = summary;
    }
    @Override
    public Tooltip getTooltip() {return new Tooltip(Summary);}

// Entrainment
    @Override
    public int startup_entrainmentpartcount() {
        return 10;
    }

// Creation
    @Override
    public boolean creation_buildEntrainment() {
        if (root.getOptions().getSessionOptions().getRampenabled()) {
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
            else {rampfile = entrainment.ramp_get(Options.CUTNAMES.indexOf(parttotest.name.toUpperCase()) - 1);}
            entrainment.setRampfile(rampfile);
            if (ramponly) {setDuration(Duration.millis(entrainment.getRampfile().getDuration()));}
            return super.creation_buildEntrainment() && entrainment.getRampfile().isValid();
        } else {return super.creation_buildEntrainment();}
    }

    @Override
    public File reference_getFile() {
        ReferenceType referenceType = root.getOptions().getSessionOptions().getReferencetype();
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
