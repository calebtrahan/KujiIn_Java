package kujiin.ui.playback;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.ui.MainController;
import kujiin.ui.dialogs.alerts.AnswerDialog;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.ui.table.PlaylistTableItem;
import kujiin.util.Util;
import kujiin.util.enums.PlayerState;
import kujiin.util.enums.ReferenceType;
import kujiin.xml.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import static kujiin.util.enums.PlayerState.*;

public class Player extends Stage {
// UI Elements
    // Reference
    public HBox ReferenceControls;
    public CheckBox ReferenceToggleCheckBox;
    public ChoiceBox<String> ReferenceTypeChoiceBox;
    public ScrollPane ReferenceContentPane;
    // Playlist
    public TableView<PlaylistTableItem> PlaylistTableView;
    public TableColumn<PlaylistTableItem, String> NameColumn;
    public TableColumn<PlaylistTableItem, String> DurationColumn;
    public TableColumn<PlaylistTableItem, String> PercentColumn;
    // Goals Progress
    public Label GoalTopLabel;
    public Label SessionPartElapsedTime;
    public ProgressBar CurrentGoalProgress;
    public Label CurrentGoalPercentage;
    public Label SessionPartGoalTime;
    // Session Progress
    public Label SessionCurrentTime;
    public ProgressBar SessionProgress;
    public Label SessionProgressPercentage;
    public Label SessionTotalTime;
    // Controls
    public Slider EntrainmentVolume;
    public Label EntrainmentVolumePercentage;
    public HBox AmbienceVolumeControls;
    public Slider AmbienceVolume;
    public Label AmbienceVolumePercentage;
    public Button PlayButton;
    public Button PauseButton;
    public Button StopButton;
// Animation
    private Animation fade_entrainment_play;
    private Animation fade_entrainment_resume;
    private Animation fade_entrainment_pause;
    private Animation fade_entrainment_stop;
    private Animation fade_ambience_play;
    private Animation fade_ambience_resume;
    private Animation fade_ambience_pause;
    private Animation fade_ambience_stop;
    private Animation timeline_fadeout_timer;
    private Animation timeline_progresstonextsessionpart;
    private Animation timeline_start_ending_ramp;
    private Animation updateuitimeline;
    private final Duration updateuifrequency = Duration.millis(100);
// Playback
    private MediaPlayer entrainmentplayer;
    private MediaPlayer ambienceplayer;
    private SoundFile currentambiencesoundfile;
    private Double currententrainmentvolume;
    private Double currentambiencevolume;
// Class Objects
    private Session SessionTemplate;
    private Session SessionInProgress;
    private PlaybackItem selectedPlaybackItem;
    private Goals Goals;
    private AvailableEntrainments availableEntrainments;
    private RampFiles rampfiles;
    private Sessions sessions;
    private PlayerState playerState;
    private Preferences Preferences;
    private ReferenceType referenceType;
// Toggles
    public Boolean displaynormaltime = true;
// Event Handlers
    EventHandler<KeyEvent> AmbienceSwitchWithKeyboard = new EventHandler<KeyEvent>() {
        KeyCodeCombination forwardambience = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN);
        KeyCodeCombination backambience = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN);

        @Override
        public void handle(KeyEvent event) {
            if (playerState == PLAYING) {
                if (forwardambience.match(event)) {
                    playnextambience();
                } else if (backambience.match(event)) {
                    playpreviousambiencefromhistory();
                } else if (event.getCode() == KeyCode.ESCAPE) {
                }
            }
        }
    };

    public Player(MainController Root, Sessions sessions, Session sessiontoplay) {
        try {
            Preferences = Root.getPreferences();
            SessionTemplate = sessiontoplay;
            SessionInProgress = SessionTemplate;
            availableEntrainments = Root.getAvailableEntrainments();
            rampfiles = Root.getRampFiles();
            this.sessions = sessions;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/playback/Player.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setTitle("Session Player");
            updateplaylist();
            NameColumn.setCellValueFactory(cellData -> cellData.getValue().itemname);
            DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().duration);
            PercentColumn.setCellValueFactory(cellDate -> cellDate.getValue().percentcompleted);
            PlaylistTableView.setOnMouseClicked(Event::consume);
            playerState = IDLE;
            setupTooltips();
            setOnCloseRequest(event -> closedialog());
            ReferenceTypeChoiceBox.setOnAction(event -> referencetypechanged());
            ReferenceToggleCheckBox.setOnAction(event -> ReferenceToggleCheckboxtoggled());
            setOnCloseRequest(event -> {
                PlayerState p = playerState;
                boolean animationinprogress = p == FADING_PAUSE || p == FADING_PLAY || p == FADING_RESUME || p == FADING_STOP;
                if (animationinprogress) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Cannot Close Player");
                    a.setHeaderText("Currently Pausing, Starting Playback Or Stopping");
                    a.setContentText("Please Wait Till Done Until Closing Player");
                    DialogPane dialogPane = a.getDialogPane();
                    dialogPane.getStylesheets().add(Preferences.getUserInterfaceOptions().getThemefile());
                    a.show();
                    event.consume();
                    return;
                }
                if (playerState != IDLE && playerState != STOPPED) {
                    if (! endsessionprematurely(false)) {event.consume();}
                }
            });
            SessionTotalTime.setText(Util.formatdurationtoStringDecimalWithColons(SessionInProgress.getExpectedSessionDuration()));
            PlayButton.requestFocus();
            SessionProgressPercentage.setVisible(false);
            SessionProgress.setOnMouseEntered(event -> SessionProgressPercentage.setVisible(true));
            SessionProgress.setOnMouseExited(event -> SessionProgressPercentage.setVisible(false));
            SessionProgress.setOnMouseClicked(event -> SessionProgressPercentage.setVisible(! SessionProgressPercentage.isVisible()));
            ReferenceTypeChoiceBox.setItems(FXCollections.observableArrayList(Arrays.asList("html", "txt")));
            ReferenceControls.setDisable(true);
        } catch (IOException ignored) {ignored.printStackTrace();}
    }
    public void setupTooltips() {
    }

// UI Methods
    public void playbuttonpressed() {
        switch (playerState) {
            case IDLE:
            case STOPPED:
                SessionInProgress = SessionTemplate;
//                setupKeyBoardShortcuts();
                updateuitimeline = new Timeline(new KeyFrame(updateuifrequency, ae -> updateplayerui()));
                updateuitimeline.setCycleCount(Animation.INDEFINITE);
                updateuitimeline.play();
                selectedPlaybackItem = SessionInProgress.getPlaybackItems().get(0);
                AmbienceVolumeControls.setVisible(selectedPlaybackItem.getAmbience().isEnabled());
                currententrainmentvolume = Preferences.getPlaybackOptions().getEntrainmentvolume();
                currentambiencevolume = Preferences.getPlaybackOptions().getAmbiencevolume();
                setupsession();
                start();
                break;
            case PAUSED:
                updateuitimeline.play();
                resume();
                break;
        }
    }
    public void pausebuttonpressed() {
        if (playerState == PLAYING) {
            pause();
            updateuitimeline.pause();
        }
    }
    public void stopbuttonpressed() {
        if (playerState == PLAYING || playerState == PAUSED) {
            endsessionprematurely(true);
        }
    }
    public void ReferenceToggleCheckboxtoggled() {
        if (ReferenceToggleCheckBox.isSelected()) {
            if (ReferenceTypeChoiceBox.getSelectionModel().getSelectedIndex() == -1) {
                switch (Preferences.getSessionOptions().getReferencetype()) {
                    case html:
                        ReferenceTypeChoiceBox.getSelectionModel().select(0);
                        break;
                    case txt:
                        ReferenceTypeChoiceBox.getSelectionModel().select(1);
                        break;
                    default:
                        ReferenceTypeChoiceBox.getSelectionModel().select(0);
                        break;
                }
            }
            referencetypechanged();
        } else {ReferenceContentPane.setContent(null);}
    }
    public void referencetypechanged() {
        int index = ReferenceTypeChoiceBox.getSelectionModel().getSelectedIndex();
        if (index != -1 && ! ReferenceToggleCheckBox.isSelected()) {ReferenceToggleCheckBox.setSelected(true);}
        switch (index) {
            case 0: referenceType = ReferenceType.html; loadreference(); return;
            case 1: referenceType = ReferenceType.txt; loadreference();
        }
    }
    private void loadreference() {
        try {
            File referencefile = selectedPlaybackItem.getReferenceFile(referenceType);
            if (referencefile != null) {
                switch (referenceType) {
                    case txt:
                        StringBuilder sb = new StringBuilder();
                        try (FileInputStream fis = new FileInputStream(referencefile); BufferedInputStream bis = new BufferedInputStream(fis)) {
                            while (bis.available() > 0) {
                                sb.append((char) bis.read());
                            }
                        } catch (Exception ignored) {}
                        TextArea ta = new TextArea();
                        ta.setText(sb.toString());
                        ta.setWrapText(true);
                        ReferenceContentPane.setContent(ta);
                        break;
                    case html:
                        WebView browser = new WebView();
                        WebEngine webEngine = browser.getEngine();
                        webEngine.load(referencefile.toURI().toString());
                        webEngine.setUserStyleSheetLocation(kujiin.xml.Preferences.REFERENCE_THEMEFILE.toURI().toString());
                        ReferenceContentPane.setContent(browser);
                        break;
                    default:
                        break;
                }
            } else {
                TextArea ta = new TextArea("Reference File Is Empty Or Missing");
                ta.prefWidthProperty().bind(ReferenceContentPane.widthProperty());
                ta.prefHeightProperty().bind(ReferenceContentPane.heightProperty());
                ReferenceContentPane.setContent(ta);
            }
        } catch (NullPointerException ignored) {
            TextArea ta = new TextArea("No Session Playing");
            ta.prefWidthProperty().bind(ReferenceContentPane.widthProperty());
            ta.prefHeightProperty().bind(ReferenceContentPane.heightProperty());
            ReferenceContentPane.setContent(ta);
        }
    }
    private void updateplaylist() {
        PlaylistTableView.getItems().clear();
        ObservableList<PlaylistTableItem> playlistitems = FXCollections.observableArrayList();
        PlaylistTableView.getSelectionModel().select(SessionInProgress.getPlaybackItems().indexOf(selectedPlaybackItem));
        for (PlaybackItem i : SessionInProgress.getPlaybackItems()) {
            float totalprogress = (float) i.getPracticeTime() / (float) i.getExpectedDuration();
            int percentage = new Double(totalprogress * 100).intValue();
            String progress = Util.formatdurationtoStringDecimalWithColons(new Duration(i.getPracticeTime())) + " > " + Util.formatdurationtoStringDecimalWithColons(new Duration(i.getExpectedDuration()));
            playlistitems.add(new PlaylistTableItem(i.getName(), progress, percentage + "%"));
        }
        PlaylistTableView.setItems(playlistitems);
    }

// Playback
    // UI Update
    private void updateplayerui() {
        PlayerState p = playerState;
        if (p == PLAYING || p == FADING_PLAY || p == FADING_PAUSE || p == FADING_RESUME || p == FADING_STOP) {
            try {
                selectedPlaybackItem.addelapsedtime(updateuifrequency);
                SessionInProgress.addelapseduration(updateuifrequency);
                updateplaylist();
            // Update Total Progress
                SessionCurrentTime.setText(Util.formatdurationtoStringDecimalWithColons(SessionInProgress.getSessionPracticedTime()));
                Float totalprogress;
                if (SessionInProgress.getSessionPracticedTime().greaterThan(Duration.ZERO)) {totalprogress = (float) SessionInProgress.getSessionPracticedTime().toMillis() / (float) SessionInProgress.getExpectedSessionDuration().toMillis();}
                else {totalprogress = (float) 0.0;}
                SessionProgress.setProgress(totalprogress);
                BigDecimal bd = new BigDecimal(totalprogress * 100);
                bd = bd.setScale(2, RoundingMode.HALF_UP);
                SessionProgressPercentage.setText(bd.doubleValue() + "%");
                if (displaynormaltime) {SessionTotalTime.setText(Util.formatdurationtoStringDecimalWithColons(SessionInProgress.getExpectedSessionDuration()));}
                else {SessionTotalTime.setText(Util.formatdurationtoStringDecimalWithColons(SessionInProgress.getExpectedSessionDuration().subtract(SessionInProgress.getSessionPracticedTime())));}
                updatesessionui();
                updategoalui();
            } catch (Exception ignored) {ignored.printStackTrace();}
        }
    }
    private void updatesessionui() {}
    private void updategoalui() {

    }
    private void toggleplayerbuttons() {
        if (playerState == null) {return;}
        boolean idle = playerState == PlayerState.IDLE;
        boolean playing = playerState == PlayerState.PLAYING;
        boolean paused = playerState == PlayerState.PAUSED;
        boolean stopped = playerState == PlayerState.STOPPED;
        boolean fade_play = playerState == PlayerState.FADING_PLAY;
        boolean fade_resume = playerState == PlayerState.FADING_RESUME;
        boolean fade_pause = playerState == PlayerState.FADING_PAUSE;
        boolean fade_stop = playerState == PlayerState.FADING_STOP;
        PlayButton.setDisable(playing || fade_play || fade_resume || fade_pause || fade_stop);
        PauseButton.setDisable(paused || fade_play || fade_resume || fade_pause || fade_stop || idle);
        StopButton.setDisable(stopped || fade_play || fade_resume || fade_pause || fade_stop || idle);
//        ReferenceControls.setDisable(fade_play || fade_resume || fade_pause || fade_stop);
        String playbuttontext;
        String pausebuttontext;
        String stopbuttontext;
        switch (playerState) {
            case IDLE:
                playbuttontext = "Start";
                pausebuttontext = "Pause";
                stopbuttontext = "Stop";
                break;
            case PLAYING:
                playbuttontext = "Playing";
                pausebuttontext = "Pause";
                stopbuttontext = "Stop";
                break;
            case PAUSED:
                playbuttontext = "Resume";
                pausebuttontext = "Paused";
                stopbuttontext = "Stop";
                break;
            case STOPPED:
                playbuttontext = "Play";
                pausebuttontext = "Stopped";
                stopbuttontext = "Stopped";
                break;
            case TRANSITIONING:
                playbuttontext = "Transitioning";
                pausebuttontext = "Transitioning";
                stopbuttontext = "Transitioning";
                break;
            case FADING_PLAY:
                playbuttontext = "Starting";
                pausebuttontext = "Starting";
                stopbuttontext = "Starting";
                break;
            case FADING_RESUME:
                playbuttontext = "Resuming";
                pausebuttontext = "Resuming";
                stopbuttontext = "Resuming";
                break;
            case FADING_PAUSE:
                playbuttontext = "Pausing";
                pausebuttontext = "Pausing";
                stopbuttontext = "Pausing";
                break;
            case FADING_STOP:
                playbuttontext = "Stopping";
                pausebuttontext = "Stopping";
                stopbuttontext = "Stopping";
                break;
            default:
                playbuttontext = "";
                pausebuttontext = "";
                stopbuttontext = "";
                break;
        }
        PlayButton.setText(playbuttontext);
        PauseButton.setText(pausebuttontext);
        StopButton.setText(stopbuttontext);
//        if (referencecurrentlyDisplayed()) {
//            root.getSessionCreator().getDisplayReference().PlayButton.setText(playbuttontext);
//            root.getSessionCreator().getDisplayReference().PauseButton.setText(pausebuttontext);
//            root.getSessionCreator().getDisplayReference().StopButton.setText(stopbuttontext);
//        }
        toggleplayervolumecontrols();
    }
    private void toggleplayervolumecontrols() {
        boolean enabled = playerState == PlayerState.PLAYING;
        EntrainmentVolume.setDisable(! enabled);
        if (selectedPlaybackItem.getAmbience().isEnabled()) {AmbienceVolume.setDisable(! enabled);}
    }
    // Playback Methods
    private void setupsession() {
        PlaylistTableView.getSelectionModel().select(0);
        PlaylistTableView.setOnMouseClicked(event -> PlaylistTableView.getSelectionModel().select(-1));
        selectedPlaybackItem = SessionInProgress.getPlaybackItems().get(0);
        SessionInProgress.setSessionPracticedTime(0.0);
        SessionInProgress.calculateactualduration();
        setupfadeanimations();
        volume_unbindentrainment();
    }
    private void start() {
        setupfadeanimations();
        ReferenceControls.setDisable(false);
        PlaybackItemEntrainment playbackItemEntrainment = availableEntrainments.getsessionpartEntrainment(selectedPlaybackItem);
        if (! selectedPlaybackItem.isRampOnly() || selectedPlaybackItem.getPlaybackindex() == SessionInProgress.getPlaybackItems().size() - 1) {entrainmentplayer = new MediaPlayer(new Media(playbackItemEntrainment.getFreq().getFile().toURI().toString()));}
        else {entrainmentplayer = new MediaPlayer(new Media(rampfiles.getRampFile(selectedPlaybackItem, SessionInProgress.getPlaybackItems().get(SessionInProgress.getPlaybackItems().indexOf(selectedPlaybackItem) + 1)).getFile().toURI().toString()));}
        entrainmentplayer.setVolume(0.0);
        if (! selectedPlaybackItem.isRampOnly()) {entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);}
        entrainmentplayer.setOnError(this::entrainmenterror);
        entrainmentplayer.play();
        timeline_progresstonextsessionpart = new Timeline(new KeyFrame(new Duration(selectedPlaybackItem.getExpectedDuration()), ae -> progresstonextsessionpart()));
        timeline_progresstonextsessionpart.play();
        boolean isLastSessionPart = SessionInProgress.getPlaybackItems().indexOf(selectedPlaybackItem) == SessionInProgress.getPlaybackItems().size() - 1;
        if (! selectedPlaybackItem.isRampOnly() && ! isLastSessionPart && Preferences.getSessionOptions().getRampenabled()) {
            SoundFile rampfile = rampfiles.getRampFile(selectedPlaybackItem, SessionInProgress.getPlaybackItems().get(SessionInProgress.getPlaybackItems().indexOf(selectedPlaybackItem) + 1));
            System.out.println("Ramp File Is Null: " + Boolean.toString(rampfile == null));
            timeline_start_ending_ramp = new Timeline(new KeyFrame(new Duration(selectedPlaybackItem.getExpectedDuration()).subtract(Duration.millis(rampfile.getDuration())), ae -> {
                volume_unbindentrainment();
                entrainmentplayer.stop();
                entrainmentplayer.dispose();
                entrainmentplayer = new MediaPlayer(new Media(rampfile.getFile().toURI().toString()));
                entrainmentplayer.setOnError(this::entrainmenterror);
                entrainmentplayer.setVolume(currententrainmentvolume);
                entrainmentplayer.play();
                entrainmentplayer.setOnPlaying(this::volume_bindentrainment);
            }));
            timeline_start_ending_ramp.play();
        }
        if (fade_entrainment_stop != null) {
            Duration startfadeout = new Duration(selectedPlaybackItem.getExpectedDuration());
            if (selectedPlaybackItem.isRampOnly()) {startfadeout = startfadeout.subtract(Duration.seconds(kujiin.xml.Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
            else {startfadeout = startfadeout.subtract(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_stop_value()));}
            timeline_fadeout_timer = new Timeline(new KeyFrame(startfadeout, ae -> {
                volume_unbindentrainment();
                fade_entrainment_stop.play();
                if (fade_ambience_stop != null) {
                    volume_unbindambience();
                    fade_ambience_stop.play();
                }
            }));
            timeline_fadeout_timer.play();
        }
        if (fade_entrainment_play != null) {
            if (fade_entrainment_play.getStatus() == Animation.Status.RUNNING) {return;}
            playerState = kujiin.util.enums.PlayerState.FADING_PLAY;
            fade_entrainment_play.play();
        } else {
            entrainmentplayer.setVolume(currententrainmentvolume);
            String percentage = new Double(currententrainmentvolume * 100).intValue() + "%";
            EntrainmentVolumePercentage.setText(percentage);
            playerState = kujiin.util.enums.PlayerState.PLAYING;
            volume_bindentrainment();
        }
        if (selectedPlaybackItem.getAmbience().isEnabled()) {
            currentambiencevolume = Preferences.getPlaybackOptions().getAmbiencevolume();
            volume_unbindambience();
            currentambiencesoundfile = selectedPlaybackItem.getAmbience().get(0);
            ambienceplayer = new MediaPlayer(new Media(currentambiencesoundfile.getFile().toURI().toString()));
            ambienceplayer.setVolume(0.0);
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            ambienceplayer.play();
            if (fade_ambience_play != null) {fade_ambience_play.play();}
            else {
                System.out.println("Fade_Ambience_Play Is Null");
                ambienceplayer.setVolume(currentambiencevolume);
                String percentage = new Double(currentambiencevolume * 100).intValue() + "%";
                AmbienceVolumePercentage.setText(percentage);
                volume_bindambience();
            }
        }
        toggleplayerbuttons();
        getScene().addEventHandler(KeyEvent.KEY_PRESSED, AmbienceSwitchWithKeyboard);
    }
    private void resume() {
        volume_unbindentrainment();
        entrainmentplayer.play();
        if (fade_entrainment_resume != null) {
            entrainmentplayer.setVolume(0.0);
            if (fade_entrainment_resume.getStatus() == Animation.Status.RUNNING) {return;}
            playerState = FADING_RESUME;
            fade_entrainment_resume.play();
        } else {
            entrainmentplayer.setVolume(currententrainmentvolume);
            volume_bindentrainment();
            playerState = PLAYING;
            timeline_progresstonextsessionpart.play();
            if (Preferences.getSessionOptions().getRampenabled() && timeline_start_ending_ramp.getStatus() == Animation.Status.PAUSED) {
                timeline_start_ending_ramp.play();}
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.play();}
        }
        if (selectedPlaybackItem.getAmbience().isEnabled()) {
            volume_unbindambience();
            ambienceplayer.play();
            if (fade_ambience_resume != null) {
                ambienceplayer.setVolume(0.0);
                if (fade_ambience_resume.getStatus() == Animation.Status.RUNNING) {return;}
                fade_ambience_resume.play();
            } else {
                ambienceplayer.setVolume(currentambiencevolume);
                volume_bindambience();
            }
        }
        toggleplayerbuttons();
    }
    private void pause() {
        volume_unbindentrainment();
        if (fade_entrainment_pause != null) {
            if (selectedPlaybackItem.getAmbience().isEnabled() && fade_ambience_pause.getStatus() == Animation.Status.RUNNING) {return;}
            playerState = FADING_PAUSE;
            fade_entrainment_pause.play();
            if (selectedPlaybackItem.getAmbience().isEnabled()) {
                volume_unbindambience();
                fade_ambience_pause.play();
            }
        } else {pausewithoutanimation();}
        toggleplayerbuttons();
    }
    private void pausewithoutanimation() {
        playerState = PAUSED;
        entrainmentplayer.pause();
        timeline_progresstonextsessionpart.pause();
        if (Preferences.getSessionOptions().getRampenabled() && timeline_start_ending_ramp != null && timeline_start_ending_ramp.getStatus() == Animation.Status.RUNNING) {
            timeline_start_ending_ramp.pause();}
        if (timeline_fadeout_timer != null) {timeline_fadeout_timer.pause();}
        if (selectedPlaybackItem.getAmbience().isEnabled()) {
            volume_unbindambience();
            ambienceplayer.pause();
        }
    }
    private void playnextentrainment() {
        try {
            volume_unbindentrainment();
            entrainmentplayer.dispose();
            entrainmentplayer = null;
            entrainmentplayer = new MediaPlayer(new Media(availableEntrainments.getsessionpartEntrainment(selectedPlaybackItem).getFreq().getFile().toURI().toString()));
            entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
            entrainmentplayer.setOnError(this::entrainmenterror);
            entrainmentplayer.setVolume(currententrainmentvolume);
            entrainmentplayer.play();
            entrainmentplayer.setOnPlaying(this::volume_bindentrainment);
        } catch (IndexOutOfBoundsException ignored) {
            entrainmentplayer.dispose();
            cleanupPlayersandAnimations();
        }
    }
    private void playnextambience() {
        try {
            volume_unbindambience();
            ambienceplayer.dispose();
            ambienceplayer = null;
            currentambiencesoundfile = selectedPlaybackItem.getAmbience().getnextambienceforplayback();
            ambienceplayer = new MediaPlayer(new Media(currentambiencesoundfile.getFile().toURI().toString()));
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            ambienceplayer.setVolume(currentambiencevolume);
            ambienceplayer.play();
            ambienceplayer.setOnPlaying(this::volume_bindambience);
        } catch (IndexOutOfBoundsException ignored) {ambienceplayer.dispose();}
    }
    public void playpreviousambiencefromhistory() {
        SoundFile previousambiencefile = selectedPlaybackItem.getAmbience().getpreviousambiencehistory();
        if (previousambiencefile != null) {
            volume_unbindambience();
            ambienceplayer = new MediaPlayer(new Media(previousambiencefile.getFile().toURI().toString()));
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            ambienceplayer.setVolume(currentambiencevolume);
            ambienceplayer.play();
            ambienceplayer.setOnPlaying(this::volume_bindambience);
        }
    }
    public void playnextambiencefromhistory() {
        SoundFile nextambiencefile = selectedPlaybackItem.getAmbience().getnextambiencehistory();
        if (nextambiencefile != null) {
            volume_unbindambience();
            ambienceplayer = new MediaPlayer(new Media(nextambiencefile.getFile().toURI().toString()));
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            ambienceplayer.setVolume(currentambiencevolume);
            ambienceplayer.play();
            ambienceplayer.setOnPlaying(this::volume_bindambience);
        }
    }
    private void stop() {
        volume_unbindentrainment();
        if (fade_entrainment_stop != null) {
            if (fade_entrainment_stop.getStatus() == Animation.Status.RUNNING) {return;}
            fade_entrainment_stop.play();
            playerState = FADING_STOP;
            if (selectedPlaybackItem.getAmbience().isEnabled()) {
                volume_unbindambience();
                fade_ambience_stop.play();
            }
        } else {
            playerState = STOPPED;
            entrainmentplayer.stop();
            entrainmentplayer.dispose();
            timeline_progresstonextsessionpart.stop();
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop();}
            if (selectedPlaybackItem.getAmbience().isEnabled()) {
                volume_unbindambience();
                ambienceplayer.stop();
                ambienceplayer.dispose();
            }
        }
        toggleplayerbuttons();
    }
    public void progresstonextsessionpart() {
        try {
            switch (playerState) {
                case TRANSITIONING:
                    try {
                        cleanupPlayersandAnimations();
                        int index = SessionInProgress.getPlaybackItems().indexOf(selectedPlaybackItem) + 1;
                        selectedPlaybackItem = SessionInProgress.getPlaybackItems().get(index);
                        PlaylistTableView.getSelectionModel().select(index);
                        start();
                        if (ReferenceToggleCheckBox.isSelected() && ReferenceTypeChoiceBox.getSelectionModel().getSelectedIndex() != -1) {loadreferencecontent();}
                    } catch (IndexOutOfBoundsException ignored) {
                        cleanupPlayersandAnimations();
                        playerState = IDLE;
                        endofsession();
                    }
                    break;
                case PLAYING:
                    ReferenceControls.setDisable(true);
                    transition();
                    break;
            }
        } catch (Exception ignored) {}
    }
    public void transition() {
        selectedPlaybackItem.updateduration(new Duration(selectedPlaybackItem.getExpectedDuration()));
        updatesessionui();
        updategoalui();
        if (Preferences.getSessionOptions().getAlertfunction()) {
            Media alertmedia = new Media(Preferences.getSessionOptions().getAlertfilelocation());
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
        if (endofsession) {
            PlayButton.setText("Replay");
            PauseButton.setText("Stop");
            PauseButton.setDisable(true);
            StopButton.setText("Stop");
            StopButton.setDisable(true);
        }
        else {PlayButton.setText("Start");}
        SessionProgress.setProgress(0.0);
        SessionProgressPercentage.setText("0.0%");
        SessionProgress.setDisable(true);
        EntrainmentVolume.setDisable(true);
        EntrainmentVolume.setValue(0.0);
        EntrainmentVolumePercentage.setText("0%");
        AmbienceVolume.setDisable(true);
        AmbienceVolume.setValue(0.0);
        AmbienceVolumePercentage.setText("0%");
        PauseButton.setDisable(true);
        StopButton.setDisable(true);
    }
    public void endofsession() {
        updateuitimeline.stop();
        updateuitimeline.setOnFinished(event -> reset(false));
        playerState = STOPPED;
        sessions.add(SessionInProgress);
        // TODO Prompt For Export
        updatesessionui();
        updategoalui();
        SessionInProgress = null;
        reset(true);
        SessionComplete sessionComplete = new SessionComplete(SessionInProgress, true);
        sessionComplete.initModality(Modality.APPLICATION_MODAL);
        sessionComplete.showAndWait();
    }
    public boolean endsessionprematurely(boolean resetdialogcontrols) {
        pausewithoutanimation();
        updateuitimeline.pause();
        if (new ConfirmationDialog(Preferences, "End Session Early", "Session Is Not Completed.", "End Session Prematurely?", "End Session", "Continue").getResult()) {
            sessions.add(SessionInProgress);
            if (resetdialogcontrols) {
                updatesessionui();
                updategoalui();
                reset(true);
                cleanupPlayersandAnimations();
            }
            SessionComplete sessionComplete = new SessionComplete(SessionInProgress, false);
            sessionComplete.initModality(Modality.APPLICATION_MODAL);
            sessionComplete.showAndWait();
            return true;
        } else {playbuttonpressed(); return false;}
    }
    public void togglevolumebinding() {
        if (selectedPlaybackItem != null && (playerState == IDLE || playerState == STOPPED)) {
            volume_rebindentrainment();
            if (selectedPlaybackItem.getAmbience().isEnabled()) {volume_rebindambience();}
        }
    }
    // Animation
    private void setupfadeanimations() {
        // PLAY
        if (selectedPlaybackItem.isRampOnly() || (Preferences.getPlaybackOptions().getAnimation_fade_play_enabled() && Preferences.getPlaybackOptions().getAnimation_fade_play_value() > 0.0)) {
            fade_entrainment_play = new Transition() {
                {if (selectedPlaybackItem.isRampOnly()) {setCycleDuration(Duration.seconds(kujiin.xml.Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
                else {setCycleDuration(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_play_value()));}}

                @Override
                protected void interpolate(double frac) {
                    if (entrainmentplayer != null) {
                        double entrainmentvolume = frac * currententrainmentvolume;
                        String percentage = new Double(entrainmentvolume * 100).intValue() + "%";
                        entrainmentplayer.setVolume(entrainmentvolume);
                        EntrainmentVolume.setValue(entrainmentvolume);
                        EntrainmentVolumePercentage.setText(percentage);
//                        if (referencecurrentlyDisplayed()) {
//                            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setValue(entrainmentvolume);
//                            root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
//                        }
                    }
                }
            };
            fade_entrainment_play.setOnFinished(event -> {
                playerState = PLAYING;
                toggleplayerbuttons();
                volume_bindentrainment();
            });
            if (selectedPlaybackItem.getAmbience().isEnabled()) {
                fade_ambience_play = new Transition() {
                    {if (selectedPlaybackItem.isRampOnly()) {setCycleDuration(Duration.seconds(kujiin.xml.Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
                    else {setCycleDuration(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_play_value()));}}

                    @Override
                    protected void interpolate(double frac) {
                        if (ambienceplayer != null) {
                            double ambiencevolume = frac * currentambiencevolume;
                            String percentage = new Double(ambiencevolume * 100).intValue() + "%";
                            ambienceplayer.setVolume(ambiencevolume);
                            AmbienceVolume.setValue(ambiencevolume);
                            AmbienceVolumePercentage.setText(percentage);
//                            if (referencecurrentlyDisplayed()) {
//                                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setValue(ambiencevolume);
//                                root.getSessionCreator().getDisplayReference().AmbienceVolumePercentage.setText(percentage);
//                            }
                        }
                    }
                };
                fade_ambience_play.setOnFinished(event -> volume_bindambience());
            }
        }
        // RESUME
        if (Preferences.getPlaybackOptions().getAnimation_fade_resume_enabled() && Preferences.getPlaybackOptions().getAnimation_fade_resume_value() > 0.0) {
            fade_entrainment_resume = new Transition() {
                {setCycleDuration(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_resume_value()));}

                @Override
                protected void interpolate(double frac) {
                    if (entrainmentplayer != null) {
                        double entrainmentvolume = frac * currententrainmentvolume;
                        String percentage = new Double(entrainmentvolume * 100).intValue() + "%";
                        entrainmentplayer.setVolume(entrainmentvolume);
                        EntrainmentVolume.setValue(entrainmentvolume);
                        EntrainmentVolumePercentage.setText(percentage);
//                        if (referencecurrentlyDisplayed()) {
//                            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setValue(entrainmentvolume);
//                            root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
//                        }
                    }
                }
            };
            fade_entrainment_resume.setOnFinished(event -> {
                playerState = PLAYING;
                timeline_progresstonextsessionpart.play();
                if (Preferences.getSessionOptions().getRampenabled() && (timeline_start_ending_ramp != null && timeline_start_ending_ramp.getStatus() == Animation.Status.PAUSED)) {
                    timeline_start_ending_ramp.play();
                }
                if (timeline_fadeout_timer != null) {
                    timeline_fadeout_timer.play();
                }
                toggleplayerbuttons();
                volume_bindentrainment();
            });
            if (selectedPlaybackItem.getAmbience().isEnabled()) {
                fade_ambience_resume = new Transition() {
                    {setCycleDuration(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_resume_value()));}

                    @Override
                    protected void interpolate(double frac) {
                        if (ambienceplayer != null) {
                            double ambiencevolume = frac * currentambiencevolume;
                            String percentage = new Double(ambiencevolume * 100).intValue() + "%";
                            ambienceplayer.setVolume(ambiencevolume);
                            AmbienceVolume.setValue(ambiencevolume);
                            AmbienceVolumePercentage.setText(percentage);
//                            if (referencecurrentlyDisplayed()) {
//                                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setValue(ambiencevolume);
//                                root.getSessionCreator().getDisplayReference().AmbienceVolumePercentage.setText(percentage);
//                            }
                        }
                    }
                };
                fade_ambience_resume.setOnFinished(event -> volume_bindambience());
            }
        }
        // PAUSE
        if (Preferences.getPlaybackOptions().getAnimation_fade_pause_enabled() && Preferences.getPlaybackOptions().getAnimation_fade_pause_value() > 0.0) {
            fade_entrainment_pause = new Transition() {
                {setCycleDuration(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_pause_value()));}

                @Override
                protected void interpolate(double frac) {
                    if (entrainmentplayer != null) {
                        double fadeoutvolume = currententrainmentvolume - (frac * currententrainmentvolume);
                        String percentage = new Double(fadeoutvolume * 100).intValue() + "%";
                        entrainmentplayer.setVolume(fadeoutvolume);
                        EntrainmentVolume.setValue(fadeoutvolume);
                        EntrainmentVolumePercentage.setText(percentage);
//                        if (referencecurrentlyDisplayed()) {
//                            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setValue(fadeoutvolume);
//                            root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
//                        }
                    }
                }
            };
            fade_entrainment_pause.setOnFinished(event -> {
                entrainmentplayer.pause();
                timeline_progresstonextsessionpart.pause();
                updateuitimeline.pause();
                if (Preferences.getSessionOptions().getRampenabled() && (timeline_start_ending_ramp != null && timeline_start_ending_ramp.getStatus() == Animation.Status.RUNNING)) {timeline_start_ending_ramp.pause();}
                if (timeline_fadeout_timer != null) {timeline_fadeout_timer.pause();}
                playerState = PAUSED;
                toggleplayerbuttons();
            });
            if (selectedPlaybackItem.getAmbience().isEnabled()) {
                fade_ambience_pause = new Transition() {
                    {
                        setCycleDuration(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_pause_value()));
                    }

                    @Override
                    protected void interpolate(double frac) {
                        if (ambienceplayer != null) {
                            double fadeoutvolume = currentambiencevolume - (frac * currentambiencevolume);
                            String percentage = new Double(fadeoutvolume * 100).intValue() + "%";
                            ambienceplayer.setVolume(fadeoutvolume);
                            AmbienceVolume.setValue(fadeoutvolume);
                            AmbienceVolumePercentage.setText(percentage);
//                            if (referencecurrentlyDisplayed()) {
//                                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setValue(fadeoutvolume);
//                                root.getSessionCreator().getDisplayReference().AmbienceVolumePercentage.setText(percentage);
//                            }
                        }
                    }
                };
                fade_ambience_pause.setOnFinished(event -> ambienceplayer.pause());
            }
        }
        // STOP
        if (selectedPlaybackItem.isRampOnly() || (Preferences.getPlaybackOptions().getAnimation_fade_stop_enabled() && Preferences.getPlaybackOptions().getAnimation_fade_stop_value() > 0.0)) {
            fade_entrainment_stop = new Transition() {
                {if (selectedPlaybackItem.isRampOnly()) {setCycleDuration(Duration.seconds(kujiin.xml.Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
                else {setCycleDuration(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_stop_value()));}}

                @Override
                protected void interpolate(double frac) {
                    if (entrainmentplayer != null) {
                        double fadeoutvolume = currententrainmentvolume - (frac * currententrainmentvolume);
                        String percentage = new Double(fadeoutvolume * 100).intValue() + "%";
                        entrainmentplayer.setVolume(fadeoutvolume);
                        EntrainmentVolume.setValue(fadeoutvolume);
                        EntrainmentVolumePercentage.setText(percentage);
//                        if (referencecurrentlyDisplayed()) {
//                            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setValue(fadeoutvolume);
//                            root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
//                        }
                    }
                }
            };
            fade_entrainment_stop.setOnFinished(event -> {
                entrainmentplayer.stop();
                entrainmentplayer.dispose();
                if (Preferences.getSessionOptions().getRampenabled() && timeline_start_ending_ramp != null && timeline_start_ending_ramp.getStatus() == Animation.Status.RUNNING) {timeline_start_ending_ramp.stop();}
                updateuitimeline.stop();
                timeline_progresstonextsessionpart.stop();
                if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop();}
                playerState = STOPPED;
                toggleplayerbuttons();
            });
            if (selectedPlaybackItem.getAmbience().isEnabled()) {
                fade_ambience_stop = new Transition() {
                    {if (selectedPlaybackItem.isRampOnly()) {setCycleDuration(Duration.seconds(kujiin.xml.Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
                    else {setCycleDuration(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_stop_value()));}}

                    @Override
                    protected void interpolate(double frac) {
                        if (ambienceplayer != null) {
                            double fadeoutvolume = currentambiencevolume - (frac * currentambiencevolume);
                            String percentage = new Double(fadeoutvolume * 100).intValue() + "%";
                            ambienceplayer.setVolume(fadeoutvolume);
                            AmbienceVolume.setValue(fadeoutvolume);
                            AmbienceVolumePercentage.setText(percentage);
//                            if (referencecurrentlyDisplayed()) {
//                                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setValue(fadeoutvolume);
//                                root.getSessionCreator().getDisplayReference().AmbienceVolumePercentage.setText(percentage);
//                            }
                        }
                    }
                };
                fade_ambience_stop.setOnFinished(event -> {
                    ambienceplayer.stop();
                    ambienceplayer.dispose();
                });
            }
        }
    }
    private void cleanupPlayersandAnimations() {
        try {
            volume_unbindentrainment();
            volume_unbindambience();
            if (entrainmentplayer != null) {entrainmentplayer.stop(); entrainmentplayer.dispose(); entrainmentplayer = null;}
            if (ambienceplayer != null) {ambienceplayer.stop(); ambienceplayer.dispose(); ambienceplayer = null;}
            if (fade_entrainment_play != null) {fade_entrainment_play.stop(); fade_entrainment_play = null;}
            if (fade_entrainment_pause != null) {fade_entrainment_pause.stop(); fade_entrainment_pause = null;}
            if (fade_entrainment_resume != null) {fade_entrainment_resume.stop(); fade_entrainment_resume = null;}
            if (fade_entrainment_stop != null) {fade_entrainment_stop.stop(); fade_entrainment_stop = null;}
            if (fade_ambience_play != null) {fade_ambience_play.stop(); fade_ambience_play = null;}
            if (fade_ambience_pause != null) {fade_ambience_pause.stop(); fade_ambience_pause = null;}
            if (fade_ambience_resume != null) {fade_ambience_resume.stop(); fade_ambience_resume = null;}
            if (fade_ambience_stop != null) {fade_ambience_stop.stop(); fade_ambience_stop = null;}
            if (timeline_progresstonextsessionpart != null) {timeline_progresstonextsessionpart.stop(); timeline_progresstonextsessionpart = null;}
            if (timeline_start_ending_ramp != null) {timeline_fadeout_timer.stop(); timeline_start_ending_ramp = null;}
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop(); timeline_fadeout_timer = null;}
            toggleplayerbuttons();
//            System.out.println(name + "'s PlaybackItemEntrainment Player Status: " + entrainmentplayer.getStatus());
//            System.out.println(name + "'s Ambience Player Status: " + ambienceplayer.getStatus());
        } catch (Exception ignored) {}
    }
    private void volume_bindentrainment() {
        EntrainmentVolume.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
        EntrainmentVolume.setDisable(false);
        EntrainmentVolume.setOnMouseDragged(event1 -> {
            String percentage = new Double(EntrainmentVolume.getValue() * 100).intValue() + "%";
            currententrainmentvolume =EntrainmentVolume.getValue();
            EntrainmentVolumePercentage.setText(percentage);
//            if (referencecurrentlyDisplayed()) {
//                root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.valueProperty().unbindBidirectional(entrainmentplayer.volumeProperty());
//                root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setValue(currententrainmentvolume);
//                root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
//                root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
//            }
        });
        EntrainmentVolume.setOnScroll(event -> {
            Double newvalue =EntrainmentVolume.getValue();
            if (event.getDeltaY() < 0) {newvalue -= kujiin.xml.Preferences.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
            else {newvalue += kujiin.xml.Preferences.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
            if (newvalue <= 1.0 && newvalue >= 0.0) {
                Double roundedvalue = Util.round_nearestmultipleof5(newvalue * 100);
                String percentage = roundedvalue.intValue() + "%";
                currententrainmentvolume = roundedvalue / 100;
                EntrainmentVolume.setValue(roundedvalue / 100);
                EntrainmentVolume.setTooltip(new Tooltip(percentage));
                EntrainmentVolumePercentage.setText(percentage);
//                if (referencecurrentlyDisplayed()) {
//                    root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.valueProperty().unbindBidirectional(entrainmentplayer.volumeProperty());
//                    root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setValue(currententrainmentvolume);
//                    root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
//                    root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
//                }
            }
        });
//        if (referencecurrentlyDisplayed()) {
//            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
//            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setDisable(false);
//            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setOnMouseDragged(event1 -> {
//                String percentage = new Double(root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.getValue() * 100).intValue() + "%";
//                currententrainmentvolume =EntrainmentVolume.getValue();
//                setCurrententrainmentvolume(currententrainmentvolume);
//                root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
//                EntrainmentVolume.valueProperty().unbindBidirectional(entrainmentplayer.volumeProperty());
//                EntrainmentVolume.setValue(currententrainmentvolume);
//                EntrainmentVolume.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
//                EntrainmentVolumePercentage.setText(percentage);
//            });
//            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setOnScroll(event -> {
//                Double newvalue = root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.getValue();
//                if (event.getDeltaY() < 0) {newvalue -= Preferences.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
//                else {newvalue += Preferences.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
//                if (newvalue <= 1.0 && newvalue >= 0.0) {
//                    Double roundedvalue = Util.round_nearestmultipleof5(newvalue * 100);
//                    String percentage = roundedvalue.intValue() + "%";
//                    currententrainmentvolume = roundedvalue / 100;
//                    setCurrententrainmentvolume(currententrainmentvolume);
//                    root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.valueProperty().unbindBidirectional(entrainmentplayer.volumeProperty());
//                    root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setValue(currententrainmentvolume);
//                    root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
//                    root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
//                    EntrainmentVolume.valueProperty().unbindBidirectional(entrainmentplayer.volumeProperty());
//                    EntrainmentVolume.setValue(currententrainmentvolume);
//                    EntrainmentVolume.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
//                    EntrainmentVolumePercentage.setText(percentage);
//                }
//            });
//        }
    }
    private void volume_unbindentrainment() {
        try {
            EntrainmentVolume.valueProperty().unbindBidirectional(entrainmentplayer.volumeProperty());
            EntrainmentVolume.setDisable(true);
//            if (referencecurrentlyDisplayed()) {
//                root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.valueProperty().unbindBidirectional(entrainmentplayer.volumeProperty());
//                root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setDisable(true);
//            }
        } catch (NullPointerException ignored) {}
    }
    private void volume_bindambience() {
        AmbienceVolume.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
        AmbienceVolume.setDisable(false);
        AmbienceVolume.setOnMouseDragged(event1 -> {
            String percentage = new Double(AmbienceVolume.getValue() * 100).intValue() + "%";
            currentambiencevolume = AmbienceVolume.getValue();
            AmbienceVolumePercentage.setText(percentage);
            AmbienceVolume.setTooltip(new Tooltip(percentage));
//            if (referencecurrentlyDisplayed()) {
//                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.valueProperty().unbindBidirectional(ambienceplayer.volumeProperty());
//                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setValue(currentambiencevolume);
//                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
//                root.getSessionCreator().getDisplayReference().AmbienceVolumePercentage.setText(percentage);
//            }
        });
        AmbienceVolume.setOnScroll(event -> {
            Double newvalue =AmbienceVolume.getValue();
            if (event.getDeltaY() < 0) {newvalue -= kujiin.xml.Preferences.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
            else {newvalue += kujiin.xml.Preferences.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
            if (newvalue <= 1.0 && newvalue >= 0.0) {
                Double roundedvalue = Util.round_nearestmultipleof5(newvalue * 100);
                String percentage = roundedvalue.intValue() + "%";
                currentambiencevolume = roundedvalue / 100;
                AmbienceVolume.setValue(roundedvalue / 100);
                AmbienceVolume.setTooltip(new Tooltip(percentage));
                AmbienceVolumePercentage.setText(percentage);
//                if (referencecurrentlyDisplayed()) {
//                    root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.valueProperty().unbindBidirectional(ambienceplayer.volumeProperty());
//                    root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setValue(currentambiencevolume);
//                    root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
//                    root.getSessionCreator().getDisplayReference().AmbienceVolumePercentage.setText(percentage);
//                }
            }
        });
//        if (referencecurrentlyDisplayed()) {
//            root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
//            root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setDisable(false);
//            root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setOnMouseDragged(event1 -> {
//                String percentage = new Double(root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.getValue() * 100).intValue() + "%";
//                currentambiencevolume =AmbienceVolume.getValue();
//                setCurrentambiencevolume(currentambiencevolume);
//                root.getSessionCreator().getDisplayReference().AmbienceVolumePercentage.setText(percentage);
//                AmbienceVolume.valueProperty().unbindBidirectional(ambienceplayer.volumeProperty());
//                AmbienceVolume.setValue(currentambiencevolume);
//                AmbienceVolume.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
//                AmbienceVolumePercentage.setText(percentage);
//            });
//            root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setOnScroll(event -> {
//                Double newvalue = root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.getValue();
//                if (event.getDeltaY() < 0) {newvalue -= Preferences.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
//                else {newvalue += Preferences.VOLUME_SLIDER_ADJUSTMENT_INCREMENT / 100;}
//                if (newvalue <= 1.0 && newvalue >= 0.0) {
//                    Double roundedvalue = Util.round_nearestmultipleof5(newvalue * 100);
//                    String percentage = roundedvalue.intValue() + "%";
//                    currentambiencevolume = roundedvalue / 100;
//                    setCurrentambiencevolume(roundedvalue / 100);
//                    root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.valueProperty().unbindBidirectional(ambienceplayer.volumeProperty());
//                    root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setValue(roundedvalue / 100);
//                    root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
//                    root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setTooltip(new Tooltip(percentage));
//                    root.getSessionCreator().getDisplayReference().AmbienceVolumePercentage.setText(percentage);
//                    AmbienceVolume.valueProperty().unbindBidirectional(ambienceplayer.volumeProperty());
//                    AmbienceVolume.setValue(currentambiencevolume);
//                    AmbienceVolume.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
//                    AmbienceVolumePercentage.setText(percentage);
//                }
//            });
//        }
    }
    private void volume_unbindambience() {
        try {
            AmbienceVolume.valueProperty().unbindBidirectional(ambienceplayer.volumeProperty());
            AmbienceVolume.setDisable(true);
//            if (referencecurrentlyDisplayed()) {
//                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.valueProperty().unbindBidirectional(ambienceplayer.volumeProperty());
//                root.getSessionCreator().getDisplayReference().AmbienceVolumeSlider.setDisable(true);
//            }
        } catch (NullPointerException ignored) {}
    }
    private void volume_rebindambience() {volume_unbindambience(); volume_bindambience();}
    private void volume_rebindentrainment() {volume_unbindentrainment(); volume_bindentrainment();}
    // Error Handling
    private void entrainmenterror() {
        System.out.println("PlaybackItemEntrainment Error");
        // Pause Ambience If Exists
        switch (new AnswerDialog(Preferences, this, "PlaybackItemEntrainment Playback Error", null, "An Error Occured While Playing " + selectedPlaybackItem.getName() +
                "'s PlaybackItemEntrainment. Problem File Is: '" + entrainmentplayer.getMedia().getSource() + "'",
                "Retry Playback", "Mute PlaybackItemEntrainment", "Stop SessionInProgress Playback").getResult()) {
            case YES:
                entrainmentplayer.stop();
                entrainmentplayer.play();
                entrainmentplayer.setOnError(this::entrainmenterror);
                break;
            case CANCEL:
//                player_error();
                break;
        }
    }
    private void ambienceerror() {
        System.out.println("Ambience Error!");
        // Pause PlaybackItemEntrainment
        switch (new AnswerDialog(Preferences, this, "Ambience Playback Error", null, "An Error Occured While Playing " + selectedPlaybackItem.getName() +
                "'s Ambience. Problem File Is: '" + ambienceplayer.getMedia().getSource() + "'",
                "Retry Playback", "Mute Ambience", "Stop SessionInProgress Playback").getResult()) {
            case YES:
                ambienceplayer.stop();
                ambienceplayer.play();
                ambienceplayer.setOnError(this::ambienceerror);
                break;
            case NO:
                ambienceplayer.stop();
            case CANCEL:
//                player_error();
                break;
        }
    }

// Reference
    public void togglereference() {
        boolean buttontoggled = ReferenceToggleCheckBox.isSelected();
        if (buttontoggled && selectedPlaybackItem != null) {
            if (ReferenceTypeChoiceBox.getSelectionModel().getSelectedIndex() != -1) {loadreferencecontent();}
            else if (ReferenceTypeChoiceBox.getSelectionModel().getSelectedIndex() == -1) {ReferenceContentPane.setContent(new TextArea("Select Reference Type For " + selectedPlaybackItem.getName()));}
            else {ReferenceContentPane.setContent(new TextArea(""));}
        } else {ReferenceContentPane.setContent(new TextArea(""));}
    }
    private void loadreferencecontent() {
        switch (referenceType) {
            case txt:
                StringBuilder sb = new StringBuilder();
                try (FileInputStream fis = new FileInputStream(selectedPlaybackItem.getReferenceFile(referenceType));
                     BufferedInputStream bis = new BufferedInputStream(fis)) {
                    while (bis.available() > 0) {sb.append((char) bis.read());}
                } catch (Exception ignored) {}
                TextArea ta = new TextArea();
                ta.setText(sb.toString());
                ta.setWrapText(true);
                ReferenceContentPane.setContent(ta);
                break;
            case html:
                WebView browser = new WebView();
                WebEngine webEngine = browser.getEngine();
                webEngine.load(selectedPlaybackItem.getReferenceFile(referenceType).toURI().toString());
                webEngine.setUserStyleSheetLocation(kujiin.xml.Preferences.REFERENCE_THEMEFILE.toURI().toString());
                ReferenceContentPane.setContent(browser);
                break;
            default:
                break;
        }
    }
    public void closedialog() {}

}