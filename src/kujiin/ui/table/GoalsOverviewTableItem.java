package kujiin.ui.table;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GoalsOverviewTableItem {
    public StringProperty goaltime;
    public StringProperty iscompleted;
    public StringProperty datecompleted;
    public StringProperty percentcompleted;

    public GoalsOverviewTableItem(String goaltime, boolean iscompleted, String datecompleted, String percentcompleted) {
        this.goaltime = new SimpleStringProperty(goaltime);
        if (iscompleted) {this.iscompleted = new SimpleStringProperty("Yes");}
        else {this.iscompleted = new SimpleStringProperty("No");}
        this.datecompleted = new SimpleStringProperty(datecompleted);
        this.percentcompleted = new SimpleStringProperty(percentcompleted);
    }
}
