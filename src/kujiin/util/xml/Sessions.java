package kujiin.util.xml;

import kujiin.This_Session;
import kujiin.dialogs.DisplayCutTotalsDialog;
import kujiin.dialogs.DisplayPrematureEndingsDialog;
import kujiin.dialogs.DisplaySessionListDialog;
import kujiin.util.lib.GuiUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Sessions")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Sessions {
    private List<Session> Session;

    public Sessions() {
        try {populatefromxml();}
        catch (JAXBException ignored) {}
    }

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
    public int getgrandtotaltimepracticedinminutes(boolean includepreandpost) {
        try {
            int totalminutes = 0;
            for (kujiin.util.xml.Session i : getSession()) {
                if (includepreandpost) {for (int x=0; x<11;x++) {totalminutes += i.getcutduration(x);}}
                else {for (int x=1; x<10;x++) {totalminutes += i.getcutduration(x);}}
            }
            return totalminutes;
        } catch (NullPointerException e) {return 0;}
    }
    public float getaveragesessiontimeinminutes(boolean includepreandpost) {
        try {return (float) getgrandtotaltimepracticedinminutes(includepreandpost) / getSession().size();}
        catch (NullPointerException ignored) {return 0;}
    }
    public ArrayList<Session> getsessionwithprematureendings() {
        try {
            ArrayList<Session> sessionswithprematureendings = new ArrayList<>();
            for (kujiin.util.xml.Session i : getSession()) {
                if (i.wasendedPremature()) {sessionswithprematureendings.add(i);}
            }
            return sessionswithprematureendings;
        } catch (NullPointerException e) {return new ArrayList<>();}
    }
    public void displaylistofsessions() {
        if (getSession() == null || getSession().size() == 0) {
            GuiUtils.showinformationdialog("Cannot Display", "Nothing To Display", "Need To Practice At Least One Session To Use This Feature");
        } else {
            new DisplaySessionListDialog(null, getSession()).showAndWait();
        }
    }
    public void displayprematureendings() {
        ArrayList<Session> prematuresessionlist = getsessionwithprematureendings();
        if (prematuresessionlist.size() > 0) {
            DisplayPrematureEndingsDialog a = new DisplayPrematureEndingsDialog(null, prematuresessionlist);
            a.showAndWait();
        } else {
            GuiUtils.showinformationdialog("Cannot Display", "No Premature Endings", "No Premature Endings To Display");
        }
    }
    public void displaycutprogress() {
        if (getSession() != null) {new DisplayCutTotalsDialog(getSession());}
        else {GuiUtils.showinformationdialog("Cannot Display", "Nothing To Display", "Need To Practice At Least One Session To Use This Feature");}
    }
    public int getsessioncount() {
        if (getSession() != null) {return getSession().size();}
        else {return 0;}
    }

}
