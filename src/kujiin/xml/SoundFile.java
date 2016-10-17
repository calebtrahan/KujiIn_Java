package kujiin.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.File;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class SoundFile {
    private File file;
    private String name;
    private Double duration;

    public SoundFile() {

    }
    public SoundFile(File file) {
        if (file != null) {
            this.file = file;
            this.name = file.getName().substring(0, file.getName().lastIndexOf("."));
            duration = null;
        }
    }

// Getters And Setters
    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Double getDuration() {
        return duration;
    }
    public void setDuration(Double duration) {
        this.duration = duration;
    }

// Utility Methods
    public boolean isValid() {
        boolean isvalid = name != null && file != null && duration != null && duration > 0.0;
        if (! isvalid) {
            System.out.println("Invalid File: Nane: " + name + " File: " + file.getAbsolutePath() + "Duration: " + duration);
        }
        return isvalid;
    }

// Method Overrides
    @Override
    public String toString() {
        return "Name: " + getName() + ". File: " + file.getAbsolutePath() + ". Duration: " + getDuration() + " Milliseconds";
    }
}
