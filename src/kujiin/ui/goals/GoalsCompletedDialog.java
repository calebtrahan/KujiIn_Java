package kujiin.ui.goals;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.util.Util;
import kujiin.xml.AllGoals;
import kujiin.xml.Goal;
import kujiin.xml.PlaybackItemGoals;

import java.io.IOException;

public class GoalsCompletedDialog extends StyledStage {
    public Label TopLabel;
    public ListView<String> GoalsCompletedListView;

    public GoalsCompletedDialog(AllGoals allGoals) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/goals/GoalsCompletedDialog.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setTitle("Goals Completed");
            ObservableList<String> goalscompleted = FXCollections.observableArrayList();
            int completedgoalcount = 0;
            for (int i = 0; i < 16; i++) {
                PlaybackItemGoals playbackItemGoals = allGoals.getplaybackItemGoals(i);
                if (playbackItemGoals.goalscompletedthisession()) {
                    for (Goal x : playbackItemGoals.getGoalsCompletedThisSession()) {
                        goalscompleted.add(String.format("%s Goal Completed (%s)", playbackItemGoals.getPlaybackItemName(), Util.formatdurationtoStringDecimalWithColons(x.getDuration())));
                        completedgoalcount++;
                    }
                }
            }
            GoalsCompletedListView.setItems(goalscompleted);
            TopLabel.setText("You Have Completed " + completedgoalcount + " Goals!");
        } catch (IOException e) {e.printStackTrace();}
    }
}
