package kujiin.util.xml;

import javafx.scene.control.Alert;
import kujiin.This_Session;
import kujiin.dialogs.DisplayPrematureEndingsDialog;
import kujiin.dialogs.DisplaySessionListDialog;

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
        int totalminutes = 0;
        for (kujiin.util.xml.Session i : getSession()) {
            if (includepreandpost) {
                for (int x=0; x<11;x++) {totalminutes += i.getcutduration(x);}
            } else {
                for (int x=1; x<10;x++) {totalminutes += i.getcutduration(x);}
            }
        }
        return totalminutes;
    }
    public float getaveragesessiontimeinminutes(boolean includepreandpost) {
        return (float) getgrandtotaltimepracticedinminutes(includepreandpost) / getSession().size();
    }
    public ArrayList<Session> getsessionwithprematureendings() {
        ArrayList<Session> sessionswithprematureendings = new ArrayList<>();
        for (kujiin.util.xml.Session i : getSession()) {
            if (i.wasendedPremature()) {sessionswithprematureendings.add(i);}
        }
        return sessionswithprematureendings;
    }
    public void displaylistofsessions() {
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
    public void displayprematureendings() {
        ArrayList<Session> prematuresessionlist = getsessionwithprematureendings();
        if (prematuresessionlist.size() > 0) {
            DisplayPrematureEndingsDialog a = new DisplayPrematureEndingsDialog(null, prematuresessionlist);
            a.showAndWait();
        } else {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("No Premature Endings");
            a.setHeaderText("Nothing To Display");
            a.setContentText("No Premature Endings. Great Work!");
            a.showAndWait();
        }
    }
}
