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
import kujiin.MainController;
import kujiin.xml.*;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    protected int entrainmentplaycount;
    protected int ambienceplaycount;
    protected MediaPlayer entrainmentplayer;
    protected MediaPlayer ambienceplayer;
    protected Boolean ambienceenabled;
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
    private Double currententrainmentvolume;
    private Double currentambiencevolume;
    public Duration elapsedtime;
    protected List<Cut> cutstoplay;
    protected List<Element> elementstoplay;
    protected List<Meditatable> allmeditatablestoplay;
    protected List<kujiin.xml.Goals.Goal> goalscompletedthissession;
// Goal Fields
    protected Goals GoalsController;
    protected List<kujiin.xml.Goals.Goal> Goals;

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
                    thissession.getRoot().creation_gui_update();
                } catch (NumberFormatException ignored) {setDuration(0);}
            });
            if (briefsummary != null) {Switch.setTooltip(new Tooltip(briefsummary));}
            Switch.setOnAction(event -> toggleswitch());
            toggleswitch();
        }
        entrainment = thissession.getEntrainments().getmeditatableEntrainment(number);
        ambience = thissession.getAmbiences().getmeditatableAmbience(number);
        //        tempentrainmenttextfile = new File(Options.DIRECTORYTEMP, "txt/" + name + "Ent.txt");
//        tempentrainmentfile = new File(Options.DIRECTORYTEMP, "Entrainment/" + name + "Temp.mp3");
//        finalentrainmentfile = new File(Options.DIRECTORYTEMP, "Entrainment/" + name + ".mp3");
//        tempambiencetextfile = new File(Options.DIRECTORYTEMP, "txt/" + name + "Amb.txt");
//        tempambiencefile = new File(Options.DIRECTORYTEMP, "Ambience/" + name + "Temp.mp3");
//        finalambiencefile = new File(Options.DIRECTORYTEMP, "Ambience/" + name + ".mp3");
//        setFinalexportfile(new File(Options.DIRECTORYTEMP, name + ".mp3"));
    }

// GUI Methods
    public void toggleswitch() {
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
    public void setDisable(boolean disabled) {
        Switch.setDisable(disabled);
        Value.setDisable(disabled);
    }
// Getters And Setters
    public String getNameForChart() {return name;}
    protected MediaPlayer getCurrentEntrainmentPlayer() {return entrainmentplayer;}
    protected MediaPlayer getCurrentAmbiencePlayer() {return ambienceplayer;}
    public void setDuration(double newduration) {duration = new Duration((newduration * 60) * 1000);}
    public void setAmbienceenabled(boolean ambienceenabled) {
        this.ambienceenabled = ambienceenabled;
    }
    public void setCutstoplay(ArrayList<Cut> cutstoplay) {this.cutstoplay = cutstoplay;}
    public void setElementstoplay(ArrayList<Element> elementstoplay) {this.elementstoplay = elementstoplay;}
    public void setAllmeditatablestoplay(List<Meditatable> allmeditatablestoplay) {
        this.allmeditatablestoplay = allmeditatablestoplay;
        sortElementsAndCuts();
    }
    public List<Meditatable> getAllmeditatablestoplay() {
        return allmeditatablestoplay;
    }
    public Ambience getAmbience() {return ambience;}
    public Entrainment getEntrainment() {return entrainment;}
    public void sortElementsAndCuts() {
        ArrayList<Cut> cutlist = new ArrayList<>();
        ArrayList<Element> elementlist = new ArrayList<>();
        for (Object i : getAllmeditatablestoplay()) {
            if (i instanceof Cut) {cutlist.add((Cut) i);}
            if (i instanceof Element) {elementlist.add((Element) i);}
        }
        setCutstoplay(cutlist);
        setElementstoplay(elementlist);
    }
    public void setGoalsController(Goals goals) {
        GoalsController = goals;
    }
    public Goals getGoalsController() {
        return GoalsController;
    }
    public Boolean getAmbienceenabled() {
        return ambienceenabled;
    }
    public Double getCurrententrainmentvolume() {
        return currententrainmentvolume;
    }
    public void setCurrententrainmentvolume(Double currententrainmentvolume) {
        this.currententrainmentvolume = currententrainmentvolume;
    }
    public Double getCurrentambiencevolume() {
        return currentambiencevolume;
    }
    public void setCurrentambiencevolume(Double currentambiencevolume) {
        this.currentambiencevolume = currentambiencevolume;
    }
    // Duration
    public Duration getduration() {return duration;}
    public Duration getelapsedtime() {return elapsedtime;}

// Creation
    public boolean build(List<Meditatable> allcutandelementitems, boolean ambienceenabled) {
        setAmbienceenabled(ambienceenabled);
        setAllmeditatablestoplay(allcutandelementitems);
        if (ambienceenabled) {return buildEntrainment() && buildAmbience();}
        else {return buildEntrainment();}
    }
    public boolean buildEntrainment() {
        entrainment.created_clear();
        return true;
    }
    public boolean buildAmbience() {
        ambience.created_clear();
        Duration currentambienceduration = new Duration(0);
        if (ambience.hasEnoughAmbience(getduration())) {
            for (SoundFile i : ambience.getAmbience()) {
                if (ambience.gettotalCreatedDuration().lessThan(getduration())) {
                    ambience.created_add(i);
                    currentambienceduration = currentambienceduration.add(new Duration(i.getDuration()));
                } else {break;}
            }
        } else {
            Random randint = new Random();
            while (currentambienceduration.lessThan(getduration())) {
                List<SoundFile> createdambience = ambience.created_getAll();
                SoundFile selectedsoundfile = ambience.actual_get(randint.nextInt(ambience.getAmbience().size() - 1));
                if (createdambience.size() < 2) {
                    ambience.created_add(selectedsoundfile);
                    currentambienceduration = currentambienceduration.add(new Duration(selectedsoundfile.getDuration()));
                } else if (createdambience.size() == 2) {
                    if (!selectedsoundfile.equals(createdambience.get(createdambience.size() - 1))) {
                        ambience.created_add(selectedsoundfile);
                        currentambienceduration = currentambienceduration.add(new Duration(selectedsoundfile.getDuration()));
                    }
                } else if (createdambience.size() == 3) {
                    if (!selectedsoundfile.equals(createdambience.get(createdambience.size() - 1)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 2))) {
                        ambience.created_add(selectedsoundfile);
                        currentambienceduration = currentambienceduration.add(new Duration(selectedsoundfile.getDuration()));
                    }
                } else if (createdambience.size() <= 5) {
                    if (!selectedsoundfile.equals(createdambience.get(createdambience.size() - 1)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 2)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 3))) {
                        ambience.created_add(selectedsoundfile);
                        currentambienceduration = currentambienceduration.add(new Duration(selectedsoundfile.getDuration()));
                    }
                } else if (createdambience.size() > 5) {
                    if (!selectedsoundfile.equals(createdambience.get(createdambience.size() - 1)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 2)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 3)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 4))) {
                        ambience.created_add(selectedsoundfile);
                        currentambienceduration = currentambienceduration.add(new Duration(selectedsoundfile.getDuration()));
                    }
                }
            }
        }
        return ambience.created_getAll().size() > 0 && currentambienceduration.greaterThanOrEqualTo(getduration());
    }
    public void resetCreation() {
        entrainment.created_clear();
        ambience.created_clear();
        Switch.setSelected(false);
        toggleswitch();
    }

// Playback
    public void setupfadeanimations() {
    // PLAY
        if (thisession.Root.getOptions().getSessionOptions().getFadeinduration() > 0.0) {
            fade_entrainment_play = new Transition() {
                {setCycleDuration(new Duration(thisession.Root.getOptions().getSessionOptions().getFadeinduration() * 1000));}

                @Override
                protected void interpolate(double frac) {
                    double entrainmentvolume = frac * thisession.Root.getOptions().getSessionOptions().getEntrainmentvolume();
                    getCurrentEntrainmentPlayer().setVolume(entrainmentvolume);
                    thisession.Root.getPlayer().EntrainmentVolume.setValue(entrainmentvolume);
                    Double value = entrainmentvolume * 100;
                    thisession.Root.getPlayer().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                    if (thisession.referencecurrentlyDisplayed()) {
                        thisession.getDisplayReference().EntrainmentVolumeSlider.setValue(entrainmentvolume);
                        thisession.getDisplayReference().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                    }
                }
            };
            fade_entrainment_play.setOnFinished(event -> {thisession.setPlayerState(MainController.PlayerState.PLAYING); toggleplayerbuttons(); volume_bindentrainment(); setCurrententrainmentvolume(thisession.Root.getPlayer().EntrainmentVolume.getValue());});
            if (ambienceenabled) {
                fade_ambience_play = new Transition() {
                    {setCycleDuration(new Duration(thisession.Root.getOptions().getSessionOptions().getFadeinduration() * 1000));}

                    @Override
                    protected void interpolate(double frac) {
                        double ambiencevolume = frac * thisession.Root.getOptions().getSessionOptions().getAmbiencevolume();
                        getCurrentAmbiencePlayer().setVolume(ambiencevolume);
                        thisession.Root.getPlayer().AmbienceVolume.setValue(ambiencevolume);
                        Double value = ambiencevolume * 100;
                        thisession.Root.getPlayer().AmbienceVolumePercentage.setText(value.intValue() + "%");
                        if (thisession.referencecurrentlyDisplayed()) {
                            thisession.getDisplayReference().AmbienceVolumeSlider.setValue(ambiencevolume);
                            thisession.getDisplayReference().AmbienceVolumePercentage.setText(value.intValue() + "%");
                        }
                    }
                };
                fade_ambience_play.setOnFinished(event -> {volume_bindambience(); setCurrentambiencevolume(thisession.Root.getPlayer().AmbienceVolume.getValue());});
            }
        }
    // RESUME
        fade_entrainment_resume = new Transition() {
                {setCycleDuration(new Duration(Options.DEFAULT_FADERESUMEANDPAUSEDURATION * 1000));}

                @Override
                protected void interpolate(double frac) {
                    double entrainmentvolume;
                    if (currententrainmentvolume != null && currententrainmentvolume > 0.0) {entrainmentvolume = frac * getCurrententrainmentvolume();}
                    else {entrainmentvolume = frac * thisession.Root.getOptions().getSessionOptions().getEntrainmentvolume();}
                    getCurrentEntrainmentPlayer().setVolume(entrainmentvolume);
                    thisession.Root.getPlayer().EntrainmentVolume.setValue(entrainmentvolume);
                    Double value = entrainmentvolume * 100;
                    thisession.Root.getPlayer().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                    if (thisession.referencecurrentlyDisplayed()) {
                        thisession.getDisplayReference().EntrainmentVolumeSlider.setValue(entrainmentvolume);
                        thisession.getDisplayReference().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                    }
                }
            };
        fade_entrainment_resume.setOnFinished(event -> {thisession.setPlayerState(MainController.PlayerState.PLAYING); timeline_progresstonextmeditatable.play(); if (timeline_fadeout_timer != null) {timeline_fadeout_timer.play();} toggleplayerbuttons(); volume_bindentrainment();});
        if (ambienceenabled) {
            fade_ambience_resume = new Transition() {
                {setCycleDuration(new Duration(Options.DEFAULT_FADERESUMEANDPAUSEDURATION * 1000));}

                @Override
                protected void interpolate(double frac) {
                    double ambiencevolume;
                    if (currentambiencevolume != null && currentambiencevolume > 0.0) {ambiencevolume = frac * getCurrentambiencevolume();}
                    else {ambiencevolume = frac * thisession.Root.getOptions().getSessionOptions().getAmbiencevolume();}
                    getCurrentAmbiencePlayer().setVolume(ambiencevolume);
                    thisession.Root.getPlayer().AmbienceVolume.setValue(ambiencevolume);
                    Double value = ambiencevolume * 100;
                    thisession.Root.getPlayer().AmbienceVolumePercentage.setText(value.intValue() + "%");
                    if (thisession.referencecurrentlyDisplayed()) {
                        thisession.getDisplayReference().AmbienceVolumeSlider.setValue(ambiencevolume);
                        thisession.getDisplayReference().AmbienceVolumePercentage.setText(value.intValue() + "%");
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
                    double basevalue;
                    double entrainmentvolume;
                    if (currententrainmentvolume != null && currententrainmentvolume > 0.0) {basevalue = getCurrententrainmentvolume(); entrainmentvolume = frac * basevalue;}
                    else {basevalue = thisession.Root.getOptions().getSessionOptions().getEntrainmentvolume(); entrainmentvolume = frac * basevalue;}
                    double fadeoutvolume = basevalue - entrainmentvolume;
                    getCurrentEntrainmentPlayer().setVolume(fadeoutvolume);
                    thisession.Root.getPlayer().EntrainmentVolume.setValue(fadeoutvolume);
                    Double value = fadeoutvolume * 100;
                    thisession.Root.getPlayer().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                    if (thisession.referencecurrentlyDisplayed()) {
                        thisession.getDisplayReference().EntrainmentVolumeSlider.setValue(fadeoutvolume);
                        thisession.getDisplayReference().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                    }
                }
    };
        fade_entrainment_pause.setOnFinished(event -> {entrainmentplayer.pause(); timeline_progresstonextmeditatable.pause(); if (timeline_fadeout_timer != null) {timeline_fadeout_timer.pause();} thisession.setPlayerState(MainController.PlayerState.PAUSED); toggleplayerbuttons();});
        if (ambienceenabled) {
            fade_ambience_pause = new Transition() {
                {setCycleDuration(new Duration(Options.DEFAULT_FADERESUMEANDPAUSEDURATION * 1000));}

                @Override
                protected void interpolate(double frac) {
                    double basevalue;
                    double ambiencevolume;
                    if (currentambiencevolume != null && currentambiencevolume > 0.0) {basevalue = getCurrentambiencevolume(); ambiencevolume = frac * basevalue;}
                    else {basevalue = thisession.Root.getOptions().getSessionOptions().getAmbiencevolume(); ambiencevolume = frac * basevalue;}
                    double fadeoutvolume = basevalue - ambiencevolume;
                    getCurrentAmbiencePlayer().setVolume(fadeoutvolume);
                    thisession.Root.getPlayer().AmbienceVolume.setValue(fadeoutvolume);
                    Double value = fadeoutvolume * 100;
                    thisession.Root.getPlayer().AmbienceVolumePercentage.setText(value.intValue() + "%");
                    if (thisession.referencecurrentlyDisplayed()) {
                        thisession.getDisplayReference().AmbienceVolumeSlider.setValue(fadeoutvolume);
                        thisession.getDisplayReference().AmbienceVolumePercentage.setText(value.intValue() + "%");
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
                    double basevalue;
                    double entrainmentvolume;
                    if (currententrainmentvolume != null && currententrainmentvolume > 0.0) {basevalue = getCurrententrainmentvolume(); entrainmentvolume = frac * basevalue;}
                    else {basevalue = thisession.Root.getOptions().getSessionOptions().getEntrainmentvolume(); entrainmentvolume = frac * basevalue;}
                    double fadeoutvolume = basevalue - entrainmentvolume;
                    getCurrentEntrainmentPlayer().setVolume(fadeoutvolume);
                    thisession.Root.getPlayer().EntrainmentVolume.setValue(fadeoutvolume);
                    Double value = fadeoutvolume * 100;
                    thisession.Root.getPlayer().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                    if (thisession.referencecurrentlyDisplayed()) {
                        thisession.getDisplayReference().EntrainmentVolumeSlider.setValue(fadeoutvolume);
                        thisession.getDisplayReference().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                    }
                }
            };
            fade_entrainment_stop.setOnFinished(event -> {entrainmentplayer.stop(); entrainmentplayer.dispose(); timeline_progresstonextmeditatable.stop(); if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop();} thisession.setPlayerState(MainController.PlayerState.STOPPED); toggleplayerbuttons();});
            if (ambienceenabled) {
                fade_ambience_stop = new Transition() {
                    {setCycleDuration(new Duration(thisession.Root.getOptions().getSessionOptions().getFadeoutduration() * 1000));}

                    @Override
                    protected void interpolate(double frac) {
                        double basevalue;
                        double ambiencevolume;
                        if (currentambiencevolume != null && currentambiencevolume > 0.0) {basevalue = getCurrentambiencevolume(); ambiencevolume = frac * basevalue;}
                        else {basevalue = thisession.Root.getOptions().getSessionOptions().getAmbiencevolume(); ambiencevolume = frac * basevalue;}
                        double fadeoutvolume = basevalue - ambiencevolume;
                        getCurrentAmbiencePlayer().setVolume(fadeoutvolume);
                        thisession.Root.getPlayer().AmbienceVolume.setValue(fadeoutvolume);
                        Double value = fadeoutvolume * 100;
                        thisession.Root.getPlayer().AmbienceVolumePercentage.setText(value.intValue() + "%");
                        if (thisession.referencecurrentlyDisplayed()) {
                            thisession.getDisplayReference().AmbienceVolumeSlider.setValue(fadeoutvolume);
                            thisession.getDisplayReference().AmbienceVolumePercentage.setText(value.intValue() + "%");
                        }
                    }
                };
                fade_entrainment_stop.setOnFinished(event -> {ambienceplayer.stop(); ambienceplayer.dispose();});
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
        timeline_progresstonextmeditatable = new Timeline(new KeyFrame(getduration(), ae -> thisession.progresstonextmeditatable()));
        timeline_progresstonextmeditatable.play();
        if (fade_entrainment_stop != null) {
            timeline_fadeout_timer = new Timeline(new KeyFrame(getduration().subtract(new Duration(thisession.Root.getOptions().getSessionOptions().getFadeoutduration() * 1000)), ae -> {
                volume_unbindentrainment();
                fade_entrainment_stop.play();
                if (fade_ambience_stop != null) {
                    volume_unbindambience();
                    fade_ambience_stop.play();
                }
            }));
            timeline_fadeout_timer.play();
        }
        thisession.displayreferencefile();
        if (fade_entrainment_play != null) {
            if (fade_entrainment_play.getStatus() == Animation.Status.RUNNING) {return;}
            thisession.setPlayerState(MainController.PlayerState.FADING_PLAY);
            fade_entrainment_play.play();
        } else {
            entrainmentplayer.setVolume(thisession.Root.getOptions().getSessionOptions().getEntrainmentvolume());
            thisession.setPlayerState(MainController.PlayerState.PLAYING);
            volume_bindentrainment();}
        if (ambienceenabled) {
            volume_unbindambience();
            ambienceplayer = new MediaPlayer(new Media(ambience.created_get(ambienceplaycount).getFile().toURI().toString()));
            ambienceplayer.setVolume(0.0);
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            ambienceplayer.play();
            if (fade_ambience_play != null) {fade_ambience_play.play();}
            else {ambienceplayer.setVolume(thisession.Root.getOptions().getSessionOptions().getAmbiencevolume()); volume_bindambience();}
        }
        toggleplayerbuttons();
        thisession.Root.sessionandgoals_forceselectmeditatable(number);
        goalscompletedthissession = new ArrayList<>();
    }
    public void resume() {
        volume_unbindentrainment();
        entrainmentplayer.setVolume(0.0);
        entrainmentplayer.play();
        if (fade_entrainment_resume != null) {
            if (fade_entrainment_resume.getStatus() == Animation.Status.RUNNING) {return;}
            thisession.setPlayerState(MainController.PlayerState.FADING_RESUME);
            fade_entrainment_resume.play();
        } else {
            entrainmentplayer.setVolume(thisession.Root.getOptions().getSessionOptions().getEntrainmentvolume());
            volume_bindentrainment();
            thisession.setPlayerState(MainController.PlayerState.PLAYING);
            timeline_progresstonextmeditatable.play();
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.play();}
        }
        if (ambienceenabled) {
            volume_unbindambience();
            ambienceplayer.setVolume(0.0);
            ambienceplayer.play();
            if (fade_ambience_resume != null) {
                if (fade_ambience_resume.getStatus() == Animation.Status.RUNNING) {return;}
                fade_ambience_resume.play();
            } else {
                ambienceplayer.setVolume(thisession.Root.getOptions().getSessionOptions().getAmbiencevolume());
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
            thisession.setPlayerState(MainController.PlayerState.FADING_PAUSE);
            fade_entrainment_pause.play();
            if (ambienceenabled) {
                volume_unbindambience();
                fade_ambience_pause.play();
            }
            // Close Loading Dialog
        } else {
            thisession.setPlayerState(MainController.PlayerState.PAUSED);
            entrainmentplayer.pause();
            timeline_progresstonextmeditatable.pause();
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.pause();}
            if (ambienceenabled) {
                volume_unbindambience();
                ambienceplayer.pause();
            }
        }
        toggleplayerbuttons();
    }
    public void stop() {
        thisession.closereferencefile();
        volume_unbindentrainment();
        if (fade_ambience_stop != null) {
            if (fade_ambience_stop.getStatus() == Animation.Status.RUNNING) {return;}
            thisession.setPlayerState(MainController.PlayerState.FADING_STOP);
            fade_entrainment_stop.play();
            if (ambienceenabled) {
                volume_unbindambience();
                fade_ambience_stop.play();
            }
        } else {
            thisession.setPlayerState(MainController.PlayerState.STOPPED);
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
            if (currententrainmentvolume != null && currententrainmentvolume > 0.0) {entrainmentplayer.setVolume(getCurrententrainmentvolume());}
            else {entrainmentplayer.setVolume(thisession.Root.getOptions().getSessionOptions().getEntrainmentvolume());}
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
            if (currentambiencevolume != null && currentambiencevolume > 0.0) {ambienceplayer.setVolume(getCurrententrainmentvolume());}
            else {ambienceplayer.setVolume(thisession.Root.getOptions().getSessionOptions().getAmbiencevolume());}
            ambienceplayer.play();
            ambienceplayer.setOnPlaying(this::volume_bindambience);
        } catch (IndexOutOfBoundsException ignored) {ambienceplayer.dispose();}
    }
    public void cleanupPlayersandAnimations() {
        try {
            if (getCurrentEntrainmentPlayer() != null) {getCurrentEntrainmentPlayer().dispose();}
            if (getCurrentAmbiencePlayer() != null && ambienceenabled) {getCurrentAmbiencePlayer().dispose();}
            if (fade_entrainment_play != null) {fade_entrainment_play.stop();}
            if (fade_entrainment_pause != null) {fade_entrainment_pause.stop();}
            if (fade_entrainment_resume != null) {fade_entrainment_resume.stop();}
            if (fade_entrainment_stop != null) {fade_entrainment_stop.stop();}
            if (fade_ambience_play != null) {fade_ambience_play.stop();}
            if (fade_ambience_pause != null) {fade_ambience_pause.stop();}
            if (fade_ambience_resume != null) {fade_ambience_resume.stop();}
            if (fade_ambience_stop != null) {fade_ambience_stop.stop();}
            if (timeline_progresstonextmeditatable != null) {timeline_progresstonextmeditatable.stop();}
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop();}
            toggleplayerbuttons();
        } catch (Exception ignored) {}
    }
    public void toggleplayerbuttons() {
        if (thisession.getPlayerState() == null) {return;}
        boolean idle = thisession.getPlayerState() == MainController.PlayerState.IDLE;
        boolean playing = thisession.getPlayerState() == MainController.PlayerState.PLAYING;
        boolean paused = thisession.getPlayerState() == MainController.PlayerState.PAUSED;
        boolean stopped = thisession.getPlayerState() == MainController.PlayerState.STOPPED;
        boolean fade_play = thisession.getPlayerState() == MainController.PlayerState.FADING_PLAY;
        boolean fade_resume = thisession.getPlayerState() == MainController.PlayerState.FADING_RESUME;
        boolean fade_pause = thisession.getPlayerState() == MainController.PlayerState.FADING_PAUSE;
        boolean fade_stop = thisession.getPlayerState() == MainController.PlayerState.FADING_STOP;
        thisession.Root.getPlayer().PlayButton.setDisable(playing || fade_play || fade_resume || fade_pause || fade_stop);
        thisession.Root.getPlayer().PauseButton.setDisable(paused || fade_play || fade_resume || fade_pause || fade_stop || idle);
        thisession.Root.getPlayer().StopButton.setDisable(stopped || fade_play || fade_resume || fade_pause || fade_stop || idle);
        thisession.Root.getPlayer().ReferenceToggleButton.setDisable(fade_play || fade_resume || fade_pause || fade_stop);
        thisession.Root.getPlayer().ReferenceHTMLButton.setDisable(fade_play || fade_resume || fade_pause || fade_stop);
        thisession.Root.getPlayer().ReferenceTXTButton.setDisable(fade_play || fade_resume || fade_pause || fade_stop);
        if (thisession.referencecurrentlyDisplayed()) {
            thisession.getDisplayReference().PlayButton.setDisable(playing || fade_play || fade_resume || fade_pause || fade_stop);
            thisession.getDisplayReference().PauseButton.setDisable(paused || fade_play || fade_resume || fade_pause || fade_stop || idle);
            thisession.getDisplayReference().StopButton.setDisable(stopped || fade_play || fade_resume || fade_pause || fade_stop || idle);
        }
        String playbuttontext;
        String pausebuttontext;
        String stopbuttontext;
        String statusbartext;
        String meditatablename;
        if (thisession.getCurrentmeditatable() != null) {meditatablename = thisession.getCurrentmeditatable().name;}
        else {meditatablename = "Session";}
        switch (thisession.getPlayerState()) {
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
        thisession.Root.getPlayer().PlayButton.setText(playbuttontext);
        thisession.Root.getPlayer().PauseButton.setText(pausebuttontext);
        thisession.Root.getPlayer().StopButton.setText(stopbuttontext);
        thisession.Root.getPlayer().StatusBar.setText(statusbartext);
        if (thisession.referencecurrentlyDisplayed()) {
            thisession.getDisplayReference().PlayButton.setText(playbuttontext);
            thisession.getDisplayReference().PauseButton.setText(pausebuttontext);
            thisession.getDisplayReference().StopButton.setText(stopbuttontext);
        }
        toggleplayervolumecontrols();
    }
    public void toggleplayervolumecontrols() {
        boolean enabled = thisession.getPlayerState() == MainController.PlayerState.PLAYING;
        thisession.Root.getPlayer().EntrainmentVolume.setDisable(! enabled);
        if (ambienceenabled) {thisession.Root.getPlayer().AmbienceVolume.setDisable(! enabled);}
        if (thisession.referencecurrentlyDisplayed()) {
            thisession.getDisplayReference().EntrainmentVolumeSlider.setDisable(! enabled);
            if (ambienceenabled) {thisession.getDisplayReference().AmbienceVolumeSlider.setDisable(! enabled);}
        }
    }
    public void volume_bindentrainment() {
        thisession.Root.getPlayer().EntrainmentVolume.valueProperty().bindBidirectional(getCurrentEntrainmentPlayer().volumeProperty());
        thisession.Root.getPlayer().EntrainmentVolume.setDisable(false);
        thisession.Root.getPlayer().EntrainmentVolume.setOnMouseDragged(event1 -> {
            setCurrententrainmentvolume(thisession.Root.getPlayer().EntrainmentVolume.getValue());
            Double value = thisession.Root.getPlayer().EntrainmentVolume.getValue() * 100;
            thisession.Root.getPlayer().EntrainmentVolumePercentage.setText(value.intValue() + "%");
            thisession.Root.getPlayer().EntrainmentVolume.setTooltip(new Tooltip(value.intValue() + "%"));
        });
        thisession.Root.getPlayer().EntrainmentVolume.setOnScroll(event -> {
            Double newvalue = thisession.Root.getPlayer().EntrainmentVolume.getValue();
            if (event.getDeltaY() < 0) {newvalue -= Options.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
            else {newvalue += Options.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
            if (newvalue <= 1.0 && newvalue >= 0.0) {
                setCurrententrainmentvolume(thisession.Root.getPlayer().EntrainmentVolume.getValue());
                Double value = Util.round_nearestmultipleof5(newvalue * 100);
                String percentage = value.intValue() + "%";
                thisession.Root.getPlayer().EntrainmentVolume.setValue(value / 100);
                thisession.Root.getPlayer().EntrainmentVolume.setTooltip(new Tooltip(percentage));
                thisession.Root.getPlayer().EntrainmentVolumePercentage.setText(percentage);
            }
        });
        if (thisession.referencecurrentlyDisplayed()) {
            thisession.getDisplayReference().EntrainmentVolumeSlider.valueProperty().bindBidirectional(getCurrentEntrainmentPlayer().volumeProperty());
            thisession.getDisplayReference().EntrainmentVolumeSlider.setDisable(false);
            thisession.getDisplayReference().EntrainmentVolumeSlider.setOnMouseDragged(event1 -> {
                setCurrententrainmentvolume(thisession.Root.getPlayer().EntrainmentVolume.getValue());
                Double value = thisession.getDisplayReference().EntrainmentVolumeSlider.getValue() * 100;
                thisession.getDisplayReference().EntrainmentVolumePercentage.setText(value.intValue() + "%");
//                thisession.Root.getOptions().getSessionOptions().setEntrainmentvolume(thisession.getDisplayReference().EntrainmentVolumeSlider.getValue());
                thisession.Root.getPlayer().EntrainmentVolume.setValue(value /= 100);
                thisession.Root.getPlayer().EntrainmentVolumePercentage.setText(value.intValue() + "%");
            });
            thisession.getDisplayReference().EntrainmentVolumeSlider.setOnScroll(event -> {
                Double newvalue = thisession.getDisplayReference().EntrainmentVolumeSlider.getValue();
                if (event.getDeltaY() < 0) {newvalue -= Options.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
                else {newvalue += Options.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
                if (newvalue <= 1.0 && newvalue >= 0.0) {
                    setCurrententrainmentvolume(thisession.Root.getPlayer().EntrainmentVolume.getValue());
                    Double value = Util.round_nearestmultipleof5(newvalue * 100);
                    String percentage = value.intValue() + "%";
                    thisession.getDisplayReference().EntrainmentVolumeSlider.setValue(value / 100);
                    thisession.getDisplayReference().EntrainmentVolumeSlider.setTooltip(new Tooltip(percentage));
                    thisession.getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
                }
            });
        }
    }
    public void volume_unbindentrainment() {
        thisession.Root.getPlayer().EntrainmentVolume.valueProperty().unbind();
        thisession.Root.getPlayer().EntrainmentVolume.setDisable(true);
        if (thisession.referencecurrentlyDisplayed()) {
            thisession.getDisplayReference().EntrainmentVolumeSlider.valueProperty().unbind();
            thisession.getDisplayReference().EntrainmentVolumeSlider.setDisable(true);
        }
    }
    public void volume_bindambience() {
        thisession.Root.getPlayer().AmbienceVolume.valueProperty().bindBidirectional(getCurrentAmbiencePlayer().volumeProperty());
        thisession.Root.getPlayer().AmbienceVolume.setDisable(false);
        thisession.Root.getPlayer().AmbienceVolume.setOnMouseDragged(event1 -> {
            setCurrentambiencevolume(thisession.Root.getPlayer().AmbienceVolume.getValue());
            Double value = thisession.Root.getPlayer().AmbienceVolume.getValue() * 100;
            thisession.Root.getPlayer().AmbienceVolumePercentage.setText(value.intValue() + "%");
            thisession.Root.getPlayer().AmbienceVolume.setTooltip(new Tooltip(value.intValue() + "%"));
        });
        thisession.Root.getPlayer().AmbienceVolume.setOnScroll(event -> {
            Double newvalue = thisession.Root.getPlayer().AmbienceVolume.getValue();
            if (event.getDeltaY() < 0) {newvalue -= Options.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
            else {newvalue += Options.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
            if (newvalue <= 1.0 && newvalue >= 0.0) {
                setCurrentambiencevolume(thisession.Root.getPlayer().AmbienceVolume.getValue());
                Double value = Util.round_nearestmultipleof5(newvalue * 100);
                String percentage = value.intValue() + "%";
                thisession.Root.getPlayer().AmbienceVolume.setValue(value / 100);
                thisession.Root.getPlayer().AmbienceVolume.setTooltip(new Tooltip(percentage));
                thisession.Root.getPlayer().AmbienceVolumePercentage.setText(percentage);
            }
        });
        if (thisession.referencecurrentlyDisplayed()) {
            thisession.getDisplayReference().AmbienceVolumeSlider.valueProperty().bindBidirectional(getCurrentAmbiencePlayer().volumeProperty());
            thisession.getDisplayReference().AmbienceVolumeSlider.setDisable(false);
            thisession.getDisplayReference().AmbienceVolumeSlider.setOnMouseDragged(event1 -> {
                setCurrentambiencevolume(thisession.Root.getPlayer().AmbienceVolume.getValue());
                Double value = thisession.getDisplayReference().AmbienceVolumeSlider.getValue() * 100;
//                thisession.Root.getOptions().getSessionOptions().setAmbiencevolume(thisession.getDisplayReference().AmbienceVolumeSlider.getValue());
                thisession.getDisplayReference().AmbienceVolumePercentage.setText(value.intValue() + "%");
                thisession.Root.getPlayer().AmbienceVolume.setValue(value /= 100);
                thisession.Root.getPlayer().AmbienceVolumePercentage.setText(value.intValue() + "%");
            });
            thisession.getDisplayReference().AmbienceVolumeSlider.setOnScroll(event -> {
                Double newvalue = thisession.getDisplayReference().AmbienceVolumeSlider.getValue();
                if (event.getDeltaY() < 0) {newvalue -= Options.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
                else {newvalue += Options.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
                if (newvalue <= 1.0 && newvalue >= 0.0) {
                    setCurrentambiencevolume(thisession.Root.getPlayer().AmbienceVolume.getValue());
                    Double value = Util.round_nearestmultipleof5(newvalue * 100);
                    String percentage = value.intValue() + "%";
                    thisession.getDisplayReference().AmbienceVolumeSlider.setValue(value / 100);
                    thisession.getDisplayReference().AmbienceVolumeSlider.setTooltip(new Tooltip(percentage));
                    thisession.getDisplayReference().AmbienceVolumePercentage.setText(percentage);
                }
            });
        }
    }
    public void volume_unbindambience() {
        thisession.Root.getPlayer().AmbienceVolume.valueProperty().unbind();
        thisession.Root.getPlayer().AmbienceVolume.setDisable(true);
        try {
            if (thisession.referencecurrentlyDisplayed()) {
                thisession.getDisplayReference().AmbienceVolumeSlider.valueProperty().unbind();
                thisession.getDisplayReference().AmbienceVolumeSlider.setDisable(true);
            }
        } catch (NullPointerException ignored) {}
    }
    public void volume_rebindambience() {volume_unbindambience(); volume_bindambience();}
    public void volume_rebindentrainment() {volume_unbindentrainment(); volume_bindentrainment();}
    public void tick() {
//        try {
//            entrainmentplayer.setVolume(currententrainmentvolume);
//            if (ambienceenabled) ambienceplayer.setVolume(currentambiencevolume);
//        } catch (NullPointerException ignored) {}
        if (entrainmentplayer.getStatus() == MediaPlayer.Status.PLAYING) {
            try {
                thisession.Root.getSessions().sessioninformation_getspecificsession(thisession.Root.getSessions().getSession().size() - 1).updatecutduration(number, new Double(elapsedtime.toMinutes()).intValue());
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
    // Add
    public void addGoal(kujiin.xml.Goals.Goal newgoal) {
        System.out.println("Called Add Goal In Meditatable Logic");
        GoalsController.add(number, newgoal);
    }
    // Update
    public void updateGoals(List<kujiin.xml.Goals.Goal> goalslist) {
        GoalsController.update(goalslist, number);
    }
    // Delete
    public void deleteGoal(kujiin.xml.Goals.Goal currentgoal) {
        GoalsController.delete(number, currentgoal);
    }
    public void deleteGoal(int goalindex) {
        GoalsController.delete(number, goalindex);
    }
    // Getters
    public kujiin.xml.Goals.Goal getCurrentGoal() {
        return GoalsController.getCurrentGoal(number);
    }
    public List<kujiin.xml.Goals.Goal> getAllGoals() {return GoalsController.getAllGoals(number);}
    public List<kujiin.xml.Goals.Goal> getCompletedGoals() {return GoalsController.getCompletedGoals(number);}
    public int getcompletedgoalcount() {return GoalsController.count_completedgoals(number);}
    // Validation
    public boolean goalsarelongenough() {
        try {
            return (getTotalMinutesPracticed(false) / 60) + getduration().toHours() >= getCurrentGoal().getGoal_Hours();
        } catch (NullPointerException e) {return true;}
    }
    // Utility
    public void transition_goalscheck() {
        goalscompletedthissession = completegoalsandgetcompleted();
        if (goalscompletedthissession.size() > 0) {
            thisession.MeditatableswithGoalsCompletedThisSession.add(this);
        }
    }
    public void completecutgoals() {
        GoalsController.completegoals(number, getduration());
    }
    public List<kujiin.xml.Goals.Goal> completegoalsandgetcompleted() {
        return GoalsController.completegoalsandgetcompleted(number, getduration());
    }
    public List<kujiin.xml.Goals.Goal> getGoalsCompletedThisSession() {
        return GoalsController.getgoalsCompletedOn(number, LocalDate.now());
    }
    public List<kujiin.xml.Goals.Goal> getGoalsCompletedOn(LocalDate localDate) {
        return GoalsController.getgoalsCompletedOn(number, localDate);
    }

// Session Tracking
    public double getAveragePracticeTime(boolean includepreandpost) {
        return thisession.Root.getSessions().sessioninformation_getaveragepracticetime(number, includepreandpost);
    }
    public int getTotalMinutesPracticed(boolean includepreandpost) {
        return thisession.Root.getSessions().sessioninformation_getallsessiontotals(number, includepreandpost);
    }
    public int getNumberOfSessionsPracticed(boolean includepreandpost) {
        return thisession.Root.getSessions().sessioninformation_getsessioncount(number, includepreandpost);
    }

// Reference Files
    public File getReferenceFile() {
        MainController.ReferenceType referenceType = thisession.Root.getOptions().getSessionOptions().getReferencetype();
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
    public boolean referencefilevalid(MainController.ReferenceType referenceType) {
        if (referenceType == null) {return false;}
        if (! getReferenceFile().exists()) {return false;}
        String contents = Util.file_getcontents(getReferenceFile());
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
