package kujiin.ui.playback;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import kujiin.ui.boilerplate.IconImageView;
import kujiin.ui.dialogs.alerts.AnswerDialog;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.ui.goals.GoalsCompletedDialog;
import kujiin.util.Util;
import kujiin.util.enums.IconDisplayType;
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

public class Player_New extends Stage {
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
    // AllGoals Progress
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
    private Duration fade_play_value;
    private Duration fade_stop_value;
    // Playback
    private MediaPlayer entrainmentplayer;
    private MediaPlayer ambienceplayer;
    private SoundFile currentambiencesoundfile;
    private Double currententrainmentvolume;
    private Double currentambiencevolume;
    // Goals
    private Duration totalpracticedtime;
    // Class Objects
    private boolean testingmode;
    private Session SessionTemplate;
    private Session SessionInProgress;
    private PlaybackItem selectedPlaybackItem;
    private AllGoals AllGoals;
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

    public Player_New(MainController Root, Sessions sessions, AllGoals allGoals, Session sessiontoplay) {
        try {
            testingmode = Root.isTestingmode();
            Preferences = Root.getPreferences();
            SessionTemplate = sessiontoplay;
            for (PlaybackItem i : SessionTemplate.getPlaybackItems()) {i.calculatetotalpracticetime(sessions);}
            SessionInProgress = SessionTemplate;
            resetsessionpracticedtime();
            availableEntrainments = Root.getAvailableEntrainments();
            rampfiles = Root.getRampFiles();
            this.sessions = sessions;
            AllGoals = allGoals;
            totalpracticedtime = sessions.gettotalpracticedtime();
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
            setPlayerState(IDLE);
            setupTooltips();
            setupIcons();
            ReferenceTypeChoiceBox.setOnAction(event -> referencetypechanged());
            ReferenceToggleCheckBox.setOnAction(event -> ReferenceToggleCheckboxtoggled());
            setOnCloseRequest(event -> {
                PlayerState p = playerState;
                boolean animationinprogress = p == FADING_PAUSE || p == FADING_PLAY || p == FADING_RESUME || p == FADING_STOP;
                if (animationinprogress) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Cannot Close Player");
                    String state = "";
                    switch (p) {
                        case FADING_PLAY:
                            state = "Starting Playback";
                            break;
                        case FADING_PAUSE:
                            state = "Pausing Playback";
                            break;
                        case FADING_RESUME:
                            state = "Resuming Playback";
                            break;
                        case FADING_STOP:
                            state = "Stopping Playback";
                            break;
                    }
                    a.setHeaderText("Currently " + state);
                    a.setContentText("Please Wait Till Done Until Closing Player");
                    a.getDialogPane().getStylesheets().add(kujiin.xml.Preferences.DEFAULTSTYLESHEET.toURI().toString());
                    a.show();
                    event.consume();
                    return;
                }
                if (playerState != IDLE && playerState != STOPPED) {
                    if (! endsessionprematurely(false)) {event.consume();}
                }
            });
            SessionTotalTime.setText(Util.formatdurationtoStringDecimalWithColons(SessionInProgress.getExpectedSessionDuration()));
            SessionProgressPercentage.setVisible(false);
            SessionProgress.setOnMouseEntered(event -> SessionProgressPercentage.setVisible(true));
            SessionProgress.setOnMouseExited(event -> SessionProgressPercentage.setVisible(false));
            SessionProgress.setOnMouseClicked(event -> SessionProgressPercentage.setVisible(! SessionProgressPercentage.isVisible()));
            ReferenceTypeChoiceBox.setItems(FXCollections.observableArrayList(Arrays.asList("html", "txt")));
            ReferenceControls.setDisable(true);
            updategoalsui();
            updateuitimeline = new Timeline(new KeyFrame(updateuifrequency, ae -> updateplayerui()));
            updateuitimeline.setCycleCount(Animation.INDEFINITE);
            new Timeline(new KeyFrame(Duration.millis(100), ae -> {PlayButton.requestFocus();})).play();
        } catch (IOException ignored) {ignored.printStackTrace();}
    }
    private void setupTooltips() {
        PlayButton.setTooltip(new Tooltip("Play"));
        PauseButton.setTooltip(new Tooltip("Pause"));
        StopButton.setTooltip(new Tooltip("Stop"));
    }
    private void setupIcons() {
        IconDisplayType dt = Preferences.getUserInterfaceOptions().getIconDisplayType();
        if (dt == IconDisplayType.ICONS_AND_TEXT || dt == IconDisplayType.ICONS_ONLY) {
            PlayButton.setGraphic(new IconImageView(kujiin.xml.Preferences.ICON_PLAY, 20.0));
            PauseButton.setGraphic(new IconImageView(kujiin.xml.Preferences.ICON_PAUSE, 20.0));
            StopButton.setGraphic(new IconImageView(kujiin.xml.Preferences.ICON_STOP, 20.0));
        }
//        if (dt == IconDisplayType.ICONS_ONLY) {
//            PlayButton.setText("");
//            PauseButton.setText("");
//            StopButton.setText("");
//        } else {
//            PlayButton.setText("Play");
//            PauseButton.setText("Pause");
//            StopButton.setText("Stop");
//        }
    }

// Playback Methods
    private void setTo(PlayAction playaction) {
        switch (playaction) {
            case PLAY:
                if (playerState == IDLE || playerState == STOPPED) { setupsession(); }
                System.out.println("Playback Item Is Now :" + selectedPlaybackItem.getName());
                setupfadeanimations();
                loadEntrainment();
                entrainmentplayer.play();
                if (fade_entrainment_play != null) {fade_entrainment_play.play(); setPlayerState(FADING_PLAY);}
                else {setEntrainmentVolume(currententrainmentvolume); volume_bindentrainment(); setPlayerState(PLAYING);}
                if (timeline_start_ending_ramp != null) {timeline_start_ending_ramp.play();}
                if (selectedPlaybackItem.getAmbience().isEnabled()) {
                    loadAmbience();
                    ambienceplayer.play();
                    if (fade_ambience_play != null) { fade_ambience_play.play(); }
                    else {setAmbienceVolume(currentambiencevolume); volume_bindambience();}
                }
                timeline_progresstonextsessionpart = new Timeline(new KeyFrame(new Duration(selectedPlaybackItem.getExpectedDuration()), ae -> setTo(PlayAction.TRANSITION)));
                timeline_progresstonextsessionpart.play();
                if (timeline_fadeout_timer != null) { timeline_fadeout_timer.play(); }
                updateuitimeline.play();
                if (ReferenceToggleCheckBox.isSelected() && ReferenceTypeChoiceBox.getSelectionModel().getSelectedIndex() != -1) {loadreference();}
                break;
            case PAUSE:
                if (fade_entrainment_pause != null && sessionparttimeleft().greaterThan(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_pause_value()))) {
                    volume_unbindentrainment();
                    fade_entrainment_pause.play();
                    if (selectedPlaybackItem.getAmbience().isEnabled()) {
                        volume_unbindambience();
                        fade_ambience_pause.play();
                    }
                } else {
                    volume_unbindentrainment();
                    setEntrainmentVolume(0.0);
                    setPlayerState(PAUSED);
                    entrainmentplayer.pause();
                    if (Preferences.getSessionOptions().getRampenabled() && timeline_start_ending_ramp != null && timeline_start_ending_ramp.getStatus() == Animation.Status.RUNNING) { timeline_start_ending_ramp.pause();}
                    if (timeline_fadeout_timer != null) {timeline_fadeout_timer.pause();}
                    if (selectedPlaybackItem.getAmbience().isEnabled()) {
                        volume_unbindambience();
                        setAmbienceVolume(0.0);
                        ambienceplayer.pause();
                    }
                    timeline_progresstonextsessionpart.pause();
                }
                break;
            case RESUME:
                updateuitimeline.play();
                volume_unbindentrainment();
                entrainmentplayer.play();
                if (fade_entrainment_resume != null && sessionparttimeleft().greaterThan(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_resume_value()))) {
                    setEntrainmentVolume(0.0); fade_entrainment_resume.play(); setPlayerState(FADING_RESUME);
                } else { setEntrainmentVolume(currententrainmentvolume); volume_bindentrainment(); setPlayerState(PLAYING); }
                timeline_progresstonextsessionpart.play();
                if (timeline_start_ending_ramp != null) { timeline_start_ending_ramp.play();}
                if (timeline_fadeout_timer != null) {timeline_fadeout_timer.play();}
                if (selectedPlaybackItem.getAmbience().isEnabled()) {
                    volume_unbindambience();
                    ambienceplayer.play();
                    if (fade_ambience_resume != null) { setAmbienceVolume(0.0); fade_ambience_resume.play(); }
                    else { setAmbienceVolume(currentambiencevolume); volume_bindambience(); }
                }
                break;
            case STOP:
                volume_unbindentrainment();
                if (fade_entrainment_stop != null && sessionparttimeleft().greaterThan(fade_stop_value)) {
                    fade_entrainment_stop.play();
                    setPlayerState(FADING_STOP);
                    if (selectedPlaybackItem.getAmbience().isEnabled()) {
                        volume_unbindambience();
                        fade_ambience_stop.play();
                    }
                } else {
                    updateuitimeline.stop();
                    setPlayerState(STOPPED);
                    entrainmentplayer.stop();
                    timeline_progresstonextsessionpart.stop();
                    if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop();}
                    if (timeline_start_ending_ramp != null) {timeline_start_ending_ramp.stop();}
                    if (selectedPlaybackItem.getAmbience().isEnabled()) {
                        volume_unbindambience();
                        ambienceplayer.stop();
                    }
                }
                break;
            case TRANSITION:
                setPlayerState(TRANSITIONING);
                entrainmentplayer.stop();
                if (selectedPlaybackItem.getAmbience().isEnabled()) {
                    ambienceplayer.stop();
                }
                updateuitimeline.stop();
                selectedPlaybackItem.updateduration(new Duration(selectedPlaybackItem.getExpectedDuration()));
                updategoalsui();
                if (Preferences.getSessionOptions().getAlertfunction() && ! selectedPlaybackItem.equals(SessionInProgress.getPlaybackItems().get(SessionInProgress.getPlaybackItems().size() - 1))) {
                    Media alertmedia = new Media(Preferences.getSessionOptions().getAlertfilelocation());
                    MediaPlayer alertplayer = new MediaPlayer(alertmedia);
                    alertplayer.play();
                    PlayButton.setDisable(true);
                    PauseButton.setDisable(true);
                    StopButton.setDisable(true);
                    alertplayer.setOnEndOfMedia(() -> {
                        setTo(PlayAction.NEXT_PLAYBACK_ITEM);
                    });
                } else { setTo(PlayAction.NEXT_PLAYBACK_ITEM); }
                break;
            case NEXT_PLAYBACK_ITEM:
                try {
                    int index = SessionInProgress.getPlaybackItems().indexOf(selectedPlaybackItem) + 1;
                    selectedPlaybackItem = SessionInProgress.getPlaybackItems().get(index);
                    setTo(PlayAction.PLAY);
                } catch (IndexOutOfBoundsException e) {
                    cleanupPlayersandAnimations();
                    endofsession();
                }
                break;
        }
        ReferenceControls.setDisable(playerState != PLAYING);
    }
    private void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
        toggleplayerbuttons();
    }
    // Utility
    private void setupsession() {
        SessionInProgress = SessionTemplate;
        selectedPlaybackItem = SessionInProgress.getPlaybackItems().get(0);
        SessionInProgress.setSessionPracticedTime();
        SessionInProgress.calculateactualduration();
        AmbienceVolumeControls.setVisible(selectedPlaybackItem.getAmbience().isEnabled());
        currententrainmentvolume = Preferences.getPlaybackOptions().getEntrainmentvolume();
        currentambiencevolume = Preferences.getPlaybackOptions().getAmbiencevolume();
    }
    private void pausewoanimation() {}
    private void resumewoanimation() {}
    private void setupfadeanimations() {
        // Reset Existing Fade Animations
        fade_entrainment_play = null;
        fade_entrainment_pause = null;
        fade_entrainment_resume = null;
        fade_entrainment_stop = null;
        fade_ambience_play = null;
        fade_ambience_pause = null;
        fade_ambience_resume = null;
        fade_ambience_stop = null;
        // Calculate Fade Play Times
        fade_play_value = Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_play_value());
        fade_stop_value = Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_stop_value());
        Duration playbackitemduration = new Duration(selectedPlaybackItem.getExpectedDuration());
        if (playbackitemduration.lessThan(fade_play_value.add(fade_stop_value))) {
            while (fade_play_value.add(fade_stop_value).greaterThanOrEqualTo(playbackitemduration)) {
                fade_play_value = fade_play_value.subtract(Duration.seconds(0.5));
                fade_stop_value = fade_stop_value.subtract(Duration.seconds(0.5));
            }
        }
        // PLAY
        if (selectedPlaybackItem.isRampOnly() || (Preferences.getPlaybackOptions().getAnimation_fade_play_enabled() && Preferences.getPlaybackOptions().getAnimation_fade_play_value() > 0.0)) {
            fade_entrainment_play = new Transition() {
                {if (selectedPlaybackItem.isRampOnly()) {setCycleDuration(Duration.seconds(kujiin.xml.Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
                else {setCycleDuration(fade_play_value);}}

                @Override
                protected void interpolate(double frac) {
                    if (entrainmentplayer != null) {
                        if (entrainmentplayer.getStatus() != MediaPlayer.Status.PLAYING) {entrainmentplayer.play();}
                        try { setEntrainmentVolume(frac * currententrainmentvolume); }
                        catch (RuntimeException ignored) {}
                    }
                }
            };
            fade_entrainment_play.setOnFinished(event -> {
                setPlayerState(PLAYING);
                volume_bindentrainment();
            });
            if (selectedPlaybackItem.getAmbience().isEnabled()) {
                fade_ambience_play = new Transition() {
                    {if (selectedPlaybackItem.isRampOnly()) {setCycleDuration(Duration.seconds(kujiin.xml.Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
                    else {setCycleDuration(fade_play_value);}}

                    @Override
                    protected void interpolate(double frac) {
                        if (ambienceplayer != null) {
                            if (ambienceplayer.getStatus() != MediaPlayer.Status.PLAYING) {ambienceplayer.play();}
                            try { setAmbienceVolume(frac * currentambiencevolume); }
                            catch (RuntimeException ignored) {}
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
                        try { setEntrainmentVolume(frac * currententrainmentvolume); }
                        catch (RuntimeException ignored) {}
                    }
                }
            };
            fade_entrainment_resume.setOnFinished(event -> {
                timeline_progresstonextsessionpart.play();
                if (Preferences.getSessionOptions().getRampenabled() && (timeline_start_ending_ramp != null && timeline_start_ending_ramp.getStatus() == Animation.Status.PAUSED)) {
                    timeline_start_ending_ramp.play();
                }
                if (timeline_fadeout_timer != null) { timeline_fadeout_timer.play(); }
                setPlayerState(PLAYING);
                volume_bindentrainment();
            });
            if (selectedPlaybackItem.getAmbience().isEnabled()) {
                fade_ambience_resume = new Transition() {
                    {setCycleDuration(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_resume_value()));}

                    @Override
                    protected void interpolate(double frac) {
                        if (ambienceplayer != null) {
                            try { setAmbienceVolume(frac * currentambiencevolume); }
                            catch (RuntimeException ignored) {}
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
                        try { setEntrainmentVolume(currententrainmentvolume - (frac * currententrainmentvolume)); }
                        catch (RuntimeException ignored) {}
                    }
                }
            };
            fade_entrainment_pause.setOnFinished(event -> {
                entrainmentplayer.pause();
                timeline_progresstonextsessionpart.pause();
                updateuitimeline.pause();
                if (Preferences.getSessionOptions().getRampenabled() && (timeline_start_ending_ramp != null && timeline_start_ending_ramp.getStatus() == Animation.Status.RUNNING)) {timeline_start_ending_ramp.pause();}
                if (timeline_fadeout_timer != null) {timeline_fadeout_timer.pause();}
                setPlayerState(PAUSED);
            });
            if (selectedPlaybackItem.getAmbience().isEnabled()) {
                fade_ambience_pause = new Transition() {
                    {
                        setCycleDuration(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_pause_value()));
                    }

                    @Override
                    protected void interpolate(double frac) {
                        if (ambienceplayer != null) {
                            try { setAmbienceVolume(currentambiencevolume - (frac * currentambiencevolume)); }
                            catch (RuntimeException ignored) {}
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
                else {setCycleDuration(fade_stop_value);}}

                @Override
                protected void interpolate(double frac) {
                    if (entrainmentplayer != null) {
                        try { setEntrainmentVolume(currententrainmentvolume - (frac * currententrainmentvolume)); }
                        catch (RuntimeException ignored) {}
                    }
                }
            };
            fade_entrainment_stop.setOnFinished(event -> {
//                if (playerState != TRANSITIONING) {setPlayerState(STOPPED);}
                entrainmentplayer.stop();
                if (Preferences.getSessionOptions().getRampenabled() && timeline_start_ending_ramp != null && timeline_start_ending_ramp.getStatus() == Animation.Status.RUNNING) {timeline_start_ending_ramp.stop();}
                timeline_progresstonextsessionpart.stop();
                if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop();}
            });
            if (selectedPlaybackItem.getAmbience().isEnabled()) {
                fade_ambience_stop = new Transition() {
                    {if (selectedPlaybackItem.isRampOnly()) {setCycleDuration(Duration.seconds(kujiin.xml.Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
                    else {setCycleDuration(fade_stop_value);}}

                    @Override
                    protected void interpolate(double frac) {
                        if (ambienceplayer != null) {
                            try { setAmbienceVolume(currentambiencevolume - (frac * currentambiencevolume)); }
                            catch (RuntimeException ignored) {}
                        }
                    }
                };
                fade_ambience_stop.setOnFinished(event -> {
                    ambienceplayer.stop();
                });
            }
            Duration startfadeout = new Duration(selectedPlaybackItem.getExpectedDuration());
            if (selectedPlaybackItem.isRampOnly()) {startfadeout = startfadeout.subtract(Duration.seconds(kujiin.xml.Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
            else {startfadeout = startfadeout.subtract(fade_stop_value);}
            timeline_fadeout_timer = new Timeline(new KeyFrame(startfadeout, ae -> {
                setPlayerState(FADING_STOP);
                volume_unbindentrainment();
                fade_entrainment_stop.play();
                if (fade_ambience_stop != null) {
                    volume_unbindambience();
                    fade_ambience_stop.play();
                }
            }));
        }
    }
    private void loadEntrainment() {
        // Start Entrainment Playback
        entrainmentplayer = null;
        PlaybackItemEntrainment playbackItemEntrainment = availableEntrainments.getsessionpartEntrainment(selectedPlaybackItem);
        boolean isLastSessionPart = SessionInProgress.getPlaybackItems().indexOf(selectedPlaybackItem) == SessionInProgress.getPlaybackItems().size() - 1;
        if (! selectedPlaybackItem.isRampOnly() || isLastSessionPart) {entrainmentplayer = new MediaPlayer(new Media(playbackItemEntrainment.getFreq().getFile().toURI().toString()));}
        else {entrainmentplayer = new MediaPlayer(new Media(rampfiles.getRampFile(selectedPlaybackItem, SessionInProgress.getPlaybackItems().get(SessionInProgress.getPlaybackItems().indexOf(selectedPlaybackItem) + 1)).getFile().toURI().toString()));}
        setEntrainmentVolume(0.0);
        if (! selectedPlaybackItem.isRampOnly()) {entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);}
        entrainmentplayer.setOnError(this::entrainmenterror);
        // Set Up Entrainment Ramp
        if (! selectedPlaybackItem.isRampOnly() && ! isLastSessionPart && Preferences.getSessionOptions().getRampenabled()) {
            SoundFile rampfile = rampfiles.getRampFile(selectedPlaybackItem, SessionInProgress.getPlaybackItems().get(SessionInProgress.getPlaybackItems().indexOf(selectedPlaybackItem) + 1));
            Duration timetillendingramp = new Duration(selectedPlaybackItem.getExpectedDuration()).subtract(Duration.millis(rampfile.getDuration()));
            if (timetillendingramp.greaterThan(Duration.ZERO)) {
                timeline_start_ending_ramp = new Timeline(new KeyFrame(new Duration(selectedPlaybackItem.getExpectedDuration()).subtract(Duration.millis(rampfile.getDuration())), (ActionEvent ae) -> {
                    volume_unbindentrainment();
                    entrainmentplayer.stop();
                    entrainmentplayer = new MediaPlayer(new Media(rampfile.getFile().toURI().toString()));
                    entrainmentplayer.setOnError(this::entrainmenterror);
                    setEntrainmentVolume(currententrainmentvolume);
                    entrainmentplayer.play();
                    entrainmentplayer.setOnPlaying(this::volume_bindentrainment);
                }));
            }
        }
    }
    private void loadAmbience() {
        ambienceplayer = null;
        currentambiencevolume = Preferences.getPlaybackOptions().getAmbiencevolume();
        volume_unbindambience();
        selectedPlaybackItem.getAmbience().resetplaycount();
        currentambiencesoundfile = selectedPlaybackItem.getAmbience().getnextambienceforplayback();
        ambienceplayer = new MediaPlayer(new Media(currentambiencesoundfile.getFile().toURI().toString()));
        setAmbienceVolume(0.0);
        ambienceplayer.setOnEndOfMedia(this::playnextambience);
        ambienceplayer.setOnError(this::ambienceerror);
        getScene().addEventHandler(KeyEvent.KEY_PRESSED, AmbienceSwitchWithKeyboard);
    }
    private void setEntrainmentVolume(double volume) {
        System.out.println("Entrainment Player Is: " + entrainmentplayer.getStatus() + ". Should Be Setting Volume To " + volume);
        entrainmentplayer.setVolume(volume);
        String percentage = new Double(volume * 100).intValue() + "%";
        EntrainmentVolumePercentage.setText(percentage);
        EntrainmentVolume.setValue(volume);
    }
    private void setAmbienceVolume(double volume) {
        System.out.println("Ambience Player Is: " + ambienceplayer.getStatus() + ". Should Be Setting Volume To " + volume);
        ambienceplayer.setVolume(volume);
        String percentage = new Double(volume * 100).intValue() + "%";
        AmbienceVolumePercentage.setText(percentage);
        AmbienceVolume.setValue(volume);
    }
    private void playnextentrainment() {
        try {
            volume_unbindentrainment();
            entrainmentplayer.stop();
            entrainmentplayer.dispose();
            entrainmentplayer = null;
            entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);
            entrainmentplayer.setOnError(this::entrainmenterror);
            entrainmentplayer.setOnPlaying(this::volume_bindentrainment);
            entrainmentplayer.play();
        } catch (Exception ignored) {
            entrainmentplayer.dispose();
            cleanupPlayersandAnimations();
        }
    }
    private void playnextambience() {
        try {
            volume_unbindambience();
            ambienceplayer.stop();
            ambienceplayer = null;
            currentambiencesoundfile = selectedPlaybackItem.getAmbience().getnextambienceforplayback();
            ambienceplayer = new MediaPlayer(new Media(currentambiencesoundfile.getFile().toURI().toString()));
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            setAmbienceVolume(currentambiencevolume);
            ambienceplayer.play();
            ambienceplayer.setOnPlaying(this::volume_bindambience);
        } catch (IndexOutOfBoundsException ignored) {}
    }
    public void playpreviousambiencefromhistory() {
        SoundFile previousambiencefile = selectedPlaybackItem.getAmbience().getpreviousambiencehistory();
        if (previousambiencefile != null) {
            volume_unbindambience();
            ambienceplayer = new MediaPlayer(new Media(previousambiencefile.getFile().toURI().toString()));
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            setAmbienceVolume(currentambiencevolume);
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
            setAmbienceVolume(currentambiencevolume);
            ambienceplayer.play();
            ambienceplayer.setOnPlaying(this::volume_bindambience);
        }
    }
    public void reset() {
        PlayButton.setDisable(false);
        PlayButton.setTooltip(new Tooltip("Replay"));
//        if (Preferences.getUserInterfaceOptions().getIconDisplayType() != IconDisplayType.ICONS_ONLY) {
//            PlayButton.setText("Replay");
//            PauseButton.setText("Pause");
//            StopButton.setText("Stop");
//        }
        PauseButton.setDisable(true);
        StopButton.setDisable(true);
        if (updateuitimeline != null) {updateuitimeline.stop(); updateuitimeline = null;}
        SessionCurrentTime.setText("00:00");
        SessionProgress.setProgress(0.0);
        SessionProgressPercentage.setText("0.0%");
        SessionProgress.setDisable(true);
        EntrainmentVolume.setDisable(true);
        EntrainmentVolume.setValue(0.0);
        EntrainmentVolumePercentage.setText("0%");
        AmbienceVolume.setDisable(true);
        AmbienceVolume.setValue(0.0);
        AmbienceVolumePercentage.setText("0%");
    }
    public void endofsession() {
        // TODO Prompt For Export
        updategoalsui();
        reset();
        final Session sessioninprogress = SessionInProgress;
        try {
            SessionComplete sessionComplete = new SessionComplete(SessionInProgress, true);
            sessionComplete.initModality(Modality.APPLICATION_MODAL);
            sessionComplete.show();
            sessionComplete.setOnHidden(event -> {
                if (sessionComplete.needtosetNotes()) {sessioninprogress.setNotes(sessionComplete.getNotes());}
//                if (! sessionComplete.keepplayeropen()) {close();}
                if (AllGoals.sessionhasgoalscompleted()) {
                    GoalsCompletedDialog goalsCompletedDialog = new GoalsCompletedDialog(AllGoals);
                    goalsCompletedDialog.initModality(Modality.APPLICATION_MODAL);
                    goalsCompletedDialog.show();
                }
            });
        } catch (Exception e) {e.printStackTrace();}
        SessionInProgress = sessioninprogress;
        if (! testingmode) { sessions.add(SessionInProgress); }
        SessionInProgress = null;
    }
    public boolean endsessionprematurely(boolean resetdialogcontrols) {
//        pausewithoutanimation();
        updateuitimeline.pause();
        if (new ConfirmationDialog(Preferences, "End Session Early", "Session Is Not Completed.", "End Session Prematurely?", "End Session", "Continue").getResult()) {
//            setPlayerstate(STOPPED);
            if (! testingmode) { sessions.add(SessionInProgress); }
            if (resetdialogcontrols) {
                updategoalsui();
                reset();
                cleanupPlayersandAnimations();
            }
            SessionComplete sessionComplete = new SessionComplete(SessionInProgress, false);
            sessionComplete.initModality(Modality.APPLICATION_MODAL);
            sessionComplete.showAndWait();
//            if (! sessionComplete.keepplayeropen()) {close();}
            resetsessionpracticedtime();
            return true;
        } else {playbuttonpressed(); return false;}
    }
    private void resetsessionpracticedtime() {
        for (PlaybackItem i : SessionInProgress.getPlaybackItems()) {i.resetpracticetime();}
        SessionInProgress.resetpracticetime();
    }
    // Animation
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
        } catch (Exception ignored) {}
    }
    private void volume_bindentrainment() {
        EntrainmentVolume.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
        EntrainmentVolume.setDisable(false);
        EntrainmentVolume.setOnMouseDragged(event1 -> {
            String percentage = new Double(EntrainmentVolume.getValue() * 100).intValue() + "%";
            currententrainmentvolume =EntrainmentVolume.getValue();
            EntrainmentVolumePercentage.setText(percentage);
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
            }
        });
    }
    private void volume_unbindentrainment() {
        try {
            EntrainmentVolume.valueProperty().unbindBidirectional(entrainmentplayer.volumeProperty());
            EntrainmentVolume.setDisable(true);
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
    }
    private void volume_unbindambience() {
        try {
            AmbienceVolume.valueProperty().unbindBidirectional(ambienceplayer.volumeProperty());
            AmbienceVolume.setDisable(true);
        } catch (NullPointerException ignored) {}
    }
    private void volume_rebindambience() {volume_unbindambience(); volume_bindambience();}
    private void volume_rebindentrainment() {volume_unbindentrainment(); volume_bindentrainment();}
    private Duration sessionparttimeleft() {
        return new Duration(selectedPlaybackItem.getExpectedDuration()).subtract(new Duration(selectedPlaybackItem.getPracticeTime()));
    }
    // Error Handling
    private void entrainmenterror() {
        System.out.println("PlaybackItemEntrainment Error");
        // Pause Ambience If Exists
        switch (new AnswerDialog(Preferences, "PlaybackItemEntrainment Playback Error", null, "An Error Occured While Playing " + selectedPlaybackItem.getName() +
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
        switch (new AnswerDialog(Preferences, "Ambience Playback Error", null, "An Error Occured While Playing " + selectedPlaybackItem.getName() +
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


// Button Methods
    public void playbuttonpressed() { setTo(PlayAction.PLAY); }
    public void pausebuttonpressed() { setTo(PlayAction.PAUSE); }
    public void stopbuttonpressed() { setTo(PlayAction.STOP); }


// Reference Methods
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


    // UI, Playlist & Button Methods
    private void updateplaylist() {
        PlaylistTableView.getItems().clear();
        ObservableList<PlaylistTableItem> playlistitems = FXCollections.observableArrayList();
        for (PlaybackItem i : SessionInProgress.getPlaybackItems()) {
            float totalprogress = (float) i.getPracticeTime() / (float) i.getExpectedDuration();
            int percentage = new Double(totalprogress * 100).intValue();
            String progress = Util.formatdurationtoStringDecimalWithColons(new Duration(i.getPracticeTime())) + " > " + Util.formatdurationtoStringDecimalWithColons(new Duration(i.getExpectedDuration()));
            playlistitems.add(new PlaylistTableItem(i.getName(), progress, percentage + "%"));
        }
        PlaylistTableView.setItems(playlistitems);
        PlaylistTableView.getSelectionModel().select(SessionInProgress.getPlaybackItems().indexOf(selectedPlaybackItem));
    }
    private void updateplayerui() {
        PlayerState p = playerState;
        if (p == PLAYING || p == FADING_PLAY || p == FADING_PAUSE || p == FADING_RESUME || p == FADING_STOP) {
            try {
                selectedPlaybackItem.addelapsedtime(updateuifrequency);
                AllGoals.calculateifPlaybackItemgoalscompleted(selectedPlaybackItem.getCreationindex(), selectedPlaybackItem.getTotalpracticetime());
                SessionInProgress.addelapseduration(updateuifrequency);
                totalpracticedtime = totalpracticedtime.add(updateuifrequency);
                AllGoals.calculateifTotalGoalsCompleted(totalpracticedtime);
                updateplaylist();
                // Update Total Progress
                SessionCurrentTime.setText(Util.formatdurationtoStringDecimalWithColons(SessionInProgress.getSessionPracticedTime()));
                Float totalprogress;
                if (SessionInProgress.getSessionPracticedTime().greaterThan(Duration.ZERO)) {totalprogress = (float) SessionInProgress.getSessionPracticedTime().toMillis() / (float) SessionInProgress.getExpectedSessionDuration().toMillis();}
                else {totalprogress = (float) 0.0;}
                SessionProgress.setProgress(totalprogress);
                BigDecimal bd = new BigDecimal(totalprogress * 100);
                bd = bd.setScale(1, RoundingMode.HALF_UP);
                SessionProgressPercentage.setText(bd.doubleValue() + "%");
                if (displaynormaltime) {SessionTotalTime.setText(Util.formatdurationtoStringDecimalWithColons(SessionInProgress.getExpectedSessionDuration()));}
                else {SessionTotalTime.setText(Util.formatdurationtoStringDecimalWithColons(SessionInProgress.getExpectedSessionDuration().subtract(SessionInProgress.getSessionPracticedTime())));}
                updategoalsui();
            } catch (Exception ignored) {ignored.printStackTrace();}
        }
    }
    private void updategoalsui() {
        String goalprogressandpercentage;
        String goaltime;
        Double percentage;
        if (selectedPlaybackItem != null && AllGoals.getplaybackItemGoals(selectedPlaybackItem.getCreationindex()) != null) {
            PlaybackItemGoals playbackItemGoals = AllGoals.getplaybackItemGoals(selectedPlaybackItem.getCreationindex());
            Goal currentgoal = playbackItemGoals.getCurrentGoal();
            Duration practiceduration = sessions.gettotalpracticedtime(selectedPlaybackItem, false);
            practiceduration = practiceduration.add(SessionInProgress.getSessionPracticedTime());
            if (currentgoal != null) {percentage = (practiceduration.toMillis() / currentgoal.getDuration().toMillis()) * 100;}
            else {percentage = 0.0;}
            goalprogressandpercentage = String.format("%s (%.1f%%)", Util.formatdurationtoStringDecimalWithColons(practiceduration), percentage);
            percentage /= 100;
            if (currentgoal != null) {goaltime = Util.formatdurationtoStringDecimalWithColons(currentgoal.getDuration());}
            else {goaltime = "-";}
        } else {
            goalprogressandpercentage = "No Goal Set";
            goaltime = "-";
            percentage = 0.0;
        }
        CurrentGoalProgress.setProgress(percentage);
        CurrentGoalPercentage.setText(goalprogressandpercentage);
        SessionPartGoalTime.setText(goaltime);
    }
    private void toggleplayerbuttons() {
        System.out.println("Player State Is Now: " + playerState.toString());
        if (playerState == null || selectedPlaybackItem == null) {return;}
        boolean idle = playerState == IDLE;
        boolean playing = playerState == PLAYING;
        boolean paused = playerState == PAUSED;
        boolean stopped = playerState == STOPPED;
        boolean fade_play = playerState == FADING_PLAY;
        boolean fade_resume = playerState == FADING_RESUME;
        boolean fade_pause = playerState == FADING_PAUSE;
        boolean fade_stop = playerState == FADING_STOP;
        boolean transitioning = playerState == TRANSITIONING;
        PlayButton.setDisable(playing || fade_play || fade_resume || fade_pause || fade_stop || transitioning);
        PauseButton.setDisable(paused || fade_play || fade_resume || fade_pause || fade_stop || idle || transitioning);
        StopButton.setDisable(stopped || fade_play || fade_resume || fade_pause || fade_stop || idle || transitioning);
        ReferenceControls.setDisable(paused || stopped || transitioning);
        boolean enabled = playerState == PLAYING;
        EntrainmentVolume.setDisable(! enabled);
        if (selectedPlaybackItem.getAmbience().isEnabled()) {AmbienceVolume.setDisable(! enabled);}
        if (paused) {PlayButton.setTooltip(new Tooltip("Resume"));}
        else if (stopped) {PlayButton.setTooltip(new Tooltip("Replay"));}
        else {PlayButton.setTooltip(new Tooltip("Play"));}
    }

    private enum PlayAction {PLAY, PAUSE, RESUME, STOP, TRANSITION, NEXT_PLAYBACK_ITEM}
    public static class PlaylistTableItem {
        public StringProperty itemname;
        public StringProperty duration;
        public StringProperty percentcompleted;

        public PlaylistTableItem(String itemname, String duration, String percentcompleted) {
            this.itemname = new SimpleStringProperty(itemname);
            this.duration = new SimpleStringProperty(duration);
            this.percentcompleted = new SimpleStringProperty(percentcompleted);
        }

    }
}