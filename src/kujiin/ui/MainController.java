package kujiin.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kujiin.ui.boilerplate.IconImageView;
import kujiin.ui.creation.AddOrEditAmbience;
import kujiin.ui.creation.SetOrAdjustDuration;
import kujiin.ui.dialogs.AmbienceEditor_Advanced;
import kujiin.ui.dialogs.AmbienceEditor_Simple;
import kujiin.ui.dialogs.ChangeProgramOptions;
import kujiin.ui.dialogs.EditReferenceFiles;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.ui.export.Exporter;
import kujiin.ui.playback.Player;
import kujiin.ui.table.CreatedSessionTableItem;
import kujiin.util.enums.IconDisplayType;
import kujiin.util.enums.ProgramState;
import kujiin.util.enums.StartupCheckType;
import kujiin.xml.*;
import kujiin.xml.Preferences;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static kujiin.xml.Preferences.*;
// New Design
    // TODO Set/Edit Preset Ambience On Session Details Dialog

// Bugs To Fix
    // TODO Find NullPointer During Session Playback On Thread During Transition
    // TODO Startup Checks
        // Percentage Isn't Calculating Correctly
    // TODO Find A Way To Reset Session After Stop Animation Ends

// Features To Test

// Additional Features To Definitely Add
    // TODO Create Goal Progress Similar To Session Details And Add To Session Details Dialog
    // TODO Exporter
    // TODO Add Logging (And Write To Log File) For Troubleshooting

// Optional Additional Features
    // TODO Refactor Freq Files So There Can Be 2 or 3 Different Frequency Octaves For The Same Session Part (Use enum FreqType)

// Mind Workstation
    // TODO Add Low (And Possibly Medium) Variations Of All Session Parts
    // TODO Add Ramps To Connect Low (And Possibly Medium) Variations Of Session Parts With Each Other

public class MainController implements Initializable {
    private Scene Scene;
    private Stage Stage;
// Controller Classes
    private Preferences preferences;
    private AvailableAmbiences availableAmbiences;
    private AvailableEntrainments AvailableEntrainments;
    private Sessions sessions;
    private FavoriteSessions favoriteSessions;
    private Goals goals;
//    private StartupChecks startupChecks;
    protected StartupCheckType startupCheckType = StartupCheckType.ENTRAINMENT;
    private ProgramState programState = ProgramState.IDLE;
// GUI Fields
    // Top Menu
    public MenuItem PreferenceMenuItem;
    public MenuItem CloseMenuItem;
    public MenuItem EditAvailableAmbienceMenuItem;
    public MenuItem EditReferenceFilesMenuItem;
    public MenuItem AboutMenuItem;
    // Play/Export Tab
    public MenuButton CreateSessionMenu;
    public MenuItem CreateBlankSessionMenuItem;
    public MenuItem OpenPresetsMenuItem;
    public Button OpenFileButton;
    public Button OpenRecentSessionsButton;
    public Button OpenFavoritesButton;
    public TableView<CreatedSessionTableItem> CreatedTableView;
    public TableColumn<CreatedSessionTableItem, Integer> CreatedTableNumberColumn;
    public TableColumn<CreatedSessionTableItem, String> CreatedTableItemColumn;
    public TableColumn<CreatedSessionTableItem, String> CreatedTableDurationColumn;
    public TableColumn<CreatedSessionTableItem, String> CreatedTableAmbienceColumn;
    public MenuButton AddItemsMenu;
    public Menu AddAllMenu;
    public MenuItem AddAllKujiInMenuItem;
    public MenuItem AddAllElementsMenuItem;
    public MenuItem AddQiGongMenuItem;
    public MenuItem AddRinMenuItem;
    public MenuItem AddKyoMenuItem;
    public MenuItem AddTohMenuItem;
    public MenuItem AddShaMenuItem;
    public MenuItem AddKaiMenuItem;
    public MenuItem addJinMenuItem;
    public MenuItem AddRetsuMenuItem;
    public MenuItem AddZaiMenuItem;
    public MenuItem AddZenMenuItem;
    public MenuItem AddEarthMenuItem;
    public MenuItem AddAirMenuItem;
    public MenuItem AddFireMenuItem;
    public MenuItem AddWaterMenuItem;
    public MenuItem AddVoidMenuItem;
    public Button EditDurationsButton;
    public Button AddEditAmbienceButton;
    public Button MoveUpButton;
    public Button MoveDownButton;
    public Button RemoveButton;
    public Button AddToFavoritesButton;
    public Button PlayButton;
    public Button SaveAsFileButton;
    public Button ExportButton;
    public Label CreatorStatusBar;
    // Progress Pane
        // Overview Tab
    public BarChart ProgressOverviewBarChart;
    public TextField ProgressOverviewTotalTimePracticed;
    public TextField ProgressOverviewItemWithMostProgress;
    public PieChart ProgressBalancePieChart;
        // Session Browser Tab
    public ListView SessionBrowser_SelectSessionListView;
    public DatePicker SessionBrowser_Filter_DateRange_From;
    public DatePicker SessionBrowser_Filter_DateRange_To;
    public TextField SessionBrowser_Filter_Duration_From_Hours;
    public TextField SessionBrowser_Filter_Duration_From_Minutes;
    public TextField SessionBrowser_Filter_Duration_To_Hours;
    public TextField SessionBrowser_Filter_Duration_To_Minutes;
    public TableView SessionBrowser_DetailsTable;
    public TableColumn SessionBrowser_DetailsTable_NumberColumn;
    public TableColumn SessionBrowser_DetailsTable_ItemColumn;
    public TableColumn SessionBrowser_DetailsTable_TimePracticedColumn;
    public TextField SessionBrowser_Details_TotalDuration;
    // Goals Pane
        // Overview Tab
    public TableView GoalsOverview_Table;
    public TableColumn GoalsOverview_ItemColumn;
    public TableColumn GoalsOverview_PracticedTimeColumn;
    public TableColumn GoalsOverview_CurrentGoalColumn;
    public TableColumn GoalsOverview_PercentCompletedColumn;
    public TableColumn GoalsOverview_GoalsCompletedColumn;
        // Individual Tab
    public ChoiceBox GoalsIndividual_SelectedSessionItemChoiceBox;
    public TextField GoalsIndividual_PracticedTime;
    public TableView GoalsIndividual_Table;
    public TableColumn GoalsIndividual_GoalTimeColumn;
    public TableColumn GoalsIndividual_IsCompletedColumn;
    public TableColumn GoalsIndividual_DateCompletedColumn;
    public TableColumn GoalsIndividual_PercentCompletedColumn;
    public CheckBox GoalsIndividual_ShowCompletedGoalsCheckbox;
    public Button GoalsIndividual_SetNewGoalButton;
    public Button GoalsIndividual_DeleteGoalButton;
// My Fields
    // Play/Export Tab
    private Session createdsession;
    private Session.PlaybackItem createdtableselecteditem;
    private ObservableList<CreatedSessionTableItem> createdtableitems = FXCollections.observableArrayList();
    private ArrayList<Session.PlaybackItem> createdtableplaybackitems;


// Getters And Setters
    public Preferences getPreferences() {
        return preferences;
    }
    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }
    public javafx.scene.Scene getScene() {
        return Scene;
    }
    public void setScene(javafx.scene.Scene scene) {
        Scene = scene;
    }
    public javafx.stage.Stage getStage() {
        return Stage;
    }
    public void setStage(javafx.stage.Stage stage) {
        Stage = stage;
    }
    public void setAvailableAmbiences(AvailableAmbiences availableAmbiences) {
        this.availableAmbiences = availableAmbiences;
    }
    public AvailableAmbiences getAvailableAmbiences() {
        return availableAmbiences;
    }
    public kujiin.xml.AvailableEntrainments getAvailableEntrainments() {
        return AvailableEntrainments;
    }
    public void setAvailableEntrainments(kujiin.xml.AvailableEntrainments availableEntrainments) {
        AvailableEntrainments = availableEntrainments;
    }
    public void setSessions(Sessions sessions) {
        this.sessions = sessions;
    }
    public void setFavoriteSessions(FavoriteSessions favoriteSessions) {
        this.favoriteSessions = favoriteSessions;
    }


// Window Methods
    private boolean cleanup() {
    availableAmbiences.marshall();
    AvailableEntrainments.marshall();
    preferences.marshall();
    return true;
}
    private void close() {
        if (cleanup()) {System.exit(0);}
    }


// Setup Methods
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setPreferences(new Preferences());
        getPreferences().unmarshall();
        setupIcons();
        setupCreationTable();
    }
    private void setupCreationTable() {
        CreatedTableNumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
        CreatedTableItemColumn.setCellValueFactory(cellData -> cellData.getValue().itemname);
        CreatedTableDurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
        CreatedTableAmbienceColumn.setCellValueFactory(cellData -> cellData.getValue().ambience);
        CreatedTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> tableselectionchanged());
    }
    private void setupIcons() {
        IconDisplayType dt = preferences.getUserInterfaceOptions().getIconDisplayType();
        if (dt == IconDisplayType.ICONS_AND_TEXT || dt == IconDisplayType.ICONS_ONLY) {
            double fh = 20.0;
            AddItemsMenu.setGraphic(new IconImageView(ICON_ADD, fh));
            CreateSessionMenu.setGraphic(new IconImageView(ICON_ADD, fh));
            OpenFileButton.setGraphic(new IconImageView(ICON_OPENFILE, fh));
            OpenRecentSessionsButton.setGraphic(new IconImageView(ICON_RECENTSESSIONS, fh));
            OpenFavoritesButton.setGraphic(new IconImageView(ICON_FAVORITES, fh));
            EditDurationsButton.setGraphic(new IconImageView(ICON_EDITDURATION, fh));
            AddEditAmbienceButton.setGraphic(new IconImageView(ICON_AMBIENCE, fh));
            MoveUpButton.setGraphic(new IconImageView(ICON_MOVEUP, fh));
            MoveDownButton.setGraphic(new IconImageView(ICON_MOVEDOWN, fh));
            RemoveButton.setGraphic(new IconImageView(ICON_REMOVE, fh));
            AddToFavoritesButton.setGraphic(new IconImageView(ICON_ADDTOFAVORITE, fh));
            PlayButton.setGraphic(new IconImageView(ICON_PLAY, fh));
            SaveAsFileButton.setGraphic(new IconImageView(ICON_EXPORTTODOCUMENT, fh));
            ExportButton.setGraphic(new IconImageView(ICON_EXPORTTOAUDIO, fh));
        }
        if (dt == IconDisplayType.ICONS_ONLY) {
            AddEditAmbienceButton.setText("");
            AddItemsMenu.setText("");
            CreateSessionMenu.setText("");
            OpenFileButton.setText("");
            OpenRecentSessionsButton.setText("");
            OpenFavoritesButton.setText("");
            EditDurationsButton.setText("");
            MoveUpButton.setText("");
            MoveDownButton.setText("");
            RemoveButton.setText("");
            AddToFavoritesButton.setText("");
            PlayButton.setText("");
            SaveAsFileButton.setText("");
            ExportButton.setText("");
        }
        AddItemsMenu.setTooltip(new Tooltip("Add Items"));
        CreateSessionMenu.setTooltip(new Tooltip("Create New Session"));
        OpenFileButton.setTooltip(new Tooltip("Open Session From File"));
        OpenRecentSessionsButton.setTooltip(new Tooltip("Open Recent Session"));
        OpenFavoritesButton.setTooltip(new Tooltip("Open Favorites"));
        EditDurationsButton.setTooltip(new Tooltip("Edit Duration"));
        AddEditAmbienceButton.setTooltip(new Tooltip("Add/Edit Ambience"));
        MoveUpButton.setTooltip(new Tooltip("Move Up In Table"));
        MoveDownButton.setTooltip(new Tooltip("Move Down In Table"));
        RemoveButton.setTooltip(new Tooltip("Remove"));
        AddToFavoritesButton.setTooltip(new Tooltip("Add This Session To Favorites"));
        SaveAsFileButton.setTooltip(new Tooltip("Save As Document"));
        PlayButton.setTooltip(new Tooltip("Play This Session"));
        ExportButton.setTooltip(new Tooltip("Export To Audio File"));
        syncbuttons();
    }


// Top Menu Methods
    public void editpreferences() {
        if (programState == ProgramState.IDLE) {
            ChangeProgramOptions changeProgramOptions = new ChangeProgramOptions(this);
            changeProgramOptions.initModality(Modality.APPLICATION_MODAL);
            changeProgramOptions.initOwner(getStage());
            changeProgramOptions.showAndWait();
            preferences.marshall();
        }
    }
    public void closeprogram() {
        new SetOrAdjustDuration(null).showAndWait();
    }
    public void editavailableambience() {
        if (getPreferences().getAdvancedOptions().getDefaultambienceeditor().equals("Simple")) {new AmbienceEditor_Simple(availableAmbiences, preferences).showAndWait();}
        else if (getPreferences().getAdvancedOptions().getDefaultambienceeditor().equals("Advanced")) {new AmbienceEditor_Advanced(availableAmbiences, preferences).showAndWait();}
    }
    public void editreferencefiles() {
       EditReferenceFiles editReferenceFiles = new EditReferenceFiles(this);
       editReferenceFiles.initOwner(getStage());
       editReferenceFiles.initModality(Modality.APPLICATION_MODAL);
       editReferenceFiles.showAndWait();
    }
    public void aboutthisprogram() {
    }


// Play/Export Tab
    // Create/Open Toolbar Methods
    public void createblanksession() {
        if (createdsession != null && ! new ConfirmationDialog(preferences, "Load New Session",
                "Really Load New Session?", "This will clear any unsaved changes you made to this session").getResult()) {
            return;
        }
        createdsession = new Session();
        populatetable();
    }
    public void createwithpreset() {
    }
    public void opensessionfromfile() {

    }
    public void openrecentsession() {
    }
    public void openfavoritesession() {
    }
    // Creation Table Listeners
    public void setupCreatedSessionTable() {
        CreatedTableNumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
        CreatedTableItemColumn.setCellValueFactory(cellData -> cellData.getValue().itemname);
        CreatedTableDurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
        CreatedTableAmbienceColumn.setCellValueFactory(cellData -> cellData.getValue().ambience);
        CreatedTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> tableselectionchanged());
    }
    private void tableselectionchanged() {
        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
        if (selectedindex != -1) {
            createdtableselecteditem = createdtableplaybackitems.get(selectedindex);
            syncbuttons();
        } else {createdtableselecteditem = null;}
    }
    // Creation Table Methods
    private void add(int availableambienceindex) {
        createdsession.addplaybackitem(availableambienceindex);
        Session.PlaybackItem playbackItem = createdsession.getPlaybackItems().get(createdsession.getPlaybackItems().size() - 1);
        SetOrAdjustDuration setOrAdjustDuration = new SetOrAdjustDuration(playbackItem);
        setOrAdjustDuration.showAndWait();
        if (setOrAdjustDuration.isAccepted()) {
            createdsession.getPlaybackItems().get(createdsession.getPlaybackItems().size() - 1).setDuration(setOrAdjustDuration.getNewduration().toMillis());
        }
        populatetable();
    }
    private void add(int[] availableambienceindexes) {
        for (int i : availableambienceindexes) {createdsession.addplaybackitem(i);}
        SetOrAdjustDuration setOrAdjustDuration = new SetOrAdjustDuration(availableambienceindexes.length);
        setOrAdjustDuration.showAndWait();
        if (setOrAdjustDuration.isAccepted()) {
            int playbackitemssize = createdsession.getPlaybackItems().size();
            for (int i = playbackitemssize - 1; i >= playbackitemssize - availableambienceindexes.length; i--) {
                createdsession.getPlaybackItems().get(i).setDuration(setOrAdjustDuration.getNewduration().toMillis());
            }
        }
        populatetable();
    }
    public void addallitems_kujiin() {
        int[] indexes = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        add(indexes);
        populatetable();
    }
    public void addallitems_elements() {
        int[] indexes = {10, 11, 12, 13, 14};
        add(indexes);
        populatetable();
    }
    public void add_QiGong() {add(0);}
    public void addRin() {add(1);}
    public void addkyo() {add(2);}
    public void addToh() {add(3);}
    public void addSha() {add(4);}
    public void addKai() {add(5);}
    public void addJin() {add(6);}
    public void addRetsu() {add(7);}
    public void addZai() {add(8);}
    public void addZen() {add(9);}
    public void addEarth() {add(10);}
    public void addAir() {add(11);}
    public void addFire() {add(12);}
    public void addWater() {add(13);}
    public void addVoid() {add(14);}
    public void editduration() {
        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
        if (selectedindex != -1 && createdtableselecteditem != null) {
            SetOrAdjustDuration setOrAdjustDuration = new SetOrAdjustDuration(createdtableselecteditem);
            setOrAdjustDuration.initOwner(getStage());
            setOrAdjustDuration.showAndWait();
            if (setOrAdjustDuration.isAccepted()) {
                Session.PlaybackItem playbackItem = createdtableselecteditem;
                playbackItem.setDuration(setOrAdjustDuration.getNewduration().toMillis());
                createdtableplaybackitems.set(selectedindex, playbackItem);
                populatetable();
            }
        } else {new InformationDialog(preferences, "Cannot Edit Duration", "Select A Single Table Item To Edit Duration", null);}
    }
    public void addeditambience() {
        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
        if (selectedindex != -1 && createdtableselecteditem != null) {
            AddOrEditAmbience addOrEditAmbience = new AddOrEditAmbience(preferences, createdtableselecteditem, availableAmbiences);
            addOrEditAmbience.showAndWait();
            if (addOrEditAmbience.isAccepted()) {populatetable();}
        }
    }
    public void moveupincreatortable() {
        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
        if (selectedindex > 0) {
            ArrayList<Session.PlaybackItem> itemsinsession = createdsession.getPlaybackItems();
            Collections.swap(itemsinsession, selectedindex, selectedindex - 1);
            createdsession.setPlaybackItems(itemsinsession);
            populatetable();
        }
    }
    public void movedownincreatortable() {
        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
        if (selectedindex != -1 && selectedindex < createdtableplaybackitems.size() - 1) {
            ArrayList<Session.PlaybackItem> itemsinsession = createdsession.getPlaybackItems();
            Collections.swap(itemsinsession, selectedindex, selectedindex + 1);
            createdsession.setPlaybackItems(itemsinsession);
            populatetable();
        }
    }
    public void removefromcreatortable() {
        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
        if (selectedindex != -1 && createdtableitems != null) {
            createdsession.removeplaybackitem(selectedindex);
            populatetable();
        }
    }
    // Action Toolbar
    public void addcreatedsessiontofavorites() {
        if (createdsession != null) {
            String name = "";
            favoriteSessions.add(name, createdsession);
            new InformationDialog(preferences, "Favorite Session Added", "Session Added To Favorites", null);
        }
    }
    public void savecreatedsessionasfile() {

    }
    public void playcreatedsession() {
        if (createdsession != null) {
            getStage().setIconified(true);
            Player player = new Player(preferences, sessions, AvailableEntrainments, createdsession);
            player.initModality(Modality.APPLICATION_MODAL);
            player.initOwner(getStage());
            player.showAndWait();
            getStage().setIconified(false);
        }
    }
    public void exportcreatedsession() {
        new Exporter();
    }
    // Utility Methods
    private void populatetable() {
        if (createdtableplaybackitems != null) {createdtableplaybackitems.clear();}
        else {createdtableplaybackitems = new ArrayList<>();}
        if (! createdtableitems.isEmpty()) {createdtableitems.clear();}
        if (! createdsession.getPlaybackItems().isEmpty()) {
            createdtableplaybackitems.addAll(createdsession.getPlaybackItems());
            int number = 1;
            for (Session.PlaybackItem i : createdtableplaybackitems) {
                createdtableitems.add(new CreatedSessionTableItem(number, i.getName(),
                        i.getdurationasString(CreatedTableItemColumn.getWidth()), i.getAmbienceasString(CreatedTableAmbienceColumn.getWidth())));
                number++;
            }
            CreatedTableView.setItems(createdtableitems);
        }
        createdtableselecteditem = null;
        syncbuttons();
    }
    private void syncbuttons() {
        boolean nosessionloaded = createdsession == null;
        boolean tableempty = CreatedTableView.getItems().isEmpty();
        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
        AddItemsMenu.setDisable(nosessionloaded);
        EditDurationsButton.setDisable(nosessionloaded || tableempty || selectedindex == -1);
        AddEditAmbienceButton.setDisable(nosessionloaded || tableempty || selectedindex == -1);
        PlayButton.setDisable(nosessionloaded || tableempty);
        SaveAsFileButton.setDisable(nosessionloaded || tableempty);
        ExportButton.setDisable(nosessionloaded || tableempty);
        RemoveButton.setDisable(nosessionloaded || tableempty || selectedindex == -1);
        MoveUpButton.setDisable(nosessionloaded || tableempty || selectedindex == -1 || selectedindex == 0);
        MoveDownButton.setDisable(nosessionloaded || tableempty || selectedindex == -1 || selectedindex == CreatedTableView.getItems().size() -1);
        AddToFavoritesButton.setDisable(nosessionloaded || tableempty);
        if (nosessionloaded) {CreatedTableView.setPlaceholder(new Label("Please Create Or Load A Session"));}
        else {CreatedTableView.setPlaceholder(new Label("Session Is Empty"));}
    }


// Progress Tab
    // Overview Tab Methods
    public void populateprogressoverviewchart() {}
    // Session Browser Tab Methods
    public void populatesessionbrowserlistview() {}
    public void populatesessionbrowsertable() {}
    public void checkfilterdate_from() {}
    public void checkfilterdate_to() {}
    public void checkfilterduration_min() {}
    public void checkfilterduration_max() {}


// Goals Tab
    // Overview Tab Methods
    public void populategoalsoverviewtable() {}
    // Details Tab Methods
    public void goalsdetailtab_selectedsessionitemchanged() {}
    public void populategoalsdetailstable() {}
    public void setnewgoal() {
    }
    public void deletegoal() {
    }
    // Utility
    public List<Goals.Goal> getGoalsFor(Session.PlaybackItem playbackItem) {return goals.get(playbackItem);}

// Startup Checks

//    public void startupchecks_start() {
//        programState = ProgramState.STARTING_UP;
////        sessionCreator.setDisable(true, "");
////        startupChecks = new StartupChecks(getSessionParts(0, 16));
////        startupChecks.run();
//    }
//    public void startupchecks_finished() {
////        sessionCreator.setDisable(false, "");
//        programState = ProgramState.IDLE;
//        startupChecks = null;
////        Util.gui_showtimedmessageonlabel(CreatorStatusBar, "Startup Checks Completed", 3000);
//    }
//    class StartupChecks extends Task {


/////////////////////// STARTUP CHECKS END ////////////////////////////////

}