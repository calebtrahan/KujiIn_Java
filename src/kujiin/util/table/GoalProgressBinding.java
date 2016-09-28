package kujiin.util.table;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by caleb on 9/28/16.
 */
public class GoalProgressBinding {
    public StringProperty name;
    public StringProperty practicedtime;
    public StringProperty currentgoaltime;
    public StringProperty percentcompleted;
    public StringProperty numbergoalscompleted;

    public GoalProgressBinding(String name, String practicedtime, String currentgoaltime, String percentcompleted, int numbergoalscompleted) {
        this.name = new SimpleStringProperty(name);
        this.practicedtime = new SimpleStringProperty(practicedtime);
        this.currentgoaltime = new SimpleStringProperty(currentgoaltime);
        this.percentcompleted = new SimpleStringProperty(percentcompleted);
        this.numbergoalscompleted = new SimpleStringProperty(String.valueOf(numbergoalscompleted));
    }
}
