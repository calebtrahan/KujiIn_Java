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
        this.presession = new SimpleStringProperty(convertduration(presession));
        this.rin = new SimpleStringProperty(convertduration(rin));
        this.kyo = new SimpleStringProperty(convertduration(kyo));
        this.toh = new SimpleStringProperty(convertduration(toh));
        this.sha = new SimpleStringProperty(convertduration(sha));
        this.kai = new SimpleStringProperty(convertduration(kai));
        this.jin = new SimpleStringProperty(convertduration(jin));
        this.retsu = new SimpleStringProperty(convertduration(retsu));
        this.zai = new SimpleStringProperty(convertduration(zai));
        this.zen = new SimpleStringProperty(convertduration(zen));
        this.earth = new SimpleStringProperty(convertduration(earth));
        this.air = new SimpleStringProperty(convertduration(air));
        this.fire = new SimpleStringProperty(convertduration(fire));
        this.water = new SimpleStringProperty(convertduration(water));
        this.Void = new SimpleStringProperty(convertduration(Void));
        this.postsession = new SimpleStringProperty(convertduration(postsession));
        this.total = new SimpleStringProperty(Util.formatdurationtoStringDecimalWithColons(total));
    }

    public String convertduration(Duration duration) {
        return String.valueOf((int) duration.toMinutes());
    }
    public String toString() {
        return String.format("%s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s",
                id.getValue(), datepracticed.getValue(), presession.getValue(), rin.getValue(), kyo.getValue(),
                toh.getValue(), sha.getValue(), kai.getValue(), jin.getValue(), retsu.getValue(), zai.getValue(),
                zen.getValue(), postsession.getValue(), total.getValue());
    }

}