package kujiin.dialogs;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import kujiin.Database;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DisplaySessionListDialog extends Stage {

    public TableView<Database.SessionRow> sessionsTableView;

    public TableColumn<Database.SessionRow, String> DateColumn;
    public TableColumn<Database.SessionRow, Integer> RinColumn;
    public TableColumn<Database.SessionRow, Integer> KyoColumn;
    public TableColumn<Database.SessionRow, Integer> TohColumn;
    public TableColumn<Database.SessionRow, Integer> ShaColumn;
    public TableColumn<Database.SessionRow, Integer> KaiColumn;
    public TableColumn<Database.SessionRow, Integer> JinColumn;
    public TableColumn<Database.SessionRow, Integer> RetsuColumn;
    public TableColumn<Database.SessionRow, Integer> ZaiColumn;
    public TableColumn<Database.SessionRow, Integer> ZenColumn;
    public TableColumn<Database.SessionRow, Integer> TotalColumn;
    public Button CloseButton;
    private Database sessiondb;
    private ObservableList<Database.SessionRow> sessionlist = FXCollections.observableArrayList();


    public DisplaySessionListDialog(Parent parent, Database sessiondb) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplaySessionList.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("New Goal");}
        catch (IOException e) {e.printStackTrace();}
        this.sessiondb = sessiondb;
        for (Database.SessionRow i : sessiondb.sessions) {
            sessionlist.add(i);
        }
        DateColumn.setCellValueFactory(cellData -> cellData.getValue().datepracticed);
        RinColumn.setCellValueFactory(cellData -> cellData.getValue().rin.asObject());
        KyoColumn.setCellValueFactory(cellData -> cellData.getValue().kyo.asObject());
        TohColumn.setCellValueFactory(cellData -> cellData.getValue().toh.asObject());
        ShaColumn.setCellValueFactory(cellData -> cellData.getValue().sha.asObject());
        KaiColumn.setCellValueFactory(cellData -> cellData.getValue().kai.asObject());
        JinColumn.setCellValueFactory(cellData -> cellData.getValue().jin.asObject());
        RetsuColumn.setCellValueFactory(cellData -> cellData.getValue().retsu.asObject());
        ZaiColumn.setCellValueFactory(cellData -> cellData.getValue().zai.asObject());
        ZenColumn.setCellValueFactory(cellData -> cellData.getValue().zen.asObject());
        TotalColumn.setCellValueFactory(cellData -> cellData.getValue().total.asObject());
        sessionsTableView.setItems(sessionlist);
    }

    public void closeDialog(Event event) {this.close();}
}
