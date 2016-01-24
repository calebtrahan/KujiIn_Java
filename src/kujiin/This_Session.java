package kujiin;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import kujiin.dialogs.SimpleTextDialogWithCancelButton;
import kujiin.widgets.CreatorAndExporterWidget;
import kujiin.widgets.GoalsWidget;
import kujiin.widgets.PlayerWidget;
import kujiin.xml.Sessions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class This_Session {
    public static final File projectroot = new File(System.getProperty("user.dir"));
    public static final File rootdirectory = new File(This_Session.projectroot, "src/kujiin/");
    public static final File sounddirectory = new File(This_Session.rootdirectory, "assets/sound/");
    public static final File xmldirectory = new File(This_Session.rootdirectory, "assets/xml/");
    public static final File directoryentrainment = new File(This_Session.sounddirectory, "entrainment/");
    public static final File directoryambience = new File(This_Session.sounddirectory, "ambience/");
    public static final File directorytemp = new File(This_Session.sounddirectory, "temp/");
    public static final File directorymaincuts = new File(This_Session.directoryentrainment, "maincuts/");
    public static final File directorytohramp = new File(This_Session.directoryentrainment, "tohramp/");
    public static final File directoryrampdown = new File(This_Session.directoryentrainment, "ramp/down/");
    public static final File directoryrampup = new File(This_Session.directoryentrainment, "ramp/up/");
    public static final File alertfile = new File(This_Session.sounddirectory, "Alert.mp3");
    public static final File logfile = new File(This_Session.rootdirectory, "assets/sessionlog.txt");
    public static final File directoryreference = new File(This_Session.rootdirectory, "assets/reference/");
    public static final File sessionsxmlfile = new File(This_Session.xmldirectory, "sessions.xml");
    public static final File currentgoalsxmlfile = new File(This_Session.xmldirectory, "current_goals.xml");
    public static final File completedgoalsxmlfile = new File(This_Session.xmldirectory, "completed_goals.xml");
    public static final ArrayList<String> allnames = new ArrayList<>(Arrays.asList(
            "Presession", "RIN", "KYO", "TOH", "SHA", "KAI", "JIN", "RETSU", "ZAI", "ZEN", "Postsession"));
    private Cut presession = new Cut(0, "Presession", true, 0, this);
    private Cut rin = new Cut(1, "RIN", false, 0, this);
    private Cut kyo = new Cut(2, "KYO", false, 0, this);
    private Cut toh = new Cut(3, "TOH", false, 0, this);
    private Cut sha = new Cut(4, "SHA", false, 0, this);
    private Cut jin = new Cut(6, "JIN", false, 0, this);
    private Cut kai = new Cut(5, "KAI", false, 0, this);
    private Cut retsu = new Cut(7, "RETSU", false, 0, this);
    private Cut zai = new Cut(8, "ZAI", false, 0, this);
    private Cut zen = new Cut(9, "ZEN", false, 0, this);
    private Cut postsession = new Cut(10, "Postsession", true, 0, this);
    private ArrayList<Cut> cutsinsession;
    private Boolean ambienceenabled;
    private PlayerWidget.PlayerState playerState;
    private Cut currentcut;
    private Timeline currentcuttimeline;
    private Sessions sessions;
    private int totalsecondselapsed;
    private int totalsecondsinsession;
    private int cutcount;
    private Label CutCurrentTime;
    private Label CutTotalTime;
    private Label SessionCurrentTime;
    private Label SessionTotalTime;
    private ProgressBar CutProgress;
    private ProgressBar TotalProgress;
    private Label CutPlayingText;
    private Label SessionPlayingText;
    private Label StatusBar;
    private PlayerWidget.DisplayReference displayReference;
    private Boolean referencedisplayoption;
    private Boolean referencefullscreenoption;
    private PlayerWidget.ReferenceType referenceType;
    private Double entrainmentvolume;
    private Double ambiencevolume;
    private GoalsWidget goalsWidget;
    private MainController Root;

    public This_Session(MainController mainController) {
        Root = mainController;
        this.sessions = Root.getProgressTrackerWidget().getSessions();
        CutCurrentTime = Root.CutProgressLabelCurrent;
        CutTotalTime = Root.CutProgressLabelTotal;
        SessionCurrentTime = Root.TotalProgressLabelCurrent;
        SessionTotalTime = Root.TotalProgressLabelTotal;
        CutProgress = Root.CutProgressBar;
        TotalProgress = Root.TotalProgressBar;
        CutPlayingText = Root.CutProgressTopLabel;
        SessionPlayingText = Root.TotalSessionLabel;
        StatusBar = Root.StatusBar;
        cutsinsession = new ArrayList<>();
        ambienceenabled = false;
        setPlayerState(PlayerWidget.PlayerState.IDLE);
   }

// Getters And Setters
    public void setAmbienceenabled(Boolean ambienceenabled) {
        this.ambienceenabled = ambienceenabled;
    }
    public boolean getAmbienceenabled() {return ambienceenabled;}
    public void setCutsinsession(ArrayList<Cut> cutsinsession) {this.cutsinsession = cutsinsession;}
    public ArrayList<Cut> getCutsinsession() {return cutsinsession;}
    public String gettotalsessionduration() {
        Integer totaltime = 0;
        for (Cut i : cutsinsession) {
            totaltime += i.getdurationinminutes();
        }
        return Tools.minutestoformattedhoursandmins(totaltime);
    }
    public PlayerWidget.PlayerState getPlayerState() {return playerState;}
    public void setPlayerState(PlayerWidget.PlayerState playerState) {this.playerState = playerState;}
    public PlayerWidget.ReferenceType getReferenceType() {
        return referenceType;
    }
    public void setReferenceType(PlayerWidget.ReferenceType referenceType) {
        this.referenceType = referenceType;
    }
    public ArrayList<Cut> getallCuts() {return new ArrayList<>(Arrays.asList(presession, rin, kyo, toh, sha, kai, jin, retsu, zai, zen, postsession));}
    public boolean isValid() {
        int totaltime = 0;
        for (Cut i : getallCuts()) {
            if (i.number != 0 && i.number != 10) {totaltime += i.duration;}
        }
        return totaltime > 0;
    }
    public Boolean isReferencedisplayoption() {return referencedisplayoption;}
    public void setReferencedisplayoption(boolean referencedisplayoption) {this.referencedisplayoption = referencedisplayoption;}
    public void setReferencefullscreenoption(Boolean referencefullscreenoption) {
        this.referencefullscreenoption = referencefullscreenoption;
    }
    public void setSessionAmbienceVolume(Double volume) {entrainmentvolume = volume;}
    public Double getSessionAmbienceVolume() {return ambiencevolume;}
    public void setSessionEntrainmentVolume(Double volume) {ambiencevolume = volume;}
    public Double getSessionEntrainmentVolume() {return entrainmentvolume;}
    public GoalsWidget getGoalsWidget() {return goalsWidget;}
    public void setGoalsWidget(GoalsWidget goalsWidget) {this.goalsWidget = goalsWidget;}

// Creation Methods
    public static void deleteprevioussession() {
        ArrayList<File> folders = new ArrayList<>();
        folders.add(new File(This_Session.directorytemp, "Ambience"));
        folders.add(new File(This_Session.directorytemp, "Entrainment"));
        folders.add(new File(This_Session.directorytemp, "txt"));
        folders.add(new File(This_Session.directorytemp, "Export"));
        for (File i : folders) {
            try {
                for (File x : i.listFiles()) {x.delete();}
            } catch (NullPointerException ignored) {}
        }
    }
    public boolean textfieldvaluesareOK(ArrayList<Integer> textfieldvalues) {
        for (int i = 0; i < textfieldvalues.size(); i++) {
            if (i != 0 && i != 10 && textfieldvalues.get(i) > 0) {
                return true;}
        }
        return false;
    }
    public void checkifambienceisgood(ArrayList<Integer> textfieldvalues, CheckBox ambiencecheckbox) {
        if (textfieldvaluesareOK(textfieldvalues)) {
            ArrayList<Cut> cutswithnoambience = new ArrayList<>();
            ArrayList<Cut> cutswithreducedambience = new ArrayList<>();
            Cut[] tempcuts = {presession, rin, kyo, toh, sha, kai, jin, retsu, zai, zen, postsession};
            Service<Void> ambiencecheckerservice = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            int cutcount = 0;
                            for (Integer i : textfieldvalues) {
                                if (i != 0) {
                                    Cut thiscut = tempcuts[cutcount];
                                    updateMessage(String.format("Currently Checking %s...", thiscut.name));
                                    if (thiscut.getambienceindirectory()) {
                                        if (!thiscut.hasenoughAmbience(i * 60)) {cutswithreducedambience.add(thiscut);}
                                    } else {cutswithnoambience.add(thiscut);}
                                }
                                cutcount++;
                            }
                            updateMessage("Done Checking Ambience");
                            return null;
                        }
                    };
                }
            };
            final SimpleTextDialogWithCancelButton[] cad = new SimpleTextDialogWithCancelButton[1];
            ambiencecheckerservice.setOnRunning(event -> {
                cad[0] = new SimpleTextDialogWithCancelButton("Checking Ambience", "Checking Ambience", "");
                cad[0].Message.textProperty().bind(ambiencecheckerservice.messageProperty());
                cad[0].CancelButton.setOnAction(ev -> ambiencecheckerservice.cancel());
                cad[0].showAndWait();
            });
            ambiencecheckerservice.setOnSucceeded(event -> {
                cad[0].close();
                if (cutswithnoambience.size() > 0) {
                    StringBuilder a = new StringBuilder();
                    for (int i = 0; i < cutswithnoambience.size(); i++) {
                        a.append(cutswithnoambience.get(i).name);
                        if (i != cutswithnoambience.size() - 1) {a.append(", ");}
                    }
                    if (cutswithnoambience.size() > 1) {
                        Tools.showerrordialog("Error", String.format("%s Have No Ambience At All", a.toString()), "Cannot Add Ambience");
                        if (Tools.getanswerdialog("Add Ambience", a.toString() + " Needs Ambience", "Open The Ambience Editor?")) {
                            MainController.SessionAmbienceEditor ambienceEditor = new MainController.SessionAmbienceEditor();
                            ambienceEditor.showAndWait();
                        }
                    } else {
                        Tools.showerrordialog("Error", String.format("%s Have No Ambience At All", a.toString()), "Cannot Add Ambience");
                        if (Tools.getanswerdialog("Add Ambience", a.toString() + " Need Ambience", "Open The Ambience Editor?")) {
                            MainController.SessionAmbienceEditor ambienceEditor = new MainController.SessionAmbienceEditor(cutswithnoambience.get(0).name);
                            ambienceEditor.showAndWait();
                        }
                    }
                    ambiencecheckbox.setSelected(false);
                } else {
                    if (cutswithreducedambience.size() > 0) {
                        StringBuilder a = new StringBuilder();
                        int count = 1;
                        for (int i = 0; i < cutswithreducedambience.size(); i++) {
                            a.append("\n");
                            Cut thiscut = cutswithreducedambience.get(i);
                            String formattedcurrentduration = Tools.minutestoformattedhoursandmins((int) thiscut.getTotalambienceduration() / 60);
                            String formattedexpectedduration = Tools.minutestoformattedhoursandmins(textfieldvalues.get(This_Session.allnames.indexOf(cutswithreducedambience.get(i).name)));
                            a.append(count).append(". ").append(thiscut.name).append(" >  Current: ").append(formattedcurrentduration).append(" | Needed: ").append(formattedexpectedduration);
                            count++;
                        }
                        if (cutswithreducedambience.size() == 1) {
                            ambiencecheckbox.setSelected(Tools.getanswerdialog("Confirmation", String.format("The Following Cut's Ambience Isn't Long Enough: %s ", a.toString()), "Shuffle And Loop Ambience For This Cut?"));
                        } else {
                            ambiencecheckbox.setSelected(Tools.getanswerdialog("Confirmation", String.format("The Following Cuts' Ambience Aren't Long Enough: %s ", a.toString()), "Shuffle And Loop Ambience For These Cuts?"));
                        }
                    } else {
                        ambiencecheckbox.setSelected(true);
                    }
                }
            });
            ambiencecheckerservice.setOnCancelled(event -> {
                cad[0].close();
                ambiencecheckbox.setSelected(false);
            });
            ambiencecheckerservice.setOnFailed(event -> {
                cad[0].close();
                Tools.showerrordialog("Error", "Couldn't Check Ambience", "Check Ambience Folder Read Permissions");
                ambiencecheckbox.setSelected(false);
            });
            ambiencecheckerservice.start();
        } else {Tools.showinformationdialog("Information", "Cannot Check Ambience", "No Cuts Have > 0 Values, So I Don't Know Which Ambience To Check");}
    }
    public boolean sessioncreationwellformednesschecks(ArrayList<Integer> textfieldtimes) {
        int lastcutindex = 0;
        for (int i = 0; i < textfieldtimes.size(); i++) {
            if (textfieldtimes.get(i) > 0) {lastcutindex = i;}
        }
        // Get NonSequential Cuts
        ArrayList<Integer> indexestochange = new ArrayList<>();
        for (int i = 0; i < lastcutindex; i++) {
            if (i > 0) {if (textfieldtimes.get(i) == 0) {indexestochange.add(i);}}
        }
        if (indexestochange.size() > 0) {
            ArrayList<String> cutsmissinglist = new ArrayList<>();
            for (Integer x : indexestochange) {cutsmissinglist.add(This_Session.allnames.get(x));}
            StringBuilder cutsmissingtext = new StringBuilder();
            for (int i = 0; i < cutsmissinglist.size(); i++) {
                cutsmissingtext.append(cutsmissinglist.get(i));
                if (i != cutsmissinglist.size() - 1) {cutsmissingtext.append(", ");}
            }
            CreatorAndExporterWidget.SessionNotWellformedDialog notWellformedDialog = new CreatorAndExporterWidget.SessionNotWellformedDialog(null, textfieldtimes, cutsmissingtext.toString(), lastcutindex);
            notWellformedDialog.showAndWait();
            if (notWellformedDialog.isCreatesession()) {
                int invocationduration = notWellformedDialog.getInvocationduration();
                for (int i : indexestochange) {textfieldtimes.set(i, invocationduration);}
                return true;
            } else {return false;}
        }
        return true;
    }
    public boolean setupcutsinsession(ArrayList<Integer> textfieldtimes) {
        presession.setDuration(textfieldtimes.get(0));
        rin.setDuration(textfieldtimes.get(1));
        kyo.setDuration(textfieldtimes.get(2));
        toh.setDuration(textfieldtimes.get(3));
        sha.setDuration(textfieldtimes.get(4));
        kai.setDuration(textfieldtimes.get(5));
        jin.setDuration(textfieldtimes.get(6));
        retsu.setDuration(textfieldtimes.get(7));
        zai.setDuration(textfieldtimes.get(8));
        zen.setDuration(textfieldtimes.get(9));
        postsession.setDuration(textfieldtimes.get(10));
        cutsinsession.add(presession);
        if (rin.duration != 0) {cutsinsession.add(rin);}
        if (kyo.duration != 0) {cutsinsession.add(kyo);}
        if (toh.duration != 0) {cutsinsession.add(toh);}
        if (sha.duration != 0) {cutsinsession.add(sha);}
        if (kai.duration != 0) {cutsinsession.add(kai);}
        if (jin.duration != 0) {cutsinsession.add(jin);}
        if (retsu.duration != 0) {cutsinsession.add(retsu);}
        if (zai.duration != 0) {cutsinsession.add(zai);}
        if (zen.duration != 0) {cutsinsession.add(zen);}
        cutsinsession.add(postsession);
        return cutsinsession.size() > 0;
    }
    public void create(ArrayList<Integer> textfieldtimes) {
        if (sessioncreationwellformednesschecks(textfieldtimes)) {
            setupcutsinsession(textfieldtimes);
            final SimpleTextDialogWithCancelButton[] sdcb = new SimpleTextDialogWithCancelButton[1];
            Service<Void> creationservice = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            for (Cut i : cutsinsession) {
                                updateMessage("Creating " + i.name);
                                if (! i.build(cutsinsession, ambienceenabled)) {cancel();}
                                updateMessage("Finished Creating " + i.name);
                            }
                            return null;
                    }};
                }
            };
            creationservice.setOnRunning(event -> {
                sdcb[0] = new SimpleTextDialogWithCancelButton("Creating Session", "Creating Session", "");
                sdcb[0].Message.textProperty().bind(creationservice.messageProperty());
                sdcb[0].CancelButton.setOnAction(ev -> creationservice.cancel());
            });
            creationservice.setOnCancelled(event -> {
                resetallcuts();
                sdcb[0].close();
            });
            creationservice.setOnSucceeded(event -> {
                sdcb[0].close();
            });
            creationservice.setOnFailed(event -> {
                Platform.runLater(() -> Tools.showerrordialog("Error", "Session Creation Failed", "Check Sound File Read Permissions"));
                resetallcuts();
                sdcb[0].close();
            });
            creationservice.start();
        }
    }
    public void resetallcuts() {
        for (Cut i : getallCuts()) {
            i.reset();
            i.setAmbienceenabled(false);
        }
    }

// Export
    public boolean export() {
        FileChooser fileChooser = new FileChooser();
        File exportfile = fileChooser.showOpenDialog(null);
        if (exportfile == null) {return false;}
        CreatorAndExporterWidget.ExportingSessionDialog exportingSessionDialog = new CreatorAndExporterWidget.ExportingSessionDialog(this);
        Service<Boolean> exporterservice = new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                    // Combine Cut Entrainment + Ambience
                        for (Cut i : cutsinsession) {
                            if (isCancelled()) {return false;}
                            updateMessage("Currently Creating " + i.name);
                            updateProgress((double) cutsinsession.indexOf(i), (double) cutsinsession.size() - 1);
                            boolean cutcreatedsuccesfully = i.export();
                            if (!cutcreatedsuccesfully) {return false;}
                            updateMessage("Finished Creating " + i.name);
                        }
                    // Combine Cut Files + Alert Files into Exportfile

                    // Test Exportfile To Make Sure It's Not Zero And Doesn't Throw An Error When Opened In A Mediaplayer

                        return exportfile.exists();
                    }
                };
            }
        };
        exportingSessionDialog.creatingsessionProgressBar.progressProperty().bind(exporterservice.progressProperty());
        exportingSessionDialog.creatingsessionTextStatusBar.textProperty().bind(exporterservice.messageProperty());
        exportingSessionDialog.CancelButton.setOnAction(event -> exporterservice.cancel());
        exporterservice.setOnSucceeded(event -> {
            if (exporterservice.getValue()) {Tools.showinformationdialog("Information", "Export Succeeded", "File Saved To: ");}
            else {Tools.showerrordialog("Error", "Errors Occured During Export", "Please Try Again Or Contact Me For Support");}
            exportingSessionDialog.close();
        });
        exporterservice.setOnFailed(event -> {
            String v = exporterservice.getException().getMessage();
            Tools.showerrordialog("Error", "Errors Occured While Trying To Create The This_Session. The Main Exception I Encoured Was " + v,
                    "Please Try Again Or Contact Me For Support");
            This_Session.deleteprevioussession();
            exportingSessionDialog.close();
        });
        exporterservice.setOnCancelled(event -> {
            Tools.showinformationdialog("Cancelled", "Export Cancelled", "You Cancelled Export");
            This_Session.deleteprevioussession();
            exportingSessionDialog.close();
        });
        return false;
    }

// Playback
    public void startplayback() {
        totalsecondselapsed = 0;
        totalsecondsinsession = 0;
        for (Cut i : cutsinsession) {totalsecondsinsession += i.getdurationinseconds();}
        SessionTotalTime.setText(Tools.formatlengthshort(totalsecondsinsession));
        currentcuttimeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> updateplayerui()));
        currentcuttimeline.setCycleCount(Animation.INDEFINITE);
        currentcuttimeline.play();
        cutcount = 0;
        currentcut = cutsinsession.get(cutcount);
        setSessionEntrainmentVolume(PlayerWidget.ENTRAINMENTVOLUME);
        setSessionAmbienceVolume(PlayerWidget.AMBIENCEVOLUME);
        playthiscut();
        sessions.createnewsession();
    }
    public String play() {
        if (playerState == PlayerWidget.PlayerState.IDLE) {
            startplayback();
            return "Playing Session...";
        }
        else if(playerState == PlayerWidget.PlayerState.PAUSED) {
            currentcuttimeline.play();
            currentcut.resume();
            setPlayerState(PlayerWidget.PlayerState.PLAYING);
            return "Resuming Session...";
        }
        else if(playerState == PlayerWidget.PlayerState.STOPPED) {
            startplayback();
            return "Playing Session...";
        }
        else if(playerState == PlayerWidget.PlayerState.PLAYING) {
            return "Already Playing";
        }
        else if(playerState == PlayerWidget.PlayerState.TRANSITIONING) {
            return "Transistioning To The Next Cut";
        } else {
            return "";
        }
    }
    public String pause() {
        if (playerState == PlayerWidget.PlayerState.PLAYING) {
            currentcut.pause();
            currentcuttimeline.pause();
            setPlayerState(PlayerWidget.PlayerState.PAUSED);
            return "Session Paused";
        } else if (playerState == PlayerWidget.PlayerState.PAUSED) {
            return "Already Paused";
        } else if (playerState == PlayerWidget.PlayerState.TRANSITIONING) {
            return "Currently Transitioning To The Next Cut. Please Wait Till At The Next Cut To Pause";
        } else {
            return "No Session Playing";
        }
    }
    public String stop() {
        if (playerState == PlayerWidget.PlayerState.PLAYING) {
            pause();
            if (Tools.getanswerdialog("End Prematurely", "End Session", "Really End This Session Prematurely?")) {
                endsessionprematurely();
                currentcut.stop();
                currentcuttimeline.stop();
                setPlayerState(PlayerWidget.PlayerState.STOPPED);
                resetthissession();
                return "Session Stopped";
            } else {play(); return "";}
        } else if (playerState == PlayerWidget.PlayerState.PAUSED) {
            if (Tools.getanswerdialog("End Prematurely", "End Session", "Really End This Session Prematurely?")) {
                endsessionprematurely();
                currentcut.stop();
                currentcuttimeline.stop();
                setPlayerState(PlayerWidget.PlayerState.STOPPED);
                resetthissession();
                return "Session Stopped";
            } else {play(); return "";}
        } else if (playerState == PlayerWidget.PlayerState.IDLE) {
            return "No Session Playing, Cannot Stop";
        } else if (playerState == PlayerWidget.PlayerState.TRANSITIONING) {
            return "Currently Transitioning To The Next Cut. Please Wait Till At The Next Cut To Stop";
        } else {
            return "";
        }
    }
    public void endsessionprematurely() {
        int secondsleft = currentcut.getdurationinseconds() / currentcut.getSecondselapsed();
        int secondspracticed = currentcut.getdurationinseconds() - secondsleft;
        Double minutes = Math.floor(secondspracticed / 60);
        sessions.getsession(sessions.sessionscount() - 1).updatecutduration(currentcut.number, minutes.intValue());
        GoalsWidget.PrematureEndingDialog ped = new GoalsWidget.PrematureEndingDialog();
        String prematureendingreason = ped.getReason();
        if (prematureendingreason != null) {
            sessions.getsession(sessions.sessionscount() - 1).writeprematureending(currentcut.name, prematureendingreason);
        }
    }
    public void updateplayerui() {
        try {
            if (playerState == PlayerWidget.PlayerState.PLAYING) {
                totalsecondselapsed++;
                CutPlayingText.setText(String.format("%s Progress", currentcut.name));
                SessionPlayingText.setText("Total Progress");
                CutCurrentTime.setText(currentcut.getcurrenttimeformatted());
                CutTotalTime.setText(currentcut.gettotaltimeformatted());
                SessionCurrentTime.setText(Tools.formatlengthshort(totalsecondselapsed));
                StatusBar.setText("Session Playing. Currently Practicing " + currentcut.name + "...");
                if (currentcut.getSecondselapsed() != 0) {CutProgress.setProgress((float) currentcut.getSecondselapsed() / (float) currentcut.getdurationinseconds());}
                if (totalsecondselapsed != 0) {TotalProgress.setProgress((float) totalsecondselapsed / (float) totalsecondsinsession);}
            } else if (playerState == PlayerWidget.PlayerState.TRANSITIONING) {
                CutProgress.setProgress(1.0);
                if (currentcut.number != 10) {StatusBar.setText("Prepare For " + cutsinsession.get(currentcut.number + 1).name);}
                CutCurrentTime.setText(currentcut.gettotaltimeformatted());
                CutTotalTime.setText(currentcut.gettotaltimeformatted());
            } else if (playerState == PlayerWidget.PlayerState.PAUSED) {
                StatusBar.setText("Session Paused");
            } else if (playerState == PlayerWidget.PlayerState.STOPPED) {
                StatusBar.setText("Session Stopped");
            }
        } catch (Exception e) {e.printStackTrace();}
    }
    public void playthiscut() {
        try {
            if (isReferencedisplayoption() != null) {displayreferencefile();}
            Duration cutduration = currentcut.getDuration();
            currentcut.start();
            Timeline timeline = new Timeline(new KeyFrame(cutduration, ae -> progresstonextcut()));
            timeline.play();
            setPlayerState(PlayerWidget.PlayerState.PLAYING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void progresstonextcut() {
        try {
            if (playerState == PlayerWidget.PlayerState.TRANSITIONING) {
//                System.out.println(TimeUtils.getformattedtime() + "> Clause 1");
                try {
                    cutcount++;
                    currentcut = cutsinsession.get(cutcount);
                    playthiscut();
                } catch (ArrayIndexOutOfBoundsException e) {e.printStackTrace(); endofsession();}
            } else if (playerState == PlayerWidget.PlayerState.PLAYING) {transition();}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void endofsession() {
        closereferencefile();
        currentcuttimeline.stop();
        setPlayerState(PlayerWidget.PlayerState.STOPPED);
        sessions.deletenonvalidsessions();
//        try {sessions.addsession(TemporarySession);}
//        catch (JAXBException ignored) {GuiUtils.showerrordialog("Error", "Cannot Save Session", "XML Error. Please Check File Permissions");}
        if (Tools.getanswerdialog("Confirmation", "Session Completed", "Export This Session For Later Use?")) {export();}
        Root.getGoalsWidget().update();
    }
    public void resetthissession() {
        currentcuttimeline = null;
        sessions.deletenonvalidsessions();
        cutcount = 0;
        totalsecondselapsed = 0;
        totalsecondsinsession = 0;
    }
    public void adjustvolume() {
        if (getPlayerState() == PlayerWidget.PlayerState.PLAYING) {
            PlayerWidget.AdjustVolume av = new PlayerWidget.AdjustVolume(currentcut);
            av.show();
            if (av.getAmbienceVolume() != null) {setSessionAmbienceVolume(av.getAmbienceVolume());}
            if (av.getEntrainmentVolume() != null) {setSessionEntrainmentVolume(av.getEntrainmentVolume());}
        } else {
            Tools.showtimedmessage(StatusBar, "Cannot Adjust Volume. No Session Playing", 5000);}
    }
    public void transition() {
        closereferencefile();
        sessions.getsession(sessions.sessionscount() - 1).updatecutduration(currentcut.number, currentcut.getdurationinminutes());
        goalsWidget.update();
        currentcut.stop();
        if (currentcut.number == 10) {setPlayerState(PlayerWidget.PlayerState.TRANSITIONING); progresstonextcut();}
        else {
            Media alertmedia = new Media(This_Session.alertfile.toURI().toString());
            MediaPlayer alertplayer = new MediaPlayer(alertmedia);
            alertplayer.play();
            setPlayerState(PlayerWidget.PlayerState.TRANSITIONING);
            alertplayer.setOnEndOfMedia(() -> {
                alertplayer.stop();
                alertplayer.dispose();
                progresstonextcut();
            });
            alertplayer.setOnError(() -> {
                if (Tools.getanswerdialog("Confirmation", "An Error Occured While Playing Alert File" +
                        alertplayer.getMedia().getSource() + "'", "Retry Playing Alert File? (Pressing Cancel " +
                        "Will Progress To The Next Cut)")) {
                    alertplayer.stop();
                    alertplayer.play();
                } else {
                    alertplayer.stop();
                    alertplayer.dispose();
                    progresstonextcut();
                }
            });
        }
    }
    public void error_endplayback() {

    }

// Reference Files
    public boolean choosereferencetype() {
        PlayerWidget.ReferenceTypeDialog reftype = new PlayerWidget.ReferenceTypeDialog(referenceType, referencefullscreenoption);
        reftype.showAndWait();
        setReferenceType(reftype.getReferenceType());
        setReferencefullscreenoption(reftype.getFullscreen());
        setReferencedisplayoption(reftype.getEnabled());
        return reftype.getEnabled();
    }
    public void togglereferencedisplay(CheckBox ReferenceFileCheckbox) {
        if (ReferenceFileCheckbox.isSelected()) {
            boolean value = choosereferencetype();
            ReferenceFileCheckbox.setSelected(value);
            if (value && playerState == PlayerWidget.PlayerState.PLAYING) {displayreferencefile();}
            if (value) {ReferenceFileCheckbox.setText("Reference Display Enabled");}
            else {ReferenceFileCheckbox.setText("Reference Display Disabled");}
        }
    }
    public void displayreferencefile() {
        displayReference = new PlayerWidget.DisplayReference(currentcut, referenceType, referencefullscreenoption);
        displayReference.show();
    }
    public void closereferencefile() {
        if (displayReference != null) {
            displayReference.close();
            displayReference = null;
        }
    }
}
