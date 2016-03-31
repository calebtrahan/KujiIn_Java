package kujiin.xml;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.Cut;
import kujiin.Qi_Gong;
import kujiin.Tools;
import kujiin.widgets.Meditatable;

import javax.xml.bind.Element;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.File;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Entrainments {
    private Entrainment rampinfile;
    private Entrainment freqshort;
    private Entrainment freqlong;
    private Entrainment rampoutfile;
    @XmlElement(name = "Entrainment")
    private List<Entrainment> Entrainment;
    private List<Entrainment> CreatedEntrainment;
    private Meditatable cutorelement;
    private File directoryrampin;
    private File directoryrampout;

    public Entrainments() {}
    public Entrainments(Meditatable cutorelement, File freqshort, File freqlong) {
        this.cutorelement = cutorelement;
        if (cutorelement instanceof Element) {
            directoryrampin = new File(Options.DIRECTORYELEMENTRAMP, "in/");
            directoryrampout = new File(Options.DIRECTORYELEMENTRAMP, "out/");
        } else if (cutorelement instanceof Cut && cutorelement.number == 3) {
            directoryrampin = Options.DIRECTORYTOHRAMP;
            directoryrampout = Options.DIRECTORYTOHRAMP;
        } else if (cutorelement instanceof Qi_Gong) {
            directoryrampin = Options.DIRECTORYRAMPUP;
            directoryrampout = Options.DIRECTORYRAMPDOWN;
        }
        if (freqshort != null && freqshort.exists()) {this.freqshort = new Entrainment(freqshort);}
        if (freqlong != null && freqlong.exists()) {this.freqlong = new Entrainment(freqlong);}
    }
    public Entrainments(File freqshort, File freqlong, List<Entrainment> entrainments) {
        if (freqshort != null && freqshort.exists()) {this.freqshort = new Entrainment(freqshort);}
        if (freqlong != null && freqlong.exists()) {this.freqlong = new Entrainment(freqlong);}
        CreatedEntrainment = entrainments;
    }

// Getters And Setters For XML
    public List<Entrainment> getEntrainment() {
        return Entrainment;
    }
    public void setEntrainment(List<Entrainment> entrainment) {
        Entrainment = entrainment;
    }
    public List<Entrainment> getCreatedEntrainment() {return CreatedEntrainment;}
    public void setCreatedEntrainment(List<Entrainments.Entrainment> createdEntrainment) {
        CreatedEntrainment = createdEntrainment;
    }

// Valid Entrainment Methods
    public boolean hasFreqs() {return freqshort != null && freqlong != null && freqshort.getFile().exists() && freqlong.getFile().exists();}
    public boolean hasRamp() {
        return rampinfile != null && rampoutfile != null && rampinfile.getFile().exists() && rampoutfile.getFile().exists();
    }

// Creation Methods
    public void addtoCreated(int index, File file) {CreatedEntrainment.add(index, new Entrainment(file));}
    public void addtoCreated(File file) {CreatedEntrainment.add(new Entrainment(file));}
    public boolean build(int durationinminutes, List<Meditatable> allcutsorelementstoplay) {
        if (hasFreqs()) {
            int index = allcutsorelementstoplay.indexOf(cutorelement);
            Meditatable cutorelementbefore = null;
            Meditatable cutorelementafter = null;
            if (index != 0) {cutorelementbefore = allcutsorelementstoplay.get(index - 1);}
            if (index != allcutsorelementstoplay.size() - 1) {cutorelementafter = allcutsorelementstoplay.get(index + 1);}
            if (cutorelementbefore != null || cutorelementafter != null) {
                int rampduration;
                if (durationinminutes > 13) {rampduration = 3;}
                else if (durationinminutes > 8) {rampduration = 2;}
                else {rampduration = 1;}
                if (cutorelementbefore != null) {
                    rampinfile = new Entrainment(new File(directoryrampin, cutorelementbefore.name.toLowerCase() + rampduration + ".mp3"));
                    durationinminutes -= rampduration;
                }
                if (cutorelementafter != null) {
                    rampoutfile = new Entrainment(new File(directoryrampout, cutorelementafter.name.toLowerCase() + rampduration + ".mp3"));
                    durationinminutes -= rampduration;
                }
            }
            int longtimes = (int) Math.ceil(durationinminutes / freqlong.getDurationinMinutes());
            int shorttimes = (int) Math.ceil(durationinminutes % freqshort.getDurationinMinutes());
            for (int i = 0; i < longtimes; i++) {CreatedEntrainment.add(freqlong);}
            for (int i = 0; i < shorttimes; i++) {CreatedEntrainment.add(freqshort);}
            Tools.list_shuffle(CreatedEntrainment, 5);
            if (rampinfile != null) {CreatedEntrainment.add(0, rampinfile);}
            if (rampoutfile != null) {CreatedEntrainment.add(rampoutfile);}
            return getCreatedEntrainment().size() > 0 && getTotalCreatedEntrainmentDuration().toMinutes() >= durationinminutes;
        } else {return false;}
    }
    public boolean isBuilt() {return CreatedEntrainment != null && CreatedEntrainment.size() > 0;}
    public void reset() {
        CreatedEntrainment = null;}
    public Entrainment getSelectedEntrainment(int index) {return getEntrainment().get(index);}
//
    public Duration getTotalEntrainmentDuration() {
        Duration duration = new Duration(0.0);
        for (Entrainment i : getEntrainment()) {duration.add(i.getDuration());}
        return duration;
    }
    public Duration getTotalCreatedEntrainmentDuration() {
        Duration duration = new Duration(0.0);
        for (Entrainment i : getCreatedEntrainment()) {duration.add(i.getDuration());}
        return duration;
    }

// Creation Methods

// Entrainment Subclass
    public class Entrainment {
        private File file;
        private String name;
        private Duration duration;
        private Media media;

        public Entrainment() {
        }
        public Entrainment(File file) {
            if (file != null) {
                this.file = file;
                this.name = file.getName().substring(0, file.getName().lastIndexOf("."));
                this.media = new Media(this.file.toURI().toString());
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
        @XmlElement
        public Media getMedia() {
            return media;
        }
        public void setMedia(Media media) {
            this.media = media;
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
