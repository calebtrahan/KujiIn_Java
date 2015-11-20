package kujiin.util.xml;

import javafx.scene.control.Alert;
import kujiin.This_Session;
import kujiin.dialogs.DisplayPrematureEndingsDialog;
import kujiin.dialogs.DisplaySessionListDialog;
import kujiin.util.GuiUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
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

    public Sessions() {}

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
        return (float) getgrandtotaltimepracticedinminutes(includepreandpost) / getSession().size();
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
        if (getSession() == null) {
            GuiUtils.showinformationdialog("Cannot Display", "No Sessions", "No Sessions To Display");
        } else {
            if (getSession().size() > 0) {
                DisplaySessionListDialog a = new DisplaySessionListDialog(null, getSession());
                a.showAndWait();
            } else {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("No Sessions");
                a.setHeaderText("Nothing To Display");
                a.setContentText("No Sessions Practiced Yet");
                a.showAndWait();
            }
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
    public int getsessioncount() {
        if (getSession() != null) {return getSession().size();}
        else {return 0;}
    }


}
