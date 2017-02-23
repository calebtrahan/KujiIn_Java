package kujiin.ui.table;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SelectSessionRecentTableItem {
    public IntegerProperty number;
    public StringProperty practicedtime;

    public SelectSessionRecentTableItem(int number, String practicedtime) {
        this.number = new SimpleIntegerProperty(number);
        this.practicedtime = new SimpleStringProperty(practicedtime);
    }
}
