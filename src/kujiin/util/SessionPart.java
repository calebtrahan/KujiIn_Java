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
import java.util.Random;
import java.util.stream.Collectors;

import static kujiin.xml.Options.DEFAULT_FADERESUMEANDPAUSEDURATION;

public class SessionPart {
// GUI Fields
    protected ToggleButton Switch;
    protected TextField Value;
// Data Fields
    public int number;
    public String name;
    public Duration duration;
    protected This_Session thisession;
    protected Ambience ambience;
    protected Entrainment entrainment;
// Playback Fields
    protected int entrainmentplaycount;
    protected int ambienceplaycount;
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
    protected Animation timeline_progresstonextsessionpart;
    protected Animation timeline_start_ending_ramp;
    protected Animation timeline_end_starting_ramp;
    protected FreqType freqType;
    private Double currententrainmentvolume;
    private Double currentambiencevolume;
    private ArrayList<SoundFile> ambienceplayhistory;
    public Duration elapsedtime;
    protected List<SessionPart> allsessionpartstoplay;
    protected List<kujiin.xml.Goals.Goal> goalscompletedthissession;
// Goal Fields
    protected Goals GoalsController;
    protected List<kujiin.xml.Goals.Goal> Goals;
// Entrainment Fields
    protected boolean entrainmentready = false;
    protected boolean entrainmentmissingfiles = false;
    protected int entrainmentchecker_partcount;
    protected final ArrayList<String> entrainmentchecker_partcutnames = new ArrayList<>(Arrays.asList("rin", "kyo", "toh", "sha", "kai", "jin", "retsu", "zai", "zen"));
    public MediaPlayer entrainmentchecker_calculateplayer;
    public List<File> entrainmentchecker_missingfiles = new ArrayList<>();
// Ambience Fields
    private ArrayList<File> ambiencechecker_soundfilestoaddtoambience = new ArrayList<>();
    private int ambiencechecker_soundfilestoaddcount = 0;
    private MediaPlayer ambiencechecker_calculateplayer;
    private boolean ambienceready = false;

    public SessionPart() {}
    public SessionPart(int number, String name, String briefsummary, This_Session thissession, ToggleButton aSwitch, TextField value) {
        this.number = number;
        this.name = name;
        this.duration = Duration.ZERO;
        this.thisession = thissession;
        if (aSwitch != null && value != null) {
            Switch = aSwitch;
            Value = value;
            Util.custom_textfield_integer(Value, Switch, 0, 600, 1);
            Value.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    changevalue(Integer.parseInt(Value.getText()));
                    thissession.Root.creation_gui_update();
                } catch (NumberFormatException ignored) {setDuration(0);}
            });
            if (briefsummary != null) {Switch.setTooltip(new Tooltip(briefsummary));}
            Switch.setOnAction(event -> gui_toggleswitch());
            gui_toggleswitch();
        }
        entrainment = thissession.Root.getEntrainments().getsessionpartEntrainment(number);
        entrainmentchecker_partcount = 0;
        entrainment_populate();
        ambience = thissession.Root.getAmbiences().getsessionpartAmbience(number);
        ambience_cleanupexistingambience();
        ambience_addnewfromdirectory();
    //        tempentrainmenttextfile = new File(Options.DIRECTORYTEMP, "txt/" + name + "Ent.txt");
//        tempentrainmentfile = new File(Options.DIRECTORYTEMP, "Entrainment/" + name + "Temp.mp3");
//        finalentrainmentfile = new File(Options.DIRECTORYTEMP, "Entrainment/" + name + ".mp3");
//        tempambiencetextfile = new File(Options.DIRECTORYTEMP, "txt/" + name + "Amb.txt");
//        tempambiencefile = new File(Options.DIRECTORYTEMP, "Ambience/" + name + "Temp.mp3");
//        finalambiencefile = new File(Options.DIRECTORYTEMP, "Ambience/" + name + ".mp3");
//        setFinalexportfile(new File(Options.DIRECTORYTEMP, name + ".mp3"));
    }

// Entrainment Methods
    public void entrainment_populate() {}
    public boolean entrainment_isReady() {return entrainmentready;}
    public boolean entrainment_missingfiles() {
        return entrainmentmissingfiles;
    }
    public List<File> entrainment_getMissingFiles() {return entrainmentchecker_missingfiles;}
    public Duration ambience_getTotalActualDuration() {return ambience.gettotalActualDuration();}

// Ambience Methods
    public void ambience_cleanupexistingambience() {
        if (ambience.getAmbience() != null) {
            ambience.getAmbience().stream().filter(i -> i.getFile() == null || !i.getFile().exists()).forEach(i -> ambience.actual_remove(i));
        } else {}
    }
    public void ambience_addnewfromdirectory() {
        // TODO Fix This So It Checks Existing Ambience If Existing (Deleting Non Existing), And Adds New From Ambience Directory If Valid
        if (ambiencechecker_soundfilestoaddtoambience.isEmpty()) {
            File ambiencedirectory = new File(Options.DIRECTORYAMBIENCE, name);
            ambiencechecker_soundfilestoaddcount = 0;
            try {
                for (File i : ambiencedirectory.listFiles()) {
                    if (Util.audio_isValid(i)) {
                        if (! ambience.getAmbienceFiles().contains(i)) {
                            ambiencechecker_soundfilestoaddtoambience.add(i);}
                        else {
                            try {
                                Double duration = ambience.getAmbience().get(ambience.getAmbienceFiles().indexOf(i)).getDuration();
                                if (duration == null || duration == 0.0) {
                                    ambiencechecker_soundfilestoaddtoambience.add(i);}
                            } catch (ArrayIndexOutOfBoundsException ignored) {
                                ambiencechecker_soundfilestoaddtoambience.add(i);}
                        }
                    }
                }
                if (! ambiencechecker_soundfilestoaddtoambience.isEmpty()) {ambience_addnewfromdirectory();}
                else {ambienceready = true;}
            } catch (NullPointerException ignored) {
                // TODO Change This To Reflect No Ambience Files In Directory
                ambienceready= true;
            }
        } else {
            try {
                File actualfile = ambiencechecker_soundfilestoaddtoambience.get(ambiencechecker_soundfilestoaddcount);
                ambiencechecker_calculateplayer = new MediaPlayer(new Media(actualfile.toURI().toString()));
                ambiencechecker_calculateplayer.setOnReady(() -> {
                    SoundFile soundFile = new SoundFile(actualfile);
                    soundFile.setDuration(ambiencechecker_calculateplayer.getTotalDuration().toMillis());
                    ambience.actual_add(soundFile);
                    ambiencechecker_soundfilestoaddcount++;
                    ambiencechecker_calculateplayer.dispose();
                    ambience_addnewfromdirectory();
                });
                ambiencechecker_calculateplayer.setOnError(() -> {
                    ambiencechecker_soundfilestoaddcount++;
                    ambiencechecker_calculateplayer.dispose();
                    ambience_addnewfromdirectory();
                });
            } catch (IndexOutOfBoundsException ignored) {
                thisession.Root.getAmbiences().setsessionpartAmbience(number, ambience);
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
        Value.setText(Integer.toString(newvalue));
        setDuration(newvalue);
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
    public List<kujiin.xml.Goals.Goal> getGoals() {
        return Goals;
    }
    public List<kujiin.xml.Goals.Goal> getGoalscompletedthissession() {
        return goalscompletedthissession;
    }
    public boolean getramponly() {
        try {return ((Qi_Gong) this).ramponly;}
        catch (ClassCastException ignored) {return false;}
    }
    public String getNameForFiles() {return name.toLowerCase();}
    public String getNameForChart() {return name;}
    private void setDuration(double newduration) {
        duration = Duration.minutes(newduration);
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
    public boolean creation_build(List<SessionPart> sessionpartstoplay) {
        allsessionpartstoplay = sessionpartstoplay;
        if (thisession.Root.AmbienceSwitch.isSelected()) {return creation_buildEntrainment() && creation_buildAmbience();}
        else {return creation_buildEntrainment();}
    }
    protected boolean creation_buildEntrainment() {
        return ! duration.equals(Duration.ZERO) && entrainment.getFreq().isValid();
    }
    protected boolean creation_buildAmbience() {
        return ambience.hasAnyAmbience();
    }
    public void creation_reset(boolean setvaluetozero) {
        if (setvaluetozero) {
            Switch.setSelected(false);
            gui_toggleswitch();
        }
    }

// Playback
    public void start() {
        // TODO On Last Session Part:
            // Do NOT Play Ramp (If Enabled For Session)
        System.out.println("Starting " + name);
        boolean ramponly = getramponly();
        elapsedtime = Duration.ZERO;
        entrainmentplaycount = 0;
        ambienceplaycount = 0;
        setupfadeanimations();
        volume_unbindentrainment();
        if (! ramponly) {entrainmentplayer = new MediaPlayer(new Media(entrainment.getFreq().getFile().toURI().toString()));}
        else {entrainmentplayer = new MediaPlayer(new Media(entrainment.getRampfile().getFile().toURI().toString()));}
        entrainmentplayer.setVolume(0.0);
        entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
        entrainmentplayer.setOnError(this::entrainmenterror);
        entrainmentplayer.play();
        timeline_progresstonextsessionpart = new Timeline(new KeyFrame(getduration(), ae -> thisession.player_progresstonextsessionpart()));
        timeline_progresstonextsessionpart.play();
        currententrainmentvolume = thisession.getCurrententrainmentvolume();
        thisession.player_displayreferencefile();
        boolean isLastSessionPart = allsessionpartstoplay.indexOf(this) == allsessionpartstoplay.size() - 1;
        if (! ramponly && ! isLastSessionPart && thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            timeline_start_ending_ramp = new Timeline(new KeyFrame(getduration().subtract(Duration.millis(entrainment.getRampfile().getDuration())), ae -> {
                volume_unbindentrainment();
                entrainmentplayer.stop();
                entrainmentplayer.dispose();
                entrainmentplayer = new MediaPlayer(new Media(entrainment.getRampfile().getFile().toURI().toString()));
                entrainmentplayer.setOnError(this::entrainmenterror);
                entrainmentplayer.setVolume(currententrainmentvolume);
                entrainmentplayer.play();
                entrainmentplayer.setOnPlaying(this::volume_bindentrainment);
            }));
            timeline_start_ending_ramp.play();
        }
        if (fade_entrainment_stop != null) {
            timeline_fadeout_timer = new Timeline(new KeyFrame(duration.subtract(Duration.seconds(thisession.Root.getOptions().getSessionOptions().getFadeoutduration())), ae -> {
                volume_unbindentrainment();
                fade_entrainment_stop.play();
                if (fade_ambience_stop != null) {
                    volume_unbindambience();
                    fade_ambience_stop.play();
                }
            }));
            timeline_fadeout_timer.play();
        }
        if (fade_entrainment_play != null) {
            if (fade_entrainment_play.getStatus() == Animation.Status.RUNNING) {return;}
            thisession.playerState = This_Session.PlayerState.FADING_PLAY;
            fade_entrainment_play.play();
        } else {
            entrainmentplayer.setVolume(currententrainmentvolume);
            String percentage = new Double(currententrainmentvolume * 100).intValue() + "%";
            thisession.playerUI.EntrainmentVolumePercentage.setText(percentage);
            thisession.playerState = This_Session.PlayerState.PLAYING;
            if (thisession.player_isreferencecurrentlyDisplayed()) {
                thisession.displayReference.EntrainmentVolumeSlider.setValue(currententrainmentvolume);
                thisession.displayReference.EntrainmentVolumePercentage.setText(percentage);
            }
            volume_bindentrainment();}
        if (thisession.Root.AmbienceSwitch.isSelected() && thisession.ambiencePlaybackType != null) {
            ambienceplayhistory = new ArrayList<>();
            currentambiencevolume = thisession.getCurrentambiencevolume();
            volume_unbindambience();
            File ambiencefile = null;
            switch (thisession.ambiencePlaybackType) {
                case REPEAT:
                    ambiencefile = ambience.actual_get(0).getFile();
                    break;
                case SHUFFLE:
                    Random random = new Random();
                    SoundFile selectedsoundfile = ambience.actual_get(random.nextInt(ambience.getAmbience().size() - 1));
                    ambiencefile = selectedsoundfile.getFile();
                    ambienceplayhistory.add(selectedsoundfile);
                    break;
            }
            ambienceplayer = new MediaPlayer(new Media(ambiencefile.toURI().toString()));
            ambienceplayer.setVolume(0.0);
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            ambienceplayer.play();
            if (fade_ambience_play != null && ! ramponly) {fade_ambience_play.play();}
            else {
                ambienceplayer.setVolume(currentambiencevolume);
                String percentage = new Double(currentambiencevolume * 100).intValue() + "%";
                thisession.playerUI.AmbienceVolumePercentage.setText(percentage);
                if (thisession.player_isreferencecurrentlyDisplayed()) {
                    thisession.displayReference.AmbienceVolumeSlider.setValue(currentambiencevolume);
                    thisession.displayReference.AmbienceVolumePercentage.setText(percentage);
                }
                volume_bindambience();
            }
        }
        toggleplayerbuttons();
        thisession.Root.sessionandgoals_forceselectsessionpart(number);
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
            timeline_progresstonextsessionpart.play();
            if (thisession.Root.getOptions().getSessionOptions().getRampenabled() && timeline_start_ending_ramp.getStatus() == Animation.Status.PAUSED) {
                timeline_start_ending_ramp.play();}
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.play();}
        }
        if (thisession.Root.AmbienceSwitch.isSelected()) {
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
            if (thisession.Root.AmbienceSwitch.isSelected()) {
                volume_unbindambience();
                fade_ambience_pause.play();
            }
            // Close Loading Dialog
        } else {
            thisession.playerState = This_Session.PlayerState.PAUSED;
            entrainmentplayer.pause();
            timeline_progresstonextsessionpart.pause();
            if (thisession.Root.getOptions().getSessionOptions().getRampenabled() && timeline_start_ending_ramp.getStatus() == Animation.Status.RUNNING) {
                timeline_start_ending_ramp.pause();}
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.pause();}
            if (thisession.Root.AmbienceSwitch.isSelected()) {
                volume_unbindambience();
                ambienceplayer.pause();
            }
        }
        toggleplayerbuttons();
    }
    public void stop() {
        thisession.player_closereferencefile();
        volume_unbindentrainment();
        if (fade_entrainment_stop != null) {
            if (fade_entrainment_stop.getStatus() == Animation.Status.RUNNING) {return;}
            fade_entrainment_stop.play();
            thisession.playerState = This_Session.PlayerState.FADING_STOP;
            if (thisession.Root.AmbienceSwitch.isSelected()) {
                volume_unbindambience();
                fade_ambience_stop.play();
            }
        } else {
            thisession.playerState = This_Session.PlayerState.STOPPED;
            entrainmentplayer.stop();
            entrainmentplayer.dispose();
            timeline_progresstonextsessionpart.stop();
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop();}
            if (thisession.Root.AmbienceSwitch.isSelected()) {
                volume_unbindambience();
                ambienceplayer.stop();
                ambienceplayer.dispose();
            }
        }
        toggleplayerbuttons();
    }
    private void setupfadeanimations() {
        // PLAY
        if (getramponly()) {return;}
        if (thisession.Root.getOptions().getSessionOptions().getFadeinduration() > 0.0) {
            fade_entrainment_play = new Transition() {
                {setCycleDuration(Duration.seconds(thisession.Root.getOptions().getSessionOptions().getFadeinduration()));}

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
            if (thisession.Root.AmbienceSwitch.isSelected()) {
                fade_ambience_play = new Transition() {
                    {setCycleDuration(Duration.seconds(thisession.Root.getOptions().getSessionOptions().getFadeinduration()));}

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
            {setCycleDuration(Duration.seconds(DEFAULT_FADERESUMEANDPAUSEDURATION));}

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
        fade_entrainment_resume.setOnFinished(event -> {thisession.playerState = This_Session.PlayerState.PLAYING; timeline_progresstonextsessionpart.play(); if (thisession.Root.getOptions().getSessionOptions().getRampenabled() && timeline_start_ending_ramp.getStatus() == Animation.Status.PAUSED) {
            timeline_start_ending_ramp.play();}
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.play();} toggleplayerbuttons(); volume_bindentrainment();});
        if (thisession.Root.AmbienceSwitch.isSelected()) {
            fade_ambience_resume = new Transition() {
                {setCycleDuration(Duration.seconds(DEFAULT_FADERESUMEANDPAUSEDURATION));}

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
            {setCycleDuration(Duration.seconds(DEFAULT_FADERESUMEANDPAUSEDURATION));}

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
        fade_entrainment_pause.setOnFinished(event -> {entrainmentplayer.pause(); timeline_progresstonextsessionpart.pause();
            if (thisession.Root.getOptions().getSessionOptions().getRampenabled() && timeline_start_ending_ramp.getStatus() == Animation.Status.RUNNING) {
                timeline_start_ending_ramp.pause();}
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.pause();} thisession.playerState = This_Session.PlayerState.PAUSED; toggleplayerbuttons();});
        if (thisession.Root.AmbienceSwitch.isSelected()) {
            fade_ambience_pause = new Transition() {
                {setCycleDuration(Duration.seconds(DEFAULT_FADERESUMEANDPAUSEDURATION));}

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
                {setCycleDuration(Duration.seconds(thisession.Root.getOptions().getSessionOptions().getFadeoutduration()));}

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
                if (thisession.Root.getOptions().getSessionOptions().getRampenabled() && timeline_start_ending_ramp.getStatus() == Animation.Status.RUNNING) {
                    timeline_start_ending_ramp.stop();}
                timeline_progresstonextsessionpart.stop(); if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop();} thisession.playerState = This_Session.PlayerState.STOPPED; toggleplayerbuttons();});
            if (thisession.Root.AmbienceSwitch.isSelected()) {
                fade_ambience_stop = new Transition() {
                    {setCycleDuration(Duration.seconds(thisession.Root.getOptions().getSessionOptions().getFadeoutduration()));}

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
    private void playnextentrainment() {
        try {
            volume_unbindentrainment();
            entrainmentplaycount++;
            entrainmentplayer.dispose();
            entrainmentplayer = new MediaPlayer(new Media(entrainment.getFreq().getFile().toURI().toString()));
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
    private void playnextambience() {
        try {
            volume_unbindambience();
            ambienceplaycount++;
            File ambiencefile = null;
            File previousambiencefile = new File(ambienceplayer.getMedia().getSource());
            switch (thisession.ambiencePlaybackType) {
                case REPEAT:
                    int currentambienceindex = ambience.getAmbienceFiles().indexOf(previousambiencefile);
                    if (currentambienceindex < ambience.getAmbience().size()) {ambiencefile = ambience.actual_get(currentambienceindex + 1).getFile();}
                    else {ambiencefile = ambience.actual_get(0).getFile();}
                    break;
                case SHUFFLE:
                    Random random = new Random();
                    int sizetotest;
                    File filetotest;
                    if (ambienceplayhistory.size() >= ambience.getAmbience().size()) {sizetotest = (ambience.getAmbience().size() - 1) - ambienceplayhistory.size() % ambience.getAmbience().size();}
                    else {sizetotest = ambienceplayhistory.size() - 1;}
                    if (ambience.getAmbience().size() == 1) {ambiencefile = ambience.actual_get(0).getFile(); break;}
                    else {
                        while (true) {
                            filetotest = ambience.actual_get(random.nextInt(ambience.getAmbience().size() - 1)).getFile();
                            // TODO Optimize Shuffle Algorithm For Ambience Here
                            boolean includefile = false;
                            for (int i = sizetotest; i > 0; i--) {
                                if (! ambience.actual_get(i).getFile().equals(filetotest)) {includefile = true; break;}
                            }
                            if (includefile) {ambiencefile = filetotest; break;}
                        }
                    }
                    break;
            }
            ambienceplayer.dispose();
            System.out.println("Next Ambience File Is " + ambiencefile.getAbsolutePath());
            ambienceplayer = new MediaPlayer(new Media(ambiencefile.toURI().toString()));
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
            if (ambienceplayer != null) {ambienceplayer.dispose();}
            if (fade_entrainment_play != null) {fade_entrainment_play.stop();}
            if (fade_entrainment_pause != null) {fade_entrainment_pause.stop();}
            if (fade_entrainment_resume != null) {fade_entrainment_resume.stop();}
            if (fade_entrainment_stop != null) {fade_entrainment_stop.stop();}
            if (fade_ambience_play != null) {fade_ambience_play.stop();}
            if (fade_ambience_pause != null) {fade_ambience_pause.stop();}
            if (fade_ambience_resume != null) {fade_ambience_resume.stop();}
            if (fade_ambience_stop != null) {fade_ambience_stop.stop();}
            if (timeline_progresstonextsessionpart != null) {timeline_progresstonextsessionpart.stop();}
            if (timeline_start_ending_ramp != null) {timeline_fadeout_timer.stop();}
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop();}
            volume_unbindentrainment();
            volume_unbindambience();
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
        String sessionpartname;
        if (thisession.getCurrentsessionpart() != null) {sessionpartname = thisession.getCurrentsessionpart().name;}
        else {sessionpartname = "Session";}
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
                statusbartext = sessionpartname + " Playing";
                break;
            case PAUSED:
                playbuttontext = "Resume";
                pausebuttontext = "Paused";
                stopbuttontext = "Stop";
                statusbartext = sessionpartname + " Paused";
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
                statusbartext = "Fading In To " + sessionpartname ;
                break;
            case FADING_RESUME:
                playbuttontext = "Resuming";
                pausebuttontext = "Resuming";
                stopbuttontext = "Resuming";
                statusbartext = "Resuming " + sessionpartname ;
                break;
            case FADING_PAUSE:
                playbuttontext = "Pausing";
                pausebuttontext = "Pausing";
                stopbuttontext = "Pausing";
                statusbartext = "Pausing " + sessionpartname;
                break;
            case FADING_STOP:
                playbuttontext = "Stopping";
                pausebuttontext = "Stopping";
                stopbuttontext = "Stopping";
                statusbartext = "Stopping " + sessionpartname;
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
        if (thisession.Root.AmbienceSwitch.isSelected()) {thisession.playerUI.AmbienceVolume.setDisable(! enabled);}
        if (thisession.player_isreferencecurrentlyDisplayed()) {
            thisession.displayReference.EntrainmentVolumeSlider.setDisable(! enabled);
            if (thisession.Root.AmbienceSwitch.isSelected()) {thisession.displayReference.AmbienceVolumeSlider.setDisable(! enabled);}
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
                thisession.Root.getSessions().getspecificsession(thisession.Root.getSessions().getSession().size() - 1).updatesessionpartduration(number, new Double(elapsedtime.toMinutes()).intValue());
                goals_updateduringplayback();
            } catch (NullPointerException ignored) {}
        }
    }
    // Error Handling
    protected void entrainmenterror() {
        System.out.println("Entrainment Error");
        // Pause Ambience If Exists
        switch (thisession.Root.dialog_getAnswer("Entrainment Playback Error", null, "An Error Occured While Playing " + name +
                        "'s Entrainment. Problem File Is: '" + entrainmentplayer.getMedia().getSource() + "'",
                "Retry Playback", "Mute Entrainment", "Stop Session Playback")) {
            case YES:
                entrainmentplayer.stop();
                entrainmentplayer.play();
                entrainmentplayer.setOnError(this::entrainmenterror);
                break;
            case CANCEL:
                thisession.player_error();
                break;
        }
    }
    protected void ambienceerror() {
        System.out.println("Ambience Error!");
        // Pause Entrainment
        switch (thisession.Root.dialog_getAnswer("Ambience Playback Error", null, "An Error Occured While Playing " + name +
                        "'s Ambience. Problem File Is: '" + ambienceplayer.getMedia().getSource() + "'",
                "Retry Playback", "Mute Ambience", "Stop Session Playback")) {
            case YES:
                ambienceplayer.stop();
                ambienceplayer.play();
                ambienceplayer.setOnError(this::ambienceerror);
                break;
            case NO:
                ambienceplayer.stop();
            case CANCEL:
                thisession.player_error();
                break;
        }
    }

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
    // Goals XML
    public void goals_unmarshall() {Goals = thisession.Root.getGoals().getSessionPartGoalList(number);}
    public void goals_marshall() {thisession.Root.getGoals().setSessionPartGoalList(number, Goals);}
    // List Methods
    public void goals_add(kujiin.xml.Goals.Goal newgoal) {
        if (Goals == null) {Goals = new ArrayList<>();}
        Goals.add(newgoal);
        goals_sort();
    }
    private void goals_sort() {
        List<kujiin.xml.Goals.Goal> goallist = Goals;
        if (goallist != null && ! goallist.isEmpty()) {
            try {
                goallist = kujiin.xml.Goals.sortgoalsbyHours(goallist);
                int count = 1;
                for (kujiin.xml.Goals.Goal i : goallist) {
                    i.setID(count);
                    count++;
                }
                Goals = goallist;
            } catch (Exception ignored) {}
        }
    }
    public void goals_remove(kujiin.xml.Goals.Goal currentgoal) {Goals.remove(currentgoal);}
    // Getters
    // Current Goal
    public kujiin.xml.Goals.Goal goals_getCurrent() {
        if (Goals == null || Goals.isEmpty()) {return null;}
        List<kujiin.xml.Goals.Goal> uncompletedgoals = goals_getAllCurrent();
        if (! uncompletedgoals.isEmpty()) {
            uncompletedgoals = kujiin.xml.Goals.sortgoalsbyHours(uncompletedgoals);
            return uncompletedgoals.get(0);
        } else {return null;}
    }
    public Duration goals_getCurrentDuration() {
        if (goals_ui_currentgoalisset()) {
            return Duration.hours(goals_getCurrent().getGoal_Hours());
        } else {
            return Duration.ZERO;
        }
    }
    // Current And Future Goals
    public List<kujiin.xml.Goals.Goal> goals_getAllCurrent() {
        return Goals.stream().filter(i -> i.getCompleted() != null && !i.getCompleted()).collect(Collectors.toList());
    }
    // Completed Goals
    public List<kujiin.xml.Goals.Goal> goals_getCompleted() {
        try {return Goals.stream().filter(i -> i.getCompleted() != null && i.getCompleted()).collect(Collectors.toList());}
        catch (NullPointerException e) {return new ArrayList<>();}
    }
    public int goals_getCompletedCount() {return goals_getCompleted().size();}
    public List<kujiin.xml.Goals.Goal> goals_getCompletedOn(LocalDate localDate) {
        try {
            List<kujiin.xml.Goals.Goal> goalslist = new ArrayList<>();
            for (kujiin.xml.Goals.Goal i : Goals) {
                if (i.getDate_Completed() == null || ! i.getCompleted()) {continue;}
                if (Util.convert_stringtolocaldate(i.getDate_Completed()).equals(localDate)) {goalslist.add(i);}
            }
            return goalslist;
        } catch (Exception ignored) {return new ArrayList<>();}
    }
    // Validation Methods
    public boolean goals_arelongenough() {
        try {
            Duration goalduration = Duration.hours(goals_getCurrent().getGoal_Hours());
            Duration practiceddurationplusthissession = thisession.Root.getSessions().gettotalpracticedtime(number, false).add(getduration());
            return goalduration.greaterThanOrEqualTo(practiceddurationplusthissession);
        } catch (NullPointerException e) {return true;}
    }
    // Playback Methods
    private void goals_updateduringplayback() {
        List<kujiin.xml.Goals.Goal> goallist = new ArrayList<>();
        for (kujiin.xml.Goals.Goal i : Goals) {
            if (i.getCompleted() != null && ! i.getCompleted() && sessions_getPracticedDuration(false).greaterThanOrEqualTo(Duration.hours(i.getGoal_Hours()))) {
                i.setCompleted(true);
                i.setDate_Completed(Util.gettodaysdate());
                goalscompletedthissession.add(i);
            }
            goallist.add(i);
        }
        Goals = goallist;
    }
    public void goals_transitioncheck() {
        if (goalscompletedthissession.size() > 0) {thisession.sessionpartswithGoalsCompletedThisSession.add(this);}
    }
    // UI
    public boolean goals_ui_currentgoalisset() {return goals_getCurrent() != null;}
    public double goals_ui_getcurrentgoalprogress() {
        if (goals_getCurrent() == null || goals_getCurrent().getGoal_Hours() == 0.0) {return 0.0;}
        Duration goalduration = Duration.hours(goals_getCurrent().getGoal_Hours());
        return sessions_getPracticedDuration(false).toMillis() / goalduration.toMillis();
    }
    public String goals_ui_getcurrentgoalpercentage() {
        int percentage = new Double(goals_ui_getcurrentgoalprogress() * 100).intValue();
        if (percentage > 0) {return String.valueOf(percentage) + "%";}
        else {return "";}
    }
    public String goals_ui_getcurrentgoalDuration(Double maxchars) {
        return Util.formatdurationtoStringSpelledOut(goals_getCurrentDuration(), maxchars);
    }

// Session Tracking
    // UI
    public String sessions_ui_getPracticedDuration() {
        return Util.formatdurationtoStringSpelledOut(sessions_getPracticedDuration(null), thisession.Root.TotalTimePracticed.getLayoutBounds().getWidth());
    }
    public String sessions_ui_getPracticedSessionCount() {
        return String.valueOf(sessions_getPracticedSessionCount(null));
    }
    public String sessions_ui_getAverageSessionLength() {
        return Util.formatdurationtoStringSpelledOut(sessions_getAverageSessionLength(null), thisession.Root.AverageSessionDuration.getLayoutBounds().getWidth());
    }
    // Utility
    public boolean sessions_includepreandpost() {
        return ! thisession.Root.PrePostSwitch.isDisabled() && thisession.Root.PrePostSwitch.isSelected();
    }
    public Duration sessions_getPracticedDuration(Boolean includepreandpostoverride) {
        boolean includepreandpost;
        if (includepreandpostoverride != null) {includepreandpost = includepreandpostoverride;}
        else {includepreandpost = sessions_includepreandpost();}
        return thisession.Root.getSessions().gettotalpracticedtime(number, includepreandpost);
    }
    public int sessions_getPracticedSessionCount(Boolean includepreandpostoverride) {
        boolean includepreandpost;
        if (includepreandpostoverride != null) {includepreandpost = includepreandpostoverride;}
        else {includepreandpost = sessions_includepreandpost();}
        return thisession.Root.getSessions().getsessioncount(number, includepreandpost);
    }
    public Duration sessions_getAverageSessionLength(Boolean includepreandpostoverride) {
        boolean includepreandpost;
        if (includepreandpostoverride != null) {includepreandpost = includepreandpostoverride;}
        else {includepreandpost = sessions_includepreandpost();}
        return thisession.Root.getSessions().getaveragepracticedurationforallsessions(number,  includepreandpost);
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

    enum FreqType {
        LOW, MEDIUM, HIGH
    }
}
