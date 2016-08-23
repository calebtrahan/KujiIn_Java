package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.xml.Entrainments;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;

import java.io.File;


public class Element extends Meditatable {

    public Element(int number, String name, int duration, String briefsummary, This_Session thissession, ToggleButton aSwitch, TextField value) {
        super(number, name, duration, briefsummary, thissession, aSwitch, value);
        if (thissession.Root.getOptions().getProgramOptions().getTooltips()) {Value.setTooltip(new Tooltip("Minutes You Want To Practice " + name));}
        else {Value.setTooltip(null);}
    }

// Entrainment
    @Override
    public void entrainment_populate() {
        // TODO Entrainment Variation Count Is Resetting To 0 With Each Recursive Call
        System.out.println("1 Variation Count Is " + this.entrainmentchecker_variationcount);
        StringBuilder filename = new StringBuilder();
        File actualfile = null;
        SoundFile actualsoundfile = null;
        if (this.entrainmentchecker_partcount == 0) {
            // Freq Short
            try {
                System.out.println("Should Be Pulling Freq Short");
                actualsoundfile = entrainment.getFreqshort();
                System.out.println("2 Variation Count Is: " + this.entrainmentchecker_variationcount);
                int variation = Entrainments.DURATIONSVARIATIONS.get(this.entrainmentchecker_variationcount);
                filename.append("ELEMENT").append(variation).append(".mp3");
                actualfile = new File(Options.DIRECTORYENTRAINMENT, "entrainment/" + filename.toString());
                System.out.println(actualfile.getAbsolutePath());
                if (actualsoundfile == null) {actualsoundfile = new SoundFile(actualfile);}
                entrainment_incremenetvariation();
                System.out.println("3 Variation Count Is: " + this.entrainmentchecker_variationcount);
            } catch (IndexOutOfBoundsException ignored) {
                this.entrainmentchecker_variationcount = 0;
                this.entrainmentchecker_partcount++;
                entrainment_populate();
            }
        } else if (this.entrainmentchecker_partcount == 1) {
            // Freq Long
            try {
                System.out.println("Should Be Pulling Freq Long");
                Entrainments.DURATIONSVARIATIONS.get(this.entrainmentchecker_variationcount);
                actualsoundfile = entrainment.getFreqlong();
                filename.append("ELEMENT").append(Entrainments.DURATIONSVARIATIONS.get(this.entrainmentchecker_variationcount)).append(".mp3");
                actualfile = new File(Options.DIRECTORYENTRAINMENT, "entrainment/" + filename.toString());
                if (actualsoundfile == null) {actualsoundfile = new SoundFile(actualfile);}
                this.entrainmentchecker_variationcount++;
            } catch (Exception ignored) {
                ignored.printStackTrace();
                System.out.println("Out Of Bounds On Freq Long");
                this.entrainmentchecker_variationcount = 0;
                entrainmentchecker_partcount++;
                entrainment_populate();
            }
        } else {
            System.out.println("Should Be In Ramp");
                try {
                    String ramptype = entrainmentchecker_ramptypes.get(this.entrainmentchecker_ramptypecount);
                    System.out.println("Ramp Type Is " + ramptype);
                    try {
                        System.out.println("Selected Variation: " + entrainmentchecker_rampvariations.get(this.entrainmentchecker_rampvariationcount));
                        filename.append("element").append(ramptype).append(entrainmentchecker_rampvariations.get(this.entrainmentchecker_rampvariationcount)).append(".mp3");
                        actualfile = new File(Options.DIRECTORYENTRAINMENT, "entrainment/" + filename.toString());
                        System.out.println("Ramp File: " + actualfile.getAbsolutePath());
                        if (ramptype.equals("in")) {actualsoundfile = entrainment.ramp_getin(this.entrainmentchecker_rampvariationcount);}
                        else {actualsoundfile = entrainment.ramp_getout(this.entrainmentchecker_rampvariationcount);}
                        if (actualsoundfile == null) {actualsoundfile = new SoundFile(actualfile);}
                        this.entrainmentchecker_rampvariationcount++;
                        System.out.println(entrainmentchecker_rampvariationcount);
                    } catch (IndexOutOfBoundsException ignored) {
                        System.out.println("Index Out Of Bounds 1");
                        this.entrainmentchecker_ramptypecount++;
                        this.entrainmentchecker_rampvariationcount = 0;
                        entrainment_populate();
                    }
                } catch (IndexOutOfBoundsException ignored) {
                    System.out.println("Index Out Of Bounds 2");
                    this.entrainmentchecker_partcount++;
                    this.entrainmentchecker_rampvariationcount = 0;
                    entrainment_populate();
                }
        }
        if (actualfile.exists()) {
            if (actualsoundfile == null || actualsoundfile.getDuration() == null || actualsoundfile.getDuration() == 0.0) {
                entrainmentchecker_calculateplayer = new MediaPlayer(new Media(actualfile.toURI().toString()));
                File finalActualfile = actualfile;
                entrainmentchecker_calculateplayer.setOnReady(() -> {
                    SoundFile soundFile = new SoundFile(finalActualfile);
                    soundFile.setDuration(entrainmentchecker_calculateplayer.getTotalDuration().toMillis());
                    if (this.entrainmentchecker_partcount == 0) {entrainment.setFreqshort(soundFile);}
                    else if (this.entrainmentchecker_partcount == 1) {entrainment.setFreqlong(soundFile);}
                    else {
                        if (this.entrainmentchecker_ramptypecount == 0) {entrainment.ramp_addin(soundFile);}
                        else {entrainment.ramp_addout(soundFile);}
                    }
                    entrainmentchecker_calculateplayer.dispose();
                    if (this.entrainmentchecker_partcount <= 1) {entrainmentchecker_variationcount++;}
                    else {entrainmentchecker_rampvariationcount++;}
                    entrainment_populate();
                });
            } else {
                if (this.entrainmentchecker_partcount <= 1) {entrainmentchecker_variationcount++;}
                else {entrainmentchecker_rampvariationcount++;}
                entrainment_populate();
            }
        } else {
            entrainmentmissingfiles = true;
            entrainmentchecker_missingfiles.add(actualfile);
            if (this.entrainmentchecker_partcount <= 1) {entrainmentchecker_variationcount++;}
            else {entrainmentchecker_rampvariationcount++;}
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
        Meditatable meditatablebefore = null;
        Meditatable meditatableafter = null;
        if (index != 0) {meditatablebefore = allmeditatablestoplay.get(index - 1);}
        if (index != allmeditatablestoplay.size() - 1) {meditatableafter = allmeditatablestoplay.get(index + 1);}
        Duration timeleft = getduration();
        Duration freqlongduration = new Duration(entrainment.getFreqlong().getDuration());
        Duration freqshortduration = new Duration(entrainment.getFreqshort().getDuration());
        SoundFile rampinfile = null;
        SoundFile rampoutfile = null;
        if (meditatablebefore != null || meditatableafter != null) {
            if (meditatablebefore != null) {
                rampinfile = entrainment.ramp_getin(meditatablebefore.number - 1);
                timeleft = timeleft.subtract(Duration.seconds(rampinfile.getDuration()));
                rampenabled = true;
            }
            if (meditatableafter != null) {
                rampoutfile = entrainment.ramp_getout(meditatableafter.number - 1);
                timeleft = timeleft.subtract(Duration.seconds(rampoutfile.getDuration()));
                rampenabled = true;
            }
        }
        while (timeleft.greaterThan(freqlongduration)) {
            entrainment.created_add(entrainment.getFreqlong());
            timeleft = timeleft.subtract(freqlongduration);
        }
        while (timeleft.greaterThan(Duration.ZERO)) {
            entrainment.created_add(entrainment.getFreqshort());
            timeleft = timeleft.subtract(freqshortduration);
        }
        entrainment.shuffleCreated();
        if (rampinfile != null) {entrainment.created_add(0, rampinfile);}
        if (rampoutfile != null) {entrainment.created_add(rampoutfile);}
        return entrainment.created_getAll().size() > 0 && entrainment.gettotalCreatedDuration().greaterThanOrEqualTo(getduration());
    }

// Goals

// Export

}
