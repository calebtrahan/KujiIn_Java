package kujiin.xml;

import kujiin.ui.MainController;
import kujiin.ui.dialogs.alerts.ErrorDialog;
import kujiin.ui.dialogs.alerts.InformationDialog;

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
public class AvailableEntrainments {
    private PlaybackItemEntrainment QiGong;
    private PlaybackItemEntrainment Rin;
    private PlaybackItemEntrainment Kyo;
    private PlaybackItemEntrainment Toh;
    private PlaybackItemEntrainment Sha;
    private PlaybackItemEntrainment Kai;
    private PlaybackItemEntrainment Jin;
    private PlaybackItemEntrainment Retsu;
    private PlaybackItemEntrainment Zai;
    private PlaybackItemEntrainment Zen;
    private PlaybackItemEntrainment Earth;
    private PlaybackItemEntrainment Air;
    private PlaybackItemEntrainment Fire;
    private PlaybackItemEntrainment Water;
    private PlaybackItemEntrainment Void;
    private RampFiles rampFiles;
    @XmlTransient
    private MainController Root;

    public AvailableEntrainments() {}
    public AvailableEntrainments(MainController Root) {
        this.Root = Root;
        if (! Root.getPreferences().getAdvancedOptions().getOS().equals(System.getProperty("os.name"))) {
            if (! Preferences.ENTRAINMENTXMLFILE.delete()) {
                new ErrorDialog(Root.getPreferences(), "Error", "Cannot Write To PlaybackItemEntrainment's XML File",
                        "Check Permissions For " + Preferences.ENTRAINMENTXMLFILE.getAbsolutePath());
                return;
            }
            Root.getPreferences().getAdvancedOptions().setOS(System.getProperty("os.name"));
        }
        unmarshall();
    }

// XML Processing
    public void unmarshall() {
        if (Preferences.ENTRAINMENTXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(AvailableEntrainments.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                AvailableEntrainments availableEntrainments = (AvailableEntrainments) createMarshaller.unmarshal(Preferences.ENTRAINMENTXMLFILE);
                QiGong = availableEntrainments.QiGong;
                Rin = availableEntrainments.Rin;
                Kyo = availableEntrainments.Kyo;
                Toh = availableEntrainments.Toh;
                Sha = availableEntrainments.Sha;
                Kai = availableEntrainments.Kai;
                Jin = availableEntrainments.Jin;
                Retsu = availableEntrainments.Retsu;
                Zai = availableEntrainments.Zai;
                Zen = availableEntrainments.Zen;
                Earth = availableEntrainments.Earth;
                Air = availableEntrainments.Air;
                Fire = availableEntrainments.Fire;
                Water = availableEntrainments.Water;
                Void = availableEntrainments.Void;
                rampFiles = availableEntrainments.rampFiles;
            } catch (JAXBException ignored) {}
        } else {
            QiGong = new PlaybackItemEntrainment();
            Rin = new PlaybackItemEntrainment();
            Kyo = new PlaybackItemEntrainment();
            Toh = new PlaybackItemEntrainment();
            Sha = new PlaybackItemEntrainment();
            Kai = new PlaybackItemEntrainment();
            Jin = new PlaybackItemEntrainment();
            Retsu = new PlaybackItemEntrainment();
            Zai = new PlaybackItemEntrainment();
            Zen = new PlaybackItemEntrainment();
            Earth = new PlaybackItemEntrainment();
            Air = new PlaybackItemEntrainment();
            Fire = new PlaybackItemEntrainment();
            Water = new PlaybackItemEntrainment();
            Void = new PlaybackItemEntrainment();
            rampFiles = new RampFiles();
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(AvailableEntrainments.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Preferences.ENTRAINMENTXMLFILE);
        } catch (JAXBException e) {
            e.printStackTrace();
            new InformationDialog(Root.getPreferences(), "Information", "Couldn't Write PlaybackItemEntrainment XML File", "Check Write File Permissions Of " + Preferences.ENTRAINMENTXMLFILE.getAbsolutePath());
        }
    }

// Other Methods
    public PlaybackItemEntrainment getsessionpartEntrainment(Session.PlaybackItem sessionItem) {
        switch (sessionItem.getEntrainmentandavailableambienceindex()) {
            case 0: return QiGong;
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
        }
        return null;
    }
    public void setsessionpartEntrainment(Session.PlaybackItem sessionItem, PlaybackItemEntrainment playbackItemEntrainment) {
        switch (sessionItem.getEntrainmentandavailableambienceindex()) {
            case 0: QiGong = playbackItemEntrainment; break;
            case 1: Rin = playbackItemEntrainment; break;
            case 2: Kyo = playbackItemEntrainment; break;
            case 3: Toh = playbackItemEntrainment; break;
            case 4: Sha = playbackItemEntrainment; break;
            case 5: Kai = playbackItemEntrainment; break;
            case 6: Jin = playbackItemEntrainment; break;
            case 7: Retsu = playbackItemEntrainment; break;
            case 8: Zai = playbackItemEntrainment; break;
            case 9: Zen = playbackItemEntrainment; break;
            case 10: Earth = playbackItemEntrainment; break;
            case 11: Air = playbackItemEntrainment; break;
            case 12: Fire = playbackItemEntrainment; break;
            case 13: Water = playbackItemEntrainment; break;
            case 14: Void = playbackItemEntrainment; break;
        }
    }
    public RampFiles getRampFiles() {
        return rampFiles;
    }
    public void setRampFiles(RampFiles rampFiles) {
        this.rampFiles = rampFiles;
    }

}