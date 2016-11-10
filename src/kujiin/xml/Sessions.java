package kujiin.xml;

import javafx.util.Duration;
import kujiin.ui.MainController;
import kujiin.ui.dialogs.InformationDialog;
import kujiin.util.Qi_Gong;
import kujiin.util.SessionPart;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
            JAXBContext context = JAXBContext.newInstance(Sessions.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Preferences.SESSIONSXMLFILE);
        } catch (JAXBException e) {
            new InformationDialog(Root.getPreferences(), "Information", "Couldn't Write Sessions XML File", "Check Write File Permissions Of " + Preferences.SESSIONSXMLFILE.getAbsolutePath());}
    }

// Session Methods
    public void add(Session session) throws JAXBException {
        if (Preferences.SESSIONSXMLFILE.exists()) {unmarshall();}
        List<Session> sessionsList = getSession();
        if (sessionsList != null && sessionsList.size() > 0) {
            sort();
        } else {sessionsList = new ArrayList<>();}
        sessionsList.add(session);
        setSession(sessionsList);
    }
    public Session get(int index) {
        try {return Session.get(index);}
        catch (IndexOutOfBoundsException ignored) {return null;}
    }
    public void remove(Session session) throws JAXBException {
        List<Session> sessionList = getSession();
        sessionList.remove(sessionList.indexOf(session));
        setSession(sessionList);
    }
    public void sort() {
        Collections.sort(Session, (o1, o2) -> o1.getDate_Practiced().compareTo(o2.getDate_Practiced()));
    }

// Session Information Getters
    public List<Session> getsessionpartsessions(SessionPart sessionPart) {
        return getSession().stream().filter(i -> i.getduration(sessionPart).greaterThan(Duration.ZERO)).collect(Collectors.toList());
    }
    public Duration gettotalpracticedtime(SessionPart sessionpart, boolean includepreandpost) {
        try {
            Duration totalduration = Duration.ZERO;
            for (kujiin.xml.Session i : getSession()) {totalduration = totalduration.add(i.getduration(sessionpart));}
            if (includepreandpost) {
                for (SessionPart i : Root.getAllSessionParts(true)) {
                    if (i instanceof Qi_Gong) {
                        for (kujiin.xml.Session x : getSession()) {totalduration = totalduration.add(x.getduration(i));}
                    }
                }
            }
            return totalduration;
        } catch (NullPointerException ignored) {return Duration.ZERO;}
    }
    public Duration getaveragepracticedurationforallsessions(SessionPart sessionpart, boolean includepreandpost) {
        try {
            return new Duration(gettotalpracticedtime(sessionpart, includepreandpost).toMillis() / getsessioncount(sessionpart, includepreandpost));}
        catch (NullPointerException | ArithmeticException ignored) {return Duration.ZERO;}
    }
    public int getsessioncount(SessionPart sessionpart, boolean includepreandpost) {
        try {
            int sessioncount = 0;
            for (kujiin.xml.Session i : getSession()) {
                if (i.getduration(sessionpart).greaterThan(Duration.ZERO)) {sessioncount++; continue;}
                if (includepreandpost) {
                    if (i.getduration(Root.getSessionPart(0)).greaterThan(Duration.ZERO) || i.getduration(Root.getSessionPart(15)).greaterThan(Duration.ZERO)) {sessioncount++;}
                }
            }
            return sessioncount;
        } catch (NullPointerException ignored) {ignored.printStackTrace(); return 0;}
    }

}