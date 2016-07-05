package kujiin.lib;

import javafx.scene.control.TextField;

/**
 * Created by caleb on 7/3/16.
 */
public class IntegerTextField extends TextField {
    private int minvalue;
    private int maxvalue;
    private int increment;

    public IntegerTextField(TextField parent, int minvalue, int maxvalue, int increment) {
        super();
        this.minvalue = minvalue;
        this.maxvalue = maxvalue;
        this.increment = increment;
        setuplisteners();
    }
    public IntegerTextField(TextField parent, String text, int minvalue, int maxvalue, int increment) {
        super(text);
        this.minvalue = minvalue;
        this.maxvalue = maxvalue;
        this.increment = increment;
        setuplisteners();
    }

    public void setuplisteners() {
        setOnScroll(event -> {
            Integer newvalue = new Integer(getText());
            boolean validvalue;
            if (event.getDeltaY() < 0) {newvalue -= increment; validvalue = newvalue >= minvalue;} else {newvalue += increment; validvalue = newvalue <= maxvalue;}
            if (validvalue) {setText(String.valueOf(newvalue.intValue()));}
        });
        setOnKeyPressed(event -> {
            Integer newvalue = new Integer(getText());
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
            if (validvalue) {setText(String.valueOf(newvalue.intValue()));}
        });
    }
}
