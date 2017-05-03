package kujiin.xml;

import javafx.util.Duration;
import kujiin.util.enums.AmbiencePlaybackType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@XmlAccessorType(XmlAccessType.FIELD)
public class Ambience {
    private List<SoundFile> Ambience;
    private boolean Enabled;
    private ArrayList<SoundFile> PlaybackHistory;
    @XmlTransient
    private SoundFile CurrentAmbienceSoundfile;
    @XmlTransient
    private int currentplaycount = 0;
    @XmlTransient
    private int currentplaybackhistorycount = 0;

    public Ambience() {}
    public Ambience(AvailableAmbiences availableAmbiences) {}

// Getters And Setters
    public boolean isEnabled() {
        return Enabled;
    }
    public void setEnabled(boolean enabled) {
        this.Enabled = enabled;
    }
    public SoundFile getCurrentAmbienceSoundfile() {
        return CurrentAmbienceSoundfile;
    }
    public void setCurrentAmbienceSoundfile(SoundFile currentAmbienceSoundfile) {
        this.CurrentAmbienceSoundfile = currentAmbienceSoundfile;
    }
    public List<File> getAmbienceFiles() {
        List<File> files = new ArrayList<>();
        try {
//            files.addAll(AvailableAmbiences.stream().map(SoundFile::getFile).collect(Collectors.toList()));
            return files;
        } catch (NullPointerException ignored) {return new ArrayList<>();}
    }

// Utility Methods
//    public void startup_addambiencefromdirectory(SessionItem selectedsessionpart) {
//        try {
//            File ambiencedirectory = new File(Preferences.DIRECTORYAMBIENCE, selectedsessionpart.name);
//            for (File i : ambiencedirectory.listFiles()) {
//                if (Util.audio_isValid(i) && ! selectedsessionpart.getAmbience().getAmbienceFiles().contains(i)) {add(new SoundFile(i));}
//            }
//        } catch (NullPointerException ignored) {}
//    }
//    public void startup_checkfordeletedfiles() {
////        if (AvailableAmbiences != null) {
////            AvailableAmbiences.stream().filter(i -> !i.getFile().exists()).forEach(this::remove);
////        }
//    }

// Ambience List Methods
    public void add(SoundFile soundFile) {
        if (Ambience == null) Ambience = new ArrayList<>();
        Ambience.add(soundFile);
    }
    public void setoraddsoundfile(SoundFile soundFile) {
        int index = getAmbienceFiles().indexOf(soundFile.getFile());
        if (index != -1) {
//            AvailableAmbiences.set(index, soundFile);}
        } else {add(soundFile);}
    }
    public SoundFile get(int index) throws IndexOutOfBoundsException {return Ambience.get(index);}
    public SoundFile getnextambienceforplayback() {
        if (PlaybackHistory == null) {PlaybackHistory = new ArrayList<>();}
        try {
            SoundFile nextambiencefile = Ambience.get(currentplaycount);
            currentplaycount++;
            PlaybackHistory.add(nextambiencefile);
            currentplaybackhistorycount++;
            return nextambiencefile;
        } catch (IndexOutOfBoundsException ignored) {
            currentplaycount = 0;
            PlaybackHistory.add(Ambience.get(0));
            currentplaybackhistorycount++;
            return Ambience.get(0);
        }
    }
    public SoundFile getnextambiencehistory() {
        try {
            currentplaybackhistorycount++;
            return PlaybackHistory.get(currentplaybackhistorycount);
        } catch (IndexOutOfBoundsException ignored) {
            currentplaybackhistorycount--;
            return null;
        }
    }
    public SoundFile getpreviousambiencehistory() {
        try {
            currentplaybackhistorycount--;
            return PlaybackHistory.get(currentplaybackhistorycount);
        } catch (IndexOutOfBoundsException ignored) {
            currentplaybackhistorycount++;
            return null;
        }
    }
    public SoundFile ambiencegenerator(AmbiencePlaybackType ambiencePlaybackType, List<SoundFile> playbackhistory, SoundFile currentsoundfile) {
        switch (ambiencePlaybackType) {
            case REPEAT:
                if (currentsoundfile != null) {
                    int currentambienceindex = Ambience.indexOf(currentsoundfile);
                    if (currentambienceindex < Ambience.size() - 1) {return get(currentambienceindex + 1);}
                    else {return get(0);}
                } else {return get(0);}
            case SHUFFLE:
                if (Ambience.size() == 1) {return get(0);}
                Random random = new Random();
                SoundFile soundfiletotest;
                if (playbackhistory.size() < Ambience.size()) {
                    while (true) {
                        soundfiletotest = get(random.nextInt(Ambience.size()));
                        if (! playbackhistory.contains(soundfiletotest)) {
                            return soundfiletotest;
                        }
                    }
                } else {
                    while (true) {
                        soundfiletotest = get(random.nextInt(Ambience.size()));
                        if (! soundfiletotest.equals(currentsoundfile)) {
                            return soundfiletotest;
                        }
                    }
                }
            case CUSTOM:
                if (currentsoundfile != null) {
                    int index = Ambience.indexOf(currentsoundfile);
                    return Ambience.get(index + 1);
                } else {return Ambience.get(0);}
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
    public void addavailableambience_repeat(Duration duration, PlaybackItemAmbience playbackItemAmbience) {
        Duration currentduration = Duration.ZERO;
        int indexcount = 0;
        while (currentduration.lessThan(duration)) {
            try {
                SoundFile filetoadd = playbackItemAmbience.getAmbience().get(indexcount);
                add(filetoadd);
                currentduration = currentduration.add(Duration.millis(filetoadd.getDuration()));
            }
            catch (IndexOutOfBoundsException ignored) {indexcount = 0;}
        }
    }
    public void addavailableambience_shuffle(Duration duration, PlaybackItemAmbience playbackItemAmbience) {
        List<SoundFile> ambiencelist = new ArrayList<>();
        Duration currentduration = Duration.ZERO;
        List<Integer> indexhistory = new ArrayList<>();
        int indexcount;
        while (currentduration.lessThan(duration)) {
            int size = playbackItemAmbience.getAmbience().size();
            if (size > 1) {
                Random random = new Random();
                while (true) {
                    indexcount = random.nextInt(size);
                    if (indexhistory.isEmpty()) {indexhistory.add(indexcount); break;}
                    if (indexcount != indexhistory.get(indexhistory.size() - 1)) {break;}
                }
            } else {indexcount = 0;}
            try {
                SoundFile filetoadd = playbackItemAmbience.getAmbience().get(indexcount);
                ambiencelist.add(filetoadd);
                currentduration = currentduration.add(Duration.millis(filetoadd.getDuration()));
            }
            catch (IndexOutOfBoundsException ignored) {}
        }
        Collections.shuffle(ambiencelist);
        setAmbience(ambiencelist);
    }
    public void clearambience() { if (Ambience != null) {Ambience.clear();}}
    public void remove(int index) {
        Ambience.remove(index);
    }

// Custom Ambience
    public boolean hasCustomAmbience() {return Ambience != null && ! Ambience.isEmpty();}
    public void setAmbience(List<SoundFile> ambience) {
        Ambience = ambience;}
    public List<SoundFile> getAmbience() {return Ambience;}

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
    public void addambiencetoplaybackhistory(SoundFile soundFile) {
        if (PlaybackHistory == null) {PlaybackHistory = new ArrayList<>();}
        PlaybackHistory.add(soundFile);
    }

}
