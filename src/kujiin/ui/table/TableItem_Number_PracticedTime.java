package kujiin.ui.table;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableItem_Number_PracticedTime {
    public IntegerProperty number;
    public StringProperty practicedtime;

    public TableItem_Number_PracticedTime(int number, String practicedtime) {
        this.number = new SimpleIntegerProperty(number);
        this.practicedtime = new SimpleStringProperty(practicedtime);
    }
}
