package kujiin.util.table;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;
import kujiin.util.Util;
import kujiin.xml.SoundFile;

import java.io.File;

public class AmbienceSong {
    public StringProperty name;
    public StringProperty length;
    private File file;
    private double duration;

    public AmbienceSong(SoundFile soundFile) {
        this.name = new SimpleStringProperty(soundFile.getName());
        this.file = soundFile.getFile();
        duration = soundFile.getDuration();
        this.length = new SimpleStringProperty(Util.formatdurationtoStringSpelledOut(new Duration(duration), null));
    }

    public String getName() {
        return name.getValue();
    }
    public File getFile() {
        return file;
    }
    public double getDuration() {return duration;}
}
