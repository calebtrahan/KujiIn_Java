package kujiin.util;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.MainController;
import kujiin.xml.Options;
import kujiin.xml.Session;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class This_Session {
    private Qi_Gong Presession;
    private Cut Rin;
    private Cut Kyo;
    private Cut Toh;
    private Cut Sha;
    private Cut Jin;
    private Cut Kai;
    private Cut Retsu;
    private Cut Zai;
    private Cut Zen;
    private Qi_Gong Postsession;
    private Element Earth;
    private Element Air;
    private Element Fire;
    private Element Water;
    private Element Void;
    private Total Total;
    private SessionPart currentsessionpart;
    private Timeline updateuitimeline;
    private Duration totalsessiondurationelapsed;
    private Duration totalsessionduration;
    private int sessionpartcount;
    public MainController Root;
    public List<SessionPart> sessionpartswithGoalsCompletedThisSession;
    private List<SessionPart> itemsinsession;
    public PlayerUI playerUI;
    public DisplayReference displayReference;
    public Options options;
    public PlayerState playerState;
    public CreatorState creatorState = CreatorState.NOT_CREATED;
    public ExporterState exporterState;
    public ReferenceType referenceType;
    public AmbiencePlaybackType ambiencePlaybackType;
    private Double currententrainmentvolume;
    private Double currentambiencevolume;
    private Integer exportserviceindex;
    private ArrayList<Service<Boolean>> exportservices;
    private Service<Boolean> currentexporterservice;
    public boolean ambienceenabled;

    public This_Session(MainController mainController) {
        Root = mainController;
        playerState = PlayerState.IDLE;
        options = Root.getOptions();
        Presession =  new Qi_Gong(0, "Presession", "Gather Qi Before The Session Starts", this, Root.PreSwitch, Root.PreTime);
        Rin = new Cut(1, "RIN", "Meet (Spirit & Invite Into The Body)", this, Root.RinSwitch, Root.RinTime);
        Kyo = new Cut(2, "KYO", "Troops (Manage Internal Strategy/Tools)", this, Root.KyoSwitch, Root.KyoTime);
        Toh = new Cut(3, "TOH", "Fighting (Against Myself To Attain Harmony)", this, Root.TohSwitch, Root.TohTime);
        Sha = new Cut(4, "SHA", "Person (Meet & Become Person We Met In RIN)", this, Root.ShaSwitch, Root.ShaTime);
        Kai = new Cut(5, "KAI", "All/Everything (Feeling Love & Compassion For Absolutely Everything)", this, Root.KaiSwitch, Root.KaiTime);
        Jin = new Cut(6, "JIN", "Understanding", this, Root.JinSwitch, Root.JinTime);
        Retsu = new Cut(7, "RETSU", "Dimension", this, Root.RetsuSwitch, Root.RetsuTime);
        Zai = new Cut(8, "ZAI", "Creation", this, Root.ZaiSwitch, Root.ZaiTime);
        Zen = new Cut(9, "ZEN", "Perfection", this, Root.ZenSwitch, Root.ZenTime);
        Earth = new Element(10, "Earth", "", this, Root.EarthSwitch, Root.EarthTime);
        Air = new Element(11, "Air", "", this, Root.AirSwitch, Root.AirTime);
        Fire = new Element(12, "Fire", "", this, Root.FireSwitch, Root.FireTime);
        Water = new Element(13, "Water", "", this, Root.WaterSwitch, Root.WaterTime);
        Void = new Element(14, "Void", "", this, Root.VoidSwitch, Root.VoidTime);
        Postsession = new Qi_Gong(15, "Postsession", "Gather Qi After The Session Ends", this, Root.PostSwitch, Root.PostTime);
        Total = new Total(16, "Total", "", this, null, null);
    }

// Getters And Setters
    public ArrayList<SessionPart> getAllSessionParts() {return new ArrayList<>(Arrays.asList(Presession, Rin, Kyo, Toh, Sha, Kai, Jin, Retsu, Zai, Zen, Earth, Air, Fire, Water, Void, Postsession));}
    public static ArrayList<String> getAllSessionParts_Names() {
        return new ArrayList<>(Arrays.asList("Presession", "RIN", "KYO", "TOH", "SHA", "KAI", "JIN", "RETSU", "ZAI", "ZEN", "EARTH", "AIR", "FIRE", "WATER", "VOID", "Postsession"));
    }
    public ArrayList<SessionPart> getAllSessionPartsincludingTotal() {return new ArrayList<>(Arrays.asList(Presession, Rin, Kyo, Toh, Sha, Kai, Jin, Retsu, Zai, Zen, Earth, Air, Fire, Water, Void, Postsession, Total));}
    public ArrayList<String> getAllSessionPartsincludingTotal_Names() {
        return getAllSessionPartsincludingTotal().stream().map(i -> i.name).collect(Collectors.toCollection(ArrayList::new));
    }
    public ArrayList<Cut> getallCuts()  {return new ArrayList<>(Arrays.asList(Rin, Kyo, Toh, Sha, Kai, Jin, Retsu, Zai, Zen));}
    public ArrayList<Element> getallElements() {return new ArrayList<>(Arrays.asList(Earth, Air, Fire, Water, Void));}
    public List<SessionPart> getallitemsinSession() {
        return itemsinsession;
    }
    public SessionPart getCurrentsessionpart() {
        return currentsessionpart;
    }
    public int getCurrentindexofplayingelement() {
        try {return getallitemsinSession().indexOf(currentsessionpart);}
        catch (NullPointerException | IndexOutOfBoundsException ignored) {return -1;}
    }
    public void setItemsinsession(List<SessionPart> itemsinsession) {
        this.itemsinsession = itemsinsession;
    }
    public Double getCurrententrainmentvolume() {
        return currententrainmentvolume;
    }
    public void setCurrententrainmentvolume(Double currententrainmentvolume) {
        this.currententrainmentvolume = currententrainmentvolume;
    }
    public Double getCurrentambiencevolume() {
        return currentambiencevolume;
    }
    public void setCurrentambiencevolume(Double currentambiencevolume) {
        this.currentambiencevolume = currentambiencevolume;
    }

    // Cut And Element Getters
    public Qi_Gong getPresession() {
        return Presession;
    }
    public Cut getRin() {
        return Rin;
    }
    public Cut getKyo() {
        return Kyo;
    }
    public Cut getToh() {
        return Toh;
    }
    public Cut getSha() {
        return Sha;
    }
    public Cut getJin() {
        return Jin;
    }
    public Cut getKai() {
        return Kai;
    }
    public Cut getRetsu() {
        return Retsu;
    }
    public Cut getZai() {
        return Zai;
    }
    public Cut getZen() {
        return Zen;
    }
    public Qi_Gong getPostsession() {
        return Postsession;
    }
    public Element getEarth() {
        return Earth;
    }
    public Element getAir() {
        return Air;
    }
    public Element getFire() {
        return Fire;
    }
    public Element getWater() {
        return Water;
    }
    public Element getVoid() {
        return Void;
    }
    public Total getTotal() {return Total;}

// GUI
    public ArrayList<Integer> gui_getallsessionvalues() {
        return getAllSessionParts().stream().map(i -> new Double(i.getduration().toMinutes()).intValue()).collect(Collectors.toCollection(ArrayList::new));
    }

// Creation
    public void creation_createsession() {
        int count = 1;
        for (SessionPart i : getallitemsinSession()) {
            System.out.println(count + ": " + i.name + " With Duration: " + i.getduration().toSeconds() + " Seconds");
            if (! i.creation_build(getallitemsinSession())) {
                creation_reset(false);
                return;
            }
            count++;
        }
        creatorState = CreatorState.CREATED;
    }
    public List<Cut> creation_getCutsInSession() {return getallitemsinSession().stream().filter(i -> i instanceof Cut).map(i -> (Cut) i).collect(Collectors.toCollection(ArrayList::new));}
    public List<Element> creation_getElementsInSession() {return getallitemsinSession().stream().filter(i -> i instanceof Element).map(i -> (Element) i).collect(Collectors.toCollection(ArrayList::new));}
    public void creation_populateitemsinsession() {
        itemsinsession = new ArrayList<>();
        for (SessionPart i : getAllSessionParts()) {
            if (i.getduration().greaterThan(Duration.ZERO)) {
                itemsinsession.add(i);
            } else if (i instanceof Qi_Gong) {
                if (((Qi_Gong) i).ramponly) {itemsinsession.add(i);}
                else {
                    switch (i.number) {
                        case 0: if (Root.getOptions().getSessionOptions().getPrerampenabled()) {itemsinsession.add(i);} break;
                        case 15: if (Root.getOptions().getSessionOptions().getPostrampenabled()) {itemsinsession.add(i);} break;
                    }
                }
            }
        }
//        for (SessionPart i : itemsinsession) {System.out.println(String.format("%s With A Duration Of %s", i.name, i.getduration().toSeconds()));}
    }
    public boolean creation_checkfirstandlastcutsconnect(List<Cut> cutsinsession) {
        if (cutsinsession.size() == 1) {return true;}
        try {
            int count = cutsinsession.get(0).number;
            for (Cut i : cutsinsession) {
                if (i.number != count) {return false;}
                count++;
            }
            return true;
        } catch (NullPointerException | IndexOutOfBoundsException ignored) {return false;}
    }
    public boolean creation_checksessionwellformed() {
//        List<Cut> cutsinsession = creation_getCutsInSession();
//        List<Element> elementsinsession = creation_getElementsInSession();
//        if (! cutsinsession.isEmpty()) {
//            boolean rinisfirstcutinsession = cutsinsession.get(0).number == 1;
//            if (! rinisfirstcutinsession) {
//                rinisfirstcutinsession = ! Root.dialog_getConfirmation("Practiced Cuts Do Not Connect To RIN", null,
//                        "Cuts In Session Not Connected To RIN. Connect " + cutsinsession.get(0).name + " Back To RIN?", null, null);}
//            if (! rinisfirstcutinsession || ! creation_checkfirstandlastcutsconnect(cutsinsession)) {
//                CutsMissingDialog cutsMissingDialog = new CutsMissingDialog(Root, cutsinsession);
//                cutsMissingDialog.showAndWait();
//                switch (cutsMissingDialog.getResult()) {
//                    case YES:
//                        creation_populateitemsinsession();
//                        cutsinsession = creation_getCutsInSession();
//                        break;
//                    case NO:
//                        break;
//                    case CANCEL:
//                        return false;
//                }
//            }
//        }
//        if (! cutsinsession.isEmpty() && ! elementsinsession.isEmpty()) {
//            SessionPlaybackOverview sessionPlaybackOverview = new SessionPlaybackOverview(Root, getallitemsinSession());
//            sessionPlaybackOverview.showAndWait();
//            switch (sessionPlaybackOverview.getResult()) {
//                case YES:
//                    setItemsinsession(sessionPlaybackOverview.getorderedsessionitems());
//                    break;
//                case NO:
//                    break;
//                case CANCEL:
//                    return false;
//            }
//            // Sort Session Parts
//        }
        return true;
    }
    public void creation_checkgoals() {
        ArrayList<SessionPart> sessionpartswithoutlongenoughgoals = Root.goals_util_getsessionpartswithoutlongenoughgoals(getallitemsinSession());
        List<Integer>  notgooddurations = new ArrayList<>();
        if (! sessionpartswithoutlongenoughgoals.isEmpty()) {
            boolean presessionmissinggoals = false;
            int cutcount = 0;
            int elementcount = 0;
            boolean postsessionmissinggoals = false;
            for (SessionPart i : sessionpartswithoutlongenoughgoals) {
                if (i instanceof Cut) {cutcount++;}
                if (i instanceof Element) {elementcount++;}
                else {
                    if (i.name.equals("Presession")) {presessionmissinggoals = true;}
                    if (i.name.equals("Postsession")) {postsessionmissinggoals = true;}
                }
                notgooddurations.add(new Double(i.getduration().toMinutes()).intValue());
            }
            StringBuilder notgoodtext = new StringBuilder();
            if (presessionmissinggoals) {notgoodtext.append("Presession\n");}
            if (cutcount > 0) {notgoodtext.append(cutcount).append(" Cut(s)\n");}
            if (elementcount > 0) {notgoodtext.append(elementcount).append(" Element(s)\n");}
            if (postsessionmissinggoals) {notgoodtext.append("Postsession\n");}
            if (Root.dialog_getConfirmation("Confirmation", "Goals Are Missing/Not Long Enough For:", notgoodtext.toString(), "Set Goal And Play", "Play Anyway")) {
                // TODO Make A Goal Set Dialog Before Playback Here
                // Was last parameter-> Util.list_getmaxintegervalue(notgooddurations)
                /*ProgressAndGoalsUI.SetANewGoalForMultipleCutsOrElements s = new ProgressAndGoalsUI.SetANewGoalForMultipleCutsOrElements(Root, sessionpartswithoutlongenoughgoals);
                s.showAndWait();
                if (s.isAccepted()) {
                    List<Integer> cutindexes = s.getSelectedCutIndexes();
                    Double goalhours = s.getGoalhours();
                    LocalDate goaldate = s.getGoaldate();
                    boolean goalssetsuccessfully = true;
                    for (Integer i : cutindexes) {
                        try {
                            SessionPart x = getAllSessionPartsincludingTotal().get(i);
                            x.goals_add(new Goals.Goal(goalhours, x.name));
                        } catch (JAXBException ignored) {
                            goalssetsuccessfully = false;
                            Util.dialog_displayError(Root, "Error", "Couldn't Add Goal For " + getAllSessionPartsincludingTotal().get(i).name, "Check File Permissions");
                        }
                    }
                    if (goalssetsuccessfully) {
                        Util.dialog_displayInformation(Root, "Information", "Goals For " + notgoodtext.toString() + "Set Successfully", "Session Will Now Be Created");
                    }
                }
                break;*/
            }
        }
    }
    public void creation_checkprepostramp() {
        if (Root.getOptions().getSessionOptions().getRampenabled()) {
            if (! itemsinsession.contains(Presession)) {Presession.setRamponly(true);}
            if (! itemsinsession.contains(Postsession)) {Postsession.setRamponly(true);}
        } else if (! itemsinsession.contains(Presession) || ! itemsinsession.contains(Postsession)) {
            AddPrePostRampDialog addPrePostRampDialog = new AddPrePostRampDialog();
            addPrePostRampDialog.showAndWait();
            if (! addPrePostRampDialog.PresessionButton.isDisabled()) {
                Presession.setRamponly(addPrePostRampDialog.PresessionButton.isSelected());
                if (addPrePostRampDialog.MakeDefaultCheckbox.isSelected()) {Root.getOptions().getSessionOptions().setPrerampenabled(addPrePostRampDialog.PresessionButton.isSelected());}
                if (addPrePostRampDialog.PresessionButton.isSelected()) {}
            }
            if (! addPrePostRampDialog.PostsessionButton.isDisabled()) {
                Postsession.setRamponly(addPrePostRampDialog.PostsessionButton.isSelected());
                if (addPrePostRampDialog.MakeDefaultCheckbox.isSelected()) {Root.getOptions().getSessionOptions().setPostrampenabled(addPrePostRampDialog.PostsessionButton.isSelected());}
            }
        }
        creation_populateitemsinsession();
    }
    public void creation_checkambience(CheckBox ambiencecheckbox) {
        ArrayList<SessionPart> sessionpartswithnoambience = new ArrayList<>();
        ArrayList<SessionPart> sessionpartswithreducedambience = new ArrayList<>();
        getAllSessionParts().stream().filter(i -> i.getduration().greaterThan(Duration.ZERO)).forEach(i -> {
            Root.CreatorStatusBar.setText(String.format("Checking Ambience. Currently Checking %s...", i.name));
            if (!i.getAmbience().hasAnyAmbience()) {sessionpartswithnoambience.add(i);}
            else if (!i.getAmbience().hasEnoughAmbience(i.getduration())) {sessionpartswithreducedambience.add(i);}
        });
        Root.CreatorStatusBar.setText("");
        if (! sessionpartswithnoambience.isEmpty()) {
            StringBuilder a = new StringBuilder();
            for (int i = 0; i < sessionpartswithnoambience.size(); i++) {
                a.append(sessionpartswithnoambience.get(i).name);
                if (i != sessionpartswithnoambience.size() - 1) {a.append(", ");}
            }
            if (Root.dialog_getConfirmation("Missing Ambience", null, "Missing Ambience For " + a.toString() + ". Ambience Cannot Be Enabled For Session Without At Least One Working Ambience File" +
                            " Per Session Part", "Add Ambience", "Disable Ambience")) {
                if (sessionpartswithnoambience.size() == 1) {Root.menu_openadvancedambienceeditor(sessionpartswithnoambience.get(0));}
                else {Root.menu_openadvancedambienceeditor();}
            } else {ambiencecheckbox.setSelected(false);}
        } else if (! sessionpartswithreducedambience.isEmpty()) {
                StringBuilder a = new StringBuilder();
                int count = 0;
                for (int i = 0; i < sessionpartswithreducedambience.size(); i++) {
                    a.append("\n");
                    SessionPart thissessionpart = sessionpartswithreducedambience.get(i);
                    String formattedcurrentduration = Util.formatdurationtoStringSpelledOut(thissessionpart.getAmbience().gettotalActualDuration(), null);
                    String formattedexpectedduration = Util.formatdurationtoStringSpelledOut(thissessionpart.getduration(), null);
                    a.append(count + 1).append(". ").append(thissessionpart.name).append(" >  Current: ").append(formattedcurrentduration).append(" | Needed: ").append(formattedexpectedduration);
                    count++;
                }
                new SelectAmbiencePlaybackType(count).showAndWait();
                if (ambiencePlaybackType == null) {ambiencecheckbox.setSelected(false);}
        } else {
            ambiencecheckbox.setSelected(true);
            ambiencePlaybackType = Root.getOptions().getSessionOptions().getAmbiencePlaybackType();
        }
    }
    public boolean creation_checkreferencefiles(boolean enableprompt) {
        int invalidsessionpartcount = 0;
        for (SessionPart i : getallitemsinSession()) {
            if (!i.reference_filevalid(referenceType)) invalidsessionpartcount++;
        }
        if (invalidsessionpartcount > 0 && enableprompt) {
            return Root.dialog_getConfirmation("Confirmation", null, "There Are " + invalidsessionpartcount + " Session Parts With Empty/Invalid Reference Files", "Enable Reference Anyway", "Disable Reference");
        } else {return invalidsessionpartcount == 0;}
    }
    public void creation_reset(boolean setvaluetozero) {
        if (itemsinsession != null) {itemsinsession.clear();}
        creatorState = CreatorState.NOT_CREATED;
        for (SessionPart i : getAllSessionParts()) {i.creation_reset(setvaluetozero);}
    }

// Export
    public Service<Boolean> exporter_getsessionexporter() {
//        CreatorAndExporterUI.ExporterUI exportingSessionDialog = new CreatorAndExporterUI.ExporterUI(this);
        return new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        updateTitle("Finalizing Session");
//                        int taskcount = cutsinsession.size() + 2;
//                        // TODO Mix Entrainment And Ambience
//                        for (Cut i : cutsinsession) {
//                            updateMessage("Combining Entrainment And Ambience For " + i.name);
//                            if (! i.mixentrainmentandambience()) {cancel();}
//                            if (isCancelled()) {return false;}
//                            updateProgress((double) (cutsinsession.indexOf(i) / taskcount), 1.0);
//                            updateMessage("Finished Combining " + i.name);
//                        }
                        updateMessage("Creating Final Session File (May Take A While)");
                        exporter_export();
                        if (isCancelled()) {return false;}
//                        updateProgress(taskcount - 1, 1.0);
                        updateMessage("Double-Checking Final Session File");
                        boolean success = exporter_testfile();
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
//            if (exporterservice.getValue()) {Util.dialog_displayInformation("Information", "Export Succeeded", "File Saved To: ");}
//            else {Util.dialog_displayError("Error", "Errors Occured During Export", "Please Try Again Or Contact Me For Support");}
//            exportingSessionDialog.close();
//        });
//        exporterservice.setOnFailed(event -> {
//            String v = exporterservice.getException().getMessage();
//            Util.dialog_displayError("Error", "Errors Occured While Trying To Create The This_Session. The Main Exception I Encoured Was " + v,
//                    "Please Try Again Or Contact Me For Support");
//            This_Session.exporter_deleteprevioussession();
//            exportingSessionDialog.close();
//        });
//        exporterservice.setOnCancelled(event -> {
//            Util.dialog_displayInformation("Cancelled", "Export Cancelled", "You Cancelled Export");
//            This_Session.exporter_deleteprevioussession();
//            exportingSessionDialog.close();
//        });
//        return false;
    }
    public void exporter_getnewexportsavefile() {
//        File tempfile = Util.filechooser_save(Root.getScene(), "Save Export File As", null);
//        if (tempfile != null && Util.audio_isValid(tempfile)) {
//            setExportfile(tempfile);
//        } else {
//            if (tempfile == null) {return;}
//            if (Util.dialog_OKCancelConfirmation(Root, "Confirmation", "Invalid Audio File Extension", "Save As .mp3?")) {
//                String file = tempfile.getAbsolutePath();
//                int index = file.lastIndexOf(".");
//                String firstpart = file.substring(0, index - 1);
//                setExportfile(new File(firstpart.concat(".mp3")));
//            }
//        }
    }
    public boolean exporter_export() {
        ArrayList<File> filestoexport = new ArrayList<>();
//        for (int i=0; i < cutsinsession.size(); i++) {
//            filestoexport.add(cutsinsession.get(i).getFinalexportfile());
//            if (i != cutsinsession.size() - 1) {
//                filestoexport.add(new File(Root.getOptions().getSessionOptions().getAlertfilelocation()));
//            }
//        }
        return filestoexport.size() != 0;
    }
    public boolean exporter_testfile() {
//        try {
//            MediaPlayer test = new MediaPlayer(new Media(getExportfile().toURI().toString()));
//            test.setOnReady(test::dispose);
//            return true;
//        } catch (MediaException ignored) {return false;}
        return false;
    }
    public static void exporter_deleteprevioussession() {
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
    public boolean player_confirmOverview() {
        SessionPlaybackOverview sessionPlaybackOverview = new SessionPlaybackOverview();
        sessionPlaybackOverview.showAndWait();
        return true;
    }
    public void player_openplayer() {
        playerUI = new PlayerUI();
        playerUI.setOnShowing(event -> Root.getStage().setIconified(true));
        playerUI.setOnCloseRequest(event -> {
            if (! player_endsessionprematurely()) {event.consume();}
            else {player_stop();}
        });
        playerUI.setOnHidden(event -> {if (Root.getStage().isIconified()) {Root.getStage().setIconified(false);}});
        playerUI.showAndWait();
        Root.creation_gui_setDisable(false);
    }
    public void player_play() {
        switch (playerState) {
            case IDLE:
            case STOPPED:
                sessionpartswithGoalsCompletedThisSession = new ArrayList<>();
                totalsessiondurationelapsed = Duration.ZERO;
                totalsessionduration = Duration.ZERO;
                for (SessionPart i : itemsinsession) {totalsessionduration = totalsessionduration.add(i.getduration());}
                playerUI.TotalTotalTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessionduration));
                updateuitimeline = new Timeline(new KeyFrame(Duration.millis(100), ae -> player_updateui()));
                updateuitimeline.setCycleCount(Animation.INDEFINITE);
                updateuitimeline.play();
                sessionpartcount = 0;
                currentsessionpart = itemsinsession.get(sessionpartcount);
                Root.getSessions().createnew();
                currententrainmentvolume = Root.getOptions().getSessionOptions().getEntrainmentvolume();
                currentambiencevolume = Root.getOptions().getSessionOptions().getAmbiencevolume();
                currentsessionpart.start();
                break;
            case PAUSED:
                updateuitimeline.play();
                currentsessionpart.resume();
                break;
        }
    }
    public void player_pause() {
        if (playerState == PlayerState.PLAYING) {
            currentsessionpart.pause();
            updateuitimeline.pause();
        }
    }
    public void player_stop() {
        try {
            currentsessionpart.stop();
            updateuitimeline.stop();
        } catch (NullPointerException ignored) {}
        player_reset(false);
    }
    public void player_updateui() {
        try {
            totalsessiondurationelapsed = totalsessiondurationelapsed.add(Duration.millis(100));
            try {
                currentsessionpart.elapsedtime = currentsessionpart.elapsedtime.add(Duration.millis(100));} catch (NullPointerException ignored) {}
            Float currentprogress;
            Float totalprogress;
            try {
                if (currentsessionpart.elapsedtime.greaterThan(Duration.ZERO)) {currentprogress = (float) currentsessionpart.elapsedtime.toMillis() / (float) currentsessionpart.getduration().toMillis();}
                else {currentprogress = (float) 0;}
            } catch (NullPointerException ignored) {currentprogress = (float) 0;}
            if (totalsessiondurationelapsed.greaterThan(Duration.ZERO)) {
                totalprogress = (float) totalsessiondurationelapsed.toMillis()
                        / (float) totalsessionduration.toMillis();}
            else {totalprogress = (float) 0.0;}
            playerUI.CurrentSessionPartProgress.setProgress(currentprogress);
            playerUI.TotalProgress.setProgress(totalprogress);
            currentprogress *= 100;
            totalprogress *= 100;
            playerUI.CurrentSessionPartTopLabel.setText(String.format("%s (%d", currentsessionpart.name, currentprogress.intValue()) + "%)");
            playerUI.TotalSessionLabel.setText(String.format("Session (%d", totalprogress.intValue()) + "%)");
            try {playerUI.SessionPartCurrentTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(currentsessionpart.elapsedtime));}
            catch (NullPointerException ignored) {playerUI.SessionPartCurrentTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(Duration.ZERO));}
            playerUI.TotalCurrentTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessiondurationelapsed));
            boolean displaynormaltime = playerUI.displaynormaltime;
            if (displaynormaltime) {playerUI.SessionPartTotalTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(currentsessionpart.getduration()));}
            else {playerUI.SessionPartTotalTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(currentsessionpart.getduration().subtract(currentsessionpart.elapsedtime)));}
            if (displaynormaltime) {playerUI.TotalTotalTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessionduration));}
            else {playerUI.TotalTotalTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessionduration.subtract(totalsessiondurationelapsed)));}
            try {
                if (displayReference != null && displayReference.isShowing()) {
                    displayReference.CurrentProgress.setProgress(currentprogress / 100);
                    displayReference.CurrentPercentage.setText(currentprogress.intValue() + "%");
                    displayReference.TotalProgress.setProgress(totalprogress / 100);
                    displayReference.TotalPercentage.setText(totalprogress.intValue() + "%");
                    displayReference.CurrentName.setText(currentsessionpart.name);
                }
            } catch (NullPointerException ignored) {}
            Root.goals_gui_updateui();
            Root.sessions_gui_updateui();
            currentsessionpart.tick();
        } catch (Exception ignored) {}
    }
    public void player_progresstonextsessionpart() {
        try {
            switch (playerState) {
                case TRANSITIONING:
                    try {
                        currentsessionpart.goals_transitioncheck();
                        currentsessionpart.cleanupPlayersandAnimations();
                        sessionpartcount++;
                        currentsessionpart = getallitemsinSession().get(sessionpartcount);
                        currentsessionpart.start();
                    } catch (IndexOutOfBoundsException ignored) {
                        playerState = PlayerState.IDLE;
                        currentsessionpart.cleanupPlayersandAnimations();
                        player_endofsession();
                    }
                    break;
                case PLAYING:
                    player_closereferencefile();
                    player_transition();
                    break;
            }
        } catch (Exception ignored) {}
    }
    public void player_endofsession() {
        playerUI.CurrentSessionPartTopLabel.setText(currentsessionpart.name + " Completed");
        playerUI.TotalSessionLabel.setText("Session Completed");
        updateuitimeline.stop();
        playerUI.PlayButton.setText("Replay");
        playerState = PlayerState.STOPPED;

        Root.getSessions().deletenonvalidsessions();
        Root.session_gui_opensessiondetailsdialog();
        // TODO Prompt For Export
//        if (Util.dialog_OKCancelConfirmation(Root, "Confirmation", "Session Completed", "Export This Session For Later Use?")) {
//            exporter_getsessionexporter();}
        Root.sessions_gui_updateui();
        Root.goals_gui_updateui();
        player_reset(true);
    }
    public void player_reset(boolean endofsession) {
        updateuitimeline = null;
        Root.getSessions().deletenonvalidsessions();
        sessionpartcount = 0;
        totalsessiondurationelapsed = Duration.ZERO;
        totalsessionduration = Duration.ZERO;
        playerUI.reset(endofsession);
    }
    public void player_transition() {
        Session currentsession =  Root.getSessions().getspecificsession( Root.getSessions().totalsessioncount() - 1);
        currentsession.updatesessionpartduration(currentsessionpart.number, new Double(currentsessionpart.getduration().toMinutes()).intValue());
        Root.getSessions().marshall();
        Root.goals_gui_updateui();
        currentsessionpart.stop();
        if (Root.getOptions().getSessionOptions().getAlertfunction()) {
            Media alertmedia = new Media(Root.getOptions().getSessionOptions().getAlertfilelocation());
            MediaPlayer alertplayer = new MediaPlayer(alertmedia);
            alertplayer.play();
            playerState = PlayerState.TRANSITIONING;
            alertplayer.setOnEndOfMedia(() -> {
                alertplayer.stop();
                alertplayer.dispose();
                player_progresstonextsessionpart();
            });
            alertplayer.setOnError(() -> {
                if (Root.dialog_getConfirmation("Alert File Playback Error", null, "An Error Occured While Playing The Alert File.",
                        "Retry Playback", "Progress In Session")) {
                    alertplayer.stop();
                    alertplayer.play();
                } else {
                    alertplayer.stop();
                    alertplayer.dispose();
                    player_progresstonextsessionpart();
                }
            });
        } else {
            playerState = PlayerState.TRANSITIONING;
            player_progresstonextsessionpart();
        }
    }
    public void player_error() {

    }
    public boolean player_endsessionprematurely() {
        if (playerState == PlayerState.PLAYING || playerState == PlayerState.PAUSED || playerState == PlayerState.TRANSITIONING) {
            player_pause();
            if (Root.dialog_getConfirmation("End Session Early", "Session Is Not Completed.", "End Session Prematurely?", "End Session", "Continue")) {return true;}
            else {player_play(); return false;}
        } else {return true;}
    }
    public void player_togglevolumebinding() {
        if (playerState == PlayerState.IDLE || playerState == PlayerState.STOPPED) {
            currentsessionpart.volume_rebindentrainment();
            if (Root.AmbienceSwitch.isSelected()) {
                currentsessionpart.volume_rebindambience();}
        }
    }
    public void player_displayreferencefile() {
        boolean notalreadyshowing = displayReference == null || ! displayReference.isShowing();
        boolean referenceenabledwithvalidtype = Root.getOptions().getSessionOptions().getReferenceoption() &&
                (Root.getOptions().getSessionOptions().getReferencetype() == ReferenceType.html || Root.getOptions().getSessionOptions().getReferencetype() == ReferenceType.txt);
        if (notalreadyshowing && referenceenabledwithvalidtype) {
            displayReference = new DisplayReference();
            displayReference.show();
            displayReference.setOnHidden(event -> {
                currentsessionpart.volume_rebindentrainment();
                if (Root.AmbienceSwitch.isSelected()) {
                    currentsessionpart.volume_rebindambience();
                }
            });
        }
    }
    public void player_displayreferencepreview(String referencetext) {
        new DisplayReference(referencetext).showAndWait();
    }
    public void player_closereferencefile() {
        if (player_isreferencecurrentlyDisplayed()) {
            displayReference.close();
        }
    }
    public boolean player_isreferencecurrentlyDisplayed() {
        return displayReference != null && displayReference.isShowing() && displayReference.EntrainmentVolumeSlider != null;
    }

// Dialogs
    public class AddPrePostRampDialog extends Stage {
        public ToggleButton PresessionButton;
        public ToggleButton PostsessionButton;
        public CheckBox MakeDefaultCheckbox;
        public Button CloseButton;

        public AddPrePostRampDialog() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/PrePostRampDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                setTitle("Add Pre/Post Ramp To Session");
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                getallitemsinSession().stream().filter(i -> i instanceof Qi_Gong && i.getduration().greaterThan(Duration.ZERO)).forEach(i -> {
                    if (i.number == 0) {
                        PresessionButton.setSelected(true);
                        PresessionButton.setDisable(true);
                        PresessionButton.setTooltip(new Tooltip("Already A Part Of Session"));}
                    if (i.number == 15) {
                        PostsessionButton.setSelected(true);
                        PostsessionButton.setDisable(true);
                        PostsessionButton.setTooltip(new Tooltip("Already A Part Of Session"));}
                });
                setOnCloseRequest(event -> {});
            } catch (IOException ignored) {}
        }
    }
    public class SessionPlaybackOverview extends Stage {
        public TableView<SessionItem> SessionItemsTable;
        public TableColumn<SessionItem, Integer> NumberColumn;
        public TableColumn<SessionItem, String> NameColumn;
        public TableColumn<SessionItem, String> DurationColumn;
        public TableColumn<SessionItem, String> GoalColumn;
        public Button UpButton;
        public Button DownButton;
        public Button CancelButton;
        public Button AdjustDurationButton;
        public Button SetGoalButton;
        public TextField TotalSessionTime;
        public Button PlaySessionButton;
        public TextField CompletionTime;
        private List<SessionPart> alladjustedsessionitems;
        private SessionPart selectedsessionpart;
        private ObservableList<SessionItem> tableitems = FXCollections.observableArrayList();

        public SessionPlaybackOverview() {
            try {
                alladjustedsessionitems = itemsinsession;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SortSessionParts.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                this.setOnCloseRequest(event -> {
//                    if (Root.dialog_getConfirmation("Cancel Session Playback", null, "This Will Cancel Session Playback", "Cancel Playback", "Continue")) {
//                        close();
//                    }
                });
                NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
                NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
                DurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
                SessionItemsTable.setOnMouseClicked(event -> itemselected());
                tableitems = FXCollections.observableArrayList();
                UpButton.setDisable(true);
                DownButton.setDisable(true);
                populatetable();
            } catch (IOException ignored) {}
        }

        // Table Methods
        public void itemselected() {
            int index = SessionItemsTable.getSelectionModel().getSelectedIndex();
            boolean validitemselected = index != -1;
            UpButton.setDisable(! validitemselected && index == 0);
            DownButton.setDisable(! validitemselected && index != SessionItemsTable.getItems().size() - 1);
            if (index != -1) {selectedsessionpart = alladjustedsessionitems.get(index);}
        }
        public void populatetable() {
            System.out.println("Populating Table");
            tableitems.clear();
            if (alladjustedsessionitems == null) {alladjustedsessionitems = new ArrayList<>();}
            else {alladjustedsessionitems.clear();}
            int count = 1;
            for (SessionPart x : getAllSessionParts()) {
                if ((alladjustedsessionitems.contains(x)) || (!getwellformedcuts().isEmpty() && x instanceof Cut && (!alladjustedsessionitems.contains(x) && getwellformedcuts().contains(x)))) {
                    tableitems.add(new SessionItem(count, x.name, x.getdurationasString(150.0), x.goals_getCurrentAsString(150.0)));
                    alladjustedsessionitems.add(x);
                    count++;
                }
            }
            System.out.println("Table Has " + tableitems.size() + " Items");
            SessionItemsTable.setItems(tableitems);
        }

    // Order/Sort Session Parts
        public void moveitemup(ActionEvent actionEvent) {
            int selectedindex = SessionItemsTable.getSelectionModel().getSelectedIndex();
            if (selectedindex == -1) {return;}
            if (tableitems.get(selectedindex).name.get().equals("Presession") || tableitems.get(selectedindex).name.get().equals("Postsession")) {
                Root.dialog_displayInformation("Information", "Cannot Move", "Presession And Postsession Cannot Be Moved");
                return;
            }
            if (selectedindex == 0) {return;}
            SessionPart selecteditem = alladjustedsessionitems.get(selectedindex);
            SessionPart oneitemup = alladjustedsessionitems.get(selectedindex - 1);
            if (selecteditem instanceof Cut && oneitemup instanceof Cut) {
                if (selecteditem.number > oneitemup.number) {
                    Root.dialog_displayInformation("Cannot Move", selecteditem.name + " Cannot Be Moved Before " + oneitemup.name + ". Cuts Would Be Out Of Order", "Cannot Move");
                    return;
                }
            }
            if (oneitemup instanceof Qi_Gong) {
                Root.dialog_displayInformation("Cannot Move", "Cannot Replace Presession", "Cannot Move");
                return;
            }
            Collections.swap(alladjustedsessionitems, selectedindex, selectedindex - 1);
            populatetable();
        }
        public void moveitemdown(ActionEvent actionEvent) {
            int selectedindex = SessionItemsTable.getSelectionModel().getSelectedIndex();
            if (selectedindex == -1) {return;}
            if (tableitems.get(selectedindex).name.get().equals("Presession") || tableitems.get(selectedindex).name.get().equals("Postsession")) {
                Root.dialog_displayInformation("Information", "Cannot Move", "Presession And Postsession Cannot Be Moved");
                return;
            }
            if (selectedindex == tableitems.size() - 1) {return;}
            SessionPart selecteditem = alladjustedsessionitems.get(selectedindex);
            SessionPart oneitemdown = alladjustedsessionitems.get(selectedindex + 1);
            if (selecteditem instanceof Cut && oneitemdown instanceof Cut) {
                if (selecteditem.number < oneitemdown.number) {
                    Root.dialog_displayInformation("Cannot Move", selecteditem.name + " Cannot Be Moved After " + oneitemdown.name + ". Cuts Would Be Out Of Order", "Cannot Move");
                    return;
                }
            }
            if (oneitemdown instanceof Qi_Gong) {
                Root.dialog_displayInformation("Cannot Move", "Cannot Replace Postsession", "Cannot Move");
                return;
            }
            Collections.swap(alladjustedsessionitems, selectedindex, selectedindex + 1);
            populatetable();
        }

    // Session Parts Missing/ Out Of Order
        public List<Cut> getwellformedcuts() {
            List<Cut> wellformedcuts = new ArrayList<>();
            for (int i=0; i<getlastworkingcutindex(); i++) {
                wellformedcuts.add(getallCuts().get(i));
            }
            return wellformedcuts;
        }
        public void adjustduration(ActionEvent actionEvent) {
            if (selectedsessionpart != null) {
                SessionOverviewChangeDuration changedurationdialog = new SessionOverviewChangeDuration();
                changedurationdialog.showAndWait();
                selectedsessionpart.changevalue(changedurationdialog.getDuration());
                populatetable();
            }
        }
        public int getlastworkingcutindex() {
            int lastcutindex = 0;
            for (SessionPart i : itemsinsession) {
                if (i instanceof Cut && i.getduration().greaterThan(Duration.ZERO)) {lastcutindex = i.number;}
            }
            return lastcutindex;
        }

    // Pre/Post Addition If Missing

    // Alert File


    // Dialog Methods
        public void playsession(ActionEvent actionEvent) {
            itemsinsession = alladjustedsessionitems;
            close();
        }
        public void cancel(ActionEvent actionEvent) {
            alladjustedsessionitems = null;
            close();
        }

    // Goal Methods
        public void setgoal(ActionEvent actionEvent) {

        }


        class SessionItem {
            private IntegerProperty number;
            private StringProperty name;
            private StringProperty duration;
            private StringProperty goal;

            public SessionItem(int number, String name, String duration, String goal) {
                this.number = new SimpleIntegerProperty(number);
                this.name = new SimpleStringProperty(name);
                this.duration = new SimpleStringProperty(duration);
                this.goal = new SimpleStringProperty(goal);
            }
        }

    }
    public class SessionOverviewChangeDuration extends Stage {
        public Button CancelButton;
        public Button OKButton;
        public TextField MinutesTextField;
        private int duration;

        public SessionOverviewChangeDuration() {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionPartInvocationDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                options.setStyle(this);
                this.setResizable(false);
            } catch (IOException ignored) {}
            setTitle("SessionPart Invocation");
            MinutesTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {if (newValue.matches("\\d*")) {
                    MinutesTextField.setText(Integer.toString(Integer.parseInt(newValue)));}  else {
                    MinutesTextField.setText(oldValue);}}
                catch (Exception e) {MinutesTextField.setText("");}
            });
            MinutesTextField.setText("0");
        }
        public int getDuration() {
            return duration;
        }
        public void setDuration(int duration) {
            this.duration = duration;
        }
        public void CancelButtonPressed(Event event) {
            setDuration(0);
            this.close();
        }
        public void OKButtonPressed(Event event) {
            try {
                int value = Integer.parseInt(MinutesTextField.getText());
                if (value != 0) {
                    setDuration(value);
                    this.close();
                } else {
                    if (Root.dialog_getConfirmation("Confirmation", null, "Continue With Zero Value (These Session Parts Won't Be Included)", "Continue", "Cancel")) {
                        setDuration(0);
                        this.close();
                    }
                }
            } catch (NumberFormatException e) {
                Root.dialog_displayError("Error", "Value Is Empty", "Enter A Numeric Value Then Press OK");}
        }
    }
    public class SessionOverviewSetGoal extends Stage {}
    public class PlayerUI extends Stage {
        public Button PlayButton;
        public Button PauseButton;
        public Button StopButton;
        public Label StatusBar;
        public RadioButton ReferenceHTMLButton;
        public RadioButton ReferenceTXTButton;
        public Slider EntrainmentVolume;
        public Label EntrainmentVolumePercentage;
        public Slider AmbienceVolume;
        public Label AmbienceVolumePercentage;
        public Label CurrentSessionPartTopLabel;
        public Label SessionPartCurrentTimeLabel;
        public ProgressBar CurrentSessionPartProgress;
        public Label SessionPartTotalTimeLabel;
        public Label TotalCurrentTimeLabel;
        public ProgressBar TotalProgress;
        public Label TotalTotalTimeLabel;
        public Label TotalSessionLabel;
        public Label GoalTopLabel;
        public ProgressBar GoalProgressBar;
        public ToggleButton ReferenceToggleButton;
        public Label GoalPercentageLabel;
        public boolean displaynormaltime = true;


        public PlayerUI() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionPlayerDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                options.setStyle(this);
                setTitle("Session Player");
                reset(false);
                boolean referenceoption = Root.getOptions().getSessionOptions().getReferenceoption();
                if (referenceoption && referenceType != null && creation_checkreferencefiles(false)) {ReferenceToggleButton.setSelected(true);}
                else {ReferenceToggleButton.setSelected(false);}
                togglereference(null);
                ReferenceToggleButton.setSelected(Root.getOptions().getSessionOptions().getReferenceoption());
                setResizable(false);
                SessionPartTotalTimeLabel.setOnMouseClicked(event -> displaynormaltime = !displaynormaltime);
                TotalTotalTimeLabel.setOnMouseClicked(event -> displaynormaltime = !displaynormaltime);
                setOnCloseRequest(event -> {
                    if (playerState == PlayerState.PLAYING || playerState == PlayerState.STOPPED || playerState == PlayerState.PAUSED || playerState == PlayerState.IDLE) {
                        if (player_endsessionprematurely()) {close(); cleanupPlayer();} else {play(); event.consume();}
                    } else {
                        Util.gui_showtimedmessageonlabel(StatusBar, "Cannot Close Player During Fade Animation", 400);
                        new Timeline(new KeyFrame(Duration.millis(400), ae -> currentsessionpart.toggleplayerbuttons()));
                        event.consume();
                    }
                });
            } catch (Exception ignored) {}
        }

        // Button Actions
        public void play() {player_play();}
        public void pause() {
            player_pause();}
        public void stop() {
            player_stop();}
        public void togglereference(ActionEvent actionEvent) {
            boolean buttontoggled = ReferenceToggleButton.isSelected();
            Root.getOptions().getSessionOptions().setReferenceoption(buttontoggled);
            ReferenceHTMLButton.setDisable(! buttontoggled);
            ReferenceTXTButton.setDisable(! buttontoggled);
            if (! buttontoggled) {
                ReferenceHTMLButton.setSelected(false);
                ReferenceTXTButton.setSelected(false);
                Root.getOptions().getSessionOptions().setReferencetype(null);
                player_closereferencefile();
                player_togglevolumebinding();
            } else {
                if (Root.getOptions().getSessionOptions().getReferencetype() == null) {Root.getOptions().getSessionOptions().setReferencetype(kujiin.xml.Options.DEFAULT_REFERENCE_TYPE_OPTION);}
                switch (Root.getOptions().getSessionOptions().getReferencetype()) {
                    case html:
                        ReferenceHTMLButton.setSelected(true);
                        htmlreferenceoptionselected(null);
                        break;
                    case txt:
                        ReferenceTXTButton.setSelected(true);
                        txtreferenceoptionselected(null);
                        break;
                }
                if (! creation_checkreferencefiles(true)) {
                    ReferenceToggleButton.setSelected(false);
                    togglereference(null);
                }
                if (playerState == PlayerState.PLAYING) {
                    player_displayreferencefile();
                    player_togglevolumebinding();
                }
            }
        }
        public void htmlreferenceoptionselected(ActionEvent actionEvent) {
            if (ReferenceToggleButton.isSelected()) {
                ReferenceTXTButton.setSelected(! ReferenceHTMLButton.isSelected());
                if (ReferenceHTMLButton.isSelected()) {referenceType = ReferenceType.html;}
                else {referenceType = ReferenceType.txt;}
            } else {Root.getOptions().getSessionOptions().setReferencetype(null);}
        }
        public void txtreferenceoptionselected(ActionEvent actionEvent) {
            if (ReferenceToggleButton.isSelected()) {
                ReferenceHTMLButton.setSelected(! ReferenceTXTButton.isSelected());
                if (ReferenceTXTButton.isSelected()) {referenceType = ReferenceType.txt;}
                else {referenceType = ReferenceType.html;}
            } else {Root.getOptions().getSessionOptions().setReferencetype(null);}
        }
        public void cleanupPlayer() {}
        public void reset(boolean endofsession) {
            SessionPartCurrentTimeLabel.setText("--:--");
            CurrentSessionPartProgress.setProgress(0.0);
            SessionPartTotalTimeLabel.setText("--:--");
            TotalCurrentTimeLabel.setText("--:--");
            TotalProgress.setProgress(0.0);
            TotalTotalTimeLabel.setText("--:--");
            EntrainmentVolume.setDisable(true);
            EntrainmentVolume.setValue(0.0);
            EntrainmentVolumePercentage.setText("0%");
            AmbienceVolume.setDisable(true);
            AmbienceVolume.setValue(0.0);
            AmbienceVolumePercentage.setText("0%");
            // TODO Reset Goal UI Here
            if (endofsession) {PlayButton.setText("Replay");}
            else {PlayButton.setText("Start");}
            PauseButton.setDisable(true);
            StopButton.setDisable(true);
        }

    }
    public class ExporterUI extends Stage {
        public Button CancelButton;
        public ProgressBar TotalProgress;
        public Label StatusBar;
        public ProgressBar CurrentProgress;
        public Label TotalLabel;
        public Label CurrentLabel;

        public ExporterUI() {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ExportingSessionDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                options.setStyle(this);
                this.setResizable(false);
            } catch (IOException ignored) {}
            setTitle("Exporting Session");
        }

        public void unbindproperties() {
            TotalProgress.progressProperty().unbind();
            CurrentProgress.progressProperty().unbind();
            StatusBar.textProperty().unbind();
            CurrentLabel.textProperty().unbind();
        }
    }
    public class DisplayReference extends Stage {
        public ScrollPane ContentPane;
        public Slider EntrainmentVolumeSlider;
        public Label EntrainmentVolumePercentage;
        public Slider AmbienceVolumeSlider;
        public Label AmbienceVolumePercentage;
        public Button PlayButton;
        public Button PauseButton;
        public Button StopButton;
        public ProgressBar TotalProgress;
        public ProgressBar CurrentProgress;
        public Label CurrentName;
        public Label CurrentPercentage;
        public Label TotalPercentage;
        private Boolean fullscreenoption;
        private Scene scene;

        public DisplayReference() {
            try {
                referenceType = Root.getOptions().getSessionOptions().getReferencetype();
                fullscreenoption = Root.getOptions().getSessionOptions().getReferencefullscreen();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferenceDisplay.fxml"));
                fxmlLoader.setController(this);
                scene = new Scene(fxmlLoader.load());
                setScene(scene);
                Root.getOptions().setStyle(this);
//                this.setResizable(false);
                setTitle(currentsessionpart.name + "'s Reference");
                setsizing();
                loadcontent();
                AmbienceVolumeSlider.setValue(playerUI.AmbienceVolume.getValue());
                AmbienceVolumePercentage.setText(playerUI.AmbienceVolumePercentage.getText());
                EntrainmentVolumeSlider.setValue(playerUI.EntrainmentVolume.getValue());
                EntrainmentVolumePercentage.setText(playerUI.EntrainmentVolumePercentage.getText());
                setOnCloseRequest(event -> untoggleplayerreference());
                if (Root.getSession().getCurrentindexofplayingelement() == 0) {
                    setFullScreenExitHint("Press F11 To Toggle Fullscreen, ESC To Hide Reference");
                } else {setFullScreenExitHint("");}
                addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    switch (event.getCode()) {
                        case ESCAPE:
//                                hide();
//                                untoggleplayerreference();
//                                break;
                        case F11:
                            if (playerState == PlayerState.PLAYING) {
                                boolean fullscreen = this.isFullScreen();
                                fullscreenoption = !fullscreen;
                                Root.getOptions().getSessionOptions().setReferencefullscreen(fullscreenoption);
                                setsizing();
                                if (!fullscreen) {setFullScreenExitHint("");}
                                break;
                            }
                    }
                });
            } catch (IOException ignored) {}
        }
        public DisplayReference(String htmlcontent) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ReferencePreview.fxml"));
            fxmlLoader.setController(this);
            try {
                scene = new Scene(fxmlLoader.load());
                setScene(scene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException ignored) {}
            setTitle("Reference File Preview");
            fullscreenoption = false;
            setsizing();
            WebView browser = new WebView();
            WebEngine webEngine = browser.getEngine();
            webEngine.setUserStyleSheetLocation(new File(kujiin.xml.Options.DIRECTORYSTYLES, "referencefile.css").toURI().toString());
            webEngine.loadContent(htmlcontent);
            ContentPane.setContent(browser);
        }

        public void setsizing() {
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            double height = primaryScreenBounds.getHeight();
            double width = primaryScreenBounds.getWidth();
            if (! fullscreenoption) {height -= 100; width -= 100;}
            this.setFullScreen(fullscreenoption);
            this.setHeight(height);
            this.setWidth(width);
            this.centerOnScreen();
            ContentPane.setFitToWidth(true);
            ContentPane.setFitToHeight(true);
            ContentPane.setStyle("-fx-background-color: #212526");
        }
        public void loadcontent() {
            File referencefile = currentsessionpart.reference_getFile();
            if (referencefile != null) {
                switch (referenceType) {
                    case txt:
                        StringBuilder sb = new StringBuilder();
                        try (FileInputStream fis = new FileInputStream(referencefile);
                             BufferedInputStream bis = new BufferedInputStream(fis)) {
                            while (bis.available() > 0) {
                                sb.append((char) bis.read());
                            }
                        } catch (Exception ignored) {}
                        TextArea ta = new TextArea();
                        ta.setText(sb.toString());
                        ta.setWrapText(true);
                        ContentPane.setContent(ta);
                        Root.getOptions().setStyle(this);
                        break;
                    case html:
                        WebView browser = new WebView();
                        WebEngine webEngine = browser.getEngine();
                        webEngine.load(referencefile.toURI().toString());
                        webEngine.setUserStyleSheetLocation(Options.REFERENCE_THEMEFILE.toURI().toString());
                        ContentPane.setContent(browser);
                        break;
                    default:
                        break;
                }
            } else {System.out.println("Reference File Is Null");}
        }
        public void untoggleplayerreference() {
            playerUI.ReferenceToggleButton.setSelected(false);
            playerUI.togglereference(null);
        }

        public void play(ActionEvent actionEvent) {player_play();}
        public void pause(ActionEvent actionEvent) {
            player_pause();}
        public void stop(ActionEvent actionEvent) {
            player_stop();}

}
    public class ExportDialog extends Stage {
        private File finalexportfile;
        private File tempentrainmenttextfile;
        private File tempambiencetextfile;
        private File tempentrainmentfile;
        private File tempambiencefile;
        private File finalentrainmentfile;
        private File finalambiencefile;


        public ExportDialog() {

        }


    }
    public class SelectAmbiencePlaybackType extends Stage {
        public Label TopLabel;
        public Button RepeatButton;
        public Button ShuffleButton;

        public SelectAmbiencePlaybackType(int sessionpartswithoutsufficientambience) {
            ambiencePlaybackType = null;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/AmbiencePlaybackType.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                options.setStyle(this);
                setResizable(false);
                TopLabel.setText("Ambience Is Not Long Enough For " + sessionpartswithoutsufficientambience +  " Session Parts");
                setOnCloseRequest(event -> {
                    if (ambiencePlaybackType == null) {
                        if (! Root.dialog_getConfirmation("Disable Ambience", null, "No Ambience Playback Type Selected", "Disable Ambience", "Cancel")) {event.consume();}
                    }
                });
            } catch (IOException ignored) {}
            setTitle("Select Ambience Playback Type");
        }

        public void repeatbuttonpressed(ActionEvent actionEvent) {
            ambiencePlaybackType = AmbiencePlaybackType.REPEAT;
            close();
        }
        public void shufflebuttonpressed(ActionEvent actionEvent) {
            ambiencePlaybackType = AmbiencePlaybackType.SHUFFLE;
            close();
        }

    }
    public enum ExporterState {
        NOT_EXPORTED, WORKING, COMPLETED, FAILED, CANCELLED
    }
    public enum CreatorState {
        NOT_CREATED, CREATED
    }
    public enum ReferenceType {
        html, txt
    }
    public enum PlayerState {
        PLAYING, PAUSED, STOPPED, TRANSITIONING, IDLE, FADING_PLAY, FADING_RESUME, FADING_PAUSE, FADING_STOP
    }
    public enum AmbiencePlaybackType {
        REPEAT, SHUFFLE, CUSTOM
    }

}