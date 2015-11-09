package kujiin.dialogs;

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
import kujiin.Database;

import java.io.IOException;

public class DisplayPrematureEndingsDialog extends Stage {

    public TableView<Database.PrematureEnding> prematureendingTable;
    public TableColumn<Database.PrematureEnding, String> DateColumn;
    public TableColumn<Database.PrematureEnding, String> LastCutPracticedColumn;
    public TableColumn<Database.PrematureEnding, String> ExpectedSessionColumn;
    public Button CloseButton;
    public TableColumn<Database.PrematureEnding, String> ReasonColumn;

    public DisplayPrematureEndingsDialog(Parent parent, Database sessiondb) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayPrematureEndings.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Premature Endings");}
        catch (IOException e) {e.printStackTrace();}
        ObservableList<Database.PrematureEnding> prematureEndings = FXCollections.observableArrayList();
        prematureEndings.addAll(sessiondb.prematureendings);
        // Set Bindings
        DateColumn.setCellValueFactory(cellData -> cellData.getValue().date);
        LastCutPracticedColumn.setCellValueFactory(cellData -> cellData.getValue().lastcutpracticed);
        ExpectedSessionColumn.setCellValueFactory(cellData -> cellData.getValue().expectedsessionlist);
        ReasonColumn.setCellValueFactory(cellData -> cellData.getValue().reason);
        prematureendingTable.setItems(prematureEndings);
    }

    public void closeDialog(Event event) {this.close();}
}
