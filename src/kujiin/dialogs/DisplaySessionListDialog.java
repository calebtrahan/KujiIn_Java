package kujiin.dialogs;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import java.util.List;

public class DisplaySessionListDialog extends Stage {

    public TableView<SessionRow> sessionsTableView;
    public TableColumn<SessionRow, String> DateColumn;
    public TableColumn<SessionRow, Integer> RinColumn;
    public TableColumn<SessionRow, Integer> KyoColumn;
    public TableColumn<SessionRow, Integer> TohColumn;
    public TableColumn<SessionRow, Integer> ShaColumn;
    public TableColumn<SessionRow, Integer> KaiColumn;
    public TableColumn<SessionRow, Integer> JinColumn;
    public TableColumn<SessionRow, Integer> RetsuColumn;
    public TableColumn<SessionRow, Integer> ZaiColumn;
    public TableColumn<SessionRow, Integer> ZenColumn;
    public TableColumn<SessionRow, Integer> TotalColumn;
    public Button CloseButton;
    private ObservableList<SessionRow> sessionlist = FXCollections.observableArrayList();


    public DisplaySessionListDialog(Parent parent, List<Session> sessionlist) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplaySessionList.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("New Goal");}
        catch (IOException e) {e.printStackTrace();}
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
        ArrayList<SessionRow> sessionRows = new ArrayList<>();
        int count = 1;
        for (Session i : sessionlist) {
            sessionRows.add(new SessionRow(count, i.getDate_Practiced(), i.getPresession_Duration(), i.getRin_Duration(),
                    i.getKyo_Duration(), i.getToh_Duration(), i.getSha_Duration(), i.getKai_Duration(), i.getJin_Duration(),
                    i.getRetsu_Duration(), i.getZai_Duration(), i.getZen_Duration(), i.getPostsession_Duration(),
                    i.getTotal_Session_Duration()));
            count++;
        }
        ObservableList<SessionRow> rowlist = FXCollections.observableArrayList();
        rowlist.addAll(sessionRows);
        sessionsTableView.setItems(rowlist);
    }

    public void closeDialog(Event event) {this.close();}

    public class SessionRow {
        public IntegerProperty id;
        public StringProperty datepracticed;
        public IntegerProperty presession;
        public IntegerProperty rin;
        public IntegerProperty kyo;
        public IntegerProperty toh;
        public IntegerProperty sha;
        public IntegerProperty kai;
        public IntegerProperty jin;
        public IntegerProperty retsu;
        public IntegerProperty zai;
        public IntegerProperty zen;
        public IntegerProperty postsession;
        public IntegerProperty total;

        public SessionRow(int id, String datepracticed, int presession, int rin, int kyo, int toh, int sha, int kai, int jin, int retsu, int zai, int zen, int postsession, int total) {
            this.id = new SimpleIntegerProperty(id);
            this.datepracticed = new SimpleStringProperty(datepracticed);
            this.presession = new SimpleIntegerProperty(presession);
            this.rin = new SimpleIntegerProperty(rin);
            this.kyo = new SimpleIntegerProperty(kyo);
            this.toh = new SimpleIntegerProperty(toh);
            this.sha = new SimpleIntegerProperty(sha);
            this.kai = new SimpleIntegerProperty(kai);
            this.jin = new SimpleIntegerProperty(jin);
            this.retsu = new SimpleIntegerProperty(retsu);
            this.zai = new SimpleIntegerProperty(zai);
            this.zen = new SimpleIntegerProperty(zen);
            this.postsession = new SimpleIntegerProperty(postsession);
            this.total = new SimpleIntegerProperty(total);
        }

        public String toString() {
            String a = String.format("%s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s",
                    id.getValue(), datepracticed.getValue(), presession.getValue(), rin.getValue(), kyo.getValue(),
                    toh.getValue(), sha.getValue(), kai.getValue(), jin.getValue(), retsu.getValue(), zai.getValue(),
                    zen.getValue(), postsession.getValue(), total.getValue());
            return a;
        }

        public StringProperty getDatepracticed() {
            return datepracticed;
        }
    }
}
