package kujiin.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class CreateANewGoalDialog extends Stage {
    public Spinner<Integer> GoalHours;
    public DatePicker DueDate;
    public Button SetGoalButton;
    public Button CloseButton;
    private LocalDate GoalDate;
    private Integer goalhours;

    public CreateANewGoalDialog(LocalDate todaysdate, Integer currentpracticedhours) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/CreateNewGoalDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Change All Values To:");}
        catch (IOException e) {e.printStackTrace();}
        GoalHours.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(currentpracticedhours, Integer.MAX_VALUE, currentpracticedhours));
    }

// Getters And Setters
    public LocalDate getGoalDate() {return GoalDate;}
    public void setGoalDate(LocalDate goalDate) {GoalDate = goalDate;}
    public Integer getGoalhours() {return goalhours;}
    public void setGoalhours(Integer goalhours) {this.goalhours = goalhours;}

// Button Actions
    public void setgoalandclose(ActionEvent actionEvent) {
        super.close();
    }
    public void closedialog(ActionEvent actionEvent) {super.close();}

}
