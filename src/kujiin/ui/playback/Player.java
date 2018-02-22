package kujiin.ui.playback;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.StackedBarChart;
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
import org.apache.commons.lang3.time.StopWatch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static kujiin.util.enums.PlayerState.*;

public class Player extends Stage {
// UI Elements
    // Menu
    public MenuItem PlayMenuItem;
    public MenuItem PauseMenuItem;
    public MenuItem StopMenuItem;
    public Menu AmbienceMenu;
    // Reference
    public HBox ReferenceControls;
    public CheckBox ReferenceToggleCheckBox;
    public ChoiceBox<String> ReferenceTypeChoiceBox;
    public ScrollPane ReferenceContentPane;
    // Sidebar
        // Playlist
    public Tab PlaylistTab;
    public TableView<PlaylistTableItem> PlaylistTableView;
    public TableColumn<PlaylistTableItem, String> NameColumn;
    public TableColumn<PlaylistTableItem, String> DurationColumn;
    public TableColumn<PlaylistTableItem, String> PercentColumn;
        // Ambience
    public Tab AmbienceTab;
    public TabPane AmbiencePlaylistTabPane;
    public Tab AmbiencePresetTab;
    public TableView<AmbiencePlaylistTableItem> AmbiencePlaylistTable_Preset;
    public TableColumn<AmbiencePlaylistTableItem, Integer> AmbiencePlaylistPresetNumberColumn;
    public TableColumn<AmbiencePlaylistTableItem, String> AmbiencePlaylistPresetNameColumn;
    public TableColumn<AmbiencePlaylistTableItem, String> AmbiencePlaylistPresetDurationColumn;
    public Tab AmbienceAvailableTab;
    public TableView<AmbiencePlaylistTableItem> AmbiencePlaylistTable_Available;
    public TableColumn<AmbiencePlaylistTableItem, Integer> AmbiencePlaylistAvailableNumberColumn;
    public TableColumn<AmbiencePlaylistTableItem, String> AmbiencePlaylistAvailableNameColumn;
    public TableColumn<AmbiencePlaylistTableItem, String> AmbiencePlaylistAvailableDurationColumn;
    public ProgressBar CurrentAmbienceProgressBar;
    public Label CurrentAmbiencePercentage;
    public Button AmbienceShuffleButton;
    public Button AmbiencePreviousButton;
    public Button AmbiencePauseButton;
    public Button AmbienceNextButton;
    public HBox AmbienceVolumeControls;
    public Slider AmbienceVolume;
    public Label AmbienceVolumePercentage;
    public Button AmbienceMuteButton;
        // Goals
    public Tab GoalsTab;
    public HBox GoalLabels;
    public Label Goals_SessionPartPracticedTime;
    public Label Goals_SessionPartGoalTime;
    public ProgressIndicator GoalProgress;
    // Session Progress
    public Label SessionCurrentTime;
    public ProgressBar SessionProgress;
    public Label SessionProgressPercentage;
    public Label SessionTotalTime;
    // Controls
    public Slider EntrainmentVolume;
    public Label EntrainmentVolumePercentage;
    public Button EntrainmentMuteButton;
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
    private StopWatch sessionStopWatch;
    private StopWatch playbackItemStopWatch;
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
    private boolean exitprogram = false;
    private LocalDateTime starttime;
    private LocalDateTime stoptime;
    // Event Handlers
    private EventHandler<KeyEvent> AmbienceSwitchWithKeyboard = new EventHandler<KeyEvent>() {
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
    private EventHandler<KeyEvent> SpaceBarPressed = new EventHandler<KeyEvent>() {

        @Override
        public void handle(KeyEvent event) {
            System.out.println("Key Pressed");
            try {
                if (event.getCode() == KeyCode.SPACE) {
                    switch (playerState) {
                        case IDLE:
                        case STOPPED:
                        case PAUSED:
                            playbuttonpressed();
                            break;
                        case PLAYING:
                            pausebuttonpressed();
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public Player(MainController Root, Sessions sessions, AllGoals allGoals, Session sessiontoplay) {
        try {
            sessionStopWatch = new StopWatch();
            sessionStopWatch.reset();
            playbackItemStopWatch = null;
            exitprogram = false;
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
            setupTables();
            setPlayerstate(IDLE);
            setupTooltips();
            setupIcons();
            setOnCloseRequest(event -> closedialog());
            ReferenceTypeChoiceBox.setOnAction(event -> referencetypechanged());
            ReferenceToggleCheckBox.setOnAction(event -> referencetoggled());
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
            ReferenceTypeChoiceBox.setItems(FXCollections.observableArrayList(Arrays.asList("html", "text")));
            ReferenceControls.setDisable(true);
            updategoalsui();
            updateambienceui();
            getScene().addEventHandler(KeyEvent.KEY_PRESSED, SpaceBarPressed);
            new Timeline(new KeyFrame(Duration.millis(100), ae -> {PlayButton.requestFocus();})).play();
        } catch (IOException ignored) {ignored.printStackTrace();}
    }
    private void setupTables() {
        NameColumn.setCellValueFactory(cellData -> cellData.getValue().itemname);
        DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().duration);
        PercentColumn.setCellValueFactory(cellDate -> cellDate.getValue().percentcompleted);
        PlaylistTableView.setOnMouseClicked(Event::consume);
        AmbiencePlaylistPresetNumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
        AmbiencePlaylistPresetNumberColumn.setSortable(false);
        AmbiencePlaylistPresetNameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        AmbiencePlaylistPresetNameColumn.setSortable(false);
        AmbiencePlaylistPresetDurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
        AmbiencePlaylistPresetDurationColumn.setSortable(false);
        AmbiencePlaylistTable_Preset.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 && playerState == PLAYING) {
                int index = AmbiencePlaylistTable_Preset.getSelectionModel().getSelectedIndex();
                SoundFile i = selectedPlaybackItem.getAmbience().getPreset(index);
                playambience(i);
            }
        });
        AmbiencePlaylistAvailableNumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
        AmbiencePlaylistAvailableNumberColumn.setSortable(false);
        AmbiencePlaylistAvailableNameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        AmbiencePlaylistAvailableNameColumn.setSortable(false);
        AmbiencePlaylistAvailableDurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
        AmbiencePlaylistAvailableDurationColumn.setSortable(false);
        AmbiencePlaylistTable_Available.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 && playerState == PLAYING) {
                int index = AmbiencePlaylistTable_Available.getSelectionModel().getSelectedIndex();
                SoundFile i = selectedPlaybackItem.getAmbience().getAvailable(index);
                playambience(i);
            }
        });
        AmbienceTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            boolean hassessionambience = selectedPlaybackItem.getAmbience().hasPresetAmbience();
            AmbiencePresetTab.setDisable(! hassessionambience);
            if (! hassessionambience) {
                AmbiencePlaylistTabPane.getSelectionModel().select(1);
            }
        });
        AmbiencePresetTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (AmbiencePresetTab.isDisabled()) {return;}
            AmbienceNextButton.setDisable(selectedPlaybackItem.getAmbience().getSessionAmbience().size() == 1);
            AmbiencePreviousButton.setDisable(selectedPlaybackItem.getAmbience().getSessionAmbience().size() == 1);
            AmbienceShuffleButton.setDisable(selectedPlaybackItem.getAmbience().getSessionAmbience().size() < 3);
        });
        AmbienceAvailableTab.selectedProperty().addListener(observable -> {
            AmbienceShuffleButton.setDisable(true);
            AmbienceNextButton.setDisable(selectedPlaybackItem.getAmbience().getAvailableAmbience().size() == 1);
            AmbiencePreviousButton.setDisable(selectedPlaybackItem.getAmbience().getAvailableAmbience().size() == 1);
        });
    }
    private void setupTooltips() {
        StackedBarChart<String, Integer> barchart;
        PlayButton.setTooltip(new Tooltip("Play"));
        AmbiencePauseButton.setTooltip(new Tooltip("Play Ambience"));
        PauseButton.setTooltip(new Tooltip("Pause"));
        AmbienceNextButton.setTooltip(new Tooltip("Next Ambience"));
        StopButton.setTooltip(new Tooltip("Stop"));
        AmbienceShuffleButton.setTooltip(new Tooltip("Shuffle Ambience"));
        AmbiencePlaylistTable_Preset.setRowFactory(tv -> new TableRow<AmbiencePlaylistTableItem>() {
            private Tooltip tooltip = new Tooltip();
            @Override
            protected void updateItem(AmbiencePlaylistTableItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    tooltip.setText(
                            "Name: " + item.name.get() + "\n" +
                            "Location: " + item.getFile().getAbsolutePath()
                    );
                    setTooltip(tooltip);
                } else {setTooltip(null);}
            }
        });
        AmbiencePlaylistTable_Available.setRowFactory(tv -> new TableRow<AmbiencePlaylistTableItem>() {
            private Tooltip tooltip = new Tooltip();
            @Override
            protected void updateItem(AmbiencePlaylistTableItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    tooltip.setText(
                            "Name: " + item.name.get() + "\n" +
                            "Location: " + item.getFile().getAbsolutePath()
                    );
                    setTooltip(tooltip);
                } else {setTooltip(null);}
            }
        });
    }
    private void setupIcons() {
        IconDisplayType dt = Preferences.getUserInterfaceOptions().getIconDisplayType();
        if (dt == IconDisplayType.ICONS_AND_TEXT || dt == IconDisplayType.ICONS_ONLY) {
            PlayButton.setGraphic(new IconImageView(kujiin.xml.Preferences.ICON_PLAY, 20.0));
            PauseButton.setGraphic(new IconImageView(kujiin.xml.Preferences.ICON_PAUSE, 20.0));
            StopButton.setGraphic(new IconImageView(kujiin.xml.Preferences.ICON_STOP, 20.0));
            AmbienceNextButton.setGraphic(new IconImageView(kujiin.xml.Preferences.ICON_NEXT, 20.0));
            AmbiencePreviousButton.setGraphic(new IconImageView(kujiin.xml.Preferences.ICON_PREVIOUS, 20.0));
            AmbiencePauseButton.setGraphic(new IconImageView(kujiin.xml.Preferences.ICON_PAUSE, 20.0));
            AmbienceShuffleButton.setGraphic(new IconImageView(kujiin.xml.Preferences.ICON_SHUFFLE, 20.0));
            EntrainmentMuteButton.setGraphic(new IconImageView(kujiin.xml.Preferences.ICON_MUTE, 20.0));
            AmbienceMuteButton.setGraphic(new IconImageView(kujiin.xml.Preferences.ICON_MUTE, 20.0));
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
    private void setPlayerstate(PlayerState playerstate) {

        playerState = playerstate;
    }
    public boolean isExitprogram() {
        return exitprogram;
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
                selectedPlaybackItem = SessionInProgress.getPlaybackItems().get(0);
                currententrainmentvolume = Preferences.getPlaybackOptions().getEntrainmentvolume();
                currentambiencevolume = Preferences.getPlaybackOptions().getAmbiencevolume();
                setupsession();
                start();
                break;
            case PAUSED:
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
            if (playerState == PLAYING) {pausewithoutanimation();}
            if (new ConfirmationDialog(Preferences, "Confirmation", "End Session Early?", null).getResult()) {
                endsessionprematurely(true);
            } else { if (playerState == PLAYING) {resume();} }
        }
    }

// Playback
    // UI Update
        // Player
    private void updateplayerui() {
        PlayerState p = playerState;
        if (p == PLAYING || p == FADING_PLAY || p == FADING_PAUSE || p == FADING_RESUME || p == FADING_STOP) {
            try {
                selectedPlaybackItem.syncelapsedtime(playbackItemStopWatch);
                AllGoals.calculateifPlaybackItemgoalscompleted(selectedPlaybackItem.getCreationindex(), selectedPlaybackItem.getTotalpracticetime(playbackItemStopWatch));
                SessionInProgress.syncelapsedduration(sessionStopWatch);
                AllGoals.calculateifTotalGoalsCompleted(totalpracticedtime.add(Duration.millis(sessionStopWatch.getTime())));
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
                Boolean displaynormaltime = true;
                if (displaynormaltime) {
                    SessionTotalTime.setText(Util.formatdurationtoStringDecimalWithColons(SessionInProgress.getExpectedSessionDuration()));
                    StringBuilder stringBuilder = new StringBuilder();
                    Duration timeleft = SessionInProgress.getExpectedSessionDuration().subtract(SessionInProgress.getSessionPracticedTime());
                    LocalTime completiontime = LocalTime.now().plusSeconds((long) timeleft.toSeconds());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
                    String completiontext = completiontime.format(formatter);
                    SessionTotalTime.setTooltip(new Tooltip("Estimated Completion Time: " + completiontext));
                }
                else {SessionTotalTime.setText(Util.formatdurationtoStringDecimalWithColons(SessionInProgress.getExpectedSessionDuration().subtract(SessionInProgress.getSessionPracticedTime())));}
                updategoalsui();
                updateambienceui();
            } catch (Exception ignored) {ignored.printStackTrace();}
        }
    }
    private void toggleplayerbuttons() {
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
        PlayMenuItem.setDisable(playing || fade_play || fade_resume || fade_pause || fade_stop || transitioning);
        PauseButton.setDisable(paused || fade_play || fade_resume || fade_pause || fade_stop || idle || transitioning);
        PauseMenuItem.setDisable(paused || fade_play || fade_resume || fade_pause || fade_stop || idle || transitioning);
        StopButton.setDisable(stopped || fade_play || fade_resume || fade_pause || fade_stop || idle || transitioning);
        StopMenuItem.setDisable(stopped || fade_play || fade_resume || fade_pause || fade_stop || idle || transitioning);
        AmbienceMenu.setDisable(stopped || fade_play || fade_resume || fade_pause || fade_stop || idle || transitioning);
        AmbienceShuffleButton.setDisable(! playing);
        AmbiencePreviousButton.setDisable(! playing);
        AmbiencePauseButton.setDisable(! playing);
        AmbienceNextButton.setDisable(! playing);
        if (ambienceactive()) {
            if (AmbiencePresetTab.isSelected()) {
                AmbienceNextButton.setDisable(selectedPlaybackItem.getAmbience().getSessionAmbience().size() == 1);
                AmbiencePreviousButton.setDisable(selectedPlaybackItem.getAmbience().getSessionAmbience().size() == 1);
                AmbienceShuffleButton.setDisable(selectedPlaybackItem.getAmbience().getSessionAmbience().size() < 3);
            } else if (AmbienceAvailableTab.isSelected()) {
                AmbienceNextButton.setDisable(selectedPlaybackItem.getAmbience().getAvailableAmbience().size() == 1);
                AmbiencePreviousButton.setDisable(selectedPlaybackItem.getAmbience().getAvailableAmbience().size() == 1);
            }
        }
        toggleplayervolumecontrols();
    }
    private void toggleplayervolumecontrols() {
        boolean enabled = playerState == PLAYING;
        EntrainmentVolume.setDisable(! enabled);
        AmbienceVolume.setDisable(! enabled);
    }
        // Playlist
    private void updateplaylist() {
        PlaylistTableView.getItems().clear();
        ObservableList<PlaylistTableItem> playlistitems = FXCollections.observableArrayList();
        for (PlaybackItem i : SessionInProgress.getPlaybackItems()) {
            int percentage;
            String progress;
            if (! i.isPracticecompleted()) {
                float totalprogress = (float) i.getPracticeTime() / (float) i.getExpectedDuration();
                percentage = new Double(totalprogress * 100).intValue();
                progress = Util.formatdurationtoStringDecimalWithColons(new Duration(i.getPracticeTime())) + " > " + Util.formatdurationtoStringDecimalWithColons(new Duration(i.getExpectedDuration()));
            } else {
                percentage = 100;
                String expectedduration = Util.formatdurationtoStringDecimalWithColons(new Duration(i.getExpectedDuration()));
                progress = expectedduration + " > " + expectedduration;
            }
            playlistitems.add(new PlaylistTableItem(i, i.getName(), progress, percentage + "%"));
        }
        PlaylistTableView.setItems(playlistitems);
        PlaylistTableView.getSelectionModel().select(SessionInProgress.getPlaybackItems().indexOf(selectedPlaybackItem));

    }
        // Ambience
    private boolean ambienceactive() {return selectedPlaybackItem != null && selectedPlaybackItem.getAmbience().hasAvailableAmbience() && ambienceplayer != null;}
    private void populateambienceplaylist() {
    // Preset
        if (selectedPlaybackItem.getAmbience().hasPresetAmbience()) {
            ObservableList<AmbiencePlaylistTableItem> presetitems = FXCollections.observableArrayList();
            List<SoundFile> presetambience = selectedPlaybackItem.getAmbience().getSessionAmbience();
            int count = 1;
            for (SoundFile i : presetambience) {
                presetitems.add(new AmbiencePlaylistTableItem(count, i.getName(), Util.formatdurationtoStringDecimalWithColons(Duration.millis(i.getDuration())), i.getFile()));
                count++;
            }
            AmbiencePlaylistTable_Preset.setItems(presetitems);
        }
    // Available
        ObservableList<AmbiencePlaylistTableItem> availableitems = FXCollections.observableArrayList();
        List<SoundFile> availableambience = selectedPlaybackItem.getAmbience().getAvailableAmbience();
        int counted = 1;
        for (SoundFile i : availableambience) {
            availableitems.add(new AmbiencePlaylistTableItem(counted, i.getName(), Util.formatdurationtoStringDecimalWithColons(Duration.millis(i.getDuration())), i.getFile()));
            counted++;
        }
        AmbiencePlaylistTable_Available.setItems(availableitems);
    // Sync Buttons
        if (AmbiencePresetTab.isSelected()) {
            if (selectedPlaybackItem.getAmbience().hasPresetAmbience()) {
                AmbienceNextButton.setDisable(selectedPlaybackItem.getAmbience().getSessionAmbience().size() == 1);
                AmbiencePreviousButton.setDisable(selectedPlaybackItem.getAmbience().getSessionAmbience().size() == 1);
                AmbienceShuffleButton.setDisable(selectedPlaybackItem.getAmbience().getSessionAmbience().size() < 3);
            } else {
                AmbienceNextButton.setDisable(true);
                AmbiencePreviousButton.setDisable(true);
                AmbienceShuffleButton.setDisable(true);
            }
        } else {
            AmbienceNextButton.setDisable(selectedPlaybackItem.getAmbience().getAvailableAmbience().size() == 1);
            AmbiencePreviousButton.setDisable(selectedPlaybackItem.getAmbience().getAvailableAmbience().size() == 1);
        }
    }
    private void updateambienceui() {
        if (selectedPlaybackItem == null) { AmbienceTab.setDisable(true); return; }
        AmbiencePresetTab.setDisable(! selectedPlaybackItem.getAmbience().hasPresetAmbience());
        AmbienceTab.setDisable(! selectedPlaybackItem.getAmbience().hasAvailableAmbience());
        AmbiencePresetTab.setDisable(! selectedPlaybackItem.getAmbience().hasPresetAmbience());
        if (ambienceplayer != null) {
            if (currentambiencesoundfile != null) {
                double percentage;
                percentage = ambienceplayer.getCurrentTime().toMillis() / ambienceplayer.getTotalDuration().toMillis();
                CurrentAmbienceProgressBar.setProgress(percentage);
                String name;
                name = currentambiencesoundfile.getName();
                if (name.length() > 24) { name = name.substring(0, 24); }
                String percenttext = String.format("(%.1f%%)", percentage * 100);
                CurrentAmbiencePercentage.setText(name + " " + percenttext);
            }
        } else {
            CurrentAmbiencePercentage.setText("Nothing Playing");
            CurrentAmbienceProgressBar.setProgress(0.0);
        }
    }
    public void nextambiencebuttonpressed() {
        playnextambience();
    }
    public void previousambiencebuttonpressed() {
        playpreviousambience();
    }
    public void pauseambiencebuttonpressed() {
        if (selectedPlaybackItem != null) {
            if (currentambiencesoundfile != null) {
                switch (ambienceplayer.getStatus()) {
                    case PAUSED:
                        resumeambience();
                        // TODO Set Play Button Icon Here
                        break;
                    case PLAYING:
                        pauseambience();
                        // TODO Set Pause Button Icon Here
                        break;
                }
            } else { startambience(); }
        }
    }
    public void shuffleambiencebuttonpressed() {
        if (selectedPlaybackItem != null && selectedPlaybackItem.getAmbience().hasPresetAmbience()) {
            List<SoundFile> ambiencelist = selectedPlaybackItem.getAmbience().getSessionAmbience();
            while (true) {
                Collections.shuffle(ambiencelist);
                if (ambiencelist.size() > 2) {
                    if (ambiencelist.get(0).equals(currentambiencesoundfile) && ! ambiencelist.get(1).equals(currentambiencesoundfile)) {break;}
                } else { break; }
            }
            selectedPlaybackItem.getAmbience().setSessionAmbience(ambiencelist);
            updateambienceui();
        }
    }
        // Goals
    private void updategoalsui() {
            Duration practiceduration = sessions.gettotalpracticedtime(selectedPlaybackItem, false);
            practiceduration = practiceduration.add(SessionInProgress.getSessionPracticedTime());
            String practicetime = Util.formatdurationtoStringSpelledOutShort(practiceduration, true);
            Goals_SessionPartPracticedTime.setText(practicetime);
            String goaltime;
            Double percentage;
            if (selectedPlaybackItem != null && AllGoals.getplaybackItemGoals(selectedPlaybackItem.getCreationindex()) != null) {
                PlaybackItemGoals playbackItemGoals = AllGoals.getplaybackItemGoals(selectedPlaybackItem.getCreationindex());
                Goal currentgoal = playbackItemGoals.getCurrentGoal();
                if (currentgoal != null) {percentage = (practiceduration.toMillis() / currentgoal.getDuration().toMillis());}
                else {percentage = 0.0;}
//            percentagetext = String.format("(%.1f%%)", percentage * 100);
                if (currentgoal != null) {goaltime = Util.formatdurationtoStringSpelledOutShort(currentgoal.getDuration(), true);}
                else {goaltime = "No Goal Set";}
            } else {
//            percentagetext = "No Goal Set";
                goaltime = "No Goal Set";
                percentage = 0.0;
            }
            Goals_SessionPartGoalTime.setText(goaltime);
            GoalProgress.setDisable(percentage == 0.0);
            if (percentage > 0.0) { GoalProgress.setProgress(percentage); }
            else {GoalProgress.setProgress(0.0);}
        }
    // Playback Methods
    private void setupsession() {
        selectedPlaybackItem = SessionInProgress.getPlaybackItems().get(0);
        SessionInProgress.setSessionPracticedTime();
        SessionInProgress.calculateactualduration();
        volume_unbindentrainment();
        SessionInProgress.addPlaycount();
        SessionInProgress.setTimestarted(LocalDateTime.now());
    }
    private void start() {
        updateuitimeline.play();
        fade_play_value = Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_play_value());
        fade_stop_value = Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_stop_value());
        Duration playbackitemduration = new Duration(selectedPlaybackItem.getExpectedDuration());
        if (playbackitemduration.lessThan(fade_play_value.add(fade_stop_value))) {
            while (fade_play_value.add(fade_stop_value).greaterThanOrEqualTo(playbackitemduration)) {
                fade_play_value = fade_play_value.subtract(Duration.seconds(0.5));
                fade_stop_value = fade_stop_value.subtract(Duration.seconds(0.5));
            }
        }
        setupentrainmentfadeanimations();
        if (selectedPlaybackItem.getAmbience().hasAvailableAmbience()) {setupambiencefadeanimations();}
        ReferenceControls.setDisable(false);
        PlaybackItemEntrainment playbackItemEntrainment = availableEntrainments.getsessionpartEntrainment(selectedPlaybackItem);
        boolean isLastSessionPart = SessionInProgress.getPlaybackItems().indexOf(selectedPlaybackItem) == SessionInProgress.getPlaybackItems().size() - 1;
        if (! selectedPlaybackItem.isRampOnly() || isLastSessionPart) {entrainmentplayer = new MediaPlayer(new Media(playbackItemEntrainment.getFreq().getFile().toURI().toString()));}
        else {entrainmentplayer = new MediaPlayer(new Media(rampfiles.getRampFile(selectedPlaybackItem, SessionInProgress.getPlaybackItems().get(SessionInProgress.getPlaybackItems().indexOf(selectedPlaybackItem) + 1)).getFile().toURI().toString()));}
        entrainmentplayer.setVolume(0.0);
        if (! selectedPlaybackItem.isRampOnly()) {entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);}
        entrainmentplayer.setOnError(this::entrainmenterror);
        entrainmentplayer.play();
        if (! sessionStopWatch.isStarted()) { sessionStopWatch.start();}
        playbackItemStopWatch = new StopWatch();
        playbackItemStopWatch.start();
        timeline_progresstonextsessionpart = new Timeline(new KeyFrame(new Duration(selectedPlaybackItem.getExpectedDuration()), ae -> progresstonextsessionpart()));
        timeline_progresstonextsessionpart.play();
        if (! selectedPlaybackItem.isRampOnly() && ! isLastSessionPart && Preferences.getSessionOptions().getRampenabled()) {
            SoundFile rampfile = rampfiles.getRampFile(selectedPlaybackItem, SessionInProgress.getPlaybackItems().get(SessionInProgress.getPlaybackItems().indexOf(selectedPlaybackItem) + 1));
            Duration timetillendingramp = new Duration(selectedPlaybackItem.getExpectedDuration()).subtract(Duration.millis(rampfile.getDuration()));
            if (timetillendingramp.greaterThan(Duration.ZERO)) {
                timeline_start_ending_ramp = new Timeline(new KeyFrame(new Duration(selectedPlaybackItem.getExpectedDuration()).subtract(Duration.millis(rampfile.getDuration())), (ActionEvent ae) -> {
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
        }
        if (fade_entrainment_stop != null) {
            Duration startfadeout = new Duration(selectedPlaybackItem.getExpectedDuration());
            if (selectedPlaybackItem.isRampOnly()) {startfadeout = startfadeout.subtract(Duration.seconds(kujiin.xml.Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
            else {startfadeout = startfadeout.subtract(fade_stop_value);}
            timeline_fadeout_timer = new Timeline(new KeyFrame(startfadeout, ae -> {
                setPlayerstate(FADING_STOP);
                toggleplayerbuttons();
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
            setPlayerstate(FADING_PLAY);
            fade_entrainment_play.play();
        } else {
            entrainmentplayer.setVolume(currententrainmentvolume);
            String percentage = new Double(currententrainmentvolume * 100).intValue() + "%";
            EntrainmentVolumePercentage.setText(percentage);
            setPlayerstate(PLAYING);
            volume_bindentrainment();
        }
        if (selectedPlaybackItem.getAmbience().hasAvailableAmbience()) {
            populateambienceplaylist();
            if (selectedPlaybackItem.getAmbience().hasPresetAmbience()) {
                if (currentambiencevolume == null) { currentambiencevolume = Preferences.getPlaybackOptions().getAmbiencevolume(); }
                volume_unbindambience();
                currentambiencesoundfile = selectedPlaybackItem.getAmbience().getnextpresetambienceforplayback(null);
                ambienceplayer = new MediaPlayer(new Media(currentambiencesoundfile.getFile().toURI().toString()));
                ambienceplayer.setVolume(0.0);
                ambienceplayer.setOnEndOfMedia(this::playnextambience);
                ambienceplayer.setOnError(this::ambienceerror);
                ambienceplayer.play();
                if (fade_ambience_play != null) {fade_ambience_play.play();}
                else {
                    ambienceplayer.setVolume(currentambiencevolume);
                    String percentage = new Double(currentambiencevolume * 100).intValue() + "%";
                    AmbienceVolumePercentage.setText(percentage);
                    volume_bindambience();
                }
            }
        }
        toggleplayerbuttons();
        getScene().addEventHandler(KeyEvent.KEY_PRESSED, AmbienceSwitchWithKeyboard);
    }
    private void resume() {
        updateuitimeline.play();
        volume_unbindentrainment();
        SessionInProgress.endbreak();
        entrainmentplayer.play();
        sessionStopWatch.resume();
        playbackItemStopWatch.resume();
        if (fade_entrainment_resume != null && sessionparttimeleft().greaterThan(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_resume_value()))) {
            entrainmentplayer.setVolume(0.0);
            if (fade_entrainment_resume.getStatus() == Animation.Status.RUNNING) {return;}
            setPlayerstate(FADING_RESUME);
            fade_entrainment_resume.play();
        } else {
            entrainmentplayer.setVolume(currententrainmentvolume);
            volume_bindentrainment();
            setPlayerstate(PLAYING);
            timeline_progresstonextsessionpart.play();
            if (Preferences.getSessionOptions().getRampenabled() && timeline_start_ending_ramp.getStatus() == Animation.Status.PAUSED) {
                timeline_start_ending_ramp.play();}
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.play();}
        }
        if (ambienceactive()) {
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
        sessionStopWatch.suspend();
        playbackItemStopWatch.suspend();
        SessionInProgress.startbreak();
        if (fade_entrainment_pause != null && sessionparttimeleft().greaterThan(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_pause_value()))) {
            if (ambienceactive() && fade_ambience_pause.getStatus() == Animation.Status.RUNNING) {return;}
            setPlayerstate(FADING_PAUSE);
            fade_entrainment_pause.play();
            if (ambienceactive()) {
                volume_unbindambience();
                fade_ambience_pause.play();
            }
        } else {pausewithoutanimation();}
        toggleplayerbuttons();
    }
    private void pausewithoutanimation() {
        setPlayerstate(PAUSED);
        entrainmentplayer.pause();
        timeline_progresstonextsessionpart.pause();
        if (Preferences.getSessionOptions().getRampenabled() && timeline_start_ending_ramp != null && timeline_start_ending_ramp.getStatus() == Animation.Status.RUNNING) {
            timeline_start_ending_ramp.pause();}
        if (timeline_fadeout_timer != null) {timeline_fadeout_timer.pause();}
        if (ambienceactive()) {
            volume_unbindambience();
            ambienceplayer.pause();
        }
    }
    private void stop() {
        volume_unbindentrainment();
        if (fade_entrainment_stop != null && sessionparttimeleft().greaterThan(fade_stop_value)) {
            if (fade_entrainment_stop.getStatus() == Animation.Status.RUNNING) {return;}
            fade_entrainment_stop.play();
            setPlayerstate(FADING_STOP);
            if (ambienceactive()) {
                volume_unbindambience();
                fade_ambience_stop.play();
            }
        } else {
            setPlayerstate(STOPPED);
            entrainmentplayer.stop();
            entrainmentplayer.dispose();
            timeline_progresstonextsessionpart.stop();
            if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop();}
            if (ambienceactive()) {
                volume_unbindambience();
                ambienceplayer.stop();
                ambienceplayer.dispose();
            }
        }
        toggleplayerbuttons();
    }
    private void progresstonextsessionpart() {
        try {
            switch (playerState) {
                case TRANSITIONING:
                    try {
                        cleanupPlayersandAnimations();
                        int index = SessionInProgress.getPlaybackItems().indexOf(selectedPlaybackItem) + 1;
                        selectedPlaybackItem.setPracticecompleted(true);
                        selectedPlaybackItem = SessionInProgress.getPlaybackItems().get(index);
                        start();
                        if (ReferenceToggleCheckBox.isSelected() && ReferenceTypeChoiceBox.getSelectionModel().getSelectedIndex() != -1) {loadreference();}
                    } catch (IndexOutOfBoundsException ignored) {
                        cleanupPlayersandAnimations();
                        setPlayerstate(IDLE);
                        endofsession();
                    }
                    break;
                case PLAYING:
                case FADING_STOP:
                    ReferenceControls.setDisable(true);
                    transition();
                    break;
            }
        } catch (Exception ignored) {
            System.out.println("Exception Occured " + ignored.getMessage());
            ignored.printStackTrace();
        }
    }
    private void transition() {
        selectedPlaybackItem.updateduration(new Duration(selectedPlaybackItem.getExpectedDuration()));
        updategoalsui();
        updateambienceui();
        if (Preferences.getSessionOptions().getAlertfunction() && ! selectedPlaybackItem.equals(SessionInProgress.getPlaybackItems().get(SessionInProgress.getPlaybackItems().size() - 1))) {
            Media alertmedia = new Media(Preferences.getSessionOptions().getAlertfilelocation());
            MediaPlayer alertplayer = new MediaPlayer(alertmedia);
            alertplayer.play();
            setPlayerstate(TRANSITIONING);
            PlayButton.setDisable(true);
            PauseButton.setDisable(true);
            StopButton.setDisable(true);
            alertplayer.setOnEndOfMedia(() -> {
                setPlayerstate(TRANSITIONING);
                toggleplayerbuttons();
                progresstonextsessionpart();
            });
        } else {
            setPlayerstate(TRANSITIONING);
            progresstonextsessionpart();
        }
    }
    // Entrainment
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
    // Ambience
    private void startambience() {
        setupambiencefadeanimations();
        if (AmbiencePresetTab.isSelected()) { playambience(selectedPlaybackItem.getAmbience().getPreset(0)); }
        else { playambience(selectedPlaybackItem.getAmbience().getAvailable(0)); }
    }
    private void pauseambience() {
        if (playerState == PLAYING) {
            AmbiencePauseButton.setGraphic(new IconImageView(kujiin.xml.Preferences.ICON_PLAY, 20.0));
            ambienceplayer.pause();
        }
    }
    private void resumeambience() {
        if (playerState == PLAYING) {
            AmbiencePauseButton.setGraphic(new IconImageView(kujiin.xml.Preferences.ICON_PAUSE, 20.0));
            ambienceplayer.play();
        }
    }
    private void playambience(SoundFile soundFile) {
        volume_unbindambience();
        try { ambienceplayer.dispose(); ambienceplayer = null; } catch (NullPointerException ignored) {}
        currentambiencesoundfile = soundFile;
        ambienceplayer = new MediaPlayer(new Media(currentambiencesoundfile.getFile().toURI().toString()));
        ambienceplayer.setOnEndOfMedia(this::playnextambience);
        ambienceplayer.setOnError(this::ambienceerror);
        ambienceplayer.setVolume(currentambiencevolume);
        ambienceplayer.play();
        ambienceplayer.setOnPlaying(() -> {
            volume_bindambience();
            updateambienceui();
            if (AmbiencePresetTab.isSelected()) {
                AmbiencePlaylistTable_Preset.getSelectionModel().select(selectedPlaybackItem.getAmbience().getSessionAmbience().indexOf(soundFile));
            } else {
                AmbiencePlaylistTable_Available.getSelectionModel().select(selectedPlaybackItem.getAmbience().getAvailableAmbience().indexOf(soundFile));
            }
        });
    }
    private void playnextambience() {
        if (AmbiencePresetTab.isSelected()) {
            playambience(selectedPlaybackItem.getAmbience().getnextpresetambienceforplayback(currentambiencesoundfile));
        } else {
            playambience(selectedPlaybackItem.getAmbience().getnextavailableambienceforplayback(currentambiencesoundfile));
        }
    }
    private void playpreviousambience() {
        playambience(selectedPlaybackItem.getAmbience().getpreviouspresetambienceforplayback(currentambiencesoundfile));
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
    // End Of Session
    private void reset() {
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
    private void endofsession() {
        if (sessionStopWatch.isStarted()) { sessionStopWatch.stop();}
        if (playbackItemStopWatch.isStarted()) {playbackItemStopWatch.stop();}
        SessionInProgress.setSessionPracticedTime(SessionInProgress.getExpectedSessionDuration().toMillis());
        setPlayerstate(STOPPED);
        // TODO Prompt For Export
        updategoalsui();
        updateambienceui();
        reset();
        if (! testingmode) {
            SessionInProgress.addCompletedcount();
            final Session sessioninprogress = SessionInProgress;
            try {
                SessionComplete sessionComplete = new SessionComplete(SessionInProgress, true);
                sessionComplete.initModality(Modality.APPLICATION_MODAL);
                sessionComplete.show();
                sessionComplete.setOnHidden(event -> {
                    if (sessionComplete.needtosetNotes()) {sessioninprogress.setNotes(sessionComplete.getNotes());}
                    if (AllGoals.sessionhasgoalscompleted()) {
                        GoalsCompletedDialog goalsCompletedDialog = new GoalsCompletedDialog(AllGoals);
                        goalsCompletedDialog.initModality(Modality.APPLICATION_MODAL);
                        goalsCompletedDialog.show();
                        AllGoals.marshall();
                    }
                    if (sessionComplete.getSessionCompleteDirections() != null) {
                        switch (sessionComplete.getSessionCompleteDirections()) {
                            case EXITPROGRAM:
                                exitprogram = true;
                                closedialog();
                                break;
                            case KEEPPLAYEROPEN:
                                break;
                            case CLOSEPLAYER:
                                closedialog();
                                break;
                        }
                    }
                });
            } catch (Exception e) {e.printStackTrace();}
            SessionInProgress = sessioninprogress;
            sessions.add(SessionInProgress);
        }
        SessionInProgress = null;
    }
    private boolean endsessionprematurely(boolean resetdialogcontrols) {
        if (sessionStopWatch.isStarted()) {sessionStopWatch.suspend();}
        if (playbackItemStopWatch.isStarted()) {sessionStopWatch.suspend();}
        pausewithoutanimation();
        updateuitimeline.pause();
        if (testingmode || new ConfirmationDialog(Preferences, "End Session Early", "Session Is Not Completed.", "End Session Prematurely?", "End Session", "Continue").getResult()) {
            if (sessionStopWatch.isStarted()) { sessionStopWatch.stop();}
            setPlayerstate(STOPPED);
            if (! testingmode) { sessions.add(SessionInProgress); }
            if (resetdialogcontrols) {
                updategoalsui();
                updateambienceui();
                reset();
                cleanupPlayersandAnimations();
            }
            SessionComplete sessionComplete = new SessionComplete(SessionInProgress, false);
            sessionComplete.initModality(Modality.APPLICATION_MODAL);
            sessionComplete.showAndWait();
            resetsessionpracticedtime();
            if (sessionComplete.getSessionCompleteDirections() != null) {
                switch (sessionComplete.getSessionCompleteDirections()) {
                    case EXITPROGRAM:
                        exitprogram = true;
                        close();
                        break;
                    case KEEPPLAYEROPEN:
                        break;
                    case CLOSEPLAYER:
                        close();
                        break;
                }
            }
            return true;
        } else {playbuttonpressed(); return false;}
    }
    public void togglevolumebinding() {
        if (selectedPlaybackItem != null && (playerState == IDLE || playerState == STOPPED)) {
            volume_rebindentrainment();
            if (ambienceactive()) {volume_rebindambience();}
        }
    }
    private void resetsessionpracticedtime() {
        for (PlaybackItem i : SessionInProgress.getPlaybackItems()) {i.resetpracticetime();}
        SessionInProgress.resetpracticetime();
    }
    // Animation
    private void setupambiencefadeanimations() {
        // PLAY
        fade_ambience_play = new Transition() {
            {if (selectedPlaybackItem.isRampOnly()) {setCycleDuration(Duration.seconds(kujiin.xml.Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
            else {setCycleDuration(fade_play_value);}}

            @Override
            protected void interpolate(double frac) {
                if (ambienceplayer != null && currentambiencevolume > 0.0) {
                    try {
                        double ambiencevolume = frac * currentambiencevolume;
                        String percentage = new Double(ambiencevolume * 100).intValue() + "%";
                        ambienceplayer.setVolume(ambiencevolume);
                        AmbienceVolume.setValue(ambiencevolume);
                        AmbienceVolumePercentage.setText(percentage);
                    } catch (RuntimeException ignored) {}
                }
            }
        };
        fade_ambience_play.setOnFinished(event -> volume_bindambience());
        // RESUME
        fade_ambience_resume = new Transition() {
            {setCycleDuration(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_resume_value()));}

            @Override
            protected void interpolate(double frac) {
                if (ambienceplayer != null && currentambiencevolume > 0.0) {
                    try {
                        double ambiencevolume = frac * currentambiencevolume;
                        String percentage = new Double(ambiencevolume * 100).intValue() + "%";
                        ambienceplayer.setVolume(ambiencevolume);
                        AmbienceVolume.setValue(ambiencevolume);
                        AmbienceVolumePercentage.setText(percentage);
                    } catch (RuntimeException ignored) {}
                }
            }
        };
        fade_ambience_resume.setOnFinished(event -> volume_bindambience());
        // PAUSE
        fade_ambience_pause = new Transition() {
            {
                setCycleDuration(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_pause_value()));
            }

            @Override
            protected void interpolate(double frac) {
                if (ambienceplayer != null && currentambiencevolume > 0.0) {
                    try {
                        double ambiencevolume = currentambiencevolume - (frac * currentambiencevolume);
                        String percentage = new Double(ambiencevolume * 100).intValue() + "%";
                        ambienceplayer.setVolume(ambiencevolume);
                        AmbienceVolume.setValue(ambiencevolume);
                        AmbienceVolumePercentage.setText(percentage);
                    } catch (RuntimeException ignored) {}
                }
            }
        };
        fade_ambience_pause.setOnFinished(event -> ambienceplayer.pause());
        // STOP
        fade_ambience_stop = new Transition() {
            {if (selectedPlaybackItem.isRampOnly()) {setCycleDuration(Duration.seconds(kujiin.xml.Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
            else {setCycleDuration(fade_stop_value);}}

            @Override
            protected void interpolate(double frac) {
                if (ambienceplayer != null && currentambiencevolume > 0.0) {
                    try {
                        double ambiencevolume = currentambiencevolume - (frac * currentambiencevolume);
                        String percentage = new Double(ambiencevolume * 100).intValue() + "%";
                        ambienceplayer.setVolume(ambiencevolume);
                        AmbienceVolume.setValue(ambiencevolume);
                        AmbienceVolumePercentage.setText(percentage);
                    } catch (RuntimeException ignored) {}
                }
            }
        };
        fade_ambience_stop.setOnFinished(event -> {
            ambienceplayer.stop();
            ambienceplayer.dispose();
        });
    }
    private void setupentrainmentfadeanimations() {
        // PLAY
        if (selectedPlaybackItem.isRampOnly() || (Preferences.getPlaybackOptions().getAnimation_fade_play_enabled() && Preferences.getPlaybackOptions().getAnimation_fade_play_value() > 0.0)) {
            fade_entrainment_play = new Transition() {
                {if (selectedPlaybackItem.isRampOnly()) {setCycleDuration(Duration.seconds(kujiin.xml.Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
                else {setCycleDuration(fade_play_value);}}

                @Override
                protected void interpolate(double frac) {
                    if (entrainmentplayer != null && currententrainmentvolume > 0.0) {
                        try {
                            double entrainmentvolume = frac * currententrainmentvolume;
                            String percentage = new Double(entrainmentvolume * 100).intValue() + "%";
                            entrainmentplayer.setVolume(entrainmentvolume);
                            EntrainmentVolume.setValue(entrainmentvolume);
                            EntrainmentVolumePercentage.setText(percentage);
                        } catch (RuntimeException ignored) {}
                    }
                }
            };
            fade_entrainment_play.setOnFinished(event -> {
                setPlayerstate(PLAYING);
                toggleplayerbuttons();
                volume_bindentrainment();
            });
        }
        // RESUME
        if (Preferences.getPlaybackOptions().getAnimation_fade_resume_enabled() && Preferences.getPlaybackOptions().getAnimation_fade_resume_value() > 0.0) {
            fade_entrainment_resume = new Transition() {
                {setCycleDuration(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_resume_value()));}

                @Override
                protected void interpolate(double frac) {
                    if (entrainmentplayer != null && currententrainmentvolume > 0.0) {
                        try {
                            double entrainmentvolume = frac * currententrainmentvolume;
                            String percentage = new Double(entrainmentvolume * 100).intValue() + "%";
                            entrainmentplayer.setVolume(entrainmentvolume);
                            EntrainmentVolume.setValue(entrainmentvolume);
                            EntrainmentVolumePercentage.setText(percentage);
                        } catch (RuntimeException ignored) {}
//                        if (referencecurrentlyDisplayed()) {
//                            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setValue(entrainmentvolume);
//                            root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
//                        }
                    }
                }
            };
            fade_entrainment_resume.setOnFinished(event -> {
                setPlayerstate(PLAYING);
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
        }
        // PAUSE
        if (Preferences.getPlaybackOptions().getAnimation_fade_pause_enabled() && Preferences.getPlaybackOptions().getAnimation_fade_pause_value() > 0.0) {
            fade_entrainment_pause = new Transition() {
                {setCycleDuration(Duration.seconds(Preferences.getPlaybackOptions().getAnimation_fade_pause_value()));}

                @Override
                protected void interpolate(double frac) {
                    if (entrainmentplayer != null && currententrainmentvolume > 0.0) {
                        try {
                            double entrainmentvolume = currententrainmentvolume - (frac * currententrainmentvolume);
                            String percentage = new Double(entrainmentvolume * 100).intValue() + "%";
                            entrainmentplayer.setVolume(entrainmentvolume);
                            EntrainmentVolume.setValue(entrainmentvolume);
                            EntrainmentVolumePercentage.setText(percentage);
                        } catch (RuntimeException ignored) {}
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
                setPlayerstate(PAUSED);
                toggleplayerbuttons();
            });
        }
        // STOP
        if (selectedPlaybackItem.isRampOnly() || (Preferences.getPlaybackOptions().getAnimation_fade_stop_enabled() && Preferences.getPlaybackOptions().getAnimation_fade_stop_value() > 0.0)) {
            fade_entrainment_stop = new Transition() {
                {if (selectedPlaybackItem.isRampOnly()) {setCycleDuration(Duration.seconds(kujiin.xml.Preferences.DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION));}
                else {setCycleDuration(fade_stop_value);}}

                @Override
                protected void interpolate(double frac) {
                    if (entrainmentplayer != null && currententrainmentvolume > 0.0) {
                        try {
                            double entrainmentvolume = currententrainmentvolume - (frac * currententrainmentvolume);
                            String percentage = new Double(entrainmentvolume * 100).intValue() + "%";
                            entrainmentplayer.setVolume(entrainmentvolume);
                            EntrainmentVolume.setValue(entrainmentvolume);
                            EntrainmentVolumePercentage.setText(percentage);
                        } catch (RuntimeException ignored) {}
//                        if (referencecurrentlyDisplayed()) {
//                            root.getSessionCreator().getDisplayReference().EntrainmentVolumeSlider.setValue(fadeoutvolume);
//                            root.getSessionCreator().getDisplayReference().EntrainmentVolumePercentage.setText(percentage);
//                        }
                    }
                }
            };
            fade_entrainment_stop.setOnFinished(event -> {
                if (playerState != TRANSITIONING) {setPlayerstate(STOPPED);}
                entrainmentplayer.stop();
                entrainmentplayer.dispose();
                if (Preferences.getSessionOptions().getRampenabled() && timeline_start_ending_ramp != null && timeline_start_ending_ramp.getStatus() == Animation.Status.RUNNING) {timeline_start_ending_ramp.stop();}
                timeline_progresstonextsessionpart.stop();
                if (timeline_fadeout_timer != null) {timeline_fadeout_timer.stop();}
                toggleplayerbuttons();
            });
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
        } catch (Exception ignored) {}
    }
    // Volume Control
    private void volume_bindentrainment() {
        EntrainmentVolume.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
        EntrainmentVolume.setDisable(false);
        EntrainmentVolume.setOnMouseDragged(event1 -> {
            String percentage = new Double(EntrainmentVolume.getValue() * 100).intValue() + "%";
            currententrainmentvolume = EntrainmentVolume.getValue();
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
    private void toggleambiencemute() {}
    private void toggleentrainmentmute() {}


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

// Reference
    public void referencetoggled() {
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
                        ta.setEditable(false);
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
                ta.setEditable(false);
                ReferenceContentPane.setContent(ta);
            }
        } catch (NullPointerException ignored) {
            TextArea ta = new TextArea("No Session Playing");
            ta.setEditable(false);
            ta.prefWidthProperty().bind(ReferenceContentPane.widthProperty());
            ta.prefHeightProperty().bind(ReferenceContentPane.heightProperty());
            ReferenceContentPane.setContent(ta);
        }
    }
    private void closedialog() {close();}

    public static class PlaylistTableItem {
        StringProperty itemname;
        StringProperty duration;
        StringProperty percentcompleted;
        PlaybackItem playbackItem;

        public PlaylistTableItem(PlaybackItem playbackItem, String itemname, String duration, String percentcompleted) {
            this.itemname = new SimpleStringProperty(itemname);
            this.duration = new SimpleStringProperty(duration);
            this.percentcompleted = new SimpleStringProperty(percentcompleted);
            this.playbackItem = playbackItem;
        }

        public PlaybackItem getPlaybackItem() {
            return playbackItem;
        }
    }
    class AmbiencePlaylistTableItem {
        public IntegerProperty number;
        public StringProperty name;
        public StringProperty duration;
        private File file;

        public AmbiencePlaylistTableItem(int number, String name, String duration, File file) {
            this.number = new SimpleIntegerProperty(number);
            this.name = new SimpleStringProperty(name);
            this.duration = new SimpleStringProperty(duration);
            this.file = file;
        }

        public File getFile() {
            return file;
        }
    }
    enum PlayerExitState {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }
}