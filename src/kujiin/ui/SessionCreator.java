package kujiin.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.ui.dialogs.*;
import kujiin.util.*;
import kujiin.util.enums.*;
import kujiin.util.interfaces.UI;
import kujiin.xml.Preferences;
import kujiin.xml.Preset;
import kujiin.xml.Session;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static kujiin.util.enums.PlayerState.*;
import static kujiin.xml.Preferences.PROGRAM_ICON;

public class SessionCreator implements UI {
    private Button LoadPresetButton;
    private Button SavePresetButton;
    private TextField ApproximateEndTime;
    private TextField TotalSessionTime;
    private Button ChangeAllCutsButton;
    private Button ChangeAllElementsButton;
    private Button ResetButton;
    private Button PlayButton;
    private Button ExportButton;
    private Label StatusBar;
    private Preset Preset;
    private Timeline updateuitimeline;
    private Preferences preferences;
    private List<SessionPart> AllSessionParts;
    private MainController Root;
    private Player Player;
    private ProgressTracker progressTracker;
    private PlayerState playerState = IDLE;
    private DisplayReference displayReference;
    private ExporterState exporterState;
    private AmbiencePlaybackType ambiencePlaybackType;
    private List<SessionPart> itemsinsession;
    private boolean ambienceenabled = false;

    public SessionCreator(MainController Root) {
        this.Root = Root;
        Preset = new Preset(Root);
        setupListeners(Root);
        LoadPresetButton = Root.LoadPresetButton;
        SavePresetButton = Root.SavePresetButton;
        ApproximateEndTime = Root.ApproximateEndTime;
        TotalSessionTime = Root.TotalSessionTime;
        ChangeAllCutsButton = Root.ChangeAllCutsButton;
        ChangeAllElementsButton = Root.ChangeAllElementsButton;
        ResetButton = Root.ResetCreatorButton;
        PlayButton = Root.PlayButton;
        ExportButton = Root.ExportButton;
        StatusBar = Root.CreatorStatusBar;
        preferences = Root.getPreferences();
        AllSessionParts = Root.getSessionParts(0, 16);
        updateuitimeline = new Timeline(new KeyFrame(Duration.seconds(10), ae -> updategui()));
        updateuitimeline.setCycleCount(Animation.INDEFINITE);
        ambiencePlaybackType = Root.getPreferences().getSessionOptions().getAmbiencePlaybackType();
    }

    public void setupListeners(MainController Root) {
        Root.LoadPresetButton.setOnAction(event -> loadPreset());
        Root.SavePresetButton.setOnAction(event -> savePreset());
        Root.ChangeAllCutsButton.setOnAction(event -> changeallcutvalues());
        Root.ChangeAllElementsButton.setOnAction(event -> changeallelementvalues());
        Root.ResetCreatorButton.setOnAction(event -> reset(true));
        Root.PlayButton.setOnAction(event -> playsession());
        Root.ExportButton.setOnAction(event -> exportsession());
    }
    public void setupTooltips() {
        if (preferences.getUserInterfaceOptions().getTooltips()) {
            TotalSessionTime.setTooltip(new Tooltip("Total Session Time (Not Including Presession + Postsession Ramp, And Alert File)"));
            ApproximateEndTime.setTooltip(new Tooltip("Approximate Finish Time For This Session (Assuming You Start Now)"));
            ChangeAllCutsButton.setTooltip(new Tooltip("Change All Cut Values Simultaneously"));
            ChangeAllElementsButton.setTooltip(new Tooltip("Change All Element Values Simultaneously"));
            LoadPresetButton.setTooltip(new Tooltip("Load A Saved Preset"));
            SavePresetButton.setTooltip(new Tooltip("Save This Session As A Preset"));
            ExportButton.setTooltip(new Tooltip("Export This Session To .mp3 For Use Without The Program"));
        } else {
            TotalSessionTime.setTooltip(null);
            ApproximateEndTime.setTooltip(null);
            ChangeAllCutsButton.setTooltip(null);
            ChangeAllElementsButton.setTooltip(null);
            LoadPresetButton.setTooltip(null);
            SavePresetButton.setTooltip(null);
            ExportButton.setTooltip(null);
        }
    }
    public void setDisable(boolean disabled) {
        LoadPresetButton.setDisable(disabled);
        SavePresetButton.setDisable(disabled);
        ApproximateEndTime.setDisable(disabled);
        TotalSessionTime.setDisable(disabled);
        ChangeAllCutsButton.setDisable(disabled);
        ChangeAllElementsButton.setDisable(disabled);
        PlayButton.setDisable(disabled);
        ExportButton.setDisable(disabled);
        ResetButton.setDisable(disabled);
        for (SessionPart i : AllSessionParts) {i.gui_setDisable(disabled);}
        if (disabled) {updateuitimeline.stop();
        } else {updateuitimeline.play();}
    }
    public void setDisable(boolean disabled, String statusbarmsg) {
        setDisable(disabled);
        StatusBar.setText(statusbarmsg);
    }
    public boolean cleanup() {return true;}

// Getters And Setters
    public boolean isAmbienceenabled() {
        return ambienceenabled;
    }
    public void setAmbienceenabled(boolean ambienceenabled) {
        this.ambienceenabled = ambienceenabled;
    }
    public void setAmbiencePlaybackType(AmbiencePlaybackType ambiencePlaybackType) {
        this.ambiencePlaybackType = ambiencePlaybackType;
    }
    public AmbiencePlaybackType getAmbiencePlaybackType() {
        return ambiencePlaybackType;
    }
    public PlayerState getPlayerState() {
        return playerState;
    }
    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }
    public Player getPlayer() {
        return Player;
    }
    public DisplayReference getDisplayReference() {
        return displayReference;
    }
    public ArrayList<Integer> getallsessionvalues() {
        return AllSessionParts.stream().map(i -> new Double(i.getduration().toMinutes()).intValue()).collect(Collectors.toCollection(ArrayList::new));
    }
    public boolean allvaluesnotzero() {
        for (SessionPart i : AllSessionParts) {if (i.hasValidValue()) {return true;}}
        return false;
    }

// Creation
    public void updategui() {
        boolean notallzero = false;
        try {for (Integer i : getallsessionvalues()) {if (i > 0) {notallzero = true;}}}
        catch (NullPointerException ignored) {}
        if (notallzero) {
            Duration totalsessiontime = Duration.ZERO;
            for (SessionPart i : AllSessionParts) {totalsessiontime = totalsessiontime.add(i.getduration());}
            TotalSessionTime.setText(Util.formatdurationtoStringSpelledOut(totalsessiontime, TotalSessionTime.getLayoutBounds().getWidth()));
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MILLISECOND, new Double(totalsessiontime.toMillis()).intValue());
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            ApproximateEndTime.setText(sdf.format(cal.getTime()));
        } else {
            TotalSessionTime.setText("-");
            ApproximateEndTime.setText("-");
        }
    }
    public void changeallcutvalues() {
        ChangeAllValues changevaluesdialog = new ChangeAllValues(Root, "Change All Cut Values To: ");
        changevaluesdialog.showAndWait();
        if (changevaluesdialog.getAccepted()) {
            Integer min = changevaluesdialog.getMinutes();
            for (Cut i : Root.getAllCuts()) {i.changevalue(min);}
            if (changevaluesdialog.getincludepresession()) {
                Root.getSessionPart(0).changevalue(min);
            }
            if (changevaluesdialog.getincludepostsession()) {
                Root.getSessionPart(15).changevalue(min);
            }
        }
    }
    public void changeallelementvalues() {
        ChangeAllValues changevaluesdialog = new ChangeAllValues(Root, "Change All Element Values To: ");
        changevaluesdialog.showAndWait();
        if (changevaluesdialog.getAccepted()) {
            Integer min = changevaluesdialog.getMinutes();
            for (Element i : Root.getAllElements()) {i.changevalue(min);}
            if (changevaluesdialog.getincludepresession()) {
                Root.getSessionPart(0).changevalue(min);
            }
            if (changevaluesdialog.getincludepostsession()) {
                Root.getSessionPart(15).changevalue(min);
            }
        }
    }
    public void populateitemsinsession() {
        itemsinsession = new ArrayList<>();
        for (SessionPart i : Root.getAllSessionParts(false)) {
            if (i.getduration().greaterThan(Duration.ZERO) || i.ramponly) {itemsinsession.add(i);}
            else if (i instanceof Qi_Gong && Root.getPreferences().getSessionOptions().getPrepostrampenabled()) {i.setRamponly(true); itemsinsession.add(i);}
        }
    }
    public boolean creationprechecks() {
        if (Root.getProgramState() == ProgramState.IDLE) {
            if (! allvaluesnotzero()) {
                new ErrorDialog(preferences, "Error", "All Values Are 0", "Cannot Play Session");
                return false;
            }
            populateitemsinsession();
            setDisable(true, "Creator Disabled While Confirming Session");
            SessionPlaybackOverview sessionPlaybackOverview = new SessionPlaybackOverview(Root, itemsinsession);
            sessionPlaybackOverview.showAndWait();
            setDisable(false, "");
            if (sessionPlaybackOverview.getResult()) {
                itemsinsession = sessionPlaybackOverview.getAlladjustedsessionitems();
                return true;
            }
            else {reset(false); return false;}
        } else {return false;}
    }
    public boolean create() {
        for (SessionPart i : itemsinsession) {
            if (! i.creation_build(itemsinsession)) {reset(false); return false;}
        }
        return true;
    }
    public void reset(boolean setvaluetozero) {
        itemsinsession.clear();
        for (SessionPart i : AllSessionParts) {i.creation_reset(setvaluetozero);}
    }

// Playback
    public void playsession() {
        if (creationprechecks() && create() && (Player == null || ! Player.isShowing())) {
            setDisable(true, "Creator Disabled During Session Playback");
            Player = new Player();
            Player.showAndWait();
            setDisable(false, "");
        }
    }

// Export
    public void exportsession() {}

// Preset Methods
    public void loadPreset() {
        File presetfile = Preset.open();
        if (presetfile != null && Preset.hasvalidValues()) {
            preset_changecreationvaluestopreset(Preset.gettimes());
        } else {
            if (presetfile != null)
                new InformationDialog(preferences, "Invalid Preset File", "Invalid Preset File", "Cannot Load File");
        }
    }
    public void savePreset() {
        ArrayList<Double> creatorvaluesinminutes = new ArrayList<>();
        boolean validsession = false;
        for (SessionPart i : AllSessionParts) {
            creatorvaluesinminutes.add(i.getduration().toMinutes());
            if (i.getduration().greaterThan(Duration.ZERO)) {
                validsession = true;
            }
        }
        if (validsession) {
            Preset.settimes(creatorvaluesinminutes);
            if (Preset.save()) {
                Util.gui_showtimedmessageonlabel(StatusBar, "Preset Successfully Saved", 1500);
            } else {
                new ErrorDialog(preferences, "Error", "Couldn't Save Preset", "Your Preset Could Not Be Saved, Do You Have Write Access To That Directory?");
            }
        } else {
            new InformationDialog(preferences, "Information", "Cannot Save Preset", "All Values Are 0");
        }
    }
    public void preset_changecreationvaluestopreset(ArrayList<Double> presetvalues) {
        try {
            for (int i = 0; i < AllSessionParts.size(); i++) {
                AllSessionParts.get(i).changevalue(presetvalues.get(i).intValue());
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
            new ErrorDialog(preferences, "Error", "Couldn't Change Creator Values To Preset", "Try Reloaded Preset");
        }
    }

// Subclasses
    public class Player extends Stage {
        public Label CurrentSessionPartTopLabel;
        public ProgressIndicator CurrentSessionPartProgress;
        public Label CurrentProgressDetails;
        public Slider EntrainmentVolume;
        public Label EntrainmentVolumePercentage;
        public Slider AmbienceVolume;
        public Label AmbienceVolumePercentage;
        public Label TotalSessionLabel;
        public ProgressIndicator TotalProgress;
        public Label TotalProgressDetails;
        public CheckBox ReferenceCheckBox;
        public Button PlayButton;
        public Button PauseButton;
        public Button StopButton;
    // Playback Fields
        public Boolean displaynormaltime = true;
        public Label EntrainmentVolumeTopLabel;
        public Label AmbienceVolumeTopLabel;
        private Timeline player_updateuitimeline;
        private Double currententrainmentvolume;
        private Double currentambiencevolume;
        public SessionPart currentsessionpart;
        public Duration totalsessiondurationelapsed;
        public Duration totalsessionduration;
        public int sessionpartcount;
        public List<SessionPart> sessionpartswithGoalsCompletedThisSession;
    // Dialog Fields
        public SelectReferenceType selectReferenceType;
    // Tracking Fields
        public Session currentsession;

        public Player() {
            try {
                updateuitimeline.stop();
                if (! Root.getStage().isIconified()) {Root.getStage().setIconified(true);}
                progressTracker = Root.getProgressTracker();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionPlayer.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getIcons().clear();
                getIcons().add(PROGRAM_ICON);
                String themefile = Root.getPreferences().getUserInterfaceOptions().getThemefile();
                if (themefile != null) {getScene().getStylesheets().add(themefile);}
                setTitle("Session Player");
                reset(false);
                if (Root.getPreferences().getSessionOptions().getReferenceoption() && Root.getPreferences().getSessionOptions().getReferencetype() != null) {
                    ReferenceCheckBox.setSelected(true);
                } else {ReferenceCheckBox.setSelected(false);}
                togglereference();
                ReferenceCheckBox.setSelected(Root.getPreferences().getSessionOptions().getReferenceoption());
                setResizable(false);
                CurrentProgressDetails.setOnMouseClicked(event -> displaynormaltime = ! displaynormaltime);
                TotalProgressDetails.setOnMouseClicked(event -> displaynormaltime = ! displaynormaltime);
                setOnCloseRequest(event -> {
                    if (playerState == PlayerState.PLAYING || playerState == PlayerState.STOPPED || playerState == PlayerState.PAUSED || playerState == PlayerState.IDLE) {
                        if (endsessionprematurely()) {close();} else {play(); event.consume();}
                    } else {
//                        Util.gui_showtimedmessageonlabel(StatusBar, "Cannot Close Player During Fade Animation", 400);
                        new Timeline(new KeyFrame(Duration.millis(400), ae -> currentsessionpart.toggleplayerbuttons()));
                        event.consume();
                    }
                });
                if (Root.getPreferences().getUserInterfaceOptions().getTooltips()) {setupTooltips();}
            } catch (IOException ignored) {}
        }

        public void setupKeyBoardShortcuts() {
            addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                KeyCodeCombination forwardambience = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN);
                KeyCodeCombination backambience = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN);
                @Override
                public void handle(KeyEvent event) {
                    if (playerState == PLAYING) {
                        if (forwardambience.match(event)) {currentsessionpart.playnextambience();}
                        else if (backambience.match(event)) {currentsessionpart.playpreviousambience();}
                        else if (event.getCode() == KeyCode.ESCAPE) {close();}
                    }
                }
            });
        }
        public void setupTooltips() {
            CurrentSessionPartTopLabel.setTooltip(new Tooltip("Current Session Part Playing"));
            CurrentProgressDetails.setTooltip(new Tooltip("Current Session Part Progress"));
            CurrentProgressDetails.setTooltip(new Tooltip("Detailed Current Session Part Progress"));
            EntrainmentVolume.setTooltip(new Tooltip("Current Entrainment Volume"));
            EntrainmentVolumeTopLabel.setTooltip(new Tooltip("Entrainment Volume"));
            EntrainmentVolumePercentage.setTooltip(new Tooltip("Current Entrainment Volume Percentage"));
            AmbienceVolume.setTooltip(new Tooltip("Current Ambience Volume"));
            AmbienceVolumeTopLabel.setTooltip(new Tooltip("Ambience Volume"));
            AmbienceVolumePercentage.setTooltip(new Tooltip("Current Ambience Volume Percentage"));
            TotalProgress.setTooltip(new Tooltip("Total Session Progress"));
            TotalProgressDetails.setTooltip(new Tooltip("Detailed Total Session Progress"));
            ReferenceCheckBox.setTooltip(new Tooltip("Display Reference During Session Playback"));
        }

    // Getters And Setters
        public Double getCurrententrainmentvolume() {
            return currententrainmentvolume;
        }
        public Double getCurrentambiencevolume() {
            return currentambiencevolume;
        }
        public void setCurrententrainmentvolume(Double currententrainmentvolume) {
            this.currententrainmentvolume = currententrainmentvolume;
        }
        public void setCurrentambiencevolume(Double currentambiencevolume) {
            this.currentambiencevolume = currentambiencevolume;
        }
        public Timeline getPlayer_updateuitimeline() {
            return player_updateuitimeline;
        }

    // Playback
        public void play() {
            switch (playerState) {
                case IDLE:
                case STOPPED:
                    currentsession = new Session();
                    setupKeyBoardShortcuts();
                    sessionpartswithGoalsCompletedThisSession = new ArrayList<>();
                    totalsessiondurationelapsed = Duration.ZERO;
                    totalsessionduration = Duration.ZERO;
                    for (SessionPart i : itemsinsession) {totalsessionduration = totalsessionduration.add(i.getduration());}
//                    TotalTotalTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessionduration));
                    player_updateuitimeline = new Timeline(new KeyFrame(Duration.millis(100), ae -> updateui()));
                    player_updateuitimeline.setCycleCount(Animation.INDEFINITE);
                    player_updateuitimeline.play();
                    sessionpartcount = 0;
                    currentsessionpart = itemsinsession.get(sessionpartcount);
                    currententrainmentvolume = Root.getPreferences().getPlaybackOptions().getEntrainmentvolume();
                    currentambiencevolume = Root.getPreferences().getPlaybackOptions().getAmbiencevolume();
                    currentsessionpart.start();
                    displayreferencefile();
                    break;
                case PAUSED:
                    player_updateuitimeline.play();
                    currentsessionpart.resume();
                    break;
            }
        }
        public void pause() {
            if (playerState == PlayerState.PLAYING) {
                currentsessionpart.pause();
                player_updateuitimeline.pause();
            }
        }
        public void stop() {
            if (playerState == PLAYING || playerState == PAUSED) {
                if (new ConfirmationDialog(preferences, "Stop Session", "Really Stop Session?", "This Will Reset Session Player", "Stop Session", "Cancel").getResult()) {
                    currentsessionpart.stop();
                    player_updateuitimeline.stop();
                    reset(false);
                }
            }
        }
        public void updateui() {
            try {
                totalsessiondurationelapsed = totalsessiondurationelapsed.add(Duration.millis(100));
                try {currentsessionpart.elapsedtime = currentsessionpart.elapsedtime.add(Duration.millis(100));} catch (NullPointerException ignored) {}
                Float currentprogress;
                Float totalprogress;
                try {
                    if (currentsessionpart.elapsedtime.greaterThan(Duration.ZERO)) {currentprogress = (float) currentsessionpart.elapsedtime.toMillis() / (float) currentsessionpart.getduration().toMillis();}
                    else {currentprogress = (float) 0;}
                } catch (NullPointerException ignored) {currentprogress = (float) 0;}
                if (totalsessiondurationelapsed.greaterThan(Duration.ZERO)) {totalprogress = (float) totalsessiondurationelapsed.toMillis() / (float) totalsessionduration.toMillis();}
                else {totalprogress = (float) 0.0;}
                CurrentSessionPartProgress.setProgress(currentprogress);
                TotalProgress.setProgress(totalprogress);
                CurrentSessionPartTopLabel.setText(currentsessionpart.name + " (" + (itemsinsession.indexOf(currentsessionpart) + 1)  + "/" + itemsinsession.size() + ")");
                TotalSessionLabel.setText("Session");
                String currentparttime;
                String totalparttime;
                try {currentparttime = Util.formatdurationtoStringDecimalWithColons(currentsessionpart.elapsedtime);}
                catch (NullPointerException ignored) {currentparttime = Util.formatdurationtoStringDecimalWithColons(Duration.ZERO);}
                if (displaynormaltime) {totalparttime = Util.formatdurationtoStringDecimalWithColons(currentsessionpart.getduration());}
                else {totalparttime = Util.formatdurationtoStringDecimalWithColons(currentsessionpart.getduration().subtract(currentsessionpart.elapsedtime));}
                CurrentProgressDetails.setText(String.format("%s -> %s", currentparttime, totalparttime));
                String currenttotaltime;
                String totaltotaltime;
                currenttotaltime = Util.formatdurationtoStringDecimalWithColons(totalsessiondurationelapsed);
                if (displaynormaltime) {totaltotaltime = Util.formatdurationtoStringDecimalWithColons(totalsessionduration);}
                else {totaltotaltime = Util.formatdurationtoStringDecimalWithColons(totalsessionduration.subtract(totalsessiondurationelapsed));}
                TotalProgressDetails.setText(String.format("%s -> %s", currenttotaltime, totaltotaltime));
                try {
                    if (displayReference != null && displayReference.isShowing()) {
                        displayReference.setCurrentProgress((double) currentprogress);
                        displayReference.setTotalProgress((double) totalprogress);
                        displayReference.setName(currentsessionpart.name);
                    }
                } catch (Exception ignored) {}
                progressTracker.goals_updateui(this);
                progressTracker.sessions_updateui();
                if (playerState == PLAYING) {currentsession.updateduration(currentsessionpart, currentsessionpart.elapsedtime);}
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
        public void progresstonextsessionpart() {
            try {
                switch (playerState) {
                    case TRANSITIONING:
                        try {
                            currentsessionpart.goals_transitioncheck();
                            currentsessionpart.cleanupPlayersandAnimations();
                            sessionpartcount++;
                            currentsessionpart = itemsinsession.get(sessionpartcount);
                            currentsessionpart.start();
                            displayreferencefile();
                        } catch (IndexOutOfBoundsException ignored) {
                            playerState = PlayerState.IDLE;
                            currentsessionpart.cleanupPlayersandAnimations();
                            endofsession();
                        }
                        break;
                    case PLAYING:
                        closereferencefile();
                        transition();
                        break;
                }
            } catch (Exception ignored) {}
        }
        public void transition() {
            currentsession.updateduration(currentsessionpart, currentsessionpart.getduration());
            currentsession.updatesessionpartambience(currentsessionpart, currentsessionpart.getAmbienceplayhistory());
            progressTracker.sessions_updateui();
            progressTracker.goals_updateui(this);
            currentsessionpart.stop();
            if (Root.getPreferences().getSessionOptions().getAlertfunction()) {
                Media alertmedia = new Media(Root.getPreferences().getSessionOptions().getAlertfilelocation());
                MediaPlayer alertplayer = new MediaPlayer(alertmedia);
                alertplayer.play();
                playerState = TRANSITIONING;
                alertplayer.setOnEndOfMedia(() -> {
                    alertplayer.stop();
                    alertplayer.dispose();
                    progresstonextsessionpart();
                });
//                alertplayer.setOnError(() -> {
//                    if (new ConfirmationDialog(preferences, "Alert File Playback Error", null, "An Error Occured While Playing The Alert File.",
//                            "Retry", "Skip")) {
//                        alertplayer.stop();
//                        alertplayer.play();
//                    } else {
//                        alertplayer.stop();
//                        alertplayer.dispose();
//                        player_progresstonextsessionpart();
//                    }
//                });
            } else {
                playerState = TRANSITIONING;
                progresstonextsessionpart();
            }
        }
        public void reset(boolean endofsession) {
            CurrentSessionPartProgress.setProgress(0.0);
            TotalProgress.setProgress(0.0);
            CurrentProgressDetails.setText("");
            TotalProgressDetails.setText("");
            EntrainmentVolume.setDisable(true);
            EntrainmentVolume.setValue(0.0);
            EntrainmentVolumePercentage.setText("0%");
            AmbienceVolume.setDisable(true);
            AmbienceVolume.setValue(0.0);
            AmbienceVolumePercentage.setText("0%");
            if (endofsession) {PlayButton.setText("Replay");}
            else {PlayButton.setText("Start");}
            PauseButton.setDisable(true);
            StopButton.setDisable(true);
        }
        public void endofsession() {
            CurrentSessionPartTopLabel.setText(currentsessionpart.name + " Completed");
            TotalSessionLabel.setText("Session Completed");
            player_updateuitimeline.stop();
            PlayButton.setText("Replay");
            playerState = PlayerState.STOPPED;
            itemsinsession.forEach(SessionPart::cleanupPlayersandAnimations);
            try {progressTracker.getSessions().add(currentsession);}
            catch (JAXBException ignored) {new ErrorDialog(Root.getPreferences(), "Error", "Cannot Save Session", "Check File Permisssions");}
            // TODO Prompt For Export
            progressTracker.sessions_updateui();
            progressTracker.goals_updateui(this);
            currentsession = null;
            reset(true);
            progressTracker.session_displaydetails(itemsinsession);
        }
        public boolean endsessionprematurely() {
            if (playerState == PlayerState.PLAYING || playerState == PlayerState.PAUSED || playerState == TRANSITIONING) {
                currentsessionpart.pausewithoutanimation();
                player_updateuitimeline.pause();
                if (new ConfirmationDialog(preferences, "End Session Early", "Session Is Not Completed.", "End Session Prematurely?", "End Session", "Continue").getResult()) {
                    try {progressTracker.getSessions().add(currentsession);}
                    catch (JAXBException ignored) {new ErrorDialog(Root.getPreferences(), "Error", "Cannot Save Session", "Check File Permisssions");}
                    progressTracker.sessions_updateui();
                    progressTracker.goals_updateui(this);
                    reset(true);
                    return true;
                }
                else {play(); return false;}
            } else {return true;}
        }
        public void togglevolumebinding() {
            if (currentsessionpart != null && playerState == PlayerState.IDLE || playerState == PlayerState.STOPPED) {
                currentsessionpart.volume_rebindentrainment();
                if (ambienceenabled) {currentsessionpart.volume_rebindambience();}
            }
        }

    // Reference
        public void togglereference() {
            boolean buttontoggled = ReferenceCheckBox.isSelected();
            Root.getPreferences().getSessionOptions().setReferenceoption(buttontoggled);
            if (! buttontoggled) {
                closereferencefile();
                togglevolumebinding();
            } else {
                if (selectReferenceType != null && selectReferenceType.isShowing()) {return;}
                else {selectReferenceType = new SelectReferenceType(Root, itemsinsession, playerState != PLAYING);}
                switch (playerState) {
                    case IDLE:
                        selectReferenceType.showAndWait();
                        Root.getPreferences().getSessionOptions().setReferenceoption(selectReferenceType.getResult());
                        if (selectReferenceType.getResult()) {
                            Root.getPreferences().getSessionOptions().setReferencetype(selectReferenceType.getReferenceType());
                            Root.getPreferences().getSessionOptions().setReferencefullscreen(selectReferenceType.getFullScreen());
                        } else {ReferenceCheckBox.setSelected(false);}
                        break;
                    case PLAYING:
                        selectReferenceType.showAndWait();
                        Root.getPreferences().getSessionOptions().setReferenceoption(selectReferenceType.getResult());
                        if (selectReferenceType.getResult()) {
                            Root.getPreferences().getSessionOptions().setReferencetype(selectReferenceType.getReferenceType());
                            Root.getPreferences().getSessionOptions().setReferencefullscreen(selectReferenceType.getFullScreen());
                            displayreferencefile();
                            togglevolumebinding();
                        }
                        break;
                    case PAUSED:
                    case STOPPED:
                    default:
                        ReferenceCheckBox.setSelected(false);
                        break;
                }
            }
        }
        public void displayreferencefile() {
            boolean notalreadyshowing = displayReference == null || ! displayReference.isShowing();
            boolean referenceenabledwithvalidtype = Root.getPreferences().getSessionOptions().getReferenceoption() &&
                    (Root.getPreferences().getSessionOptions().getReferencetype() == ReferenceType.html || Root.getPreferences().getSessionOptions().getReferencetype() == ReferenceType.txt);
            if (notalreadyshowing && referenceenabledwithvalidtype) {
                displayReference = new DisplayReference(Root, currentsessionpart, itemsinsession.indexOf(currentsessionpart) == 0);
                displayReference.show();
                displayReference.setOnHidden(event -> {
                    currentsessionpart.volume_rebindentrainment();
                    if (ambienceenabled) {currentsessionpart.volume_rebindambience();}
                });
            }
        }
        public void closereferencefile() {
            if (referencecurrentlyDisplayed()) {
                displayReference.close();
            }
        }
        public boolean referencecurrentlyDisplayed() {
            return displayReference != null && displayReference.isShowing() && displayReference.EntrainmentVolumeSlider != null;
        }

        @Override
        public void close() {
            super.close();
            reset(false);
            if (Root.getStage().isIconified()) {Root.getStage().setIconified(false);}
            updategui();
            updateuitimeline.play();
        }
    }
    public class Exporter extends Stage {
        public Button CancelButton;
        public ProgressBar TotalProgress;
        public Label StatusBar;
        public ProgressBar CurrentProgress;
        public Label TotalLabel;
        public Label CurrentLabel;
        private File finalexportfile;
        private File tempentrainmenttextfile;
        private File tempambiencetextfile;
        private File tempentrainmentfile;
        private File tempambiencefile;
        private File finalentrainmentfile;
        private File finalambiencefile;
        private Integer exportserviceindex;
        private ArrayList<Service<Boolean>> exportservices;
        private Service<Boolean> currentexporterservice;

        public Exporter() {
            try {
                if (! Root.getStage().isIconified()) {Root.getStage().setIconified(true);}
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ExportingSessionDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getIcons().clear();
                getIcons().add(PROGRAM_ICON);
                String themefile = Root.getPreferences().getUserInterfaceOptions().getThemefile();
                if (themefile != null) {getScene().getStylesheets().add(themefile);}
                this.setResizable(false);
                setTitle("Exporting Session");
            } catch (IOException ignored) {}
        }

        public void unbindproperties() {
            TotalProgress.progressProperty().unbind();
            CurrentProgress.progressProperty().unbind();
            StatusBar.textProperty().unbind();
            CurrentLabel.textProperty().unbind();
        }

        public void exporter_initialize() {
        }
        public void exporter_toggle() {
//        switch (Session.exporterState) {
//            case NOT_EXPORTED:
//                break;
//            case WORKING:
//                break;
//            case FAILED:
//                break;
//            case COMPLETED:
//                break;
//            case CANCELLED:
//                break;
//            default:
//                break;
//        }
        }
        public void exporter_exportsession() {
            //        CreatorAndExporter.startexport();}
//            Util.gui_showtimedmessageonlabel(CreatorStatusBar, "Exporter Is Broken. FFMPEG Is Being A Pain In The Ass", 3000);
            //        if (creationchecks()) {
//            if (getExporterState() == ExporterState.NOT_EXPORTED) {
//                if (checkforffmpeg()) {
//                    if (session.exportfile() == null) {
//                        session.exporter_getnewexportsavefile();
//                    } else {
//                        if (session.getExportfile().exists()) {
//                            if (!Util.dialog_OKCancelConfirmation(Root, "Confirmation", "Overwrite Saved Exported Session?", "Saved Session: " + session.getExportfile().getAbsolutePath())) {
//                                session.exporter_getnewexportsavefile();
//                            }
//                        } else {session.exporter_getnewexportsavefile();}
//                    }
//                    if (session.getExportfile() == null) {Util.gui_showtimedmessageonlabel(StatusBar, "Export Session Cancelled", 3000); return;}
//                    exportserviceindex = 0;
//                    ArrayList<Cut> cutsinsession = session.getCutsinsession();
//                    for (Cut i : cutsinsession) {
//                        exportservices.add(i.getexportservice());
//                    }
//                    exportservices.add(session.exporter_getsessionexporter());
//                    exporterUI = new ExporterUI(Root);
//                    exporterUI.show();
//                    setExporterState(ExporterState.WORKING);
//                    exporter_util_movetonextexportservice();
//                } else {
//                    Util.dialog_displayError(Root, "Error", "Cannot Export. Missing FFMpeg", "Please Install FFMpeg To Use The Export Feature");
//                    // TODO Open A Browser Showing How To Install FFMPEG
//                }
//            } else if (getExporterState() == ExporterState.WORKING) {
//                Util.gui_showtimedmessageonlabel(StatusBar, "Session Currently Being Exported", 3000);
//            } else {
//                if (Util.dialog_OKCancelConfirmation(Root, "Confirmation", "Session Already Exported", "Export Again?")) {
//                    setExporterState(ExporterState.NOT_EXPORTED);
//                    startexport();
//                }
//            }
//        } else {Util.dialog_displayInformation(Root, "Information", "Cannot Export", "No Cuts Selected");}
        }
        private void exporter_util_movetonextexportservice() {
//        System.out.println("Starting Next Export Service");
//        exporterUI.TotalProgress.setProgress((double) exportserviceindex / exportservices.size());
//        try {
//            currentexporterservice = exportservices.get(exportserviceindex);
//            currentexporterservice.setOnRunning(event -> {
//                exporterUI.CurrentProgress.progressProperty().bind(currentexporterservice.progressProperty());
//                exporterUI.StatusBar.textProperty().bind(currentexporterservice.messageProperty());
//                exporterUI.CurrentLabel.textProperty().bind(currentexporterservice.titleProperty());
//            });
//            currentexporterservice.setOnSucceeded(event -> {
//                exporterUI.unbindproperties(); exportserviceindex++; exporter_util_movetonextexportservice();});
//            currentexporterservice.setOnCancelled(event -> exporter_export_cancelled());
//            currentexporterservice.setOnFailed(event -> exporter_export_failed());
//            currentexporterservice.start();
//        } catch (ArrayIndexOutOfBoundsException ignored) {
//            exporter_export_finished();}
        }
        public void exporter_export_finished() {
//        System.out.println("Export Finished!");
//        exporterState = ExporterState.COMPLETED;
        }
        public void exporter_export_cancelled() {
//        System.out.println("Cancelled!");
//        exporterState = ExporterState.CANCELLED;}
        }
        public void exporter_export_failed() {
//        System.out.println(currentexporterservice.getException().getMessage());
//        System.out.println("Failed!");
//        exporterState = ExporterState.FAILED;
        }
        public boolean exporter_cleanup() {
//        boolean currentlyexporting = exporterState == ExporterState.WORKING;
//        if (currentlyexporting) {
//            dialog_displayInformation(this, "Information", "Currently Exporting", "Wait For The Export To Finish Before Exiting");
//        } else {This_Session.exporter_deleteprevioussession();}
//        return ! currentlyexporting;
            return true;
        }


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
//                filestoexport.add(new File(Root.getPreferences().getSessionOptions().getAlertfilelocation()));
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
        public void exporter_deleteprevioussession() {
            ArrayList<File> folders = new ArrayList<>();
            folders.add(new File(Preferences.DIRECTORYTEMP, "Ambience"));
            folders.add(new File(Preferences.DIRECTORYTEMP, "Entrainment"));
            folders.add(new File(Preferences.DIRECTORYTEMP, "txt"));
            folders.add(new File(Preferences.DIRECTORYTEMP, "Export"));
            for (File i : folders) {
                try {
                    for (File x : i.listFiles()) {x.delete();}
                } catch (NullPointerException ignored) {}
            }
            try {
                for (File x : Preferences.DIRECTORYTEMP.listFiles()) {
                    if (! x.isDirectory()) {x.delete();}
                }
            } catch (NullPointerException ignored) {}
        }

        @Override
        public void close() {
            super.close();
            if (Root.getStage().isIconified()) {Root.getStage().setIconified(false);}
        }
    }

}
