package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;

import java.io.File;


public class Element extends Meditatable {

    public Element(int number, String name, int duration, String briefsummary, This_Session thissession, ToggleButton aSwitch, TextField value) {
        super(number, name, duration, briefsummary, thissession, aSwitch, value);
        if (thissession.Root.getOptions().getProgramOptions().getTooltips()) {Value.setTooltip(new Tooltip("Minutes You Want To Practice " + name));}
        else {Value.setTooltip(null);}
    }

// Getters For Playback
    @Override
    public String getNameForFiles() {return "qi";}

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

// GUI
//    public boolean hasValidValue() {
//        if (Switch.isSelected()) {
//            Value.setText("0");
//            Value.setDisable(false);
//            Value.setTooltip(new Tooltip("Practice Time For " + name + " (In Minutes)"));
//        } else {
//            Value.setText("0");
//            Value.setDisable(true);
//            Value.setTooltip(new Tooltip(name + " Is Disabled. Click " + name + " Button Above To Enable"));
//        }
//    }

    @Override
    public boolean creation_buildEntrainment() {
        int index = allmeditatablestoplay.indexOf(this);
        Meditatable meditatableafter = null;
        if (index != allmeditatablestoplay.size() - 1) {meditatableafter = allmeditatablestoplay.get(index + 1);}
        // rin kyo toh sha kai jin retsu zai zen
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled() && meditatableafter != null && ! meditatableafter.getNameForFiles().equals("qi")) {
            entrainment.setRampfile(entrainment.ramp_get(meditatableafter.number - 1));
            return super.creation_buildEntrainment() && entrainment.getRampfile().isValid();
        }
        return super.creation_buildEntrainment();
    }

// Goals

// Export

}
