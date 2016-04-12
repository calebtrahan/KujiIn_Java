package kujiin.xml;

import kujiin.MainController;
import kujiin.Tools;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement
public class Ambiences {
    @XmlElement(name="Presession")
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
                Tools.gui_showinformationdialog(Root, "Information", "Couldn't Read Ambience XML File", "Check Read File Permissions Of " + Options.AMBIENCEXMLFILE.getAbsolutePath());
            }
        } else {
            for (int i = 0; i < AllAmbiences.size(); i++) {
                Ambience selectedambience = AllAmbiences.get(i);
                selectedambience = new Ambience();
                setcutorelementsAmbience(i, selectedambience);
                getcutorelementsAmbience(i).actual_retrievefromdefaultdirectory(Options.ALLNAMES.get(i));
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
            Tools.gui_showinformationdialog(Root, "Information", "Couldn't Write Ambience XML File", "Check Write File Permissions Of " + Options.AMBIENCEXMLFILE.getAbsolutePath());}
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

}
