package kujiin.util.table;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SessionItem {
    public IntegerProperty number;
    public StringProperty name;
    public StringProperty duration;
    public StringProperty ambiencesummary;
    public StringProperty goalsummary;

    public SessionItem(int number, String name, String duration, String ambiencesummary, String goalsummary) {
        this.number = new SimpleIntegerProperty(number);
        this.name = new SimpleStringProperty(name);
        this.duration = new SimpleStringProperty(duration);
        this.ambiencesummary = new SimpleStringProperty(ambiencesummary);
        this.goalsummary = new SimpleStringProperty(goalsummary);
    }
}
