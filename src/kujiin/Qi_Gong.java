package kujiin;

import javafx.beans.value.ChangeListener;
import javafx.concurrent.Service;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.interfaces.Creatable;
import kujiin.interfaces.Exportable;
import kujiin.interfaces.Trackable;
import kujiin.widgets.Playable;
import kujiin.widgets.ProgressAndGoalsWidget;
import kujiin.xml.Goals;
import kujiin.xml.Options;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Qi_Gong extends Playable implements Creatable, Exportable, Trackable {
    private ToggleButton Switch;
    private TextField Value;
    private Goals GoalsController;

    public Qi_Gong (int number, String name, int duration, This_Session thissession, ToggleButton aSwitch, TextField value) {
        this.number = number;
        this.name = name;
        super.duration = duration;
        super.thisession = thissession;
        ambiencedirectory = new File(Options.DIRECTORYAMBIENCE, name);
        Switch = aSwitch;
        Value = value;
        Switch.setOnAction(event -> toggleswitch());
        toggleswitch();
    }

// GUI
    public void toggleswitch() {
        ChangeListener<String> integertextfield = (observable, oldValue, newValue) -> {
            try {if (newValue.matches("\\d*")) {Value.setText(Integer.toString(Integer.parseInt(newValue)));}  else {Value.setText(oldValue);}}
            catch (Exception e) {Value.setText("");}
        };
        if (Switch.isSelected()) {
            Value.textProperty().addListener(integertextfield);
            Value.setText("0");
            Value.setDisable(false);
        } else {
            Value.textProperty().removeListener(integertextfield);
            Value.setText("-");
            Value.setDisable(true);
        }
    }
    public void changevalue(int newvalue) {
        if (newvalue == 0) {Switch.setSelected(false); toggleswitch();}
        else {
            Switch.setSelected(true);
            Value.setDisable(false);
            Value.setText(Integer.toString(newvalue));
            setDuration(newvalue);
        }
    }

// Creation
    public boolean build(ArrayList<Object> elementsorcutstoplay, boolean ambienceenabled) {
        setAmbienceenabled(ambienceenabled);
        setAllcutsorelementstoplay(elementsorcutstoplay);
        entrainmentlist = new ArrayList<>();
        entrainmentmedia = new ArrayList<>();
        if (name.equals("Presession")) {
            buildEntrainment();
            if (thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
                int rampdur = thisession.Root.getOptions().getSessionOptions().getRampduration();
                int number;
                int actualnumber = ((Playable) elementsorcutstoplay.get(1)).number;
                if (actualnumber > 9) {number = 10;}
                else {number = actualnumber;}
                String rampupfirstname = "ar" + number + rampdur + ".mp3";
                File ramptofirstcut = new File(Options.DIRECTORYRAMPUP, rampupfirstname);
                entrainmentlist.add(ramptofirstcut);
            }
        }
        if (name.equals("Postsession")) {
            buildEntrainment();
            if (thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
                int rampdur = thisession.Root.getOptions().getSessionOptions().getRampduration();
                int number;
                int actualnumber = ((Playable) elementsorcutstoplay.get(elementsorcutstoplay.size() - 2)).number;
                if (actualnumber > 9) {number = 10;}
                else {number = actualnumber;}
                String rampdowntopost = "zr" + number + rampdur + ".mp3";
                File thisfile = new File(Options.DIRECTORYRAMPDOWN, rampdowntopost);
                entrainmentlist.add(0, thisfile);
            }
        }
        boolean entrainmentgood = entrainmentlist.size() > 0 && entrainmentmedia.size() > 0;
        if (ambienceenabled) {return entrainmentgood && buildAmbience();}
        else {return entrainmentgood;}
    }
    @Override
    public boolean isValid() {
        return Switch.isSelected() && Integer.parseInt(Value.getText()) != 0;
    }
    @Override
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
    @Override
    public boolean hasenoughAmbience(int secondstocheck) {
        double a = 0;
        for (Double i : ambiencefiledurations) {a += i;}
        setTotalambienceduration(a);
        return a > (double) secondstocheck;
    }
    @Override
    public boolean buildEntrainment() {
        int fivetimes = 0;
        int singletimes = 0;
        if (duration != 0) {
            fivetimes = duration / 5;
            singletimes = duration % 5;
        }
        for (int i = 0; i < fivetimes; i++) {entrainmentlist.add(new File(Options.DIRECTORYMAINCUTS, "Qi-Gong5.mp3"));}
        for (int i = 0; i < singletimes; i++) {entrainmentlist.add(new File(Options.DIRECTORYMAINCUTS, "Qi-Gong1.mp3"));}
        Tools.shufflelist(entrainmentlist, 5);
        for (File i : entrainmentlist) {entrainmentmedia.add(new Media(i.toURI().toString()));}
        return entrainmentmedia.size() > 0;
    }
    @Override
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
    @Override
    public void reset() {
        if (entrainmentlist != null) entrainmentlist.clear();
        if (entrainmentmedia != null) entrainmentmedia.clear();
        if (ambiencelist != null) ambiencelist.clear();
        if (ambiencemedia != null) ambiencemedia.clear();
    }

// Playback
    @Override
    public void start() {
        super.start();
        thisession.Root.getProgressTracker().selectcut(number);
    }
    @Override
    public void tick() {
        super.tick();
        if (entrainmentplayer.getStatus() == MediaPlayer.Status.PLAYING) {
            ProgressAndGoalsWidget progressAndGoalsWidget = thisession.Root.getProgressTracker();
            progressAndGoalsWidget.getSessions().getsession(progressAndGoalsWidget.getSessions().getSession().size() - 1).updatecutduration(number, secondselapsed / 60);
        }
    }
    @Override
    public void playnextentrainment() {
        try {
            super.playnextentrainment();
        } catch (IndexOutOfBoundsException ignored) {
            System.out.println("Out Of Bounds In " + this.name + "'s Entrainment List: ");
            for (Media i : entrainmentmedia) {System.out.println(i.getSource() + i.getDuration().toSeconds());}
        }
    }
    @Override
    public void playnextambience() {
        try {
            super.playnextambience();
        } catch (IndexOutOfBoundsException ignored) {
            System.out.println("Out Of Bounds In " + this.name + "'s Ambience List: ");
            for (Media i : ambiencemedia) {System.out.println(i.getSource() + i.getDuration().toSeconds());}
        }
    }
    // Playback Getters
    @Override
    public Duration getdurationasobject() {
        double dur = super.getdurationasobject().toSeconds();
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            dur += thisession.Root.getOptions().getSessionOptions().getRampduration() * 60;
        }
        return new Duration(dur * 1000);
    }
    @Override
    public int getdurationinseconds() {
        int seconds = super.getdurationinseconds();
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            seconds += thisession.Root.getOptions().getSessionOptions().getRampduration() * 60;
        }
        return seconds;
    }
    @Override
    public int getdurationinminutes() {
        int minutes = super.getdurationinminutes();
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            minutes += thisession.Root.getOptions().getSessionOptions().getRampduration();
        }
        return minutes;
    }
    @Override
    public Double getdurationindecimalhours() {
        return Tools.convertminutestodecimalhours(this.getdurationinminutes(), 2);
    }
    @Override
    public String gettotaltimeformatted() {
        return Tools.formatlengthshort(this.getdurationinseconds());
    }
    @Override
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
    @Override
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

// Goals
    @Override
    public void setGoalsController(Goals goals) {
        GoalsController = goals;
    }
    @Override
    public Goals getGoalsController() {
        return GoalsController;
    }
    @Override
    public void setCurrentGoal() {

    }
    @Override
    public Goals.Goal getCurrentGoal() {
        return null;
    }
    @Override
    public void setGoals(List<Goals.Goal> goalslist) {

    }
    @Override
    public List<Goals.Goal> getGoals(boolean includecompleted) {
        return null;
    }
    @Override
    public void checkCurrentGoal(double currrentpracticedhours) {

    }

// Export
    @Override
    public Service<Boolean> getexportservice() {
        return null;
    }
    @Override
    public Boolean exportedsuccesfully() {
        return null;
    }
    @Override
    public Boolean cleanuptempfiles() {
        return null;
    }
    @Override
    public File getFinalexportfile() {
        return null;
    }
    @Override
    public Boolean mixentrainmentandambience() {
        return null;
    }
    @Override
    public Boolean sessionreadyforFinalExport() {
        return null;
    }

}
