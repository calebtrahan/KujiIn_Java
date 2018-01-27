package kujiin.xml;

import javafx.util.Duration;
import kujiin.util.enums.FreqType;
import org.apache.commons.lang3.time.StopWatch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static kujiin.util.Util.dateFormat;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Session {
    private UUID id;
    private String Date_Practiced;
    private ArrayList<PlaybackItem> playbackItems;
    private Double ExpectedSessionDuration;
    private Double SessionPracticedTime;
    private String Notes;
    private FreqType freqType;
    private boolean missedsession;
    private int playcount;
    private int completedcount;
    private String timestarted;
    private String timefinished;
    private boolean completed = false;
    private List<Break> breaks;
    @XmlTransient
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy MM dd H m s S");
    @XmlTransient
    private Break currentbreak;
    private AvailableAmbiences availableAmbiences;

    public Session() {

    }
    public Session(AvailableAmbiences availableAmbiences) {
        newID();
        setDate_Practiced(LocalDate.now());
        playcount = 0;
        completedcount = 0;
        this.availableAmbiences = availableAmbiences;
    }

// Getters And Setters
    public UUID getId() {
        return id;
    }
    public String getNotes() {
        return Notes;
    }
    public int getPlaycount() {
        return playcount;
    }
    public int getCompletedcount() {
        return completedcount;
    }
    public void addPlaycount() {playcount++;}
    public void addCompletedcount() {completedcount++;}
    public void setNotes(String notes) {
        Notes = notes;
    }
    public void setDate_Practiced(LocalDate date_Practiced) {Date_Practiced = date_Practiced.format(dateFormat);}
    public LocalDate getDate_Practiced() {return LocalDate.parse(Date_Practiced, dateFormat);}
    public Duration getSessionPracticedTime() {
        return new Duration(SessionPracticedTime);
    }
    public void setSessionPracticedTime() {
        SessionPracticedTime = 0.0;
    }
    public void setSessionPracticedTime(double practicetime) {
        SessionPracticedTime = practicetime;
    }
    public Duration getExpectedSessionDuration() {
        return new Duration(ExpectedSessionDuration);
    }
    public void setPlaybackItems(ArrayList<PlaybackItem> playbackItems) {
        this.playbackItems = playbackItems;
    }
    public ArrayList<PlaybackItem> getPlaybackItems() {
        if (playbackItems == null) {return new ArrayList<>();}
        else {return playbackItems;}
    }
    public PlaybackItem getplaybackitem(int index) {
        PlaybackItem playbackItem;
        switch (index) {
            case 0:
                playbackItem = new PlaybackItem(this, "Qi-Gong");
                playbackItem.setCreationindex(0);
                break;
            case 1:
                playbackItem = new PlaybackItem(this,"Rin");
                playbackItem.setCreationindex(1);
                playbackItem.setTypeindex(1);
                break;
            case 2:
                playbackItem = new PlaybackItem(this,"Kyo");
                playbackItem.setCreationindex(2);
                playbackItem.setTypeindex(2);
                break;
            case 3:
                playbackItem = new PlaybackItem(this,"Toh");
                playbackItem.setCreationindex(3);
                playbackItem.setTypeindex(3);
                break;
            case 4:
                playbackItem = new PlaybackItem(this,"Sha");
                playbackItem.setCreationindex(4);
                playbackItem.setTypeindex(4);
                break;
            case 5:
                playbackItem = new PlaybackItem(this,"Kai");
                playbackItem.setCreationindex(5);
                playbackItem.setTypeindex(5);
                break;
            case 6:
                playbackItem = new PlaybackItem(this,"Jin");
                playbackItem.setCreationindex(6);
                playbackItem.setTypeindex(6);
                break;
            case 7:
                playbackItem = new PlaybackItem(this,"Retsu");
                playbackItem.setCreationindex(7);
                playbackItem.setTypeindex(7);
                break;
            case 8:
                playbackItem = new PlaybackItem(this,"Zai");
                playbackItem.setCreationindex(8);
                playbackItem.setTypeindex(8);
                break;
            case 9:
                playbackItem = new PlaybackItem(this,"Zen");
                playbackItem.setCreationindex(9);
                playbackItem.setTypeindex(9);
                break;
            case 10:
                playbackItem = new PlaybackItem(this,"Earth");
                playbackItem.setCreationindex(10);
                break;
            case 11:
                playbackItem = new PlaybackItem(this,"Air");
                playbackItem.setCreationindex(11);
                break;
            case 12:
                playbackItem = new PlaybackItem(this,"Fire");
                playbackItem.setCreationindex(12);
                break;
            case 13:
                playbackItem = new PlaybackItem(this,"Water");
                playbackItem.setCreationindex(13);
                break;
            case 14:
                playbackItem = new PlaybackItem(this,"Void");
                playbackItem.setCreationindex(14);
                break;
            default:
                return null;
        }
        return playbackItem;
    }
    public PlaybackItem getplaybackitemwithambience(int index) {
        PlaybackItem playbackItem;
        switch (index) {
            case 0:
                playbackItem = new PlaybackItem(this, "Qi-Gong", availableAmbiences.getsessionpartAmbience(0));
                playbackItem.setCreationindex(0);
                break;
            case 1:
                playbackItem = new PlaybackItem(this,"Rin", availableAmbiences.getsessionpartAmbience(1));
                playbackItem.setCreationindex(1);
                playbackItem.setTypeindex(1);
                break;
            case 2:
                playbackItem = new PlaybackItem(this,"Kyo", availableAmbiences.getsessionpartAmbience(2));
                playbackItem.setCreationindex(2);
                playbackItem.setTypeindex(2);
                break;
            case 3:
                playbackItem = new PlaybackItem(this,"Toh", availableAmbiences.getsessionpartAmbience(3));
                playbackItem.setCreationindex(3);
                playbackItem.setTypeindex(3);
                break;
            case 4:
                playbackItem = new PlaybackItem(this,"Sha", availableAmbiences.getsessionpartAmbience(4));
                playbackItem.setCreationindex(4);
                playbackItem.setTypeindex(4);
                break;
            case 5:
                playbackItem = new PlaybackItem(this,"Kai", availableAmbiences.getsessionpartAmbience(5));
                playbackItem.setCreationindex(5);
                playbackItem.setTypeindex(5);
                break;
            case 6:
                playbackItem = new PlaybackItem(this,"Jin", availableAmbiences.getsessionpartAmbience(6));
                playbackItem.setCreationindex(6);
                playbackItem.setTypeindex(6);
                break;
            case 7:
                playbackItem = new PlaybackItem(this,"Retsu", availableAmbiences.getsessionpartAmbience(7));
                playbackItem.setCreationindex(7);
                playbackItem.setTypeindex(7);
                break;
            case 8:
                playbackItem = new PlaybackItem(this,"Zai", availableAmbiences.getsessionpartAmbience(8));
                playbackItem.setCreationindex(8);
                playbackItem.setTypeindex(8);
                break;
            case 9:
                playbackItem = new PlaybackItem(this,"Zen", availableAmbiences.getsessionpartAmbience(9));
                playbackItem.setCreationindex(9);
                playbackItem.setTypeindex(9);
                break;
            case 10:
                playbackItem = new PlaybackItem(this,"Earth", availableAmbiences.getsessionpartAmbience(10));
                playbackItem.setCreationindex(10);
                break;
            case 11:
                playbackItem = new PlaybackItem(this,"Air", availableAmbiences.getsessionpartAmbience(11));
                playbackItem.setCreationindex(11);
                break;
            case 12:
                playbackItem = new PlaybackItem(this,"Fire", availableAmbiences.getsessionpartAmbience(12));
                playbackItem.setCreationindex(12);
                break;
            case 13:
                playbackItem = new PlaybackItem(this,"Water", availableAmbiences.getsessionpartAmbience(13));
                playbackItem.setCreationindex(13);
                break;
            case 14:
                playbackItem = new PlaybackItem(this,"Void", availableAmbiences.getsessionpartAmbience(14));
                playbackItem.setCreationindex(14);
                break;
            default:
                return null;
        }
        return playbackItem;
    }
    public LocalDateTime getTimestarted() {
        return LocalDateTime.parse(timestarted, dateTimeFormatter);
    }
    public LocalDateTime getTimefinished() {
        return LocalDateTime.parse(timefinished, dateTimeFormatter);
    }
    public void setTimestarted(LocalDateTime timestarted) {
        this.timestarted = timestarted.format(dateTimeFormatter);
    }
    public void setTimefinished(LocalDateTime timefinished) {
        this.timefinished = timefinished.format(dateTimeFormatter);
    }
    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    public List<Break> getBreaks() {
        return breaks;
    }
    public void addplaybackitems(Integer startindex, List<PlaybackItem> playbackitems) {
        if (playbackItems == null) {playbackItems = new ArrayList<>();}
        if (startindex != null) { playbackItems.addAll(startindex, playbackitems); }
        else { playbackItems.addAll(playbackitems); }
    }
    public void addplaybackitem(int index) {
        if (playbackItems == null) {playbackItems = new ArrayList<>();}
        playbackItems.add(getplaybackitem(index));
    }
    public void removeplaybackitem(int index) {
        System.out.println("Removed Item " + index);
        playbackItems.remove(index);
    }
    public boolean isMissedsession() {
        return missedsession;
    }


    // Utility Methods
    public boolean containsPlaybackItem(int creationindex) {
        boolean value = false;
        for (PlaybackItem i : getPlaybackItems()) {
            if (i.getCreationindex() == creationindex && i.getPracticeTime() > 0.0) {value = true; break;}
        }
        return value;
    }
    public void newID() {id = UUID.randomUUID();}
    public boolean hasCuts() {
        for (PlaybackItem i : getPlaybackItems()) {
            if (i.getPlaybackItemType() == PlaybackItem.PlaybackItemType.CUT) {return true;}
        }
        return false;
    }
    public boolean hasElements() {
        for (PlaybackItem i : getPlaybackItems()) {
            if (i.getPlaybackItemType() == PlaybackItem.PlaybackItemType.ELEMENT) {return true;}
        }
        return false;
    }
    public void calculateactualduration() {
        Duration duration = Duration.ZERO;
        for (PlaybackItem i : getPlaybackItems()) {
            duration = duration.add(Duration.millis(i.getPracticeTime()));
        }
        SessionPracticedTime = duration.toMillis();
    }
    public void calculateexpectedduration() {
        Duration duration = Duration.ZERO;
        for (PlaybackItem i : getPlaybackItems()) {
            duration = duration.add(Duration.millis(i.getExpectedDuration()));
        }
        ExpectedSessionDuration = duration.toMillis();
    }
    public void syncelapsedduration(StopWatch stopWatch) {
        SessionPracticedTime = (double) stopWatch.getTime();
    }
    public boolean isPracticed() {
        return getSessionPracticedTime().greaterThan(Duration.ZERO);
    }
    public boolean hasItems() {
        return playbackItems != null && ! playbackItems.isEmpty();
    }
    public void resetpracticetime() {
        SessionPracticedTime = 0.0;
    }
    public void setasMissedSession() {
        missedsession = true;
        for (PlaybackItem i : playbackItems) {
            i.setPracticeTime(i.getExpectedDuration());
        }
        setSessionPracticedTime(getExpectedSessionDuration().toMillis());
    }

// Playback Methods
    public void startplayback() {
        timestarted = LocalDateTime.now().format(dateTimeFormatter);
    }
    public void endplayback(boolean completed) {
        setTimefinished(LocalDateTime.now());
        setCompleted(completed);
    }
    public void startbreak() {
        if (breaks == null) {breaks = new ArrayList<>();}
        currentbreak = new Break(LocalDate.now());
    }
    public void endbreak() {
        breaks.add(currentbreak);
        currentbreak = null;
    }

}