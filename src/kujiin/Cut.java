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
import kujiin.widgets.ProgressAndGoalsWidget;
import kujiin.xml.Ambiences;
import kujiin.xml.Goals;
import kujiin.xml.Options;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cut extends kujiin.widgets.Playable implements Exportable, Creatable, Trackable {
    private ToggleButton Switch;
    private TextField Value;
    private Goals GoalsController;
    private ChangeListener<String> integertextfield = (observable, oldValue, newValue) -> {
        try {if (newValue.matches("\\d*")) {Value.setText(Integer.toString(Integer.parseInt(newValue)));}  else {Value.setText(oldValue);}}
        catch (Exception e) {Value.setText("");}
    };

    public Cut(int number, String name, int duration, String briefsummary, This_Session thisession, ToggleButton aSwitch, TextField value) {
        this.number = number;
        this.name = name;
        super.duration = duration;
        super.thisession = thisession;
        ambiencedirectory = new File(Options.DIRECTORYAMBIENCE, name);
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
    public boolean build(ArrayList<Object> elementorcutstoplay, boolean ambienceenabled) {
        setAmbienceenabled(ambienceenabled);
        setAllcutsorelementstoplay(elementorcutstoplay);
        if (ambienceenabled) {return buildEntrainment() && buildAmbience();}
        else {return buildEntrainment();}
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
        File rampin1 = new File(Options.DIRECTORYTOHRAMP, "3in1.mp3");
        File rampin2 = new File(Options.DIRECTORYTOHRAMP, "3in2.mp3");
        File rampout1 = new File(Options.DIRECTORYTOHRAMP, "3out1.mp3");
        File rampout2 = new File(Options.DIRECTORYTOHRAMP, "3out2.mp3");
        File rampoutspecial1 = new File(Options.DIRECTORYTOHRAMP, "3outpostsession1.mp3");
        File rampoutspecial2 = new File(Options.DIRECTORYTOHRAMP, "3outpostsession2.mp3");
        int fivetimes = 0;
        int singletimes = 0;
        if (duration != 0) {
            fivetimes = duration / 5;
            singletimes = duration % 5;
        }
        if (number == 3) {
            int adjustedduration = duration;
            if (duration <= 5) adjustedduration -= 2;
            else adjustedduration -= 4;
            fivetimes = adjustedduration / 5;
            singletimes = adjustedduration % 5;
        }
        for (int i = 0; i < fivetimes; i++) {
            String filename = name + "5.mp3";
            File thisfile = new File(Options.DIRECTORYMAINCUTS, filename);
            entrainments.addtoCreated(thisfile);
        }
        for (int i = 0; i < singletimes; i++) {
            String filename = name + "1.mp3";
            File thisfile = new File(Options.DIRECTORYMAINCUTS, filename);
            entrainments.addtoCreated(thisfile);
        }
        Tools.list_shuffle(entrainments.getCreatedEntrainment(), 5);
        if (number == 3) {
            File rampinfile;
            File rampoutfile;
            if (duration <= 5) {rampinfile = rampin1; rampoutfile = rampout1;}
            else {rampinfile = rampin2; rampoutfile = rampout2;}
            if (cutstoplay.size() - cutstoplay.indexOf(this) <= 2) {
                if (duration <= 5) entrainments.addtoCreated(rampoutspecial1);
                else entrainments.addtoCreated(rampoutspecial2);
            } else {
                entrainments.addtoCreated(0, rampinfile);
                entrainments.addtoCreated(rampoutfile);
            }
        }
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
        return getGoalsController().getallcutgoals(number, includecompleted);
    }
    @Override
    public void checkCurrentGoal(double currrentpracticedhours) {

    }

// Export
    @Override
    public Service<Boolean> getexportservice() {
    //    return new Service<Boolean>() {
    //        @Override
    //        protected Task<Boolean> createTask() {
    //            return new Task<Boolean>() {
    //                @Override
    //                protected Boolean call() throws Exception {
    //                    updateTitle("Building " + name);
    //                    System.out.println("Concatenating Entrainment For " + name);
    //                    updateMessage("Concatenating Entrainment Files");
    //                    Tools.audio_concatenatefiles(entrainmentlist, tempentrainmenttextfile, finalentrainmentfile);
    //                    if (isCancelled()) return false;
    //                    if (ambienceenabled) {
    //                        updateProgress(0.25, 1.0);
    //                        System.out.println("Concatenating Ambience For " + name);
    //                        updateMessage("Concatenating Ambience Files");
    //                        Tools.audio_concatenatefiles(ambiencelist, tempambiencetextfile, finalambiencefile);
    //                        if (isCancelled()) return false;
    //                        updateProgress(0.50, 1.0);
    ////                            System.out.println("Reducing Ambience Duration For " + name);
    ////                            updateMessage("Cutting Ambience Audio To Selected Duration");
    ////                            System.out.println("Final Ambience File" + finalambiencefile.getAbsolutePath());
    ////                            if (Tools.audio_getduration(finalambiencefile) > getdurationinseconds()) {
    ////                                Tools.audio_trimfile(finalambiencefile, getdurationinseconds());
    ////                            }
    ////                            if (isCancelled()) return false;
    //                        updateProgress(0.75, 1.0);
    //                        System.out.println("Mixing Final Audio For " + name);
    //                        updateMessage("Combining Entrainment And Ambience Files");
    //                        mixentrainmentandambience();
    //                        if (isCancelled()) return false;
    //                        updateProgress(1.0, 1.0);
    //                    } else {updateProgress(1.0, 1.0);}
    //                    return exportedsuccessfully();
    //                }
    //            };
    //        }
    //    };
            return null;
        }
    @Override
    public Boolean exportedsuccesfully() {
//        if (ambienceenabled) {return finalambiencefile.exists() && finalentrainmentfile.exists();}
//        else {return finalentrainmentfile.exists();}
        return false;
    }
    @Override
    public Boolean mixentrainmentandambience() {
//        if (! ambienceenabled) {
//            try {
//                FileUtils.copyFile(finalentrainmentfile, getFinalexportfile());
//                return true;
//            } catch (IOException e) {return false;}
//        } else {return Tools.audio_mixfiles(new ArrayList<>(Arrays.asList(finalambiencefile, finalentrainmentfile)), getFinalexportfile());}
        return false;
    }
    @Override
    public Boolean sessionreadyforFinalExport() {
//        boolean cutisgood;
//        File entrainmentfile = new File(Options.DIRECTORYTEMP, "Entrainment/" + name + ".mp3");
//        cutisgood = entrainmentfile.exists();
//        if (ambienceenabled) {
//            File ambiencefile = new File(Options.DIRECTORYTEMP, "Ambience/" + name + ".mp3");
//            cutisgood = ambiencefile.exists();
//        }
//        return cutisgood;
        return false;
    }
    @Override
    public Boolean cleanuptempfiles() {
//        if (tempambiencefile.exists()) {tempambiencefile.delete();}
//        if (tempentrainmentfile.exists()) {tempentrainmentfile.delete();}
//        if (tempentrainmenttextfile.exists()) {tempentrainmenttextfile.delete();}
//        if (tempambiencetextfile.exists()) {tempambiencetextfile.delete();}
        return false;
    }
    @Override
    public File getFinalexportfile() {
        return null;
    }

}
