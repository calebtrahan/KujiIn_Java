package kujiin.xml;

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
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class FavoriteSessions {
    private List<FavoriteSession> FavoriteSessions;
    @XmlTransient
    private MainController root;

    public FavoriteSessions() {}
    public FavoriteSessions(MainController Root) {
        root = Root;
        unmarshall();
    }

// XML Processing
    public void unmarshall() {
        if (Preferences.FAVORITESESSIONSXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(Entrainments.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                FavoriteSessions favoriteSessions = (FavoriteSessions) createMarshaller.unmarshal(Preferences.FAVORITESESSIONSXMLFILE);
                FavoriteSessions = favoriteSessions.FavoriteSessions;
            } catch (JAXBException ignored) {}
        } else {FavoriteSessions = new ArrayList<>();}
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(FavoriteSessions.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Preferences.FAVORITESESSIONSXMLFILE);
        } catch (JAXBException e) {
            e.printStackTrace();
            new InformationDialog(root.getPreferences(), "Information", "Couldn't Write Entrainment XML File", "Check Write File Permissions Of " + Preferences.ENTRAINMENTXMLFILE.getAbsolutePath());
        }
    }

// Other Methods
    public void add(String name, Session session) {
        FavoriteSessions.add(new FavoriteSession(name, session));
    }
    public FavoriteSession get(int index) {return FavoriteSessions.get(index);}

}