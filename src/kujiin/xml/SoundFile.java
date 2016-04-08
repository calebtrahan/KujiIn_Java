package kujiin.xml;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.File;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class SoundFile {
    private File file;
    private String name;
    private Duration duration;
    private Media media;

    public SoundFile() {

    }
    public SoundFile(File file) {
        if (file != null) {
            this.file = file;
            this.name = file.getName().substring(0, file.getName().lastIndexOf("."));
            this.media = new Media(this.file.toURI().toString());
            calculateduration();
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
    public Duration getDuration() {
        return duration;
    }
    public void setDuration(Duration duration) {
        this.duration = duration;
    }
    public Media toMedia() {return media;}
    public double getDurationinMillis() {
        return getDuration().toMillis();
    }
    public double getDurationinSeconds() {
        return getDuration().toSeconds();
    }
    public double getDurationinMinutes() {
        return getDuration().toMinutes();
    }

    // Utility Methods
    private void calculateduration() {
        media = new Media(getFile().toURI().toString());
        if (duration == null) {
            Service<Void> calculatedurationservice = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                MediaPlayer shortplayer = new MediaPlayer(media);
                                shortplayer.setOnReady(() -> {
                                    setDuration(shortplayer.getTotalDuration());
                                    shortplayer.dispose();
                                });
                            } catch (MediaException | NullPointerException ignored) {
                            }
                            return null;
                        }
                    };
                }
            };
            calculatedurationservice.start();
        }
    }
}
