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
import kujiin.ui.MainController;
import kujiin.ui.dialogs.AnswerDialog;
import kujiin.util.enums.PlayerState;
import kujiin.util.enums.ReferenceType;
import kujiin.util.enums.StartupCheckType;
import kujiin.xml.*;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SessionPart {
// GUI Fields
    protected ToggleButton Switch;
    protected TextField Value;
    protected MainController root;
// Data Fields
    public int number;
    public String name;
    public Duration duration;
    protected Ambience ambience;
    protected Entrainment entrainment;
// Playback Fields
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
    protected StartupCheckType startupCheckType = StartupCheckType.ENTRAINMENT;
    public boolean ramponly;
    private Double currententrainmentvolume;
    private Double currentambiencevolume;
    private ArrayList<SoundFile> ambienceplayhistory;
    private SoundFile currentambiencesoundfile;
    public Duration elapsedtime;
    protected List<SessionPart> allsessionpartstoplay;
    protected List<kujiin.xml.Goals.Goal> goalscompletedthissession;
    protected Goals GoalsController;
    protected List<kujiin.xml.Goals.Goal> Goals;
    protected int startupchecks_entrainment_count;
    protected int startupchecks_ambience_count;
    protected boolean Ambience_hasAny = true;
    protected boolean Entrainment_Valid;

    public SessionPart() {}
    public SessionPart(int number, String name, MainController Root,  ToggleButton aSwitch, TextField value) {
        root = Root;
        this.number = number;
        this.name = name;
        this.duration = Duration.ZERO;
        if (this instanceof Cut || this instanceof Element || this instanceof Qi_Gong) {
            if (aSwitch != null && value != null) {
                Switch = aSwitch;
                Value = value;
                syncguielements();
                Value.textProperty().addListener((observable, oldValue, newValue) -> {
                    try {
                        changevalue(Integer.parseInt(Value.getText()));
                        root.getSessionCreator().updategui();
                    } catch (NumberFormatException ignored) {setDuration(0);}
                });
                Switch.setOnAction(event -> gui_toggleswitch());
                gui_toggleswitch();
            }
            entrainment = root.getEntrainments().getsessionpartEntrainment(this);
            ambience = root.getAmbiences().getsessionpartAmbience(this);
        }
        //        tempentrainmenttextfile = new File(Preferences.DIRECTORYTEMP, "txt/" + name + "Ent.txt");
//        tempentrainmentfile = new File(Preferences.DIRECTORYTEMP, "Entrainment/" + name + "Temp.mp3");
//        finalentrainmentfile = new File(Preferences.DIRECTORYTEMP, "Entrainment/" + name + ".mp3");
//        tempambiencetextfile = new File(Preferences.DIRECTORYTEMP, "txt/" + name + "Amb.txt");
//        tempambiencefile = new File(Preferences.DIRECTORYTEMP, "Ambience/" + name + "Temp.mp3");
//        finalambiencefile = new File(Preferences.DIRECTORYTEMP, "Ambience/" + name + ".mp3");
//        setFinalexportfile(new File(Preferences.DIRECTORYTEMP, name + ".mp3"));
    }

    public void syncguielements() {
        if (this instanceof Cut || this instanceof Element || this instanceof Qi_Gong) {
            Util.custom_textfield_integer(Value, Switch, 0, 600, root.getPreferences().getCreationOptions().getScrollincrement());
        }
    }

// Startup
    public StartupCheckType getStartupCheckType() {
        return startupCheckType;
    }
    public SoundFile startup_getNext() throws IndexOutOfBoundsException {
        if (startupCheckType == StartupCheckType.ENTRAINMENT) {
            try {return startup_getnextentrainment();} catch (IndexOutOfBoundsException ignored) {
                startupCheckType = StartupCheckType.AMBIENCE;
                return startup_getnextambience();
            }
        } else {return startup_getnextambience();}
    }
    // Entrainment
    public void startup_setEntrainmentSoundFile(SoundFile soundFile) {entrainment.set(soundFile);}
    public int startup_entrainmentpartcount() {return 0;}
    public SoundFile startup_getnextentrainment() throws IndexOutOfBoundsException {
        SoundFile soundFile;
        if (startupchecks_entrainment_count > startup_entrainmentpartcount() - 1) {throw new IndexOutOfBoundsException();}
        soundFile = entrainment.get(startupchecks_entrainment_count);
        File file;
        if (startupchecks_entrainment_count == 0) {file = new File(Preferences.DIRECTORYENTRAINMENT, getNameForFiles().toUpperCase() + ".mp3");}
        else {file = new File(Preferences.DIRECTORYENTRAINMENT, "ramp/" + getNameForFiles() + "to" + root.getSessionPart_Names(1, 10).get(startupchecks_entrainment_count - 1).toLowerCase() + ".mp3");}
        if (soundFile == null && file.exists()) {soundFile = new SoundFile(file);}
        return soundFile;
    }
    public void startup_incremententrainmentcount() {startupchecks_entrainment_count++;}
    public void startup_entrainmenttest() {
        System.out.println(name + "'s Entrainment:");
        System.out.println(" - Freq: " + entrainment.getFreq().toString());
        for (SoundFile i : entrainment.getRampfiles()) {
            System.out.println(" - Ramp: " + i.toString());
        }
    }
    // Ambience
    public void startup_setAmbienceSoundFile(SoundFile soundFile) {
        ambience.setoraddsoundfile(soundFile);
    }
    public SoundFile startup_getnextambience() throws IndexOutOfBoundsException {
        if (startupchecks_ambience_count == 0) {
            ambience.startup_addambiencefromdirectory(this);
            ambience.startup_checkfordeletedfiles();
        }
        if (! ambience.hasAnyAmbience()) {Ambience_hasAny = false; throw new IndexOutOfBoundsException();}
        SoundFile soundFile = ambience.get(startupchecks_ambience_count);
        return soundFile;
    }
    public void startup_incrementambiencecount() {startupchecks_ambience_count++;}
    public void startup_ambiencetest() {
        System.out.println(name + "'s Ambience:");
        if (ambience.hasAnyAmbience()) {
            for (SoundFile i : ambience.getAmbience()) {System.out.println(" - " + i.toString());}
        } else {System.out.println(name + " Has No Ambience");}
    }

// GUI
    public void setToolTip() {Switch.setTooltip(getTooltip());}
    public Tooltip getTooltip() {return new Tooltip("");}
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
        if (! disabled) {
            if (Integer.parseInt(Value.getText()) > 0) {Value.setDisable(false);}
            else {Value.setDisable(true);}
        } else {Value.setDisable(true);}
    }
    public int gui_getvalue() {return Integer.parseInt(Value.getText());}

// Getters And Setters
    public boolean getAmbience_hasAny() {
        return Ambience_hasAny;
    }
    public Duration getAmbience_TotalActualDuration() {return ambience.gettotalDuration();}
    public List<kujiin.xml.Goals.Goal> getGoals() {
        return Goals;
    }
    public List<kujiin.xml.Goals.Goal> getGoalscompletedthissession() {
        return goalscompletedthissession;
    }
    public String getNameForFiles() {return name.toLowerCase();}
    public String getNameForReference() {return name;}
    public String getNameForChart() {return name;}
    private void setDuration(double newduration) {
        duration = Duration.minutes(newduration);
    }
    public Ambience getAmbience() {
        return ambience;
    }
    public void setAmbience(Ambience ambience) {
        this.ambience = ambience;
        root.getAmbiences().setsessionpartAmbience(this, ambience);
    }
    public Entrainment getEntrainment() {return entrainment;}
    public void setEntrainment(Entrainment entrainment) {
        this.entrainment = entrainment;
        root.getEntrainments().setsessionpartEntrainment(this, entrainment);
    }
    public void setGoalsController(Goals goals) {
        GoalsController = goals;
    }
    public ArrayList<SoundFile> getAmbienceplayhistory() {
        return ambienceplayhistory;
    }

    // Duration
    public void setRamponly(boolean ramponly) {
        this.ramponly = ramponly;
    }
    public void setDuration(Duration duration) {this.duration = duration;}
    public Duration getduration() {return duration;}
    public String getdurationasString(boolean includeramp, double maxchars) {
        if ((duration == null || duration.equals(Duration.ZERO)) && ! ramponly) {return "No Duration Set";}
        else {
            if (duration.equals(Duration.ZERO) && includeramp && ramponly) {return "Ramp Only";}
            else {return Util.formatdurationtoStringSpelledOut(duration, maxchars);}
        }
    }
    public Duration getelapsedtime() {return elapsedtime;}

// Creation
    public boolean creation_build(List<SessionPart> sessionpartstoplay) {
        allsessionpartstoplay = sessionpartstoplay;
        if (root.
                getSessionCreator().
                isAmbienceenabled()) {return
                creation_buildEntrainment() &&
                        creation_buildAmbience();}
        else {return creation_buildEntrainment();}
    }
    protected boolean creation_buildEntrainment() {return ! duration.equals(Duration.ZERO) && entrainment.getFreq().isValid();}
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
        elapsedtime = Duration.ZERO;
        setupfadeanimations();
        volume_unbindentrainment();
        if (! ramponly || number == 15) {entrainmentplayer = new MediaPlayer(new Media(entrainment.getFreq().getFile().toURI().toString()));}
        else {entrainmentplayer = new MediaPlayer(new Media(entrainment.getRampfile().getFile().toURI().toString()));}
        entrainmentplayer.setVolume(0.0);
        if (! ramponly) {entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);}
        entrainmentplayer.setOnError(this::entrainmenterror);
        entrainmentplayer.play();
        timeline_progresstonextsessionpart = new Timeline(new KeyFrame(getduration(), ae -> root.getSessionCreator().getPlayer().progresstonextsessionpart()));
        timeline_progresstonextsessionpart.play();
        currententrainmentvolume = root.
                getSessionCreator().
                getPlayer().
                getCurrententrainmentvolume();
        boolean isLastSessionPart = allsessionpartstoplay.indexOf(this) == allsessionpartstoplay.size() - 1;
        if (! ramponly && ! isLastSessionPart && root.getPreferences().getSessionOptions().getRampenabled()) {
            timeline_start_ending_ramp = new Timeline(new KeyFrame(duration.subtract(Duration.millis(entrainment.getRampfile().getDuration())), ae -> {
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
            Duration startfadeout = duration;
            if (ramponly) {startfadeout = startfadeout.subtract(Duration.seconds(Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
            else {startfadeout = startfadeout.subtract(Duration.seconds(root.getPreferences().getPlaybackOptions().getAnimation_fade_stop_value()));}
            timeline_fadeout_timer = new Timeline(new KeyFrame(startfadeout, ae -> {
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
            root.getSessionCreator().setPlayerState(PlayerState.FADING_PLAY);
            fade_entrainment_play.play();
        } else {
            entrainmentplayer.setVolume(currententrainmentvolume);
            String percentage = new Double(currententrainmentvolume * 100).intValue() + "%";
            root.getSessionCreator().getPlayer().EntrainmentVolumePercentage.setText(percentage);
            root.getSessionCreator().setPlayerState(PlayerState.PLAYING);
            if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
                root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setValue(currententrainmentvolume);
                root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
            }
            volume_bindentrainment();
        }
        if (root.getSessionCreator().isAmbienceenabled() && root.getSessionCreator().getAmbiencePlaybackType() != null) {
            ambienceplayhistory = new ArrayList<>();
            currentambiencevolume = root.getSessionCreator().getPlayer().getCurrentambiencevolume();
            volume_unbindambience();
            currentambiencesoundfile = ambience.ambiencegenerator(root.getSessionCreator().getAmbiencePlaybackType(), ambienceplayhistory, currentambiencesoundfile);
            ambienceplayhistory.add(currentambiencesoundfile);
            ambienceplayer = new MediaPlayer(new Media(currentambiencesoundfile.getFile().toURI().toString()));
            ambienceplayer.setVolume(0.0);
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            ambienceplayer.play();
            if (fade_ambience_play != null) {fade_ambience_play.play();}
            else {
                ambienceplayer.setVolume(currentambiencevolume);
                String percentage = new Double(currentambiencevolume * 100).intValue() + "%";
                root.getSessionCreator().getPlayer().AmbienceVolumePercentage.setText(percentage);
                if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
                    root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setValue(currentambiencevolume);
                    root.getSessionCreator().getDisplayReference().AmbienceVolumePercentage.setText(percentage);
                }
                volume_bindambience();
            }
        }
        toggleplayerbuttons();
        root.getProgressTracker().sessionpart_forceselect(number);
        goalscompletedthissession = new ArrayList<>();
    }
    public void resume() {
        volume_unbindentrainment();
        entrainmentplayer.play();
        if (fade_entrainment_resume != null) {
            entrainmentplayer.setVolume(0.0);
            if (fade_entrainment_resume.getStatus() == Animation.Status.RUNNING) {return;}
            root.getSessionCreator().setPlayerState(PlayerState.FADING_RESUME);
            fade_entrainment_resume.play();
        } else {
            entrainmentplayer.setVolume(currententrainmentvolume);
            volume_bindentrainment();
            root.getSessionCreator().setPlayerState(PlayerState.PLAYING);
            timeline_progresstonextsessionpart.play();
            if (root.getPreferences().getSessionOptions().getRampenabled() && timeline_start_ending_ramp.getStatus() == Animation.Status.PAUSED) {
                timeline_start_ending_ramp.play();}
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.play();}
        }
        if (root.getSessionCreator().isAmbienceenabled()) {
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
            root.getSessionCreator().setPlayerState(PlayerState.FADING_PAUSE);
            fade_entrainment_pause.play();
            if (root.getSessionCreator().isAmbienceenabled()) {
                volume_unbindambience();
                fade_ambience_pause.play();
            }
            // Close Loading Dialog
        } else {pausewithoutanimation();}
        toggleplayerbuttons();
    }
    public void pausewithoutanimation() {
        root.getSessionCreator().setPlayerState(PlayerState.PAUSED);
        entrainmentplayer.pause();
        timeline_progresstonextsessionpart.pause();
        if (root.getPreferences().getSessionOptions().getRampenabled() && timeline_start_ending_ramp != null && timeline_start_ending_ramp.getStatus() == Animation.Status.RUNNING) {
            timeline_start_ending_ramp.pause();}
        if (timeline_fadeout_timer != null) {timeline_fadeout_timer.pause();}
        if (root.getSessionCreator().isAmbienceenabled()) {
            volume_unbindambience();
            ambienceplayer.pause();
        }
    }
    public void stop() {
        root.getSessionCreator().getPlayer().closereferencefile();
        volume_unbindentrainment();
        if (fade_entrainment_stop != null) {
            if (fade_entrainment_stop.getStatus() == Animation.Status.RUNNING) {return;}
            fade_entrainment_stop.play();
            root.getSessionCreator().setPlayerState(PlayerState.FADING_STOP);
            if (root.getSessionCreator().isAmbienceenabled()) {
                volume_unbindambience();
                fade_ambience_stop.play();
            }
        } else {
            root.getSessionCreator().setPlayerState(PlayerState.STOPPED);
            entrainmentplayer.stop();
            entrainmentplayer.dispose();
            timeline_progresstonextsessionpart.stop();
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop();}
            if (root.getSessionCreator().isAmbienceenabled()) {
                volume_unbindambience();
                ambienceplayer.stop();
                ambienceplayer.dispose();
            }
        }
        toggleplayerbuttons();
    }
    private void setupfadeanimations() {
        // PLAY
        if (ramponly || (root.getPreferences().getPlaybackOptions().getAnimation_fade_play_enabled() && root.getPreferences().getPlaybackOptions().getAnimation_fade_play_value() > 0.0)) {
            fade_entrainment_play = new Transition() {
                {if (ramponly) {setCycleDuration(Duration.seconds(Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
                 else {setCycleDuration(Duration.seconds(root.getPreferences().getPlaybackOptions().getAnimation_fade_play_value()));}}

                @Override
                protected void interpolate(double frac) {
                    if (entrainmentplayer != null) {
                        double entrainmentvolume = frac * currententrainmentvolume;
                        String percentage = new Double(entrainmentvolume * 100).intValue() + "%";
                        entrainmentplayer.setVolume(entrainmentvolume);
                       root.getSessionCreator().getPlayer().EntrainmentVolume.setValue(entrainmentvolume);
                       root.getSessionCreator().getPlayer().EntrainmentVolumePercentage.setText(percentage);
                        if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
                            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setValue(entrainmentvolume);
                            root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
                        }
                    }
                }
            };
            fade_entrainment_play.setOnFinished(event -> {
                root.getSessionCreator().setPlayerState(PlayerState.PLAYING);
                toggleplayerbuttons();
                volume_bindentrainment();
            });
            if (root.getSessionCreator().isAmbienceenabled()) {
                fade_ambience_play = new Transition() {
                    {if (ramponly) {setCycleDuration(Duration.seconds(Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
                    else {setCycleDuration(Duration.seconds(root.getPreferences().getPlaybackOptions().getAnimation_fade_play_value()));}}

                    @Override
                    protected void interpolate(double frac) {
                        if (ambienceplayer != null) {
                            double ambiencevolume = frac * currentambiencevolume;
                            String percentage = new Double(ambiencevolume * 100).intValue() + "%";
                            ambienceplayer.setVolume(ambiencevolume);
                           root.getSessionCreator().getPlayer().AmbienceVolume.setValue(ambiencevolume);
                           root.getSessionCreator().getPlayer().AmbienceVolumePercentage.setText(percentage);
                            if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
                                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setValue(ambiencevolume);
                                root.getSessionCreator().getDisplayReference().AmbienceVolumePercentage.setText(percentage);
                            }
                        }
                    }
                };
                fade_ambience_play.setOnFinished(event -> volume_bindambience());
            }
        }
        // RESUME
        if (root.getPreferences().getPlaybackOptions().getAnimation_fade_resume_enabled() && root.getPreferences().getPlaybackOptions().getAnimation_fade_resume_value() > 0.0) {
            fade_entrainment_resume = new Transition() {
                {setCycleDuration(Duration.seconds(root.getPreferences().getPlaybackOptions().getAnimation_fade_resume_value()));}

                @Override
                protected void interpolate(double frac) {
                    if (entrainmentplayer != null) {
                        double entrainmentvolume = frac * currententrainmentvolume;
                        String percentage = new Double(entrainmentvolume * 100).intValue() + "%";
                        entrainmentplayer.setVolume(entrainmentvolume);
                       root.getSessionCreator().getPlayer().EntrainmentVolume.setValue(entrainmentvolume);
                       root.getSessionCreator().getPlayer().EntrainmentVolumePercentage.setText(percentage);
                        if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
                            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setValue(entrainmentvolume);
                            root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
                        }
                    }
                }
            };
            fade_entrainment_resume.setOnFinished(event -> {
                root.getSessionCreator().setPlayerState(PlayerState.PLAYING);
                timeline_progresstonextsessionpart.play();
                if (root.getPreferences().getSessionOptions().getRampenabled() && (timeline_start_ending_ramp != null && timeline_start_ending_ramp.getStatus() == Animation.Status.PAUSED)) {
                    timeline_start_ending_ramp.play();
                }
                if (timeline_fadeout_timer != null) {
                    timeline_fadeout_timer.play();
                }
                toggleplayerbuttons();
                volume_bindentrainment();
            });
            if (root.getSessionCreator().isAmbienceenabled()) {
                fade_ambience_resume = new Transition() {
                    {setCycleDuration(Duration.seconds(root.getPreferences().getPlaybackOptions().getAnimation_fade_resume_value()));}

                    @Override
                    protected void interpolate(double frac) {
                        if (ambienceplayer != null) {
                            double ambiencevolume = frac * currentambiencevolume;
                            String percentage = new Double(ambiencevolume * 100).intValue() + "%";
                            ambienceplayer.setVolume(ambiencevolume);
                           root.getSessionCreator().getPlayer().AmbienceVolume.setValue(ambiencevolume);
                           root.getSessionCreator().getPlayer().AmbienceVolumePercentage.setText(percentage);
                            if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
                                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setValue(ambiencevolume);
                                root.getSessionCreator().getDisplayReference().AmbienceVolumePercentage.setText(percentage);
                            }
                        }
                    }
                };
                fade_ambience_resume.setOnFinished(event -> volume_bindambience());
            }
        }
        // PAUSE
        if (root.getPreferences().getPlaybackOptions().getAnimation_fade_pause_enabled() && root.getPreferences().getPlaybackOptions().getAnimation_fade_pause_value() > 0.0) {
            fade_entrainment_pause = new Transition() {
                {setCycleDuration(Duration.seconds(root.getPreferences().getPlaybackOptions().getAnimation_fade_pause_value()));}

                @Override
                protected void interpolate(double frac) {
                    if (entrainmentplayer != null) {
                        double fadeoutvolume = currententrainmentvolume - (frac * currententrainmentvolume);
                        String percentage = new Double(fadeoutvolume * 100).intValue() + "%";
                        entrainmentplayer.setVolume(fadeoutvolume);
                        root.getSessionCreator().getPlayer().EntrainmentVolume.setValue(fadeoutvolume);
                        root.getSessionCreator().getPlayer().EntrainmentVolumePercentage.setText(percentage);
                        if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
                            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setValue(fadeoutvolume);
                            root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
                        }
                    }
                }
            };
            fade_entrainment_pause.setOnFinished(event -> {
                entrainmentplayer.pause();
                timeline_progresstonextsessionpart.pause();
                root.getSessionCreator().getPlayer().getPlayer_updateuitimeline().pause();
                if (root.getPreferences().getSessionOptions().getRampenabled() && (timeline_start_ending_ramp != null && timeline_start_ending_ramp.getStatus() == Animation.Status.RUNNING)) {timeline_start_ending_ramp.pause();}
                if (timeline_fadeout_timer != null) {timeline_fadeout_timer.pause();}
                root.getSessionCreator().setPlayerState(PlayerState.PAUSED);
                toggleplayerbuttons();
            });
            if (root.getSessionCreator().isAmbienceenabled()) {
                fade_ambience_pause = new Transition() {
                    {
                        setCycleDuration(Duration.seconds(root.getPreferences().getPlaybackOptions().getAnimation_fade_pause_value()));
                    }

                    @Override
                    protected void interpolate(double frac) {
                        if (ambienceplayer != null) {
                            double fadeoutvolume = currentambiencevolume - (frac * currentambiencevolume);
                            String percentage = new Double(fadeoutvolume * 100).intValue() + "%";
                            ambienceplayer.setVolume(fadeoutvolume);
                           root.getSessionCreator().getPlayer().AmbienceVolume.setValue(fadeoutvolume);
                           root.getSessionCreator().getPlayer().AmbienceVolumePercentage.setText(percentage);
                            if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
                                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setValue(fadeoutvolume);
                                root.getSessionCreator().getDisplayReference().AmbienceVolumePercentage.setText(percentage);
                            }
                        }
                    }
                };
                fade_ambience_pause.setOnFinished(event -> ambienceplayer.pause());
            }
        }
        // STOP
        if (ramponly || (root.getPreferences().getPlaybackOptions().getAnimation_fade_stop_enabled() && root.getPreferences().getPlaybackOptions().getAnimation_fade_stop_value() > 0.0)) {
            fade_entrainment_stop = new Transition() {
                {if (ramponly) {setCycleDuration(Duration.seconds(Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
                else {setCycleDuration(Duration.seconds(root.getPreferences().getPlaybackOptions().getAnimation_fade_stop_value()));}}

                @Override
                protected void interpolate(double frac) {
                    if (entrainmentplayer != null) {
                        double fadeoutvolume = currententrainmentvolume - (frac * currententrainmentvolume);
                        String percentage = new Double(fadeoutvolume * 100).intValue() + "%";
                        entrainmentplayer.setVolume(fadeoutvolume);
                       root.getSessionCreator().getPlayer().EntrainmentVolume.setValue(fadeoutvolume);
                       root.getSessionCreator().getPlayer().EntrainmentVolumePercentage.setText(percentage);
                        if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
                            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setValue(fadeoutvolume);
                            root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
                        }
                    }
                }
            };
            fade_entrainment_stop.setOnFinished(event -> {
                entrainmentplayer.stop();
                entrainmentplayer.dispose();
                if (root.getPreferences().getSessionOptions().getRampenabled() && timeline_start_ending_ramp != null && timeline_start_ending_ramp.getStatus() == Animation.Status.RUNNING) {timeline_start_ending_ramp.stop();}
                root.getSessionCreator().getPlayer().getPlayer_updateuitimeline().stop();
                timeline_progresstonextsessionpart.stop();
                if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop();}
                root.getSessionCreator().setPlayerState(PlayerState.STOPPED);
                toggleplayerbuttons();
            });
            if (root.getSessionCreator().isAmbienceenabled()) {
                fade_ambience_stop = new Transition() {
                    {if (ramponly) {setCycleDuration(Duration.seconds(Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
                    else {setCycleDuration(Duration.seconds(root.getPreferences().getPlaybackOptions().getAnimation_fade_stop_value()));}}

                    @Override
                    protected void interpolate(double frac) {
                        if (ambienceplayer != null) {
                            double fadeoutvolume = currentambiencevolume - (frac * currentambiencevolume);
                            String percentage = new Double(fadeoutvolume * 100).intValue() + "%";
                            ambienceplayer.setVolume(fadeoutvolume);
                           root.getSessionCreator().getPlayer().AmbienceVolume.setValue(fadeoutvolume);
                           root.getSessionCreator().getPlayer().AmbienceVolumePercentage.setText(percentage);
                            if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
                                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setValue(fadeoutvolume);
                                root.getSessionCreator().getDisplayReference().AmbienceVolumePercentage.setText(percentage);
                            }
                        }
                    }
                };
                fade_ambience_stop.setOnFinished(event -> {
                    ambienceplayer.stop();
                    ambienceplayer.dispose();
                });
            }
        }
    }
    private void playnextentrainment() {
        try {
            volume_unbindentrainment();
            entrainmentplayer.dispose();
            entrainmentplayer = null;
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
            ambienceplayer.dispose();
            ambienceplayer = null;
            currentambiencesoundfile = ambience.ambiencegenerator(root.getSessionCreator().getAmbiencePlaybackType(), ambienceplayhistory, currentambiencesoundfile);
            ambienceplayhistory.add(currentambiencesoundfile);
            ambienceplayer = new MediaPlayer(new Media(currentambiencesoundfile.getFile().toURI().toString()));
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            ambienceplayer.setVolume(currentambiencevolume);
            ambienceplayer.play();
            ambienceplayer.setOnPlaying(this::volume_bindambience);
        } catch (IndexOutOfBoundsException ignored) {ambienceplayer.dispose();}
    }
    public void cleanupPlayersandAnimations() {
        try {
            volume_unbindentrainment();
            volume_unbindambience();
            if (entrainmentplayer != null) {entrainmentplayer.stop(); entrainmentplayer.dispose(); entrainmentplayer = null;}
            if (ambienceplayer != null) {ambienceplayer.stop(); ambienceplayer.dispose(); ambienceplayer = null;}
            if (fade_entrainment_play != null) {fade_entrainment_play.stop(); fade_ambience_play = null;}
            if (fade_entrainment_pause != null) {fade_entrainment_pause.stop(); fade_entrainment_pause = null;}
            if (fade_entrainment_resume != null) {fade_entrainment_resume.stop(); fade_entrainment_resume = null;}
            if (fade_entrainment_stop != null) {fade_entrainment_stop.stop(); fade_entrainment_stop = null;}
            if (fade_ambience_play != null) {fade_ambience_play.stop(); fade_ambience_play = null;}
            if (fade_ambience_pause != null) {fade_ambience_pause.stop(); fade_ambience_pause = null;}
            if (fade_ambience_resume != null) {fade_ambience_resume.stop(); fade_ambience_resume = null;}
            if (fade_ambience_stop != null) {fade_ambience_stop.stop(); fade_ambience_stop = null;}
            if (timeline_progresstonextsessionpart != null) {timeline_progresstonextsessionpart.stop(); timeline_progresstonextsessionpart = null;}
            if (timeline_start_ending_ramp != null) {timeline_fadeout_timer.stop(); timeline_start_ending_ramp = null;}
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop(); timeline_fadeout_timer = null;}
            toggleplayerbuttons();
            System.out.println(name + "'s Entrainment Player Status: " + entrainmentplayer.getStatus());
            System.out.println(name + "'s Ambience Player Status: " + ambienceplayer.getStatus());
        } catch (Exception ignored) {}
    }
    public void toggleplayerbuttons() {
        if (root.getSessionCreator().getPlayerState() == null) {return;}
        boolean idle = root.getSessionCreator().getPlayerState() == PlayerState.IDLE;
        boolean playing = root.getSessionCreator().getPlayerState() == PlayerState.PLAYING;
        boolean paused = root.getSessionCreator().getPlayerState() == PlayerState.PAUSED;
        boolean stopped = root.getSessionCreator().getPlayerState() == PlayerState.STOPPED;
        boolean fade_play = root.getSessionCreator().getPlayerState() == PlayerState.FADING_PLAY;
        boolean fade_resume = root.getSessionCreator().getPlayerState() == PlayerState.FADING_RESUME;
        boolean fade_pause = root.getSessionCreator().getPlayerState() == PlayerState.FADING_PAUSE;
        boolean fade_stop = root.getSessionCreator().getPlayerState() == PlayerState.FADING_STOP;
       root.getSessionCreator().getPlayer().PlayButton.setDisable(playing || fade_play || fade_resume || fade_pause || fade_stop);
       root.getSessionCreator().getPlayer().PauseButton.setDisable(paused || fade_play || fade_resume || fade_pause || fade_stop || idle);
       root.getSessionCreator().getPlayer().StopButton.setDisable(stopped || fade_play || fade_resume || fade_pause || fade_stop || idle);
       root.getSessionCreator().getPlayer().ReferenceCheckBox.setDisable(fade_play || fade_resume || fade_pause || fade_stop);
        if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
            root.getSessionCreator().getDisplayReference().PlayButton.setDisable(playing || fade_play || fade_resume || fade_pause || fade_stop);
            root.getSessionCreator().getDisplayReference().PauseButton.setDisable(paused || fade_play || fade_resume || fade_pause || fade_stop || idle);
            root.getSessionCreator().getDisplayReference().StopButton.setDisable(stopped || fade_play || fade_resume || fade_pause || fade_stop || idle);
        }
        String playbuttontext;
        String pausebuttontext;
        String stopbuttontext;
        switch (root.getSessionCreator().getPlayerState()) {
            case IDLE:
                playbuttontext = "Start";
                pausebuttontext = "Pause";
                stopbuttontext = "Stop";
                break;
            case PLAYING:
                playbuttontext = "Playing";
                pausebuttontext = "Pause";
                stopbuttontext = "Stop";
                break;
            case PAUSED:
                playbuttontext = "Resume";
                pausebuttontext = "Paused";
                stopbuttontext = "Stop";
                break;
            case STOPPED:
                playbuttontext = "Play";
                pausebuttontext = "Stopped";
                stopbuttontext = "Stopped";
                break;
            case TRANSITIONING:
                playbuttontext = "Transitioning";
                pausebuttontext = "Transitioning";
                stopbuttontext = "Transitioning";
                break;
            case FADING_PLAY:
                playbuttontext = "Starting";
                pausebuttontext = "Starting";
                stopbuttontext = "Starting";
                break;
            case FADING_RESUME:
                playbuttontext = "Resuming";
                pausebuttontext = "Resuming";
                stopbuttontext = "Resuming";
                break;
            case FADING_PAUSE:
                playbuttontext = "Pausing";
                pausebuttontext = "Pausing";
                stopbuttontext = "Pausing";
                break;
            case FADING_STOP:
                playbuttontext = "Stopping";
                pausebuttontext = "Stopping";
                stopbuttontext = "Stopping";
                break;
            default:
                playbuttontext = "";
                pausebuttontext = "";
                stopbuttontext = "";
                break;
        }
       root.getSessionCreator().getPlayer().PlayButton.setText(playbuttontext);
       root.getSessionCreator().getPlayer().PauseButton.setText(pausebuttontext);
       root.getSessionCreator().getPlayer().StopButton.setText(stopbuttontext);
        if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
            root.getSessionCreator().getDisplayReference().PlayButton.setText(playbuttontext);
            root.getSessionCreator().getDisplayReference().PauseButton.setText(pausebuttontext);
            root.getSessionCreator().getDisplayReference().StopButton.setText(stopbuttontext);
        }
        toggleplayervolumecontrols();
    }
    public void toggleplayervolumecontrols() {
        boolean enabled = root.getSessionCreator().getPlayerState() == PlayerState.PLAYING;
       root.getSessionCreator().getPlayer().EntrainmentVolume.setDisable(! enabled);
        if (root.getSessionCreator().isAmbienceenabled()) {root.getSessionCreator().getPlayer().AmbienceVolume.setDisable(! enabled);}
        if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setDisable(! enabled);
            if (root.getSessionCreator().isAmbienceenabled()) {root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setDisable(! enabled);}
        }
    }
    public void volume_bindentrainment() {
       root.getSessionCreator().getPlayer().EntrainmentVolume.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
       root.getSessionCreator().getPlayer().EntrainmentVolume.setDisable(false);
       root.getSessionCreator().getPlayer().EntrainmentVolume.setOnMouseDragged(event1 -> {
            String percentage = new Double(root.getSessionCreator().getPlayer().EntrainmentVolume.getValue() * 100).intValue() + "%";
            currententrainmentvolume =root.getSessionCreator().getPlayer().EntrainmentVolume.getValue();
            root.getSessionCreator().getPlayer().setCurrententrainmentvolume(currententrainmentvolume);
           root.getSessionCreator().getPlayer().EntrainmentVolumePercentage.setText(percentage);
            if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
                root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.valueProperty().unbindBidirectional(entrainmentplayer.volumeProperty());
                root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setValue(currententrainmentvolume);
                root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
                root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
            }
        });
       root.getSessionCreator().getPlayer().EntrainmentVolume.setOnScroll(event -> {
            Double newvalue =root.getSessionCreator().getPlayer().EntrainmentVolume.getValue();
            if (event.getDeltaY() < 0) {newvalue -= Preferences.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
            else {newvalue += Preferences.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
            if (newvalue <= 1.0 && newvalue >= 0.0) {
                Double roundedvalue = Util.round_nearestmultipleof5(newvalue * 100);
                String percentage = roundedvalue.intValue() + "%";
                currententrainmentvolume = roundedvalue / 100;
                root.getSessionCreator().getPlayer().setCurrententrainmentvolume(roundedvalue / 100);
               root.getSessionCreator().getPlayer().EntrainmentVolume.setValue(roundedvalue / 100);
               root.getSessionCreator().getPlayer().EntrainmentVolume.setTooltip(new Tooltip(percentage));
               root.getSessionCreator().getPlayer().EntrainmentVolumePercentage.setText(percentage);
                if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
                    root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.valueProperty().unbindBidirectional(entrainmentplayer.volumeProperty());
                    root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setValue(currententrainmentvolume);
                    root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
                    root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
                }
            }
        });
        if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setDisable(false);
            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setOnMouseDragged(event1 -> {
                String percentage = new Double(root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.getValue() * 100).intValue() + "%";
                currententrainmentvolume =root.getSessionCreator().getPlayer().EntrainmentVolume.getValue();
                root.getSessionCreator().getPlayer().setCurrententrainmentvolume(currententrainmentvolume);
                root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
               root.getSessionCreator().getPlayer().EntrainmentVolume.valueProperty().unbindBidirectional(entrainmentplayer.volumeProperty());
               root.getSessionCreator().getPlayer().EntrainmentVolume.setValue(currententrainmentvolume);
               root.getSessionCreator().getPlayer().EntrainmentVolume.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
               root.getSessionCreator().getPlayer().EntrainmentVolumePercentage.setText(percentage);
            });
            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setOnScroll(event -> {
                Double newvalue = root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.getValue();
                if (event.getDeltaY() < 0) {newvalue -= Preferences.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
                else {newvalue += Preferences.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
                if (newvalue <= 1.0 && newvalue >= 0.0) {
                    Double roundedvalue = Util.round_nearestmultipleof5(newvalue * 100);
                    String percentage = roundedvalue.intValue() + "%";
                    currententrainmentvolume = roundedvalue / 100;
                    root.getSessionCreator().getPlayer().setCurrententrainmentvolume(currententrainmentvolume);
                    root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.valueProperty().unbindBidirectional(entrainmentplayer.volumeProperty());
                    root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setValue(currententrainmentvolume);
                    root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
                    root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
                   root.getSessionCreator().getPlayer().EntrainmentVolume.valueProperty().unbindBidirectional(entrainmentplayer.volumeProperty());
                   root.getSessionCreator().getPlayer().EntrainmentVolume.setValue(currententrainmentvolume);
                   root.getSessionCreator().getPlayer().EntrainmentVolume.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
                   root.getSessionCreator().getPlayer().EntrainmentVolumePercentage.setText(percentage);
                }
            });
        }
    }
    public void volume_unbindentrainment() {
        try {
           root.getSessionCreator().getPlayer().EntrainmentVolume.valueProperty().unbindBidirectional(entrainmentplayer.volumeProperty());
           root.getSessionCreator().getPlayer().EntrainmentVolume.setDisable(true);
            if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
                root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.valueProperty().unbindBidirectional(entrainmentplayer.volumeProperty());
                root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setDisable(true);
            }
        } catch (NullPointerException ignored) {}
    }
    public void volume_bindambience() {
       root.getSessionCreator().getPlayer().AmbienceVolume.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
       root.getSessionCreator().getPlayer().AmbienceVolume.setDisable(false);
       root.getSessionCreator().getPlayer().AmbienceVolume.setOnMouseDragged(event1 -> {
            String percentage = new Double(root.getSessionCreator().getPlayer().AmbienceVolume.getValue() * 100).intValue() + "%";
            currentambiencevolume =root.getSessionCreator().getPlayer().AmbienceVolume.getValue();
            root.getSessionCreator().getPlayer().setCurrentambiencevolume(currentambiencevolume);
           root.getSessionCreator().getPlayer().AmbienceVolumePercentage.setText(percentage);
           root.getSessionCreator().getPlayer().AmbienceVolume.setTooltip(new Tooltip(percentage));
            if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.valueProperty().unbindBidirectional(ambienceplayer.volumeProperty());
                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setValue(currentambiencevolume);
                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
                root.getSessionCreator().getDisplayReference().AmbienceVolumePercentage.setText(percentage);
            }
        });
       root.getSessionCreator().getPlayer().AmbienceVolume.setOnScroll(event -> {
            Double newvalue =root.getSessionCreator().getPlayer().AmbienceVolume.getValue();
            if (event.getDeltaY() < 0) {newvalue -= Preferences.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
            else {newvalue += Preferences.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
            if (newvalue <= 1.0 && newvalue >= 0.0) {
                Double roundedvalue = Util.round_nearestmultipleof5(newvalue * 100);
                String percentage = roundedvalue.intValue() + "%";
                currentambiencevolume = roundedvalue / 100;
                root.getSessionCreator().getPlayer().setCurrentambiencevolume(roundedvalue / 100);
               root.getSessionCreator().getPlayer().AmbienceVolume.setValue(roundedvalue / 100);
               root.getSessionCreator().getPlayer().AmbienceVolume.setTooltip(new Tooltip(percentage));
               root.getSessionCreator().getPlayer().AmbienceVolumePercentage.setText(percentage);
                if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
                    root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.valueProperty().unbindBidirectional(ambienceplayer.volumeProperty());
                    root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setValue(currentambiencevolume);
                    root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
                    root.getSessionCreator().getDisplayReference().AmbienceVolumePercentage.setText(percentage);
                }
            }
        });
        if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
            root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
            root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setDisable(false);
            root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setOnMouseDragged(event1 -> {
                String percentage = new Double(root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.getValue() * 100).intValue() + "%";
                currentambiencevolume =root.getSessionCreator().getPlayer().AmbienceVolume.getValue();
                root.getSessionCreator().getPlayer().setCurrentambiencevolume(currentambiencevolume);
                root.getSessionCreator().getDisplayReference().AmbienceVolumePercentage.setText(percentage);
               root.getSessionCreator().getPlayer().AmbienceVolume.valueProperty().unbindBidirectional(ambienceplayer.volumeProperty());
               root.getSessionCreator().getPlayer().AmbienceVolume.setValue(currentambiencevolume);
               root.getSessionCreator().getPlayer().AmbienceVolume.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
               root.getSessionCreator().getPlayer().AmbienceVolumePercentage.setText(percentage);
            });
            root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setOnScroll(event -> {
                Double newvalue = root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.getValue();
                if (event.getDeltaY() < 0) {newvalue -= Preferences.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
                else {newvalue += Preferences.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
                if (newvalue <= 1.0 && newvalue >= 0.0) {
                    Double roundedvalue = Util.round_nearestmultipleof5(newvalue * 100);
                    String percentage = roundedvalue.intValue() + "%";
                    currentambiencevolume = roundedvalue / 100;
                    root.getSessionCreator().getPlayer().setCurrentambiencevolume(roundedvalue / 100);
                    root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.valueProperty().unbindBidirectional(ambienceplayer.volumeProperty());
                    root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setValue(roundedvalue / 100);
                    root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
                    root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setTooltip(new Tooltip(percentage));
                    root.getSessionCreator().getDisplayReference().AmbienceVolumePercentage.setText(percentage);
                   root.getSessionCreator().getPlayer().AmbienceVolume.valueProperty().unbindBidirectional(ambienceplayer.volumeProperty());
                   root.getSessionCreator().getPlayer().AmbienceVolume.setValue(currentambiencevolume);
                   root.getSessionCreator().getPlayer().AmbienceVolume.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
                   root.getSessionCreator().getPlayer().AmbienceVolumePercentage.setText(percentage);
                }
            });
        }
    }
    public void volume_unbindambience() {
        try {
           root.getSessionCreator().getPlayer().AmbienceVolume.valueProperty().unbindBidirectional(ambienceplayer.volumeProperty());
           root.getSessionCreator().getPlayer().AmbienceVolume.setDisable(true);
            if (root.getSessionCreator().getPlayer().referencecurrentlyDisplayed()) {
                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.valueProperty().unbindBidirectional(ambienceplayer.volumeProperty());
                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setDisable(true);
            }
        } catch (NullPointerException ignored) {}
    }
    public void volume_rebindambience() {volume_unbindambience(); volume_bindambience();}
    public void volume_rebindentrainment() {volume_unbindentrainment(); volume_bindentrainment();}
    public void tick() {
        if (entrainmentplayer.getStatus() == MediaPlayer.Status.PLAYING) {
            try {
                root.getProgressTracker().getSessions().getspecificsession(root.getProgressTracker().getSessions().getSession().size() - 1).updateduration(this, elapsedtime);
                goals_playbackupdate();
            } catch (NullPointerException ignored) {}
        }
    }
    // Error Handling
    protected void entrainmenterror() {
        System.out.println("Entrainment Error");
        // Pause Ambience If Exists
        switch (new AnswerDialog(root.getPreferences(), "Entrainment Playback Error", null, "An Error Occured While Playing " + name +
                        "'s Entrainment. Problem File Is: '" + entrainmentplayer.getMedia().getSource() + "'",
                "Retry Playback", "Mute Entrainment", "Stop Session Playback").getResult()) {
            case YES:
                entrainmentplayer.stop();
                entrainmentplayer.play();
                entrainmentplayer.setOnError(this::entrainmenterror);
                break;
            case CANCEL:
//                root.getSessionCreator().getPlayer().player_error();
                break;
        }
    }
    protected void ambienceerror() {
        System.out.println("Ambience Error!");
        // Pause Entrainment
        switch (new AnswerDialog(root.getPreferences(), "Ambience Playback Error", null, "An Error Occured While Playing " + name +
                        "'s Ambience. Problem File Is: '" + ambienceplayer.getMedia().getSource() + "'",
                "Retry Playback", "Mute Ambience", "Stop Session Playback").getResult()) {
            case YES:
                ambienceplayer.stop();
                ambienceplayer.play();
                ambienceplayer.setOnError(this::ambienceerror);
                break;
            case NO:
                ambienceplayer.stop();
            case CANCEL:
//                root.getSessionCreator().getPlayer().player_error();
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
//        File entrainmentfile = new File(Preferences.DIRECTORYTEMP, "Entrainment/" + name + ".mp3");
//        cutisgood = entrainmentfile.exists();
//        if (ambienceenabled) {
//            File ambiencefile = new File(Preferences.DIRECTORYTEMP, "Ambience/" + name + ".mp3");
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
    // XML
    public void goals_unmarshall() {Goals = root.getProgressTracker().getGoals().getSessionPartGoalList(number);}
    public void goals_marshall() {root.getProgressTracker().getGoals().setSessionPartGoalList(number, Goals);}
    // List Methods
    public kujiin.xml.Goals.Goal goals_getCurrent() {
        if (Goals == null || Goals.isEmpty() || goals_get(true, false).isEmpty()) {return null;}
        return goals_get(true, false).get(0);
    }
    public String goals_getCurrentAsString(boolean includepercentage, double maxchars) {
        kujiin.xml.Goals.Goal currentgoal = goals_getCurrent();
        if (currentgoal == null || currentgoal.getDuration().lessThanOrEqualTo(Duration.ZERO)) {return "No Goal Set";}
        else {
            Duration goal = currentgoal.getDuration();
            if (includepercentage) {
                StringBuilder text = new StringBuilder();
                Duration practiced = sessions_getPracticedDuration(false);
                try {
                    int percentage = new Double((practiced.toHours() / goal.toHours()) * 100).intValue();
                    String percentagetext =  " (" + percentage + "%)";
                    text.append(Util.formatdurationtoStringSpelledOut(goal, maxchars - percentagetext.length()));
                    text.append(percentagetext);
                } catch (ArithmeticException ignored) {text.append(Util.formatdurationtoStringSpelledOut(goal, maxchars));}
                return text.toString();
            } else {return Util.formatdurationtoStringSpelledOut(goal, maxchars);}
        }
    }
    public List<kujiin.xml.Goals.Goal> goals_get(boolean includecurrent, boolean includecompleted) {
        List<kujiin.xml.Goals.Goal> goals = new ArrayList<>();
        for (kujiin.xml.Goals.Goal i : Goals) {
            if (includecurrent && includecompleted) {goals.add(i);}
            else if (includecompleted) {if (! i.getCompleted()) goals.add(i);}
            else {
                if (i.getCompleted())
                goals.add(i);
            }
        }
        return goals;
    }
    public List<kujiin.xml.Goals.Goal> goals_get(boolean includecurrent, boolean includecompleted, LocalDate practicedorcompletedondate) {
        List<kujiin.xml.Goals.Goal> goals = new ArrayList<>();
        for (kujiin.xml.Goals.Goal i : Goals) {
            boolean completedonselecteddate = i.getDate_Completed().isEqual(practicedorcompletedondate);
            if (includecurrent && includecompleted && completedonselecteddate) {goals.add(i);}
            else if (includecompleted && completedonselecteddate) {if (! i.getCompleted()) goals.add(i);}
            else {if (i.getCompleted() && completedonselecteddate) goals.add(i);}
        }
        return goals;
    }
    public void goals_add(kujiin.xml.Goals.Goal newgoal) {
        if (Goals == null) {Goals = new ArrayList<>();}
        Goals.add(newgoal);
        goals_sort();
    }
    public void goals_add(List<kujiin.xml.Goals.Goal> newgoals) {
        if (Goals == null) {Goals = new ArrayList<>();}
        Goals.addAll(newgoals);
        goals_sort();
    }
    public void goals_sort() {
        List<kujiin.xml.Goals.Goal> goallist = Goals;
        if (goallist != null && ! goallist.isEmpty()) {
            try {
                goallist = kujiin.xml.Goals.sortgoalsbyDuration(goallist);
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
    // Playback
    private void goals_playbackupdate() {
        List<kujiin.xml.Goals.Goal> goallist = new ArrayList<>();
        for (kujiin.xml.Goals.Goal i : Goals) {
            if (i.getCompleted() != null && ! i.getCompleted() && sessions_getPracticedDuration(false).greaterThanOrEqualTo(i.getDuration())) {
                i.setCompleted(true);
                i.setDate_Completed(LocalDate.now());
                goalscompletedthissession.add(i);
            }
            goallist.add(i);
        }
        Goals = goallist;
    }
    public void goals_transitioncheck() {
        if (goalscompletedthissession.size() > 0) {root.getSessionCreator().getPlayer().sessionpartswithGoalsCompletedThisSession.add(this);}
    }
    // UI
    public boolean goals_ui_hascurrentgoal() {return goals_getCurrent() != null;}
    public double goals_ui_getcurrentgoalprogress() {
        if (goals_getCurrent() == null || goals_getCurrent().getDuration().lessThanOrEqualTo(Duration.ZERO)) {return 0.0;}
        Duration goalduration = goals_getCurrent().getDuration();
        return sessions_getPracticedDuration(false).toMillis() / goalduration.toMillis();
    }
    public String goals_ui_getcurrentgoalpercentage(int decimalplaces) {
        Double rawpercentage = goals_ui_getcurrentgoalprogress();
        String percentage;
        if (decimalplaces == 0) {percentage =  String.valueOf(rawpercentage.intValue() * 100);}
        else {percentage = String.valueOf(Util.rounddouble(goals_ui_getcurrentgoalprogress() * 100, decimalplaces));}
        if (rawpercentage > 0.0) {return String.valueOf(percentage) + "%";}
        else {return "";}
    }
    public String goals_ui_getcurrentgoalDuration(Double maxchars) {
        if (goals_getCurrent() != null) {
            return Util.formatdurationtoStringSpelledOut(goals_getCurrent().getDuration(), maxchars);
        } else {return null;}
    }
    // Validation
    public boolean goals_arelongenough() {
        try {
            Duration goalduration = goals_getCurrent().getDuration();
            Duration practiceddurationplusthissession = root.getProgressTracker().getSessions().gettotalpracticedtime(this, false).add(getduration());
            return goalduration.greaterThanOrEqualTo(practiceddurationplusthissession);
        } catch (NullPointerException e) {return true;}
    }

// Session Tracking
    // UI
    public String sessions_ui_getPracticedDuration() {
        return Util.formatdurationtoStringSpelledOut(sessions_getPracticedDuration(null), root.TotalTimePracticed.getLayoutBounds().getWidth());
    }
    public String sessions_ui_getPracticedSessionCount() {
        return String.valueOf(sessions_getPracticedSessionCount(null));
    }
    public String sessions_ui_getAverageSessionLength() {
        return Util.formatdurationtoStringSpelledOut(sessions_getAverageSessionLength(null), root.AverageSessionDuration.getLayoutBounds().getWidth());
    }
    // Utility
    public boolean sessions_includepreandpost() {
        return ! root.PrePostSwitch.isDisabled() && root.PrePostSwitch.isSelected();
    }
    public Duration sessions_getPracticedDuration(Boolean includepreandpostoverride) {
        boolean includepreandpost;
        if (includepreandpostoverride != null) {includepreandpost = includepreandpostoverride;}
        else {includepreandpost = sessions_includepreandpost();}
        return root.getProgressTracker().getSessions().gettotalpracticedtime(this, includepreandpost);
    }
    public int sessions_getPracticedSessionCount(Boolean includepreandpostoverride) {
        boolean includepreandpost;
        if (includepreandpostoverride != null) {includepreandpost = includepreandpostoverride;}
        else {includepreandpost = sessions_includepreandpost();}
        return root.getProgressTracker().getSessions().getsessioncount(this, includepreandpost);
    }
    public Duration sessions_getAverageSessionLength(Boolean includepreandpostoverride) {
        boolean includepreandpost;
        if (includepreandpostoverride != null) {includepreandpost = includepreandpostoverride;}
        else {includepreandpost = sessions_includepreandpost();}
        return root.getProgressTracker().getSessions().getaveragepracticedurationforallsessions(this, includepreandpost);
    }

// Reference Display
    public File reference_getFile(ReferenceType referenceType) {
        if (referenceType == null) {referenceType = root.getPreferences().getSessionOptions().getReferencetype();}
        switch (referenceType) {
            case html: {
                String name = this.name + ".html";
                return new File(Preferences.DIRECTORYREFERENCE, "html/" + name);
            }
            case txt: {
                String name = this.name + ".txt";
                return new File(Preferences.DIRECTORYREFERENCE, "txt/" + name);
            }
            default:
                return null;
        }
    }
    public boolean reference_exists(ReferenceType referenceType) {
        return reference_getFile(referenceType).exists();
    }
    public boolean reference_empty(ReferenceType referenceType) {
        String filecontents = Util.file_getcontents(reference_getFile(referenceType));
        return filecontents == null || filecontents.isEmpty();
    }
    public boolean reference_invalid(ReferenceType referenceType) {
        String contents = Util.file_getcontents(reference_getFile(referenceType));
        switch (referenceType) {
            case html:
                return ! contents.isEmpty() && Util.String_validhtml(contents);
            case txt:
                return ! contents.isEmpty();
        }
        return false;
    }

}
