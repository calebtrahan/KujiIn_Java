package kujiin.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
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
import kujiin.ui.dialogs.SelectDate;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.ui.dialogs.alerts.ErrorDialog;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.ui.goals.SetNewGoalDialog;
import kujiin.ui.playback.Player;
import kujiin.ui.progress.EditSessionNotes;
import kujiin.ui.progress.ImportSessionFile;
import kujiin.ui.table.*;
import kujiin.util.Util;
import kujiin.util.enums.IconDisplayType;
import kujiin.util.enums.ProgramState;
import kujiin.util.enums.QuickAddAmbienceType;
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

public class MainController implements Initializable {
    private boolean testingmode;
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
    public MenuItem ImportSessionsMenuItem;
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
    public MenuItem Ambience_QuickAdd;
    public MenuItem AmbienceCustomize;
    public Button MoveUpButton;
    public Button MoveDownButton;
    public Button RemoveButton;
    public Label SessionSummary_CompletionTime_Label;
    public TextField SessionSummary_CompletionTime;
    public Label SessionSummary_Duration_Label;
    public TextField SessionSummary_Duration;
    public Button ClearButton;
    public MenuButton AddSessionAsMenuButton;
    public MenuItem FavoriteMenuItem;
    public MenuItem MissedSessionMenuItem;
    public Button PlayButton;
    public Button SaveAsFileButton;
    public Button ExportButton;
    public Label CreatorStatusBar;
    // Progress Tab
    public Tab ProgressTab;
        // Overview Tab
    public Tab ProgressOverviewTab;
    public ChoiceBox<String> ProgressOverviewTypeChoiceBox;
    public BarChart<String, Number> ProgressOverviewBarChart;
    public NumberAxis ProgressOverviewNumbersAxis;
    public CategoryAxis ProgressOverviewCategoryAxis;
    public Label ProgressOverviewTotalTimePracticedLabel;
    public TextField ProgressOverviewTotalTimePracticed;
    public AnchorPane ProgressBalanceAnchorPane;
        // Balance Tab
    public Tab ProgressBalanceTab;
    public ChoiceBox<String> ProgressBalanceTypeChoiceBox;
    public PieChart ProgressBalancePieChart;
        // Session Browser Tab
    public TableView<SessionBrowserTableItem> PracticedSessionListTable;
    public TableColumn<SessionBrowserTableItem, String> PracticedSessionListTable_DateColumn;
    public TableColumn<SessionBrowserTableItem, String> PracticedSessionListTable_ItemsColumn;
    public TableColumn<SessionBrowserTableItem, String> PracticedSessionListTable_DurationColumn;
    public Accordion SessionBrowser_Filter;
    public CheckBox SessionBrowser_Filter_ShowMissedSessions;
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
    public Button SessionBrowser_DeleteSessionButton;
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
    private ObservableList<TableItem_Number_Name_Duration_Ambience> createdtableitems = FXCollections.observableArrayList();
    private ArrayList<PlaybackItem> createdtableplaybackitems;
    private Timeline updatecompletiontime;
    private Duration addduration = Duration.seconds(1.5);
    // Progress Tab
    private Session selectedsessionbrowsersession;

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
    public boolean isTestingmode() {return testingmode;}

// Window Methods
    private boolean cleanup() {
        availableAmbiences.marshall();
        AvailableEntrainments.marshall();
        RampFiles.marshall();
        preferences.marshall();
        return true;
    }
    public void close() {
        Platform.runLater(() -> {
            if (cleanup()) {System.exit(0);}
        });
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
                populateprogressoverview(ProgressOverviewTypeChoiceBox.getValue());
                populateprogressbalance(ProgressBalanceTypeChoiceBox.getValue());
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
        updatecompletiontime = new Timeline(new KeyFrame(Duration.seconds(1.0), ae -> calculatedurationandestimatedcompletion()));
    }
    private void setupTables() {
    // Creation Table
        CreatedTableNumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
        CreatedTableItemColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        CreatedTableDurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
        CreatedTableAmbienceColumn.setCellValueFactory(cellData -> cellData.getValue().ambience);
        CreatedTableView.setOnMousePressed(event -> {
            ObservableList<Integer> indexes = CreatedTableView.getSelectionModel().getSelectedIndices();
            if (! indexes.isEmpty()) {
                tableselectionchanged();
                if (event.isSecondaryButtonDown()) {
                    boolean singleitemselected = indexes.size() == 1;

                    ContextMenu contextMenu = new ContextMenu();

                    MenuItem moveup = new MenuItem("Move Up");
                    moveup.setOnAction(event1 -> moveupincreatortable());
                    MenuItem movedown = new MenuItem("Move Down");
                    movedown.setOnAction(event1 -> movedownincreatortable());

                    Menu durationmenu = new Menu("Duration");
                    MenuItem setduration = new MenuItem("Adjust Duration");
                    setduration.setOnAction(event1 -> editdurations());
                    MenuItem ramponly = new MenuItem("Set Ramp Only");
                    ramponly.setOnAction(event1 -> setramponly());
                    durationmenu.getItems().add(setduration);
                    durationmenu.getItems().add(ramponly);

                    Menu ambiencemenu = new Menu("Ambience");
                    MenuItem quickaddambience = new MenuItem("Quick Add");
                    quickaddambience.setOnAction(event1 -> quickaddambience_shuffle());
                    MenuItem customizeambience = new MenuItem("Customize Ambience");
                    customizeambience.setOnAction(event1 -> customizeambience());
                    ambiencemenu.getItems().add(quickaddambience);
                    MenuItem clearambience = new MenuItem("Clear Ambience");
                    clearambience.setOnAction(event1 -> clearambience());

                    if (singleitemselected) { ambiencemenu.getItems().add(customizeambience); }
                    boolean hasambience = false;
                    boolean missingambience = false;
                    for (PlaybackItem i : createdtableplaybackitems) {
                        if (i.getAmbience().hasPresetAmbience()) {hasambience = true;}
                        if (! availableAmbiences.getsessionpartAmbience(i.getCreationindex()).hasAny()) {missingambience = true;}
                    }
                    if (hasambience) {ambiencemenu.getItems().add(clearambience);}

                    MenuItem remove = new MenuItem("Remove");
                    remove.setOnAction(event1 -> removefromcreatortable());

                    if (singleitemselected) {
                        if (indexes.size() == 1 && indexes.get(0) > 0) {contextMenu.getItems().add(moveup);}
                        if (indexes.size() ==1 && indexes.get(0) < CreatedTableView.getItems().size() - 1) {contextMenu.getItems().add(movedown);}
                    }
                    contextMenu.getItems().add(durationmenu);


                    if (! missingambience) {contextMenu.getItems().add(ambiencemenu);}
                    contextMenu.getItems().add(remove);

                    MenuItem clearsession = new MenuItem("Clear Session");
                    clearsession.setOnAction(event1 -> clearloadedsession());
                    contextMenu.getItems().add(clearsession);
                    CreatedTableView.setContextMenu(contextMenu);
                }
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    TablePosition pos = CreatedTableView.getSelectionModel().getSelectedCells().get(0);
                    int column = pos.getColumn();
                    switch (column) {
                        case 2:
                            editdurations();
                            break;
                        case 3:
                            customizeambience();
                            break;
                    }
                }
            }
        });
    // Session Overview Chart
        ProgressTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (ProgressOverviewTypeChoiceBox.getSelectionModel().getSelectedIndex() == -1) {ProgressOverviewTypeChoiceBox.getSelectionModel().select(1);}
            if (ProgressBalanceTypeChoiceBox.getSelectionModel().getSelectedIndex() == -1) {ProgressBalanceTypeChoiceBox.getSelectionModel().select(1);}
        });
        ProgressOverviewTab.selectedProperty().addListener((observable, oldValue, newValue) -> {

            populateprogressoverview(ProgressOverviewTypeChoiceBox.getValue());
        });
        final ObservableList<String> items = FXCollections.observableArrayList("All", "Kuji-In", "Elements");
        ProgressOverviewTypeChoiceBox.setItems(items);
        ProgressBalanceTypeChoiceBox.getSelectionModel().select(1);
        ProgressOverviewTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            populateprogressoverview(newValue);
            ProgressBalanceTypeChoiceBox.getSelectionModel().select(ProgressOverviewTypeChoiceBox.getSelectionModel().getSelectedIndex());
        });
        ProgressBalanceTab.selectedProperty().addListener(observable -> populateprogressbalance(ProgressBalanceTypeChoiceBox.getValue()));
        ProgressBalanceTypeChoiceBox.setItems(items);
        ProgressBalanceTypeChoiceBox.getSelectionModel().select(1);
        ProgressBalanceTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            populateprogressbalance(newValue);
            ProgressOverviewTypeChoiceBox.getSelectionModel().select(ProgressBalanceTypeChoiceBox.getSelectionModel().getSelectedIndex());
        });
    // Session Browser List Table
        PracticedSessionListTable.setRowFactory(tv -> new TableRow<SessionBrowserTableItem>() {
            private Tooltip tooltip = new Tooltip();

            @Override
            protected void updateItem(SessionBrowserTableItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    Session practicedsession = sessions.get(item.uuid);;
                    StringBuilder text = new StringBuilder();
                    if (practicedsession.getDate_Practiced() != null) {text.append("Date Practiced: ").append(practicedsession.getDate_Practiced().format(Util.dateFormat)).append("\n");}
                    text.append("Practice Time: ").append(Util.formatdurationtoStringDecimalWithColons(practicedsession.getSessionPracticedTime())).append("\n");
                    text.append("Playback Items: ").append("\n");
                    int count = 1;
                    for (PlaybackItem i : practicedsession.getPlaybackItems()) {
                        if (i.getPracticeTime() > 0.0) {
                            text.append(" ").append(count).append(". ").append(i.getName()).append(" (");
                            text.append(Util.formatdurationtoStringDecimalWithColons(Duration.millis(i.getPracticeTime()))).append(")");
                            text.append("\n");
                            count++;
                        }
                    }
                    tooltip.setText(text.toString());
                    setTooltip(tooltip);
                } else { setTooltip(null); }
            }
        });
        PracticedSessionListTable_DateColumn.setCellValueFactory(cellData -> cellData.getValue().date);
        PracticedSessionListTable_ItemsColumn.setCellValueFactory(cellData -> cellData.getValue().items);
        PracticedSessionListTable_DurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
    // Session Browser Details Table
        SessionBrowser_DetailsTable_NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asString());
        SessionBrowser_DetailsTable_ItemColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        SessionBrowser_DetailsTable_TimePracticedColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
    // Goal Overview Table
        GoalsOverview_Table.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                int column = GoalsOverview_Table.getSelectionModel().getSelectedCells().get(0).getColumn();
                switch (column) {
                    case 0:
                    case 1:
                    case 2:
                        // Set/Edit Goals
                        break;
                    case 3:
                        // Percentage Breakdown Of Practiced And Detail With ProgressIndicator
                        break;
                    case 4:
                        // ListView Of All Goals Completed
                        break;
                }
                // Set New Goal On Current Goal Column
                // Goals Completed Detail On Goals Completed Column
                // Handlers
            }
            if (event.isSecondaryButtonDown()) {
                ContextMenu contextMenu = new ContextMenu();

                MenuItem setnewgoal = new MenuItem("Set/Edit Goals");

                MenuItem viewpracticeddetails = new MenuItem("View Practiced Details");

                MenuItem viewcompletedgoals = new MenuItem("View Completed Goals");


                // Context Menu
            }
        });
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
        CreatedTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    // Goal Details Table
        GoalsIndividual_Table.setRowFactory(tv -> new TableRow<GoalDetailsTableItem>() {
            @Override
            protected void updateItem(GoalDetailsTableItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    if (item.isCompleted()) { setTextFill(Color.GREEN); }
                    else { setTextFill(null); }
                } else {
                    setText(null);
                    setTextFill(null);
                }
            }
        });
    }
    private void setupIcons() {
        IconDisplayType dt = IconDisplayType.ICONS_ONLY;
        if (dt == IconDisplayType.ICONS_AND_TEXT || dt == IconDisplayType.ICONS_ONLY) {
            AddItemsMenu.setGraphic(new IconImageView(ICON_ADD, 20.0));
            CreateNewSessionButton.setGraphic(new IconImageView(new Image(new File(DIRECTORYICONS, "NewDocument.png").toURI().toString()), 20.0));
            OpenFileButton.setGraphic(new IconImageView(ICON_OPENFILE, 20.0));
            OpenRecentSessionsButton.setGraphic(new IconImageView(ICON_RECENTSESSIONS, 20.0));
            ClearButton.setGraphic(new IconImageView(ICON_CLEARSESSION, 20.0));
            OpenFavoritesButton.setGraphic(new IconImageView(ICON_FAVORITES, 20.0));
            DurationMenu.setGraphic(new IconImageView(ICON_EDITDURATION, 20.0));
            AmbienceMenu.setGraphic(new IconImageView(ICON_AMBIENCE, 20.0));
            MoveUpButton.setGraphic(new IconImageView(ICON_MOVEUP, 20.0));
            MoveDownButton.setGraphic(new IconImageView(ICON_MOVEDOWN, 20.0));
            RemoveButton.setGraphic(new IconImageView(ICON_REMOVE, 20.0));
            AddSessionAsMenuButton.setGraphic(new IconImageView(new Image(new File(DIRECTORYICONS, "AddAs.png").toURI().toString()), 20.0));
            FavoriteMenuItem.setGraphic(new IconImageView(ICON_ADDTOFAVORITE, 20.0));
            MissedSessionMenuItem.setGraphic(new IconImageView(new Image(new File(DIRECTORYICONS, "MissedSession.png").toURI().toString()), 20.0));
            PlayButton.setGraphic(new IconImageView(ICON_PLAY, 20.0));
            SaveAsFileButton.setGraphic(new IconImageView(ICON_EXPORTTODOCUMENT, 20.0));
            ExportButton.setGraphic(new IconImageView(ICON_EXPORTTOAUDIO, 20.0));
        }
        if (dt == IconDisplayType.ICONS_ONLY) {
            OpenFileButton.setText("");
            OpenRecentSessionsButton.setText("");
            OpenFavoritesButton.setText("");
            AmbienceMenu.setText("");
            AddItemsMenu.setText("");
            DurationMenu.setText("");
            MoveUpButton.setText("");
            MoveDownButton.setText("");
            RemoveButton.setText("");
            ClearButton.setText("");
            AddSessionAsMenuButton.setText("");
            PlayButton.setText("");
            SaveAsFileButton.setText("");
            ExportButton.setText("");
        } else {
//            AmbienceMenu.setText("Ambience");
//            AddItemsMenu.setText("Add");
            OpenFileButton.setText("Open Session");
            OpenRecentSessionsButton.setText("Recent");
            OpenFavoritesButton.setText("Favorites");
//            DurationMenu.setText("Duration");
//            MoveUpButton.setText("Up");
//            MoveDownButton.setText("Down");
//            RemoveButton.setText("Remove");
            ClearButton.setText("Clear Session");
            AddSessionAsMenuButton.setText("Add Session As");
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
            AddSessionAsMenuButton.setGraphic(null);
            FavoriteMenuItem.setGraphic(null);
            MissedSessionMenuItem.setGraphic(null);
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
        AddSessionAsMenuButton.setTooltip(new Tooltip("Add Session As..."));
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
    public void closeprogram() {}
    public void importsessions() {
        ImportSessionFile importSessionFile = new ImportSessionFile(sessions, preferences);
        importSessionFile.initModality(Modality.APPLICATION_MODAL);
        importSessionFile.showAndWait();
        sessions = importSessionFile.getSessions();
    }
    public void editavailableambience() {
        AvailableAmbienceEditor availableAmbienceEditor = new AvailableAmbienceEditor(preferences, availableAmbiences);
//        availableAmbienceEditor.initModality(Modality.APPLICATION_MODAL);
        availableAmbienceEditor.showAndWait();
    }
    public void editreferencefiles() {
       EditReferenceFiles editReferenceFiles = new EditReferenceFiles(this);
       editReferenceFiles.initOwner(getStage());
       editReferenceFiles.initModality(Modality.APPLICATION_MODAL);
       editReferenceFiles.showAndWait();
    }
    public void displaytutorial() {}
    public void aboutthisprogram() {
    }

// Play/Export Tab
    // Create/Open Toolbar Methods
    public void createnewsession() {
        if (createdsession != null && sessions.hasSessions() && getAvailableAmbiences().completelyempty()) {
            if (new ConfirmationDialog(getPreferences(), "Add Available Ambience", "There Is No Available Ambience For Any Playback Items", "Open Ambience Editor To Add Ambience?", true).getResult()) {
                AmbienceEditor_Simple ambienceEditor_simple = new AmbienceEditor_Simple(availableAmbiences, getPreferences());
                ambienceEditor_simple.initModality(Modality.APPLICATION_MODAL);
                ambienceEditor_simple.showAndWait();
            }
        }
        if ((createdsession != null && ! createdsession.hasItems()) && ! new ConfirmationDialog(preferences, "Overwrite Session", "Really Load New Session?", "This will clear any unsaved changes you made to this session", true).getResult()) {return;}
        createdsession = new Session(availableAmbiences);
        populatecreatedsessiontable();
        displayStatusBarMessage("New Session Created", Duration.seconds(1.0));
        AddItemsMenu.requestFocus();
    }
    public void opensessionfromfile() {
        updatecompletiontime.stop();
        File filetoload;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select A Session To Open");
        filetoload = fileChooser.showOpenDialog(getStage());
        if (filetoload != null) {
            try {
                JAXBContext context = JAXBContext.newInstance(Session.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                createdsession = (Session) createMarshaller.unmarshal(filetoload);
                populatecreatedsessiontable();
                displayStatusBarMessage("Opened Session From File: " + filetoload.getName(), Duration.seconds(2));
                PlayButton.requestFocus();
            } catch (JAXBException e) {
                new ErrorDialog(preferences, "Invalid File", "'" + filetoload.getName() + "' Isn't A Valid Session File", "Select A Valid Session To Load", true);
            }
        }
        updatecompletiontime.play();
    }
    public void openrecentsession() {
        updatecompletiontime.stop();
        if (! sessions.hasSessions()) {
            new InformationDialog(preferences, "No Recent Sessions", "No Recent Sessions", "No Sessions Practiced");
            updatecompletiontime.play();
            return;
        }
        boolean overwritesession = true;
        if (createdsession != null && createdsession.hasItems()) {
            overwritesession = new ConfirmationDialog(preferences, "Overwrite Session", "Really Open Recent Session?", "This will clear any unsaved changes you made to this session").getResult();
        }
        SelectASession selectASession = new SelectASession(preferences, sessions);
        selectASession.initModality(Modality.APPLICATION_MODAL);
        selectASession.showAndWait();
        if (! overwritesession) {return;}
        else {createdsession = null;}
        if (selectASession.isAccepted()) {
            createdsession = selectASession.getSelectedsession();
            populatecreatedsessiontable();
            displayStatusBarMessage("Recent Session Successfully Opened", Duration.seconds(1.0));
            PlayButton.requestFocus();
        }
        updatecompletiontime.play();
    }
    public void openfavoritesession() {
        updatecompletiontime.stop();
        if (favoriteSessions.getFavoriteSessions() == null || favoriteSessions.getFavoriteSessions().isEmpty()) {
            new InformationDialog(preferences, "No Favorite Sessions", "No Sessions Marked As Favorite", "Mark At Least One Session As Favorite To Use This Feature");
            return;
        }
        boolean overwritesession = true;
        if (createdsession != null && createdsession.hasItems()) {
            overwritesession = new ConfirmationDialog(preferences, "Overwrite Session", "Really Open Favorite Session?", "This will clear any unsaved changes you made to this session").getResult();
        }
        SelectASession selectASession = new SelectASession(preferences, favoriteSessions);
        selectASession.initModality(Modality.APPLICATION_MODAL);
        selectASession.showAndWait();
        if (! overwritesession) {return;}
        else {createdsession = null;}
        if (selectASession.isAccepted()) {
            createdsession = selectASession.getSelectedsession();
            populatecreatedsessiontable();
            displayStatusBarMessage("Favorite Session \"" + selectASession.getFavoriteSessionName() + "\" Loaded", Duration.seconds(2));
            PlayButton.requestFocus();
        }
        updatecompletiontime.play();
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
        ObservableList<Integer> indexes = CreatedTableView.getSelectionModel().getSelectedIndices();
        if (! indexes.isEmpty()) { syncbuttons(); }
    }
    // Creation Table Methods
    private List<PlaybackItem> quickaddambience(QuickAddAmbienceType quickAddAmbienceType, List<PlaybackItem> items) {
        int missingambiencecount = 0;
        for (PlaybackItem i : items) {
            if (availableAmbiences.getsessionpartAmbience(i.getCreationindex()).hasAny()) {
                boolean clearambience = true;
                if (i.getAmbience().hasPresetAmbience()) {
                    clearambience = new ConfirmationDialog(preferences, "Confirmation", "Ambience Already Exists", "Clear Ambience Before Quick Add?").getResult();
                    if (clearambience) { i.getAmbience().clearPresetambience();}
                }
                if (quickAddAmbienceType == QuickAddAmbienceType.REPEAT) {i.getAmbience().quickadd_repeat(i, clearambience);}
                else if (quickAddAmbienceType == QuickAddAmbienceType.SHUFFLE) {i.getAmbience().quickadd_shuffle(i, clearambience);}
            } else {missingambiencecount++;}
        }
        if (missingambiencecount > 0) {
//            new InformationDialog(preferences, "Missing Ambience", "Missing Ambience For " + missingambiencecount + " Playback Items",
//                    "Added " + quickAmbienceType.toString() + " Ambience For " + (playbackItemList.size() - missingambiencecount) + " Playback Items");
        }
        System.out.println("Made It Out Of Quick Add Ambience Method");
        return items;
    }
    private boolean add(int[] availableambienceindexes) {
        updatecompletiontime.stop();
        boolean hasCuts = false;
        ArrayList<PlaybackItem> playbackItems = createdsession.getPlaybackItems();
        int currentitemsize = playbackItems.size();
        for (PlaybackItem i : createdsession.getPlaybackItems()) {
            if (i.getPlaybackItemType() == PlaybackItem.PlaybackItemType.CUT) {hasCuts = true; break;}
        }
        if ((currentitemsize == 0 || hasCuts) && availableambienceindexes.length == 1) {
            int n = availableambienceindexes[0];
            if (n > 1 && n < 10) {

            }
        }
        List<PlaybackItem> items = new ArrayList<>();
        for (int i : availableambienceindexes) {
            PlaybackItem playbackItem = createdsession.getplaybackitemwithambience(i);
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
                items = quickaddambience(QuickAddAmbienceType.SHUFFLE, items);
            }
            int startindex;
            if (CreatedTableView.getSelectionModel().getSelectedIndex() != -1) {
                startindex = CreatedTableView.getSelectionModel().getSelectedIndex();
                createdsession.addplaybackitems(startindex, items); createdsession.calculateexpectedduration();
            } else {
                startindex = createdsession.getPlaybackItems().size() - 1;
                createdsession.addplaybackitems(null, items); createdsession.calculateexpectedduration();
            }
            if (CreatedTableView.getSelectionModel().getSelectedIndex() != -1 && createdsession.getplaybackitemwithambience(startindex).getCreationindex() == items.get(0).getCreationindex()) {
                mergeitems(createdsession.getplaybackitemwithambience(startindex), createdsession.getplaybackitemwithambience(startindex + 1));
                populatecreatedsessiontable();
                return true;
            }
            populatecreatedsessiontable();
            return true;
        } else {
            populatecreatedsessiontable();
            return false;
        }
    }
    public void addallitems_kujiin() {
        int[] indexes = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        if (add(indexes)) {
            displayStatusBarMessage("Added 9 Playback Items", addduration);
        }
    }
    public void addallitems_elements() {
        int[] indexes = {10, 11, 12, 13, 14};
        if (add(indexes)) {
            displayStatusBarMessage("Added 5 Playback Items", addduration);
        }
    }
    public void add_QiGong() {
        int[] items = {0};
        if (add(items)) {
            displayStatusBarMessage("Added Qi-Gong To Session", addduration);
        }
    }
    public void addRin() {
        int[] items = {1};
        if (add(items)) {
            displayStatusBarMessage("Added Rin To Session", addduration);
        }
    }
    public void addKyo() {
        int[] items = {2};
        if (add(items)) {
            displayStatusBarMessage("Added Kyo To Session", addduration);
        }
    }
    public void addToh() {
        int[] items = {3};
        if (add(items)) {
            displayStatusBarMessage("Added Toh To Session", addduration);
        }
    }
    public void addSha() {
        int[] items = {4};
        if (add(items)) {
            displayStatusBarMessage("Added Sha To Session", addduration);
        }
    }
    public void addKai() {
        int[] items = {5};
        if (add(items)) {
            displayStatusBarMessage("Added Kai To Session", addduration);
        }
    }
    public void addJin() {
        int[] items = {6};
        if (add(items)) {
            displayStatusBarMessage("Added Jin To Session", addduration);
        }
    }
    public void addRetsu() {
        int[] items = {7};
        if (add(items)) {
            displayStatusBarMessage("Added Retsu To Session", addduration);
        }
    }
    public void addZai() {
        int[] items = {8};
        if (add(items)) {
            displayStatusBarMessage("Added Zai To Session", addduration);
        }
    }
    public void addZen() {
        int[] items = {9};
        if (add(items)) {
            displayStatusBarMessage("Added Zen To Session", addduration);
        }
    }
    public void addEarth() {
        int[] items = {10};
        if (add(items)) {
            displayStatusBarMessage("Added Earth To Session", addduration);
        }
    }
    public void addAir() {
        int[] items = {11};
        if (add(items)) {
            displayStatusBarMessage("Added Air To Session", addduration);
        }
    }
    public void addFire() {
        int[] items = {12};
        if (add(items)) {
            displayStatusBarMessage("Added Fire To Session", addduration);
        }
    }
    public void addWater() {
        int[] items = {13};
        if (add(items)) {
            displayStatusBarMessage("Added Water To Session", addduration);
        }
    }
    public void addVoid() {
        int[] items = {14};
        if (add(items)) {
            displayStatusBarMessage("Added Void To Session", addduration);
        }
    }
    public void setramponly() {
        ObservableList<Integer> indexes = CreatedTableView.getSelectionModel().getSelectedIndices();
        if (! indexes.isEmpty()) {
            for (int i : indexes) {
                PlaybackItem playbackItem = createdtableplaybackitems.get(i);
                playbackItem.setRampOnly(true);
                playbackItem.setExpectedDuration(0.0);
                createdtableplaybackitems.set(i, playbackItem);
            }
            populatecreatedsessiontable();
            syncbuttons();
        }
    }
    public void editdurations() {
        ObservableList<Integer> indexes = CreatedTableView.getSelectionModel().getSelectedIndices();
        if (! indexes.isEmpty()) {
            List<PlaybackItem> itemstochange = new ArrayList<>();
            boolean hasAmbience = false;
            for (Integer i : indexes) {
                PlaybackItem x = createdtableplaybackitems.get(i);
                itemstochange.add(x);
                if (x.getAmbience().hasPresetAmbience()) {hasAmbience = true;}
            }
            if (hasAmbience) {
                if (! new ConfirmationDialog(preferences, "Confirmation", "Setting Durations For Playback Item(s) With Set Ambience", "This Will Clear Ambience. Is That OK?").getResult()) {
                    return;
                } else { for (PlaybackItem i :itemstochange) { i.getAmbience().clearPresetambience(); } }
            }
            SetDurationWithAmbienceOption adjustDuration = new SetDurationWithAmbienceOption(preferences, availableAmbiences, itemstochange, false);
            adjustDuration.initOwner(getStage());
            adjustDuration.showAndWait();
            if (adjustDuration.isAccepted()) {
                List<PlaybackItem> changeditems = createdsession.getPlaybackItems();
                for (int i : indexes) {
                    PlaybackItem x = changeditems.get(i);
                    x.setExpectedDuration(adjustDuration.getDuration());
                    changeditems.set(i, x);
                    }
                createdsession.setPlaybackItems(new ArrayList<>(changeditems));
                createdsession.calculateexpectedduration();
                populatecreatedsessiontable();
                displayStatusBarMessage("Duration Edited", Duration.seconds(1.0));
                syncbuttons();
            }
        } else {new InformationDialog(preferences, "Cannot Edit Duration", "Select A Single Table Item To Edit Duration", null, true);}
    }
    public void quickaddambience_repeat() {
        ObservableList<Integer> indexes = CreatedTableView.getSelectionModel().getSelectedIndices();
        if (! indexes.isEmpty()) {
            List<PlaybackItem> itemstoaddambienceto = new ArrayList<>();
            for (Integer i : indexes) {
                itemstoaddambienceto.add(createdtableplaybackitems.get(i));
            }
            int availableambiencecount = 0;
            for (PlaybackItem i : itemstoaddambienceto) {
                if (availableAmbiences.getsessionpartAmbience(i.getCreationindex()).hasAny()) {availableambiencecount++;}
            }
            if (availableambiencecount != itemstoaddambienceto.size()) {
                new InformationDialog(preferences, "Information", "Cannot Quick Add Ambience", itemstoaddambienceto.size() - availableambiencecount + " Items Are Missing Ambience");
                return;
            }
            quickaddambience(QuickAddAmbienceType.REPEAT, itemstoaddambienceto);
            populatecreatedsessiontable();
        }
    }
    public void quickaddambience_shuffle() {
        ObservableList<Integer> indexes = CreatedTableView.getSelectionModel().getSelectedIndices();
        if (! indexes.isEmpty()) {
            List<PlaybackItem> itemstoaddambienceto = new ArrayList<>();
            for (Integer i : indexes) {
                itemstoaddambienceto.add(createdtableplaybackitems.get(i));
            }
            int availableambiencecount = 0;
            for (PlaybackItem i : itemstoaddambienceto) {
                if (availableAmbiences.getsessionpartAmbience(i.getCreationindex()).hasAny()) {availableambiencecount++;}
            }
            if (availableambiencecount != itemstoaddambienceto.size()) {
                new InformationDialog(preferences, "Information", "Cannot Quick Add Ambience", itemstoaddambienceto.size() - availableambiencecount + " Items Are Missing Ambience");
                return;
            }
            quickaddambience(QuickAddAmbienceType.SHUFFLE, itemstoaddambienceto);
            populatecreatedsessiontable();
        }
    }
    public void customizeambience() {
        ObservableList<Integer> indexes = CreatedTableView.getSelectionModel().getSelectedIndices();
        if (! indexes.isEmpty() && indexes.size() == 1) {
            PlaybackItem x = createdtableplaybackitems.get(indexes.get(0));
            CustomizeAmbience customizeAmbience = new CustomizeAmbience(preferences, x);
            customizeAmbience.initModality(Modality.APPLICATION_MODAL);
            customizeAmbience.showAndWait();
            if (customizeAmbience.isAccepted()) {
                ArrayList<PlaybackItem> items = createdtableplaybackitems;
                items.set(createdtableplaybackitems.indexOf(x), customizeAmbience.getPlaybackItem());
                createdsession.setPlaybackItems(items);
                populatecreatedsessiontable();
            }
        }
    }
    private void clearambience() {
        ObservableList<Integer> indexes = CreatedTableView.getSelectionModel().getSelectedIndices();
        if (! indexes.isEmpty()) {
            for (int i : indexes) {
                PlaybackItem x = createdtableplaybackitems.get(i);
                x.getAmbience().clearPresetambience();
                createdtableplaybackitems.set(indexes.get(0), x);
            }
            createdsession.setPlaybackItems(createdtableplaybackitems);
            populatecreatedsessiontable();
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
            populatecreatedsessiontable();
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
            populatecreatedsessiontable();
        }

    }
    public void removefromcreatortable() {
        ObservableList<Integer> indexes = CreatedTableView.getSelectionModel().getSelectedIndices();
        if (! indexes.isEmpty()) {
            if (indexes.size() > 1) {
                if (! new ConfirmationDialog(preferences, "Confirmation", "Really Remove " + indexes.size() + " Items?", null).getResult()) { return; }
            }
            List<PlaybackItem> items = createdsession.getPlaybackItems();
            List<PlaybackItem> itemstoremove = new ArrayList<>();
            for (int i : indexes) {
                itemstoremove.add(items.get(i));
            }
            for (PlaybackItem i :itemstoremove) { items.remove(i); }
            createdsession.setPlaybackItems(new ArrayList<>(items));
            populatecreatedsessiontable();
        }
    }
    private void mergeitems(PlaybackItem item1, PlaybackItem item2) {
        if (new ConfirmationDialog(preferences, "Confirmation", "Duplicate Playback Item  + '" + item1.getName() + "' Detected", "Merge? (This Will Clear Ambience For Merged Items)").getResult()) {
            ArrayList<PlaybackItem> itemsinsession = createdsession.getPlaybackItems();
            int item1index = itemsinsession.indexOf(item1);
            itemsinsession.remove(item2);
            boolean ambiencewasenabled = item1.getAmbience().hasPresetAmbience() || item2.getAmbience().hasPresetAmbience();
            Duration newduration = new Duration(item1.getExpectedDuration());
            newduration = newduration.add(new Duration(item2.getExpectedDuration()));
            item1.setExpectedDuration(newduration.toMillis());
            item1.getAmbience().setSessionAmbience(new ArrayList<>());
            if (ambiencewasenabled) {
                Duration ambienceduration = Duration.ZERO;
                if (item1.getAmbience().hasPresetAmbience()) {ambienceduration = ambienceduration.add(item1.getAmbience().getPresetAmbienceDuration());}
                if (item2.getAmbience().hasPresetAmbience()) {ambienceduration = ambienceduration.add(item2.getAmbience().getPresetAmbienceDuration());}
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
                                if (new ConfirmationDialog(preferences, "Quick Add Ambience", "Quick Add Ambience?", null).getResult()) {
                                    item1 = quickaddambience(QuickAddAmbienceType.SHUFFLE, Collections.singletonList(item1)).get(0);
                                }
                                break;
                            case "Customize Ambience":
                                CustomizeAmbience customizeAmbience = new CustomizeAmbience(preferences, item1);
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
        updatecompletiontime.stop();
        if (createdsession != null && new ConfirmationDialog(preferences, "Clear Session", "Really Clear Session?", "This will clear any unsaved changes you made to this session", true).getResult()) {
            createdsession = new Session(availableAmbiences);
            populatecreatedsessiontable();
            displayStatusBarMessage("Session Cleared", Duration.seconds(1.0));
        } else { updatecompletiontime.play(); }
    }
    public void addasmissedsession() {
        updatecompletiontime.stop();
        if (createdsession.hasItems() && new ConfirmationDialog(preferences, "Confirmation", "Really Add As Missed Session? ", "This Will Mark The Session As Practiced And Add To Progress").getResult()) {
            SelectDate selectDate = new SelectDate("Select Missed Session Date");
            selectDate.showAndWait();
            if (selectDate.isAccepted()) {
                Session missedsession = createdsession;
                missedsession.setDate_Practiced(selectDate.getDate());
                missedsession.setasMissedSession();
                LocalDate sessiondate = selectDate.getDate();
                int index = 0;
                for (int i = 0; i < sessions.getSession().size(); i++) {
                    Session x = sessions.getSession().get(i);
                    if (x.getDate_Practiced().isEqual(sessiondate) || x.getDate_Practiced().isAfter(sessiondate)) {
                        index = i;
                        break;
                    }
                }
                missedsession.newID();
                sessions.add(index, missedsession);
                sessions.marshall();
            }
        }
        updatecompletiontime.play();
    }
    public void addcreatedsessiontofavorites() {
        if (createdsession != null) {
            updatecompletiontime.stop();
            NameFavoriteSession nameFavoriteSession = new NameFavoriteSession(favoriteSessions);
            nameFavoriteSession.initModality(Modality.APPLICATION_MODAL);
            nameFavoriteSession.showAndWait();
            if (nameFavoriteSession.isAccepted()) {
                favoriteSessions.add(nameFavoriteSession.getName(), createdsession);
                new InformationDialog(preferences, "Favorite Session Added", "Session Added To Favorites", null, true);
            }
            updatecompletiontime.play();
        }
    }
    public void savecreatedsessionasfile() {
        if (createdsession != null && ! createdsession.hasItems()) {
            updatecompletiontime.stop();
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
            updatecompletiontime.play();
        }
    }
    public void playcreatedsession() {
        if (createdsession != null) {
            updatecompletiontime.stop();
            boolean updatesession = false;
            for (PlaybackItem i : createdsession.getPlaybackItems()) {
                if (i.isRampOnly()) {
                    SoundFile rampfile = RampFiles.getRampFile(i, createdsession.getPlaybackItems().get(createdsession.getPlaybackItems().indexOf(i) + 1));
                    i.setExpectedDuration(rampfile.getDuration());
                    updatesession = true;
                }
            }
            if (updatesession) {createdsession.calculateexpectedduration();}
            testingmode = new ConfirmationDialog(preferences, "Confirmation", "Is This A Test Session?", null, "Yes", "No").getResult();
            getStage().setIconified(true);
            Player player = new Player(this, sessions, allGoals, createdsession);
            player.initModality(Modality.APPLICATION_MODAL);
            player.showAndWait();
            if (player.isExitprogram()) { close(); }
            else {
                getStage().setIconified(false);
                updatecompletiontime.play();
            }
        }
    }
    public void exportcreatedsession() {
        updatecompletiontime.stop();
        new InformationDialog(preferences, "Under Construction", "This Feature Coming Soon!", "");
        updatecompletiontime.play();
    }
    private void displayStatusBarMessage(String message, Duration duration) {
        CreatorStatusBar.setText(message);
        new Timeline(new KeyFrame(duration, ae -> CreatorStatusBar.setText(""))).play();
    }
    // Utility Methods
    private void populatecreatedsessiontable() {
        updatecompletiontime.stop();
        calculatedurationandestimatedcompletion();
        if (createdtableplaybackitems != null) {createdtableplaybackitems.clear();}
        else {createdtableplaybackitems = new ArrayList<>();}
        if (! createdtableitems.isEmpty()) {createdtableitems.clear();}
        if (createdsession.hasItems()) {
            System.out.println("Session Has Items");
            createdtableplaybackitems.addAll(createdsession.getPlaybackItems());
            int number = 1;
            for (PlaybackItem i : createdtableplaybackitems) {
                createdtableitems.add(new TableItem_Number_Name_Duration_Ambience(number, i.getName(), i.getdurationasString(), i.getAmbienceasString()));
                number++;
            }
            CreatedTableView.setItems(createdtableitems);
        }
        syncbuttons();
        updatecompletiontime.play();
    }
    private void syncbuttons() {
        boolean nosessionloaded = createdsession == null;
        boolean tableempty = CreatedTableView.getItems().isEmpty();
        ObservableList<Integer> indexes = CreatedTableView.getSelectionModel().getSelectedIndices();
        boolean singleitemselected = indexes.size() == 1;
        AddItemsMenu.setDisable(nosessionloaded);
        DurationMenu.setDisable(nosessionloaded || tableempty || indexes.isEmpty());
        AmbienceMenu.setDisable(nosessionloaded || tableempty || indexes.isEmpty());
        AmbienceCustomize.setVisible(singleitemselected);
        PlayButton.setDisable(nosessionloaded || tableempty);
        SaveAsFileButton.setDisable(nosessionloaded || tableempty);
        ExportButton.setDisable(nosessionloaded || tableempty);
        RemoveButton.setDisable(nosessionloaded || tableempty || indexes.isEmpty());
        if (! singleitemselected) {
            MoveUpButton.setDisable(true);
            MoveDownButton.setDisable(true);
        } else {
            MoveUpButton.setDisable(nosessionloaded || tableempty || indexes.isEmpty() || indexes.get(0) == 0);
            MoveDownButton.setDisable(nosessionloaded || tableempty || indexes.isEmpty() || indexes.get(0) == CreatedTableView.getItems().size() -1);
        }
        FavoriteMenuItem.setDisable(nosessionloaded || tableempty);
        ClearButton.setDisable(nosessionloaded || tableempty);
        SessionSummary_CompletionTime_Label.setDisable(nosessionloaded || tableempty);
        SessionSummary_CompletionTime.setDisable(nosessionloaded || tableempty);
        SessionSummary_Duration.setDisable(nosessionloaded || tableempty);
        SessionSummary_Duration_Label.setDisable(nosessionloaded || tableempty);
        if (nosessionloaded) {CreatedTableView.setPlaceholder(new Label("Please Create Or Open A Session"));}
        else {CreatedTableView.setPlaceholder(new Label("Session Is Empty"));}
    }
    private void calculatedurationandestimatedcompletion() {
        if (createdsession != null && createdsession.hasItems()) {
            SessionSummary_Duration.setText(Util.formatdurationtoStringSpelledOutShort(createdsession.getExpectedSessionDuration(), false));
            SessionSummary_Duration.setTooltip(new Tooltip(Util.formatdurationtoStringSpelledOut(createdsession.getExpectedSessionDuration(), Double.MAX_VALUE)));
            LocalTime now = LocalTime.now();
            LocalTime completiontime = now.plusMinutes((int) createdsession.getExpectedSessionDuration().toMinutes());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
            String completiontext = completiontime.format(formatter);
            SessionSummary_CompletionTime.setText(completiontext);
            SessionSummary_CompletionTime.setTooltip(new Tooltip("Completion Time If You Start Now"));
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
    private PlaybackItemDetails getTotalDetails(String type) {
        List<PlaybackItemDetails> items = getSessionItemDetails(type);
        PlaybackItemDetails totaldetails = new PlaybackItemDetails(15, "Total");
        for (Session i : sessions.getSession()) {
            for (PlaybackItemDetails x : items) {
                if (i.containsPlaybackItem(x.getCreationindex())) {
                    totaldetails.addsessioncount();
                    break;
                }
            }
        }
        return totaldetails;
    }
    private List<PlaybackItemDetails> getSessionItemDetails(String type) {
        if (type == null) {return null;}
        Session testsession = new Session();
        switch (type) {
            case "All":
                for (int i = 0; i < 15; i++) { testsession.addplaybackitem(i);}
                break;
            case "Kuji-In":
                for (int i = 1; i < 10; i++) { testsession.addplaybackitem(i);}
                break;
            case "Elements":
                for (int i = 10; i < 15; i++) { testsession.addplaybackitem(i);}
                break;
        }
        testsession.calculateexpectedduration();
        List<PlaybackItemDetails> itemDetails = new ArrayList<>();
        List<Integer> sessionindexes = new ArrayList<>();
        for (PlaybackItem i : testsession.getPlaybackItems()) { itemDetails.add(new PlaybackItemDetails(i.getCreationindex(), i.getName())); sessionindexes.add(i.getCreationindex());}
        for (Session i : sessions.getSession()) {
            for (PlaybackItem x : i.getPlaybackItems()) {
                if (sessionindexes.contains(x.getCreationindex()) && x.getPracticeTime() > 0.0) {
                    for (PlaybackItemDetails t : itemDetails) {
                        if (t.getCreationindex() == x.getCreationindex()) {
                            t.addsessioncount();
                            t.addtotalDuration(Duration.millis(x.getPracticeTime()));
                        }
                    }
                }
            }
        }
        return itemDetails;
    }
    private void populateprogressoverview(String type) {
        ProgressOverviewBarChart.getData().clear();
        if (sessions.hasSessions() && type != null) {
            List<PlaybackItemDetails> playbackItemDetails = getSessionItemDetails(type);
            boolean displayinhours = false;
            for (PlaybackItemDetails i : playbackItemDetails) { if (i.getTotalduration().greaterThanOrEqualTo(Duration.hours(2.5))) { displayinhours = true; } }

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            Duration grandtotalduration = Duration.ZERO;
            for (PlaybackItemDetails i : playbackItemDetails) {
                double time;
                if (displayinhours) { time = i.getTotalduration().toHours(); ProgressOverviewNumbersAxis.setLabel("Hours"); }
                else { time = i.getTotalduration().toMinutes(); ProgressOverviewNumbersAxis.setLabel("Minutes"); }
                series.getData().add(new XYChart.Data<>(i.getName(), time));
                grandtotalduration = grandtotalduration.add(i.getTotalduration());
            }
            ProgressOverviewBarChart.getData().add(series);
            for (final XYChart.Data<String, Number> data : series.getData()) {
                PlaybackItemDetails pid = playbackItemDetails.get(series.getData().indexOf(data));
                Tooltip tooltip = new Tooltip();
                StringBuilder tooltiptext = new StringBuilder();
                tooltiptext.append("Total Duration: ");
                if (displayinhours) {tooltiptext.append(Util.formatdurationtoStringSpelledOut(Duration.hours(data.getYValue().doubleValue()), Double.MAX_VALUE));}
                else {tooltiptext.append(Util.formatdurationtoStringSpelledOut(Duration.minutes(data.getYValue().doubleValue()), Double.MAX_VALUE));}
                tooltiptext.append("\n");

                tooltiptext.append("Average Practice Length: ");
                tooltiptext.append(Util.formatdurationtoStringDecimalWithColons(pid.getAverageDuration())).append("\n");
                tooltiptext.append("# Of Sessions Practiced: ").append(pid.getSessioncount());

                tooltip.setText(tooltiptext.toString());
                Tooltip.install(data.getNode(), tooltip);
            }
        // Set Total Time Practiced
            PlaybackItemDetails totaldetails = getTotalDetails(type);

            String typename;
            if (type.equals("All")) {typename = "";}
            else {typename = type + " ";}

            Tooltip totaltooltip = new Tooltip();
            StringBuilder tooltiptext = new StringBuilder();
            tooltiptext.append("Total ").append(typename).append("Duration: ");
            tooltiptext.append(Util.formatdurationtoStringSpelledOut(grandtotalduration, Double.MAX_VALUE));
            tooltiptext.append("\n");

            tooltiptext.append("Average ").append(typename).append("Practice Length: ");
            Duration averageduration = grandtotalduration.divide(totaldetails.getSessioncount());
            tooltiptext.append(Util.formatdurationtoStringDecimalWithColons(averageduration)).append("\n");
            tooltiptext.append("# Of ").append(typename).append("Sessions Practiced: ").append(totaldetails.getSessioncount());

            totaltooltip.setText(tooltiptext.toString());

            ProgressOverviewTotalTimePracticed.setText(Util.formatdurationtoStringSpelledOutShort(grandtotalduration, true));
            ProgressOverviewTotalTimePracticed.setTooltip(totaltooltip);
            ProgressOverviewBarChart.setLegendVisible(false);
        }
    }
    private void populateprogressbalance(String type) {
        ProgressBalancePieChart.getData().clear();
        if (sessions.hasSessions() && type != null) {
            List<PlaybackItemDetails> playbackItemDetails = getSessionItemDetails(type);
            boolean displayinhours = false;
            for (PlaybackItemDetails i : playbackItemDetails) { if (i.getTotalduration().greaterThanOrEqualTo(Duration.hours(2.5))) { displayinhours = true; } }
            ObservableList<PieChart.Data> piechartvalues = FXCollections.observableArrayList();
            for (PlaybackItemDetails i : playbackItemDetails) {
                double time;
                if (displayinhours) {time = i.getTotalduration().toHours();}
                else {time = i.getTotalduration().toMinutes();}
                if (i.getTotalduration().greaterThan(Duration.ZERO)) {piechartvalues.add(new PieChart.Data(i.getName(), time));}
            }
            ProgressBalancePieChart.setData(piechartvalues);
            double total = 0;
            for (PieChart.Data d : ProgressBalancePieChart.getData()) {total += d.getPieValue();}
            for (final PieChart.Data data : ProgressBalancePieChart.getData()) {
                String name = String.format("%s (%.1f%%)", data.getName(), 100 * data.getPieValue() / total);
                data.setName(name);
                PlaybackItemDetails pid = playbackItemDetails.get(ProgressBalancePieChart.getData().indexOf(data));
                Tooltip tooltip = new Tooltip();
                StringBuilder tooltiptext = new StringBuilder();
                tooltiptext.append("Total Duration: ");
                if (displayinhours) {tooltiptext.append(Util.formatdurationtoStringSpelledOut(Duration.hours(data.getPieValue()), Double.MAX_VALUE));}
                else {tooltiptext.append(Util.formatdurationtoStringSpelledOut(Duration.minutes(data.getPieValue()), Double.MAX_VALUE));}
                tooltiptext.append("\n");

                tooltiptext.append("Average Practice Length: ");
                tooltiptext.append(Util.formatdurationtoStringDecimalWithColons(pid.getAverageDuration())).append("\n");
                tooltiptext.append("# Of Sessions Practiced: ").append(pid.getSessioncount());

                tooltip.setText(tooltiptext.toString());
                Tooltip.install(data.getNode(), tooltip);
            }
        }
    }
    // Session Browser Tab Methods
    public void populatesessionbrowsertable() {
        if (sessions.hasSessions()) {
            List<Session> filteredsessions = new ArrayList<>();
            for (Session i : sessions.getSession()) {
                if (! SessionBrowser_Filter_ShowMissedSessions.isSelected() && i.isMissedsession()) {continue;}
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
            Collections.reverse(sessionlist);
            PracticedSessionListTable.setItems(sessionlist);
        } else {PracticedSessionListTable.setPlaceholder(new Label("No Practiced Sessions"));}
    }
    public void populatesessiondetailstable() {
        SessionBrowserTableItem item = PracticedSessionListTable.getSelectionModel().getSelectedItem();
        SessionBrowser_DetailsTable.getItems().clear();
        SessionBrowser_Details_TotalDuration.setText(null);
        SessionBrowser_Details_TotalDuration_Label.setDisable(item == null);
        SessionBrowser_Details_TotalDuration.setDisable(item == null);
        SessionBrowser_DeleteSessionButton.setDisable(item == null);
        if (item != null) {
            selectedsessionbrowsersession = sessions.get(item.uuid);
            ObservableList<TableItem_Number_Name_Duration> sessionitems = FXCollections.observableArrayList();
            int count = 1;
            for (PlaybackItem i : selectedsessionbrowsersession.getPlaybackItems()) {
                String duration = Util.formatdurationtoStringDecimalWithColons(new Duration(i.getPracticeTime()));
                if (i.getPracticeTime() < i.getExpectedDuration()) {
                    duration += " (Of " + Util.formatdurationtoStringDecimalWithColons(new Duration(i.getExpectedDuration())) + ")";
                }
                sessionitems.add(new TableItem_Number_Name_Duration(count, i.getName(), duration));
                count++;
            }
            SessionBrowser_DetailsTable.setItems(sessionitems);
            SessionBrowser_Details_TotalDuration.setText(Util.formatdurationtoStringSpelledOutShort(selectedsessionbrowsersession.getSessionPracticedTime(), true));
            SessionBrowser_Details_TotalDuration.setTooltip(new Tooltip(Util.formatdurationtoStringSpelledOut(selectedsessionbrowsersession.getSessionPracticedTime(), Double.MAX_VALUE)));
            SessionBrowser_ViewNotesButton.setDisable(selectedsessionbrowsersession.getNotes() == null || selectedsessionbrowsersession.getNotes().isEmpty());
        } else {
            selectedsessionbrowsersession = null;
            SessionBrowser_DetailsTable.setPlaceholder(new Label("Select A Session To View Details"));
        }
    }
    public void viewsessionnotes() {
        SessionBrowserTableItem item = PracticedSessionListTable.getSelectionModel().getSelectedItem();
        if (item != null) {
            EditSessionNotes editSessionNotes = new EditSessionNotes(sessions.get(item.uuid), "Session Notes", true);
            editSessionNotes.initModality(Modality.APPLICATION_MODAL);
            editSessionNotes.showAndWait();
        }
    }
    public void deletesession() {
        if (selectedsessionbrowsersession != null && new ConfirmationDialog(preferences, "Delete Session", "Really Delete Session?", "This Cannot Be Undone").getResult()) {
            List<Session> sessionlist = sessions.getSession();
            sessionlist.remove(selectedsessionbrowsersession);
            sessions.setSession(sessionlist);
            sessions.marshall();
            populatesessionbrowsertable();
            populategoalsdetailstable();
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
        if (sessions.hasSessions()) {
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
            SessionBrowser_Filter.setDisable(! sessions.hasSessions());
            if (sessions.hasSessions()) {
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
                        double percent = (sessions.gettotalpracticedtime(playbackItem, false).toMillis() / i.getDuration().toMillis()) * 100;
                        if (percent < 100) { percentcompleted = String.format("%.2f%%", percent);}
                        else {percentcompleted = "100%";}
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

    class PlaybackItemDetails {
        private String name;
        private int creationindex;
        private int sessioncount;
        private Duration totalduration;

        public PlaybackItemDetails(int creationindex, String name) {
            this.creationindex = creationindex;
            this.name = name;
            sessioncount = 0;
            totalduration = Duration.ZERO;
        }

        void addsessioncount() {sessioncount++;}
        void addtotalDuration(Duration duration) {totalduration = totalduration.add(duration);}
        int getCreationindex() {
            return creationindex;
        }
        int getSessioncount() {
            return sessioncount;
        }
        Duration getTotalduration() {
            return totalduration;
        }
        String getName() {
            return name;
        }
        Duration getAverageDuration() {return totalduration.divide(sessioncount);}
    }
}