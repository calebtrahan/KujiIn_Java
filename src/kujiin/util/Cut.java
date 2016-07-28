package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import kujiin.MainController;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;

import java.io.File;

public class Cut extends Meditatable {

    public Cut(int number, String name, int duration, String briefsummary, This_Session thisession, ToggleButton aSwitch, TextField value) {
        super(number, name, duration, briefsummary, thisession, aSwitch, value);
        if (entrainment.getFreqshort() == null) {entrainment.setFreqshort(new SoundFile(new File(Options.DIRECTORYENTRAINMENT, "entrainment/" + super.name + "1.mp3"))); entrainment.calculateshortfreqduration();}
        if (entrainment.getFreqlong() == null) {entrainment.setFreqlong(new SoundFile(new File(Options.DIRECTORYENTRAINMENT, "entrainment/" + super.name + "5.mp3"))); entrainment.calculatelongfreqduration();}
        super.Value.setTooltip(new Tooltip("Minutes You Want To Practice " + name));
    }

// GUI

// Creation
    @Override
    public boolean buildEntrainment() {
        if (duration.equals(Duration.ZERO)) {return false;}
        Duration adjustedduration = duration;
        if (number == 3 && duration.greaterThanOrEqualTo(Duration.minutes(3))) {adjustedduration.subtract(Duration.minutes(2));}
        int fivetimes = new Double(adjustedduration.toMinutes() / 5).intValue();
        int singletimes = new Double(adjustedduration.toMinutes() % 5).intValue();
        for (int i = 0; i < fivetimes; i++) {entrainment.created_add(entrainment.getFreqlong());}
        for (int i = 0; i < singletimes; i++) {entrainment.created_add(entrainment.getFreqshort());}
        entrainment.shuffleCreated();
        if (number == 3 && duration.greaterThanOrEqualTo(Duration.minutes(3))) {
            int index = getAllmeditatablestoplay().indexOf(this);
            Meditatable meditatablebefore = null;
            Meditatable meditatableafter = null;
            if (index != 0) {meditatablebefore = getAllmeditatablestoplay().get(index - 1);}
            if (index != getAllmeditatablestoplay().size() - 1) {meditatableafter = getAllmeditatablestoplay().get(index + 1);}
            if (meditatablebefore != null && meditatablebefore.name.equals("Presession")) {entrainment.setRampinfile(new SoundFile(new File(Options.DIRECTORYRAMP, "tohoinqi.mp3")));}
            else {entrainment.setRampinfile(new SoundFile(new File(Options.DIRECTORYRAMP, "tohin.mp3")));}
            if (meditatableafter != null && meditatableafter.name.equals("Postsession")) {
                entrainment.setRampinfile(new SoundFile(new File(Options.DIRECTORYRAMP, "tohoutqi.mp3")));
            } else {
                entrainment.setRampoutfile(new SoundFile(new File(Options.DIRECTORYRAMP, "tohout.mp3")));
            }
            entrainment.created_add(0, entrainment.getRampinfile());
            entrainment.created_add(entrainment.getRampoutfile());
        }
        return entrainment.created_getAll().size() > 0 && entrainment.gettotalCreatedDuration().greaterThanOrEqualTo(getduration());
    }

// Playback
    @Override
    public void tick() {
        super.tick();
        if (thisession.getPlayerState() == MainController.PlayerState.PLAYING) {
            thisession.Root.getSessions().sessioninformation_getspecificsession(thisession.Root.getSessions().getSession().size() - 1).updatecutduration(number, new Double(elapsedtime.toMinutes()).intValue());
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
