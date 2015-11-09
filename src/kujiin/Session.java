package kujiin;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import kujiin.dialogs.CreatingSessionDialog;
import kujiin.dialogs.SessionNotWellformedDialog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class Session {
    public static final File projectroot = new File(System.getProperty("user.dir"));
    public static final File rootdirectory = new File(Session.projectroot, "src/kujiin/");
    public static final File sounddirectory = new File(Session.rootdirectory, "assets/sound/");
    public static final File directoryentrainment = new File(Session.sounddirectory, "entrainment/");
    public static final File directoryambience = new File(Session.sounddirectory, "ambience/");
    public static final File directorytemp = new File(Session.sounddirectory, "temp/");
    public static final File directorymaincuts = new File(Session.directoryentrainment, "maincuts/");
    public static final File directorytohramp = new File(Session.directoryentrainment, "tohramp/");
    public static final File directoryrampdown = new File(Session.directoryentrainment, "ramp/down/");
    public static final File directoryrampup = new File(Session.directoryentrainment, "ramp/up/");
    public static final File alertfile = new File(Session.sounddirectory, "Alert.mp3");
    public static final File logfile = new File(Session.rootdirectory, "assets/sessionlog.txt");
    public static final ArrayList<String> allnames = new ArrayList<>(Arrays.asList(
            "Presession", "RIN", "KYO", "TOH", "SHA", "KAI", "JIN", "RETSU", "ZAI", "ZEN", "Postsession"));
    public static final File sessiondatabase = new File(rootdirectory, "assets/database/sessiondatabase.db");
    Cut presession = new Cut(0, "Presession", true, 0, this);
    Cut rin = new Cut(1, "RIN", false, 0, this);
    Cut kyo = new Cut(2, "KYO", false, 0, this);
    Cut toh = new Cut(3, "TOH", false, 0, this);
    Cut sha = new Cut(4, "SHA", false, 0, this);
    Cut jin = new Cut(6, "JIN", false, 0, this);
    Cut kai = new Cut(5, "KAI", false, 0, this);
    Cut retsu = new Cut(7, "RETSU", false, 0, this);
    Cut zai = new Cut(8, "ZAI", false, 0, this);
    Cut zen = new Cut(9, "ZEN", false, 0, this);
    Cut postsession = new Cut(10, "Postsession", true, 0, this);
    double totalduration;
    ArrayList<Cut> cutsinsession;
    Exporter exporter;
    Player player;
    private Boolean ambienceenabled;
    // Creation Variables
    private Boolean created;
    public CreateANewSession createANewSession;
    private CreatingSessionDialog creatingSessionDialog;
    Root root;
    Database sessiondb;

    public Session(Root root) {
        sessiondb = new Database(root);
        sessiondb.getdetailedprogress();
        sessiondb.createtables();
//        sessiondb.createnewsession();
        clearlogfile();
        this.root = root;
        cutsinsession = new ArrayList<>();
        created = false;
        ambienceenabled = false;
    }

    public String gettotalsessionduration() {
        Integer totaltime = 0;
        for (Cut i : cutsinsession) {
            totaltime += i.getdurationinminutes();
        }
        return Tools.minutestoformattedhoursandmins(totaltime);
    }

    public ObservableList<String> getsessiondetails() {
        // TODO Session Details Go In Here
        return FXCollections.observableArrayList();
    }

    // <-------------------------------   GETTERS & SETTTERS  ----------------------> //

    // Check If Session Is Created
    public Boolean getCreated() {
        ArrayList<File> sessionpartsmissing = new ArrayList<>();
        ArrayList<String> variations = new ArrayList<>();
        variations.add("Entrainment");
        if (ambienceenabled) {
            variations.add("Ambience");
        }
        if (cutsinsession.size() != 0) {
            for (Cut i : cutsinsession) {
                for (String foldername : variations) {
                    File folderdirectory = new File(Session.directorytemp, foldername);
                    File actualfile = new File(folderdirectory, i.name + ".mp3");
                    if (!actualfile.exists()) {
                        sessionpartsmissing.add(actualfile);
                    }
                }
            }
        } else {
            for (String i : Session.allnames) {
                variations.add("Ambience");
                for (String foldername : variations) {
                    File folderdirectory = new File(Session.directorytemp, foldername);
                    File actualfile = new File(folderdirectory, i + ".mp3");
                    if (!actualfile.exists()) {
                        sessionpartsmissing.add(actualfile);
                    }
                }
            }
        }
        return sessionpartsmissing.size() == 0;
    }

    // Setter For ambienceenabled
    public void setAmbienceenabled(Boolean ambienceenabled) {
        this.ambienceenabled = ambienceenabled;
    }

    public boolean getAmbienceenabled() {return ambienceenabled;}

    // Getter For cutsinsession
    public ArrayList<Cut> getCutsinsession() {return cutsinsession;}


    // <-------------------------------   CREATION  -------------------------------> //

    // Static Method To Delete Previous Session From Temp Files
    public static void deleteprevioussession() {
        ArrayList<File> folders = new ArrayList<>();
        folders.add(new File(Session.directorytemp, "Ambience"));
        folders.add(new File(Session.directorytemp, "Entrainment"));
        folders.add(new File(Session.directorytemp, "txt"));
        folders.add(new File(Session.directorytemp, "Export"));
        for (File i : folders) {
            try {
                for (File x : i.listFiles()) {x.delete();}
            } catch (NullPointerException ignored) {}
        }
    }

    // Check If There Are Any Cut Values (Rin - Zen) That Aren't Zero
    public boolean textfieldvaluesareOK(ArrayList<Integer> textfieldvalues) {
        for (int i = 0; i < textfieldvalues.size(); i++) {
            if (i != 0 && i != 10 && textfieldvalues.get(i) > 0) {
                return true;}
        }
        return false;
    }

    // Check If Any Ambience Exists And If The Ambience Is Long Enough For The Textfieldvalues
    public boolean checkifambienceisgood(ArrayList<Integer> textfieldvalues, CreateANewSession createsessionDialog) {
        if (textfieldvaluesareOK(textfieldvalues)) {
            ArrayList<Cut> cutswithnoambience = new ArrayList<>();
            ArrayList<Cut> cutswithreducedambience = new ArrayList<>();
            Cut[] tempcuts = {presession, rin, kyo, toh, sha, kai, jin, retsu, zai, zen, postsession};
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    int cutcount = 0;
                    for (Integer i : textfieldvalues) {
                        if (i != 0) {
                            Cut thiscut = tempcuts[cutcount];
                            updateMessage(String.format("Please Wait! Currently Checking %s's Ambience...", thiscut.name));
                            if (thiscut.hasanyAmbience()) {
                                if (!thiscut.hasenoughAmbience()) {
                                    cutswithreducedambience.add(thiscut);
                                }
                            } else {
                                cutswithnoambience.add(thiscut);
                            }
                        }
                        cutcount++;
                    }
                    if (cutswithnoambience.size() > 0) {
                        StringBuilder a = new StringBuilder();
                        for (int i = 0; i < cutswithnoambience.size(); i++) {
                            a.append(cutswithnoambience.get(i).name);
                            if (i != cutswithnoambience.size() - 1) {
                                a.append(", ");
                            }
                        }
                        Alert b = new Alert(Alert.AlertType.ERROR);
                        b.setTitle("Error");
                        b.setHeaderText("Cannot Add Ambience");
                        if (cutswithnoambience.size() > 1) {
                            b.setContentText(String.format("%s Have No Ambience At All", a.toString()));
                        } else {
                            b.setContentText(String.format("%s Has No Ambience At All", a.toString()));
                        }
                        b.setResizable(true);
                        b.showAndWait();
                        Alert c = new Alert(Alert.AlertType.INFORMATION);
                        c.setHeaderText("Please Add Ambience To The Session");
                        c.setContentText("To Do This, Please Click Tools -> Add Ambience");
                        c.showAndWait();
                        ambienceenabled = false;
                    } else {
                        if (cutswithreducedambience.size() > 0) {
                            StringBuilder a = new StringBuilder();
                            for (int i = 0; i < cutswithreducedambience.size(); i++) {
                                a.append("\n");
                                Cut thiscut = cutswithreducedambience.get(i);
                                String formattedcurrentduration = Tools.minutestoformattedhoursandmins((int) thiscut.getTotalambienceduration());
                                String formattedexpectedduration = Tools.minutestoformattedhoursandmins(cutswithreducedambience.get(i).getdurationinminutes());
                                a.append(thiscut.name).append("(Found: ").append(formattedcurrentduration).append(" | Expected: ").append(formattedexpectedduration).append(")");
                            }
                            Alert b = new Alert(Alert.AlertType.CONFIRMATION);
                            b.setResizable(true);
                            b.setTitle("Error");
                            if (cutswithreducedambience.size() == 1) {
                                b.setHeaderText(String.format("The Following Cut's Ambience Isn't Long Enough: %s ", a.toString()));
                            } else {
                                b.setHeaderText(String.format("The Following Cuts' Ambience Aren't Long Enough: %s ", a.toString()));
                            }
                            b.setContentText("Shuffle And Loop Ambience For These Cuts?");
                            Optional<ButtonType> c = b.showAndWait();
                            ambienceenabled = c.isPresent() && c.get() == ButtonType.OK;
                        } else {
                            ambienceenabled = true;
                        }
                    }
                    updateMessage("Done Checking Ambience. You Can Now Create This Session...");
                    return null;
                }
            };
            createsessionDialog.sessioncreatorstatusbar.textProperty().bind(task.messageProperty());
            task.setOnSucceeded(event -> createsessionDialog.sessioncreatorstatusbar.textProperty().unbind());
            task.setOnFailed(event -> createsessionDialog.sessioncreatorstatusbar.textProperty().unbind());
            new Thread(task).start();
        } else {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Ambience Validation");
            a.setHeaderText("Cannot Check Ambience");
            a.setContentText("No Cuts Have > 0 Values, So I Don't Know Which Ambience To Check");
            a.showAndWait();
            ambienceenabled = false;
        }
        return ambienceenabled;
    }

    // Check If Session Is Well-Formed (Sequential Cuts With Proper Connects)
    public boolean sessioncreationwellformednesschecks(ArrayList<Integer> textfieldtimes) {
        boolean createsession = true;
        int lastcutindex = 0;
        for (int i = 0; i < textfieldtimes.size(); i++) {
            if (textfieldtimes.get(i) > 0) {lastcutindex = i;}
        }
        // Get NonSequential Cuts
        ArrayList<Integer> indexestochange = new ArrayList<>();
        for (int i = 0; i < lastcutindex; i++) {
            if (i > 0) {
                if (textfieldtimes.get(i) == 0) {
                    indexestochange.add(i);
                }
            }
        }
        if (indexestochange.size() > 0) {
            ArrayList<String> cutsmissinglist = new ArrayList<>();
            for (Integer x : indexestochange) {cutsmissinglist.add(Session.allnames.get(x));}
            StringBuilder cutsmissingtext = new StringBuilder();
            for (int i = 0; i < cutsmissinglist.size(); i++) {
                cutsmissingtext.append(cutsmissinglist.get(i));
                if (i != cutsmissinglist.size() - 1) {cutsmissingtext.append(", ");}
            }
            SessionNotWellformedDialog notWellformedDialog = new SessionNotWellformedDialog(null, textfieldtimes, cutsmissingtext.toString(), lastcutindex);
            notWellformedDialog.showAndWait();
            // TODO CONTINUE HERE
            if (notWellformedDialog.isCreatesession()) {
                int invocationduration = notWellformedDialog.getInvocationduration();
                for (int i : indexestochange) {textfieldtimes.set(i, invocationduration);}
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    // Pass Durations From TextFieldValues Into Cuts For Creation
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
        if (rin.duration != 0) {
            cutsinsession.add(rin);
        }
        if (kyo.duration != 0) {
            cutsinsession.add(kyo);
        }
        if (toh.duration != 0) {
            cutsinsession.add(toh);
        }
        if (sha.duration != 0) {
            cutsinsession.add(sha);
        }
        if (kai.duration != 0) {
            cutsinsession.add(kai);
        }
        if (jin.duration != 0) {
            cutsinsession.add(jin);
        }
        if (retsu.duration != 0) {
            cutsinsession.add(retsu);
        }
        if (zai.duration != 0) {
            cutsinsession.add(zai);
        }
        if (zen.duration != 0) {
            cutsinsession.add(zen);
        }
        cutsinsession.add(postsession);
        return cutsinsession.size() > 0;
    }

    // Create The Session
    public void create(ArrayList<Integer> textfieldtimes, CreateANewSession createANewSession) {
        Session.deleteprevioussession();
        if (sessioncreationwellformednesschecks(textfieldtimes)) {
            setupcutsinsession(textfieldtimes);
            int sessionparts = 0;
            if (ambienceenabled) {sessionparts += (cutsinsession.size() * 2);}
            sessionparts += cutsinsession.size();
            if (createANewSession != null) {this.createANewSession = createANewSession;}
            creatingSessionDialog = new CreatingSessionDialog(null, this);
            creatingSessionDialog.setSessionparts(sessionparts);
            creatingSessionDialog.show();
            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    for (Cut i : cutsinsession) {
                        if (isCancelled()) {return false;}
                        updateMessage("Currently Creating " + i.name);
                        updateProgress((double) cutsinsession.indexOf(i), (double) cutsinsession.size() - 1);
                        boolean cutcreatedsuccesfully = i.create(ambienceenabled, cutsinsession, creatingSessionDialog);
                        if (! cutcreatedsuccesfully) {return false;}
                        updateMessage("Finished Creating " + i.name);
                    }
                    return getCreated();
                }
            };
            creatingSessionDialog.creatingsessionProgressBar.progressProperty().bind(task.progressProperty());
            creatingSessionDialog.creatingsessionTextStatusBar.textProperty().bind(task.messageProperty());
            creatingSessionDialog.CancelButton.setOnAction(event -> task.cancel());
            task.setOnSucceeded(event -> {
                if (task.getValue()) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Created Succeeded");
                    a.setHeaderText("Creation Completed With No Errors");
                    a.setContentText("You Can Now Play Or Export This Session");
                    a.showAndWait();
                    creatingSessionDialog.close();
                    if (createANewSession != null) {
                        createANewSession.close();
                    }
                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Creation Failed");
//                    String v = task.getException().getMessage();
                    a.setHeaderText("Errors Occured While Trying To Create The Session. Please Try Again Or Contact Me For Support ");
                    a.setContentText("Please Try Again Or Contact Me For Support");
                    a.showAndWait();
                    Session.deleteprevioussession();
                    creatingSessionDialog.close();
                }
            });
            task.setOnFailed(event -> {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Creation Failed");
                String v = task.getException().getMessage();
                a.setHeaderText("Errors Occured While Trying To Create The Session. The Main Exception I Encoured Was " + v);
                a.setContentText("Please Try Again Or Contact Me For Support");
                a.showAndWait();
                Session.deleteprevioussession();
                creatingSessionDialog.close();
            });
            task.setOnCancelled(event -> {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Creation Cancelled");
                a.setHeaderText("You Cancelled The Session Creation");
                a.setContentText("Re-Create To Play Or Export");
                a.showAndWait();
                Session.deleteprevioussession();
                creatingSessionDialog.close();
            });
            new Thread(task).start();
        }
    }

    // <------------------------------- PLAYBACK --------------------------------> //

    // Plays The Session
    public void play() {
        if (getCreated()) {
            if (player == null) {player = new Player(cutsinsession, ambienceenabled, root, sessiondb);}
            player.playbuttonpressed();
        }
    }

    // Pauses The Session
    public void pause() {
        player.pause();
        // Pauses The Session
    }

    // Stops The Session
    public void stop() {
        player.stop();
    }

    // Displays The Status Of The Playing Session On The Main UI
    public void displaystatus() {

    }

    // Clean Up Session And Ask If User Wants To Export
    public void endofsession() {

    }

    // <------------------------------- EXPORT --------------------------------> //
    // Export The Session
    public void export() {
        // Exports The Session
    }

    // Check If Session Export Succeeded
    public void getexported() {}

    // <------------------------------- LOG FILES ------------------------------> //

    private static void clearlogfile() {
        try {
            FileWriter clearlog = new FileWriter(logfile);
            clearlog.close();
        } catch (IOException ignored) {}
    }

}
