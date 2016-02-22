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
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.dialogs.SimpleTextDialogWithCancelButton;
import kujiin.widgets.CreatorAndExporterWidget;
import kujiin.widgets.PlayerWidget;
import kujiin.xml.Options;
import kujiin.xml.Session;
import kujiin.xml.Sessions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

// TODO Double Check That Reference Files Switch On And Off And Work With The Options Being In XML
// TODO Work On Completed Goals

public class This_Session {
    private Cut presession = new Cut(0, "Presession", 0, this);
    private Cut rin = new Cut(1, "RIN", 0, this);
    private Cut kyo = new Cut(2, "KYO", 0, this);
    private Cut toh = new Cut(3, "TOH", 0, this);
    private Cut sha = new Cut(4, "SHA", 0, this);
    private Cut jin = new Cut(6, "JIN", 0, this);
    private Cut kai = new Cut(5, "KAI", 0, this);
    private Cut retsu = new Cut(7, "RETSU", 0, this);
    private Cut zai = new Cut(8, "ZAI", 0, this);
    private Cut zen = new Cut(9, "ZEN", 0, this);
    private Cut postsession = new Cut(10, "Postsession", 0, this);
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
    private File exportfile;
    public MainController Root;

    public This_Session(MainController mainController) {
        Root = mainController;
        this.sessions = Root.getProgressTracker().getSessions();
        CutCurrentTime = Root.CutProgressLabelCurrent;
        CutTotalTime = Root.CutProgressLabelTotal;
        SessionCurrentTime = Root.TotalProgressLabelCurrent;
        SessionTotalTime = Root.TotalProgressLabelTotal;
        CutProgress = Root.CutProgressBar;
        TotalProgress = Root.TotalProgressBar;
        CutPlayingText = Root.CutProgressTopLabel;
        SessionPlayingText = Root.TotalSessionLabel;
        StatusBar = Root.PlayerStatusBar;
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
    public void setPlayerState(PlayerWidget.PlayerState playerState) {this.playerState = playerState;}
    public PlayerWidget.PlayerState getPlayerState() {return playerState;}
    public ArrayList<Cut> getallCuts() {return new ArrayList<>(Arrays.asList(presession, rin, kyo, toh, sha, kai, jin, retsu, zai, zen, postsession));}
    public boolean isValid() {
        int totaltime = 0;
        for (Cut i : getallCuts()) {
            if (i.number != 0 && i.number != 10) {totaltime += i.duration;}
        }
        return totaltime > 0;
    }
    public File getExportfile() {
        return exportfile;
    }
    public void setExportfile(File exportfile) {
        this.exportfile = exportfile;
    }

// Creation Methods
    public boolean checktextfieldvalues(ArrayList<Integer> textfieldvalues) {
        for (int i = 0; i < textfieldvalues.size(); i++) {
            if (i != 0 && i != 10 && textfieldvalues.get(i) > 0) {
                return true;}
        }
        return false;
    }
    public void checkambience(ArrayList<Integer> textfieldvalues, CheckBox ambiencecheckbox) {
        if (checktextfieldvalues(textfieldvalues)) {
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
                                Cut thiscut = tempcuts[cutcount];
                                if (i != 0 || thiscut.number == 0 || thiscut.number == 10) {
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
                cad[0] = new SimpleTextDialogWithCancelButton(Root, "Checking Ambience", "Checking Ambience", "");
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
                        Tools.showerrordialog(Root, "Error", String.format("%s Have No Ambience At All", a.toString()), "Cannot Add Ambience");
                        if (Tools.getanswerdialog(Root, "Add Ambience", a.toString() + " Needs Ambience", "Open The Ambience Editor?")) {
                            MainController.SessionAmbienceEditor ambienceEditor = new MainController.SessionAmbienceEditor(Root);
                            ambienceEditor.showAndWait();
                        }
                    } else {
                        Tools.showerrordialog(Root, "Error", String.format("%s Have No Ambience At All", a.toString()), "Cannot Add Ambience");
                        if (Tools.getanswerdialog(Root, "Add Ambience", a.toString() + " Need Ambience", "Open The Ambience Editor?")) {
                            MainController.SessionAmbienceEditor ambienceEditor = new MainController.SessionAmbienceEditor(Root, cutswithnoambience.get(0).name);
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
                            String formattedexpectedduration = Tools.minutestoformattedhoursandmins(textfieldvalues.get(Options.ALLNAMES.indexOf(cutswithreducedambience.get(i).name)));
                            a.append(count).append(". ").append(thiscut.name).append(" >  Current: ").append(formattedcurrentduration).append(" | Needed: ").append(formattedexpectedduration);
                            count++;
                        }
                        if (cutswithreducedambience.size() == 1) {
                            ambiencecheckbox.setSelected(Tools.getanswerdialog(Root, "Confirmation", String.format("The Following Cut's Ambience Isn't Long Enough: %s ", a.toString()), "Shuffle And Loop Ambience For This Cut?"));
                        } else {
                            ambiencecheckbox.setSelected(Tools.getanswerdialog(Root, "Confirmation", String.format("The Following Cuts' Ambience Aren't Long Enough: %s ", a.toString()), "Shuffle And Loop Ambience For These Cuts?"));
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
                Tools.showerrordialog(Root, "Error", "Couldn't Check Ambience", "Check Ambience Folder Read Permissions");
                ambiencecheckbox.setSelected(false);
            });
            ambiencecheckerservice.start();
        } else {Tools.showinformationdialog(Root, "Information", "Cannot Check Ambience", "No Cuts Have > 0 Values, So I Don't Know Which Ambience To Check");}
    }
    public boolean checksessionwellformedness(ArrayList<Integer> textfieldtimes) {
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
            for (Integer x : indexestochange) {cutsmissinglist.add(Options.ALLNAMES.get(x));}
            StringBuilder cutsmissingtext = new StringBuilder();
            for (int i = 0; i < cutsmissinglist.size(); i++) {
                cutsmissingtext.append(cutsmissinglist.get(i));
                if (i != cutsmissinglist.size() - 1) {cutsmissingtext.append(", ");}
            }
            CreatorAndExporterWidget.SessionNotWellformedDialog notWellformedDialog = new CreatorAndExporterWidget.SessionNotWellformedDialog(Root, textfieldtimes, cutsmissingtext.toString(), lastcutindex);
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
        ArrayList<Cut> cutlist = new ArrayList<>();
        cutlist.add(presession);
        if (rin.duration != 0) {cutlist.add(rin);}
        if (kyo.duration != 0) {cutlist.add(kyo);}
        if (toh.duration != 0) {cutlist.add(toh);}
        if (sha.duration != 0) {cutlist.add(sha);}
        if (kai.duration != 0) {cutlist.add(kai);}
        if (jin.duration != 0) {cutlist.add(jin);}
        if (retsu.duration != 0) {cutlist.add(retsu);}
        if (zai.duration != 0) {cutlist.add(zai);}
        if (zen.duration != 0) {cutlist.add(zen);}
        cutlist.add(postsession);
        setCutsinsession(cutlist);
        return getCutsinsession().size() > 0;
    }
    public void create(ArrayList<Integer> textfieldtimes) {
        if (checksessionwellformedness(textfieldtimes)) {
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
                sdcb[0] = new SimpleTextDialogWithCancelButton(Root, "Creating Session", "Creating Session", "");
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
                Platform.runLater(() -> Tools.showerrordialog(Root, "Error", "Session Creation Failed", "Check Sound File Read Permissions"));
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
    public Service<Boolean> getsessionexporter() {
//        CreatorAndExporterWidget.ExportingSessionDialog exportingSessionDialog = new CreatorAndExporterWidget.ExportingSessionDialog(this);
        return new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        updateTitle("Finalizing Session");
                        int taskcount = cutsinsession.size() + 2;
                        // TODO Mix Entrainment And Ambience
                        for (Cut i : cutsinsession) {
                            updateMessage("Combining Entrainment And Ambience For " + i.name);
                            if (! i.mixentrainmentandambience()) {cancel();}
                            if (isCancelled()) {return false;}
                            updateProgress((double) (cutsinsession.indexOf(i) / taskcount), 1.0);
                            updateMessage("Finished Combining " + i.name);
                        }
                        updateMessage("Creating Final Session File (May Take A While)");
                        export();
                        if (isCancelled()) {return false;}
                        updateProgress(taskcount - 1, 1.0);
                        updateMessage("Double-Checking Final Session File");
                        boolean success = testexportfile();
                        if (isCancelled()) {return false;}
                        updateProgress(1.0, 1.0);
                        return success;
                    }
                };
            }
        };
//        exportingSessionDialog.creatingsessionProgressBar.progressProperty().bind(exporterservice.progressProperty());
//        exportingSessionDialog.creatingsessionTextStatusBar.textProperty().bind(exporterservice.messageProperty());
//        exportingSessionDialog.CancelButton.setOnAction(event -> exporterservice.cancel());
//        exporterservice.setOnSucceeded(event -> {
//            if (exporterservice.getValue()) {Tools.showinformationdialog("Information", "Export Succeeded", "File Saved To: ");}
//            else {Tools.showerrordialog("Error", "Errors Occured During Export", "Please Try Again Or Contact Me For Support");}
//            exportingSessionDialog.close();
//        });
//        exporterservice.setOnFailed(event -> {
//            String v = exporterservice.getException().getMessage();
//            Tools.showerrordialog("Error", "Errors Occured While Trying To Create The This_Session. The Main Exception I Encoured Was " + v,
//                    "Please Try Again Or Contact Me For Support");
//            This_Session.deleteprevioussession();
//            exportingSessionDialog.close();
//        });
//        exporterservice.setOnCancelled(event -> {
//            Tools.showinformationdialog("Cancelled", "Export Cancelled", "You Cancelled Export");
//            This_Session.deleteprevioussession();
//            exportingSessionDialog.close();
//        });
//        return false;
    }
    public void getnewexportsavefile() {
        File tempfile = Tools.savefilechooser(Root.getScene(), "Save Export File As", null);
        if (tempfile != null && Tools.validaudiofile(tempfile)) {
            setExportfile(tempfile);
        } else {
            if (tempfile == null) {return;}
            if (Tools.getanswerdialog(Root, "Confirmation", "Invalid Audio File Extension", "Save As .mp3?")) {
                String file = tempfile.getAbsolutePath();
                int index = file.lastIndexOf(".");
                String firstpart = file.substring(0, index - 1);
                setExportfile(new File(firstpart.concat(".mp3")));
            }
        }
    }
    public boolean export() {
        ArrayList<File> filestoexport = new ArrayList<>();
        for (int i=0; i < cutsinsession.size(); i++) {
            filestoexport.add(cutsinsession.get(i).getFinalcutexportfile());
            if (i != cutsinsession.size() - 1) {
                filestoexport.add(new File(Root.getOptions().getSessionOptions().getAlertfilelocation()));
            }
        }
        if (filestoexport.size() == 0) {return false;}
        else {return Tools.concatenateaudiofiles(filestoexport, new File(Options.DIRECTORYTEMP, "Session.txt"), getExportfile());}
    }
    public boolean testexportfile() {
        try {
            MediaPlayer test = new MediaPlayer(new Media(getExportfile().toURI().toString()));
            test.setOnReady(test::dispose);
            return true;
        } catch (MediaException ignored) {return false;}
    }
    public static void deleteprevioussession() {
        ArrayList<File> folders = new ArrayList<>();
        folders.add(new File(Options.DIRECTORYTEMP, "Ambience"));
        folders.add(new File(Options.DIRECTORYTEMP, "Entrainment"));
        folders.add(new File(Options.DIRECTORYTEMP, "txt"));
        folders.add(new File(Options.DIRECTORYTEMP, "Export"));
        for (File i : folders) {
            try {
                for (File x : i.listFiles()) {x.delete();}
            } catch (NullPointerException ignored) {}
        }
        try {
            for (File x : Options.DIRECTORYTEMP.listFiles()) {
                if (! x.isDirectory()) {x.delete();}
            }
        } catch (NullPointerException ignored) {}
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
            currentcut.stop();
            currentcuttimeline.stop();
            setPlayerState(PlayerWidget.PlayerState.STOPPED);
            resetthissession();
            return "Session Stopped";
        } else if (playerState == PlayerWidget.PlayerState.PAUSED) {
            currentcut.stop();
            currentcuttimeline.stop();
            setPlayerState(PlayerWidget.PlayerState.STOPPED);
            resetthissession();
            return "Session Stopped";
        } else if (playerState == PlayerWidget.PlayerState.IDLE) {
            return "No Session Playing, Cannot Stop";
        } else if (playerState == PlayerWidget.PlayerState.TRANSITIONING) {
            return "Currently Transitioning To The Next Cut. Please Wait Till At The Next Cut To Stop";
        } else {
            return "";
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
                Root.getProgressTracker().updategoalsui();
                Root.getProgressTracker().updateprogressui();
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
        } catch (Exception e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
    }
    public void playthiscut() {
        try {
            if (Root.getOptions().getSessionOptions().getReferenceoption()) {displayreferencefile();}
            Duration cutduration = currentcut.getDuration();
            currentcut.start();
            Timeline timeline = new Timeline(new KeyFrame(cutduration, ae -> progresstonextcut()));
            timeline.play();
            setPlayerState(PlayerWidget.PlayerState.PLAYING);
        } catch (Exception e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
    }
    public void progresstonextcut() {
        try {
            if (playerState == PlayerWidget.PlayerState.TRANSITIONING) {
//                System.out.println(TimeUtils.getformattedtime() + "> Clause 1");
                try {
                    cutcount++;
                    currentcut = cutsinsession.get(cutcount);
                    playthiscut();
                } catch (ArrayIndexOutOfBoundsException ignored) {endofsession();}
            } else if (playerState == PlayerWidget.PlayerState.PLAYING) {transition();}
        } catch (Exception e) {new MainController.ExceptionDialog(Root, e).show();}
    }
    public void endofsession() {
        closereferencefile();
        currentcuttimeline.stop();
        setPlayerState(PlayerWidget.PlayerState.STOPPED);
        sessions.deletenonvalidsessions();
        new PlayerWidget.SessionFinishedDialog(Root).showAndWait();
//        if (Tools.getanswerdialog(Root, "Confirmation", "Session Completed", "Export This Session For Later Use?")) {
//            getsessionexporter();}
        Root.getProgressTracker().updategoalsui();
    }
    public void resetthissession() {
        currentcuttimeline = null;
        sessions.deletenonvalidsessions();
        cutcount = 0;
        totalsecondselapsed = 0;
        totalsecondsinsession = 0;
    }
    public void transition() {
        closereferencefile();
        Session currentsession = sessions.getsession(sessions.totalsessioncount() - 1);
        currentsession.updatecutduration(currentcut.number, currentcut.getdurationinminutes());
        sessions.marshall();
        // TODO Right Here Sessions Isn't Marshalling Properly. Not Saving Sessions
        Root.getProgressTracker().updategoalsui();
        currentcut.stop();
        if (currentcut.number == 10) {setPlayerState(PlayerWidget.PlayerState.TRANSITIONING); progresstonextcut();}
        else {
            if (Root.getOptions().getSessionOptions().getAlertfunction()) {
                Media alertmedia = new Media(Root.getOptions().getSessionOptions().getAlertfilelocation());
                MediaPlayer alertplayer = new MediaPlayer(alertmedia);
                alertplayer.play();
                setPlayerState(PlayerWidget.PlayerState.TRANSITIONING);
                alertplayer.setOnEndOfMedia(() -> {
                    alertplayer.stop();
                    alertplayer.dispose();
                    progresstonextcut();
                });
                alertplayer.setOnError(() -> {
                    if (Tools.getanswerdialog(Root, "Confirmation", "An Error Occured While Playing Alert File" +
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
            } else {
                setPlayerState(PlayerWidget.PlayerState.TRANSITIONING);
                progresstonextcut();
            }
        }
    }
    public void error_endplayback() {

    }

// Reference Files
    public boolean choosereferencetype() {
        if (! Root.getOptions().getSessionOptions().getReferenceoption()) {
            PlayerWidget.ReferenceTypeDialog reftype = new PlayerWidget.ReferenceTypeDialog(Root);
            reftype.showAndWait();
            Root.getOptions().getSessionOptions().setReferencetype(reftype.getReferenceType());
            Root.getOptions().getSessionOptions().setReferencefullscreen(reftype.getFullscreen());
            Root.getOptions().getSessionOptions().setReferenceoption(reftype.getEnabled());
            return reftype.getEnabled();
        } else {return true;}
    }
    public void togglereferencedisplay(CheckBox ReferenceFileCheckbox) {
        if (ReferenceFileCheckbox.isSelected()) {
            boolean value = choosereferencetype();
            ReferenceFileCheckbox.setSelected(value);
            if (value && playerState == PlayerWidget.PlayerState.PLAYING) {displayreferencefile();}
            if (value) {ReferenceFileCheckbox.setText("Display Reference");}
            else {ReferenceFileCheckbox.setText("Display Reference");}
        } else {
            Root.getOptions().getSessionOptions().setReferenceoption(false);
            Root.getOptions().getSessionOptions().setReferencetype(null);
            closereferencefile();
        }
    }
    public void displayreferencefile() {
        displayReference = new PlayerWidget.DisplayReference(Root, currentcut);
        displayReference.show();
    }
    public void closereferencefile() {
        if (displayReference != null) {
            displayReference.close();
            displayReference = null;
        }
    }

}