package kujiin.xml;

import kujiin.ui.MainController;

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
public class AvailableAmbiences {
    private PlaybackItemAmbience QiGong;
    private PlaybackItemAmbience Rin;
    private PlaybackItemAmbience Kyo;
    private PlaybackItemAmbience Toh;
    private PlaybackItemAmbience Sha;
    private PlaybackItemAmbience Kai;
    private PlaybackItemAmbience Jin;
    private PlaybackItemAmbience Retsu;
    private PlaybackItemAmbience Zai;
    private PlaybackItemAmbience Zen;
    private PlaybackItemAmbience Earth;
    private PlaybackItemAmbience Air;
    private PlaybackItemAmbience Fire;
    private PlaybackItemAmbience Water;
    private PlaybackItemAmbience Void;
    @XmlTransient
    private MainController root;

    public AvailableAmbiences() {}
    public AvailableAmbiences(MainController Root) {
        root = Root;
        unmarshall();
    }

    // XML Processing
    public void unmarshall() {
        if (Preferences.AMBIENCEXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(AvailableAmbiences.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                AvailableAmbiences ambiences = (AvailableAmbiences) createMarshaller.unmarshal(Preferences.AVAILABLEAMBIENCEXMLFILE);
                QiGong = ambiences.QiGong;
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
            } catch (JAXBException ignored) {}
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(AvailableAmbiences.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Preferences.AVAILABLEAMBIENCEXMLFILE);
        } catch (JAXBException ignored) {ignored.printStackTrace();}
    }

    // Other Methods
    public PlaybackItemAmbience getsessionpartAmbience(int index) {
        switch (index) {
            case 0:
                if (QiGong != null) {return QiGong;} else {return new PlaybackItemAmbience();}
            case 1:
                if (Rin != null) {return Rin;} else {return new PlaybackItemAmbience();}
            case 2:
                if (Kyo != null) {return Kyo;} else {return new PlaybackItemAmbience();}
            case 3:
                if (Toh != null) {return Toh;} else {return new PlaybackItemAmbience();}
            case 4:
                if (Sha != null) {return Sha;} else {return new PlaybackItemAmbience();}
            case 5:
                if (Kai != null) {return Kai;} else {return new PlaybackItemAmbience();}
            case 6:
                if (Jin != null) {return Jin;} else {return new PlaybackItemAmbience();}
            case 7:
                if (Retsu != null) {return Retsu;} else {return new PlaybackItemAmbience();}
            case 8:
                if (Zai != null) {return Zai;} else {return new PlaybackItemAmbience();}
            case 9:
                if (Zen != null) {return Zen;} else {return new PlaybackItemAmbience();}
            case 10:
                if (Earth != null) {return Earth;} else {return new PlaybackItemAmbience();}
            case 11:
                if (Air != null) {return Air;} else {return new PlaybackItemAmbience();}
            case 12:
                if (Fire != null) {return Fire;} else {return new PlaybackItemAmbience();}
            case 13:
                if (Water != null) {return Water;} else {return new PlaybackItemAmbience();}
            case 14:
                if (Void != null) {return Void;} else {return new PlaybackItemAmbience();}
            default:
                return null;
        }
    }
    public void setsessionpartAmbience(int index, PlaybackItemAmbience ambience) {
        switch (index) {
            case 0:
                QiGong = ambience;
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
        }
    }

}
