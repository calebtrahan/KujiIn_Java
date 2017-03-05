package kujiin.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@XmlAccessorType(XmlAccessType.FIELD)
public class PlaybackItemEntrainment {
    private SoundFile freq;
    private SoundFile rampfile;
    private List<SoundFile> rampfiles;
    @XmlTransient
    private BlockingQueue<SoundFile> EntrainmentFilesToCheck;

    public PlaybackItemEntrainment() {
    }

// Getters And Setters
    public List<SoundFile> getAllEntrainmentFiles() {return Arrays.asList(freq);}
    public SoundFile getFreq() {
        return freq;
    }
    public void setFreq(SoundFile freq) {
        this.freq = freq;
    }

//// Ramp Methods
//    public void ramp_initialize() {if (rampfiles == null) {rampfiles = new ArrayList<>();}}
//    public void ramp_addorset(SoundFile soundFile) {
//        ramp_initialize();
//        int index = rampfiles.stream().map(SoundFile::getFile).collect(Collectors.toCollection(ArrayList::new)).indexOf(soundFile.getFile());
//        if (index == -1) {rampfiles.add(soundFile);}
//        else if (! soundFile.equals(rampfiles.get(index))) {rampfiles.set(index, soundFile);}
//    }
//    public SoundFile ramp_get(int index) {
//        try {
//            return rampfiles.get(index);
//        } catch (NullPointerException | IndexOutOfBoundsException ignored) {
//            if (ignored instanceof IndexOutOfBoundsException) {ramp_initialize(); return null;}
//            else {return null;}
//        }
//    }

    @Override
    public String toString() {
        return "Ramp File: " + rampfile.getFile().getAbsolutePath() + " & Freq: " + freq.getFile().getAbsolutePath();
    }

    public boolean isValid() {return true;}
}
