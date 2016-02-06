package kujiin.widgets;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import kujiin.MainController;
import kujiin.Tools;
import kujiin.interfaces.Widget;
import kujiin.xml.*;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GoalsWidget implements Widget{
    private Button NewGoalButton;
    private Button CurrentGoalsButton;
    private Button CompletedGoalsButton;
    private Label PracticedHours;
    private Label CurrentGoalHours;
    private Label TopLabel;
    private ComboBox<String> CutSelectorComboBox;
    private ProgressBar CurrentGoalProgress;
    private Sessions allpracticedsessions;
    private CurrentGoals currentGoals;
    private CompletedGoals completedGoals;
    private List<CurrentGoals.CurrentGoal> SelectedCutGoals;
    public static String[] GOALCUTNAMES = {"Rin", "Kyo", "Toh", "Sha", "Kai", "Jin", "Retsu", "Zai", "Zen", "Total"};

// TODO Make Goals For Individual Cuts Rin-Zen And Integerate Into Goal Widget (And Keep Existing Total Hour Goal)
    public GoalsWidget(MainController mainController) {
        NewGoalButton = mainController.newgoalButton;
        CurrentGoalsButton = mainController.viewcurrrentgoalsButton;
        CompletedGoalsButton = mainController.viewcompletedgoalsButton;
        PracticedHours = mainController.goalscurrrentvalueLabel;
        CurrentGoalHours = mainController.goalssettimeLabel;
        CurrentGoalProgress = mainController.goalsprogressbar;
        CutSelectorComboBox = mainController.GoalCutComboBox;
        TopLabel = mainController.GoalTopLabel;
        allpracticedsessions = mainController.getProgressTracker().getSessions();
        currentGoals = new CurrentGoals();
        completedGoals = new CompletedGoals();
        ObservableList<String> cutnames = FXCollections.observableArrayList(GOALCUTNAMES);
        CutSelectorComboBox.setItems(cutnames);
        CutSelectorComboBox.setOnAction(this::cutselectionchanged);
        Service<Void> getcurrentgoals = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        completedGoals.unmarshall();
                        return null;
                    }
                };
            }
        };
        getcurrentgoals.setOnSucceeded(event -> update());
        getcurrentgoals.setOnFailed(event -> update());
        getcurrentgoals.start();
    }

// Button Actions
    public void setnewgoal() {
        int cutindex = CutSelectorComboBox.getSelectionModel().getSelectedIndex();
        if (cutindex == -1) {Tools.showinformationdialog("Information","No Cut Selected", "Select A Cut To Add A Goal To"); return;}
        SetANewGoalDialog setANewGoalDialog = new SetANewGoalDialog(cutindex, allpracticedsessions);
        setANewGoalDialog.showAndWait();
        if (setANewGoalDialog.isAccepted()) {
            try {currentGoals.add(cutindex, new CurrentGoals.CurrentGoal(setANewGoalDialog.getGoaldate(), setANewGoalDialog.getGoalhours()));}
            catch (JAXBException ignored) {Tools.showerrordialog("Error", "Couldn't Add Goal", "Check File Permissions");}
        }
        update();
    }
    public void displaycurrentgoals() {
        int index = CutSelectorComboBox.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            Tools.showinformationdialog("Information", "No Cut Selected", "Please Select A Cut To Display Current Goals");
        } else if (! currentGoals.goalsexist(index)) {
            String cutname = GOALCUTNAMES[index];
            Tools.showinformationdialog("Information", "No Goals Exist For " + cutname, "Please Add A Goal For " + cutname);
        } else {
            List<CurrentGoals.CurrentGoal> goalslist = currentGoals.getallcutgoals(index);
            new DisplayCurrentGoalsDialog(goalslist, allpracticedsessions.gettotalcutpracticetimeinminutes(index)).showAndWait();
        }
        List<CurrentGoals.CurrentGoal> currentGoalslist = currentGoals.getTotalGoals();
        if (currentGoalslist == null) {Tools.showinformationdialog("Information", "No Goals To Display", "Set A New Goal First"); return;}
        new DisplayCurrentGoalsDialog(currentGoalslist, allpracticedsessions.gettotalcutpracticetimeinminutes(index)).showAndWait();
    }
    public void displaycompletedgoals() {
        int index = CutSelectorComboBox.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            Tools.showinformationdialog("Information", "No Cut Selected", "Please Select A Cut To Display Completed Goals");
        } else if (! completedGoals.goalsexist(index)) {
            String cutname = GOALCUTNAMES[index];
            Tools.showinformationdialog("Information", "No Goals Completed For " + cutname, "Complete A Goal For " + cutname);
        } else {
            List<CompletedGoals.CompletedGoal> goalslist = completedGoals.getallcutgoals(index);
            new DisplayCompletedGoalsDialog(goalslist).showAndWait();
        }
    }
    public void goalpacing() {
        int index = CutSelectorComboBox.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            Tools.showinformationdialog("Information", "No Cut Selected", "Please Select A Cut To Calculate Goal Pacing");
        } else {
            new GoalPacingDialog(currentGoals.getgoal(index, 0), currentGoals.getTotalGoals(), allpracticedsessions.gettotalcutpracticetimeinminutes(index)).showAndWait();
        }
    }
    public void cutselectionchanged(ActionEvent actionEvent) {
        update();
    }
    public void update() {
        try {
            int index = CutSelectorComboBox.getSelectionModel().getSelectedIndex();
            if (index == -1) {resetallvalues();}
            else {
                String cutname = GOALCUTNAMES[index];
                if (!currentGoals.goalsexist(index)) {
                    if (Tools.getanswerdialog("Information", "No Goals Exist For " + cutname, "Add A Goal For " + cutname + "?")) {setnewgoal();}
                    else {resetallvalues(); return;}
                }
                Double practiced = Tools.convertminutestodecimalhours(allpracticedsessions.gettotalcutpracticetimeinminutes(index));
                Double goal = currentGoals.getgoal(index, 0).getGoal_Hours();
                PracticedHours.setText(practiced.toString() + " hrs");
                CurrentGoalHours.setText(goal.toString() + " hrs");
                CurrentGoalProgress.setProgress(practiced / goal);
                TopLabel.setText(cutname + " 's Current Goal");
            }
        } catch (NullPointerException ignored) {resetallvalues();}
    }
    public boolean cleanup() {
        currentGoals.marshall();
        completedGoals.marshall();
        return true;
    }
    public boolean updategoal() {
        SelectedCutGoals = currentGoals.getallcutgoals(CutSelectorComboBox.getSelectionModel().getSelectedIndex());
        // TODO Find A Way To Pass Practiced Goal Time (For Each Cut) Into Here And Check Goals
        return false;
    }

// Widget Implementation
    @Override
    public void disable() {
        NewGoalButton.setDisable(true);
        CurrentGoalsButton.setDisable(true);
        CompletedGoalsButton.setDisable(true);
        PracticedHours.setDisable(true);
        CurrentGoalHours.setDisable(true);
        CurrentGoalProgress.setDisable(true);
    }
    @Override
    public void enable() {
        NewGoalButton.setDisable(false);
        CurrentGoalsButton.setDisable(false);
        CompletedGoalsButton.setDisable(false);
        PracticedHours.setDisable(false);
        CurrentGoalHours.setDisable(false);
        CurrentGoalProgress.setDisable(false);
    }
    @Override
    public void resetallvalues() {
//        CutSelectorComboBox.getSelectionModel().clearSelection();
        PracticedHours.setText("-   ");
        CurrentGoalHours.setText("  -");
        CurrentGoalProgress.setProgress(0.0);
        TopLabel.setText("Select A Cut...");
    }

// Subclasses/Dialogs
    public static class DisplayCompletedGoalsDialog extends Stage {
        public TableView<CompletedGoal> currentgoaltable;
        public TableColumn<CompletedGoal, Integer> NumberColumn;
        public TableColumn<CompletedGoal, String> GoalTimeColumn;
        public TableColumn<CompletedGoal, String> CompletedOnColumn;
        public Button CloseButton;

        public DisplayCompletedGoalsDialog(List<CompletedGoals.CompletedGoal> completedgoals) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayCompletedGoals.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Completed Goals");}
            catch (IOException e) {e.printStackTrace();}
            ObservableList<CompletedGoal> completedGoals = FXCollections.observableArrayList();
            completedGoals.addAll(completedgoals.stream().map(i -> new CompletedGoal(completedgoals.indexOf(i) + 1, Double.toString(i.getGoal_Hours()), i.getDate_Completed())).collect(Collectors.toList()));
            NumberColumn.setCellValueFactory(cellData -> cellData.getValue().goalid.asObject());
            GoalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().goalhours);
            CompletedOnColumn.setCellValueFactory(cellData -> cellData.getValue().datecompleted);
            currentgoaltable.setItems(completedGoals);
        }

        public void closeDialog(Event event) {this.close();}

        public class CompletedGoal {
            public IntegerProperty goalid;
            public StringProperty goalhours;
            public StringProperty datecompleted;

            public CompletedGoal(int goalid, String goalhours, String datecompleted) {
                this.goalid = new SimpleIntegerProperty(goalid);
                this.goalhours = new SimpleStringProperty(goalhours);
                this.datecompleted = new SimpleStringProperty(datecompleted);
            }
        }
    }
    public static class DisplayCurrentGoalsDialog extends Stage {
        public TableView<CurrentGoalBinding> currentgoaltable;
        public TableColumn<CurrentGoalBinding, Integer> NumberColumn;
        public TableColumn<CurrentGoalBinding, String> GoalTimeColumn;
        public TableColumn<CurrentGoalBinding, String> DueDateColumn;
        public TableColumn<CurrentGoalBinding, String> PercentCompleteColumn;
        public Button CloseButton;

        public DisplayCurrentGoalsDialog(List<CurrentGoals.CurrentGoal> currentGoalList, double currentpracticedhours) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayCurrentGoals.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Current Goals");}
            catch (IOException e) {e.printStackTrace();}
            ObservableList<CurrentGoalBinding> currentGoals = FXCollections.observableArrayList();
            currentGoals.addAll(currentGoalList.stream().map(i -> new CurrentGoalBinding(currentGoalList.indexOf(i) + 1, Double.toString(i.getGoal_Hours()), i.getDate_Set(), i.getpercentagecompleted(currentpracticedhours))).collect(Collectors.toList()));
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

        public void closeDialog(Event event) {this.close();}
    }
    public static class DisplayPrematureEndingsDialog extends Stage {

        public TableView<PrematureEnding> prematureendingTable;
        public TableColumn<PrematureEnding, String> DateColumn;
        public TableColumn<PrematureEnding, String> LastCutPracticedColumn;
        public TableColumn<PrematureEnding, String> ExpectedSessionColumn;
        public Button CloseButton;
        public TableColumn<PrematureEnding, String> ReasonColumn;
        public ArrayList<Session> sessionwithprematureendings;

        public DisplayPrematureEndingsDialog(Parent parent, ArrayList<Session> sessionwithprematureendings) {
            this.sessionwithprematureendings = sessionwithprematureendings;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayPrematureEndings.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Premature Endings");}
            catch (IOException e) {e.printStackTrace();}
            ObservableList<PrematureEnding> prematureEndings = FXCollections.observableArrayList();
            for (Session i : sessionwithprematureendings) {
                prematureEndings.add(new PrematureEnding(i.getDate_Practiced(), i.getLast_Cut_Practiced_Before_Premature_Ending(), i.getExpected_Session_List(), i.getPremature_Ending_Reason()));
            }
            DateColumn.setCellValueFactory(cellData -> cellData.getValue().date);
            LastCutPracticedColumn.setCellValueFactory(cellData -> cellData.getValue().lastcutpracticed);
            ExpectedSessionColumn.setCellValueFactory(cellData -> cellData.getValue().expectedsessionlist);
            ReasonColumn.setCellValueFactory(cellData -> cellData.getValue().reason);
            prematureendingTable.setItems(prematureEndings);
        }

        public void closeDialog(Event event) {this.close();}

        public class PrematureEnding {
            public StringProperty date;
            public StringProperty lastcutpracticed;
            public StringProperty expectedsessionlist;
            public StringProperty reason;

            public PrematureEnding(String date, String lastcutpracticed, String expectedsessionlist, String reason) {
                this.date = new SimpleStringProperty(date);
                this.lastcutpracticed = new SimpleStringProperty(lastcutpracticed);
                this.expectedsessionlist = new SimpleStringProperty(expectedsessionlist);
                this.reason = new SimpleStringProperty(reason);
            }
        }

    }
    public static class GoalPacingDialog extends Stage implements Initializable {
        public Label SelectedGoalHours;
        public Button SelectADiffferentGoalButton;
        public Spinner<Integer> DaysSpinner;
        public Button CalculateButton;
        public Button CloseButton;
        private CurrentGoals.CurrentGoal currentGoal;
        private List<CurrentGoals.CurrentGoal> currentGoals;
        private double alreadypracticedhours;

        public GoalPacingDialog(CurrentGoals.CurrentGoal currentGoal, List<CurrentGoals.CurrentGoal> currentGoals, double alreadypracticedhours) {
            this.currentGoal = currentGoal;
            this.currentGoals = currentGoals;
            this.alreadypracticedhours = alreadypracticedhours;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/GoalPacingDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Goal Pacing");}
            catch (IOException e) {e.printStackTrace();}
            SelectedGoalHours.setText(currentGoal.getGoal_Hours() + " Hours");
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            DaysSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        }

    // Getters And Setters
        public CurrentGoals.CurrentGoal getCurrentGoal() {
            return currentGoal;
        }
        public void setCurrentGoal(CurrentGoals.CurrentGoal currentGoal) {
            this.currentGoal = currentGoal;
        }

    // Button Actions
        public void selectanewgoal(ActionEvent actionEvent) {
            SelectGoalDialog selectGoalDialog = new SelectGoalDialog(currentGoals, alreadypracticedhours);
            selectGoalDialog.showAndWait();
            setCurrentGoal(selectGoalDialog.getSelectedgoal());
            if (getCurrentGoal() == null) {return;}
            SelectedGoalHours.setText(getCurrentGoal().getGoal_Hours() + " Hours");
        }
        public void calculate(ActionEvent actionEvent) {
            if (getCurrentGoal() == null) {Tools.showerrordialog("Error", "No Goal Selected", "Please Select A Goal"); return;}
            if (DaysSpinner.getValue() == 0) {Tools.showerrordialog("Error", "Cannot Calculate", "Days Cannot Be 0"); return;}
            Double goalhours = getCurrentGoal().getGoal_Hours();
            Double days = (double) DaysSpinner.getValue();
            Float hourstopractice = goalhours.floatValue() / days.floatValue();
            int minsaday = Tools.convertdecimalhourstominutes(hourstopractice.doubleValue());
            String formattedgoalhours = Tools.minstoformattedlonghoursandminutes(Tools.convertdecimalhourstominutes(goalhours));
            Tools.showinformationdialog("Calculation", "To Reach " + formattedgoalhours + " In " + days.intValue() + " Days:",
                    "Practice For " + Tools.minstoformattedlonghoursandminutes(minsaday) + " A Day");
        }
        public void closedialog(ActionEvent actionEvent) {close();}

    }
    public static class PrematureEndingDialog extends Stage {
        public TextArea Reason;
        public Button AcceptButton;
        public Button CancelButton;
        private kujiin.xml.Options Options;

        public PrematureEndingDialog(Options options) {
            Options = options;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/PrematureEndingDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Ending Session Prematurely");
            } catch (IOException e) {e.printStackTrace();}
        }

        public String getReason() {
            if (! Reason.getText().isEmpty()) {return Reason.getText();}
            else {return null;}
        }
        public void accepted(ActionEvent actionEvent) {
            this.close();
        }
        public void rejected(ActionEvent actionEvent) {
            Options.getSessionOptions().setPrematureendings(! Tools.getanswerdialog("Disable Premature Endings", "Disable Premature Endings Dialog", "This Will Keep This Session From Displaying In The Future"));
            Reason.setText("");
            this.close();
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
        public CurrentGoals.CurrentGoal selectedgoal;
        private List<CurrentGoals.CurrentGoal> currentGoalList;

        public SelectGoalDialog(List<CurrentGoals.CurrentGoal> currentGoalList, double currentpracticedhours) {
            this.currentGoalList = currentGoalList;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SelectGoalDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Select A Goal");}
            catch (IOException e) {e.printStackTrace();}
            ObservableList<CurrentGoalBinding> currentGoals = FXCollections.observableArrayList();
            currentGoals.addAll(currentGoalList.stream().map(i -> new CurrentGoalBinding(currentGoalList.indexOf(i) + 1, Double.toString(i.getGoal_Hours()), i.getDate_Set(), i.getpercentagecompleted(currentpracticedhours))).collect(Collectors.toList()));
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
        public CurrentGoals.CurrentGoal getSelectedgoal() {
            return selectedgoal;
        }
        public void setSelectedgoal(CurrentGoals.CurrentGoal selectedgoal) {
            this.selectedgoal = selectedgoal;
        }

    // Button Actions
        public void closeDialog(ActionEvent actionEvent) {close();}
        public void selectgoal(ActionEvent actionEvent) {
            int index = currentgoaltable.getSelectionModel().getSelectedIndex();
            if (index == -1) {Tools.showinformationdialog("Information", "No Goal Selected", "Select A Goal"); return;}
            setSelectedgoal(currentGoalList.get(index));
            close();
        }
    }
    public static class SetANewGoalDialog extends Stage {
        private final Sessions sessions;
        public Spinner<Integer> GoalHoursSpinner;
        public DatePicker GoalDatePicker;
        public Button CancelButton;
        public Button OKButton;
        public Button CurrentGoalsButton;
        public Spinner<Integer> GoalMinutesSpinner;
        public ComboBox<String> CutComboBox;
        private LocalDate goaldate;
        private Double goalhours;

        public SetANewGoalDialog(int cutindex, Sessions sessions) {
            this.sessions = sessions;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SetNewGoalDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("New Goal");}
            catch (IOException e) {e.printStackTrace();}
            GoalHoursSpinner.setEditable(true);
            GoalMinutesSpinner.setEditable(true);
            GoalDatePicker.setValue(LocalDate.now());
            ObservableList<String> cutnames = FXCollections.observableArrayList(GOALCUTNAMES);
            CutComboBox.setItems(cutnames);
            CutComboBox.setOnAction(this::cutselected);
            CutComboBox.getSelectionModel().select(cutindex);
            cutselected(null);
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
        public boolean isAccepted() {return getGoalhours() != null && getGoaldate() != null;}

    // Button Actions
        public void cancelgoalsetting(Event event) {this.close();}
        public void Accept(Event event) {
            if (GoalMinutesSpinner.getValue() > 59) {
                Tools.showinformationdialog("Information", "Minutes Cannot Be Greater Than 59", "Select A Value Less Than 59"); return;}
            boolean dategood = GoalDatePicker.getValue().isAfter(LocalDate.now());
            if (dategood) {
                int hours = GoalHoursSpinner.getValue();
                int minutes = GoalMinutesSpinner.getValue();
                double newhours = Tools.hoursandminutestoformatteddecimalhours(hours, minutes);
                setGoalhours(newhours);
                setGoaldate(GoalDatePicker.getValue());
                super.close();
            } else {
                Tools.showinformationdialog("Cannot Set Goal", "Cannot Set Goal", "Due Date Must Be After Today");
                setGoalhours(null);
                setGoaldate(null);
            }
        }
        public void viewcurrentgoals(Event event) {

        }
        public void cutselected(ActionEvent actionEvent) {
            int index = CutComboBox.getSelectionModel().getSelectedIndex();
            int minutes = sessions.gettotalcutpracticetimeinminutes(index);
            int hrs = minutes / 60;
            int mins = minutes % 60;
            GoalHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(hrs, Integer.MAX_VALUE, 0, 1));
            GoalHoursSpinner.getValueFactory().setValue(hrs);
            GoalMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(mins, 59, 0, 5));
            GoalMinutesSpinner.getValueFactory().setValue(mins);
        }
    }
    public static class GoalCompleted extends Stage {
        public Label GoalHours;
        public Button CloseButton;
        public Label CurrentHoursLabel;

        public GoalCompleted(CurrentGoals.CurrentGoal currentGoal, Double currentpracticedhours) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/GoalCompleted.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Goal Achieved");}
            catch (IOException e) {e.printStackTrace();}
            GoalHours.setText(currentGoal.getGoal_Hours().toString());
            CurrentHoursLabel.setText(currentpracticedhours.toString());
            CloseButton.setOnAction(event -> close());
        }
    }
    public static class CurrentGoalBinding {
        private IntegerProperty goalid;
        private StringProperty goalhours;
        private StringProperty duedate;
        private StringProperty percentcomplete;

        public CurrentGoalBinding(int id, String goalhours, String duedate, String percentcomplete) {
            this.goalid = new SimpleIntegerProperty(id);
            this.goalhours = new SimpleStringProperty(goalhours);
            this.duedate = new SimpleStringProperty(duedate);
            this.percentcomplete = new SimpleStringProperty(percentcomplete);
        }
    }
}
