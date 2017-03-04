package kujiin.util;

import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.ui.MainController;
import kujiin.xml.*;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class AudioChecks extends Task {
    private Session.PlaybackItem selectedplaybackitem;
    private PlaybackItemEntrainment playbackItemEntrainment;
    private PlaybackItemAmbience playbackItemAmbience;
    private RampFiles rampfiles;
    private MediaPlayer startupcheckplayer;
    private ArrayList<Session.PlaybackItem> partswithnoambience = new ArrayList<>();
    private ArrayList<Session.PlaybackItem> partswithmissingentrainment = new ArrayList<>();
    private MainController root;
    private BlockingQueue<Session.PlaybackItem> playbackitemstocheck = new ArrayBlockingQueue<>(15);
    private BlockingQueue<SoundFile> PlaybackItemEntrainmentFiles;
    private BlockingQueue<SoundFile> PlaybackItemAmbienceFiles;
    private BlockingQueue<SoundFile> SessionRampFiles;

    public AudioChecks(MainController Root) {
        root = Root;
        for (int i = 0; i < 15; i++) {
            Session testSession = new Session();
            testSession.addplaybackitem(i);
            playbackitemstocheck.add(testSession.getPlaybackItems().get(i));
            populatefilestocheckforSelectedPlaybackItem();
            populateRampFilesToCheck();
        }
    }

// Getters And Setters
    public ArrayList<Session.PlaybackItem> getPartswithmissingentrainment() {
        return partswithmissingentrainment;
    }

// Method Overrides
    @Override
    protected Object call() throws Exception {
    // Get Sound File
        SoundFile soundFile;
        AudioCheckingType audioCheckingType;
        if (! playbackitemstocheck.isEmpty()) {
//            if (playbackitemstocheck.size() == 15) {CreatorStatusBar.textProperty().bind(messageProperty()); calculatetotalworktodo();}
            if (selectedplaybackitem == null) {selectedplaybackitem = playbackitemstocheck.take(); populatefilestocheckforSelectedPlaybackItem();}
            if (! PlaybackItemEntrainmentFiles.isEmpty()) {
                audioCheckingType = AudioCheckingType.Entrainment; soundFile = PlaybackItemEntrainmentFiles.take();}
            else if (! PlaybackItemAmbienceFiles.isEmpty()) {
                audioCheckingType = AudioCheckingType.Ambience; soundFile = PlaybackItemAmbienceFiles.take();}
            else {savechangestoXML(); selectedplaybackitem = null; return call();}
        } else {
            if (! SessionRampFiles.isEmpty()) {
                audioCheckingType = AudioCheckingType.Ramp;
                soundFile = SessionRampFiles.take();
            } else {
                root.getAvailableEntrainments().setRampFiles(rampfiles);
                // End Of Audio Checks
                return null;
            }
        }
    // Check If File Exists
        System.out.println("Grabbed " + soundFile.getFile().getAbsolutePath() + " As Sound File To Check");
        if (! soundFile.getFile().exists()) {
            if (audioCheckingType == AudioCheckingType.Ambience) {playbackItemAmbience.remove(playbackItemAmbience.getAmbience().indexOf(soundFile));}
            return call();
        }
    // Check And Calculate Duration If Needed
        if (! soundFile.isValid()) {
            startupcheckplayer = new MediaPlayer(new Media(soundFile.getFile().toURI().toString()));
            startupcheckplayer.setOnReady(() -> {
                if (startupcheckplayer.getTotalDuration().greaterThan(Duration.ZERO)) {
                    soundFile.setDuration(startupcheckplayer.getTotalDuration().toMillis());
                    startupcheckplayer.dispose();
                    startupcheckplayer = null;
//                    updateProgress(workcount[0], workcount[1]);
//                    updateMessage("Performing Startup Checks. Please Wait (" + new Double(getProgress() * 100).intValue() + "%)");
                    try {call();} catch (Exception ignored) {ignored.printStackTrace();}
                } else {
                    startupcheckplayer.dispose();
                    startupcheckplayer = null;
                    try {call();} catch (Exception ignored) {ignored.printStackTrace();}
                }
            });
        } else {
//            updateProgress(workcount[0], workcount[1]);
//            updateMessage("Performing Startup Checks. Please Wait (" + new Double(getProgress() * 100).intValue() + "%)");
            try {call();} catch (Exception ignored) {ignored.printStackTrace();}}
        return null;
    }


// Utility Methods
    private void savechangestoXML() {
        root.getAvailableEntrainments().setsessionpartEntrainment(selectedplaybackitem, playbackItemEntrainment);
        root.getAvailableAmbiences().setsessionpartAmbience(selectedplaybackitem.getEntrainmentandavailableambienceindex(), playbackItemAmbience);
    }
    private void populatefilestocheckforSelectedPlaybackItem() {
        playbackItemEntrainment = root.getAvailableEntrainments().getsessionpartEntrainment(selectedplaybackitem);
        playbackItemAmbience = root.getAvailableAmbiences().getsessionpartAmbience(selectedplaybackitem.getEntrainmentandavailableambienceindex());
        PlaybackItemAmbienceFiles = null;
        PlaybackItemEntrainmentFiles = null;
        PlaybackItemEntrainmentFiles = new ArrayBlockingQueue<>(playbackItemEntrainment.getAllEntrainmentFiles().size());
        PlaybackItemEntrainmentFiles.addAll(playbackItemEntrainment.getAllEntrainmentFiles());
        PlaybackItemAmbienceFiles = new ArrayBlockingQueue<>(playbackItemAmbience.getAmbience().size());
        PlaybackItemAmbienceFiles.addAll(playbackItemAmbience.getAmbience());

    }
    private void populateRampFilesToCheck() {
        rampfiles = root.getAvailableEntrainments().getRampFiles();
        SessionRampFiles = new ArrayBlockingQueue<>(root.getAvailableEntrainments().getRampFiles().getRampFiles().size());
    }

}

enum AudioCheckingType {
    Ambience, Entrainment, Ramp
}