package kujiin.util;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.concurrent.Service;
import javafx.scene.control.Tooltip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.ui.PlayerUI;
import kujiin.xml.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Meditatable {
    public int number;
    public String name;
    protected int duration;
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
    public int secondselapsed;
    protected List<Cut> cutstoplay;
    protected List<Element> elementstoplay;
    protected List<Meditatable> allcutsorelementstoplay;
    protected Goals GoalsController;

    public Meditatable(int number, String name, int duration, This_Session thissession) {
        this.number = number;
        this.name = name;
        this.duration = duration;
        this.thisession = thissession;
        entrainment = thissession.getEntrainments().getcutorelementsEntrainment(number);
        ambience = thissession.getAmbiences().getcutorelementsAmbience(number);
    }

// Getters And Setters
    protected MediaPlayer getCurrentEntrainmentPlayer() {return entrainmentplayer;}
    protected MediaPlayer getCurrentAmbiencePlayer() {return ambienceplayer;}
    public void setDuration(int newduration) {duration = newduration;}
    public void setAmbienceenabled(boolean ambienceenabled) {
        this.ambienceenabled = ambienceenabled;
    }
    public void setCutstoplay(ArrayList<Cut> cutstoplay) {this.cutstoplay = cutstoplay;}
    public void setElementstoplay(ArrayList<Element> elementstoplay) {this.elementstoplay = elementstoplay;}
    public void setAllcutsorelementstoplay(List<Meditatable> allcutsorelementstoplay) {
        this.allcutsorelementstoplay = allcutsorelementstoplay;
        sortElementsAndCuts();
    }
    public List<Meditatable> getAllcutsorelementstoplay() {
        return allcutsorelementstoplay;
    }
    public Ambience getAmbience() {return ambience;}
    public Entrainment getEntrainment() {return entrainment;}
    public void sortElementsAndCuts() {
        ArrayList<Cut> cutlist = new ArrayList<>();
        ArrayList<Element> elementlist = new ArrayList<>();
        for (Object i : allcutsorelementstoplay) {
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

// Creation
    public boolean build(List<Meditatable> allcutandelementitems, boolean ambienceenabled) {
        setAmbienceenabled(ambienceenabled);
        setAllcutsorelementstoplay(allcutandelementitems);
        if (ambienceenabled) {return buildEntrainment() && buildAmbience();}
        else {return buildEntrainment();}
    }
    public boolean buildEntrainment() {
        entrainment.created_clear();
        return true;
    }
    public boolean buildAmbience() {
        ambience.created_clear();
        double currentdurationinmillis = 0.0;
        if (ambience.hasEnoughAmbience(getdurationinmillis())) {
            for (SoundFile i : ambience.getAmbience()) {
                if (ambience.gettotalCreatedDuration() < getdurationinmillis()) {
                    ambience.created_add(i);
                    currentdurationinmillis += i.getDuration();
                } else {break;}
            }
        } else {
            Random randint = new Random();
            while (currentdurationinmillis < getdurationinmillis()) {
                List<SoundFile> createdambience = ambience.created_getAll();
                SoundFile selectedsoundfile = ambience.actual_get(randint.nextInt(ambience.getAmbience().size() - 1));
                if (createdambience.size() < 2) {
                    ambience.created_add(selectedsoundfile);
                    currentdurationinmillis += selectedsoundfile.getDuration();
                } else if (createdambience.size() == 2) {
                    if (!selectedsoundfile.equals(createdambience.get(createdambience.size() - 1))) {
                        ambience.created_add(selectedsoundfile);
                        currentdurationinmillis += selectedsoundfile.getDuration();
                    }
                } else if (createdambience.size() == 3) {
                    if (!selectedsoundfile.equals(createdambience.get(createdambience.size() - 1)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 2))) {
                        ambience.created_add(selectedsoundfile);
                        currentdurationinmillis += selectedsoundfile.getDuration();
                    }
                } else if (createdambience.size() <= 5) {
                    if (!selectedsoundfile.equals(createdambience.get(createdambience.size() - 1)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 2)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 3))) {
                        ambience.created_add(selectedsoundfile);
                        currentdurationinmillis += selectedsoundfile.getDuration();
                    }
                } else if (createdambience.size() > 5) {
                    if (!selectedsoundfile.equals(createdambience.get(createdambience.size() - 1)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 2)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 3)) && !selectedsoundfile.equals(createdambience.get(createdambience.size() - 4))) {
                        ambience.created_add(selectedsoundfile);
                        currentdurationinmillis += selectedsoundfile.getDuration();
                    }
                }
            }
        }
        return ambience.created_getAll().size() > 0;
    }
    public void resetCreation() {
        entrainment.created_clear();
        ambience.created_clear();
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
            fade_entrainment_play.setOnFinished(event -> {thisession.setPlayerState(PlayerUI.PlayerState.PLAYING); toggleplayerbuttons(); volume_bindentrainment();});
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
                fade_ambience_play.setOnFinished(event -> volume_bindambience());
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
        fade_entrainment_resume.setOnFinished(event -> {thisession.setPlayerState(PlayerUI.PlayerState.PLAYING); timeline_progresstonextmeditatable.play(); if (timeline_fadeout_timer != null) {timeline_fadeout_timer.play();} toggleplayerbuttons(); volume_bindentrainment();});
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
        fade_entrainment_pause.setOnFinished(event -> {entrainmentplayer.pause(); timeline_progresstonextmeditatable.pause(); if (timeline_fadeout_timer != null) {timeline_fadeout_timer.pause();} thisession.setPlayerState(PlayerUI.PlayerState.PAUSED); toggleplayerbuttons();});
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
            fade_entrainment_stop.setOnFinished(event -> {entrainmentplayer.stop(); entrainmentplayer.dispose(); timeline_progresstonextmeditatable.stop(); if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop();} thisession.setPlayerState(PlayerUI.PlayerState.STOPPED); toggleplayerbuttons();});
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
        entrainmentplaycount = 0;
        ambienceplaycount = 0;
        setupfadeanimations();
        volume_unbindentrainment();
        entrainmentplayer = new MediaPlayer(new Media(entrainment.created_get(entrainmentplaycount).getFile().toURI().toString()));
        entrainmentplayer.setVolume(0.0);
        entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
        entrainmentplayer.setOnError(this::entrainmenterror);
        entrainmentplayer.play();
        timeline_progresstonextmeditatable = new Timeline(new KeyFrame(new Duration(getdurationinmillis()), ae -> thisession.progresstonextcut()));
        timeline_progresstonextmeditatable.play();
        if (fade_entrainment_stop != null) {
            timeline_fadeout_timer = new Timeline(new KeyFrame(new Duration(getdurationinmillis() - (thisession.Root.getOptions().getSessionOptions().getFadeoutduration() * 1000)), ae -> {
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
            if (fade_ambience_play.getStatus() == Animation.Status.RUNNING) {return;}
            thisession.setPlayerState(PlayerUI.PlayerState.FADING_PLAY);
            fade_entrainment_play.play();
        } else {
            entrainmentplayer.setVolume(thisession.Root.getOptions().getSessionOptions().getEntrainmentvolume());
            thisession.setPlayerState(PlayerUI.PlayerState.PLAYING);
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
    }
    public void resume() {
        volume_unbindentrainment();
        entrainmentplayer.setVolume(0.0);
        entrainmentplayer.play();
        if (fade_entrainment_resume != null) {
            if (fade_entrainment_resume.getStatus() == Animation.Status.RUNNING) {return;}
            thisession.setPlayerState(PlayerUI.PlayerState.FADING_RESUME);
            fade_entrainment_resume.play();
        } else {
            entrainmentplayer.setVolume(thisession.Root.getOptions().getSessionOptions().getEntrainmentvolume());
            volume_bindentrainment();
            thisession.setPlayerState(PlayerUI.PlayerState.PLAYING);
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
            thisession.setPlayerState(PlayerUI.PlayerState.FADING_PAUSE);
            fade_entrainment_pause.play();
            if (ambienceenabled) {
                volume_unbindambience();
                fade_ambience_pause.play();
            }
        } else {
            thisession.setPlayerState(PlayerUI.PlayerState.PAUSED);
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
            thisession.setPlayerState(PlayerUI.PlayerState.FADING_STOP);
            fade_entrainment_stop.play();
            if (ambienceenabled) {
                volume_unbindambience();
                fade_ambience_stop.play();
            }
        } else {
            thisession.setPlayerState(PlayerUI.PlayerState.STOPPED);
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
    public void tick() {
//        if (entrainmentplayer.getStatus() == MediaPlayer.Status.PLAYING) {
//            if (secondselapsed <= thisession.Root.getOptions().getSessionOptions().getFadeinduration()) {
//                thisession.Root.getPlayer().StatusBar.setText("Fading Into " + name);
//            } else if ((getdurationinseconds() - secondselapsed) <= thisession.Root.getOptions().getSessionOptions().getFadeoutduration()) {
////                startfadeout();
//                thisession.Root.getPlayer().StatusBar.setText("Fading Out Of " + name);
//            } else {
//                thisession.Root.getPlayer().StatusBar.setText("Current Playing " + name);
//            }
//        }
    }
    public void playnextentrainment() {
        try {
            volume_unbindentrainment();
            entrainmentplaycount++;
            entrainmentplayer.dispose();
            entrainmentplayer = new MediaPlayer(new Media(entrainment.created_get(entrainmentplaycount).getFile().toURI().toString()));
            entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
            entrainmentplayer.setOnError(this::entrainmenterror);
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
        boolean idle = thisession.getPlayerState() == PlayerUI.PlayerState.IDLE;
        boolean playing = thisession.getPlayerState() == PlayerUI.PlayerState.PLAYING;
        boolean paused = thisession.getPlayerState() == PlayerUI.PlayerState.PAUSED;
        boolean stopped = thisession.getPlayerState() == PlayerUI.PlayerState.STOPPED;
        boolean fade_play = thisession.getPlayerState() == PlayerUI.PlayerState.FADING_PLAY;
        boolean fade_resume = thisession.getPlayerState() == PlayerUI.PlayerState.FADING_RESUME;
        boolean fade_pause = thisession.getPlayerState() == PlayerUI.PlayerState.FADING_PAUSE;
        boolean fade_stop = thisession.getPlayerState() == PlayerUI.PlayerState.FADING_STOP;
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
        String cutorelementname;
        if (thisession.getCurrentcutorelement() != null) {cutorelementname = thisession.getCurrentcutorelement().name;}
        else {cutorelementname = "Session";}
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
                statusbartext = cutorelementname + " Playing";
                break;
            case PAUSED:
                playbuttontext = "Resume";
                pausebuttontext = "Paused";
                stopbuttontext = "Stop";
                statusbartext = cutorelementname + " Paused";
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
                statusbartext = "Fading In To " + cutorelementname ;
                break;
            case FADING_RESUME:
                playbuttontext = "Resuming";
                pausebuttontext = "Resuming";
                stopbuttontext = "Resuming";
                statusbartext = "Resuming " + cutorelementname ;
                break;
            case FADING_PAUSE:
                playbuttontext = "Pausing";
                pausebuttontext = "Pausing";
                stopbuttontext = "Pausing";
                statusbartext = "Pausing " + cutorelementname;
                break;
            case FADING_STOP:
                playbuttontext = "Stopping";
                pausebuttontext = "Stopping";
                stopbuttontext = "Stopping";
                statusbartext = "Stopping " + cutorelementname;
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
        boolean enabled = thisession.getPlayerState() == PlayerUI.PlayerState.PLAYING;
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
        // TODO Fix Reference && Player Sync Values And Percentages
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

// Tracking & Goals
    public void setCurrentGoal() {

    }
    public Goals.Goal getCurrentGoal() {
        return null;
    }
    public void setGoals(List<Goals.Goal> goalslist) {

    }
    public List<Goals.Goal> getGoals(boolean includecompleted) {
        return getGoalsController().getallcutgoals(number, includecompleted);
    }
    public void checkCurrentGoal(double currrentpracticedhours) {

    }

// Session Information Getters
    public Duration getdurationasobject() {return new Duration(getdurationinmillis());}
    public int getdurationinmillis() {return getdurationinseconds() * 1000;}
    public int getdurationinseconds() {
        return duration * 60;
    }
    public int getdurationinminutes() {
        return duration;
    }
    public Double getdurationindecimalhours() {return Util.convert_minstodecimalhours(getdurationinminutes(), 2);}
    public String getcurrenttimeformatted() {
        return Util.format_secondsforplayerdisplay(secondselapsed);
    }
    public String gettotaltimeformatted() {return Util.format_secondsforplayerdisplay(getdurationinseconds());}

// Error Handling
    protected void entrainmenterror() {}
    protected void ambienceerror() {}

// Reference Files
    public File getReferenceFile() {
        PlayerUI.ReferenceType referenceType = thisession.Root.getOptions().getSessionOptions().getReferencetype();
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
    public boolean referencefilevalid(PlayerUI.ReferenceType referenceType) {
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
