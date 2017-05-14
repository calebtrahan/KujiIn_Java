package kujiin.ui.creation;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import kujiin.ui.boilerplate.StyledStage;
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

    public OpenRecentSessions(Sessions sessions) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/OpenRecentSessions.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setTitle("Open Recent Sessions");
        } catch (IOException ignored) {}
    }

// Getters And Setters
    public boolean isAccepted() {
        return accepted;
    }

// Button Actions
    public void showmorerecentsessions() {recentsessionmax+= 10; populatetable();}
    public void viewsessiondetails() {}
    public void open() {
        accepted = true;
    }
    public void cancel() {}

// Utility Methods
    public void populatetable() {}

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
