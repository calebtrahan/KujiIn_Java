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
import javafx.scene.text.Text;
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
    private Meditatable currentmeditatable;
    private Timeline updateuitimeline;
    private Duration totalsessiondurationelapsed;
    private Duration totalsessionduration;
    private int meditatablecount;
    public MainController Root;
    public List<Meditatable> MeditatableswithGoalsCompletedThisSession;
    private List<Meditatable> itemsinsession;
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
    public ArrayList<Meditatable> getAllMeditatables() {return new ArrayList<>(Arrays.asList(Presession, Rin, Kyo, Toh, Sha, Kai, Jin, Retsu, Zai, Zen, Earth, Air, Fire, Water, Void, Postsession));}
    public static ArrayList<String> getAllMeditatables_Names() {
        return new ArrayList<>(Arrays.asList("Presession", "RIN", "KYO", "TOH", "SHA", "KAI", "JIN", "RETSU", "ZAI", "ZEN", "EARTH", "AIR", "FIRE", "WATER", "VOID", "Postsession"));
    }
    public ArrayList<Meditatable> getAllMeditatablesincludingTotal() {return new ArrayList<>(Arrays.asList(Presession, Rin, Kyo, Toh, Sha, Kai, Jin, Retsu, Zai, Zen, Earth, Air, Fire, Water, Void, Postsession, Total));}
    public ArrayList<String> getAllMeditablesincludingTotal_Names() {
        return getAllMeditatablesincludingTotal().stream().map(i -> i.name).collect(Collectors.toCollection(ArrayList::new));
    }
    public ArrayList<Cut> getallCuts()  {return new ArrayList<>(Arrays.asList(Rin, Kyo, Toh, Sha, Kai, Jin, Retsu, Zai, Zen));}
    public ArrayList<Element> getallElements() {return new ArrayList<>(Arrays.asList(Earth, Air, Fire, Water, Void));}
    public List<Meditatable> getallitemsinSession() {
        return itemsinsession;
    }
    public Meditatable getCurrentmeditatable() {
        return currentmeditatable;
    }
    public int getCurrentindexofplayingelement() {
        try {return getallitemsinSession().indexOf(currentmeditatable);}
        catch (NullPointerException | IndexOutOfBoundsException ignored) {return -1;}
    }
    public void setItemsinsession(List<Meditatable> itemsinsession) {
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
        return getAllMeditatables().stream().map(i -> new Double(i.getduration().toMinutes()).intValue()).collect(Collectors.toCollection(ArrayList::new));
    }

// Creation
    public void creation_createsession() {
        for (Meditatable i : getallitemsinSession()) {
            if (! i.creation_build(getallitemsinSession())) {
                creation_reset(false);
                return;
            }
        }
        creatorState = CreatorState.CREATED;
    }
    public List<Cut> creation_getCutsInSession() {return getallitemsinSession().stream().filter(i -> i instanceof Cut).map(i -> (Cut) i).collect(Collectors.toCollection(ArrayList::new));}
    public List<Element> creation_getElementsInSession() {return getallitemsinSession().stream().filter(i -> i instanceof Element).map(i -> (Element) i).collect(Collectors.toCollection(ArrayList::new));}
    public void creation_populateitemsinsession() {
        itemsinsession = new ArrayList<>();
        for (Meditatable i : getAllMeditatables()) {
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
//        for (Meditatable i : itemsinsession) {System.out.println(String.format("%s With A Duration Of %s", i.name, i.getduration().toSeconds()));}
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
        List<Cut> cutsinsession = creation_getCutsInSession();
        List<Element> elementsinsession = creation_getElementsInSession();
        if (! cutsinsession.isEmpty()) {
            boolean rinisfirstmeditatable = cutsinsession.get(0).number == 1;
            if (! rinisfirstmeditatable) {rinisfirstmeditatable = ! Root.dialog_YesNoConfirmation("Session May ", "Cuts In Session Not Connected To RIN", "Connect " + cutsinsession.get(0).name + " Back To RIN?");}
            if (! rinisfirstmeditatable || ! creation_checkfirstandlastcutsconnect(cutsinsession)) {
                CutsMissingDialog cutsMissingDialog = new CutsMissingDialog(Root, cutsinsession);
                cutsMissingDialog.showAndWait();
                switch (cutsMissingDialog.getResult()) {
                    case YES:
                        creation_populateitemsinsession();
                        cutsinsession = creation_getCutsInSession();
                        break;
                    case NO:
                        break;
                    case CANCEL:
                        return false;
                }
            }
        }
        if (! cutsinsession.isEmpty() && ! elementsinsession.isEmpty()) {
            SortSessionItems sortSessionItems = new SortSessionItems(Root, getallitemsinSession());
            sortSessionItems.showAndWait();
            switch (sortSessionItems.getResult()) {
                case YES:
                    setItemsinsession(sortSessionItems.getorderedsessionitems());
                    break;
                case NO:
                    break;
                case CANCEL:
                    return false;
            }
            // Sort Session Parts
        }
        return true;
    }
    public boolean creation_checkgoals() {
        ArrayList<Meditatable> meditatableswithoutlongenoughgoals = Root.goals_util_getmeditatableswithoutlongenoughgoals(getallitemsinSession());
        List<Integer>  notgooddurations = new ArrayList<>();
        if (! meditatableswithoutlongenoughgoals.isEmpty()) {
            boolean presessionmissinggoals = false;
            int cutcount = 0;
            int elementcount = 0;
            boolean postsessionmissinggoals = false;
            for (Meditatable i : meditatableswithoutlongenoughgoals) {
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
            // TODO Make A Goal Set Dialog Before Playback Here
// Was last parameter-> Util.list_getmaxintegervalue(notgooddurations)
/*ProgressAndGoalsUI.SetANewGoalForMultipleCutsOrElements s = new ProgressAndGoalsUI.SetANewGoalForMultipleCutsOrElements(Root, meditatableswithoutlongenoughgoals);
s.showAndWait();
if (s.isAccepted()) {
    List<Integer> cutindexes = s.getSelectedCutIndexes();
    Double goalhours = s.getGoalhours();
    LocalDate goaldate = s.getGoaldate();
    boolean goalssetsuccessfully = true;
    for (Integer i : cutindexes) {
        try {
            Meditatable x = getAllMeditatablesincludingTotal().get(i);
            x.goals_add(new Goals.Goal(goalhours, x.name));
        } catch (JAXBException ignored) {
            goalssetsuccessfully = false;
            Util.dialog_Error(Root, "Error", "Couldn't Add Goal For " + getAllMeditatablesincludingTotal().get(i).name, "Check File Permissions");
        }
    }
    if (goalssetsuccessfully) {
        Util.dialog_Information(Root, "Information", "Goals For " + notgoodtext.toString() + "Set Successfully", "Session Will Now Be Created");
    }
}
break;*/
            return Root.dialog_YesNoConfirmation("Confirmation", "Goals Are Missing/Not Long Enough For \n" + notgoodtext.toString(), "Set A Goal Before Playback?");
        } else {return true;}
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
        ArrayList<Meditatable> meditatableswithnoambience = new ArrayList<>();
        ArrayList<Meditatable> meditatableswithreducedambience = new ArrayList<>();
        getAllMeditatables().stream().filter(i -> i.getduration().greaterThan(Duration.ZERO)).forEach(i -> {
            Root.CreatorStatusBar.setText(String.format("Checking Ambience. Currently Checking %s...", i.name));
            if (!i.getAmbience().hasAnyAmbience()) {meditatableswithnoambience.add(i);}
            else if (!i.getAmbience().hasEnoughAmbience(i.getduration())) {meditatableswithreducedambience.add(i);}
        });
        Root.CreatorStatusBar.setText("");
        if (! meditatableswithnoambience.isEmpty()) {
            StringBuilder a = new StringBuilder();
            for (int i = 0; i < meditatableswithnoambience.size(); i++) {
                a.append(meditatableswithnoambience.get(i).name);
                if (i != meditatableswithnoambience.size() - 1) {a.append(", ");}
            }
            switch (Root.dialog_YesNoCancelConfirmation("Missing Ambience", "Missing Ambience For " + a.toString(), "Add Ambience Before Session Playback?")) {
                case YES:
                    if (meditatableswithnoambience.size() == 1) {Root.menu_openadvancedambienceeditor(meditatableswithnoambience.get(0));}
                    else { Root.menu_openadvancedambienceeditor();}
                    break;
                case NO:
                    break;
                case CANCEL:
                    ambiencecheckbox.setSelected(false);
                    break;
            }
        } else if (! meditatableswithreducedambience.isEmpty()) {
                StringBuilder a = new StringBuilder();
                int count = 0;
                for (int i = 0; i < meditatableswithreducedambience.size(); i++) {
                    a.append("\n");
                    Meditatable thismeditatable = meditatableswithreducedambience.get(i);
                    String formattedcurrentduration = Util.formatdurationtoStringSpelledOut(thismeditatable.getAmbience().gettotalActualDuration(), null);
                    String formattedexpectedduration = Util.formatdurationtoStringSpelledOut(thismeditatable.getduration(), null);
                    a.append(count + 1).append(". ").append(thismeditatable.name).append(" >  Current: ").append(formattedcurrentduration).append(" | Needed: ").append(formattedexpectedduration);
                    count++;
                }
                new SelectAmbiencePlaybackType(count).showAndWait();
                if (ambiencePlaybackType == null) {ambiencecheckbox.setSelected(false);}
//                    if (meditatableswithreducedambience.size() == 1) {
//                        ambiencecheckbox.setSelected(Root.dialog_YesNoConfirmation("Confirmation", String.format("The Following Meditatable's Ambience Isn't Long Enough: %s ", a.toString()), "Shuffle And Loop Ambience For This Meditatables?"));
//                    } else {
//                        ambiencecheckbox.setSelected(Root.dialog_YesNoConfirmation("Confirmation", String.format("The Following Cuts' Ambience Aren't Long Enough: %s ", a.toString()), "Shuffle And Loop Ambience For These Meditatables?"));
//                    }
        } else {
            ambiencecheckbox.setSelected(true);
            ambiencePlaybackType = Root.getOptions().getSessionOptions().getAmbiencePlaybackType();
        }
    }
    public boolean creation_checkreferencefiles(boolean enableprompt) {
        int invalidmeditatablecount = 0;
        for (Meditatable i : getallitemsinSession()) {
            if (!i.reference_filevalid(referenceType)) invalidmeditatablecount++;
        }
        if (invalidmeditatablecount > 0 && enableprompt) {
            return Root.dialog_YesNoConfirmation("Confirmation", "There Are " + invalidmeditatablecount + " Meditatables With Empty/Invalid Reference Files", "Enable Reference Anyways?");
        } else {return invalidmeditatablecount == 0;}
    }
    public void creation_reset(boolean setvaluetozero) {
        if (itemsinsession != null) {itemsinsession.clear();}
        creatorState = CreatorState.NOT_CREATED;
        for (Meditatable i : getAllMeditatables()) {i.creation_reset(setvaluetozero);}
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
//            if (exporterservice.getValue()) {Util.dialog_Information("Information", "Export Succeeded", "File Saved To: ");}
//            else {Util.dialog_Error("Error", "Errors Occured During Export", "Please Try Again Or Contact Me For Support");}
//            exportingSessionDialog.close();
//        });
//        exporterservice.setOnFailed(event -> {
//            String v = exporterservice.getException().getMessage();
//            Util.dialog_Error("Error", "Errors Occured While Trying To Create The This_Session. The Main Exception I Encoured Was " + v,
//                    "Please Try Again Or Contact Me For Support");
//            This_Session.exporter_deleteprevioussession();
//            exportingSessionDialog.close();
//        });
//        exporterservice.setOnCancelled(event -> {
//            Util.dialog_Information("Cancelled", "Export Cancelled", "You Cancelled Export");
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
//            if (Util.dialog_YesNoConfirmation(Root, "Confirmation", "Invalid Audio File Extension", "Save As .mp3?")) {
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
                MeditatableswithGoalsCompletedThisSession = new ArrayList<>();
                totalsessiondurationelapsed = Duration.ZERO;
                totalsessionduration = Duration.ZERO;
                for (Meditatable i : itemsinsession) {totalsessionduration = totalsessionduration.add(i.getduration());}
                playerUI.TotalTotalTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessionduration));
                updateuitimeline = new Timeline(new KeyFrame(Duration.millis(100), ae -> player_updateui()));
                updateuitimeline.setCycleCount(Animation.INDEFINITE);
                updateuitimeline.play();
                meditatablecount = 0;
                currentmeditatable = itemsinsession.get(meditatablecount);
                Root.getSessions().createnew();
                currententrainmentvolume = Root.getOptions().getSessionOptions().getEntrainmentvolume();
                currentambiencevolume = Root.getOptions().getSessionOptions().getAmbiencevolume();
                currentmeditatable.start();
                break;
            case PAUSED:
                updateuitimeline.play();
                currentmeditatable.resume();
                break;
        }
    }
    public void player_pause() {
        if (playerState == PlayerState.PLAYING) {
            currentmeditatable.pause();
            updateuitimeline.pause();
        }
    }
    public void player_stop() {
        try {
            currentmeditatable.stop();
            updateuitimeline.stop();
        } catch (NullPointerException ignored) {}
        player_reset();
    }
    public void player_updateui() {
        try {
            totalsessiondurationelapsed = totalsessiondurationelapsed.add(Duration.millis(100));
            try {currentmeditatable.elapsedtime = currentmeditatable.elapsedtime.add(Duration.millis(100));} catch (NullPointerException ignored) {}
            Float currentprogress;
            Float totalprogress;
            try {
                if (currentmeditatable.elapsedtime.greaterThan(Duration.ZERO)) {currentprogress = (float) currentmeditatable.elapsedtime.toMillis() / (float) currentmeditatable.getduration().toMillis();}
                else {currentprogress = (float) 0;}
            } catch (NullPointerException ignored) {currentprogress = (float) 0;}
            if (totalsessiondurationelapsed.greaterThan(Duration.ZERO)) {
                totalprogress = (float) totalsessiondurationelapsed.toMillis()
                        / (float) totalsessionduration.toMillis();}
            else {totalprogress = (float) 0.0;}
            playerUI.CurrentMeditatableProgress.setProgress(currentprogress);
            playerUI.TotalProgress.setProgress(totalprogress);
            currentprogress *= 100;
            totalprogress *= 100;
            playerUI.CurrentMeditatableTopLabel.setText(String.format("%s (%d", currentmeditatable.name, currentprogress.intValue()) + "%)");
            playerUI.TotalSessionLabel.setText(String.format("Session (%d", totalprogress.intValue()) + "%)");
            try {playerUI.MeditatableCurrentTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(currentmeditatable.elapsedtime));}
            catch (NullPointerException ignored) {playerUI.MeditatableCurrentTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(Duration.ZERO));}
            playerUI.TotalCurrentTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessiondurationelapsed));
            boolean displaynormaltime = playerUI.displaynormaltime;
            if (displaynormaltime) {playerUI.MeditatableTotalTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(currentmeditatable.getduration()));}
            else {playerUI.MeditatableTotalTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(currentmeditatable.getduration().subtract(currentmeditatable.elapsedtime)));}
            if (displaynormaltime) {playerUI.TotalTotalTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessionduration));}
            else {playerUI.TotalTotalTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessionduration.subtract(totalsessiondurationelapsed)));}
            try {
                if (displayReference != null && displayReference.isShowing()) {
                    displayReference.CurrentProgress.setProgress(currentprogress / 100);
                    displayReference.CurrentPercentage.setText(currentprogress.intValue() + "%");
                    displayReference.TotalProgress.setProgress(totalprogress / 100);
                    displayReference.TotalPercentage.setText(totalprogress.intValue() + "%");
                    displayReference.CurrentName.setText(currentmeditatable.name);
                }
            } catch (NullPointerException ignored) {}
            Root.goals_gui_updateui();
            Root.sessions_gui_updateui();
            currentmeditatable.tick();
        } catch (Exception ignored) {}
    }
    public void player_progresstonextmeditatable() {
        try {
            switch (playerState) {
                case TRANSITIONING:
                    try {
                        currentmeditatable.goals_transitioncheck();
                        currentmeditatable.cleanupPlayersandAnimations();
                        meditatablecount++;
                        currentmeditatable = getallitemsinSession().get(meditatablecount);
                        currentmeditatable.start();
                    } catch (IndexOutOfBoundsException ignored) {
                        playerState = PlayerState.IDLE;
                        currentmeditatable.cleanupPlayersandAnimations();
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
        playerUI.CurrentMeditatableTopLabel.setText(currentmeditatable.name + " Completed");
        playerUI.TotalSessionLabel.setText("Session Completed");
        updateuitimeline.stop();
        playerState = PlayerState.STOPPED;
        Root.getSessions().deletenonvalidsessions();
        // TODO Some Animation Is Still Running At End Of Session. Find It And Stop It Then Change Session Finsished Dialog To Showandwait
        Root.session_gui_opensessiondetailsdialog();
        // TODO Prompt For Export
//        if (Util.dialog_YesNoConfirmation(Root, "Confirmation", "Session Completed", "Export This Session For Later Use?")) {
//            exporter_getsessionexporter();}
        Root.sessions_gui_updateui();
        Root.goals_gui_updateui();
        player_reset();
    }
    public void player_reset() {
        updateuitimeline = null;
        Root.getSessions().deletenonvalidsessions();
        meditatablecount = 0;
        totalsessiondurationelapsed = Duration.ZERO;
        totalsessionduration = Duration.ZERO;
        playerUI.reset();
    }
    public void player_transition() {
        Session currentsession =  Root.getSessions().sessioninformation_getspecificsession( Root.getSessions().sessioninformation_totalsessioncount() - 1);
        currentsession.updatemeditatableduration(currentmeditatable.number, new Double(currentmeditatable.getduration().toMinutes()).intValue());
        Root.getSessions().marshall();
        Root.goals_gui_updateui();
        currentmeditatable.stop();
        if (Root.getOptions().getSessionOptions().getAlertfunction()) {
            Media alertmedia = new Media(Root.getOptions().getSessionOptions().getAlertfilelocation());
            MediaPlayer alertplayer = new MediaPlayer(alertmedia);
            alertplayer.play();
            playerState = PlayerState.TRANSITIONING;
            alertplayer.setOnEndOfMedia(() -> {
                alertplayer.stop();
                alertplayer.dispose();
                player_progresstonextmeditatable();
            });
            alertplayer.setOnError(() -> {
                if (Root.dialog_YesNoConfirmation("Confirmation", "An Error Occured While Playing Alert File" +
                        alertplayer.getMedia().getSource() + "'", "Retry Playing Alert File? (Pressing Cancel " +
                        "Will Progress To The Next Cut)")) {
                    alertplayer.stop();
                    alertplayer.play();
                } else {
                    alertplayer.stop();
                    alertplayer.dispose();
                    player_progresstonextmeditatable();
                }
            });
        } else {
            playerState = PlayerState.TRANSITIONING;
            player_progresstonextmeditatable();
        }
    }
    public void player_error() {

    }
    public boolean player_endsessionprematurely() {
        if (playerState == PlayerState.PLAYING || playerState == PlayerState.PAUSED || playerState == PlayerState.TRANSITIONING) {
            player_pause();
            if (Root.dialog_YesNoConfirmation("End Session Early", "End Session Prematurely?", "Really End Session Prematurely")) {return true;}
            else {player_play(); return false;}
        } else {return true;}
    }
    public void player_togglevolumebinding() {
        if (playerState == PlayerState.IDLE || playerState == PlayerState.STOPPED) {
            currentmeditatable.volume_rebindentrainment();
            if (Root.AmbienceSwitch.isSelected()) {
                currentmeditatable.volume_rebindambience();}
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
                currentmeditatable.volume_rebindentrainment();
                if (Root.AmbienceSwitch.isSelected()) {
                    currentmeditatable.volume_rebindambience();
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
    public class CutsMissingDialog extends Stage {
        public Button AddMissingMeditatablesButton;
        public ListView<Text> SessionListView;
        public Button CreateAnywayButton;
        public Button CancelCreationButton;
        private List<Cut> allcuts;
        private List<Cut> missingmeditatables;
        private Util.AnswerType result;
        private MainController Root;

        public CutsMissingDialog(MainController root, List<Cut> allcuts) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/CutsOutOfOrderOrMissing.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                this.setOnCloseRequest(event -> dialogclosed());
            } catch (IOException e) {
    //            new MainController.ExceptionDialog(Root, e).showAndWait();
            }
            setTitle("Meditatables Missing");
            this.allcuts = allcuts;
            populatelistview();
            Root.dialog_Information("Cuts Missing", "Each Cut Should Connect From RIN Up", "Use This Dialog To Connect Cuts");
        }

        public int getlastworkingcutindex() {
            int lastcutindex = 0;
            for (Cut i : allcuts) {
                if (i.getduration().greaterThan(Duration.ZERO)) {lastcutindex = i.number;}
            }
            return lastcutindex;
        }
        public void populatelistview() {
            ObservableList<Text> sessionitems = FXCollections.observableArrayList();
            for (int i=0; i<getlastworkingcutindex(); i++) {
                Text item = new Text();
                StringBuilder currentcuttext = new StringBuilder();
                Cut selectedcut = allcuts.get(i);
                currentcuttext.append(selectedcut.number).append(". ").append(selectedcut.name);
                if (selectedcut.getduration().greaterThan(Duration.ZERO)) {
                    currentcuttext.append(" (").append(Util.formatdurationtoStringSpelledOut(selectedcut.getduration(), SessionListView.getLayoutBounds().getWidth() - (currentcuttext.length() + 1)));
                    currentcuttext.append(")");
                } else {
                    if (missingmeditatables == null) {
                        missingmeditatables = new ArrayList<>();}
                    missingmeditatables.add(selectedcut);
                    currentcuttext.append(" (Missing Value!)");
                    item.setStyle("-fx-font-weight:bold; -fx-font-style: italic;");
                }
                item.setText(currentcuttext.toString());
                sessionitems.add(item);
            }
            SessionListView.setItems(sessionitems);
        }
        public void addmissingcutstoSession(Event event) {
            if (missingmeditatables != null && missingmeditatables.size() > 0) {
                MeditatableInvocationDialog meditatabledurationdialog = new MeditatableInvocationDialog();
                meditatabledurationdialog.showAndWait();
                missingmeditatables.stream().filter(i -> meditatabledurationdialog.getDuration() != 0).forEach(i -> {
                    i.changevalue(meditatabledurationdialog.getDuration());
                });
            }
            setResult(Util.AnswerType.YES);
            this.close();
        }
        public void createSessionwithoutmissingmeditatables(Event event) {
            if (Root.dialog_YesNoConfirmation("Confirmation", "Session Not Well-Formed", "Really Create Anyway?")) {
                setResult(Util.AnswerType.YES);
                this.close();
            }
        }
        public void dialogclosed() {
            if (result == null && Root.dialog_YesNoConfirmation("Confirmation", "Close Dialog Without Creating", "This Will Return To The Creator")) {
                setResult(Util.AnswerType.CANCEL);
                this.close();
            }
        }
        public Util.AnswerType getResult() {
            return result;
        }
        public void setResult(Util.AnswerType result) {
            this.result = result;
        }
        public void cancelcreation(ActionEvent actionEvent) {
            setResult(Util.AnswerType.CANCEL);
            this.close();
        }
    }
    public class SortSessionItems extends Stage {
        public TableView<SessionItem> SessionItemsTable;
        public TableColumn<SessionItem, Integer> NumberColumn;
        public TableColumn<SessionItem, String> NameColumn;
        public TableColumn<SessionItem, String> DurationColumn;
        public Button UpButton;
        public Button DownButton;
        public Button AcceptButton;
        public Button CancelButton;
        private List<Meditatable> sessionitems;
        private ObservableList<SessionItem> tableitems;
        private MainController Root;
        private Util.AnswerType result;

        public SortSessionItems(MainController Root, List<Meditatable> sessionitems) {
            this.sessionitems = sessionitems;
            this.Root = Root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SortSessionParts.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                this.setOnCloseRequest(event -> dialogClosed());
            } catch (IOException ignored) {}
            NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
            NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
            DurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
            SessionItemsTable.setOnMouseClicked(event -> itemselected());
            tableitems = FXCollections.observableArrayList();
            UpButton.setDisable(true);
            DownButton.setDisable(true);
            populatetable();
        }

        public void itemselected() {
            int index = SessionItemsTable.getSelectionModel().getSelectedIndex();
            boolean validitemselected = index != -1;
            UpButton.setDisable(! validitemselected && index == 0);
            DownButton.setDisable(! validitemselected && index != SessionItemsTable.getItems().size() - 1);
        }
        public void populatetable() {
            SessionItemsTable.getItems().clear();
            tableitems.clear();
            int count = 1;
            for (Meditatable i : sessionitems) {
                tableitems.add(new SessionItem(count, i.name, Util.formatdurationtoStringDecimalWithColons(i.getduration())));
                count++;
            }
            SessionItemsTable.setItems(tableitems);
        }
        public void moveitemup(ActionEvent actionEvent) {
            int selectedindex = SessionItemsTable.getSelectionModel().getSelectedIndex();
            if (selectedindex == -1) {return;}
            if (tableitems.get(selectedindex).name.get().equals("Presession") || tableitems.get(selectedindex).name.get().equals("Postsession")) {
                Root.dialog_Information("Information", "Cannot Move", "Presession And Postsession Cannot Be Moved");
                return;
            }
            if (selectedindex == 0) {return;}
            Meditatable selecteditem = sessionitems.get(selectedindex);
            Meditatable oneitemup = sessionitems.get(selectedindex - 1);
            if (selecteditem instanceof Cut && oneitemup instanceof Cut) {
                if (selecteditem.number > oneitemup.number) {
                    Root.dialog_Information("Cannot Move", selecteditem.name + " Cannot Be Moved Before " + oneitemup.name + ". Cuts Would Be Out Of Order", "Cannot Move");
                    return;
                }
            }
            if (oneitemup instanceof Qi_Gong) {
                Root.dialog_Information("Cannot Move", "Cannot Replace Presession", "Cannot Move");
                return;
            }
            Collections.swap(sessionitems, selectedindex, selectedindex - 1);
            populatetable();
        }
        public void moveitemdown(ActionEvent actionEvent) {
            int selectedindex = SessionItemsTable.getSelectionModel().getSelectedIndex();
            if (selectedindex == -1) {return;}
            if (tableitems.get(selectedindex).name.get().equals("Presession") || tableitems.get(selectedindex).name.get().equals("Postsession")) {
                Root.dialog_Information("Information", "Cannot Move", "Presession And Postsession Cannot Be Moved");
                return;
            }
            if (selectedindex == tableitems.size() - 1) {return;}
            Meditatable selecteditem = sessionitems.get(selectedindex);
            Meditatable oneitemdown = sessionitems.get(selectedindex + 1);
            if (selecteditem instanceof Cut && oneitemdown instanceof Cut) {
                if (selecteditem.number < oneitemdown.number) {
                    Root.dialog_Information("Cannot Move", selecteditem.name + " Cannot Be Moved After " + oneitemdown.name + ". Cuts Would Be Out Of Order", "Cannot Move");
                    return;
                }
            }
            if (oneitemdown instanceof Qi_Gong) {
                Root.dialog_Information("Cannot Move", "Cannot Replace Postsession", "Cannot Move");
                return;
            }
            Collections.swap(sessionitems, selectedindex, selectedindex + 1);
            populatetable();
        }
        public void cutcheck() {

        }
        public List<Meditatable> getorderedsessionitems() {
            return sessionitems;
        }
        public void accept(ActionEvent actionEvent) {
            close();
        }
        public void cancel(ActionEvent actionEvent) {
            sessionitems = null;
            close();
        }
        public void dialogClosed() {
            if (Root.dialog_YesNoConfirmation("Cancel Creation", "Cancel Creation", "This Will Return To The Creator Main Window")) {
                setResult(Util.AnswerType.CANCEL);
                this.close();
            }
        }

        public Util.AnswerType getResult() {
            return result;
        }
        public void setResult(Util.AnswerType result) {
            this.result = result;
        }

        class SessionItem {
            private IntegerProperty number;
            private StringProperty name;
            private StringProperty duration;

            public SessionItem(int number, String name, String duration) {
                this.number = new SimpleIntegerProperty(number);
                this.name = new SimpleStringProperty(name);
                this.duration = new SimpleStringProperty(duration);
            }
        }
    }
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
        public Label CurrentMeditatableTopLabel;
        public Label MeditatableCurrentTimeLabel;
        public ProgressBar CurrentMeditatableProgress;
        public Label MeditatableTotalTimeLabel;
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
                reset();
                boolean referenceoption = Root.getOptions().getSessionOptions().getReferenceoption();
                if (referenceoption && referenceType != null && creation_checkreferencefiles(false)) {ReferenceToggleButton.setSelected(true);}
                else {ReferenceToggleButton.setSelected(false);}
                togglereference(null);
                ReferenceToggleButton.setSelected(Root.getOptions().getSessionOptions().getReferenceoption());
                setResizable(false);
                MeditatableTotalTimeLabel.setOnMouseClicked(event -> displaynormaltime = !displaynormaltime);
                TotalTotalTimeLabel.setOnMouseClicked(event -> displaynormaltime = !displaynormaltime);
                setOnCloseRequest(event -> {
                    if (playerState == PlayerState.PLAYING || playerState == PlayerState.STOPPED || playerState == PlayerState.PAUSED || playerState == PlayerState.IDLE) {
                        if (player_endsessionprematurely()) {close(); cleanupPlayer();} else {play(); event.consume();}
                    } else {
                        Util.gui_showtimedmessageonlabel(StatusBar, "Cannot Close Player During Fade Animation", 400);
                        new Timeline(new KeyFrame(Duration.millis(400), ae -> currentmeditatable.toggleplayerbuttons()));
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
        public void reset() {
            MeditatableCurrentTimeLabel.setText("--:--");
            CurrentMeditatableProgress.setProgress(0.0);
            MeditatableTotalTimeLabel.setText("--:--");
            TotalCurrentTimeLabel.setText("--:--");
            TotalProgress.setProgress(0.0);
            TotalTotalTimeLabel.setText("--:--");
            EntrainmentVolume.setDisable(true);
            EntrainmentVolumePercentage.setText("0%");
            AmbienceVolume.setDisable(true);
            AmbienceVolumePercentage.setText("0%");
            // TODO Reset Goal UI Here
            PlayButton.setText("Start");
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
                setTitle(currentmeditatable.name + "'s Reference");
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
                            // TODO Closing Reference Display On Escape Is Crashing The Whole App
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
            File referencefile = currentmeditatable.reference_getFile();
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
    public class MeditatableInvocationDialog extends Stage {
        public Button CancelButton;
        public Button OKButton;
        public TextField MinutesTextField;
        private int duration;

        public MeditatableInvocationDialog() {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/MeditatableInvocationDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                options.setStyle(this);
                this.setResizable(false);
            } catch (IOException ignored) {}
            setTitle("Meditatable Invocation");
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
                    if (Root.dialog_YesNoConfirmation("Confirmation", "Cut Invocation Value Is 0", "Continue With Zero Value (These Cuts Won't Be Included)" )) {
                        setDuration(0);
                        this.close();
                    }
                }
            } catch (NumberFormatException e) {
                Root.dialog_Error("Error", "Value Is Empty", "Enter A Numeric Value Then Press OK");}
        }
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

        public SelectAmbiencePlaybackType(int meditatableswithoutsufficientambience) {
            ambiencePlaybackType = null;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/AmbiencePlaybackType.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                options.setStyle(this);
                setResizable(false);
                TopLabel.setText("Ambience Is Not Long Enough For " + meditatableswithoutsufficientambience +  " Meditatables");
                setOnCloseRequest(event -> {
                    if (ambiencePlaybackType == null) {
                        if (! Root.dialog_YesNoConfirmation("Disable Ambience", "No Ambience Playback Type Selected", "Disable Ambience?")) {event.consume();}
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