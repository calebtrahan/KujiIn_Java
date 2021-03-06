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

// TODO Not Iterating Into Rampfiles If Ambience Does Not Exist

public class AudioChecks extends Task {
    private PlaybackItem selectedplaybackitem;
    private PlaybackItemEntrainment playbackItemEntrainment;
    private PlaybackItemAmbience playbackItemAmbience;
    private MediaPlayer startupcheckplayer;
    private ArrayList<PlaybackItem> partswithnoambience = new ArrayList<>();
    private ArrayList<PlaybackItem> partswithmissingentrainment = new ArrayList<>();
    private ArrayList<PlaybackItem> partswithnoentrainment = new ArrayList<>();
    private MainController root;
    private BlockingQueue<PlaybackItem> playbackitemstocheck = new ArrayBlockingQueue<>(15);
    private BlockingQueue<SoundFile> PlaybackItemEntrainmentFiles;
    private BlockingQueue<SoundFile> PlaybackItemAmbienceFiles;
    private BlockingQueue<SoundFile> TempSessionRampFiles;
    private BlockingQueue<SoundFile> SessionRampFiles;
    private int totaltasks = 0;
    private int currenttaskcount = 0;
    private int selectedplaybackitemindex = 0;
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
        populaterampfilestocheck();
        calculateworktodo();
    }

// Getters And Setters
    public ArrayList<PlaybackItem> getPartswithmissingentrainment() {
        return partswithmissingentrainment;
    }
    public ArrayList<PlaybackItem> getPartswithnoambience() {
        return partswithnoambience;
    }

    // Method Overrides
    @Override
    protected Object call() throws Exception {
        // Get Sound File
        try {
            SoundFile soundFile;
            AudioCheckingType audioCheckingType;
            if (! playbackitemstocheck.isEmpty() || selectedplaybackitemindex == 14) {
                if (selectedplaybackitem == null) {
                    selectedplaybackitem = playbackitemstocheck.take();
                    selectedplaybackitemindex++;
                    populatefilestocheckforSelectedPlaybackItem();
                }
                if (! PlaybackItemEntrainmentFiles.isEmpty()) {
                    audioCheckingType = AudioCheckingType.Entrainment;
                    soundFile = PlaybackItemEntrainmentFiles.take();
                }
                else if (PlaybackItemAmbienceFiles != null && ! PlaybackItemAmbienceFiles.isEmpty()) {
                    System.out.println("Entered Ambience");
                    audioCheckingType = AudioCheckingType.Ambience;
                    soundFile = PlaybackItemAmbienceFiles.take();
                    if (selectedplaybackitemindex == 14 && PlaybackItemAmbienceFiles.isEmpty()) {selectedplaybackitemindex++;}
                } else {
                    saveplaybackitemchangestoXML(); selectedplaybackitem = null; return call();
                }
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
            System.out.println("Checking " + soundFile.getName());
            if (! soundFile.getFile().exists()) {
                System.out.println(soundFile.getName() + " Does Not Exist!!!");
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
                            if (audioCheckingType == AudioCheckingType.Ramp) {SessionRampFiles.put(soundFile);}
                            startupcheckplayer.dispose();
                            startupcheckplayer = null;
                            updateProgress(currenttaskcount, totaltasks);
                            updateMessage("Checking Audio Files Please Wait");
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
                            try {call();} catch (Exception e) {e.printStackTrace();}
                        }
                    } catch (InterruptedException e) {e.printStackTrace();}
                });
            } else {
                if (audioCheckingType == AudioCheckingType.Ramp) {SessionRampFiles.put(soundFile);}
                currenttaskcount++;
                updateProgress(currenttaskcount, totaltasks);
                updateMessage("Please Wait (" + new Double(getProgress() * 100).intValue() + "%)");
                try {call();} catch (Exception ignored) {ignored.printStackTrace();}
            }
        } catch (Exception e) {e.printStackTrace();}
        return null;
    }


// Utility Methods
    private void saveplaybackitemchangestoXML() {
        root.getAvailableEntrainments().setsessionpartEntrainment(selectedplaybackitem, playbackItemEntrainment);
        root.getAvailableAmbiences().setsessionpartAmbience(selectedplaybackitem.getCreationindex(), playbackItemAmbience);
    }
    private void populaterampfilestocheck() {
        TempSessionRampFiles = new ArrayBlockingQueue<>(root.getRampFiles().getRampFiles().size());
        SessionRampFiles = new ArrayBlockingQueue<>(root.getRampFiles().getRampFiles().size());
        TempSessionRampFiles.addAll(root.getRampFiles().getRampFiles());
    }
    private void populatefilestocheckforSelectedPlaybackItem() {
        playbackItemEntrainment = root.getAvailableEntrainments().getsessionpartEntrainment(selectedplaybackitem);
        PlaybackItemEntrainmentFiles = null;
        try {PlaybackItemEntrainmentFiles = new ArrayBlockingQueue<>(playbackItemEntrainment.getAllEntrainmentFiles().size()); PlaybackItemEntrainmentFiles.addAll(playbackItemEntrainment.getAllEntrainmentFiles());}
        catch (NullPointerException e) {e.printStackTrace(); partswithnoentrainment.add(selectedplaybackitem);}
        playbackItemAmbience = root.getAvailableAmbiences().getsessionpartAmbience(selectedplaybackitem.getCreationindex());
        if (playbackItemAmbience.getAmbience() != null) {
            PlaybackItemAmbienceFiles = null;
            try {PlaybackItemAmbienceFiles = new ArrayBlockingQueue<>(playbackItemAmbience.getAmbience().size()); PlaybackItemAmbienceFiles.addAll(playbackItemAmbience.getAmbience());}
            catch (NullPointerException e) {e.printStackTrace(); partswithnoambience.add(selectedplaybackitem);}
        }
    }
    private void calculateworktodo() {
        for (PlaybackItem i : testsession.getPlaybackItems()) {
            totaltasks += root.getAvailableEntrainments().getsessionpartEntrainment(i).getAllEntrainmentFiles().size();
            try {totaltasks += root.getAvailableAmbiences().getsessionpartAmbience(i.getCreationindex()).getAmbience().size();}
            catch (NullPointerException ignored) {}
        }
        totaltasks += root.getRampFiles().getRampFiles().size();
    }
}

enum AudioCheckingType {
    Ambience, Entrainment, Ramp
}