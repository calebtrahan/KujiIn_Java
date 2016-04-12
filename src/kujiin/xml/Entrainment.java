package kujiin.xml;

import javafx.util.Duration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.File;
import java.util.Collections;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Entrainment {
    @XmlElement
    private SoundFile rampinfile;
    @XmlElement
    private SoundFile freqshort;
    @XmlElement
    private SoundFile freqlong;
    @XmlElement
    private SoundFile rampoutfile;
    private List<SoundFile> CreatedEntrainment;

    public Entrainment() {}

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
    public void created_add(SoundFile soundFile) {CreatedEntrainment.add(soundFile);}
    public void created_add(int index, SoundFile soundFile) {CreatedEntrainment.add(soundFile);}
    public void created_add(List<SoundFile> soundFiles) {CreatedEntrainment.addAll(soundFiles);}
    public SoundFile created_get(int index) {return CreatedEntrainment.get(index);}
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
    public List<SoundFile> created_getAll() {return CreatedEntrainment;}
    public void created_remove(SoundFile soundFile) {CreatedEntrainment.remove(soundFile);}
    public void created_remove(int index) {CreatedEntrainment.remove(index);}
    public void created_clear() {CreatedEntrainment.clear();}

    // Information Methods
    public Duration gettotalCreatedDuration() {
        Duration duration = new Duration(0.0);
        for (SoundFile i : CreatedEntrainment) {duration.add(i.getDuration());}
        return duration;
    }

    // Other Methods
    public void shuffleCreated() {
        Collections.shuffle(CreatedEntrainment);}
}
