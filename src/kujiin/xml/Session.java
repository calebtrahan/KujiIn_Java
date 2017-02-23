package kujiin.xml;

import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import kujiin.util.Util;
import kujiin.util.enums.FreqType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.time.LocalDate;
import java.util.ArrayList;

import static kujiin.util.Util.dateFormat;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Session {
    private String Date_Practiced;
    private ArrayList<PlaybackItem> playbackItems;
    private Double SessionDuration; // In Millis
    private FreqType freqType;

    public Session() {
        SessionDuration = 0.0;
        setDate_Practiced(LocalDate.now());
    }

// Getters And Setters
    public void setDate_Practiced(LocalDate date_Practiced) {Date_Practiced = date_Practiced.format(dateFormat);}
    public LocalDate getDate_Practiced() {return LocalDate.parse(Date_Practiced, dateFormat);}
    public Duration getSessionDuration() {
        Duration totalduration = Duration.ZERO;
        for (PlaybackItem i : playbackItems) {
            totalduration = totalduration.add(new Duration(i.getDuration()));
        }
        return totalduration;
    }
    public void setPlaybackItems(ArrayList<PlaybackItem> playbackItems) {
        this.playbackItems = playbackItems;
    }
    public ArrayList<PlaybackItem> getPlaybackItems() {
        if (playbackItems == null) {return new ArrayList<>();}
        else {return playbackItems;}
    }
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
    public boolean isValid() {
        return Duration.minutes(SessionDuration).greaterThan(Duration.ZERO);
    }

// Subclasses
    @XmlAccessorType(XmlAccessType.FIELD)
    public class PlaybackItem {
        protected int availableambienceindex;
        protected int playbackindex;
        protected String Name;
        private double Duration; // As Millis
        private boolean RampOnly;
        private Ambience ambience;
        private Entrainment entrainment;
        @XmlTransient
        private ArrayList<Goals.Goal> GoalsCompletedThisSession;

        public PlaybackItem() {}
        public PlaybackItem(String name) {
            this.Name = name;
            Duration = 0.0;
            ambience = new Ambience();
        }

    // Getters And Setters
        public int getAvailableambienceindex() {
            return availableambienceindex;
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
        public String getdurationasString(double maxchars) {
            if (Duration == 0.0 && ! RampOnly) {return "No Duration Set";}
            else {
                if (Duration == 0.0 && RampOnly) {return "Ramp Only";}
                else {return Util.formatdurationtoStringSpelledOut(new Duration(getDuration()), maxchars);}
            }
        }
        public String getAmbienceasString(double maxchars) {
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

    // Utility Methods
        public void addCompletedGoal(Goals.Goal Goal) {
            if (GoalsCompletedThisSession == null) {
                GoalsCompletedThisSession = new ArrayList<>();}
            GoalsCompletedThisSession.add(Goal);
        }
        public boolean isValid() {return javafx.util.Duration.seconds(Duration).greaterThan(javafx.util.Duration.ZERO);}
        public Tooltip getTooltip() {return new Tooltip(toString());}

    // Creation Methods
//        public boolean creation_buildEntrainment() {
//            if (root.getPreferences().getSessionOptions().getRampenabled()) {
//                try {
//                    int index = allsessionpartstoplay.indexOf(this);
//                    SessionItem partafter = allsessionpartstoplay.get(index + 1);
//                    if ((partafter instanceof Qi_Gong || partafter instanceof Element) && ! name.equals("ZEN")) {entrainment.setRampfile(entrainment.ramp_get(1));}
//                    else {entrainment.setRampfile(entrainment.ramp_get(0));}
//                    if (ramponly) {setDuration(Duration.millis(entrainment.getRampfile().getDuration()));}
//                    return super.creation_buildEntrainment() && entrainment.getRampfile().isValid();
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
//                    soundFile = entrainment.getFreq();
//                    file = new File(Preferences.DIRECTORYENTRAINMENT, getNameForFiles().toUpperCase() + ".mp3");
//                    break;
//                case 1:
//                    soundFile = entrainment.ramp_get(0);
//                    if (index != 9) {
//                        file = new File(Preferences.DIRECTORYENTRAINMENT, "ramp/" + getNameForFiles() + "to" +
//                                root.getSessionPart_Names(1, 10).get(root.getSessionPart_Names(1, 10).indexOf(name) + 1).toLowerCase() + ".mp3");
//                    } else {file = new File(Preferences.DIRECTORYENTRAINMENT, "ramp/" + getNameForFiles() + "toqi.mp3");}
//                    break;
//                case 2:
//                    if (index == 9) {startupCheckType = StartupCheckType.AMBIENCE; throw new IndexOutOfBoundsException();}
//                    else {
//                        soundFile = entrainment.ramp_get(1);
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
    }
    public class Element extends PlaybackItem {

        public Element(String name) {
            super(name);
        }
    }
    public class QiGong extends PlaybackItem {
        protected final String summary = "Gather Qi (Life Energy) Before The Session Starts";

        public QiGong() {
            super("Qi-Gong");
            super.availableambienceindex = 0;
        }

        @Override
        public String toString() {
            return super.Name + "\n" + summary;
        }
    }
    public class Rin extends Cut {

        public Rin() {
            super("Rin");
            super.availableambienceindex = 1;
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
            super.availableambienceindex = 2;
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
            super.availableambienceindex = 3;
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
            super.availableambienceindex = 4;
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
            super.availableambienceindex = 5;
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
            super.availableambienceindex = 6;
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
            super.availableambienceindex = 7;
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
            super.availableambienceindex = 8;
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
            super.availableambienceindex = 9;
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
            super.availableambienceindex = 10;
        }
    }
    public class Air extends Element {

        public Air() {
            super("Air");
            super.availableambienceindex = 11;
        }
    }
    public class Fire extends Element {

        public Fire() {
            super("Fire");
            super.availableambienceindex = 12;
        }
    }
    public class Water extends Element {

        public Water() {
            super("Water");
            super.availableambienceindex = 13;
        }
    }
    public class Void extends Element {

        public Void() {
            super("Void");
            super.availableambienceindex = 14;
        }
    }

}