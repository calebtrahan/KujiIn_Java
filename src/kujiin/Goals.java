package kujiin;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import kujiin.dialogs.DisplayCompletedGoalsDialog;
import kujiin.dialogs.DisplayCurrentGoalsDialog;
import kujiin.dialogs.GoalPacingDialog;
import kujiin.dialogs.SetANewGoalDialog;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class Goals {
    Database db;
    Root root;
    public ArrayList<CurrentGoal> currentgoals = new ArrayList<>();
    public ArrayList<CompletedGoal> completedgoals = new ArrayList<>();

    public Goals(Database sessiondb, Root root) {
        db = sessiondb;
        this.root = root;
    }

    public void updategoaltracker() {
        root.goalscurrrentvalueLabel.setText(String.format("%.2f", db.gettotalpracticedhours()));
        double totalhours;
        try {
            ResultSet rs = db.stmt.executeQuery("SELECT HOURS FROM GOALS");
            if (rs.next()) {totalhours = rs.getDouble(1);}
            else {totalhours = 0;}
        } catch (SQLException e) {e.printStackTrace(); totalhours = 0;}
        if (totalhours > 0) {
            System.out.println(String.format("Practiced Hours: %s, Goal Hours: %s", db.gettotalpracticedhours(), totalhours));
            if (db.gettotalpracticedhours() > 0.0) {
                double progress = (totalhours / db.gettotalpracticedhours());
                root.goalsprogressbar.setProgress(progress);
            } else {
                root.goalsprogressbar.setProgress(0.0);
            }
            root.goalssettimeLabel.setText(String.format("%.2f", totalhours));
        } else {
            root.goalscurrrentvalueLabel.setText("No Goal Set");
            root.goalsprogressbar.setProgress(0.0);
            root.goalssettimeLabel.setText("No Goal Set");
        }
    }

    // Check The Goal To See If It's After All Others And > Hours Practiced And Other Goal Hours
    public boolean checkgoal(String duedate, double alreadypracticedhours, double goalhours) {
        try {
            if (goalhours > 0) {
                Date date = db.dateFormat.parse(duedate);
                if (date.after(new Date())) {
                    double currenthours = db.gettotalpracticedhours();
                    if (currenthours <= goalhours) {
                        ResultSet rs = db.stmt.executeQuery("SELECT Hours FROM Goals");
                        int count = 0;
                        while (rs.next()) {
                            if (goalhours <= rs.getDouble("Hours")) {
                                Alert b = new Alert(Alert.AlertType.WARNING);
                                b.setTitle("Cannot Add Goal");
                                b.setHeaderText("Unable To Add Goal");
                                b.setContentText("Goal Must Be Greater Than You Other Goals");
                                b.showAndWait();
                                return false;
                            }
                        }
                        return true;
                    } else {
                        Alert b = new Alert(Alert.AlertType.WARNING);
                        b.setTitle("Cannot Add Goal");
                        b.setHeaderText("Unable To Add Goal");
                        b.setContentText("You Have Already Exceeded Or Surpassed " + goalhours + " Hours");
                        b.showAndWait();
                        return false;
                    }
                } else {
                    Alert b = new Alert(Alert.AlertType.WARNING);
                    b.setTitle("Cannot Add Goal");
                    b.setHeaderText("Unable To Add Goal");
                    b.setContentText("Date Must Be After Today");
                    b.showAndWait();
                    return false;
                }
            } else {
                Alert b = new Alert(Alert.AlertType.WARNING);
                b.setTitle("Cannot Add Goal");
                b.setHeaderText("Unable To Add Goal");
                b.setContentText("Goal Hours Needs To Be Greater Than Zero");
                b.showAndWait();
                return false;
            }
        } catch (ParseException |SQLException e) {e.printStackTrace(); return false;}
    }

    // Insert/Set A New Goal
    public boolean insertgoal(String duedate, double goalhours) {
        try {
            PreparedStatement a = db.c.prepareStatement("INSERT INTO Goals ( HOURS, DUEDATE ) VALUES (?, ?)");
            a.setInt(1, (int) goalhours);
            a.setString(2, duedate);
            a.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check If Goal(s) Completed. If So -> Display Dialog Congratulating User
    public void checkifgoalcompleted() {
        double currenthours = db.gettotalpracticedhours();
        ArrayList<Integer> idstomovetocompleted = new ArrayList<>();
        if (currenthours > 0.0) {
            try {
                ResultSet rs = db.stmt.executeQuery("SELECT Hours FROM Goals");
                int count = 0;
                double totalhours = 0.0;
                while (rs.next()) {
                    if (currenthours > rs.getDouble(count)) {
                        idstomovetocompleted.add((int) rs.getDouble(count));
                    }
                }
                if (idstomovetocompleted.size() > 0) {
                    if (idstomovetocompleted.size() > 1) {
                        // TODO Dialog Here Multiple Goals Completed!!!
                    } else {
                        // TODO Dialog Here Single Goal Completed!!!
                    }
                }
            } catch (SQLException e) {e.printStackTrace();}
        }
    }

    // Sets A New Goal
    public void setnewgoal() {
        getcurrentgoals();
        SetANewGoalDialog b = new SetANewGoalDialog(null, db, this);
        b.showAndWait();
    }

    // Gets The Estimated Time Each Day To Goal Completion
    public void getgoalpacing() {
        getcurrentgoals();
        if (currentgoals.size() > 0) {
            GoalPacingDialog a = new GoalPacingDialog(null, this);
            a.showAndWait();
        } else {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("No Goals Set");
            a.setHeaderText("Cannot Calculate Goal Pacing");
            a.setContentText("At Least One Goal Must Be Set, Please Set A Goal");
            a.showAndWait();
        }
    }

    // Display A List Of All Current Goals
    public void viewcurrentgoals() {
        getcurrentgoals();
        if (currentgoals.size() > 0) {
            DisplayCurrentGoalsDialog a = new DisplayCurrentGoalsDialog(null, this);
            a.showAndWait();
        } else {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("No Goals Set");
            a.setHeaderText("Nothing To Display");
            a.setContentText("Please Set A Goal");
            a.showAndWait();
        }
    }

    // Display A List of All Completed Goals
    public void viewcompletedgoals() {
        getcompletedgoals();
        if (completedgoals.size() > 0) {
            DisplayCompletedGoalsDialog a = new DisplayCompletedGoalsDialog(null, db, this);
            a.showAndWait();
        } else {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("No Goals Completed");
            a.setHeaderText("You Haven't Completed A Goal Yet");
            a.setContentText("Keep Up The Hard Work And You'll Complete A Goal Soon");
            a.showAndWait();
        }
    }

    // Get Current Goals From Database And add to currentgoals Field
    public void getcurrentgoals() {
        if (currentgoals.size() > 0) {currentgoals.clear();}
        try {
            ResultSet rs = db.stmt.executeQuery("Select * From Goals");
            int count = 1;
            while (rs.next()) {
                System.out.println(String.format("Iteration %s's 1st Value Is : %s", count, rs.getString(1)));
                currentgoals.add(new CurrentGoal(count, rs.getString(1), rs.getString(2)));
                count++;
            }
        } catch (SQLException e) {e.printStackTrace();}
    }

    // Get Completed Goals From Database And Add to completedgoals Field
    public void getcompletedgoals() {
        if (completedgoals.size() > 0) {completedgoals.clear();}
        try {
            ResultSet rs = db.stmt.executeQuery("SELECT * FROM CompletedGoals");
            int count = 1;
            while (rs.next()) {
                completedgoals.add(new CompletedGoal(count, rs.getString(2), rs.getString(3)));
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Moves A Goal From Current Goals To Completed Goals
    public void movegoaltocompleted(Integer goalid) {
        try {
            ResultSet rs = db.stmt.executeQuery("SELECT HOURS FROM GOALS WHERE ID=" + goalid);
            int hours = rs.getInt(0);
            db.stmt.executeUpdate("DELETE FROM GOALS WHERE ID=" + goalid);
            db.stmt.executeUpdate("INSERT INTO CompletedGoals ( HOURS, DATECOMPLETED ) VALUES (" + hours + ", " + Tools.gettodaysdate() + ")");
        } catch (SQLException e) {e.printStackTrace();}
    }

    public class CurrentGoal {
        private IntegerProperty goalid;
        private StringProperty goalhours;
        private StringProperty duedate;
        private StringProperty percentcomplete;

        public CurrentGoal(int id, String goalhours, String duedate) {
            this.goalid = new SimpleIntegerProperty(id);
            this.goalhours = new SimpleStringProperty(goalhours);
            double percentcomplete = (db.gettotalpracticedhours() / Double.parseDouble(goalhours)) * 100;
            this.duedate = new SimpleStringProperty(duedate);
            this.percentcomplete = new SimpleStringProperty(String.valueOf(percentcomplete) + "%");
        }
    }

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
