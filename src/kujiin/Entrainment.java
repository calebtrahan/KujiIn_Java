package kujiin;

import javafx.scene.media.Media;

import java.io.File;
import java.time.Duration;

// TODO Add XML Bindings
// TODO Use This To Calculate Duration
public class Entrainment {
    private File file;
    private Duration duration;
    private Media media;

    public Entrainment(File file) {}

// Getters And Setters
    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }
    public Duration getDuration() {
        return duration;
    }
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public boolean fileExists() {return file != null && file.exists();}
    public boolean validfileextension() {}
    public void calculateduration() {
        // TODO Use Media And MediaPlayer To Calculate Duration

    }

}
