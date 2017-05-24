package kujiin.xml;

import javafx.util.Duration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class PlaybackItemGoals {
    private String PlaybackItemName;
    private List<Goal> Goals;
    @XmlTransient
    private List<Goal> GoalsCompletedThisSession;

    public PlaybackItemGoals() {}
    public PlaybackItemGoals(String name) {
        this.PlaybackItemName = name;
        Goals = new ArrayList<>();
        GoalsCompletedThisSession = new ArrayList<>();
    }

// Getters And Setters
    public List<Goal> getGoals() {
        if (Goals != null) {return Goals;}
        else {return new ArrayList<>();}
    }
    public void setGoals(List<Goal> goals) {
        Goals = goals;
    }
    public Goal getCurrentGoal() {
        if (Goals == null || Goals.isEmpty()) {return null;}
        return Goals.get(Goals.size() - 1);
    }
    public List<Goal> getCurrentGoals() {
        List<Goal> goalList = new ArrayList<>();
        for (Goal i : Goals) {
            if (! i.getCompleted()) {goalList.add(i);}
        }
        return goalList;
    }
    public List<Goal> getGoalsCompletedThisSession() {
        return GoalsCompletedThisSession;
    }
    public String getPlaybackItemName() {
        return PlaybackItemName;
    }

    // Utility Methods
    public void add(Goal goal) {
        if (Goals == null) {Goals = new ArrayList<>();}
        Goals.add(goal);
    }
    public void remove(Goal goal) {Goals.remove(goal);}

// Playback Methods
    public void checkifgoalscompleted(Duration practiceduration) {
        if (Goals == null) {return;}
        int index = 0;
        System.out.println(toString());
        for (Goal i : Goals) {
            if (! i.getCompleted() && practiceduration.greaterThanOrEqualTo(i.getDuration())) {
                i.setCompleted(true);
                i.setDate_Completed(LocalDate.now());
                Goals.set(index, i);
                if (GoalsCompletedThisSession == null) {GoalsCompletedThisSession = new ArrayList<>();}
                GoalsCompletedThisSession.add(i);
                System.out.println(toString());
            }
            index++;
        }
    }
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Goal i : Goals) {
            stringBuilder.append(i.toString());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
    public boolean goalscompletedthisession() {
        return GoalsCompletedThisSession != null && GoalsCompletedThisSession.size() > 0;
    }

}