package kujiin.xml;

import kujiin.ui.MainController;
import kujiin.ui.dialogs.ErrorDialog;
import kujiin.ui.dialogs.InformationDialog;
import kujiin.util.SessionPart;

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
    @XmlTransient
    private MainController Root;

    public Entrainments() {}
    public Entrainments(MainController Root) {
        this.Root = Root;
        if (! Root.getPreferences().getAdvancedOptions().getOS().equals(System.getProperty("os.name"))) {
            if (! Preferences.ENTRAINMENTXMLFILE.delete()) {
                new ErrorDialog(Root.getPreferences(), "Error", "Cannot Write To Entrainment's XML File",
                        "Check Permissions For " + Preferences.ENTRAINMENTXMLFILE.getAbsolutePath());
                return;
            }
        }
        unmarshall();
    }

// XML Processing
    public void unmarshall() {
        if (Preferences.ENTRAINMENTXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(Entrainments.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                Entrainments entrainments = (Entrainments) createMarshaller.unmarshal(Preferences.ENTRAINMENTXMLFILE);
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
            } catch (JAXBException ignored) {}
        } else {
            Presession = new Entrainment();
            Rin = new Entrainment();
            Kyo = new Entrainment();
            Toh = new Entrainment();
            Sha = new Entrainment();
            Kai = new Entrainment();
            Jin = new Entrainment();
            Retsu = new Entrainment();
            Zai = new Entrainment();
            Zen = new Entrainment();
            Earth = new Entrainment();
            Air = new Entrainment();
            Fire = new Entrainment();
            Water = new Entrainment();
            Void = new Entrainment();
            Postsession = new Entrainment();
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Entrainments.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Preferences.ENTRAINMENTXMLFILE);
        } catch (JAXBException e) {
            e.printStackTrace();
            new InformationDialog(Root.getPreferences(), "Information", "Couldn't Write Entrainment XML File", "Check Write File Permissions Of " + Preferences.ENTRAINMENTXMLFILE.getAbsolutePath());
        }
    }

// Other Methods
    public Entrainment getsessionpartEntrainment(SessionPart sessionPart) {
        switch (sessionPart.number) {
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
        }
        return null;
    }
    public void setsessionpartEntrainment(SessionPart sessionPart, Entrainment entrainment) {
        switch (sessionPart.number) {
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
