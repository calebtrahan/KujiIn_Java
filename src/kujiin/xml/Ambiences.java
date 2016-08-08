package kujiin.xml;

import kujiin.MainController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
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
    @XmlTransient
    private final List<Ambience> AllAmbiences = new ArrayList<>(Arrays.asList(Presession, Rin, Kyo, Toh, Sha, Kai, Jin, Retsu, Zai, Zen, Earth, Air, Fire, Water, Void, Postsession));
    @XmlTransient
    private MainController Root;

    public Ambiences() {}
    public Ambiences(MainController Root) {this.Root = Root; unmarshall();}

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
            } catch (JAXBException ignored) {}
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Ambiences.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Options.AMBIENCEXMLFILE);
        } catch (JAXBException ignored) {Root.dialog_Information("Information", "Couldn't Write Ambience XML File", "Check Write File Permissions Of " + Options.AMBIENCEXMLFILE.getAbsolutePath());}
    }

// Other Methods
    public Ambience getmeditatableAmbience(int index) {
        Ambience ambience = AllAmbiences.get(index);
        if (ambience == null) {return new Ambience();}
        else {return ambience;}
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
