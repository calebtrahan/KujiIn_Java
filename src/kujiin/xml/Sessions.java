package kujiin.xml;

import javafx.util.Duration;
import kujiin.lib.BeanComparator;
import kujiin.ui.MainController;
import kujiin.ui.dialogs.ErrorDialog;
import kujiin.ui.dialogs.InformationDialog;
import kujiin.util.Qi_Gong;
import kujiin.util.SessionPart;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

@XmlRootElement(name = "Sessions")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Sessions {
    @XmlElement(name = "Session")
    private List<Session> Session;
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
        if (Options.SESSIONSXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(Sessions.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                Sessions noises1 = (Sessions) createMarshaller.unmarshal(Options.SESSIONSXMLFILE);
                setSession(noises1.getSession());
            } catch (JAXBException e) {
                new InformationDialog(Root.getOptions(), "Information", "Couldn't Read Sessions XML File", "Check Read File Permissions Of " + Options.SESSIONSXMLFILE.getAbsolutePath());
            }
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Sessions.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Options.SESSIONSXMLFILE);
        } catch (JAXBException e) {
            new InformationDialog(Root.getOptions(), "Information", "Couldn't Write Sessions XML File", "Check Write File Permissions Of " + Options.SESSIONSXMLFILE.getAbsolutePath());}
    }
    public void createnew() {
        try {
            add(new Session(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));}
        catch (JAXBException ignored) {
            new ErrorDialog(Root.getOptions(), "Error", "Cannot Create Session. This Session's Progress Won't Be Updated Into The Total Tracker", "Check File Permissions");}
    }
    public void add(Session session) throws JAXBException {
        if (Options.SESSIONSXMLFILE.exists()) {unmarshall();}
        List<Session> sessionsList = getSession();
        if (sessionsList != null && sessionsList.size() > 0) {
            sort();
        } else {sessionsList = new ArrayList<>();}
        sessionsList.add(session);
        setSession(sessionsList);
        marshall();
    }
    public void remove(Session session) throws JAXBException {
        List<Session> sessionList = getSession();
        sessionList.remove(sessionList.indexOf(session));
        setSession(sessionList);
        JAXBContext context = JAXBContext.newInstance(Sessions.class);
        Marshaller createMarshaller = context.createMarshaller();
        createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        createMarshaller.marshal(this, Options.SESSIONSXMLFILE);
    }
    public void deletenonvalidsessions() {
        try {
            for (kujiin.xml.Session i : getSession()) {
                if (! i.sessionnotEmpty()) {
                    try {
                        remove(i);}
                    catch (JAXBException ignored) {}
                }
            }
        } catch (NullPointerException | ConcurrentModificationException ignored) {}
    }
    public void sort() {
        List<Session> sessions = getSession();
        Collections.sort(sessions, new BeanComparator(Session.class, "getDate_Practiced"));
        setSession(sessions);
    }

// Session Information Getters
    public Duration gettotalpracticedtime(int index, boolean includepreandpost) {
        try {
            Duration totalduration = Duration.ZERO;
            for (kujiin.xml.Session i : getSession()) {totalduration = totalduration.add(i.getsessionpartdurationasObject(index));}
            if (includepreandpost) {
                for (SessionPart i : Root.getAllSessionParts(true)) {
                    if (i instanceof Qi_Gong) {
                        for (kujiin.xml.Session x : getSession()) {totalduration = totalduration.add(x.getsessionpartdurationasObject(i.number));}
                    }
                }
            }
            return totalduration;
        } catch (NullPointerException ignored) {return Duration.ZERO;}
    }
    public Duration getaveragepracticedurationforallsessions(int index, boolean includepreandpost) {
        try {
            return new Duration(gettotalpracticedtime(index, includepreandpost).toMillis() / getsessioncount(index, includepreandpost));}
        catch (NullPointerException | ArithmeticException ignored) {return Duration.ZERO;}
    }
    public int getsessioncount(int index, boolean includepreandpost) {
        try {
            int sessioncount = 0;
            for (kujiin.xml.Session i : getSession()) {
                if (i.getsessionpartduration(index) != 0) {sessioncount++; continue;}
                if (includepreandpost) {
                    if (i.getsessionpartduration(0) != 0 || i.getsessionpartduration(15) != 0) {sessioncount++;}
                }
            }
            return sessioncount;
        } catch (NullPointerException ignored) {return 0;}
    }
    public int totalsessioncount() {
        try {
            return getSession().size();
        } catch (NullPointerException ignored) {return 0;}
    }
    public Session getspecificsession(int index) {
        return getSession().get(index);
    }

}
