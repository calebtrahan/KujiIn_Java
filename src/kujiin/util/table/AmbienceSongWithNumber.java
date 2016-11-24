package kujiin.util.table;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;
import kujiin.util.Util;
import kujiin.xml.SoundFile;

import java.io.File;

public class AmbienceSongWithNumber {
    public IntegerProperty number;
    public StringProperty name;
    public StringProperty length;
    private File file;
    private double duration;

    public AmbienceSongWithNumber(int id, SoundFile soundFile) {
        number = new SimpleIntegerProperty(id);
        name = new SimpleStringProperty(soundFile.getName());
        file = soundFile.getFile();
        duration = soundFile.getDuration();
        length = new SimpleStringProperty(Util.formatdurationtoStringDecimalWithColons(Duration.millis(duration)));
    }
    public AmbienceSongWithNumber(int id, AmbienceSong ambienceSong) {
        number = new SimpleIntegerProperty(id);
        name = new SimpleStringProperty(ambienceSong.getName());
        file = ambienceSong.getFile();
        duration = ambienceSong.getDuration();
        length = new SimpleStringProperty(Util.formatdurationtoStringDecimalWithColons(Duration.millis(duration)));
    }

    public String getName() {
        return name.getValue();
    }
    public File getFile() {
        return file;
    }
    public double getDuration() {return duration;}
    public void setNumber(int number) {
        this.number.set(number);
    }
}
