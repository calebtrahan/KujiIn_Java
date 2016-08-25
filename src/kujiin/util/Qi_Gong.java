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

public class Qi_Gong extends Meditatable {
    protected boolean ramponly = false;

    public Qi_Gong (int number, String name, String briefsummary, This_Session thissession, ToggleButton aSwitch, TextField value) {
        super(number, name, briefsummary, thissession, aSwitch, value);
        if (thissession.Root.getOptions().getProgramOptions().getTooltips()) {
            if (number == 0) {Value.setTooltip(new Tooltip("Minutes You Want To Collect Qi/Prana Preceding The Session"));}
            else {Value.setTooltip(new Tooltip("Minutes You Want To Collect Qi/Prana Following The Session"));}
        } else {Value.setTooltip(null);}
    }

    // Getters & Setters
    public boolean isRamponly() {
        return ramponly;
    }
    public void setRamponly(boolean ramponly) {
        this.ramponly = ramponly;
    }
    public void setDuration(Duration duration) {this.duration = duration;}
    @Override
    public String getNameForFiles() {return "qi";}
    @Override
    public String getNameForChart() {
        if (name.equals("Presession")) {return "Pre";}
        else return "Post";
    }


// Entrainment
    @Override
    public void entrainment_populate() {
        File expectedentrainmentfile;
        SoundFile actualsoundfile;
        if (entrainmentchecker_partcount == 0) {
            actualsoundfile = entrainment.getFreq();
            expectedentrainmentfile = new File(Options.DIRECTORYENTRAINMENT, getNameForFiles().toUpperCase() + ".mp3");
        } else {
            try {
                actualsoundfile = entrainment.ramp_get(entrainmentchecker_partcount);
                expectedentrainmentfile = new File(Options.DIRECTORYENTRAINMENT, "ramp/" + getNameForFiles() + "to" + entrainmentchecker_partcutnames.get(entrainmentchecker_partcount - 1) + ".mp3");
            } catch (IndexOutOfBoundsException ignored) {
                entrainmentready = true;
                thisession.Root.getEntrainments().setmeditatableEntrainment(number, entrainment);
                return;
            }
        }
        if (expectedentrainmentfile.exists()) {
            if (actualsoundfile == null || ! actualsoundfile.isValid()) {
                entrainmentchecker_calculateplayer = new MediaPlayer(new Media(expectedentrainmentfile.toURI().toString()));
                entrainmentchecker_calculateplayer.setOnReady(() -> {
                    SoundFile soundFile = new SoundFile(expectedentrainmentfile);
                    soundFile.setDuration(entrainmentchecker_calculateplayer.getTotalDuration().toMillis());
                    if (this.entrainmentchecker_partcount == 0) {entrainment.setFreq(soundFile);}
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
        int index = allmeditatablestoplay.indexOf(this);
        Meditatable meditatableafter = null;
        if (index != allmeditatablestoplay.size() - 1) {meditatableafter = allmeditatablestoplay.get(index + 1);}
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled() && meditatableafter != null && ! meditatableafter.getNameForFiles().equals("qi")) {
            entrainment.setRampfile(entrainment.ramp_get(meditatableafter.number - 1));
            if (ramponly) {setDuration(Duration.millis(entrainment.getRampfile().getDuration()));}
            return super.creation_buildEntrainment() && entrainment.getRampfile().isValid();
        }
        return super.creation_buildEntrainment();
    }

// Playback
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

}
