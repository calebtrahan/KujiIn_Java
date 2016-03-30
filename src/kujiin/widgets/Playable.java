package kujiin.widgets;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.Cut;
import kujiin.Element;
import kujiin.This_Session;
import kujiin.Tools;
import kujiin.xml.Ambiences;
import kujiin.xml.Entrainments;
import kujiin.xml.Options;

import java.io.File;
import java.util.ArrayList;

public class Playable {
    public int number;
    public String name;
    protected int duration;
    protected This_Session thisession;
    protected File ambiencedirectory;
    protected Ambiences ambiences;
    protected Entrainments entrainments;
    protected int entrainmentplaycount;
    protected int ambienceplaycount;
    protected MediaPlayer entrainmentplayer;
    protected MediaPlayer ambienceplayer;
    protected Boolean ambienceenabled;
    protected Animation fadeinentrainment;
    protected Animation fadeoutentrainment;
    protected Animation fadeinambience;
    protected Animation fadeoutambience;
    protected Timeline fadeouttimeline;
    protected int secondselapsed;
    protected ArrayList<Cut> cutstoplay;
    protected ArrayList<Element> elementstoplay;
    protected ArrayList<Object> allcutsorelementstoplay;
    protected Timeline cutorelementtimeline;

// Getters And Setters
    protected MediaPlayer getCurrentEntrainmentPlayer() {return entrainmentplayer;}
    protected MediaPlayer getCurrentAmbiencePlayer() {return ambienceplayer;}
    public void setDuration(int newduration) {duration = newduration;}
    public void setAmbienceenabled(boolean ambienceenabled) {
        this.ambienceenabled = ambienceenabled;
    }
    public void setCutstoplay(ArrayList<Cut> cutstoplay) {this.cutstoplay = cutstoplay;}
    public void setElementstoplay(ArrayList<Element> elementstoplay) {this.elementstoplay = elementstoplay;}
    public void setAllcutsorelementstoplay(ArrayList<Object> allcutsorelementstoplay) {
        this.allcutsorelementstoplay = allcutsorelementstoplay;
        sortElementsAndCuts();
    }
    public ArrayList<Object> getAllcutsorelementstoplay() {
        return allcutsorelementstoplay;
    }
    public Ambiences getAmbiences() {return ambiences;}
    public Entrainments getEntrainments() {return entrainments;}
    public int getSecondselapsed() {return secondselapsed;}
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

// Playback Controls
    public void start() {
        entrainmentplaycount = 0;
        ambienceplaycount = 0;
        double fadeinduration = thisession.Root.getOptions().getSessionOptions().getFadeinduration();
        double fadeoutduration = thisession.Root.getOptions().getSessionOptions().getFadeoutduration();
        // Set Up Audio Fade In
        if (fadeinduration > 0.0) {
            fadeinentrainment = new Transition() {
                {setCycleDuration(new Duration(fadeinduration * 1000));}
                @Override
                protected void interpolate(double frac) {
                    double entrainmentvolume = frac * thisession.Root.getOptions().getSessionOptions().getEntrainmentvolume();
                    getCurrentEntrainmentPlayer().setVolume(entrainmentvolume);
                    thisession.Root.getPlayer().EntrainmentVolume.setValue(entrainmentvolume);
                    Double value = thisession.Root.getPlayer().EntrainmentVolume.getValue() * 100;
                    thisession.Root.getPlayer().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                    thisession.Root.getPlayer().EntrainmentVolume.setDisable(true);
                }
            };
            fadeinentrainment.setOnFinished(event -> {
                thisession.Root.getPlayer().EntrainmentVolume.setDisable(false);
                thisession.Root.getPlayer().EntrainmentVolume.valueProperty().bindBidirectional(getCurrentEntrainmentPlayer().volumeProperty());
                thisession.Root.getPlayer().EntrainmentVolume.setOnMouseDragged(event1 -> {
                    thisession.Root.getOptions().getSessionOptions().setEntrainmentvolume(thisession.Root.getPlayer().EntrainmentVolume.getValue());
                    Double value = thisession.Root.getPlayer().EntrainmentVolume.getValue() * 100;
                    thisession.Root.getPlayer().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                });
            });
            if (ambienceenabled) {
                fadeinambience = new Transition() {
                    {setCycleDuration(new Duration(fadeinduration * 1000));}
                    @Override
                    protected void interpolate(double frac) {
                        double ambiencevolume = frac * thisession.Root.getOptions().getSessionOptions().getAmbiencevolume();
                        getCurrentAmbiencePlayer().setVolume(ambiencevolume);
                        thisession.Root.getPlayer().AmbienceVolume.setValue(ambiencevolume);
                        Double value = thisession.Root.getPlayer().AmbienceVolume.getValue() * 100;
                        thisession.Root.getPlayer().AmbienceVolumePercentage.setText(value.intValue() + "%");
                        thisession.Root.getPlayer().AmbienceVolume.setDisable(true);
                    }
                };
                fadeinambience.setOnFinished(event -> {
                    thisession.Root.getPlayer().AmbienceVolume.setDisable(false);
                    thisession.Root.getPlayer().AmbienceVolume.valueProperty().bindBidirectional(getCurrentAmbiencePlayer().volumeProperty());
                    thisession.Root.getPlayer().AmbienceVolume.setOnMouseDragged(event1 -> {
                        thisession.Root.getOptions().getSessionOptions().setAmbiencevolume(thisession.Root.getPlayer().AmbienceVolume.getValue());
                        Double value = thisession.Root.getPlayer().AmbienceVolume.getValue() * 100;
                        thisession.Root.getPlayer().AmbienceVolumePercentage.setText(value.intValue() + "%");
                    });
                });
            }
        }
        // Set Up Audio Fade Out
        if (fadeoutduration > 0.0) {
            fadeoutentrainment = new Transition() {
                {setCycleDuration(new Duration(fadeoutduration * 1000));}
                @Override
                protected void interpolate(double frac) {
                    double entvol = thisession.Root.getOptions().getSessionOptions().getEntrainmentvolume();
                    double entrainmentvolume = frac * entvol;
                    double fadeoutvolume = entvol - entrainmentvolume;
                    getCurrentEntrainmentPlayer().setVolume(fadeoutvolume);
                    thisession.Root.getPlayer().EntrainmentVolume.setValue(fadeoutvolume);
                    thisession.Root.getPlayer().EntrainmentVolume.setDisable(true);
                    Double value = thisession.Root.getPlayer().EntrainmentVolume.getValue() * 100;
                    thisession.Root.getPlayer().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                }
            };
            if (ambienceenabled) {
                fadeoutambience = new Transition() {
                    {setCycleDuration(new Duration(fadeoutduration * 1000));}
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
                    }
                };
            }
        }
        entrainmentplayer = new MediaPlayer(entrainments.getSelectedEntrainment(entrainmentplaycount).getMedia());
        entrainmentplayer.setVolume(0.0);
        entrainmentplayer.setOnPlaying(() -> {
            if (entrainmentplaycount == 0) {fadeinentrainment.play();}
        });
        entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
        entrainmentplayer.setOnError(this::entrainmenterror);
        entrainmentplayer.play();
        if (ambienceenabled) {
            ambienceplayer = new MediaPlayer(ambiences.getSelectedAmbience(ambienceplaycount).getMedia());
            ambienceplayer.setVolume(0.0);
            ambienceplayer.setOnPlaying(() -> fadeinambience.play());
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            ambienceplayer.play();
        }
        Double millistillfadeout = (getdurationinseconds() * 1000) - (fadeoutduration * 1000);
        fadeouttimeline = new Timeline(new KeyFrame(Duration.millis(millistillfadeout), ae -> startfadeout()));
        fadeouttimeline.play();
        cutorelementtimeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> tick()));
        cutorelementtimeline.setCycleCount(Animation.INDEFINITE);
        cutorelementtimeline.play();
    }
    public void resume() {
        entrainmentplayer.play();
        if (ambienceenabled) {ambienceplayer.play();}
        cutorelementtimeline.play();
        if (secondselapsed <= thisession.Root.getOptions().getSessionOptions().getFadeinduration()) {fadeinentrainment.play();}
        if (secondselapsed >= getdurationinseconds() - thisession.Root.getOptions().getSessionOptions().getFadeoutduration()) {fadeoutentrainment.play();}
        fadeouttimeline.play();
    }
    public void pause() {
        entrainmentplayer.pause();
        if (ambienceenabled) {ambienceplayer.pause();}
        cutorelementtimeline.pause();
        if (secondselapsed <= thisession.Root.getOptions().getSessionOptions().getFadeinduration()) {fadeinentrainment.pause();}
        if (secondselapsed >= getdurationinseconds() - thisession.Root.getOptions().getSessionOptions().getFadeoutduration()) {fadeoutentrainment.pause();}
        fadeouttimeline.pause();
    }
    public void stop() {
        entrainmentplayer.stop();
        entrainmentplayer.dispose();
        if (ambienceenabled) {
            ambienceplayer.stop();
            ambienceplayer.dispose();
        }
        cutorelementtimeline.stop();
        fadeouttimeline.stop();
    }
    public void tick() {
        if (entrainmentplayer.getStatus() == MediaPlayer.Status.PLAYING) {secondselapsed++;}
    }
    public void playnextentrainment() throws IndexOutOfBoundsException {
        try {
            entrainmentplaycount++;
            entrainmentplayer.dispose();
            entrainmentplayer = new MediaPlayer(entrainments.getSelectedEntrainment(entrainmentplaycount).getMedia());
            entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
            entrainmentplayer.setOnError(this::entrainmenterror);
            entrainmentplayer.play();
        } catch (IndexOutOfBoundsException ignored) {}
    }
    public void playnextambience() throws IndexOutOfBoundsException {
        ambienceplaycount++;
        ambienceplayer.dispose();
        ambienceplayer = new MediaPlayer(ambiences.getSelectedAmbience(ambienceplaycount).getMedia());
        ambienceplayer.setOnEndOfMedia(this::playnextambience);
        ambienceplayer.setOnError(this::ambienceerror);
        ambienceplayer.play();
    }
    public void startfadeout() {
        thisession.Root.getPlayer().EntrainmentVolume.valueProperty().unbindBidirectional(getCurrentEntrainmentPlayer().volumeProperty());
        fadeoutentrainment.play();
        if (ambienceenabled) {
            thisession.Root.getPlayer().AmbienceVolume.valueProperty().unbindBidirectional(getCurrentAmbiencePlayer().volumeProperty());
            fadeoutambience.play();
        }
    }
    public void cleanup() {
        try {
            if (ambienceenabled) {getCurrentAmbiencePlayer().dispose();}
            getCurrentEntrainmentPlayer().dispose();
            fadeinentrainment.stop();
            fadeinambience.stop();
            fadeoutambience.stop();
            fadeinentrainment.stop();
            fadeouttimeline.stop();
            cutorelementtimeline.stop();
        } catch (Exception ignored) {}
    }

// Session Information Getters
    public Duration getdurationasobject() {return new Duration((double) getdurationinseconds() * 1000);}
    public int getdurationinseconds() {
        return duration * 60;
    }
    public int getdurationinminutes() {
        return duration;
    }
    public Double getdurationindecimalhours() {return Tools.convert_minstodecimalhours(getdurationinminutes(), 2);}
    public String getcurrenttimeformatted() {
        return Tools.format_secondsforplayerdisplay(getSecondselapsed());
    }
    public String gettotaltimeformatted() {return Tools.format_secondsforplayerdisplay(getdurationinseconds());}

// Error Handling
    protected void entrainmenterror() {}
    protected void ambienceerror() {}

// Reference Files
    protected File getReferenceFile() {
        PlayerWidget.ReferenceType referenceType = thisession.Root.getOptions().getSessionOptions().getReferencetype();
        if (referenceType == null) {return null;}
        if (referenceType == PlayerWidget.ReferenceType.html) {
            String name = this.name + ".html";
            return new File(Options.DIRECTORYREFERENCE, "html/" + name);
        } else if (referenceType == PlayerWidget.ReferenceType.txt) {
            String name = this.name + ".txt";
            return new File(Options.DIRECTORYREFERENCE, "txt/" + name);
        } else {return null;}
    }

}
