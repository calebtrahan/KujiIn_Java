package kujiin.ui;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import kujiin.MainController;
import kujiin.util.Meditatable;
import kujiin.util.Total;
import kujiin.util.Util;
import kujiin.xml.Goals;
import kujiin.xml.Session;
import kujiin.xml.Sessions;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

// TODO Finish Making EditGoalsDialog (Also Add A Select A Different Cut Feature, And Make It So They Can Pass Cutindex Data Back And Forth)
// TODO !IMPORTANT Fix Goal Multiple Goal Setter (Minutes Was Stuck At 30)

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
        CutSelectorComboBox.setOnAction(this::cutselectionchanged);
        PreAndPostOption.setOnAction(this::cutselectionchanged);
    }

// Getters And Setters
    public Sessions getSessions() {return Sessions;}
    public Goals getGoal()        {
        return Goals;
    }

// Button Actions
    public void cutselectionchanged(ActionEvent actionEvent) {
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
    public void displaydetailedcutprogress() {
//        if (Sessions.getSession() != null) {new DisplayCutTotalsDialog(Root, Sessions.getSession());}
//        else {
//            Util.gui_showinformationdialog(Root, "Cannot Display", "Nothing To Display", "Need To Practice At Least One Session To Use This Feature");}
    }
    public void displaysessionlist() {
        if (Sessions.getSession() == null || Sessions.getSession().size() == 0) {
            Util.gui_showinformationdialog(Root, "No Sessions", "No Practiced Sessions", "Cannot View Sessions");
        } else {new AllSessionsDetails(Root).showAndWait();}
    }
    public void setnewgoal() {
        if (SelectedMeditatable.getCurrentGoal() != null) {
            Util.gui_showinformationdialog(Root, "Current Goal Already Set", "Current Goal Already Set", "Cannot Set New Goal Until Current Goal Is Completed");
        } else {
            new SetANewGoalForSingleCut(Root, SelectedMeditatable).showAndWait();
            updategoalsui();
        }
    }
    public void opengoaleditor() {
        if (SelectedMeditatable.getAllGoals() == null || SelectedMeditatable.getAllGoals().size() == 0) {
            Util.gui_showinformationdialog(Root, "Information", "No Goals Exist For " + SelectedMeditatable.name, "Please Add A Goal For " + SelectedMeditatable.name);
        } else {new EditGoalsDialog(Root, SelectedMeditatable).showAndWait();}
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
                averagesessiondurationtext = Util.format_minstohrsandmins_short(averagesessionduration.intValue());
                String test = Util.format_minstohrsandmins_short(totalminutespracticed);
                if (test.toCharArray().length <= 14) {totalminutespracticedtext = test;}
                else {totalminutespracticedtext = Util.format_minstohrsandmins_abbreviated(totalminutespracticed);}
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
        } else if (SelectedMeditatable.getCurrentGoal() == null || SelectedMeditatable.getTotalMinutesPracticed(false) == 0) {
            System.out.println(SelectedMeditatable.getCurrentGoal());
            // No Current Goal Set
            toptext = "No Current Goal";
            percentage = "";
            progress = 0.0;
            goalprogresstooltip = new Tooltip("No Current Goal Set For " + SelectedMeditatable.name);
        } else {
            toptext = "Current Goal Progress";
            Double goalminutes = SelectedMeditatable.getCurrentGoal().getGoal_Hours() * 60;
            progress = Util.convert_minstodecimalhours(SelectedMeditatable.getTotalMinutesPracticed(false), 2) / (goalminutes / 60);
            goalprogresstooltip = new Tooltip(String.format("Currently Practiced: %s -> Goal: %s", Util.format_minstohrsandmins_long(SelectedMeditatable.getTotalMinutesPracticed(false)), Util.format_minstohrsandmins_long(goalminutes.intValue())));
            percentage = new Double(progress * 100).intValue() + "%";
            // Populate Goal
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
    public static class DisplayCutTotalsDialog extends Stage {
//        public TableView<TotalProgressRow> progresstable;
//        public TableColumn<TotalProgressRow, String> NameColumn;
//        public TableColumn<TotalProgressRow, String> ProgressColumn;
//        public TableColumn<TotalProgressRow, Integer> NumberColumn;
        private MainController Root;
        private List<Session> allsessions;

//        public DisplayCutTotalsDialog(MainController root, List<Session> allsessions) {
//            Root = root;
//            this.allsessions = allsessions;
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayCutTotalsDialog.fxml"));
//            fxmlLoader.setController(this);
//            try {
//                Scene defaultscene = new Scene(fxmlLoader.load());
//                setScene(defaultscene);
//                Root.getOptions().setStyle(this);
//                this.setResizable(false);
//            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
//            setTitle("Cut Totals");
//            NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
//            NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
//            ProgressColumn.setCellValueFactory(cellData -> cellData.getValue().formattedduration);
//            populatetable();
//        }
//
//        public void populatetable() {
//            ArrayList<TotalProgressRow> allrows = new ArrayList<>();
//            for (int i = 1; i < 10; i++) {
//                int durationinmins = 0;
//                for (Session x : allsessions) {durationinmins += x.getcutduration(i);}
//                String duration;
//                if (durationinmins > 0) {duration = Util.format_minstohrsandmins_short(durationinmins);}
//                else {duration = "-";}
//                allrows.add(new TotalProgressRow(i, Options.ALLNAMES.get(i), duration));
//            }
//            progresstable.getItems().addAll(allrows);
//        }
    }
    public static class EditGoalsDialog extends Stage {
        public TableView<CurrentGoalBinding> CurrentGoalTable;
        public TableColumn<CurrentGoalBinding, Integer> NumberColumn;
        public TableColumn<CurrentGoalBinding, String> GoalTimeColumn;
        public TableColumn<CurrentGoalBinding, String> DueDateColumn;
        public TableColumn<CurrentGoalBinding, String> PercentCompleteColumn;
        public Button CloseButton;
        public TableColumn<CurrentGoalBinding, Boolean> IsCompletedColumn;
        public TableColumn<CurrentGoalBinding, String> CompletionDateColumn;
        public CheckBox ShowCompletedCheckBox;
        public Button AddGoalButton;
        public Button RemoveGoalButton;
        public ChoiceBox<String> CutSelectorComboBox;
        public Button GoalPacingButton;
        private MainController Root;
        private ProgressAndGoalsUI ProgressAndGoals;
        private List<kujiin.xml.Goals.Goal> CurrentGoalList;
        private kujiin.xml.Goals.Goal SelectedGoal;
        private Meditatable SelectedMeditatable;

        public EditGoalsDialog(MainController root, Meditatable meditatable) {
            Root = root;
            SelectedMeditatable = meditatable;
            ProgressAndGoals = Root.getProgressTracker();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayGoals.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("View/Edit " + meditatable.name + "'s Goals");
            ObservableList<String> allnames = FXCollections.observableArrayList();
            allnames.addAll(Root.getSession().getAllMeditatablesincludingTotalforTracking().stream().map(i -> i.name).collect(Collectors.toList()));
            CutSelectorComboBox.setItems(allnames);
            populatetable();
            CutSelectorComboBox.getSelectionModel().select(SelectedMeditatable.number);
            CutSelectorComboBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.intValue() == -1) {return;}
                if (oldValue != null && goalschanged()) {
                    switch (Util.gui_getyesnocancelconfirmationdialog(Root, "Confirmation", "You Have Made Unsaved Changes To " + SelectedMeditatable.number, "Save These Changes Before Changing Cuts?")) {
                        case YES:
                            savechanges();
                            break;
                        case NO:
                            break;
                        case CANCEL:
                            CutSelectorComboBox.getSelectionModel().select(oldValue.intValue());
                            return;
                    }
                }
                ProgressAndGoals.selectmeditatable(SelectedMeditatable);
                CurrentGoalTable.getItems().clear();
                RemoveGoalButton.setDisable(true);
                GoalPacingButton.setDisable(true);
                populatetable();
            });
            setOnCloseRequest(event -> {
                if (SelectedMeditatable != null && goalschanged()) {
                    switch (Util.gui_getyesnocancelconfirmationdialog(Root, "Confirmation", "Unsaved Changes To " + SelectedMeditatable.name, "Save Before Exiting")) {
                        case YES:
                            savechanges();
                            break;
                        case NO:
                            break;
                        case CANCEL:
                            event.consume();
                    }
                }
            });
        }

    // Getters And Setters

    // Cut Selection Methods
        public boolean goalschanged() {
            try {
                List<kujiin.xml.Goals.Goal> goalsfromxml = SelectedMeditatable.getAllGoals();
                return ! CurrentGoalList.containsAll(goalsfromxml) ||  goalsfromxml.size() != CurrentGoalList.size();
            } catch (NullPointerException ignored) {return false;}
        }

    // Table Methods
        public void populatetable() {
            try {
                ObservableList<CurrentGoalBinding> currentGoals = FXCollections.observableArrayList();
                CurrentGoalList = new ArrayList<>();
                setTitle("View/Edit " + SelectedMeditatable.name + "'s Goals");
                int count = 1;
                for (kujiin.xml.Goals.Goal i : SelectedMeditatable.getAllGoals()) {
                    currentGoals.add(new CurrentGoalBinding(count, Double.toString(i.getGoal_Hours()), i.getDate_Set(),
                            i.getpercentagecompleted(SelectedMeditatable.getTotalMinutesPracticed(false)),
                            i.getCompleted(), i.getDate_Completed()));
                    CurrentGoalList.add(i);
                    count++;
                }
                NumberColumn.setCellValueFactory(cellData -> cellData.getValue().goalid.asObject());
                NumberColumn.setStyle("-fx-alignment: CENTER;");
                GoalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().goalhours);
                GoalTimeColumn.setStyle("-fx-alignment: CENTER;");
                DueDateColumn.setCellValueFactory(cellData -> cellData.getValue().duedate);
                DueDateColumn.setStyle("-fx-alignment: CENTER;");
                PercentCompleteColumn.setCellValueFactory(cellData -> cellData.getValue().percentcomplete);
                PercentCompleteColumn.setStyle("-fx-alignment: CENTER;");
                IsCompletedColumn.setCellValueFactory(cellData -> cellData.getValue().completed);
                IsCompletedColumn.setStyle("-fx-alignment: CENTER;");
                CompletionDateColumn.setCellValueFactory(cellData -> cellData.getValue().datecompleted);
                CompletionDateColumn.setStyle("-fx-alignment: CENTER;");
                CurrentGoalTable.setItems(currentGoals);
            } catch (NullPointerException | IndexOutOfBoundsException ignored) {reset();}
        }
        public void tableselectionchanged(Event event) {
            int index = CurrentGoalTable.getSelectionModel().getSelectedIndex();
            if (index != -1) {SelectedGoal = CurrentGoalList.get(index);}
            RemoveGoalButton.setDisable(index == -1);
            GoalPacingButton.setDisable(index == -1 || SelectedGoal == null || SelectedGoal.getCompleted());
        }
        public void completedgoalstoggle(ActionEvent actionEvent) {
            populatetable();
        }
        public void addgoal(ActionEvent actionEvent) {
            ProgressAndGoals.setnewgoal();
            populatetable();}
        public void removegoal(ActionEvent actionEvent) {
            if (SelectedGoal == null) {return;}
            if (! SelectedGoal.getCompleted() && Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "Remove This Goal?", "This Cannot Be Undone")) {
                CurrentGoalList.remove(CurrentGoalTable.getSelectionModel().getSelectedIndex());
            }
        }
        public void savechanges() {
            if (SelectedMeditatable != null) {SelectedMeditatable.updateGoals(CurrentGoalList);}
        }

    // Dialog Methods
        public void closeDialog(Event event) {this.close();}
        public void reset() {
            CurrentGoalTable.getItems().clear();
        }

    // Goal pacing
        public void goalpacing(ActionEvent actionEvent) {
            if (SelectedGoal != null && CurrentGoalList != null && SelectedMeditatable != null) {
                new GoalPacingDialog(Root, SelectedGoal, CurrentGoalList, SelectedMeditatable).showAndWait();
            }
        }

        public void changecutselection(ActionEvent actionEvent) {}
    }
    public static class GoalPacingDialog extends Stage {
        public Spinner<Integer> PracticeDays;
        public TextField PracticeTimeADay;
        public TextField GoalDuration;
        public TextField GoalDueDate;
        public TextField GoalDaysTillDue;
        public Button ExtendDueDateButton;
        public Button CloseButton;
        public TextField GoalTimeLeft;
        public TextField PracticeTimeDaysAWeek;
        public TextField TotalPracticedTime;
        private MainController Root;
        private Goals.Goal CurrentGoal;
        private List<Goals.Goal> CurrentGoals;
        private Meditatable SelectedMeditatable;

        public GoalPacingDialog(MainController root, kujiin.xml.Goals.Goal currentGoal, List<kujiin.xml.Goals.Goal> currentGoals, Meditatable meditatable) {
            Root = root;
            SelectedMeditatable = meditatable;
            CurrentGoal = currentGoal;
            CurrentGoals = currentGoals;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/GoalPacingDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Goal Pacing");
            int practicedminutes = meditatable.getTotalMinutesPracticed(false);
            int goalminutes = Util.convertdecimalhourstominutes(currentGoal.getGoal_Hours());
            GoalDuration.setText(Util.format_minstohrsandmins_abbreviated(goalminutes));
            GoalDueDate.setText(CurrentGoal.getDate_Due());
            TotalPracticedTime.setText(Util.format_minstohrsandmins_abbreviated(practicedminutes));
            int minutesleft = goalminutes - practicedminutes;
            GoalTimeLeft.setText(Util.format_minstohrsandmins_abbreviated(minutesleft));
            LocalDate datedue = Util.convert_stringtolocaldate(CurrentGoal.getDate_Due());
            int daystilldue = Period.between(LocalDate.now(), datedue).getDays();
            System.out.println("Days Till Due " + daystilldue);
            if (daystilldue >= 0) {
                GoalDaysTillDue.setText(String.format("%s Days", daystilldue));
                PracticeDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, daystilldue, daystilldue));
                Util.custom_spinner_integer(PracticeDays, 1, daystilldue, 1, false);
                calculate();
                PracticeDays.valueProperty().addListener((observable, oldValue, newValue) -> {
                    calculate();
                });
            } else {
                Util.gui_showinformationdialog(Root, "Goal Is Overdue", "Cannot Calculate Goal Pacing For A Goal That Is Past Due", "Set A New Due Date To Use This Feature");
                if (! extendduedate(null)) {close();}
                else {
                    datedue = Util.convert_stringtolocaldate(CurrentGoal.getDate_Due());
                    daystilldue = Period.between(LocalDate.now(), datedue).getDays();
                    GoalDaysTillDue.setText(String.format("%s Days", daystilldue));
                    PracticeDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, daystilldue, daystilldue));
                    calculate();
                    PracticeDays.valueProperty().addListener((observable, oldValue, newValue) -> {
                        calculate();
                    });
                }
            }
        }

    // Button Actions
        public void closedialog(ActionEvent actionEvent) {close();}
        public boolean extendduedate(ActionEvent actionEvent) {
            DatePickerDialog dpd = new DatePickerDialog(Root, "Select A New Due Date", "Select A New Due Date", LocalDate.now());
            dpd.showAndWait();
            if (dpd.getDate() != null) {
                if (Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "This Will Postpone This Goal's Due Date To " + dpd.getDate().toString(), "Really Postpone?")) {
                    CurrentGoal.setDate_Due(Util.convert_localdatetostring(dpd.getDate()));
                    return true;
                } else {
                    Util.gui_showinformationdialog(Root, "Information", "Extend Due Date Cancelled", "This Goal's Due Date Was Not Extended"); return false;}
            } else {return false;}
        }

    // Other Methods
        public void selectanewgoal(ActionEvent actionEvent) {
//            SelectGoalDialog selectGoalDialog = new SelectGoalDialog(Root, currentGoals, alreadypracticedhours);
//            selectGoalDialog.showAndWait();
//            setCurrentGoal(selectGoalDialog.getSelectedgoal());
//            if (getCurrentGoal() == null) {return;}
//            SelectedGoalHours.setText(getCurrentGoal().getGoal_Hours() + " Hours");
        }
        public void calculate() {
            System.out.println("Calculating");
            Double goalhours = CurrentGoal.getGoal_Hours();
            Double days = (double) PracticeDays.getValue();
            Float hourstopractice = goalhours.floatValue() / days.floatValue();
            int minsaday = Util.convertdecimalhourstominutes(hourstopractice.doubleValue());
            String formattedgoalhours = Util.format_minstohrsandmins_abbreviated(minsaday);
            PracticeTimeADay.setText(formattedgoalhours);
        }
    }
    public static class SelectGoalDialog extends Stage {
        public TableView<CurrentGoalBinding> currentgoaltable;
        public TableColumn<CurrentGoalBinding, Integer> NumberColumn;
        public TableColumn<CurrentGoalBinding, String> GoalTimeColumn;
        public TableColumn<CurrentGoalBinding, String> DueDateColumn;
        public TableColumn<CurrentGoalBinding, String> PercentCompleteColumn;
        public Button SelectButton;
        public Button CancelButton;
        public kujiin.xml.Goals.Goal selectedgoal;
        private MainController Root;
        private List<kujiin.xml.Goals.Goal> currentGoalList;

        public SelectGoalDialog(MainController root, List<kujiin.xml.Goals.Goal> currentGoalList, double currentpracticedhours) {
            Root = root;
            this.currentGoalList = currentGoalList;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SelectGoalDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Select Goal");
            ObservableList<CurrentGoalBinding> currentGoals = FXCollections.observableArrayList();
//            currentGoals.addAll(currentGoalList.stream().map(i -> new CurrentGoalBinding(currentGoalList.indexOf(i) + 1, Double.toString(i.getGoal_Hours()), i.getDate_Set(), i.getpercentagecompleted(currentpracticedhours))).collect(Collectors.toList()));
            NumberColumn.setCellValueFactory(cellData -> cellData.getValue().goalid.asObject());
            NumberColumn.setStyle("-fx-alignment: CENTER;");
            GoalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().goalhours);
            GoalTimeColumn.setStyle("-fx-alignment: CENTER;");
            DueDateColumn.setCellValueFactory(cellData -> cellData.getValue().duedate);
            DueDateColumn.setStyle("-fx-alignment: CENTER;");
            PercentCompleteColumn.setCellValueFactory(cellData -> cellData.getValue().percentcomplete);
            PercentCompleteColumn.setStyle("-fx-alignment: CENTER;");
            currentgoaltable.setItems(currentGoals);
        }

        // Getters And Setters
        public kujiin.xml.Goals.Goal getSelectedgoal() {
            return selectedgoal;
        }
        public void setSelectedgoal(kujiin.xml.Goals.Goal selectedgoal) {
            this.selectedgoal = selectedgoal;
        }

        // Button Actions
        public void closeDialog(ActionEvent actionEvent) {close();}
        public void selectgoal(ActionEvent actionEvent) {
            int index = currentgoaltable.getSelectionModel().getSelectedIndex();
            if (index == -1) {
                Util.gui_showinformationdialog(Root, "Information", "No Goal Selected", "Select A Goal"); return;}
            setSelectedgoal(currentGoalList.get(index));
            close();
        }
    }
    public static class SetANewGoalForSingleCut extends Stage {
        public Label StatusBar;
        public ChoiceBox<String> MeditatableChoiceBox;
        private ProgressAndGoalsUI progressAndGoalsUI;
        public Spinner<Integer> GoalHoursSpinner;
        public Spinner<Integer> GoalMinutesSpinner;
        public Button CancelButton;
        public Button SetGoalButton;
        public Button EditGoalsButton;
        private MainController Root;
        private int practicedminutes;
        private int goalminutes;
        private Meditatable selectedmeditatable;

        public SetANewGoalForSingleCut(MainController root, Meditatable selectedMeditatable) {
            Root = root;
            progressAndGoalsUI = Root.getProgressTracker();
            selectedmeditatable = selectedMeditatable;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SetNewGoalDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Set A New Goal");
            GoalHoursSpinner.setEditable(true);
            GoalMinutesSpinner.setEditable(true);
            MeditatableChoiceBox.getItems().addAll(FXCollections.observableArrayList(Root.getSession().getAllMeditablesincludingTotalNames()));
            MeditatableChoiceBox.setOnAction(event -> meditatableselected());
            GoalHoursSpinner.valueProperty().addListener((observable, oldValue, newValue) -> checkvalue());
            GoalMinutesSpinner.valueProperty().addListener((observable, oldValue, newValue) -> checkvalue());
            MeditatableChoiceBox.getSelectionModel().select(selectedmeditatable.number);
            meditatableselected();
            Util.custom_spinner_integer(GoalHoursSpinner, 0, Integer.MAX_VALUE, 1, false);
            Util.custom_spinner_integer(GoalMinutesSpinner, 0, 59, 1, false);
        }

        // Button Actions
        public void setGoal(Event event) {
            int thisminutes = (GoalHoursSpinner.getValue() * 60) + GoalMinutesSpinner.getValue();
            if (thisminutes <= practicedminutes) {
                Util.gui_showinformationdialog(Root, "Cannot Set Goal", "Goal Time Must Be Higher Than Practiced Time " + Util.format_minstohrsandmins_long(practicedminutes), "Cannot Set Goal");
                return;
            }
            double newhours = Util.convert_hrsandminstodecimalhours(GoalHoursSpinner.getValue(), GoalMinutesSpinner.getValue());
            selectedmeditatable.addGoal(new Goals.Goal(newhours, selectedmeditatable));
            Util.gui_showtimedmessageonlabel(StatusBar, "Success! Goal Of " + newhours + " hrs Added As " + selectedmeditatable.name + "'s Current Goal", 2000);
        }
        public void editgoals(Event event) {
            progressAndGoalsUI.opengoaleditor();
        }

        // Other Methods
        public void meditatableselected() {
            try {
                selectedmeditatable = Root.getSession().getAllMeditatablesincludingTotalforTracking().get(MeditatableChoiceBox.getSelectionModel().getSelectedIndex());
                GoalHoursSpinner.setDisable(false);
                GoalMinutesSpinner.setDisable(false);
            } catch (Exception ignored) {
                try {GoalHoursSpinner.getValueFactory().setValue(0);} catch (NullPointerException e) {}
                GoalHoursSpinner.setDisable(true);
                try {GoalMinutesSpinner.getValueFactory().setValue(0);} catch (NullPointerException e) {}
                GoalMinutesSpinner.setDisable(true);
                return;
            }
            practicedminutes = selectedmeditatable.getTotalMinutesPracticed(false);
            if (selectedmeditatable.getCurrentGoal() == null) {
                goalminutes = practicedminutes;
                int hr = practicedminutes / 60;
                int min = practicedminutes % 60;
                GoalHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(hr, Integer.MAX_VALUE, hr));
                GoalMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, min));
                StatusBar.setText("");
            } else {
                goalminutes = Util.convertdecimalhourstominutes(selectedmeditatable.getCurrentGoal().getGoal_Hours());
                GoalHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(goalminutes / 60, Integer.MAX_VALUE, goalminutes / 60));
                GoalMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, goalminutes % 60));
                StatusBar.setText("Cannot Set A New Goal Until Current Goal Is Completed");
            }
            SetGoalButton.setDisable(selectedmeditatable.getCurrentGoal() != null && ! validvalue());
            GoalMinutesSpinner.setDisable(selectedmeditatable.getCurrentGoal() != null);
            GoalHoursSpinner.setDisable(selectedmeditatable.getCurrentGoal() != null);
            EditGoalsButton.setDisable(selectedmeditatable.getAllGoals() == null);
        }
        public boolean validvalue() {
            return (GoalHoursSpinner.getValue() * 60) + GoalMinutesSpinner.getValue() > practicedminutes;
        }
        public void checkvalue() {
            try {
                int value = (GoalHoursSpinner.getValue() * 60) + GoalMinutesSpinner.getValue();
                if (value < practicedminutes) {
                    GoalHoursSpinner.getValueFactory().setValue(practicedminutes / 60);
                    GoalMinutesSpinner.getValueFactory().setValue(practicedminutes % 60);
                    Util.gui_showtimedmessageonlabel(StatusBar, "Cannot Set Goal Lower Than Practiced Hours", 2000);
                }
            } catch (NullPointerException ignored) {}
        }
    }
    public static class SetANewGoalForMultipleCutsOrElements extends Stage {
        public ToggleButton Presession;
        public ToggleButton RIN;
        public ToggleButton KYO;
        public ToggleButton TOH;
        public ToggleButton SHA;
        public ToggleButton KAI;
        public ToggleButton JIN;
        public ToggleButton RETSU;
        public ToggleButton ZAI;
        public ToggleButton ZEN;
        public ToggleButton Postsession;
        public ToggleButton Earth;
        public ToggleButton Air;
        public ToggleButton Fire;
        public ToggleButton Water;
        public ToggleButton Void;
        public Button SelectAllCutsButton;
        public Button SelectAllElementsButton;
        public Button UnselectAllButton;
        private MainController Root;
        public Spinner<Integer> GoalHoursSpinner;
        public Spinner<Integer> GoalMinutesSpinner;
        public DatePicker GoalDatePicker;
        public Button CancelButton;
        public Button OKButton;
        public Label TopLabel;
        private LocalDate goaldate;
        private Double goalhours;
        private ArrayList<Meditatable> cutsorlementstosetgoalsfor;
        private Integer lowestgoalminutes;

        public SetANewGoalForMultipleCutsOrElements(MainController root, ArrayList<Meditatable> cutsorlementstosetgoalsfor) {
            try {
                Root = root;
                this.cutsorlementstosetgoalsfor = cutsorlementstosetgoalsfor;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SetMultipleGoalsDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                setTitle("Set A Goal For Multiple Cuts");
                calculatelowestgoal();
                Presession.setOnAction(event -> calculatelowestgoal());
                RIN.setOnAction(event -> calculatelowestgoal());
                KYO.setOnAction(event -> calculatelowestgoal());
                TOH.setOnAction(event -> calculatelowestgoal());
                SHA.setOnAction(event -> calculatelowestgoal());
                KAI.setOnAction(event -> calculatelowestgoal());
                JIN.setOnAction(event -> calculatelowestgoal());
                RETSU.setOnAction(event -> calculatelowestgoal());
                ZAI.setOnAction(event -> calculatelowestgoal());
                ZEN.setOnAction(event -> calculatelowestgoal());
                Earth.setOnAction(event -> calculatelowestgoal());
                Air.setOnAction(event -> calculatelowestgoal());
                Fire.setOnAction(event -> calculatelowestgoal());
                Water.setOnAction(event -> calculatelowestgoal());
                Void.setOnAction(event -> calculatelowestgoal());
                Postsession.setOnAction(event -> calculatelowestgoal());
                GoalHoursSpinner.setEditable(true);
                GoalHoursSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                    int value = (newValue * 60) + GoalMinutesSpinner.getValue();
                    if (newvaluehigherthanmin(value)) {GoalHoursSpinner.getValueFactory().setValue(newValue);}
                    else {GoalHoursSpinner.getValueFactory().setValue(oldValue);}
                });
                Util.custom_spinner_integer(GoalHoursSpinner, 0, Integer.MAX_VALUE, 1, false);
                Util.custom_spinner_integer(GoalMinutesSpinner, 0, 59, 1, true);
                GoalMinutesSpinner.setEditable(true);
                GoalMinutesSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                    int value = (GoalHoursSpinner.getValue() * 60) + newValue;
                    if (newvaluehigherthanmin(value)) {GoalMinutesSpinner.getValueFactory().setValue(newValue);}
                    else {GoalMinutesSpinner.getValueFactory().setValue(oldValue);}
                });
                GoalDatePicker.setValue(LocalDate.now());
                for (Meditatable i : cutsorlementstosetgoalsfor) {
                    System.out.println(i.name); select(i.number, true);}
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
        }

    // Getters And Setters
        public LocalDate getGoaldate() {
            return goaldate;
        }
        public void setGoaldate(LocalDate goaldate) {
            this.goaldate = goaldate;
        }
        public Double getGoalhours() {
            return goalhours;
        }
        public void setGoalhours(Double goalhours) {
            this.goalhours = goalhours;
        }
        public boolean isAccepted() {return getGoalhours() != null && getGoaldate() != null && ! getSelectedCutIndexes().isEmpty();}
        public List<Integer> getSelectedCutIndexes() {
            List<Integer> selectedcuts = new ArrayList<>();
            for (int i = 0; i<=10; i++) {if (getselected(i)) {selectedcuts.add(i);}}
            return selectedcuts;
        }
        public void select(int index, boolean value) {
            if (index == 0) {Presession.setSelected(value);}
            if (index == 1) {RIN.setSelected(value);}
            if (index == 2) {KYO.setSelected(value);}
            if (index == 3) {TOH.setSelected(value);}
            if (index == 4) {SHA.setSelected(value);}
            if (index == 5) {KAI.setSelected(value);}
            if (index == 6) {JIN.setSelected(value);}
            if (index == 7) {RETSU.setSelected(value);}
            if (index == 8) {ZAI.setSelected(value);}
            if (index == 9) {ZEN.setSelected(value);}
            if (index == 10) {Earth.setSelected(value);}
            if (index == 11) {Air.setSelected(value);}
            if (index == 12) {Fire.setSelected(value);}
            if (index == 13) {Water.setSelected(value);}
            if (index == 14) {Void.setSelected(value);}
            if (index == 15) {Postsession.setSelected(value);}
        }
        public boolean getselected(int index) {
            if (index == 0) {return Presession.isSelected();}
            if (index == 1) {return RIN.isSelected();}
            if (index == 2) {return KYO.isSelected();}
            if (index == 3) {return TOH.isSelected();}
            if (index == 4) {return SHA.isSelected();}
            if (index == 5) {return KAI.isSelected();}
            if (index == 6) {return JIN.isSelected();}
            if (index == 7) {return RETSU.isSelected();}
            if (index == 8) {return ZAI.isSelected();}
            if (index == 9) {return ZEN.isSelected();}
            if (index == 10) {return ZEN.isSelected();}
            if (index == 11) {return ZEN.isSelected();}
            if (index == 12) {return ZEN.isSelected();}
            if (index == 13) {return ZEN.isSelected();}
            if (index == 14) {return ZEN.isSelected();}
            if (index == 15) {return Postsession.isSelected();}
            return false;
        }

    // Button Actions
        public void Accept(Event event) {
            if (getSelectedCutIndexes().isEmpty()) {
                Util.gui_showinformationdialog(Root, "Information", "Cannot Add Goal", "No Cuts Selected"); return;}
            if (GoalMinutesSpinner.getValue() > 59) {
                Util.gui_showinformationdialog(Root, "Information", "Minutes Cannot Be Greater Than 59", "Select A Value Less Than 59"); return;}
            boolean dategood = GoalDatePicker.getValue().isAfter(LocalDate.now());
            if (dategood) {
                int hours = GoalHoursSpinner.getValue();
                int minutes = GoalMinutesSpinner.getValue();
                double newhours = Util.convert_hrsandminstodecimalhours(hours, minutes);
                setGoalhours(newhours);
                setGoaldate(GoalDatePicker.getValue());
                super.close();
            } else {
                Util.gui_showinformationdialog(Root, "Cannot Set Goal", "Cannot Set Goal", "Due Date Must Be After Today");
                setGoalhours(null);
                setGoaldate(null);
            }
        }
        public void cancelgoalsetting(Event event) {
            this.close();
        }
        public void selectallcuts(ActionEvent actionEvent) {
            for (int i = 1; i < 10; i++) {select(i, true);}
            calculatelowestgoal();
        }
        public void selectallelements(ActionEvent actionEvent) {
            for (int i = 10; i < 15; i++) {select(i, true);}
            calculatelowestgoal();
        }
        public void unselectall(ActionEvent actionEvent) {
            for (int i = 0; i < 16; i++) {
                select(i, false);
            }
            calculatelowestgoal();
        }

    // Utility Methods
        public void calculatelowestgoal() {
            lowestgoalminutes = Root.getProgressTracker().getlowestgoalminutesforallmeditatables(cutsorlementstosetgoalsfor);
            if (lowestgoalminutes > 0) {
                GoalHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(lowestgoalminutes / 60, Integer.MAX_VALUE, lowestgoalminutes / 60));
                GoalMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, lowestgoalminutes % 60));
            } else {
                GoalHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
                GoalMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, lowestgoalminutes % 60));
            }
        }
        public boolean newvaluehigherthanmin(int newvalue) {
            return lowestgoalminutes == null || lowestgoalminutes == 0 || newvalue > lowestgoalminutes;
        }

    }
    public static class SingleGoalCompletedDialog extends Stage {
        public Label GoalHours;
        public Button CloseButton;
        public Label CurrentHoursLabel;
        public Label TopLabel;
        private MainController Root;

        public SingleGoalCompletedDialog(MainController root, kujiin.xml.Goals.Goal currentGoal, Double currentpracticedhours) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/GoalCompleted.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Goal Achieved");
//            TopLabel.setText(currentGoal.getMeditatableName() + " Goal Achieved");
            GoalHours.setText(currentGoal.getGoal_Hours().toString());
            CurrentHoursLabel.setText("Practiced Hours: " + currentpracticedhours.toString());
            CloseButton.setOnAction(event -> close());
        }
    }
    public static class MultipleGoalsCompletedDialog extends Stage implements Initializable {
        public Label TopLabel;
        public TableView<CompletedGoalsAtEndOfSessionBinding> GoalsCompletedTable;
        public TableColumn<CompletedGoalsAtEndOfSessionBinding, String> CutNameColumn;
        public TableColumn<CompletedGoalsAtEndOfSessionBinding, String> CurrentHoursColumn;
        public TableColumn<CompletedGoalsAtEndOfSessionBinding, String> GoalHoursColumn;
        public TableColumn<CompletedGoalsAtEndOfSessionBinding, String> DateSetColumn;
        public TableColumn<CompletedGoalsAtEndOfSessionBinding, Integer> DaysTakenColumn;
        public TableColumn<CompletedGoalsAtEndOfSessionBinding, String> DateCompletedColumn;
        public Button CloseButton;
        private MainController Root;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            CutNameColumn.setCellValueFactory(cellData -> cellData.getValue().cutname);
            GoalHoursColumn.setCellValueFactory(cellData -> cellData.getValue().goalhours);
            DateSetColumn.setCellValueFactory(cellData -> cellData.getValue().dateset);
            DaysTakenColumn.setCellValueFactory(cellData -> cellData.getValue().daysittooktocomplete.asObject());
            DateCompletedColumn.setCellValueFactory(cellData -> cellData.getValue().datecompleted);
            CurrentHoursColumn.setCellValueFactory(cellData -> cellData.getValue().practicedhours);
        }

        public MultipleGoalsCompletedDialog(MainController root, List<kujiin.xml.Goals.Goal> completedgoals) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/GoalsCompleted.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle(completedgoals.size() + " Goals Achieved");
            TopLabel.setText("You Completed " + completedgoals.size() + " Goals This Session");
            ObservableList<CompletedGoalsAtEndOfSessionBinding> newcompletedgoals = FXCollections.observableArrayList();
            for (kujiin.xml.Goals.Goal i : completedgoals) {
                String cutname = "";
                Meditatable x = Root.getSession().getAllMeditatablesincludingTotalforTracking().get(Root.getSession().getAllMeditablesincludingTotalNames().indexOf(cutname));
                String practicedhours = Double.toString(Util.convert_minstodecimalhours(x.getTotalMinutesPracticed(false), 2));
                String goalhours = i.getGoal_Hours().toString();
                String dateset = i.getDate_Set();
                Integer daystaken = (int) ChronoUnit.DAYS.between(Util.convert_stringtolocaldate(i.getDate_Set()), Util.convert_stringtolocaldate(i.getDate_Due()));
                String datecompleted = i.getDate_Completed();
                newcompletedgoals.add(new CompletedGoalsAtEndOfSessionBinding(cutname, practicedhours, goalhours, dateset, daystaken, datecompleted));
            }
            GoalsCompletedTable.setItems(newcompletedgoals);
            CloseButton.setOnAction(event -> close());
        }
    }
    public static class CurrentGoalBinding {
        private IntegerProperty goalid;
        private StringProperty goalhours;
        private StringProperty duedate;
        private StringProperty percentcomplete;
        private BooleanProperty completed;
        private StringProperty datecompleted;

        public CurrentGoalBinding(int id, String goalhours, String duedate, String percentcomplete, Boolean completed, String datecompleted) {
            this.goalid = new SimpleIntegerProperty(id);
            this.goalhours = new SimpleStringProperty(Util.format_minstohrsandmins_abbreviated(Util.convertdecimalhourstominutes(new Double(goalhours))));
            this.duedate = new SimpleStringProperty(Util.checkifdateoverdue(duedate));
            this.percentcomplete = new SimpleStringProperty(percentcomplete);
            this.completed = new SimpleBooleanProperty(completed);
            this.datecompleted = new SimpleStringProperty(datecompleted);
        }
    }
    public static class CompletedGoalsAtEndOfSessionBinding {
        private StringProperty cutname;
        private StringProperty practicedhours;
        private StringProperty goalhours;
        private StringProperty dateset;
        private IntegerProperty daysittooktocomplete;
        private StringProperty datecompleted;

        public CompletedGoalsAtEndOfSessionBinding(String cutname, String practicedhours, String goalhours, String dateset, int daysittooktocomplete, String datecompleted) {
            this.cutname = new SimpleStringProperty(cutname);
            this.practicedhours = new SimpleStringProperty(practicedhours);
            this.goalhours = new SimpleStringProperty(goalhours);
            this.dateset = new SimpleStringProperty(dateset);
            this.daysittooktocomplete = new SimpleIntegerProperty(daysittooktocomplete);
            this.datecompleted = new SimpleStringProperty(datecompleted);
        }
    }
    public static class DatePickerDialog extends Stage {
        public Label TopLabel;
        public DatePicker Date;
        public Button AcceptButton;
        public Button CloseButton;
        private LocalDate date;
        private MainController Root;
        private LocalDate MustBeAfterDate;

        public DatePickerDialog(MainController root, String titletext, String TopLabelText, LocalDate minimumdate) {
            Root = root;
            MustBeAfterDate = minimumdate;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DatePickerDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle(titletext);
            TopLabel.setText(TopLabelText);
            Date.setValue(LocalDate.now());
        }

        public LocalDate getDate() {
            return date;
        }
        public void setDate(LocalDate date) {
            this.date = date;
        }
        public void accept(ActionEvent actionEvent) {
            if (Date.getValue().isAfter(MustBeAfterDate) || Date.getValue().isEqual(MustBeAfterDate)) {
                setDate(Date.getValue());
                close();
            } else {
                Util.gui_showinformationdialog(Root, "Information", "Goal Date Must Be After " + Util.convert_localdatetostring(MustBeAfterDate), "Select A Later Date");}
        }
        public void cancel(ActionEvent actionEvent) {
            close();
        }
    }
    public static class GoalOverView extends Stage {
        public StackedBarChart<String, Number> GoalBarChart;
        public CategoryAxis CategoryAxis;
        public NumberAxis NumberAxis;
        public Button AddGoalButton;
        public Button EditGoalsButton;
        public Button CloseButton;

        // TODO Set A Limiter So Only 1 Goal Active At A Time. Cannot Set New Goal For (Meditatable) Until Current Goal Is Exceeded
        // TODO Stacked Bar Chart
            // Series: Current Practiced Time
            // Series: Current Goal
        public GoalOverView(MainController Root) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/GoalsOverview.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                setTitle("Goal Overview");
                XYChart.Series<String, java.lang.Number> currentpracticedseries = new XYChart.Series<>();
                XYChart.Series<String, java.lang.Number> currentgoalseries = new XYChart.Series<>();
                currentpracticedseries.setName("Current Practiced Hours");
                currentgoalseries.setName("Current Goal Hours");
                for (Meditatable i : Root.getSession().getAllMeditatables()) {
                    if (i.getTotalMinutesPracticed(false) > 0) {currentpracticedseries.getData().add(new XYChart.Data<>(i.getNameForChart(), Util.convert_hrsandminstodecimalhours(0, i.getTotalMinutesPracticed(false))));}
                    else {currentpracticedseries.getData().add(new XYChart.Data<>(i.getNameForChart(), 0));}
                    try {currentgoalseries.getData().add(new XYChart.Data<>(i.getNameForChart(), i.getCurrentGoal().getGoal_Hours()));}
                    catch (NullPointerException ignored) {currentgoalseries.getData().add(new XYChart.Data<>(i.getNameForChart(), 0));}
                }
                GoalBarChart.getData().add(currentpracticedseries);
                GoalBarChart.getData().add(currentgoalseries);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
        }

        public void addgoal(ActionEvent actionEvent) {

        }
        public void editgoals(ActionEvent actionEvent) {

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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/AllSessionDetails.fxml"));
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
                totalprogressrows.add(new TotalProgressRow(i.getNameForChart(), Util.format_minstohrsandmins_long(i.getTotalMinutesPracticed(false))));
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
                this.total = new SimpleStringProperty(Util.format_minstohrsandmins_abbreviated(total));
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