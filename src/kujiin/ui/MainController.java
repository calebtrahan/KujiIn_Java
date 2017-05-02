package kujiin.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.ui.ambience.AvailableAmbienceEditor;
import kujiin.ui.boilerplate.IconImageView;
import kujiin.ui.creation.AddOrEditAmbience;
import kujiin.ui.creation.AdjustDuration;
import kujiin.ui.creation.SetDurationAndAmbience;
import kujiin.ui.dialogs.AmbienceEditor_Simple;
import kujiin.ui.dialogs.ChangeProgramOptions;
import kujiin.ui.dialogs.EditReferenceFiles;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.ui.dialogs.alerts.ErrorDialog;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.ui.export.Exporter;
import kujiin.ui.playback.Player;
import kujiin.ui.table.GoalDetailsTableItem;
import kujiin.ui.table.GoalOverviewTableItem;
import kujiin.ui.table.TableItem_Number_Name_Duration;
import kujiin.ui.table.TableItem_Number_Name_Duration_Ambience;
import kujiin.util.Util;
import kujiin.util.enums.IconDisplayType;
import kujiin.util.enums.ProgramState;
import kujiin.xml.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
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
    private RampFiles RampFiles;
    private Sessions sessions;
    private FavoriteSessions favoriteSessions;
    private Goals goals;
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
    public TableView<TableItem_Number_Name_Duration_Ambience> CreatedTableView;
    public TableColumn<TableItem_Number_Name_Duration_Ambience, Integer> CreatedTableNumberColumn;
    public TableColumn<TableItem_Number_Name_Duration_Ambience, String> CreatedTableItemColumn;
    public TableColumn<TableItem_Number_Name_Duration_Ambience, String> CreatedTableDurationColumn;
    public TableColumn<TableItem_Number_Name_Duration_Ambience, String> CreatedTableAmbienceColumn;
    public ToolBar CreationTableControlsToolBar;
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
    public ListView<String> SessionBrowser_SelectSessionListView;
    public DatePicker SessionBrowser_Filter_DateRange_From;
    public DatePicker SessionBrowser_Filter_DateRange_To;
    public TextField SessionBrowser_Filter_Duration_From_Hours;
    public TextField SessionBrowser_Filter_Duration_From_Minutes;
    public TextField SessionBrowser_Filter_Duration_To_Hours;
    public TextField SessionBrowser_Filter_Duration_To_Minutes;
    public TableView<TableItem_Number_Name_Duration> SessionBrowser_DetailsTable;
    public TableColumn<TableItem_Number_Name_Duration, String> SessionBrowser_DetailsTable_NumberColumn;
    public TableColumn<TableItem_Number_Name_Duration, String> SessionBrowser_DetailsTable_ItemColumn;
    public TableColumn<TableItem_Number_Name_Duration, String> SessionBrowser_DetailsTable_TimePracticedColumn;
    public TextField SessionBrowser_Details_TotalDuration;
    // Goals Pane
    public TabPane GoalsTabPane;
        // Overview Tab
    public TableView<GoalOverviewTableItem> GoalsOverview_Table;
    public TableColumn<GoalOverviewTableItem, String> GoalsOverview_ItemColumn;
    public TableColumn<GoalOverviewTableItem, String> GoalsOverview_PracticedTimeColumn;
    public TableColumn<GoalOverviewTableItem, String> GoalsOverview_CurrentGoalColumn;
    public TableColumn<GoalOverviewTableItem, String> GoalsOverview_PercentCompletedColumn;
    public TableColumn<GoalOverviewTableItem, String> GoalsOverview_GoalsCompletedColumn;
    public Button GoalsOverview_GoToDetailsButton;
        // Individual Tab
    public ChoiceBox GoalsIndividual_SelectedSessionItemChoiceBox;
    public TextField GoalsIndividual_PracticedTime;
    public TableView<GoalDetailsTableItem> GoalsIndividual_Table;
    public TableColumn<GoalDetailsTableItem, String> GoalsIndividual_GoalTimeColumn;
    public TableColumn<GoalDetailsTableItem, String> GoalsIndividual_IsCompletedColumn;
    public TableColumn<GoalDetailsTableItem, String> GoalsIndividual_DateCompletedColumn;
    public TableColumn<GoalDetailsTableItem, String> GoalsIndividual_PercentCompletedColumn;
    public CheckBox GoalsIndividual_ShowCompletedGoalsCheckbox;
    public Button GoalsIndividual_SetNewGoalButton;
    public Button GoalsIndividual_DeleteGoalButton;
// My Fields
    // Play/Export Tab
    private Session createdsession;
    private Session.PlaybackItem createdtableselecteditem;
    private ObservableList<TableItem_Number_Name_Duration_Ambience> createdtableitems = FXCollections.observableArrayList();
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
    public ProgramState getProgramState() {
        return programState;
    }
    public void setRampFiles(kujiin.xml.RampFiles rampFiles) {
        RampFiles = rampFiles;
    }
    public kujiin.xml.RampFiles getRampFiles() {
        return RampFiles;
    }
    public Goals getGoals() {
        return goals;
    }
    public void setGoals(Goals goals) {
        this.goals = goals;
    }

// Window Methods
    private boolean cleanup() {
        availableAmbiences.marshall();
        AvailableEntrainments.marshall();
        RampFiles.marshall();
        preferences.marshall();
        return true;
    }
    public void close() {
        if (cleanup()) {System.exit(0);}
    }


// Setup Methods
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setPreferences(new Preferences());
        getPreferences().unmarshall();
        setupIcons();
        setupCreationTable();
        setupGoalOverviewTable();
    }
    private void setupCreationTable() {
        CreatedTableNumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
        CreatedTableItemColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        CreatedTableDurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
        CreatedTableAmbienceColumn.setCellValueFactory(cellData -> cellData.getValue().ambience);
        CreatedTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> tableselectionchanged());
    }
    private void setupGoalOverviewTable() {
        GoalsOverview_ItemColumn.setCellValueFactory(cellData -> cellData.getValue().sessionitem);
        GoalsOverview_PracticedTimeColumn.setCellValueFactory(cellData -> cellData.getValue().practicedtime);
        GoalsOverview_CurrentGoalColumn.setCellValueFactory(cellData -> cellData.getValue().currentgoal);
        GoalsOverview_PercentCompletedColumn.setCellValueFactory(cellData -> cellData.getValue().percentcompleted);
        GoalsOverview_GoalsCompletedColumn.setCellValueFactory(cellData -> cellData.getValue().goalscompleted);
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
        new AdjustDuration(null).showAndWait();
    }
    public void editavailableambience() {
        AvailableAmbienceEditor availableAmbienceEditor = new AvailableAmbienceEditor(preferences, availableAmbiences);
        availableAmbienceEditor.initModality(Modality.APPLICATION_MODAL);
        availableAmbienceEditor.showAndWait();
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
        if (createdsession != null && sessions.getSession().isEmpty() && getAvailableAmbiences().completelyempty()) {
            if (new ConfirmationDialog(getPreferences(), "Add Available Ambience", "There Is No Available Ambience For Any Playback Items", "Open Ambience Editor To Add Ambience?").getResult()) {
                AmbienceEditor_Simple ambienceEditor_simple = new AmbienceEditor_Simple(availableAmbiences, getPreferences());
                ambienceEditor_simple.initModality(Modality.APPLICATION_MODAL);
                ambienceEditor_simple.showAndWait();
            }
        }
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
        File filetoload;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select A Session To Open");
        filetoload = fileChooser.showOpenDialog(getStage());
        if (filetoload != null) {
            try {
                JAXBContext context = JAXBContext.newInstance(Session.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                createdsession = (Session) createMarshaller.unmarshal(filetoload);
                populatetable();
            } catch (JAXBException e) {
                new ErrorDialog(preferences, "Invalid File", "'" + filetoload.getName() + "' Isn't A Valid Session File", "Select A Valid Session To Load");
            }
        }
    }
    public void openrecentsession() {
    }
    public void openfavoritesession() {
    }
    // Creation Table Listeners
    public void setupCreatedSessionTable() {
        CreatedTableNumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
        CreatedTableItemColumn.setCellValueFactory(cellData -> cellData.getValue().name);
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
        Session.PlaybackItem playbackItem = createdsession.getplaybackitem(availableambienceindex);
        SetDurationAndAmbience adjustDuration = new SetDurationAndAmbience(preferences, availableAmbiences, Collections.singletonList(playbackItem));
        adjustDuration.showAndWait();
        if (adjustDuration.isAccepted()) {createdsession.addplaybackitems(adjustDuration.getPlaybackItemList());}
        populatetable();
    }
    private void add(int[] availableambienceindexes) {
        List<Session.PlaybackItem> items = new ArrayList<>();
        for (int i : availableambienceindexes) {items.add(createdsession.getplaybackitem(i));}
        SetDurationAndAmbience adjustDuration = new SetDurationAndAmbience(preferences, availableAmbiences, items);
        adjustDuration.showAndWait();
        if (adjustDuration.isAccepted()) {createdsession.addplaybackitems(adjustDuration.getPlaybackItemList());}
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
            AdjustDuration adjustDuration = new AdjustDuration(createdtableselecteditem);
            adjustDuration.initOwner(getStage());
            adjustDuration.showAndWait();
            if (adjustDuration.isAccepted()) {
                Session.PlaybackItem playbackItem = createdtableselecteditem;
                playbackItem.setDuration(adjustDuration.getNewduration().toMillis());
                createdtableplaybackitems.set(selectedindex, playbackItem);
                populatetable();
            }
        } else {new InformationDialog(preferences, "Cannot Edit Duration", "Select A Single Table Item To Edit Duration", null);}
    }
    public void addoreditambience() {
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
            Player player = new Player(this, sessions, createdsession);
            player.initModality(Modality.APPLICATION_MODAL);
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
                createdtableitems.add(new TableItem_Number_Name_Duration_Ambience(number, i.getName(),
                        i.getdurationasString(CreatedTableItemColumn.getWidth()), i.getAmbienceasString()));
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
    public void populatesessionbrowserlistview() {
        ObservableList<String> sessionlist = FXCollections.observableArrayList();
        List<Session> allsessions = this.sessions.getSession();
        // Filter Sessions Based On Selected Filter Checkboxes Here
        for (Session i : allsessions) {
            sessionlist.add(String.format("%s (%s)", i.getDate_Practiced().format(Util.dateFormat), Util.formatdurationtoStringDecimalWithColons(i.getSessionDuration())));
        }
        SessionBrowser_SelectSessionListView.setItems(sessionlist);
    }
    public void populatesessionbrowsertable() {
        int index = SessionBrowser_SelectSessionListView.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            Session selectedsession = this.sessions.get(index);
            ObservableList<TableItem_Number_Name_Duration> sessionitems = FXCollections.observableArrayList();
            int count = 1;
            for (Session.PlaybackItem i : selectedsession.getPlaybackItems()) {
                sessionitems.add(new TableItem_Number_Name_Duration(count, i.getName(), Util.formatdurationtoStringDecimalWithColons(new Duration(i.getDuration()))));
                count++;
            }
            SessionBrowser_DetailsTable.setItems(sessionitems);
        }
    }
    public void checkfilterdate_from() {

    }
    public void checkfilterdate_to() {}
    public void checkfilterduration_min() {}
    public void checkfilterduration_max() {}


// Goals Tab
    // Overview Tab Methods
    private void populategoalsoverviewtable() {
        GoalsOverview_Table.getItems().clear();
        ObservableList<GoalOverviewTableItem> tableitems = FXCollections.observableArrayList();
        Session demosession = new Session();
        for (int i = 0; i < 15; i++) {
            String practicedtime;
            String currentgoal;
            String percentcompleted;
            String goalscompleted;
            Session.PlaybackItem playbackItem = demosession.getplaybackitem(i);
            Duration practicedduration = sessions.gettotalpracticedtime(playbackItem, false);
            practicedtime = Util.formatdurationtoStringSpelledOut(practicedduration, 1000.0);
            Goals.Goal goal = goals.getCurrentGoal(i);
            if (goal == null) {
                currentgoal = "None";
                percentcompleted = "-";
            }
            else {
                currentgoal = Util.formatdurationtoStringSpelledOut(goal.getDuration(), 1000.0);
                percentcompleted = String.format("%.2f", (practicedduration.toMillis() / goal.getDuration().toMillis()) * 100) + "%";
            }
            int completedgoalcount = 0;
            for (Goals.Goal x : goals.get(GoalsOverview_Table.getSelectionModel().getSelectedIndex())) {if (x.getCompleted()) {completedgoalcount++;}}
            goalscompleted = String.valueOf(completedgoalcount);
            tableitems.add(new GoalOverviewTableItem(playbackItem.getName(), practicedtime, currentgoal, percentcompleted, goalscompleted));
        }
        GoalsOverview_Table.setItems(tableitems);
    }
    public void gotogoaldetails() {
        int index = GoalsOverview_Table.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            GoalsTabPane.getSelectionModel().select(1);
            GoalsIndividual_SelectedSessionItemChoiceBox.getSelectionModel().select(index);
            populategoalsdetailstable();
        }
    }
    // Details Tab Methods
    public void populategoalsdetailstable() {
        int index = GoalsIndividual_SelectedSessionItemChoiceBox.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            Session dummysession = new Session();
            Session.PlaybackItem playbackItem = dummysession.getplaybackitem(index);
            GoalsIndividual_PracticedTime.setText(Util.formatdurationtoStringSpelledOut(sessions.gettotalpracticedtime(playbackItem, false), GoalsIndividual_PracticedTime.getWidth()));
            ObservableList<GoalDetailsTableItem> tableitems = FXCollections.observableArrayList();
            for (Goals.Goal i :goals.get(index)) {
                if (! GoalsIndividual_ShowCompletedGoalsCheckbox.isSelected() && i.getCompleted()) {continue;}
                String goaltime = Util.formatdurationtoStringSpelledOut(i.getDuration(), 1000.0);
                String datecompleted;
                String percentcompleted;
                if (i.getCompleted()) {
                    datecompleted = i.getDate_Completed().format(Util.dateFormat);
                    percentcompleted = "100%";
                } else {
                    datecompleted = "Not Completed";
                    percentcompleted = String.format("%.2f", (sessions.gettotalpracticedtime(playbackItem, false).toMillis() / i.getDuration().toMillis()) * 100);
                }
                tableitems.add(new GoalDetailsTableItem(goaltime, i.getCompleted(), datecompleted, percentcompleted));
            }
            GoalsIndividual_Table.setItems(tableitems);
        }
    }
    public void setnewgoal() {

    }
    public void deletegoal() {

    }

}