package kujiin.util;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.ui.ProgressAndGoalsUI;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;

import java.io.File;

public class Qi_Gong extends Meditatable {
    private ToggleButton Switch;
    private TextField Value;
    private ChangeListener<String> integertextfield = (observable, oldValue, newValue) -> {
        try {if (newValue.matches("\\d*")) {Value.setText(Integer.toString(Integer.parseInt(newValue)));}  else {Value.setText(oldValue);}}
        catch (Exception e) {Value.setText("");}
    };

    public Qi_Gong (int number, String name, int duration, String briefsummary, This_Session thissession, ToggleButton aSwitch, TextField value) {
        super(number, name, duration, thissession);
//        if (entrainment.getFreqlong() == null) {entrainment.setFreqlong(new SoundFile(new File(Options.DIRECTORYENTRAINMENT, "Qi-Gong5.mp3")));}
//        if (entrainment.getFreqshort() == null) {entrainment.setFreqshort(new SoundFile(new File(Options.DIRECTORYENTRAINMENT, "Qi-Gong1.mp3")));}
        Switch = aSwitch;
        Value = value;
        Switch.setOnAction(event -> toggleswitch());
        Switch.setTooltip(new Tooltip(briefsummary));
        if (number == 0) {Value.setTooltip(new Tooltip("Minutes You Want To Collect Qi/Prana Preceding The Session"));}
        else {Value.setTooltip(new Tooltip("Minutes You Want To Collect Qi/Prana Following The Session"));}
        if (entrainment == null) {System.out.println("Entrainment Is Null");}
        if (entrainment.getFreqshort() == null) {entrainment.setFreqshort(new SoundFile(new File(Options.DIRECTORYENTRAINMENT, "entrainment/Qi-Gong1.mp3"))); entrainment.calculateshortfreqduration();}
        if (entrainment.getFreqlong() == null) {entrainment.setFreqlong(new SoundFile(new File(Options.DIRECTORYENTRAINMENT, "entrainment/Qi-Gong5.mp3"))); entrainment.calculatelongfreqduration();}
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
            Value.setText("0");
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
    @Override
    public boolean buildEntrainment() {
        int fivetimes = duration / 5;
        int singletimes = duration % 5;
        for (int i = 0; i < fivetimes; i++) {entrainment.created_add(entrainment.getFreqlong());}
        for (int i = 0; i < singletimes; i++) {entrainment.created_add(entrainment.getFreqshort());}
        entrainment.shuffleCreated();
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            int index = allcutsorelementstoplay.indexOf(this);
            Meditatable cutorelementbefore = null;
            Meditatable cutorelementafter = null;
            if (index != 0) {cutorelementbefore = allcutsorelementstoplay.get(index - 1);}
            if (index != allcutsorelementstoplay.size() - 1) {cutorelementafter = allcutsorelementstoplay.get(index + 1);}
            if (name.equals("Presession") && cutorelementafter != null) {
                String rampupfirstname = "qiin" + cutorelementafter.name.toLowerCase() + ".mp3";
                entrainment.setRampinfile(new SoundFile(new File(Options.DIRECTORYRAMP, rampupfirstname)));
                entrainment.created_add(entrainment.getRampinfile());
            }
            if (name.equals("Postsession") && cutorelementbefore != null) {
                String rampdowntopost = "qiout" + cutorelementbefore.name.toLowerCase() + ".mp3";
                entrainment.setRampoutfile(new SoundFile(new File(Options.DIRECTORYRAMP, rampdowntopost)));
                entrainment.created_add(0, entrainment.getRampoutfile());
            }
        }
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            return entrainment.created_getAll().size() > 0;
        } else {
            return entrainment.created_getAll().size() > 0 && entrainment.gettotalCreatedDuration() > 0.0;
        }
    }
    @Override
    public void resetCreation() {
        super.resetCreation();
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
            ProgressAndGoalsUI progressAndGoalsUI = thisession.Root.getProgressTracker();
            progressAndGoalsUI.getSessions().sessioninformation_getspecificsession(progressAndGoalsUI.getSessions().getSession().size() - 1).updatecutduration(number, secondselapsed / 60);
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
        return Util.convert_minstodecimalhours(this.getdurationinminutes(), 2);
    }
    @Override
    public String gettotaltimeformatted() {
        return Util.format_secondsforplayerdisplay(this.getdurationinseconds());
    }
    @Override
    public void entrainmenterror() {
        System.out.println("Entrainment Error");
        // Pause Ambience If Exists
        if (Util.gui_getokcancelconfirmationdialog(thisession.Root, "Confirmation", "An Error Occured While Playing " + name +
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
        if (Util.gui_getokcancelconfirmationdialog(thisession.Root, "Confirmation", "An Error Occured While Playing " + name +
                        "'s Ambience. Problem File Is: '" + getCurrentAmbiencePlayer().getMedia().getSource() + "'",
                "Retry Playing This File? (Pressing Cancel Will Completely Stop Session Playback)")) {
            ambienceplayer.stop();
            ambienceplayer.play();
            ambienceplayer.setOnError(this::ambienceerror);
        } else {thisession.error_endplayback();}
    }
// Goals

// Export

}
