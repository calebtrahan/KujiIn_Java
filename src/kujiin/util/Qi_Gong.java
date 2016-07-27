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
    public boolean buildEntrainment() {
        int fivetimes = duration / 5;
        int singletimes = duration % 5;
        for (int i = 0; i < fivetimes; i++) {entrainment.created_add(entrainment.getFreqlong());}
        for (int i = 0; i < singletimes; i++) {entrainment.created_add(entrainment.getFreqshort());}
        entrainment.shuffleCreated();
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            int index = getAllmeditatablestoplay().indexOf(this);
            Meditatable meditatablebefore = null;
            Meditatable meditatableafter = null;
            if (index != 0) {meditatablebefore = getAllmeditatablestoplay().get(index - 1);}
            if (index != getAllmeditatablestoplay().size() - 1) {meditatableafter = getAllmeditatablestoplay().get(index + 1);}
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
        }
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            return entrainment.created_getAll().size() > 0;
        } else {
            return entrainment.created_getAll().size() > 0 && entrainment.gettotalCreatedDuration() > 0.0;
        }
    }

// Playback
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
