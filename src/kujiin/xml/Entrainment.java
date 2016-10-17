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

    public void setFile(SoundFile soundFile) {
        if (! soundFile.getFile().getAbsolutePath().contains("ramp")) {setFreq(soundFile);}
        else {ramp_set(soundFile);}
    }

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
    public void ramp_add(SoundFile soundFile) {ramp_initialize(); rampfiles.add(soundFile);}
    public void ramp_set(SoundFile soundFile) {
        ramp_initialize();
        int index = rampfiles.stream().map(SoundFile::getFile).collect(Collectors.toCollection(ArrayList::new)).indexOf(soundFile.getFile());
        if (index == -1) {ramp_add(soundFile);}
        else {rampfiles.set(index, soundFile);}
    }
    public SoundFile ramp_get(int index) {
//        try {
//            int count = 0;
//            for (SoundFile i : rampfiles) {System.out.println(count + ": " + i.getFile().getAbsolutePath());}
//        } catch (NullPointerException ignored) {}
        try {return rampfiles.get(index);}
        catch (NullPointerException | IndexOutOfBoundsException ignored) {ramp_initialize(); return null;}
    }

    @Override
    public String toString() {
        return super.toString();
    }

    // Other Methods

}
