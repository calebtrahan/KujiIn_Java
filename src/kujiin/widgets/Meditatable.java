package kujiin.widgets;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.concurrent.Service;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.Cut;
import kujiin.Element;
import kujiin.This_Session;
import kujiin.Tools;
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
    protected Animation fadeinentrainment;
    protected Animation fadeoutentrainment;
    protected Animation fadeinambience;
    protected Animation fadeoutambience;
    protected Timeline fadeouttimeline;
    protected int secondselapsed;
    protected List<Cut> cutstoplay;
    protected List<Element> elementstoplay;
    protected List<Meditatable> allcutsorelementstoplay;
    protected Timeline cutorelementtimeline;
// Trackable Fields
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
        // TODO Convert To Millis For Duration Comparing
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
                    thisession.Root.getPlayer().StopButton.setDisable(true);
                    thisession.Root.getPlayer().PauseButton.setDisable(true);
                    thisession.Root.getPlayer().EntrainmentVolume.setValue(entrainmentvolume);
                    Double value = thisession.Root.getPlayer().EntrainmentVolume.getValue() * 100;
                    thisession.Root.getPlayer().EntrainmentVolumePercentage.setText(value.intValue() + "%");
                    thisession.Root.getPlayer().EntrainmentVolume.setDisable(true);
                }
            };
            fadeinentrainment.setOnFinished(event -> {
                thisession.Root.getPlayer().EntrainmentVolume.setDisable(false);
                thisession.Root.getPlayer().StopButton.setDisable(false);
                thisession.Root.getPlayer().PauseButton.setDisable(false);
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
                    thisession.Root.getPlayer().PauseButton.setDisable(true);
                    thisession.Root.getPlayer().StopButton.setDisable(true);
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
        entrainmentplayer = new MediaPlayer(new Media(entrainment.created_get(entrainmentplaycount).getFile().toURI().toString()));
        entrainmentplayer.setVolume(0.0);
        entrainmentplayer.setOnPlaying(() -> {
            if (entrainmentplaycount == 0) {fadeinentrainment.play();}
        });
        entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
        entrainmentplayer.setOnError(this::entrainmenterror);
        entrainmentplayer.play();
        if (ambienceenabled) {
            ambienceplayer = new MediaPlayer(new Media(ambience.created_get(ambienceplaycount).getFile().toURI().toString()));
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
            entrainmentplayer = new MediaPlayer(new Media(entrainment.created_get(entrainmentplaycount).getFile().toURI().toString()));
            entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
            entrainmentplayer.setOnError(this::entrainmenterror);
            entrainmentplayer.play();
        } catch (IndexOutOfBoundsException ignored) {}
    }
    public void playnextambience() throws IndexOutOfBoundsException {
        ambienceplaycount++;
        ambienceplayer.dispose();
        ambienceplayer = new MediaPlayer(new Media(ambience.created_get(ambienceplaycount).getFile().toURI().toString()));
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
        //                    Tools.audio_concatenatefiles(entrainmentlist, tempentrainmenttextfile, finalentrainmentfile);
        //                    if (isCancelled()) return false;
        //                    if (ambienceenabled) {
        //                        updateProgress(0.25, 1.0);
        //                        System.out.println("Concatenating Ambience For " + name);
        //                        updateMessage("Concatenating Ambience Files");
        //                        Tools.audio_concatenatefiles(ambiencelist, tempambiencetextfile, finalambiencefile);
        //                        if (isCancelled()) return false;
        //                        updateProgress(0.50, 1.0);
        ////                            System.out.println("Reducing Ambience Duration For " + name);
        ////                            updateMessage("Cutting Ambience Audio To Selected Duration");
        ////                            System.out.println("Final Ambience File" + finalambiencefile.getAbsolutePath());
        ////                            if (Tools.audio_getduration(finalambiencefile) > getdurationinseconds()) {
        ////                                Tools.audio_trimfile(finalambiencefile, getdurationinseconds());
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
//        } else {return Tools.audio_mixfiles(new ArrayList<>(Arrays.asList(finalambiencefile, finalentrainmentfile)), getFinalexportfile());}
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
