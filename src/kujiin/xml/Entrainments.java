package kujiin.xml;

import javafx.util.Duration;
import kujiin.MainController;
import kujiin.Tools;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.File;
import java.util.Collections;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Entrainments {
    private Entrainment Presession;
    private Entrainment Rin;
    private Entrainment Kyo;
    private Entrainment Toh;
    private Entrainment Sha;
    private Entrainment Kai;
    private Entrainment Jin;
    private Entrainment Retsu;
    private Entrainment Zai;
    private Entrainment Zen;
    private Entrainment Earth;
    private Entrainment Air;
    private Entrainment Fire;
    private Entrainment Water;
    private Entrainment Void;
    private Entrainment Postsession;
    private MainController Root;

    public Entrainments() {}
    public Entrainments(MainController Root) {this.Root = Root;}

// Getters And Setters
    public Entrainment getPresession() {
        return Presession;
    }
    public void setPresession(Entrainment presession) {
        Presession = presession;
    }
    public Entrainment getRin() {
        return Rin;
    }
    public void setRin(Entrainment rin) {
        Rin = rin;
    }
    public Entrainment getKyo() {
        return Kyo;
    }
    public void setKyo(Entrainment kyo) {
        Kyo = kyo;
    }
    public Entrainment getToh() {
        return Toh;
    }
    public void setToh(Entrainment toh) {
        Toh = toh;
    }
    public Entrainment getSha() {
        return Sha;
    }
    public void setSha(Entrainment sha) {
        Sha = sha;
    }
    public Entrainment getKai() {
        return Kai;
    }
    public void setKai(Entrainment kai) {
        Kai = kai;
    }
    public Entrainment getJin() {
        return Jin;
    }
    public void setJin(Entrainment jin) {
        Jin = jin;
    }
    public Entrainment getRetsu() {
        return Retsu;
    }
    public void setRetsu(Entrainment retsu) {
        Retsu = retsu;
    }
    public Entrainment getZai() {
        return Zai;
    }
    public void setZai(Entrainment zai) {
        Zai = zai;
    }
    public Entrainment getZen() {
        return Zen;
    }
    public void setZen(Entrainment zen) {
        Zen = zen;
    }
    public Entrainment getEarth() {
        return Earth;
    }
    public void setEarth(Entrainment earth) {
        Earth = earth;
    }
    public Entrainment getAir() {
        return Air;
    }
    public void setAir(Entrainment air) {
        Air = air;
    }
    public Entrainment getFire() {
        return Fire;
    }
    public void setFire(Entrainment fire) {
        Fire = fire;
    }
    public Entrainment getWater() {
        return Water;
    }
    public void setWater(Entrainment water) {
        Water = water;
    }
    public Entrainment getVoid() {
        return Void;
    }
    public void setVoid(Entrainment aVoid) {
        Void = aVoid;
    }
    public Entrainment getPostsession() {
        return Postsession;
    }
    public void setPostsession(Entrainment postsession) {
        Postsession = postsession;
    }

// XML Processing
    public void unmarshall() {
        if (Options.SESSIONSXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(Entrainments.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                Entrainments entrainments = (Entrainments) createMarshaller.unmarshal(Options.ENTRAINMENTXMLFILE);
                setPresession(entrainments.getPresession());
                setRin(entrainments.getRin());
                setKyo(entrainments.getKyo());
                setToh(entrainments.getToh());
                setSha(entrainments.getSha());
                setKai(entrainments.getKai());
                setJin(entrainments.getJin());
                setRetsu(entrainments.getRetsu());
                setZai(entrainments.getZai());
                setZen(entrainments.getZen());
                setEarth(entrainments.getEarth());
                setAir(entrainments.getAir());
                setFire(entrainments.getFire());
                setWater(entrainments.getWater());
                setVoid(entrainments.getVoid());
                setPostsession(entrainments.getPostsession());
            } catch (JAXBException e) {
                Tools.gui_showinformationdialog(Root, "Information", "Couldn't Read Entrainment XML File", "Check Read File Permissions Of " + Options.ENTRAINMENTXMLFILE.getAbsolutePath());
            }
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Entrainments.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Options.ENTRAINMENTXMLFILE);
        } catch (JAXBException e) {Tools.gui_showinformationdialog(Root, "Information", "Couldn't Write Entrainment XML File", "Check Write File Permissions Of " + Options.ENTRAINMENTXMLFILE.getAbsolutePath());}
    }

// Other Methods
    public Entrainment getcutorelementsEntrainment(int index) {
        if (index == 0) {return getPresession();}
        else if (index == 1) {return getRin();}
        else if (index == 2) {return getKyo();}
        else if (index == 3) {return getToh();}
        else if (index == 4) {return getSha();}
        else if (index == 5) {return getKai();}
        else if (index == 6) {return getJin();}
        else if (index == 7) {return getRetsu();}
        else if (index == 8) {return getZai();}
        else if (index == 9) {return getZen();}
        else if (index == 10) {return getEarth();}
        else if (index == 11) {return getAir();}
        else if (index == 12) {return getFire();}
        else if (index == 13) {return getWater();}
        else if (index == 14) {return getVoid();}
        else if (index == 15) {return getPostsession();}
        else {return null;}
    }
    public void setcutorelementsEntrainment(int index, Entrainment entrainment) {
        if (index == 0) {setPresession(entrainment);}
        else if (index == 1) {setRin(entrainment);}
        else if (index == 2) {setKyo(entrainment);}
        else if (index == 3) {setToh(entrainment);}
        else if (index == 4) {setSha(entrainment);}
        else if (index == 5) {setKai(entrainment);}
        else if (index == 6) {setJin(entrainment);}
        else if (index == 7) {setRetsu(entrainment);}
        else if (index == 8) {setZai(entrainment);}
        else if (index == 9) {setZen(entrainment);}
        else if (index == 10) {setEarth(entrainment);}
        else if (index == 11) {setAir(entrainment);}
        else if (index == 12) {setFire(entrainment);}
        else if (index == 13) {setWater(entrainment);}
        else if (index == 14) {setVoid(entrainment);}
        else if (index == 15) {setPostsession(entrainment);}
    }

    // Entrainment Subclass
    @XmlAccessorType(XmlAccessType.PROPERTY)
    public class Entrainment {
        private SoundFile rampinfile;
        private SoundFile freqshort;
        private SoundFile freqlong;
        private SoundFile rampoutfile;
        private List<SoundFile> CreatedEntrainment;

        public Entrainment() {}

    // Getters And Setters
        public SoundFile getRampinfile() {
            return rampinfile;
        }
        public void setRampinfile(SoundFile rampinfile) {
            this.rampinfile = rampinfile;
        }
        public SoundFile getFreqshort() {
            return freqshort;
        }
        public void setFreqshort(SoundFile freqshort) {
            this.freqshort = freqshort;
        }
        public SoundFile getFreqlong() {
            return freqlong;
        }
        public void setFreqlong(SoundFile freqlong) {
            this.freqlong = freqlong;
        }
        public SoundFile getRampoutfile() {
            return rampoutfile;
        }
        public void setRampoutfile(SoundFile rampoutfile) {
            this.rampoutfile = rampoutfile;
        }

    // Add/Remove Created Entrainment
        public void addcreated(SoundFile soundFile) {CreatedEntrainment.add(soundFile);}
        public void addcreated(int index, SoundFile soundFile) {CreatedEntrainment.add(soundFile);}
        public void addcreated(List<SoundFile> soundFiles) {CreatedEntrainment.addAll(soundFiles);}
        public SoundFile getcreated(int index) {return CreatedEntrainment.get(index);}
        public SoundFile getcreated(String name) {
            for (SoundFile i : CreatedEntrainment) {
                if (i.getName().equals(name)) return i;
            }
            return null;
        }
        public SoundFile getcreated(File file) {
            for (SoundFile i : CreatedEntrainment) {
                if (i.getFile().equals(file)) return i;
            }
            return null;
        }
        public List<SoundFile> getAllCreated() {return CreatedEntrainment;}
        public void removecreated(SoundFile soundFile) {CreatedEntrainment.remove(soundFile);}
        public void removecreated(int index) {CreatedEntrainment.remove(index);}
        public void clearcreated() {CreatedEntrainment.clear();}

    // Information Methods
        public Duration gettotalCreatedDuration() {
            Duration duration = new Duration(0.0);
            for (SoundFile i : CreatedEntrainment) {duration.add(i.getDuration());}
            return duration;
        }

    // Other Methods
        public void shuffleCreated() {Collections.shuffle(CreatedEntrainment);}
    }
}
