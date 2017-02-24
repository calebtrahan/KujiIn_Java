package kujiin.ui.playback;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Transition;
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
import kujiin.ui.dialogs.SessionDetails;
import kujiin.ui.dialogs.alerts.AnswerDialog;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.util.Util;
import kujiin.util.enums.PlayerState;
import kujiin.util.enums.ReferenceType;
import kujiin.xml.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static kujiin.util.enums.PlayerState.*;

public class Player extends Stage {
    // UI Elements
    public MenuBar TopMenuBar;
    public MenuItem ToggleFullScreenMenuItem;
    public MenuItem EnablePlaylistSelectionMenuItem;
    public MenuItem AboutMenuItem;
    public CheckBox ReferenceToggleCheckBox;
    public ChoiceBox ReferenceTypeChoiceBox;
    public TableView PlaylistTableView;
    public TableColumn NameColumn;
    public TableColumn DurationColumn;
    public TableColumn PercentColumn;
    public Label SessionCurrentTime;
    public ProgressBar SessionProgress;
    public Label SessionProgressPercentage;
    public Label SessionTotalTime;
    public Slider EntrainmentVolume;
    public Label EntrainmentVolumePercentage;
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
    private ArrayList<Session.PlaybackItem> allPlaybackItems;
    private int sessionitemindex;
    private MediaPlayer entrainmentplayer;
    private MediaPlayer ambienceplayer;
    private SoundFile currentambiencesoundfile;
    private Double currententrainmentvolume;
    private Double currentambiencevolume;
    private Duration sessionelapsedtime;
    private Duration sessionitemelapsedtime;
    private Duration sessionduration;
// Class Objects
    private Session SessionTemplate;
    private Session SessionInProgress;
    private Session.PlaybackItem selectedPlaybackItem;
    private Sessions Sessions;
    private Goals Goals;
    private Entrainments Entrainments;
    private Entrainment SessionPartEntrainment;
    private Ambiences Ambiences;
    private Ambience SessionPartAmbience;
    private PlayerState playerState;
    private Preferences Preferences;
    private ReferenceType referenceType;
// Toggles
    public Boolean displaynormaltime = true;
// Session And Goal Tracking
    public List<Session.PlaybackItem> sessionItemsWithGoalsCompletedThisPlayback;
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

    public Player(Session sessionInProgress) {
        try {
            SessionTemplate = sessionInProgress;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/playback/Player.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setTitle("Simple Ambience Editor");
            setOnCloseRequest(event -> closedialog());
        } catch (IOException ignored) {}
    }
    public void setupTooltips() {
        if (Preferences.getUserInterfaceOptions().getTooltips()) {
//            CurrentTopLabel.setTooltip(new Tooltip("Current SessionInProgress Part Playing"));
//            CurrentProgressDetails.setTooltip(new Tooltip("Current SessionInProgress Part Progress"));
//            CurrentProgressDetails.setTooltip(new Tooltip("Detailed Current SessionInProgress Part Progress"));
//            EntrainmentVolume.setTooltip(new Tooltip("Current Entrainment Volume"));
//            EntrainmentVolumeTopLabel.setTooltip(new Tooltip("Entrainment Volume"));
//            EntrainmentVolumePercentage.setTooltip(new Tooltip("Current Entrainment Volume Percentage"));
//            AmbienceVolume.setTooltip(new Tooltip("Current Ambience Volume"));
//            AmbienceVolumeTopLabel.setTooltip(new Tooltip("Ambience Volume"));
//            AmbienceVolumePercentage.setTooltip(new Tooltip("Current Ambience Volume Percentage"));
//            TotalProgress.setTooltip(new Tooltip("Total SessionInProgress Progress"));
//            TotalProgressDetails.setTooltip(new Tooltip("Detailed Total SessionInProgress Progress"));
//            ReferenceToggleCheckBox.setTooltip(new Tooltip("Display Reference During SessionInProgress Playback"));
        }
    }

// UI Methods
    private void playbuttonpressed() {
        switch (playerState) {
            case IDLE:
            case STOPPED:
                SessionInProgress = SessionTemplate;
//                setupKeyBoardShortcuts();
                sessionItemsWithGoalsCompletedThisPlayback = new ArrayList<>();
                sessionelapsedtime = Duration.ZERO;
                sessionitemelapsedtime = Duration.ZERO;
                for (Session.PlaybackItem i : allPlaybackItems) {sessionduration = sessionduration.add(new Duration(i.getDuration()));}
//                    TotalTotalTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessionduration));
                updateuitimeline = new Timeline(new KeyFrame(updateuifrequency, ae -> updateplayerui()));
                updateuitimeline.setCycleCount(Animation.INDEFINITE);
                updateuitimeline.play();
                sessionitemindex = 0;
                selectedPlaybackItem = allPlaybackItems.get(0);
                currententrainmentvolume = Preferences.getPlaybackOptions().getEntrainmentvolume();
                currentambiencevolume = Preferences.getPlaybackOptions().getAmbiencevolume();
                start();
                displayreferencefile();
                break;
            case PAUSED:
                updateuitimeline.play();
                resume();
                break;
        }
    }
    private void pausebuttonpressed() {
        if (playerState == PLAYING) {
            pause();
            updateuitimeline.pause();
        }
    }
    private void stopbuttonpressed() {
        if (playerState == PLAYING || playerState == PAUSED) {
            if (new ConfirmationDialog(Preferences, "Stop Session", "Really Stop Session?", "Session Will Be Stopped And Cannot Be Resumed",
                    "Stop Session", "Cancel").getResult()) {
                stop();
                updateuitimeline.stop();
            }
        }
    }
    private void ReferenceToggleCheckboxtoggled() {}
    private void referencetypechanged() {}

// Playback
    // UI Update
    private void updateplayerui() {
        try {
            sessionelapsedtime = sessionelapsedtime.add(updateuifrequency);
            sessionitemelapsedtime = sessionitemelapsedtime.add(updateuifrequency);
            Float currentprogress;
            Float totalprogress;
            try {
                if (sessionitemelapsedtime.greaterThan(Duration.ZERO)) {currentprogress = (float) sessionitemelapsedtime.toMillis() / (float) selectedPlaybackItem.getDuration();}
                else {currentprogress = (float) 0;}
            } catch (NullPointerException ignored) {currentprogress = (float) 0;}
            if (sessionelapsedtime.greaterThan(Duration.ZERO)) {totalprogress = (float) sessionelapsedtime.toMillis() / (float) sessionduration.toMillis();}
            else {totalprogress = (float) 0.0;}
            SessionProgress.setProgress(totalprogress * 100);
            BigDecimal bd = new BigDecimal(totalprogress);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            SessionProgressPercentage.setText(bd.doubleValue() + "%");
//            CurrentTopLabel.setText(selectedPlaybackItem.getName() + "(" + new Float(currentprogress * 100).intValue() + "%) [" + (allPlaybackItems.indexOf(selectedPlaybackItem) + 1)  + "/" + allPlaybackItems.size() + "]");
//            SessionTopLabel.setText("SessionInProgress (" + new Float(totalprogress * 100).intValue() + "%)");
            String currentparttime;
            String totalparttime;
            try {currentparttime = Util.formatdurationtoStringDecimalWithColons(sessionitemelapsedtime);}
            catch (NullPointerException ignored) {currentparttime = Util.formatdurationtoStringDecimalWithColons(Duration.ZERO);}
            if (displaynormaltime) {totalparttime = Util.formatdurationtoStringDecimalWithColons(new Duration(selectedPlaybackItem.getDuration()));}
            else {totalparttime = Util.formatdurationtoStringDecimalWithColons(new Duration(selectedPlaybackItem.getDuration()).subtract(sessionitemelapsedtime));}
//            CurrentProgressTime.setText(String.format("%s -> %s", currentparttime, totalparttime));
            String currenttotaltime;
            String totaltotaltime;
            currenttotaltime = Util.formatdurationtoStringDecimalWithColons(sessionelapsedtime);
            if (displaynormaltime) {totaltotaltime = Util.formatdurationtoStringDecimalWithColons(sessionduration);}
            else {totaltotaltime = Util.formatdurationtoStringDecimalWithColons(sessionduration.subtract(sessionelapsedtime));}
            SessionCurrentTime.setText(currenttotaltime);
//            try {
//                if (displayReference != null && displayReference.isShowing()) {
//                    displayReference.setCurrentProgress((double) currentprogress);
//                    displayReference.setTotalProgress((double) totalprogress);
//                    displayReference.setName(selectedPlaybackItem.getName());
//                }
//            } catch (Exception ignored) {}
            updatesessionui();
            updategoalui();
            if (playerState == PLAYING) {
                selectedPlaybackItem.updateduration(sessionitemelapsedtime);}
        } catch (Exception ignored) {ignored.printStackTrace();}
    }
    private void updatesessionui() {}
    private void updategoalui() {}
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
        ReferenceToggleCheckBox.setDisable(fade_play || fade_resume || fade_pause || fade_stop);
        if (referencecurrentlyDisplayed()) {
//            root.getSessionCreator().getDisplayReference().PlayButton.setDisable(playing || fade_play || fade_resume || fade_pause || fade_stop);
//            root.getSessionCreator().getDisplayReference().PauseButton.setDisable(paused || fade_play || fade_resume || fade_pause || fade_stop || idle);
//            root.getSessionCreator().getDisplayReference().StopButton.setDisable(stopped || fade_play || fade_resume || fade_pause || fade_stop || idle);
        }
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
    private void start() {
        sessionelapsedtime = Duration.ZERO;
        setupfadeanimations();
        volume_unbindentrainment();
        if (! selectedPlaybackItem.isRampOnly() || selectedPlaybackItem.getPlaybackindex() == SessionInProgress.getPlaybackItems().size() - 1) {entrainmentplayer = new MediaPlayer(new Media(SessionPartEntrainment.getFreq().getFile().toURI().toString()));}
        else {entrainmentplayer = new MediaPlayer(new Media(SessionPartEntrainment.getRampfile().getFile().toURI().toString()));}
        entrainmentplayer.setVolume(0.0);
        if (! selectedPlaybackItem.isRampOnly()) {entrainmentplayer.setOnEndOfMedia(this::playnextentrainment);}
        entrainmentplayer.setOnError(this::entrainmenterror);
        entrainmentplayer.play();
        timeline_progresstonextsessionpart = new Timeline(new KeyFrame(new Duration(selectedPlaybackItem.getDuration()), ae -> progresstonextsessionpart()));
        timeline_progresstonextsessionpart.play();
        boolean isLastSessionPart = allPlaybackItems.indexOf(selectedPlaybackItem) == allPlaybackItems.size() - 1;
        if (! selectedPlaybackItem.isRampOnly() && ! isLastSessionPart && Preferences.getSessionOptions().getRampenabled()) {
            timeline_start_ending_ramp = new Timeline(new KeyFrame(new Duration(selectedPlaybackItem.getDuration()).subtract(Duration.millis(SessionPartEntrainment.getRampfile().getDuration())), ae -> {
                volume_unbindentrainment();
                entrainmentplayer.stop();
                entrainmentplayer.dispose();
                entrainmentplayer = new MediaPlayer(new Media(SessionPartEntrainment.getRampfile().getFile().toURI().toString()));
                entrainmentplayer.setOnError(this::entrainmenterror);
                entrainmentplayer.setVolume(currententrainmentvolume);
                entrainmentplayer.play();
                entrainmentplayer.setOnPlaying(this::volume_bindentrainment);
            }));
            timeline_start_ending_ramp.play();
        }
        if (fade_entrainment_stop != null) {
            Duration startfadeout = new Duration(selectedPlaybackItem.getDuration());
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
//            if (referencecurrentlyDisplayed()) {
//                displayReference.EntrainmentVolumeSlider.setValue(currententrainmentvolume);
//                displayReference.EntrainmentVolumePercentage.setText(percentage);
//            }
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
                ambienceplayer.setVolume(currentambiencevolume);
                String percentage = new Double(currentambiencevolume * 100).intValue() + "%";
                AmbienceVolumePercentage.setText(percentage);
//                if (referencecurrentlyDisplayed()) {
//                    displayReference.AmbienceVolumeSlider.setValue(currentambiencevolume);
//                    displayReference.AmbienceVolumePercentage.setText(percentage);
//                }
                volume_bindambience();
            }
        }
        toggleplayerbuttons();
        getScene().addEventHandler(KeyEvent.KEY_PRESSED, AmbienceSwitchWithKeyboard);
        sessionItemsWithGoalsCompletedThisPlayback = new ArrayList<>();
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
            if (fade_ambience_pause.getStatus() == Animation.Status.RUNNING) {return;}
            // Open Loading Dialog
            playerState = FADING_PAUSE;
            fade_entrainment_pause.play();
            if (selectedPlaybackItem.getAmbience().isEnabled()) {
                volume_unbindambience();
                fade_ambience_pause.play();
            }
            // Close Loading Dialog
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
            entrainmentplayer = new MediaPlayer(new Media(SessionPartEntrainment.getFreq().getFile().toURI().toString()));
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
            currentambiencesoundfile = SessionPartAmbience.getnextambienceforplayback();
            ambienceplayer = new MediaPlayer(new Media(currentambiencesoundfile.getFile().toURI().toString()));
            ambienceplayer.setOnEndOfMedia(this::playnextambience);
            ambienceplayer.setOnError(this::ambienceerror);
            ambienceplayer.setVolume(currentambiencevolume);
            ambienceplayer.play();
            ambienceplayer.setOnPlaying(this::volume_bindambience);
        } catch (IndexOutOfBoundsException ignored) {ambienceplayer.dispose();}
    }
    private void playpreviousambiencefromhistory() {
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
    private void playnextambiencefromhistory() {
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
        closereferencefile();
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
                        if (! selectedPlaybackItem.getGoalsCompletedThisSession().isEmpty()) {
                            sessionItemsWithGoalsCompletedThisPlayback.add(selectedPlaybackItem);}
                        cleanupPlayersandAnimations();
                        sessionitemindex++;
                        selectedPlaybackItem = allPlaybackItems.get(sessionitemindex);
                        start();
                        displayreferencefile();
                    } catch (IndexOutOfBoundsException ignored) {
                        playerState = IDLE;
                        cleanupPlayersandAnimations();
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
        selectedPlaybackItem.updateduration(new Duration(selectedPlaybackItem.getDuration()));
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
        if (endofsession) {PlayButton.setText("Replay");}
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
        PlayButton.setText("Replay");
        playerState = STOPPED;
        Sessions.add(SessionInProgress);
        // TODO Prompt For Export
        updatesessionui();
        updategoalui();
        SessionInProgress = null;
        new SessionDetails(SessionInProgress).show();
        reset(true);
    }
    public boolean endsessionprematurely() {
        if (playerState == PLAYING || playerState == PAUSED || playerState == TRANSITIONING) {
            pausewithoutanimation();
            updateuitimeline.pause();
            if (new ConfirmationDialog(Preferences, "End Session Early", "Session Is Not Completed.", "End Session Prematurely?", "End Session", "Continue").getResult()) {
                Sessions.add(SessionInProgress);
                updatesessionui();
                updategoalui();
                reset(true);
                return true;
            }
            else {playbuttonpressed(); return false;}
        } else {return true;}
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
            if (fade_entrainment_play != null) {fade_entrainment_play.stop(); fade_ambience_play = null;}
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
//            System.out.println(name + "'s Entrainment Player Status: " + entrainmentplayer.getStatus());
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
        System.out.println("Entrainment Error");
        // Pause Ambience If Exists
        switch (new AnswerDialog(Preferences, this, "Entrainment Playback Error", null, "An Error Occured While Playing " + selectedPlaybackItem.getName() +
                "'s Entrainment. Problem File Is: '" + entrainmentplayer.getMedia().getSource() + "'",
                "Retry Playback", "Mute Entrainment", "Stop SessionInProgress Playback").getResult()) {
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
        // Pause Entrainment
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
//        boolean buttontoggled = ReferenceToggleCheckBox.isSelected();
//        Root.getPreferences().getSessionOptions().setReferenceoption(buttontoggled);
//        if (! buttontoggled) {
//            closereferencefile();
//            togglevolumebinding();
//        } else {
//        if (selectReferenceType != null && selectReferenceType.isShowing()) {return;}
//        else {selectReferenceType = new SelectReferenceType(Root, this, false, itemsinsession);}
//        switch (playerState) {
//            case IDLE:
//                selectReferenceType.showAndWait();
//                Root.getPreferences().getSessionOptions().setReferenceoption(selectReferenceType.getResult());
//                if (selectReferenceType.getResult()) {
//                    Root.getPreferences().getSessionOptions().setReferencetype(selectReferenceType.getReferenceType());
//                    Root.getPreferences().getSessionOptions().setReferencefullscreen(selectReferenceType.getFullScreen());
//                } else {
//                    ReferenceToggleCheckBox.setSelected(false);}
//                break;
//            case PLAYING:
//                selectReferenceType.showAndWait();
//                Root.getPreferences().getSessionOptions().setReferenceoption(selectReferenceType.getResult());
//                if (selectReferenceType.getResult()) {
//                    Root.getPreferences().getSessionOptions().setReferencetype(selectReferenceType.getReferenceType());
//                    Root.getPreferences().getSessionOptions().setReferencefullscreen(selectReferenceType.getFullScreen());
//                    displayreferencefile();
//                    togglevolumebinding();
//                }
//                break;
//            case PAUSED:
//            case STOPPED:
//            default:
//                ReferenceToggleCheckBox.setSelected(false);
//                break;
//        }
    }
    public void displayreferencefile() {
//        boolean notalreadyshowing = displayReference == null || ! displayReference.isShowing();
//        boolean referenceenabledwithvalidtype = Root.getPreferences().getSessionOptions().getReferenceoption() &&
//                (Root.getPreferences().getSessionOptions().getReferencetype() == ReferenceType.html || Root.getPreferences().getSessionOptions().getReferencetype() == ReferenceType.txt);
//        if (notalreadyshowing && referenceenabledwithvalidtype) {
//            displayReference = new DisplayReference(Root, this, false, currentsessionpart, itemsinsession.indexOf(currentsessionpart) == 0);
//            displayReference.show();
//            displayReference.setOnHidden(event -> {
//                currentsessionpart.volume_rebindentrainment();
//                if (ambienceenabled) {currentsessionpart.volume_rebindambience();}
//            });
//        }
    }
    public void closereferencefile() {
//        if (referencecurrentlyDisplayed()) {
//            displayReference.close();
//        }
    }
    public boolean referencecurrentlyDisplayed() {
        return true;
//        return displayReference != null && displayReference.isShowing() && displayReference.EntrainmentVolumeSlider != null;
    }

    public void closedialog() {}
}
