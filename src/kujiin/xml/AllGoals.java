package kujiin.xml;

import javafx.util.Duration;
import kujiin.ui.MainController;
import kujiin.ui.dialogs.alerts.InformationDialog;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import static kujiin.xml.Preferences.ALLNAMES;

@XmlRootElement(name = "AllGoals")
@XmlAccessorType(XmlAccessType.FIELD)
public class AllGoals {
    private PlaybackItemGoals QiGongGoals;
    private PlaybackItemGoals RinGoals;
    private PlaybackItemGoals KyoGoals;
    private PlaybackItemGoals TohGoals;
    private PlaybackItemGoals ShaGoals;
    private PlaybackItemGoals KaiGoals;
    private PlaybackItemGoals JinGoals;
    private PlaybackItemGoals RetsuGoals;
    private PlaybackItemGoals ZaiGoals;
    private PlaybackItemGoals ZenGoals;
    private PlaybackItemGoals EarthGoals;
    private PlaybackItemGoals AirGoals;
    private PlaybackItemGoals FireGoals;
    private PlaybackItemGoals WaterGoals;
    private PlaybackItemGoals VoidGoals;
    private PlaybackItemGoals TotalGoals;
    @XmlTransient
    private MainController Root;

    public AllGoals() {}
    public AllGoals(MainController root) {
        Root = root;
        unmarshall();
    }

// List Getters
    public PlaybackItemGoals getplaybackItemGoals(int index) {
        switch (index) {
            case 0: return QiGongGoals;
            case 1: return RinGoals;
            case 2: return KyoGoals;
            case 3: return TohGoals;
            case 4: return ShaGoals;
            case 5: return KaiGoals;
            case 6: return JinGoals;
            case 7: return RetsuGoals;
            case 8: return ZaiGoals;
            case 9: return ZenGoals;
            case 10: return EarthGoals;
            case 11: return AirGoals;
            case 12: return FireGoals;
            case 13: return WaterGoals;
            case 14: return VoidGoals;
            case 15: return TotalGoals;
            default: return null;
        }
    }
    public void setPlaybackItemGoals(int index, PlaybackItemGoals playbackItemGoals) {
        switch (index) {
            case 0: QiGongGoals = playbackItemGoals; break;
            case 1: RinGoals = playbackItemGoals; break;
            case 2: KyoGoals = playbackItemGoals; break;
            case 3: TohGoals = playbackItemGoals; break;
            case 4: ShaGoals = playbackItemGoals; break;
            case 5: KaiGoals = playbackItemGoals; break;
            case 6: JinGoals = playbackItemGoals; break;
            case 7: RetsuGoals = playbackItemGoals; break;
            case 8: ZaiGoals = playbackItemGoals; break;
            case 9: ZenGoals = playbackItemGoals; break;
            case 10: EarthGoals = playbackItemGoals; break;
            case 11: AirGoals = playbackItemGoals; break;
            case 12: FireGoals = playbackItemGoals; break;
            case 13: WaterGoals = playbackItemGoals; break;
            case 14: VoidGoals = playbackItemGoals; break;
            case 15: TotalGoals = playbackItemGoals; break;
            default:
        }
        marshall();
    }

// XML Processing
    public void unmarshall() {
        if (Preferences.GOALSXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(AllGoals.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                AllGoals currentAllGoals = (AllGoals) createMarshaller.unmarshal(Preferences.GOALSXMLFILE);
                QiGongGoals = currentAllGoals.QiGongGoals;
                RinGoals = currentAllGoals.RinGoals;
                KyoGoals = currentAllGoals.KyoGoals;
                TohGoals = currentAllGoals.TohGoals;
                ShaGoals = currentAllGoals.ShaGoals;
                KaiGoals = currentAllGoals.KaiGoals;
                JinGoals = currentAllGoals.JinGoals;
                RetsuGoals = currentAllGoals.RetsuGoals;
                ZaiGoals = currentAllGoals.ZaiGoals;
                ZenGoals = currentAllGoals.ZenGoals;
                EarthGoals = currentAllGoals.EarthGoals;
                AirGoals = currentAllGoals.AirGoals;
                FireGoals = currentAllGoals.FireGoals;
                WaterGoals = currentAllGoals.WaterGoals;
                VoidGoals = currentAllGoals.VoidGoals;
                TotalGoals = currentAllGoals.TotalGoals;
            } catch (JAXBException e) {
                new InformationDialog(Root.getPreferences(), "Information", "Couldn't Open Goals XML File", "Check Read File Permissions Of " + Preferences.GOALSXMLFILE.getAbsolutePath(), false);
            }
        } else {
            QiGongGoals = new PlaybackItemGoals(ALLNAMES.get(0));
            RinGoals = new PlaybackItemGoals(ALLNAMES.get(1));
            KyoGoals = new PlaybackItemGoals(ALLNAMES.get(2));
            TohGoals = new PlaybackItemGoals(ALLNAMES.get(3));
            ShaGoals = new PlaybackItemGoals(ALLNAMES.get(4));
            KaiGoals = new PlaybackItemGoals(ALLNAMES.get(5));
            JinGoals = new PlaybackItemGoals(ALLNAMES.get(6));
            RetsuGoals = new PlaybackItemGoals(ALLNAMES.get(7));
            ZaiGoals = new PlaybackItemGoals(ALLNAMES.get(8));
            ZenGoals = new PlaybackItemGoals(ALLNAMES.get(9));
            EarthGoals = new PlaybackItemGoals(ALLNAMES.get(10));
            AirGoals = new PlaybackItemGoals(ALLNAMES.get(11));
            FireGoals = new PlaybackItemGoals(ALLNAMES.get(12));
            WaterGoals = new PlaybackItemGoals(ALLNAMES.get(13));
            VoidGoals = new PlaybackItemGoals(ALLNAMES.get(14));
            TotalGoals = new PlaybackItemGoals("Total");
            marshall();
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(AllGoals.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Preferences.GOALSXMLFILE);
        } catch (JAXBException e) {
            e.printStackTrace();
            new InformationDialog(Root.getPreferences(), "Information", "Couldn't Save Goals XML File", "Check Write File Permissions Of " + Preferences.GOALSXMLFILE.getAbsolutePath(), false);
        }
    }

// Utility Methods
    public void calculateifPlaybackItemgoalscompleted(int index, Duration duration) {
        getplaybackItemGoals(index).checkifgoalscompleted(duration);
    }
    public void calculateifTotalGoalsCompleted(Duration duration) {
        getplaybackItemGoals(15).checkifgoalscompleted(duration);
    }
    public boolean goalscompletedthissession() {
        for (int i = 0; i < 16; i++) {
            if (getplaybackItemGoals(i).goalscompletedthisession()) {return true;}
        }
        return false;
    }

}