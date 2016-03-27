package kujiin.xml;

import kujiin.MainController;
import kujiin.Tools;
import kujiin.widgets.ProgressAndGoalsWidget;

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
                Tools.gui_showinformationdialog(Root, "Information", "Couldn't Read Sessions XML File", "Check Read File Permissions Of " + Options.SESSIONSXMLFILE.getAbsolutePath());
            }
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Sessions.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Options.SESSIONSXMLFILE);
        } catch (JAXBException e) {Tools.gui_showinformationdialog(Root, "Information", "Couldn't Write Sessions XML File", "Check Write File Permissions Of " + Options.SESSIONSXMLFILE.getAbsolutePath());}
    }
    public void createnewsession() {
        try {addsession(new Session(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));}
        catch (JAXBException ignored) {
            Tools.gui_showerrordialog(Root, "Error", "Cannot Create Session. This Session's Progress Won't Be Updated Into The Total Tracker", "Check File Permissions");}
    }
    public void addsession(Session session) throws JAXBException {
        if (Options.SESSIONSXMLFILE.exists()) {unmarshall();}
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
        createMarshaller.marshal(this, Options.SESSIONSXMLFILE);
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
    // TODO Refactor These Methods So They Make More Sense
    public int getpracticetimeinminutesforthissession(int index, Boolean includepreandpost) {
        try {
            int totalminutes = 0;
            kujiin.xml.Session thissession = getsession(totalsessioncount() - 1);
            if (index == 0) {
                // Pre And Post
                totalminutes += thissession.getcutduration(0);
                totalminutes += thissession.getcutduration(10);
            } else if (index == ProgressAndGoalsWidget.GOALCUTNAMES.length - 1) {
                // TOTAL!
                if (includepreandpost) {for (int x=0; x<ProgressAndGoalsWidget.GOALCUTNAMES.length - 1;x++) {totalminutes += thissession.getcutduration(x);}}
                else {for (int x=1; x<ProgressAndGoalsWidget.GOALCUTNAMES.length - 2;x++) {totalminutes += thissession.getcutduration(x);}}
            } else {
                // Indidivual Cut
                totalminutes += thissession.getcutduration(index);
            }
            return totalminutes;
        } catch (NullPointerException ignored) {return 0;}
    }
    public int getpracticedtimeinminutesforallsessions(int index, Boolean includepreandpost) {
        try {
            int totalminutes = 0;
            if (index == ProgressAndGoalsWidget.GOALCUTNAMES.length - 1) {
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
        try {return getpracticedtimeinminutesforallsessions(index, includepreandpost) / getSession().size();}
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
