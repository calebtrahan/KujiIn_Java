package kujiin;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.*;
import javafx.util.Duration;
import kujiin.xml.Options;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Tools {
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static final String[] SUPPORTEDAUDIOFORMATS = {"mp3", "aac", "wav", "aif", "aiff", "m4a"};

// Menu Actions
    public static void howtouseprogram() {

    }
    public static void aboutthisprogram() {}
    public static void contactme() {

    }

// Gui Utils
    public static void integerTextField(TextField txtfield) {
        txtfield.textProperty().addListener((observable, oldValue, newValue) -> {
            try {if (newValue.matches("\\d*")) {Integer.parseInt(newValue);}  else {txtfield.setText(oldValue);}}
            catch (Exception e) {txtfield.setText("");}});
        txtfield.setText("0");
    }
    public static boolean getanswerdialog(String titletext, String headertext, String contenttext) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(titletext);
        a.setHeaderText(headertext);
        a.setContentText(contenttext);
        Optional<ButtonType> answer = a.showAndWait();
        return answer.isPresent() && answer.get() == ButtonType.OK;
    }
    public static void showinformationdialog(String titletext, String headertext, String contexttext) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titletext);
        a.setHeaderText(headertext);
        a.setContentText(contexttext);
        a.showAndWait();
    }
    public static void showerrordialog(String titletext, String headertext, String contenttext) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titletext);
        a.setHeaderText(headertext);
        a.setContentText(contenttext);
        a.showAndWait();
    }
    public static void showtimedmessage(Label label, String text, double millis) {
        label.setText(text);
        new Timeline(new KeyFrame(Duration.millis(millis), ae -> label.setText(""))).play();
    }

// Time Utils
    public static double hoursandminutestoformatteddecimalhours(int hours, int minutes) {
        System.out.println("Minutes Is " + minutes);
        double newval;
        if (minutes != 0) {
            newval = hours + ((double) minutes / 60.0);
        } else {newval = hours;}
        NumberFormat numberFormat = new DecimalFormat("#0.00");
        return Double.parseDouble(numberFormat.format(newval));
    }
    public static String minstoformattedlonghoursandminutes(int mins) {
        int hours = mins / 60;
        int minutes = mins % 60;
        StringBuilder stringbuilder = new StringBuilder();
        if (hours > 0) {
            stringbuilder.append(hours);
            stringbuilder.append(" Hour");
            if (hours > 1) {stringbuilder.append("s");}
            if (minutes > 0) {stringbuilder.append(" ");}
        }
        if (minutes > 0) {
            stringbuilder.append(minutes);
            stringbuilder.append(" Minute");
            if (minutes > 1) {stringbuilder.append("s");}
        }
        return stringbuilder.toString();
    }
    public static String minutestoformattedhoursandmins(int min) {
        if (min > 0) {
            int hours = min / 60;
            int minutes = min % 60;
            StringBuilder text = new StringBuilder();
            if (hours > 0) {
                text.append(hours).append(" Hour");
                if (hours > 1) {
                    text.append("s");
                }
            }
            if (minutes > 0) {
                if (hours > 0) {
                    text.append(" ");
                }
                text.append(minutes).append(" Minute");
                if (minutes > 1) {
                    text.append("s");
                }
            }
            return text.toString();
        } else {
            return "0 Minutes";
        }
    }
    public static String secondstominutesandseconds(int secs) {
        int seconds;
        int minutes;
        if (secs > 60) {minutes = secs / 60;} else {minutes = 0;}
        seconds = secs % 60;
        StringBuilder text = new StringBuilder();
        if (minutes > 0) {
            text.append(minutes).append(" Minute");
            if (minutes > 1) {text.append("s");}
        }
        if (seconds > 0) {
            if (minutes > 0) {text.append(" ");}
            text.append(seconds).append(" Second");
            if (seconds > 0) {text.append("s");}
        }
        return text.toString();
    }
    public static String formatlengthshort(int sec) {
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        if (sec >= 3600) {hours = sec / 3600; sec -= hours * 3600;}
        if (sec >= 60) {minutes = sec / 60; sec -= minutes * 60;}
        seconds = sec;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
    public static String gettodaysdate() {
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter= new SimpleDateFormat("MM/dd/yyyy");
        return formatter.format(currentDate.getTime());
    }
    public static LocalDate converttolocaldate(String dateformatted) {
        return LocalDate.parse(dateformatted, dateFormat);
    }
    public static String convertfromlocaldatetostring(LocalDate localdate) {
        return localdate.format(dateFormat);
    }
    public static Double convertminutestodecimalhours(int mins) {
        int hours = mins / 60;
        int minutes = mins % 60;
        double decimalhours = minutes / 60;
        decimalhours += hours;
        return decimalhours;
    }
    public static int convertdecimalhourstominutes(double decimalhours) {
        Double minutes = 60 * decimalhours;
        return minutes.intValue();
    }

// Session Utils
    public static void formatcurrentcutprogress(Cut currentcut, int currenttimeinseconds, Label currentlabel, ProgressBar currentprogress, Label totallabel) {
        int totalduration = currentcut.getdurationinminutes();
        String formattedtotalduration =  String.format("%02d:00", totalduration);
        int minutes = currenttimeinseconds / 60;
        int seconds = currenttimeinseconds % 60;
        String formattedcurrentduration = String.format("%02d:%02d", minutes, seconds);
        currentlabel.setText(formattedcurrentduration);
        currentprogress.setProgress(totalduration / currenttimeinseconds);
        totallabel.setText(formattedtotalduration);
    }
    public static void formattotalprogress(int totaltimeelapsed, int totalsessionduration, Label currentlabel, ProgressBar currentprogress, Label totallabel) {
        totaltimeelapsed /= 10;
        int totalhours;
        int hours;
        if (totalsessionduration >= 3600) {
            totalhours = totalsessionduration / 3600;
            totalsessionduration -= totalhours * 3600;
        } else {totalhours = 0;}
        int totalminutes = totalsessionduration / 60;
        int totalseconds = totalsessionduration % 60;
        String formattedtotalduration =  String.format("%02d:%02d:%02d", totalhours, totalminutes, totalseconds);
        if (totaltimeelapsed >= 3600) {
            hours = totalsessionduration / 3600;
            totaltimeelapsed -= hours * 3600;
        } else {hours = 0;}
        int minutes = totaltimeelapsed / 60;
        int seconds = totaltimeelapsed % 60;
        String formattedcurrenduration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        currentlabel.setText(formattedcurrenduration);
        currentprogress.setProgress(totaltimeelapsed / totalsessionduration);
        totallabel.setText(formattedtotalduration);
    }

// File Utils
    public static void printfile(File textfile) {
        try (BufferedReader br = new BufferedReader(new FileReader(textfile))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void erasetextfile(File textfile) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(textfile);
            writer.close();
        } catch (FileNotFoundException ignored) {}
    }
    public static String getFileContents(File file) {
        try {
            return org.apache.commons.io.FileUtils.readFileToString(file);
        } catch (IOException ignored) {return "";}
    }
    public static boolean writeFileContents(File file, String contents) {
        try {
            org.apache.commons.io.FileUtils.writeStringToFile(file, contents);
            return true;
        } catch (IOException ignored) {return false;}
    }

// Audio Utils
    public static boolean testAlertFile() {
        return Options.alertfile.exists() && Tools.getaudioduration(Options.alertfile) != 0.0;
    }
    public static double getaudioduration(File audiofile) {
        try {
            Runtime rt = Runtime.getRuntime();
            String[] commands = {"ffprobe", "-v", "quiet", "-print_format", "compact=print_section=0:nokey=1:escape=csv",
                    "-show_entries", "format=duration", audiofile.getAbsolutePath()};
            Process proc = rt.exec(commands);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));
            String s = null;
            StringBuilder a = new StringBuilder();
            while ((s = stdInput.readLine()) != null) {
                a.append(s);
            }
            return Double.parseDouble(a.toString());
        } catch (IOException e) {e.printStackTrace();}
        return 0.0;
    }
    public static boolean checkaudioduration(File audiofile, double expectedduration) {
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
            String s = null;
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
    public static File fadeaudiofile(File oldfile, File outputFile, int fadeinduration, int fadeoutduration, int durationoffileinseconds, File logfile, Boolean outputtologfile, String name) {
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
    public static boolean validaudiofile(File file) {
        return file.getName().endsWith(".mp3") || file.getName().endsWith(".aac") || file.getName().endsWith(".wav")
                || file.getName().endsWith(".aif") || file.getName().endsWith(".aiff") || file.getName().endsWith("m4a");
    }
    public static String supportedaudiotext() {
        StringBuilder s = new StringBuilder();
        for (String i : SUPPORTEDAUDIOFORMATS) {
            s.append(".").append(i);
            if (! Objects.equals(i, SUPPORTEDAUDIOFORMATS[SUPPORTEDAUDIOFORMATS.length - 1])) {
                s.append(", ");
            }
        }
        return s.toString();
    }
    public static boolean concatenateaudiofiles(ArrayList<File> filestoconcatenate, File temptextfile, File finalfile) {
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
//                if (Tools.checkaudioduration(finalentrainmentfile, getdurationinseconds())) {break;}
//                else {
//                    if (count > 3) {return false;}
//                    else {count++;}
//                }
            temptextfile.delete();
            return exitcode == 0;
        } catch (IOException | InterruptedException e) {
            new MainController.ExceptionDialog(e.getClass().getName(), e.getMessage());
            return false;
        }
    }
    public static boolean mixaudiofiles(ArrayList<File> filestomix, File outputfile) {
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
            new MainController.ExceptionDialog(e.getClass().getName(), e.getMessage());
            return false;
        }
    }
    public static boolean trimaudiofile(File filetotrim, Integer lengthinseconds) {
        try {
            // ffmpeg -i input.mp3 -ss 00:02:54.583 -t 300 -acodec copy output.mp3
            File tempfile = new File(Options.directorytemp, "Export/temptrim.mp3");
            FileUtils.moveFile(filetotrim, tempfile);
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
            new MainController.ExceptionDialog(e.getClass().getName(), e.getMessage());
            return false;
        }
    }

// List Utils
    public static List<?> shufflelist(List<?> list, int times) {
        for (int i = 0; i < times; i++) {
            Collections.shuffle(list);
        }
        return list;
    }

// Log Utils
    public static boolean sendstacktracetodeveloper(String stacktrace) {
        // TODO Email Stacktrace To Me
        return false;
    }
}
