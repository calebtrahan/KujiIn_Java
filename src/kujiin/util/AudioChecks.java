package kujiin.util;

import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.ui.MainController;
import kujiin.xml.*;

import java.util.ArrayList;
import java.util.List;
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
    private BlockingQueue<SoundFile> TempSessionRampFiles;
    private BlockingQueue<SoundFile> SessionRampFiles;
    private int totaltasks = 0;
    private int currenttaskcount = 0;
    private final Session testsession;

    public AudioChecks(MainController Root) {
        root = Root;
        testsession = new Session();
        for (int i = 0; i < 15; i++) {
            testsession.addplaybackitem(i);
            playbackitemstocheck.add(testsession.getPlaybackItems().get(i));
        }
        try {selectedplaybackitem = playbackitemstocheck.take();} catch (InterruptedException e) {e.printStackTrace();}
        populatefilestocheckforSelectedPlaybackItem();
        rampfiles = root.getRampFiles();
        TempSessionRampFiles = new ArrayBlockingQueue<>(root.getRampFiles().getRampFiles().size());
        SessionRampFiles = new ArrayBlockingQueue<>(root.getRampFiles().getRampFiles().size());
        TempSessionRampFiles.addAll(root.getRampFiles().getRampFiles());
        calculateworktodo();
    }

// Getters And Setters
    public ArrayList<Session.PlaybackItem> getPartswithmissingentrainment() {
        return partswithmissingentrainment;
    }

// Method Overrides
    @Override
    protected Object call() throws Exception {
    // Get Sound File
        try {
            SoundFile soundFile;
            AudioCheckingType audioCheckingType;
            if (! playbackitemstocheck.isEmpty()) {
                if (selectedplaybackitem == null) {selectedplaybackitem = playbackitemstocheck.take(); populatefilestocheckforSelectedPlaybackItem();}
                if (! PlaybackItemEntrainmentFiles.isEmpty()) {audioCheckingType = AudioCheckingType.Entrainment; soundFile = PlaybackItemEntrainmentFiles.take();}
                else if (PlaybackItemAmbienceFiles != null && ! PlaybackItemAmbienceFiles.isEmpty()) {audioCheckingType = AudioCheckingType.Ambience; soundFile = PlaybackItemAmbienceFiles.take();}
                else {savechangestoXML(); selectedplaybackitem = null; return call();}
            } else if (! TempSessionRampFiles.isEmpty()) {
                audioCheckingType = AudioCheckingType.Ramp;
                soundFile = TempSessionRampFiles.take();
            } else {
                List<SoundFile> rampfiles = new ArrayList<>();
                SessionRampFiles.drainTo(rampfiles);
                root.getRampFiles().setRampFiles(rampfiles);
                // End Of Audio Checks
                return null;
            }
        // Check If File Exists
            if (! soundFile.getFile().exists()) {
                if (audioCheckingType == AudioCheckingType.Ambience) {playbackItemAmbience.remove(playbackItemAmbience.getAmbience().indexOf(soundFile));}
                return call();
            }
        // Check And Calculate Duration If Needed
            if (! soundFile.isValid()) {
                startupcheckplayer = new MediaPlayer(new Media(soundFile.getFile().toURI().toString()));
                startupcheckplayer.setOnReady(() -> {
                    try {
                        if (startupcheckplayer.getTotalDuration().greaterThan(Duration.ZERO)) {
                            currenttaskcount++;
                            soundFile.setDuration(startupcheckplayer.getTotalDuration().toMillis());
                            startupcheckplayer.dispose();
                            startupcheckplayer = null;
                            updateProgress(currenttaskcount, totaltasks);
                            updateMessage("Please Wait (" + new Double(getProgress() * 100).intValue() + "% ) [" + currenttaskcount + "/" + totaltasks + "]");
                            try {call();} catch (Exception ignored) {ignored.printStackTrace();}
                        } else {
                            switch (audioCheckingType) {
                                case Entrainment:
                                    PlaybackItemEntrainmentFiles.put(soundFile);
                                    break;
                                case Ambience:
                                    PlaybackItemAmbienceFiles.put(soundFile);
                                    break;
                                case Ramp:
                                    SessionRampFiles.put(soundFile);
                                    break;
                            }
                            startupcheckplayer.dispose();
                            startupcheckplayer = null;
                            try {call();} catch (Exception ignored) {ignored.printStackTrace();}
                        }
                    } catch (InterruptedException e) {e.printStackTrace();}
                });
            } else {
                currenttaskcount++;
                updateProgress(currenttaskcount, totaltasks);
                updateMessage("Please Wait (" + new Double(getProgress() * 100).intValue() + "% [" + currenttaskcount + "/" + totaltasks + "]");
                try {call();} catch (Exception ignored) {ignored.printStackTrace();}
            }
        } catch (Exception ignored) {
            System.out.println("Exception Encountered!");
            ignored.printStackTrace();
        }
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
        try {
            PlaybackItemAmbienceFiles = new ArrayBlockingQueue<>(playbackItemAmbience.getAmbience().size());
            PlaybackItemAmbienceFiles.addAll(playbackItemAmbience.getAmbience());
        } catch (Exception ignored) {}
    }
    private void calculateworktodo() {
        for (Session.PlaybackItem i : testsession.getPlaybackItems()) {
            totaltasks += root.getAvailableEntrainments().getsessionpartEntrainment(i).getAllEntrainmentFiles().size();
            try {totaltasks += root.getAvailableAmbiences().getsessionpartAmbience(i.getEntrainmentandavailableambienceindex()).getAmbience().size();}
            catch (NullPointerException ignored) {}
        }
        totaltasks += root.getRampFiles().getRampFiles().size();
    }

}

enum AudioCheckingType {
    Ambience, Entrainment, Ramp
}