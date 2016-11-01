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
import java.util.stream.Collectors;

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
            deletenonvalidsessions();
            JAXBContext context = JAXBContext.newInstance(Sessions.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Options.SESSIONSXMLFILE);
        } catch (JAXBException e) {
            new InformationDialog(Root.getOptions(), "Information", "Couldn't Write Sessions XML File", "Check Write File Permissions Of " + Options.SESSIONSXMLFILE.getAbsolutePath());}
    }
    public void createnew() {
        try {
            int[] array = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            add(new Session(array));
        } catch (JAXBException ignored) {
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
    }
    public void remove(Session session) throws JAXBException {
        List<Session> sessionList = getSession();
        sessionList.remove(sessionList.indexOf(session));
        setSession(sessionList);
    }
    public void deletenonvalidsessions() {
        try {
            getSession().stream().filter(kujiin.xml.Session::sessionempty).forEach(i -> {
                try {remove(i);}
                catch (JAXBException ignored) {}
            });
        } catch (NullPointerException | ConcurrentModificationException ignored) {}
    }
    public void sort() {
        List<Session> sessions = getSession();
        Collections.sort(sessions, new BeanComparator(Session.class, "getDate_Practiced"));
        setSession(sessions);
    }

// Session Information Getters
    public List<Session> getsessionpartsessions(SessionPart sessionPart) {
        return getSession().stream().filter(i -> i.getsessionpartduration(sessionPart) > 0).collect(Collectors.toList());
    }
    public Duration gettotalpracticedtime(SessionPart sessionpart, boolean includepreandpost) {
        try {
            Duration totalduration = Duration.ZERO;
            for (kujiin.xml.Session i : getSession()) {totalduration = totalduration.add(i.getsessionpartdurationasObject(sessionpart));}
            if (includepreandpost) {
                for (SessionPart i : Root.getAllSessionParts(true)) {
                    if (i instanceof Qi_Gong) {
                        for (kujiin.xml.Session x : getSession()) {totalduration = totalduration.add(x.getsessionpartdurationasObject(i));}
                    }
                }
            }
            return totalduration;
        } catch (NullPointerException ignored) {return Duration.ZERO;}
    }
    public Duration getaveragepracticedurationforallsessions(SessionPart sessionpart, boolean includepreandpost) {
        System.out.println("Trying To Get Average Practice Duration For " + sessionpart.name);
        try {
            return new Duration(gettotalpracticedtime(sessionpart, includepreandpost).toMillis() / getsessioncount(sessionpart, includepreandpost));}
        catch (NullPointerException | ArithmeticException ignored) {return Duration.ZERO;}
    }
    public int getsessioncount(SessionPart sessionpart, boolean includepreandpost) {
        try {
            int sessioncount = 0;
            System.out.println(getSession().size());
            for (kujiin.xml.Session i : getSession()) {
                if (i.getsessionpartduration(sessionpart) != 0) {sessioncount++; continue;}
                if (includepreandpost) {
                    if (i.getsessionpartduration(Root.getPresession()) != 0 || i.getsessionpartduration(Root.getPostsession()) != 0) {sessioncount++;}
                }
            }
            return sessioncount;
        } catch (NullPointerException ignored) {ignored.printStackTrace(); return 0;}
    }
    public int totalsessioncount() {
        try {return getSession().size();}
        catch (NullPointerException ignored) {return 0;}
    }
    public Session getspecificsession(int index) {
        return getSession().get(index);
    }

}
