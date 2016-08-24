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
        if (thissession.Root.getOptions().getProgramOptions().getTooltips()) {
            if (number == 0) {Value.setTooltip(new Tooltip("Minutes You Want To Collect Qi/Prana Preceding The Session"));}
            else {Value.setTooltip(new Tooltip("Minutes You Want To Collect Qi/Prana Following The Session"));}
        } else {Value.setTooltip(null);}
    }

// Entrainment
    @Override
    public void entrainment_populate() {
//        ArrayList<String> rampvariations = thisession.getallCuts().stream().map(i -> i.name).collect(Collectors.toCollection(ArrayList::new));

        super.entrainment_populate();
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
                rampenabled = true;
            } else if (name.equals("Postsession") && meditatablebefore != null) {
                String rampdowntopost = "qiout" + meditatablebefore.name.toLowerCase() + ".mp3";
                entrainment.setRampoutfile(new SoundFile(new File(Options.DIRECTORYRAMP, rampdowntopost)));
                entrainment.created_add(0, entrainment.getRampoutfile());
                rampenabled = true;
            } else {rampenabled = false;}
            if (entrainment.created_getAll().size() == 1) {return true;}
        }
        return entrainment.created_getAll().size() > 0 && entrainment.gettotalCreatedDuration().greaterThan(Duration.ZERO);
    }

// Playback
    @Override
    public Duration getduration() {
        Duration dur = super.getduration();
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            dur = dur.add(Duration.minutes(thisession.Root.getOptions().getSessionOptions().getRampduration()));
        }
        return dur;
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

}
