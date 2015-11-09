package kujiin;

import javafx.concurrent.Service;
import kujiin.dialogs.CreatingSessionDialog;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

//enum AmbienceOption {General, Specific};

// TODO Continue Encapsulating This And Connect New Logic (Or Maybe Put Creating
//  Threads In Session Or As Part Of Creating Session Dialog

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
    private Session thisession;
    private Service<Void> entrainmentcreatorservice;
    private Service<Void> ambiencecreatorservice;


    public Cut(int number, String name, Boolean ramp, int duration, Session thisession) {
        this.number = number;
        this.name = name;
        this.ramp = ramp;
        if (this.ramp) { this.rampduration = 2;} // TODO This Can Be Set In Options
        this.duration = duration;
        this.thisession = thisession;
        ambiencedirectory = new File(Session.directoryambience, name);
        tempentrainmenttextfile = new File(Session.directorytemp, "txt/" + name + "Ent.txt");
        tempentrainmentfile = new File(Session.directorytemp, "Entrainment/" + name + "Temp.mp3");
        finalentrainmentfile = new File(Session.directorytemp, "Entrainment/" + name + ".mp3");
        tempambiencetextfile = new File(Session.directorytemp, "txt/" + name + "Amb.txt");
        tempambiencefile = new File(Session.directorytemp, "Ambience/" + name + "Temp.mp3");
        finalambiencefile = new File(Session.directorytemp, "Ambience/" + name + ".mp3");
    }

    public Cut(String alertname) {
        name = alertname;
    }
    // <-------------------------- GETTERS AND SETTERS ------------------------------------> //

    // Converts Total Duration To Seconds
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

    // Converts Total Duration To Minutes
    public int getdurationinminutes() {
        int audiodurationinseconds;
        audiodurationinseconds = duration;
        if (number == 0 || number == 10) {audiodurationinseconds += rampduration;}
        return audiodurationinseconds;
    }

    // Setter For duration Field
    public void setDuration(int newduration) {
        this.duration = newduration;
    }

    // Getter For totalambienceduration
    public double getTotalambienceduration() {
        return totalambienceduration;
    }

    // Setter For totalambienceduration
    public void setTotalambienceduration(double totalambienceduration) {
        this.totalambienceduration = totalambienceduration;
    }

    // <-------------------------- GENERAL CREATION METHODS ---------------------------------> //

    // Checks If Session Is Created
    public Boolean sessioniscreated(boolean ambienceenabled) {
        boolean cutisgood;
        File entrainmentfile = new File(Session.directorytemp, "Entrainment/" + name + ".mp3");
        cutisgood = entrainmentfile.exists();
        if (ambienceenabled) {
            File ambiencefile = new File(Session.directorytemp, "Ambience/" + name + ".mp3");
            cutisgood = ambiencefile.exists();
        }
        return cutisgood;
    }

    // TODO Check If This Cut Has ANY Ambience At All
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

    // Check If The Ambience Duration Is >= The Duration Needed
    public boolean hasenoughAmbience() {
        double a = 0;
        for (Double i : ambiencefiledurations) {a += i;}
        setTotalambienceduration(a);
        return a > (double) getdurationinseconds();
    }

    // Clean Up Temp Files Used When During Cut's Creation
    public void cleanuptempfiles() {
        if (tempambiencefile.exists()) {tempambiencefile.delete();}
        if (tempentrainmentfile.exists()) {tempentrainmentfile.delete();}
        if (tempentrainmenttextfile.exists()) {tempentrainmenttextfile.delete();}
        if (tempambiencetextfile.exists()) {tempambiencetextfile.delete();}
    }

    // Contains Services That Call The Methods That Call The Methods Below
    public boolean create(Boolean ambienceenabled, ArrayList<Cut> cutstoplay, CreatingSessionDialog creatingsessiondialog) {
        boolean entrainmentisgood = false;
        boolean ambienceisgood = false;
        this.cutstoplay = cutstoplay;
        generateentrainmentlists();
        writeentrainmenttextfile();
        entrainmentisgood = buildentrainment();
        if (ambienceenabled) {
            generateambiencelist();
            writeambiencelisttotextfile();
            ambienceisgood = buildambience();
        }
        cleanuptempfiles();
        return true;
//        if (ambienceenabled) {return entrainmentisgood && ambienceisgood;}
//        else {return entrainmentisgood;}
    }

    // <-------------------------- CREATING ENTRAINMENT METHODS ----------------------------> //

    // Generates A List Of How Many And Which Entrainment Files To Be Concatenated
    public boolean generateentrainmentlists() {
        entrainmentlist = new ArrayList<>();
        if (number == 0) {                                                                                              // Presession
            for (Integer z = 0; z < duration; z++) {
                String filename = name + ".mp3";
                File thisfile = new File(Session.directorymaincuts, filename);
                entrainmentlist.add(thisfile);
            }
            String rampupfirstname = "ar" + cutstoplay.get(1).number + rampduration + ".mp3";
            File ramptofirstcut = new File(Session.directoryrampup, rampupfirstname);
            entrainmentlist.add(ramptofirstcut);
        }
        else if (number == 10) {                                                                                        // PostSession
            String rampdowntopost =  "zr" +
                    cutstoplay.get(cutstoplay.size()-2).number + rampduration + ".mp3";
            File thisfile = new File(Session.directoryrampdown, rampdowntopost);
            entrainmentlist.add(thisfile);
            for (Integer g = 0; g < duration; g++) {
                String postname = name + ".mp3";
                File thatfile = new File(Session.directorymaincuts, postname);
                entrainmentlist.add(thatfile);
            }
        }
        else if (number == 3) {                                                                              // TOH
            Boolean postsessionisnext = false;
            if (cutstoplay.size() - cutstoplay.indexOf(this) <= 2) {postsessionisnext = true;}
            File tohmainfile = new File(Session.directorymaincuts, "TOH.mp3");
            // TODO Add 3inpostsession for if going straight into TOH
            File rampin1 = new File(Session.directorytohramp, "3in1.mp3");
            File rampin2 = new File(Session.directorytohramp, "3in2.mp3");
            File rampout1 = new File(Session.directorytohramp, "3out1.mp3");
            File rampout2 = new File(Session.directorytohramp, "3out2.mp3");
            File rampoutspecial1 = new File(Session.directorytohramp, "3outpostsession1.mp3");
            File rampoutspecial2 = new File(Session.directorytohramp, "3outpostsession2.mp3");
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
                File thisfile = new File(Session.directorymaincuts, filename);
                entrainmentlist.add(thisfile);
            }
        }
        int count = 0;
        System.out.println(name + "'s Entrainment List");
        for (File i : entrainmentlist) {
            System.out.println(i.getName());
        }
        return entrainmentlist.size() > 0;
    }

    // Writes The Above List To A File So FFMPEG Can Read It
    public boolean writeentrainmenttextfile() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(tempentrainmenttextfile);
            for (File k : entrainmentlist) {writer.println("file " + "\'" + k.getAbsolutePath() + "\'");}
            writer.close();
        } catch (FileNotFoundException ignored) {}
        return tempentrainmenttextfile.exists();
    }

    // Calls FFMPEG To Actually Concatenate The Audio Files Into One File
    public boolean buildentrainment() {
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
        try {
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
        } catch (IOException | InterruptedException e) {e.printStackTrace();}
        return finalentrainmentfile.exists();
    }

    // Adds Fade Filters To The Audio File For Postprocessing
    public boolean addaudiofiltersforentrainment() {
        CommandLine audiofilterentrainmentcmdlist = new CommandLine("ffmpeg");
        audiofilterentrainmentcmdlist.addArgument("-i");
        audiofilterentrainmentcmdlist.addArgument(tempentrainmentfile.getAbsolutePath());
        audiofilterentrainmentcmdlist.addArgument("-af");
        audiofilterentrainmentcmdlist.addArgument("afade=t=in:ss=0:d=10");
//            audiofilterentrainmentcmdlist.addArgument("-af");
//            audiofilterentrainmentcmdlist.addArgument("afade=t=out:st=" + (currentcut.getdurationinseconds() - 10) + ":d=10"); // INVALID ARGUMENT!
        audiofilterentrainmentcmdlist.addArgument(finalentrainmentfile.getAbsolutePath());
//            System.out.println(audiofilterentrainmentcmdlist.toString());
        java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream();
        java.io.ByteArrayOutputStream err=new java.io.ByteArrayOutputStream();
        DefaultExecutor executor = new DefaultExecutor();
        PumpStreamHandler handler=new PumpStreamHandler(out, err);
        executor.setStreamHandler(handler);
        try {int exitValue = executor.execute(audiofilterentrainmentcmdlist);
        } catch (IOException ignored) {ignored.printStackTrace();}
        tempentrainmentfile.delete();
//        System.out.print(err.toString());
        return finalentrainmentfile.exists();
//        if (! final.exists()) {System.out.println(String.format("Couldn't Add Audio Filters And Create The Final File For %s Entrainment", finalfile.getAbsolutePath()));}
    }

    // <-------------------------- CREATING AMBIENCE METHODS -------------------------------> //

    // Generates A List Of How Many And Which Ambience Files To Be Concatenated
    public boolean generateambiencelist() {
        ambiencelist = new ArrayList<>();
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
        System.out.println(name + "'s Ambience");
        for (File i : ambiencelist) {
            System.out.println(i.getName());
        }
        return currentduration > sessionduration;
    }

    // Writes The Above List To A File So FFMPEG Can Read It
    public boolean writeambiencelisttotextfile() {
        Tools.erasetextfile(tempambiencetextfile);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(tempambiencetextfile);
            for (File k : ambiencefiles) {writer.println("file " + "\'" + k.getAbsolutePath() + "\'");}
            writer.close();
        } catch (FileNotFoundException ignored) {}
        return tempambiencetextfile.exists();
    }

    // Calls FFMPEG To Actually Concatenate The Audio Files Into One File
    public boolean buildambience() {
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
                // generateambiencelist();
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
    }

    // Adds Fade Filters To The Audio File For Postprocessing
    private boolean applyaudiofilters() {
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
        return false;
    }


}
