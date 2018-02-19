package kujiin.ui.table;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import kujiin.util.Util;
import kujiin.xml.Session;

import java.util.UUID;

public class SessionBrowserTableItem {
    public UUID uuid;
    public StringProperty date;
    public StringProperty items;
    public StringProperty duration;

    public SessionBrowserTableItem(Session session) {
        this.uuid = session.getId();
        this.date = new SimpleStringProperty(session.getDate_Practiced().format(Util.dateFormat));
        this.items = new SimpleStringProperty(String.valueOf(session.getPlaybackItems().size()));
        this.duration = new SimpleStringProperty(Util.formatdurationtoStringDecimalWithColons(session.getSessionPracticedTime()));
    }

}
