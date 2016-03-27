package kujiin.xml;

import kujiin.MainController;
import kujiin.Tools;
import kujiin.lib.BeanComparator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlRootElement(name = "Goals")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Goals {
    private List<Goal> PresessionGoals;
    private List<Goal> RinGoals;
    private List<Goal> KyoGoals;
    private List<Goal> TohGoals;
    private List<Goal> ShaGoals;
    private List<Goal> KaiGoals;
    private List<Goal> JinGoals;
    private List<Goal> RetsuGoals;
    private List<Goal> ZaiGoals;
    private List<Goal> ZenGoals;
    private List<Goal> EarthGoals;
    private List<Goal> AirGoals;
    private List<Goal> FireGoals;
    private List<Goal> WaterGoals;
    private List<Goal> VoidGoals;
    private List<Goal> PostsessionGoals;
    private List<Goal> TotalGoals;
    private MainController Root;

    // TODO Unify Completed And Current Goals With The IsCompleted() Boolean

    public Goals() {}
    public Goals(MainController root) {
        Root = root;
    }

// Getters And Setters
    public List<Goal> getPresessionGoals() {
        return PresessionGoals;
    }
    public void setPresessionGoals(List<Goal> presessionGoals) {
        PresessionGoals = presessionGoals;
    }
    public List<Goal> getRinGoals() {
    return RinGoals;
}
    public void setRinGoals(List<Goal> rinGoals) {
        RinGoals = rinGoals;
    }
    public List<Goal> getKyoGoals() {
        return KyoGoals;
    }
    public void setKyoGoals(List<Goal> kyoGoals) {
        KyoGoals = kyoGoals;
    }
    public List<Goal> getTohGoals() {
        return TohGoals;
    }
    public void setTohGoals(List<Goal> tohGoals) {
        TohGoals = tohGoals;
    }
    public List<Goal> getShaGoals() {
        return ShaGoals;
    }
    public void setShaGoals(List<Goal> shaGoals) {
        ShaGoals = shaGoals;
    }
    public List<Goal> getKaiGoals() {
        return KaiGoals;
    }
    public void setKaiGoals(List<Goal> kaiGoals) {
        KaiGoals = kaiGoals;
    }
    public List<Goal> getJinGoals() {
        return JinGoals;
    }
    public void setJinGoals(List<Goal> jinGoals) {
        JinGoals = jinGoals;
    }
    public List<Goal> getRetsuGoals() {
        return RetsuGoals;
    }
    public void setRetsuGoals(List<Goal> retsuGoals) {
        RetsuGoals = retsuGoals;
    }
    public List<Goal> getZaiGoals() {
        return ZaiGoals;
    }
    public void setZaiGoals(List<Goal> zaiGoals) {
        ZaiGoals = zaiGoals;
    }
    public List<Goal> getZenGoals() {
        return ZenGoals;
    }
    public void setZenGoals(List<Goal> zenGoals) {
        ZenGoals = zenGoals;
    }
    public List<Goal> getEarthGoals() {
        return EarthGoals;
    }
    public void setEarthGoals(List<Goal> earthGoals) {
        EarthGoals = earthGoals;
    }
    public List<Goal> getAirGoals() {
        return AirGoals;
    }
    public void setAirGoals(List<Goal> airGoals) {
        AirGoals = airGoals;
    }
    public List<Goal> getFireGoals() {
        return FireGoals;
    }
    public void setFireGoals(List<Goal> fireGoals) {
        FireGoals = fireGoals;
    }
    public List<Goal> getWaterGoals() {
        return WaterGoals;
    }
    public void setWaterGoals(List<Goal> waterGoals) {
        WaterGoals = waterGoals;
    }
    public List<Goal> getVoidGoals() {
        return VoidGoals;
    }
    public void setVoidGoals(List<Goal> voidGoals) {
        VoidGoals = voidGoals;
    }
    public List<Goal> getPostsessionGoals() {
        return PostsessionGoals;
    }
    public void setPostsessionGoals(List<Goal> postsessionGoals) {
        PostsessionGoals = postsessionGoals;
    }
    public List<Goal> getTotalGoals() {return TotalGoals;}
    public void setTotalGoals(List<Goal> totalGoals) {
        TotalGoals = totalGoals;}

// XML Processing
    public void unmarshall() {
        if (Options.GOALSXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(Goals.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                Goals currentGoals = (Goals) createMarshaller.unmarshal(Options.GOALSXMLFILE);
                setPresessionGoals(currentGoals.getPresessionGoals());
                setRinGoals(currentGoals.getRinGoals());
                setKyoGoals(currentGoals.getKyoGoals());
                setTohGoals(currentGoals.getTohGoals());
                setShaGoals(currentGoals.getShaGoals());
                setKaiGoals(currentGoals.getKaiGoals());
                setJinGoals(currentGoals.getJinGoals());
                setRetsuGoals(currentGoals.getRetsuGoals());
                setZaiGoals(currentGoals.getZaiGoals());
                setZenGoals(currentGoals.getZenGoals());
                setEarthGoals(currentGoals.getEarthGoals());
                setAirGoals(currentGoals.getAirGoals());
                setFireGoals(currentGoals.getFireGoals());
                setWaterGoals(currentGoals.getWaterGoals());
                setVoidGoals(currentGoals.getVoidGoals());
                setPostsessionGoals(currentGoals.getPostsessionGoals());
                setTotalGoals(currentGoals.getTotalGoals());
            } catch (JAXBException e) {
                Tools.gui_showinformationdialog(Root, "Information", "Couldn't Open Current Goals XML File", "Check Read File Permissions Of " + Options.GOALSXMLFILE.getAbsolutePath());
            }
        }
    }
    public void marshall() {
        try {
            sortallcompletedgoals();
            JAXBContext context = JAXBContext.newInstance(Goals.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Options.GOALSXMLFILE);
        } catch (JAXBException e) {
            Tools.gui_showinformationdialog(Root, "Information", "Couldn't Save Current Goals XML File", "Check Write File Permissions Of " + Options.GOALSXMLFILE.getAbsolutePath());
        }
    }
    public void add(int cutorelementindex, Goal newgoal) throws JAXBException {
        List<Goal> newgoals = getallcutgoals(cutorelementindex, true);
        System.out.println("Old Goals:");
        for (Goal i : newgoals) {
            System.out.println(i.toString());
        }
        newgoals.add(newgoal);
        update(sort(newgoals), cutorelementindex);
        System.out.println("New Goals:");
        for (Goal i : getallcutgoals(cutorelementindex, true)) {
            System.out.println(i.toString());
        }
        marshall();
    }
    public boolean delete(int cutorelementindex, Goal currentGoal) {
        try {
            List<Goal> cutgoallist = getallcutgoals(cutorelementindex, true);
            cutgoallist.remove(currentGoal);
            update(sort(cutgoallist), cutorelementindex);
            marshall();
            return true;
        } catch (Exception e) {return false;}
    }
    public List<Goal> sort(List<Goal> goallist) {
        try {
            BeanComparator bc = new BeanComparator(Goal.class, "getGoal_Hours");
            Collections.sort(goallist, bc);
            int count = 1;
            for (Goal i : goallist) {i.setID(count); count++;}
            return goallist;
        } catch (Exception ignored) {return null;}
    }
    public void update(List<Goal> cutgoallist, int cutorelementindex) {
        if (cutorelementindex == 0) setPresessionGoals(cutgoallist);
        if (cutorelementindex == 1) setRinGoals(cutgoallist);
        if (cutorelementindex == 2) setKyoGoals(cutgoallist);
        if (cutorelementindex == 3) setTohGoals(cutgoallist);
        if (cutorelementindex == 4) setShaGoals(cutgoallist);
        if (cutorelementindex == 5) setKaiGoals(cutgoallist);
        if (cutorelementindex == 6) setJinGoals(cutgoallist);
        if (cutorelementindex == 7) setRetsuGoals(cutgoallist);
        if (cutorelementindex == 8) setZaiGoals(cutgoallist);
        if (cutorelementindex == 9) setZenGoals(cutgoallist);
        if (cutorelementindex == 10) setPostsessionGoals(cutgoallist);
        if (cutorelementindex == 11) setTotalGoals(cutgoallist);
    }

// Goal Getters
    public boolean goalsexist(int cutorelementindex, boolean includecompleted) {
        return getallcutgoals(cutorelementindex, includecompleted) != null && ! getallcutgoals(cutorelementindex, true).isEmpty();
    }
    public Goal getcurrentgoal(int cutorelementindex) {
        try {return getallcutgoals(cutorelementindex, false).get(0);}
        catch (NullPointerException | IndexOutOfBoundsException ignored) {return null;}
    }
    public Goal getgoal(int cutorelementindex, Integer goalindex, boolean includecompleted) {
        try {return getallcutgoals(cutorelementindex, includecompleted).get(goalindex);}
        catch (IndexOutOfBoundsException | NullPointerException ignored) {return null;}
    }
    public List<Goal> getallcutgoals(int cutorelementindex, boolean includecompleted) {
        if (cutorelementindex == 0) return filtergoals(PresessionGoals, includecompleted);
        if (cutorelementindex == 1) return filtergoals(RinGoals, includecompleted);
        if (cutorelementindex == 2) return filtergoals(KyoGoals, includecompleted);
        if (cutorelementindex == 3) return filtergoals(TohGoals, includecompleted);
        if (cutorelementindex == 4) return filtergoals(ShaGoals, includecompleted);
        if (cutorelementindex == 5) return filtergoals(KaiGoals, includecompleted);
        if (cutorelementindex == 6) return filtergoals(JinGoals, includecompleted);
        if (cutorelementindex == 7) return filtergoals(RetsuGoals, includecompleted);
        if (cutorelementindex == 8) return filtergoals(ZaiGoals, includecompleted);
        if (cutorelementindex == 9) return filtergoals(ZenGoals, includecompleted);
        if (cutorelementindex == 10) return filtergoals(EarthGoals, includecompleted);
        if (cutorelementindex == 11) return filtergoals(AirGoals, includecompleted);
        if (cutorelementindex == 12) return filtergoals(FireGoals, includecompleted);
        if (cutorelementindex == 13) return filtergoals(WaterGoals, includecompleted);
        if (cutorelementindex == 14) return filtergoals(VoidGoals, includecompleted);
        if (cutorelementindex == 15) return filtergoals(PostsessionGoals, includecompleted);
        if (cutorelementindex == 16) return filtergoals(TotalGoals, includecompleted);
        else return null;
    }
    public List<Goal> filtergoals(List<Goal> cutgoals, boolean includecompleted) {
        try {
            List<Goal> newgoallist = new ArrayList<>();
            for (Goal i : cutgoals) {
                if (includecompleted) {newgoallist.add(i);}
                else {
                    if (i.getCompleted() == null || ! i.getCompleted()) {newgoallist.add(i);}
                }
            }
            return newgoallist;
        } catch (NullPointerException e) {return new ArrayList<>();}
    }
    public int getcompletedgoalcount(int cutorelementindex) {
        int completedgoalcount = 0;
        for (Goal i : getallcutgoals(cutorelementindex, true)) {
            if (i.getCompleted()) completedgoalcount++;
        }
        return completedgoalcount;
    }
//    public List<Goal> getgoalscompletedondate(int cutindex, LocalDate localDate) {
//       List<Goal> goalscompletedondate = new ArrayList<>();
//        for (Goal i : getallcutgoals(cutindex, false)) {
//            LocalDate date = Tools.convert_stringtolocaldate(i.getDate_Completed())
//        }
//    }

// Goal Completion Methods
    public void sortallcompletedgoals() {
        for (int i = 0; i <= 10; i++) {
            double currentpracticedhours = Root.getProgressTracker().getSessions().getpracticedtimeinminutesforallsessions(i, false);
            sortcompletedgoals(i, currentpracticedhours);
        }
    }
    public void sortcompletedgoals(int cutorelementindex, double currentpracticedhours) {
        for (Goal i : getallcutgoals(cutorelementindex, true)) {
            boolean completed = currentpracticedhours >= i.getGoal_Hours();
            i.setCompleted(completed);
            if (completed && ! i.getCompleted()) {i.setDate_Completed(Tools.gettodaysdate());}
        }
    }
    public List<Goal> completecutgoals(int cutorelementindex, double currentpracticedhours) {
        List<Goal> completedthisession = new ArrayList<>();
        for (Goal i : getallcutgoals(cutorelementindex, true)) {
            boolean completed = currentpracticedhours >= i.getGoal_Hours();
            boolean notcompletedbefore = ! i.getCompleted();
            if (completed && notcompletedbefore) {
                i.setCompleted(true);
                i.setDate_Completed(Tools.gettodaysdate());
                completedthisession.add(i);
            }
        }
        return completedthisession;
    }

    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static class Goal {
        private Integer ID;
        private String CutName;
        private String Date_Set;
        private String Date_Due;
        private String Date_Completed;
        private Double Goal_Hours;
        private Boolean Completed;

        public Goal() {}

        public Goal(LocalDate duedate, Double goalhours, String cutname) {
            setDate_Due(Tools.convert_localdatetostring(duedate));
            setGoal_Hours(goalhours);
            setDate_Set(Tools.convert_localdatetostring(LocalDate.now()));
            setCutName(cutname);
            setCompleted(false);
            setDate_Completed("Not Completed");
        }

        // Getters And Setters
        public String getDate_Set() {return Date_Set;}
        public void setDate_Set(String date_Set) {Date_Set = date_Set;}
        public String getDate_Due() {return Date_Due;}
        public void setDate_Due(String date_Due) {Date_Due = date_Due;}
        public Double getGoal_Hours() {return Goal_Hours;}
        public void setGoal_Hours(Double goal_Hours) {Goal_Hours = goal_Hours;}
        public Integer getID() {return ID;}
        public void setID(Integer ID) {this.ID = ID;}
        public String getDate_Completed() {
            return Date_Completed;
        }
        public void setDate_Completed(String date_Completed) {
            Date_Completed = date_Completed;
        }
        public Boolean getCompleted() {
            return Completed;
        }
        public void setCompleted(Boolean completed) {
            Completed = completed;
        }
        public String getCutName() {
            return CutName;
        }
        public void setCutName(String cutName) {
            CutName = cutName;
        }

        // Other Methods
        public boolean isCompleted(Integer currenthours) {
            try {return currenthours >= getGoal_Hours();}
            catch (NullPointerException | ArithmeticException ignored) {return false;}
        }
        public String getpercentagecompleted(double currenthours) {
            float percent = (float) currenthours / getGoal_Hours().floatValue();
            percent *= 100;
            if (percent >= 100) {percent = 100;}
            return String.format("%.2f", percent) + "%";
        }

        @Override
        public String toString() {
            return String.format("Set Date: %s Goal Hours: %s Is Completed: %s Date Completed: %s", getDate_Set(), getGoal_Hours(), getCompleted(), getDate_Completed());
        }
    }

}


