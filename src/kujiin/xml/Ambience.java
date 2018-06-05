package kujiin.xml;

import javafx.util.Duration;

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
    private List<SoundFile> SessionAmbience;
    @XmlTransient
    private List<SoundFile> AvailableAmbience;
    private ArrayList<SoundFile> PlaybackHistory;
    @XmlTransient
    private SoundFile CurrentAmbienceSoundfile;
    @XmlTransient
    private int currentplaybackhistorycount = 0;
    @XmlTransient
    private AvailableAmbiences availableAmbiences;

    public Ambience() {}
    public Ambience(AvailableAmbiences availableAmbiences) {}

// Getters And Setters
    public void setSessionAmbience(List<SoundFile> sessionAmbience) {
    SessionAmbience = sessionAmbience;}
    public List<SoundFile> getSessionAmbience() {return SessionAmbience;}
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
    public List<SoundFile> getAvailableAmbience() {
        return AvailableAmbience;
    }
    public void setAvailableAmbience(List<SoundFile> availableAmbience) {
        AvailableAmbience = availableAmbience;
    }

// Ambience List Methods
    // Preset
    public void addPreset(SoundFile soundFile) {
        if (SessionAmbience == null) SessionAmbience = new ArrayList<>();
        SessionAmbience.add(soundFile);
    }
    public void addPreset(List<SoundFile> soundFiles) {
        if (SessionAmbience == null) SessionAmbience = new ArrayList<>();
        SessionAmbience.addAll(soundFiles);
    }
    public boolean hasPresetAmbience() {return SessionAmbience != null && ! SessionAmbience.isEmpty();}
    public SoundFile getPreset(int index) throws IndexOutOfBoundsException {return SessionAmbience.get(index);}
    public SoundFile getPreset(String name) {
        for (SoundFile i : SessionAmbience) {if (i.getName().equals(name)) return i;}
        return null;
    }
    public SoundFile getPreset(File file) {
        for (SoundFile i : SessionAmbience) {if (i.getFile().equals(file)) return i;}
        return null;
    }
    public SoundFile getnextpresetambienceforplayback(SoundFile previousfile) {
        if (PlaybackHistory == null) {PlaybackHistory = new ArrayList<>();}
        if (previousfile == null) {return SessionAmbience.get(0);}
        int index = SessionAmbience.indexOf(previousfile) + 1;
        SoundFile nextambience;
        try { nextambience = SessionAmbience.get(index); }
        catch (IndexOutOfBoundsException ignored) {nextambience = SessionAmbience.get(0);}
        PlaybackHistory.add(nextambience);
        currentplaybackhistorycount++;
        return nextambience;
    }
    public SoundFile getpreviouspresetambienceforplayback(SoundFile previousfile) {
        if (PlaybackHistory == null) {PlaybackHistory = new ArrayList<>();}
        if (previousfile == null) {return SessionAmbience.get(SessionAmbience.size() - 1);}
        int index = SessionAmbience.indexOf(previousfile) - 1;
        SoundFile previousambience;
        try { previousambience = SessionAmbience.get(index); }
        catch (IndexOutOfBoundsException ignored) {previousambience = SessionAmbience.get(SessionAmbience.size() - 1);}
        PlaybackHistory.add(previousambience);
        currentplaybackhistorycount++;
        return previousambience;
    }
    public Duration getPresetAmbienceDuration() {
        Duration totalduration = Duration.ZERO;
        if (SessionAmbience != null) {
            for (SoundFile i : SessionAmbience) {totalduration = totalduration.add(new Duration(i.getDuration()));}
        }
        return totalduration;
    }
    public void removePreset(int index) {
        SessionAmbience.remove(index);
    }
    public void clearPresetambience() { if (SessionAmbience != null) SessionAmbience.clear(); }
    // Available
    public boolean hasAvailableAmbience() {return AvailableAmbience != null && ! AvailableAmbience.isEmpty();}
    public SoundFile getAvailable(int index) throws IndexOutOfBoundsException {return AvailableAmbience.get(index);}
    public SoundFile getAvailable(String name) {
        for (SoundFile i : AvailableAmbience) {if (i.getName().equals(name)) return i;}
        return null;
    }
    public SoundFile getAvailable(File file) {
        for (SoundFile i : AvailableAmbience) {if (i.getFile().equals(file)) return i;}
        return null;
    }
    public SoundFile getnextavailableambienceforplayback(SoundFile previousfile) {
        if (PlaybackHistory == null) {PlaybackHistory = new ArrayList<>();}
        if (previousfile == null) {return AvailableAmbience.get(0);}
        int index = AvailableAmbience.indexOf(previousfile) + 1;
        SoundFile nextambience;
        try { nextambience = AvailableAmbience.get(index); }
        catch (IndexOutOfBoundsException ignored) {nextambience = AvailableAmbience.get(0);}
        PlaybackHistory.add(nextambience);
        currentplaybackhistorycount++;
        return nextambience;
    }
    public SoundFile getpreviousavailableambienceforplayback(SoundFile previousfile) {
        if (PlaybackHistory == null) {PlaybackHistory = new ArrayList<>();}
        if (previousfile == null) {return AvailableAmbience.get(AvailableAmbience.size() - 1);}
        int index = AvailableAmbience.indexOf(previousfile) - 1;
        SoundFile previousambience;
        try { previousambience = AvailableAmbience.get(index); }
        catch (IndexOutOfBoundsException ignored) {previousambience = AvailableAmbience.get(AvailableAmbience.size() - 1);}
        PlaybackHistory.add(previousambience);
        currentplaybackhistorycount++;
        return previousambience;
    }
    public Duration getAvailableAmbienceDuration() {
        Duration totalduration = Duration.ZERO;
        for (SoundFile i : AvailableAmbience) {totalduration = totalduration.add(new Duration(i.getDuration()));}
        return totalduration;
    }

    // History
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
    // Quick Add Ambience
    public void quickadd_repeat(PlaybackItem playbackItem, boolean clearambience) {
    // Clear Ambience (If Needed)
        if (clearambience) { clearPresetambience();}
    // Calculate Current Duration
        Duration currentduration = Duration.ZERO;
        if (hasPresetAmbience()) {
            for (SoundFile i : SessionAmbience) {
                currentduration = currentduration.add(Duration.millis(i.getDuration()));
            }
        }
    // Calculate Max Duration
        Duration maxduration;
        if (playbackItem.isRampOnly()) {maxduration = Duration.minutes(1);}
        else {maxduration = new Duration(playbackItem.getExpectedDuration());}
        int indexcount = 0;
        while (currentduration.lessThan(maxduration)) {
            try {
                SoundFile filetoadd = AvailableAmbience.get(indexcount);
                addPreset(filetoadd);
                currentduration = currentduration.add(Duration.millis(filetoadd.getDuration()));
                indexcount++;
            }
            catch (IndexOutOfBoundsException ignored) {indexcount = 0;}
        }
    }
    public void quickadd_shuffle_old(PlaybackItem playbackItem, boolean clearambience) {
    // Clear Ambience (If Needed)
        if (clearambience) {clearPresetambience();}
    // Get Current Duration And Existing Ambience Indexes
        List<SoundFile> ambiencelist = new ArrayList<>();
        Duration currentduration = Duration.ZERO;
        List<Integer> indexhistory = new ArrayList<>();
        if (hasPresetAmbience()) {
            for (SoundFile i : SessionAmbience) {
                ambiencelist.add(i);
                indexhistory.add(AvailableAmbience.indexOf(i));
            }
            currentduration = getPresetAmbienceDuration();
        }
    // Set Max Duration
        Duration maxduration;
        if (playbackItem.isRampOnly()) {maxduration = Duration.minutes(1);}
        else {maxduration = new Duration(playbackItem.getExpectedDuration());}
    // Set Ambience Size
        int indexcount;
        Random random = new Random();
        while (currentduration.lessThan(maxduration)) {
            if (! indexhistory.isEmpty()) {
                while (true) {
                    indexcount = random.nextInt(AvailableAmbience.size());
                    if (indexhistory.isEmpty()) {indexhistory.add(indexcount); break;}
                    if (indexhistory.size() <= AvailableAmbience.size()) {
                        if (! indexhistory.contains(indexcount)) {break;}
                    } else if (indexcount != indexhistory.get(indexhistory.size() - 1)) {break;}
                }
            } else {indexcount = random.nextInt(AvailableAmbience.size());}
            try {
                SoundFile filetoadd = getAvailable(indexcount);
                ambiencelist.add(filetoadd);
                currentduration = currentduration.add(Duration.millis(filetoadd.getDuration()));
                indexhistory.add(indexcount);
            } catch (IndexOutOfBoundsException ignored) {ignored.printStackTrace();}
        }
        setSessionAmbience(ambiencelist);
    }
    public void quickadd_shuffle(PlaybackItem playbackItem, boolean clearambience) {
        List<SoundFile> ambiencelist = new ArrayList<>();
        Duration currentduration = Duration.ZERO;
    // Clear Ambience (If Needed)
        if (clearambience) {clearPresetambience();}
    // Add Existing Preset Ambience
        else if (hasPresetAmbience()) {
            ambiencelist.addAll(SessionAmbience);
            currentduration = getPresetAmbienceDuration();
        }
    // Set Max Duration
        Duration maxduration;
        if (playbackItem.isRampOnly()) {maxduration = Duration.minutes(1);}
        else {maxduration = new Duration(playbackItem.getExpectedDuration());}
    // Add Shuffled Ambience
        if (AvailableAmbience.size() > 1) {
            while (currentduration.lessThan(maxduration)) {
                List<SoundFile> shuffledambiencelist = AvailableAmbience;
                while (true) {
                    Collections.shuffle(shuffledambiencelist);
                    // Test For Duplicate First And Last Item Here
                    if (ambiencelist.isEmpty() || ! ambiencelist.get(ambiencelist.size() - 1).equals(shuffledambiencelist.get(shuffledambiencelist.size() - 1))) {break;}
                }
                for (SoundFile x : shuffledambiencelist) {
                    ambiencelist.add(x);
                    currentduration = currentduration.add(Duration.millis(x.getDuration()));
                    if (currentduration.greaterThanOrEqualTo(maxduration)) { SessionAmbience = ambiencelist; return;}
                }
            }
        } else {
            while (currentduration.lessThan(maxduration)) {
                SoundFile file = AvailableAmbience.get(0);
                ambiencelist.add(file);
                currentduration = currentduration.add(Duration.millis(file.getDuration()));
            }
        }
        System.out.println("Ambience List Is:");
        int count = 1;
        for (SoundFile i : ambiencelist) {
            System.out.println(count + ": " + i.getName());
        }
        setSessionAmbience(ambiencelist);
    }

}
