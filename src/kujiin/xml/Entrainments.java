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
public class Entrainments {
    @XmlElement(name = "Entrainment")
    private List<Entrainment> Entrainment;
    private List<Entrainment> CreatedEntrainmentList;

    public Entrainments() {}
    public Entrainments(List<Entrainment> entrainments) {CreatedEntrainmentList = entrainments;}

// Getters And Setters
    public List<Entrainment> getEntrainment() {
        return Entrainment;
    }
    public void setEntrainment(List<Entrainment> entrainment) {
        Entrainment = entrainment;
    }

// Creation Methods
    public void build(int minutes) {}
    public boolean isBuilt() {return CreatedEntrainmentList != null && CreatedEntrainmentList.size() > 0;}
    public List<Entrainment> getCreatedEntrainment() {return CreatedEntrainmentList;}
    public void reset() {CreatedEntrainmentList = null;}

// Entrainment Subclass
    class Entrainment {
        private File file;
        private String name;
        private Duration duration; // In Seconds

        public Entrainment() {
        }
        public Entrainment(File file) {
            if (file != null) {
                this.file = file;
                this.name = file.getName().substring(0, file.getName().lastIndexOf("."));
                calculateduration();
            }
        }

    // Getters And Setters
        @XmlElement
        public File getFile() {
            return file;
        }
        public void setFile(File file) {
            this.file = file;
        }
        @XmlElement
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        @XmlElement
        public Duration getDuration() {
            return duration;
        }
        public void setDuration(Duration duration) {
            this.duration = duration;
        }
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
            Service<Void> calculatedurationservice = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                MediaPlayer shortplayer = new MediaPlayer(new Media(getFile().toURI().toString()));
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
