package kujiin.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import kujiin.MainController;
import kujiin.xml.Options;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Util {
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static final String[] SUPPORTEDAUDIOFORMATS = {"mp3", "aac", "wav", "aif", "aiff", "m4a"};

// Menu Methods
    public static void menu_howtouse(MainController root) {}
    public static void menu_aboutthisprogram() {}
    public static void menu_contactme() {

    }

// Gui Methods
//     TODO Find Out Why Displaying Some Dialogs Makes Root Uniconified (When It's Supposed To Be)
    public static boolean gui_getokcancelconfirmationdialog(MainController root, String titletext, String headertext, String contenttext) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(titletext);
        a.setHeaderText(headertext);
        a.setContentText(contenttext);
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(root.getOptions().getAppearanceOptions().getThemefile());
        Optional<ButtonType> answer = a.showAndWait();
        return answer.isPresent() && answer.get() == ButtonType.OK;
    }
    public static AnswerType gui_getyesnocancelconfirmationdialog(MainController root, String titletext, String headertext, String contenttext) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(titletext);
        a.setHeaderText(headertext);
        a.setContentText(contenttext);
        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        ButtonType cancel = new ButtonType("Cancel");
        a.getButtonTypes().clear();
        a.getButtonTypes().add(yes);
        a.getButtonTypes().add(no);
        a.getButtonTypes().add(cancel);
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(root.getOptions().getAppearanceOptions().getThemefile());
        Optional<ButtonType> answer = a.showAndWait();
        if (answer.isPresent()) {
            if (answer.get() == yes) {return AnswerType.YES;}
            if (answer.get() == no) {return AnswerType.NO;}
            if (answer.get() == cancel) {return AnswerType.CANCEL;}
        }
        return AnswerType.CANCEL;
    }
    public static AnswerType gui_getyesnocancelconfirmationdialog(MainController root, String titletext, String headertext, String contenttext, String yesbuttontext, String nobuttontext, String cancelbuttontext) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(titletext);
        a.setHeaderText(headertext);
        a.setContentText(contenttext);
        ButtonType yes;
        ButtonType no;
        ButtonType cancel;
        if (yesbuttontext != null) {yes = new ButtonType("Yes");} else {yes = new ButtonType(yesbuttontext);}
        if (nobuttontext != null) {no = new ButtonType("No");} else {no = new ButtonType(nobuttontext);}
        if (cancelbuttontext != null) {cancel = new ButtonType("Cancel");} else {cancel = new ButtonType(cancelbuttontext);}
        a.getButtonTypes().clear();
        a.getButtonTypes().add(yes);
        a.getButtonTypes().add(no);
        a.getButtonTypes().add(cancel);
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(root.getOptions().getAppearanceOptions().getThemefile());
        Optional<ButtonType> answer = a.showAndWait();
        if (answer.isPresent()) {
            if (answer.get() == yes) {return AnswerType.YES;}
            if (answer.get() == no) {return AnswerType.NO;}
            if (answer.get() == cancel) {return AnswerType.CANCEL;}
        }
        return AnswerType.CANCEL;
    }
    public static void gui_showinformationdialog(MainController root, String titletext, String headertext, String contexttext) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titletext);
        a.setHeaderText(headertext);
        a.setContentText(contexttext);
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(root.getOptions().getAppearanceOptions().getThemefile());
        a.showAndWait();
    }
    public static void gui_showerrordialog(MainController root, String titletext, String headertext, String contenttext) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titletext);
        a.setHeaderText(headertext);
        a.setContentText(contenttext);
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(root.getOptions().getAppearanceOptions().getThemefile());
        a.showAndWait();
    }
    public static void gui_showtimedmessageonlabel(Label label, String text, double millis) {
        label.setText(text);
        new Timeline(new KeyFrame(Duration.millis(millis), ae -> label.setText(""))).play();
    }
    public static void gui_validate(TextField txtfield, int highvalue, int valtotest) {
        ObservableList<String> styleclass = txtfield.getStyleClass();
        if (valtotest > highvalue) {if (!styleclass.contains("error")) {styleclass.add("error");}}
        else {styleclass.removeAll(Collections.singleton("error"));}
    }
    public static void gui_validate(ChoiceBox<String> choicebox, Boolean val) {
        ObservableList<String> styleclass = choicebox.getStyleClass();
        if (! val ) {if (!styleclass.contains("error")) {styleclass.add("error");}}
        else {styleclass.removeAll(Collections.singleton("error"));}
    }
    public static void gui_validate(TextField txtfield, Boolean val) {
        ObservableList<String> styleclass = txtfield.getStyleClass();
        if (! val ) {if (!styleclass.contains("error")) {styleclass.add("error");}}
        else {styleclass.removeAll(Collections.singleton("error"));}
    }
    public static void gui_validate(Label lbl, Boolean val) {
        ObservableList<String> styleclass = lbl.getStyleClass();
        if (! val ) {if (!styleclass.contains("error")) {styleclass.add("error");}
        } else {styleclass.removeAll(Collections.singleton("error"));}
    }
    public static void addscrolllistenerincrementdecrement(TextField textField, double minvalue, double maxvalue, double increment) {
        textField.setOnScroll(event -> {
            Double newvalue = new Double(textField.getText());
            if (event.getDeltaY() < 0) {
                newvalue -= increment;
                if (newvalue >= minvalue) {textField.setText(newvalue.toString());}
            }
            else {
                newvalue += increment;
                if (newvalue <= maxvalue) {textField.setText(newvalue.toString());}
            }
        });
    }
    public static void addscrolllistenerincrementdecrement(TextField textField, ToggleButton toggleButton,  double minvalue, double maxvalue, double increment) {
        textField.setOnScroll(event -> {
            Double newvalue = new Double(textField.getText());
            if (event.getDeltaY() < 0) {
                newvalue -= increment;
                if (newvalue >= minvalue) {textField.setText(String.valueOf(newvalue.intValue()));}
            }
            else {
                newvalue += increment;
                if (newvalue <= maxvalue) {textField.setText(String.valueOf(newvalue.intValue()));}
            }
            textField.setDisable(newvalue == 0);
            toggleButton.setSelected(newvalue != 0);
        });
    }

// Math Methods
    public static Double rounddouble(double number, int decimalplaces) {
    BigDecimal bd = new BigDecimal(number);
    bd = bd.setScale(decimalplaces, RoundingMode.HALF_UP);
    return bd.doubleValue();
}

// Time Methods
    // Object Convert
    public static double convert_hrsandminstodecimalhours(int hours, int minutes) {
        System.out.println("Minutes Is " + minutes);
        double newval;
        if (minutes != 0) {
            newval = hours + ((double) minutes / 60.0);
        } else {newval = hours;}
        NumberFormat numberFormat = new DecimalFormat("#0.00");
        return Double.parseDouble(numberFormat.format(newval));
    }
    public static LocalDate convert_stringtolocaldate(String dateformatted) {
        return LocalDate.parse(dateformatted, dateFormat);
    }
    public static String convert_localdatetostring(LocalDate localdate) {
        return localdate.format(dateFormat);
    }
    public static Double convert_minstodecimalhours(int mins, int decimalplaces) {
        double hours = mins / 60;
        double minutes = mins % 60;
        double decimalminutes = minutes / 60;
        decimalminutes += hours;
        return rounddouble(decimalminutes, 2);
    }
    public static int convertdecimalhourstominutes(double decimalhours) {
        Double minutes = 60 * decimalhours;
        return minutes.intValue();
    }
    // String Time Formatting
    public static String format_minstohrsandmins_abbreviated(int mins) {
        int hours = mins / 60;
        int minutes = mins % 60;
        StringBuilder stringbuilder = new StringBuilder();
        if (hours > 0) {
            stringbuilder.append(hours);
            stringbuilder.append(" Hr");
            if (hours > 1) {stringbuilder.append("s");}
            if (minutes > 0) {stringbuilder.append(" ");}
        }
        stringbuilder.append(minutes);
        stringbuilder.append(" Min");
        if (minutes > 1) {stringbuilder.append("s");}
        return stringbuilder.toString();
    }
    public static String format_minstohrsandmins_short(int min) {
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
    public static String format_minstohrsandmins_long(int mins) {
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
    public static String format_secstominsandseconds(int secs) {
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
    public static String format_secondsforplayerdisplay(int sec) {
        int hours = 0;
        int minutes = 0;
        if (sec >= 3600) {hours = sec / 3600; sec -= hours * 3600;}
        if (sec >= 60) {minutes = sec / 60; sec -= minutes * 60;}
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, sec);
        } else {
            return String.format("%02d:%02d", minutes, sec);
        }
    }
    public static String format_secondsleftforplayerdisplay(int elapsedseconds, int totalseconds) {
        int secondsleft = totalseconds - elapsedseconds;
        int hours = 0;
        int minutes = 0;
        if (secondsleft >= 3600) {hours = secondsleft / 3600; secondsleft -= hours * 3600;}
        if (secondsleft >= 60) {minutes = secondsleft / 60; secondsleft -= minutes * 60;}
        if (hours > 0) {
            return String.format("-" + "%02d:%02d:%02d", hours, minutes, secondsleft);
        } else {
            return String.format("-" + "%02d:%02d", minutes, secondsleft);
        }
    }

// Date Methods
    public static String gettodaysdate() {
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter= new SimpleDateFormat("MM/dd/yyyy");
        return formatter.format(currentDate.getTime());
    }
    public static String checkifdateoverdue(String DateFormatted) {
        LocalDate datedue = Util.convert_stringtolocaldate(DateFormatted);
        int daystilldue = Period.between(LocalDate.now(), datedue).getDays();
        if (daystilldue > 1) {return DateFormatted;}
        if (daystilldue == 1) {return "Tomorrow";}
        if (daystilldue == 0) {return "Today";}
        if (daystilldue < 0) {return "Past Due";}
        else {return "";}
    }

// File Methods
    public static void file_printtostdout(File textfile) {
        try (BufferedReader br = new BufferedReader(new FileReader(textfile))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ignored) {}
    }
    public static boolean file_erase(File textfile) {
        PrintWriter writer = null;
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
        try {
            org.apache.commons.io.FileUtils.writeStringToFile(file, contents);
            return true;
        } catch (IOException ignored) {return false;}
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
    public static File file_extensioncorrect(MainController root, String expectedextension, File filetocheck) {
        if (! filetocheck.getName().contains(".")) {
            if (Util.gui_getokcancelconfirmationdialog(root, "Confirmation", "Invalid Extension", "Save As A ." + expectedextension + " File?")) {
                return new File(filetocheck.getAbsolutePath().concat("." + expectedextension));
            } else {return filetocheck;}
        } else {
            String extension = filetocheck.getName().substring(filetocheck.getName().lastIndexOf("."));
            if (Util.gui_getokcancelconfirmationdialog(root, "Confirmation", "Invalid Extension " + extension, "Rename As ." + expectedextension + "?")) {
                String filewithoutextension = filetocheck.getAbsolutePath().substring(0, filetocheck.getName().lastIndexOf("."));
                return new File(filewithoutextension.concat("." + expectedextension));
            } else {
                return filetocheck;
            }
        }
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
            try {
                FileUtils.moveFile(file, newfile);
                if (newfile.exists()) {return newfile;}
                else {return null;}
            } catch (FileExistsException ignored) {return newfile;}
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
            String s = null;
            StringBuilder a = new StringBuilder();
            while ((s = stdInput.readLine()) != null) {
                a.append(s);
            }
            return Double.parseDouble(a.toString());
        } catch (IOException | NumberFormatException ignored) {return 0.0;}

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
        return file.getName().endsWith(".mp3") || file.getName().endsWith(".aac") || file.getName().endsWith(".wav")
                || file.getName().endsWith(".aif") || file.getName().endsWith(".aiff") || file.getName().endsWith("m4a");
    }
    public static String audio_getsupportedText() {
        StringBuilder s = new StringBuilder();
        for (String i : SUPPORTEDAUDIOFORMATS) {
            s.append(".").append(i);
            if (! Objects.equals(i, SUPPORTEDAUDIOFORMATS[SUPPORTEDAUDIOFORMATS.length - 1])) {
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
        } catch (IOException | InterruptedException e) {
            new MainController.ExceptionDialog(null, e);
            return false;
        }
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
            new MainController.ExceptionDialog(null, e);
            return false;
        }
    }
    public static boolean audio_trimfile(File filetotrim, Integer lengthinseconds) {
        try {
            // ffmpeg -i input.mp3 -ss 00:02:54.583 -t 300 -acodec copy output.mp3
            File tempfile = new File(Options.DIRECTORYTEMP, "Export/temptrim.mp3");
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
            new MainController.ExceptionDialog(null, e);
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
        SAXBuilder builder = new SAXBuilder();
        try {builder.build(IOUtils.toInputStream(text, "UTF-8")); return true;}
        catch (IOException | JDOMException | NullPointerException e) {return false;}
    }
    public static boolean String_validhtml(File file) {
        SAXBuilder builder = new SAXBuilder();
        try {Document document = builder.build(file); return true;}
        catch (IOException | JDOMException e) {return false;}
    }

// List Methods
    public static List<?> list_shuffle(List<?> list, int times) {
        for (int i = 0; i < times; i++) {
            Collections.shuffle(list);
        }
        return list;
    }
    public static int list_getmaxintegervalue(List<Integer> list) {
        List<Integer> listcopy = list;
        Collections.sort(listcopy);
        return listcopy.get(listcopy.size() - 1);
    }

// Log Methods
    public static boolean sendstacktracetodeveloper(String stacktrace) {
        // TODO Email Stacktrace To Me
        return false;
    }

// Math Methods
    public static double round_nearestmultipleof5(double value) {
        if (value % 5 == 0) {return value;}
        else if (value % 5 < 2.5) {return value - value % 5;}
        else {return value + (5 - value % 5);}
    }

    public enum AnswerType {
        YES, NO, CANCEL
    }
}
