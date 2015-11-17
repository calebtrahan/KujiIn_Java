package kujiin;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.util.TimeUtils;

import java.util.ArrayList;
import java.util.Optional;

public class Player {
//    ArrayList<Media> entrainmentparts = new ArrayList<>();
//    ArrayList<Media> ambienceparts = new ArrayList<>();
    ArrayList<Cut> cutstoplay;
    Cut currentcut;
    Boolean ambienceenabled;
    Root root;
    PlayerStatus playerStatus;
    Database database;
    private Timeline currentcuttimeline;
    private int totalsecondselapsed;
    private int cutcount;
    private int totalsecondsinsession;

    public Player(ArrayList<Cut> cutstoplay, Boolean ambienceenabled, Root root) {
        this.cutstoplay = cutstoplay;
        this.root = root;
        this.ambienceenabled = ambienceenabled;
        playerStatus = PlayerStatus.NONE;
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
            if (playerStatus == PlayerStatus.PAUSED) {
                currentcut.resumeplayingcut();
                currentcuttimeline.play();
                playerStatus = PlayerStatus.PLAYING;
            } else if (playerStatus == PlayerStatus.NONE || playerStatus == PlayerStatus.STOPPED) {
                play();
            } else if (playerStatus == PlayerStatus.PLAYING || playerStatus == PlayerStatus.TRANSITIONING) {
                root.StatusBar.setText("Session Already Playing");
            }
        } catch (ArrayIndexOutOfBoundsException e) {endofsession();}
    }
    public void pause() {
        if (playerStatus == PlayerStatus.PLAYING) {
            currentcut.pauseplayingcut();
            currentcuttimeline.pause();
            playerStatus = PlayerStatus.PAUSED;
        } else if (playerStatus == PlayerStatus.PAUSED) {
            root.StatusBar.setText("Session Is Already Paused");
        } else if (playerStatus == PlayerStatus.TRANSITIONING) {
            root.StatusBar.setText("Currently Transitioning To The Next Cut. Please Wait Till At The Next Cut To Pause");
        } else if (playerStatus == PlayerStatus.NONE || playerStatus == PlayerStatus.STOPPED) {
            root.StatusBar.setText("No Session Playing");
        }
    }
    public void stop() {
        if (playerStatus == PlayerStatus.PLAYING || playerStatus == PlayerStatus.PAUSED) {
            if (playerStatus == PlayerStatus.PLAYING) {pause();}
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("End Prematurely");
            a.setContentText("Really End This_Session Before It's Finished?");
            Optional<ButtonType> b = a.showAndWait();
            if (b.isPresent() && b.get() == ButtonType.OK) {
                // TODO Get Premature Ending Reason (If You Decide To Include It) Here
                // TODO Stop Session Here
                playerStatus = PlayerStatus.STOPPED;
            }
        } else if (playerStatus == PlayerStatus.NONE || playerStatus == PlayerStatus.STOPPED) {
            root.StatusBar.setText("No This_Session Playing");
        } else if (playerStatus == PlayerStatus.TRANSITIONING) {
            root.StatusBar.setText("Currently Transitioning To The Next Cut. Please Wait Till At The Next Cut To Stop");
        }
    }
    public void playthiscut() {
        Duration cutduration = currentcut.getthiscutduration();
        currentcut.startplayback();
        Timeline timeline = new Timeline(new KeyFrame(cutduration, ae -> progresstonextcut()));
        timeline.play();
        playerStatus = PlayerStatus.PLAYING;
    }
    public void progresstonextcut() {
        if (playerStatus == PlayerStatus.TRANSITIONING) {
            try {
                cutcount++;
                currentcut = cutstoplay.get(cutcount);
                playthiscut();
            } catch (ArrayIndexOutOfBoundsException e) {endofsession();}
        } else if (playerStatus == PlayerStatus.PLAYING) {
            currentcut.stopplayingcut();
            Media alertmedia = new Media(This_Session.alertfile.toURI().toString());
            MediaPlayer alertplayer = new MediaPlayer(alertmedia);
            alertplayer.play();
            playerStatus = PlayerStatus.TRANSITIONING;
            alertplayer.setOnEndOfMedia(() -> {alertplayer.stop(); alertplayer.dispose(); progresstonextcut();});
        }
    }
    public void updateplayerui() {
        if (playerStatus == PlayerStatus.PLAYING) {
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
        } else if (playerStatus == PlayerStatus.TRANSITIONING) {
            root.CutProgressBar.setProgress(1.0);
            root.StatusBar.setText("Prepare For " + cutstoplay.get(currentcut.number + 1).name);
            root.CutProgressLabelCurrent.setText(currentcut.gettotaltimeformatted());
            root.CutProgressLabelTotal.setText(currentcut.gettotaltimeformatted());
        } else if (playerStatus == PlayerStatus.PAUSED) {

        } else if (playerStatus == PlayerStatus.STOPPED) {

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
