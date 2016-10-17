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

    public SessionRow(int id, String datepracticed, int presession, int rin, int kyo, int toh, int sha, int kai, int jin, int retsu,
                      int zai, int zen, int postsession, int earth, int air, int fire, int water, int Void, int total) {
        this.id = new SimpleIntegerProperty(id);
        this.datepracticed = new SimpleStringProperty(datepracticed);
        this.presession = new SimpleStringProperty(String.valueOf(presession));
        this.rin = new SimpleStringProperty(String.valueOf(rin));
        this.kyo = new SimpleStringProperty(String.valueOf(kyo));
        this.toh = new SimpleStringProperty(String.valueOf(toh));
        this.sha = new SimpleStringProperty(String.valueOf(sha));
        this.kai = new SimpleStringProperty(String.valueOf(kai));
        this.jin = new SimpleStringProperty(String.valueOf(jin));
        this.retsu = new SimpleStringProperty(String.valueOf(retsu));
        this.zai = new SimpleStringProperty(String.valueOf(zai));
        this.zen = new SimpleStringProperty(String.valueOf(zen));
        this.earth = new SimpleStringProperty(String.valueOf(earth));
        this.air = new SimpleStringProperty(String.valueOf(air));
        this.fire = new SimpleStringProperty(String.valueOf(fire));
        this.water = new SimpleStringProperty(String.valueOf(water));
        this.Void = new SimpleStringProperty(String.valueOf(Void));
        this.postsession = new SimpleStringProperty(String.valueOf(postsession));
        this.total = new SimpleStringProperty(Util.formatdurationtoStringDecimalWithColons(new Duration(total * 1000)));
    }

    public String toString() {
        return String.format("%s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s",
                id.getValue(), datepracticed.getValue(), presession.getValue(), rin.getValue(), kyo.getValue(),
                toh.getValue(), sha.getValue(), kai.getValue(), jin.getValue(), retsu.getValue(), zai.getValue(),
                zen.getValue(), postsession.getValue(), total.getValue());
    }

    public StringProperty getDatepracticed() {
        return datepracticed;
    }
}