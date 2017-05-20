package kujiin.ui.creation;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.ui.dialogs.SessionDetails;
import kujiin.util.Util;
import kujiin.xml.Session;
import kujiin.xml.Sessions;

import java.io.IOException;

public class OpenRecentSessions extends StyledStage {
    public TableView<RecentSessionTableItem> RecentSessionsTable;
    public TableColumn<RecentSessionTableItem, Integer> NumberColumn;
    public TableColumn<RecentSessionTableItem, String> DatePracticedColumn;
    public TableColumn<RecentSessionTableItem, String> PlaybackItemsColumn;
    public TableColumn<RecentSessionTableItem, String> DurationColumn;
    public Button ShowMoreButton;
    public Button ViewDetailsButton;
    public Button OpenButton;
    public Button CancelButton;
    private int recentsessionmax = 10;
    private boolean accepted = false;
    private Sessions sessions;
    private Session selectedsession;

    public OpenRecentSessions(Sessions sessions) {
        try {
            this.sessions = sessions;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/OpenRecentSessions.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setTitle("Open Recent Sessions");
            populatetable();
            ViewDetailsButton.setDisable(true);
            OpenButton.setDisable(true);
            RecentSessionsTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {selectedsession = sessions.get((sessions.getSession().size() - 1) - newValue.intValue());}
                else {selectedsession = null;}
                ViewDetailsButton.setDisable(newValue == null);
                OpenButton.setDisable(newValue == null);
            });
            NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
            DatePracticedColumn.setCellValueFactory(cellData -> cellData.getValue().datepracticed);
            PlaybackItemsColumn.setCellValueFactory(cellData -> cellData.getValue().playbackitems);
            DurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
        } catch (IOException ignored) {}
    }

// Getters And Setters
    public boolean isAccepted() {
        return accepted;
    }
    public Session getSelectedsession() {
        return selectedsession;
    }

// Button Actions
    public void showmorerecentsessions() {recentsessionmax+= 10; populatetable();}
    public void viewsessiondetails() {
        if (selectedsession != null) {
            SessionDetails sessionDetails = new SessionDetails(selectedsession, "Session Details", false);
            sessionDetails.initModality(Modality.APPLICATION_MODAL);
            sessionDetails.showAndWait();
        }
    }
    public void open() {
        accepted = selectedsession != null;
        close();
    }
    public void cancel() {
        accepted = false;
        close();
    }

// Utility Methods
    private void populatetable() {
        ObservableList<RecentSessionTableItem> tableitems = FXCollections.observableArrayList();
        try {
            int size = sessions.getSession().size() - 1;
            int number = 1;
            for (int i = size; i > size - recentsessionmax; i--) {
                Session session = sessions.getSession().get(i);
                tableitems.add(new RecentSessionTableItem(number, session.getDate_Practiced().format(Util.dateFormat), String.valueOf(session.getPlaybackItems().size()), Util.formatdurationtoStringDecimalWithColons(session.getSessionPracticedTime())));
                number++;
            }
        } catch (IndexOutOfBoundsException ignored) {
        } finally {RecentSessionsTable.setItems(tableitems);}
    }

    class RecentSessionTableItem {
        private IntegerProperty number;
        private StringProperty datepracticed;
        private StringProperty playbackitems;
        private StringProperty duration;

        public RecentSessionTableItem(Integer number, String datepracticed, String playbackitems, String duration) {
            this.number = new SimpleIntegerProperty(number);
            this.datepracticed = new SimpleStringProperty(datepracticed);
            this.playbackitems = new SimpleStringProperty(playbackitems);
            this.duration = new SimpleStringProperty(duration);
        }
    }
}
