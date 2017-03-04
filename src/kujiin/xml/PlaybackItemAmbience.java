package kujiin.xml;

import javafx.util.Duration;

import java.util.List;

public class PlaybackItemAmbience {
    private List<SoundFile> Ambience;

    // Getters And Setters
    public List<SoundFile> getAmbience() {
        return Ambience;
    }
    public void setAmbience(List<SoundFile> ambience) {
        Ambience = ambience;
    }

    // Utility Methods
    public void add(SoundFile soundFile) {Ambience.add(soundFile);}
    public void remove(int index) {Ambience.remove(index);}
    public Duration gettotalduration() {
        Duration duration = Duration.ZERO;
        if (Ambience != null) {
            for (SoundFile i : Ambience) {
                duration = duration.add(new Duration(i.getDuration()));
            }
        }
        return duration;
    }
    public boolean hasAny() {return Ambience != null && ! Ambience.isEmpty();}
    public boolean filealreadyexists(SoundFile soundFile) {
        return Ambience.contains(soundFile);
    }

}