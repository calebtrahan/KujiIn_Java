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
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Ambiences {
    @XmlElement(name = "Ambience")
    private List<Ambience> Ambience;
    private List<Ambience> CreatedAmbienceList;

    public Ambiences() {}
    public Ambiences(List<Ambience> ambiences) {CreatedAmbienceList = ambiences;}

// Getters And Setters
    public List<Ambience> getAmbience() {
        return Ambience;
    }
    public void setAmbience(List<Ambience> ambience) {
        Ambience = ambience;
    }

// Creation Methods
    public void build(int minutes) {}
    public boolean isBuilt() {return false;}
    public boolean hasEnoughAmbience(int minutes) {return false;}
    public void reset() {}

// Ambience Subclass
    class Ambience {
        private File file;
        private String name;
        private Duration duration;

        public Ambience() {}
        public Ambience(File file) {
            this.file = file;
            this.name = file.getName().substring(0, file.getName().lastIndexOf("."));
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
