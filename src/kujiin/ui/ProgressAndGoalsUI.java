package kujiin.ui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import kujiin.MainController;
import kujiin.util.Meditatable;
import kujiin.util.Total;
import kujiin.util.Util;
import kujiin.xml.Goals;
import kujiin.xml.Session;
import kujiin.xml.Sessions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// TODO Finish Making EditGoalsDialog (Also Add A Select A Different Cut Feature, And Make It So They Can Pass Cutindex Data Back And Forth)
// TODO !IMPORTANT Fix Goal Multiple Goal Setter (Minutes Was Stuck At 30)
// TODO Make Goal Setter Only Whole Digit Integer Hours Instead Of Decimal

public class ProgressAndGoalsUI {
    private ComboBox<String> CutSelectorComboBox;
    private Meditatable SelectedMeditatable;
    private Sessions Sessions;
    private TextField TotalTimePracticed;
    private TextField NumberOfSessionsPracticed;
    private TextField AverageSessionDuration;
    private CheckBox PreAndPostOption;
    private Button SessionListButton;
    private Label GoalProgressPercentageLabel;
    private Goals Goals;
    private Button NewGoalButton;
    private Button EditGoalsButton;
    private ProgressBar GoalProgress;
    private Label TopLabel;
    private Label StatusBar;
    private MainController Root;
    private final String newgoaltext = "New Goal";
    private final String goalpacingtext = "Goal Pacing";

    public ProgressAndGoalsUI(MainController root) {
        Root = root;
        NewGoalButton = root.newgoalButton;
        EditGoalsButton = root.viewcurrrentgoalsButton;
        GoalProgress = root.goalsprogressbar;
        CutSelectorComboBox = root.GoalCutComboBox;
        TopLabel = root.GoalTopLabel;
        StatusBar = root.GoalStatusBar;
        TotalTimePracticed = root.TotalTimePracticed;
        NumberOfSessionsPracticed = root.NumberOfSessionsPracticed;
        AverageSessionDuration = root.AverageSessionDuration;
        PreAndPostOption = root.PrePostSwitch;
        SessionListButton = root.ListOfSessionsButton;
        GoalProgressPercentageLabel = root.GoalProgressPercentageLabel;
        Sessions = new Sessions(Root);
        Goals = new Goals(Root);
        Sessions.unmarshall();
        updatesessionsprogressui();
        Goals.unmarshall();
        updategoalsui();
        TotalTimePracticed.setEditable(false);
        NumberOfSessionsPracticed.setEditable(false);
        AverageSessionDuration.setEditable(false);
        CutSelectorComboBox.setItems(FXCollections.observableArrayList(Root.getSession().getAllMeditablesincludingTotalNames()));
        CutSelectorComboBox.setOnAction(this::meditatableselectionchanged);
        PreAndPostOption.setOnAction(this::meditatableselectionchanged);
    }

// Getters And Setters
    public Sessions getSessions() {return Sessions;}
    public Goals getGoal()        {
        return Goals;
    }

// Button Actions
    public void meditatableselectionchanged(ActionEvent actionEvent) {
        try {
            int index = CutSelectorComboBox.getSelectionModel().getSelectedIndex();
            SelectedMeditatable = Root.getSession().getAllMeditatablesincludingTotalforTracking().get(index);
            if (SelectedMeditatable instanceof Total) {PreAndPostOption.setSelected(true);}
            updatesessionsprogressui();
            updategoalsui();
        } catch (NullPointerException ignored) {
            resetallvalues();
        }
    }
    public void displaysessionlist() {
        if (Sessions.getSession() == null || Sessions.getSession().size() == 0) {
            Util.gui_showinformationdialog(Root, "No Sessions", "No Practiced Sessions", "Cannot View Sessions");
        } else {new AllSessionsDetails(Root).showAndWait();}
    }
    public void setnewgoal() {
        if (NewGoalButton.getText().equals(newgoaltext)) {
            SimpleGoalSetDialog simpleGoalSetDialog = new SimpleGoalSetDialog(Root, SelectedMeditatable);
            simpleGoalSetDialog.showAndWait();
            if (simpleGoalSetDialog.shouldSetgoal()) {
                // TODO Set Goal Here
                SelectedMeditatable.addGoal(new Goals.Goal(simpleGoalSetDialog.getNewGoalHours(), SelectedMeditatable));
                updategoalsui();
            }
        } else if (NewGoalButton.getText().equals(goalpacingtext)) {
            new GoalPacingDialog(Root, SelectedMeditatable).showAndWait();
        }
    }
    public void opengoaleditor() {
        if (SelectedMeditatable.getAllGoals() == null || SelectedMeditatable.getAllGoals().size() == 0) {
            Util.gui_showinformationdialog(Root, "Information", "No Goals Exist For " + SelectedMeditatable.name, "Please Add A Goal For " + SelectedMeditatable.name);
        } else {new AllMeditatablesGoalProgress(Root, SelectedMeditatable).showAndWait();}
    }

// Total Progress Specific Methods
    public void updatesessionsprogressui() {
    // Update Total Progress
        String averagesessiondurationtext;
        String totalminutespracticedtext;
        String numberofsessionspracticedtext;
        boolean disabled;
        int selectionindex = CutSelectorComboBox.getSelectionModel().getSelectedIndex();
        if (selectionindex == -1 || SelectedMeditatable == null) {
            averagesessiondurationtext = "No Sessions";
            totalminutespracticedtext = "No Sessions";
            numberofsessionspracticedtext = "No Sessions";
            disabled = true;
        } else {
            Double averagesessionduration = SelectedMeditatable.getAveragePracticeTime(PreAndPostOption.isSelected());
            Integer totalminutespracticed = SelectedMeditatable.getTotalMinutesPracticed(PreAndPostOption.isSelected());
            Integer numberofsessionspracticed = SelectedMeditatable.getNumberOfSessionsPracticed(PreAndPostOption.isSelected());
            if (numberofsessionspracticed > 0) {
                averagesessiondurationtext = Util.formatdurationtoStringSpelledOut(new Duration(averagesessionduration * 1000), AverageSessionDuration.getLayoutBounds().getWidth());
                totalminutespracticedtext = Util.formatdurationtoStringSpelledOut(new Duration(totalminutespracticed * 1000), TotalTimePracticed.getLayoutBounds().getWidth());
                numberofsessionspracticedtext = numberofsessionspracticed.toString();
                disabled = false;
            } else {
                averagesessiondurationtext = "No Sessions";
                totalminutespracticedtext = "No Sessions";
                numberofsessionspracticedtext = "No Sessions";
                disabled = true;
            }
        }
        TotalTimePracticed.setText(totalminutespracticedtext);
        NumberOfSessionsPracticed.setText(numberofsessionspracticedtext);
        AverageSessionDuration.setText(averagesessiondurationtext);
        TotalTimePracticed.setDisable(disabled);
        NumberOfSessionsPracticed.setDisable(disabled);
        AverageSessionDuration.setDisable(disabled);
        if (selectionindex == 0 || selectionindex == 15) {
            PreAndPostOption.setDisable(true);
            PreAndPostOption.setSelected(false);
        } else {PreAndPostOption.setDisable(disabled);}
    }

// Goal Specific Methods
    public ArrayList<Meditatable> getmeditatableswithoutlongenoughgoals(List<Meditatable> cutsandelementsinsession) {
        ArrayList<Meditatable> notgoodelementselementsorcuts = new ArrayList<>();
        for (Meditatable i : cutsandelementsinsession) {
            if (i.getdurationinminutes() != 0 && ! i.goalsarelongenough()) {
                notgoodelementselementsorcuts.add(i);
            }
        }
        return notgoodelementselementsorcuts;
    }
    public int getlowestgoalminutesforallmeditatables(List<Meditatable> meditatables) {
        List<Integer> currentgoalhours = new ArrayList<>();
        try {
            currentgoalhours.addAll(meditatables.stream().map(i -> i.getCurrentGoal().getGoal_Hours().intValue()).collect(Collectors.toList()));
            return Collections.min(currentgoalhours);
        } catch (Exception e) {return 0;}
    }
    public void updategoalsui() {
        boolean disabled = SelectedMeditatable == null || SelectedMeditatable.getCurrentGoal() == null;
        Tooltip goalprogresstooltip;
        String percentage;
        String toptext;
        Double progress;
        String newgoalbuttontext;
        Tooltip newgoalbuttontooltip;
        NewGoalButton.setDisable(SelectedMeditatable == null);
        EditGoalsButton.setDisable(disabled);
        GoalProgress.setDisable(disabled);
        GoalProgressPercentageLabel.setDisable(disabled);
        TopLabel.setDisable(disabled);
        if (SelectedMeditatable == null) {
            toptext = "Goal Progress Tracker";
            percentage = "";
            progress = 0.0;
            goalprogresstooltip = new Tooltip("");
            newgoalbuttontext = newgoaltext;
            newgoalbuttontooltip = new Tooltip("Set A New Goal");
        } else if (SelectedMeditatable.getCurrentGoal() == null || SelectedMeditatable.getTotalMinutesPracticed(false) == 0) {
            System.out.println(SelectedMeditatable.getCurrentGoal());
            // No Current Goal Set
            toptext = "No Current Goal";
            percentage = "";
            progress = 0.0;
            goalprogresstooltip = new Tooltip("No Current Goal Set For " + SelectedMeditatable.name);
            newgoalbuttontext = newgoaltext;
            newgoalbuttontooltip = new Tooltip("Set A New Goal");
        } else {
            toptext = "Current Goal Progress";
            Double goalminutes = SelectedMeditatable.getCurrentGoal().getGoal_Hours() * 60;
            progress = Util.convert_minstodecimalhours(SelectedMeditatable.getTotalMinutesPracticed(false), 2) / (goalminutes / 60);
            goalprogresstooltip = new Tooltip(String.format("Currently Practiced: %s -> Goal: %s",
                    Util.formatdurationtoStringSpelledOut(new Duration(SelectedMeditatable.getTotalMinutesPracticed(false) * 1000), null),
                    Util.formatdurationtoStringSpelledOut(new Duration(goalminutes * 1000), null))
            );
            percentage = new Double(progress * 100).intValue() + "%";
            newgoalbuttontext = goalpacingtext;
            newgoalbuttontooltip = new Tooltip("Calculate Goal Pacing For This Goal");
        }
        if (SelectedMeditatable != null && Root.getOptions().getProgramOptions().getTooltips()) {
            NewGoalButton.setTooltip(new Tooltip("Set A New Goal"));
            EditGoalsButton.setTooltip(new Tooltip("Edit " + SelectedMeditatable.name + "'s Goals"));
        }
        GoalProgressPercentageLabel.setText(percentage);
        GoalProgress.setProgress(progress);
        TopLabel.setText(toptext);
        GoalProgressPercentageLabel.setTooltip(goalprogresstooltip);
        GoalProgress.setTooltip(goalprogresstooltip);
        NewGoalButton.setText(newgoalbuttontext);
        NewGoalButton.setTooltip(newgoalbuttontooltip);
        PlayerUI playerUI = Root.getPlayer();
        if (playerUI != null && playerUI.isShowing()) {
            playerUI.GoalTopLabel.setDisable(disabled);
            playerUI.GoalPercentageLabel.setDisable(disabled);
            playerUI.GoalProgressBar.setDisable(disabled);
            playerUI.GoalTopLabel.setText(toptext);
            playerUI.GoalProgressBar.setProgress(progress);
            playerUI.GoalPercentageLabel.setText(percentage);
            // String.format("%s hrs -> %s hrs (%d", practiceddecimalhours, goaldecimalhours, progress.intValue()) + "%)");
        }
    }

// Other Methods
    public void selectmeditatable(int cutindex) {
        CutSelectorComboBox.getSelectionModel().select(cutindex);
    }
    public void selectmeditatable(Meditatable meditatable) {CutSelectorComboBox.getSelectionModel().select(meditatable.number);}

    public void resetallvalues()  {resetgoalsui(); resetsessionsui();}
    public void resetgoalsui()    {
//        PracticedHours.setText("");
//        PracticedMinutes.setText("");
//        GoalHours.setText("");
//        GoalMinutes.setText("");
        GoalProgress.setProgress(0.0);
    }
    public void resetsessionsui() {
        TotalTimePracticed.setText("No Sessions");
        NumberOfSessionsPracticed.setText("No Sessions");
        AverageSessionDuration.setText("No Sessions");
    }
    public boolean cleanup()      {
        Goals.marshall();
        Sessions.marshall();
        return true;
    }

// Subclasses/Dialogs
    public static class AllMeditatablesGoalProgress extends Stage {
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
        private Meditatable selectedmeditatable;
        public MainController Root;
        private String setgoaltext = "Set Goal";
        private String goalpacingtext = "Current Goal Pacing";

        public AllMeditatablesGoalProgress(MainController Root, Meditatable selectedmeditatable) {
            this.Root = Root;
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/AllMeditatablesGoalProgress.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
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
                if (selectedmeditatable != null) {GoalsTable.getSelectionModel().select(selectedmeditatable.number);}
                this.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        // TODO Check If Goals Set For All Meditatables, If Not Display Confirmation Dialog

                    }
                });
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
        }

        public void populatetable() {
            allgoalsdetails.clear();
            for (Meditatable i : Root.getSession().getAllMeditatablesincludingTotalforTracking()) {
                String practicedtime = Util.formatdurationtoStringSpelledOut(new Duration(i.getTotalMinutesPracticed(false)), null);
                if (practicedtime.equals("0 Minutes")) {
                    // TODO None Text Here! No Practiced Time Set
                }
                String currentgoaltime;
                String percentcompleted ;
                try {
                    currentgoaltime = Util.formatdurationtoStringSpelledOut(new Duration(i.getCurrentGoal().getGoal_Hours() * 3_600_000), null);
                    percentcompleted = String.valueOf(new Double((i.getTotalMinutesPracticed(false) / (i.getCurrentGoal().getGoal_Hours() * 60)) * 100).intValue()) + "%";
                } catch (NullPointerException ignored) {
                    currentgoaltime = "No Goal Set";
                    percentcompleted = "No Goal Set";
                }
                allgoalsdetails.add(new GoalProgressBinding(i.name, practicedtime, currentgoaltime, percentcompleted, i.getcompletedgoalcount()));
            }
            GoalsTable.setItems(allgoalsdetails);
        }
        public void newrowselected() {
            if (GoalsTable.getSelectionModel().getSelectedIndex() == -1) {selectedmeditatable = null;}
            else {selectedmeditatable = Root.getSession().getAllMeditatablesincludingTotalforTracking().get(GoalsTable.getSelectionModel().getSelectedIndex());}
            if (selectedmeditatable == null) {
                SetCurrentGoalButton.setDisable(true);
                ViewCompletedGoalsButton.setDisable(true);
            } else {
                SetCurrentGoalButton.setDisable(false);
                if (selectedmeditatable.getCurrentGoal() == null) {SetCurrentGoalButton.setText(setgoaltext);}
                else {SetCurrentGoalButton.setText(goalpacingtext);}
                ViewCompletedGoalsButton.setDisable(selectedmeditatable.getcompletedgoalcount() == 0);
            }
        }
        public void setcurrentgoal(ActionEvent actionEvent) {
            if (selectedmeditatable != null) {
                SimpleGoalSetDialog setDialog = new SimpleGoalSetDialog(Root, selectedmeditatable);
                setDialog.showAndWait();
                if (setDialog.shouldSetgoal()) {
                    selectedmeditatable.addGoal(new Goals.Goal(setDialog.getNewGoalHours(), selectedmeditatable));
                    populatetable();
                }
            }
        }
        public void viewcompletedgoals(ActionEvent actionEvent) {
            if (selectedmeditatable != null) {

            }
        }

        class GoalProgressBinding {
            private StringProperty name;
            private StringProperty practicedtime;
            private StringProperty currentgoaltime;
            private StringProperty percentcompleted;
            private StringProperty numbergoalscompleted;

            public GoalProgressBinding(String name, String practicedtime, String currentgoaltime, String percentcompleted, int numbergoalscompleted) {
                this.name = new SimpleStringProperty(name);
                this.practicedtime = new SimpleStringProperty(practicedtime);
                this.currentgoaltime = new SimpleStringProperty(currentgoaltime);
                this.percentcompleted = new SimpleStringProperty(percentcompleted);
                this.numbergoalscompleted = new SimpleStringProperty(String.valueOf(numbergoalscompleted));
            }
        }
    }
    public static class SimpleGoalSetDialog extends Stage {
        public MainController Root;
        public Label TopLabel;
        public Spinner<Integer> HoursSpinner;
        public Spinner<Integer> MinutesSpinner;
        public TextField DecimalHoursTextField;
        public Label StatusBar;
        public Button AcceptButton;
        public Button CancelButton;
        private Meditatable SelectedMeditatable;
        private boolean setgoal = false;
        private int practicedminutes;

        public SimpleGoalSetDialog(MainController root, Meditatable selectedmeditatable) {
            try {
                Root = root;
                SelectedMeditatable = selectedmeditatable;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SetGoalDialog_Simple.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                setTitle("Set A New Goal");
                HoursSpinner.valueProperty().addListener((observable, oldValue, newValue) -> checkvalue());
                MinutesSpinner.valueProperty().addListener((observable, oldValue, newValue) -> checkvalue());
                HoursSpinner.getValueFactory().setValue(SelectedMeditatable.getTotalMinutesPracticed(false) / 60);
                MinutesSpinner.getValueFactory().setValue(SelectedMeditatable.getTotalMinutesPracticed(false) % 60);
                Util.custom_spinner_integer(HoursSpinner, 0, Integer.MAX_VALUE, 1, false);
                Util.custom_spinner_integer(MinutesSpinner, 0, 59, 1, false);
                practicedminutes = SelectedMeditatable.getTotalMinutesPracticed(false);
                DecimalHoursTextField.setText(Util.convert_minstodecimalhours(practicedminutes, 1).toString());
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
        }

        public boolean shouldSetgoal() {
            return setgoal;
        }
        public void checkvalue() {
            try {
                int value = (HoursSpinner.getValue() * 60) + MinutesSpinner.getValue();
                if (value < practicedminutes) {
                    HoursSpinner.getValueFactory().setValue(practicedminutes / 60);
                    MinutesSpinner.getValueFactory().setValue(practicedminutes % 60);
                    StatusBar.setText("Cannot Set Goal Lower Than Practiced Hours");
                } else {StatusBar.setText("");}
                DecimalHoursTextField.setText(Util.convert_minstodecimalhours((HoursSpinner.getValue() * 60) + MinutesSpinner.getValue(), 1).toString());
            } catch (NullPointerException ignored) {}
        }
        public Double getNewGoalHours() {
            try {
                return Util.convert_minstodecimalhours((HoursSpinner.getValue() * 60) + MinutesSpinner.getValue(), 2);
            } catch (NullPointerException e) {return null;}
        }
        public void accept(ActionEvent actionEvent) {
            if (((HoursSpinner.getValue() * 60) + MinutesSpinner.getValue()) <= practicedminutes) {
                Util.gui_showinformationdialog(Root, "Cannot Accept", "Goal Is Less Than Or Equal To Practiced Minutes", "Goal Must Be Greater Than Practiced Minutes");
                return;
            }
            setgoal = true;
        }

    }
    public static class GoalPacingDialog extends Stage {
        public Spinner<Integer> PracticeDays;
        public TextField PracticeTimeADay;
        public TextField GoalDuration;
        public Button CloseButton;
        public TextField GoalTimeLeft;
        public TextField TotalPracticedTime;
        public Label TopLabel;
        private MainController Root;
        private Double practicedhours;
        private Double goalhours;
        private Double hoursleft;

        public GoalPacingDialog(MainController root, Meditatable meditatable) {
            try {
                Root = root;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/GoalPacingDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                setTitle("Goal Pacing");
                practicedhours = (double) (meditatable.getTotalMinutesPracticed(false) / 60);
                goalhours = meditatable.getCurrentGoal().getGoal_Hours();
                GoalDuration.setText(Util.formatdurationtoStringSpelledOut(new Duration(goalhours * 3_600_000), GoalDuration.getLayoutBounds().getWidth()));
                TotalPracticedTime.setText(Util.formatdurationtoStringSpelledOut(new Duration(practicedhours * 3600000), TotalPracticedTime.getLayoutBounds().getWidth()));
                hoursleft = goalhours - practicedhours;
                GoalTimeLeft.setText(Util.formatdurationtoStringSpelledOut(new Duration(hoursleft * 3600000), GoalTimeLeft.getLayoutBounds().getWidth()));
                Util.custom_spinner_integer(PracticeDays, 1, Integer.MAX_VALUE, 1, false);
                PracticeDays.valueProperty().addListener((observable, oldValue, newValue) -> calculate());
                PracticeDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
                TopLabel.setText("Goal Pacing For " + meditatable.name + " Current Goal");
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
        }

    // Other Methods
        public void calculate() {
            Double days = (double) PracticeDays.getValue();
            Float hourstopractice = hoursleft.floatValue() / days.floatValue();
            int minsaday = Util.convert_decimalhourstominutes(hourstopractice.doubleValue());
            PracticeTimeADay.setText(Util.formatdurationtoStringSpelledOut(new Duration(minsaday * 1000), PracticeTimeADay.getLayoutBounds().getWidth()));
        }
    }
    public static class AllSessionsDetails extends Stage {
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
            // Selected Meditatables
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
        private MainController Root;

        public AllSessionsDetails(MainController root) {
            Root = root;
            allsessionslist = Root.getProgressTracker().getSessions().getSession();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionDetails_All.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
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
        }

        public CheckBox getcheckbox(int meditatableindex) {
            switch (meditatableindex) {
                case 0: return Filter_PresessionCheckbox;
                case 1: return Filter_RinCheckbox;
                case 2: return Filter_KyoCheckbox;
                case 3: return Filter_TohCheckbox;
                case 4: return Filter_ShaCheckbox;
                case 5: return Filter_KaiCheckbox;
                case 6: return Filter_JinCheckbox;
                case 7: return Filter_RetsuCheckbox;
                case 8: return Filter_ZaiCheckbox;
                case 9: return Filter_ZenCheckbox;
                case 10: return Filter_EarthCheckbox;
                case 11: return Filter_AirCheckbox;
                case 12: return Filter_FireCheckbox;
                case 13: return Filter_WaterCheckbox;
                case 14: return Filter_VoidCheckbox;
                case 15: return Filter_PostsessionCheckbox;
                default: return null;
            }
        }

        public void populatetotalsbargraphandtable() {
            ObservableList<TotalProgressRow> totalprogressrows = FXCollections.observableArrayList();
            ObservableList<PieChart.Data> piecesofthepie = FXCollections.observableArrayList();
            XYChart.Series<String, java.lang.Number> series = new XYChart.Series<>();
            for (Meditatable i : Root.getSession().getAllMeditatablesincludingTotalforTracking()) {
                if (! (i instanceof Total)) {
                    series.getData().add(new XYChart.Data<>(i.getNameForChart(), Util.convert_minstodecimalhours(i.getTotalMinutesPracticed(false), 1)));
                    piecesofthepie.add(new PieChart.Data(i.getNameForChart(), Util.convert_minstodecimalhours(i.getTotalMinutesPracticed(false), 1)));
                }
                totalprogressrows.add(new TotalProgressRow(i.getNameForChart(), Util.formatdurationtoStringDecimalWithColons(new Duration(i.getTotalMinutesPracticed(false) * 60000))));
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
                    LocalDate sessiondate = Util.convert_stringtolocaldate(i.getDate_Practiced());
                    if (Filter_DateRange_From.getValue() != null) {if (sessiondate.isBefore(Filter_DateRange_From.getValue())) {continue;}}
                    if (Filter_DateRange_To.getValue() != null) {if (sessiondate.isAfter(Filter_DateRange_To.getValue())) {continue;}}
                }
                if (FilterBySelectedSwitch.isSelected()) {
                    boolean validsession = true;
                    for (int j = 0; j < 16; j++) {
                        if (! validsession) {break;}
                        if (getcheckbox(j).isSelected()) {
                            if (Filter_DurationThresholdCheckbox.isSelected()) {
                                try {
                                    if (i.getcutduration(j) <= Integer.parseInt(Filter_ThresholdMinutesTextField.getText())) {validsession = false;}
                                } catch (NumberFormatException | NullPointerException ignored) {validsession = i.getcutduration(j) > 0;}
                            } else {if (i.getcutduration(j) == 0) {validsession = false;}}
                        }
                    }
                    if (! validsession) {continue;}
                }
                rowlist.add(new SessionRow(count, i.getDate_Practiced(), i.getPresession_Duration(), i.getRin_Duration(),
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
            int index = sessionsTableView.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                new MainController.SessionDetails(Root, filteredsessionlist.get(index)).showAndWait();
            }
        }
        public void filterbydateselected(ActionEvent actionEvent) {
            if (! FilterByDateSwitch.isSelected()) {
                Filter_DateRange_From.setValue(null);
                Filter_DateRange_To.setValue(null);
            } else {
                try {
                    Filter_DateRange_From.setValue(Util.convert_stringtolocaldate(allsessionslist.get(0).getDate_Practiced()));
                } catch (NullPointerException | IndexOutOfBoundsException ignored) {
                    Filter_DateRange_From.setValue(LocalDate.now());
                }
                Filter_DateRange_To.setValue(LocalDate.now());
            }
        }

        public class SessionRow {
            public IntegerProperty id;
            public StringProperty datepracticed;
            public StringProperty presession;
            public StringProperty rin;
            public StringProperty kyo;
            public StringProperty toh;
            public StringProperty sha;
            public StringProperty kai;
            public StringProperty jin;
            public StringProperty retsu;
            public StringProperty zai;
            public StringProperty zen;
            public StringProperty earth;
            public StringProperty air;
            public StringProperty fire;
            public StringProperty water;
            public StringProperty Void;
            public StringProperty postsession;
            public StringProperty total;

            public SessionRow(int id, String datepracticed, int presession, int rin, int kyo, int toh, int sha, int kai, int jin, int retsu,
                              int zai, int zen, int postsession, int earth, int air, int fire, int water, int Void, int total) {
                this.id = new SimpleIntegerProperty(id);
                this.datepracticed = new SimpleStringProperty(datepracticed);
                this.presession = new SimpleStringProperty(String.valueOf(presession));
                this.rin = new SimpleStringProperty(String.valueOf(rin));
                this.kyo = new SimpleStringProperty(String.valueOf(kyo));
                this.toh = new SimpleStringProperty(String.valueOf(toh));
                this.sha = new SimpleStringProperty(String.valueOf(sha));
                this.kai = new SimpleStringProperty(String.valueOf(kai));
                this.jin = new SimpleStringProperty(String.valueOf(jin));
                this.retsu = new SimpleStringProperty(String.valueOf(retsu));
                this.zai = new SimpleStringProperty(String.valueOf(zai));
                this.zen = new SimpleStringProperty(String.valueOf(zen));
                this.earth = new SimpleStringProperty(String.valueOf(earth));
                this.air = new SimpleStringProperty(String.valueOf(air));
                this.fire = new SimpleStringProperty(String.valueOf(fire));
                this.water = new SimpleStringProperty(String.valueOf(water));
                this.Void = new SimpleStringProperty(String.valueOf(Void));
                this.postsession = new SimpleStringProperty(String.valueOf(postsession));
                this.total = new SimpleStringProperty(Util.formatdurationtoStringDecimalWithColons(new Duration(total * 1000)));
            }

            public String toString() {
                return String.format("%s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s",
                        id.getValue(), datepracticed.getValue(), presession.getValue(), rin.getValue(), kyo.getValue(),
                        toh.getValue(), sha.getValue(), kai.getValue(), jin.getValue(), retsu.getValue(), zai.getValue(),
                        zen.getValue(), postsession.getValue(), total.getValue());
            }

            public StringProperty getDatepracticed() {
                return datepracticed;
            }
        }
        public class TotalProgressRow {
            private StringProperty name;
            private StringProperty formattedduration;

            public TotalProgressRow(String name, String formattedduration) {
                this.name = new SimpleStringProperty(name);
                this.formattedduration = new SimpleStringProperty(formattedduration);
            }
        }
    }

}