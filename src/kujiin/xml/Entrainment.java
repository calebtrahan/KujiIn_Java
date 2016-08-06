package kujiin.xml;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Entrainment {
    private SoundFile rampinfile;
    private SoundFile freqshort;
    private SoundFile freqlong;
    private SoundFile rampoutfile;
    private List<SoundFile> CreatedEntrainment;

    public Entrainment() {
    }

    // Getters And Setters
    public SoundFile getRampinfile() {
        return rampinfile;
    }

    public void setRampinfile(SoundFile rampinfile) {
        this.rampinfile = rampinfile;
    }

    public SoundFile getFreqshort() {
        return freqshort;
    }

    public void setFreqshort(SoundFile freqshort) {
        this.freqshort = freqshort;
    }

    public SoundFile getFreqlong() {
        return freqlong;
    }

    public void setFreqlong(SoundFile freqlong) {
        this.freqlong = freqlong;
    }

    public SoundFile getRampoutfile() {
        return rampoutfile;
    }

    public void setRampoutfile(SoundFile rampoutfile) {
        this.rampoutfile = rampoutfile;
    }

    // Add/Remove Created Entrainment
    private void created_initialize() {
        if (CreatedEntrainment == null) CreatedEntrainment = new ArrayList<>();
    }

    public void created_add(SoundFile soundFile) {
        created_initialize();
        CreatedEntrainment.add(soundFile);
    }

    public void created_add(int index, SoundFile soundFile) {
        created_initialize();
        CreatedEntrainment.add(index, soundFile);
    }

    public SoundFile created_get(int index) {
        return CreatedEntrainment.get(index);
    }

    public SoundFile created_get(String name) {
        for (SoundFile i : CreatedEntrainment) {
            if (i.getName().equals(name)) return i;
        }
        return null;
    }

    public SoundFile created_get(File file) {
        for (SoundFile i : CreatedEntrainment) {
            if (i.getFile().equals(file)) return i;
        }
        return null;
    }

    public List<SoundFile> created_getAll() {
        return CreatedEntrainment;
    }

    public void created_remove(SoundFile soundFile) {
        CreatedEntrainment.remove(soundFile);
    }

    public void created_remove(int index) {
        CreatedEntrainment.remove(index);
    }

    public void created_clear() {
        if (CreatedEntrainment != null) CreatedEntrainment.clear();
    }

    // Information Methods
    public Duration gettotalCreatedDuration() {
        Duration duration = Duration.ZERO;
        for (SoundFile i : CreatedEntrainment) {
            if (i.getDuration() != null) {
                duration = duration.add(new Duration(i.getDuration()));
            }
        }
        return duration;
    }

    public void calculateshortfreqduration() {
        MediaPlayer mediaPlayer = new MediaPlayer(new Media(getFreqshort().getFile().toURI().toString()));
        mediaPlayer.setOnReady(() -> {
            getFreqshort().setDuration(mediaPlayer.getTotalDuration().toMillis());
            mediaPlayer.dispose();
        });
    }

    public void calculatelongfreqduration() {
        MediaPlayer mediaPlayer = new MediaPlayer(new Media(getFreqlong().getFile().toURI().toString()));
        mediaPlayer.setOnReady(() -> {
            getFreqlong().setDuration(mediaPlayer.getTotalDuration().toMillis());
            mediaPlayer.dispose();
        });
    }

    // Other Methods
    public void shuffleCreated() {
        if (CreatedEntrainment != null) Collections.shuffle(CreatedEntrainment);
    }

}
