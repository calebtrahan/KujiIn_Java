package kujiin.ui.table;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SessionBrowserTableItem {
    public StringProperty date;
    public StringProperty items;
    public StringProperty duration;

    public SessionBrowserTableItem(String date, String items, String duration) {
        this.date = new SimpleStringProperty(date);
        this.items = new SimpleStringProperty(items);
        this.duration = new SimpleStringProperty(duration);
    }

}
