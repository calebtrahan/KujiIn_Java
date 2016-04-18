package kujiin;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.media.MediaPlayer;
import kujiin.widgets.Meditatable;
import kujiin.widgets.ProgressAndGoalsWidget;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;

import java.io.File;

public class Cut extends Meditatable {
    private ToggleButton Switch;
    private TextField Value;
    private ChangeListener<String> integertextfield = (observable, oldValue, newValue) -> {
        try {if (newValue.matches("\\d*")) {Value.setText(Integer.toString(Integer.parseInt(newValue)));}  else {Value.setText(oldValue);}}
        catch (Exception e) {Value.setText("");}
    };

    public Cut(int number, String name, int duration, String briefsummary, This_Session thisession, ToggleButton aSwitch, TextField value) {
        super(number, name, duration, thisession);
//        if (entrainment.getFreqlong() == null) {entrainment.setFreqlong(new SoundFile(new File(Options.DIRECTORYENTRAINMENT, name + "5.mp3")));}
//        if (entrainment.getFreqshort() == null) {entrainment.setFreqshort(new SoundFile(new File(Options.DIRECTORYENTRAINMENT, name + "1.mp3")));}
        Switch = aSwitch;
        Value = value;
        Switch.setTooltip(new Tooltip(briefsummary));
        Value.setTooltip(new Tooltip("Minutes You Want To Practice " + name));
        Switch.setOnAction(event -> toggleswitch());
//        tempentrainmenttextfile = new File(Options.DIRECTORYTEMP, "txt/" + name + "Ent.txt");
//        tempentrainmentfile = new File(Options.DIRECTORYTEMP, "Entrainment/" + name + "Temp.mp3");
//        finalentrainmentfile = new File(Options.DIRECTORYTEMP, "Entrainment/" + name + ".mp3");
//        tempambiencetextfile = new File(Options.DIRECTORYTEMP, "txt/" + name + "Amb.txt");
//        tempambiencefile = new File(Options.DIRECTORYTEMP, "Ambience/" + name + "Temp.mp3");
//        finalambiencefile = new File(Options.DIRECTORYTEMP, "Ambience/" + name + ".mp3");
//        setFinalexportfile(new File(Options.DIRECTORYTEMP, name + ".mp3"));
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
        if (newvalue == 0) {Switch.setSelected(false);}
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
        if (duration == 0) {return false;}
        int adjustedduration = duration;
        if (number == 3) {adjustedduration -= 2;}
        int fivetimes = adjustedduration / 5;
        int singletimes = adjustedduration % 5;
        for (int i = 0; i < fivetimes; i++) {entrainment.created_add(entrainment.getFreqlong());}
        for (int i = 0; i < singletimes; i++) {entrainment.created_add(entrainment.getFreqshort());}
        entrainment.shuffleCreated();
        if (number == 3) {
            int index = allcutsorelementstoplay.indexOf(this);
            Meditatable cutorelementbefore = null;
            Meditatable cutorelementafter = null;
            if (index != 0) {cutorelementbefore = allcutsorelementstoplay.get(index - 1);}
            if (index != allcutsorelementstoplay.size() - 1) {cutorelementafter = allcutsorelementstoplay.get(index + 1);}
            if (cutorelementbefore != null && cutorelementbefore.name.equals("Presession")) {entrainment.setRampinfile(new SoundFile(new File(Options.DIRECTORYRAMP, "tohoinqi.mp3")));}
            else {entrainment.setRampinfile(new SoundFile(new File(Options.DIRECTORYRAMP, "tohin.mp3")));}
            if (cutorelementafter != null && cutorelementafter.name.equals("Postsession")) {entrainment.setRampinfile(new SoundFile(new File(Options.DIRECTORYRAMP, "tohoutqi.mp3")));}
            else {entrainment.setRampoutfile(new SoundFile(new File(Options.DIRECTORYRAMP, "tohout.mp3")));}
            entrainment.created_add(0, entrainment.getRampinfile());
            entrainment.created_add(entrainment.getRampoutfile());
        }
        return entrainment.created_getAll().size() > 0 && entrainment.gettotalCreatedDuration() / 1000 >= getdurationinseconds();
    }
    @Override
    public void resetCreation() {
        super.resetCreation();
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

// Export

}
