package kujiin.util;

import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import kujiin.xml.Preferences;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Util {
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static final ArrayList<String> SUPPORTEDAUDIOFORMATS = new ArrayList<>(Arrays.asList("mp3", "aac", "wav", "aif", "aiff", "m4a"));

// Math Methods
    public static Double rounddouble(double number, int decimalplaces) {
    BigDecimal bd = new BigDecimal(number);
    bd = bd.setScale(decimalplaces, RoundingMode.HALF_UP);
    return bd.doubleValue();
}

// Time Methods
    // Object Convert
    public static double convert_hrsandminstodecimalhours(int hours, int minutes) {
        double newval;
        if (minutes != 0) {
            newval = hours + ((double) minutes / 60.0);
        } else {newval = hours;}
        NumberFormat numberFormat = new DecimalFormat("#0.00");
        return Double.parseDouble(numberFormat.format(newval));
    }
    // String Time Formatting
    public static String formatdurationtoStringDecimalWithColons(Duration duration) {
        int seconds = new Double(duration.toSeconds()).intValue();
        int hours = 0;
        int minutes = 0;
        if (seconds >= 3600) {hours = seconds / 3600; seconds -= hours * 3600;}
        if (seconds >= 60) {minutes = seconds / 60; seconds -= minutes * 60;}
        if (hours > 0) {return String.format("%02d:%02d:%02d", hours, minutes, seconds);}
        else {return String.format("%02d:%02d", minutes, seconds);}
    }
    public static String formatdurationtoStringSpelledOutShort(Duration duration, boolean includeseconds) {
        int seconds = new Double(duration.toSeconds()).intValue();
        int hours = 0;
        int minutes = 0;
        if (seconds >= 3600) {hours = seconds / 3600; seconds -= hours * 3600;}
        if (seconds >= 60) {minutes = seconds / 60; seconds -= minutes * 60;}
        StringBuilder durationtext = new StringBuilder();
        if (hours > 0) {durationtext.append(hours).append("h").append(" ");}
        if (minutes > 0) {durationtext.append(minutes).append("m");}
        if (includeseconds && seconds > 0) { durationtext.append(" ").append(seconds).append("s"); }
        return durationtext.toString();
    }
    public static String formatdurationtoStringSpelledOut(Duration duration, Double maxcharlength) {
        int seconds = new Double(duration.toSeconds()).intValue();
        int minutes = 0;
        int hours = 0;
        if (seconds >= 3600) {hours = seconds / 3600; seconds -= hours * 3600;}
        if (seconds >= 60) {minutes = seconds / 60; seconds -= minutes * 60;}
        if (!(hours > 0) && !(minutes > 0) && !(seconds > 0)) {
            if ("0 Minutes".length() >= maxcharlength) {return "0 Minutes";}
            else if ("0 Mins".length() >= maxcharlength) {return "0 Mins";}
            else {return "0 M";}
        } else {
            StringBuilder longtext = new StringBuilder();
            StringBuilder shorttext = new StringBuilder();
            StringBuilder reallyshorttext = new StringBuilder();
            if (hours > 0) {
                longtext.append(hours).append(" Hour"); if (hours > 1) {longtext.append("s");}
                shorttext.append(hours).append(" Hr"); if (hours > 1) {shorttext.append("s");}
                reallyshorttext.append(hours).append(" H");
                if (minutes > 0 || seconds > 0) {longtext.append(" "); shorttext.append(" "); reallyshorttext.append(" ");}
            }
            if (minutes > 0) {
                longtext.append(minutes).append(" Minute"); if (minutes > 1) {longtext.append("s");}
                shorttext.append(minutes).append(" Min"); if (minutes > 1) {shorttext.append("s");}
                reallyshorttext.append(minutes).append(" M");
                if (seconds > 0) {longtext.append(" "); shorttext.append(" "); reallyshorttext.append(" ");}
            }
            if (seconds > 0) {
                longtext.append(seconds).append(" Second"); if (seconds > 1) {longtext.append("s");}
                shorttext.append(seconds).append(" Sec"); if (seconds > 1) {shorttext.append("s");}
                reallyshorttext.append(seconds).append(" S");
            }
            if (maxcharlength >= longtext.length()) {return longtext.toString();}
            else if (maxcharlength >= shorttext.length()) {return shorttext.toString();}
            else {return reallyshorttext.toString();}
        }
    }

// File Methods
    public static void file_printtostdout(File textfile) {
        try (BufferedReader br = new BufferedReader(new FileReader(textfile))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ignored) {}
    }
    public static boolean file_erase(File textfile) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(textfile);
            writer.close();
            return true;
        } catch (FileNotFoundException ignored) {return false;}
    }
    public static String file_getcontents(File file) {
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }
                return sb.toString();
            }
        } catch (IOException e) {e.printStackTrace(); return null;}
    }
    public static boolean file_writecontents(File file, String contents) {
//        try {
////            org.apache.commons.io.FileUtils.writeStringToFile(file, contents);
//            return true;
//        } catch (IOException ignored) {return false;}
        return true;
    }
    public static File filechooser_single(Scene scene, String dialogtitle, File initialdirectory) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(dialogtitle);
        if (initialdirectory == null) {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        } else {fileChooser.setInitialDirectory(initialdirectory);}
        return fileChooser.showOpenDialog(scene.getWindow());
    }
    public static List<File> filechooser_multiple(Scene scene, String dialogtitle, File initialdirectory) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(dialogtitle);
        if (initialdirectory == null) {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        } else {fileChooser.setInitialDirectory(initialdirectory);}
        return fileChooser.showOpenMultipleDialog(scene.getWindow());
    }
    public static File filechooser_save(Scene scene, String dialogtitle, File initialdirectory) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(dialogtitle);
        if (initialdirectory == null) {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        } else {fileChooser.setInitialDirectory(initialdirectory);}
        return fileChooser.showSaveDialog(scene.getWindow());
    }
    public static File file_removetrailingnonalphabeticcharactersfromfilename(File file) {
        try {
            int firstindex = 0;
            char[] filenamearray = file.getName().toCharArray();
            for (int i = 0; i < filenamearray.length; i++) {
                char n = filenamearray[i];
                if (Character.isAlphabetic(n)) {
                    firstindex = i;
                    break;
                }
            }
            String newname = file.getName().substring(firstindex, file.getName().length());
            File newfile = new File(file.getParentFile(), newname);
//            try {
//                FileUtils.moveFile(file, newfile);
//                if (newfile.exists()) {return newfile;}
//                else {return null;}
//            } catch (FileExistsException ignored) {return newfile;}
            return null;
        } catch (Exception ignored) {return null;}
    }

// Audio Methods
    public static double audio_getduration(File audiofile) {
        try {
            Runtime rt = Runtime.getRuntime();
            String[] commands = {"ffprobe", "-v", "quiet", "-print_format", "compact=print_section=0:nokey=1:escape=csv",
                    "-show_entries", "format=duration", audiofile.getAbsolutePath()};
            Process proc = rt.exec(commands);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));
            String s;
            StringBuilder a = new StringBuilder();
            while ((s = stdInput.readLine()) != null) {
                a.append(s);
            }
            return Double.parseDouble(a.toString());
        } catch (Exception ignored) {return 0.0;}
    }
    public static boolean audio_checkduration(File audiofile, double expectedduration) {
        boolean durationOK;
        try {
            Runtime rt = Runtime.getRuntime();
            String[] commands = {"ffprobe", "-v", "quiet", "-print_format", "compact=print_section=0:nokey=1:escape=csv",
                    "-show_entries", "format=duration", audiofile.getAbsolutePath()};
            Process proc = rt.exec(commands);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));
            String s;
            StringBuilder a = new StringBuilder();
            while ((s = stdInput.readLine()) != null) {
                a.append(s);
            }
            double currentduration = Double.parseDouble(a.toString());
            durationOK = currentduration >= expectedduration;
            if (! durationOK) {
                System.out.println(String.format("This File's Duration Is %s But Is Expected To Be %s", currentduration, expectedduration));
            }
        } catch (IOException e) {durationOK = false;}
        return durationOK;
    }
    public static File audio_fadeout(File oldfile, File outputFile, int fadeinduration, int fadeoutduration, int durationoffileinseconds, File logfile, Boolean outputtologfile, String name) {
        System.out.println("Started Fading " + name + "'s Audio");
        ArrayList<String> cmdlist = new ArrayList<>();
        cmdlist.add("ffmpeg");
        cmdlist.add("-i");
        cmdlist.add(oldfile.getAbsolutePath());
        cmdlist.add("-af");
        cmdlist.add("afade=t=in:ss=0:d=" + fadeinduration + ",afade=t=out:st=" + (durationoffileinseconds - 10) + ":d=" + fadeoutduration);
        cmdlist.add(outputFile.getAbsolutePath());
        ProcessBuilder a = new ProcessBuilder(cmdlist);
        a.redirectErrorStream(true);
        if (outputtologfile) {a.redirectOutput(ProcessBuilder.Redirect.appendTo(logfile));}
        try {
            Process workprocess = a.start();
            assert a.redirectInput() == ProcessBuilder.Redirect.PIPE;
            assert a.redirectOutput().file() == logfile;
            workprocess.waitFor();
            oldfile.delete();
            System.out.println("Started Fading " + name + "'s Audio");
            return outputFile;
        } catch (IOException | InterruptedException e) {return null;}
    }
    public static boolean audio_isValid(File file) {
        return file.exists() && Util.SUPPORTEDAUDIOFORMATS.contains(file.getName().substring(file.getName().lastIndexOf(".") + 1));
    }
    public static String audio_getsupportedText() {
        StringBuilder s = new StringBuilder();
        for (String i : SUPPORTEDAUDIOFORMATS) {
            s.append(".").append(i);
            if (! Objects.equals(i, SUPPORTEDAUDIOFORMATS.get(SUPPORTEDAUDIOFORMATS.size() - 1))) {
                s.append(", ");
            }
        }
        return s.toString();
    }
    public static boolean audio_concatenatefiles(ArrayList<File> filestoconcatenate, File temptextfile, File finalfile) {
        try {
            System.out.println("Started Concatenating Audio");
            PrintWriter writer = new PrintWriter(temptextfile);
            for (File k : filestoconcatenate) {writer.println("file " + "\'" + k.getAbsolutePath() + "\'");}
            writer.close();
            ArrayList<String> cmdarraylist = new ArrayList<>();
            cmdarraylist.add("ffmpeg");
            cmdarraylist.add("-f");
            cmdarraylist.add("concat");
            cmdarraylist.add("-i");
            cmdarraylist.add(temptextfile.getAbsolutePath());
            cmdarraylist.add("-c");
            cmdarraylist.add("copy");
            cmdarraylist.add(finalfile.getAbsolutePath());
            ProcessBuilder cmdlist = new ProcessBuilder(cmdarraylist);
            cmdlist.redirectErrorStream(true);
//            int count = 0;
            final Process p;
            p = cmdlist.start();
            int exitcode = p.waitFor();
            System.out.println("Finished Concatenating Audio");
//                if (Util.audio_checkduration(finalentrainmentfile, getdurationinseconds())) {break;}
//                else {
//                    if (count > 3) {return false;}
//                    else {count++;}
//                }
            temptextfile.delete();
            return exitcode == 0;
        } catch (IOException | InterruptedException e) {return false;}
    }
    public static boolean audio_mixfiles(ArrayList<File> filestomix, File outputfile) {
        try {
            System.out.println("Started Mixing Audio");
            // ffmpeg -i INPUT1 -i INPUT2 -i INPUT3 -filter_complex amix=inputs=3:duration=first:dropout_transition=3 OUTPUT
            System.out.println("Called Mix Audio Files");
            ArrayList<String> cmdarraylist = new ArrayList<>();
            cmdarraylist.add("ffmpeg");
            for (File i : filestomix) {
                cmdarraylist.add("-i");
                cmdarraylist.add(i.getAbsolutePath());
            }
            cmdarraylist.add("-filter_complex");
            cmdarraylist.add(String.format("\"amix=inputs=%d:duration=first\"", filestomix.size()));
//            cmdarraylist.add();
            cmdarraylist.add(outputfile.getAbsolutePath());
            System.out.println(cmdarraylist.toString());
            ProcessBuilder cmdlist = new ProcessBuilder(cmdarraylist);
            final Process p;
            p = cmdlist.start();
            cmdlist.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            cmdlist.redirectError(ProcessBuilder.Redirect.INHERIT);
            cmdlist.redirectInput(ProcessBuilder.Redirect.INHERIT);
            int exitcode = p.waitFor();
            System.out.println("Finished Mixing Audio. Exited With Code" + exitcode);
            return exitcode == 0;
        } catch (IOException | InterruptedException e) {
//            new MainController.ExceptionDialog(null, e);
            return false;
        }
    }
    public static boolean audio_trimfile(File filetotrim, Integer lengthinseconds) {
        try {
            // ffmpeg -i input.mp3 -ss 00:02:54.583 -t 300 -acodec copy output.mp3
            File tempfile = new File(Preferences.DIRECTORYTEMP, "Export/temptrim.mp3");
//            FileUtils.moveFile(filetotrim, tempfile);
            ArrayList<String> cmdarraylist = new ArrayList<>();
            cmdarraylist.add("ffmpeg");
            cmdarraylist.add("-i");
            cmdarraylist.add(tempfile.getAbsolutePath());
            // Starting Time Here
            cmdarraylist.add("-t");
            cmdarraylist.add(lengthinseconds.toString());
            cmdarraylist.add("-acodec");
            cmdarraylist.add("copy");
            cmdarraylist.add(filetotrim.getAbsolutePath());
            ProcessBuilder cmdlist = new ProcessBuilder(cmdarraylist);
            final Process p;
            p = cmdlist.start();
            int exitcode = p.waitFor();
            tempfile.delete();
            return exitcode == 0;
        } catch (IOException | InterruptedException e) {
//            new MainController.ExceptionDialog(null, e);
            return false;
        }
    }

// String Methods
    public static String reformatcapatalized(String text) {
        StringBuilder newname = new StringBuilder();
        char[] tempname = text.toCharArray();
        for (int x = 0; x < tempname.length; x++) {
            if (x == 0) {
                newname.append(Character.toUpperCase(tempname[0]));
            } else if (Character.isUpperCase(tempname[x])) {
                newname.append(" ");
                newname.append(tempname[x]);
            } else {
                newname.append(tempname[x]);
            }
        }
        return newname.toString();
    }
    public static boolean String_validhtml(String text) {
//        SAXBuilder builder = new SAXBuilder();
//        try {builder.build(text); return true;}
//        catch (IOException | JDOMException | NullPointerException e) {return false;}
        return true;
    }
    public static boolean String_validhtml(File file) {
//        SAXBuilder builder = new SAXBuilder();
//        try {Document document = builder.build(file); return true;}
//        catch (IOException | JDOMException e) {return false;}
        return false;
    }

// List Methods
    public static ArrayList<File> list_removeduplicates(List<File> list) {
        Set<File> lump = new HashSet<>();
        list.stream().filter(i -> ! lump.contains(i)).forEach(lump::add);
        return new ArrayList<>(lump);
    }
    public static boolean list_hasduplicates(List<File> list) {
        Set<File> lump = new HashSet<>();
       for (File i : list) {
           if (lump.contains(i)) {return true;}
           lump.add(i);
       }
        return false;
    }

// Log Methods
    public static boolean sendstacktracetodeveloper(String stacktrace) {
        // TODO Email Stacktrace To Me
        return false;
    }

// Math Methods
    public static Double round_nearestmultipleof5(double value) {
        if (value % 5 == 0) {return value;}
        else if (value % 5 < 2.5) {return value - value % 5;}
        else {return value + (5 - value % 5);}
    }

    public enum AnswerType {
        YES, NO, CANCEL
    }
}
