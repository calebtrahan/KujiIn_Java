package kujiin.xml;

import javafx.util.Duration;
import kujiin.util.Util;
import kujiin.util.enums.ReferenceType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.util.ArrayList;

@XmlAccessorType(XmlAccessType.FIELD)
public class PlaybackItem {
    @XmlTransient
    private Session session;
    protected int creationindex;
    protected int playbackindex;
    protected int typeindex;
    protected String Name;
    private double ExpectedDuration;
    private double PracticeTime;
    private boolean RampOnly;
    private Ambience ambience;
    private PlaybackItemEntrainment playbackItemEntrainment;
    private PlaybackItemType playbackItemType;
    @XmlTransient
    private Duration totalpracticetime;
    @XmlTransient
    private ArrayList<Goal> GoalsCompletedThisSession;

    public PlaybackItem() {}
    public PlaybackItem(Session session, String name) {
        this.session = session;
        this.Name = name;
        PracticeTime = 0.0;
        ambience = new Ambience();
    }

// Getters And Setters
    public int getCreationindex() {
        return creationindex;
    }
    public void setCreationindex(int creationindex) {
        this.creationindex = creationindex;
    }
    public void setPlaybackindex(int playbackindex) {
            this.playbackindex = playbackindex;
        }
    public int getPlaybackindex() {
        return playbackindex;
    }
    public boolean isRampOnly() {
        return RampOnly;
    }
    public void setRampOnly(boolean rampOnly) {
        this.RampOnly = rampOnly;
    }
    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }
    public int getTypeindex() {
        return typeindex;
    }
    public void setTypeindex(int typeindex) {
        this.typeindex = typeindex;
    }
    public void setExpectedDuration(double expectedDuration) {
        ExpectedDuration = expectedDuration;
    }
    public double getExpectedDuration() {
            return ExpectedDuration;
        }
    public double getPracticeTime() {
        return PracticeTime;
    }
    public String getdurationasString(double maxchars) {
        if (ExpectedDuration == 0.0 && ! RampOnly) {return "No Duration Set";}
        else {
            if (ExpectedDuration == 0.0 && RampOnly) {return "Ramp Only";}
            else {return Util.formatdurationtoStringSpelledOut(new Duration(getExpectedDuration()), maxchars);}
        }
    }
    public String getAmbienceasString() {
        if (ambience.getAmbience() == null || ambience.getAmbience().isEmpty()) {return "No Ambience Set";}
        else {return "Ambience Set " + "(" + ambience.getAmbience().size() + " Files)";}
    }
    public void updateduration(Duration duration) {this.ExpectedDuration = duration.toMillis();}
    public ArrayList<Goal> getGoalsCompletedThisSession() {
        return GoalsCompletedThisSession;
    }
    public Ambience getAmbience() {
        return ambience;
    }
    public void setAmbience(Ambience ambience) {
        this.ambience = ambience;
    }
    public Duration getTotalpracticetime() {
        return totalpracticetime;
    }

// Utility Methods
    public void addelapsedtime(Duration duration) {
        PracticeTime = PracticeTime + duration.toMillis();
        totalpracticetime = totalpracticetime.add(duration);
    }
    public void addCompletedGoal(Goal Goal) {
        if (GoalsCompletedThisSession == null) {
            GoalsCompletedThisSession = new ArrayList<>();}
        GoalsCompletedThisSession.add(Goal);
    }
    public boolean isValid() {return javafx.util.Duration.seconds(ExpectedDuration).greaterThan(javafx.util.Duration.ZERO);}
    public void calculatetotalpracticetime(Sessions sessions) {
        totalpracticetime = sessions.gettotalpracticedtime(this, false);
    }
    public void resetpracticetime() {
        PracticeTime = 0.0;
    }

// Reference
    public File getReferenceFile(ReferenceType referenceType) {
        switch (referenceType) {
            case html: {
                return new File(Preferences.DIRECTORYREFERENCE, "html/" + Name + ".html");
            }
            case txt: {
                return new File(Preferences.DIRECTORYREFERENCE, "txt/" + Name + ".txt");
            }
            default:
                return null;
        }
    }

    enum PlaybackItemType {
        QIGONG, CUT, ELEMENT
    }
}
