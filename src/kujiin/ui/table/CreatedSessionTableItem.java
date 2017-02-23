package kujiin.ui.table;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CreatedSessionTableItem {
    public IntegerProperty number;
    public StringProperty itemname;
    public StringProperty duration;
    public StringProperty ambience;

    public CreatedSessionTableItem(int number, String itemname, String duration, String ambience) {
        this.number = new SimpleIntegerProperty(number);
        this.itemname = new SimpleStringProperty(itemname);
        this.duration = new SimpleStringProperty(duration);
        this.ambience = new SimpleStringProperty(ambience);
    }
}
