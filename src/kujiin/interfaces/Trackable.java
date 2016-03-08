package kujiin.interfaces;

import kujiin.xml.Goals;

import java.util.List;

public interface Trackable {
    void setGoalsController(Goals goals);
    Goals getGoalsController();
    void setCurrentGoal();
    Goals.Goal getCurrentGoal();
    void setGoals(List<Goals.Goal> goalslist);
    List<Goals.Goal> getGoals(boolean includecompleted);
    void checkCurrentGoal(double currrentpracticedhours);

}
