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

public class Cut extends Meditatable {

    public Cut(int number, String name, String briefsummary, This_Session thisession, ToggleButton aSwitch, TextField value) {
        super(number, name, briefsummary, thisession, aSwitch, value);
        if (thisession.Root.getOptions().getProgramOptions().getTooltips()) {
            Value.setTooltip(new Tooltip("Minutes You Want To Practice " + name));
        }
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
                thisession.Root.getEntrainments().setmeditatableEntrainment(number, entrainment);
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
        if (duration.equals(Duration.ZERO)) {return false;}
        int index = allmeditatablestoplay.indexOf(this);
        Meditatable meditatableafter = null;
        if (index != allmeditatablestoplay.size() - 1) {meditatableafter = allmeditatablestoplay.get(index + 1);}
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled() && meditatableafter != null) {
            if (meditatableafter.getNameForFiles().equals("qi")) {entrainment.setRampfile(entrainment.ramp_get(1));}
            else {entrainment.setRampfile(entrainment.ramp_get(0));}
            return super.creation_buildEntrainment() && entrainment.getRampfile().isValid();
        }
        return super.creation_buildEntrainment();
    }

// Playback
    @Override
    public void entrainmenterror() {
        System.out.println("Entrainment Error");
        // Pause Ambience If Exists
        if (thisession.Root.dialog_YesNoConfirmation("Confirmation", "An Error Occured While Playing " + name +
                "'s Entrainment. Problem File Is: '" + entrainmentplayer.getMedia().getSource() + "'",
                "Retry Playing This File? (Pressing Cancel Will Completely Stop Session Playback)")) {
                entrainmentplayer.stop();
                entrainmentplayer.play();
                entrainmentplayer.setOnError(this::entrainmenterror);
        } else {thisession.player_error();}
    }
    @Override
    public void ambienceerror() {
        System.out.println("Ambience Error!");
        // Pause Entrainment
        if (thisession.Root.dialog_YesNoConfirmation("Confirmation", "An Error Occured While Playing " + name +
                        "'s Ambience. Problem File Is: '" + ambienceplayer.getMedia().getSource() + "'",
                "Retry Playing This File? (Pressing Cancel Will Completely Stop Session Playback)")) {
            ambienceplayer.stop();
            ambienceplayer.play();
            ambienceplayer.setOnError(this::ambienceerror);
        } else {thisession.player_error();}
    }

// Goals

// Export

}
