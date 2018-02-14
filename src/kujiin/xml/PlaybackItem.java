package kujiin.xml;

import javafx.util.Duration;
import kujiin.util.Util;
import kujiin.util.enums.ReferenceType;
import org.apache.commons.lang3.time.StopWatch;

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
    public PlaybackItem(Session session, String name, PlaybackItemAmbience playbackItemAmbience) {
        this.session = session;
        this.Name = name;
        PracticeTime = 0.0;
        ambience = new Ambience();
        ambience.setAvailableAmbience(playbackItemAmbience.getAmbience());
    }

// Getters And Setters
    public PlaybackItemType getPlaybackItemType() {
        return playbackItemType;
    }
    public void setPlaybackItemType(PlaybackItemType playbackItemType) {
        this.playbackItemType = playbackItemType;
    }
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
    public void setPracticeTime(double practiceTime) {PracticeTime = practiceTime;}
    public String getdurationasString() {
        if (ExpectedDuration == 0.0 && ! RampOnly) {return "No Duration Set";}
        else {
            if (ExpectedDuration == 0.0 && RampOnly) {return "Ramp Only";}
            else {return Util.formatdurationtoStringDecimalWithColons(new Duration(getExpectedDuration()));}
        }
    }
    public String getAmbienceasString() {
        if (! ambience.hasPresetAmbience()) {return "No Ambience Set";}
        else {return "Ambience Set " + "(" + ambience.getSessionAmbience().size() + " Files)";}
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
    public Duration getTotalpracticetime(StopWatch stopWatch) {
        return totalpracticetime.add(Duration.millis(stopWatch.getTime()));
    }

// Utility Methods
    public void syncelapsedtime(StopWatch stopWatch) {
        PracticeTime = stopWatch.getTime();
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
                return new File(Preferences.DIRECTORYREFERENCE, "html/" + Name.toUpperCase() + ".html");
            }
            case txt: {
                return new File(Preferences.DIRECTORYREFERENCE, "txt/" + Name.toUpperCase() + ".txt");
            }
            default:
                return null;
        }
    }

    public enum PlaybackItemType {
        QIGONG, CUT, ELEMENT
    }
}
