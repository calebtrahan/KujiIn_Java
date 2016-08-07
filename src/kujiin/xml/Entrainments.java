package kujiin.xml;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import kujiin.MainController;

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
    private final List<Entrainment> AllEntrainment = new ArrayList<>(Arrays.asList(Presession, Rin, Kyo, Toh, Sha, Kai, Jin, Retsu, Zai, Zen, Earth, Air, Fire, Water, Void, Postsession));
    private MainController Root;
    @XmlTransient
    private int meditatablecount;
    @XmlTransient
    private static final List<Integer> DURATIONSVARIATIONS = Arrays.asList(1, 5);
    @XmlTransient
    private int variationcount;

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
        if (Options.ENTRAINMENTXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(Entrainments.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                Entrainments entrainments = (Entrainments) createMarshaller.unmarshal(Options.ENTRAINMENTXMLFILE);
                Presession = entrainments.Presession;
                Rin = entrainments.Rin;
                Kyo = entrainments.Kyo;
                Toh = entrainments.Toh;
                Sha = entrainments.Sha;
                Kai = entrainments.Kai;
                Jin = entrainments.Jin;
                Retsu = entrainments.Retsu;
                Zai = entrainments.Zai;
                Zen = entrainments.Zen;
                Earth = entrainments.Earth;
                Air = entrainments.Air;
                Fire = entrainments.Fire;
                Water = entrainments.Water;
                Void = entrainments.Void;
                Postsession = entrainments.Postsession;
            } catch (JAXBException e) {
                e.printStackTrace();
                Root.dialog_Information("Information", "Couldn't Read Entrainment XML File", "Check Read File Permissions Of " + Options.ENTRAINMENTXMLFILE.getAbsolutePath());
            }
        } else {populateentrainmentdurations();}
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Entrainments.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Options.ENTRAINMENTXMLFILE);
        } catch (JAXBException e) {
            e.printStackTrace();
            Root.dialog_Information("Information", "Couldn't Write Entrainment XML File", "Check Write File Permissions Of " + Options.ENTRAINMENTXMLFILE.getAbsolutePath());
        }
    }
    public void populateentrainmentdurations() {
        // Calculate File With Variation
        String meditatablename;
        int variation;
        Entrainment selectedentrainment;
        try {
            selectedentrainment = getmeditatableEntrainment(meditatablecount);
            if (meditatablecount > 9 && meditatablecount < 15) {meditatablename = "ELEMENT";}
            else {meditatablename = Root.getSession().getAllMeditatables_Names().get(meditatablecount).toUpperCase();}
            try {variation = DURATIONSVARIATIONS.get(variationcount); variationcount++;}
            catch (IndexOutOfBoundsException e) {
                variation = 0;
                variationcount = 0;
                meditatablecount++;
                populateentrainmentdurations();
            }
        } catch (IndexOutOfBoundsException ignored) {
            return;
            // End Calculation
        }
        File actualfile = new File(Options.DIRECTORYENTRAINMENT, "entrainment/" + meditatablename + variation + ".mp3");
        if (actualfile.exists()) {
            MediaPlayer mediaPlayer = new MediaPlayer(new Media(actualfile.toURI().toString()));
            mediaPlayer.setOnReady(() -> {
                SoundFile soundFile = new SoundFile(actualfile);
                soundFile.setDuration(mediaPlayer.getTotalDuration().toMillis());
                if (variationcount == 0) {selectedentrainment.setFreqshort(soundFile);
                } else if (variationcount == 1) {selectedentrainment.setFreqlong(soundFile);}
                mediaPlayer.dispose();
                populateentrainmentdurations();
            });
        }
    }

// Other Methods
    public Entrainment getmeditatableEntrainment(int index) {
        switch (index) {
            case 0: return Presession;
            case 1: return Rin;
            case 2: return Kyo;
            case 3: return Toh;
            case 4: return Sha;
            case 5: return Kai;
            case 6: return Jin;
            case 7: return Retsu;
            case 8: return Zai;
            case 9: return Zen;
            case 10: return Earth;
            case 11: return Air;
            case 12: return Fire;
            case 13: return Water;
            case 14: return Void;
            case 15: return Postsession;
            default: return null;
        }
    }
    public void setmeditatableEntrainment(int index, Entrainment entrainment) {
        switch (index) {
            case 0: Presession = entrainment; break;
            case 1: Rin = entrainment; break;
            case 2: Kyo = entrainment; break;
            case 3: Toh = entrainment; break;
            case 4: Sha = entrainment; break;
            case 5: Kai = entrainment; break;
            case 6: Jin = entrainment; break;
            case 7: Retsu = entrainment; break;
            case 8: Zai = entrainment; break;
            case 9: Zen = entrainment; break;
            case 10: Earth = entrainment; break;
            case 11: Air = entrainment; break;
            case 12: Fire = entrainment; break;
            case 13: Water = entrainment; break;
            case 14: Void = entrainment; break;
            case 15: Postsession = entrainment; break;
        }
    }

}
