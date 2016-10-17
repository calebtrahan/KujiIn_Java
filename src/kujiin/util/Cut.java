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

public class Cut extends SessionPart {
    private String FocusPoint;
    private String Concept;
    private String Mantra_Meaning;
    private String Side_Effects;

    public Cut(int number, String name, MainController Root, ToggleButton aSwitch, TextField value) {
        super(number, name, Root, aSwitch, value);
        if (root.getOptions().getUserInterfaceOptions().getTooltips()) {
            Value.setTooltip(new Tooltip("Minutes You Want To Practice " + name));
        }
    }

// Description
    public void setFocusPoint(String focusPoint) {
        FocusPoint = focusPoint;
    }
    public void setConcept(String concept) {
        Concept = concept;
    }
    public void setMantra_Meaning(String mantra_Meaning) {
        Mantra_Meaning = mantra_Meaning;
    }
    public void setSide_Effects(String side_Effects) {
        Side_Effects = side_Effects;
    }

// Getters And Setters
    @Override
    public Tooltip getTooltip() {
        StringBuilder a = new StringBuilder();
        a.append("Focus Point: ").append(FocusPoint).append("\n");
        a.append("Concept: ").append(Concept).append("\n");
        a.append("Mantra Meaning: ").append(Mantra_Meaning).append("\n");
        a.append("Side Effects: ").append(Side_Effects).append("\n");
        return new Tooltip(a.toString());
    }

// Entrainment
    @Override
    public int startup_entrainmentpartcount() {
        return 3;
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
            case 1:
                soundFile = entrainment.ramp_get(0);
                if (number != 9) {
                    file = new File(kujiin.xml.Options.DIRECTORYENTRAINMENT, "ramp/" + getNameForFiles() + "to" +
                            getallCutNames().get(getallCutNames().indexOf(name) + 1).toLowerCase() + ".mp3");
                } else {file = new File(Options.DIRECTORYENTRAINMENT, "ramp/" + getNameForFiles() + "toqi.mp3");}
                break;
            case 2:
                if (number == 9) {startupCheckType = StartupCheckType.AMBIENCE; throw new IndexOutOfBoundsException();}
                else {
                    soundFile = entrainment.ramp_get(1);
                    file = new File(kujiin.xml.Options.DIRECTORYENTRAINMENT, "ramp/" + getNameForFiles() + "toqi.mp3");
                    break;
                }
            default:
                throw new IndexOutOfBoundsException();
        }
        if (soundFile == null) {
            if (file.exists()) {soundFile = new SoundFile(file);}
            else {return null;}
        }
        return soundFile;
    }

// Creation
    @Override
    public boolean creation_buildEntrainment() {
        if (root.getOptions().getSessionOptions().getRampenabled()) {
            try {
                int index = allsessionpartstoplay.indexOf(this);
                SessionPart partafter = allsessionpartstoplay.get(index + 1);
                if ((partafter instanceof Qi_Gong || partafter instanceof Element) && ! name.equals("ZEN")) {entrainment.setRampfile(entrainment.ramp_get(1));}
                else {entrainment.setRampfile(entrainment.ramp_get(0));}
                if (ramponly) {setDuration(Duration.millis(entrainment.getRampfile().getDuration()));}
                return super.creation_buildEntrainment() && entrainment.getRampfile().isValid();
            } catch (IndexOutOfBoundsException ignored) {return false;}
        }
        return super.creation_buildEntrainment();
    }

}
