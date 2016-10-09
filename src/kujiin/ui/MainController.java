package kujiin.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.ui.dialogs.*;
import kujiin.util.*;
import kujiin.util.enums.ProgramState;
import kujiin.util.enums.ReferenceType;
import kujiin.util.enums.StartupCheckType;
import kujiin.xml.Ambiences;
import kujiin.xml.Entrainments;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
// TODO Bugs To Fix
    // TODO Entrainment Always Calculates Duration Instead Of Just Using Previously Calculated XML Values
    // TODO Ambience Shuffle Algorithm Doesn't Add Last Actual Ambience File
    // TODO Session Playback Overview Set Dynamic Text Colors (It's All Fucked Up)

    // TODO Preferences Dialog Doesn't Initially Populate With Options From XML (Check If It Saves As Well?)
    // TODO Find Out Why Displaying Some Dialogs Makes Root Uniconified
    // TODO Closing Reference Display With 'ESC' Is Crashing The Whole App

// TODO Test

// TODO Additional Features To Definitely Add
    // TODO Create A Custom Ambience Selection Wizard To Add Ambience Individually To Each Session Part In Session
    // TODO Create Goal Progress Similar To Session Details And Add To Session Details Dialog
    // TODO Exporter

// TODO Optional Additional Features
    // TODO Refactor Freq Files So There Can Be 2 or 3 Different Frequency Octaves For The Same Session Part (Use enum FreqType)
    // TODO Display Short Cut Descriptions (Power/Responsibility... On The Player Widget While Playing)
    // TODO Add Tooltips To Cuts Saying A One Word Brief Summary (Rin -> Strength, Kyo -> Control, Toh->Harmony)
    // TODO Put Add A Japanese Character Symbol Picture (Representing Each Cut) To Creator Cut Labels (With Tooltips Displaying Names)
    // TODO Set Font Size, So The Program Looks Universal And Text Isn't Oversized Cross-Platform

// TODO Mind Workstation
    // TODO Add Low (And Possibly Medium) Variations Of All Session Parts
    // TODO Add Ramps To Connect Low (And Possibly Medium) Variations Of Session Parts With Each Other

public class MainController implements Initializable {
    public Label CreatorStatusBar;
    public Label PlayerStatusBar;
    public Button ExportButton;
    public Button PlayButton;
    public Button ListOfSessionsButton;
    public ProgressBar goalsprogressbar;
    public Button newgoalButton;
    public Button viewcurrrentgoalsButton;
    public TextField AverageSessionDuration;
    public TextField TotalTimePracticed;
    public TextField NumberOfSessionsPracticed;
    public CheckBox PrePostSwitch;
    public Button LoadPresetButton;
    public Button SavePresetButton;
    public TextField ApproximateEndTime;
    public Button ChangeAllCutsButton;
    public TextField TotalSessionTime;
    public ComboBox<String> GoalSessionPartComboBox;
    public Label GoalTopLabel;
    public Label LengthLabel;
    public Label CompletionLabel;
    public Label GoalStatusBar;
    public ToggleButton RinSwitch;
    public ToggleButton KyoSwitch;
    public ToggleButton TohSwitch;
    public ToggleButton ShaSwitch;
    public ToggleButton KaiSwitch;
    public ToggleButton JinSwitch;
    public ToggleButton RetsuSwitch;
    public ToggleButton ZaiSwitch;
    public ToggleButton ZenSwitch;
    public ToggleButton EarthSwitch;
    public ToggleButton AirSwitch;
    public ToggleButton FireSwitch;
    public ToggleButton WaterSwitch;
    public ToggleButton VoidSwitch;
    public TextField PreTime;
    public TextField RinTime;
    public TextField KyoTime;
    public TextField TohTime;
    public TextField ShaTime;
    public TextField KaiTime;
    public TextField JinTime;
    public TextField RetsuTime;
    public TextField ZaiTime;
    public TextField ZenTime;
    public TextField PostTime;
    public TextField EarthTime;
    public TextField AirTime;
    public TextField FireTime;
    public TextField WaterTime;
    public TextField VoidTime;
    public ToggleButton PreSwitch;
    public ToggleButton PostSwitch;
    public Button ChangeAllElementsButton;
    public Button ResetCreatorButton;
    public Label GoalProgressPercentageLabel;
    private SoundFile ambiencetestsoundfile;
    private List<SoundFile> ambienceplaybackhistory = new ArrayList<>();
    private SessionCreator sessionCreator;
    private ProgressTracker progressTracker;
    private Scene Scene;
    private Stage Stage;
    private StartupChecks startupChecks;
    private ProgramState programState = ProgramState.IDLE;

    // My Fields
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
    private Options Options;
    private Entrainments Entrainments;
    private Ambiences Ambiences;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setOptions(new Options(this));
        getOptions().unmarshall();
    }
    public void setupSessionParts() {
        Presession =  new Qi_Gong(0, "Presession", this, PreSwitch, PreTime);
        Presession.setSummary("Gather Qi (Life Energy) Before The Session Starts");
        Presession.setToolTip();
        Rin = new Cut(1, "RIN", this, RinSwitch, RinTime);
        Rin.setFocusPoint("Root Chakra");
        Rin.setConcept("A Celebration Of The Spirit Coming Into The Body");
        Rin.setMantra_Meaning("All/Everything/Vast As It Is Defined Now");
        Rin.setSide_Effects("Increases The Bioelectric Output Of The Body");
        Rin.setToolTip();
        Kyo = new Cut(2, "KYO", this, KyoSwitch, KyoTime);
        Kyo.setFocusPoint("Navel Chakra");
        Kyo.setConcept("In Order To Become Powerful, Responsiblity Must Be Taken For All Actions");
        Kyo.setMantra_Meaning("Use Your Tools/Manage Yourself Correctly");
        Kyo.setSide_Effects("Increases The Healthy Flow Of Energy Leading To The Mastery Of The Control And Direction Of Energy");
        Kyo.setToolTip();
        Toh = new Cut(3, "TOH", this, TohSwitch, TohTime);
        Toh.setFocusPoint("Dan-tian");
        Toh.setConcept("Conscious Dissolement Of All Personal Fights In Order To Achieve Harmony");
        Toh.setMantra_Meaning("Conquering Limiting Beliefs/Doubts Will Allow You To Get The Treasures Of Life");
        Toh.setSide_Effects("Enhances Your Positive Relationship With The Universe, Resulting In Improved Harmony And Balance");
        Toh.setToolTip();
        Sha = new Cut(4, "SHA", this, ShaSwitch, ShaTime);
        Sha.setFocusPoint("Solar Plexus Charkra");
        Sha.setConcept("By Letting Go Of The Limits Of My Mind You Can Vibrate With The Power Of The Universe And Exist Fully Powerful");
        Sha.setMantra_Meaning("Grounded, I Understand The Power That I Express");
        Sha.setSide_Effects("Increases The Healing Ability Of The Body As A Result Of Higher Energy Levels Passing Through The Body");
        Sha.setToolTip();
        Kai = new Cut(5, "KAI", this, KaiSwitch, KaiTime);
        Kai.setFocusPoint("Heart Chakra");
        Kai.setConcept("Everything (Created Or Not) In The Universe Is One");
        Kai.setMantra_Meaning("I Acknowledge The All Pervading Conscious State of Things As They Are, And I Live It [I Am Conscious Of EVERYTHING]");
        Kai.setSide_Effects("Develops Foreknowledge, Premonition, Intuition And Feeling By Acknowlegding That Everything Is One");
        Kai.setToolTip();
        Jin = new Cut(6, "JIN", this, JinSwitch, JinTime);
        Jin.setFocusPoint("Throat Chakra");
        Jin.setConcept("An Observation Of The Universe And What Binds Every Part Of Us To Every Part Of Everything Else");
        Jin.setMantra_Meaning("Conscious Experience Of The Fire That Everything Is Really Made Of");
        Jin.setToolTip();
        Retsu = new Cut(7, "RETSU", this, RetsuSwitch, RetsuTime);
        Retsu.setFocusPoint("Jade Gate Chakra");
        Retsu.setConcept("Transmute The Limits Of Perception By Remembering Our Wholeness As Spirit");
        Retsu.setMantra_Meaning("Everything Flows/Is Elevated To The Divine");
        Retsu.setSide_Effects("Enhances Your Perception And Mastery Of Space-Time Dimensions");
        Retsu.setToolTip();
        Zai = new Cut(8, "ZAI", this, ZaiSwitch, ZaiTime);
        Zai.setFocusPoint("Third Eye Chakra");
        Zai.setConcept("Works With Our Mind, Heart And Body In Order To Define Ourselves As A Spirit That Is Having A Human Experience, Rather Than A Human Being Sometimes Having A Spiritual Experience");
        Zai.setMantra_Meaning("Everything Is Manifested In The Correct Way According To The Experience That I Live");
        Zai.setSide_Effects("Increases My Power Of Manifestation By Fostering A Relationship With The Elements Of Creation");
        Zai.setToolTip();
        Zen = new Cut(9, "ZEN", this, ZenSwitch, ZenTime);
        Zen.setFocusPoint("Crown Chakra");
        Zen.setConcept("The Human Completely Relents Itself To The Spirit With Only The Consciousness Aspect Of The Human Remaining Active");
        Zen.setMantra_Meaning("I am the void and the light");
        Zen.setSide_Effects("Completely Relenting Into Spirit Results In Englightenment, Completeness, Suggestive Invisibility");
        Zen.setToolTip();
        Earth = new Element(10, "Earth", this, EarthSwitch, EarthTime);
        Air = new Element(11, "Air", this, AirSwitch, AirTime);
        Fire = new Element(12, "Fire", this, FireSwitch, FireTime);
        Water = new Element(13, "Water", this, WaterSwitch, WaterTime);
        Void = new Element(14, "Void", this, VoidSwitch, VoidTime);
        Postsession = new Qi_Gong(15, "Postsession", this, PostSwitch, PostTime);
        Postsession.setSummary("Gather Qi (Life Energy) Before The Session Starts");
        Postsession.setToolTip();
        Total = new Total(16, "Total", this, null, null);
        for (SessionPart i : getAllSessionParts(false)) {
            i.setGoalsController(getProgressTracker().getGoals());
            i.goals_unmarshall();
        }
        getProgressTracker().setSessionParts(getAllSessionParts(true));
    }
    public boolean cleanup() {
        Ambiences.marshall();
        Entrainments.marshall();
        Options.marshall();
        return sessionCreator.cleanup() && progressTracker.cleanup();
    }
    public void close() {
        if (cleanup()) {
            System.exit(0);
        }
    }

// Getters And Setters
    public void setSessionCreator(SessionCreator sessionCreator) {
        this.sessionCreator = sessionCreator;
    }
    public void setProgressTracker(ProgressTracker progressTracker) {
        this.progressTracker = progressTracker;
    }
    public SessionCreator getSessionCreator() {
        return sessionCreator;
    }
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }
    public ArrayList<SessionPart> getAllSessionParts(boolean includetotal) {
        ArrayList<SessionPart> sessionparts = new ArrayList<>();
        sessionparts.addAll(Arrays.asList(Presession, Rin, Kyo, Toh, Sha, Kai, Jin, Retsu, Zai, Zen,
                Earth, Air, Fire, Water, Void, Postsession));
        if (includetotal) {sessionparts.add(Total);}
        return sessionparts;
    }
    public ArrayList<Cut> getAllCuts() {
        return getAllSessionParts(false).stream().filter(i -> i instanceof Cut).map(i -> (Cut) i).collect(Collectors.toCollection(ArrayList::new));
    }
    public static List<String> getallCutNames() {
        return getAllSessionPartsNames(false).subList(1, 10);
    }
    public ArrayList<Element> getAllElements() {
        return getAllSessionParts(false).stream().filter(i -> i instanceof Element).map(i -> (Element) i).collect(Collectors.toCollection(ArrayList::new));
    }
    public static ArrayList<String> getAllSessionPartsNames(boolean includetotal) {
        ArrayList<String> sessionpartnames = new ArrayList<>();
        sessionpartnames.addAll(Arrays.asList("Presession", "RIN", "KYO", "TOH", "SHA", "KAI",
                "JIN", "RETSU", "ZAI", "ZEN", "Earth", "Air", "Fire", "Water", "Void", "Postsession"));
        if (includetotal) {sessionpartnames.add("Total");}
        return sessionpartnames;
    }
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
    public ProgramState getProgramState() {
        return programState;
    }
    public Entrainments getEntrainments() {
        return Entrainments;
    }
    public void setEntrainments(kujiin.xml.Entrainments entrainments) {
        Entrainments = entrainments;
    }
    public void setAmbiences(kujiin.xml.Ambiences ambiences) {
        Ambiences = ambiences;
    }
    public Ambiences getAmbiences() {
        return Ambiences;
    }
    public Options getOptions() {
        return Options;
    }
    public void setOptions(Options options) {
        Options = options;
    }
    public javafx.scene.Scene getScene() {
        return Scene;
    }
    public void setScene(javafx.scene.Scene scene) {
        Scene = scene;
    }
    public javafx.stage.Stage getStage() {
        return Stage;
    }
    public void setStage(javafx.stage.Stage stage) {
        Stage = stage;
    }

// Menu
    public void menu_changesessionoptions(ActionEvent actionEvent) {
        new ChangeProgramOptions().showAndWait();
        Options.marshall();
        getProgressTracker().updateui_sessions();
        getProgressTracker().updateui_goals(null);
    }
    public void menu_editprogramsambience(ActionEvent actionEvent) {
        if (programState == ProgramState.IDLE) {
            new AmbienceEditor_Simple(this).showAndWait();
        } else {
            new InformationDialog(getOptions(), "Information", "Cannot Edit Ambience While Performing Startup Checks", "");
        }
    }
    public void menu_opensimpleambienceeditor() {
        new AmbienceEditor_Simple(this).showAndWait();
    }
    public void menu_opensimpleambienceeditor(SessionPart sessionPart) {
        new AmbienceEditor_Simple(this, sessionPart).showAndWait();
    }
    public void menu_openadvancedambienceeditor() {
    }
    public void menu_openadvancedambienceeditor(SessionPart sessionPart) {
        AmbienceEditor_Advanced sae = new AmbienceEditor_Advanced(this, sessionPart);
        sae.showAndWait();
    }
    public void menu_editreferencefiles(ActionEvent actionEvent) {
        new EditReferenceFiles(getOptions(), getSessionCreator().getReferenceType()).showAndWait();
    }
    public void menu_howtouseprogram(ActionEvent actionEvent) {
    }
    public void menu_aboutthisprogram(ActionEvent actionEvent) {
//        for (SessionPart x : getAllSessionParts(false)) {
//            if (x instanceof Cut || x instanceof Qi_Gong) {
//                Ambience ambience = x.getAmbience();
//                int count = 1;
//                for (int i = 0; i < 100; i++) {
//                    ambiencetestsoundfile = ambience.ambiencegenerator(AmbiencePlaybackType.SHUFFLE, ambienceplaybackhistory, ambiencetestsoundfile);
//                    ambienceplaybackhistory.add(ambiencetestsoundfile);
////                System.out.println(count + ": Sound File: " + ambiencetestsoundfile.getFile().getAbsolutePath());
//                    count++;
//                }
//            }
//        }
    }
    public void menu_contactme(ActionEvent actionEvent) {
//        PreviewFile previewFile = new PreviewFile(kujiin.xml.Options.TESTFILE, this);
//        previewFile.showAndWait();
    }

// Startup Checks
    public void startupchecks_start() {
        programState = ProgramState.STARTING_UP;
        sessionCreator.setDisable(true, "");
        startupChecks = new StartupChecks(getAllSessionParts(false));
        startupChecks.run();
        startupChecks.setOnRunning(event -> CreatorStatusBar.textProperty().bind(startupChecks.messageProperty()));
    }
    public void startupchecks_finished() {
        CreatorStatusBar.textProperty().unbind();
        sessionCreator.setDisable(false, "");
        Util.gui_showtimedmessageonlabel(CreatorStatusBar, "Startup Checks Completed", 3000);
        programState = ProgramState.IDLE;
    }
    class StartupChecks extends Task {
        private SessionPart selectedsessionpart;
        private List<SessionPart> sessionPartList;
        private int sessionpartcount = 0;
        private MediaPlayer startupcheckplayer;
        private ArrayList<SessionPart> partswithnoambience = new ArrayList<>();
        private ArrayList<SessionPart> partswithmissingentrainment = new ArrayList<>();
        private boolean firstcall = true;
        private final double[] workcount = {0, 0};
        private boolean progresstonextsessionpart = true;

        public StartupChecks(List<SessionPart> allsessionparts) {
            sessionPartList = allsessionparts;
        }

    // Getters And Setters
        public ArrayList<SessionPart> getPartswithnoambience() {
            return partswithnoambience;
        }
        public ArrayList<SessionPart> getPartswithmissingentrainment() {
            return partswithmissingentrainment;
        }

    // Method Overrides
        @Override
        protected Object call() throws Exception {
            if (firstcall) {calculatetotalworktodo(); firstcall = false;}
            if (progresstonextsessionpart) {
                try {
                    if (selectedsessionpart == null) {selectedsessionpart = sessionPartList.get(sessionpartcount);}
                } catch (IndexOutOfBoundsException e) {
                    // End Of Startup Checks
                    getEntrainments().marshall();
                    getAmbiences().marshall();
                    startupchecks_finished();
                    return null;
                }
            }
            SoundFile soundFile;
            try {
                soundFile = selectedsessionpart.startup_getNext();
            }
            catch (IndexOutOfBoundsException ignored) {
                if (! selectedsessionpart.getAmbience_hasAny() && ! partswithnoambience.contains(selectedsessionpart)) {partswithnoambience.add(selectedsessionpart);}
                sessionpartcount++;
                selectedsessionpart = null;
                try {call();} catch (Exception ign) {ignored.printStackTrace();}
                return null;
            }
            if (! soundFile.isValid()) {
                startupcheckplayer = new MediaPlayer(new Media(soundFile.getFile().toURI().toString()));
                startupcheckplayer.setOnReady(() -> {
                    if (startupcheckplayer.getTotalDuration().greaterThan(Duration.ZERO)) {
                        soundFile.setDuration(startupcheckplayer.getTotalDuration().toMillis());
                        startupcheckplayer.dispose();
                        startupcheckplayer = null;
                        if (selectedsessionpart.getStartupCheckType() == StartupCheckType.ENTRAINMENT) {
                            selectedsessionpart.startup_setEntrainmentSoundFile(soundFile);
                            selectedsessionpart.startup_incremententrainmentcount();
                        } else if (selectedsessionpart.getStartupCheckType() == StartupCheckType.AMBIENCE) {
                            selectedsessionpart.startup_setAmbienceSoundFile(soundFile);
                            selectedsessionpart.startup_incrementambiencecount();
                        }
                        workcount[0]++;
                        updateProgress(workcount[0], workcount[1]);
                        updateMessage("Performing Startup Checks. Please Wait (" + new Double(getProgress() * 100).intValue() + "%)");
                        progresstonextsessionpart = true;
                        try {call();} catch (Exception ignored) {ignored.printStackTrace();}
                    } else {
                        progresstonextsessionpart = false;
                        startupcheckplayer.dispose();
                        startupcheckplayer = null;
                        try {call();} catch (Exception ignored) {ignored.printStackTrace();}
                    }
                });
            } else {progresstonextsessionpart = true; try {call();} catch (Exception ignored) {ignored.printStackTrace();}}
            return null;
        }

        protected void calculatetotalworktodo() {
            for (SessionPart i : sessionPartList) {
                workcount[1] += i.startup_entrainmentpartcount();
                if (i.getAmbience().hasAnyAmbience()) {
                    workcount[1] += i.getAmbience().getAmbience().size();
                }
            }
        }

    }

// Subclasses
    public class ChangeProgramOptions extends Stage {
        public CheckBox TooltipsCheckBox;
        public CheckBox HelpDialogsCheckBox;
        public CheckBox FadeSwitch;
        public TextField FadeInValue;
        public TextField FadeOutValue;
        public TextField EntrainmentVolumePercentage;
        public TextField AmbienceVolumePercentage;
        public ChoiceBox<String> ProgramThemeChoiceBox;
        public Button CloseButton;
        public Button DeleteAllGoalsButton;
        public Button DeleteAllSessionsProgressButton;
        public Button DefaultsButton;
        public CheckBox ReferenceSwitch;
        public CheckBox RampSwitch;
        public Button AddNewThemeButton;
        public Label ProgramOptionsStatusBar;
        public Label DescriptionBoxTopLabel;
        public TextArea DescriptionTextField;
        public CheckBox AlertFileSwitch;
        public CheckBox PrePostRamp;
        public TextField ScrollIncrement;
        private kujiin.xml.Options Options;
        private ArrayList<ItemWithDescription> descriptionitems = new ArrayList<>();

        public ChangeProgramOptions() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ChangeProgramOptions.fxml"));
                fxmlLoader.setController(this);
                Options = getOptions();
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                setResizable(false);
                setTitle("Preferences");
                setuplisteners();
                setuptooltips();
                setupdescriptions();
                populatefromxml();
                referencetoggle();
            } catch (IOException e) {new ExceptionDialog(getOptions(), e).showAndWait();}
        }

    // Setup Methods
        public void populatefromxml() {
            // Program Options
            TooltipsCheckBox.setSelected(getOptions().getProgramOptions().getTooltips());
            HelpDialogsCheckBox.setSelected(getOptions().getProgramOptions().getHelpdialogs());
            // Session & Playback Options
            if (getOptions().getSessionOptions().getAlertfunction()) {
                AlertFileSwitch.setSelected(getOptions().hasValidAlertFile());
                if (! AlertFileSwitch.isSelected()) {getOptions().getSessionOptions().setAlertfunction(false); getOptions().getSessionOptions().setAlertfilelocation(null);}
            } else {AlertFileSwitch.setSelected(false);}
            AlertFileSwitch.setOnAction(event -> alertfiletoggled());
            RampSwitch.setSelected(Options.getSessionOptions().getRampenabled());
            PrePostRamp.setSelected(Options.getSessionOptions().getPrepostrampenabled());
            FadeSwitch.setSelected(Options.getSessionOptions().getFadeenabled());
            FadeInValue.setText(String.format("%.2f", Options.getSessionOptions().getFadeinduration()));
            FadeInValue.setDisable(! FadeSwitch.isSelected());
            FadeOutValue.setText(String.format("%.2f", Options.getSessionOptions().getFadeoutduration()));
            FadeOutValue.setDisable(! FadeSwitch.isSelected());
            EntrainmentVolumePercentage.setText(String.valueOf(new Double(Options.getSessionOptions().getEntrainmentvolume() * 100).intValue()));
            AmbienceVolumePercentage.setText(String.valueOf(new Double(Options.getSessionOptions().getAmbiencevolume() * 100).intValue()));
            // Appearance Options
            populateappearancecheckbox();
        }
        public void setuptooltips() {
            TooltipsCheckBox.setTooltip(new Tooltip("Display Messages Like These When Hovering Over Program Controls"));
            HelpDialogsCheckBox.setTooltip(new Tooltip("Display Help Dialogs"));
            AlertFileSwitch.setTooltip(new Tooltip("Alert File Is A Sound File Played In Between Different Session Parts"));
            RampSwitch.setTooltip(new Tooltip("Enable A Ramp In Between Session Parts To Smooth Mental Transition"));
            FadeInValue.setTooltip(new Tooltip("Seconds To Fade In Audio Into Session Part"));
            FadeOutValue.setTooltip(new Tooltip("Seconds To Fade Out Audio Out Of Session Part"));
            EntrainmentVolumePercentage.setTooltip(new Tooltip("Default Volume Percentage For Entrainment (Changeable In Session)"));
            AmbienceVolumePercentage.setTooltip(new Tooltip("Default Volume Percentage For Ambience (Changeable In Session)"));
            DeleteAllGoalsButton.setTooltip(new Tooltip("Delete ALL Goals Past, Present And Completed (This CANNOT Be Undone)"));
            DeleteAllSessionsProgressButton.setTooltip((new Tooltip("Delete ALL Sessions Past, Present And Completed (This CANNOT Be Undone)")));
        }
        public void setuplisteners() {
            Util.custom_textfield_double(FadeInValue, 0.0, kujiin.xml.Options.FADE_VALUE_MAX_DURATION, 1, 1);
            Util.custom_textfield_double(FadeOutValue, 0.0, kujiin.xml.Options.FADE_VALUE_MAX_DURATION, 1, 1);
            Util.custom_textfield_integer(EntrainmentVolumePercentage, 1, 100, 5);
            Util.custom_textfield_integer(EntrainmentVolumePercentage, 1, 100, 5);
            ProgramThemeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectnewtheme());
            FadeSwitch.setOnAction(event -> togglefade());
            RampSwitch.setOnAction(event -> toggleramp());
            PrePostRamp.setOnAction(event -> toggleprepostramp());
            ReferenceSwitch.setOnMouseClicked(event -> referencetoggle());
            DeleteAllSessionsProgressButton.setOnAction(event -> deleteallsessions());
            DeleteAllGoalsButton.setOnAction(event -> deleteallgoals());
            AddNewThemeButton.setOnAction(event -> addnewtheme());
            DefaultsButton.setOnAction(event -> resettodefaults());
            CloseButton.setOnAction(event -> close());
        }

    // Description Box Methods
        public void setupdescriptions() {
            descriptionitems.add(new ItemWithDescription("Tool Tips Checkbox", "Display/Don't Display Description Messages When Hovering Over Program Controls"));
            descriptionitems.add(new ItemWithDescription("Help Dialogs Checkbox", "Display/Don't Display Additional Dialogs Explaining Various Features Of The Program"));
            descriptionitems.add(new ItemWithDescription("Fade Checkbox", "Fade In/Out Volume Of Each Session Part To Make For A Smoother Playback Experience"));
            descriptionitems.add(new ItemWithDescription("Fade In", "Seconds To Fade In From Silent Into Each Session Part"));
            descriptionitems.add(new ItemWithDescription("Fade Out", "Seconds To Fade Out To Silent Into Each Session Part"));
            descriptionitems.add(new ItemWithDescription("Entrainment Volume", "Default Volume Percentage For Entrainment\n(Can Be Adjusted In Session)"));
            descriptionitems.add(new ItemWithDescription("Ambience Volume", "Default Volume Percentage For Ambience\n(Can Be Adjusted In Session)"));
            descriptionitems.add(new ItemWithDescription("Alert File", "An Alert File Is An Optional Sound File Played In Between Session Elements"));
            descriptionitems.add(new ItemWithDescription("Display Reference", "Default To Display/Don't Display Reference Files During Session Playback\n(Can Be Changed In Session)"));
            descriptionitems.add(new ItemWithDescription("Ramp Checkbox", "Enable/Disable A Ramp In Session Parts To Smooth Mental Transition"));
            descriptionitems.add(new ItemWithDescription("Pre/Post Ramp Checkbox", "Add A Ramp For Pre And Post Even If They Are Not In Session"));
            descriptionitems.add(new ItemWithDescription("Delete Session Button", "This Button Will Permanently Delete All Session Progress And Reset All Cut/Elements Progress"));
            descriptionitems.add(new ItemWithDescription("Delete Goal Button", "This Button Will Permanently Delete All Current And Completed Goals"));
            descriptionitems.add(new ItemWithDescription("Appearance Selection", "List Of The Available Appearance Themes For The Program"));
            descriptionitems.add(new ItemWithDescription("Add New Theme Button", "Add A New Theme To The List Of Available Themes"));
            TooltipsCheckBox.setOnMouseEntered(event -> populatedescriptionbox(0));
            TooltipsCheckBox.setOnMouseExited(event -> cleardescription());
            HelpDialogsCheckBox.setOnMouseEntered(event -> populatedescriptionbox(1));
            HelpDialogsCheckBox.setOnMouseExited(event -> cleardescription());
            FadeSwitch.setOnMouseEntered(event -> populatedescriptionbox(2));
            FadeSwitch.setOnMouseExited(event -> cleardescription());
            FadeInValue.setOnMouseEntered(event -> populatedescriptionbox(3));
            FadeInValue.setOnMouseExited(event -> cleardescription());
            FadeOutValue.setOnMouseEntered(event -> populatedescriptionbox(4));
            FadeOutValue.setOnMouseExited(event -> cleardescription());
            EntrainmentVolumePercentage.setOnMouseEntered(event -> populatedescriptionbox(5));
            EntrainmentVolumePercentage.setOnMouseExited(event -> cleardescription());
            AmbienceVolumePercentage.setOnMouseEntered(event -> populatedescriptionbox(6));
            AmbienceVolumePercentage.setOnMouseExited(event -> cleardescription());
            AlertFileSwitch.setOnMouseEntered(event -> populatedescriptionbox(6));
            AlertFileSwitch.setOnMouseExited(event -> cleardescription());
            ReferenceSwitch.setOnMouseEntered(event -> populatedescriptionbox(8));
            ReferenceSwitch.setOnMouseExited(event -> cleardescription());
            RampSwitch.setOnMouseEntered(event -> populatedescriptionbox(9));
            RampSwitch.setOnMouseExited(event -> cleardescription());
            PrePostRamp.setOnMouseEntered(event -> populatedescriptionbox(10));
            PrePostRamp.setOnMouseExited(event -> cleardescription());
            DeleteAllSessionsProgressButton.setOnMouseEntered(event -> populatedescriptionbox(11));
            DeleteAllSessionsProgressButton.setOnMouseExited(event -> cleardescription());
            DeleteAllGoalsButton.setOnMouseEntered(event -> populatedescriptionbox(12));
            DeleteAllGoalsButton.setOnMouseExited(event -> cleardescription());
            ProgramThemeChoiceBox.setOnMouseEntered(event -> populatedescriptionbox(13));
            ProgramThemeChoiceBox.setOnMouseExited(event -> cleardescription());
            AddNewThemeButton.setOnMouseEntered(event -> populatedescriptionbox(14));
            AddNewThemeButton.setOnMouseExited(event -> cleardescription());
        }
        public void populatedescriptionbox(int index) {
            ItemWithDescription item = descriptionitems.get(index);
            DescriptionBoxTopLabel.setText(item.getName());
            DescriptionTextField.setText(item.getDescription());
        }
        public void cleardescription() {
            DescriptionBoxTopLabel.setText("Description");
            DescriptionTextField.setText("");
        }

    // Alert File Methods
        public void alertfiletoggled() {
            if (AlertFileSwitch.isSelected()) {
                SelectAlertFile selectAlertFile = new SelectAlertFile(MainController.this);
                selectAlertFile.showAndWait();
                AlertFileSwitch.setSelected(Options.getSessionOptions().getAlertfunction() && Options.hasValidAlertFile());
            }
        }

    // Reference Methods
        public void referencetoggle() {
            Options.getSessionOptions().setReferenceoption(ReferenceSwitch.isSelected());
            if (ReferenceSwitch.isSelected()) {
                SelectReferenceType selectReferenceType = new SelectReferenceType(MainController.this);
                selectReferenceType.showAndWait();
                if (selectReferenceType.getResult()) {
                    Options.getSessionOptions().setReferencetype(selectReferenceType.getReferenceType());
                    Options.getSessionOptions().setReferencefullscreen(selectReferenceType.getFullScreen());
                }
            }
        }

    // Ramp Methods
        public void toggleramp() {
            PrePostRamp.setSelected(RampSwitch.isSelected());
            Options.getSessionOptions().setRampenabled(RampSwitch.isSelected());
            if (RampSwitch.isSelected()) {toggleprepostramp();}
        }
        public void toggleprepostramp() {
            Options.getSessionOptions().setPrepostrampenabled(PrePostSwitch.isSelected());
        }

    // Fade Methods
        public void togglefade() {
            Options.getSessionOptions().setFadeenabled(FadeSwitch.isSelected());
            FadeInValue.setDisable(! FadeSwitch.isSelected());
            FadeOutValue.setDisable(! FadeSwitch.isSelected());
        }

    // Appearance Methods
        public void populateappearancecheckbox() {
            ProgramThemeChoiceBox.setItems(FXCollections.observableArrayList(getOptions().getAppearanceOptions().getThemefilenames()));
            try {
                int index = getOptions().getAppearanceOptions().getThemefiles().indexOf(getOptions().getAppearanceOptions().getThemefile());
                ProgramThemeChoiceBox.getSelectionModel().select(index);
            } catch (Exception ignored) {}
        }
        public void addnewtheme() {
            File newfile = new FileChooser().showOpenDialog(this);
            if (newfile == null) {return;}
            Options.addthemefile(newfile.getName().substring(0, newfile.getName().lastIndexOf(".")), newfile.toURI().toString());
            populateappearancecheckbox();
        }
        public void selectnewtheme() {
            int index = ProgramThemeChoiceBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                Options.getAppearanceOptions().setThemefile(getOptions().getAppearanceOptions().getThemefiles().get(index));
                getScene().getStylesheets().clear();
                getScene().getStylesheets().add(Options.getAppearanceOptions().getThemefile());
            }
        }

    // Button Actions
        public void resettodefaults() {
            if (new ConfirmationDialog(Options, "Reset To Defaults", null, "Reset All Values To Defaults? You Will Lose Any Unsaved Changes", "Reset", "Cancel").getResult()) {
                Options.resettodefaults();
                populatefromxml();
            }
        }
        public void deleteallsessions() {
            if (new ConfirmationDialog(Options, "Confirmation", null, "This Will Permanently And Irreversible Delete All Sessions Progress And Reset The Progress Tracker", "Delete?", "Cancel").getResult()) {
                if (! kujiin.xml.Options.SESSIONSXMLFILE.delete()) {new ErrorDialog(Options, "Error", "Couldn't Delete Sessions File", "Check File Permissions For: " + kujiin.xml.Options.SESSIONSXMLFILE.getAbsolutePath());}
                else {new InformationDialog(Options, "Success", null, "Successfully Delete Sessions And Reset All Progress");}
            }
        }
        public void deleteallgoals() {
            if (new ConfirmationDialog(Options, "Confirmation", null, "This Will Permanently And Irreversible Delete All Goals Completed And Current", "Delete", "Cancel").getResult()) {
                if (! kujiin.xml.Options.SESSIONSXMLFILE.delete()) {new ErrorDialog(Options, "Error", "Couldn't Delete Goals File", "Check File Permissions For: " + kujiin.xml.Options.GOALSXMLFILE.getAbsolutePath());}
                else {new InformationDialog(Options, "Success", null, "Successfully Deleted All Goals");}
            }
        }

        @Override
        public void close() {
            Options.getProgramOptions().setTooltips(TooltipsCheckBox.isSelected());
            Options.getProgramOptions().setHelpdialogs(HelpDialogsCheckBox.isSelected());
            Options.getSessionOptions().setEntrainmentvolume(new Double(EntrainmentVolumePercentage.getText()) / 100);
            Options.getSessionOptions().setAmbiencevolume(new Double(AmbienceVolumePercentage.getText()) / 100);
            Options.getSessionOptions().setFadeoutduration(new Double(FadeOutValue.getText()));
            Options.getSessionOptions().setFadeinduration(new Double(FadeInValue.getText()));
            Options.getSessionOptions().setRampenabled(RampSwitch.isSelected());
            Options.getSessionOptions().setReferenceoption(ReferenceSwitch.isSelected());
            Options.marshall();
            super.close();
        }
        class ItemWithDescription {
            private final String name;
            private final String description;

            public ItemWithDescription(String name, String description) {
                this.name = name + " Description";
                this.description = description;
            }

            public String getName() {
                return name;
            }
            public String getDescription() {
                return description;
            }
        }
    }
    public class EditReferenceFiles extends Stage {
        public ChoiceBox<String> SessionPartNamesChoiceBox;
        public TextArea MainTextArea;
        public Button CloseButton;
        public Label StatusBar;
        public Button SaveButton;
        public Button PreviewButton;
        public RadioButton HTMLVariation;
        public RadioButton TEXTVariation;
        private File selectedfile;
        private SessionPart selectedsessionpart;
        private ArrayList<Integer> userselectedindexes;
        private ReferenceType referenceType;

        public EditReferenceFiles(Options options, ReferenceType referenceType) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/EditReferenceFiles.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                options.setStyle(this);
                setTitle("Reference Files Editor");
                ObservableList<String> sessionpartnames = FXCollections.observableArrayList();
                sessionpartnames.addAll(kujiin.xml.Options.ALLNAMES);
                userselectedindexes = new ArrayList<>();
                SessionPartNamesChoiceBox.setItems(sessionpartnames);
                MainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {textchanged();});
                SessionPartNamesChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {if (oldValue != null) userselectedindexes.add(oldValue.intValue());});
                HTMLVariation.setDisable(SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
                TEXTVariation.setDisable(SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
                if (referenceType == null) {referenceType = kujiin.xml.Options.DEFAULT_REFERENCE_TYPE_OPTION;}
                HTMLVariation.setSelected(referenceType == ReferenceType.html);
                TEXTVariation.setSelected(referenceType == ReferenceType.txt);
                this.referenceType = referenceType;
                PreviewButton.setDisable(true);
                SaveButton.setDisable(true);
                String referencename = referenceType.name();
                this.setOnCloseRequest(event -> {
                    if (unsavedchanges()) {
                        switch (new AnswerDialog(Options, "Confirmation", null, SessionPartNamesChoiceBox.getValue() + " " + referencename + " Variation Has Unsaved Changes",
                                "Save And Close", "Close Without Saving", "Cancel").getResult()) {
                            case YES:
                                saveselectedfile();
                                break;
                            case NO:
                                break;
                            case CANCEL:
                                event.consume();
                        }
                    }
                });
                SessionPartNamesChoiceBox.setOnAction(event -> newsessionpartselected());
                HTMLVariation.setOnAction(event -> htmlselected());
                TEXTVariation.setOnAction(event -> textselected());
                PreviewButton.setOnAction(event -> preview());
                SaveButton.setOnAction(event -> saveselectedfile());
                CloseButton.setOnAction(event -> close());
            } catch (IOException e) {new ExceptionDialog(getOptions(), e).showAndWait();}
        }

    // Text Area Methods
        private boolean unsavedchanges() {
            try {
                return ! MainTextArea.getText().equals(Util.file_getcontents(selectedfile));
            } catch (Exception e) {return false;}
        }
        public void newsessionpartselected() {
            HTMLVariation.setDisable(SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
            TEXTVariation.setDisable(SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
            if (userselectedindexes.size() > 0 && selectedfile != null && unsavedchanges()) {
                Util.AnswerType answerType = new AnswerDialog(Options, "Confirmation", null, "Previous Reference File Has Unsaved Changes",
                        "Save And Close", "Close Without Saving", "Cancel").getResult();
                switch (answerType) {
                    case YES:
                        saveselectedfile();
                        break;
                    case NO:
                        break;
                    case CANCEL:
                        SessionPartNamesChoiceBox.getSelectionModel().select(userselectedindexes.get(userselectedindexes.size() - 1));
                        return;
                }
            }
            loadselectedfile();
        }
        private void textchanged() {
            if (referenceType != null && selectedsessionpart != null && selectedfile != null) {
                boolean hasvalidtext = MainTextArea.getText() != null && MainTextArea.getText().length() > 0;
                PreviewButton.setDisable(! hasvalidtext || referenceType == ReferenceType.txt);
                SaveButton.setDisable(MainTextArea.getText() == null || Util.file_getcontents(selectedfile).equals(MainTextArea.getText().toCharArray()));
                switch (referenceType) {
                    case html:
                        if (MainTextArea.getText() != null && Util.String_validhtml(MainTextArea.getText())) {StatusBar.setTextFill(Color.BLACK); StatusBar.setText("");}
                        else {StatusBar.setTextFill(Color.RED); StatusBar.setText("Not Valid .html");}
                        break;
                    case txt:
                        if (MainTextArea.getText() != null && MainTextArea.getText().length() == 0) {StatusBar.setTextFill(Color.RED); StatusBar.setText("No Text Entered");}
                        else {StatusBar.setTextFill(Color.BLACK); StatusBar.setText("");}
                        break;
                }
            } else {
                MainTextArea.clear();
                StatusBar.setTextFill(Color.RED);
                Util.gui_showtimedmessageonlabel(StatusBar, "No SessionPart Selected", 3000);
            }
        }

    // Button Methods
        public void htmlselected() {
        if (unsavedchanges()) {
            switch (new AnswerDialog(Options, "Confirmation", null, "Previous Reference File Has Unsaved Changes",
                    "Save And Close", "Close Without Saving", "Cancel").getResult()) {
                case YES:
                    saveselectedfile();
                    break;
                case NO:
                    break;
                case CANCEL:
                    HTMLVariation.setSelected(false);
                    TEXTVariation.setSelected(true);
                    return;
            }
        }
        // Test If Unsaved Changes Here
        TEXTVariation.setSelected(false);
        PreviewButton.setDisable(! HTMLVariation.isSelected());
        referenceType = ReferenceType.html;
        selectnewfile();
        loadselectedfile();
    }
        public void textselected() {
            if (unsavedchanges()) {
                switch (new AnswerDialog(Options, "Confirmation", null, "Previous Reference File Has Unsaved Changes",
                        "Save And Close", "Close Without Saving", "Cancel").getResult()) {
                    case YES:
                        saveselectedfile();
                        break;
                    case NO:
                        break;
                    case CANCEL:
                        HTMLVariation.setSelected(true);
                        TEXTVariation.setSelected(false);
                        return;
                }
            }
            // Test If Unsaved Changes Here
            HTMLVariation.setSelected(false);
            PreviewButton.setDisable(! HTMLVariation.isSelected());
            referenceType = ReferenceType.txt;
            selectnewfile();
            loadselectedfile();
        }
        public void preview() {
            if (MainTextArea.getText().length() > 0 && HTMLVariation.isSelected() && referenceType == ReferenceType.html) {
                if (! Util.String_validhtml(MainTextArea.getText())) {
                    if (! new ConfirmationDialog(Options, "Confirmation", null, "Html Code In Text Area Is Not Valid HTML", "Preview Anyways", "Cancel").getResult()) {return;}
                }
                // TODO Fix Reference Display Preview
//                new DisplayReference(MainTextArea.getText());
            }
        }

    // Utility Methods
        public void saveselectedfile() {
            if (Util.file_writecontents(selectedfile, MainTextArea.getText())) {
                String text = selectedsessionpart + "'s Reference File (" + referenceType.toString() + " Variation) Has Been Saved";
                new InformationDialog(Options, "Changes Saved", text, "");
            } else {
                new ErrorDialog(Options, "Error", "Couldn't Save To:\n" + selectedfile.getAbsolutePath(), "Check If You Have Write Access To File");}
        }
        public void loadselectedfile() {
            int index = SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex();
            if (index != -1 && (HTMLVariation.isSelected() || TEXTVariation.isSelected())) {
                selectedsessionpart = getAllSessionParts(false).get(index);
                selectnewfile();
                String contents = Util.file_getcontents(selectedfile);
                MainTextArea.setText(contents);
                PreviewButton.setDisable(TEXTVariation.isSelected() || contents == null || contents.length() == 0);
                StatusBar.setTextFill(Color.BLACK);
                StatusBar.setText("");
                SaveButton.setDisable(true);
            } else {
                if (SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1) {
                    new InformationDialog(Options, "Information", "No SessionPart Selected", "Select A SessionPart To Load");}
                else {
                    new InformationDialog(Options, "Information", "No Variation Selected", "Select A Variation To Load");}
                PreviewButton.setDisable(true);
            }
        }
        public void selectnewfile() {
            if (referenceType == null || selectedsessionpart == null) {selectedfile = null; return;}
            switch (referenceType) {
                case html:
                    selectedfile = new File(new File(kujiin.xml.Options.DIRECTORYREFERENCE, "html"), selectedsessionpart.getNameForReference() + ".html");
                    if (! selectedfile.exists()) {try {selectedfile.createNewFile();} catch (IOException e) {new ExceptionDialog(getOptions(), e);}}
                    break;
                case txt:
                    selectedfile = new File(new File(kujiin.xml.Options.DIRECTORYREFERENCE, "txt"), selectedsessionpart.getNameForReference() + ".txt");
                    if (! selectedfile.exists()) {try {selectedfile.createNewFile();} catch (IOException e) {new ExceptionDialog(getOptions(), e);}}
                    break;
            }
        }

    // Dialog Methods
        public void closewindow(Event event) {
            // Check If Unsaved Text
            this.close();
        }

    }

}