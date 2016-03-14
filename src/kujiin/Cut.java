package kujiin;

import javafx.concurrent.Service;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import kujiin.interfaces.Creatable;
import kujiin.interfaces.Exportable;
import kujiin.interfaces.Trackable;
import kujiin.widgets.ProgressAndGoalsWidget;
import kujiin.xml.Goals;
import kujiin.xml.Options;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Cut extends kujiin.widgets.Playable implements Exportable, Creatable, Trackable {
    private ToggleButton Switch;
    private TextField Value;
    private Goals GoalsController;

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
        Switch.setOnAction(event -> Tools.valueboxandlabelpairswitch(Switch, Value));
//        tempentrainmenttextfile = new File(Options.DIRECTORYTEMP, "txt/" + name + "Ent.txt");
//        tempentrainmentfile = new File(Options.DIRECTORYTEMP, "Entrainment/" + name + "Temp.mp3");
//        finalentrainmentfile = new File(Options.DIRECTORYTEMP, "Entrainment/" + name + ".mp3");
//        tempambiencetextfile = new File(Options.DIRECTORYTEMP, "txt/" + name + "Amb.txt");
//        tempambiencefile = new File(Options.DIRECTORYTEMP, "Ambience/" + name + "Temp.mp3");
//        finalambiencefile = new File(Options.DIRECTORYTEMP, "Ambience/" + name + ".mp3");
//        setFinalexportfile(new File(Options.DIRECTORYTEMP, name + ".mp3"));
    }

// GUI
//    public void toggleswitch() {
//        ChangeListener<String> integertextfield = (observable, oldValue, newValue) -> {
//            try {if (newValue.matches("\\d*")) {Value.setText(Integer.toString(Integer.parseInt(newValue)));}  else {Value.setText(oldValue);}}
//            catch (Exception e) {Value.setText("");}
//        };
//        if (Switch.isSelected()) {
//            Value.textProperty().addListener(integertextfield);
//            Value.setText("0");
//            Value.setDisable(false);
//        } else {
//            Value.textProperty().removeListener(integertextfield);
//            Value.setText("-");
//            Value.setDisable(true);
//        }
//    }
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
        File rampin1 = new File(Options.DIRECTORYTOHRAMP, "3in1.mp3");
        File rampin2 = new File(Options.DIRECTORYTOHRAMP, "3in2.mp3");
        File rampout1 = new File(Options.DIRECTORYTOHRAMP, "3out1.mp3");
        File rampout2 = new File(Options.DIRECTORYTOHRAMP, "3out2.mp3");
        File rampoutspecial1 = new File(Options.DIRECTORYTOHRAMP, "3outpostsession1.mp3");
        File rampoutspecial2 = new File(Options.DIRECTORYTOHRAMP, "3outpostsession2.mp3");
        entrainmentlist = new ArrayList<>();
        entrainmentmedia = new ArrayList<>();
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
            entrainmentlist.add(thisfile);
        }
        for (int i = 0; i < singletimes; i++) {
            String filename = name + "1.mp3";
            File thisfile = new File(Options.DIRECTORYMAINCUTS, filename);
            entrainmentlist.add(thisfile);
        }
        Tools.shufflelist(entrainmentlist, 5);
        if (number == 3) {
            File rampinfile;
            File rampoutfile;
            if (duration <= 5) {rampinfile = rampin1; rampoutfile = rampout1;}
            else {rampinfile = rampin2; rampoutfile = rampout2;}
            if (cutstoplay.size() - cutstoplay.indexOf(this) <= 2) {
                if (duration <= 5) entrainmentlist.add(rampoutspecial1);
                else entrainmentlist.add(rampoutspecial2);
            } else {
                entrainmentlist.add(0, rampinfile);
                entrainmentlist.add(rampoutfile);
            }
        }
        for (File i : entrainmentlist) {
            entrainmentmedia.add(new Media(i.toURI().toString()));
        }
        return entrainmentmedia.size() > 0;
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
        super.ambiencemedia.addAll(ambiencelist.stream().map(i -> new Media(i.toURI().toString())).collect(Collectors.toList()));
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
            System.out.println("Out Of Bounds In " + this.name + "'s Entrainment List: ");
            for (Media i : entrainmentmedia) {System.out.println(i.getSource() + i.getDuration().toSeconds());}
        }
    }
    @Override
    public void playnextambience() {
        try {
            super.playnextambience();
        } catch (IndexOutOfBoundsException ignored) {
            System.out.println("Out Of Bounds In " + this.name + "'s Ambience List: ");
            for (Media i : ambiencemedia) {System.out.println(i.getSource() + i.getDuration().toSeconds());}
        }
    }
    @Override
    public void entrainmenterror() {
        System.out.println("Entrainment Error");
        // Pause Ambience If Exists
        if (Tools.getanswerdialog(thisession.Root, "Confirmation", "An Error Occured While Playing " + name +
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
        if (Tools.getanswerdialog(thisession.Root, "Confirmation", "An Error Occured While Playing " + name +
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
    //                    Tools.concatenateaudiofiles(entrainmentlist, tempentrainmenttextfile, finalentrainmentfile);
    //                    if (isCancelled()) return false;
    //                    if (ambienceenabled) {
    //                        updateProgress(0.25, 1.0);
    //                        System.out.println("Concatenating Ambience For " + name);
    //                        updateMessage("Concatenating Ambience Files");
    //                        Tools.concatenateaudiofiles(ambiencelist, tempambiencetextfile, finalambiencefile);
    //                        if (isCancelled()) return false;
    //                        updateProgress(0.50, 1.0);
    ////                            System.out.println("Reducing Ambience Duration For " + name);
    ////                            updateMessage("Cutting Ambience Audio To Selected Duration");
    ////                            System.out.println("Final Ambience File" + finalambiencefile.getAbsolutePath());
    ////                            if (Tools.getaudioduration(finalambiencefile) > getdurationinseconds()) {
    ////                                Tools.trimaudiofile(finalambiencefile, getdurationinseconds());
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
//        } else {return Tools.mixaudiofiles(new ArrayList<>(Arrays.asList(finalambiencefile, finalentrainmentfile)), getFinalexportfile());}
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
