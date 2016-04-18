package kujiin.xml;

import kujiin.Tools;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Ambience {
    @XmlElement
    private List<SoundFile> Ambience;
    @XmlElement
    private List<SoundFile> CreatedAmbience;

    public Ambience() {}

    // Getters And Setters
    public List<SoundFile> getAmbience() {
        return Ambience;
    }
    public void setAmbience(List<SoundFile> ambience) {
        this.Ambience = ambience;
    }

    // Ambience Editing Methods
    // Actual Ambience
    public void actual_retrievefromdefaultdirectory(String name) {
        try {
            for (File i : new File(Options.DIRECTORYAMBIENCE, name).listFiles()) {
                actual_addfromfile(i);
            }
        } catch (NullPointerException ignored) {}
    }
    private boolean actual_addfromfile(File file) {
        if (Tools.audio_isValid(file)) {
            SoundFile tempfile = new SoundFile(file);
            if (!ambienceexistsinActual(tempfile)) {
                actual_add(tempfile); return true;}
            else {return false;}
        } else {return false;}
    }
    private void actual_initialize() {if (Ambience == null) Ambience = new ArrayList<>();}
    public void actual_add(SoundFile soundFile) {
        actual_initialize(); Ambience.add(soundFile);}
    public void actual_add(int index, SoundFile soundFile) {
        actual_initialize(); Ambience.add(index, soundFile);}
    public void actual_add(List<SoundFile> soundFiles) {
        actual_initialize(); Ambience.addAll(soundFiles);}
    public SoundFile actual_get(int index) {return Ambience.get(index);}
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
    public void actual_remove(SoundFile soundFile) {Ambience.remove(soundFile);}
    public void actual_remove(int index) {Ambience.remove(index);}
    // Created Ambience
    private void created_initialize() {if (CreatedAmbience == null) CreatedAmbience = new ArrayList<>();}
    public void created_add(SoundFile soundFile) {
        created_initialize(); CreatedAmbience.add(soundFile);}
    public void created_add(List<SoundFile> soundFiles) {
        created_initialize(); CreatedAmbience.addAll(soundFiles);}
    public SoundFile created_get(int index) {return CreatedAmbience.get(index);}
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
    public List<SoundFile> created_getAll() {return CreatedAmbience;}
    public void created_remove(SoundFile soundFile) {CreatedAmbience.remove(soundFile);}
    public void created_remove(int index) {CreatedAmbience.remove(index);}
    public void created_clear() {CreatedAmbience.clear();}

    // Validation Methods
    public boolean hasAnyAmbience() {return Ambience != null && Ambience.size() > 0;}
    public boolean hasEnoughAmbience(int seconds) {
        return gettotalActualDuration() / 1000 >= seconds;
    }
    public boolean ambienceexistsinActual(SoundFile soundFile) {
        try {
            if (Ambience.contains(soundFile)) {return true;}
            for (SoundFile i : Ambience) {
                if (i.getFile().equals(soundFile.getFile())) {return true;}
            }
            return false;
        }
        catch (NullPointerException ignored) {return false;}
    }
    public boolean ambienceexistsinCreated(SoundFile soundFile) {return CreatedAmbience.contains(soundFile);}

    // Information Methods
    public Double gettotalActualDuration() {
        Double duration = 0.0;
        for (SoundFile i : Ambience) {duration += i.getDuration();}
        return duration;
    }
    public Double gettotalCreatedDuration() {
        Double duration = 0.0;
        for (SoundFile i : CreatedAmbience) {duration += i.getDuration();}
        return duration;
    }

    // Playback Methods
}
