package kujiin.xml;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Ambience {
    private List<SoundFile> Ambience;
    @XmlTransient
    private List<SoundFile> CreatedAmbience;

    public Ambience() {
    }

    // Getters And Setters
    public List<SoundFile> getAmbience() {
        return Ambience;
    }
    public void setAmbience(List<SoundFile> ambience) {
        this.Ambience = ambience;
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

    // Created Ambience
    private void created_initialize() {
        if (CreatedAmbience == null) CreatedAmbience = new ArrayList<>();
    }
    public void created_add(SoundFile soundFile) {
        created_initialize();
        CreatedAmbience.add(soundFile);
    }
    public SoundFile created_get(int index) {
        if (CreatedAmbience == null) {
            System.out.println("Created Ambience Is Null");
        }
        return CreatedAmbience.get(index);
    }
    public SoundFile created_get(String name) {
        for (SoundFile i : CreatedAmbience) {
            if (i.getName().equals(name)) return i;
        }
        return null;
    }
    public SoundFile created_get(File file) {
        for (SoundFile i : CreatedAmbience) {
            if (i.getFile().equals(file)) return i;
        }
        return null;
    }
    public List<SoundFile> created_getAll() {
        return CreatedAmbience;
    }
    public void created_remove(SoundFile soundFile) {
        CreatedAmbience.remove(soundFile);
    }
    public void created_remove(int index) {
        CreatedAmbience.remove(index);
    }
    public void created_clear() {
        if (CreatedAmbience != null) CreatedAmbience.clear();
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
    public boolean ambienceexistsinCreated(SoundFile soundFile) {
        return CreatedAmbience.contains(soundFile);
    }

    // Information Methods
    public Duration gettotalActualDuration() {
        Duration duration = new Duration(0);
        if (Ambience != null) {
            for (SoundFile i : Ambience) {
                duration = duration.add(new Duration(i.getDuration()));
            }
        }
        return duration;
    }
    public Duration gettotalCreatedDuration() {
        Duration duration = new Duration(0);
        if (CreatedAmbience != null) {
            for (SoundFile i : CreatedAmbience) {
                duration = duration.add(new Duration(i.getDuration()));
            }
        }
        return duration;
    }

    // Playback Methods

}
