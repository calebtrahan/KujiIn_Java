package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;

import java.io.File;

public class Qi_Gong extends Meditatable {

    public Qi_Gong (int number, String name, int duration, String briefsummary, This_Session thissession, ToggleButton aSwitch, TextField value) {
        super(number, name, duration, briefsummary, thissession, aSwitch, value);
//        if (entrainment.getFreqlong() == null) {entrainment.setFreqlong(new SoundFile(new File(Options.DIRECTORYENTRAINMENT, "Qi-Gong5.mp3")));}
//        if (entrainment.getFreqshort() == null) {entrainment.setFreqshort(new SoundFile(new File(Options.DIRECTORYENTRAINMENT, "Qi-Gong1.mp3")));}
        if (number == 0) {Value.setTooltip(new Tooltip("Minutes You Want To Collect Qi/Prana Preceding The Session"));}
        else {Value.setTooltip(new Tooltip("Minutes You Want To Collect Qi/Prana Following The Session"));}
        if (entrainment.getFreqshort() == null) {entrainment.setFreqshort(new SoundFile(new File(Options.DIRECTORYENTRAINMENT, "entrainment/Qi-Gong1.mp3"))); entrainment.calculateshortfreqduration();}
        if (entrainment.getFreqlong() == null) {entrainment.setFreqlong(new SoundFile(new File(Options.DIRECTORYENTRAINMENT, "entrainment/Qi-Gong5.mp3"))); entrainment.calculatelongfreqduration();}
    }

// GUI

    @Override
    public String getNameForChart() {
        if (name.equals("Presession")) {return "Pre";}
        else return "Post";
    }

// Creation
    @Override
    public boolean creation_buildEntrainment() {
        Duration timeleft = getduration();
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled()) {timeleft = timeleft.subtract(Duration.minutes(2));}
        int fivetimes = new Double(timeleft.toMinutes() / 5).intValue();
        int singletimes = new Double(timeleft.toMinutes() % 5).intValue();
        for (int i = 0; i < fivetimes; i++) {entrainment.created_add(entrainment.getFreqlong());}
        for (int i = 0; i < singletimes; i++) {entrainment.created_add(entrainment.getFreqshort());}
        entrainment.shuffleCreated();
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            int index = allmeditatablestoplay.indexOf(this);
            Meditatable meditatablebefore = null;
            Meditatable meditatableafter = null;
            if (index != 0) {meditatablebefore = allmeditatablestoplay.get(index - 1);}
            if (index != allmeditatablestoplay.size() - 1) {meditatableafter = allmeditatablestoplay.get(index + 1);}
            if (name.equals("Presession") && meditatableafter != null) {
                String rampupfirstname = "qiin" + meditatableafter.name.toLowerCase() + ".mp3";
                entrainment.setRampinfile(new SoundFile(new File(Options.DIRECTORYRAMP, rampupfirstname)));
                entrainment.created_add(entrainment.getRampinfile());
            }
            if (name.equals("Postsession") && meditatablebefore != null) {
                String rampdowntopost = "qiout" + meditatablebefore.name.toLowerCase() + ".mp3";
                entrainment.setRampoutfile(new SoundFile(new File(Options.DIRECTORYRAMP, rampdowntopost)));
                entrainment.created_add(0, entrainment.getRampoutfile());
            }
            if (entrainment.created_getAll().size() == 1) {return true;}
        }
        return entrainment.created_getAll().size() > 0 && entrainment.gettotalCreatedDuration().greaterThan(Duration.ZERO);
    }

// Playback
    // Playback Getters
    @Override
    public Duration getduration() {
        double dur = super.getduration().toSeconds();
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            dur += thisession.Root.getOptions().getSessionOptions().getRampduration() * 60;
        }
        return new Duration(dur * 1000);
    }
    public Duration getdurationwithoutramp() {
        return super.getduration();
    }
    public void entrainmenterror() {
        System.out.println("Entrainment Error");
        // Pause Ambience If Exists
        if (thisession.Root.dialog_YesNoConfirmation("Confirmation", "An Error Occured While Playing " + name +
                        "'s Entrainment. Problem File Is: '" + entrainmentplayer.getMedia().getSource() + "'",
                "Retry Playing This File? (Pressing Cancel Will Completely Stop Session Playback)")) {
            entrainmentplayer.stop();
            entrainmentplayer.play();
            entrainmentplayer.setOnError(this::entrainmenterror);
        } else {thisession.player_error();}
    }
    @Override
    public void ambienceerror() {
        System.out.println("Ambience Error!");
        // Pause Entrainment
        if (thisession.Root.dialog_YesNoConfirmation("Confirmation", "An Error Occured While Playing " + name +
                        "'s Ambience. Problem File Is: '" + ambienceplayer.getMedia().getSource() + "'",
                "Retry Playing This File? (Pressing Cancel Will Completely Stop Session Playback)")) {
            ambienceplayer.stop();
            ambienceplayer.play();
            ambienceplayer.setOnError(this::ambienceerror);
        } else {thisession.player_error();}
    }
// Goals

// Export

}
