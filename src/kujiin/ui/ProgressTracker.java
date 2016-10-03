package kujiin.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.ui.dialogs.ConfirmationDialog;
import kujiin.ui.dialogs.ExceptionDialog;
import kujiin.ui.dialogs.InformationDialog;
import kujiin.ui.dialogs.SessionDetails;
import kujiin.util.Qi_Gong;
import kujiin.util.SessionPart;
import kujiin.util.Total;
import kujiin.util.Util;
import kujiin.util.interfaces.UI;
import kujiin.util.table.GoalProgressBinding;
import kujiin.util.table.SessionRow;
import kujiin.util.table.TotalProgressRow;
import kujiin.xml.Goals;
import kujiin.xml.Options;
import kujiin.xml.Session;
import kujiin.xml.Sessions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProgressTracker implements UI {
    private ComboBox<String> GoalSessionPartComboBox;
    private TextField AverageSessionDuration;
    private TextField TotalTimePracticed;
    private TextField NumberOfSessionPracticed;
    private CheckBox PrePostSwitch;
    private Button ListOfSessionsButton;
    private Label GoalTopLabel;
    private ProgressBar GoalProgressBar;
    private Label GoalPercentageLabel;
    private Button NewGoalButton;
    private Button ViewCurrentGoalsButton;
    private Sessions Sessions;
    private Goals Goals;
    private SessionPart SelectedSessionPart;
    private ArrayList<SessionPart> SessionParts;
    private Options Options;
    private MainController Root;

    public ProgressTracker(MainController Root) {
        Options = Root.getOptions();
        Sessions = new Sessions(Root);
        this.Root = Root;
        Sessions.unmarshall();
        Goals = new Goals(Root);
        Goals.unmarshall();
        setupListeners(Root);
        GoalSessionPartComboBox = Root.GoalSessionPartComboBox;
        AverageSessionDuration = Root.AverageSessionDuration;
        TotalTimePracticed = Root.TotalTimePracticed;
        NumberOfSessionPracticed = Root.NumberOfSessionsPracticed;
        PrePostSwitch = Root.PrePostSwitch;
        ListOfSessionsButton = Root.ListOfSessionsButton;
        GoalTopLabel = Root.GoalTopLabel;
        GoalProgressBar = Root.goalsprogressbar;
        GoalPercentageLabel = Root.GoalProgressPercentageLabel;
        NewGoalButton = Root.newgoalButton;
        ViewCurrentGoalsButton = Root.viewcurrrentgoalsButton;
        NewGoalButton.setDisable(true);
        GoalSessionPartComboBox.setItems(FXCollections.observableArrayList(MainController.getAllSessionPartsNames(true)));
    }

    public SessionPart getSelectedSessionPart() {
        return SelectedSessionPart;
    }
    public void setSessionParts(ArrayList<SessionPart> sessionParts) {
        SessionParts = sessionParts;
    }

    public void setupListeners(MainController Root) {
        Root.GoalSessionPartComboBox.setOnAction(event -> sessionpartchanged());
        Root.PrePostSwitch.setOnAction(event -> updateui_sessions());
        Root.ListOfSessionsButton.setOnAction(event -> displaysessionlist());
        Root.newgoalButton.setOnAction(event -> setnewgoal());
        Root.viewcurrrentgoalsButton.setOnAction(event -> viewcurrentgoals());
    }
    public void setupTooltips() {}
    public void setDisable(boolean disable) {}
    public boolean cleanup() {return true;}

// Getters And Setters
    public kujiin.xml.Sessions getSessions() {
        return Sessions;
    }
    public kujiin.xml.Goals getGoals() {
        return Goals;
    }

// Sessions Part Selection
    private void sessionpartchanged() {
        int index = GoalSessionPartComboBox.getSelectionModel().getSelectedIndex();
        NewGoalButton.setDisable(index == -1);
        if (index != -1) {
            SelectedSessionPart = SessionParts.get(index);
            if (SelectedSessionPart instanceof Total) {PrePostSwitch.setSelected(true);}
        }
        updateui_sessions();
        updateui_goals(null);
    }
    public void sessionpart_forceselect(int sessionpartindex) {
        GoalSessionPartComboBox.getSelectionModel().select(sessionpartindex);
    }

// Sessions
    public void updateui_sessions() {
        String averagesessiondurationtext;
        String totalminutespracticedtext;
        String numberofsessionspracticedtext;
        boolean disabled;
        if (SelectedSessionPart == null) {
            averagesessiondurationtext = "No Sessions";
            totalminutespracticedtext = "No Sessions";
            numberofsessionspracticedtext = "No Sessions";
            disabled = true;
        } else {
            if (SelectedSessionPart.sessions_getPracticedSessionCount(null) > 0) {
                averagesessiondurationtext = SelectedSessionPart.sessions_ui_getAverageSessionLength();
                totalminutespracticedtext = SelectedSessionPart.sessions_ui_getPracticedDuration();
                numberofsessionspracticedtext = SelectedSessionPart.sessions_ui_getPracticedSessionCount();
                disabled = false;
            } else {
                averagesessiondurationtext = "No Sessions";
                totalminutespracticedtext = "No Sessions";
                numberofsessionspracticedtext = "No Sessions";
                disabled = true;
            }
        }
        TotalTimePracticed.setText(totalminutespracticedtext);
        NumberOfSessionPracticed.setText(numberofsessionspracticedtext);
        AverageSessionDuration.setText(averagesessiondurationtext);
        TotalTimePracticed.setDisable(disabled);
        NumberOfSessionPracticed.setDisable(disabled);
        AverageSessionDuration.setDisable(disabled);
        if (SelectedSessionPart instanceof Qi_Gong) {
            PrePostSwitch.setDisable(true);
            PrePostSwitch.setSelected(false);
        } else {
            PrePostSwitch.setDisable(disabled);
        }
    }
    public void displaysessionlist() {
        if (Sessions.getSession() == null || Sessions.getSession().size() == 0) {
            new InformationDialog(Options, "No Sessions", "No Practiced Sessions", "Cannot View Sessions");
        } else {new AllSessionDetails().showAndWait();}
    }
    public void displaysessiondetails(Session individualsession) {
        new SessionDetails(Options, individualsession).showAndWait();
    }
    public void displaysessiondetails(List<SessionPart> itemsinsession) {
        new SessionDetails(Root, itemsinsession).show();
    }

// Goals
    public void updateui_goals(SessionCreator.Player playerUI) {
        boolean disabled = SelectedSessionPart == null || SelectedSessionPart.goals_getCurrent() == null;
        Tooltip goalprogresstooltip;
        String toptext;
        String newgoalbuttontext;
        String percentage;
        Double progress;
        Tooltip newgoalbuttontooltip;
        NewGoalButton.setDisable(SelectedSessionPart == null);
        ViewCurrentGoalsButton.setDisable(disabled);
        GoalProgressBar.setDisable(disabled);
        GoalPercentageLabel.setDisable(disabled);
        GoalTopLabel.setDisable(disabled);
        if (SelectedSessionPart == null) {
            toptext = "Goal Progress Tracker";
            goalprogresstooltip = new Tooltip("");
            percentage = "";
            progress = 0.0;
            newgoalbuttontext = kujiin.xml.Options.NEWGOALTEXT;
            newgoalbuttontooltip = new Tooltip("Set A New Goal");
        } else if (SelectedSessionPart.goals_getCurrent() == null || SelectedSessionPart.sessions_getPracticedDuration(null).lessThanOrEqualTo(Duration.ZERO)) {
            // No Current Goal Set
            toptext = "No Current Goal";
            percentage = SelectedSessionPart.goals_ui_getcurrentgoalpercentage(2);
            progress = SelectedSessionPart.goals_ui_getcurrentgoalprogress();
            goalprogresstooltip = new Tooltip("No Current Goal Set For " + SelectedSessionPart.name);
            newgoalbuttontext = kujiin.xml.Options.NEWGOALTEXT;
            newgoalbuttontooltip = new Tooltip("Set A New Goal");
        } else {
            toptext = "Current Goal Progress";
            percentage = SelectedSessionPart.goals_ui_getcurrentgoalpercentage(2);
            progress = SelectedSessionPart.goals_ui_getcurrentgoalprogress();
            goalprogresstooltip = new Tooltip(String.format("Currently Practiced: %s -> Goal: %s",
                    Util.formatdurationtoStringSpelledOut(SelectedSessionPart.sessions_getPracticedDuration(null), null),
                    Util.formatdurationtoStringSpelledOut(SelectedSessionPart.goals_getCurrent().getDuration(), null))
            );
            newgoalbuttontext = kujiin.xml.Options.GOALPACINGTEXT;
            newgoalbuttontooltip = new Tooltip("Calculate Goal Pacing For This Goal");
        }
        GoalPercentageLabel.setText(percentage);
        GoalProgressBar.setProgress(progress);
        GoalTopLabel.setText(toptext);
        GoalPercentageLabel.setTooltip(goalprogresstooltip);
        GoalProgressBar.setTooltip(goalprogresstooltip);
        NewGoalButton.setText(newgoalbuttontext);
        NewGoalButton.setTooltip(newgoalbuttontooltip);
        if (playerUI != null && playerUI.isShowing()) {
            playerUI.GoalTopLabel.setDisable(disabled);
            playerUI.GoalPercentageLabel.setDisable(disabled);
            playerUI.GoalProgressBar.setDisable(disabled);
            playerUI.GoalTopLabel.setText(toptext);
            playerUI.GoalProgressBar.setProgress(progress);
            playerUI.GoalPercentageLabel.setText(percentage);
            // String.format("%s hrs -> %s hrs (%d", practiceddecimalhours, goaldecimalhours, progress.intValue()) + "%)");
        }
        if (SelectedSessionPart != null && Options.getProgramOptions().getTooltips()) {
            NewGoalButton.setTooltip(new Tooltip("Set A New Goal"));
            ViewCurrentGoalsButton.setTooltip(new Tooltip("Edit " + SelectedSessionPart.name + "'s Goals"));
        }
    }
    private void setnewgoal() {
        if (NewGoalButton.getText().equals(kujiin.xml.Options.NEWGOALTEXT)) {
            SimpleGoalSetDialog simpleGoalSetDialog = new SimpleGoalSetDialog(SelectedSessionPart);
            simpleGoalSetDialog.showAndWait();
            if (simpleGoalSetDialog.shouldSetgoal()) {
                SelectedSessionPart.goals_add(new Goals.Goal(simpleGoalSetDialog.getNewGoalDuration()));
                updateui_goals(null);
            }
        } else if (NewGoalButton.getText().equals(kujiin.xml.Options.GOALPACINGTEXT)) {
            new GoalPacingDialog().showAndWait();
        }
    }
    public void setnewgoal(SessionPart sessionPart) {
        SimpleGoalSetDialog simpleGoalSetDialog = new SimpleGoalSetDialog(sessionPart);
        simpleGoalSetDialog.showAndWait();
        if (simpleGoalSetDialog.shouldSetgoal()) {
            sessionPart.goals_add(new Goals.Goal(simpleGoalSetDialog.getNewGoalDuration()));
            sessionPart.goals_marshall();
        }
    }
    public void viewcurrentgoals() {
        if (SelectedSessionPart.getGoals() == null || SelectedSessionPart.getGoals().isEmpty()) {
            new InformationDialog(Options, "Information", "No Goals Exist For " + SelectedSessionPart.name, "Please Add A Goal For " + SelectedSessionPart.name);
        } else {
            new AllSessionPartGoalProgress().showAndWait();
        }
    }

// Subclasses / Dialogs
    public class AllSessionPartGoalProgress extends Stage {
        public TableView<GoalProgressBinding> GoalsTable;
        public TableColumn<GoalProgressBinding, String> NameColumn;
        public TableColumn<GoalProgressBinding, String> PracticedTimeColumn;
        public TableColumn<GoalProgressBinding, String> CurrentGoalColumn;
        public TableColumn<GoalProgressBinding, String> PercentCompletedColumn;
        public TableColumn<GoalProgressBinding, String> NumberGoalsCompletedColumn;
        public Button SetCurrentGoalButton;
        public Button ViewCompletedGoalsButton;
        public Button CloseButton;
        private ObservableList<GoalProgressBinding> allgoalsdetails = FXCollections.observableArrayList();
        private String setgoaltext = "Set Goal";
        private String goalpacingtext = "Current Goal Pacing";

        public AllSessionPartGoalProgress() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/AllSessionPartsGoalProgress.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Options.setStyle(this);
                this.setResizable(false);
                setTitle("Goal Progress");
                NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
                PracticedTimeColumn.setCellValueFactory(cellData -> cellData.getValue().practicedtime);
                CurrentGoalColumn.setCellValueFactory(cellData -> cellData.getValue().currentgoaltime);
                PercentCompletedColumn.setCellValueFactory(cellData -> cellData.getValue().percentcompleted);
                NumberGoalsCompletedColumn.setCellValueFactory(cellData -> cellData.getValue().numbergoalscompleted);
                NameColumn.setStyle("-fx-alignment: CENTER;");
                PracticedTimeColumn.setStyle("-fx-alignment: CENTER;");
                CurrentGoalColumn.setStyle("-fx-alignment: CENTER;");
                PercentCompletedColumn.setStyle("-fx-alignment: CENTER;");
                NumberGoalsCompletedColumn.setStyle("-fx-alignment: CENTER;");
                GoalsTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> newrowselected());
                populatetable();
                newrowselected();
                if (SelectedSessionPart != null) {
                    GoalsTable.getSelectionModel().select(SelectedSessionPart.number);
                }
                this.setOnCloseRequest(event -> {
                    ArrayList<SessionPart> sessionpartsmissingcurrentgoals = SessionParts.stream().filter(i -> i.goals_getCurrent() == null).collect(Collectors.toCollection(ArrayList::new));
                    if (sessionpartsmissingcurrentgoals.size() > 0) {
                        if (! new ConfirmationDialog(Options, "Confirmation", null, "Missing Current Goals For " + sessionpartsmissingcurrentgoals.size() + " Session Parts. Close Without Setting Goals", "Close", "Cancel").getResult()) {
                            event.consume();
                        }
                    }
                });
            } catch (IOException e) {
                new ExceptionDialog(Options, e).showAndWait();
            }
        }

        public void populatetable() {
            allgoalsdetails.clear();
            for (SessionPart i : SessionParts) {
                Duration practicedtime = i.sessions_getPracticedDuration(false);
                String practicedtext;
                if (practicedtime.lessThanOrEqualTo(Duration.ZERO)) {
                    practicedtext = "No Practiced Time";
                } else {
                    practicedtext = Util.formatdurationtoStringSpelledOut(i.sessions_getPracticedDuration(false), null);
                }
                String currentgoaltime;
                String percentcompleted;
                if (i.goals_ui_hascurrentgoal()) {
                    currentgoaltime = i.goals_ui_getcurrentgoalDuration(null);
                    percentcompleted = i.goals_ui_getcurrentgoalpercentage(0);
                } else {
                    currentgoaltime = "No Goal Set";
                    percentcompleted = "No Goal Set";
                }
                allgoalsdetails.add(new GoalProgressBinding(i.name, practicedtext, currentgoaltime, percentcompleted, i.goals_get(false, true).size()));
            }
            GoalsTable.setItems(allgoalsdetails);
        }
        public void newrowselected() {
            if (GoalsTable.getSelectionModel().getSelectedIndex() == -1) {
                SelectedSessionPart = null;
            } else {
                SelectedSessionPart = SessionParts.get(GoalsTable.getSelectionModel().getSelectedIndex());
            }
            if (SelectedSessionPart == null) {
                SetCurrentGoalButton.setDisable(true);
                ViewCompletedGoalsButton.setDisable(true);
            } else {
                SetCurrentGoalButton.setDisable(false);
                if (SelectedSessionPart.goals_getCurrent() == null) {
                    SetCurrentGoalButton.setText(setgoaltext);
                } else {
                    SetCurrentGoalButton.setText(goalpacingtext);
                }
                ViewCompletedGoalsButton.setDisable(SelectedSessionPart.goals_get(false, true).size() == 0);
            }
        }
        public void setcurrentgoal(ActionEvent actionEvent) {
            if (SelectedSessionPart != null) {
                SimpleGoalSetDialog setDialog = new SimpleGoalSetDialog(SelectedSessionPart);
                setDialog.showAndWait();
                if (setDialog.shouldSetgoal()) {
                    SelectedSessionPart.goals_add(new Goals.Goal(setDialog.getNewGoalDuration()));
                    populatetable();
                }
            }
        }
        public void viewcompletedgoals(ActionEvent actionEvent) {
            if (SelectedSessionPart != null) {

            }
        }

    }
    public class AllSessionDetails extends Stage {
        // All Session Totals Tab Fields
        public BarChart<String, Number> SessionTotalsBarGraph;
        public javafx.scene.chart.CategoryAxis CategoryAxis;
        public NumberAxis NumbersAxis;
        // Session Details List Tab Fields
        // Filter Accordion
        // Date
        public CheckBox FilterByDateSwitch;
        public DatePicker Filter_DateRange_From;
        public DatePicker Filter_DateRange_To;
        // Selected Session Parts
        public CheckBox FilterBySelectedSwitch;
        public CheckBox Filter_PresessionCheckbox;
        public CheckBox Filter_PostsessionCheckbox;
        public CheckBox Filter_RinCheckbox;
        public CheckBox Filter_KyoCheckbox;
        public CheckBox Filter_TohCheckbox;
        public CheckBox Filter_ShaCheckbox;
        public CheckBox Filter_KaiCheckbox;
        public CheckBox Filter_JinCheckbox;
        public CheckBox Filter_RetsuCheckbox;
        public CheckBox Filter_ZaiCheckbox;
        public CheckBox Filter_ZenCheckbox;
        public CheckBox Filter_EarthCheckbox;
        public CheckBox Filter_AirCheckbox;
        public CheckBox Filter_FireCheckbox;
        public CheckBox Filter_WaterCheckbox;
        public CheckBox Filter_VoidCheckbox;
        // Optional Threshhold
        public CheckBox Filter_DurationThresholdCheckbox;
        public TextField Filter_ThresholdMinutesTextField;
        // Session List Table
        public TableView<SessionRow> sessionsTableView;
        public TableColumn<SessionRow, String> DateColumn;
        public TableColumn<SessionRow, String> PreColumn;
        public TableColumn<SessionRow, String> RinColumn;
        public TableColumn<SessionRow, String> KyoColumn;
        public TableColumn<SessionRow, String> TohColumn;
        public TableColumn<SessionRow, String> ShaColumn;
        public TableColumn<SessionRow, String> KaiColumn;
        public TableColumn<SessionRow, String> JinColumn;
        public TableColumn<SessionRow, String> RetsuColumn;
        public TableColumn<SessionRow, String> ZaiColumn;
        public TableColumn<SessionRow, String> ZenColumn;
        public TableColumn<SessionRow, String> EarthColumn;
        public TableColumn<SessionRow, String> AirColumn;
        public TableColumn<SessionRow, String> FireColumn;
        public TableColumn<SessionRow, String> WaterColumn;
        public TableColumn<SessionRow, String> VoidColumn;
        public TableColumn<SessionRow, String> PostColumn;
        public TableColumn<SessionRow, String> TotalColumn;
        public Button ViewDetailsButton;
        public TableView<TotalProgressRow> TotalProgressTableView;
        public TableColumn<TotalProgressRow, String> NameColumn;
        public TableColumn<TotalProgressRow, String> CurrentPracticeTimeColumn;
        public PieChart SessionBalancePieChart;

        // My Fields
        private List<Session> allsessionslist;
        private List<Session> filteredsessionlist;
        private ObservableList<SessionRow> sessionrowlist = FXCollections.observableArrayList();

        public AllSessionDetails() {
            try {
                allsessionslist = getSessions().getSession();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SessionDetails_All.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Options.setStyle(this);
                this.setResizable(false);
                setTitle("Session List");
                DateColumn.setCellValueFactory(cellData -> cellData.getValue().datepracticed);
                DateColumn.setCellValueFactory(cellData -> cellData.getValue().datepracticed);
                RinColumn.setCellValueFactory(cellData -> cellData.getValue().rin);
                KyoColumn.setCellValueFactory(cellData -> cellData.getValue().kyo);
                TohColumn.setCellValueFactory(cellData -> cellData.getValue().toh);
                ShaColumn.setCellValueFactory(cellData -> cellData.getValue().sha);
                KaiColumn.setCellValueFactory(cellData -> cellData.getValue().kai);
                JinColumn.setCellValueFactory(cellData -> cellData.getValue().jin);
                RetsuColumn.setCellValueFactory(cellData -> cellData.getValue().retsu);
                ZaiColumn.setCellValueFactory(cellData -> cellData.getValue().zai);
                ZenColumn.setCellValueFactory(cellData -> cellData.getValue().zen);
                EarthColumn.setCellValueFactory(cellData -> cellData.getValue().earth);
                AirColumn.setCellValueFactory(cellData -> cellData.getValue().air);
                FireColumn.setCellValueFactory(cellData -> cellData.getValue().fire);
                WaterColumn.setCellValueFactory(cellData -> cellData.getValue().water);
                VoidColumn.setCellValueFactory(cellData -> cellData.getValue().Void);
                PostColumn.setCellValueFactory(cellData -> cellData.getValue().postsession);
                TotalColumn.setCellValueFactory(cellData -> cellData.getValue().total);
                NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
                CurrentPracticeTimeColumn.setCellValueFactory(cellData -> cellData.getValue().formattedduration);
                ViewDetailsButton.setDisable(true);
                sessionsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> ViewDetailsButton.setDisable(sessionsTableView.getSelectionModel().getSelectedIndex() == -1));
                Util.custom_textfield_integer(Filter_ThresholdMinutesTextField, 0, Integer.MAX_VALUE, 1);
                populatetotalsbargraphandtable();
                populatetable(null);
            } catch (IOException e) {
                new ExceptionDialog(Options, e).showAndWait();
            }
        }

        public CheckBox getcheckbox(SessionPart sessionpart) {
            switch (sessionpart.number) {
                case 0:
                    return Filter_PresessionCheckbox;
                case 1:
                    return Filter_RinCheckbox;
                case 2:
                    return Filter_KyoCheckbox;
                case 3:
                    return Filter_TohCheckbox;
                case 4:
                    return Filter_ShaCheckbox;
                case 5:
                    return Filter_KaiCheckbox;
                case 6:
                    return Filter_JinCheckbox;
                case 7:
                    return Filter_RetsuCheckbox;
                case 8:
                    return Filter_ZaiCheckbox;
                case 9:
                    return Filter_ZenCheckbox;
                case 10:
                    return Filter_EarthCheckbox;
                case 11:
                    return Filter_AirCheckbox;
                case 12:
                    return Filter_FireCheckbox;
                case 13:
                    return Filter_WaterCheckbox;
                case 14:
                    return Filter_VoidCheckbox;
                case 15:
                    return Filter_PostsessionCheckbox;
                default:
                    return null;
            }
        }
        public void populatetotalsbargraphandtable() {
            ObservableList<TotalProgressRow> totalprogressrows = FXCollections.observableArrayList();
            ObservableList<PieChart.Data> piecesofthepie = FXCollections.observableArrayList();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (SessionPart i : SessionParts) {
                if (!(i instanceof Total)) {
                    series.getData().add(new XYChart.Data<>(i.getNameForChart(), i.sessions_getPracticedDuration(false).toHours()));
                    piecesofthepie.add(new PieChart.Data(i.getNameForChart(), i.sessions_getPracticedDuration(false).toHours()));
                }
                totalprogressrows.add(new TotalProgressRow(i.getNameForChart(), Util.formatdurationtoStringDecimalWithColons(i.sessions_getPracticedDuration(false))));
            }
            SessionBalancePieChart.getData().addAll(piecesofthepie);
            TotalProgressTableView.setItems(totalprogressrows);
            SessionTotalsBarGraph.getData().add(series);
        }
        public void populatetable(ActionEvent actionEvent) {
            filteredsessionlist = new ArrayList<>();
            ObservableList<SessionRow> rowlist = FXCollections.observableArrayList();
            int count = 1;
            for (Session i : allsessionslist) {
                if (FilterByDateSwitch.isSelected()) {
                    LocalDate sessiondate = i.getDate_Practiced();
                    if (Filter_DateRange_From.getValue() != null) {
                        if (sessiondate.isBefore(Filter_DateRange_From.getValue())) {
                            continue;
                        }
                    }
                    if (Filter_DateRange_To.getValue() != null) {
                        if (sessiondate.isAfter(Filter_DateRange_To.getValue())) {
                            continue;
                        }
                    }
                }
                if (FilterBySelectedSwitch.isSelected()) {
                    boolean validsession = true;
                    for (SessionPart j : Root.getAllSessionParts(false)) {
                        if (! validsession) {break;}
                        if (getcheckbox(j).isSelected()) {
                            if (Filter_DurationThresholdCheckbox.isSelected()) {
                                try {
                                    if (i.getsessionpartduration(j) <= Integer.parseInt(Filter_ThresholdMinutesTextField.getText())) {
                                        validsession = false;
                                    }
                                } catch (NumberFormatException | NullPointerException ignored) {
                                    validsession = i.getsessionpartduration(j) > 0;
                                }
                            } else {
                                if (i.getsessionpartduration(j) == 0) {
                                    validsession = false;
                                }
                            }
                        }
                    }
                    if (!validsession) {
                        continue;
                    }
                }
                rowlist.add(new SessionRow(count, i.getDate_Practiced().format(Util.dateFormat), i.getPresession_Duration(), i.getRin_Duration(),
                        i.getKyo_Duration(), i.getToh_Duration(), i.getSha_Duration(), i.getKai_Duration(), i.getJin_Duration(),
                        i.getRetsu_Duration(), i.getZai_Duration(), i.getZen_Duration(), i.getEarth_Duration(), i.getAir_Duration(),
                        i.getFire_Duration(), i.getWater_Duration(), i.getVoid_Duration(), i.getPostsession_Duration(),
                        i.getTotal_Session_Duration()));
                filteredsessionlist.add(i);
                count++;
            }
            sessionsTableView.setItems(rowlist);
        }
        public void viewsessiondetails(ActionEvent actionEvent) {
            if (sessionsTableView.getSelectionModel().getSelectedIndex() != -1) {
                new SessionDetails(Options, filteredsessionlist.get(sessionsTableView.getSelectionModel().getSelectedIndex())).showAndWait();
            }
        }
        public void filterbydateselected(ActionEvent actionEvent) {
            if (!FilterByDateSwitch.isSelected()) {
                Filter_DateRange_From.setValue(null);
                Filter_DateRange_To.setValue(null);
            } else {
                try {Filter_DateRange_From.setValue(allsessionslist.get(0).getDate_Practiced());}
                catch (NullPointerException | IndexOutOfBoundsException ignored) {Filter_DateRange_From.setValue(LocalDate.now());}
                Filter_DateRange_To.setValue(LocalDate.now());
            }
        }

    }
    public class GoalPacingDialog extends Stage {
        public Spinner<Integer> PracticeDays;
        public TextField PracticeTimeADay;
        public TextField GoalDuration;
        public Button CloseButton;
        public TextField GoalTimeLeft;
        public TextField TotalPracticedTime;
        public Label TopLabel;
        private Duration practicedduration;
        private Duration goalduration;
        private Duration durationleft;

        public GoalPacingDialog() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/GoalPacingDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Options.setStyle(this);
                setResizable(false);
                setTitle("Goal Pacing");
                practicedduration = SelectedSessionPart.sessions_getPracticedDuration(false);
                goalduration = SelectedSessionPart.goals_getCurrent().getDuration();
                GoalDuration.setText(Util.formatdurationtoStringSpelledOut(goalduration, GoalDuration.getLayoutBounds().getWidth()));
                TotalPracticedTime.setText(Util.formatdurationtoStringSpelledOut(practicedduration, TotalPracticedTime.getLayoutBounds().getWidth()));
                durationleft = goalduration.subtract(practicedduration);
                GoalTimeLeft.setText(Util.formatdurationtoStringSpelledOut(durationleft, GoalTimeLeft.getLayoutBounds().getWidth()));
                Util.custom_spinner_integer(PracticeDays, 1, Integer.MAX_VALUE, 1, 1, false);
                PracticeDays.valueProperty().addListener((observable, oldValue, newValue) -> calculate());
                PracticeDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
                TopLabel.setText("Goal Pacing For " + SelectedSessionPart.name + " Current Goal");
                calculate();
            } catch (IOException e) {new ExceptionDialog(Options, e).showAndWait();}
        }

        // Other Methods
        public void calculate() {
            Double days = (double) PracticeDays.getValue();
            Float hourstopractice = (float) durationleft.toHours() / days.floatValue();
            int minsaday = (int) Duration.hours(hourstopractice.doubleValue()).toMinutes();
            PracticeTimeADay.setText(Util.formatdurationtoStringSpelledOut(new Duration((minsaday * 60) * 1000), PracticeTimeADay.getLayoutBounds().getWidth()));
        }
    }
    public class SimpleGoalSetDialog extends Stage {
        public Label TopLabel;
        public TextField HoursSpinner;
        public TextField MinutesSpinner;
        public Label StatusBar;
        public Button AcceptButton;
        public Button CancelButton;
        private boolean setgoal = false;
        private Duration practicedduration;

        public SimpleGoalSetDialog(SessionPart sessionPart) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SetGoalDialog_Simple.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Options.setStyle(this);
                setResizable(false);
                setTitle("Set A New Goal");
                TopLabel.setText("Set A New Goal For " + sessionPart.name);
                practicedduration = sessionPart.sessions_getPracticedDuration(false);
                System.out.println(sessionPart.name + "'s Practiced Duration: " + practicedduration.toMinutes() + " Minutes");
                HoursSpinner.setText(String.valueOf((int) practicedduration.toMinutes() / 60));
                MinutesSpinner.setText(String.valueOf((int) practicedduration.toMinutes() % 60));
                Util.custom_textfield_integer(HoursSpinner, 0, Integer.MAX_VALUE, 1);
                Util.custom_textfield_integer(MinutesSpinner, 0, 59, 5);
                HoursSpinner.textProperty().addListener((observable, oldValue, newValue) -> checkvalue());
                MinutesSpinner.textProperty().addListener((observable, oldValue, newValue) -> checkvalue());
            } catch (IOException e) {new ExceptionDialog(Options, e).showAndWait();}
        }

        private Duration getPotentialGoalDuration() {
            try {return Duration.hours(Integer.parseInt(HoursSpinner.getText())).add(Duration.minutes(Integer.parseInt(MinutesSpinner.getText())));}
            catch (NumberFormatException | NullPointerException ignored) {return Duration.ZERO;}
        }
        private void checkvalue() {
            Paint color;
            String text;
            boolean disabled;
            if (getPotentialGoalDuration().lessThanOrEqualTo(practicedduration)) {
                color = Color.RED;
                text = "Goal Value Less Than Practiced";
                disabled = true;
            } else {
                color = Color.BLACK;
                text = "";
                disabled = false;
            }
            StatusBar.setTextFill(color);
            StatusBar.setText(text);
            AcceptButton.setDisable(disabled);
        }

        public boolean shouldSetgoal() {
            return setgoal;
        }
        public Duration getNewGoalDuration() {
            try {
                return Duration.hours(Double.parseDouble(HoursSpinner.getText())).add(Duration.minutes(Double.parseDouble(MinutesSpinner.getText())));
            } catch (NullPointerException e) {return null;}
        }

        public void accept(ActionEvent actionEvent) {
            setgoal = true;
            close();
        }
    }

}