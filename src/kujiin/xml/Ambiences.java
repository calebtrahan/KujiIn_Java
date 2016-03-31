package kujiin.xml;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Ambiences {
    @XmlElement(name = "Ambience")
    private List<Ambience> Ambience;
    private List<Ambience> CreatedAmbience;

    public Ambiences() {}
    public Ambiences(List<Ambience> ambiences) {
        CreatedAmbience = ambiences;}

// Getters And Setters
    public List<Ambience> getAmbience() {
        return Ambience;
    }
    public void setAmbience(List<Ambience> ambience) {
        Ambience = ambience;
    }
    public List<Ambiences.Ambience> getCreatedAmbience() {
        return CreatedAmbience;
    }
    public void setCreatedAmbience(List<Ambiences.Ambience> createdAmbience) {
        CreatedAmbience = createdAmbience;
    }
    public Ambience getSelectedAmbience(int index) {return getAmbience().get(index);}
    public List<File> getAmbienceFiles() {
        List<File> files = new ArrayList<>();
        for (Ambience i : getAmbience()) {
            if (i.getFile() != null && i.getFile().exists()) {
                files.add(i.getFile());
            }
        }
        return files;
    }

// Ambience Information
    public Duration getAmbienceDuration() {
        Duration duration = new Duration(0.0);
        for (Ambience i : getAmbience()) {duration.add(i.getDuration());}
        return duration;
    }
    public Duration getCreatedAmbienceDuration() {
        Duration duration = new Duration(0.0);
        for (Ambience i : getCreatedAmbience()) {duration.add(i.getDuration());}
        return duration;
    }

// Resource Ambience Methods
    public boolean ambiencealreadyexists(File file) {
    for (Ambience i : Ambience) {
        if (i.getFile().equals(file)) {return true;}
    }
    return false;
}
    public void addResourceAmbience(File file) {if (! ambiencealreadyexists(file)) Ambience.add(new Ambience(file));}
    public void removeResourceAmbience() {}

// Created Ambience Methods
    public void addCreatedAmbience(Ambience i) {CreatedAmbience.add(i);}
    public void removeCreatedAmbience() {}
    public boolean isBuilt() {return CreatedAmbience != null && CreatedAmbience.size() > 0;}
    public boolean hasEnoughAmbience(int minutes) {return false;}
    public void reset() {
        CreatedAmbience.clear();}

    // Ambience Subclass
    public class Ambience {
        private File file;
        private String name;
        private Duration duration;
        private Media media;

        public Ambience() {}
        public Ambience(File file) {
            this.file = file;
            this.name = file.getName().substring(0, file.getName().lastIndexOf("."));
            this.media = new Media(file.toURI().toString());
            calculateduration();
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
        public Media getMedia() {
            return media;
        }
        public void setMedia(Media media) {
            this.media = media;
        }
        public double getDurationinMillis() {return getDuration().toMillis();}
        public double getDurationinSeconds() {return getDuration().toSeconds();}
        public double getDurationinMinutes() {return getDuration().toMinutes();}

        // Utility Methods
        private void calculateduration() {
            Service<Void> calculatedurationservice = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                MediaPlayer shortplayer = new MediaPlayer(new Media(getFile().toURI().toString()));
                                shortplayer.setOnReady(() -> {setDuration(shortplayer.getTotalDuration()); shortplayer.dispose();});
                            } catch (MediaException | NullPointerException ignored) {}
                            return null;
                        }
                    };
                }
            };
            calculatedurationservice.start();
        }

    }
}
