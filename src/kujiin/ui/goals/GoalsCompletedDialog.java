package kujiin.ui.goals;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.util.Util;
import kujiin.xml.AllGoals;
import kujiin.xml.Goal;
import kujiin.xml.PlaybackItemGoals;

public class GoalsCompletedDialog extends StyledStage {
    public Label TopLabel;
    public ListView<String> GoalsCompletedListView;

    public GoalsCompletedDialog(AllGoals allGoals) {
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
        TopLabel.setText("You Have Completed " + completedgoalcount + " Goals!");
    }
}
