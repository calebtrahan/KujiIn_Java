package kujiin;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class Player {
//    ArrayList<Media> entrainmentparts = new ArrayList<>();
//    ArrayList<Media> ambienceparts = new ArrayList<>();
    Integer partindex; // This Will Keep Track Of Which Cut (It's Index) To Grab Below (No Generators In Java!)
    ArrayList<Cut> cutstoplay;
    Cut currentcut;
    Boolean ambienceenabled;
    File entrainmentfile;
    File ambiencefile;
    Media entrainmentmedia;
    Media ambiencemedia;
    Media alertmedia;
    MediaPlayer entrainmentplayer;
    MediaPlayer ambienceplayer;
    MediaPlayer alertplayer;
    Label PlayercurrentlyplayingLabel;
    Label PlayercurrentcutprogressLabel;
    Label PlayertotalsessionprogressLabel;
    Label PlayercurrentprogressLabelwithprefix;
    Integer totalseconds;
    Root root;
    PlayerStatus playerStatus = PlayerStatus.NONE;
    Database database;

    public Player(ArrayList<Cut> cutstoplay, Boolean ambienceenabled, Root root, Database database) {
        this.cutstoplay = new ArrayList<>();
        this.root = root;
        for (Cut i : cutstoplay) {
            this.cutstoplay.add(i);
            if (! i.name.equals("Postsession")) {
                this.cutstoplay.add(new Cut("Alert"));
            }
        }
        this.ambienceenabled = ambienceenabled;
//        PlayercurrentlyplayingLabel = root.PlayercurrentlyplayingLabel;
//        PlayercurrentcutprogressLabel = root.PlayercurrentcutprogressLabel;
//        PlayertotalsessionprogressLabel = root.PlayertotalsessionprogressLabel;
//        PlayercurrentprogressLabelwithprefix = root.PlayercurrentlyplayingLabelWithProgressPrefix;
        partindex = 0;
        totalseconds = 0;
        this.database = database;
    }

    public boolean isPaused() {
        try {return entrainmentplayer.getStatus() == MediaPlayer.Status.PAUSED;}
        catch (NullPointerException e) {return false;}
    }

    public boolean isPlaying() {
        try {return entrainmentplayer.getStatus() == MediaPlayer.Status.PLAYING;}
        catch (NullPointerException e) {return false;}
    }

    public void play() {
        System.out.println("Called Play Method");
        currentcut = cutstoplay.get(partindex);
        if (!currentcut.name.equals("Alert")) {
            entrainmentfile = new File(This_Session.directorytemp, "Entrainment/" + currentcut.name + ".mp3");
            entrainmentmedia = new Media(entrainmentfile.toURI().toString());
            if (ambienceenabled) {
                ambiencefile = new File(This_Session.directorytemp, "Ambience/" + currentcut.name + ".mp3");
                ambiencemedia = new Media(ambiencefile.toURI().toString());
            }
            entrainmentplayer = new MediaPlayer(entrainmentmedia);
            entrainmentplayer.play();
            if (ambienceenabled) {
                ambienceplayer = new MediaPlayer(ambiencemedia);
                ambienceplayer.play();
            }
            partindex++;
            entrainmentplayer.setOnEndOfMedia(this::play);
            entrainmentplayer.currentTimeProperty().addListener(observable -> {
                updateplayerui();
            });
            entrainmentplayer.setVolume(0.6);
            root.EntrainmentVolumeSlider.valueProperty().bindBidirectional(entrainmentplayer.volumeProperty());
            if (ambienceenabled) {
                root.AmbienceVolumeSlider.valueProperty().bindBidirectional(ambienceplayer.volumeProperty());
            }
            playerStatus = PlayerStatus.PLAYING;
        } else {
            alertmedia = new Media(This_Session.alertfile.toURI().toString());
            alertplayer = new MediaPlayer(alertmedia);
            alertplayer.play();
            alertplayer.setOnEndOfMedia(() -> {
                alertplayer.dispose();
                play();
            });
            partindex++;
            PlayercurrentlyplayingLabel.setText("Prepare For " + cutstoplay.get(partindex).name);
            playerStatus = PlayerStatus.TRANSITIONING;
        }
    }

    // TODO Continue Here Make It So Only One Version Of The This_Session Can Be Playing At A Time. And Pause/Stop/Resume Works
    public void playbuttonpressed() {
        System.out.println("Play Button Pressed");
        try {
            if (playerStatus == PlayerStatus.PAUSED) {
                entrainmentplayer.play();
                if (ambienceenabled) {ambienceplayer.play();}
                playerStatus = PlayerStatus.PLAYING;
            } else if (playerStatus == PlayerStatus.NONE || playerStatus == PlayerStatus.STOPPED) {
                // TODO Unbind root.StatusBar's Auto Off Delay Here
                play();
            } else if (playerStatus == PlayerStatus.PLAYING || playerStatus == PlayerStatus.TRANSITIONING) {
                root.StatusBar.setText("This_Session Already Playing");
            }
        } catch (ArrayIndexOutOfBoundsException e) {endofsession();}
    }

    public int gettotalduration() {
        int totalduration = 0;
        for (Cut i : cutstoplay) {
            totalduration += i.getdurationinseconds();
        }
        return totalduration;
    }

    public void pause() {
        if (playerStatus == PlayerStatus.PLAYING) {
            if (ambienceenabled) {ambienceplayer.pause();}
            entrainmentplayer.pause();
            playerStatus = PlayerStatus.PAUSED;
        } else if (playerStatus == PlayerStatus.PAUSED) {
            root.StatusBar.setText("This_Session Already Paused");
        } else if (playerStatus == PlayerStatus.TRANSITIONING) {
            root.StatusBar.setText("Currently Transitioning To The Next Cut. Please Wait Till At The Next Cut To Pause");
        } else if (playerStatus == PlayerStatus.NONE || playerStatus == PlayerStatus.STOPPED) {
            root.StatusBar.setText("No This_Session Playing");
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
                entrainmentplayer.stop();
                entrainmentplayer.dispose();
                if (ambienceenabled)
                    ambienceplayer.stop();
                    ambienceplayer.dispose();
                playerStatus = PlayerStatus.STOPPED;
            } else {play();}
        } else if (playerStatus == PlayerStatus.NONE || playerStatus == PlayerStatus.STOPPED) {
            root.StatusBar.setText("No This_Session Playing");
        } else if (playerStatus == PlayerStatus.TRANSITIONING) {
            root.StatusBar.setText("Currently Transitioning To The Next Cut. Please Wait Till At The Next Cut To Stop");
        }
    }

    public void updateplayerui() {
        root.CutProgressTopLabel.setText(String.format("%s Progress", currentcut.name));
        Tools.formatcurrentcutprogress(currentcut, (int) entrainmentplayer.getCurrentTime().toSeconds(),
                root.CutProgressLabelCurrent, root.CutProgressBar, root.CutProgressLabelTotal);
        Tools.formattotalprogress(totalseconds, gettotalduration(), root.TotalProgressLabelCurrent,
                root.TotalProgressBar, root.TotalProgressLabelTotal);
        root.goalscurrrentvalueLabel.setText(database.getcurrenthoursformatted());
        root.goalsprogressbar.setProgress(database.gettotalpracticedhours() / database.getcurrentgoalhours());
        root.goalssettimeLabel.setText(database.getgoalhoursformatted());
        root.StatusBar.setText("Playing " + currentcut.name);
        totalseconds ++;
    }

    public void endofsession() {
        System.out.println("End Of This_Session!");
    }
}
