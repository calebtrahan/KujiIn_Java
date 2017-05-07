package kujiin.xml;

import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import kujiin.util.Util;
import kujiin.util.enums.FreqType;
import kujiin.util.enums.ReferenceType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static kujiin.util.Util.dateFormat;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Session {
    private String Date_Practiced;
    private ArrayList<PlaybackItem> playbackItems;
    private Double ExpectedSessionDuration;
    private Double ActualSessionDuration;
    private FreqType freqType;
    @XmlTransient
    private Duration elapsedtime;

    public Session() {
        ActualSessionDuration = 0.0;
        elapsedtime = Duration.ZERO;
        setDate_Practiced(LocalDate.now());
    }

// Getters And Setters
    public void setDate_Practiced(LocalDate date_Practiced) {Date_Practiced = date_Practiced.format(dateFormat);}
    public LocalDate getDate_Practiced() {return LocalDate.parse(Date_Practiced, dateFormat);}
    public Duration getActualSessionDuration() {
        return new Duration(ActualSessionDuration);
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
    public void setElapsedtime(Duration elapsedtime) {
        this.elapsedtime = elapsedtime;
    }
    public double getElapsedTime() {return elapsedtime.toMillis();}
    public PlaybackItem getplaybackitem(int index) {
        switch (index) {
            case 0:
                return new QiGong();
            case 1:
                return new Rin();
            case 2:
                return new Kyo();
            case 3:
                return new Toh();
            case 4:
                return new Sha();
            case 5:
                return new Kai();
            case 6:
                return new Jin();
            case 7:
                return new Retsu();
            case 8:
                return new Zai();
            case 9:
                return new Zen();
            case 10:
                return new Earth();
            case 11:
                return new Air();
            case 12:
                return new Fire();
            case 13:
                return new Water();
            case 14:
                return new Void();
            default:
                return null;
        }
    }
    public void addplaybackitems(List<PlaybackItem> playbackitems) {if (playbackItems == null) {playbackItems = new ArrayList<>();} playbackItems.addAll(playbackitems);}
    public void addplaybackitem(int index) {
        if (playbackItems == null) {playbackItems = new ArrayList<>();}
        switch (index) {
            case 0:
                playbackItems.add(new QiGong());
                break;
            case 1:
                playbackItems.add(new Rin());
                break;
            case 2:
                playbackItems.add(new Kyo());
                break;
            case 3:
                playbackItems.add(new Toh());
                break;
            case 4:
                playbackItems.add(new Sha());
                break;
            case 5:
                playbackItems.add(new Kai());
                break;
            case 6:
                playbackItems.add(new Jin());
                break;
            case 7:
                playbackItems.add(new Retsu());
                break;
            case 8:
                playbackItems.add(new Zai());
                break;
            case 9:
                playbackItems.add(new Zen());
                break;
            case 10:
                playbackItems.add(new Earth());
                break;
            case 11:
                playbackItems.add(new Air());
                break;
            case 12:
                playbackItems.add(new Fire());
                break;
            case 13:
                playbackItems.add(new Water());
                break;
            case 14:
                playbackItems.add(new Void());
                break;
            default:
                break;
        }
    }
    public void removeplaybackitem(int index) {
        playbackItems.remove(index);
    }

// Utility Methods
    protected void calculateactualduration() {
        Duration duration = Duration.ZERO;
        for (PlaybackItem i : getPlaybackItems()) {
            duration = duration.add(Duration.millis(i.getElapsedTime()));
        }
        ActualSessionDuration = duration.toMillis();
    }
    public void calculateexpectedduration() {
        Duration duration = Duration.ZERO;
        for (PlaybackItem i : getPlaybackItems()) {
            duration = duration.add(Duration.millis(i.getDuration()));
        }
        ExpectedSessionDuration = duration.toMillis();
    }
    public void addelapsedtime(Duration duration) {
        elapsedtime = elapsedtime.add(duration);
    }
    public boolean isValid() {
        return Duration.minutes(ActualSessionDuration).greaterThan(Duration.ZERO);
    }

// Subclasses
    @XmlAccessorType(XmlAccessType.FIELD)
    public class PlaybackItem {
        protected int creationindex;
        protected int playbackindex;
        protected String Name;
        private double Duration; // As Millis
        private boolean RampOnly;
        private Ambience ambience;
        private PlaybackItemEntrainment playbackItemEntrainment;
        @XmlTransient
        private Duration elapsedtime;
        @XmlTransient
        private ArrayList<Goals.Goal> GoalsCompletedThisSession;

        public PlaybackItem() {}
        public PlaybackItem(String name) {
            this.Name = name;
            Duration = 0.0;
            elapsedtime = javafx.util.Duration.ZERO;
            ambience = new Ambience();
        }

    // Getters And Setters
        public int getCreationindex() {
            return creationindex;
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
        public void setDuration(double duration) {
            Duration = duration;
        }
        public double getDuration() {
                return Duration;
            }
        public void setElapsedtime(javafx.util.Duration elapsedtime) {
            this.elapsedtime = elapsedtime;
            calculateactualduration();
        }
        public double getElapsedTime() {return elapsedtime.toMillis();}
        public String getdurationasString(double maxchars) {
            if (Duration == 0.0 && ! RampOnly) {return "No Duration Set";}
            else {
                if (Duration == 0.0 && RampOnly) {return "Ramp Only";}
                else {return Util.formatdurationtoStringSpelledOut(new Duration(getDuration()), maxchars);}
            }
        }
        public String getAmbienceasString() {
            if (ambience.getAmbience() == null || ambience.getAmbience().isEmpty()) {return "No Ambience Set";}
            else {return "Ambience Set " + "(" + ambience.getAmbience().size() + " Files)";}
        }
        public void updateduration(Duration duration) {this.Duration = duration.toMillis();}
        public ArrayList<Goals.Goal> getGoalsCompletedThisSession() {
            return GoalsCompletedThisSession;
        }
        public Ambience getAmbience() {
            return ambience;
        }
        public void setAmbience(Ambience ambience) {
            this.ambience = ambience;
        }

    // Utility Methods
        public void addelapsedtime(Duration duration) {elapsedtime = elapsedtime.add(duration);}
        public void addCompletedGoal(Goals.Goal Goal) {
            if (GoalsCompletedThisSession == null) {
                GoalsCompletedThisSession = new ArrayList<>();}
            GoalsCompletedThisSession.add(Goal);
        }
        public boolean isValid() {return javafx.util.Duration.seconds(Duration).greaterThan(javafx.util.Duration.ZERO);}
        public Tooltip getTooltip() {return new Tooltip(toString());}

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

    // Creation Methods
//        public boolean creation_buildEntrainment() {
//            if (root.getPreferences().getSessionOptions().getRampenabled()) {
//                try {
//                    int index = allsessionpartstoplay.indexOf(this);
//                    SessionItem partafter = allsessionpartstoplay.get(index + 1);
//                    if ((partafter instanceof Qi_Gong || partafter instanceof Element) && ! name.equals("ZEN")) {playbackItemEntrainment.setRampfile(playbackItemEntrainment.ramp_get(1));}
//                    else {playbackItemEntrainment.setRampfile(playbackItemEntrainment.ramp_get(0));}
//                    if (ramponly) {setDuration(Duration.millis(playbackItemEntrainment.getRampfile().getDuration()));}
//                    return super.creation_buildEntrainment() && playbackItemEntrainment.getRampfile().isValid();
//                } catch (IndexOutOfBoundsException ignored) {return false;}
//            }
//            return super.creation_buildEntrainment();
//        }

    // Startup Methods
//        public int startup_entrainmentpartcount() {
//            if (index == 9) {return 2;}
//            else {return 3;}
//        }
//        public SoundFile startup_getnextentrainment() throws IndexOutOfBoundsException {
//            SoundFile soundFile;
//            File file;
//            switch (startupchecks_entrainment_count) {
//                case 0:
//                    soundFile = playbackItemEntrainment.getFreq();
//                    file = new File(Preferences.DIRECTORYENTRAINMENT, getNameForFiles().toUpperCase() + ".mp3");
//                    break;
//                case 1:
//                    soundFile = playbackItemEntrainment.ramp_get(0);
//                    if (index != 9) {
//                        file = new File(Preferences.DIRECTORYENTRAINMENT, "ramp/" + getNameForFiles() + "to" +
//                                root.getSessionPart_Names(1, 10).get(root.getSessionPart_Names(1, 10).indexOf(name) + 1).toLowerCase() + ".mp3");
//                    } else {file = new File(Preferences.DIRECTORYENTRAINMENT, "ramp/" + getNameForFiles() + "toqi.mp3");}
//                    break;
//                case 2:
//                    if (index == 9) {startupCheckType = StartupCheckType.AMBIENCE; throw new IndexOutOfBoundsException();}
//                    else {
//                        soundFile = playbackItemEntrainment.ramp_get(1);
//                        file = new File(Preferences.DIRECTORYENTRAINMENT, "ramp/" + getNameForFiles() + "toqi.mp3");
//                        break;
//                    }
//                default:
//                    throw new IndexOutOfBoundsException();
//            }
//            if (soundFile == null && file.exists()) {soundFile = new SoundFile(file);}
//            return soundFile;
//        }

    // String Methods
        @Override
        public String toString() {
            return Name;
        }
        public String getNameforFiles() {return Name.toLowerCase();}

    }
    public class Cut extends PlaybackItem {
        @XmlTransient
        protected int cutindex;
        @XmlTransient
        protected String focuspoint;
        @XmlTransient
        protected String concept;
        @XmlTransient
        protected String mantrameaning;
        @XmlTransient
        protected String sideeffects;

        public Cut(String name) {
            super(name);
        }

        @Override
        public String toString() {
            return super.Name + "\n" +
                    "Focus Point: " + focuspoint + "\n" +
                    "Concept: " + concept + "\n" +
                    "Mantra Meaning: " + mantrameaning + "\n" +
                    "Side Effects: " + sideeffects + "\n";
        }
//        // PlaybackItemEntrainment
//        @Override
//        public int startup_entrainmentpartcount() {
//            if (number == 9) {return 2;}
//            else {return 3;}
//        }
//        @Override
//        public SoundFile startup_getnextentrainment() throws IndexOutOfBoundsException {
//            SoundFile soundFile;
//            File file;
//            switch (startupchecks_entrainment_count) {
//                case 0:
//                    soundFile = playbackItemEntrainment.getFreq();
//                    file = new File(Preferences.DIRECTORYENTRAINMENT, getNameForFiles().toUpperCase() + ".mp3");
//                    break;
//                case 1:
//                    soundFile = playbackItemEntrainment.ramp_get(0);
//                    if (number != 9) {
//                        file = new File(Preferences.DIRECTORYENTRAINMENT, "ramp/" + getNameForFiles() + "to" +
//                                root.getSessionPart_Names(1, 10).get(root.getSessionPart_Names(1, 10).indexOf(name) + 1).toLowerCase() + ".mp3");
//                    } else {file = new File(Preferences.DIRECTORYENTRAINMENT, "ramp/" + getNameForFiles() + "toqi.mp3");}
//                    break;
//                case 2:
//                    if (number == 9) {startupCheckType = StartupCheckType.AMBIENCE; throw new IndexOutOfBoundsException();}
//                    else {
//                        soundFile = playbackItemEntrainment.ramp_get(1);
//                        file = new File(Preferences.DIRECTORYENTRAINMENT, "ramp/" + getNameForFiles() + "toqi.mp3");
//                        break;
//                    }
//                default:
//                    throw new IndexOutOfBoundsException();
//            }
//            if (soundFile == null && file.exists()) {soundFile = new SoundFile(file);}
//            return soundFile;
//        }
//
//        // Creation
//        @Override
//        public boolean creation_buildEntrainment() {
//            if (root.getPreferences().getSessionOptions().getRampenabled()) {
//                try {
//                    int index = allsessionpartstoplay.indexOf(this);
//                    SessionPart partafter = allsessionpartstoplay.get(index + 1);
//                    if ((partafter instanceof Qi_Gong || partafter instanceof kujiin.util.Element) && ! name.equals("ZEN")) {playbackItemEntrainment.setRampfile(playbackItemEntrainment.ramp_get(1));}
//                    else {playbackItemEntrainment.setRampfile(playbackItemEntrainment.ramp_get(0));}
//                    if (ramponly) {setDuration(Duration.millis(playbackItemEntrainment.getRampfile().getDuration()));}
//                    return super.creation_buildEntrainment() && playbackItemEntrainment.getRampfile().isValid();
//                } catch (IndexOutOfBoundsException ignored) {return false;}
//            }
//            return super.creation_buildEntrainment();
//        }
    }
    public class Element extends PlaybackItem {

        public Element(String name) {
            super(name);
        }

//        // Gettters And Setters
//        @Override
//        public Tooltip getTooltip() {
//            return super.getTooltip();
//        }
//        @Override
//        public String getNameForFiles() {return "qi";}
//
//        // PlaybackItemEntrainment
//        @Override
//        public int startup_entrainmentpartcount() {
//            return 10;
//        }
//
//        // Creation
//        @Override
//        public boolean creation_buildEntrainment() {
//            if (root.getPreferences().getSessionOptions().getRampenabled()) {
//                try {
//                    int index = allsessionpartstoplay.indexOf(this);
//                    SessionPart parttotest = allsessionpartstoplay.get(index + 1);
//                    SoundFile rampfile;
//                    if (parttotest instanceof Qi_Gong || parttotest instanceof kujiin.util.Element) {rampfile = playbackItemEntrainment.getFreq();}
//                    else {rampfile = playbackItemEntrainment.ramp_get(Preferences.CUTNAMES.indexOf(parttotest.name.toUpperCase()));}
//                    playbackItemEntrainment.setRampfile(rampfile);
//                    if (ramponly) {setDuration(Duration.millis(playbackItemEntrainment.getRampfile().getDuration()));}
//                    return super.creation_buildEntrainment() && playbackItemEntrainment.getRampfile().isValid();
//                } catch (IndexOutOfBoundsException ignored) {return super.creation_buildEntrainment();}
//            } else {return super.creation_buildEntrainment();}
//        }
    }
    public class QiGong extends PlaybackItem {
        protected final String summary = "Gather Qi (Life Energy) Before The Session Starts";

        public QiGong() {
            super("Qi-Gong");
            super.creationindex = 0;
        }

        @Override
        public String toString() {
            return super.Name + "\n" + summary;
        }

        @Override
        public File getReferenceFile(ReferenceType referenceType) {
            switch (referenceType) {
                case html: {
                    String name = "Qi-Gong.html";
                    return new File(Preferences.DIRECTORYREFERENCE, "html/" + name);
                }
                case txt: {
                    String name = "Qi-Gong.txt";
                    return new File(Preferences.DIRECTORYREFERENCE, "txt/" + name);
                }
                default:
                    return null;
            }
        }

//        @Override
//        public boolean creation_buildEntrainment() {
//            if (root.getPreferences().getSessionOptions().getRampenabled()) {
//                int index = allsessionpartstoplay.indexOf(this);
//                SessionPart parttotest;
//                switch (number) {
//                    case 0:
//                        parttotest = allsessionpartstoplay.get(index + 1);
//                        break;
//                    case 15:
//                        parttotest = allsessionpartstoplay.get(index - 1);
//                        break;
//                    default:
//                        parttotest = null;
//                }
//                SoundFile rampfile;
//                if (parttotest instanceof Qi_Gong || parttotest instanceof Element) {rampfile = playbackItemEntrainment.getFreq();}
//                else {rampfile = playbackItemEntrainment.ramp_get(Preferences.ALLNAMES.indexOf(parttotest.name.toUpperCase()) - 1);}
//                playbackItemEntrainment.setRampfile(rampfile);
//                if (ramponly) {setDuration(Duration.millis(playbackItemEntrainment.getRampfile().getDuration()));}
//                return super.creation_buildEntrainment() && playbackItemEntrainment.getRampfile().isValid();
//            } else {return super.creation_buildEntrainment();}
//        }
    }
    public class Rin extends Cut {

        public Rin() {
            super("Rin");
            super.creationindex = 1;
            cutindex = 1;
            super.focuspoint = "Root Chakra";
            super.concept = "A Celebration Of The Spirit Coming Into The Body";
            super.mantrameaning = "All/Everything/Vast As It Is Defined Now";
            super.sideeffects = "Increases The Bioelectric Output Of The Body";
        }
    }
    public class Kyo extends Cut {

        public Kyo() {
            super("Kyo");
            super.creationindex = 2;
            cutindex = 2;
            super.focuspoint = "Navel Chakra";
            super.concept = "In Order To Become Powerful, Responsiblity Must Be Taken For All Actions";
            super.mantrameaning = "Use Your Tools/Manage Yourself Correctly";
            super.sideeffects = "Increases The Healthy Flow Of Energy Leading To The Mastery Of The Control And Direction Of Energy";
        }
    }
    public class Toh extends Cut {

        public Toh() {
            super("Toh");
            super.creationindex = 3;
            cutindex = 3;
            super.focuspoint = "Dan-tian";
            super.concept = "Conscious Dissolement Of All Personal Fights In Order To Achieve Harmony";
            super.mantrameaning = "Conquering Limiting Beliefs/Doubts Will Allow You To Get The Treasures Of Life";
            super.sideeffects = "Enhances Your Positive Relationship With The Universe, Resulting In Improved Harmony And Balance";
        }
    }
    public class Sha extends Cut {

        public Sha() {
            super("Sha");
            super.creationindex = 4;
            cutindex = 4;
            super.focuspoint = "Solar Plexus Charkra";
            super.concept = "By Letting Go Of The Limits Of My Mind You Can Vibrate With The Power Of The Universe And Exist Fully Powerful";
            super.mantrameaning = "Grounded, I Understand The Power That I Express";
            super.sideeffects = "Increases The Healing Ability Of The Body As A Result Of Higher Energy Levels Passing Through The Body";
        }
    }
    public class Kai extends Cut {

        public Kai() {
            super("Kai");
            super.creationindex = 5;
            cutindex = 5;
            super.focuspoint = "Heart Chakra";
            super.concept = "Everything (Created Or Not) In The Universe Is One";
            super.mantrameaning = "I Acknowledge The All Pervading Conscious State of Things As They Are, And I Live It [I Am Conscious Of EVERYTHING]";
            super.sideeffects = "Develops Foreknowledge, Premonition, Intuition And Feeling By Acknowlegding That Everything Is One";
        }
    }
    public class Jin extends Cut {

        public Jin() {
            super("Jin");
            super.creationindex = 6;
            cutindex = 6;
            super.focuspoint = "Throat Chakra";
            super.concept = "An Observation Of The Universe And What Binds Every Part Of Us To Every Part Of Everything Else";
            super.mantrameaning = "Conscious Experience Of The Fire That Everything Is Really Made Of";
            super.sideeffects = "By Understanding The ";
        }
    }
    public class Retsu extends Cut {

        public Retsu() {
            super("Retsu");
            super.creationindex = 7;
            cutindex = 7;
            super.focuspoint = "Jade Gate Chakra";
            super.concept = "Transmute The Limits Of Perception By Remembering Our Wholeness As Spirit";
            super.mantrameaning = "Everything Flows/Is Elevated To The Divine";
            super.sideeffects = "Enhances Your Perception And Mastery Of Space-Time Dimensions";
        }
    }
    public class Zai extends Cut {

        public Zai() {
            super("Zai");
            super.creationindex = 8;
            cutindex = 8;
            super.focuspoint = "Third Eye Chakra";
            super.concept = "Works With Our Mind, Heart And Body In Order To Define Ourselves As A Spirit That Is Having A Human Experience, Rather Than A Human Being Sometimes Having A Spiritual Experience";
            super.mantrameaning = "Everything Is Manifested In The Correct Way According To The Experience That I Live";
            super.sideeffects = "Increases My Power Of Manifestation By Fostering A Relationship With The Elements Of Creation";
        }
    }
    public class Zen extends Cut {

        public Zen() {
            super("Zen");
            super.creationindex = 9;
            cutindex = 9;
            super.focuspoint = "Crown Chakra";
            super.concept = "The Human Completely Relents Itself To The Spirit With Only The Consciousness Aspect Of The Human Remaining Active";
            super.mantrameaning = "I am the void and the light";
            super.sideeffects = "Completely Relenting Into Spirit Results In Englightenment, Completeness, Suggestive Invisibility";
        }
    }
    public class Earth extends Element {

        public Earth() {
            super("Earth");
            super.creationindex = 10;
        }
    }
    public class Air extends Element {

        public Air() {
            super("Air");
            super.creationindex = 11;
        }
    }
    public class Fire extends Element {

        public Fire() {
            super("Fire");
            super.creationindex = 12;
        }
    }
    public class Water extends Element {

        public Water() {
            super("Water");
            super.creationindex = 13;
        }
    }
    public class Void extends Element {

        public Void() {
            super("Void");
            super.creationindex = 14;
        }
    }

}