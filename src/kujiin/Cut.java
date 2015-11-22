package kujiin;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.util.lib.TimeUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

//enum AmbienceOption {General, Specific};

// TODO Move Big Ass Table Widget On Main Display (And Move To A Separate Dialog)
    // Create One Single Row (Or textfield) On The Main Display, Displaying:
        // Total Time Practiced
        // Total Sessions Practiced
        // Average Session Length
        // Most Recent Practiced Session?
    // Move Buttons (List Session, List Premature Sessions) With New Button (Display Each Cut Progress)

public class Cut {
    public String name;
    public int number;
    public Boolean ramp;
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
    private This_Session thisession;
    private Service<Void> entrainmentcreatorservice;
    private Service<Void> ambiencecreatorservice;
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

    public Cut(String alertname) {
        name = alertname;
    }
    // <-------------------------- GETTERS AND SETTERS ------------------------------------> //

    public int getdurationinseconds() {
        int audiodurationinseconds;
        audiodurationinseconds = duration;
        if (number == 0 || number == 10) {audiodurationinseconds += rampduration;}
        return audiodurationinseconds * 60;
    }
    public String getcreatedtext() {
        StringBuilder text = new StringBuilder();
        text.append(name).append(": ");
        text.append(Tools.minutestoformattedhoursandmins(getdurationinminutes()));
        if (number == 0 || number == 10) {
            text.append(" (").append(rampduration).append(" Min. Ramp").append(")");
        }
        return text.toString();
    }
    public int getdurationinminutes() {
        int audiodurationinseconds;
        audiodurationinseconds = duration;
        if (number == 0 || number == 10) {audiodurationinseconds += rampduration;}
        return audiodurationinseconds;
    }
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
    public Duration getthiscutduration() {return new Duration((double) getdurationinseconds() * 1000);}
    public int getSecondselapsed() {return secondselapsed;}

    // <-------------------------- GENERAL CREATION METHODS ---------------------------------> //

    public Boolean sessioniscreated(boolean ambienceenabled) {
        boolean cutisgood;
        File entrainmentfile = new File(This_Session.directorytemp, "Entrainment/" + name + ".mp3");
        cutisgood = entrainmentfile.exists();
        if (ambienceenabled) {
            File ambiencefile = new File(This_Session.directorytemp, "Ambience/" + name + ".mp3");
            cutisgood = ambiencefile.exists();
        }
        return cutisgood;
    }
    public boolean hasanyAmbience() {
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
    public boolean hasenoughAmbience() {
        double a = 0;
        for (Double i : ambiencefiledurations) {a += i;}
        setTotalambienceduration(a);
        return a > (double) getdurationinseconds();
    }
    public void cleanuptempfiles() {
        if (tempambiencefile.exists()) {tempambiencefile.delete();}
        if (tempentrainmentfile.exists()) {tempentrainmentfile.delete();}
        if (tempentrainmenttextfile.exists()) {tempentrainmenttextfile.delete();}
        if (tempambiencetextfile.exists()) {tempambiencetextfile.delete();}
    }
    public boolean create(ArrayList<Cut> cutstoplay, boolean ambienceenabled) {
        setAmbienceenabled(ambienceenabled);
        setCutstoplay(cutstoplay);
        if (isAmbienceenabled()) {return makeEntrainmentList() && makeAmbienceList();}
        else {return makeEntrainmentList();}
    }
    public boolean makeEntrainmentList() {
        entrainmentlist = new ArrayList<>();
        entrainmentmedia = new ArrayList<>();
        if (number == 0) {                                                                                              // Presession
            for (Integer z = 0; z < duration; z++) {
                String filename = name + ".mp3";
                File thisfile = new File(This_Session.directorymaincuts, filename);
                entrainmentlist.add(thisfile);
            }
            String rampupfirstname = "ar" + cutstoplay.get(1).number + rampduration + ".mp3";
            File ramptofirstcut = new File(This_Session.directoryrampup, rampupfirstname);
            entrainmentlist.add(ramptofirstcut);
        }
        else if (number == 10) {                                                                                        // PostSession
            String rampdowntopost =  "zr" +
                    cutstoplay.get(cutstoplay.size()-2).number + rampduration + ".mp3";
            File thisfile = new File(This_Session.directoryrampdown, rampdowntopost);
            entrainmentlist.add(thisfile);
            for (Integer g = 0; g < duration; g++) {
                String postname = name + ".mp3";
                File thatfile = new File(This_Session.directorymaincuts, postname);
                entrainmentlist.add(thatfile);
            }
        }
        else if (number == 3) {                                                                                         // TOH
            Boolean postsessionisnext = false;
            if (cutstoplay.size() - cutstoplay.indexOf(this) <= 2) {postsessionisnext = true;}
            File tohmainfile = new File(This_Session.directorymaincuts, "TOH.mp3");
            // TODO Add 3inpostsession for if going straight into TOH
            File rampin1 = new File(This_Session.directorytohramp, "3in1.mp3");
            File rampin2 = new File(This_Session.directorytohramp, "3in2.mp3");
            File rampout1 = new File(This_Session.directorytohramp, "3out1.mp3");
            File rampout2 = new File(This_Session.directorytohramp, "3out2.mp3");
            File rampoutspecial1 = new File(This_Session.directorytohramp, "3outpostsession1.mp3");
            File rampoutspecial2 = new File(This_Session.directorytohramp, "3outpostsession2.mp3");
            if (duration <= 5) {
                entrainmentlist.add(rampin1);
                for (int c = 0; c < duration - 2; c++) {
                    entrainmentlist.add(tohmainfile);
                }
                if (postsessionisnext) {entrainmentlist.add(rampoutspecial1);}
                else {entrainmentlist.add(rampout1);}
            } else {
                entrainmentlist.add(rampin2);
                for (int c = 0; c < duration - 4; c++) {
                    entrainmentlist.add(tohmainfile);
                }
                if (postsessionisnext) {entrainmentlist.add(rampoutspecial2);}
                else {entrainmentlist.add(rampout2);}
            }
        }
        else {                                                                                                      // All Other Cuts
            String filename = name + ".mp3";
            for (int c = 0; c < duration; c++) {
                File thisfile = new File(This_Session.directorymaincuts, filename);
                entrainmentlist.add(thisfile);
            }
        }
//        System.out.println(name + "'s Entrainment List");
//        for (File i : entrainmentlist) {
//            System.out.println(i.getName());
//        }
        for (File i : entrainmentlist) {entrainmentmedia.add(new Media(i.toURI().toString()));}
        return entrainmentmedia.size() > 0;
    }
    public boolean makeAmbienceList() {
        ambiencelist = new ArrayList<>();
        ambiencemedia = new ArrayList<>();
        // RANDOM!
        double currentduration = 0.0;
        double sessionduration = (double) getdurationinseconds();
        Random randint = new Random();
        while (currentduration < sessionduration) {
//            System.out.println("CurrentDuration (" + currentduration + ") Is Less Than SessionDuration (" + sessionduration + ")");
            File tempfile = ambiencefiles.get(randint.nextInt(ambiencefiles.size() - 1));
            double tempduration = ambiencefiledurations.get(ambiencefiles.indexOf(tempfile));
            if (hasenoughAmbience()) {
                for (int i = 0; i < ambiencefiles.size(); i++) {
                    if (currentduration < sessionduration) {
                        ambiencelist.add(ambiencefiles.get(i));
                        currentduration += tempduration;
                    } else {break;}
                }
            } else {
                if (ambiencelist.size() < ambiencefiles.size()) {                                                       // Shouldn't Need To Repeat
//                        System.out.println("Conditional 1");
                    if (!ambiencelist.contains(tempfile)) {
                        ambiencelist.add(tempfile);
                        currentduration += tempduration;
                    }
                } else {
                    int size = ambiencelist.size();
                    if (ambiencefiles.size() == 1) {
//                            System.out.println("Conditional 2");
                        ambiencelist.add(tempfile);
                        currentduration += tempduration;
                    } else if (ambiencefiles.size() < 3) {
//                            System.out.println("Conditional 3");
                        if ( ! tempfile.equals(ambiencelist.get(size - 1))) {
                            ambiencelist.add(tempfile);
                            currentduration += tempduration;
                        }
                    } else if (ambiencefiles.size() < 4) {
//                            System.out.println("Conditional 4");
                        if (! tempfile.equals(ambiencelist.get(size - 1)) &&
                                ! tempfile.equals(ambiencelist.get(size - 2))) {
                            ambiencelist.add(tempfile);
                            currentduration += tempduration;
                        }
                    } else if (ambiencefiles.size() <= 5) {
//                            System.out.println("Conditional 5");
                        if (! tempfile.equals(ambiencelist.get(size - 1)) &&
                                ! tempfile.equals(ambiencelist.get(size - 2)) &&
                                ! tempfile.equals(ambiencelist.get(size - 3))) {
                            ambiencelist.add(tempfile);
                            currentduration += tempduration;
                        }
                    } else if (ambiencefiles.size() >= 6) {
//                            System.out.println("Conditional 6");
                        if (! tempfile.equals(ambiencelist.get(size - 1)) &&
                                ! tempfile.equals(ambiencelist.get(size - 2)) &&
                                ! tempfile.equals(ambiencelist.get(size - 3)) &&
                                ! tempfile.equals(ambiencelist.get(size - 4))) {
                            ambiencelist.add(tempfile);
                            currentduration += tempduration;
                        }
                    } else {
                        System.out.println("Nothing Qualified, Your Logic Is Fucked Up");
                    }
                }
            }
        }
//        System.out.println(name + "'s Ambience");
//        for (File i : ambiencelist) {
//            System.out.println(i.getName());
//        }
        for (File i : ambiencelist) {ambiencemedia.add(new Media(i.toURI().toString()));}
        return currentduration > sessionduration && ambiencemedia.size() > 0;
    }

    // <----------------------------------- PLAYBACK --------------------------------------> //

    public void startplayback() {
        entrainmentplaycount = 0;
        ambienceplaycount = 0;
        entrainmentplayer = new MediaPlayer(entrainmentmedia.get(entrainmentplaycount));
        entrainmentplayer.play();
        entrainmentplayer.setVolume(Root.ENTRAINMENTVOLUME);
        entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
        if (ambienceenabled) {
            System.out.println(ambiencemedia);
            ambienceplayer = new MediaPlayer(ambiencemedia.get(ambienceplaycount));
            ambienceplayer.play();
//            ambienceplayer.setVolume(Root.AMBIENCEVOLUME);
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
        }
        cuttimeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> updatecuttime()));
        cuttimeline.setCycleCount(Animation.INDEFINITE);
        cuttimeline.play();
    }
    public void playnextentrainment() {
        entrainmentplaycount++;
        entrainmentplayer.dispose();
        entrainmentplayer = new MediaPlayer(entrainmentmedia.get(entrainmentplaycount));
        entrainmentplayer.play();
        entrainmentplayer.setVolume(Root.ENTRAINMENTVOLUME);
        entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
    }
    public void playnextambience() {
        ambienceplaycount++;
        ambienceplayer.dispose();
        ambienceplayer = new MediaPlayer(ambiencemedia.get(ambienceplaycount));
        ambienceplayer.play();
        ambienceplayer.setVolume(Root.AMBIENCEVOLUME);
        ambienceplayer.setOnEndOfMedia(this::playnextambience);
    }
    public void pauseplayingcut() {
        entrainmentplayer.pause();
        if (ambienceenabled) {ambienceplayer.pause();}
        cuttimeline.pause();
    }
    public void resumeplayingcut() {
        entrainmentplayer.play();
        if (ambienceenabled) {ambienceplayer.pause();}
        cuttimeline.play();
    }
    public void stopplayingcut() {
        entrainmentplayer.stop();
        entrainmentplayer.dispose();
        if (ambienceenabled) {
            ambienceplayer.stop();
            ambienceplayer.dispose();
        }
    }
    public void updatecuttime() {if (entrainmentplayer.getStatus() == MediaPlayer.Status.PLAYING) {secondselapsed++;}}
    public String getcurrenttimeformatted() {return TimeUtils.formatlengthshort(secondselapsed + 1);}
    public String gettotaltimeformatted() {return TimeUtils.formatlengthshort(getdurationinseconds());}

    // <----------------------------------- EXPORT --------------------------------------> //

    public boolean export() {System.out.println("Need To Work On This"); return false;}
    public boolean concatanateentrainment() {
    // Write Entrainment List To File For FFMPEG To Use
        try {
            PrintWriter writer = null;
            writer = new PrintWriter(tempentrainmenttextfile);
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
                // makeAmbienceList();
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

    public void setCutstoplay(ArrayList<Cut> cutstoplay) {
        this.cutstoplay = cutstoplay;
    }
}
