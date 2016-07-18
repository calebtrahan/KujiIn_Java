package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class Total extends Meditatable {


    public Total(int number, String name, int duration, String briefsummary, This_Session thissession, ToggleButton aSwitch, TextField value) {
        super(number, name, duration, briefsummary, thissession, aSwitch, value);
    }


    @Override
    public double getAveragePracticeTime(boolean includepreandpost) {
        int totalminutes = 0;
        int sessioncount = 0;
        for (Meditatable i : thisession.getAllMeditatables()) {
            if (! includepreandpost && i instanceof Qi_Gong) {continue;}
            totalminutes += i.getTotalMinutesPracticed(false);
            sessioncount += i.getNumberOfSessionsPracticed(false);
        }
        try {
            return totalminutes / sessioncount;
        } catch (Exception ignored) {return 0.0;}
    }
    @Override
    public int getTotalMinutesPracticed(boolean includepreandpost) {
        int totalminutes = 0;
        for (Meditatable i : thisession.getAllMeditatables()) {
            if (! includepreandpost && i instanceof Qi_Gong) {continue;}
            totalminutes += i.getTotalMinutesPracticed(false);
        }
        try {return totalminutes;}
        catch (Exception ignored) {return 0;}
    }
    @Override
    public int getNumberOfSessionsPracticed(boolean includepreandpost) {
        int sessioncount = 0;
        for (Meditatable i : thisession.getAllMeditatables()) {
            if (! includepreandpost && i instanceof Qi_Gong) {continue;}
            sessioncount += i.getNumberOfSessionsPracticed(false);
        }
        try {return sessioncount;} catch (Exception ignored) {return 0;}
    }

}
