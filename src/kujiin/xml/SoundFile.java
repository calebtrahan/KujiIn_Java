package kujiin.xml;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import kujiin.Tools;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.File;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class SoundFile {
    private File file;
    private String name;
    private Double duration;
    private Media media;

    public SoundFile() {

    }
    public SoundFile(File file) {
        if (file != null) {
            this.file = file;
            this.name = file.getName().substring(0, file.getName().lastIndexOf("."));
            this.media = new Media(this.file.toURI().toString());
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
    public Media toMedia() {return media;}
    public double getDurationinMillis() {return duration;}
    public double getDurationinSeconds() {return duration /= 1000;}
    public double getDurationinMinutes() {return  (duration / 1000) / 60;}

    // Utility Methods
    public boolean isValid() {
        if (file == null) {return false;}
        return Tools.audio_isValid(file);
    }
    private void calculateduration() {
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> {
            setDuration(mediaPlayer.getTotalDuration().toMillis());
            mediaPlayer.dispose();
        });
    }
}
