package kujiin.util;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class Cut extends SessionPart {
    private String FocusPoint;
    private String Concept;
    private String Mantra_Meaning;
    private String Side_Effects;

    public Cut(int number, String name, This_Session thisession, ToggleButton aSwitch, TextField value) {
        super(number, name, thisession, aSwitch, value);
        if (thisession.Root.getOptions().getProgramOptions().getTooltips()) {
            Value.setTooltip(new Tooltip("Minutes You Want To Practice " + name));
        }
    }

// Description Methods
    public void setFocusPoint(String focusPoint) {
        FocusPoint = focusPoint;
    }
    public void setConcept(String concept) {
        Concept = concept;
    }
    public void setMantra_Meaning(String mantra_Meaning) {
        Mantra_Meaning = mantra_Meaning;
    }
    public void setSide_Effects(String side_Effects) {
        Side_Effects = side_Effects;
    }

    @Override
    public Tooltip getTooltip() {
        StringBuilder a = new StringBuilder();
        a.append("Focus Point: ").append(FocusPoint).append("\n");
        a.append("Concept: ").append(Concept).append("\n");
        a.append("Mantra Meaning: ").append(Mantra_Meaning).append("\n");
        a.append("Side Effects: ").append(Side_Effects).append("\n");
        return new Tooltip(a.toString());
    }

// Entrainment
    @Override
    public int entrainmentpartcount() {
        return 3;
    }

// Creation
    @Override
    public boolean creation_buildEntrainment() {
        if (thisession.Root.getOptions().getSessionOptions().getRampenabled()) {
            try {
                int index = allsessionpartstoplay.indexOf(this);
                SessionPart partafter = allsessionpartstoplay.get(index + 1);
                if (partafter instanceof Qi_Gong || partafter instanceof Element) {entrainment.setRampfile(entrainment.ramp_get(1));}
                else {entrainment.setRampfile(entrainment.ramp_get(0));}
                if (ramponly) {setDuration(Duration.millis(entrainment.getRampfile().getDuration()));}
                return super.creation_buildEntrainment() && entrainment.getRampfile().isValid();
            } catch (IndexOutOfBoundsException ignored) {return false;}
        }
        return super.creation_buildEntrainment();
    }

}
