package kujiin.util.table;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;
import kujiin.util.Util;

public class SessionRow {
    public IntegerProperty id;
    public StringProperty datepracticed;
    public StringProperty presession;
    public StringProperty rin;
    public StringProperty kyo;
    public StringProperty toh;
    public StringProperty sha;
    public StringProperty kai;
    public StringProperty jin;
    public StringProperty retsu;
    public StringProperty zai;
    public StringProperty zen;
    public StringProperty earth;
    public StringProperty air;
    public StringProperty fire;
    public StringProperty water;
    public StringProperty Void;
    public StringProperty postsession;
    public StringProperty total;

    public SessionRow(int id, String datepracticed, Duration presession, Duration rin, Duration kyo, Duration toh, Duration sha, Duration kai, Duration jin,
                      Duration retsu, Duration zai, Duration zen, Duration postsession, Duration earth, Duration air, Duration fire, Duration water,
                      Duration Void, Duration total) {
        this.id = new SimpleIntegerProperty(id);
        this.datepracticed = new SimpleStringProperty(datepracticed);
        this.presession = new SimpleStringProperty(formatduration(presession));
        this.rin = new SimpleStringProperty(formatduration(rin));
        this.kyo = new SimpleStringProperty(formatduration(kyo));
        this.toh = new SimpleStringProperty(formatduration(toh));
        this.sha = new SimpleStringProperty(formatduration(sha));
        this.kai = new SimpleStringProperty(formatduration(kai));
        this.jin = new SimpleStringProperty(formatduration(jin));
        this.retsu = new SimpleStringProperty(formatduration(retsu));
        this.zai = new SimpleStringProperty(formatduration(zai));
        this.zen = new SimpleStringProperty(formatduration(zen));
        this.earth = new SimpleStringProperty(formatduration(earth));
        this.air = new SimpleStringProperty(formatduration(air));
        this.fire = new SimpleStringProperty(formatduration(fire));
        this.water = new SimpleStringProperty(formatduration(water));
        this.Void = new SimpleStringProperty(formatduration(Void));
        this.postsession = new SimpleStringProperty(formatduration(postsession));
        this.total = new SimpleStringProperty(Util.formatdurationtoStringDecimalWithColons(total));
    }

    public String formatduration(Duration duration) {
        return String.format("%.1f", duration.toMinutes());
    }
    public String toString() {
        return String.format("%s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s",
                id.getValue(), datepracticed.getValue(), presession.getValue(), rin.getValue(), kyo.getValue(),
                toh.getValue(), sha.getValue(), kai.getValue(), jin.getValue(), retsu.getValue(), zai.getValue(),
                zen.getValue(), postsession.getValue(), total.getValue());
    }

}