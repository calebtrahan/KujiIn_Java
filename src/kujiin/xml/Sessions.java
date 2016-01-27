package kujiin.xml;

import kujiin.This_Session;
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
    private List<Session> Session = null;

    public Sessions() {deletenonvalidsessions();}

// Getters And Setters
    public List<kujiin.xml.Session> getSession() {return Session;}
    public void setSession(List<kujiin.xml.Session> session) {Session = session;}

// XML Processing
    public void unmarshall() throws JAXBException {
        if (This_Session.sessionsxmlfile.exists()) {
            JAXBContext context = JAXBContext.newInstance(Sessions.class);
            Unmarshaller createMarshaller = context.createUnmarshaller();
            Sessions noises1 = (Sessions) createMarshaller.unmarshal(This_Session.sessionsxmlfile);
            setSession(noises1.getSession());
        }
    }
    public void marshall() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Sessions.class);
        Marshaller createMarshaller = context.createMarshaller();
        createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        createMarshaller.marshal(this, This_Session.sessionsxmlfile);
    }
    public void createnewsession() {
        try {addsession(new Session(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));}
        catch (JAXBException e) {
            e.printStackTrace();
            Tools.showerrordialog("Error", "Cannot Create Session. This Session's Progress Won't Be Updated Into The Total Tracker", "Check File Permissions");}
    }
    public void addsession(Session session) throws JAXBException {
        if (This_Session.sessionsxmlfile.exists()) {unmarshall();}
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
        createMarshaller.marshal(this, This_Session.sessionsxmlfile);
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
    public int totalpracticetimeinminutes(boolean includepreandpost) {
        try {
            int totalminutes = 0;
            for (kujiin.xml.Session i : getSession()) {
                if (includepreandpost) {for (int x=0; x<11;x++) {totalminutes += i.getcutduration(x);}}
                else {for (int x=1; x<10;x++) {totalminutes += i.getcutduration(x);}}
            }
            return totalminutes;
        } catch (NullPointerException ignored) {return 0;}
    }
    public double totalpracticetimeinhours(boolean includepreandpost) {
        return Tools.convertminutestodecimalhours(totalpracticetimeinminutes(includepreandpost));
    }
    public int averagepracticetimeinminutes(boolean includepreandpost) {
        try {return totalpracticetimeinminutes(includepreandpost) / getSession().size();}
        catch (NullPointerException | ArithmeticException ignored) {return 0;}
    }
    public ArrayList<Session> getsessionswithprematureendings() {
        try {
            ArrayList<Session> sessionswithprematureendings = new ArrayList<>();
            for (kujiin.xml.Session i : getSession()) {
                if (i.wasendedPremature()) {sessionswithprematureendings.add(i);}
            }
            return sessionswithprematureendings;
        } catch (NullPointerException e) {return new ArrayList<>();}
    }
    public int sessionscount() {
        if (getSession() != null) {return getSession().size();}
        else {return 0;}
    }
    public Session getsession(int index) {
        return getSession().get(index);
    }
    public int gettotalcutpracticetimeinminutes(int cutindex) {
        try {
            int minutes;
            if (cutindex != 10) {
                minutes = 0;
                for (Session i : getSession()) {minutes += i.getcutduration(cutindex);}
            } else {minutes = totalpracticetimeinminutes(false);}
            return minutes;
        } catch (NullPointerException ignored) {return 0;}
    }
}
