package kujiin.util.xml;

import kujiin.This_Session;
import kujiin.util.lib.GuiUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

@XmlRootElement(name = "Sessions")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Sessions {
    private List<Session> Session;

    public Sessions() {deletenonvalidsessions();}

// Getters And Setters
    public List<kujiin.util.xml.Session> getSession() {return Session;}
    public void setSession(List<kujiin.util.xml.Session> session) {Session = session;}

// Other Methods
    public void populatefromxml() throws JAXBException {
        if (This_Session.sessionsxmlfile.exists()) {
            JAXBContext context = JAXBContext.newInstance(Sessions.class);
            Unmarshaller createMarshaller = context.createUnmarshaller();
            Sessions noises1 = (Sessions) createMarshaller.unmarshal(This_Session.sessionsxmlfile);
            setSession(noises1.getSession());
        }
    }
    public void addnewsession(Session session) throws JAXBException {
        if (This_Session.sessionsxmlfile.exists()) {populatefromxml();}
        List<Session> sessionsList = getSession();
        if (sessionsList == null) {sessionsList = new ArrayList<>();}
        sessionsList.add(session);
        setSession(sessionsList);
        JAXBContext context = JAXBContext.newInstance(Sessions.class);
        Marshaller createMarshaller = context.createMarshaller();
        createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        createMarshaller.marshal(this, This_Session.sessionsxmlfile);
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
    public int getgrandtotaltimepracticedinminutes(boolean includepreandpost) {
        try {
            int totalminutes = 0;
            for (kujiin.util.xml.Session i : getSession()) {
                if (includepreandpost) {for (int x=0; x<11;x++) {totalminutes += i.getcutduration(x);}}
                else {for (int x=1; x<10;x++) {totalminutes += i.getcutduration(x);}}
            }
            return totalminutes;
        } catch (NullPointerException ignored) {return 0;}
    }
    public float getaveragesessiontimeinminutes(boolean includepreandpost) {
        try {return (float) getgrandtotaltimepracticedinminutes(includepreandpost) / getSession().size();}
        catch (NullPointerException ignored) {return 0;}
    }
    public ArrayList<Session> getsessionswithprematureendings() {
        try {
            ArrayList<Session> sessionswithprematureendings = new ArrayList<>();
            for (kujiin.util.xml.Session i : getSession()) {
                if (i.wasendedPremature()) {sessionswithprematureendings.add(i);}
            }
            return sessionswithprematureendings;
        } catch (NullPointerException e) {return new ArrayList<>();}
    }
    public int getsessioncount() {
        if (getSession() != null) {return getSession().size();}
        else {return 0;}
    }
    public void createnewsession() {
        try {addnewsession(new Session("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));}
        catch (JAXBException ignored) {
            GuiUtils.showerrordialog("Error", "Cannot Create Session. This Session's Progress Won't Be Updated Into The Total Tracker", "Check File Permissions");
        }
    }
    public Session getcurrentsession() {return getSession().get(getSession().size() - 1);}
    public void deletenonvalidsessions() {
        try {
            for (kujiin.util.xml.Session i : getSession()) {
                if (! i.sessionnotEmpty()) {
                    try {removesession(i);}
                    catch (JAXBException ignored) {}
                }
            }
        } catch (NullPointerException | ConcurrentModificationException ignored) {}
    }

}
