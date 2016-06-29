package kujiin.ui;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import kujiin.MainController;
import kujiin.util.Meditatable;
import kujiin.util.Util;
import kujiin.xml.Goals;
import kujiin.xml.Options;
import kujiin.xml.Session;
import kujiin.xml.Sessions;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;

// TODO Finish Making EditGoalsDialog (Also Add A Select A Different Cut Feature, And Make It So They Can Pass Cutindex Data Back And Forth)
public class ProgressAndGoalsUI {
    public static String[] GOALCUTNAMES = {"Presession", "Rin", "Kyo", "Toh", "Sha", "Kai", "Jin", "Retsu", "Zai", "Zen", "Earth", "Air", "Fire", "Water", "Void", "Postsession", "Total"};
    public static int CUTORELEMENTCOUNT = GOALCUTNAMES.length;
    private ComboBox<String> CutSelectorComboBox;
    private Integer cutorelementindex;
// Progress Tracker Fields
    private Sessions Sessions;
    private TextField TotalTimePracticed;
    private TextField NumberOfSessionsPracticed;
    private TextField AverageSessionDuration;
    private CheckBox PreAndPostOption;
    private Button SessionListButton;
// Goals Fields
    private Goals Goals;
    private Button NewGoalButton;
    private Button CurrentGoalsButton;
    private TextField PracticedHours;
    private TextField PracticedMinutes;
    private TextField GoalHours;
    private TextField GoalMinutes;
    private ProgressBar GoalProgress;
    private Label TopLabel;
    private Label StatusBar;
    private MainController Root;

    public ProgressAndGoalsUI(MainController root) {
        Root = root;
        NewGoalButton = root.newgoalButton;
        CurrentGoalsButton = root.viewcurrrentgoalsButton;
        PracticedHours = root.GoalPracticedHours;
        PracticedHours.setEditable(false);
        PracticedMinutes = root.GoalPracticedMinutes;
        PracticedMinutes.setEditable(false);
        GoalHours = root.GoalSetHours;
        GoalHours.setEditable(false);
        GoalMinutes = root.GoalSetMinutes;
        GoalMinutes.setEditable(false);
        GoalProgress = root.goalsprogressbar;
        CutSelectorComboBox = root.GoalCutComboBox;
        TopLabel = root.GoalTopLabel;
        StatusBar = root.GoalStatusBar;
        TotalTimePracticed = root.TotalTimePracticed;
        NumberOfSessionsPracticed = root.NumberOfSessionsPracticed;
        AverageSessionDuration = root.AverageSessionDuration;
        PreAndPostOption = root.PrePostSwitch;
        SessionListButton = root.ListOfSessionsButton;
        Sessions = new Sessions(Root);
        Goals = new Goals(Root);
        Service<Void> getsessions = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Sessions.unmarshall();
                        return null;
                    }
                };
            }
        };
        getsessions.setOnRunning(event -> loading());
        getsessions.setOnFailed(event -> updateprogressui());
        getsessions.setOnSucceeded(event -> updateprogressui());
        getsessions.start();
        TotalTimePracticed.setEditable(false);
        NumberOfSessionsPracticed.setEditable(false);
        AverageSessionDuration.setEditable(false);
        ObservableList<String> cutnames = FXCollections.observableArrayList(GOALCUTNAMES);
        CutSelectorComboBox.setItems(cutnames);
        CutSelectorComboBox.setOnAction(this::cutselectionchanged);
        Service<Void> getgoals = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Goals.unmarshall();
                        return null;
                    }
                };
            }
        };
        getgoals.setOnSucceeded(event -> updaterootgoalsui());
        getgoals.setOnFailed(event -> updaterootgoalsui());
        getgoals.start();
        PreAndPostOption.setOnAction(this::cutselectionchanged);
        cutorelementindex = CutSelectorComboBox.getSelectionModel().getSelectedIndex();
    }

// Getters And Setters
    public Sessions getSessions() {return Sessions;}
    public Goals getGoal() {
        return Goals;
    }
    public Integer getCutorelementindex() {
        return cutorelementindex;
    }
    public void setCutorelementindex(Integer cutorelementindex) {
        this.cutorelementindex = cutorelementindex;
    }

// Button Actions
    public void cutselectionchanged(ActionEvent actionEvent) {
        try {
            cutorelementindex = CutSelectorComboBox.getSelectionModel().getSelectedIndex();
            PreAndPostOption.setDisable(cutorelementindex != GOALCUTNAMES.length - 1);
            if (cutorelementindex != GOALCUTNAMES.length - 1) {PreAndPostOption.setSelected(false);}
            if (cutorelementindex == -1) {resetallvalues();}
            else {
                updateprogressui();
                updaterootgoalsui();
            }
        } catch (NullPointerException ignored) {resetallvalues();}
    }
    public void displaydetailedcutprogress() {
        if (Sessions.getSession() != null) {new DisplayCutTotalsDialog(Root, Sessions.getSession());}
        else {
            Util.gui_showinformationdialog(Root, "Cannot Display", "Nothing To Display", "Need To Practice At Least One Session To Use This Feature");}
    }
    public void displaysessionlist() {
        if (Sessions.getSession() == null || Sessions.getSession().size() == 0) {
            Util.gui_showinformationdialog(Root, "Cannot Display", "Nothing To Display", "Need To Practice At Least One Session To Use This Feature");
        } else {new DisplaySessionListDialog(Root, Sessions.getSession()).showAndWait();}
    }
    public void setnewgoal() {
        if (cutorelementindex == -1) {
            Util.gui_showinformationdialog(Root, "Information","No Cut Selected", "Select A Cut To Add A Goal To"); return;}
        SetANewGoalForSingleCut setANewGoalForSingleCutDialog = new SetANewGoalForSingleCut(cutorelementindex, Root);
        setANewGoalForSingleCutDialog.showAndWait();
        if (setANewGoalForSingleCutDialog.isAccepted()) {
            try {
                Goals.add(cutorelementindex, new Goals.Goal(setANewGoalForSingleCutDialog.getGoaldate(), setANewGoalForSingleCutDialog.getGoalhours(), GOALCUTNAMES[cutorelementindex]));}
            catch (JAXBException ignored) {
                Util.gui_showerrordialog(Root, "Error", "Couldn't Add Goal", "Check File Permissions");}
        }
        updaterootgoalsui();
    }
    public void opengoaleditor() {
        if (cutorelementindex == -1) {
            Util.gui_showinformationdialog(Root, "Information", "No Cut Selected", "Please Select A Cut To Edit Its Goals"); return;}
        if (! Goals.goalsexist(cutorelementindex, true)) {
            Util.gui_showinformationdialog(Root, "Information", "No Goals Exist For " + GOALCUTNAMES[cutorelementindex], "Please Add A Goal For " + GOALCUTNAMES[cutorelementindex]); return;}
        new EditGoalsDialog(Root, cutorelementindex).showAndWait();
    }

// Widget Implementation
    public void loading() {
        AverageSessionDuration.setText("-");
        TotalTimePracticed.setText("-");
        NumberOfSessionsPracticed.setText("-");
    }
    public void resetallvalues() {
        TotalTimePracticed.setText("No Sessions");
        NumberOfSessionsPracticed.setText("No Sessions");
        AverageSessionDuration.setText("No Sessions");
        PracticedHours.setText("-");
        PracticedMinutes.setText("-");
        GoalHours.setText("-");
        GoalMinutes.setText("-");
        GoalProgress.setProgress(0.0);
        if (cutorelementindex != null && cutorelementindex != -1) {
            TopLabel.setText(GOALCUTNAMES[cutorelementindex]);
        } else {TopLabel.setText("Select A Cut");}
    }
    public boolean cleanup() {
        Goals.marshall();
        Sessions.marshall();
        return true;
    }

// Total Progress Specific Methods
    public void updateprogressui() {
    // Update Total Progress
        try {
            if (cutorelementindex == -1) {PreAndPostOption.setDisable(true);}
            else {
                int averagesessionduration = Sessions.sessioninformation_getaveragepracticetime(cutorelementindex, PreAndPostOption.isSelected());
                int totalminutespracticed = Sessions.sessioninformation_getallsessiontotals(cutorelementindex, PreAndPostOption.isSelected());
                int numberofsessionspracticed = Sessions.sessioninformation_getsessioncount(cutorelementindex);
                boolean sessionsgood = averagesessionduration != 0 || totalminutespracticed != 0 || numberofsessionspracticed != 0;
                String nonetext = "No Sessions";
                if (sessionsgood) {
                    String longtext = Util.format_minstohrsandmins_short(averagesessionduration);
                    if (longtext.toCharArray().length <= 14) {AverageSessionDuration.setText(longtext);}
                    else {AverageSessionDuration.setText(Util.format_minstohrsandmins_abbreviated(averagesessionduration));}
                }
                else {AverageSessionDuration.setText(nonetext);}
                if (sessionsgood) {
                    String longtext = Util.format_minstohrsandmins_short(totalminutespracticed);
                    if (longtext.toCharArray().length <= 14) {TotalTimePracticed.setText(longtext);}
                    else {TotalTimePracticed.setText(Util.format_minstohrsandmins_abbreviated(totalminutespracticed));}
                }
                else {TotalTimePracticed.setText(nonetext);}
                if (sessionsgood) {NumberOfSessionsPracticed.setText(Integer.toString(numberofsessionspracticed));}
                else {NumberOfSessionsPracticed.setText(nonetext);}
            }
        } catch (NullPointerException ignored) {}
    }
    public void selectcut(int cutindex) {
        CutSelectorComboBox.getSelectionModel().select(cutindex);
        setCutorelementindex(cutindex);
    }

// Goal Specific Methods
    public ArrayList<Meditatable> precreationgoalchecks(List<Meditatable> cutsandelementsinsession) {
        ArrayList<Meditatable> notgoodelementselementsorcuts = new ArrayList<>();
        for (Meditatable i : cutsandelementsinsession) {
            if (i.getdurationinminutes() != 0 && ! checkifgoalsetandlongenough(i.number, i.getdurationinminutes())) {
                notgoodelementselementsorcuts.add(i);
            }
        }
        return notgoodelementselementsorcuts;
    }
    public int getlowestgoalminutesforallmeditatables(List<Meditatable> meditatables) {
        List<Integer> currentgoalhours = new ArrayList<>();
        try {
            for (Meditatable i : meditatables) {
                List<kujiin.xml.Goals.Goal> currentGoals = Goals.sort(Goals.getallcutgoals(i.number, false));
                currentgoalhours.add(currentGoals.get(currentGoals.size() - 1).getGoal_Hours().intValue());
            }
            return Collections.min(currentgoalhours);
        } catch (Exception e) {return 0;}
    }
    public boolean checkifgoalsetandlongenough(int cut_index, int duration) {
        try {
            List<kujiin.xml.Goals.Goal> currentGoals = Goals.sort(Goals.getallcutgoals(cut_index, false));
            int currentpracticedminutes = Sessions.sessioninformation_getallsessiontotals(cut_index, PreAndPostOption.isSelected());
            currentpracticedminutes += duration;
            return currentpracticedminutes >= currentGoals.get(currentGoals.size() - 1).getGoal_Hours().intValue();
        } catch (Exception e) {return false;}
    }
    // TODO !IMPORTANT Fix Goal Multiple Goal Setter (Minutes Was Stuck At 30)
    public void updateplayergoalsui() {
        PlayerUI playerUI = Root.getPlayer();
        if (playerUI != null && playerUI.isShowing()) {
            double practiceddecimalhours = Util.convert_minstodecimalhours(Sessions.sessioninformation_getallsessiontotals(cutorelementindex, PreAndPostOption.isSelected()), 2);
            playerUI.GoalTopLabel.setText("Current " + GOALCUTNAMES[cutorelementindex] + " Goal");
            try {
                Double goal = Goals.getgoal(cutorelementindex, 0, false).getGoal_Hours();
                Double goaldecimalhours = Util.convert_minstodecimalhours(new Double(goal * 60).intValue(), 1);
                Double progress = goaldecimalhours / practiceddecimalhours;
                playerUI.GoalProgressLabel.setText(String.format("%s hrs -> %s hrs (%d", practiceddecimalhours, goaldecimalhours, progress.intValue()) + "%)");
                playerUI.GoalProgressBar.setProgress(progress / 100);
            } catch (NullPointerException ignored) {
                playerUI.GoalProgressLabel.setText("No Goal Set (" + practiceddecimalhours + " Current hrs)");
                playerUI.GoalProgressBar.setProgress(0.0);
            }
        }
    }
    public void updaterootgoalsui() {
        updateplayergoalsui();
        try {
//            Goals.sortallcompletedgoals();
            NewGoalButton.setDisable(cutorelementindex == -1);
            CurrentGoalsButton.setDisable(cutorelementindex == -1);
            if (cutorelementindex == -1) {
                NewGoalButton.setTooltip(new Tooltip("Select A Cut Or Element Above To Set A Goal"));
                CurrentGoalsButton.setTooltip(new Tooltip("Select A Cut Or Element Above To See/Edit Current Goals"));
            } else {
                String cutorelementname = Options.ALLNAMES.get(cutorelementindex);
                NewGoalButton.setTooltip(new Tooltip("Set A New Goal For " + cutorelementname));
                CurrentGoalsButton.setTooltip(new Tooltip("View/Edit Goals For " + cutorelementname));
            }
            int practicedminutes = Sessions.sessioninformation_getallsessiontotals(cutorelementindex, PreAndPostOption.isSelected());
            Double goal = Goals.getgoal(cutorelementindex, 0, false).getGoal_Hours();
            Integer hours = practicedminutes / 60;
            Integer minutes = practicedminutes % 60;
            PracticedHours.setText(hours.toString());
            PracticedMinutes.setText(minutes.toString());
            if (goal != null) {
                Double progress = Util.convert_minstodecimalhours(practicedminutes, 2) / goal;
                goal *= 60;
                Integer hrs = goal.intValue() / 60;
                Integer mins = goal.intValue() % 60;
                GoalHours.setText(hrs.toString());
                GoalMinutes.setText(mins.toString());
                GoalProgress.setProgress(progress);
            } else {
                GoalHours.setText("?");
                GoalMinutes.setText("?");
                GoalProgress.setProgress(0.0);
            }
            TopLabel.setText(GOALCUTNAMES[cutorelementindex]);
        } catch (NullPointerException ignored) {
            if (cutorelementindex != -1) {
                TopLabel.setText(GOALCUTNAMES[cutorelementindex]);
                Util.gui_showtimedmessageonlabel(StatusBar, "No Current Goal Set (" + Goals.getcompletedgoalcount(cutorelementindex) + " Completed)", 4000);
                int practicedminutes = Sessions.sessioninformation_getallsessiontotals(cutorelementindex, PreAndPostOption.isSelected());
                Integer hours = practicedminutes / 60;
                Integer minutes = practicedminutes % 60;
                PracticedHours.setText(hours.toString());
                PracticedMinutes.setText(minutes.toString());
                GoalMinutes.setText("?");
                GoalHours.setText("?");
            } else {
                PracticedHours.setText("-");
                PracticedMinutes.setText("-");
                GoalMinutes.setText("-");
                GoalHours.setText("-");
            }
            NewGoalButton.setDisable(cutorelementindex == -1);
            CurrentGoalsButton.setDisable(cutorelementindex == -1);
            GoalProgress.setProgress(0.0);
        } catch (ArrayIndexOutOfBoundsException ignored) {resetallvalues();}
    }
    public List<kujiin.xml.Goals.Goal> getcurrentgoallist(boolean includecompleted) {
        if (cutorelementindex != null) {return Goals.getallcutgoals(cutorelementindex, includecompleted);}
        else {return null;}
    }

// Subclasses/Dialogs
    public static class DisplaySessionListDialog extends Stage {
        public TableView<SessionRow> sessionsTableView;
        public TableColumn<SessionRow, String> DateColumn;
        public TableColumn<SessionRow, String> RinColumn;
        public TableColumn<SessionRow, String> KyoColumn;
        public TableColumn<SessionRow, String> TohColumn;
        public TableColumn<SessionRow, String> ShaColumn;
        public TableColumn<SessionRow, String> KaiColumn;
        public TableColumn<SessionRow, String> JinColumn;
        public TableColumn<SessionRow, String> RetsuColumn;
        public TableColumn<SessionRow, String> ZaiColumn;
        public TableColumn<SessionRow, String> ZenColumn;
        public TableColumn<SessionRow, String> TotalColumn;
        public TableColumn<SessionRow, String> EarthColumn;
        public TableColumn<SessionRow, String> AirColumn;
        public TableColumn<SessionRow, String> FireColumn;
        public TableColumn<SessionRow, String> WaterColumn;
        public TableColumn<SessionRow, String> VoidColumn;
        public Button CloseButton;
        public Button ViewDetailsButton;
        private List<Session> sessionlist;
        private MainController Root;


        public DisplaySessionListDialog(MainController root, List<Session> sessionlist) {
            Root = root;
            this.sessionlist = sessionlist;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplaySessionList.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Session List");
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
            TotalColumn.setCellValueFactory(cellData -> cellData.getValue().total);
            ObservableList<SessionRow> rowlist = FXCollections.observableArrayList();
            int count = 1;
            for (Session i : sessionlist) {
                rowlist.add(new SessionRow(count, i.getDate_Practiced(), i.getPresession_Duration(), i.getRin_Duration(),
                        i.getKyo_Duration(), i.getToh_Duration(), i.getSha_Duration(), i.getKai_Duration(), i.getJin_Duration(),
                        i.getRetsu_Duration(), i.getZai_Duration(), i.getZen_Duration(), i.getEarth_Duration(), i.getAir_Duration(),
                        i.getFire_Duration(), i.getWater_Duration(), i.getVoid_Duration(), i.getPostsession_Duration(),
                        i.getTotal_Session_Duration()));
                count++;
            }
            ViewDetailsButton.setDisable(true);
            sessionsTableView.setItems(rowlist);
            sessionsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                ViewDetailsButton.setDisable(sessionsTableView.getSelectionModel().getSelectedIndex() == -1);
            });
        }

        public void viewsessiondetails(ActionEvent actionEvent) {
            int index = sessionsTableView.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                new MainController.SessionDetails(Root, sessionlist.get(index)).showAndWait();
            }
        }

        public void closeDialog(Event event) {this.close();}


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
    }
    public static class DisplayCutTotalsDialog extends Stage {
        public TableView<TotalProgressRow> progresstable;
        public TableColumn<TotalProgressRow, String> NameColumn;
        public TableColumn<TotalProgressRow, String> ProgressColumn;
        public TableColumn<TotalProgressRow, Integer> NumberColumn;
        private MainController Root;
        private List<Session> allsessions;

        public DisplayCutTotalsDialog(MainController root, List<Session> allsessions) {
            Root = root;
            this.allsessions = allsessions;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayCutTotalsDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Cut Totals");
            NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
            NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
            ProgressColumn.setCellValueFactory(cellData -> cellData.getValue().formattedduration);
            populatetable();
        }

        public void populatetable() {
            ArrayList<TotalProgressRow> allrows = new ArrayList<>();
            for (int i = 1; i < 10; i++) {
                int durationinmins = 0;
                for (Session x : allsessions) {durationinmins += x.getcutduration(i);}
                String duration;
                if (durationinmins > 0) {duration = Util.format_minstohrsandmins_short(durationinmins);}
                else {duration = "-";}
                allrows.add(new TotalProgressRow(i, Options.ALLNAMES.get(i), duration));
            }
            progresstable.getItems().addAll(allrows);
        }

        public class TotalProgressRow {
            private IntegerProperty number;
            private StringProperty name;
            private StringProperty formattedduration;

            public TotalProgressRow(Integer id, String name, String formattedduration) {
                this.number = new SimpleIntegerProperty(id);
                this.name = new SimpleStringProperty(name);
                this.formattedduration = new SimpleStringProperty(formattedduration);
            }
        }
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
        private Integer cutindex;

        public EditGoalsDialog(MainController root, Integer cutindex) {
            Root = root;
            ProgressAndGoals = Root.getProgressTracker();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayGoals.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            String name = ProgressAndGoalsUI.GOALCUTNAMES[cutindex];
            setTitle("View/Edit " + name + "'s Goals");
            CutSelectorComboBox.setItems(FXCollections.observableArrayList(GOALCUTNAMES));
            populatetable();
            CutSelectorComboBox.getSelectionModel().select(cutindex);
            setOnCloseRequest(event -> {
                if (cutindex != null && cutindex != -1 && goalschanged()) {
                    switch (Util.gui_getyesnocancelconfirmationdialog(Root, "Confirmation", "Unsaved Changes To " + ProgressAndGoalsUI.GOALCUTNAMES[cutindex], "Save Before Exiting")) {
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
        public Integer getCutindex() {
        return cutindex;
    }
        public void setCutindex(Integer cutindex) {
            this.cutindex = cutindex;
        }
        public kujiin.xml.Goals.Goal getselectedgoal() {return SelectedGoal;}

    // Cut Selection Methods
        public boolean goalschanged() {
            List<kujiin.xml.Goals.Goal> goalsfromxml = ProgressAndGoals.getGoal().getallcutgoals(ProgressAndGoals.getCutorelementindex(), ShowCompletedCheckBox.isSelected());
            System.out.println(CurrentGoalList.containsAll(goalsfromxml));
            System.out.println(goalsfromxml.size() + " Compared To: " + CurrentGoalList.size());
            return ! CurrentGoalList.containsAll(goalsfromxml) ||  goalsfromxml.size() != CurrentGoalList.size();
        }
        public void changecutselection(ActionEvent actionEvent) {
            if (getCutindex() != null) {
                if (goalschanged()) {
                    if (Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "You Have Made Unsaved Changes To " + GOALCUTNAMES[getCutindex()], "Save These Changes Before Changing Cuts?")) {
                        savechanges();
                    }
                }
            }
            int index = CutSelectorComboBox.getSelectionModel().getSelectedIndex();
            if (index == -1) {return;}
            setCutindex(index);
            ProgressAndGoals.selectcut(getCutindex());
            CurrentGoalTable.getItems().clear();
            RemoveGoalButton.setDisable(true);
            GoalPacingButton.setDisable(true);
            populatetable();
        }

    // Table Methods
        public void populatetable() {
            try {
                ObservableList<CurrentGoalBinding> currentGoals = FXCollections.observableArrayList();
                int cutindex = ProgressAndGoals.getCutorelementindex();
                CurrentGoalList = new ArrayList<>();
                String name = ProgressAndGoalsUI.GOALCUTNAMES[cutindex];
                if (cutindex != ProgressAndGoalsUI.GOALCUTNAMES.length - 1) {setTitle("View/Edit " + name + "'s Goals");}
                else {setTitle(name + " Goals");}
                int count = 1;
                for (kujiin.xml.Goals.Goal i : ProgressAndGoals.getGoal().getallcutgoals(cutindex, ShowCompletedCheckBox.isSelected())) {
                    currentGoals.add(new CurrentGoalBinding(count, Double.toString(i.getGoal_Hours()), i.getDate_Set(),
                            i.getpercentagecompleted(ProgressAndGoals.getSessions().sessioninformation_getallsessiontotals(cutindex, false)),
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
            } catch (ArrayIndexOutOfBoundsException ignored) {reset();}
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
                ProgressAndGoals.getGoal().update(CurrentGoalList, ProgressAndGoals.getCutorelementindex());
            }
        }
        public void savechanges() {
            if (getCutindex() != null && getCutindex() != -1) {
                ProgressAndGoals.getGoal().update(CurrentGoalList, getCutindex());
            }
        }

    // Dialog Methods
        public void closeDialog(Event event) {this.close();}
        public void reset() {
            CurrentGoalTable.getItems().clear();
        }
        @Override
        public void close() {
            if (getCutindex() != null && getCutindex() != -1) {
                if (Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "Unsaved Changes To " + GOALCUTNAMES[getCutindex()], "Save Changes Before Exiting?")) {
                    savechanges();
                }
                ProgressAndGoals.selectcut(getCutindex());
            }
            super.close();
        }

    // Goal pacing
        public void goalpacing(ActionEvent actionEvent) {
            if (getselectedgoal() != null && CurrentGoalList != null && getCutindex() != null) {
                new GoalPacingDialog(Root, getselectedgoal(), CurrentGoalList, Root.getProgressTracker().getSessions().sessioninformation_getallsessiontotals(cutindex, false)).showAndWait();
            }
        }
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

        public GoalPacingDialog(MainController root, kujiin.xml.Goals.Goal currentGoal, List<kujiin.xml.Goals.Goal> currentGoals, int cutindex) {
            Root = root;
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
            int practicedminutes = Root.getProgressTracker().getSessions().sessioninformation_getallsessiontotals(cutindex, false);
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
                Util.addscrolllistenerincrementdecrement(PracticeDays, 1, daystilldue, 1, false);
                Util.addupdownarrowlistenerincrementdecrement(PracticeDays, 1, daystilldue, 1, false);
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
        private ProgressAndGoalsUI progressAndGoalsUI;
        public Spinner<Integer> GoalHoursSpinner;
        public Spinner<Integer> GoalMinutesSpinner;
        public DatePicker GoalDatePicker;
        public Button CancelButton;
        public Button OKButton;
        public Button CurrentGoalsButton;
        public Label TopLabel;
        private LocalDate goaldate;
        private Double goalhours;
        private MainController Root;
        private int practicedminutes;
        private int goalminutes;

        public SetANewGoalForSingleCut(int cutindex, MainController root) {
            Root = root;
            progressAndGoalsUI = Root.getProgressTracker();
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
            GoalDatePicker.setValue(LocalDate.now());
            practicedminutes = Root.getProgressTracker().getSessions().sessioninformation_getallsessiontotals(cutindex, false);
            try {
                List<kujiin.xml.Goals.Goal> currentgoals = progressAndGoalsUI.getGoal().getallcutgoals(cutindex, false);
                System.out.println(currentgoals.size());
                goalminutes = Util.convertdecimalhourstominutes(progressAndGoalsUI.getGoal().getgoal(cutindex, currentgoals.size() - 1, false).getGoal_Hours());
                GoalHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(goalminutes / 60, Integer.MAX_VALUE, goalminutes / 60));
                GoalMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, goalminutes % 60));
            } catch (NullPointerException ignored) {
                goalminutes = 0;
                int hr = practicedminutes / 60;
                int min = practicedminutes % 60;
                GoalHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(hr, Integer.MAX_VALUE, hr));
                GoalMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, min));
            }
            Util.addscrolllistenerincrementdecrement(GoalHoursSpinner, 0, Integer.MAX_VALUE, 1, false);
            Util.addupdownarrowlistenerincrementdecrement(GoalHoursSpinner, 0, Integer.MAX_VALUE, 1, false);
            Util.addscrolllistenerincrementdecrement(GoalMinutesSpinner, 0, 59, 5, true);
            Util.addupdownarrowlistenerincrementdecrement(GoalMinutesSpinner, 0, 59, 5, true);
            if (cutindex != 11) TopLabel.setText("New Goal For " + GOALCUTNAMES[cutindex]);
            else TopLabel.setText("New Total Goal");
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
            int thisminutes = (GoalHoursSpinner.getValue() * 60) + GoalMinutesSpinner.getValue();
            if (thisminutes <= practicedminutes) {
                Util.gui_showinformationdialog(Root, "Cannot Set Goal", "Goal Time Must Be Higher Than Practiced Time " + Util.format_minstohrsandmins_long(practicedminutes), "Cannot Set Goal");
                setGoalhours(null);
                setGoaldate(null);
                return;
            }
            if (thisminutes > goalminutes) {
                Util.gui_showinformationdialog(Root, "Cannot Set Goal", "Goal Time Must Be Higher Than Practiced Time " + Util.format_minstohrsandmins_long(practicedminutes), "Cannot Set Goal");
                setGoalhours(null);
                setGoaldate(null);
                return;
            }
            if (! GoalDatePicker.getValue().isAfter(LocalDate.now())) {
                Util.gui_showinformationdialog(Root, "Cannot Set Goal",  "Due Date Must Be After Today", "Cannot Set Goal");
                setGoalhours(null);
                setGoaldate(null);
                return;
            }
            int hours = GoalHoursSpinner.getValue();
            int minutes = GoalMinutesSpinner.getValue();
            double newhours = Util.convert_hrsandminstodecimalhours(hours, minutes);
            setGoalhours(newhours);
            setGoaldate(GoalDatePicker.getValue());
            super.close();
        }
        public void viewcurrentgoals(Event event) {
            progressAndGoalsUI.opengoaleditor();}
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
                Util.addscrolllistenerincrementdecrement(GoalHoursSpinner, 0, Integer.MAX_VALUE, 1, false);
                Util.addupdownarrowlistenerincrementdecrement(GoalHoursSpinner, 0, Integer.MAX_VALUE, 1, false);
                GoalMinutesSpinner.setEditable(true);
                GoalMinutesSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                    int value = (GoalHoursSpinner.getValue() * 60) + newValue;
                    if (newvaluehigherthanmin(value)) {GoalMinutesSpinner.getValueFactory().setValue(newValue);}
                    else {GoalMinutesSpinner.getValueFactory().setValue(oldValue);}
                });
                Util.addscrolllistenerincrementdecrement(GoalMinutesSpinner, 0, 59, 5, true);
                Util.addupdownarrowlistenerincrementdecrement(GoalMinutesSpinner, 0, 59, 5, true);
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
            TopLabel.setText(currentGoal.getCutName() + " Goal Achieved");
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
                String cutname = i.getCutName();
                int cutindex = new ArrayList<>(Arrays.asList(ProgressAndGoalsUI.GOALCUTNAMES)).indexOf(cutname);
                String practicedhours = Double.toString(Util.convert_minstodecimalhours(Root.getProgressTracker().getSessions().sessioninformation_getallsessiontotals(cutindex, false), 2));
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

}
