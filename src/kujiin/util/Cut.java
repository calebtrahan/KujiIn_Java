package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;

import java.io.File;

public class Cut extends Meditatable {

    public Cut(int number, String name, int duration, String briefsummary, This_Session thisession, ToggleButton aSwitch, TextField value) {
        super(number, name, duration, briefsummary, thisession, aSwitch, value);
        if (thisession.Root.getOptions().getProgramOptions().getTooltips()) {
            Value.setTooltip(new Tooltip("Minutes You Want To Practice " + name));
        }
    }

// GUI

// Creation
    @Override
    public boolean creation_buildEntrainment() {
        if (duration.equals(Duration.ZERO)) {return false;}
        Duration adjustedduration = duration;
        if (number == 3 && duration.greaterThanOrEqualTo(Duration.minutes(3))) {adjustedduration = adjustedduration.subtract(Duration.minutes(2));}
        Duration freqlongduration = new Duration(entrainment.getFreqlong().getDuration());
        Duration freqshortduration = new Duration(entrainment.getFreqshort().getDuration());
        while (adjustedduration.greaterThan(freqlongduration)) {
            entrainment.created_add(entrainment.getFreqlong());
            adjustedduration = adjustedduration.subtract(freqlongduration);
        }
        while (adjustedduration.greaterThan(Duration.ZERO)) {
            entrainment.created_add(entrainment.getFreqshort());
            adjustedduration = adjustedduration.subtract(freqshortduration);
        }
        entrainment.shuffleCreated();
        if (number == 3 && duration.greaterThanOrEqualTo(Duration.minutes(3))) {
            rampenabled = true;
            int index = allmeditatablestoplay.indexOf(this);
            Meditatable meditatablebefore = null;
            Meditatable meditatableafter = null;
            if (index != 0) {meditatablebefore = allmeditatablestoplay.get(index - 1);}
            if (index != allmeditatablestoplay.size() - 1) {meditatableafter = allmeditatablestoplay.get(index + 1);}
            if (meditatablebefore != null && meditatablebefore.name.equals("Presession")) {entrainment.setRampinfile(new SoundFile(new File(Options.DIRECTORYRAMP, "tohoinqi.mp3")));}
            else {entrainment.setRampinfile(new SoundFile(new File(Options.DIRECTORYRAMP, "tohin.mp3")));}
            if (meditatableafter != null && meditatableafter.name.equals("Postsession")) {
                entrainment.setRampinfile(new SoundFile(new File(Options.DIRECTORYRAMP, "tohoutqi.mp3")));
            } else {entrainment.setRampoutfile(new SoundFile(new File(Options.DIRECTORYRAMP, "tohout.mp3")));}
            entrainment.created_add(0, entrainment.getRampinfile());
            entrainment.created_add(entrainment.getRampoutfile());
            return entrainment.created_getAll().size() > 0;
        } else {rampenabled = false;}
//        System.out.println(name);
//        int count = 1;
//        for (SoundFile i : entrainment.created_getAll()) {
//            System.out.println("Entrainment " + count + ": " + i.getName());
//            count++;
//        }
        return entrainment.created_getAll().size() > 0 && entrainment.gettotalCreatedDuration().greaterThanOrEqualTo(getduration());
    }

// Playback
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
