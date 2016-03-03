package kujiin.xml;

import kujiin.Tools;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.File;
import java.util.ArrayList;

@XmlAccessorType(XmlAccessType.PROPERTY)
//@XmlType(propOrder = "Name", "Presession_Duration"...)
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
    private ArrayList<File> Presession_Ambience;
    private ArrayList<File> Earth_Ambience;
    private ArrayList<File> Air_Ambience;
    private ArrayList<File> Fire_Ambience;
    private ArrayList<File> Water_Ambience;
    private ArrayList<File> Void_Ambience;
    private ArrayList<File> Rin_Ambience;
    private ArrayList<File> Kyo_Ambience;
    private ArrayList<File> Toh_Ambience;
    private ArrayList<File> Sha_Ambience;
    private ArrayList<File> Kai_Ambience;
    private ArrayList<File> Jin_Ambience;
    private ArrayList<File> Retsu_Ambience;
    private ArrayList<File> Zai_Ambience;
    private ArrayList<File> Zen_Ambience;
    private ArrayList<File> Postsession_Ambience;

    public Session(Integer presession_duration, Integer rin_duration, Integer kyo_duration, Integer toh_duration,
                   Integer sha_duration, Integer kai_duration, Integer jin_duration, Integer retsu_duration, Integer zai_duration, Integer zen_duration, Integer postsession_duration, Integer total_session_duration) {
        Presession_Duration = presession_duration;
        Rin_Duration = rin_duration;
        Kyo_Duration = kyo_duration;
        Toh_Duration = toh_duration;
        Sha_Duration = sha_duration;
        Kai_Duration = kai_duration;
        Jin_Duration = jin_duration;
        Retsu_Duration = retsu_duration;
        Zai_Duration = zai_duration;
        Zen_Duration = zen_duration;
        Postsession_Duration = postsession_duration;
        Total_Session_Duration = total_session_duration;
        setDate_Practiced(Tools.gettodaysdate());
    }
    public Session() {setDate_Practiced(Tools.gettodaysdate());}

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
    public ArrayList<File> getPresession_Ambience() {
        return Presession_Ambience;
    }
    public void setPresession_Ambience(ArrayList<File> presession_Ambience) {Presession_Ambience = presession_Ambience;}
    public ArrayList<File> getEarth_Ambience() {
        return Earth_Ambience;
    }
    public void setEarth_Ambience(ArrayList<File> earth_Ambience) {
        Earth_Ambience = earth_Ambience;
    }
    public ArrayList<File> getAir_Ambience() {
        return Air_Ambience;
    }
    public void setAir_Ambience(ArrayList<File> air_Ambience) {
        Air_Ambience = air_Ambience;
    }
    public ArrayList<File> getFire_Ambience() {
        return Fire_Ambience;
    }
    public void setFire_Ambience(ArrayList<File> fire_Ambience) {
        Fire_Ambience = fire_Ambience;
    }
    public ArrayList<File> getWater_Ambience() {
        return Water_Ambience;
    }
    public void setWater_Ambience(ArrayList<File> water_Ambience) {
        Water_Ambience = water_Ambience;
    }
    public ArrayList<File> getVoid_Ambience() {
        return Void_Ambience;
    }
    public void setVoid_Ambience(ArrayList<File> void_Ambience) {
        Void_Ambience = void_Ambience;
    }
    public ArrayList<File> getRin_Ambience() {
        return Rin_Ambience;
    }
    public void setRin_Ambience(ArrayList<File> rin_Ambience) {
        Rin_Ambience = rin_Ambience;
    }
    public ArrayList<File> getKyo_Ambience() {
        return Kyo_Ambience;
    }
    public void setKyo_Ambience(ArrayList<File> kyo_Ambience) {
        Kyo_Ambience = kyo_Ambience;
    }
    public ArrayList<File> getToh_Ambience() {
        return Toh_Ambience;
    }
    public void setToh_Ambience(ArrayList<File> toh_Ambience) {
        Toh_Ambience = toh_Ambience;
    }
    public ArrayList<File> getSha_Ambience() {
        return Sha_Ambience;
    }
    public void setSha_Ambience(ArrayList<File> sha_Ambience) {
        Sha_Ambience = sha_Ambience;
    }
    public ArrayList<File> getKai_Ambience() {
        return Kai_Ambience;
    }
    public void setKai_Ambience(ArrayList<File> kai_Ambience) {
        Kai_Ambience = kai_Ambience;
    }
    public ArrayList<File> getJin_Ambience() {
        return Jin_Ambience;
    }
    public void setJin_Ambience(ArrayList<File> jin_Ambience) {
        Jin_Ambience = jin_Ambience;
    }
    public ArrayList<File> getRetsu_Ambience() {
        return Retsu_Ambience;
    }
    public void setRetsu_Ambience(ArrayList<File> retsu_Ambience) {
        Retsu_Ambience = retsu_Ambience;
    }
    public ArrayList<File> getZai_Ambience() {
        return Zai_Ambience;
    }
    public void setZai_Ambience(ArrayList<File> zai_Ambience) {
        Zai_Ambience = zai_Ambience;
    }
    public ArrayList<File> getZen_Ambience() {return Zen_Ambience;}
    public void setZen_Ambience(ArrayList<File> zen_Ambience) {
        Zen_Ambience = zen_Ambience;
    }
    public ArrayList<File> getPostsession_Ambience() {
        return Postsession_Ambience;
    }
    public void setPostsession_Ambience(ArrayList<File> postsession_Ambience) {
        Postsession_Ambience = postsession_Ambience;
    }
    public String getDate_Practiced() {return Date_Practiced;}
    public void setDate_Practiced(String date_Practiced) {Date_Practiced = date_Practiced;}

// Other Methods
    public void updatecutduration(int cutindex, int duration) {
        if (cutindex == 0) {setPresession_Duration(duration);}
        if (cutindex == 1) {setRin_Duration(duration);}
        if (cutindex == 2) {setKyo_Duration(duration);}
        if (cutindex == 3) {setToh_Duration(duration);}
        if (cutindex == 4) {setSha_Duration(duration);}
        if (cutindex == 5) {setKai_Duration(duration);}
        if (cutindex == 6) {setJin_Duration(duration);}
        if (cutindex == 7) {setRetsu_Duration(duration);}
        if (cutindex == 8) {setZai_Duration(duration);}
        if (cutindex == 9) {setZen_Duration(duration);}
        if (cutindex == 10) {setPostsession_Duration(duration);}
        updatetotalsessionduration();
    }
    public int getcutduration(int cutindex) {
        if (cutindex == 0) {return getPresession_Duration();}
        if (cutindex == 1) {return getRin_Duration();}
        if (cutindex == 2) {return getKyo_Duration();}
        if (cutindex == 3) {return getToh_Duration();}
        if (cutindex == 4) {return getSha_Duration();}
        if (cutindex == 5) {return getKai_Duration();}
        if (cutindex == 6) {return getJin_Duration();}
        if (cutindex == 7) {return getRetsu_Duration();}
        if (cutindex == 8) {return getZai_Duration();}
        if (cutindex == 9) {return getZen_Duration();}
        if (cutindex == 10) {return getPostsession_Duration();}
        else {return 0;}
    }
    public void setcutambience(int cutindex, ArrayList<File> ambiencelist) {
        if (cutindex == 0) setPresession_Ambience(ambiencelist);
        if (cutindex == 1) setRin_Ambience(ambiencelist);
        if (cutindex == 2) setKyo_Ambience(ambiencelist);
        if (cutindex == 3) setToh_Ambience(ambiencelist);
        if (cutindex == 4) setSha_Ambience(ambiencelist);
        if (cutindex == 5) setKai_Ambience(ambiencelist);
        if (cutindex == 6) setJin_Ambience(ambiencelist);
        if (cutindex == 7) setRetsu_Ambience(ambiencelist);
        if (cutindex == 8) setZen_Ambience(ambiencelist);
        if (cutindex == 9) setPostsession_Ambience(ambiencelist);
        if (cutindex == 10) System.out.println("Can't Set Cut Ambience Not A Valid Index: " + cutindex);
    }
    public ArrayList<Integer> getallcuttimes() {
        ArrayList<Integer> allcuttimes = new ArrayList<>();
        allcuttimes.add(getPresession_Duration());
        allcuttimes.add(getRin_Duration());
        allcuttimes.add(getKyo_Duration());
        allcuttimes.add(getToh_Duration());
        allcuttimes.add(getSha_Duration());
        allcuttimes.add(getKai_Duration());
        allcuttimes.add(getJin_Duration());
        allcuttimes.add(getRetsu_Duration());
        allcuttimes.add(getZai_Duration());
        allcuttimes.add(getZen_Duration());
        allcuttimes.add(getZen_Duration());
        allcuttimes.add(getPostsession_Duration());
        return allcuttimes;
    }
    public boolean sessionnotEmpty() {
        int count = 0;
        for (Integer i : getallcuttimes()) {
            if (count > 0 && count < 10) {if (i > 0) {return true;}}
            count++;
        }
        return false;
    }
    public void updatetotalsessionduration() {
        Integer total = 0;
        for (Integer i: getallcuttimes()) {total += i;}
        setTotal_Session_Duration(total);
    }

}
