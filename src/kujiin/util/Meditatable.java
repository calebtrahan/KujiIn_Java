package kujiin.util;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.concurrent.Service;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.xml.*;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Meditatable {
// GUI Fields
    protected ToggleButton Switch;
    protected TextField Value;
// Data Fields
    public int number;
    public String name;
    protected Duration duration;
    protected This_Session thisession;
    protected Ambience ambience;
    protected Entrainment entrainment;
// Playback Fields
    protected boolean ambienceenabled;
    protected int entrainmentplaycount;
    protected int ambienceplaycount;
    protected boolean rampenabled;
    protected MediaPlayer entrainmentplayer;
    protected MediaPlayer ambienceplayer;
    protected Animation fade_entrainment_play;
    protected Animation fade_entrainment_resume;
    protected Animation fade_entrainment_pause;
    protected Animation fade_entrainment_stop;
    protected Animation fade_ambience_play;
    protected Animation fade_ambience_resume;
    protected Animation fade_ambience_pause;
    protected Animation fade_ambience_stop;
    protected Animation timeline_fadeout_timer;
    protected Animation timeline_progresstonextmeditatable;
    protected Animation timeline_start_ramp;
    private Double currententrainmentvolume;
    private Double currentambiencevolume;
    public Duration elapsedtime;
    protected List<Meditatable> allmeditatablestoplay;
    protected List<kujiin.xml.Goals.Goal> goalscompletedthissession;
// Goal Fields
    protected Goals GoalsController;
    protected List<kujiin.xml.Goals.Goal> Goals;
// Entrainment Fields
    protected boolean entrainmentready = false;
    protected boolean entrainmentmissingfiles = false;
    protected int entrainmentchecker_partcount;
    protected int entrainmentchecker_variationcount;
    protected int entrainmentchecker_ramptypecount;
    protected int entrainmentchecker_rampvariationcount;
    protected final ArrayList<String> entrainmentchecker_ramptypes = new ArrayList<>(Arrays.asList("in", "out"));
    protected final ArrayList<String> entrainmentchecker_rampvariations = new ArrayList<>(Arrays.asList("rin", "kyo", "toh", "sha", "kai", "jin", "retsu", "zai", "zen"));
    public MediaPlayer entrainmentchecker_calculateplayer;
    public List<File> entrainmentchecker_missingfiles = new ArrayList<>();
// Ambience Fields
    private ArrayList<File> ambiencechecker_soundfilelist = new ArrayList<>();
    private int ambiencechecker_soundfilescount = 0;
    private MediaPlayer ambiencechecker_calculateplayer;
    private boolean ambienceready = false;

    public Meditatable() {}
    public Meditatable(int number, String name, int duration, String briefsummary, This_Session thissession, ToggleButton aSwitch, TextField value) {
        this.number = number;
        this.name = name;
        this.duration = new Duration((duration * 60) * 1000);
        this.thisession = thissession;
        if (aSwitch != null && value != null) {
            Switch = aSwitch;
            Value = value;
            Util.custom_textfield_integer(Value, Switch, 0, 600, 1);
            Value.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    setDuration(Integer.parseInt(Value.getText()));
                    thissession.Root.creation_gui_update();
                } catch (NumberFormatException ignored) {setDuration(0);}
            });
            if (briefsummary != null) {Switch.setTooltip(new Tooltip(briefsummary));}
            Switch.setOnAction(event -> gui_toggleswitch());
            gui_toggleswitch();
        }
        entrainment = thissession.Root.getEntrainments().getmeditatableEntrainment(number);
        entrainmentchecker_partcount = 0;
        entrainmentchecker_variationcount = 0;
        entrainmentchecker_ramptypecount = 0;
        entrainmentchecker_rampvariationcount = 0;
        entrainment_populate();
        ambience = thissession.Root.getAmbiences().getmeditatableAmbience(number);
        ambience_populate();
    //        tempentrainmenttextfile = new File(Options.DIRECTORYTEMP, "txt/" + name + "Ent.txt");
//        tempentrainmentfile = new File(Options.DIRECTORYTEMP, "Entrainment/" + name + "Temp.mp3");
//        finalentrainmentfile = new File(Options.DIRECTORYTEMP, "Entrainment/" + name + ".mp3");
//        tempambiencetextfile = new File(Options.DIRECTORYTEMP, "txt/" + name + "Amb.txt");
//        tempambiencefile = new File(Options.DIRECTORYTEMP, "Ambience/" + name + "Temp.mp3");
//        finalambiencefile = new File(Options.DIRECTORYTEMP, "Ambience/" + name + ".mp3");
//        setFinalexportfile(new File(Options.DIRECTORYTEMP, name + ".mp3"));
    }

// Entrainment Methods
    public void entrainment_populate() {
//        try {
//            int variation = Entrainments.DURATIONSVARIATIONS.get(entrainmentchecker_variationcount);
//            StringBuilder filename = new StringBuilder();
//            if (number == 0 || number == 15) {filename.append("Qi-Gong");}
//            else if (number > 9 && number < 15) {filename.append("ELEMENT");}
//            else {filename.append(name);}
//            filename.append(variation).append(".mp3");
//            File actualfile = new File(Options.DIRECTORYENTRAINMENT, "entrainment/" + filename.toString());
//            SoundFile actualsoundfile;
////            switch (entrainmentchecker_variationcount) {
////                case 0:
////                    actualsoundfile = entrainment.getFreqshort();
////                    break;
////                case 1:
////                    actualsoundfile = entrainment.getFreqlong();
////                    break;
////                case 2:
////                    actualsoundfile = entrainment.getRampinfile();
////                    break;
////                case 3:
////                    actualsoundfile = entrainment.getRampoutfile();
////                    break;
////                default:
////                    actualsoundfile = null;
////                    break;
////            }
//            if (entrainmentchecker_variationcount == 0) {actualsoundfile = entrainment.getFreqshort();}
//            else {actualsoundfile = entrainment.getFreqlong();}
//            if (actualfile.exists()) {
//                if (actualsoundfile == null || actualsoundfile.getDuration() == null || actualsoundfile.getDuration() == 0.0) {
//                    entrainmentchecker_calculateplayer = new MediaPlayer(new Media(actualfile.toURI().toString()));
//                    entrainmentchecker_calculateplayer.setOnReady(() -> {
//                        SoundFile soundFile = new SoundFile(actualfile);
//                        soundFile.setDuration(entrainmentchecker_calculateplayer.getTotalDuration().toMillis());
//                        if (entrainmentchecker_variationcount == 0) {entrainment.setFreqshort(soundFile);
//                        } else if (entrainmentchecker_variationcount == 1) {entrainment.setFreqlong(soundFile);}
//                        entrainmentchecker_calculateplayer.dispose();
//                        entrainmentchecker_variationcount++;
//                        entrainment_populate();
//                    });
//                } else {entrainment_populate();}
//            } else {entrainmentmissingfiles = true; entrainmentchecker_missingfiles.add(actualfile);}
//        } catch (IndexOutOfBoundsException ignored) {
//            entrainmentready = true;
//            thisession.Root.getEntrainments().setmeditatableEntrainment(number, entrainment);
//            thisession.Root.getEntrainments().marshall();
//        }
    }
    public boolean entrainment_isReady() {return entrainmentready;}
    public boolean entrainment_missingfiles() {
        return entrainmentmissingfiles;
    }
    public List<File> entrainment_getMissingFiles() {return entrainmentchecker_missingfiles;}
    public Duration ambience_getTotalActualDuration() {return ambience.gettotalActualDuration();}
    public void entrainment_incrementpart() {entrainmentchecker_partcount++;}
    public void entrainment_incremenetvariation() {entrainmentchecker_variationcount++;}
    public void entrainment_incremementramptype() {entrainmentchecker_ramptypecount++;}
    public void entrainment_incremementrampvariation() {entrainmentchecker_rampvariationcount++;}
    public int getEntrainmentchecker_partcount() {
        return entrainmentchecker_partcount;
    }
    public int getEntrainmentchecker_variationcount() {
        return entrainmentchecker_variationcount;
    }
    public int getEntrainmentchecker_ramptypecount() {
        return entrainmentchecker_ramptypecount;
    }
    public int getEntrainmentchecker_rampvariationcount() {
        return entrainmentchecker_rampvariationcount;
    }

    // Ambience Methods
    public void ambience_populate() {
        if (ambiencechecker_soundfilelist.isEmpty()) {
            File ambiencedirectory = new File(Options.DIRECTORYAMBIENCE, name);
            ambiencechecker_soundfilescount = 0;
            try {
                for (File i : ambiencedirectory.listFiles()) {
                    if (Util.audio_isValid(i)) {
                        if (! ambience.getAmbienceFiles().contains(i)) {ambiencechecker_soundfilelist.add(i);}
                        else {
                            try {
                                Double duration = ambience.getAmbience().get(ambience.getAmbienceFiles().indexOf(i)).getDuration();
                                if (duration == null || duration == 0.0) {ambiencechecker_soundfilelist.add(i);}
                            } catch (ArrayIndexOutOfBoundsException ignored) {ambiencechecker_soundfilelist.add(i);}
                        }
                    }
                }
                if (! ambiencechecker_soundfilelist.isEmpty()) {ambience_populate();}
                else {ambienceready = true;}
            } catch (NullPointerException ignored) {
                // TODO Change This To Reflect No Ambience Files In Directory
                ambienceready= true;
            }
        } else {
            try {
                File actualfile = ambiencechecker_soundfilelist.get(ambiencechecker_soundfilescount);
                ambiencechecker_calculateplayer = new MediaPlayer(new Media(actualfile.toURI().toString()));
                ambiencechecker_calculateplayer.setOnReady(() -> {
//                    thisession.CreatorStatusBar.setText("Scanning Directories For Ambience" + meditatablename + " (" + soundfilescount + 1 + "/" + soundfilestocalculateduration.size() + ")");
                    SoundFile soundFile = new SoundFile(actualfile);
                    soundFile.setDuration(ambiencechecker_calculateplayer.getTotalDuration().toMillis());
                    ambience.actual_add(soundFile);
                    ambiencechecker_soundfilescount++;
                    ambiencechecker_calculateplayer.dispose();
                    ambience_populate();
                });
                ambiencechecker_calculateplayer.setOnError(() -> {
                    ambiencechecker_soundfilescount++;
                    ambiencechecker_calculateplayer.dispose();
                    ambience_populate();
                });
            } catch (IndexOutOfBoundsException ignored) {
                System.out.println(Boolean.toString(ambience == null));
                thisession.Root.getAmbiences().setmeditatableAmbience(number, ambience);
//                System.out.println(name + "'s Ambience:");
//                for (SoundFile i : thisession.Root.getAmbiences().getmeditatableAmbience(number).getAmbience()) {
//                    System.out.println(i.getName() + ": " + i.getDuration());
                thisession.Root.getAmbiences().marshall();
                ambienceready = true;
            }
        }
    }
    public boolean ambience_isReady() {return ambienceready;}

// GUI Methods
    private void gui_toggleswitch() {
    if (Switch.isSelected()) {
        Value.setText("0");
        Value.setDisable(false);
        Value.setTooltip(new Tooltip("Practice Time For " + name + " (In Minutes)"));
    } else {
        Value.setText("0");
        Value.setDisable(true);
        Value.setTooltip(new Tooltip(name + " Is Disabled. Click " + name + " Button Above To Enable"));
    }
}
    public void changevalue(int newvalue) {
        Switch.setSelected(newvalue != 0);
        Value.setDisable(newvalue == 0);



        if (newvalue == 0) {Switch.setSelected(false);}
        else {
            Switch.setSelected(true);
            Value.setDisable(false);
            Value.setText(Integer.toString(newvalue));
            setDuration(newvalue);
        }
    }
    public boolean hasValidValue() {
        return Switch.isSelected() && Integer.parseInt(Value.getText()) != 0;
    }
    public void gui_setDisable(boolean disabled) {
        Switch.setDisable(disabled);
        Value.setDisable(disabled);
    }
    public int gui_getvalue() {return Integer.parseInt(Value.getText());}
// Getters And Setters
    public String getNameForChart() {return name;}
    private void setDuration(double newduration) {
        duration = new Duration((newduration * 60) * 1000);
    }
    public Ambience getAmbience() {
        return ambience;
    }
    public Entrainment getEntrainment() {return entrainment;}
    public void setEntrainment(Entrainment entrainment) {this.entrainment = entrainment;}
    public void setGoalsController(Goals goals) {
        GoalsController = goals;
    }
// Duration
    public Duration getduration() {return duration;}
    public Duration getelapsedtime() {return elapsedtime;}

// Creation
    public boolean creation_build(List<Meditatable> allmeditatables, boolean ambienceenabled) {
        this.ambienceenabled = ambienceenabled;
        allmeditatablestoplay = allmeditatables;
        if (ambienceenabled) {return creation_buildEntrainment() && creation_buildAmbience();}
        else {return creation_buildEntrainment();}
    }
    protected boolean creation_buildEntrainment() {
        entrainment.created_clear();
        return true;
    }
    protected boolean creation_buildAmbience() {
        ambience.created_clear();
        Duration currentambienceduration = Duration.ZERO;
        if (ambience.hasEnoughAmbience(getduration())) {
            for (SoundFile i : ambience.getAmbience()) {
                if (ambience.gettotalCreatedDuration().lessThan(getduration())) {
                    ambience.created_add(i);
                    currentambienceduration = currentambienceduration.add(new Duration(i.getDuration()));
                } else {break;}
            }
        } else {
            // TODO Creates Some Duplicate Files
            SoundFile selectedsoundfile;
            int ambiencecount = 0;
            int sizetotest;
            boolean includefile;
            ambience.created_initialize();
            while (currentambienceduration.lessThan(getduration())) {
                try {selectedsoundfile = ambience.getAmbience().get(ambiencecount);}
                catch (IndexOutOfBoundsException ignored) {ambiencecount = 0; selectedsoundfile = ambience.getAmbience().get(ambiencecount);}
                ambience.created_add(selectedsoundfile);
                currentambienceduration = currentambienceduration.add(new Duration(selectedsoundfile.getDuration()));
                ambiencecount++;
                    currentambienceduration = currentambienceduration.add(new Duration(selectedsoundfile.getDuration()));
                if (ambience.getAmbience().size() == 1) {ambience.created_add(ambience.getAmbience().get(0)); currentambienceduration = currentambienceduration.add(new Duration(ambience.getAmbience().get(0).getDuration()));}
                try {selectedsoundfile = ambience.getAmbience().get(ambiencecount);}
                catch (IndexOutOfBoundsException ignored) {ambiencecount = 0; selectedsoundfile = ambience.getAmbience().get(ambiencecount);}
                includefile = true;
                if (ambience.getCreatedAmbience().size() >= ambience.getAmbience().size()) {sizetotest = (ambience.getAmbience().size() - 1) - ambience.getCreatedAmbience().size() % ambience.getAmbience().size();}
                else {sizetotest = ambience.getCreatedAmbience().size() - 1;}
                for (int i = sizetotest; i > 0; i--) {if (ambience.getCreatedAmbience().get(i).equals(selectedsoundfile)) {includefile = false; break;}}
//                if (includefile && ambience.getCreatedAmbience().size() > 0 && ambience.getCreatedAmbience().get(ambience.getCreatedAmbience().size() - 1).equals(selectedsoundfile)) {includefile = false;}
                if (includefile) {
                    ambience.created_add(selectedsoundfile);
                    currentambienceduration = currentambienceduration.add(new Duration(selectedsoundfile.getDuration()));
                }
                ambiencecount++;
            }
        }
//            else {
//                Random randint = new Random();
//                while (currentambienceduration.lessThan(getduration())) {
//                    List<SoundFile> createdambience = ambience.getCreatedAmbience();
//                    SoundFile selectedsoundfile = ambience.actual_get(randint.nextInt(ambience.getAmbience().size() - 1));
//                    if (createdambience.size() < 2) {
//                        ambience.created_add(selectedsoundfile);
//                        currentambienceduration = currentambienceduration.add(new Duration(selectedsoundfile.getDuration()));
//                    } else if (createdambience.size() == 2) {
//                        if (!selectedsoundfile.equals(createdambience.get(createdambience.size() - 1))) {
//                            ambience.created_add(selectedsoundfile);
//                            currentambienceduration = currentambienceduration.add(new Duration(selectedsoundfile.getDuration()));
//                        }
//                    } else if (createdambience.size() == 3) {
//                        if (!selectedsoundfile.equals(createdambience.get(createdambience.size() - 1)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 2))) {
//                            ambience.created_add(selectedsoundfile);
//                            currentambienceduration = currentambienceduration.add(new Duration(selectedsoundfile.getDuration()));
//                        }
//                    } else if (createdambience.size() <= 5) {
//                        if (!selectedsoundfile.equals(createdambience.get(createdambience.size() - 1)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 2)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 3))) {
//                            ambience.created_add(selectedsoundfile);
//                            currentambienceduration = currentambienceduration.add(new Duration(selectedsoundfile.getDuration()));
//                        }
//                    } else if (createdambience.size() > 5) {
//                        if (!selectedsoundfile.equals(createdambience.get(createdambience.size() - 1)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 2)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 3)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 4))) {
//                            ambience.created_add(selectedsoundfile);
//                            currentambienceduration = currentambienceduration.add(new Duration(selectedsoundfile.getDuration()));
//                        }
//                    }
//                }
//            }
//        int count = 1;
//        for (SoundFile x : ambience.getCreatedAmbience()) {System.out.println("Ambience " + count + ": " + x.getName()); count++;}
//        System.out.println("Total Ambience Duration: " + currentambienceduration.toMinutes() + " Minutes");
        return ambience.getCreatedAmbience().size() > 0 && currentambienceduration.greaterThanOrEqualTo(getduration());
    }
    public void creation_reset(boolean setvaluetozero) {
        entrainment.created_clear();
        ambience.created_clear();
        if (setvaluetozero) {
            Switch.setSelected(false);
            gui_toggleswitch();
        }
    }

// Playback
    public void setupfadeanimations() {
    // PLAY
        if (thisession.Root.getOptions().getSessionOptions().getFadeinduration() > 0.0) {
            fade_entrainment_play = new Transition() {
                {setCycleDuration(new Duration(thisession.Root.getOptions().getSessionOptions().getFadeinduration() * 1000));}

                @Override
                protected void interpolate(double frac) {
                    double entrainmentvolume = frac * currententrainmentvolume;
                    String percentage = new Double(entrainmentvolume * 100).intValue() + "%";
                    entrainmentplayer.setVolume(entrainmentvolume);
                    thisession.playerUI.EntrainmentVolume.setValue(entrainmentvolume);
                    thisession.playerUI.EntrainmentVolumePercentage.setText(percentage);
                    if (thisession.player_isreferencecurrentlyDisplayed()) {
                        thisession.displayReference.EntrainmentVolumeSlider.setValue(entrainmentvolume);
                        thisession.displayReference.EntrainmentVolumePercentage.setText(percentage);
                    }
                }
            };
            fade_entrainment_play.setOnFinished(event -> {thisession.playerState = This_Session.PlayerState.PLAYING; toggleplayerbuttons(); volume_bindentrainment();});
            if (ambienceenabled) {
                fade_ambience_play = new Transition() {
                    {setCycleDuration(new Duration(thisession.Root.getOptions().getSessionOptions().getFadeinduration() * 1000));}

                    @Override
                    protected void interpolate(double frac) {
                        double ambiencevolume = frac * currentambiencevolume;
                        String percentage = new Double(ambiencevolume * 100).intValue() + "%";
                        ambienceplayer.setVolume(ambiencevolume);
                        thisession.playerUI.AmbienceVolume.setValue(ambiencevolume);
                        thisession.playerUI.AmbienceVolumePercentage.setText(percentage);
                        if (thisession.player_isreferencecurrentlyDisplayed()) {
                            thisession.displayReference.AmbienceVolumeSlider.setValue(ambiencevolume);
                            thisession.displayReference.AmbienceVolumePercentage.setText(percentage);
                        }
                    }
                };
                fade_ambience_play.setOnFinished(event -> volume_bindambience());
            }
        }
    // RESUME
        fade_entrainment_resume = new Transition() {
                {setCycleDuration(new Duration(Options.DEFAULT_FADERESUMEANDPAUSEDURATION * 1000));}

                @Override
                protected void interpolate(double frac) {
                    double entrainmentvolume = frac * currententrainmentvolume;
                    String percentage = new Double(entrainmentvolume * 100).intValue() + "%";
                    entrainmentplayer.setVolume(entrainmentvolume);
                    thisession.playerUI.EntrainmentVolume.setValue(entrainmentvolume);
                    thisession.playerUI.EntrainmentVolumePercentage.setText(percentage);
                    if (thisession.player_isreferencecurrentlyDisplayed()) {
                        thisession.displayReference.EntrainmentVolumeSlider.setValue(entrainmentvolume);
                        thisession.displayReference.EntrainmentVolumePercentage.setText(percentage);
                    }
                }
            };
        fade_entrainment_resume.setOnFinished(event -> {thisession.playerState = This_Session.PlayerState.PLAYING; timeline_progresstonextmeditatable.play(); if (rampenabled && timeline_start_ramp.getStatus() == Animation.Status.PAUSED) {timeline_start_ramp.play();}
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.play();} toggleplayerbuttons(); volume_bindentrainment();});
        if (ambienceenabled) {
            fade_ambience_resume = new Transition() {
                {setCycleDuration(new Duration(Options.DEFAULT_FADERESUMEANDPAUSEDURATION * 1000));}

                @Override
                protected void interpolate(double frac) {
                    double ambiencevolume = frac * currentambiencevolume;
                    String percentage = new Double(ambiencevolume * 100).intValue() + "%";
                    ambienceplayer.setVolume(ambiencevolume);
                    thisession.playerUI.AmbienceVolume.setValue(ambiencevolume);
                    thisession.playerUI.AmbienceVolumePercentage.setText(percentage);
                    if (thisession.player_isreferencecurrentlyDisplayed()) {
                        thisession.displayReference.AmbienceVolumeSlider.setValue(ambiencevolume);
                        thisession.displayReference.AmbienceVolumePercentage.setText(percentage);
                    }
                }
            };
            fade_ambience_resume.setOnFinished(event -> volume_bindambience());
        }
    // PAUSE
        fade_entrainment_pause = new Transition() {
                {setCycleDuration(new Duration(Options.DEFAULT_FADERESUMEANDPAUSEDURATION * 1000));}

                @Override
                protected void interpolate(double frac) {
                    double fadeoutvolume = currententrainmentvolume - (frac * currententrainmentvolume);
                    String percentage = new Double(fadeoutvolume * 100).intValue() + "%";
                    entrainmentplayer.setVolume(fadeoutvolume);
                    thisession.playerUI.EntrainmentVolume.setValue(fadeoutvolume);
                    thisession.playerUI.EntrainmentVolumePercentage.setText(percentage);
                    if (thisession.player_isreferencecurrentlyDisplayed()) {
                        thisession.displayReference.EntrainmentVolumeSlider.setValue(fadeoutvolume);
                        thisession.displayReference.EntrainmentVolumePercentage.setText(percentage);
                    }
                }
        };
        fade_entrainment_pause.setOnFinished(event -> {entrainmentplayer.pause(); timeline_progresstonextmeditatable.pause();
            if (rampenabled && timeline_start_ramp.getStatus() == Animation.Status.RUNNING) {timeline_start_ramp.pause();}
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.pause();} thisession.playerState = This_Session.PlayerState.PAUSED; toggleplayerbuttons();});
        if (ambienceenabled) {
            fade_ambience_pause = new Transition() {
                {setCycleDuration(new Duration(Options.DEFAULT_FADERESUMEANDPAUSEDURATION * 1000));}

                @Override
                protected void interpolate(double frac) {
                    double fadeoutvolume = currentambiencevolume - (frac * currentambiencevolume);
                    String percentage = new Double(fadeoutvolume * 100).intValue() + "%";
                    ambienceplayer.setVolume(fadeoutvolume);
                    thisession.playerUI.AmbienceVolume.setValue(fadeoutvolume);
                    thisession.playerUI.AmbienceVolumePercentage.setText(percentage);
                    if (thisession.player_isreferencecurrentlyDisplayed()) {
                        thisession.displayReference.AmbienceVolumeSlider.setValue(fadeoutvolume);
                        thisession.displayReference.AmbienceVolumePercentage.setText(percentage);
                    }
                }
            };
            fade_ambience_pause.setOnFinished(event -> ambienceplayer.pause());
        }
    // STOP
        if (thisession.Root.getOptions().getSessionOptions().getFadeoutduration() > 0.0) {
            fade_entrainment_stop = new Transition() {
                {setCycleDuration(new Duration(thisession.Root.getOptions().getSessionOptions().getFadeoutduration() * 1000));}

                @Override
                protected void interpolate(double frac) {
                    double fadeoutvolume = currententrainmentvolume - (frac * currententrainmentvolume);
                    String percentage = new Double(fadeoutvolume * 100).intValue() + "%";
                    entrainmentplayer.setVolume(fadeoutvolume);
                    thisession.playerUI.EntrainmentVolume.setValue(fadeoutvolume);
                    thisession.playerUI.EntrainmentVolumePercentage.setText(percentage);
                    if (thisession.player_isreferencecurrentlyDisplayed()) {
                        thisession.displayReference.EntrainmentVolumeSlider.setValue(fadeoutvolume);
                        thisession.displayReference.EntrainmentVolumePercentage.setText(percentage);
                    }
                }
            };
            fade_entrainment_stop.setOnFinished(event -> {entrainmentplayer.stop(); entrainmentplayer.dispose();
                if (rampenabled && timeline_start_ramp.getStatus() == Animation.Status.RUNNING) {timeline_start_ramp.stop();}
                timeline_progresstonextmeditatable.stop(); if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop();} thisession.playerState = This_Session.PlayerState.STOPPED; toggleplayerbuttons();});
            if (ambienceenabled) {
                fade_ambience_stop = new Transition() {
                    {setCycleDuration(new Duration(thisession.Root.getOptions().getSessionOptions().getFadeoutduration() * 1000));}

                    @Override
                    protected void interpolate(double frac) {
                        double fadeoutvolume = currentambiencevolume - (frac * currentambiencevolume);
                        String percentage = new Double(fadeoutvolume * 100).intValue() + "%";
                        ambienceplayer.setVolume(fadeoutvolume);
                        thisession.playerUI.AmbienceVolume.setValue(fadeoutvolume);
                        thisession.playerUI.AmbienceVolumePercentage.setText(percentage);
                        if (thisession.player_isreferencecurrentlyDisplayed()) {
                            thisession.displayReference.AmbienceVolumeSlider.setValue(fadeoutvolume);
                            thisession.displayReference.AmbienceVolumePercentage.setText(percentage);
                        }
                    }
                };
                fade_ambience_stop.setOnFinished(event -> {ambienceplayer.stop(); ambienceplayer.dispose();});
            }
        }
    }
    public void start() {
        elapsedtime = Duration.ZERO;
        entrainmentplaycount = 0;
        ambienceplaycount = 0;
        setupfadeanimations();
        volume_unbindentrainment();
        entrainmentplayer = new MediaPlayer(new Media(entrainment.created_get(entrainmentplaycount).getFile().toURI().toString()));
        entrainmentplayer.setVolume(0.0);
        entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
        entrainmentplayer.setOnError(this::entrainmenterror);
        entrainmentplayer.play();
        timeline_progresstonextmeditatable = new Timeline(new KeyFrame(getduration(), ae -> thisession.player_progresstonextmeditatable()));
        timeline_progresstonextmeditatable.play();
        currententrainmentvolume = thisession.getCurrententrainmentvolume();
        rampenabled = false;
        if (rampenabled) {
            System.out.println("Setting Up Ramp Timeline");
            timeline_start_ramp = new Timeline(new KeyFrame(getduration().subtract(new Duration(entrainment.getRampoutfile().getDuration() * 1000)), ae -> {
                System.out.println("Ramp TimeLine Started At " + getelapsedtime().toSeconds() + " Seconds");
                volume_unbindentrainment();
                entrainmentplayer.stop();
                entrainmentplayer.dispose();
                entrainmentplayer = new MediaPlayer(new Media(entrainment.getRampoutfile().getFile().toURI().toString()));
                entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
                entrainmentplayer.setOnError(this::entrainmenterror);
                entrainmentplayer.setVolume(currententrainmentvolume);
                entrainmentplayer.play();
                entrainmentplayer.setOnPlaying(this::volume_bindentrainment);
            }));
            timeline_start_ramp.play();
        }
        if (fade_entrainment_stop != null) {
            timeline_fadeout_timer = new Timeline(new KeyFrame(getduration().subtract(Duration.seconds(thisession.Root.getOptions().getSessionOptions().getFadeoutduration())), ae -> {
                volume_unbindentrainment();
                fade_entrainment_stop.play();
                if (fade_ambience_stop != null) {
                    volume_unbindambience();
                    fade_ambience_stop.play();
                }
            }));
            timeline_fadeout_timer.play();
        }
        thisession.player_displayreferencefile();
        if (fade_entrainment_play != null) {
            if (fade_entrainment_play.getStatus() == Animation.Status.RUNNING) {return;}
            thisession.playerState = This_Session.PlayerState.FADING_PLAY;
            fade_entrainment_play.play();
        } else {
            entrainmentplayer.setVolume(currententrainmentvolume);
            thisession.playerState = This_Session.PlayerState.PLAYING;
            volume_bindentrainment();}
        if (ambienceenabled) {
            currentambiencevolume = thisession.getCurrentambiencevolume();
            volume_unbindambience();
            ambienceplayer = new MediaPlayer(new Media(ambience.created_get(ambienceplaycount).getFile().toURI().toString()));
            ambienceplayer.setVolume(0.0);
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            ambienceplayer.play();
            if (fade_ambience_play != null) {fade_ambience_play.play();}
            else {ambienceplayer.setVolume(currentambiencevolume); volume_bindambience();}
        }
        toggleplayerbuttons();
        thisession.Root.sessionandgoals_forceselectmeditatable(number);
        goalscompletedthissession = new ArrayList<>();
    }
    public void resume() {
        volume_unbindentrainment();
        entrainmentplayer.play();
        if (fade_entrainment_resume != null) {
            entrainmentplayer.setVolume(0.0);
            if (fade_entrainment_resume.getStatus() == Animation.Status.RUNNING) {return;}
            thisession.playerState = This_Session.PlayerState.FADING_RESUME;
            fade_entrainment_resume.play();
        } else {
            entrainmentplayer.setVolume(currententrainmentvolume);
            volume_bindentrainment();
            thisession.playerState = This_Session.PlayerState.PLAYING;
            timeline_progresstonextmeditatable.play();
            if (rampenabled && timeline_start_ramp.getStatus() == Animation.Status.PAUSED) {timeline_start_ramp.play();}
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.play();}
        }
        if (ambienceenabled) {
            volume_unbindambience();
            ambienceplayer.play();
            if (fade_ambience_resume != null) {
                ambienceplayer.setVolume(0.0);
                if (fade_ambience_resume.getStatus() == Animation.Status.RUNNING) {return;}
                fade_ambience_resume.play();
            } else {
                ambienceplayer.setVolume(currentambiencevolume);
                volume_bindambience();
            }
        }
        toggleplayerbuttons();
    }
    public void pause() {
        volume_unbindentrainment();
        if (fade_entrainment_pause != null) {
            if (fade_ambience_pause.getStatus() == Animation.Status.RUNNING) {return;}
            // Open Loading Dialog
            thisession.playerState = This_Session.PlayerState.FADING_PAUSE;
            fade_entrainment_pause.play();
            if (ambienceenabled) {
                volume_unbindambience();
                fade_ambience_pause.play();
            }
            // Close Loading Dialog
        } else {
            thisession.playerState = This_Session.PlayerState.PAUSED;
            entrainmentplayer.pause();
            timeline_progresstonextmeditatable.pause();
            if (rampenabled && timeline_start_ramp.getStatus() == Animation.Status.RUNNING) {timeline_start_ramp.pause();}
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.pause();}
            if (ambienceenabled) {
                volume_unbindambience();
                ambienceplayer.pause();
            }
        }
        toggleplayerbuttons();
    }
    public void stop() {
        thisession.player_closereferencefile();
        volume_unbindentrainment();
        if (fade_ambience_stop != null) {
            if (fade_ambience_stop.getStatus() == Animation.Status.RUNNING) {return;}
            thisession.playerState = This_Session.PlayerState.FADING_STOP;
            fade_entrainment_stop.play();
            if (ambienceenabled) {
                volume_unbindambience();
                fade_ambience_stop.play();
            }
        } else {
            thisession.playerState = This_Session.PlayerState.STOPPED;
            entrainmentplayer.stop();
            entrainmentplayer.dispose();
            timeline_progresstonextmeditatable.stop();
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop();}
            if (ambienceenabled) {
                volume_unbindambience();
                ambienceplayer.stop();
                ambienceplayer.dispose();
            }
        }
        toggleplayerbuttons();
    }
    public void playnextentrainment() {
        try {
            volume_unbindentrainment();
            entrainmentplaycount++;
            entrainmentplayer.dispose();
            entrainmentplayer = new MediaPlayer(new Media(entrainment.created_get(entrainmentplaycount).getFile().toURI().toString()));
            entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
            entrainmentplayer.setOnError(this::entrainmenterror);
            entrainmentplayer.setVolume(currententrainmentvolume);
            entrainmentplayer.play();
            entrainmentplayer.setOnPlaying(this::volume_bindentrainment);
        } catch (IndexOutOfBoundsException ignored) {
            entrainmentplayer.dispose();
            cleanupPlayersandAnimations();
        }
    }
    public void playnextambience() {
        try {
            volume_unbindambience();
            ambienceplaycount++;
            ambienceplayer.dispose();
            ambienceplayer = new MediaPlayer(new Media(ambience.created_get(ambienceplaycount).getFile().toURI().toString()));
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            ambienceplayer.setVolume(currentambiencevolume);
            ambienceplayer.play();
            ambienceplayer.setOnPlaying(this::volume_bindambience);
        } catch (IndexOutOfBoundsException ignored) {ambienceplayer.dispose();}
    }
    public void cleanupPlayersandAnimations() {
        try {
            if (entrainmentplayer != null) {entrainmentplayer.dispose();}
            if (ambienceplayer != null && ambienceenabled) {ambienceplayer.dispose();}
            if (fade_entrainment_play != null) {fade_entrainment_play.stop();}
            if (fade_entrainment_pause != null) {fade_entrainment_pause.stop();}
            if (fade_entrainment_resume != null) {fade_entrainment_resume.stop();}
            if (fade_entrainment_stop != null) {fade_entrainment_stop.stop();}
            if (fade_ambience_play != null) {fade_ambience_play.stop();}
            if (fade_ambience_pause != null) {fade_ambience_pause.stop();}
            if (fade_ambience_resume != null) {fade_ambience_resume.stop();}
            if (fade_ambience_stop != null) {fade_ambience_stop.stop();}
            if (timeline_progresstonextmeditatable != null) {timeline_progresstonextmeditatable.stop();}
            if (timeline_start_ramp != null) {timeline_fadeout_timer.stop();}
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop();}
            toggleplayerbuttons();
        } catch (Exception ignored) {}
    }
    public void toggleplayerbuttons() {
        if (thisession.playerState == null) {return;}
        boolean idle = thisession.playerState == This_Session.PlayerState.IDLE;
        boolean playing = thisession.playerState == This_Session.PlayerState.PLAYING;
        boolean paused = thisession.playerState == This_Session.PlayerState.PAUSED;
        boolean stopped = thisession.playerState == This_Session.PlayerState.STOPPED;
        boolean fade_play = thisession.playerState == This_Session.PlayerState.FADING_PLAY;
        boolean fade_resume = thisession.playerState == This_Session.PlayerState.FADING_RESUME;
        boolean fade_pause = thisession.playerState == This_Session.PlayerState.FADING_PAUSE;
        boolean fade_stop = thisession.playerState == This_Session.PlayerState.FADING_STOP;
        thisession.playerUI.PlayButton.setDisable(playing || fade_play || fade_resume || fade_pause || fade_stop);
        thisession.playerUI.PauseButton.setDisable(paused || fade_play || fade_resume || fade_pause || fade_stop || idle);
        thisession.playerUI.StopButton.setDisable(stopped || fade_play || fade_resume || fade_pause || fade_stop || idle);
        thisession.playerUI.ReferenceToggleButton.setDisable(fade_play || fade_resume || fade_pause || fade_stop);
        thisession.playerUI.ReferenceHTMLButton.setDisable(fade_play || fade_resume || fade_pause || fade_stop);
        thisession.playerUI.ReferenceTXTButton.setDisable(fade_play || fade_resume || fade_pause || fade_stop);
        if (thisession.player_isreferencecurrentlyDisplayed()) {
            thisession.displayReference.PlayButton.setDisable(playing || fade_play || fade_resume || fade_pause || fade_stop);
            thisession.displayReference.PauseButton.setDisable(paused || fade_play || fade_resume || fade_pause || fade_stop || idle);
            thisession.displayReference.StopButton.setDisable(stopped || fade_play || fade_resume || fade_pause || fade_stop || idle);
        }
        String playbuttontext;
        String pausebuttontext;
        String stopbuttontext;
        String statusbartext;
        String meditatablename;
        if (thisession.getCurrentmeditatable() != null) {meditatablename = thisession.getCurrentmeditatable().name;}
        else {meditatablename = "Session";}
        switch (thisession.playerState) {
            case IDLE:
                playbuttontext = "Start";
                pausebuttontext = "Pause";
                stopbuttontext = "Stop";
                statusbartext = "";
                break;
            case PLAYING:
                playbuttontext = "Playing";
                pausebuttontext = "Pause";
                stopbuttontext = "Stop";
                statusbartext = meditatablename + " Playing";
                break;
            case PAUSED:
                playbuttontext = "Resume";
                pausebuttontext = "Paused";
                stopbuttontext = "Stop";
                statusbartext = meditatablename + " Paused";
                break;
            case STOPPED:
                playbuttontext = "Play";
                pausebuttontext = "Stopped";
                stopbuttontext = "Stopped";
                statusbartext = "Session Stopped";
                break;
            case TRANSITIONING:
                playbuttontext = "Transitioning";
                pausebuttontext = "Transitioning";
                stopbuttontext = "Transitioning";
                statusbartext = "Transitioning...Please Wait";
                break;
            case FADING_PLAY:
                playbuttontext = "Starting";
                pausebuttontext = "Starting";
                stopbuttontext = "Starting";
                statusbartext = "Fading In To " + meditatablename ;
                break;
            case FADING_RESUME:
                playbuttontext = "Resuming";
                pausebuttontext = "Resuming";
                stopbuttontext = "Resuming";
                statusbartext = "Resuming " + meditatablename ;
                break;
            case FADING_PAUSE:
                playbuttontext = "Pausing";
                pausebuttontext = "Pausing";
                stopbuttontext = "Pausing";
                statusbartext = "Pausing " + meditatablename;
                break;
            case FADING_STOP:
                playbuttontext = "Stopping";
                pausebuttontext = "Stopping";
                stopbuttontext = "Stopping";
                statusbartext = "Stopping " + meditatablename;
                break;
            default:
                playbuttontext = "";
                pausebuttontext = "";
                stopbuttontext = "";
                statusbartext = "";
                break;
        }
        thisession.playerUI.PlayButton.setText(playbuttontext);
        thisession.playerUI.PauseButton.setText(pausebuttontext);
        thisession.playerUI.StopButton.setText(stopbuttontext);
        thisession.playerUI.StatusBar.setText(statusbartext);
        if (thisession.player_isreferencecurrentlyDisplayed()) {
            thisession.displayReference.PlayButton.setText(playbuttontext);
            thisession.displayReference.PauseButton.setText(pausebuttontext);
            thisession.displayReference.StopButton.setText(stopbuttontext);
        }
        toggleplayervolumecontrols();
    }
    public void toggleplayervolumecontrols() {
        boolean enabled = thisession.playerState == This_Session.PlayerState.PLAYING;
        thisession.playerUI.EntrainmentVolume.setDisable(! enabled);
        if (ambienceenabled) {thisession.playerUI.AmbienceVolume.setDisable(! enabled);}
        if (thisession.player_isreferencecurrentlyDisplayed()) {
            thisession.displayReference.EntrainmentVolumeSlider.setDisable(! enabled);
            if (ambienceenabled) {thisession.displayReference.AmbienceVolumeSlider.setDisable(! enabled);}
        }
    }
    public void volume_bindentrainment() {
        thisession.playerUI.EntrainmentVolume.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
        thisession.playerUI.EntrainmentVolume.setDisable(false);
        thisession.playerUI.EntrainmentVolume.setOnMouseDragged(event1 -> {
            String percentage = new Double(thisession.playerUI.EntrainmentVolume.getValue() * 100).intValue() + "%";
            currententrainmentvolume = thisession.playerUI.EntrainmentVolume.getValue();
            thisession.setCurrententrainmentvolume(currententrainmentvolume);
            thisession.playerUI.EntrainmentVolumePercentage.setText(percentage);
            if (thisession.player_isreferencecurrentlyDisplayed()) {
                thisession.displayReference.EntrainmentVolumeSlider.valueProperty().unbind();
                thisession.displayReference.EntrainmentVolumeSlider.setValue(currententrainmentvolume);
                thisession.displayReference.EntrainmentVolumePercentage.setText(percentage);
                thisession.displayReference.EntrainmentVolumeSlider.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
            }
        });
        thisession.playerUI.EntrainmentVolume.setOnScroll(event -> {
            Double newvalue = thisession.playerUI.EntrainmentVolume.getValue();
            if (event.getDeltaY() < 0) {newvalue -= Options.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
            else {newvalue += Options.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
            if (newvalue <= 1.0 && newvalue >= 0.0) {
                Double roundedvalue = Util.round_nearestmultipleof5(newvalue * 100);
                String percentage = roundedvalue.intValue() + "%";
                currententrainmentvolume = roundedvalue / 100;
                thisession.setCurrententrainmentvolume(currententrainmentvolume);
                thisession.playerUI.EntrainmentVolume.valueProperty().unbind();
                thisession.playerUI.EntrainmentVolume.setValue(currententrainmentvolume);
                thisession.playerUI.EntrainmentVolume.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
                thisession.playerUI.EntrainmentVolumePercentage.setText(percentage);
                if (thisession.player_isreferencecurrentlyDisplayed()) {
                    thisession.displayReference.EntrainmentVolumeSlider.valueProperty().unbind();
                    thisession.displayReference.EntrainmentVolumeSlider.setValue(currententrainmentvolume);
                    thisession.displayReference.EntrainmentVolumeSlider.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
                    thisession.displayReference.EntrainmentVolumePercentage.setText(percentage);
                }
            }
        });
        if (thisession.player_isreferencecurrentlyDisplayed()) {
            thisession.displayReference.EntrainmentVolumeSlider.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
            thisession.displayReference.EntrainmentVolumeSlider.setDisable(false);
            thisession.displayReference.EntrainmentVolumeSlider.setOnMouseDragged(event1 -> {
                String percentage = new Double(thisession.displayReference.EntrainmentVolumeSlider.getValue() * 100).intValue() + "%";
                currententrainmentvolume = thisession.playerUI.EntrainmentVolume.getValue();
                thisession.setCurrententrainmentvolume(currententrainmentvolume);
                thisession.displayReference.EntrainmentVolumePercentage.setText(percentage);
                thisession.playerUI.EntrainmentVolume.valueProperty().unbind();
                thisession.playerUI.EntrainmentVolume.setValue(currententrainmentvolume);
                thisession.playerUI.EntrainmentVolume.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
                thisession.playerUI.EntrainmentVolumePercentage.setText(percentage);
            });
            thisession.displayReference.EntrainmentVolumeSlider.setOnScroll(event -> {
                Double newvalue = thisession.displayReference.EntrainmentVolumeSlider.getValue();
                if (event.getDeltaY() < 0) {newvalue -= Options.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
                else {newvalue += Options.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
                if (newvalue <= 1.0 && newvalue >= 0.0) {
                    Double roundedvalue = Util.round_nearestmultipleof5(newvalue * 100);
                    String percentage = roundedvalue.intValue() + "%";
                    currententrainmentvolume = roundedvalue / 100;
                    thisession.setCurrententrainmentvolume(currententrainmentvolume);
                    thisession.displayReference.EntrainmentVolumeSlider.valueProperty().unbind();
                    thisession.displayReference.EntrainmentVolumeSlider.setValue(currententrainmentvolume);
                    thisession.displayReference.EntrainmentVolumeSlider.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
                    thisession.displayReference.EntrainmentVolumePercentage.setText(percentage);
                    thisession.playerUI.EntrainmentVolume.valueProperty().unbind();
                    thisession.playerUI.EntrainmentVolume.setValue(currententrainmentvolume);
                    thisession.playerUI.EntrainmentVolume.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
                    thisession.playerUI.EntrainmentVolumePercentage.setText(percentage);
                }
            });
        }
    }
    public void volume_unbindentrainment() {
        thisession.playerUI.EntrainmentVolume.valueProperty().unbind();
        thisession.playerUI.EntrainmentVolume.setDisable(true);
        if (thisession.player_isreferencecurrentlyDisplayed()) {
            thisession.displayReference.EntrainmentVolumeSlider.valueProperty().unbind();
            thisession.displayReference.EntrainmentVolumeSlider.setDisable(true);
        }
    }
    public void volume_bindambience() {
        thisession.playerUI.AmbienceVolume.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
        thisession.playerUI.AmbienceVolume.setDisable(false);
        thisession.playerUI.AmbienceVolume.setOnMouseDragged(event1 -> {
            String percentage = new Double(thisession.playerUI.AmbienceVolume.getValue() * 100).intValue() + "%";
            currentambiencevolume = thisession.playerUI.AmbienceVolume.getValue();
            thisession.setCurrentambiencevolume(currentambiencevolume);
            thisession.playerUI.AmbienceVolumePercentage.setText(percentage);
            thisession.playerUI.AmbienceVolume.setTooltip(new Tooltip(percentage));
            if (thisession.player_isreferencecurrentlyDisplayed()) {
                thisession.displayReference.AmbienceVolumeSlider.valueProperty().unbind();
                thisession.displayReference.AmbienceVolumeSlider.setValue(currentambiencevolume);
                thisession.displayReference.AmbienceVolumeSlider.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
                thisession.displayReference.AmbienceVolumePercentage.setText(percentage);
            }
        });
        thisession.playerUI.AmbienceVolume.setOnScroll(event -> {
            Double newvalue = thisession.playerUI.AmbienceVolume.getValue();
            if (event.getDeltaY() < 0) {newvalue -= Options.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
            else {newvalue += Options.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
            if (newvalue <= 1.0 && newvalue >= 0.0) {
                Double roundedvalue = Util.round_nearestmultipleof5(newvalue * 100);
                String percentage = roundedvalue.intValue() + "%";
                currentambiencevolume = roundedvalue / 100;
                thisession.setCurrentambiencevolume(roundedvalue / 100);
                thisession.playerUI.AmbienceVolume.setValue(roundedvalue / 100);
                thisession.playerUI.AmbienceVolume.setTooltip(new Tooltip(percentage));
                thisession.playerUI.AmbienceVolumePercentage.setText(percentage);
                if (thisession.player_isreferencecurrentlyDisplayed()) {
                    thisession.displayReference.AmbienceVolumeSlider.valueProperty().unbind();
                    thisession.displayReference.AmbienceVolumeSlider.setValue(currentambiencevolume);
                    thisession.displayReference.AmbienceVolumeSlider.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
                    thisession.displayReference.AmbienceVolumePercentage.setText(percentage);
                }
            }
        });
        if (thisession.player_isreferencecurrentlyDisplayed()) {
            thisession.displayReference.AmbienceVolumeSlider.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
            thisession.displayReference.AmbienceVolumeSlider.setDisable(false);
            thisession.displayReference.AmbienceVolumeSlider.setOnMouseDragged(event1 -> {
                String percentage = new Double(thisession.displayReference.AmbienceVolumeSlider.getValue() * 100).intValue() + "%";
                currentambiencevolume = thisession.playerUI.AmbienceVolume.getValue();
                thisession.setCurrentambiencevolume(currentambiencevolume);
                thisession.displayReference.AmbienceVolumePercentage.setText(percentage);
                thisession.playerUI.AmbienceVolume.valueProperty().unbind();
                thisession.playerUI.AmbienceVolume.setValue(currentambiencevolume);
                thisession.playerUI.AmbienceVolume.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
                thisession.playerUI.AmbienceVolumePercentage.setText(percentage);
            });
            thisession.displayReference.AmbienceVolumeSlider.setOnScroll(event -> {
                Double newvalue = thisession.displayReference.AmbienceVolumeSlider.getValue();
                if (event.getDeltaY() < 0) {newvalue -= Options.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
                else {newvalue += Options.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
                if (newvalue <= 1.0 && newvalue >= 0.0) {
                    Double roundedvalue = Util.round_nearestmultipleof5(newvalue * 100);
                    String percentage = roundedvalue.intValue() + "%";
                    currentambiencevolume = roundedvalue / 100;
                    thisession.setCurrentambiencevolume(roundedvalue / 100);
                    thisession.displayReference.AmbienceVolumeSlider.valueProperty().unbind();
                    thisession.displayReference.AmbienceVolumeSlider.setValue(roundedvalue / 100);
                    thisession.displayReference.AmbienceVolumeSlider.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
                    thisession.displayReference.AmbienceVolumeSlider.setTooltip(new Tooltip(percentage));
                    thisession.displayReference.AmbienceVolumePercentage.setText(percentage);
                    thisession.playerUI.AmbienceVolume.valueProperty().unbind();
                    thisession.playerUI.AmbienceVolume.setValue(currentambiencevolume);
                    thisession.playerUI.AmbienceVolume.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
                    thisession.playerUI.AmbienceVolumePercentage.setText(percentage);
                }
            });
        }
    }
    public void volume_unbindambience() {
        thisession.playerUI.AmbienceVolume.valueProperty().unbind();
        thisession.playerUI.AmbienceVolume.setDisable(true);
        try {
            if (thisession.player_isreferencecurrentlyDisplayed()) {
                thisession.displayReference.AmbienceVolumeSlider.valueProperty().unbind();
                thisession.displayReference.AmbienceVolumeSlider.setDisable(true);
            }
        } catch (NullPointerException ignored) {}
    }
    public void volume_rebindambience() {volume_unbindambience(); volume_bindambience();}
    public void volume_rebindentrainment() {volume_unbindentrainment(); volume_bindentrainment();}
    public void tick() {
        if (entrainmentplayer.getStatus() == MediaPlayer.Status.PLAYING) {
            try {
                thisession.Root.getSessions().sessioninformation_getspecificsession(thisession.Root.getSessions().getSession().size() - 1).updatemeditatableduration(number, new Double(elapsedtime.toMinutes()).intValue());
                if (goals_getCurrent() != null && (goals_getCurrent().getGoal_Hours() * 60) >= sessions_getTotalMinutesPracticed(false)) {
                    goalscompletedthissession.addAll(goals_completeandgetcompleted());
                }
            } catch (NullPointerException ignored) {}
        }
    }
    // Error Handling
    protected void entrainmenterror() {}
    protected void ambienceerror() {}

// Export
    public Service<Boolean> getexportservice() {
        //    return new Service<Boolean>() {
        //        @Override
        //        protected Task<Boolean> createTask() {
        //            return new Task<Boolean>() {
        //                @Override
        //                protected Boolean call() throws Exception {
        //                    updateTitle("Building " + name);
        //                    System.out.println("Concatenating Entrainment For " + name);
        //                    updateMessage("Concatenating Entrainment Files");
        //                    Util.audio_concatenatefiles(entrainmentlist, tempentrainmenttextfile, finalentrainmentfile);
        //                    if (isCancelled()) return false;
        //                    if (ambienceenabled) {
        //                        updateProgress(0.25, 1.0);
        //                        System.out.println("Concatenating Ambience For " + name);
        //                        updateMessage("Concatenating Ambience Files");
        //                        Util.audio_concatenatefiles(ambiencelist, tempambiencetextfile, finalambiencefile);
        //                        if (isCancelled()) return false;
        //                        updateProgress(0.50, 1.0);
        ////                            System.out.println("Reducing Ambience Duration For " + name);
        ////                            updateMessage("Cutting Ambience Audio To Selected Duration");
        ////                            System.out.println("Final Ambience File" + finalambiencefile.getAbsolutePath());
        ////                            if (Util.audio_getduration(finalambiencefile) > getdurationinseconds()) {
        ////                                Util.audio_trimfile(finalambiencefile, getdurationinseconds());
        ////                            }
        ////                            if (isCancelled()) return false;
        //                        updateProgress(0.75, 1.0);
        //                        System.out.println("Mixing Final Audio For " + name);
        //                        updateMessage("Combining Entrainment And Ambience Files");
        //                        mixentrainmentandambience();
        //                        if (isCancelled()) return false;
        //                        updateProgress(1.0, 1.0);
        //                    } else {updateProgress(1.0, 1.0);}
        //                    return exportedsuccessfully();
        //                }
        //            };
        //        }
        //    };
        return null;
    }
    public Boolean exportedsuccesfully() {
//        if (ambienceenabled) {return finalambiencefile.exists() && finalentrainmentfile.exists();}
//        else {return finalentrainmentfile.exists();}
        return false;
    }
    public Boolean mixentrainmentandambience() {
//        if (! ambienceenabled) {
//            try {
//                FileUtils.copyFile(finalentrainmentfile, getFinalexportfile());
//                return true;
//            } catch (IOException e) {return false;}
//        } else {return Util.audio_mixfiles(new ArrayList<>(Arrays.asList(finalambiencefile, finalentrainmentfile)), getFinalexportfile());}
        return false;
    }
    public Boolean sessionreadyforFinalExport() {
//        boolean cutisgood;
//        File entrainmentfile = new File(Options.DIRECTORYTEMP, "Entrainment/" + name + ".mp3");
//        cutisgood = entrainmentfile.exists();
//        if (ambienceenabled) {
//            File ambiencefile = new File(Options.DIRECTORYTEMP, "Ambience/" + name + ".mp3");
//            cutisgood = ambiencefile.exists();
//        }
//        return cutisgood;
        return false;
    }
    public Boolean cleanuptempfiles() {
//        if (tempambiencefile.exists()) {tempambiencefile.delete();}
//        if (tempentrainmentfile.exists()) {tempentrainmentfile.delete();}
//        if (tempentrainmenttextfile.exists()) {tempentrainmenttextfile.delete();}
//        if (tempambiencetextfile.exists()) {tempambiencetextfile.delete();}
        return false;
    }
    public File getFinalexportfile() {
        return null;
    }

// Goals
    public void goals_add(kujiin.xml.Goals.Goal newgoal) {
        System.out.println("Called Add Goal In Meditatable Logic");
        GoalsController.add(number, newgoal);
    }
    public void goals_update(List<kujiin.xml.Goals.Goal> goalslist) {
        GoalsController.update(goalslist, number);
    }
    public void goals_delete(kujiin.xml.Goals.Goal currentgoal) {
        GoalsController.delete(number, currentgoal);
    }
    public void goals_delete(int goalindex) {
        GoalsController.delete(number, goalindex);
    }
    public kujiin.xml.Goals.Goal goals_getCurrent() {
        return GoalsController.getCurrentGoal(number);
    }
    public List<kujiin.xml.Goals.Goal> goals_getAll() {return GoalsController.getAllGoals(number);}
    public List<kujiin.xml.Goals.Goal> goals_getCompleted() {return GoalsController.getCompletedGoals(number);}
    public int goals_getCompletedGoalCount() {return GoalsController.count_completedgoals(number);}
    public boolean goals_arelongenough() {
        try {
            return (sessions_getTotalMinutesPracticed(false) / 60) + getduration().toHours() >= goals_getCurrent().getGoal_Hours();
        } catch (NullPointerException e) {return true;}
    }
    public void goals_transitioncheck() {
        goalscompletedthissession.addAll(goals_completeandgetcompleted());
        if (goalscompletedthissession.size() > 0) {
            thisession.MeditatableswithGoalsCompletedThisSession.add(this);
        }
    }
    public void goals_complete() {
        GoalsController.completegoals(number, session_getTotalDurationPracticed());
    }
    public List<kujiin.xml.Goals.Goal> goals_completeandgetcompleted() {
        return GoalsController.completegoalsandgetcompleted(number, session_getTotalDurationPracticed());
    }
    public List<kujiin.xml.Goals.Goal> goals_getGoalsCompletedThisSession() {
        return goalscompletedthissession;
    }
    public List<kujiin.xml.Goals.Goal> goals_getGoalsCompletedOn(LocalDate localDate) {
        return GoalsController.getgoalsCompletedOn(number, localDate);
    }

// Session Tracking
    public double sessions_getAveragePracticeTime(boolean includepreandpost) {
        return thisession.Root.getSessions().sessioninformation_getaveragepracticetime(number, includepreandpost);
    }
    public Duration session_getTotalDurationPracticed() {
        Duration totalduration = new Duration((sessions_getTotalMinutesPracticed(false) * 60) * 1000);
        if (elapsedtime.greaterThan(Duration.ZERO)) {return totalduration.add(elapsedtime);}
        else {return totalduration;}
    }
    public int sessions_getTotalMinutesPracticed(boolean includepreandpost) {
        return thisession.Root.getSessions().sessioninformation_getallsessiontotals(number, includepreandpost);
    }
    public int sessions_getNumberOfSessionsPracticed(boolean includepreandpost) {
        return thisession.Root.getSessions().sessioninformation_getsessioncount(number, includepreandpost);
    }

// Reference Files
    public File reference_getFile() {
        This_Session.ReferenceType referenceType = thisession.Root.getOptions().getSessionOptions().getReferencetype();
        if (referenceType == null) {return null;}
        switch (referenceType) {
            case html: {
                String name = this.name + ".html";
                return new File(Options.DIRECTORYREFERENCE, "html/" + name);
            }
            case txt: {
                String name = this.name + ".txt";
                return new File(Options.DIRECTORYREFERENCE, "txt/" + name);
            }
            default:
                return null;
        }
    }
    public boolean reference_filevalid(This_Session.ReferenceType referenceType) {
        if (referenceType == null) {return false;}
        if (! reference_getFile().exists()) {return false;}
        String contents = Util.file_getcontents(reference_getFile());
        if (contents == null) {return false;}
        switch (referenceType) {
            case html:
                boolean validhtml = Util.String_validhtml(contents);
                return contents.length() > 0 && validhtml;
            case txt:
                return contents.length() > 0;
            default:
                return false;
        }
    }

}
