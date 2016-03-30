package kujiin;

import javafx.beans.value.ChangeListener;
import javafx.concurrent.Service;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.interfaces.Creatable;
import kujiin.interfaces.Exportable;
import kujiin.interfaces.Trackable;
import kujiin.widgets.Playable;
import kujiin.widgets.ProgressAndGoalsWidget;
import kujiin.xml.Ambiences;
import kujiin.xml.Goals;
import kujiin.xml.Options;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Qi_Gong extends Playable implements Creatable, Exportable, Trackable {
    private ToggleButton Switch;
    private TextField Value;
    private Goals GoalsController;
    private ChangeListener<String> integertextfield = (observable, oldValue, newValue) -> {
        try {if (newValue.matches("\\d*")) {Value.setText(Integer.toString(Integer.parseInt(newValue)));}  else {Value.setText(oldValue);}}
        catch (Exception e) {Value.setText("");}
    };

    public Qi_Gong (int number, String name, int duration, String briefsummary, This_Session thissession, ToggleButton aSwitch, TextField value) {
        this.number = number;
        this.name = name;
        super.duration = duration;
        super.thisession = thissession;
        ambiencedirectory = new File(Options.DIRECTORYAMBIENCE, name);
        Switch = aSwitch;
        Value = value;
        Switch.setOnAction(event -> toggleswitch());
        Switch.setTooltip(new Tooltip(briefsummary));
        toggleswitch();
    }

// GUI
    public void toggleswitch() {
        if (Switch.isSelected()) {
            Value.textProperty().addListener(integertextfield);
            Value.setText("0");
            Value.setDisable(false);
            Value.setTooltip(new Tooltip("Practice Time For " + name + " (In Minutes)"));
        } else {
            Value.textProperty().removeListener(integertextfield);
            Value.setText("-");
            Value.setDisable(true);
            Value.setTooltip(new Tooltip(name + " Is Disabled. Click " + name + " Button Above To Enable"));
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
                entrainments.addtoCreated(ramptofirstcut);
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
                entrainments.addtoCreated(0, thisfile);
            }
        }
        boolean entrainmentgood = entrainments.getCreatedEntrainment().size() > 0;
        if (ambienceenabled) {return entrainmentgood && buildAmbience();}
        else {return entrainmentgood;}
    }
    @Override
    public boolean isValid() {
        return Switch.isSelected() && Integer.parseInt(Value.getText()) != 0;
    }
    @Override
    public boolean getambienceindirectory() {
        try {
            for (File i : new File(Options.DIRECTORYAMBIENCE, name).listFiles()) {if (Tools.audio_isValid(i)) ambiences.addResourceAmbience(i);}
        } catch (NullPointerException ignored) {}
        return ambiences.getAmbience().size() > 0;
    }
    @Override
    public boolean hasenoughAmbience(int secondstocheck) {
        return ambiences.getAmbienceDuration().toSeconds() >= secondstocheck;
    }
    @Override
    public boolean buildEntrainment() {
        int fivetimes = 0;
        int singletimes = 0;
        if (duration != 0) {
            fivetimes = duration / 5;
            singletimes = duration % 5;
        }
        for (int i = 0; i < fivetimes; i++) {entrainments.addtoCreated(new File(Options.DIRECTORYMAINCUTS, "Qi-Gong5.mp3"));}
        for (int i = 0; i < singletimes; i++) {entrainments.addtoCreated(new File(Options.DIRECTORYMAINCUTS, "Qi-Gong1.mp3"));}
        Tools.list_shuffle(entrainments.getCreatedEntrainment(), 5);
        return entrainments.getCreatedEntrainment().size() > 0;
    }
    @Override
    public boolean buildAmbience() {
        ambiences.reset();
        Duration currentduration = new Duration(0.0);
        // Ambience Is >= Session Duration
        if (hasenoughAmbience(getdurationinseconds())) {
            for (Ambiences.Ambience i : ambiences.getAmbience()) {
                if (ambiences.getCreatedAmbienceDuration().toSeconds() < getdurationinseconds()) {
                    ambiences.addCreatedAmbience(i);
                    currentduration.add(i.getDuration());
                } else {break;}
            }
            // Shuffle/Loop Ambience Randomly
        } else {
            Random randint = new Random();
            while (currentduration.toSeconds() < getdurationinseconds()) {
                List<Ambiences.Ambience> createdambience = ambiences.getCreatedAmbience();
                Ambiences.Ambience selectedambience = ambiences.getSelectedAmbience(randint.nextInt(ambiences.getAmbience().size() - 1));
                if (createdambience.size() < 2) {
                    ambiences.addCreatedAmbience(selectedambience);
                    currentduration.add(selectedambience.getDuration());
                } else if (createdambience.size() == 2) {
                    if (!selectedambience.equals(createdambience.get(createdambience.size() - 1))) {
                        ambiences.addCreatedAmbience(selectedambience);
                        currentduration.add(selectedambience.getDuration());
                    }
                } else if (createdambience.size() == 3) {
                    if (!selectedambience.equals(createdambience.get(createdambience.size() - 1)) && !selectedambience.equals(createdambience.get(createdambience.size() - 2))) {
                        ambiences.addCreatedAmbience(selectedambience);
                        currentduration.add(selectedambience.getDuration());
                    }
                } else if (createdambience.size() <= 5) {
                    if (!selectedambience.equals(createdambience.get(createdambience.size() - 1)) && !selectedambience.equals(createdambience.get(createdambience.size() - 2)) && !selectedambience.equals(createdambience.get(createdambience.size() - 3))) {
                        ambiences.addCreatedAmbience(selectedambience);
                        currentduration.add(selectedambience.getDuration());
                    }
                } else if (createdambience.size() > 5) {
                    if (!selectedambience.equals(createdambience.get(createdambience.size() - 1)) && !selectedambience.equals(createdambience.get(createdambience.size() - 2)) && !selectedambience.equals(createdambience.get(createdambience.size() - 3)) && !selectedambience.equals(createdambience.get(createdambience.size() - 4))) {
                        ambiences.addCreatedAmbience(selectedambience);
                        currentduration.add(selectedambience.getDuration());
                    }
                }
            }
        }
        return ambiences.getCreatedAmbience().size() > 0;
    }
    @Override
    public void reset() {
        Switch.setSelected(false);
        toggleswitch();
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
        } catch (IndexOutOfBoundsException ignored) {}
    }
    @Override
    public void playnextambience() {
        try {
            super.playnextambience();
        } catch (IndexOutOfBoundsException ignored) {}
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
        return Tools.convert_minstodecimalhours(this.getdurationinminutes(), 2);
    }
    @Override
    public String gettotaltimeformatted() {
        return Tools.format_secondsforplayerdisplay(this.getdurationinseconds());
    }
    @Override
    public void entrainmenterror() {
        System.out.println("Entrainment Error");
        // Pause Ambience If Exists
        if (Tools.gui_getconfirmationdialog(thisession.Root, "Confirmation", "An Error Occured While Playing " + name +
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
        if (Tools.gui_getconfirmationdialog(thisession.Root, "Confirmation", "An Error Occured While Playing " + name +
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
