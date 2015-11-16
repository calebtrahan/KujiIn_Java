package kujiin.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import kujiin.This_Session;
import kujiin.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class SessionNotWellformedDialog extends Stage {
    public Button returntoCreatorButton;
    public Button addmissingCutsButton;
    public ListView sessionlistview;
    public Label sessionmissingcutsLabel;
    public Button CreateAnywayButton;
    public Label explanationLabel;
    private ArrayList<Integer> textfieldvalues;
    private int lastcutindex;
    private int invocationduration;
    private boolean createsession;

    public SessionNotWellformedDialog(Parent parent, ArrayList<Integer> textfieldvalues, String cutsmissingtext, int lastcutindex) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionNotWellformedDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Creating This_Session");}
        catch (IOException e) {e.printStackTrace();}
        this.textfieldvalues = textfieldvalues;
        this.lastcutindex = lastcutindex;
        sessionmissingcutsLabel.setText(cutsmissingtext);
        populatelistview();
        explanationLabel.setText(("Your Practiced Cuts Do Not Connect! Due To The Nature Of The Kuji-In I Recommend " +
                "Connecting All Cuts From RIN All The Way To Your Last Cut (") + This_Session.allnames.get(lastcutindex) +
                ") Or Your This_Session Might Not Have The Energy It Needs");
        setCreatesession(false);
    }

    public void populatelistview() {
        ArrayList<String> items = new ArrayList<>();
        ObservableList<Text> sessionitems = FXCollections.observableArrayList();
        int count = 0;
        boolean thisitemmissing;
        for (int i = 0; i < textfieldvalues.size(); i++) {
            String name = This_Session.allnames.get(i);
            String minutes;
            if (i <= lastcutindex || i == textfieldvalues.size() - 1) {
                thisitemmissing = false;
                if (i == 0 || i == 10) {
                    if (textfieldvalues.get(i) == 0) {
                        minutes = "Ramp Only";
                    } else {
                        String time = Tools.minutestoformattedhoursandmins(textfieldvalues.get(i));
                        minutes = String.format("%s + Ramp", time);
                    }
                } else {
                    if (textfieldvalues.get(i) == 0) {
                        thisitemmissing = true;
                        minutes = " Missing Value! ";
                    }
                    else {minutes = Tools.minutestoformattedhoursandmins(textfieldvalues.get(i));}
                }
                String txt = String.format("%d: %s (%s )", count + 1, name, minutes);
                Text item = new Text();
                item.setText(txt);
                if (thisitemmissing) item.setStyle("-fx-font-weight:bold; -fx-font-style: italic;");
                sessionitems.add(item);
                count++;
            }
        }
        sessionlistview.setItems(sessionitems);
    }

    public void returntoCreator(Event event) {this.close();}

    public void addmissingcutstoSession(Event event) {
        CutInvocationDialog cutdurationdialog = new CutInvocationDialog(null);
        cutdurationdialog.showAndWait();
        setInvocationduration(cutdurationdialog.getCutinvocationduration());
        setCreatesession(true);
        this.close();
    }

    public void createSessionwithoutmissingcuts(Event event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation");
        a.setHeaderText("This Will Create A This_Session That Isn't Well-Formed");
        a.setContentText("Really Create?");
        Optional<ButtonType> b = a.showAndWait();
        if (b.isPresent() && b.get() == ButtonType.OK) {
            setCreatesession(true);
            this.close();
        }
    }

    public int getInvocationduration() {
        return invocationduration;
    }

    public void setInvocationduration(int invocationduration) {
        this.invocationduration = invocationduration;
    }

    public boolean isCreatesession() {
        return createsession;
    }

    public void setCreatesession(boolean createsession) {this.createsession = createsession;}
}
