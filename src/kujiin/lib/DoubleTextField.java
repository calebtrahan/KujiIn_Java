package kujiin.lib;

import javafx.scene.control.TextField;
import kujiin.util.Util;

public class DoubleTextField extends TextField {
    private double minvalue;
    private double maxvalue;
    private double increment;
    private int decimalplaces;

    public DoubleTextField(double minvalue, double maxvalue, double increment, int decimalplaces) {
        super();
        this.minvalue = minvalue;
        this.maxvalue = maxvalue;
        this.increment = increment;
        this.decimalplaces = decimalplaces;
        setuplisteners();
    }
    public DoubleTextField(String text, double minvalue, double maxvalue, double increment, int decimalplaces) {
        super(text);
        this.minvalue = minvalue;
        this.maxvalue = maxvalue;
        this.increment = increment;
        this.decimalplaces = decimalplaces;
        setuplisteners();
    }

    public void setuplisteners() {
        setOnScroll(event -> {
            Double newvalue = new Double(getText());
            boolean validvalue;
            if (event.getDeltaY() < 0) {newvalue -= increment; validvalue = newvalue >= minvalue;} else {newvalue += increment; validvalue = newvalue <= maxvalue;}
            if (validvalue) {
                if (decimalplaces > 0) {setText(Util.rounddouble(newvalue, decimalplaces).toString());}
                else {setText(String.valueOf(newvalue.intValue()));}
            }
        });
        setOnKeyPressed(event -> {
            Double newvalue = new Double(getText());
            boolean validvalue;
            switch (event.getCode()) {
                case UP:
                case PAGE_UP:
                    newvalue += increment;
                    validvalue = newvalue <= maxvalue;
                    break;
                case DOWN:
                case PAGE_DOWN:
                    newvalue -= increment;
                    validvalue = newvalue >= minvalue;
                    break;
                default:
                    validvalue = false;
            }
            if (validvalue) {
                if (decimalplaces > 0) {setText(Util.rounddouble(newvalue, decimalplaces).toString());}
                else {setText(String.valueOf(newvalue.intValue()));}
            }
        });
    }
}
