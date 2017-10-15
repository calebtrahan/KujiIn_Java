package kujiin.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import kujiin.ui.ambience.AvailableAmbienceEditor;
import kujiin.ui.boilerplate.IconImageView;
import kujiin.ui.creation.*;
import kujiin.ui.dialogs.AmbienceEditor_Simple;
import kujiin.ui.dialogs.ChangeProgramOptions;
import kujiin.ui.dialogs.EditReferenceFiles;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.ui.dialogs.alerts.ErrorDialog;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.ui.goals.SetNewGoalDialog;
import kujiin.ui.playback.Player;
import kujiin.ui.progress.EditSessionNotes;
import kujiin.ui.table.*;
import kujiin.util.Util;
import kujiin.util.enums.IconDisplayType;
import kujiin.util.enums.ProgramState;
import kujiin.xml.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static kujiin.xml.Preferences.*;

// Bugs To Fix
    // TODO Player Is EXTREMELEY MEMORY CONSUMING FOR MAC
    // TODO Player Is Cutting Off:
        // Entrainment At 5:30/6:00 for RIN
        // Entrainment At 5:30 for KYO
// Additional Features To Definitely Add
    // TODO Session Well Formedness Checks Before Playback, Save Or Export
    // TODO Create Goal Progress Similar To Session Details And Add To Session Details Dialog
    // TODO Exporter
    // TODO Add Logging (And Write To Log File) For Troubleshooting

// Optional Additional Features
    // TODO Design Journal Tab. Encrypt The Viewing Of The Information
    // TODO Refactor Freq Files So There Can Be 2 or 3 Different Frequency Octaves For The Same Session Part (Use enum FreqType)

// Mind Workstation
    // TODO Add Low (And Possibly Medium) Variations Of All Session Parts
    // TODO Add Ramps To Connect Low (And Possibly Medium) Variations Of Session Parts With Each Other


// Startup Wizard
    // Welcome To The Program ......(Description Of Program). This A Short Tutorial To Teach You About Some Of The Features Of This Program
    // Session Creation
    // Ambience & Playback
    // Ambience

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
    private AllGoals allGoals;
    private ProgramState programState = ProgramState.IDLE;
// GUI Fields
    // Top Menu
    public MenuBar TopMenuBar;
    public MenuItem PreferenceMenuItem;
    public MenuItem CloseMenuItem;
    public MenuItem EditAvailableAmbienceMenuItem;
    public MenuItem EditReferenceFilesMenuItem;
    public MenuItem AboutMenuItem;
    // Play/Export Tab
    public Tab PlayExportTab;
    public Button CreateNewSessionButton;
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
    public MenuButton DurationMenu;
    public MenuItem Duration_SetRampOnlyMenuItem;
    public MenuItem Duration_EditDurationMenuItem;
    public MenuButton AmbienceMenu;
    public Menu AmbienceQuickAddMenu;
    public MenuItem AmbienceQuickAddMenu_Repeat;
    public MenuItem AmbienceQuickAddMenu_Shuffle;
    public MenuItem AmbienceCustomize;
    public Button MoveUpButton;
    public Button MoveDownButton;
    public Button RemoveButton;
    public TextField SessionSummary_CompletionTime;
    public TextField SessionSummary_Duration;
    public Button ClearButton;
    public Button AddToFavoritesButton;
    public Button PlayButton;
    public Button SaveAsFileButton;
    public Button ExportButton;
    public Label CreatorStatusBar;
    // Progress Tab
    public Tab ProgressTab;
        // Overview Tab
    public BarChart<String, Number> ProgressOverviewBarChart;
    public NumberAxis ProgressOverviewNumbersAxis;
    public CategoryAxis ProgressOverviewCategoryAxis;
    public TextField ProgressOverviewTotalTimePracticed;
    public AnchorPane ProgressBalanceAnchorPane;
    public PieChart ProgressBalancePieChart;
        // Session Browser Tab
    public TableView<SessionBrowserTableItem> PracticedSessionListTable;
    public TableColumn<SessionBrowserTableItem, String> PracticedSessionListTable_DateColumn;
    public TableColumn<SessionBrowserTableItem, String> PracticedSessionListTable_ItemsColumn;
    public TableColumn<SessionBrowserTableItem, String> PracticedSessionListTable_DurationColumn;
    public Accordion SessionBrowser_Filter;
    public CheckBox SessionBrowser_Filter_DateRange_From_Checkbox;
    public CheckBox SessionBrowser_Filter_DateRange_To_Checkbox;
    public CheckBox SessionBrowser_Filter_Duration_Min_Checkbox;
    public CheckBox SessionBrowser_Filter_Duration_Max_Checkbox;
    public DatePicker SessionBrowser_Filter_DateRange_From;
    public DatePicker SessionBrowser_Filter_DateRange_To;
    public Spinner<Integer> SessionBrowser_Filter_Duration_From_Hours;
    public Spinner<Integer> SessionBrowser_Filter_Duration_From_Minutes;
    public Spinner<Integer> SessionBrowser_Filter_Duration_To_Hours;
    public Spinner<Integer> SessionBrowser_Filter_Duration_To_Minutes;
    public TableView<TableItem_Number_Name_Duration> SessionBrowser_DetailsTable;
    public TableColumn<TableItem_Number_Name_Duration, String> SessionBrowser_DetailsTable_NumberColumn;
    public TableColumn<TableItem_Number_Name_Duration, String> SessionBrowser_DetailsTable_ItemColumn;
    public TableColumn<TableItem_Number_Name_Duration, String> SessionBrowser_DetailsTable_TimePracticedColumn;
    public Button SessionBrowser_ViewNotesButton;
    public Label SessionBrowser_Details_TotalDuration_Label;
    public TextField SessionBrowser_Details_TotalDuration;
    // All Goals Pane
    public Tab GoalsTab;
    public TabPane GoalsTabPane;
    public Tab GoalsOverviewTab;
    public Tab GoalDetailsTab;
        // Overview Tab
    public TableView<GoalOverviewTableItem> GoalsOverview_Table;
    public TableColumn<GoalOverviewTableItem, String> GoalsOverview_ItemColumn;
    public TableColumn<GoalOverviewTableItem, String> GoalsOverview_PracticedTimeColumn;
    public TableColumn<GoalOverviewTableItem, String> GoalsOverview_CurrentGoalColumn;
    public TableColumn<GoalOverviewTableItem, String> GoalsOverview_PercentCompletedColumn;
    public TableColumn<GoalOverviewTableItem, String> GoalsOverview_GoalsCompletedColumn;
    public Button GoalsOverview_SetNewGoalButton;
    public Button GoalsOverview_GoToDetailsButton;
        // Individual Tab
    public ChoiceBox<String> GoalsIndividual_SelectedSessionItemChoiceBox;
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
    private PlaybackItem createdtableselecteditem;
    private ObservableList<TableItem_Number_Name_Duration_Ambience> createdtableitems = FXCollections.observableArrayList();
    private ArrayList<PlaybackItem> createdtableplaybackitems;
    private Timeline updatecompletiontime;

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
    public AllGoals getAllGoals() {
        return allGoals;
    }
    public void setAllGoals(AllGoals allGoals) {
        this.allGoals = allGoals;
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
        TopMenuBar.setUseSystemMenuBar(true);
        setPreferences(new Preferences());
        getPreferences().unmarshall();
        setupIcons();
        setupToolTips();
        setupTables();
        ProgressTab.selectedProperty().addListener(observable -> {
            if (ProgressTab.isSelected()) {
                populateprogressoverviewchartandbalancepie();
                populatesessionbrowsertable();
                populatesessionbrowserfilter();
                populatesessiondetailstable();
            }
        });
        GoalsTab.selectedProperty().addListener(observable -> {
            if (GoalsTab.isSelected()) {
                populategoalsoverviewtable();
            }
        });
        GoalDetailsTab.selectedProperty().addListener(observable -> {
            int index = GoalsOverview_Table.getSelectionModel().getSelectedIndex();
            if (index != -1) {GoalsIndividual_SelectedSessionItemChoiceBox.getSelectionModel().select(index);}
            populategoalsdetailstable();
        });
        PracticedSessionListTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> populatesessiondetailstable());
        GoalsIndividual_SelectedSessionItemChoiceBox.setItems(FXCollections.observableArrayList(ALLNAMES));
        GoalsIndividual_SelectedSessionItemChoiceBox.getSelectionModel().selectedIndexProperty().addListener(observable -> populategoalsdetailstable());

    }
    private void setupTables() {
        // Creation Table
        CreatedTableNumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
        CreatedTableItemColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        CreatedTableDurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
        CreatedTableAmbienceColumn.setCellValueFactory(cellData -> cellData.getValue().ambience);
        CreatedTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> tableselectionchanged());
        // Session Browser List Table
        PracticedSessionListTable_DateColumn.setCellValueFactory(cellData -> cellData.getValue().date);
        PracticedSessionListTable_ItemsColumn.setCellValueFactory(cellData -> cellData.getValue().items);
        PracticedSessionListTable_DurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
        // Session Browser Details Table
        SessionBrowser_DetailsTable_NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asString());
        SessionBrowser_DetailsTable_ItemColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        SessionBrowser_DetailsTable_TimePracticedColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
        // Goal Overview Table
        GoalsOverview_ItemColumn.setCellValueFactory(cellData -> cellData.getValue().sessionitem);
        GoalsOverview_PracticedTimeColumn.setCellValueFactory(cellData -> cellData.getValue().practicedtime);
        GoalsOverview_CurrentGoalColumn.setCellValueFactory(cellData -> cellData.getValue().currentgoal);
        GoalsOverview_PercentCompletedColumn.setCellValueFactory(cellData -> cellData.getValue().percentcompleted);
        GoalsOverview_GoalsCompletedColumn.setCellValueFactory(cellData -> cellData.getValue().goalscompleted);
        GoalsIndividual_GoalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().goaltime);
        GoalsIndividual_IsCompletedColumn.setCellValueFactory(cellData -> cellData.getValue().iscompleted);
        GoalsIndividual_DateCompletedColumn.setCellValueFactory(cellData -> cellData.getValue().datecompleted);
        GoalsIndividual_PercentCompletedColumn.setCellValueFactory(cellData -> cellData.getValue().percentcompleted);
        GoalsOverview_Table.getSelectionModel().selectedIndexProperty().addListener(observable -> {
            GoalsOverview_SetNewGoalButton.setDisable(GoalsOverview_Table.getSelectionModel().getSelectedIndex() == -1);
            GoalsOverview_GoToDetailsButton.setDisable(GoalsOverview_Table.getSelectionModel().getSelectedIndex() == -1);
        });
        GoalsIndividual_SelectedSessionItemChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldvalue, newvalue) -> {
            GoalsIndividual_SetNewGoalButton.setDisable(newvalue == null);
            GoalsIndividual_ShowCompletedGoalsCheckbox.setDisable(newvalue == null);
        });
        GoalsIndividual_Table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.isCompleted()) {GoalsIndividual_DeleteGoalButton.setDisable(true);}
            else {GoalsIndividual_DeleteGoalButton.setDisable(false);}
        });
    }
    private void setupIcons() {
        IconDisplayType dt = preferences.getUserInterfaceOptions().getIconDisplayType();
        if (dt == IconDisplayType.ICONS_AND_TEXT || dt == IconDisplayType.ICONS_ONLY) {
            AddItemsMenu.setGraphic(new IconImageView(ICON_ADD, 20.0));
            CreateNewSessionButton.setGraphic(new IconImageView(ICON_ADD, 20.0));
            OpenFileButton.setGraphic(new IconImageView(ICON_OPENFILE, 20.0));
            OpenRecentSessionsButton.setGraphic(new IconImageView(ICON_RECENTSESSIONS, 20.0));
            ClearButton.setGraphic(new IconImageView(ICON_CLEARSESSION, 20.0));
            OpenFavoritesButton.setGraphic(new IconImageView(ICON_FAVORITES, 20.0));
            DurationMenu.setGraphic(new IconImageView(ICON_EDITDURATION, 20.0));
            AmbienceMenu.setGraphic(new IconImageView(ICON_AMBIENCE, 20.0));
            MoveUpButton.setGraphic(new IconImageView(ICON_MOVEUP, 20.0));
            MoveDownButton.setGraphic(new IconImageView(ICON_MOVEDOWN, 20.0));
            RemoveButton.setGraphic(new IconImageView(ICON_REMOVE, 20.0));
            AddToFavoritesButton.setGraphic(new IconImageView(ICON_ADDTOFAVORITE, 20.0));
            PlayButton.setGraphic(new IconImageView(ICON_PLAY, 20.0));
            SaveAsFileButton.setGraphic(new IconImageView(ICON_EXPORTTODOCUMENT, 20.0));
            ExportButton.setGraphic(new IconImageView(ICON_EXPORTTOAUDIO, 20.0));
        }
        if (dt == IconDisplayType.ICONS_ONLY) {
            AmbienceMenu.setText("");
            AddItemsMenu.setText("");
            CreateNewSessionButton.setText("");
            OpenFileButton.setText("");
            OpenRecentSessionsButton.setText("");
            OpenFavoritesButton.setText("");
            DurationMenu.setText("");
            MoveUpButton.setText("");
            MoveDownButton.setText("");
            RemoveButton.setText("");
            ClearButton.setText("");
            AddToFavoritesButton.setText("");
            PlayButton.setText("");
            SaveAsFileButton.setText("");
            ExportButton.setText("");
        } else {
//            AmbienceMenu.setText("Ambience");
//            AddItemsMenu.setText("Add");
            CreateNewSessionButton.setText("Create New Session");
            OpenFileButton.setText("Open Session");
            OpenRecentSessionsButton.setText("Recent");
            OpenFavoritesButton.setText("Favorites");
//            DurationMenu.setText("Duration");
//            MoveUpButton.setText("Up");
//            MoveDownButton.setText("Down");
//            RemoveButton.setText("Remove");
            ClearButton.setText("Clear Session");
            AddToFavoritesButton.setText("Add To Favorites");
            PlayButton.setText("Play");
            SaveAsFileButton.setText("Save As");
            ExportButton.setText("Export");
        }
        if (dt == IconDisplayType.TEXT_ONLY) {
            AddItemsMenu.setGraphic(null);
            CreateNewSessionButton.setGraphic(null);
            OpenFileButton.setGraphic(null);
            OpenRecentSessionsButton.setGraphic(null);
            ClearButton.setGraphic(null);
            OpenFavoritesButton.setGraphic(null);
            DurationMenu.setGraphic(null);
            AmbienceMenu.setGraphic(null);
            MoveUpButton.setGraphic(null);
            MoveDownButton.setGraphic(null);
            RemoveButton.setGraphic(null);
            AddToFavoritesButton.setGraphic(null);
            PlayButton.setGraphic(null);
            SaveAsFileButton.setGraphic(null);
            ExportButton.setGraphic(null);
        }
        syncbuttons();
    }
    private void setupToolTips() {
        AddItemsMenu.setTooltip(new Tooltip("Add Items"));
        CreateNewSessionButton.setTooltip(new Tooltip("Create New Session"));
        OpenFileButton.setTooltip(new Tooltip("Open Session From File"));
        OpenRecentSessionsButton.setTooltip(new Tooltip("Open Recent Session"));
        OpenFavoritesButton.setTooltip(new Tooltip("Open Favorites"));
        DurationMenu.setTooltip(new Tooltip("Edit Duration"));
        AmbienceMenu.setTooltip(new Tooltip("Add/Edit Ambience"));
        MoveUpButton.setTooltip(new Tooltip("Move Up In Table"));
        MoveDownButton.setTooltip(new Tooltip("Move Down In Table"));
        RemoveButton.setTooltip(new Tooltip("Remove"));
        ClearButton.setTooltip(new Tooltip("Clear Session"));
        AddToFavoritesButton.setTooltip(new Tooltip("Add This Session To Favorites"));
        SaveAsFileButton.setTooltip(new Tooltip("Save As Document"));
        PlayButton.setTooltip(new Tooltip("Play This Session"));
        ExportButton.setTooltip(new Tooltip("Export To Audio File"));
    }


// Top Menu Methods
    public void editpreferences() {
        if (programState == ProgramState.IDLE) {
            ChangeProgramOptions changeProgramOptions = new ChangeProgramOptions(this);
            changeProgramOptions.initModality(Modality.APPLICATION_MODAL);
            changeProgramOptions.initOwner(getStage());
            changeProgramOptions.showAndWait();
            preferences.marshall();
            setupIcons();
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
    public void createnewsession() {
        if (createdsession != null && sessions.getSession().isEmpty() && getAvailableAmbiences().completelyempty()) {
            if (new ConfirmationDialog(getPreferences(), "Add Available Ambience", "There Is No Available Ambience For Any Playback Items", "Open Ambience Editor To Add Ambience?", true).getResult()) {
                AmbienceEditor_Simple ambienceEditor_simple = new AmbienceEditor_Simple(availableAmbiences, getPreferences());
                ambienceEditor_simple.initModality(Modality.APPLICATION_MODAL);
                ambienceEditor_simple.showAndWait();
            }
        }
        if ((createdsession != null && ! createdsession.getPlaybackItems().isEmpty()) && ! new ConfirmationDialog(preferences, "Overwrite Session", "Really Load New Session?", "This will clear any unsaved changes you made to this session", true).getResult()) {return;}
        createdsession = new Session();
        populatetable();
        AddItemsMenu.requestFocus();
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
                PlayButton.requestFocus();
            } catch (JAXBException e) {
                new ErrorDialog(preferences, "Invalid File", "'" + filetoload.getName() + "' Isn't A Valid Session File", "Select A Valid Session To Load", true);
            }
        }
    }
    public void openrecentsession() {
        if (sessions.getSession() == null || sessions.getSession().isEmpty()) {
            new InformationDialog(preferences, "No Recent Sessions", "No Recent Sessions", "No Sessions Practiced");
            return;
        }
        if (createdsession != null && ! createdsession.isEmpty()) {
            if (! new ConfirmationDialog(preferences, "Overwrite Session", "Really Open Recent Session?", "This will clear any unsaved changes you made to this session").getResult()) {
                return;
            }
            createdsession = null;
            populatetable();
            PlayButton.requestFocus();
        }
        SelectASession selectASession = new SelectASession(preferences, sessions);
        selectASession.initModality(Modality.APPLICATION_MODAL);
        selectASession.showAndWait();
        if (selectASession.isAccepted()) {
            createdsession = selectASession.getSelectedsession();
            populatetable();
        }
    }
    public void openfavoritesession() {
        if (favoriteSessions.getFavoriteSessions() == null || favoriteSessions.getFavoriteSessions().isEmpty()) {
            new InformationDialog(preferences, "No Favorite Sessions", "No Sessions Marked As Favorite", "Mark At Least One Session As Favorite To Use This Feature");
            return;
        }
        if (createdsession != null && ! createdsession.isEmpty()) {
            if (! new ConfirmationDialog(preferences, "Overwrite Session", "Really Open Favorite Session?", "This will clear any unsaved changes you made to this session").getResult()) {
                return;
            }
            createdsession = null;
            populatetable();
        }
        SelectASession selectASession = new SelectASession(preferences, favoriteSessions);
        selectASession.initModality(Modality.APPLICATION_MODAL);
        selectASession.showAndWait();
        if (selectASession.isAccepted()) {
            createdsession = selectASession.getSelectedsession();
            populatetable();
            PlayButton.requestFocus();
        }
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
        PlaybackItem playbackItem = createdsession.getplaybackitem(availableambienceindex);
        PlaybackItem.PlaybackItemType playbackItemType;
        if (availableambienceindex == 0) {playbackItemType = PlaybackItem.PlaybackItemType.QIGONG;}
        else if (availableambienceindex > 0 && availableambienceindex < 10) {playbackItemType = PlaybackItem.PlaybackItemType.CUT;}
        else {playbackItemType = PlaybackItem.PlaybackItemType.ELEMENT;}
        playbackItem.setPlaybackItemType(playbackItemType);
        SetDurationWithAmbienceOption adjustDuration = new SetDurationWithAmbienceOption(preferences, availableAmbiences, Collections.singletonList(playbackItem), true);
        adjustDuration.initModality(Modality.APPLICATION_MODAL);
        adjustDuration.showAndWait();
        if (adjustDuration.isAccepted()) {
            if (adjustDuration.isQuickaddambience()) {
                QuickAddAmbience quickAddAmbience = new QuickAddAmbience(preferences, availableAmbiences, Collections.singletonList(playbackItem));
                quickAddAmbience.initModality(Modality.APPLICATION_MODAL);
                quickAddAmbience.showAndWait();
                if (quickAddAmbience.isAccepted()) {playbackItem = quickAddAmbience.getPlaybackItemList().get(0);}
            }
            int startindex;
            if (CreatedTableView.getSelectionModel().getSelectedIndex() != -1) {
                startindex = CreatedTableView.getSelectionModel().getSelectedIndex();
                createdsession.addplaybackitems(startindex, Collections.singletonList(playbackItem)); createdsession.calculateexpectedduration();
            } else {
                startindex = createdsession.getPlaybackItems().size() - 1;
                createdsession.addplaybackitems(null, Collections.singletonList(playbackItem)); createdsession.calculateexpectedduration();
            }
            if (CreatedTableView.getSelectionModel().getSelectedIndex() != -1 && createdsession.getplaybackitem(startindex).getCreationindex() == playbackItem.getCreationindex()) {
                mergeitems(createdsession.getplaybackitem(startindex), createdsession.getplaybackitem(startindex + 1));
                populatetable();
                return;
            }
        }
        populatetable();
    }
    private void add(int[] availableambienceindexes) {
        List<PlaybackItem> items = new ArrayList<>();
        for (int i : availableambienceindexes) {
            PlaybackItem playbackItem = createdsession.getplaybackitem(i);
            if (i == 0) {playbackItem.setPlaybackItemType(PlaybackItem.PlaybackItemType.QIGONG);}
            else if (i > 0 && i < 10) {playbackItem.setPlaybackItemType(PlaybackItem.PlaybackItemType.CUT);}
            else {playbackItem.setPlaybackItemType(PlaybackItem.PlaybackItemType.ELEMENT);}
            items.add(playbackItem);
        }
        SetDurationWithAmbienceOption adjustDuration = new SetDurationWithAmbienceOption(preferences, availableAmbiences, items, true);
        adjustDuration.initModality(Modality.APPLICATION_MODAL);
        adjustDuration.showAndWait();
        if (adjustDuration.isAccepted()) {
            items = adjustDuration.getPlaybackItemList();
            if (adjustDuration.isQuickaddambience()) {
                QuickAddAmbience quickAddAmbience = new QuickAddAmbience(preferences, availableAmbiences, items);
                quickAddAmbience.initModality(Modality.APPLICATION_MODAL);
                quickAddAmbience.showAndWait();
                if (quickAddAmbience.isAccepted()) {items = quickAddAmbience.getPlaybackItemList();}
            }
            int startindex;
            if (CreatedTableView.getSelectionModel().getSelectedIndex() != -1) {
                startindex = CreatedTableView.getSelectionModel().getSelectedIndex();
                createdsession.addplaybackitems(startindex, items); createdsession.calculateexpectedduration();
            } else {
                startindex = createdsession.getPlaybackItems().size() - 1;
                createdsession.addplaybackitems(null, items); createdsession.calculateexpectedduration();
            }
            if (CreatedTableView.getSelectionModel().getSelectedIndex() != -1 && createdsession.getplaybackitem(startindex).getCreationindex() == items.get(0).getCreationindex()) {
                mergeitems(createdsession.getplaybackitem(startindex), createdsession.getplaybackitem(startindex + 1));
                populatetable();
                return;
            }
        }
        populatetable();
    }
    public void addallitems_kujiin() {
        int[] indexes = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        add(indexes);
    }
    public void addallitems_elements() {
        int[] indexes = {10, 11, 12, 13, 14};
        add(indexes);
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
    public void setramponly() {
        if (createdtableselecteditem != null) {
            createdtableselecteditem.setRampOnly(true);
            createdtableselecteditem.setExpectedDuration(0.0);
            populatetable();
            syncbuttons();
        }
    }
    public void editduration() {
        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
        if (selectedindex != -1 && createdtableselecteditem != null) {
            SetDurationWithAmbienceOption adjustDuration = new SetDurationWithAmbienceOption(preferences, availableAmbiences, Collections.singletonList(createdtableselecteditem), false);
            adjustDuration.initOwner(getStage());
            adjustDuration.showAndWait();
            if (adjustDuration.isAccepted()) {
                createdtableplaybackitems.set(selectedindex, adjustDuration.getPlaybackItemList().get(0));
                if (createdtableselecteditem.getAmbience().isEnabled() && createdtableselecteditem.getAmbience().getCurrentAmbienceDuration().lessThan(new Duration(createdtableplaybackitems.get(selectedindex).getExpectedDuration()))) {
                    if (new ConfirmationDialog(preferences, "Set Ambience", "Ambience Not Long Enough To Match New Duration", "Please Set More Ambience Or Disable Ambience", "Add Ambience", "Disable Ambience").getResult()) {
                        customizeambience();
                        PlaybackItem createdtableselecteditem = createdtableplaybackitems.get(selectedindex);
                        if (createdtableselecteditem.getAmbience().getCurrentAmbienceDuration().lessThan(new Duration(createdtableplaybackitems.get(selectedindex).getExpectedDuration()))) {
                            createdtableselecteditem.getAmbience().clearambience();
                            createdtableselecteditem.getAmbience().setEnabled(false);
                        }
                    } else {
                        createdtableselecteditem.getAmbience().clearambience();
                        createdtableselecteditem.getAmbience().setEnabled(false);
                    }
                }
                createdsession.calculateexpectedduration();
                populatetable();
                syncbuttons();
            }
        } else {new InformationDialog(preferences, "Cannot Edit Duration", "Select A Single Table Item To Edit Duration", null, true);}
    }
    public void quickaddambience_repeat() {
        if (createdtableselecteditem != null) {
            createdtableselecteditem.getAmbience().addavailableambience_repeat(createdtableselecteditem, availableAmbiences.getsessionpartAmbience(createdtableselecteditem.getCreationindex()));
            createdtableselecteditem.getAmbience().setEnabled(createdtableselecteditem.getAmbience().getAmbience() != null);
            populatetable();
        }
    }
    public void quickaddambience_shuffle() {
        if (createdtableselecteditem != null) {
            createdtableselecteditem.getAmbience().addavailableambience_shuffle(createdtableselecteditem, availableAmbiences.getsessionpartAmbience(createdtableselecteditem.getCreationindex()));
            createdtableselecteditem.getAmbience().setEnabled(createdtableselecteditem.getAmbience().getAmbience() != null);
            populatetable();
        }
    }
    public void customizeambience() {
        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
        if (selectedindex != -1 && createdtableselecteditem != null) {
            CustomizeAmbience customizeAmbience = new CustomizeAmbience(preferences, createdtableselecteditem, availableAmbiences);
            customizeAmbience.initModality(Modality.APPLICATION_MODAL);
            customizeAmbience.showAndWait();
            if (customizeAmbience.isAccepted()) {
                createdtableplaybackitems.set(createdtableplaybackitems.indexOf(createdtableselecteditem), customizeAmbience.getPlaybackItem());
                populatetable();
            }
        }
    }
    public void moveupincreatortable() {
        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
        if (selectedindex > 0) {
            ArrayList<PlaybackItem> itemsinsession = createdsession.getPlaybackItems();
            PlaybackItem currentitem = createdsession.getPlaybackItems().get(selectedindex);
            PlaybackItem oneitemup = createdsession.getPlaybackItems().get(selectedindex - 1);
            if (currentitem.getPlaybackItemType() == PlaybackItem.PlaybackItemType.CUT && oneitemup.getPlaybackItemType() == PlaybackItem.PlaybackItemType.CUT) {
                if (currentitem.getCreationindex() > oneitemup.getCreationindex()) {
                    if (! new ConfirmationDialog(preferences, "Confirmation", "This Will Place Cuts Out Of Order", "This Is Not Recommended", "Proceed Anyway", "Cancel").getResult()) {return;}
                }
            }
            if (selectedindex > 1 ) {
                PlaybackItem twoitemsup = createdsession.getPlaybackItems().get(selectedindex - 2);
                if (twoitemsup.getCreationindex() == currentitem.getCreationindex()) {mergeitems(twoitemsup, currentitem); }
                else {Collections.swap(itemsinsession, selectedindex, selectedindex - 1);}
            } else {Collections.swap(itemsinsession, selectedindex, selectedindex - 1);}
            createdsession.setPlaybackItems(itemsinsession);
            populatetable();
        }
    }
    public void movedownincreatortable() {
        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
        if (selectedindex != -1 && selectedindex < createdtableplaybackitems.size() - 1) {
            ArrayList<PlaybackItem> itemsinsession = createdsession.getPlaybackItems();
            PlaybackItem currentitem = createdsession.getPlaybackItems().get(selectedindex);
            PlaybackItem oneitemdown = createdsession.getPlaybackItems().get(selectedindex + 1);
            if (currentitem.getPlaybackItemType() == PlaybackItem.PlaybackItemType.CUT && oneitemdown.getPlaybackItemType() == PlaybackItem.PlaybackItemType.CUT) {
                if (currentitem.getCreationindex() < oneitemdown.getCreationindex()) {
                    if (! new ConfirmationDialog(preferences, "Confirmation", "This Will Place Cuts Out Of Order", "This Is Not Recommended", "Proceed Anyway", "Cancel").getResult()) {return;}
                }
            }
            if (selectedindex < createdtableplaybackitems.size() - 2) {
                PlaybackItem twoitemsdown = createdsession.getPlaybackItems().get(selectedindex + 2);
                if (twoitemsdown.getCreationindex() == currentitem.getCreationindex()) {mergeitems(twoitemsdown, currentitem); }
                else {Collections.swap(itemsinsession, selectedindex, selectedindex + 1);}
            } else {Collections.swap(itemsinsession, selectedindex, selectedindex + 1);}
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
    private void mergeitems(PlaybackItem item1, PlaybackItem item2) {
        if (new ConfirmationDialog(preferences, "Confirmation", "Duplicate Playback Item  + '" + item1.getName() + "' Detected", "Merge? (This Will Clear Ambience For Merged Items)").getResult()) {
            ArrayList<PlaybackItem> itemsinsession = createdsession.getPlaybackItems();
            int item1index = itemsinsession.indexOf(item1);
            itemsinsession.remove(item2);
            boolean ambiencewasenabled = item1.getAmbience().isEnabled() || item2.getAmbience().isEnabled();
            Duration newduration = new Duration(item1.getExpectedDuration());
            newduration = newduration.add(new Duration(item2.getExpectedDuration()));
            item1.setExpectedDuration(newduration.toMillis());
            item1.getAmbience().setAmbience(new ArrayList<>());
            item1.getAmbience().setEnabled(false);
            if (ambiencewasenabled) {
                Duration ambienceduration = Duration.ZERO;
                if (item1.getAmbience().isEnabled()) {ambienceduration = ambienceduration.add(item1.getAmbience().gettotalDuration());}
                if (item2.getAmbience().isEnabled()) {ambienceduration = ambienceduration.add(item2.getAmbience().gettotalDuration());}
                if (ambienceduration.lessThan(newduration)) {
                    String[] options = {"Quick Add Ambience", "Customize Ambience", "Don't Add Ambience"};
                    ChoiceDialog<String> addambiencechoicedialog = new ChoiceDialog<>(options[0], options);
                    addambiencechoicedialog.setHeaderText("Ambience Was Cleared But Previously Enabled For One Or Both Of These Playback Items");
                    addambiencechoicedialog.setContentText("Select How You Would Like To Add Ambience");
                    addambiencechoicedialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(addambiencechoicedialog.selectedItemProperty().isNull());
                    if (addambiencechoicedialog.showAndWait().isPresent()) {
                        System.out.println(addambiencechoicedialog.getSelectedItem());
                        switch (addambiencechoicedialog.getSelectedItem()) {
                            case "Quick Add Ambience":
                                QuickAddAmbience quickAddAmbience = new QuickAddAmbience(preferences, availableAmbiences, Collections.singletonList(item1));
                                quickAddAmbience.initModality(Modality.APPLICATION_MODAL);
                                quickAddAmbience.showAndWait();
                                if (quickAddAmbience.isAccepted()) { item1 = quickAddAmbience.getPlaybackItemList().get(0); }
                                break;
                            case "Customize Ambience":
                                CustomizeAmbience customizeAmbience = new CustomizeAmbience(preferences, item1, availableAmbiences);
                                customizeAmbience.showAndWait();
                                if (customizeAmbience.isAccepted()) { item1 = customizeAmbience.getPlaybackItem(); }
                                break;
                            case "Don't Add Ambience":
                                break;
                        }
                    }
                }
            }
            itemsinsession.set(item1index, item1);
            createdsession.setPlaybackItems(itemsinsession);
        }
    }
    // Action Toolbar
    public void clearloadedsession() {
        if (createdsession != null && new ConfirmationDialog(preferences, "Clear Session", "Really Clear Session?", "This will clear any unsaved changes you made to this session", true).getResult()) {
            createdsession = new Session();
            populatetable();
        }
    }
    public void addcreatedsessiontofavorites() {
        if (createdsession != null) {
            NameFavoriteSession nameFavoriteSession = new NameFavoriteSession(favoriteSessions);
            nameFavoriteSession.initModality(Modality.APPLICATION_MODAL);
            nameFavoriteSession.showAndWait();
            if (nameFavoriteSession.isAccepted()) {
                favoriteSessions.add(nameFavoriteSession.getName(), createdsession);
                new InformationDialog(preferences, "Favorite Session Added", "Session Added To Favorites", null, true);
                for (FavoriteSession i : favoriteSessions.getFavoriteSessions()) {
                    System.out.println(i.getName());
                }
            }
        }
    }
    public void savecreatedsessionasfile() {
        if (createdsession != null && ! createdsession.getPlaybackItems().isEmpty()) {
            FileChooser fileChooser = new FileChooser();
            File savefile = fileChooser.showSaveDialog(getStage());
            if (savefile != null) {
                if (! savefile.getName().endsWith(".xml")) {savefile = new File(savefile + ".xml");}
                try {
                    JAXBContext context = JAXBContext.newInstance(Session.class);
                    Marshaller createMarshaller = context.createMarshaller();
                    createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    createMarshaller.marshal(createdsession, savefile);
                    new InformationDialog(preferences, "Session Saved", "Session Saved To: " + savefile.getAbsolutePath(), null, true);
                } catch (JAXBException e) {
                    e.printStackTrace();
                    new ErrorDialog(preferences, "Cannot Save", "Cannot Save File", "Check File Permissions", true);
                }
            }
        }
    }
    public void playcreatedsession() {
        if (createdsession != null) {
            boolean updatesession = false;
            for (PlaybackItem i : createdsession.getPlaybackItems()) {
                if (i.isRampOnly()) {
                    SoundFile rampfile = RampFiles.getRampFile(i, createdsession.getPlaybackItems().get(createdsession.getPlaybackItems().indexOf(i) + 1));
                    i.setExpectedDuration(rampfile.getDuration());
                    updatesession = true;
                }
            }
            if (updatesession) {createdsession.calculateexpectedduration();}
            getStage().setIconified(true);
            Player player = new Player(this, sessions, allGoals, createdsession);
            player.initModality(Modality.APPLICATION_MODAL);
            player.showAndWait();
            getStage().setIconified(false);
        }
    }
    public void exportcreatedsession() {
        new InformationDialog(preferences, "Under Construction", "This Feature Coming Soon!", "");
    }
    // Utility Methods
    private void populatetable() {
        calculatedurationandestimatedcompletion();
        if (createdtableplaybackitems != null) {createdtableplaybackitems.clear();}
        else {createdtableplaybackitems = new ArrayList<>();}
        if (! createdtableitems.isEmpty()) {createdtableitems.clear();}
        if (createdsession.getPlaybackItems() != null && ! createdsession.getPlaybackItems().isEmpty()) {
            createdtableplaybackitems.addAll(createdsession.getPlaybackItems());
            int number = 1;
            for (PlaybackItem i : createdtableplaybackitems) {
                createdtableitems.add(new TableItem_Number_Name_Duration_Ambience(number, i.getName(), i.getdurationasString(), i.getAmbienceasString()));
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
        DurationMenu.setDisable(nosessionloaded || tableempty || selectedindex == -1);
        AmbienceMenu.setDisable(nosessionloaded || tableempty || selectedindex == -1);
        PlayButton.setDisable(nosessionloaded || tableempty);
        SaveAsFileButton.setDisable(nosessionloaded || tableempty);
        ExportButton.setDisable(nosessionloaded || tableempty);
        RemoveButton.setDisable(nosessionloaded || tableempty || selectedindex == -1);
        MoveUpButton.setDisable(nosessionloaded || tableempty || selectedindex == -1 || selectedindex == 0);
        MoveDownButton.setDisable(nosessionloaded || tableempty || selectedindex == -1 || selectedindex == CreatedTableView.getItems().size() -1);
        AddToFavoritesButton.setDisable(nosessionloaded || tableempty);
        ClearButton.setDisable(nosessionloaded || tableempty);
        if (nosessionloaded) {CreatedTableView.setPlaceholder(new Label("Please Create Or Load A Session"));}
        else {CreatedTableView.setPlaceholder(new Label("Session Is Empty"));}
    }
    private void calculatedurationandestimatedcompletion() {
        if (createdsession != null && ! createdsession.getPlaybackItems().isEmpty()) {
            SessionSummary_Duration.setText(Util.formatdurationtoStringSpelledOutShort(createdsession.getExpectedSessionDuration(), false));
            LocalTime now = LocalTime.now();
            LocalTime completiontime = now.plusMinutes((int) createdsession.getExpectedSessionDuration().toMinutes());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
            String completiontext = completiontime.format(formatter);
            SessionSummary_CompletionTime.setText(completiontext);
            SessionSummary_CompletionTime.setTooltip(new Tooltip("Completion Time If You Start Now"));
            updatecompletiontime = null;
            new Timeline(new KeyFrame(Duration.millis(5000), ae -> calculatedurationandestimatedcompletion())).play();
        } else {
            SessionSummary_CompletionTime.setText(null);
            SessionSummary_CompletionTime.setTooltip(null);
            SessionSummary_Duration.setText(null);
            SessionSummary_Duration.setTooltip(null);
        }
    }


// Progress Tab
    // Overview Tab Methods
    public void populateprogressoverviewchartandbalancepie() {
        ProgressBalancePieChart.getData().clear();
        ProgressOverviewBarChart.getData().clear();
        if (sessions.getSession() != null) {
            if (sessions.getSession().isEmpty()) {return;}
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            ArrayList<Duration> sessionpartdurations = new ArrayList<>();
            Duration totaltimepracticed = Duration.ZERO;
            for (int i = 0; i<15; i++) {sessionpartdurations.add(Duration.ZERO);}
            for (Session i : sessions.getSession()) {
                for (PlaybackItem x : i.getPlaybackItems()) {
                    if (x.getPracticeTime() > 0.0) {
                        Duration duration = sessionpartdurations.get(x.getCreationindex()).add(new Duration(x.getPracticeTime()));
                        sessionpartdurations.set(x.getCreationindex(), duration);
                    }
                }
                totaltimepracticed = totaltimepracticed.add(i.getSessionPracticedTime());
            }
            Duration averageduration = new Duration(totaltimepracticed.toMillis() / 15);
            boolean displayinhours = false;
            if (averageduration.greaterThanOrEqualTo(Duration.hours(2.5))) {displayinhours = true;}
            int count = 0;
            ObservableList<PieChart.Data> piechartvalues = FXCollections.observableArrayList();
            for (Duration i : sessionpartdurations) {
                double time;
                if (displayinhours) {time = i.toHours(); ProgressOverviewNumbersAxis.setLabel("Hours");}
                else {time = i.toMinutes(); ProgressOverviewNumbersAxis.setLabel("Minutes");}
                series.getData().add(new XYChart.Data<>(ALLNAMES.get(count), time));
                if (i.greaterThan(Duration.ZERO)) {piechartvalues.add(new PieChart.Data(ALLNAMES.get(count), time));}
                count++;
            }
            ProgressBalancePieChart.setData(piechartvalues);
            double total = 0;
            for (PieChart.Data d : ProgressBalancePieChart.getData()) {total += d.getPieValue();}
            for (final PieChart.Data data : ProgressBalancePieChart.getData()) {
                String name = String.format("%s (%.1f%%)", data.getName(), 100 * data.getPieValue() / total);
                data.setName(name);
            }
            ProgressOverviewTotalTimePracticed.setText(Util.formatdurationtoStringSpelledOutShort(totaltimepracticed, true));
            ProgressOverviewTotalTimePracticed.setTooltip(new Tooltip(Util.formatdurationtoStringSpelledOut(totaltimepracticed, Double.MAX_VALUE)));
            ProgressOverviewBarChart.getData().add(series);
            ProgressOverviewBarChart.setLegendVisible(false);
        }
    }
    // Session Browser Tab Methods
    public void populatesessionbrowsertable() {
        if (sessions.getSession() != null) {
            List<Session> filteredsessions = new ArrayList<>();
            for (Session i : sessions.getSession()) {
                if (SessionBrowser_Filter_DateRange_From_Checkbox.isSelected() && SessionBrowser_Filter_DateRange_From.getValue() != null) {
                    if (i.getDate_Practiced().isBefore(SessionBrowser_Filter_DateRange_From.getValue())) {continue;}
                }
                if (SessionBrowser_Filter_DateRange_To_Checkbox.isSelected() && SessionBrowser_Filter_DateRange_To.getValue() != null) {
                    if (i.getDate_Practiced().isAfter(SessionBrowser_Filter_DateRange_To.getValue())) {continue;}
                }
                Duration durationtotest = i.getSessionPracticedTime();
                if (SessionBrowser_Filter_Duration_Min_Checkbox.isSelected()) {
                    Duration minduration = Duration.ZERO;
                    minduration.add(Duration.hours(SessionBrowser_Filter_Duration_From_Hours.getValue()));
                    minduration.add(Duration.minutes(SessionBrowser_Filter_Duration_From_Minutes.getValue()));
                    if (durationtotest.lessThan(minduration)) {continue;}
                }
                if (SessionBrowser_Filter_Duration_Max_Checkbox.isSelected()) {
                    Duration maxduration = Duration.ZERO;
                    maxduration.add(Duration.hours(SessionBrowser_Filter_Duration_To_Hours.getValue()));
                    maxduration.add(Duration.minutes(SessionBrowser_Filter_Duration_To_Minutes.getValue()));
                    if (durationtotest.lessThan(maxduration)) {continue;}
                }
                filteredsessions.add(i);
            }
            ObservableList<SessionBrowserTableItem> sessionlist = FXCollections.observableArrayList();
            for (Session i : filteredsessions) {sessionlist.add(new SessionBrowserTableItem(i));}
            PracticedSessionListTable.setItems(sessionlist);
        } else {PracticedSessionListTable.setPlaceholder(new Label("No Practiced Sessions"));}
    }
    public void populatesessiondetailstable() {
        SessionBrowserTableItem item = PracticedSessionListTable.getSelectionModel().getSelectedItem();
        SessionBrowser_DetailsTable.getItems().clear();
        SessionBrowser_Details_TotalDuration.setText(null);
        SessionBrowser_Details_TotalDuration_Label.setDisable(item == null);
        SessionBrowser_Details_TotalDuration.setDisable(item == null);
        if (item != null) {
            Session selectedsession = this.sessions.get(item.uuid);
            ObservableList<TableItem_Number_Name_Duration> sessionitems = FXCollections.observableArrayList();
            int count = 1;
            for (PlaybackItem i : selectedsession.getPlaybackItems()) {
                sessionitems.add(new TableItem_Number_Name_Duration(count, i.getName(), Util.formatdurationtoStringDecimalWithColons(new Duration(i.getPracticeTime()))));
                count++;
            }
            SessionBrowser_DetailsTable.setItems(sessionitems);
            SessionBrowser_Details_TotalDuration.setText(Util.formatdurationtoStringSpelledOutShort(selectedsession.getSessionPracticedTime(), true));
            SessionBrowser_Details_TotalDuration.setTooltip(new Tooltip(Util.formatdurationtoStringSpelledOut(selectedsession.getSessionPracticedTime(), Double.MAX_VALUE)));
            SessionBrowser_ViewNotesButton.setDisable(selectedsession.getNotes() == null || selectedsession.getNotes().isEmpty());
        } else {SessionBrowser_DetailsTable.setPlaceholder(new Label("Select A Session To View Details"));}
    }
    public void viewsessionnotes() {
        SessionBrowserTableItem item = PracticedSessionListTable.getSelectionModel().getSelectedItem();
        if (item != null) {
            EditSessionNotes editSessionNotes = new EditSessionNotes(sessions.get(item.uuid), "Session Notes", true);
            editSessionNotes.initModality(Modality.APPLICATION_MODAL);
            editSessionNotes.showAndWait();
        }
    }
    public void editsessionnotes(Session session) {
        EditSessionNotes editSessionNotes = new EditSessionNotes(session, "Edit Session Notes", false);
        editSessionNotes.initModality(Modality.APPLICATION_MODAL);
        editSessionNotes.showAndWait();
        if (editSessionNotes.isAccepted()) {
            Session editedsession = editSessionNotes.getEditedSession();
            List<Session> sessionlist = sessions.getSession();
            for (Session i : sessions.getSession()) {
                if (i.getId().equals(editedsession.getId())) {
                    int index = sessions.getSession().indexOf(i);
                    sessionlist.set(index, editedsession);
                }
            }
            this.sessions.setSession(sessionlist);
        }
    }
        // Filter
    public void populatesessionbrowserfilter() {
        Duration highestduration = Duration.ZERO;
        if (sessions.getSession() != null) {
            for (Session i : sessions.getSession()) {
                if (i.getSessionPracticedTime().greaterThanOrEqualTo(highestduration)) {
                    highestduration = i.getSessionPracticedTime();
                }
            }
            Double hours = highestduration.toMinutes() / 60.0;
            Double minutes = highestduration.toMinutes() % 60.0;
            SessionBrowser_Filter_Duration_From_Hours.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
            SessionBrowser_Filter_Duration_From_Hours.valueProperty().addListener((observable, oldValue, newValue) -> {
                Duration newfromduration = Duration.hours(new Double(newValue));
                newfromduration = newfromduration.add(Duration.minutes(SessionBrowser_Filter_Duration_From_Minutes.getValue()));
                Duration toduration = Duration.hours(SessionBrowser_Filter_Duration_To_Hours.getValue());
                toduration = toduration.add(Duration.minutes(SessionBrowser_Filter_Duration_To_Minutes.getValue()));
                if (newfromduration.greaterThanOrEqualTo(toduration)) {SessionBrowser_Filter_Duration_From_Hours.getValueFactory().setValue(oldValue);}
                populatesessionbrowsertable();
            });
            SessionBrowser_Filter_Duration_From_Hours.setOnScroll(event -> {
                Integer newvalue = SessionBrowser_Filter_Duration_From_Hours.getValue();
                if (event.getDeltaY() < 0) {newvalue -= 1;} else {newvalue += 1;}
                SessionBrowser_Filter_Duration_From_Hours.getValueFactory().setValue(newvalue);
            });
            SessionBrowser_Filter_Duration_From_Minutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
            SessionBrowser_Filter_Duration_From_Minutes.valueProperty().addListener((observable, oldValue, newValue) -> {
                Duration newfromduration = Duration.minutes(new Double(newValue));
                newfromduration = newfromduration.add(Duration.hours(SessionBrowser_Filter_Duration_From_Hours.getValue()));
                Duration toduration = Duration.hours(SessionBrowser_Filter_Duration_To_Hours.getValue());
                toduration = toduration.add(Duration.minutes(SessionBrowser_Filter_Duration_To_Minutes.getValue()));
                if (newfromduration.greaterThanOrEqualTo(toduration)) {SessionBrowser_Filter_Duration_From_Minutes.getValueFactory().setValue(oldValue);}
                populatesessionbrowsertable();
            });
            SessionBrowser_Filter_Duration_From_Minutes.setOnScroll(event -> {
                Integer newvalue = SessionBrowser_Filter_Duration_From_Minutes.getValue();
                if (event.getDeltaY() < 0) {newvalue -= 1;} else {newvalue += 1;}
                SessionBrowser_Filter_Duration_From_Minutes.getValueFactory().setValue(newvalue);
            });
            SessionBrowser_Filter_Duration_To_Hours.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, hours.intValue()));
            SessionBrowser_Filter_Duration_To_Hours.valueProperty().addListener((observable, oldValue, newValue) -> {
                Duration newtoduration = Duration.hours(new Double(newValue));
                newtoduration = newtoduration.add(Duration.minutes(SessionBrowser_Filter_Duration_To_Minutes.getValue()));
                Duration fromduration = Duration.hours(SessionBrowser_Filter_Duration_From_Hours.getValue());
                fromduration = fromduration.add(Duration.minutes(SessionBrowser_Filter_Duration_From_Minutes.getValue()));
                if (newtoduration.lessThanOrEqualTo(fromduration)) {SessionBrowser_Filter_Duration_To_Hours.getValueFactory().setValue(oldValue);}
                populatesessionbrowsertable();
            });
            SessionBrowser_Filter_Duration_To_Hours.setOnScroll(event -> {
                Integer newvalue = SessionBrowser_Filter_Duration_To_Hours.getValue();
                if (event.getDeltaY() < 0) {newvalue -= 1;} else {newvalue += 1;}
                SessionBrowser_Filter_Duration_To_Hours.getValueFactory().setValue(newvalue);
            });
            SessionBrowser_Filter_Duration_To_Minutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, minutes.intValue()));
            SessionBrowser_Filter_Duration_To_Minutes.valueProperty().addListener((observable, oldValue, newValue) -> {
                Duration newtoduration = Duration.minutes(new Double(newValue));
                newtoduration = newtoduration.add(Duration.hours(SessionBrowser_Filter_Duration_To_Minutes.getValue()));
                Duration fromduration = Duration.hours(SessionBrowser_Filter_Duration_From_Hours.getValue());
                fromduration = fromduration.add(Duration.minutes(SessionBrowser_Filter_Duration_From_Minutes.getValue()));
                if (newtoduration.lessThanOrEqualTo(fromduration)) {SessionBrowser_Filter_Duration_To_Minutes.getValueFactory().setValue(oldValue);}
                populatesessionbrowsertable();
            });
            SessionBrowser_Filter_Duration_To_Minutes.setOnScroll(event -> {
                Integer newvalue = SessionBrowser_Filter_Duration_To_Minutes.getValue();
                if (event.getDeltaY() < 0) {newvalue -= 1;} else {newvalue += 1;}
                SessionBrowser_Filter_Duration_To_Minutes.getValueFactory().setValue(newvalue);
            });
            SessionBrowser_Filter.setDisable(sessions.getSession() == null);
            if (sessions.getSession() != null) {
                LocalDate mindate = null;
                LocalDate maxdate = null;
                for (Session i : sessions.getSession()) {
                    LocalDate sessiondate = i.getDate_Practiced();
                    if (mindate == null) {mindate = sessiondate;}
                    else if (sessiondate.isBefore(mindate)) {mindate = sessiondate;}
                    if (maxdate == null) {maxdate = sessiondate;}
                    else if (sessiondate.isAfter(maxdate)) {maxdate = sessiondate;}
                }
                SessionBrowser_Filter_DateRange_From.setValue(mindate);
                LocalDate finalMindate = mindate;
                LocalDate finalMaxdate = maxdate;
                SessionBrowser_Filter_DateRange_From.setDayCellFactory(new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(DatePicker param) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item.isBefore(finalMindate) || item.isAfter(finalMaxdate)) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                });
                SessionBrowser_Filter_DateRange_To.setValue(maxdate);
                SessionBrowser_Filter_DateRange_To.setDayCellFactory(new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(DatePicker param) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item.isAfter(finalMaxdate) || item.isBefore(finalMindate)) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                });
                SessionBrowser_Filter_DateRange_From.setOnAction(event -> populatesessionbrowsertable());
                SessionBrowser_Filter_DateRange_To.setOnAction(event -> populatesessionbrowsertable());
            }
        } else {

        }
    }


// AllGoals Tab
    // Overview Tab Methods
    private void populategoalsoverviewtable() {
        GoalsOverview_Table.getItems().clear();
        ObservableList<GoalOverviewTableItem> tableitems = FXCollections.observableArrayList();
        Session demosession = new Session();
        for (int i = 0; i < 15; i++) {
            String practicedtime;
            String currentgoaltext;
            String percentcompleted;
            String goalscompleted;
            PlaybackItem playbackItem = demosession.getplaybackitem(i);
            Duration practicedduration = sessions.gettotalpracticedtime(playbackItem, false);
            practicedtime = Util.formatdurationtoStringSpelledOutShort(practicedduration, true);
            Goal currentgoal = allGoals.getplaybackItemGoals(i).getCurrentGoal();
            if (currentgoal == null) {
                currentgoaltext = "None";
                percentcompleted = "-";
            } else {
                currentgoaltext = Util.formatdurationtoStringSpelledOutShort(currentgoal.getDuration(), true);
                percentcompleted = String.format("%.2f", (practicedduration.toMillis() / currentgoal.getDuration().toMillis()) * 100) + "%";
            }
            int completedgoalcount = 0;
            List<Goal> goallist = allGoals.getplaybackItemGoals(i).getGoals();
            if (goallist != null) {
                for (Goal x : goallist) {if (x.getCompleted()) {completedgoalcount++;}}
            }
            goalscompleted = String.valueOf(completedgoalcount);
            tableitems.add(new GoalOverviewTableItem(playbackItem.getName(), practicedtime, currentgoaltext, percentcompleted, goalscompleted));
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
            GoalsIndividual_Table.getItems().clear();
            Session dummysession = new Session();
            PlaybackItem playbackItem = dummysession.getplaybackitem(index);
            Duration duration = sessions.gettotalpracticedtime(playbackItem, false);
            GoalsIndividual_PracticedTime.setText(Util.formatdurationtoStringDecimalWithColons(duration));
            GoalsIndividual_PracticedTime.setTooltip(new Tooltip(Util.formatdurationtoStringDecimalWithColons(duration)));
            ObservableList<GoalDetailsTableItem> tableitems = FXCollections.observableArrayList();
            List<Goal> goalList = allGoals.getplaybackItemGoals(index).getGoals();
            boolean hascompletedgoals = allGoals.getplaybackItemGoals(index).hasCompletedGoals();
            GoalsIndividual_ShowCompletedGoalsCheckbox.setDisable(! hascompletedgoals);
            if (hascompletedgoals) { GoalsIndividual_ShowCompletedGoalsCheckbox.setTooltip(null); }
            else {GoalsIndividual_ShowCompletedGoalsCheckbox.setTooltip(new Tooltip("No Completed Goals For " + GoalsIndividual_SelectedSessionItemChoiceBox.getValue()));}
            if (goalList != null && ! goalList.isEmpty()) {
                for (Goal i :goalList) {
                    if (! GoalsIndividual_ShowCompletedGoalsCheckbox.isSelected() && i.getCompleted()) {continue;}
                    String goaltime = Util.formatdurationtoStringDecimalWithColons(i.getDuration());
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
            } else {GoalsIndividual_Table.setPlaceholder(new Label("No Goals For " + playbackItem.getName()));}
        } else {GoalsIndividual_Table.setPlaceholder(new Label("Select A Playback Item To View Goals"));}
    }
    private void setnewgoal(int playbackitemindex, String playbackitemname) {
        SetNewGoalDialog setNewGoalDialog = new SetNewGoalDialog(allGoals, playbackitemindex, playbackitemname);
        setNewGoalDialog.initModality(Modality.APPLICATION_MODAL);
        setNewGoalDialog.showAndWait();
        if (setNewGoalDialog.getSetgoal() != null) {
            PlaybackItemGoals playbackItemGoals = allGoals.getplaybackItemGoals(playbackitemindex);
            playbackItemGoals.add(setNewGoalDialog.getSetgoal());
            allGoals.setPlaybackItemGoals(playbackitemindex, playbackItemGoals);
        }
    }
    public void setnewgoalfromdetailstab() {
        int index = GoalsIndividual_SelectedSessionItemChoiceBox.getSelectionModel().getSelectedIndex();
        setnewgoal(index, ALLNAMES.get(index));
        populategoalsdetailstable();
    }
    public void setnewgoalfromoverviewtab() {
        int index = GoalsOverview_Table.getSelectionModel().getSelectedIndex();
        setnewgoal(index, ALLNAMES.get(index));
        populategoalsoverviewtable();
    }
    public void deletegoal() {
        if (new ConfirmationDialog(preferences, "Delete Goal", "This Will Permanently Delete This Goal", "Continue?").getResult()) {
            int tableindex = GoalsIndividual_Table.getSelectionModel().getSelectedIndex();
            int choiceboxindex = GoalsIndividual_SelectedSessionItemChoiceBox.getSelectionModel().getSelectedIndex();
            Goal goal;
            if (! GoalsIndividual_ShowCompletedGoalsCheckbox.isSelected()) {goal = allGoals.getplaybackItemGoals(choiceboxindex).getCurrentGoals().get(tableindex);}
            else {goal = allGoals.getplaybackItemGoals(choiceboxindex).getGoals().get(tableindex);}
            PlaybackItemGoals playbackItemGoals = allGoals.getplaybackItemGoals(choiceboxindex);
            playbackItemGoals.remove(goal);
            allGoals.setPlaybackItemGoals(choiceboxindex, playbackItemGoals);
            populategoalsdetailstable();
        }
    }

}