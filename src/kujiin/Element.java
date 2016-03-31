package kujiin;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.widgets.Meditatable;
import kujiin.widgets.ProgressAndGoalsWidget;
import kujiin.xml.Ambiences;
import kujiin.xml.Goals;
import kujiin.xml.Options;

import java.io.File;
import java.util.List;
import java.util.Random;

// TODO Put Add A Japanese Character Symbol Picture (Representing Each Cut) To Creator Cut Labels (With Tooltips Displaying Names)
// TODO Add Tooltips To Cuts Saying A One Word Brief Summary (Rin -> Strength, Kyo -> Control, Toh->Harmony)
public class Element extends Meditatable {
    private ToggleButton Switch;
    private TextField Value;
    private ChangeListener<String> integertextfield = (observable, oldValue, newValue) -> {
        try {if (newValue.matches("\\d*")) {Value.setText(Integer.toString(Integer.parseInt(newValue)));}  else {Value.setText(oldValue);}}
        catch (Exception e) {Value.setText("");}
    };

    public Element(int number, String name, int duration, String briefsummary, This_Session thissession, ToggleButton aSwitch, TextField value) {
        this.number = number;
        this.name = name;
        super.duration = duration;
        super.thisession = thissession;
        ambiencedirectory = new File(Options.DIRECTORYAMBIENCE, name);
        Switch = aSwitch;
        Value = value;
        Switch.setOnAction(event -> toggleswitch());
        Switch.setTooltip(new Tooltip(briefsummary));
        Value.setTooltip(new Tooltip("Minutes You Want To Practice " + name));
        toggleswitch();
    }

// GUI
    public boolean hasValidValue() {
    return Switch.isSelected() && Integer.parseInt(Value.getText()) != 0;
}
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
        if (newvalue == 0) {
            Switch.setSelected(false);
            toggleswitch();
        } else {
            Switch.setSelected(true);
            Value.setDisable(false);
            Value.setText(Integer.toString(newvalue));
            setDuration(newvalue);
        }
    }

// Creation
    @Override
    public boolean buildEntrainment() {
        return entrainments.build(getdurationinminutes(), getAllcutsorelementstoplay());
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
    public void resetCreation() {
        Switch.setSelected(false);
        toggleswitch();
    }

// Playback
    @Override
    public void tick() {
        super.tick();
        if (entrainmentplayer.getStatus() == MediaPlayer.Status.PLAYING) {
            ProgressAndGoalsWidget progressAndGoalsWidget = thisession.Root.getProgressTracker();
            progressAndGoalsWidget.getSessions().getsession(progressAndGoalsWidget.getSessions().getSession().size() - 1).updatecutduration(number, secondselapsed / 60);
        }
    }
    @Override
    public void start() {
        super.start();
        thisession.Root.getProgressTracker().selectcut(number);
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

// Goals
    @Override
    public void setGoals(List<Goals.Goal> goalslist) {
        GoalsController.update(goalslist, number);
    }
    @Override
    public List<Goals.Goal> getGoals(boolean includecompleted) {
        return GoalsController.getallcutgoals(number, includecompleted);
    }
    @Override
    public void checkCurrentGoal(double currrentpracticedhours) {

    }

// Export

}
