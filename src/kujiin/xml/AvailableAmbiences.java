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
    private MainController Root;

    public AvailableAmbiences() {}
    public AvailableAmbiences(MainController Root) {
        this.Root = Root;
        unmarshall();
    }

// XML Processing
    public void unmarshall() {
        if (Preferences.AVAILABLEAMBIENCEXMLFILE.exists() && System.getProperty("os.name").equals(Root.getPreferences().getAdvancedOptions().getOS())) {
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
            } catch (JAXBException ignored) {ignored.printStackTrace();}
        } else {
            QiGong = new PlaybackItemAmbience("Qi-Gong");
            Rin = new PlaybackItemAmbience("Rin");
            Kyo = new PlaybackItemAmbience("Kyo");
            Toh = new PlaybackItemAmbience("Toh");
            Sha = new PlaybackItemAmbience("Sha");
            Kai = new PlaybackItemAmbience("Kai");
            Jin = new PlaybackItemAmbience("Jin");
            Retsu = new PlaybackItemAmbience("Retsu");
            Zai = new PlaybackItemAmbience("Zai");
            Zen = new PlaybackItemAmbience("Zen");
            Earth = new PlaybackItemAmbience("Earth");
            Air = new PlaybackItemAmbience("Air");
            Fire = new PlaybackItemAmbience("Fire");
            Water = new PlaybackItemAmbience("Water");
            Void = new PlaybackItemAmbience("Void");
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
                if (QiGong != null) {return QiGong;} else {return new PlaybackItemAmbience("Qi-Gong");}
            case 1:
                if (Rin != null) {return Rin;} else {return new PlaybackItemAmbience("Rin");}
            case 2:
                if (Kyo != null) {return Kyo;} else {return new PlaybackItemAmbience("Kyo");}
            case 3:
                if (Toh != null) {return Toh;} else {return new PlaybackItemAmbience("Toh");}
            case 4:
                if (Sha != null) {return Sha;} else {return new PlaybackItemAmbience("Sha");}
            case 5:
                if (Kai != null) {return Kai;} else {return new PlaybackItemAmbience("Kai");}
            case 6:
                if (Jin != null) {return Jin;} else {return new PlaybackItemAmbience("Jin");}
            case 7:
                if (Retsu != null) {return Retsu;} else {return new PlaybackItemAmbience("Retsu");}
            case 8:
                if (Zai != null) {return Zai;} else {return new PlaybackItemAmbience("Zai");}
            case 9:
                if (Zen != null) {return Zen;} else {return new PlaybackItemAmbience("Zen");}
            case 10:
                if (Earth != null) {return Earth;} else {return new PlaybackItemAmbience("Earth");}
            case 11:
                if (Air != null) {return Air;} else {return new PlaybackItemAmbience("Air");}
            case 12:
                if (Fire != null) {return Fire;} else {return new PlaybackItemAmbience("Fire");}
            case 13:
                if (Water != null) {return Water;} else {return new PlaybackItemAmbience("Water");}
            case 14:
                if (Void != null) {return Void;} else {return new PlaybackItemAmbience("Void");}
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
    public boolean completelyempty() {
        boolean completelyempty = true;
        for (int i = 0; i < 16; i++) {
            if (getsessionpartAmbience(i).hasAny()) {completelyempty = false;}
        }
        return completelyempty;
    }

}