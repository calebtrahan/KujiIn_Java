package kujiin.xml;

import kujiin.Tools;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

@XmlRootElement(name = "Sessions")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Sessions {
    @XmlElement(name = "Session")
    private List<Session> Session;

    public Sessions() {deletenonvalidsessions();}

// Getters And Setters
    public List<kujiin.xml.Session> getSession() {return Session;}
    public void setSession(List<kujiin.xml.Session> session) {Session = session;}

// XML Processing
    public void unmarshall() {
        if (Options.sessionsxmlfile.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(Sessions.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                Sessions noises1 = (Sessions) createMarshaller.unmarshal(Options.sessionsxmlfile);
                setSession(noises1.getSession());
            } catch (JAXBException e) {
                Tools.showinformationdialog("Information", "Couldn't Read Sessions XML File", "Check Read File Permissions Of " + Options.sessionsxmlfile.getAbsolutePath());
            }
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Sessions.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Options.sessionsxmlfile);
        } catch (JAXBException e) {}
    }
    public void createnewsession() {
        try {addsession(new Session(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));}
        catch (JAXBException e) {
            e.printStackTrace();
            Tools.showerrordialog("Error", "Cannot Create Session. This Session's Progress Won't Be Updated Into The Total Tracker", "Check File Permissions");}
    }
    public void addsession(Session session) throws JAXBException {
        if (Options.sessionsxmlfile.exists()) {unmarshall();}
        List<Session> sessionsList = getSession();
        if (sessionsList != null && sessionsList.size() > 0) {
            sortsessions();
        } else {sessionsList = new ArrayList<>();}
        sessionsList.add(session);
        setSession(sessionsList);
        marshall();
    }
    public void removesession(Session session) throws JAXBException {
        List<Session> sessionList = getSession();
        sessionList.remove(sessionList.indexOf(session));
        setSession(sessionList);
        JAXBContext context = JAXBContext.newInstance(Sessions.class);
        Marshaller createMarshaller = context.createMarshaller();
        createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        createMarshaller.marshal(this, Options.sessionsxmlfile);
    }
    public void deletenonvalidsessions() {
        try {
            for (kujiin.xml.Session i : getSession()) {
                if (! i.sessionnotEmpty()) {
                    try {removesession(i);}
                    catch (JAXBException ignored) {}
                }
            }
        } catch (NullPointerException | ConcurrentModificationException ignored) {}
    }
    public void sortsessions() {
        // TODO Sort Sessions By Practice Date
    }

// Session Information Getters
    public int getpracticedtimeinminutes(int index, Boolean includepreandpost) {
        try {
            int totalminutes = 0;
            if (index == 0) {
            // Pre And Post
                for (kujiin.xml.Session i : getSession()) {
                    totalminutes += i.getcutduration(0);
                    totalminutes += i.getcutduration(10);
                }
            } else if (index == 10) {
            // TOTAL!
                for (kujiin.xml.Session i : getSession()) {
                    if (includepreandpost) {for (int x=0; x<11;x++) {totalminutes += i.getcutduration(x);}}
                    else {for (int x=1; x<10;x++) {totalminutes += i.getcutduration(x);}}
                }
            } else {
            // Indidivual Cut
                for (kujiin.xml.Session i : getSession()) {totalminutes += i.getcutduration(index);}
            }
            return totalminutes;
        } catch (NullPointerException ignored) {return 0;}
    }
    public int averagepracticetimeinminutes(int index, Boolean includepreandpost) {
        try {return getpracticedtimeinminutes(index, includepreandpost) / getSession().size();}
        catch (NullPointerException | ArithmeticException ignored) {return 0;}
    }
    public int cutsessionscount(int index) {
        try {
            int sessioncount = 0;
            for (kujiin.xml.Session i : getSession()) {
                if (i.getcutduration(index) != 0) {sessioncount++;}
            }
            return sessioncount;
        } catch (NullPointerException ignored) {return 0;}
    }
    public int totalsessioncount() {
        try {
            return getSession().size();
        } catch (NullPointerException ignored) {return 0;}
    }
    public Session getsession(int index) {
        return getSession().get(index);
    }

}
