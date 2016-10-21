package kujiin.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.FIELD)
public class Entrainment {
    private SoundFile freq;
    private SoundFile rampfile;
    private List<SoundFile> rampfiles;

    public Entrainment() {
    }

    public void set(SoundFile soundFile) {
        if (! soundFile.getFile().getAbsolutePath().contains("ramp")) {setFreq(soundFile);}
        else {ramp_addorset(soundFile);}
    }
    public SoundFile get(int index) {
        switch (index) {
            case 0: return freq;
            default: return ramp_get(index - 1);
        }
    }

// Getters And Setters
    public SoundFile getFreq() {
        return freq;
    }
    public void setFreq(SoundFile freq) {
        this.freq = freq;
    }
    public SoundFile getRampfile() {
        return rampfile;
    }
    public void setRampfile(SoundFile rampfile) {
        this.rampfile = rampfile;
    }
    public List<SoundFile> getRampfiles() {
        return rampfiles;
    }

// Ramp Methods
    public void ramp_initialize() {if (rampfiles == null) {rampfiles = new ArrayList<>();}}
    public void ramp_addorset(SoundFile soundFile) {
        ramp_initialize();
        int index = rampfiles.stream().map(SoundFile::getFile).collect(Collectors.toCollection(ArrayList::new)).indexOf(soundFile.getFile());
        if (index == -1) {rampfiles.add(soundFile);}
        else {rampfiles.set(index, soundFile);}
    }
    public SoundFile ramp_get(int index) {
        try {return rampfiles.get(index);}
        catch (NullPointerException | IndexOutOfBoundsException ignored) {ramp_initialize(); return null;}
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
