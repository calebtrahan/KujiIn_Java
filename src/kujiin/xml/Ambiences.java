package kujiin.xml;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import kujiin.MainController;
import kujiin.util.Util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement
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
    private final List<Ambience> AllAmbiences = new ArrayList<>(Arrays.asList(Presession, Rin, Kyo, Toh, Sha, Kai, Jin, Retsu, Zai, Zen, Earth, Air, Fire, Water, Void, Postsession));
    private MainController Root;
    @XmlTransient
    private int meditatablecount;
    @XmlTransient
    private ArrayList<File> soundfilestocalculateduration = new ArrayList<>();
    @XmlTransient
    private int soundfilescount;
    private String meditatablename;
    private File ambiencedirectory;
    private Ambience selectedambience;
    private MediaPlayer calculateplayer;


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
        if (Options.AMBIENCEXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(Ambiences.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                Ambiences ambiences = (Ambiences) createMarshaller.unmarshal(Options.AMBIENCEXMLFILE);
                Presession = ambiences.Presession;
                Rin = ambiences.Rin;
                Kyo = ambiences.Kyo;
                Toh = ambiences.Toh;
                Sha = ambiences.Sha;
                Kai = ambiences.Kai;
                Jin = ambiences.Jin;
                Retsu = ambiences.Retsu;
                Zai = ambiences.Zai;
                Zen = ambiences.Zen;
                Earth = ambiences.Earth;
                Air = ambiences.Air;
                Fire = ambiences.Fire;
                Water = ambiences.Water;
                Void = ambiences.Void;
                Postsession = ambiences.Postsession;
            } catch (JAXBException e) {Root.dialog_Information("Information", "Couldn't Read Ambience XML File", "Check Read File Permissions Of " + Options.AMBIENCEXMLFILE.getAbsolutePath());}
        } else {populateambiencedurations();}
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Ambiences.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Options.AMBIENCEXMLFILE);
        } catch (JAXBException ignored) {Root.dialog_Information("Information", "Couldn't Write Ambience XML File", "Check Write File Permissions Of " + Options.AMBIENCEXMLFILE.getAbsolutePath());}
    }
    public void populateambiencedurations() {
        Service<Void> populateambienceservice = new Service<java.lang.Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<java.lang.Void>() {
                    @Override
                    protected java.lang.Void call() throws Exception {
                        if (soundfilestocalculateduration.isEmpty()) {
                            try {
                                selectedambience = getmeditatableAmbience(meditatablecount);
                                if (selectedambience == null) {selectedambience = new Ambience();}
                                if (meditatablecount == 0) {meditatablename = "Presession";}
                                else if (meditatablecount == 15) {meditatablename = "Postsession";}
                                else {meditatablename = Root.getSession().getAllMeditatables_Names().get(meditatablecount).toUpperCase();}
                                updateMessage("Starting Scan For Ambience " + meditatablename);
                                ambiencedirectory = new File(Options.DIRECTORYAMBIENCE, meditatablename);
                                try {
                                    for (File i : ambiencedirectory.listFiles()) {
                                        for (String x : Util.SUPPORTEDAUDIOFORMATS) {
                                            if (i.getName().endsWith(x)) {soundfilestocalculateduration.add(i); break;}
                                        }
                                    }
                                    soundfilescount = 0;
                                } catch (NullPointerException ignored) {
                                    meditatablecount++;
                                    soundfilescount = 0;
                                }
                                try {call();} catch (Exception e) {e.printStackTrace();}
                            } catch (IndexOutOfBoundsException e) {return null;}
                        } else {
                            try {
                                File actualfile = soundfilestocalculateduration.get(soundfilescount);
                                calculateplayer = new MediaPlayer(new Media(actualfile.toURI().toString()));
                                calculateplayer.setOnReady(() -> {
                                    updateMessage("Scanning Directories For Ambience" + meditatablename + " (" + soundfilescount + 1 + "/" + soundfilestocalculateduration.size() + ")");
                                    updateProgress(soundfilescount, soundfilestocalculateduration.size() - 1);
                                    SoundFile soundFile = new SoundFile(actualfile);
                                    soundFile.setDuration(calculateplayer.getTotalDuration().toMillis());
                                    selectedambience.actual_add(meditatablecount, soundFile);
                                    soundfilescount++;
                                    calculateplayer.dispose();
                                    try {call();} catch (Exception e) {e.printStackTrace();}
                                });
                                calculateplayer.setOnError(() -> {
                                    soundfilescount++;
                                    calculateplayer.dispose();
                                    try {call();} catch (Exception e) {e.printStackTrace();}
                                });
                            } catch (IndexOutOfBoundsException ignored) {
                                soundfilestocalculateduration.clear();
                                setmeditatableAmbience(meditatablecount, selectedambience);
                                meditatablecount++;
                                try {call();} catch (Exception e) {e.printStackTrace();}
                            }
                        }
                        return null;
                    }
                };
            }
        };
        populateambienceservice.setOnRunning(event -> Root.CreatorStatusBar.textProperty().bind(populateambienceservice.messageProperty()));
        populateambienceservice.setOnSucceeded(event -> Root.CreatorStatusBar.textProperty().unbind());
        populateambienceservice.start();
    }

// Other Methods
    public Ambience getmeditatableAmbience(int index) {
        switch (index) {
            case 0:
                return Presession;
            case 1:
                return Rin;
            case 2:
                return Kyo;
            case 3:
                return Toh;
            case 4:
                return Sha;
            case 5:
                return Kai;
            case 6:
                return Jin;
            case 7:
                return Retsu;
            case 8:
                return Zai;
            case 9:
                return Zen;
            case 10:
                return Earth;
            case 11:
                return Air;
            case 12:
                return Fire;
            case 13:
                return Water;
            case 14:
                return Void;
            case 15:
                return Postsession;
            default:
                return null;
        }
    }
    public void setmeditatableAmbience(int index, Ambience ambience) {
        switch (index) {
            case 0:
                Presession = ambience;
                break;
            case 1:
                Rin = ambience;
                break;
            case 2:
                Kyo = ambience;
                break;
            case 3:
                Toh = ambience;
                break;
            case 4:
                Sha = ambience;
                break;
            case 5:
                Kai = ambience;
                break;
            case 6:
                Jin = ambience;
                break;
            case 7:
                Retsu = ambience;
                break;
            case 8:
                Zai = ambience;
                break;
            case 9:
                Zen = ambience;
                break;
            case 10:
                Earth = ambience;
                break;
            case 11:
                Air = ambience;
                break;
            case 12:
                Fire = ambience;
                break;
            case 13:
                Water = ambience;
                break;
            case 14:
                Void = ambience;
                break;
            case 15:
                Postsession = ambience;
                break;
        }
    }
    public void getSessionValues() {
        for (int i = 0; i < 15; i++) {
            for (SoundFile x : getmeditatableAmbience(i).getAmbience()) {
                System.out.println(x.getName());
            }
        }
    }

}
