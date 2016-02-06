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
import kujiin.widgets.PlayerWidget;
import kujiin.xml.Options;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class Cut {
    private This_Session thisession;
    public String name;
    public int number;
    public boolean ramp;
    public int rampduration;
    public int duration;
    // Plackback Fields
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
    private int entrainmentplaycount;
    private int ambienceplaycount;
    private ArrayList<Media> entrainmentmedia;
    private ArrayList<Media> ambiencemedia;
    private MediaPlayer entrainmentplayer;
    private MediaPlayer ambienceplayer;
    private File finalcutexportfile;
    public boolean ambienceenabled;
    private Timeline cuttimeline;
    private int secondselapsed;
    private Animation fadeinentrainment;
    private Animation fadeoutentrainment;
    private Animation fadeinambience;
    private Animation fadeoutambience;
    private boolean entrainmentfadeoutplayed;
    private boolean ambiencefadeoutplayed;

    public Cut(int number, String name, Boolean ramp, int duration, This_Session thisession) {
        this.number = number;
        this.name = name;
        this.ramp = ramp;
        if (this.ramp) { this.rampduration = 2;}
        this.duration = duration;
        this.thisession = thisession;
        ambiencedirectory = new File(Options.directoryambience, name);
        tempentrainmenttextfile = new File(Options.directorytemp, "txt/" + name + "Ent.txt");
        tempentrainmentfile = new File(Options.directorytemp, "Entrainment/" + name + "Temp.mp3");
        finalentrainmentfile = new File(Options.directorytemp, "Entrainment/" + name + ".mp3");
        tempambiencetextfile = new File(Options.directorytemp, "txt/" + name + "Amb.txt");
        tempambiencefile = new File(Options.directorytemp, "Ambience/" + name + "Temp.mp3");
        finalambiencefile = new File(Options.directorytemp, "Ambience/" + name + ".mp3");
        setFinalcutexportfile(new File(Options.directorytemp, name + ".mp3"));
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
    public File getReferenceFile(PlayerWidget.ReferenceType referenceType) {
        if (referenceType == PlayerWidget.ReferenceType.html) {
            String name = this.name + ".html";
            return new File(Options.directoryreference, "html/" + name);
        } else if (referenceType == PlayerWidget.ReferenceType.txt) {
            String name = this.name + ".txt";
            return new File(Options.directoryreference, "txt/" + name);
        } else {
            return null;
        }
    }
    public void setCutstoplay(ArrayList<Cut> cutstoplay) {this.cutstoplay = cutstoplay;}
    public File getFinalcutexportfile() {
        return finalcutexportfile;
    }
    public void setFinalcutexportfile(File finalcutexportfile) {
        this.finalcutexportfile = finalcutexportfile;
    }

// Getters For Cut Information
    public Duration getDuration() {return new Duration((double) getdurationinseconds() * 1000);}
    public int getdurationinseconds() {
    int audiodurationinseconds;
    audiodurationinseconds = duration;
    if (number == 0 || number == 10) {audiodurationinseconds += rampduration;}
    return audiodurationinseconds * 60;
}
    public int getdurationinminutes() {
        int audiodurationinseconds;
        audiodurationinseconds = duration;
        if (number == 0 || number == 10) {audiodurationinseconds += rampduration;}
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
        } catch (NullPointerException ignored) {return false;}
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
        File rampin1 = new File(Options.directorytohramp, "3in1.mp3");
        File rampin2 = new File(Options.directorytohramp, "3in2.mp3");
        File rampout1 = new File(Options.directorytohramp, "3out1.mp3");
        File rampout2 = new File(Options.directorytohramp, "3out2.mp3");
        File rampoutspecial1 = new File(Options.directorytohramp, "3outpostsession1.mp3");
        File rampoutspecial2 = new File(Options.directorytohramp, "3outpostsession2.mp3");
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
            File thisfile = new File(Options.directorymaincuts, filename);
            entrainmentlist.add(thisfile);
        }
        for (int i = 0; i < singletimes; i++) {
            String filename = name + "1.mp3";
            File thisfile = new File(Options.directorymaincuts, filename);
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
        if (number == 0) {
            String rampupfirstname = "ar" + cutstoplay.get(1).number + rampduration + ".mp3";
            File ramptofirstcut = new File(Options.directoryrampup, rampupfirstname);
            entrainmentlist.add(ramptofirstcut);
        }
        if (number == 10) {
            String rampdowntopost =  "zr" +
                    cutstoplay.get(cutstoplay.size()-2).number + rampduration + ".mp3";
            File thisfile = new File(Options.directoryrampdown, rampdowntopost);
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

// Playback
    // ------- Playback Info -------- //
    public MediaPlayer getCurrentEntrainmentPlayer() {return entrainmentplayer;}
    public MediaPlayer getCurrentAmbiencePlayer() {return ambienceplayer;}
    public int getSecondselapsed() {return secondselapsed;}
    public String getcurrenttimeformatted() {return Tools.formatlengthshort(secondselapsed + 1);}
    public String gettotaltimeformatted() {return Tools.formatlengthshort(getdurationinseconds());}
    public void updatecuttime() {
        if (entrainmentplayer.getStatus() == MediaPlayer.Status.PLAYING) {
            System.out.println(getCurrentEntrainmentPlayer().getVolume());
//
//  try {
            // TODO Use Javafx timeline to fade in fadeout audio (IF not 0.0)
//                Double fadeinduration =
//                Double fadeoutduration = thisession.Root.getOptions().getFadeoutduration();
//                if (fadeinduration > 0.0 && secondselapsed <= fadeoutduration) {
//                    if (! fadeinplayed) {
//
//                        fadeinplayed = true;
//                    }
////                    double entrainmentincrement = thisession.getSessionEntrainmentVolume() / PlayerWidget.FADEINDURATION;
////                    double entrainmentvolume = secondselapsed * entrainmentincrement;
////                    getCurrentEntrainmentPlayer().setVolume(entrainmentvolume);
////                    if (ambienceenabled) {
////                        double ambienceincrement = thisession.getSessionAmbienceVolume() / PlayerWidget.FADEINDURATION;
////                        double ambiencevolume = secondselapsed * ambienceincrement;
////                        getCurrentAmbiencePlayer().setVolume(ambiencevolume);
////                    }
//                }
//                else if (secondselapsed >= getdurationinseconds() - PlayerWidget.FADEOUTDURATION) {
//                    int secondsleft = getdurationinseconds() - secondselapsed;
//                    double entrainmentincrement = thisession.getSessionEntrainmentVolume() / PlayerWidget.FADEOUTDURATION;
//                    double entrainmentvolume = secondsleft * entrainmentincrement;
//                    getCurrentEntrainmentPlayer().setVolume(entrainmentvolume);
//                    if (ambienceenabled) {
//                        double ambienceincrement = thisession.getSessionAmbienceVolume() / PlayerWidget.FADEOUTDURATION;
//                        double ambiencevolume =  secondsleft * ambienceincrement;
//                        getCurrentAmbiencePlayer().setVolume(ambiencevolume);
//                    }
//                } else {
//                if (ambienceenabled) {getCurrentAmbiencePlayer().setVolume(thisession.getSessionAmbienceVolume());}
//                getCurrentEntrainmentPlayer().setVolume(thisession.getSessionEntrainmentVolume());
//                }
//            } catch (RuntimeException ignored) {}
            secondselapsed++;
//        }
        }
    }
    // --------- Controls ---------- //
    public void start() {
        entrainmentplaycount = 0;
        ambienceplaycount = 0;
        entrainmentplayer = new MediaPlayer(entrainmentmedia.get(entrainmentplaycount));
        entrainmentplayer.play();
        entrainmentplayer.setVolume(0.0);
        double fadeinduration = thisession.Root.getOptions().getSessionOptions().getFadeinduration();
        double fadeoutduration = thisession.Root.getOptions().getSessionOptions().getFadeoutduration();
    // Set Entrainment Fadein
        if (fadeinduration > 0.0) {
            entrainmentplayer.setOnPlaying(() -> {
                fadeinentrainment = new Transition() {
                    {setCycleDuration(new Duration(fadeinduration * 1000));}
                    @Override
                    protected void interpolate(double frac) {
                        double entrainmentvolume = frac * thisession.getSessionEntrainmentVolume();
                        getCurrentEntrainmentPlayer().setVolume(entrainmentvolume);
                    }
                };
                fadeinentrainment.play();
            });
        }
    // Set Entrainment Fade Out
        entrainmentplayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            double fadeoutstarttime = getdurationinseconds() - fadeoutduration;
            if (newValue.toMillis() >= fadeoutstarttime * 1000 && ! entrainmentfadeoutplayed) {
                new Timeline(new KeyFrame(Duration.millis(fadeoutstarttime * 1000), event -> new Transition() {
                    {setCycleDuration(new Duration(fadeoutduration * 1000));}
                    @Override
                    protected void interpolate(double frac) {
                        double entrainmentvolume = frac * thisession.getSessionEntrainmentVolume();
                        getCurrentEntrainmentPlayer().setVolume(entrainmentvolume);
                    }
                }.play()));
                entrainmentfadeoutplayed = true;
            } else {entrainmentfadeoutplayed = false;}
        });
        entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
        if (ambienceenabled) {
            ambienceplayer = new MediaPlayer(ambiencemedia.get(ambienceplaycount));
            ambienceplayer.play();
            ambienceplayer.setVolume(0.0);
    // Set Ambience Fade In
            ambienceplayer.setOnPlaying(() -> new Transition() {
                {setCycleDuration(new Duration(thisession.Root.getOptions().getSessionOptions().getFadeinduration() * 1000));}
                @Override
                protected void interpolate(double frac) {
                    double ambiencevolume = frac * thisession.getSessionAmbienceVolume();
                    getCurrentAmbiencePlayer().setVolume(ambiencevolume);
                }
            }.play());
    // Set Ambience Fade Out
            ambienceplayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                double fadeoutstarttime = getdurationinseconds() - fadeoutduration;
                if (newValue.toMillis() >= fadeoutstarttime * 1000 && ! ambiencefadeoutplayed) {
                    new Timeline(new KeyFrame(Duration.millis(fadeoutstarttime * 1000), event -> {
                        fadeoutambience = new Transition() {
                            {setCycleDuration(new Duration(fadeoutduration * 1000));}
                            @Override
                            protected void interpolate(double frac) {
                                // TODO Get Fadeout Working (Correct The Math Here And Above In Entrainment FadeOut)

                            }
                        };
                        fadeoutambience.setAutoReverse(true);
                        fadeoutambience.play();
                    }));
                    ambiencefadeoutplayed = true;
                } else {ambiencefadeoutplayed = false;}
            });
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::playnextfilebecauseoferror);
        }
        cuttimeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> updatecuttime()));
        cuttimeline.setCycleCount(Animation.INDEFINITE);
        cuttimeline.play();
    }
    public void pause() {
        entrainmentplayer.pause();
        if (ambienceenabled) {ambienceplayer.pause();}
        cuttimeline.pause();
    }
    public void resume() {
        entrainmentplayer.play();
        if (ambienceenabled) {ambienceplayer.play();}
        cuttimeline.play();
    }
    public void stop() {
        entrainmentplayer.stop();
        entrainmentplayer.dispose();
        if (ambienceenabled) {
            ambienceplayer.stop();
            ambienceplayer.dispose();
        }
    }
    public void playnextentrainment() {
        entrainmentplaycount++;
        entrainmentplayer.dispose();
        entrainmentplayer = new MediaPlayer(entrainmentmedia.get(entrainmentplaycount));
        entrainmentplayer.play();
//        entrainmentplayer.setVolume(Root.ENTRAINMENTVOLUME);
        entrainmentplayer.setVolume(0.0);
        entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
    }
    public void playnextambience() {
        // Maybe timing is off in creating ambience lists?
        try {
            ambienceplaycount++;
            ambienceplayer.dispose();
            ambienceplayer = new MediaPlayer(ambiencemedia.get(ambienceplaycount));
            ambienceplayer.play();
//        ambienceplayer.setVolume(Root.AMBIENCEVOLUME);
            ambienceplayer.setVolume(0.0);
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
        } catch (IndexOutOfBoundsException ignored) {
            System.out.println("Out Of Bounds In " + this.name + "Ambience List: ");
            for (Media i : ambiencemedia) {System.out.println(i.getSource() + i.getDuration().toSeconds());}
        }
    }
    public void setentrainmentvolume(Double volume) {

    }
    public void setambiencevolume(Double volume) {

    }
    // ------ Error Handling ------- //
    public void entrainmenterror() {
        // Pause Ambience If Exists
        if (Tools.getanswerdialog("Confirmation", "An Error Occured While Playing " + name +
                "'s Entrainment. Problem File Is: '" + getCurrentEntrainmentPlayer().getMedia().getSource() + "'",
                "Retry Playing This File? (Pressing Cancel Will Completely Stop Session Playback)")) {
                entrainmentplayer.stop();
                entrainmentplayer.play();
                entrainmentplayer.setOnError(this::entrainmenterror);
        } else {thisession.error_endplayback();}
    }
    public void ambienceerror() {
        if (Tools.getanswerdialog("Confirmation", "An Error Occured While Playing " + name +
                        "'s Entrainment. Problem File Is: '" + getCurrentEntrainmentPlayer().getMedia().getSource() + "'",
                "Retry Playing This File? (Pressing Cancel Will Completely Stop Session Playback)")) {
            entrainmentplayer.stop();
            entrainmentplayer.play();
            entrainmentplayer.setOnError(this::entrainmenterror);
        } else {thisession.error_endplayback();}
    }
    public void playnextfilebecauseoferror() {
        if (ambienceplayer.getStatus() != MediaPlayer.Status.PLAYING) {playnextambience();}
        if (entrainmentplayer.getStatus() != MediaPlayer.Status.PLAYING) {playnextentrainment();}
    }

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
        File entrainmentfile = new File(Options.directorytemp, "Entrainment/" + name + ".mp3");
        cutisgood = entrainmentfile.exists();
        if (ambienceenabled) {
            File ambiencefile = new File(Options.directorytemp, "Ambience/" + name + ".mp3");
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

// Goals / Total Progress

}
