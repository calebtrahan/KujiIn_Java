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
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import kujiin.MainController;
import kujiin.xml.Options;
import kujiin.xml.Session;
import kujiin.xml.SoundFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

// Description Format

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
    public SessionPlaybackOverview sessionPlaybackOverview;
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
    private boolean ambienceenabled = false;
    private ArrayList<Service<Boolean>> exportservices;
    private Service<Boolean> currentexporterservice;

    public This_Session(MainController mainController) {
        Root = mainController;
        playerState = PlayerState.IDLE;
        options = Root.getOptions();
        setupSessionParts();
    }

// Getters And Setters
    private void setupSessionParts() {
        Presession =  new Qi_Gong(0, "Presession", this, Root.PreSwitch, Root.PreTime);
        Presession.setSummary("Gather Qi (Life Energy) Before The Session Starts");
        Presession.setToolTip();
        Rin = new Cut(1, "RIN", this, Root.RinSwitch, Root.RinTime);
        Rin.setFocusPoint("Root Chakra");
        Rin.setConcept("A Celebration Of The Spirit Coming Into The Body");
        Rin.setMantra_Meaning("All/Everything/Vast As It Is Defined Now");
        Rin.setSide_Effects("Increases The Bioelectric Output Of The Body");
        Rin.setToolTip();
        Kyo = new Cut(2, "KYO", this, Root.KyoSwitch, Root.KyoTime);
        Kyo.setFocusPoint("Navel Chakra");
        Kyo.setConcept("In Order To Become Powerful, Responsiblity Must Be Taken For All Actions");
        Kyo.setMantra_Meaning("Use Your Tools/Manage Yourself Correctly");
        Kyo.setSide_Effects("Increases The Healthy Flow Of Energy Leading To The Mastery Of The Control And Direction Of Energy");
        Kyo.setToolTip();
        Toh = new Cut(3, "TOH", this, Root.TohSwitch, Root.TohTime);
        Toh.setFocusPoint("Dan-tian");
        Toh.setConcept("Conscious Dissolement Of All Personal Fights In Order To Achieve Harmony");
        Toh.setMantra_Meaning("Conquering Limiting Beliefs/Doubts Will Allow You To Get The Treasures Of Life");
        Toh.setSide_Effects("Enhances Your Positive Relationship With The Universe, Resulting In Improved Harmony And Balance");
        Toh.setToolTip();
        Sha = new Cut(4, "SHA", this, Root.ShaSwitch, Root.ShaTime);
        Sha.setFocusPoint("Solar Plexus Charkra");
        Sha.setConcept("By Letting Go Of The Limits Of My Mind You Can Vibrate With The Power Of The Universe And Exist Fully Powerful");
        Sha.setMantra_Meaning("Grounded, I Understand The Power That I Express");
        Sha.setSide_Effects("Increases The Healing Ability Of The Body As A Result Of Higher Energy Levels Passing Through The Body");
        Sha.setToolTip();
        Kai = new Cut(5, "KAI", this, Root.KaiSwitch, Root.KaiTime);
        Kai.setFocusPoint("Heart Chakra");
        Kai.setConcept("Everything (Created Or Not) In The Universe Is One");
        Kai.setMantra_Meaning("I Acknowledge The All Pervading Conscious State of Things As They Are, And I Live It [I Am Conscious Of EVERYTHING]");
        Kai.setSide_Effects("Develops Foreknowledge, Premonition, Intuition And Feeling By Acknowlegding That Everything Is One");
        Kai.setToolTip();
        Jin = new Cut(6, "JIN", this, Root.JinSwitch, Root.JinTime);
        Jin.setFocusPoint("Throat Chakra");
        Jin.setConcept("An Observation Of The Universe And What Binds Every Part Of Us To Every Part Of Everything Else");
        Jin.setMantra_Meaning("Conscious Experience Of The Fire That Everything Is Really Made Of");
        Jin.setToolTip();
        Retsu = new Cut(7, "RETSU", this, Root.RetsuSwitch, Root.RetsuTime);
        Retsu.setFocusPoint("Jade Gate Chakra");
        Retsu.setConcept("Transmute The Limits Of Perception By Remembering Our Wholeness As Spirit");
        Retsu.setMantra_Meaning("Everything Flows/Is Elevated To The Divine");
        Retsu.setSide_Effects("Enhances Your Perception And Mastery Of Space-Time Dimensions");
        Retsu.setToolTip();
        Zai = new Cut(8, "ZAI", this, Root.ZaiSwitch, Root.ZaiTime);
        Zai.setFocusPoint("Third Eye Chakra");
        Zai.setConcept("Works With Our Mind, Heart And Body In Order To Define Ourselves As A Spirit That Is Having A Human Experience, Rather Than A Human Being Sometimes Having A Spiritual Experience");
        Zai.setMantra_Meaning("Everything Is Manifested In The Correct Way According To The Experience That I Live");
        Zai.setSide_Effects("Increases My Power Of Manifestation By Fostering A Relationship With The Elements Of Creation");
        Zai.setToolTip();
        Zen = new Cut(9, "ZEN", this, Root.ZenSwitch, Root.ZenTime);
        Zen.setFocusPoint("Crown Chakra");
        Zen.setConcept("The Human Completely Relents Itself To The Spirit With Only The Consciousness Aspect Of The Human Remaining Active");
        Zen.setMantra_Meaning("I am the void and the light");
        Zen.setSide_Effects("Completely Relenting Into Spirit Results In Englightenment, Completeness, Suggestive Invisibility");
        Zen.setToolTip();
        Earth = new Element(10, "Earth", this, Root.EarthSwitch, Root.EarthTime);
        Air = new Element(11, "Air", this, Root.AirSwitch, Root.AirTime);
        Fire = new Element(12, "Fire", this, Root.FireSwitch, Root.FireTime);
        Water = new Element(13, "Water", this, Root.WaterSwitch, Root.WaterTime);
        Void = new Element(14, "Void", this, Root.VoidSwitch, Root.VoidTime);
        Postsession = new Qi_Gong(15, "Postsession", this, Root.PostSwitch, Root.PostTime);
        Postsession.setSummary("Gather Qi (Life Energy) Before The Session Starts");
        Postsession.setToolTip();
        Total = new Total(16, "Total", this, null, null);
    }
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
    public boolean isAmbienceenabled() {
        return ambienceenabled;
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
        for (SessionPart i : getallitemsinSession()) {
            if (! i.creation_build(getallitemsinSession())) {
                creation_reset(false);
                return;
            }
        }
        creatorState = CreatorState.CREATED;
    }
    public void creation_populateitemsinsession() {
        itemsinsession = new ArrayList<>();
        for (SessionPart i : getAllSessionParts()) {
            if (i.getduration().greaterThan(Duration.ZERO) || i.ramponly) {itemsinsession.add(i);}
            else if (i instanceof Qi_Gong && Root.getOptions().getSessionOptions().getPrepostrampenabled()) {i.setRamponly(true); itemsinsession.add(i);}
        }
    }
    public void creation_clearitemsinsession() {itemsinsession.clear();}
    public void creation_checkambience(CheckBox ambiencecheckbox) {
        if (sessionPlaybackOverview != null && sessionPlaybackOverview.isShowing() && sessionPlaybackOverview.AmbienceSwitch.isSelected()) {
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
                for (SessionPart aSessionpartswithreducedambience : sessionpartswithreducedambience) {
                    a.append("\n");
                    String formattedcurrentduration = Util.formatdurationtoStringSpelledOut(aSessionpartswithreducedambience.getAmbience().gettotalDuration(), null);
                    String formattedexpectedduration = Util.formatdurationtoStringSpelledOut(aSessionpartswithreducedambience.getduration(), null);
                    a.append(count + 1).append(". ").append(aSessionpartswithreducedambience.name).append(" >  Current: ").append(formattedcurrentduration).append(" | Needed: ").append(formattedexpectedduration);
                    count++;
                }
                new SelectAmbiencePlaybackType(count).showAndWait();
                if (ambiencePlaybackType == null) {ambiencecheckbox.setSelected(false);}
            } else {
                ambiencecheckbox.setSelected(true);
                ambiencePlaybackType = Root.getOptions().getSessionOptions().getAmbiencePlaybackType();
            }
        }
    }
    public boolean creation_checkreferencefiles(boolean enableprompt) {
        int invalidsessionpartcount = 0;
        for (SessionPart i : getallitemsinSession()) {
            if (!i.reference_filevalid(referenceType)) invalidsessionpartcount++;
        }
        if (invalidsessionpartcount > 0 && enableprompt) {
            return Root.dialog_getConfirmation("Confirmation", null, "There Are " + invalidsessionpartcount + " Session Parts With Empty/Invalid Reference Files", "Enable Reference", "Disable Reference");
        } else {return invalidsessionpartcount == 0;}
    }
    public void creation_reset(boolean setvaluetozero) {
        if (itemsinsession != null) {itemsinsession.clear();}
        creatorState = CreatorState.NOT_CREATED;
        for (SessionPart i : getAllSessionParts()) {i.creation_reset(setvaluetozero);}
    }

// Export
    public boolean exporter_confirmOverview() {
        return true;
    }
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
        sessionPlaybackOverview = new SessionPlaybackOverview();
        sessionPlaybackOverview.showAndWait();
        return sessionPlaybackOverview.getResult();
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
                    System.out.println("In Transition Clause");
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
                    System.out.println("In Playing Clause");
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
        Root.sessions_gui_updateui();
        Root.goals_gui_updateui();
        player_reset(true);
    }
    public void player_reset(boolean endofsession) {
        getAllSessionParts().forEach(SessionPart::cleanupPlayersandAnimations);
        updateuitimeline = null;
        Root.getSessions().deletenonvalidsessions();
        sessionpartcount = 0;
        totalsessiondurationelapsed = Duration.ZERO;
        totalsessionduration = Duration.ZERO;
        playerState = PlayerState.IDLE;
        if (endofsession) {playerUI.reset(true);}
        else {itemsinsession.clear();}
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
            currentsessionpart.pausewithoutanimation();
            updateuitimeline.pause();
            if (Root.dialog_getConfirmation("End Session Early", "Session Is Not Completed.", "End Session Prematurely?", "End Session", "Continue")) {return true;}
            else {player_play(); return false;}
        } else {return true;}
    }
    public void player_togglevolumebinding() {
        if (playerState == PlayerState.IDLE || playerState == PlayerState.STOPPED) {
            currentsessionpart.volume_rebindentrainment();
            if (isAmbienceenabled()) {currentsessionpart.volume_rebindambience();}
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
                if (isAmbienceenabled()) {
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
    public class SessionPlaybackOverview extends Stage {
        public TableView<SessionItem> SessionItemsTable;
        public TableColumn<SessionItem, Integer> NumberColumn;
        public TableColumn<SessionItem, String> NameColumn;
        public TableColumn<SessionItem, String> DurationColumn;
        public TableColumn<SessionItem, String> AmbienceColumn;
        public TableColumn<SessionItem, String> GoalColumn;
        public Button UpButton;
        public Button DownButton;
        public Button CancelButton;
        public Button AdjustDurationButton;
        public Button SetGoalButton;
        public TextField TotalSessionTime;
        public Button PlaySessionButton;
        public TextField CompletionTime;
        public Button SetAmbienceButton;
        public CheckBox AmbienceSwitch;
        public ComboBox<String> AmbienceTypeComboBox;
        public Label StatusBar;
        private List<SessionPart> alladjustedsessionitems;
        private SessionPart selectedsessionpart;
        private ObservableList<SessionItem> tableitems = FXCollections.observableArrayList();
        private final ObservableList<String> ambiencetypes = FXCollections.observableArrayList("Repeat", "Shuffle", "Custom");
        private boolean result;

        public SessionPlaybackOverview() {
            try {
                alladjustedsessionitems = itemsinsession;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionPlaybackOverview.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                setResizable(false);
                setOnCloseRequest(event -> {});
                setTitle("Session Playback Overview");
                NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
                NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
                DurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
                DurationColumn.setCellFactory(new Callback<TableColumn<SessionItem, String>, TableCell<SessionItem, String>>() {
                    @Override
                    public TableCell<SessionItem, String> call(TableColumn<SessionItem, String> param) {
                        return new TableCell<SessionItem, String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (! isEmpty()) {
                                    if (item.equals("No Duration Set")) {setTextFill(Color.RED);}
                                    setText(item);
                                }
                            }
                        };
                    }
                });
                AmbienceColumn.setCellValueFactory(cellData -> cellData.getValue().ambiencesummary);
                AmbienceColumn.setCellFactory(new Callback<TableColumn<SessionItem, String>, TableCell<SessionItem, String>>() {
                    @Override
                    public TableCell<SessionItem, String> call(TableColumn<SessionItem, String> param) {
                        return new TableCell<SessionItem, String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (! isEmpty()) {
                                    if (item.equals("Ambience Not Set") || item.equals("Has No Ambience")) {
                                        System.out.println("Should Be Red");
                                        setTextFill(Color.RED);}
                                    setText(item);

                                }
                            }
                        };
                    }
                });
                GoalColumn.setCellValueFactory(cellData -> cellData.getValue().goalsummary);
                GoalColumn.setCellFactory(new Callback<TableColumn<SessionItem, String>, TableCell<SessionItem, String>>() {
                    @Override
                    public TableCell<SessionItem, String> call(TableColumn<SessionItem, String> param) {
                        return new TableCell<SessionItem, String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (! isEmpty()) {
                                    if (item.equals("No Goal Set")) {this.setTextFill(Color.YELLOW);}
                                    setText(item);
                                }
                            }
                        };
                    }
                });
                SessionItemsTable.setOnMouseClicked(event -> itemselected());
                tableitems = FXCollections.observableArrayList();
                AmbienceTypeComboBox.setItems(ambiencetypes);
                AmbienceSwitch.setSelected(false);
                if (Root.getOptions().getSessionOptions().getAmbiencePlaybackType() != null) {
                    switch (Root.getOptions().getSessionOptions().getAmbiencePlaybackType()) {
                        case REPEAT: AmbienceTypeComboBox.getSelectionModel().select(0); break;
                        case SHUFFLE: AmbienceTypeComboBox.getSelectionModel().select(1); break;
                        case CUSTOM: AmbienceTypeComboBox.getSelectionModel().select(2); break;
                    }
                }
                AmbienceTypeComboBox.setDisable(true);
                populatetable();
            } catch (IOException ignored) {}
        }

    // Table Methods
        public void itemselected() {
            int index = SessionItemsTable.getSelectionModel().getSelectedIndex();
            if (index != -1) {selectedsessionpart = alladjustedsessionitems.get(index);}
            syncbuttons();
        }
        public void populatetable() {
            tableitems.clear();
            if (alladjustedsessionitems == null) {alladjustedsessionitems = new ArrayList<>();}
            int count = 1;
            List<SessionPart> newsessionitems = new ArrayList<>();
            for (SessionPart x : getAllSessionParts()) {
                if ((alladjustedsessionitems.contains(x)) || (!getwellformedcuts().isEmpty() && x instanceof Cut && (!alladjustedsessionitems.contains(x) && getwellformedcuts().contains(x)))) {
                    tableitems.add(new SessionItem(count, x.name, x.getdurationasString(true, 150.0), getambiencetext(x), x.goals_getCurrentAsString(true, 150.0)));
                    newsessionitems.add(x);
                    count++;
                }
            }
            alladjustedsessionitems = newsessionitems;
            SessionItemsTable.setItems(tableitems);
            syncbuttons();
            calculatetotalduration();
        }
        public void syncbuttons() {
            int index = SessionItemsTable.getSelectionModel().getSelectedIndex();
            boolean itemselected = index != -1;
            UpButton.setDisable(index < 1);
            DownButton.setDisable(! itemselected && index != SessionItemsTable.getItems().size() - 1);
            AdjustDurationButton.setDisable(selectedsessionpart == null);
            if (selectedsessionpart != null) {
                SetGoalButton.setDisable(selectedsessionpart.goals_ui_currentgoalisset());
                SetAmbienceButton.setDisable(ambiencePlaybackType == null || ambiencePlaybackType != AmbiencePlaybackType.CUSTOM);
            }
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
                Root.dialog_displayInformation("Information", "Cannot Move", tableitems.get(selectedindex).name + " Cannot Be Moved");
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

    // Session Parts Missing / Out Of Order
        public List<Cut> getwellformedcuts() {
            List<Cut> wellformedcuts = new ArrayList<>();
            for (int i=0; i<getlastworkingcutindex(); i++) {
                wellformedcuts.add(getallCuts().get(i));
            }
            return wellformedcuts;
        }
        public int getlastworkingcutindex() {
            int lastcutindex = 0;
            for (SessionPart i : itemsinsession) {
                if (i instanceof Cut && i.getduration().greaterThan(Duration.ZERO)) {lastcutindex = i.number;}
            }
            return lastcutindex;
        }

    //  Duration Methods
        public void adjustduration(ActionEvent actionEvent) {
            if (selectedsessionpart != null) {
                SessionOverviewChangeDuration changedurationdialog = new SessionOverviewChangeDuration(selectedsessionpart);
                changedurationdialog.showAndWait();
                switch (changedurationdialog.result) {
                    case DURATION:
                        selectedsessionpart.changevalue((int) changedurationdialog.getDuration().toMinutes());
                        break;
                    case RAMP:
                        selectedsessionpart.setRamponly(true);
                        break;
                    case CANCEL:
                        break;
                }
                populatetable();
            }
        }
        public void calculatetotalduration() {
            Duration duration = Duration.ZERO;
            for (SessionPart i : alladjustedsessionitems) {
                duration = duration.add(i.getduration());
            }
            TotalSessionTime.setText(Util.formatdurationtoStringSpelledOut(duration, TotalSessionTime.getLayoutBounds().getWidth()));
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MILLISECOND, new Double(duration.toMillis()).intValue());
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            CompletionTime.setText(sdf.format(cal.getTime()));
        }

    // Dialog Methods
        public boolean getResult() {
            return result;
        }
        public void playsession(ActionEvent actionEvent) {
            List<Integer> indexesmissingduration = new ArrayList<>();
            List<Integer>  indexesmissinggoals = new ArrayList<>();
            for (SessionPart i : alladjustedsessionitems) {
                if (i.getduration() == Duration.ZERO && ! i.ramponly) {indexesmissingduration.add(alladjustedsessionitems.indexOf(i));}
                if (! i.goals_ui_currentgoalisset()) {indexesmissinggoals.add(alladjustedsessionitems.indexOf(i));}
            }
            if (! indexesmissingduration.isEmpty()) {
                if (Root.dialog_getConfirmation("Confirmation", indexesmissingduration.size() + " Session Parts Are Missing Durations", "Set Ramp Only For The Parts Missing Durations",
                        "Set Ramp Only", "Cancel Playback")) {
                    for (int x : indexesmissingduration) {alladjustedsessionitems.get(x).setRamponly(true);}
                } else {return;}
            }
            if (! indexesmissinggoals.isEmpty()) {
                if (! Root.dialog_getConfirmation("Confirmation", indexesmissinggoals.size() + " Session Parts Are Missing Goals", "Continue Playing Session Without Goals?",
                        "Yes", "No")) {return;}
            }
            result = true;
            itemsinsession = alladjustedsessionitems;
            close();
        }
        public void cancel(ActionEvent actionEvent) {
            alladjustedsessionitems = null;
            close();
        }

    // Goal Methods
        public void setgoal(ActionEvent actionEvent) {
            if (selectedsessionpart != null) {
                Root.goals_gui_setnewgoal(selectedsessionpart);
            }
            populatetable();
        }

    // Ambience Methods
        public void ambienceswitchtoggled(Event event) {
            creation_checkambience(AmbienceSwitch);
            ambienceenabled = AmbienceSwitch.isSelected();
            AmbienceTypeComboBox.setDisable(! AmbienceSwitch.isSelected());
            populatetable();
        }
        public String getambiencetext(SessionPart sessionPart) {
            if (! isAmbienceenabled()) {return "Disabled";}
            else {
                if (! sessionPart.getAmbience().hasAnyAmbience()) {return "Has No Ambience";}
                switch (ambiencePlaybackType) {
                    case REPEAT:
                        return "Will Repeat";
                    case SHUFFLE:
                        return "Will Shuffle";
                    case CUSTOM:
                        if (sessionPart.getAmbience().getCustomAmbience() == null || ! sessionPart.getAmbience().getCustomAmbience().isEmpty()) {return "Ambience Not Set";}
                        else {return "Custom Ambience Set";}
                    default:
                        return null;
                }
            }
        }
        public void ambiencetypechanged(ActionEvent actionEvent) {
            int index = AmbienceTypeComboBox.getSelectionModel().getSelectedIndex();
            switch (index) {
                case 0: ambiencePlaybackType = AmbiencePlaybackType.REPEAT; break;
                case 1: ambiencePlaybackType = AmbiencePlaybackType.SHUFFLE; break;
                case 2: ambiencePlaybackType = AmbiencePlaybackType.CUSTOM; break;
            }
            populatetable();
        }
        public void setambience(ActionEvent actionEvent) {
            SessionOverviewAddCustomAmbience addCustomAmbience = new SessionOverviewAddCustomAmbience(selectedsessionpart);
            addCustomAmbience.showAndWait();
            if (addCustomAmbience.getResult()) {
                List<SoundFile> customambiencelist = addCustomAmbience.getCustomAmbienceList();
                selectedsessionpart.getAmbience().setCustomAmbience(customambiencelist);
            }
        }

        class SessionItem {
            private IntegerProperty number;
            private StringProperty name;
            private StringProperty duration;
            private StringProperty ambiencesummary;
            private StringProperty goalsummary;

            public SessionItem(int number, String name, String duration, String ambiencesummary, String goalsummary) {
                this.number = new SimpleIntegerProperty(number);
                this.name = new SimpleStringProperty(name);
                this.duration = new SimpleStringProperty(duration);
                this.ambiencesummary = new SimpleStringProperty(ambiencesummary);
                this.goalsummary = new SimpleStringProperty(goalsummary);
            }
        }

    }
    public class SessionOverviewChangeDuration extends Stage {
        public TextField HoursTextField;
        public TextField MinutesTextField;
        public CheckBox RampOnlyCheckBox;
        public Button SetButton;
        public Button CancelButton;
        private Duration duration;
        private ChangeDurationType result = ChangeDurationType.CANCEL;

        public SessionOverviewChangeDuration(SessionPart sessionPart) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionOverviewChangeDuration.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                options.setStyle(this);
                this.setResizable(false);
                setTitle("Change " + sessionPart.name + " Duration");
                HoursTextField.setText("0");
                MinutesTextField.setText("0");
                Util.custom_textfield_integer(HoursTextField, 0, 60, 1);
                Util.custom_textfield_integer(MinutesTextField, 0, 59, 1);
            } catch (IOException ignored) {}
        }

        public ChangeDurationType getResult() {
            return result;
        }
        public Duration getDuration() {
            return duration;
        }
        public void setDuration(Duration duration) {
            this.duration = duration;
        }
        public void ramponlyselected(ActionEvent actionEvent) {
            HoursTextField.setDisable(RampOnlyCheckBox.isSelected());
            MinutesTextField.setDisable(RampOnlyCheckBox.isSelected());
        }
        public void OKButtonPressed(ActionEvent actionEvent) {
            try {
                if (! RampOnlyCheckBox.isSelected()) {
                    Duration duration = Duration.hours(Double.parseDouble(HoursTextField.getText())).add(Duration.minutes(Double.parseDouble(MinutesTextField.getText())));
                    if (duration.greaterThan(Duration.ZERO)) {
                        setDuration(duration);
                        result = ChangeDurationType.DURATION;
                    } else {Root.dialog_displayInformation("Information", "Cannot Change Value To 0", null); return;}
                } else {result = ChangeDurationType.RAMP;}
                close();
            } catch (NumberFormatException ignored) {}
        }
    }
    public class SessionOverviewAddCustomAmbience extends Stage implements Initializable {
        public TableView<AmbienceSong> AmbienceItemsTable;
        public TableColumn<AmbienceSong, Integer> NumberColumn;
        public TableColumn<AmbienceSong, String> NameColumn;
        public TableColumn<AmbienceSong, String> DurationColumn;
        public Label TotalDurationTextField;
        public Button AcceptButton;
        public Button CancelButton;
        public Button AddButton;
        public Button RemoveButton;
        public Button MoveUpButton;
        public Button MoveDownButton;
        public Button PreviewButton;
        private AmbienceSong selectedtableitem;
        private ObservableList<AmbienceSong> TableItems = FXCollections.observableArrayList();
        private List<SoundFile> CustomAmbienceList;
        private SessionPart sessionPart;
        private boolean result = false;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
            NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
            DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
            AmbienceItemsTable.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> tableselectionchanged(newValue));
        }
        public SessionOverviewAddCustomAmbience(SessionPart sessionPart) {
            try {
                this.sessionPart = sessionPart;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/AddCustomAmbience.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                options.setStyle(this);
                this.setResizable(false);
                RemoveButton.setDisable(true);
                MoveUpButton.setDisable(true);
                MoveDownButton.setDisable(true);
                setTitle("Set Custom Ambience");
            } catch (IOException ignored) {}
        }

        private void tableselectionchanged(AmbienceSong newValue) {
            int index = AmbienceItemsTable.getSelectionModel().getSelectedIndex();
            if (index != -1) {selectedtableitem = AmbienceItemsTable.getItems().get(index);}
            else {selectedtableitem = null;}
            RemoveButton.setDisable(index == -1);
            MoveUpButton.setDisable(index == -1 || index == 0);
            MoveDownButton.setDisable(index == -1 || index == AmbienceItemsTable.getItems().size() - 1);
            PreviewButton.setDisable(index == -1);
        }
        public boolean ambiencealreadyadded(File file) {
            for (SoundFile i : CustomAmbienceList) {
                if (file.equals(i.getFile())) {return true;}
            }
            return false;
        }
        public void addambience(ActionEvent actionEvent) {
            List<File> filesselected = new FileChooser().showOpenMultipleDialog(null);
            if (filesselected == null || filesselected.isEmpty()) {return;}
            // TODO Remove Duplicate Files (If Any Here)
            for (File i : filesselected) {
                if (ambiencealreadyadded(i)) {continue;}
                String suffix = i.getName().substring(i.getName().lastIndexOf("."));
                if (Util.SUPPORTEDAUDIOFORMATS.contains(suffix)) {
                    MediaPlayer calculatedurationplayer = new MediaPlayer(new Media(i.toURI().toString()));
                    calculatedurationplayer.setOnReady(() -> {
                        SoundFile x = new SoundFile(i);
                        x.setDuration(calculatedurationplayer.getTotalDuration().toMillis());
                        CustomAmbienceList.add(x);
                        TableItems.add(new AmbienceSong(filesselected.indexOf(i), x));
                        AmbienceItemsTable.setItems(TableItems);
                        calculatetotal();
                        calculatedurationplayer.dispose();
                    });
                }
            }
        }
        public void removeambience(ActionEvent actionEvent) {
            if (selectedtableitem != null) {
                if (Root.dialog_getConfirmation("Remove Ambience", "Really Remove '" + selectedtableitem.getName() + "'?", "", "Remove", "Cancel")) {
                    int index = TableItems.indexOf(selectedtableitem);
                    TableItems.remove(index);
                    CustomAmbienceList.remove(index);
                    AmbienceItemsTable.setItems(TableItems);
                    calculatetotal();
                }
            }
        }

        public Duration getcurrenttotal() {
            Duration duration = Duration.ZERO;
            for (SoundFile i : CustomAmbienceList) {
                duration = duration.add(Duration.millis(i.getDuration()));
            }
            return duration;
        }
        public void calculatetotal() {
            TotalDurationTextField.setText(Util.formatdurationtoStringSpelledOut(getcurrenttotal(), TotalDurationTextField.getLayoutBounds().getWidth()));
        }
        public void moveupintable(ActionEvent actionEvent) {
            int selectedindex = AmbienceItemsTable.getSelectionModel().getSelectedIndex();
            if (selectedindex > 0) {
                Collections.swap(TableItems, selectedindex, selectedindex - 1);
                Collections.swap(CustomAmbienceList, selectedindex, selectedindex - 1);
                AmbienceItemsTable.setItems(TableItems);
                calculatetotal();
            }
        }
        public void movedownintable(ActionEvent actionEvent) {
            int selectedindex = AmbienceItemsTable.getSelectionModel().getSelectedIndex();
            if (selectedindex != -1 && selectedindex != TableItems.size() - 1) {
                Collections.swap(TableItems, selectedindex, selectedindex + 1);
                Collections.swap(CustomAmbienceList, selectedindex, selectedindex + 1);
                AmbienceItemsTable.setItems(TableItems);
                calculatetotal();
            }
        }
        public void preview(ActionEvent actionEvent) {
            if (selectedtableitem != null) {
                MainController.PreviewFile previewFile = new MainController.PreviewFile(selectedtableitem.getFile(), Root);
                previewFile.showAndWait();
            }
        }

        public List<SoundFile> getCustomAmbienceList() {
            return CustomAmbienceList;
        }
        public boolean getResult() {
            return result;
        }

        public void accept(ActionEvent actionEvent) {
            if (getcurrenttotal().lessThan(sessionPart.getduration())) {
                Root.dialog_displayInformation("Ambience Too Short", "Need At Least " + sessionPart.getdurationasString(false, 50) + " To Set This As Custom Ambience", "");
            } else {result = true; close();}
        }

        public class AmbienceSong {
            private IntegerProperty number;
            private StringProperty name;
            private StringProperty length;
            private File file;
            private double duration;

            public AmbienceSong(int id, SoundFile soundFile) {
                number = new SimpleIntegerProperty(id);
                name = new SimpleStringProperty(soundFile.getName());
                file = soundFile.getFile();
                duration = soundFile.getDuration();
                length = new SimpleStringProperty(Util.formatdurationtoStringDecimalWithColons(Duration.millis(duration)));
            }

            public String getName() {
                return name.getValue();
            }
            public File getFile() {
                return file;
            }
            public double getDuration() {return duration;}
        }
    }
    public class PlayerUI extends Stage {
        public Button PlayButton;
        public Button PauseButton;
        public Button StopButton;
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
        public Label GoalPercentageLabel;
        public boolean displaynormaltime = true;
        public CheckBox ReferenceCheckBox;

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
                if (referenceoption && referenceType != null && creation_checkreferencefiles(false)) {ReferenceCheckBox.setSelected(true);}
                else {ReferenceCheckBox.setSelected(false);}
                togglereference(null);
                ReferenceCheckBox.setSelected(Root.getOptions().getSessionOptions().getReferenceoption());
                setResizable(false);
                SessionPartTotalTimeLabel.setOnMouseClicked(event -> displaynormaltime = ! displaynormaltime);
                TotalTotalTimeLabel.setOnMouseClicked(event -> displaynormaltime = ! displaynormaltime);
                setOnCloseRequest(event -> {
                    if (playerState == PlayerState.PLAYING || playerState == PlayerState.STOPPED || playerState == PlayerState.PAUSED || playerState == PlayerState.IDLE) {
                        if (player_endsessionprematurely()) {close();} else {play(); event.consume();}
                    } else {
//                        Util.gui_showtimedmessageonlabel(StatusBar, "Cannot Close Player During Fade Animation", 400);
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
            boolean buttontoggled = ReferenceCheckBox.isSelected();
            Root.getOptions().getSessionOptions().setReferenceoption(buttontoggled);
            if (! buttontoggled) {
                Root.getOptions().getSessionOptions().setReferencetype(null);
                player_closereferencefile();
                player_togglevolumebinding();
            } else {
                System.out.println("Should be Selecting Reference Type");
                if (Root.getOptions().getSessionOptions().getReferencetype() == null) {Root.getOptions().getSessionOptions().setReferencetype(kujiin.xml.Options.DEFAULT_REFERENCE_TYPE_OPTION);}
                SelectReferenceType selectReferenceType = new SelectReferenceType(Root);
                selectReferenceType.show();
                selectReferenceType.setOnHidden(event -> {
                    if (selectReferenceType.getResult()) {
                        if (! creation_checkreferencefiles(true)) {
                            ReferenceCheckBox.setSelected(false);
                        }
                        if (playerState == PlayerState.PLAYING) {
                            player_displayreferencefile();
                            player_togglevolumebinding();
                        }
                    }
                });
            }
        }
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
        @Override
        public void close() {
            super.close();
            player_reset(false);
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ExportingSessionDialog.fxml"));
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
                setTitle("Reference File Preview");
                fullscreenoption = false;
                setsizing();
                WebView browser = new WebView();
                WebEngine webEngine = browser.getEngine();
                webEngine.setUserStyleSheetLocation(new File(kujiin.xml.Options.DIRECTORYSTYLES, "referencefile.css").toURI().toString());
                webEngine.loadContent(htmlcontent);
                ContentPane.setContent(browser);
            } catch (IOException ignored) {}
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
            playerUI.ReferenceCheckBox.setSelected(false);
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
            try {
                ambiencePlaybackType = null;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/AmbiencePlaybackType.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                options.setStyle(this);
                setResizable(false);
                TopLabel.setText("Ambience Is Not Long Enough For " + sessionpartswithoutsufficientambience +  " Session Parts");
                setTitle("Select Ambience Playback Type");
                setOnCloseRequest(event -> {
                    if (ambiencePlaybackType == null) {
                        if (! Root.dialog_getConfirmation("Disable Ambience", null, "No Ambience Playback Type Selected", "Disable Ambience", "Cancel")) {event.consume();}
                    }
                });
            } catch (IOException ignored) {}

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
    public static class SelectReferenceType extends Stage {
        public RadioButton HTMLRadioButton;
        public RadioButton TextRadioButton;
        public TextArea Description;
        public CheckBox FullScreenCheckbox;
        public Button AcceptButton;
        public Button CancelButton;
        private ArrayList<String> descriptions = new ArrayList<>();
        private boolean result = false;
        private MainController Root;

        public SelectReferenceType(MainController Root) {
            try {
                this.Root = Root;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SelectReferenceType.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                setTitle("Select Reference Type");
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                setOnCloseRequest(event -> {});
                descriptions.add("Display HTML Variation Of Reference Files During Session Playback. This Is Stylized Code That Allows You To Color/Format Your Reference In A Way Plain Text Cannot");
                descriptions.add("Display Text Variation Of Reference Files During Session Playback. This Is Just Plain Text So It Won't Be Formatted Or Styled");
                HTMLRadioButton.setOnMouseEntered(event -> Description.setText(descriptions.get(0)));
                HTMLRadioButton.setOnMouseExited(event -> setdescriptiontoselectedtype());
                HTMLRadioButton.setOnAction(event ->  htmlButtonselected());
                TextRadioButton.setOnMouseEntered(event -> Description.setText(descriptions.get(1)));
                TextRadioButton.setOnMouseExited(event -> setdescriptiontoselectedtype());
                TextRadioButton.setOnAction(event ->  textButtonselected());
                switch (Root.getOptions().getSessionOptions().getReferencetype()) {
                    case html:
                        HTMLRadioButton.setSelected(true);
                        setdescriptiontoselectedtype();
                        break;
                    case txt:
                        TextRadioButton.setSelected(true);
                        setdescriptiontoselectedtype();
                        break;
                }
            } catch (IOException ignored) {}
        }

        private void setdescriptiontoselectedtype() {
            if (HTMLRadioButton.isSelected()) {Description.setText(descriptions.get(0));}
            else if (TextRadioButton.isSelected()) {Description.setText(descriptions.get(1));}
        }
        private void htmlButtonselected() {
            TextRadioButton.setSelected(false);
            Description.setText(descriptions.get(0));
        }
        private void textButtonselected() {
            HTMLRadioButton.setSelected(false);
            Description.setText(descriptions.get(1));
        }
        public ReferenceType getReferenceType() {
            if (HTMLRadioButton.isSelected()) {return ReferenceType.html;}
            else if (TextRadioButton.isSelected()) {return ReferenceType.txt;}
            else {return null;}
        }
        public boolean getFullScreen() {return FullScreenCheckbox.isSelected();}
        public boolean getResult() {return result;}

        public void accept(ActionEvent actionEvent) {
            if (HTMLRadioButton.isSelected() || TextRadioButton.isSelected()) {result = true;  close();}
            else {
                Root.dialog_displayInformation("Cannot Accept", "No Reference Type Selected", null);
                result = false;
            }
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
    public enum ChangeDurationType {
        DURATION, RAMP, CANCEL
    }

}