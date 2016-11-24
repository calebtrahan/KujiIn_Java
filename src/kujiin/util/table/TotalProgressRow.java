package kujiin.util.table;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TotalProgressRow {
    public StringProperty name;
    public StringProperty formattedduration;

    public TotalProgressRow(String name, String formattedduration) {
        this.name = new SimpleStringProperty(name);
        this.formattedduration = new SimpleStringProperty(formattedduration);
    }
}
