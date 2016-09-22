package kujiin.xml;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.util.This_Session;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.FIELD)
public class Ambience {
    private List<SoundFile> Ambience;
    @XmlTransient
    private List<SoundFile> CustomAmbience;
    @XmlTransient
    private List<SoundFile> UsedFiles = new ArrayList<>();

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
            files.addAll(Ambience.stream().map(SoundFile::getFile).collect(Collectors.toList()));
            return files;
        } catch (NullPointerException ignored) {return new ArrayList<>();}
    }

// Actual Ambience
    private void initialize() {
        if (Ambience == null) Ambience = new ArrayList<>();
    }
    public void add(SoundFile soundFile) {
        initialize();
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
    public SoundFile get(int index) throws IndexOutOfBoundsException {
        return Ambience.get(index);
    }
    public SoundFile getnextSoundFile(This_Session.AmbiencePlaybackType ambiencePlaybackType, List<SoundFile> playbackhistory, SoundFile currentsoundfile) {
        switch (ambiencePlaybackType) {
            case REPEAT:
                if (currentsoundfile != null) {
                    int currentambienceindex = Ambience.indexOf(currentsoundfile);
                    if (currentambienceindex < Ambience.size() - 1) {return get(currentambienceindex + 1);}
                    else {return get(0);}
                } else {return get(0);}
            case SHUFFLE:
                if (Ambience.size() == 1) {UsedFiles.add(get(0)); return get(0);}
                Random random = new Random();
                SoundFile soundfiletotest;
                if (playbackhistory.size() < Ambience.size()) {
                    while (true) {
                        soundfiletotest = get(random.nextInt(Ambience.size()));
                        if (! playbackhistory.contains(soundfiletotest)) {
                            UsedFiles.add(soundfiletotest);
                            return soundfiletotest;
                        }
                    }
                } else {
                    while (true) {
                        soundfiletotest = get(random.nextInt(Ambience.size() - 1));
                        if (! soundfiletotest.equals(currentsoundfile)) {
                            UsedFiles.add(soundfiletotest);
                            return soundfiletotest;
                        }
                    }
                }
            case CUSTOM:
                if (currentsoundfile != null) {
                    int index = CustomAmbience.indexOf(currentsoundfile);
                    return CustomAmbience.get(index + 1);
                } else {return CustomAmbience.get(0);}
            default:
                return null;
        }
    }
    public SoundFile get(String name) {
        for (SoundFile i : Ambience) {
            if (i.getName().equals(name)) return i;
        }
        return null;
    }
    public SoundFile get(File file) {
        for (SoundFile i : Ambience) {
            if (i.getFile().equals(file)) return i;
        }
        return null;
    }
    public void remove(SoundFile soundFile) {
        Ambience.remove(soundFile);
    }
    public void actual_testifallexisting() {
        if (Ambience == null) {return;}
        int exitstingcount = 0;
        for (SoundFile i : Ambience) {
            if (i.getFile().exists()) {exitstingcount++;}
            else {System.out.println(String.format("%s Does Not Exist", i.getFile().getAbsolutePath()));}
        }
        System.out.println("Existing Ambience: " + exitstingcount);
    }

// Custom Ambience
    public void setCustomAmbience(List<SoundFile> customAmbience) {CustomAmbience = customAmbience;}
    public List<SoundFile> getCustomAmbience() {return CustomAmbience;}

// Validation Methods
    public Duration gettotalDuration() {
        Duration duration = new Duration(0);
        if (Ambience != null) {
            for (SoundFile i : Ambience) {
                duration = duration.add(new Duration(i.getDuration()));
            }
        }
        return duration;
    }
    public boolean hasAnyAmbience() {
        return Ambience != null && Ambience.size() > 0;
    }
    public boolean hasEnoughAmbience(Duration duration) {
        return gettotalDuration().greaterThanOrEqualTo(duration);
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
