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
                Root.displayDialog_Information("Information", "Couldn't Read Entrainment XML File", "Check Read File Permissions Of " + Options.ENTRAINMENTXMLFILE.getAbsolutePath());
            }
        } else {
            for (int i = 0; i < AllEntrainment.size(); i++) {
                Entrainment selectedentrainment = AllEntrainment.get(i);
                selectedentrainment = new Entrainment();
                setmeditatableEntrainment(i, selectedentrainment);
            }
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Entrainments.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Options.ENTRAINMENTXMLFILE);
        } catch (JAXBException e) {
            e.printStackTrace();
            Root.displayDialog_Information("Information", "Couldn't Write Entrainment XML File", "Check Write File Permissions Of " + Options.ENTRAINMENTXMLFILE.getAbsolutePath());
        }
    }

// Other Methods
    public Entrainment getmeditatableEntrainment(int index) {
        switch (index) {
            case 0:
                return getPresession();
            case 1:
                return getRin();
            case 2:
                return getKyo();
            case 3:
                return getToh();
            case 4:
                return getSha();
            case 5:
                return getKai();
            case 6:
                return getJin();
            case 7:
                return getRetsu();
            case 8:
                return getZai();
            case 9:
                return getZen();
            case 10:
                return getEarth();
            case 11:
                return getAir();
            case 12:
                return getFire();
            case 13:
                return getWater();
            case 14:
                return getVoid();
            case 15:
                return getPostsession();
            default:
                return null;
        }
    }
    public void setmeditatableEntrainment(int index, Entrainment entrainment) {
        switch (index) {
            case 0:
                setPresession(entrainment);
                break;
            case 1:
                setRin(entrainment);
                break;
            case 2:
                setKyo(entrainment);
                break;
            case 3:
                setToh(entrainment);
                break;
            case 4:
                setSha(entrainment);
                break;
            case 5:
                setKai(entrainment);
                break;
            case 6:
                setJin(entrainment);
                break;
            case 7:
                setRetsu(entrainment);
                break;
            case 8:
                setZai(entrainment);
                break;
            case 9:
                setZen(entrainment);
                break;
            case 10:
                setEarth(entrainment);
                break;
            case 11:
                setAir(entrainment);
                break;
            case 12:
                setFire(entrainment);
                break;
            case 13:
                setWater(entrainment);
                break;
            case 14:
                setVoid(entrainment);
                break;
            case 15:
                setPostsession(entrainment);
                break;
        }
    }
}
