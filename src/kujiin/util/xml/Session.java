package kujiin.util.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.File;
import java.util.ArrayList;

// TODO Figure Out What Information To Store In XML
// In Order To:
    // Save/Load As Preset Into Session Creator From/To XML
    // Set Session Progress Throughout Session (This Will Replace SQLITE3 And DB Storage)
@XmlAccessorType(XmlAccessType.PROPERTY)
//@XmlType(propOrder = "Name", "Presession_Duration"...)
public class Session {
    private String Name;
    private Integer Presession_Duration;
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

    public Session(String name, Integer presession_duration, Integer rin_duration, Integer kyo_duration, Integer toh_duration,
                   Integer sha_duration, Integer kai_duration, Integer jin_duration, Integer retsu_duration, Integer zai_duration, Integer zen_duration, Integer postsession_duration, Integer total_session_duration) {
        Name = name;
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
    }

// Getters And Setters
    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }
    public Integer getPresession_Duration() {
        return Presession_Duration;
    }
    public void setPresession_Duration(Integer presession_Duration) {
        Presession_Duration = presession_Duration;
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
    public void setTotal_Session_Duration(Integer total_Session_Duration) {
        Total_Session_Duration = total_Session_Duration;
    }
    public ArrayList<File> getPresession_Ambience() {
        return Presession_Ambience;
    }
    public void setPresession_Ambience(ArrayList<File> presession_Ambience) {
        Presession_Ambience = presession_Ambience;
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
    public ArrayList<File> getZen_Ambience() {
        return Zen_Ambience;
    }
    public void setZen_Ambience(ArrayList<File> zen_Ambience) {
        Zen_Ambience = zen_Ambience;
    }
    public ArrayList<File> getPostsession_Ambience() {
        return Postsession_Ambience;
    }
    public void setPostsession_Ambience(ArrayList<File> postsession_Ambience) {
        Postsession_Ambience = postsession_Ambience;
    }

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
    }
    public void setcutambience(int cutindex, ArrayList<File> ambiencelist) {
        if (cutindex == 0) {setPresession_Ambience(ambiencelist);}
        if (cutindex == 1) {setRin_Ambience(ambiencelist);}
        if (cutindex == 2) {setKyo_Ambience(ambiencelist);}
        if (cutindex == 3) {setToh_Ambience(ambiencelist);}
        if (cutindex == 4) {setSha_Ambience(ambiencelist);}
        if (cutindex == 5) {setKai_Ambience(ambiencelist);}
        if (cutindex == 6) {setJin_Ambience(ambiencelist);}
        if (cutindex == 7) {setRetsu_Ambience(ambiencelist);}
        if (cutindex == 8) {setZai_Ambience(ambiencelist);}
        if (cutindex == 9) {setZen_Ambience(ambiencelist);}
        if (cutindex == 10) {setPostsession_Ambience(ambiencelist);}
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
}
