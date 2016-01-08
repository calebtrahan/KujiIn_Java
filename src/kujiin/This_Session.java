package kujiin;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import kujiin.widgets.CreatorAndExporterWidget;
import kujiin.widgets.GoalsWidget;
import kujiin.widgets.PlayerWidget;
import kujiin.xml.Session;
import kujiin.xml.Sessions;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    public This_Session(Sessions sessions, Label cutCurrentTime, Label cutTotalTime, Label sessionCurrentTime,
                        Label sessionTotalTime, ProgressBar cutProgress, ProgressBar totalProgress, Label cutPlayingText,
                        Label sessionPlayingText, Label statusBar) {
        this.sessions = sessions;
        CutCurrentTime = cutCurrentTime;
        CutTotalTime = cutTotalTime;
        SessionCurrentTime = sessionCurrentTime;
        SessionTotalTime = sessionTotalTime;
        CutProgress = cutProgress;
        TotalProgress = totalProgress;
        CutPlayingText = cutPlayingText;
        SessionPlayingText = sessionPlayingText;
        StatusBar = statusBar;
        clearlogfile();
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
                                    if (thiscut.getambiencefiles()) {
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
            final MainController.SimpleTextDialogWithCancelButton[] cad = new MainController.SimpleTextDialogWithCancelButton[1];
            ambiencecheckerservice.setOnRunning(event -> {
                cad[0] = new MainController.SimpleTextDialogWithCancelButton("Checking Ambience", "");
                cad[0].Message.textProperty().bind(ambiencecheckerservice.messageProperty());
                cad[0].CancelButton.setOnAction(ev -> ambiencecheckerservice.cancel());
                cad[0].showAndWait();
            });
            ambiencecheckerservice.setOnSucceeded(event -> {
                cad[0].close();
                Platform.runLater(() -> {
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
    public boolean create(ArrayList<Integer> textfieldtimes) {
        if (sessioncreationwellformednesschecks(textfieldtimes)) {
            setupcutsinsession(textfieldtimes);
            boolean ok = true;
            final MainController.SimpleTextDialogWithCancelButton[] sdcb = new MainController.SimpleTextDialogWithCancelButton[1];
            Service<Void> creationservice = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            for (Cut i : cutsinsession) {
                                updateMessage("Creating" + i.name);
                                if (! i.create(cutsinsession, ambienceenabled)) {cancel();}
                            }
                            return null;
                        }
                    };
                }
            };
            creationservice.setOnRunning(event -> {
                sdcb[0] = new MainController.SimpleTextDialogWithCancelButton("Creating Session", "");
                sdcb[0].Message.textProperty().bind(creationservice.messageProperty());
                sdcb[0].CancelButton.setOnAction(ev -> creationservice.cancel());
            });
            creationservice.setOnCancelled(event -> {
                // TODO Reset Session Here
                sdcb[0].close();
            });
            creationservice.setOnSucceeded(event -> {
                sdcb[0].close();
            });
            creationservice.setOnFailed(event -> {
                Platform.runLater(() -> Tools.showerrordialog("Error", "Session Creation Failed", "Check Sound File Read Permissions"));
                // TODO Reset Session Here
                sdcb[0].close();
            });
            return ok;
        } else {return false;}
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
            if (exporterservice.getValue()) {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Export Succeeded");
                a.setHeaderText("Creation Completed With No Errors");
                a.setContentText("You Can Now Play Or Export This This_Session");
                a.showAndWait();
                exportingSessionDialog.close();
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Creation Failed");
//                    String v = task.getException().getMessage();
                a.setHeaderText("Errors Occured While Trying To Create The This_Session. Please Try Again Or Contact Me For Support ");
                a.setContentText("Please Try Again Or Contact Me For Support");
                a.showAndWait();
                This_Session.deleteprevioussession();
                exportingSessionDialog.close();
            }
        });
        exporterservice.setOnFailed(event -> {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Creation Failed");
            String v = exporterservice.getException().getMessage();
            a.setHeaderText("Errors Occured While Trying To Create The This_Session. The Main Exception I Encoured Was " + v);
            a.setContentText("Please Try Again Or Contact Me For Support");
            a.showAndWait();
            This_Session.deleteprevioussession();
            exportingSessionDialog.close();
        });
        exporterservice.setOnCancelled(event -> {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Creation Cancelled");
            a.setHeaderText("You Cancelled The This_Session Creation");
            a.setContentText("Re-Create To Play Or Export");
            a.showAndWait();
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
        switch (playerState) {
            case IDLE:
                sessions.createnewsession();
                startplayback();
                return "Playing Session...";
            case PAUSED:
                currentcuttimeline.play();
                currentcut.resumeplayingcut();
                setPlayerState(PlayerWidget.PlayerState.PLAYING);
                return "Resuming Session...";
            case STOPPED:
                sessions.createnewsession();
                startplayback();
            case PLAYING:
                return "Already Playing";
            case TRANSITIONING:
                return "Transistioning To The Next Cut";
            default:
                return "";
        }
    }
    public String pause() {
        switch (playerState) {
            case PLAYING:
                currentcut.pauseplayingcut();
                currentcuttimeline.pause();
                setPlayerState(PlayerWidget.PlayerState.PAUSED);
                return "Session Paused";
            case PAUSED:
                return "Already Paused";
            case TRANSITIONING:
                return "Currently Transitioning To The Next Cut. Please Wait Till At The Next Cut To Pause";
            default:
                return "No Session Playing";
        }
    }
    public String stop() {
        switch (playerState) {
            case PLAYING:
                pause();
                if (Tools.getanswerdialog("End Prematurely", "End Session", "Really End This Session Prematurely?")) {
                    endsessionprematurely(sessions);
                    currentcut.stopplayingcut();
                    currentcuttimeline.stop();
                    setPlayerState(PlayerWidget.PlayerState.STOPPED);
                    resetthissession();
                    return "Session Stopped";
                } else {play(); return "";}
            case PAUSED:
                if (Tools.getanswerdialog("End Prematurely", "End Session", "Really End This Session Prematurely?")) {
                    endsessionprematurely(sessions);
                    currentcut.stopplayingcut();
                    currentcuttimeline.stop();
                    setPlayerState(PlayerWidget.PlayerState.STOPPED);
                    resetthissession();
                    return "Session Stopped";
                } else {play(); return "";}
            case IDLE:
                return "No Session Playing, Cannot Stop";
            case TRANSITIONING:
                return "Currently Transitioning To The Next Cut. Please Wait Till At The Next Cut To Stop";
            default:
                return "";
        }
    }
    public void endsessionprematurely(Sessions Sessions) {
        int secondsleft = currentcut.getdurationinseconds() / currentcut.getSecondselapsed();
        int secondspracticed = currentcut.getdurationinseconds() - secondsleft;
        Double minutes = Math.floor(secondspracticed / 60);
        sessions.getcurrentsession().updatecutduration(currentcut.number, minutes.intValue());
        // TODO Get Premature Ending Reason Here
        String prematureendingreason = "";
        sessions.getcurrentsession().writeprematureending(currentcut.name, prematureendingreason);
//        try {Sessions.addnewsession(TemporarySession);}
//        catch (JAXBException ignored) {GuiUtils.showerrordialog("Error", "XML Error", "Cannot Write This Practiced Session To XML File");}
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
//            System.out.println(TimeUtils.getformattedtime() + "> Clause 3");
            if (isReferencedisplayoption() != null) {displayreferencefile();}
            Duration cutduration = currentcut.getthiscutduration();
            currentcut.startplayback();
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
//        try {sessions.addnewsession(TemporarySession);}
//        catch (JAXBException ignored) {GuiUtils.showerrordialog("Error", "Cannot Save Session", "XML Error. Please Check File Permissions");}
        if (Tools.getanswerdialog("Confirmation", "Session Completed", "Export This Session For Later Use?")) {export();}
        // TODO Update Goal Widget Here
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
        sessions.getcurrentsession().updatecutduration(currentcut.number, currentcut.getdurationinminutes());
        goalsWidget.update();
        currentcut.stopplayingcut();
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

// Presets
    public void loadpreset() {
        File xmlfile = new FileChooser().showOpenDialog(null);
        if (xmlfile != null) {
            try {
                JAXBContext context = JAXBContext.newInstance(Session.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                Session loadedsession = (Session) createMarshaller.unmarshal(xmlfile);
                if (loadedsession != null) {
                    setupcutsinsession(loadedsession.getallcuttimes());
                    Tools.showinformationdialog("Information", "Preset Loaded", "Your Preset Was Successfully Loaded");
                }
            } catch (JAXBException e) {
                Tools.showerrordialog("Error", "Not A Valid Preset File", "Please Select A Valid Preset File");}
        }
    }
    public void saveaspreset(Session session) {
        File xmlfile = new FileChooser().showSaveDialog(null);
        if (xmlfile != null) {
            try {
                JAXBContext context = JAXBContext.newInstance(Session.class);
                Marshaller createMarshaller = context.createMarshaller();
                createMarshaller.marshal(session, xmlfile);
                Tools.showinformationdialog("Information", "Preset Saved", "Your Preset Was Successfully Saved");
            } catch (JAXBException e) {
                Tools.showerrordialog("Error", "Couldn't Save Preset", "Your Preset Could Not Be Saved, Do You Have Write Access To That Directory?");}
        } else {Tools.showtimedmessage(StatusBar, "Canceled Saving Preset", 2000);}
    }

// Log Session
    // TODO Create A Log File
    private static void clearlogfile() {
        try {
            FileWriter clearlog = new FileWriter(logfile);
            clearlog.close();
        } catch (IOException ignored) {}
    }
}
