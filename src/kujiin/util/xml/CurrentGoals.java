package kujiin.util.xml;

import java.util.List;

public class CurrentGoals {
    private List<CurrentGoal> CurrentGoal;

// Getters And Setters
    public List<kujiin.util.xml.CurrentGoal> getCurrentGoal() {return CurrentGoal;}
    public void setCurrentGoal(List<kujiin.util.xml.CurrentGoal> currentGoal) {CurrentGoal = currentGoal;}

// Other Methods
    public boolean goalsexist() {
        return getCurrentGoal() != null && getCurrentGoal().size() > 0;
    }
    public boolean deletegoal(Integer id) {
        if (goalsexist()) {
            List<CurrentGoal> templist = getCurrentGoal();
            for (kujiin.util.xml.CurrentGoal i : templist) {
                if (i.getID().equals(id)) {templist.remove(i); return true;}
            }
            return false;
        } else {return false;}
    }
    public CurrentGoal getgoal(Integer id) {
        if (getCurrentGoal() != null && getCurrentGoal().size() > 0) {
            for (kujiin.util.xml.CurrentGoal i : getCurrentGoal()) {
                if (i.getID().equals(id)) {return i;}
            }
            return null;
        } else {return null;}
    }

}
