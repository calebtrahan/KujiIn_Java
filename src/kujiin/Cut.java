package kujiin;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.widgets.PlayerWidget;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class Cut {
    private This_Session thisession;
    public String name;
    public int number;
    public boolean ramp;
    public int rampduration;
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
    private int entrainmentplaycount;
    private int ambienceplaycount;
    private ArrayList<Media> entrainmentmedia;
    private ArrayList<Media> ambiencemedia;
    private MediaPlayer entrainmentplayer;
    private MediaPlayer ambienceplayer;
    public boolean ambienceenabled;
    private Timeline cuttimeline;
    private int secondselapsed;

    public Cut(int number, String name, Boolean ramp, int duration, This_Session thisession) {
        this.number = number;
        this.name = name;
        this.ramp = ramp;
        if (this.ramp) { this.rampduration = 2;} // TODO This Can Be Set In Options
        this.duration = duration;
        this.thisession = thisession;
        ambiencedirectory = new File(This_Session.directoryambience, name);
        tempentrainmenttextfile = new File(This_Session.directorytemp, "txt/" + name + "Ent.txt");
        tempentrainmentfile = new File(This_Session.directorytemp, "Entrainment/" + name + "Temp.mp3");
        finalentrainmentfile = new File(This_Session.directorytemp, "Entrainment/" + name + ".mp3");
        tempambiencetextfile = new File(This_Session.directorytemp, "txt/" + name + "Amb.txt");
        tempambiencefile = new File(This_Session.directorytemp, "Ambience/" + name + "Temp.mp3");
        finalambiencefile = new File(This_Session.directorytemp, "Ambience/" + name + ".mp3");
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
            return new File(This_Session.directoryreference, "html/" + name);
        } else if (referenceType == PlayerWidget.ReferenceType.txt) {
            String name = this.name + ".txt";
            return new File(This_Session.directoryreference, "txt/" + name);
        } else {
            return null;
        }
    }
    public void setCutstoplay(ArrayList<Cut> cutstoplay) {this.cutstoplay = cutstoplay;}

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
        File rampin1 = new File(This_Session.directorytohramp, "3in1.mp3");
        File rampin2 = new File(This_Session.directorytohramp, "3in2.mp3");
        File rampout1 = new File(This_Session.directorytohramp, "3out1.mp3");
        File rampout2 = new File(This_Session.directorytohramp, "3out2.mp3");
        File rampoutspecial1 = new File(This_Session.directorytohramp, "3outpostsession1.mp3");
        File rampoutspecial2 = new File(This_Session.directorytohramp, "3outpostsession2.mp3");
        // TODO Add 5 Min Entrainment Files, And Refactor This Here
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
            File thisfile = new File(This_Session.directorymaincuts, filename);
            entrainmentlist.add(thisfile);
        }
        for (int i = 0; i < singletimes; i++) {
            String filename = name + "1.mp3";
            File thisfile = new File(This_Session.directorymaincuts, filename);
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
            File ramptofirstcut = new File(This_Session.directoryrampup, rampupfirstname);
            entrainmentlist.add(ramptofirstcut);
        }
        if (number == 10) {
            String rampdowntopost =  "zr" +
                    cutstoplay.get(cutstoplay.size()-2).number + rampduration + ".mp3";
            File thisfile = new File(This_Session.directoryrampdown, rampdowntopost);
            entrainmentlist.add(0, thisfile);
        }
        for (File i : entrainmentlist) {
            entrainmentmedia.add(new Media(i.toURI().toString()));
        }
        return entrainmentmedia.size() > 0;
    }
    public boolean buildAmbience() {
        // TODO Ambience Hangs On Higher Than 10 Numbers
        // TODO Set Really High Durations, So You Can See If It Works For Really Long Sessions
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
            try {
                if (secondselapsed <= PlayerWidget.FADEINDURATION) {
                    double entrainmentincrement = thisession.getSessionEntrainmentVolume() / PlayerWidget.FADEINDURATION;
                    double entrainmentvolume = secondselapsed * entrainmentincrement;
                    getCurrentEntrainmentPlayer().setVolume(entrainmentvolume);
                    if (ambienceenabled) {
                        double ambienceincrement = thisession.getSessionAmbienceVolume() / PlayerWidget.FADEINDURATION;
                        double ambiencevolume = secondselapsed * ambienceincrement;
                        getCurrentAmbiencePlayer().setVolume(ambiencevolume);
                    }
                }
                else if (secondselapsed >= getdurationinseconds() - PlayerWidget.FADEOUTDURATION) {
                    int secondsleft = getdurationinseconds() - secondselapsed;
                    double entrainmentincrement = thisession.getSessionEntrainmentVolume() / PlayerWidget.FADEOUTDURATION;
                    double entrainmentvolume = secondsleft * entrainmentincrement;
                    getCurrentEntrainmentPlayer().setVolume(entrainmentvolume);
                    if (ambienceenabled) {
                        double ambienceincrement = thisession.getSessionAmbienceVolume() / PlayerWidget.FADEOUTDURATION;
                        double ambiencevolume =  secondsleft * ambienceincrement;
                        getCurrentAmbiencePlayer().setVolume(ambiencevolume);
                    }
                } else {
                        if (ambienceenabled) {getCurrentAmbiencePlayer().setVolume(thisession.getSessionAmbienceVolume());}
                        getCurrentEntrainmentPlayer().setVolume(thisession.getSessionEntrainmentVolume());
                }
            } catch (RuntimeException ignored) {}
            secondselapsed++;
        }
    }
    // --------- Controls ---------- //
    public void start() {
        entrainmentplaycount = 0;
        ambienceplaycount = 0;
        entrainmentplayer = new MediaPlayer(entrainmentmedia.get(entrainmentplaycount));
        entrainmentplayer.play();
        entrainmentplayer.setVolume(0.0);
        entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
        if (ambienceenabled) {
            ambienceplayer = new MediaPlayer(ambiencemedia.get(ambienceplaycount));
            ambienceplayer.play();
            ambienceplayer.setVolume(0.0);
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
        if (ambienceenabled) {ambienceplayer.pause();}
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
        // TODO Throwing Index Out Of Bounds Exception In Zen Ambience (And Not Playing Cause It Can't Load)
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
    public boolean export() {
        concatanateentrainment();
        if (ambienceenabled) {
            concatanateambience();
            mixentrainmentandambience();
        }
        return false;
    }
    public boolean concatanateentrainment() {
    // Write Entrainment List To File For FFMPEG To Use
        try {
            PrintWriter writer = new PrintWriter(tempentrainmenttextfile);
            for (File k : entrainmentlist) {writer.println("file " + "\'" + k.getAbsolutePath() + "\'");}
            writer.close();
            // Call FFMpeg To Concatenate The Files
            ArrayList<String> cmdarraylist = new ArrayList<>();
            cmdarraylist.add("ffmpeg");
            cmdarraylist.add("-f");
            cmdarraylist.add("concat");
            cmdarraylist.add("-i");
            cmdarraylist.add(tempentrainmenttextfile.getAbsolutePath());
            cmdarraylist.add("-c");
            cmdarraylist.add("copy");
            cmdarraylist.add(finalentrainmentfile.getAbsolutePath());
            ProcessBuilder cmdlist = new ProcessBuilder(cmdarraylist);
            int count = 0;
            while (true) {
                final Process p;
                p = cmdlist.start();
                p.waitFor();
                if (Tools.checkaudioduration(finalentrainmentfile, getdurationinseconds())) {break;}
                else {
                    if (count > 3) {return false;}
                    else {count++;}
                }
            }
//            CommandLine audiofilterentrainmentcmdlist = new CommandLine("ffmpeg");
//            audiofilterentrainmentcmdlist.addArgument("-i");
//            audiofilterentrainmentcmdlist.addArgument(tempentrainmentfile.getAbsolutePath());
//            audiofilterentrainmentcmdlist.addArgument("-af");
//            audiofilterentrainmentcmdlist.addArgument("afade=t=in:ss=0:d=10");
//            audiofilterentrainmentcmdlist.addArgument("-af");
//            audiofilterentrainmentcmdlist.addArgument("afade=t=out:st=" + (currentcut.getdurationinseconds() - 10) + ":d=10"); // INVALID ARGUMENT!
//            audiofilterentrainmentcmdlist.addArgument(finalentrainmentfile.getAbsolutePath());
//            System.out.println(audiofilterentrainmentcmdlist.toString());
//            ByteArrayOutputStream out=new ByteArrayOutputStream();
//            ByteArrayOutputStream err=new ByteArrayOutputStream();
//            DefaultExecutor executor = new DefaultExecutor();
//            PumpStreamHandler handler=new PumpStreamHandler(out, err);
//            executor.setStreamHandler(handler);
//            int exitValue = executor.execute(audiofilterentrainmentcmdlist);
            tempentrainmentfile.delete();
            return true;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException ignored) {
            return false;
        }

    }
    public boolean concatanateambience() {
    // Write Ambience List To A Temp Text File For FFMPEG
        Tools.erasetextfile(tempambiencetextfile);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(tempambiencetextfile);
            for (File k : ambiencefiles) {writer.println("file " + "\'" + k.getAbsolutePath() + "\'");}
            writer.close();
        } catch (FileNotFoundException ignored) {}
//        return tempambiencetextfile.exists();
    // Call FFMPEG TO Create File
        ArrayList<String> concatenateambiencelist = new ArrayList<>();
        concatenateambiencelist.add("ffmpeg");
        concatenateambiencelist.add("-f");
        concatenateambiencelist.add("concat");
        concatenateambiencelist.add("-i");
        concatenateambiencelist.add(tempambiencetextfile.getAbsolutePath());
        concatenateambiencelist.add("-c");
        concatenateambiencelist.add("copy");
        concatenateambiencelist.add(tempambiencefile.getAbsolutePath());
        String secondsoffiletokeep = Double.toString((double) getdurationinseconds());
        ArrayList<String> adjustlengthlist = new ArrayList<>();
        adjustlengthlist.add("ffmpeg");
        adjustlengthlist.add("-t");
        adjustlengthlist.add(secondsoffiletokeep);
        adjustlengthlist.add("-i");
        adjustlengthlist.add(tempambiencefile.getAbsolutePath());
        adjustlengthlist.add("-acodec");
        adjustlengthlist.add("copy");
        adjustlengthlist.add("-y");
        adjustlengthlist.add(finalambiencefile.getAbsolutePath());
        int count = 0;
        while (true) {
            if (count > 0) {
                tempambiencefile.delete();
                finalambiencefile.delete();
                // buildAmbience();
                // writeambiencelisttotextfile();
            }
            ProcessBuilder cmdlist = new ProcessBuilder(adjustlengthlist);
            final Process adjustlength;
            ProcessBuilder concatenateambienceprocessbuilder = new ProcessBuilder(concatenateambiencelist);
            final Process concatenate;
            try {
                concatenate = concatenateambienceprocessbuilder.start();
                if (count > 0) {
                    BufferedReader input = new BufferedReader(new InputStreamReader(concatenate.getErrorStream()));
                    String line;
                    while ((line = input.readLine()) != null) {
                        System.out.println(line);
                    }
                }
                concatenate.waitFor();
                adjustlength = cmdlist.start();
                adjustlength.waitFor();
            } catch (IOException | InterruptedException e) {e.printStackTrace();}
            boolean filegood = Tools.checkaudioduration(finalambiencefile, getdurationinseconds());
            if (filegood) {return true;}
            else {
                if (count >= 3) {return false;}
                else {count++;}
            }
        }
    // Apply Audio Filters
        //        CommandLine audiofilterentrainmentcmdlist = new CommandLine("ffmpeg");
//        audiofilterentrainmentcmdlist.addArgument("-i");
//        audiofilterentrainmentcmdlist.addArgument(adjustedlengthfile.getAbsolutePath());
//        audiofilterentrainmentcmdlist.addArgument("-af");
//        audiofilterentrainmentcmdlist.addArgument("afade=t=in:ss=0:d=10,afade=t=out:st=" + (currentcut.getdurationinseconds() - 10) + ":d=10");
//        audiofilterentrainmentcmdlist.addArgument(finalfile.getAbsolutePath());
//        java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream();
//        java.io.ByteArrayOutputStream err=new java.io.ByteArrayOutputStream();
//        DefaultExecutor executor = new DefaultExecutor();
//        PumpStreamHandler handler=new PumpStreamHandler(out,err);
//        executor.setStreamHandler(handler);
//        try {int exitValue = executor.execute(audiofilterentrainmentcmdlist);
//        } catch (IOException ignored) {}
//        try {
//            FileUtils.forceDelete(adjustedlengthfile); FileUtils.forceDelete(temptextfile);} catch (IOException ignored) {}
//        System.out.println("Done Applying Audio Filters For " + currentcut.name);
    }
    public boolean mixentrainmentandambience() {return false;}
    public File getmixedfile() {return null;}
    public Boolean sessionreadyforFinalExport(boolean ambienceenabled) {
        boolean cutisgood;
        File entrainmentfile = new File(This_Session.directorytemp, "Entrainment/" + name + ".mp3");
        cutisgood = entrainmentfile.exists();
        if (ambienceenabled) {
            File ambiencefile = new File(This_Session.directorytemp, "Ambience/" + name + ".mp3");
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
