package kujiin.ui.table;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableSessionItemNoAmbience {
    public IntegerProperty number;
    public StringProperty name;
    public StringProperty duration;

    public TableSessionItemNoAmbience(int number, String name, String duration) {
        this.number = new SimpleIntegerProperty(number);
        this.name = new SimpleStringProperty(name);
        this.duration = new SimpleStringProperty(duration);
    }
}
