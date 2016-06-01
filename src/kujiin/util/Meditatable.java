package kujiin.util;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.concurrent.Service;
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
    // TODO Find Out Why Fade Animations Are Accruing Indefinite Memory
    public void setupfadeanimations() {
    // PLAY
        if (thisession.Root.getOptions().getSessionOptions().getFadeinduration() > 0.0) {
            if (fade_entrainment_play == null) {
                fade_entrainment_play = new Transition() {
                    {setCycleDuration(new Duration(thisession.Root.getOptions().getSessionOptions().getFadeinduration() * 1000));}

                    @Override
                    protected void interpolate(double frac) {
                        double entrainmentvolume = frac * thisession.Root.getOptions().getSessionOptions().getEntrainmentvolume();
                        getCurrentEntrainmentPlayer().setVolume(entrainmentvolume);
                        thisession.Root.getPlayer().EntrainmentVolume.setValue(entrainmentvolume);
                        Double value = thisession.Root.getPlayer().EntrainmentVolume.getValue() * 100;
                        thisession.Root.getPlayer().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                        if (thisession.getDisplayReference() != null && thisession.getDisplayReference().isShowing()) {
                            thisession.getDisplayReference().EntrainmentVolumeSlider.setValue(entrainmentvolume);
                            thisession.getDisplayReference().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                        }
                    }
                };
                fade_entrainment_play.setOnFinished(event -> {thisession.setPlayerState(PlayerUI.PlayerState.PLAYING); toggleplayerbuttons(); volume_unbindentrainment(); volume_bindentrainment();});
            }
            if (ambienceenabled && fade_ambience_play == null) {
                fade_ambience_play = new Transition() {
                    {setCycleDuration(new Duration(thisession.Root.getOptions().getSessionOptions().getFadeinduration() * 1000));}
                    @Override
                    protected void interpolate(double frac) {
                        double ambiencevolume = frac * thisession.Root.getOptions().getSessionOptions().getAmbiencevolume();
                        getCurrentAmbiencePlayer().setVolume(ambiencevolume);
                        thisession.Root.getPlayer().AmbienceVolume.setValue(ambiencevolume);
                        Double value = thisession.Root.getPlayer().AmbienceVolume.getValue() * 100;
                        thisession.Root.getPlayer().AmbienceVolumePercentage.setText(value.intValue() + "%");
                        if (thisession.getDisplayReference() != null && thisession.getDisplayReference().isShowing()) {
                            thisession.getDisplayReference().AmbienceVolumeSlider.setValue(ambiencevolume);
                            thisession.getDisplayReference().AmbienceVolumePercentage.setText(value.intValue() + "%");
                        }
                    }
                };
                fade_ambience_play.setOnFinished(event -> {toggleplayerbuttons(); volume_unbindambience(); volume_bindambience();});
            }
        }
    // RESUME
        if (fade_entrainment_resume == null) {
            fade_entrainment_resume = new Transition() {
                {setCycleDuration(new Duration(2000));}

                @Override
                protected void interpolate(double frac) {
                    double entrainmentvolume = frac * thisession.Root.getOptions().getSessionOptions().getEntrainmentvolume();
                    getCurrentEntrainmentPlayer().setVolume(entrainmentvolume);
                    thisession.Root.getPlayer().EntrainmentVolume.setValue(entrainmentvolume);
                    Double value = thisession.Root.getPlayer().EntrainmentVolume.getValue() * 100;
                    thisession.Root.getPlayer().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                    if (thisession.getDisplayReference() != null && thisession.getDisplayReference().isShowing()) {
                        thisession.getDisplayReference().EntrainmentVolumeSlider.setValue(entrainmentvolume);
                        thisession.getDisplayReference().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                    }
                }
            };
            fade_entrainment_resume.setOnFinished(event -> {thisession.setPlayerState(PlayerUI.PlayerState.PLAYING); toggleplayerbuttons(); volume_unbindentrainment(); volume_bindentrainment();});
        }
        if (ambienceenabled && fade_ambience_resume == null) {
            fade_ambience_resume = new Transition() {
                {setCycleDuration(new Duration(2000));}
                @Override
                protected void interpolate(double frac) {
                    double ambiencevolume = frac * thisession.Root.getOptions().getSessionOptions().getAmbiencevolume();
                    getCurrentAmbiencePlayer().setVolume(ambiencevolume);
                    thisession.Root.getPlayer().AmbienceVolume.setValue(ambiencevolume);
                    Double value = thisession.Root.getPlayer().AmbienceVolume.getValue() * 100;
                    thisession.Root.getPlayer().AmbienceVolumePercentage.setText(value.intValue() + "%");
                    if (thisession.getDisplayReference() != null && thisession.getDisplayReference().isShowing()) {
                        thisession.getDisplayReference().AmbienceVolumeSlider.setValue(ambiencevolume);
                        thisession.getDisplayReference().AmbienceVolumePercentage.setText(value.intValue() + "%");
                    }
                }
            };
            fade_ambience_resume.setOnFinished(event -> {toggleplayerbuttons(); volume_unbindambience();volume_bindambience();});
        }
    // PAUSE
        if (fade_entrainment_pause == null) {
            fade_entrainment_pause = new Transition() {
                {setCycleDuration(new Duration(2000));}
                @Override
                protected void interpolate(double frac) {
                    double entvol = thisession.Root.getOptions().getSessionOptions().getEntrainmentvolume();
                    double entrainmentvolume = frac * entvol;
                    double fadeoutvolume = entvol - entrainmentvolume;
                    getCurrentEntrainmentPlayer().setVolume(fadeoutvolume);
                    thisession.Root.getPlayer().EntrainmentVolume.setValue(fadeoutvolume);
                    Double value = thisession.Root.getPlayer().EntrainmentVolume.getValue() * 100;
                    thisession.Root.getPlayer().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                    if (thisession.getDisplayReference() != null && thisession.getDisplayReference().isShowing()) {
                        thisession.getDisplayReference().EntrainmentVolumeSlider.setValue(fadeoutvolume);
                        thisession.getDisplayReference().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                    }
                }
            };
            fade_entrainment_pause.setOnFinished(event -> {volume_unbindentrainment(); entrainmentplayer.pause(); thisession.setPlayerState(PlayerUI.PlayerState.PAUSED); toggleplayerbuttons();});
        }
        if (ambienceenabled && fade_ambience_pause == null) {
            fade_ambience_pause = new Transition() {
                {setCycleDuration(new Duration(2000));}
                @Override
                protected void interpolate(double frac) {
                    double ambvol = thisession.Root.getOptions().getSessionOptions().getAmbiencevolume();
                    double ambiencevolume = frac * ambvol;
                    double fadeoutvolume = ambvol - ambiencevolume;
                    getCurrentAmbiencePlayer().setVolume(fadeoutvolume);
                    thisession.Root.getPlayer().AmbienceVolume.setValue(fadeoutvolume);
                    thisession.Root.getPlayer().AmbienceVolume.setDisable(true);
                    Double value = thisession.Root.getPlayer().AmbienceVolume.getValue() * 100;
                    thisession.Root.getPlayer().AmbienceVolumePercentage.setText(value.intValue() + "%");
                    if (thisession.getDisplayReference() != null && thisession.getDisplayReference().isShowing()) {
                        thisession.getDisplayReference().AmbienceVolumeSlider.setValue(fadeoutvolume);
                        thisession.getDisplayReference().AmbienceVolumePercentage.setText(value.intValue() + "%");
                    }
                }
            };
            fade_ambience_pause.setOnFinished(event -> { volume_unbindambience(); ambienceplayer.pause(); toggleplayerbuttons();});
        }
    // STOP
        if (thisession.Root.getOptions().getSessionOptions().getFadeoutduration() > 0.0) {
            if (fade_entrainment_stop == null) {
                fade_entrainment_stop = new Transition() {
                    {setCycleDuration(new Duration(thisession.Root.getOptions().getSessionOptions().getFadeoutduration() * 1000));}

                    @Override
                    protected void interpolate(double frac) {
                        double entvol = thisession.Root.getOptions().getSessionOptions().getEntrainmentvolume();
                        double entrainmentvolume = frac * entvol;
                        double fadeoutvolume = entvol - entrainmentvolume;
                        getCurrentEntrainmentPlayer().setVolume(fadeoutvolume);
                        thisession.Root.getPlayer().EntrainmentVolume.setValue(fadeoutvolume);
                        Double value = thisession.Root.getPlayer().EntrainmentVolume.getValue() * 100;
                        thisession.Root.getPlayer().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                        if (thisession.getDisplayReference() != null && thisession.getDisplayReference().isShowing()) {
                            thisession.getDisplayReference().EntrainmentVolumeSlider.setValue(fadeoutvolume);
                            thisession.getDisplayReference().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                        }
                    }
                };
                fade_entrainment_stop.setOnFinished(event -> {thisession.setPlayerState(PlayerUI.PlayerState.STOPPED); toggleplayerbuttons(); volume_unbindentrainment(); entrainmentplayer.stop(); entrainmentplayer.dispose();});
            }
            if (ambienceenabled && fade_ambience_stop == null) {
                fade_ambience_stop = new Transition() {
                    {setCycleDuration(new Duration(thisession.Root.getOptions().getSessionOptions().getFadeoutduration() * 1000));}
                    @Override
                    protected void interpolate(double frac) {
                        double ambvol = thisession.Root.getOptions().getSessionOptions().getAmbiencevolume();
                        double ambiencevolume = frac * ambvol;
                        double fadeoutvolume = ambvol - ambiencevolume;
                        getCurrentAmbiencePlayer().setVolume(fadeoutvolume);
                        thisession.Root.getPlayer().AmbienceVolume.setValue(fadeoutvolume);
                        Double value = thisession.Root.getPlayer().AmbienceVolume.getValue() * 100;
                        thisession.Root.getPlayer().AmbienceVolumePercentage.setText(value.intValue() + "%");
                        if (thisession.getDisplayReference() != null && thisession.getDisplayReference().isShowing()) {
                            thisession.getDisplayReference().AmbienceVolumeSlider.setValue(fadeoutvolume);
                            thisession.getDisplayReference().AmbienceVolumePercentage.setText(value.intValue() + "%");
                        }
                    }
                };
                fade_entrainment_stop.setOnFinished(event -> {toggleplayerbuttons(); volume_unbindambience(); ambienceplayer.stop(); ambienceplayer.dispose();});
            }
        }
    }
    public void start() {
        entrainmentplaycount = 0;
        ambienceplaycount = 0;
        setupfadeanimations();
        entrainmentplayer = new MediaPlayer(new Media(entrainment.created_get(entrainmentplaycount).getFile().toURI().toString()));
        entrainmentplayer.setVolume(0.0);
        entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
        entrainmentplayer.setOnError(this::entrainmenterror);
        entrainmentplayer.play();
        if (fade_entrainment_play != null) {
            volume_unbindentrainment();
            thisession.setPlayerState(PlayerUI.PlayerState.FADING_PLAY);
            toggleplayerbuttons();
            fade_entrainment_play.play();
        }
        else {entrainmentplayer.setVolume(thisession.Root.getOptions().getSessionOptions().getEntrainmentvolume()); thisession.setPlayerState(PlayerUI.PlayerState.PLAYING); toggleplayerbuttons(); volume_bindentrainment();}
        if (ambienceenabled) {
            ambienceplayer = new MediaPlayer(new Media(ambience.created_get(ambienceplaycount).getFile().toURI().toString()));
            ambienceplayer.setVolume(0.0);
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            ambienceplayer.play();
            if (fade_ambience_play != null) {
                volume_unbindambience();
                fade_ambience_play.play();
            }
            else {ambienceplayer.setVolume(thisession.Root.getOptions().getSessionOptions().getAmbiencevolume()); volume_bindambience();}
        }
    }
    public void resume() {
        entrainmentplayer.setVolume(0.0);
        entrainmentplayer.play();
        if (fade_entrainment_resume != null) {
            volume_unbindentrainment();
            thisession.setPlayerState(PlayerUI.PlayerState.FADING_RESUME);
            toggleplayerbuttons();
            fade_entrainment_resume.play();
        }
        else {entrainmentplayer.setVolume(thisession.Root.getOptions().getSessionOptions().getEntrainmentvolume()); volume_bindentrainment(); thisession.setPlayerState(PlayerUI.PlayerState.PLAYING); toggleplayerbuttons();}
        if (ambienceenabled) {
            ambienceplayer.setVolume(0.0);
            ambienceplayer.play();
            if (fade_ambience_resume != null) {
                volume_unbindambience();
                fade_ambience_resume.play();
            }
            else {ambienceplayer.setVolume(thisession.Root.getOptions().getSessionOptions().getAmbiencevolume()); volume_bindambience();}
        }
    }
    public void pause() {
        if (fade_entrainment_pause != null) {
            if (fade_entrainment_pause.getStatus() != Animation.Status.RUNNING) {
                volume_unbindentrainment();
                thisession.setPlayerState(PlayerUI.PlayerState.FADING_PAUSE);
                toggleplayerbuttons();
                fade_entrainment_pause.play();
                if (ambienceenabled && fade_ambience_pause != null) {
                    volume_unbindambience();
                    fade_ambience_pause.play();
                }
            } else {return;}
        } else {
            thisession.setPlayerState(PlayerUI.PlayerState.PAUSED);
            volume_unbindentrainment();
            entrainmentplayer.pause();
            if (ambienceenabled) {
                volume_unbindambience();
                ambienceplayer.pause();
            }
            toggleplayerbuttons();
        }
    }
    public void stop() {
        if (thisession.Root.getOptions().getSessionOptions().getFadeoutduration() > 0.0 && fade_ambience_stop != null) {
            if (fade_entrainment_stop.getStatus() != Animation.Status.RUNNING) {
                volume_unbindentrainment();
                thisession.setPlayerState(PlayerUI.PlayerState.FADING_STOP);
                toggleplayerbuttons();
                fade_entrainment_stop.play();
                if (ambienceenabled) {
                    volume_unbindambience();
                    fade_ambience_stop.playFromStart();
                }
            } else {return;}
        } else {
            thisession.setPlayerState(PlayerUI.PlayerState.STOPPED);
            volume_unbindentrainment();
            entrainmentplayer.stop();
            entrainmentplayer.dispose();
            if (ambienceenabled) {
                volume_unbindambience();
                ambienceplayer.stop();
                ambienceplayer.dispose();
            }
            toggleplayerbuttons();
        }
    }
    public void tick() {
        if (entrainmentplayer.getStatus() == MediaPlayer.Status.PLAYING) {
            if (secondselapsed <= thisession.Root.getOptions().getSessionOptions().getFadeinduration()) {
                thisession.Root.getPlayer().StatusBar.setText("Fading Into " + name);
            } else if ((getdurationinseconds() - secondselapsed) <= thisession.Root.getOptions().getSessionOptions().getFadeoutduration()) {
                startfadeout();
                thisession.Root.getPlayer().StatusBar.setText("Fading Out Of " + name);
            } else {
                thisession.Root.getPlayer().StatusBar.setText("Current Playing " + name);
            }
        }
    }
    public void playnextentrainment() {
        try {
            entrainmentplaycount++;
            entrainmentplayer.dispose();
            entrainmentplayer = new MediaPlayer(new Media(entrainment.created_get(entrainmentplaycount).getFile().toURI().toString()));
            entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
            entrainmentplayer.setOnError(this::entrainmenterror);
            entrainmentplayer.play();
            entrainmentplayer.setOnPlaying(() -> {
                volume_unbindentrainment();
                volume_bindentrainment();
            });
        } catch (IndexOutOfBoundsException ignored) {
            entrainmentplayer.dispose();
            cleanup();
        }
    }
    public void playnextambience() {
        try {
            ambienceplaycount++;
            ambienceplayer.dispose();
            ambienceplayer = new MediaPlayer(new Media(ambience.created_get(ambienceplaycount).getFile().toURI().toString()));
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            ambienceplayer.play();
            ambienceplayer.setOnPlaying(() -> {
                volume_unbindambience();
                volume_bindambience();
            });
        } catch (IndexOutOfBoundsException ignored) {ambienceplayer.dispose();}
    }
    public void startfadeout() {
        if (fade_entrainment_stop != null && fade_entrainment_stop.getStatus() != Animation.Status.RUNNING) {
            thisession.Root.getPlayer().EntrainmentVolume.valueProperty().unbindBidirectional(getCurrentEntrainmentPlayer().volumeProperty());
            fade_entrainment_stop.play();
            if (ambienceenabled && fade_ambience_stop != null) {
                thisession.Root.getPlayer().AmbienceVolume.valueProperty().unbindBidirectional(getCurrentAmbiencePlayer().volumeProperty());
                fade_ambience_stop.play();
            }
        }
    }
    public void cleanup() {
        try {
            getCurrentEntrainmentPlayer().dispose();
            if (ambienceenabled) {getCurrentAmbiencePlayer().dispose();}
            if (fade_entrainment_play != null) {fade_entrainment_play.stop();}
            if (fade_entrainment_pause != null) {fade_entrainment_pause.stop();}
            if (fade_entrainment_resume != null) {fade_entrainment_resume.stop();}
            if (fade_entrainment_stop != null) {fade_entrainment_stop.stop();}
            if (fade_ambience_play != null) {fade_ambience_play.stop();}
            if (fade_ambience_pause != null) {fade_ambience_pause.stop();}
            if (fade_ambience_resume != null) {fade_ambience_resume.stop();}
            if (fade_ambience_stop != null) {fade_ambience_stop.stop();}
        } catch (Exception ignored) {}
    }
    public void setvolumeenabled(boolean enabled) {
        thisession.Root.getPlayer().EntrainmentVolume.setDisable(! enabled);
        if (ambienceenabled) {thisession.Root.getPlayer().AmbienceVolume.setDisable(! enabled);}
        if (thisession.getDisplayReference() != null && thisession.getDisplayReference().isShowing()) {
            thisession.getDisplayReference().EntrainmentVolumeSlider.setDisable(! enabled);
            if (ambienceenabled) {thisession.getDisplayReference().AmbienceVolumeSlider.setDisable(! enabled);}
        }
    }
    public void toggleplayerbuttons() {
        if (thisession.getPlayerState() == null) {return;}
        boolean referenceenabled = thisession.getDisplayReference() != null && thisession.getDisplayReference().isShowing();
        boolean playing = thisession.getPlayerState() == PlayerUI.PlayerState.PLAYING;
        boolean paused = thisession.getPlayerState() == PlayerUI.PlayerState.PAUSED;
        boolean stopped = thisession.getPlayerState() == PlayerUI.PlayerState.STOPPED;
        boolean fade_play = thisession.getPlayerState() == PlayerUI.PlayerState.FADING_PLAY;
        boolean fade_resume = thisession.getPlayerState() == PlayerUI.PlayerState.FADING_RESUME;
        boolean fade_pause = thisession.getPlayerState() == PlayerUI.PlayerState.FADING_PAUSE;
        boolean fade_stop = thisession.getPlayerState() == PlayerUI.PlayerState.FADING_STOP;
        thisession.Root.getPlayer().PlayButton.setDisable(playing || fade_play || fade_resume || fade_pause || fade_stop);
        thisession.Root.getPlayer().PauseButton.setDisable(paused || fade_play || fade_resume || fade_pause || fade_stop);
        thisession.Root.getPlayer().StopButton.setDisable(stopped || fade_play || fade_resume || fade_pause || fade_stop);
        if (referenceenabled) {
            thisession.getDisplayReference().PlayButton.setDisable(playing || fade_play || fade_resume || fade_pause || fade_stop);
            thisession.getDisplayReference().PauseButton.setDisable(paused || fade_play || fade_resume || fade_pause || fade_stop);
            thisession.getDisplayReference().StopButton.setDisable(stopped || fade_play || fade_resume || fade_pause || fade_stop);
        }
        String playbuttontext;
        String pausebuttontext;
        String stopbuttontext;
        String statusbartext;
        switch (thisession.getPlayerState()) {
            case PLAYING:
                playbuttontext = "Playing";
                pausebuttontext = "Paused";
                stopbuttontext = "Stop";
                statusbartext = "Session Playing";
                break;
            case PAUSED:
                playbuttontext = "Resume";
                pausebuttontext = "Paused";
                stopbuttontext = "Stop";
                statusbartext = "Session Paused";
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
                statusbartext = "Session Transitioning, Please Wait";
                break;
            case FADING_PLAY:
                playbuttontext = "Starting";
                pausebuttontext = "Starting";
                stopbuttontext = "Starting";
                statusbartext = "Session Starting";
                break;
            case FADING_RESUME:
                playbuttontext = "Resuming";
                pausebuttontext = "Resuming";
                stopbuttontext = "Resuming";
                statusbartext = "Session Resuming";
                break;
            case FADING_PAUSE:
                playbuttontext = "Pausing";
                pausebuttontext = "Pausing";
                stopbuttontext = "Pausing";
                statusbartext = "Session Pausing";
                break;
            case FADING_STOP:
                playbuttontext = "Stopping";
                pausebuttontext = "Stopping";
                stopbuttontext = "Stopping";
                statusbartext = "Session Stopping";
                break;
            default:
                playbuttontext = "";
                pausebuttontext = "";
                stopbuttontext = "";
                statusbartext = "";
                break;
        }
        setvolumeenabled(thisession.getPlayerState() == PlayerUI.PlayerState.PLAYING);
        thisession.Root.getPlayer().PlayButton.setText(playbuttontext);
        thisession.Root.getPlayer().PauseButton.setText(pausebuttontext);
        thisession.Root.getPlayer().StopButton.setText(stopbuttontext);
        thisession.Root.getPlayer().StatusBar.setText(statusbartext);
        if (referenceenabled) {
            thisession.getDisplayReference().PlayButton.setText(playbuttontext);
            thisession.getDisplayReference().PauseButton.setText(pausebuttontext);
            thisession.getDisplayReference().StopButton.setText(stopbuttontext);
        }
    }
    public void volume_bindentrainment() {
        thisession.Root.getPlayer().EntrainmentVolume.valueProperty().bindBidirectional(getCurrentEntrainmentPlayer().volumeProperty());
        thisession.Root.getPlayer().EntrainmentVolume.setOnMouseDragged(event1 -> {
            thisession.Root.getOptions().getSessionOptions().setEntrainmentvolume(thisession.Root.getPlayer().EntrainmentVolume.getValue());
            Double value = thisession.Root.getPlayer().EntrainmentVolume.getValue() * 100;
            thisession.Root.getPlayer().EntrainmentVolumePercentage.setText(value.intValue() + "%");
        });
        if (thisession.getDisplayReference() != null && thisession.getDisplayReference().isShowing()) {
            thisession.getDisplayReference().EntrainmentVolumeSlider.valueProperty().bindBidirectional(getCurrentEntrainmentPlayer().volumeProperty());
            thisession.getDisplayReference().EntrainmentVolumeSlider.setOnMouseDragged(event1 -> {
                thisession.Root.getOptions().getSessionOptions().setEntrainmentvolume(thisession.Root.getPlayer().EntrainmentVolume.getValue());
                Double value = thisession.Root.getPlayer().EntrainmentVolume.getValue() * 100;
                thisession.getDisplayReference().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                thisession.Root.getPlayer().EntrainmentVolume.setValue(value /= 100);
                thisession.Root.getPlayer().EntrainmentVolumePercentage.setText(value.intValue() + "%");
            });
        }
    }
    public void volume_unbindentrainment() {
        thisession.Root.getPlayer().EntrainmentVolume.valueProperty().unbind();
        if (thisession.getDisplayReference() != null && thisession.getDisplayReference().isShowing()) {
            thisession.getDisplayReference().EntrainmentVolumeSlider.valueProperty().unbind();
        }
    }
    public void volume_bindambience() {
        thisession.Root.getPlayer().AmbienceVolume.valueProperty().bindBidirectional(getCurrentAmbiencePlayer().volumeProperty());
        thisession.Root.getPlayer().AmbienceVolume.setOnMouseDragged(event1 -> {
            thisession.Root.getOptions().getSessionOptions().setAmbiencevolume(thisession.Root.getPlayer().AmbienceVolume.getValue());
            Double value = thisession.Root.getPlayer().AmbienceVolume.getValue() * 100;
            thisession.Root.getPlayer().AmbienceVolumePercentage.setText(value.intValue() + "%");
        });
        if (thisession.getDisplayReference() != null && thisession.getDisplayReference().isShowing()) {
            thisession.getDisplayReference().AmbienceVolumeSlider.valueProperty().bindBidirectional(getCurrentAmbiencePlayer().volumeProperty());
            thisession.getDisplayReference().AmbienceVolumeSlider.setOnMouseDragged(event1 -> {
                thisession.Root.getOptions().getSessionOptions().setAmbiencevolume(thisession.Root.getPlayer().AmbienceVolume.getValue());
                Double value = thisession.Root.getPlayer().AmbienceVolume.getValue() * 100;
                thisession.getDisplayReference().AmbienceVolumePercentage.setText(value.intValue() + "%");
                thisession.Root.getPlayer().AmbienceVolume.setValue(value /= 100);
                thisession.Root.getPlayer().AmbienceVolumePercentage.setText(value.intValue() + "%");
            });
        }
    }
    public void volume_unbindambience() {
        thisession.Root.getPlayer().AmbienceVolume.valueProperty().unbind();
        if (thisession.getDisplayReference() != null && thisession.getDisplayReference().isShowing()) {
            thisession.getDisplayReference().AmbienceVolumeSlider.valueProperty().unbind();
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

}
