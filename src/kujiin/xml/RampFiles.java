package kujiin.xml;

import java.util.List;

public class RampFiles {
    private List<SoundFile> RampFiles;

    public RampFiles() {}

// Getters And Setters
    public List<SoundFile> getRampFiles() {
        return RampFiles;
    }
    public void setRampFiles(List<SoundFile> rampFiles) {
        RampFiles = rampFiles;
    }

// Utility Methods
    public void add(SoundFile soundFile) {RampFiles.add(soundFile);}
    public void remove(int index) {RampFiles.remove(index);}
    public SoundFile getRampFile(Session.PlaybackItem from, Session.PlaybackItem to) {
        StringBuilder name = new StringBuilder();
        if (from instanceof Session.QiGong) {name.append("qi");}
        else {name.append(from.getName());}
        if (to instanceof Session.QiGong) {name.append("qi");}
        else {name.append(to.getName());}
        for (SoundFile i : RampFiles) {if (i.getName().equals(name.toString())) {return i;}}
        return null;
    }

}