package kujiin.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Entrainment {
    private SoundFile freq;
    private SoundFile rampfile;
    private List<SoundFile> rampfiles;

    public Entrainment() {
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
    public int ramp_count() {return rampfiles.size();}
    public void ramp_initialize() {if (rampfiles == null) {rampfiles = new ArrayList<>();}}
    public void ramp_add(SoundFile soundFile) {ramp_initialize(); rampfiles.add(soundFile);}
    public void ramp_add(int index, SoundFile soundFile) {ramp_initialize(); rampfiles.add(index, soundFile);}
    public SoundFile ramp_get(int index) {
//        try {
//            int count = 0;
//            for (SoundFile i : rampfiles) {System.out.println(count + ": " + i.getFile().getAbsolutePath());}
//        } catch (NullPointerException ignored) {}
        try {return rampfiles.get(index);}
        catch (NullPointerException | IndexOutOfBoundsException ignored) {return null;}
    }

    @Override
    public String toString() {
        System.out.println("Freq: " + freq.getName() + " Ramp: " + rampfile.getName());
        return super.toString();
    }

    // Other Methods
}
