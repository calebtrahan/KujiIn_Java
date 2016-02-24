package kujiin;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.interfaces.Playable;
import kujiin.widgets.PlayerWidget;
import kujiin.widgets.ProgressAndGoalsWidget;
import kujiin.xml.Options;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class Cut implements Playable {
    private This_Session thisession;
    public String name;
    public int number;
    public int duration;
    private File tempentrainmenttextfile;
    private File tempambiencetextfile;
    private File tempentrainmentfile;
    private File tempambiencefile;
    private File finalentrainmentfile;
    private File finalambiencefile;
    private ArrayList<File> entrainmentlist;
    private ArrayList<File> ambiencelist;
    private ArrayList<Cut> cutstoplay;
    private ArrayList<File> ambiencefiles;
    private ArrayList<Double> ambiencefiledurations;
    private File ambiencedirectory;
    private double totalambienceduration;
    // Playable Fields
    private int entrainmentplaycount;
    private int ambienceplaycount;
    private ArrayList<Media> entrainmentmedia;
    private ArrayList<Media> ambiencemedia;
    private MediaPlayer entrainmentplayer;
    private MediaPlayer ambienceplayer;
    private File finalcutexportfile;
    public boolean ambienceenabled;
    private Timeline cuttimeline;
    private Timeline fadeouttimeline;
    private int secondselapsed;
    private Animation fadeinentrainment;
    private Animation fadeoutentrainment;
    private Animation fadeinambience;
    private Animation fadeoutambience;


    public Cut(int number, String name, int duration, This_Session thisession) {
        this.number = number;
        this.name = name;
        this.duration = duration;
        this.thisession = thisession;
        ambiencedirectory = new File(Options.DIRECTORYAMBIENCE, name);
        tempentrainmenttextfile = new File(Options.DIRECTORYTEMP, "txt/" + name + "Ent.txt");
        tempentrainmentfile = new File(Options.DIRECTORYTEMP, "Entrainment/" + name + "Temp.mp3");
        finalentrainmentfile = new File(Options.DIRECTORYTEMP, "Entrainment/" + name + ".mp3");
        tempambiencetextfile = new File(Options.DIRECTORYTEMP, "txt/" + name + "Amb.txt");
        tempambiencefile = new File(Options.DIRECTORYTEMP, "Ambience/" + name + "Temp.mp3");
        finalambiencefile = new File(Options.DIRECTORYTEMP, "Ambience/" + name + ".mp3");
        setFinalcutexportfile(new File(Options.DIRECTORYTEMP, name + ".mp3"));
    }

// Getters And Setters
    public void setDuration(int newduration) {
        this.duration = newduration;
    }
    public double getTotalambienceduration() {
        return totalambienceduration;
    }
    public void setTotalambienceduration(double totalambienceduration) {
        this.totalambienceduration = totalambienceduration;
    }
    public void setAmbienceenabled(boolean ambienceenabled) {this.ambienceenabled = ambienceenabled;}
    public boolean isAmbienceenabled() {return ambienceenabled;}
    public File getReferenceFile() {
        PlayerWidget.ReferenceType referenceType = thisession.Root.getOptions().getSessionOptions().getReferencetype();
        if (referenceType != null) {
            if (referenceType == PlayerWidget.ReferenceType.html) {
                String name = this.name + ".html";
                return new File(Options.DIRECTORYREFERENCE, "html/" + name);
            } else if (referenceType == PlayerWidget.ReferenceType.txt) {
                String name = this.name + ".txt";
                return new File(Options.DIRECTORYREFERENCE, "txt/" + name);
            }
            return null;
        } else {return null;}
    }
    public void setCutstoplay(ArrayList<Cut> cutstoplay) {this.cutstoplay = cutstoplay;}
    public File getFinalcutexportfile() {
        return finalcutexportfile;
    }
    public void setFinalcutexportfile(File finalcutexportfile) {
        this.finalcutexportfile = finalcutexportfile;
    }
    public int getSecondselapsed() {return secondselapsed;}

// Getters For Cut Information
    public Duration getDuration() {return new Duration((double) getdurationinseconds() * 1000);}
    public int getdurationinseconds() {
        int audiodurationinseconds;
        audiodurationinseconds = duration;
        if ((number == 0 || number == 10) && thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            audiodurationinseconds += thisession.Root.getOptions().getSessionOptions().getRampduration();
        }
        return audiodurationinseconds * 60;
    }
    public int getdurationinminutes() {
        int audiodurationinseconds;
        audiodurationinseconds = duration;
        if ((number == 0 || number == 10) && thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            audiodurationinseconds += thisession.Root.getOptions().getSessionOptions().getRampduration();
        }
        return audiodurationinseconds;
    }

// Creation
    public boolean getambienceindirectory() {
        ambiencefiles = new ArrayList<>();
        ambiencefiledurations = new ArrayList<>();
        try {
            for (File i : ambiencedirectory.listFiles()) {
                double dur = Tools.getaudioduration(i);
                if (dur > 0.0) {
                    ambiencefiles.add(i);
                    ambiencefiledurations.add(dur);
                }
            }
        } catch (NullPointerException e) {new MainController.ExceptionDialog(thisession.Root, e).showAndWait(); return false;}
        return ambiencefiles.size() > 0;
    }
    public boolean hasenoughAmbience(int secondstocheck) {
        double a = 0;
        for (Double i : ambiencefiledurations) {a += i;}
        setTotalambienceduration(a);
        return a > (double) secondstocheck;
    }
    public boolean build(ArrayList<Cut> cutstoplay, boolean ambienceenabled) {
        setAmbienceenabled(ambienceenabled);
        setCutstoplay(cutstoplay);
        if (isAmbienceenabled()) {return buildEntrainment() && buildAmbience();}
        else {return buildEntrainment();}
    }
    public boolean buildEntrainment() {
        File rampin1 = new File(Options.DIRECTORYTOHRAMP, "3in1.mp3");
        File rampin2 = new File(Options.DIRECTORYTOHRAMP, "3in2.mp3");
        File rampout1 = new File(Options.DIRECTORYTOHRAMP, "3out1.mp3");
        File rampout2 = new File(Options.DIRECTORYTOHRAMP, "3out2.mp3");
        File rampoutspecial1 = new File(Options.DIRECTORYTOHRAMP, "3outpostsession1.mp3");
        File rampoutspecial2 = new File(Options.DIRECTORYTOHRAMP, "3outpostsession2.mp3");
        entrainmentlist = new ArrayList<>();
        entrainmentmedia = new ArrayList<>();
        int fivetimes = 0;
        int singletimes = 0;
        if (duration != 0) {
            fivetimes = duration / 5;
            singletimes = duration % 5;
        }
        if (number == 3) {
            int adjustedduration = duration;
            if (duration <= 5) adjustedduration -= 2;
            else adjustedduration -= 4;
            fivetimes = adjustedduration / 5;
            singletimes = adjustedduration % 5;
        }
        for (int i = 0; i < fivetimes; i++) {
            String filename = name + "5.mp3";
            File thisfile = new File(Options.DIRECTORYMAINCUTS, filename);
            entrainmentlist.add(thisfile);
        }
        for (int i = 0; i < singletimes; i++) {
            String filename = name + "1.mp3";
            File thisfile = new File(Options.DIRECTORYMAINCUTS, filename);
            entrainmentlist.add(thisfile);
        }
        Tools.shufflelist(entrainmentlist, 5);
        if (number == 3) {
            File rampinfile;
            File rampoutfile;
            if (duration <= 5) {rampinfile = rampin1; rampoutfile = rampout1;}
            else {rampinfile = rampin2; rampoutfile = rampout2;}
            if (cutstoplay.size() - cutstoplay.indexOf(this) <= 2) {
                if (duration <= 5) entrainmentlist.add(rampoutspecial1);
                else entrainmentlist.add(rampoutspecial2);
            } else {
                entrainmentlist.add(0, rampinfile);
                entrainmentlist.add(rampoutfile);
            }
        }
        if (number == 0 && thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            int rampdur = thisession.Root.getOptions().getSessionOptions().getRampduration();
            String rampupfirstname = "ar" + cutstoplay.get(1).number + rampdur + ".mp3";
            File ramptofirstcut = new File(Options.DIRECTORYRAMPUP, rampupfirstname);
            entrainmentlist.add(ramptofirstcut);
        }
        if (number == 10 && thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            int rampdur = thisession.Root.getOptions().getSessionOptions().getRampduration();
            String rampdowntopost =  "zr" +
                    cutstoplay.get(cutstoplay.size()-2).number + rampdur + ".mp3";
            File thisfile = new File(Options.DIRECTORYRAMPDOWN, rampdowntopost);
            entrainmentlist.add(0, thisfile);
        }
        for (File i : entrainmentlist) {
            entrainmentmedia.add(new Media(i.toURI().toString()));
        }
        return entrainmentmedia.size() > 0;
    }
    public boolean buildAmbience() {
        ambiencelist = new ArrayList<>();
        ambiencemedia = new ArrayList<>();
        Double currentduration = 0.0;
        Double sessionduration = (double) getdurationinseconds();
        // Ambience Is >= Session Duration
        if (hasenoughAmbience(getdurationinseconds())) {
            for (File i : ambiencefiles) {
                if (currentduration < sessionduration) {
                    ambiencelist.add(i);
                    currentduration += ambiencefiledurations.get(ambiencefiles.indexOf(i));
                } else {break;}
            }
    // Shuffle/Loop Ambience Randomly
        } else {
            Random randint = new Random();
            while (currentduration < sessionduration) {
                File tempfile = ambiencefiles.get(randint.nextInt(ambiencefiles.size() - 1));
                double tempduration = ambiencefiledurations.get(ambiencefiles.indexOf(tempfile));
                int size = ambiencelist.size();
                if (size == 0) {
                    ambiencelist.add(tempfile);
                    currentduration += tempduration;
                } else if (size == 1) {
                    ambiencelist.add(tempfile);
                    currentduration += tempduration;
                } else if (size == 2) {
                    if (!tempfile.equals(ambiencelist.get(size - 1))) {
                        ambiencelist.add(tempfile);
                        currentduration += tempduration;
                    }
                } else if (size == 3) {
                    if (!tempfile.equals(ambiencelist.get(size - 1)) && !tempfile.equals(ambiencelist.get(size - 2))) {
                        ambiencelist.add(tempfile);
                        currentduration += tempduration;
                    }
                } else if (size <= 5) {
                    if (!tempfile.equals(ambiencelist.get(size - 1)) && !tempfile.equals(ambiencelist.get(size - 2)) && !tempfile.equals(ambiencelist.get(size - 3))) {
                        ambiencelist.add(tempfile);
                        currentduration += tempduration;
                    }
                } else if (size > 5) {
                    if (!tempfile.equals(ambiencelist.get(size - 1)) && !tempfile.equals(ambiencelist.get(size - 2)) && !tempfile.equals(ambiencelist.get(size - 3)) && !tempfile.equals(ambiencelist.get(size - 4))) {
                        ambiencelist.add(tempfile);
                        currentduration += tempduration;
                    }
                }
            }
        }
        ambiencemedia.addAll(ambiencelist.stream().map(i -> new Media(i.toURI().toString())).collect(Collectors.toList()));
        return ambiencemedia.size() > 0;
    }
    public void reset() {
        if (entrainmentlist != null) entrainmentlist.clear();
        if (entrainmentmedia != null) entrainmentmedia.clear();
        if (ambiencelist != null) ambiencelist.clear();
        if (ambiencemedia != null) ambiencemedia.clear();
    }

// Playable Implementation
    public MediaPlayer getCurrentEntrainmentPlayer() {return entrainmentplayer;}
    public MediaPlayer getCurrentAmbiencePlayer() {return ambienceplayer;}
    public void tick() {
        if (entrainmentplayer.getStatus() == MediaPlayer.Status.PLAYING) {
            secondselapsed++;
            ProgressAndGoalsWidget progressAndGoalsWidget = thisession.Root.getProgressTracker();
            progressAndGoalsWidget.getSessions().getsession(progressAndGoalsWidget.getSessions().getSession().size() - 1).updatecutduration(number, secondselapsed / 60);
        }
    }
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
                    thisession.Root.EntrainmentVolume.setValue(entrainmentvolume);
                    Double value = thisession.Root.EntrainmentVolume.getValue() * 100;
                    thisession.Root.EntrainmentVolumePercentage.setText(value.intValue() + "%");
                    thisession.Root.EntrainmentVolume.setDisable(true);
                }
            };
            fadeinentrainment.setOnFinished(event -> {
                thisession.Root.EntrainmentVolume.setDisable(false);
                thisession.Root.EntrainmentVolume.valueProperty().bindBidirectional(getCurrentEntrainmentPlayer().volumeProperty());
                thisession.Root.EntrainmentVolume.setOnMouseDragged(event1 -> {
                    thisession.Root.getOptions().getSessionOptions().setEntrainmentvolume(thisession.Root.EntrainmentVolume.getValue());
                    Double value = thisession.Root.EntrainmentVolume.getValue() * 100;
                    thisession.Root.EntrainmentVolumePercentage.setText(value.intValue() + "%");
                });
            });
            if (ambienceenabled) {
                fadeinambience = new Transition() {
                    {setCycleDuration(new Duration(fadeinduration * 1000));}
                    @Override
                    protected void interpolate(double frac) {
                        double ambiencevolume = frac * thisession.Root.getOptions().getSessionOptions().getAmbiencevolume();
                        getCurrentAmbiencePlayer().setVolume(ambiencevolume);
                        thisession.Root.AmbienceVolume.setValue(ambiencevolume);
                        Double value = thisession.Root.AmbienceVolume.getValue() * 100;
                        thisession.Root.AmbienceVolumePercentage.setText(value.intValue() + "%");
                        thisession.Root.AmbienceVolume.setDisable(true);
                    }
                };
                fadeinambience.setOnFinished(event -> {
                    thisession.Root.AmbienceVolume.setDisable(false);
                    thisession.Root.AmbienceVolume.valueProperty().bindBidirectional(getCurrentAmbiencePlayer().volumeProperty());
                    thisession.Root.AmbienceVolume.setOnMouseDragged(event1 -> {
                        thisession.Root.getOptions().getSessionOptions().setAmbiencevolume(thisession.Root.AmbienceVolume.getValue());
                        Double value = thisession.Root.AmbienceVolume.getValue() * 100;
                        thisession.Root.AmbienceVolumePercentage.setText(value.intValue() + "%");
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
                    thisession.Root.EntrainmentVolume.setValue(fadeoutvolume);
                    thisession.Root.EntrainmentVolume.setDisable(true);
                    Double value = thisession.Root.EntrainmentVolume.getValue() * 100;
                    thisession.Root.EntrainmentVolumePercentage.setText(value.intValue() + "%");
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
                        thisession.Root.AmbienceVolume.setValue(fadeoutvolume);
                        thisession.Root.AmbienceVolume.setDisable(true);
                        Double value = thisession.Root.AmbienceVolume.getValue() * 100;
                        thisession.Root.AmbienceVolumePercentage.setText(value.intValue() + "%");
                    }
                };
            }
        }
        entrainmentplayer = new MediaPlayer(entrainmentmedia.get(entrainmentplaycount));
        entrainmentplayer.setVolume(0.0);
        entrainmentplayer.setOnPlaying(() -> {
            if (entrainmentplaycount == 0) {fadeinentrainment.play();}
        });
        entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
        entrainmentplayer.setOnError(this::entrainmenterror);
        entrainmentplayer.play();
        if (ambienceenabled) {
            ambienceplayer = new MediaPlayer(ambiencemedia.get(ambienceplaycount));
            ambienceplayer.setVolume(0.0);
            ambienceplayer.setOnPlaying(() -> fadeinambience.play());
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            ambienceplayer.play();
        }
        Double millistillfadeout = (getdurationinseconds() * 1000) - (fadeoutduration * 1000);
        fadeouttimeline = new Timeline(new KeyFrame(Duration.millis(millistillfadeout), ae -> startfadeout()));
        fadeouttimeline.play();
        cuttimeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> tick()));
        cuttimeline.setCycleCount(Animation.INDEFINITE);
        cuttimeline.play();
        thisession.Root.getProgressTracker().selectcut(number);
    }
    public void pause() {
        entrainmentplayer.pause();
        if (ambienceenabled) {ambienceplayer.pause();}
        cuttimeline.pause();
        if (secondselapsed <= thisession.Root.getOptions().getSessionOptions().getFadeinduration()) {fadeinentrainment.pause();}
        if (secondselapsed >= getdurationinseconds() - thisession.Root.getOptions().getSessionOptions().getFadeoutduration()) {fadeoutentrainment.pause();}
        fadeouttimeline.pause();
    }
    public void resume() {
        entrainmentplayer.play();
        if (ambienceenabled) {ambienceplayer.play();}
        cuttimeline.play();
        if (secondselapsed <= thisession.Root.getOptions().getSessionOptions().getFadeinduration()) {fadeinentrainment.play();}
        if (secondselapsed >= getdurationinseconds() - thisession.Root.getOptions().getSessionOptions().getFadeoutduration()) {fadeoutentrainment.play();}
        fadeouttimeline.play();
    }
    public void stop() {
        entrainmentplayer.stop();
        entrainmentplayer.dispose();
        if (ambienceenabled) {
            ambienceplayer.stop();
            ambienceplayer.dispose();
        }
        cuttimeline.stop();
        fadeouttimeline.stop();
    }
    public void playnextentrainment() {
        try {
            entrainmentplaycount++;
            entrainmentplayer.dispose();
            entrainmentplayer = new MediaPlayer(entrainmentmedia.get(entrainmentplaycount));
            entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
            entrainmentplayer.setOnError(this::entrainmenterror);
            entrainmentplayer.play();
        } catch (IndexOutOfBoundsException ignored) {}
    }
    public void playnextambience() {
        try {
            ambienceplaycount++;
            ambienceplayer.dispose();
            ambienceplayer = new MediaPlayer(ambiencemedia.get(ambienceplaycount));
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            ambienceplayer.play();
        } catch (IndexOutOfBoundsException ignored) {
            System.out.println("Out Of Bounds In " + this.name + "Ambience List: ");
            for (Media i : ambiencemedia) {System.out.println(i.getSource() + i.getDuration().toSeconds());}
        }
    }
    public void entrainmenterror() {
        System.out.println("Entrainment Error");
        // Pause Ambience If Exists
        if (Tools.getanswerdialog(thisession.Root, "Confirmation", "An Error Occured While Playing " + name +
                "'s Entrainment. Problem File Is: '" + getCurrentEntrainmentPlayer().getMedia().getSource() + "'",
                "Retry Playing This File? (Pressing Cancel Will Completely Stop Session Playback)")) {
                entrainmentplayer.stop();
                entrainmentplayer.play();
                entrainmentplayer.setOnError(this::entrainmenterror);
        } else {thisession.error_endplayback();}
    }
    public void ambienceerror() {
        System.out.println("Ambience Error!");
        // Pause Entrainment
        if (Tools.getanswerdialog(thisession.Root, "Confirmation", "An Error Occured While Playing " + name +
                        "'s Ambience. Problem File Is: '" + getCurrentAmbiencePlayer().getMedia().getSource() + "'",
                "Retry Playing This File? (Pressing Cancel Will Completely Stop Session Playback)")) {
            ambienceplayer.stop();
            ambienceplayer.play();
            ambienceplayer.setOnError(this::ambienceerror);
        } else {thisession.error_endplayback();}
    }
    public void startfadeout() {
        try {
            thisession.Root.EntrainmentVolume.valueProperty().unbindBidirectional(getCurrentEntrainmentPlayer().volumeProperty());
            fadeoutentrainment.play();
            if (ambienceenabled) {
                thisession.Root.AmbienceVolume.valueProperty().unbindBidirectional(getCurrentAmbiencePlayer().volumeProperty());
                fadeoutambience.play();
            }
        } catch (NullPointerException ignored) {}
    }
    // Playback Information Getters
    public String getcurrenttimeformatted() {return Tools.formatlengthshort(secondselapsed + 1);}
    public String gettotaltimeformatted() {return Tools.formatlengthshort(getdurationinseconds());}

// Export
    public Service<Boolean> getcutexportservice() {
        return new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        updateTitle("Building " + name);
                        System.out.println("Concatenating Entrainment For " + name);
                        updateMessage("Concatenating Entrainment Files");
                        Tools.concatenateaudiofiles(entrainmentlist, tempentrainmenttextfile, finalentrainmentfile);
                        if (isCancelled()) return false;
                        if (ambienceenabled) {
                            updateProgress(0.25, 1.0);
                            System.out.println("Concatenating Ambience For " + name);
                            updateMessage("Concatenating Ambience Files");
                            Tools.concatenateaudiofiles(ambiencelist, tempambiencetextfile, finalambiencefile);
                            if (isCancelled()) return false;
                            updateProgress(0.50, 1.0);
//                            System.out.println("Reducing Ambience Duration For " + name);
//                            updateMessage("Cutting Ambience Audio To Selected Duration");
//                            System.out.println("Final Ambience File" + finalambiencefile.getAbsolutePath());
//                            if (Tools.getaudioduration(finalambiencefile) > getdurationinseconds()) {
//                                Tools.trimaudiofile(finalambiencefile, getdurationinseconds());
//                            }
//                            if (isCancelled()) return false;
                            updateProgress(0.75, 1.0);
                            System.out.println("Mixing Final Audio For " + name);
                            updateMessage("Combining Entrainment And Ambience Files");
                            mixentrainmentandambience();
                            if (isCancelled()) return false;
                            updateProgress(1.0, 1.0);
                        } else {updateProgress(1.0, 1.0);}
                        return cutexportedsuccessfully();
                    }
                };
            }
        };
    }
    public boolean cutexportedsuccessfully() {
        if (ambienceenabled) {return finalambiencefile.exists() && finalentrainmentfile.exists();}
        else {return finalentrainmentfile.exists();}
    }
    public boolean mixentrainmentandambience() {
        if (! ambienceenabled) {
            try {
                FileUtils.copyFile(finalentrainmentfile, getFinalcutexportfile());
                return true;
            } catch (IOException e) {return false;}
        } else {return Tools.mixaudiofiles(new ArrayList<>(Arrays.asList(finalambiencefile, finalentrainmentfile)), getFinalcutexportfile());}
    }
    public Boolean sessionreadyforFinalExport(boolean ambienceenabled) {
        boolean cutisgood;
        File entrainmentfile = new File(Options.DIRECTORYTEMP, "Entrainment/" + name + ".mp3");
        cutisgood = entrainmentfile.exists();
        if (ambienceenabled) {
            File ambiencefile = new File(Options.DIRECTORYTEMP, "Ambience/" + name + ".mp3");
            cutisgood = ambiencefile.exists();
        }
        return cutisgood;
    }
    public void cleanuptempfiles() {
        if (tempambiencefile.exists()) {tempambiencefile.delete();}
        if (tempentrainmentfile.exists()) {tempentrainmentfile.delete();}
        if (tempentrainmenttextfile.exists()) {tempentrainmenttextfile.delete();}
        if (tempambiencetextfile.exists()) {tempambiencetextfile.delete();}
    }

}
