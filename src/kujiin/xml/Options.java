package kujiin.xml;

import javafx.application.Platform;
import javafx.scene.image.Image;
import kujiin.ui.dialogs.InformationDialog;
import kujiin.util.enums.AmbiencePlaybackType;
import kujiin.util.enums.ReferenceType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static javafx.application.Application.STYLESHEET_CASPIAN;
import static javafx.application.Application.STYLESHEET_MODENA;

@XmlRootElement(name = "Options")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Options {
    // Directory Constants
    public static final File PROJECTROOT = new File(System.getProperty("user.dir"));
    public static final File ROOTDIRECTORY = new File(PROJECTROOT, "src/kujiin/");
    public static final File DIRECTORYREFERENCE = new File(ROOTDIRECTORY, "assets/reference/");
    public static final File LOGFILE = new File(ROOTDIRECTORY, "assets/sessionlog.txt");
    public static final File XMLDIRECTORY = new File(ROOTDIRECTORY, "assets/xml/");
    public static final File DIRECTORYSTYLES = new File(ROOTDIRECTORY, "assets/styles/");
    public static final File SOUNDDIRECTORY = new File(ROOTDIRECTORY, "assets/sound/");
    public static final File DIRECTORYTEMP = new File(SOUNDDIRECTORY, "temp/");
    public static final File DIRECTORYENTRAINMENT = new File(SOUNDDIRECTORY, "entrainment/");
    public static final File DIRECTORYAMBIENCE = new File(SOUNDDIRECTORY, "ambience/");
    public static final File DIRECTORYRAMP = new File(DIRECTORYENTRAINMENT, "ramp/");
    // XML File Location Constants
    public static final File OPTIONSXMLFILE = new File(XMLDIRECTORY, "options.xml");
    public static final File DIRECTORYIMAGES = new File(ROOTDIRECTORY, "assets/img/");
    public static final File GOALSXMLFILE = new File(XMLDIRECTORY, "goals.xml");
    public static final File SESSIONSXMLFILE = new File(XMLDIRECTORY, "sessions.xml");
    public static final File ENTRAINMENTXMLFILE = new File(XMLDIRECTORY, "entrainment.xml");
    public static final File AMBIENCEXMLFILE = new File(XMLDIRECTORY, "ambience.xml");
    public final static File TESTFILE = new File(SOUNDDIRECTORY, "Test.mp3");

    // Name Constants
    public static final ArrayList<String> QIGONGNAMES = new ArrayList<>(Arrays.asList("Presession", "Postsession"));
    public static final ArrayList<String> CUTNAMES = new ArrayList<>(Arrays.asList("RIN", "KYO", "TOH", "SHA", "KAI", "JIN", "RETSU", "ZAI", "ZEN"));
    public static final ArrayList<String> ELEMENTNAMES = new ArrayList<>(Arrays.asList("Earth", "Air", "Fire", "Water", "Void"));
    public static final ArrayList<String> ALLNAMES = new ArrayList<>(Arrays.asList("Presession", "RIN", "KYO", "TOH", "SHA", "KAI", "JIN", "RETSU", "ZAI", "ZEN", "Earth", "Air", "Fire", "Water", "Void", "Postsession"));
/// Default Option Values
    // Program Options
    public static final Boolean DEFAULT_TOOLTIPS_OPTION = true;
    public static final Boolean DEFAULT_HELP_DIALOGS_OPTION = true;
    public static final Integer DEFAULT_SCROLL_INCREMENT = 1;
    // Session Options
    public static final Double FADE_VALUE_MAX_DURATION = 30.0;
    public static final Double VOLUME_SLIDER_ADJUSTMENT_INCREMENT = 5.0;
    public static final Double DEFAULT_ENTRAINMENTVOLUME = 0.6;
    public static final Double DEFAULT_AMBIENCEVOLUME = 1.0;
    public static final Boolean DEFAULT_FADE_ENABLED = true;
    public static final Double DEFAULT_FADEINDURATION = 10.0;
    public static final Double DEFAULT_FADEOUTDURATION = 10.0;
    public static final Double DEFAULT_FADERESUMEANDPAUSEDURATION = 2.0;
    public static final Boolean DEFAULT_ALERTFUNCTION_OPTION = false;
    public static final String DEFAULT_ALERTFILELOCATION = null; // (Dialog Selecting A New Alert File)
    public static final File DEFAULT_THEMEFILE = new File(DIRECTORYSTYLES, "default.css");
    public static final File REFERENCE_THEMEFILE = new File(DIRECTORYSTYLES, "referencefile.css");
    public static final Boolean DEFAULT_RAMP_ENABLED_OPTION = true;
    public static final Boolean DEFAULT_PREPOST_RAMP_ENABLED_OPTION = true;
    public static final AmbiencePlaybackType DEFAULT_AMBIENCE_PLAYBACK_TYPE = AmbiencePlaybackType.SHUFFLE;
    public static final ReferenceType DEFAULT_REFERENCE_TYPE_OPTION = ReferenceType.html;
    public static final Boolean DEFAULT_REFERENCE_DISPLAY = false;
    public static final Boolean DEFAULT_REFERENCE_FULLSCREEN_OPTION = true;
    public static final Integer DEFAULT_LONG_SESSIONPART_DURATION = 10;
    public static final Double DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION = 3.0;
    public static final String NEWGOALTEXT = "New Goal";
    public static final String GOALPACINGTEXT = "Goal Pacing";
    public static final Image PROGRAM_ICON = new Image(new File(Options.DIRECTORYIMAGES, "icons/mainwinicon.jpg").toURI().toString());
    public final static String NO_ALERT_FILE_SELECTED_TEXT = "No Alert File Selected";
    public final static int SUGGESTED_ALERT_FILE_MAX_LENGTH = 10;
    public final static int ABSOLUTE_ALERT_FILE_MAX_LENGTH = 30;
    // Files
    private UserInterfaceOptions UserInterfaceOptions;
    private SessionOptions SessionOptions;
    private ProgramOptions ProgramOptions;

    public Options() {}

// Getters And Setters
    public UserInterfaceOptions getUserInterfaceOptions() {
    return UserInterfaceOptions;
}
    public void setUserInterfaceOptions(UserInterfaceOptions userInterfaceOptions) {
        UserInterfaceOptions = userInterfaceOptions;
    }
    public SessionOptions getSessionOptions() {
        return SessionOptions;
    }
    public void setSessionOptions(SessionOptions sessionOptions) {
        SessionOptions = sessionOptions;
    }
    public ProgramOptions getProgramOptions() {
        return ProgramOptions;
    }
    public void setProgramOptions(ProgramOptions programOptions) {
        ProgramOptions = programOptions;
    }

// XML Processing
    public void unmarshall() {
        if (OPTIONSXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(kujiin.xml.Options.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                kujiin.xml.Options options = (kujiin.xml.Options) unmarshaller.unmarshal(OPTIONSXMLFILE);
                setUserInterfaceOptions(options.getUserInterfaceOptions());
                setSessionOptions(options.getSessionOptions());
                setProgramOptions(options.getProgramOptions());
            } catch (JAXBException ignored) {
                Platform.runLater(() -> new InformationDialog(this, "Information", "Couldn't Open Options", "Check Read File Permissions Of \n" +
                        OPTIONSXMLFILE.getName()));
            }
        } else {
            resettodefaults();
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Options.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, OPTIONSXMLFILE);
        } catch (JAXBException e) {
            new InformationDialog(this, "Information", "Couldn't Save Options", "Check Write File Permissions Of " + OPTIONSXMLFILE.getAbsolutePath());
        }
    }
    public void resettodefaults() {
        UserInterfaceOptions userInterfaceOptions = new UserInterfaceOptions();
        kujiin.xml.Options.SessionOptions sessionOptions = new SessionOptions();
        ProgramOptions programOptions = new ProgramOptions();
        userInterfaceOptions.setTooltips(DEFAULT_TOOLTIPS_OPTION);
        userInterfaceOptions.setHelpdialogs(DEFAULT_HELP_DIALOGS_OPTION);
        userInterfaceOptions.setScrollincrement(DEFAULT_SCROLL_INCREMENT);
        userInterfaceOptions.setThemefile(DEFAULT_THEMEFILE.toURI().toString());
        userInterfaceOptions.setThemefiles(new ArrayList<>(Arrays.asList(DEFAULT_THEMEFILE.toURI().toString(), STYLESHEET_MODENA, STYLESHEET_CASPIAN)));
        userInterfaceOptions.setThemefilenames(new ArrayList<>(Arrays.asList("Default (Dark)", "Default (Light)", "Default (Legacy)")));
        sessionOptions.setFadeoutduration(DEFAULT_FADEOUTDURATION);
        sessionOptions.setRamponlyfadeduration(DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION);
        sessionOptions.setAmbiencevolume(DEFAULT_AMBIENCEVOLUME);
        sessionOptions.setAlertfunction(DEFAULT_ALERTFUNCTION_OPTION);
        sessionOptions.setAlertfilelocation(DEFAULT_ALERTFILELOCATION);
        sessionOptions.setFadeenabled(DEFAULT_FADE_ENABLED);
        sessionOptions.setFadeinduration(DEFAULT_FADEINDURATION);
        sessionOptions.setEntrainmentvolume(DEFAULT_ENTRAINMENTVOLUME);
        sessionOptions.setRampenabled(DEFAULT_RAMP_ENABLED_OPTION);
        sessionOptions.setPrepostrampenabled(DEFAULT_PREPOST_RAMP_ENABLED_OPTION);
        sessionOptions.setReferenceoption(DEFAULT_REFERENCE_DISPLAY);
        sessionOptions.setReferencetype(DEFAULT_REFERENCE_TYPE_OPTION);
        sessionOptions.setReferencefullscreen(DEFAULT_REFERENCE_FULLSCREEN_OPTION);
        sessionOptions.setAmbiencePlaybackType(DEFAULT_AMBIENCE_PLAYBACK_TYPE);
        programOptions.setOS(System.getProperty("os.name"));
        setUserInterfaceOptions(userInterfaceOptions);
        setSessionOptions(sessionOptions);
        setProgramOptions(programOptions);
        marshall();
    }
    public void addthemefile(String name, String file_location) {
        ArrayList<String> files = getUserInterfaceOptions().getThemefiles();
        ArrayList<String> names =  getUserInterfaceOptions().getThemefilenames();
        files.add(file_location);
        names.add(name);
        getUserInterfaceOptions().setThemefiles(files);
        getUserInterfaceOptions().setThemefilenames(names);
    }
    public boolean hasValidAlertFile() {
        String location = getSessionOptions().getAlertfilelocation();
        if (location == null || location.equals("")) {return false;}
        else {
            try {return new File(location).exists();}
            catch (Exception e) {return false;}
        }
    }

// Subclasses
    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static class UserInterfaceOptions {
        private Boolean tooltips; // Show Tooltips (Checkbox)
        private Boolean helpdialogs; // Show Help Dialogs (Checkbox)
        private Integer scrollincrement; // Creator Scroll Increment
        private String themefile;
        private ArrayList<String> themefiles;
        private ArrayList<String> themefilenames;

        public UserInterfaceOptions() {}

    // Getters And Setters
        public Integer getScrollincrement() {
            return scrollincrement;
        }
        public void setScrollincrement(Integer scrollincrement) {
            this.scrollincrement = scrollincrement;
        }
        public Boolean getTooltips() {
                return tooltips;
            }
        public void setTooltips(Boolean tooltips) {
            this.tooltips = tooltips;
        }
        public Boolean getHelpdialogs() {
            return helpdialogs;
        }
        public void setHelpdialogs(Boolean helpdialogs) {
            this.helpdialogs = helpdialogs;
    }
        public String getThemefile() {
            return themefile;
        }
        public void setThemefile(String themefile) {
            this.themefile = themefile;
        }
        public ArrayList<String> getThemefiles() {
            return themefiles;
        }
        public void setThemefiles(ArrayList<String> themefiles) {
            this.themefiles = themefiles;
        }
        public ArrayList<String> getThemefilenames() {
            return themefilenames;
        }
        public void setThemefilenames(ArrayList<String> themefilenames) {
            this.themefilenames = themefilenames;
        }

    }
    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static class SessionOptions {
        private Double entrainmentvolume;
        private Double ambiencevolume;
        private Boolean fadeenabled;
        private Double fadeinduration;
        private Double fadeoutduration;
        private Double ramponlyfadeduration;
        private String alertfilelocation;
        private Boolean rampenabled;
        private Boolean prepostrampenabled;
        private Boolean alertfunction;
        private Boolean referenceoption;
        private AmbiencePlaybackType ambiencePlaybackType;
        private ReferenceType referencetype;
        private Boolean referencefullscreen;

        public SessionOptions() {}

    // Getters And Setters
        public Double getEntrainmentvolume() {
            return entrainmentvolume;
        }
        public void setEntrainmentvolume(Double entrainmentvolume) {
            this.entrainmentvolume = entrainmentvolume;
        }
        public Double getAmbiencevolume() {
            return ambiencevolume;
        }
        public void setAmbiencevolume(Double ambiencevolume) {
            this.ambiencevolume = ambiencevolume;
        }
        public Boolean getFadeenabled() {
            return fadeenabled;
        }
        public void setFadeenabled(Boolean fadeenabled) {
            this.fadeenabled = fadeenabled;
        }
        public Double getFadeinduration() {
            return fadeinduration;
        }
        public void setFadeinduration(Double fadeinduration) {
            this.fadeinduration = fadeinduration;
        }
        public Double getFadeoutduration() {
            return fadeoutduration;
        }
        public void setFadeoutduration(Double fadeoutduration) {
            this.fadeoutduration = fadeoutduration;
        }
        public Double getRamponlyfadeduration() {
            return ramponlyfadeduration;
        }
        public void setRamponlyfadeduration(Double ramponlyfadeduration) {
            this.ramponlyfadeduration = ramponlyfadeduration;
        }
        public String getAlertfilelocation() {
            return alertfilelocation;
        }
        public void setAlertfilelocation(String alertfilelocation) {
            this.alertfilelocation = alertfilelocation;
        }
        public Boolean getRampenabled() {
            return rampenabled;
        }
        public void setRampenabled(Boolean rampenabled) {
            this.rampenabled = rampenabled;
        }
        public Boolean getPrepostrampenabled() {
            return prepostrampenabled;
        }
        public void setPrepostrampenabled(Boolean prepostrampenabled) {
            this.prepostrampenabled = prepostrampenabled;
        }
        public Boolean getAlertfunction() {
            return alertfunction;
        }
        public void setAlertfunction(Boolean alertfunction) {
            this.alertfunction = alertfunction;
        }
        public Boolean getReferenceoption() {
            return referenceoption;
        }
        public void setReferenceoption(Boolean referenceoption) {
            this.referenceoption = referenceoption;
        }
        public ReferenceType getReferencetype() {
            return referencetype;
        }
        public void setReferencetype(ReferenceType referencetype) {
            this.referencetype = referencetype;
        }
        public Boolean getReferencefullscreen() {
            return referencefullscreen;
        }
        public void setReferencefullscreen(Boolean referencefullscreen) {
            this.referencefullscreen = referencefullscreen;
        }
        public AmbiencePlaybackType getAmbiencePlaybackType() {
            return ambiencePlaybackType;
        }
        public void setAmbiencePlaybackType(AmbiencePlaybackType ambiencePlaybackType) {
            this.ambiencePlaybackType = ambiencePlaybackType;
        }

    }
    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static class ProgramOptions {
        private String OS;

        public ProgramOptions() {}

    // Getters And Setters
        public String getOS() {
            return OS;
        }
        public void setOS(String OS) {
            this.OS = OS;
        }

    }

}
