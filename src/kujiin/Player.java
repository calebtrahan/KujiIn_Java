package kujiin;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.util.lib.TimeUtils;
import kujiin.util.states.PlayerState;

import java.util.ArrayList;
import java.util.Optional;

public class Player {
//    ArrayList<Media> entrainmentparts = new ArrayList<>();
//    ArrayList<Media> ambienceparts = new ArrayList<>();
    ArrayList<Cut> cutstoplay;
    Cut currentcut;
    Boolean ambienceenabled;
    Root root;
    PlayerState playerState;
    private Timeline currentcuttimeline;
    private int totalsecondselapsed;
    private int cutcount;
    private int totalsecondsinsession;

    public Player(ArrayList<Cut> cutstoplay, Boolean ambienceenabled, Root root) {
        this.cutstoplay = cutstoplay;
        this.root = root;
        this.ambienceenabled = ambienceenabled;
        playerState = PlayerState.NONE;
    }

// Player Controls
    public void play() {
        System.out.println("Called The Play Method");
        totalsecondselapsed = 0;
        totalsecondsinsession = 0;
        for (Cut i : cutstoplay) {totalsecondsinsession += i.getdurationinseconds();}
        root.TotalProgressLabelTotal.setText(TimeUtils.formatlengthshort(totalsecondsinsession));
        currentcuttimeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> updateplayerui()));
        currentcuttimeline.setCycleCount(Animation.INDEFINITE);
        currentcuttimeline.play();
        cutcount = 0;
        currentcut = cutstoplay.get(cutcount);
        playthiscut();
    }
    public void playbuttonpressed() {
        System.out.println("Play Button Pressed");
        try {
            if (playerState == PlayerState.PAUSED) {
                currentcut.resumeplayingcut();
                currentcuttimeline.play();
                playerState = PlayerState.PLAYING;
            } else if (playerState == PlayerState.NONE || playerState == PlayerState.STOPPED) {
                play();
            } else if (playerState == PlayerState.PLAYING || playerState == PlayerState.TRANSITIONING) {
                root.StatusBar.setText("Session Already Playing");
            }
        } catch (ArrayIndexOutOfBoundsException e) {endofsession();}
    }
    public void pause() {
        if (playerState == PlayerState.PLAYING) {
            currentcut.pauseplayingcut();
            currentcuttimeline.pause();
            playerState = PlayerState.PAUSED;
        } else if (playerState == PlayerState.PAUSED) {
            root.StatusBar.setText("Session Is Already Paused");
        } else if (playerState == PlayerState.TRANSITIONING) {
            root.StatusBar.setText("Currently Transitioning To The Next Cut. Please Wait Till At The Next Cut To Pause");
        } else if (playerState == PlayerState.NONE || playerState == PlayerState.STOPPED) {
            root.StatusBar.setText("No Session Playing");
        }
    }
    public void stop() {
        if (playerState == PlayerState.PLAYING || playerState == PlayerState.PAUSED) {
            if (playerState == PlayerState.PLAYING) {pause();}
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("End Prematurely");
            a.setContentText("Really End This_Session Before It's Finished?");
            Optional<ButtonType> b = a.showAndWait();
            if (b.isPresent() && b.get() == ButtonType.OK) {
                // TODO Get Premature Ending Reason (If You Decide To Include It) Here
                // TODO Stop Session Here
                playerState = PlayerState.STOPPED;
            }
        } else if (playerState == PlayerState.NONE || playerState == PlayerState.STOPPED) {
            root.StatusBar.setText("No This_Session Playing");
        } else if (playerState == PlayerState.TRANSITIONING) {
            root.StatusBar.setText("Currently Transitioning To The Next Cut. Please Wait Till At The Next Cut To Stop");
        }
    }
    public void playthiscut() {
        Duration cutduration = currentcut.getthiscutduration();
        currentcut.startplayback();
        Timeline timeline = new Timeline(new KeyFrame(cutduration, ae -> progresstonextcut()));
        timeline.play();
        playerState = PlayerState.PLAYING;
    }
    public void progresstonextcut() {
        if (playerState == PlayerState.TRANSITIONING) {
            try {
                cutcount++;
                currentcut = cutstoplay.get(cutcount);
                playthiscut();
            } catch (ArrayIndexOutOfBoundsException e) {endofsession();}
        } else if (playerState == PlayerState.PLAYING) {
            currentcut.stopplayingcut();
            Media alertmedia = new Media(This_Session.alertfile.toURI().toString());
            MediaPlayer alertplayer = new MediaPlayer(alertmedia);
            alertplayer.play();
            playerState = PlayerState.TRANSITIONING;
            alertplayer.setOnEndOfMedia(() -> {alertplayer.stop(); alertplayer.dispose(); progresstonextcut();});
        }
    }
    public void updateplayerui() {
        if (playerState == PlayerState.PLAYING) {
            totalsecondselapsed++;
            root.TotalProgressLabelCurrent.setText(TimeUtils.formatlengthshort(totalsecondselapsed));
            root.CutProgressLabelCurrent.setText(currentcut.getcurrenttimeformatted());
            root.CutProgressLabelTotal.setText(currentcut.gettotaltimeformatted());
            root.TotalSessionLabel.setText("Total Progress");
            if (currentcut.getSecondselapsed() != 0) {
                root.CutProgressBar.setProgress((float) currentcut.getSecondselapsed() / (float) currentcut.getdurationinseconds());
            }
            if (totalsecondselapsed != 0) {
                root.TotalProgressBar.setProgress((float) totalsecondselapsed / (float) totalsecondsinsession);
            }
            root.CutProgressTopLabel.setText(String.format("%s Progress", currentcut.name));
            root.StatusBar.setText("Now Playing: " + currentcut.name);
        } else if (playerState == PlayerState.TRANSITIONING) {
            root.CutProgressBar.setProgress(1.0);
            root.StatusBar.setText("Prepare For " + cutstoplay.get(currentcut.number + 1).name);
            root.CutProgressLabelCurrent.setText(currentcut.gettotaltimeformatted());
            root.CutProgressLabelTotal.setText(currentcut.gettotaltimeformatted());
        } else if (playerState == PlayerState.PAUSED) {

        } else if (playerState == PlayerState.STOPPED) {

        }
        // TODO Update Goals Widget Here While Session Is Playing
//        Tools.formatcurrentcutprogress(currentcut, (int) entrainmentplayer.getCurrentTime().toSeconds(),
//                root.CutProgressLabelCurrent, root.CutProgressBar, root.CutProgressLabelTotal);
//        Tools.formattotalprogress(totalseconds, gettotalduration(), root.TotalProgressLabelCurrent,
//                root.TotalProgressBar, root.TotalProgressLabelTotal);
//        root.goalscurrrentvalueLabel.setText(database.getcurrenthoursformatted());
//        root.goalsprogressbar.setProgress(database.gettotalpracticedhours() / database.getcurrentgoalhours());
//        root.goalssettimeLabel.setText(database.getgoalhoursformatted());
    }
    public void endofsession() {
        System.out.println("End Of This_Session!");
    }

// Getters And Setters
    public int gettotalduration() {
        int totalduration = 0;
        for (Cut i : cutstoplay) {
            totalduration += i.getdurationinseconds();
        }
        return totalduration;
    }

}
