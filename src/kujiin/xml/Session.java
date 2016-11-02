package kujiin.xml;

import javafx.util.Duration;
import kujiin.util.SessionPart;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.time.LocalDate;
import java.util.ArrayList;

import static kujiin.util.Util.dateFormat;

// TODO Implement Ambience Functionality For Session

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
    public Integer getPresession_Duration() {
        return Presession_Duration;
    }
    public void setPresession_Duration(Integer presession_Duration) {
        Presession_Duration = presession_Duration;
    }
    public Integer getEarth_Duration() {
        return Earth_Duration;
    }
    public void setEarth_Duration(Integer earth_Duration) {
        Earth_Duration = earth_Duration;
    }
    public Integer getAir_Duration() {
        return Air_Duration;
    }
    public void setAir_Duration(Integer air_Duration) {
        Air_Duration = air_Duration;
    }
    public Integer getFire_Duration() {
        return Fire_Duration;
    }
    public void setFire_Duration(Integer fire_Duration) {
        Fire_Duration = fire_Duration;
    }
    public Integer getWater_Duration() {
        return Water_Duration;
    }
    public void setWater_Duration(Integer water_Duration) {
        Water_Duration = water_Duration;
    }
    public Integer getVoid_Duration() {
        return Void_Duration;
    }
    public void setVoid_Duration(Integer void_Duration) {
        Void_Duration = void_Duration;
    }
    public Integer getRin_Duration() {
        return Rin_Duration;
    }
    public void setRin_Duration(Integer rin_Duration) {
        Rin_Duration = rin_Duration;
    }
    public Integer getKyo_Duration() {
        return Kyo_Duration;
    }
    public void setKyo_Duration(Integer kyo_Duration) {
        Kyo_Duration = kyo_Duration;
    }
    public Integer getToh_Duration() {
        return Toh_Duration;
    }
    public void setToh_Duration(Integer toh_Duration) {
        Toh_Duration = toh_Duration;
    }
    public Integer getSha_Duration() {
        return Sha_Duration;
    }
    public void setSha_Duration(Integer sha_Duration) {
        Sha_Duration = sha_Duration;
    }
    public Integer getKai_Duration() {
        return Kai_Duration;
    }
    public void setKai_Duration(Integer kai_Duration) {
        Kai_Duration = kai_Duration;
    }
    public Integer getJin_Duration() {
        return Jin_Duration;
    }
    public void setJin_Duration(Integer jin_Duration) {
        Jin_Duration = jin_Duration;
    }
    public Integer getRetsu_Duration() {
        return Retsu_Duration;
    }
    public void setRetsu_Duration(Integer retsu_Duration) {
        Retsu_Duration = retsu_Duration;
    }
    public Integer getZai_Duration() {
        return Zai_Duration;
    }
    public void setZai_Duration(Integer zai_Duration) {
        Zai_Duration = zai_Duration;
    }
    public Integer getZen_Duration() {
        return Zen_Duration;
    }
    public void setZen_Duration(Integer zen_Duration) {
        Zen_Duration = zen_Duration;
    }
    public Integer getPostsession_Duration() {
        return Postsession_Duration;
    }
    public void setPostsession_Duration(Integer postsession_Duration) {
        Postsession_Duration = postsession_Duration;
    }
    public Integer getTotal_Session_Duration() {
        return Total_Session_Duration;
    }
    public void setTotal_Session_Duration(Integer total_Session_Duration) {Total_Session_Duration = total_Session_Duration;}
    public ArrayList<SoundFile> getPresession_Ambience() {
        return Presession_Ambience;
    }
    public void setPresession_Ambience(ArrayList<SoundFile> presession_Ambience) {
        Presession_Ambience = presession_Ambience;
    }
    public ArrayList<SoundFile> getEarth_Ambience() {
        return Earth_Ambience;
    }
    public void setEarth_Ambience(ArrayList<SoundFile> earth_Ambience) {
        Earth_Ambience = earth_Ambience;
    }
    public ArrayList<SoundFile> getAir_Ambience() {
        return Air_Ambience;
    }
    public void setAir_Ambience(ArrayList<SoundFile> air_Ambience) {
        Air_Ambience = air_Ambience;
    }
    public ArrayList<SoundFile> getFire_Ambience() {
        return Fire_Ambience;
    }
    public void setFire_Ambience(ArrayList<SoundFile> fire_Ambience) {
        Fire_Ambience = fire_Ambience;
    }
    public ArrayList<SoundFile> getWater_Ambience() {
        return Water_Ambience;
    }
    public void setWater_Ambience(ArrayList<SoundFile> water_Ambience) {
        Water_Ambience = water_Ambience;
    }
    public ArrayList<SoundFile> getVoid_Ambience() {
        return Void_Ambience;
    }
    public void setVoid_Ambience(ArrayList<SoundFile> void_Ambience) {
        Void_Ambience = void_Ambience;
    }
    public ArrayList<SoundFile> getRin_Ambience() {
        return Rin_Ambience;
    }
    public void setRin_Ambience(ArrayList<SoundFile> rin_Ambience) {
        Rin_Ambience = rin_Ambience;
    }
    public ArrayList<SoundFile> getKyo_Ambience() {
        return Kyo_Ambience;
    }
    public void setKyo_Ambience(ArrayList<SoundFile> kyo_Ambience) {
        Kyo_Ambience = kyo_Ambience;
    }
    public ArrayList<SoundFile> getToh_Ambience() {
        return Toh_Ambience;
    }
    public void setToh_Ambience(ArrayList<SoundFile> toh_Ambience) {
        Toh_Ambience = toh_Ambience;
    }
    public ArrayList<SoundFile> getSha_Ambience() {
        return Sha_Ambience;
    }
    public void setSha_Ambience(ArrayList<SoundFile> sha_Ambience) {
        Sha_Ambience = sha_Ambience;
    }
    public ArrayList<SoundFile> getKai_Ambience() {
        return Kai_Ambience;
    }
    public void setKai_Ambience(ArrayList<SoundFile> kai_Ambience) {
        Kai_Ambience = kai_Ambience;
    }
    public ArrayList<SoundFile> getJin_Ambience() {
        return Jin_Ambience;
    }
    public void setJin_Ambience(ArrayList<SoundFile> jin_Ambience) {
        Jin_Ambience = jin_Ambience;
    }
    public ArrayList<SoundFile> getRetsu_Ambience() {
        return Retsu_Ambience;
    }
    public void setRetsu_Ambience(ArrayList<SoundFile> retsu_Ambience) {
        Retsu_Ambience = retsu_Ambience;
    }
    public ArrayList<SoundFile> getZai_Ambience() {
        return Zai_Ambience;
    }
    public void setZai_Ambience(ArrayList<SoundFile> zai_Ambience) {
        Zai_Ambience = zai_Ambience;
    }
    public ArrayList<SoundFile> getZen_Ambience() {
        return Zen_Ambience;
    }
    public void setZen_Ambience(ArrayList<SoundFile> zen_Ambience) {
        Zen_Ambience = zen_Ambience;
    }
    public ArrayList<SoundFile> getPostsession_Ambience() {
        return Postsession_Ambience;
    }
    public void setPostsession_Ambience(ArrayList<SoundFile> postsession_Ambience) {
        Postsession_Ambience = postsession_Ambience;
    }
    public LocalDate getDate_Practiced() {return LocalDate.parse(Date_Practiced, dateFormat);}
    public void setDate_Practiced(LocalDate date_Practiced) {Date_Practiced = date_Practiced.format(dateFormat);}

// Other Methods
    public void updatesessionpartambience(SessionPart sessionPart,ArrayList<SoundFile> ambiencelist) {

    }
    public void updatesessionpartduration(SessionPart sessionPart, int duration) {
        switch (sessionPart.number) {
            case 0:
                setPresession_Duration(duration);
                break;
            case 1:
                setRin_Duration(duration);
                break;
            case 2:
                setKyo_Duration(duration);
                break;
            case 3:
                setToh_Duration(duration);
                break;
            case 4:
                setSha_Duration(duration);
                break;
            case 5:
                setKai_Duration(duration);
                break;
            case 6:
                setJin_Duration(duration);
                break;
            case 7:
                setRetsu_Duration(duration);
                break;
            case 8:
                setZai_Duration(duration);
                break;
            case 9:
                setZen_Duration(duration);
                break;
            case 10:
                setEarth_Duration(duration);
                break;
            case 11:
                setAir_Duration(duration);
                break;
            case 12:
                setFire_Duration(duration);
                break;
            case 13:
                setWater_Duration(duration);
                break;
            case 14:
                setVoid_Duration(duration);
                break;
            case 15:
                setPostsession_Duration(duration);
                break;
        }
        updatetotalsessionduration();
    }
    public int getsessionpartduration(SessionPart sessionpart) {
        switch (sessionpart.number) {
            case 0:
                return getPresession_Duration();
            case 1:
                return getRin_Duration();
            case 2:
                return getKyo_Duration();
            case 3:
                return getToh_Duration();
            case 4:
                return getSha_Duration();
            case 5:
                return getKai_Duration();
            case 6:
                return getJin_Duration();
            case 7:
                return getRetsu_Duration();
            case 8:
                return getZai_Duration();
            case 9:
                return getZen_Duration();
            case 10:
                return getEarth_Duration();
            case 11:
                return getAir_Duration();
            case 12:
                return getFire_Duration();
            case 13:
                return getWater_Duration();
            case 14:
                return getVoid_Duration();
            case 15:
                return getPostsession_Duration();
            default:
                return 0;
        }
    }
    public Duration getsessionpartdurationasObject(SessionPart sessionPart) {
        return Duration.minutes(getsessionpartduration(sessionPart));
    }
    public void setsessionpartambience(SessionPart sessionpart, ArrayList<SoundFile> ambiencelist) {
        switch (sessionpart.number) {
            case 0:
                setPresession_Ambience(ambiencelist);
                break;
            case 1:
                setRin_Ambience(ambiencelist);
                break;
            case 2:
                setKyo_Ambience(ambiencelist);
                break;
            case 3:
                setToh_Ambience(ambiencelist);
                break;
            case 4:
                setSha_Ambience(ambiencelist);
                break;
            case 5:
                setKai_Ambience(ambiencelist);
                break;
            case 6:
                setJin_Ambience(ambiencelist);
                break;
            case 7:
                setRetsu_Ambience(ambiencelist);
                break;
            case 8:
                setZai_Ambience(ambiencelist);
                break;
            case 9:
                setZen_Ambience(ambiencelist);
                break;
            case 10:
                setEarth_Ambience(ambiencelist);
                break;
            case 11:
                setAir_Ambience(ambiencelist);
                break;
            case 12:
                setFire_Ambience(ambiencelist);
                break;
            case 13:
                setWater_Ambience(ambiencelist);
                break;
            case 14:
                setVoid_Ambience(ambiencelist);
                break;
            case 15:
                setPostsession_Ambience(ambiencelist);
                break;
        }
    }
    public ArrayList<Integer> getallsessionparttimes() {
        ArrayList<Integer> allsessionparttimes = new ArrayList<>();
        allsessionparttimes.add(getPresession_Duration());
        allsessionparttimes.add(getRin_Duration());
        allsessionparttimes.add(getKyo_Duration());
        allsessionparttimes.add(getToh_Duration());
        allsessionparttimes.add(getSha_Duration());
        allsessionparttimes.add(getKai_Duration());
        allsessionparttimes.add(getJin_Duration());
        allsessionparttimes.add(getRetsu_Duration());
        allsessionparttimes.add(getZai_Duration());
        allsessionparttimes.add(getZen_Duration());
        allsessionparttimes.add(getZen_Duration());
        allsessionparttimes.add(getEarth_Duration());
        allsessionparttimes.add(getAir_Duration());
        allsessionparttimes.add(getFire_Duration());
        allsessionparttimes.add(getWater_Duration());
        allsessionparttimes.add(getVoid_Duration());
        allsessionparttimes.add(getPostsession_Duration());
        return allsessionparttimes;
    }
    public boolean sessionempty() {
        Duration minutes = Duration.ZERO;
        for (Integer i : getallsessionparttimes()) {minutes.add(Duration.minutes(i));}
        return minutes.greaterThan(Duration.ZERO);
    }
    public void updatetotalsessionduration() {
        Integer total = 0;
        for (Integer i: getallsessionparttimes()) {total += i;}
        setTotal_Session_Duration(total);
    }

}
