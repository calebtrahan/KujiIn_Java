package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class Total extends Meditatable {


    public Total(int number, String name, int duration, String briefsummary, This_Session thissession, ToggleButton aSwitch, TextField value) {
        super(number, name, duration, briefsummary, thissession, aSwitch, value);
    }


    @Override
    public double getAveragePracticeTime() {
        int totalminutes = 0;
        int sessioncount = 0;
        for (Meditatable i : thisession.getAllMeditatables()) {
            totalminutes += i.getTotalMinutesPracticed();
            sessioncount += i.getNumberOfSessionsPracticed();
        }
        try {
            return totalminutes / sessioncount;
        } catch (Exception ignored) {return 0.0;}
    }
    @Override
    public int getTotalMinutesPracticed() {
        int totalminutes = 0;
        for (Meditatable i : thisession.getAllMeditatables()) {
            totalminutes += i.getTotalMinutesPracticed();
        }
        try {return totalminutes;}
        catch (Exception ignored) {return 0;}
    }
    @Override
    public int getNumberOfSessionsPracticed() {
        int sessioncount = 0;
        for (Meditatable i : thisession.getAllMeditatables()) {sessioncount += i.getNumberOfSessionsPracticed();}
        try {return sessioncount;} catch (Exception ignored) {return 0;}
    }

}
