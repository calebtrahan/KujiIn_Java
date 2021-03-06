package kujiin.ui.table;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableItem_Number_Name_Duration_Ambience {
    public IntegerProperty number;
    public StringProperty name;
    public StringProperty duration;
    public StringProperty ambience;

    public TableItem_Number_Name_Duration_Ambience(int number, String name, String duration, String haspresetambience) {
        this.number = new SimpleIntegerProperty(number);
        this.name = new SimpleStringProperty(name);
        this.duration = new SimpleStringProperty(duration);
        this.ambience = new SimpleStringProperty(haspresetambience);
    }
}