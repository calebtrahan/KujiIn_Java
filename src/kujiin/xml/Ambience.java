package kujiin.xml;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Ambience {
    private List<SoundFile> Ambience;

    public Ambience() {
    }

    // Getters And Setters
    public List<SoundFile> getAmbience() {return Ambience;}
    public void setAmbience(List<SoundFile> ambience) {
        this.Ambience = ambience;
    }
    public List<File> getAmbienceFiles() {
        List<File> files = new ArrayList<>();
        try {
            files.addAll(getAmbience().stream().map(SoundFile::getFile).collect(Collectors.toList()));
            return files;
        } catch (NullPointerException ignored) {return new ArrayList<>();}
    }

    // Ambience Editing Methods
    // Actual Ambience
    private boolean actual_addfromfile(File file) {
        if (Util.audio_isValid(file)) {
            SoundFile tempfile = new SoundFile(file);
            if (!ambienceexistsinActual(tempfile)) {
                actual_add(tempfile);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    private void actual_initialize() {
        if (Ambience == null) Ambience = new ArrayList<>();
    }
    public void actual_add(SoundFile soundFile) {
        actual_initialize();
        if (soundFile.getDuration() == null) {
            System.out.println("Trying To Calculate Duration For " + soundFile.getFile().getName());
            MediaPlayer calcdurationplayer = new MediaPlayer(new Media(soundFile.getFile().toURI().toString()));
            calcdurationplayer.setOnReady(() -> {
                soundFile.setDuration(calcdurationplayer.getTotalDuration().toMillis());
                Ambience.add(soundFile);
                calcdurationplayer.dispose();
            });
        } else {
            Ambience.add(soundFile);
        }
    }
    public void actual_add(int index, SoundFile soundFile) {
        actual_initialize();
        if (soundFile.getDuration() == null) {
            MediaPlayer calcdurationplayer = new MediaPlayer(new Media(soundFile.getFile().toURI().toString()));
            calcdurationplayer.setOnReady(() -> {
                soundFile.setDuration(calcdurationplayer.getTotalDuration().toMillis());
                Ambience.add(index, soundFile);
                calcdurationplayer.dispose();
            });
        } else {
            Ambience.add(soundFile);
        }
    }
    public SoundFile actual_get(int index) {
        return Ambience.get(index);
    }
    public SoundFile actual_get(String name) {
        for (SoundFile i : getAmbience()) {
            if (i.getName().equals(name)) return i;
        }
        return null;
    }
    public SoundFile actual_get(File file) {
        for (SoundFile i : getAmbience()) {
            if (i.getFile().equals(file)) return i;
        }
        return null;
    }
    public void actual_remove(SoundFile soundFile) {
        Ambience.remove(soundFile);
    }
    public void actual_remove(int index) {
        Ambience.remove(index);
    }
    public void actual_testifallexisting() {
        if (getAmbience() == null) {return;}
        int exitstingcount = 0;
        for (SoundFile i : getAmbience()) {
            if (i.getFile().exists()) {exitstingcount++;}
            else {System.out.println(String.format("%s Does Not Exist", i.getFile().getAbsolutePath()));}
        }
        System.out.println("Existing Ambience: " + exitstingcount);
    }
    public Duration gettotalActualDuration() {
        Duration duration = new Duration(0);
        if (Ambience != null) {
            for (SoundFile i : Ambience) {
                duration = duration.add(new Duration(i.getDuration()));
            }
        }
        return duration;
    }

    // Validation Methods
    public boolean hasAnyAmbience() {
        return Ambience != null && Ambience.size() > 0;
    }
    public boolean hasEnoughAmbience(Duration duration) {
        return gettotalActualDuration().greaterThanOrEqualTo(duration);
    }
    public boolean ambienceexistsinActual(SoundFile soundFile) {
        try {
            if (Ambience.contains(soundFile)) {
                return true;
            }
            for (SoundFile i : Ambience) {
                if (i.getFile().equals(soundFile.getFile())) {
                    return true;
                }
            }
            return false;
        } catch (NullPointerException ignored) {
            return false;
        }
    }

}
