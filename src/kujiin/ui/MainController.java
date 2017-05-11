package kujiin.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.ui.ambience.AvailableAmbienceEditor;
import kujiin.ui.boilerplate.IconImageView;
import kujiin.ui.creation.AdjustDuration;
import kujiin.ui.creation.CustomizeAmbience;
import kujiin.ui.creation.QuickAddAmbience;
import kujiin.ui.creation.SetDurationWithAmbienceOption;
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

// Bugs To Fix
    // TODO End And Playback Same Session Is Causing Nullpointer Exception
    // TODO Find A Way To Reset Session After Stop Animation Ends

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
    public Tab PlayExportTab;
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
    public Button AddToFavoritesButton;
    public Button PlayButton;
    public Button SaveAsFileButton;
    public Button ExportButton;
    public Label CreatorStatusBar;
    // Progress Tab
    public Tab ProgressTab;
        // Overview Tab
    public BarChart<String, Number> ProgressOverviewBarChart;
    public TextField ProgressOverviewTotalTimePracticed;
    public TextField ProgressOverviewItemWithMostProgress;
    public PieChart ProgressBalancePieChart;
        // Session Browser Tab
    public ListView<String> SessionBrowser_SelectSessionListView;
    public CheckBox SessionBrowser_Filter_DateRange_From_Checkbox;
    public CheckBox SessionBrowser_Filter_DateRange_To_Checkbox;
    public CheckBox SessionBrowser_Filter_Duration_Min_Checkbox;
    public CheckBox SessionBrowser_Filter_Duration_Max_Checkbox;
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
    public Tab GoalsTab;
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
    private PlaybackItem createdtableselecteditem;
    private ObservableList<TableItem_Number_Name_Duration_Ambience> createdtableitems = FXCollections.observableArrayList();
    private ArrayList<PlaybackItem> createdtableplaybackitems;


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
        setupTables();
        ProgressTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (ProgressTab.isSelected()) {
                populateprogressoverviewchartandbalancepie();
                populatesessionbrowserlistview();
                populatesessiondetailstable();
            }
        });
        SessionBrowser_SelectSessionListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> populatesessiondetailstable());

    }
    private void setupTables() {
        // Creation Table
        CreatedTableNumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
        CreatedTableItemColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        CreatedTableDurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
        CreatedTableAmbienceColumn.setCellValueFactory(cellData -> cellData.getValue().ambience);
        CreatedTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> tableselectionchanged());
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
            DurationMenu.setGraphic(new IconImageView(ICON_EDITDURATION, fh));
            AmbienceMenu.setGraphic(new IconImageView(ICON_AMBIENCE, fh));
            MoveUpButton.setGraphic(new IconImageView(ICON_MOVEUP, fh));
            MoveDownButton.setGraphic(new IconImageView(ICON_MOVEDOWN, fh));
            RemoveButton.setGraphic(new IconImageView(ICON_REMOVE, fh));
            AddToFavoritesButton.setGraphic(new IconImageView(ICON_ADDTOFAVORITE, fh));
            PlayButton.setGraphic(new IconImageView(ICON_PLAY, fh));
            SaveAsFileButton.setGraphic(new IconImageView(ICON_EXPORTTODOCUMENT, fh));
            ExportButton.setGraphic(new IconImageView(ICON_EXPORTTOAUDIO, fh));
        }
        if (dt == IconDisplayType.ICONS_ONLY) {
            AmbienceMenu.setText("");
            AddItemsMenu.setText("");
            CreateSessionMenu.setText("");
            OpenFileButton.setText("");
            OpenRecentSessionsButton.setText("");
            OpenFavoritesButton.setText("");
            DurationMenu.setText("");
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
        DurationMenu.setTooltip(new Tooltip("Edit Duration"));
        AmbienceMenu.setTooltip(new Tooltip("Add/Edit Ambience"));
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
            if (new ConfirmationDialog(getPreferences(), "Add Available Ambience", "There Is No Available Ambience For Any Playback Items", "Open Ambience Editor To Add Ambience?", true).getResult()) {
                AmbienceEditor_Simple ambienceEditor_simple = new AmbienceEditor_Simple(availableAmbiences, getPreferences());
                ambienceEditor_simple.initModality(Modality.APPLICATION_MODAL);
                ambienceEditor_simple.showAndWait();
            }
        }
        if (createdsession != null && ! new ConfirmationDialog(preferences, "Load New Session", "Really Load New Session?", "This will clear any unsaved changes you made to this session", true).getResult()) {
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
                new ErrorDialog(preferences, "Invalid File", "'" + filetoload.getName() + "' Isn't A Valid Session File", "Select A Valid Session To Load", true);
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
        PlaybackItem playbackItem = createdsession.getplaybackitem(availableambienceindex);
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
            createdsession.addplaybackitems(Collections.singletonList(playbackItem)); createdsession.calculateexpectedduration();}
        populatetable();
    }
    private void add(int[] availableambienceindexes) {
        List<PlaybackItem> items = new ArrayList<>();
        for (int i : availableambienceindexes) {items.add(createdsession.getplaybackitem(i));}
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
            createdsession.addplaybackitems(items); createdsession.calculateexpectedduration();
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
                populatetable();
                syncbuttons();
            }
        } else {new InformationDialog(preferences, "Cannot Edit Duration", "Select A Single Table Item To Edit Duration", null, true);}
    }
    public void quickaddambience_repeat() {
        if (createdtableselecteditem != null) {
            createdtableselecteditem.getAmbience().addavailableambience_repeat(Duration.millis(createdtableselecteditem.getExpectedDuration()), availableAmbiences.getsessionpartAmbience(createdtableselecteditem.getCreationindex()));
            populatetable();
        }
    }
    public void quickaddambience_shuffle() {
        if (createdtableselecteditem != null) {
            createdtableselecteditem.getAmbience().addavailableambience_shuffle(Duration.millis(createdtableselecteditem.getExpectedDuration()), availableAmbiences.getsessionpartAmbience(createdtableselecteditem.getCreationindex()));
            populatetable();
        }
    }
    public void customizeambience() {
        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
        if (selectedindex != -1 && createdtableselecteditem != null) {
            CustomizeAmbience customizeAmbience = new CustomizeAmbience(preferences, createdtableselecteditem, availableAmbiences);
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
            Collections.swap(itemsinsession, selectedindex, selectedindex - 1);
            createdsession.setPlaybackItems(itemsinsession);
            populatetable();
        }
    }
    public void movedownincreatortable() {
        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
        if (selectedindex != -1 && selectedindex < createdtableplaybackitems.size() - 1) {
            ArrayList<PlaybackItem> itemsinsession = createdsession.getPlaybackItems();
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
            new InformationDialog(preferences, "Favorite Session Added", "Session Added To Favorites", null, true);
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
            for (PlaybackItem i : createdtableplaybackitems) {
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
        DurationMenu.setDisable(nosessionloaded || tableempty || selectedindex == -1);
        AmbienceMenu.setDisable(nosessionloaded || tableempty || selectedindex == -1);
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
    public void populateprogressoverviewchartandbalancepie() {
        if (sessions.getSession() != null) {
            if (sessions.getSession().isEmpty()) {return;}
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            ArrayList<Duration> sessionpartdurations = new ArrayList<>();
            Duration totaltimepracticed = Duration.ZERO;
            for (int i = 0; i<15; i++) {sessionpartdurations.add(Duration.ZERO);}
            for (Session i : sessions.getSession()) {
                for (int x = 0; x<15; x++) {
                    if (i.getplaybackitem(x).getExpectedDuration() > 0.0) {
                        Duration duration = sessionpartdurations.get(x);
                        duration = duration.add(Duration.millis(i.getplaybackitem(x).getExpectedDuration()));
                        sessionpartdurations.set(x, duration);
                    }
                }
                totaltimepracticed = totaltimepracticed.add(i.getSessionPracticedTime());
            }
            int count = 0;
            int highestdurationpracticedindex = -1;
            ObservableList<PieChart.Data> piechartvalues = FXCollections.observableArrayList();
            for (Duration i : sessionpartdurations) {
                if (highestdurationpracticedindex == -1) {highestdurationpracticedindex = 0;}
                else if (i.greaterThan(sessionpartdurations.get(count - 1))) {highestdurationpracticedindex = count;}
                series.getData().add(new XYChart.Data<>(ALLNAMES.get(count), i.toHours()));
                piechartvalues.add(new PieChart.Data(ALLNAMES.get(count), i.toHours()));
                count++;
            }
            ProgressBalancePieChart.setData(piechartvalues);
            ProgressOverviewTotalTimePracticed.setText(Util.formatdurationtoStringSpelledOut(totaltimepracticed, ProgressOverviewTotalTimePracticed.getWidth()));
            ProgressOverviewItemWithMostProgress.setText(ALLNAMES.get(highestdurationpracticedindex));
            ProgressOverviewBarChart.getData().add(series);
            ProgressOverviewBarChart.setLegendVisible(false);
        }
    }
    // Session Browser Tab Methods
    public void populatesessionbrowserlistview() {
        ObservableList<String> sessionlist = FXCollections.observableArrayList();
        List<Session> allsessions = this.sessions.getSession();
        List<Session> filteredsessions = new ArrayList<>();
        if (allsessions == null) {allsessions = new ArrayList<>();}
        for (Session i : allsessions) {
            if (SessionBrowser_Filter_DateRange_From_Checkbox.isSelected() && SessionBrowser_Filter_DateRange_From.getValue() != null) {
                if (i.getDate_Practiced().isBefore(SessionBrowser_Filter_DateRange_From.getValue())) {continue;}
            }
            if (SessionBrowser_Filter_DateRange_To_Checkbox.isSelected() && SessionBrowser_Filter_DateRange_To.getValue() != null) {
                if (i.getDate_Practiced().isAfter(SessionBrowser_Filter_DateRange_To.getValue())) {continue;}
            }
            Duration durationtotest = i.getSessionPracticedTime();
            if (SessionBrowser_Filter_Duration_Min_Checkbox.isSelected()) {
                Duration minduration = Duration.ZERO;
                minduration.add(Duration.hours(Integer.parseInt(SessionBrowser_Filter_Duration_From_Hours.getText())));
                minduration.add(Duration.minutes(Integer.parseInt(SessionBrowser_Filter_Duration_From_Minutes.getText())));
                if (durationtotest.lessThan(minduration)) {continue;}
            }
            if (SessionBrowser_Filter_Duration_Max_Checkbox.isSelected()) {
                Duration maxduration = Duration.ZERO;
                maxduration.add(Duration.hours(Integer.parseInt(SessionBrowser_Filter_Duration_To_Hours.getText())));
                maxduration.add(Duration.minutes(Integer.parseInt(SessionBrowser_Filter_Duration_To_Minutes.getText())));
                if (durationtotest.lessThan(maxduration)) {continue;}
            }
            filteredsessions.add(i);
        }
        for (Session i : filteredsessions) {
            sessionlist.add(String.format("%s (%s)", i.getDate_Practiced().format(Util.dateFormat), Util.formatdurationtoStringDecimalWithColons(i.getSessionPracticedTime())));
        }
        SessionBrowser_SelectSessionListView.setItems(sessionlist);
    }
    public void populatesessiondetailstable() {
        int index = SessionBrowser_SelectSessionListView.getSelectionModel().getSelectedIndex();
        SessionBrowser_DetailsTable.getItems().clear();
        SessionBrowser_Details_TotalDuration.setText(null);
        if (index != -1) {
            Session selectedsession = this.sessions.get(index);
            ObservableList<TableItem_Number_Name_Duration> sessionitems = FXCollections.observableArrayList();
            int count = 1;
            for (PlaybackItem i : selectedsession.getPlaybackItems()) {
                sessionitems.add(new TableItem_Number_Name_Duration(count, i.getName(), Util.formatdurationtoStringDecimalWithColons(new Duration(i.getExpectedDuration()))));
                count++;
            }
            SessionBrowser_DetailsTable.setItems(sessionitems);
            SessionBrowser_Details_TotalDuration.setText(Util.formatdurationtoStringSpelledOut(selectedsession.getSessionPracticedTime(), SessionBrowser_Details_TotalDuration.getWidth()));
        } else {SessionBrowser_DetailsTable.setPlaceholder(new Label("Select A Session To View Details"));}
    }
    public void checkfilterdate_from() {
        if (SessionBrowser_Filter_DateRange_To.getValue() != null) {
            if (SessionBrowser_Filter_DateRange_To.getValue().isBefore(SessionBrowser_Filter_DateRange_From.getValue())) {
                new InformationDialog(preferences, "Invalid Date", "From Date Must Be Before To Date", null, true);
                SessionBrowser_Filter_DateRange_From.setValue(null);
            }
        }
    }
    public void checkfilterdate_to() {
        if (SessionBrowser_Filter_DateRange_From.getValue() != null) {
            if (SessionBrowser_Filter_DateRange_From.getValue().isAfter(SessionBrowser_Filter_DateRange_To.getValue())) {
                new InformationDialog(preferences, "Invalid Date", "To Date Must Be After From Date", null, true);
                SessionBrowser_Filter_DateRange_To.setValue(null);
            }
        }
    }
    public void checkfilterduration_min() {

    }
    public void checkfilterduration_max() {

    }


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
            PlaybackItem playbackItem = demosession.getplaybackitem(i);
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
            PlaybackItem playbackItem = dummysession.getplaybackitem(index);
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