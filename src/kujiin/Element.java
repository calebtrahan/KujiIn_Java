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
import kujiin.widgets.Playable;
import kujiin.widgets.ProgressAndGoalsWidget;
import kujiin.xml.Options;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

// TODO Extend Functionality Of Cut Here, Or Put Basic Functionality here And Have Cut Extend This
// TODO Put Add A Japanese Character Symbol Picture (Representing Each Cut) To Creator Cut Labels (With Tooltips Displaying Names)
// TODO Add Tooltips To Cuts Saying A One Word Brief Summary (Rin -> Strength, Kyo -> Control, Toh->Harmony)
// TODO Player On Separate Screen
public class Element extends Playable implements Creatable, Exportable {
    private ToggleButton Switch;
    private TextField Value;

    public Element(int number, String name, int duration, This_Session thissession, ToggleButton aSwitch, TextField value) {
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
    public boolean build(ArrayList<Object> elementstoplay, boolean ambienceenabled) {
        setAmbienceenabled(ambienceenabled);
        setAllcutsorelementstoplay(elementstoplay);
        if (ambienceenabled) {return buildAmbience() && buildEntrainment();}
        else {return buildEntrainment();}
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
        try {
            entrainmentlist = new ArrayList<>();
            entrainmentmedia = new ArrayList<>();
            int index = getAllcutsorelementstoplay().indexOf(this);
            Playable cutorelementbefore = (Playable) getAllcutsorelementstoplay().get(index - 1);
            Playable cutorelementafter = (Playable) getAllcutsorelementstoplay().get(index + 1);
            int adjustedduration = getdurationinminutes();
            File rampindirectory = new File(Options.DIRECTORYELEMENTRAMP, "in/");
            File rampoutdirectory = new File(Options.DIRECTORYELEMENTRAMP, "out/");
            boolean elementbefore = cutorelementbefore.number > 9;
            boolean elementafter = cutorelementafter.number > 9;
        // Calculate Ramp Duration
            int duration;
            if (getdurationinminutes() > 13) {duration = 3;}
            else if (getdurationinminutes() > 8) {duration = 2;}
            else {duration = 1;}
        // Adjust Duration For Ramp(s)
            if (! elementbefore) {adjustedduration -= duration;}
            if (! elementafter) {adjustedduration -= duration;}
        // Add Main Entrainment Files
            int fivetimes = adjustedduration / 5;
            int singletimes = adjustedduration % 5;
            for (int i = 0; i < fivetimes; i++) {
                File thisfile = new File(Options.DIRECTORYMAINCUTS, "ELEMENT5.mp3");
                entrainmentlist.add(thisfile);
            }
            for (int i = 0; i < singletimes; i++) {
                File thisfile = new File(Options.DIRECTORYMAINCUTS, "ELEMENT1.mp3");
                entrainmentlist.add(thisfile);
            }
        // Add IN RAMP
            if (! elementbefore) {
                File rampin = new File(rampindirectory, cutorelementbefore.name.toLowerCase() + duration + ".mp3");
                entrainmentlist.add(0, rampin);
            }
        // Add OUT RAMP
            if (! elementafter) {
                File rampout = new File(rampoutdirectory, cutorelementafter.name.toLowerCase() + duration + ".mp3");
                entrainmentlist.add(rampout);
            }
            Tools.shufflelist(entrainmentlist, 5);
            for (File i : entrainmentlist) {
                entrainmentmedia.add(new Media(i.toURI().toString()));
            }
            return entrainmentmedia.size() > 0;
        } catch (ArrayIndexOutOfBoundsException ignored) {return false;}
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
        } catch (IndexOutOfBoundsException ignored) {
            System.out.println("Out Of Bounds In " + name + "'s Ambience List: ");
            for (Media i : ambiencemedia) {System.out.println(i.getSource() + i.getDuration().toSeconds());}
        }
    }
    @Override
    public void playnextambience() {
        try {
            super.playnextambience();
        } catch (IndexOutOfBoundsException ignored) {
            System.out.println("Out Of Bounds In " + name + "'s Ambience List: ");
            for (Media i : entrainmentmedia) {System.out.println(i.getSource() + i.getDuration().toSeconds());}
        }
    }
    // Playback Getters
    @Override
    public Duration getdurationasobject() {
        return super.getdurationasobject();
    }
    @Override
    public int getdurationinseconds() {
        return super.getdurationinseconds();
    }
    @Override
    public int getdurationinminutes() {
        return super.getdurationinminutes();
    }
    @Override
    public Double getdurationindecimalhours() {
        return super.getdurationindecimalhours();
    }
    @Override
    public String getcurrenttimeformatted() {
        return super.getcurrenttimeformatted();
    }
    @Override
    public String gettotaltimeformatted() {
        return super.gettotaltimeformatted();
    }
    @Override
    public void entrainmenterror() {
        super.entrainmenterror();
    }
    @Override
    public void ambienceerror() {
        super.ambienceerror();
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
