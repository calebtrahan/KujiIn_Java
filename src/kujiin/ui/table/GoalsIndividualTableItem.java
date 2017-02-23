package kujiin.ui.table;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GoalsIndividualTableItem {
    public StringProperty sessionitem;
    public StringProperty practicedtime;
    public StringProperty currentgoal;
    public StringProperty percentcompleted;
    public StringProperty goalscompleted;

    public GoalsIndividualTableItem(String sessionitem, String practicedtime, String currentgoal, String percentcompleted, String goalscompleted) {
        this.sessionitem = new SimpleStringProperty(sessionitem);
        this.practicedtime = new SimpleStringProperty(practicedtime);
        this.currentgoal = new SimpleStringProperty(currentgoal);
        this.percentcompleted = new SimpleStringProperty(percentcompleted);
        this.goalscompleted = new SimpleStringProperty(goalscompleted);
    }
}