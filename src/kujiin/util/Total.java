package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.util.Duration;

public class Total extends SessionPart {


    public Total(int number, String name, String briefsummary, This_Session thissession, ToggleButton aSwitch, TextField value) {
        this.number = number;
        this.name = name;
        this.duration = Duration.ZERO;
        this.thisession = thissession;
    }

    @Override
    public Duration sessions_getPracticedDuration(Boolean includepreandpostoverride) {
        boolean includepreandpost;
        if (includepreandpostoverride != null) {includepreandpost = includepreandpostoverride;}
        else {includepreandpost = sessions_includepreandpost();}
        Duration duration = Duration.ZERO;
        for (SessionPart i : thisession.getAllSessionParts()) {
            if (! includepreandpost && i instanceof Qi_Gong) {continue;}
            duration = duration.add(thisession.Root.getSessions().gettotalpracticedtime(number, false));
        }
        return duration;
    }
    @Override
    public int sessions_getPracticedSessionCount(Boolean includepreandpostoverride) {
        return thisession.Root.getSessions().totalsessioncount();
    }
    @Override
    public Duration sessions_getAverageSessionLength(Boolean includepreandpostoverride) {
        boolean includepreandpost;
        if (includepreandpostoverride != null) {includepreandpost = includepreandpostoverride;}
        else {includepreandpost = sessions_includepreandpost();}
        return new Duration(sessions_getPracticedDuration(includepreandpost).toMillis() / sessions_getPracticedSessionCount(includepreandpost));
    }

}
