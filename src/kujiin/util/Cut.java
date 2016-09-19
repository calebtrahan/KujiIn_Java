package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;

import java.io.File;

public class Cut extends SessionPart {
    private String FocusPoint;
    private String Concept;
    private String Mantra_Meaning;
    private String Side_Effects;

    public Cut(int number, String name, This_Session thisession, ToggleButton aSwitch, TextField value) {
        super(number, name, thisession, aSwitch, value);
        if (thisession.Root.getOptions().getProgramOptions().getTooltips()) {
            Value.setTooltip(new Tooltip("Minutes You Want To Practice " + name));
        }
    }

// Description Methods
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
    public void entrainment_populate() {
//        System.out.println("Looping Through " + name + " " + entrainmentchecker_partcount);
        File expectedentrainmentfile;
        SoundFile actualsoundfile;
        switch (entrainmentchecker_partcount) {
            case 0:
                actualsoundfile = entrainment.getFreq();
                expectedentrainmentfile = new File(Options.DIRECTORYENTRAINMENT, getNameForFiles().toUpperCase() + ".mp3");
                break;
            case 1:
                actualsoundfile = entrainment.ramp_get(0);
                try {
                    expectedentrainmentfile = new File(Options.DIRECTORYENTRAINMENT, "ramp/" + getNameForFiles() + "to" +
                            entrainmentchecker_partcutnames.get(entrainmentchecker_partcutnames.indexOf(getNameForFiles()) + 1) + ".mp3");
                } catch (IndexOutOfBoundsException ignored) {expectedentrainmentfile = new File(Options.DIRECTORYENTRAINMENT, "ramp/" + getNameForFiles() + "toqi.mp3");}
                break;
            case 2:
                actualsoundfile = entrainment.ramp_get(1);
                expectedentrainmentfile = new File(Options.DIRECTORYENTRAINMENT, "ramp/" + getNameForFiles() + "toqi.mp3");
                break;
            default:
                entrainmentready = true;
                thisession.Root.getEntrainments().setsessionpartEntrainment(number, entrainment);
                return;
        }
        if (expectedentrainmentfile.exists()) {
            if (actualsoundfile == null || ! actualsoundfile.isValid()) {
                entrainmentchecker_calculateplayer = new MediaPlayer(new Media(expectedentrainmentfile.toURI().toString()));
                File finalExpectedentrainmentfile = expectedentrainmentfile;
                entrainmentchecker_calculateplayer.setOnReady(() -> {
                    SoundFile soundFile = new SoundFile(finalExpectedentrainmentfile);
                    soundFile.setDuration(entrainmentchecker_calculateplayer.getTotalDuration().toMillis());
                    if (entrainmentchecker_partcount == 0) {entrainment.setFreq(soundFile);}
                    else {entrainment.ramp_add(soundFile);}
                    entrainmentchecker_calculateplayer.dispose();
                    entrainmentchecker_partcount++;
                    entrainment_populate();
                });
            } else {
                entrainmentchecker_partcount++;
                entrainment_populate();
            }
        } else {
            entrainmentmissingfiles = true;
            entrainmentchecker_missingfiles.add(expectedentrainmentfile);
            entrainmentchecker_partcount++;
            entrainment_populate();
        }
    }

// Creation
    @Override
    public boolean creation_buildEntrainment() {
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            try {
                int index = allsessionpartstoplay.indexOf(this);
                SessionPart partafter = allsessionpartstoplay.get(index + 1);
                if (partafter instanceof Qi_Gong || partafter instanceof Element) {entrainment.setRampfile(entrainment.ramp_get(1));}
                else {entrainment.setRampfile(entrainment.ramp_get(0));}
                if (ramponly) {setDuration(Duration.millis(entrainment.getRampfile().getDuration()));}
                return super.creation_buildEntrainment() && entrainment.getRampfile().isValid();
            } catch (IndexOutOfBoundsException ignored) {return false;}
        }
        return super.creation_buildEntrainment();
    }

}
