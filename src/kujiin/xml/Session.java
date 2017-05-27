package kujiin.xml;

import javafx.util.Duration;
import kujiin.util.enums.FreqType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;
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

    public Session() {
        id = UUID.randomUUID();
        setDate_Practiced(LocalDate.now());
    }

// Getters And Setters
    public UUID getId() {
        return id;
    }
    public String getNotes() {
        return Notes;
    }
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
    public void addplaybackitems(List<PlaybackItem> playbackitems) {if (playbackItems == null) {playbackItems = new ArrayList<>();} playbackItems.addAll(playbackitems);}
    public void addplaybackitem(int index) {
        if (playbackItems == null) {playbackItems = new ArrayList<>();}
        playbackItems.add(getplaybackitem(index));
    }
    public void removeplaybackitem(int index) {
        playbackItems.remove(index);
    }

// Utility Methods
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
    public void addelapseduration(Duration duration) {
        SessionPracticedTime = SessionPracticedTime + duration.toMillis();
    }
    public boolean isPracticed() {
        return getSessionPracticedTime().greaterThan(Duration.ZERO);
    }
    public boolean isEmpty() {
        return playbackItems == null || playbackItems.isEmpty();
    }
    public void resetpracticetime() {
        SessionPracticedTime = 0.0;
    }

}