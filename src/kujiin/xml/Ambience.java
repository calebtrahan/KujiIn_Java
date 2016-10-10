package kujiin.xml;

import javafx.util.Duration;
import kujiin.util.SessionPart;
import kujiin.util.Util;
import kujiin.util.enums.AmbiencePlaybackType;

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

    // Utility Methods
    public void startup_addambiencefromdirectory(SessionPart selectedsessionpart) {
        try {
            File ambiencedirectory = new File(Options.DIRECTORYAMBIENCE, selectedsessionpart.name);
            for (File i : ambiencedirectory.listFiles()) {
                if (Util.audio_isValid(i) && ! selectedsessionpart.getAmbience().getAmbienceFiles().contains(i)) {add(new SoundFile(i));}
            }
        } catch (NullPointerException ignored) {
            // TODO Change This To Reflect No Ambience Files In Directory
        }
    }
    public void startup_checkfordeletedfiles() {
        if (Ambience != null) {
            Ambience.stream().filter(i -> !i.getFile().exists()).forEach(this::remove);
        }
    }

// Actual Ambience
    public void add(SoundFile soundFile) {
        if (Ambience == null) Ambience = new ArrayList<>();
        Ambience.add(soundFile);
    }
    public void setoraddsoundfile(SoundFile soundFile) {
        int index = getAmbienceFiles().indexOf(soundFile.getFile());
        if (index != -1) {Ambience.set(index, soundFile);}
        else {add(soundFile);}
    }
    public SoundFile get(int index) throws IndexOutOfBoundsException {return Ambience.get(index);}
    public SoundFile ambiencegenerator(AmbiencePlaybackType ambiencePlaybackType, List<SoundFile> playbackhistory, SoundFile currentsoundfile) {
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
        for (SoundFile i : Ambience) {if (i.getName().equals(name)) return i;}
        return null;
    }
    public SoundFile get(File file) {
        for (SoundFile i : Ambience) {if (i.getFile().equals(file)) return i;}
        return null;
    }
    public void remove(SoundFile soundFile) {
        Ambience.remove(soundFile);
    }

// Custom Ambience
    public boolean hasCustomAmbience() {return CustomAmbience != null && ! CustomAmbience.isEmpty();}
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
