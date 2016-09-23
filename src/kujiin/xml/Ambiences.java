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
        } catch (JAXBException ignored) {
            Root.dialog_displayInformation("Information", "Couldn't Write Ambience XML File", "Check Write File Permissions Of " + Options.AMBIENCEXMLFILE.getAbsolutePath());}
    }

// Other Methods
    public Ambience getsessionpartAmbience(int index) {
        switch (index) {
            case 0:
                if (Presession != null) {return Presession;} else {return new Ambience();}
            case 1:
                if (Rin != null) {return Rin;} else {return new Ambience();}
            case 2:
                if (Kyo != null) {return Kyo;} else {return new Ambience();}
            case 3:
                if (Toh != null) {return Toh;} else {return new Ambience();}
            case 4:
                if (Sha != null) {return Sha;} else {return new Ambience();}
            case 5:
                if (Kai != null) {return Kai;} else {return new Ambience();}
            case 6:
                if (Jin != null) {return Jin;} else {return new Ambience();}
            case 7:
                if (Retsu != null) {return Retsu;} else {return new Ambience();}
            case 8:
                if (Zai != null) {return Zai;} else {return new Ambience();}
            case 9:
                if (Zen != null) {return Zen;} else {return new Ambience();}
            case 10:
                if (Earth != null) {return Earth;} else {return new Ambience();}
            case 11:
                if (Air != null) {return Air;} else {return new Ambience();}
            case 12:
                if (Fire != null) {return Fire;} else {return new Ambience();}
            case 13:
                if (Water != null) {return Water;} else {return new Ambience();}
            case 14:
                if (Void != null) {return Void;} else {return new Ambience();}
            case 15:
                if (Postsession != null) {return Postsession;} else {return new Ambience();}
            default:
                return null;
        }
    }
    public void setsessionpartAmbience(int index, Ambience ambience) {
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
