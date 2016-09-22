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

// Entrainment Methods
//    public Task<Boolean> entrainment_populatetask() {
//        return new Task<Boolean>() {
//            @Override
//            protected Boolean call() throws Exception {
//
//                while (true) {
//                    System.out.println("Called The Thread");
//                    File expectedentrainmentfile;
//                    SoundFile actualsoundfile;
//                    if (entrainmentchecker_partcount[0] == 0) {
//                        actualsoundfile = sessionPart.getEntrainment().getFreq();
//                        expectedentrainmentfile = new File(Options.DIRECTORYENTRAINMENT, sessionPart.getNameForFiles().toUpperCase() + ".mp3");
//                    } else {
//                        try {
//                            actualsoundfile = sessionPart.getEntrainment().ramp_get(entrainmentchecker_partcount[0]);
//                            expectedentrainmentfile = new File(Options.DIRECTORYENTRAINMENT, "ramp/" + sessionPart.getNameForFiles() + "to" + sessionPart.entrainmentchecker_partcutnames.get(entrainmentchecker_partcount[0] - 1) + ".mp3");
//                        } catch (IndexOutOfBoundsException ignored) {
//                            sessionPart.getThisession().Root.getEntrainments().setsessionpartEntrainment(sessionPart.number, sessionPart.getEntrainment());
//                            return false;
//                        }
//                    }
//                    System.out.println("Testing " + actualsoundfile.getName());
//                    if (expectedentrainmentfile.exists()) {
//                        if (actualsoundfile == null || ! actualsoundfile.isValid()) {
//                            entrainmentplayer = new MediaPlayer(new Media(expectedentrainmentfile.toURI().toString()));
//                            entrainmentplayer.setOnReady(() -> {
//                                SoundFile soundFile = new SoundFile(expectedentrainmentfile);
//                                soundFile.setDuration(entrainmentplayer.getTotalDuration().toMillis());
//                                if ( entrainmentchecker_partcount[0] == 0) {sessionPart.getEntrainment().setFreq(soundFile);}
//                                else {sessionPart.getEntrainment().ramp_add(soundFile);}
//                                entrainmentplayer.dispose();
//                                entrainmentchecker_partcount[0]++;
//                                notify();
//                            });
//                            try {wait();}
//                            catch (InterruptedException e) {}
//                        } else {entrainmentchecker_partcount[0]++;}
//                    } else {
////                    entrainmentmissingfiles = true;
////                    entrainmentchecker_missingfiles.add(expectedentrainmentfile);
//                        entrainmentchecker_partcount[0]++;
//                    }
//                }
//                return null;
//            }
//        };
//    }


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
                thisession.Root.getEntrainments().setsessionpartEntrainment(number, entrainment);
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
    public void entraiment_populatewithffmpeg() {
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
                thisession.Root.getEntrainments().setsessionpartEntrainment(number, entrainment);
                return;
            }
        }
        if (expectedentrainmentfile.exists()) {
            if (actualsoundfile == null || ! actualsoundfile.isValid()) {
                SoundFile soundFile = new SoundFile(expectedentrainmentfile);
                soundFile.setDuration(Util.audio_getduration(expectedentrainmentfile));
                if (this.entrainmentchecker_partcount == 0) {entrainment.setFreq(soundFile);}
                else {entrainment.ramp_add(soundFile);}
                entrainmentchecker_calculateplayer.dispose();
                entrainmentchecker_partcount++;
                entrainment_populate();
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

}
