package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.util.Duration;
import kujiin.ui.MainController;

public class Total extends SessionPart {


    public Total(int number, String name, MainController Root, ToggleButton aSwitch, TextField value) {
        super(number, name, Root, aSwitch, value);
        this.number = number;
        this.name = name;
        this.duration = Duration.ZERO;
    }

    @Override
    public Duration sessions_getPracticedDuration(Boolean includepreandpostoverride) {
        boolean includepreandpost;
        if (includepreandpostoverride != null) {includepreandpost = includepreandpostoverride;}
        else {includepreandpost = sessions_includepreandpost();}
        Duration duration = Duration.ZERO;
        for (SessionPart i : root.getAllSessionParts(false)) {
            if (! includepreandpost && i instanceof Qi_Gong) {continue;}
            duration = duration.add(i.sessions_getPracticedDuration(false));
        }
        return duration;
    }
    @Override
    public int sessions_getPracticedSessionCount(Boolean includepreandpostoverride) {
        return root.getProgressTracker().getSessions().totalsessioncount();
    }
    @Override
    public Duration sessions_getAverageSessionLength(Boolean includepreandpostoverride) {
        boolean includepreandpost;
        if (includepreandpostoverride != null) {includepreandpost = includepreandpostoverride;}
        else {includepreandpost = sessions_includepreandpost();}
        return new Duration(sessions_getPracticedDuration(includepreandpost).toMillis() / sessions_getPracticedSessionCount(includepreandpost));
    }

}
