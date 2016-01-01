package kujiin;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class Tools {

    public static void howtouseprogram() {

    }

    public static void aboutthisprogram() {}

    public static void contactme() {}

    public static void numericTextField(TextField txtfield) {
        txtfield.setText("0");
        txtfield.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                try {if (newValue.matches("\\d*")) {Integer.parseInt(newValue);}  else {txtfield.setText(oldValue);}}
                catch (Exception e) {txtfield.setText("");}}
        });
    }

    public static double hoursandminutestoformatteddecimalhours(int hours, int minutes) {
        System.out.println("Minutes Is " + minutes);
        double newval;
        if (minutes != 0) {
            newval = hours + ((double) minutes / 60.0);
        } else {newval = hours;}
        NumberFormat numberFormat = new DecimalFormat("#0.00");
        return Double.parseDouble(numberFormat.format(newval));
    }

    public static String minutestoformattedhoursandmins(int min) {
        int hours = min / 60;
        int minutes = min % 60;
        StringBuilder text = new StringBuilder();
        if (hours > 0) {
            text.append(hours).append(" Hour");
            if (hours > 1) {text.append("s");}}
        if (minutes > 0) {
            if (hours > 0) {text.append(" ");}
            text.append(minutes).append(" Minute");
            if (minutes > 1) {text.append("s");}}
        return text.toString();
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

    public static Boolean sessionwellformednesschecks(ArrayList<Integer> cutsinsession) {
        // TODO This Method Will Check For Sequential Cuts And Make Sure It is Well-Formed Before Calling The Creator
        return true;
    }

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

    public static boolean testAlertFile() {
        return This_Session.alertfile.exists() && Tools.getaudioduration(This_Session.alertfile) != 0.0;
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

    public static Boolean supportedaudiofile(File file) {
        return file.getAbsolutePath().endsWith(".mp3");
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

    public static void readtempfile(File textfile) {
        try (BufferedReader br = new BufferedReader(new FileReader(textfile))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static String gettodaysdate() {
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter= new SimpleDateFormat("MM/dd/yyyy");
        return formatter.format(currentDate.getTime());
    }

}
