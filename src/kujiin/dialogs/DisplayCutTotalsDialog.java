package kujiin.dialogs;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import kujiin.This_Session;
import kujiin.Tools;
import kujiin.util.xml.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DisplayCutTotalsDialog extends Stage {
    public TableView<TotalProgressRow> progresstable;
    public TableColumn<TotalProgressRow, String> NameColumn;
    public TableColumn<TotalProgressRow, String> ProgressColumn;
    public TableColumn<TotalProgressRow, Integer> NumberColumn;
    private List<Session> allsessions;

    public DisplayCutTotalsDialog(List<Session> allsessions) {
        this.allsessions = allsessions;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayCutTotalsDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Current Goals");}
        catch (IOException e) {e.printStackTrace();}
        NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
        NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        ProgressColumn.setCellValueFactory(cellData -> cellData.getValue().formattedduration);
        populatetable();
    }

    public void populatetable() {
        ArrayList<TotalProgressRow> allrows = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            int durationinmins = 0;
            for (Session x : allsessions) {durationinmins += x.getcutduration(i);}
            String duration;
            if (durationinmins > 0) {duration = Tools.minutestoformattedhoursandmins(durationinmins);}
            else {duration = "-";}
            allrows.add(new TotalProgressRow(i, This_Session.allnames.get(i), duration));
        }
        progresstable.getItems().addAll(allrows);
    }

    public class TotalProgressRow {
        private IntegerProperty number;
        private StringProperty name;
        private StringProperty formattedduration;

        public TotalProgressRow(Integer id, String name, String formattedduration) {
            this.number = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.formattedduration = new SimpleStringProperty(formattedduration);
        }
    }
}
