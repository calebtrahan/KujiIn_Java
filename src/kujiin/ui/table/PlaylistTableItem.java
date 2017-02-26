package kujiin.ui.table;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PlaylistTableItem {
    public StringProperty itemname;
    public StringProperty duration;
    public StringProperty percentcompleted;

    public PlaylistTableItem(String itemname, String duration, String percentcompleted) {
        this.itemname = new SimpleStringProperty(itemname);
        this.duration = new SimpleStringProperty(duration);
        this.percentcompleted = new SimpleStringProperty(percentcompleted);
    }

}
