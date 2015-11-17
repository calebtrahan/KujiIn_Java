package kujiin.dialogs;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import kujiin.util.xml.Session;

import java.io.IOException;
import java.util.ArrayList;

public class DisplayPrematureEndingsDialog extends Stage {

    public TableView<PrematureEnding> prematureendingTable;
    public TableColumn<PrematureEnding, String> DateColumn;
    public TableColumn<PrematureEnding, String> LastCutPracticedColumn;
    public TableColumn<PrematureEnding, String> ExpectedSessionColumn;
    public Button CloseButton;
    public TableColumn<PrematureEnding, String> ReasonColumn;
    public ArrayList<Session> sessionwithprematureendings;

    public DisplayPrematureEndingsDialog(Parent parent, ArrayList<Session> sessionwithprematureendings) {
        this.sessionwithprematureendings = sessionwithprematureendings;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayPrematureEndings.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Premature Endings");}
        catch (IOException e) {e.printStackTrace();}
        ObservableList<PrematureEnding> prematureEndings = FXCollections.observableArrayList();
        for (Session i : sessionwithprematureendings) {
            prematureEndings.add(new PrematureEnding(i.getDate_Practiced(), i.getLast_Cut_Practiced_Before_Premature_Ending(), i.getExpected_Session_List(), i.getPremature_Ending_Reason()));
        }
        DateColumn.setCellValueFactory(cellData -> cellData.getValue().date);
        LastCutPracticedColumn.setCellValueFactory(cellData -> cellData.getValue().lastcutpracticed);
        ExpectedSessionColumn.setCellValueFactory(cellData -> cellData.getValue().expectedsessionlist);
        ReasonColumn.setCellValueFactory(cellData -> cellData.getValue().reason);
        prematureendingTable.setItems(prematureEndings);
    }

    public void closeDialog(Event event) {this.close();}

    public class PrematureEnding {
        public StringProperty date;
        public StringProperty lastcutpracticed;
        public StringProperty expectedsessionlist;
        public StringProperty reason;

        public PrematureEnding(String date, String lastcutpracticed, String expectedsessionlist, String reason) {
            this.date = new SimpleStringProperty(date);
            this.lastcutpracticed = new SimpleStringProperty(lastcutpracticed);
            this.expectedsessionlist = new SimpleStringProperty(expectedsessionlist);
            this.reason = new SimpleStringProperty(reason);
        }
    }

}
