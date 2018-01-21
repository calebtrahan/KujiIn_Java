package kujiin.xml;

import javafx.util.Duration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class PlaybackItemAmbience {
    private List<SoundFile> Ambience;
    private String Name;

    public PlaybackItemAmbience() {}
    public PlaybackItemAmbience(String name) {Name = name;}


// Getters And Setters
    public List<SoundFile> getAmbience() {
        return Ambience;
    }
    public void setAmbience(List<SoundFile> ambience) {
        Ambience = ambience;
    }
    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

// Utility Methods
    public void add(SoundFile soundFile) {
        if (Ambience == null) {Ambience = new ArrayList<>();}
        Ambience.add(soundFile);
    }
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