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
    private final List<Entrainment> AllEntrainment = new ArrayList<>(Arrays.asList(Presession, Rin, Kyo, Toh, Sha, Kai, Jin, Retsu, Zai, Zen, Earth, Air, Fire, Water, Void, Postsession));
    @XmlTransient
    private MainController Root;
    @XmlTransient
    public static final List<Integer> DURATIONSVARIATIONS = Arrays.asList(1, 5);

    public Entrainments() {}
    public Entrainments(MainController Root) {this.Root = Root; unmarshall();}

// XML Processing
    public void unmarshall() {
        if (Options.ENTRAINMENTXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(Entrainments.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                Entrainments entrainments = (Entrainments) createMarshaller.unmarshal(Options.ENTRAINMENTXMLFILE);
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
            Root.dialog_displayInformation("Information", "Couldn't Write Entrainment XML File", "Check Write File Permissions Of " + Options.ENTRAINMENTXMLFILE.getAbsolutePath());
        }
    }

// Other Methods
    public Entrainment getsessionpartEntrainment(int index) {
        Entrainment entrainment = AllEntrainment.get(index);
        if (entrainment == null) {return new Entrainment();}
        else {return entrainment;}
    }
    public void setsessionpartEntrainment(int index, Entrainment entrainment) {
        switch (index) {
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
