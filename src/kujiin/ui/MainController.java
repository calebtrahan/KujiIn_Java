package kujiin.ui;

import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.ui.dialogs.AmbienceEditor_Advanced;
import kujiin.ui.dialogs.AmbienceEditor_Simple;
import kujiin.ui.dialogs.ChangeProgramOptions;
import kujiin.ui.dialogs.EditReferenceFiles;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.util.*;
import kujiin.util.enums.FreqType;
import kujiin.util.enums.ProgramState;
import kujiin.util.enums.StartupCheckType;
import kujiin.xml.Ambiences;
import kujiin.xml.Entrainments;
import kujiin.xml.Preferences;
import kujiin.xml.SoundFile;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
// Bugs To Fix
    // TODO Find NullPointer During Session Playback On Thread During Transition
    // TODO Startup Checks
        // Percentage Isn't Calculating Correctly
    // TODO Find A Way To Reset Session After Stop Animation Ends

// Features To Test

// Additional Features To Definitely Add
    // TODO Create Goal Progress Similar To Session Details And Add To Session Details Dialog
    // TODO Exporter
    // TODO Add Logging (And Write To Log File) For Troubleshooting

// Optional Additional Features
    // TODO Refactor Freq Files So There Can Be 2 or 3 Different Frequency Octaves For The Same Session Part (Use enum FreqType)

// Mind Workstation
    // TODO Add Low (And Possibly Medium) Variations Of All Session Parts
    // TODO Add Ramps To Connect Low (And Possibly Medium) Variations Of Session Parts With Each Other

public class MainController implements Initializable {
// GUI Fields
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
    public MenuItem Menu_Preferences;
    public MenuItem Menu_Close;
    public MenuItem Menu_EditAmbience;
    public MenuItem Menu_EditReference;
    public MenuItem Menu_Help;
    public MenuItem Menu_About;
    public MenuItem Menu_Contact;
    private SessionCreator sessionCreator;
    private ProgressTracker progressTracker;
    protected FreqType freqType;
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
    private Preferences Preferences;
    private Entrainments Entrainments;
    private Ambiences Ambiences;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setPreferences(new Preferences());
        getPreferences().unmarshall();
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
        for (SessionPart i : getSessionParts(0, 16)) {
            i.setGoalsController(getProgressTracker().getGoals());
            i.goals_unmarshall();
        }
        getProgressTracker().setSessionParts(getSessionParts(0, 17));
    }
    public void setupMenuActions() {
        Menu_Preferences.setOnAction(event -> {
            new ChangeProgramOptions(MainController.this, getStage(), false).showAndWait();
            getAllSessionParts(false).forEach(SessionPart::syncguielements);
            Preferences.marshall();
            getProgressTracker().sessions_updateui();
            getProgressTracker().goals_updateui(null);
        });
        Menu_Close.setOnAction(event -> close());
        Menu_EditAmbience.setOnAction(event -> {
            if (programState == ProgramState.IDLE) {
                if (getPreferences().getAdvancedOptions().getDefaultambienceeditor().equals("Simple")) {new AmbienceEditor_Simple(this, getStage(), false).showAndWait();}
                else if (getPreferences().getAdvancedOptions().getDefaultambienceeditor().equals("Advanced")) {new AmbienceEditor_Advanced(this, getStage(), false).showAndWait();}
            } else {
                new InformationDialog(getPreferences(), "Information", "Cannot Edit Ambience While Performing Startup Checks", "");
            }
        });
        Menu_EditReference.setOnAction(event -> new EditReferenceFiles(this, getStage(), false).showAndWait());
        Menu_Help.setOnAction(event -> {});
        Menu_About.setOnAction(event -> {});
        Menu_Contact.setOnAction(event -> {});
    }
    public boolean cleanup() {
        Ambiences.marshall();
        Entrainments.marshall();
        Preferences.marshall();
        return sessionCreator.cleanup() && progressTracker.cleanup();
    }
    public void close() {
        if (cleanup()) {
            System.exit(0);
        }
    }

// Getters And Setters
    // Controller Classes
    public void setSessionCreator(SessionCreator sessionCreator) {
        this.sessionCreator = sessionCreator;
    }
    public SessionCreator getSessionCreator() {
        return sessionCreator;
    }
    public void setProgressTracker(ProgressTracker progressTracker) {
        this.progressTracker = progressTracker;
    }
    public ProgressTracker getProgressTracker() {
        return progressTracker;
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
    public Preferences getPreferences() {
        return Preferences;
    }
    public void setPreferences(Preferences preferences) {
        Preferences = preferences;
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
    // Session Part Getters
    public ArrayList<SessionPart> getAllSessionParts(boolean includetotal) {
        ArrayList<SessionPart> allsessionparts = new ArrayList<>(Arrays.asList(Presession, Rin, Kyo, Toh, Sha, Kai, Jin, Retsu, Zai, Zen, Earth, Air, Fire, Water, Void, Postsession));
        if (includetotal) {allsessionparts.add(Total);}
        return allsessionparts;
    }
    public ArrayList<Cut> getAllCuts() {
        return getAllSessionParts(false).stream().filter(i -> i instanceof Cut).map(i -> (Cut) i).collect(Collectors.toCollection(ArrayList::new));
    }
    public ArrayList<Element> getAllElements() {
        return getAllSessionParts(false).stream().filter(i -> i instanceof Element).map(i -> (Element) i).collect(Collectors.toCollection(ArrayList::new));
    }
    public SessionPart getSessionPart(int index) {
        return getAllSessionParts(true).get(index);
    }
    public String getSessionPart_Name(int index) {
        return getSessionPart(index).name;
    }
    public ArrayList<SessionPart> getSessionParts(int startindex, int index) {
        return new ArrayList<>(getAllSessionParts(true).subList(startindex, index));
    }
    public ArrayList<String> getSessionPart_Names(int startindex, int index) {
        return getSessionParts(startindex, index).stream().map(i -> i.name).collect(Collectors.toCollection(ArrayList::new));
    }
    // State Getters
    public ProgramState getProgramState() {
        return programState;
    }

// Startup Checks
    public void startupchecks_start() {
        programState = ProgramState.STARTING_UP;
        sessionCreator.setDisable(true, "");
        startupChecks = new StartupChecks(getSessionParts(0, 16));
        startupChecks.run();
    }
    public void startupchecks_finished() {
        sessionCreator.setDisable(false, "");
        programState = ProgramState.IDLE;
        startupChecks = null;
        Util.gui_showtimedmessageonlabel(CreatorStatusBar, "Startup Checks Completed", 3000);
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
        public ArrayList<SessionPart> getPartswithmissingentrainment() {
            return partswithmissingentrainment;
        }

    // Method Overrides
        @Override
        protected Object call() throws Exception {
            if (firstcall) {CreatorStatusBar.textProperty().bind(messageProperty()); calculatetotalworktodo(); firstcall = false;}
            if (progresstonextsessionpart) {
                try {
                    if (selectedsessionpart == null) {selectedsessionpart = sessionPartList.get(sessionpartcount);}
                } catch (IndexOutOfBoundsException e) {
                    // End Of Startup Checks
                    CreatorStatusBar.textProperty().unbind();
                    getEntrainments().marshall();
                    getAmbiences().marshall();
                    startupchecks_finished();
                    return null;
                }
            }
            SoundFile soundFile;
            try {soundFile = selectedsessionpart.startup_getNext();}
            catch (IndexOutOfBoundsException ignored) {
                getEntrainments().setsessionpartEntrainment(selectedsessionpart, selectedsessionpart.getEntrainment());
                if (! selectedsessionpart.getAmbience_hasAny() && ! partswithnoambience.contains(selectedsessionpart)) {partswithnoambience.add(selectedsessionpart);}
                else { getAmbiences().setsessionpartAmbience(selectedsessionpart, selectedsessionpart.getAmbience());}
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
            } else {
                if (selectedsessionpart.getStartupCheckType() == StartupCheckType.ENTRAINMENT) {
                    selectedsessionpart.startup_incremententrainmentcount();
                } else if (selectedsessionpart.getStartupCheckType() == StartupCheckType.AMBIENCE) {
                    selectedsessionpart.startup_incrementambiencecount();
                }
                progresstonextsessionpart = true;
                workcount[0]++;
                updateProgress(workcount[0], workcount[1]);
                updateMessage("Performing Startup Checks. Please Wait (" + new Double(getProgress() * 100).intValue() + "%)");
                try {call();} catch (Exception ignored) {ignored.printStackTrace();}}
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

}