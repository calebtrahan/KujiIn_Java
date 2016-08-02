package kujiin.xml;

import kujiin.MainController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
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
                Root.displayDialog_Information("Information", "Couldn't Read Ambience XML File", "Check Read File Permissions Of " + Options.AMBIENCEXMLFILE.getAbsolutePath());
            }
        } else {
            for (int i = 0; i < AllAmbiences.size(); i++) {
                Ambience selectedambience = AllAmbiences.get(i);
                selectedambience = new Ambience();
                setmeditatableAmbience(i, selectedambience);
            }
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Ambiences.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Options.AMBIENCEXMLFILE);
        } catch (JAXBException e) {
            e.printStackTrace();
            Root.displayDialog_Information("Information", "Couldn't Write Ambience XML File", "Check Write File Permissions Of " + Options.AMBIENCEXMLFILE.getAbsolutePath());}
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
}
