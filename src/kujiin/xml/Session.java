package kujiin.xml;

import javafx.util.Duration;
import kujiin.util.SessionPart;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.time.LocalDate;
import java.util.ArrayList;

import static kujiin.util.Util.dateFormat;

@XmlAccessorType(XmlAccessType.FIELD)
public class Session {
    private String Date_Practiced;
    private Integer Presession_Duration;
    private Integer Earth_Duration;
    private Integer Air_Duration;
    private Integer Fire_Duration;
    private Integer Water_Duration;
    private Integer Void_Duration;
    private Integer Rin_Duration;
    private Integer Kyo_Duration;
    private Integer Toh_Duration;
    private Integer Sha_Duration;
    private Integer Kai_Duration;
    private Integer Jin_Duration;
    private Integer Retsu_Duration;
    private Integer Zai_Duration;
    private Integer Zen_Duration;
    private Integer Postsession_Duration;
    private Integer Total_Session_Duration;
    private ArrayList<SoundFile> Presession_Ambience;
    private ArrayList<SoundFile> Earth_Ambience;
    private ArrayList<SoundFile> Air_Ambience;
    private ArrayList<SoundFile> Fire_Ambience;
    private ArrayList<SoundFile> Water_Ambience;
    private ArrayList<SoundFile> Void_Ambience;
    private ArrayList<SoundFile> Rin_Ambience;
    private ArrayList<SoundFile> Kyo_Ambience;
    private ArrayList<SoundFile> Toh_Ambience;
    private ArrayList<SoundFile> Sha_Ambience;
    private ArrayList<SoundFile> Kai_Ambience;
    private ArrayList<SoundFile> Jin_Ambience;
    private ArrayList<SoundFile> Retsu_Ambience;
    private ArrayList<SoundFile> Zai_Ambience;
    private ArrayList<SoundFile> Zen_Ambience;
    private ArrayList<SoundFile> Postsession_Ambience;

    public Session(int[] durations) {
        Presession_Duration = durations[0];
        Rin_Duration = durations[1];
        Kyo_Duration = durations[2];
        Toh_Duration = durations[3];
        Sha_Duration = durations[4];
        Kai_Duration = durations[5];
        Jin_Duration = durations[6];
        Retsu_Duration = durations[7];
        Zai_Duration = durations[8];
        Zen_Duration = durations[9];
        Earth_Duration = durations[10];
        Air_Duration = durations[11];
        Fire_Duration = durations[12];
        Water_Duration = durations[13];
        Void_Duration = durations[14];
        Postsession_Duration = durations[15];
        int totalduration = 0;
        for (int i : durations) {totalduration += i;}
        Total_Session_Duration = totalduration;
        setDate_Practiced(LocalDate.now());
    }
    public Session() {setDate_Practiced(LocalDate.now());}

// Getters And Setters
    public LocalDate getDate_Practiced() {return LocalDate.parse(Date_Practiced, dateFormat);}
    public void setDate_Practiced(LocalDate date_Practiced) {Date_Practiced = date_Practiced.format(dateFormat);}
    public void updateduration(SessionPart sessionPart, Duration duration) {
        int minutes = (int) duration.toMinutes();
        switch (sessionPart.number) {
            case 0:
                Presession_Duration = minutes;
                break;
            case 1:
                Rin_Duration = minutes;
                break;
            case 2:
                Kyo_Duration = minutes;
                break;
            case 3:
                Toh_Duration = minutes;
                break;
            case 4:
                Sha_Duration = minutes;
                break;
            case 5:
                Kai_Duration = minutes;
                break;
            case 6:
                Jin_Duration = minutes;
                break;
            case 7:
                Retsu_Duration = minutes;
                break;
            case 8:
                Zai_Duration = minutes;
                break;
            case 9:
                Zen_Duration = minutes;
                break;
            case 10:
                Earth_Duration = minutes;
                break;
            case 11:
                Air_Duration = minutes;
                break;
            case 12:
                Fire_Duration = minutes;
                break;
            case 13:
                Water_Duration = minutes;
                break;
            case 14:
                Void_Duration = minutes;
                break;
            case 15:
                Postsession_Duration = minutes;
                break;
        }
        Total_Session_Duration = (int) gettotalsessionduration().toMinutes();
    }
    public Duration getduration(SessionPart sessionpart) {
        switch (sessionpart.number) {
            case 0:
                return Duration.minutes(Presession_Duration);
            case 1:
                return Duration.minutes(Rin_Duration);
            case 2:
                return Duration.minutes(Kyo_Duration);
            case 3:
                return Duration.minutes(Toh_Duration);
            case 4:
                return Duration.minutes(Sha_Duration);
            case 5:
                return Duration.minutes(Kai_Duration);
            case 6:
                return Duration.minutes(Jin_Duration);
            case 7:
                return Duration.minutes(Retsu_Duration);
            case 8:
                return Duration.minutes(Zai_Duration);
            case 9:
                return Duration.minutes(Zen_Duration);
            case 10:
                return Duration.minutes(Earth_Duration);
            case 11:
                return Duration.minutes(Air_Duration);
            case 12:
                return Duration.minutes(Fire_Duration);
            case 13:
                return Duration.minutes(Water_Duration);
            case 14:
                return Duration.minutes(Void_Duration);
            case 15:
                return Duration.minutes(Postsession_Duration);
            default:
                return Duration.ZERO;
        }
    }
    public Duration getduration(int index) {
        switch (index) {
            case 0:
                return Duration.minutes(Presession_Duration);
            case 1:
                return Duration.minutes(Rin_Duration);
            case 2:
                return Duration.minutes(Kyo_Duration);
            case 3:
                return Duration.minutes(Toh_Duration);
            case 4:
                return Duration.minutes(Sha_Duration);
            case 5:
                return Duration.minutes(Kai_Duration);
            case 6:
                return Duration.minutes(Jin_Duration);
            case 7:
                return Duration.minutes(Retsu_Duration);
            case 8:
                return Duration.minutes(Zai_Duration);
            case 9:
                return Duration.minutes(Zen_Duration);
            case 10:
                return Duration.minutes(Earth_Duration);
            case 11:
                return Duration.minutes(Air_Duration);
            case 12:
                return Duration.minutes(Fire_Duration);
            case 13:
                return Duration.minutes(Water_Duration);
            case 14:
                return Duration.minutes(Void_Duration);
            case 15:
                return Duration.minutes(Postsession_Duration);
            default:
                return Duration.ZERO;
        }
    }
    public Duration gettotalsessionduration() {
        Duration duration = Duration.ZERO;
        for (int i = 0; i < 16; i++) {duration = duration.add(getduration(i));}
        return duration;
    }
    public void updatesessionpartambience(SessionPart sessionPart, ArrayList<SoundFile> ambiencelist) {
        switch (sessionPart.number) {
            case 0:
                Presession_Ambience = ambiencelist;
                break;
            case 1:
                Rin_Ambience = ambiencelist;
                break;
            case 2:
                Kyo_Ambience = ambiencelist;
                break;
            case 3:
                Toh_Ambience = ambiencelist;
                break;
            case 4:
                Sha_Ambience = ambiencelist;
                break;
            case 5:
                Kai_Ambience = ambiencelist;
                break;
            case 6:
                Jin_Ambience = ambiencelist;
                break;
            case 7:
                Retsu_Ambience = ambiencelist;
                break;
            case 8:
                Zai_Ambience = ambiencelist;
                break;
            case 9:
                Zen_Ambience = ambiencelist;
                break;
            case 10:
                Earth_Ambience = ambiencelist;
                break;
            case 11:
                Air_Ambience = ambiencelist;
                break;
            case 12:
                Fire_Ambience = ambiencelist;
                break;
            case 13:
                Water_Ambience = ambiencelist;
                break;
            case 14:
                Void_Ambience = ambiencelist;
                break;
            case 15:
                Postsession_Ambience = ambiencelist;
                break;
        }
    }
    public ArrayList<SoundFile> getsessionpartambience(SessionPart sessionPart) {
        switch (sessionPart.number) {
            case 0:
                return Presession_Ambience;
            case 1:
                return Rin_Ambience;
            case 2:
                return Kyo_Ambience;
            case 3:
                return Toh_Ambience;
            case 4:
                return Sha_Ambience;
            case 5:
                return Kai_Ambience;
            case 6:
                return Jin_Ambience;
            case 7:
                return Retsu_Ambience;
            case 8:
                return Zai_Ambience;
            case 9:
                return Zen_Ambience;
            case 10:
                return Earth_Ambience;
            case 11:
                return Air_Ambience;
            case 12:
                return Fire_Ambience;
            case 13:
                return Water_Ambience;
            case 14:
                return Void_Ambience;
            case 15:
                return Postsession_Ambience;
            default:
                return null;
        }
    }

// Utility Methods
    public boolean sessionempty() {
        return gettotalsessionduration().greaterThan(Duration.ZERO);
    }

}
