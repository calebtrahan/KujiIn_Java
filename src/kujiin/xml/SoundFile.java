package kujiin.xml;

import kujiin.util.Util;

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
        return file != null && duration != null && Util.audio_isValid(file);
    }
}
