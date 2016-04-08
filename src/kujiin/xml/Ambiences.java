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
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Ambiences {
    private Ambience Presession;
    private Ambience Rin;
    private Ambience Kyo;
    private Ambience Toh;
    private Ambience Sha;
    private Ambience Kai;
    private Ambience Jin;
    private Ambience Retsu;
    private Ambience Zai;
    private Ambience Zen;
    private Ambience Earth;
    private Ambience Air;
    private Ambience Fire;
    private Ambience Water;
    private Ambience Void;
    private Ambience Postsession;
    private MainController Root;

    public Ambiences() {}
    public Ambiences(MainController Root) {this.Root = Root;}

// Getters And Setters
    public Ambience getPresession() {
        return Presession;
    }
    public void setPresession(Ambience presession) {
        Presession = presession;
    }
    public Ambience getRin() {
        return Rin;
    }
    public void setRin(Ambience rin) {
        Rin = rin;
    }
    public Ambience getKyo() {
        return Kyo;
    }
    public void setKyo(Ambience kyo) {
        Kyo = kyo;
    }
    public Ambience getToh() {
        return Toh;
    }
    public void setToh(Ambience toh) {
        Toh = toh;
    }
    public Ambience getSha() {
        return Sha;
    }
    public void setSha(Ambience sha) {
        Sha = sha;
    }
    public Ambience getKai() {
        return Kai;
    }
    public void setKai(Ambience kai) {
        Kai = kai;
    }
    public Ambience getJin() {
        return Jin;
    }
    public void setJin(Ambience jin) {
        Jin = jin;
    }
    public Ambience getRetsu() {
        return Retsu;
    }
    public void setRetsu(Ambience retsu) {
        Retsu = retsu;
    }
    public Ambience getZai() {
        return Zai;
    }
    public void setZai(Ambience zai) {
        Zai = zai;
    }
    public Ambience getZen() {
        return Zen;
    }
    public void setZen(Ambience zen) {
        Zen = zen;
    }
    public Ambience getEarth() {
        return Earth;
    }
    public void setEarth(Ambience earth) {
        Earth = earth;
    }
    public Ambience getAir() {
        return Air;
    }
    public void setAir(Ambience air) {
        Air = air;
    }
    public Ambience getFire() {
        return Fire;
    }
    public void setFire(Ambience fire) {
        Fire = fire;
    }
    public Ambience getWater() {
        return Water;
    }
    public void setWater(Ambience water) {
        Water = water;
    }
    public Ambience getVoid() {
        return Void;
    }
    public void setVoid(Ambience aVoid) {
        Void = aVoid;
    }
    public Ambience getPostsession() {
        return Postsession;
    }
    public void setPostsession(Ambience postsession) {
        Postsession = postsession;
    }

// XML Processing
    public void unmarshall() {
    if (Options.SESSIONSXMLFILE.exists()) {
        try {
            JAXBContext context = JAXBContext.newInstance(Ambiences.class);
            Unmarshaller createMarshaller = context.createUnmarshaller();
            Ambiences ambiences = (Ambiences) createMarshaller.unmarshal(Options.AMBIENCEXMLFILE);
            setPresession(ambiences.getPresession());
            setRin(ambiences.getRin());
            setKyo(ambiences.getKyo());
            setToh(ambiences.getToh());
            setSha(ambiences.getSha());
            setKai(ambiences.getKai());
            setJin(ambiences.getJin());
            setRetsu(ambiences.getRetsu());
            setZai(ambiences.getZai());
            setZen(ambiences.getZen());
            setEarth(ambiences.getEarth());
            setAir(ambiences.getAir());
            setFire(ambiences.getFire());
            setWater(ambiences.getWater());
            setVoid(ambiences.getVoid());
            setPostsession(ambiences.getPostsession());
        } catch (JAXBException e) {
            Tools.gui_showinformationdialog(Root, "Information", "Couldn't Read Ambience XML File", "Check Read File Permissions Of " + Options.AMBIENCEXMLFILE.getAbsolutePath());
        }
    }
}
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Ambiences.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Options.AMBIENCEXMLFILE);
        } catch (JAXBException e) {Tools.gui_showinformationdialog(Root, "Information", "Couldn't Write Ambience XML File", "Check Write File Permissions Of " + Options.AMBIENCEXMLFILE.getAbsolutePath());}
    }

    // Other Methods
    public Ambience getcutorelementsAmbience(int index) {
        if (index == 0) {return Presession;}
        else if (index == 1) {return Rin;}
        else if (index == 2) {return Kyo;}
        else if (index == 3) {return Toh;}
        else if (index == 4) {return Sha;}
        else if (index == 5) {return Kai;}
        else if (index == 6) {return Jin;}
        else if (index == 7) {return Retsu;}
        else if (index == 8) {return Zai;}
        else if (index == 9) {return Zen;}
        else if (index == 10) {return Earth;}
        else if (index == 11) {return Air;}
        else if (index == 12) {return Fire;}
        else if (index == 13) {return Water;}
        else if (index == 14) {return Void;}
        else if (index == 15) {return Postsession;}
        else {return null;}
    }
    public void setcutorelementsAmbience(int index, Ambience ambience) {
        if (index == 0) {Presession = ambience;}
        else if (index == 1) {Rin = ambience;}
        else if (index == 2) {Kyo = ambience;}
        else if (index == 3) {Toh = ambience;}
        else if (index == 4) {Sha = ambience;}
        else if (index == 5) {Kai = ambience;}
        else if (index == 6) {Jin = ambience;}
        else if (index == 7) {Retsu = ambience;}
        else if (index == 8) {Zai = ambience;}
        else if (index == 9) {Zen = ambience;}
        else if (index == 10) {Earth = ambience;}
        else if (index == 11) {Air = ambience;}
        else if (index == 12) {Fire = ambience;}
        else if (index == 13) {Water = ambience;}
        else if (index == 14) {Void = ambience;}
        else if (index == 15) {Postsession = ambience;}
    }

// Ambience Subclass
    @XmlAccessorType(XmlAccessType.PROPERTY)
    public class Ambience {
        private List<SoundFile> Ambience;
        private List<SoundFile> CreatedAmbience;

    // Getters And Setters
        public List<SoundFile> getAmbience() {
            return Ambience;
        }
        public void setAmbience(List<SoundFile> ambience) {
            this.Ambience = ambience;
        }

    // Ambience Editing Methods
        // Actual Ambience
        public void actual_retrievefromdefaultdirectory(String name) {
            for (File i : new File(Options.DIRECTORYAMBIENCE, name).listFiles()) {
                actual_addfromfile(i);}
        }
        private boolean actual_addfromfile(File file) {
            if (Tools.audio_isValid(file)) {
                SoundFile tempfile = new SoundFile(file);
                if (!ambienceexistsinActual(tempfile)) {
                    actual_add(tempfile); return true;}
                else {return false;}
            } else {return false;}
        }
        private void actual_initialize() {if (Ambience == null) Ambience = new ArrayList<>();}
        public void actual_add(SoundFile soundFile) {
            actual_initialize(); Ambience.add(soundFile);}
        public void actual_add(int index, SoundFile soundFile) {
            actual_initialize(); Ambience.add(index, soundFile);}
        public void actual_add(List<SoundFile> soundFiles) {
            actual_initialize(); Ambience.addAll(soundFiles);}
        public SoundFile actual_get(int index) {return Ambience.get(index);}
        public SoundFile actual_get(String name) {
            for (SoundFile i : getAmbience()) {
                if (i.getName().equals(name)) return i;
            }
            return null;
        }
        public SoundFile actual_get(File file) {
            for (SoundFile i : getAmbience()) {
                if (i.getFile().equals(file)) return i;
            }
            return null;
        }
        public void actual_remove(SoundFile soundFile) {Ambience.remove(soundFile);}
        public void actual_remove(int index) {Ambience.remove(index);}
        // Created Ambience
        private void created_initialize() {if (CreatedAmbience == null) CreatedAmbience = new ArrayList<>();}
        public void created_add(SoundFile soundFile) {
            created_initialize(); CreatedAmbience.add(soundFile);}
        public void created_add(List<SoundFile> soundFiles) {
            created_initialize(); CreatedAmbience.addAll(soundFiles);}
        public SoundFile created_get(int index) {return CreatedAmbience.get(index);}
        public SoundFile created_get(String name) {
            for (SoundFile i : CreatedAmbience) {
                if (i.getName().equals(name)) return i;
            }
            return null;
        }
        public SoundFile created_get(File file) {
            for (SoundFile i : CreatedAmbience) {
                if (i.getFile().equals(file)) return i;
            }
            return null;
        }
        public List<SoundFile> created_getAll() {return CreatedAmbience;}
        public void created_remove(SoundFile soundFile) {CreatedAmbience.remove(soundFile);}
        public void created_remove(int index) {CreatedAmbience.remove(index);}
        public void created_clear() {CreatedAmbience.clear();}

    // Validation Methods
        public boolean ambienceexistsinActual(SoundFile soundFile) {return Ambience.contains(soundFile);}
        public boolean ambienceexistsinCreated(SoundFile soundFile) {return CreatedAmbience.contains(soundFile);}
        public boolean hasEnoughActualAmbience() {return false;}

    // Information Methods
        public Duration gettotalActualDuration() {
            Duration duration = new Duration(0.0);
            for (SoundFile i : Ambience) {duration.add(i.getDuration());}
            return duration;
        }
        public Duration gettotalCreatedDuration() {
            Duration duration = new Duration(0.0);
            for (SoundFile i : CreatedAmbience) {duration.add(i.getDuration());}
            return duration;
        }

    // Playback Methods
    }
}
