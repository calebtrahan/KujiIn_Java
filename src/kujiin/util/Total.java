package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.util.Duration;

public class Total extends Meditatable {


    public Total(int number, String name, int duration, String briefsummary, This_Session thissession, ToggleButton aSwitch, TextField value) {
        this.number = number;
        this.name = name;
        this.duration = new Duration((duration * 60) * 1000);
        this.thisession = thissession;
    }


    @Override
    public double sessions_getAveragePracticeTime(boolean includepreandpost) {
        int totalminutes = 0;
        int sessioncount = 0;
        for (Meditatable i : thisession.getAllMeditatables()) {
            if (! includepreandpost && i instanceof Qi_Gong) {continue;}
            totalminutes += i.sessions_getTotalMinutesPracticed(false);
            sessioncount += i.sessions_getNumberOfSessionsPracticed(false);
        }
        try {
            return totalminutes / sessioncount;
        } catch (Exception ignored) {return 0.0;}
    }
    @Override
    public int sessions_getTotalMinutesPracticed(boolean includepreandpost) {
        int totalminutes = 0;
        for (Meditatable i : thisession.getAllMeditatables()) {
            if (! includepreandpost && i instanceof Qi_Gong) {continue;}
            totalminutes += i.sessions_getTotalMinutesPracticed(false);
        }
        try {return totalminutes;}
        catch (Exception ignored) {return 0;}
    }
    @Override
    public int sessions_getNumberOfSessionsPracticed(boolean includepreandpost) {
        int sessioncount = 0;
        for (Meditatable i : thisession.getAllMeditatables()) {
            if (! includepreandpost && i instanceof Qi_Gong) {continue;}
            sessioncount += i.sessions_getNumberOfSessionsPracticed(false);
        }
        try {return sessioncount;} catch (Exception ignored) {return 0;}
    }

}
