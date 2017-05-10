package kujiin.xml;

import javafx.util.Duration;
import kujiin.ui.MainController;
import kujiin.ui.dialogs.alerts.InformationDialog;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@XmlRootElement(name = "Sessions")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Sessions {
    @XmlElement(name = "Session")
    private List<Session> Session;
    @XmlTransient
    private MainController Root;

    public Sessions() {}
    public Sessions(MainController root) {
        Root = root;
        unmarshall();
    }

// Getters And Setters
    public List<kujiin.xml.Session> getSession() {return Session;}
    public void setSession(List<kujiin.xml.Session> session) {Session = session;}

// XML Processing
    public void unmarshall() {
        if (Preferences.SESSIONSXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(Sessions.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                Sessions noises1 = (Sessions) createMarshaller.unmarshal(Preferences.SESSIONSXMLFILE);
                setSession(noises1.getSession());
            } catch (JAXBException e) {
                new InformationDialog(Root.getPreferences(), "Information", "Couldn't Read Sessions XML File", "Check Read File Permissions Of " + Preferences.SESSIONSXMLFILE.getAbsolutePath());
            }
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Sessions.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Preferences.SESSIONSXMLFILE);
        } catch (JAXBException e) {e.printStackTrace();}
    }

// Session Methods
    public void add(Session session) {
        if (Session == null) {Session = new ArrayList<>();}
        if (session.isValid()) {
            Session.add(session);
            sort();
            marshall();
        }
    }
    public Session get(int index) {
        try {return Session.get(index);}
        catch (IndexOutOfBoundsException ignored) {return null;}
    }
    public void remove(Session session) {
        if (Session != null) {
            Session.remove(session);
            sort();
            marshall();
        }
    }
    public void sort() {Session.sort(Comparator.comparing(kujiin.xml.Session::getDate_Practiced));}

// Session Information Getters
    public Duration gettotalpracticedtime(PlaybackItem playbackItem, boolean includeqigong) {
        try {
            Duration totalduration = Duration.ZERO;
            for (kujiin.xml.Session i : getSession()) {
                for (PlaybackItem x : i.getPlaybackItems()) {
                    if (x.getCreationindex() == playbackItem.getCreationindex()) {
                        totalduration = totalduration.add(new Duration(x.getExpectedDuration()));
                    }
                    if (includeqigong && ! (playbackItem.getCreationindex() == 0) && x.getCreationindex() == 0) {
                        totalduration = totalduration.add(new Duration(x.getExpectedDuration()));
                    }
                }
            }
            return totalduration;
        } catch (NullPointerException ignored) {return Duration.ZERO;}
    }
    public Duration getaveragepracticedurationforallsessions(PlaybackItem playbackitem, boolean includepreandpost) {
        try {
            return new Duration(gettotalpracticedtime(playbackitem, includepreandpost).toMillis() / getsessioncount(playbackitem, includepreandpost));}
        catch (NullPointerException | ArithmeticException ignored) {return Duration.ZERO;}
    }
    public int getsessioncount(PlaybackItem playbackitem, boolean includeqigong) {
        try {
            int sessioncount = 0;
            for (kujiin.xml.Session i : getSession()) {
                for (PlaybackItem x : i.getPlaybackItems()) {
                    if (x.getCreationindex() == playbackitem.getCreationindex() && x.getExpectedDuration() > 0.0) {sessioncount++; continue;}
                    if (includeqigong && ! (playbackitem.getCreationindex() == 0) && x.getExpectedDuration() > 0.0) {sessioncount++;}
                }
            }
            return sessioncount;
        } catch (NullPointerException ignored) {return 0;}
    }

}