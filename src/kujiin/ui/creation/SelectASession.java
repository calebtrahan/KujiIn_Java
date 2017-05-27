package kujiin.ui.creation;

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
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.util.Util;
import kujiin.xml.*;

import java.io.IOException;
import java.util.UUID;

public class SelectASession extends StyledStage {
    public TableView<RecentSessionTableItem> RecentSessionsTable;
    public TableColumn<RecentSessionTableItem, String> NumberColumn;
    public TableColumn<RecentSessionTableItem, String> DatePracticedColumn;
    public TableColumn<RecentSessionTableItem, String> PlaybackItemsColumn;
    public TableColumn<RecentSessionTableItem, String> DurationColumn;
    public Button ShowMoreButton;
    public Button RemoveButton;
    public Button ViewDetailsButton;
    public Button OpenButton;
    public Button CancelButton;
    private int recentsessionmax = 10;
    private boolean accepted = false;
    private Sessions sessions;
    private Session selectedsession;
    private FavoriteSession favoriteSession;
    private FavoriteSessions favoriteSessions;
    private SelectSessionType selectSessionType;
    private Preferences preferences;


    public SelectASession(Preferences preferences, Sessions sessions) {
        try {
            this.preferences = preferences;
            this.sessions = sessions;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/SelectASession.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            RemoveButton.setVisible(false);
            setTitle("Open Recent Sessions");
            selectSessionType = SelectSessionType.RECENT;
            populatetable();
            ViewDetailsButton.setDisable(true);
            OpenButton.setDisable(true);
            RecentSessionsTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                RecentSessionTableItem recentSessionTableItem = RecentSessionsTable.getSelectionModel().getSelectedItem();
                if (recentSessionTableItem != null) {
                    if (newValue != null) {selectedsession = sessions.get(recentSessionTableItem.id);}
                    else {selectedsession = null;}
                }
                ViewDetailsButton.setDisable(newValue == null);
                OpenButton.setDisable(newValue == null);
            });
            NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number);
            DatePracticedColumn.setCellValueFactory(cellData -> cellData.getValue().datepracticed);
            PlaybackItemsColumn.setCellValueFactory(cellData -> cellData.getValue().playbackitems);
            DurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
        } catch (IOException ignored) {}
    }
    public SelectASession(Preferences preferences, FavoriteSessions favoriteSessions) {
        try {
            this.preferences = preferences;
            this.favoriteSessions = favoriteSessions;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/SelectASession.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setTitle("Open Favorite Session");
            selectSessionType = SelectSessionType.FAVORITE;
            populatetable();
            ViewDetailsButton.setDisable(true);
            OpenButton.setDisable(true);
            ShowMoreButton.setVisible(false);
            RecentSessionsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (newValue != null) {favoriteSession = favoriteSessions.get(newValue.id);}
                    else {favoriteSession = null;}
                }
                ViewDetailsButton.setDisable(newValue == null);
                OpenButton.setDisable(newValue == null);
                RemoveButton.setDisable(newValue == null);
            });
            RemoveButton.setOnAction(event -> removefavoritesession());
            NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number);
            DatePracticedColumn.setCellValueFactory(cellData -> cellData.getValue().datepracticed);
            PlaybackItemsColumn.setCellValueFactory(cellData -> cellData.getValue().playbackitems);
            DurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
            NumberColumn.prefWidthProperty().bind(RecentSessionsTable.widthProperty().multiply(2 / 5));
            DatePracticedColumn.prefWidthProperty().bind(RecentSessionsTable.widthProperty().multiply(1 / 5));
            PlaybackItemsColumn.prefWidthProperty().bind(RecentSessionsTable.widthProperty().multiply(1 / 5));
            DurationColumn.prefWidthProperty().bind(RecentSessionsTable.widthProperty().multiply(1 / 5));
            NumberColumn.setText("Name");
            PlaybackItemsColumn.setText("Items");
        } catch (IOException ignored) {}
    }


// Getters And Setters
    public boolean isAccepted() {
        return accepted;
    }
    public Session getSelectedsession() {
        switch (selectSessionType) {
            case FAVORITE:
                return favoriteSession.getSession();
            case RECENT:
                return selectedsession;
        }
        return null;
    }

// Button Actions
    public void showmorerecentsessions() {recentsessionmax+= 10; populatetable();}
    public void viewsessiondetails() {
        switch (selectSessionType) {
            case RECENT:
                if (selectedsession != null) {
                    SessionDetails sessionDetails = new SessionDetails(selectedsession, "Session Details", false);
                    sessionDetails.initModality(Modality.APPLICATION_MODAL);
                    sessionDetails.showAndWait();
                }
                break;
            case FAVORITE:
                if (favoriteSession != null) {
                    SessionDetails sessionDetails = new SessionDetails(favoriteSession.getSession(), "Favorite Session Details", false);
                    sessionDetails.initModality(Modality.APPLICATION_MODAL);
                    sessionDetails.showAndWait();
                }
                break;
        }


    }
    public void removefavoritesession() {
        if (favoriteSession != null && new ConfirmationDialog(preferences, "Remove Favorite Session", "This Will Remove " + favoriteSession.getName() + " From Favorite Sessions", "Continue?").getResult()) {
            favoriteSessions.remove(favoriteSession);
            populatetable();
        }
    }
    public void open() {
        switch (selectSessionType) {
            case RECENT:
                accepted = selectedsession != null;
                break;
            case FAVORITE:
                accepted = favoriteSession != null;
                break;
        }
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
            int size;
            switch (selectSessionType) {
                case FAVORITE:
                    int count = 1;
                    for (FavoriteSession i : favoriteSessions.getFavoriteSessions()) {
                        tableitems.add(new RecentSessionTableItem(String.valueOf(count), i));
                        count++;
                    }
                    break;
                case RECENT:
                    Session selectedsession;
                    int number = 1;
                    size = sessions.getSession().size() - 1;
                    int leftover;
                    if (size > recentsessionmax) {leftover = size - recentsessionmax;}
                    else {leftover = 0; ShowMoreButton.setVisible(false);}
                    for (int i = size; i >= leftover; i--) {
                        selectedsession = sessions.getSession().get(i);
                        tableitems.add(new RecentSessionTableItem(String.valueOf(number), selectedsession));
                        number++;
                    }
                    break;
            }
        } catch (IndexOutOfBoundsException ignored) {ignored.printStackTrace();
        } finally {RecentSessionsTable.setItems(tableitems);}
    }

    class RecentSessionTableItem {
        public UUID id;
        public StringProperty number;
        public StringProperty datepracticed;
        public StringProperty playbackitems;
        public StringProperty duration;

        public RecentSessionTableItem(String number, Session session) {
            id = session.getId();
            this.number = new SimpleStringProperty(number);
            this.datepracticed = new SimpleStringProperty(session.getDate_Practiced().format(Util.dateFormat));
            this.playbackitems = new SimpleStringProperty(String.valueOf(session.getPlaybackItems().size()));
            this.duration = new SimpleStringProperty(Util.formatdurationtoStringDecimalWithColons(session.getSessionPracticedTime()));
        }
        public RecentSessionTableItem(String number, FavoriteSession favoriteSession) {
            id = favoriteSession.getId();
            Session session = favoriteSession.getSession();
            this.number = new SimpleStringProperty(number);
            this.datepracticed = new SimpleStringProperty(session.getDate_Practiced().format(Util.dateFormat));
            this.playbackitems = new SimpleStringProperty(String.valueOf(session.getPlaybackItems().size()));
            this.duration = new SimpleStringProperty(Util.formatdurationtoStringDecimalWithColons(session.getExpectedSessionDuration()));
        }

    }
    enum SelectSessionType {
        FAVORITE, RECENT
    }

}