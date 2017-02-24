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
            System.out.println("Should Be Marshalling " + Session.size() + " Sessions");
            JAXBContext context = JAXBContext.newInstance(Sessions.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Preferences.SESSIONSXMLFILE);
        } catch (JAXBException e) {
            new InformationDialog(Root.getPreferences(), "Information", "Couldn't Write Sessions XML File", "Check Write File Permissions Of " + Preferences.SESSIONSXMLFILE.getAbsolutePath());}
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
    public Duration gettotalpracticedtime(kujiin.xml.Session.PlaybackItem playbackItem, boolean includeqigong) {
        try {
            Duration totalduration = Duration.ZERO;
            for (kujiin.xml.Session i : getSession()) {
                for (kujiin.xml.Session.PlaybackItem x : i.getPlaybackItems()) {
                    if (x.getEntrainmentandavailableambienceindex() == playbackItem.getEntrainmentandavailableambienceindex()) {
                        totalduration = totalduration.add(new Duration(x.getDuration()));
                    }
                    if (includeqigong && ! (playbackItem instanceof kujiin.xml.Session.QiGong) && x instanceof kujiin.xml.Session.QiGong) {
                        totalduration = totalduration.add(new Duration(x.getDuration()));
                    }
                }
            }
            return totalduration;
        } catch (NullPointerException ignored) {return Duration.ZERO;}
    }
    public Duration getaveragepracticedurationforallsessions(kujiin.xml.Session.PlaybackItem playbackitem, boolean includepreandpost) {
        try {
            return new Duration(gettotalpracticedtime(playbackitem, includepreandpost).toMillis() / getsessioncount(playbackitem, includepreandpost));}
        catch (NullPointerException | ArithmeticException ignored) {return Duration.ZERO;}
    }
    public int getsessioncount(kujiin.xml.Session.PlaybackItem playbackitem, boolean includeqigong) {
        try {
            int sessioncount = 0;
            for (kujiin.xml.Session i : getSession()) {
                for (kujiin.xml.Session.PlaybackItem x : i.getPlaybackItems()) {
                    if (x.getEntrainmentandavailableambienceindex() == playbackitem.getEntrainmentandavailableambienceindex() && x.getDuration() > 0.0) {sessioncount++; continue;}
                    if (includeqigong && ! (playbackitem instanceof kujiin.xml.Session.QiGong) && x.getDuration() > 0.0) {sessioncount++;}
                }
            }
            return sessioncount;
        } catch (NullPointerException ignored) {return 0;}
    }

}