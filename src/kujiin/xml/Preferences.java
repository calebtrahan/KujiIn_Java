package kujiin.xml;

import javafx.application.Platform;
import javafx.scene.image.Image;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.util.enums.IconDisplayType;
import kujiin.util.enums.QuickAddAmbienceType;
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

@XmlRootElement(name = "Preferences")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Preferences {
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
    public static final File DIRECTORYRAMP = new File(SOUNDDIRECTORY, "ramp/");
    public static final File DEFAULTSTYLESHEET = new File(DIRECTORYSTYLES, "default.css");
    // XML File Location Constants
    public static final File OPTIONSXMLFILE = new File(XMLDIRECTORY, "options.xml");
    public static final File DIRECTORYIMAGES = new File(ROOTDIRECTORY, "assets/img/");
    public static final File DIRECTORYICONS = new File(DIRECTORYIMAGES, "icons/");
    // Icons
    public static final Image ICON_ADDTOFAVORITE = new Image(new File(DIRECTORYICONS, "AddToFavorite.png").toURI().toString());
    public static final Image ICON_ADD = new Image(new File(DIRECTORYICONS, "Add.png").toURI().toString());
    public static final Image ICON_REMOVE = new Image(new File(DIRECTORYICONS, "Remove.png").toURI().toString());
    public static final Image ICON_EXPORTTOAUDIO = new Image(new File(DIRECTORYICONS, "ExportSession.png").toURI().toString());
    public static final Image ICON_EXPORTTODOCUMENT = new Image(new File(DIRECTORYICONS, "ExportToFile.png").toURI().toString());
    public static final Image ICON_FAVORITES = new Image(new File(DIRECTORYICONS, "Favorites.png").toURI().toString());
    public static final Image ICON_FULLSCREEN = new Image(new File(DIRECTORYICONS, "Fullscreen.png").toURI().toString());
    public static final Image ICON_MOVEDOWN = new Image(new File(DIRECTORYICONS, "MoveDown.png").toURI().toString());
    public static final Image ICON_MOVEUP = new Image(new File(DIRECTORYICONS, "MoveUp.png").toURI().toString());
    public static final Image ICON_PAUSE = new Image(new File(DIRECTORYICONS, "Pause.png").toURI().toString());
    public static final Image ICON_PLAY = new Image(new File(DIRECTORYICONS, "Play.png").toURI().toString());
    public static final Image ICON_RECENTSESSIONS = new Image(new File(DIRECTORYICONS, "RecentlyPlayed.png").toURI().toString());
    public static final Image ICON_CLEARSESSION = new Image(new File(DIRECTORYICONS, "ClearSession.png").toURI().toString());
    public static final Image ICON_SETTINGS = new Image(new File(DIRECTORYICONS, "Settings.png").toURI().toString());
    public static final Image ICON_STOP = new Image(new File(DIRECTORYICONS, "Stop.png").toURI().toString());
    public static final Image ICON_OPENFILE = new Image(new File(DIRECTORYICONS, "OpenFile.png").toURI().toString());
    public static final Image ICON_EDITDURATION = new Image(new File(DIRECTORYICONS, "EditDuration.png").toURI().toString());
    public static final Image ICON_AMBIENCE = new Image(new File(DIRECTORYICONS, "Ambience.png").toURI().toString());
    public static final File GOALSXMLFILE = new File(XMLDIRECTORY, "goals.xml");
    public static final File SESSIONSXMLFILE_TESTING = new File(XMLDIRECTORY, "sessions_testing.xml");
    public static final File SESSIONSXMLFILE = new File(XMLDIRECTORY, "sessions.xml");
    public static final File ENTRAINMENTXMLFILE = new File(XMLDIRECTORY, "entrainment.xml");
    public static final File AMBIENCEXMLFILE = new File(XMLDIRECTORY, "ambience.xml");
    public static final File RAMPFILESXMLFILE = new File(XMLDIRECTORY, "rampfiles.xml");
    public static final File AVAILABLEAMBIENCEXMLFILE = new File(XMLDIRECTORY, "ambience.xml");
    public static final File FAVORITESESSIONSXMLFILE = new File(XMLDIRECTORY, "favoritesessions.xml");
    public final static File TESTFILE = new File(SOUNDDIRECTORY, "Test.mp3");
    // Name Constants
    public static final ArrayList<String> ALLNAMES = new ArrayList<>(Arrays.asList("Qi-Gong", "Rin", "Kyo", "Toh", "Sha", "Kai", "Jin", "Retsu", "Zai", "Zen", "Earth", "Air", "Fire", "Water", "Void"));
/// Default Option Values
    // Program Preferences
    private static final Boolean DEFAULT_TOOLTIPS_OPTION = true;
    private static final Boolean DEFAULT_HELP_DIALOGS_OPTION = true;
    private static final Integer DEFAULT_SCROLL_INCREMENT = 1;
    // Session Preferences
    public static final Double FADE_VALUE_MAX_DURATION = 15.0;
    public static final Double VOLUME_SLIDER_ADJUSTMENT_INCREMENT = 5.0;
    private static final Double DEFAULT_ENTRAINMENTVOLUME = 0.6;
    private static final Double DEFAULT_AMBIENCEVOLUME = 1.0;
    private static final Boolean DEFAULT_FADE_PLAY_ENABLED = true;
    private static final Double DEFAULT_FADE_PLAY_DURATION = 10.0;
    private static final Boolean DEFAULT_FADE_STOP_ENABLED = true;
    private static final Double DEFAULT_FADE_STOP_DURATION = 10.0;
    private static final Boolean DEFAULT_FADE_RESUME_ENABLED = true;
    private static final Double DEFAULT_FADE_RESUME_DURATION = 2.0;
    private static final Boolean DEFAULT_FADE_PAUSE_ENABLED = true;
    private static final Double DEFAULT_FADE_PAUSE_DURATION = 2.0;
    private static final Boolean DEFAULT_ALERTFUNCTION_OPTION = false;
    private static final String DEFAULT_ALERTFILELOCATION = null; // (Dialog Selecting A New Alert File)
    public static final File REFERENCE_THEMEFILE = new File(DIRECTORYSTYLES, "referencefile.css");
    private static final Boolean DEFAULT_RAMP_ENABLED_OPTION = true;
    private static final QuickAddAmbienceType DEFAULT_AMBIENCE_QUICKADD_TYPE = QuickAddAmbienceType.SHUFFLE;
    public static final ReferenceType DEFAULT_REFERENCE_TYPE_OPTION = ReferenceType.html;
    private static final Boolean DEFAULT_REFERENCE_DISPLAY = false;
    public static final Integer DEFAULT_LONG_SESSIONPART_DURATION = 10;
    public static final Double DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION = 5.0;
    public static final Image PROGRAM_ICON = new Image(new File(Preferences.DIRECTORYICONS, "mainwinicon.jpg").toURI().toString());
    public final static String NO_ALERT_FILE_SELECTED_TEXT = "No Alert File Selected";
    public final static int SUGGESTED_ALERT_FILE_MAX_LENGTH = 10;
    public final static int ABSOLUTE_ALERT_FILE_MAX_LENGTH = 30;
    // Files
    private UserInterfaceOptions UserInterfaceOptions;
    private CreationOptions CreationOptions;
    private ExportOptions ExportOptions;
    private SessionOptions SessionOptions;
    private PlaybackOptions PlaybackOptions;
    private AdvancedOptions AdvancedOptions;

    public Preferences() {}

// Getters And Setters
    public UserInterfaceOptions getUserInterfaceOptions() {
    return UserInterfaceOptions;
}
    public void setUserInterfaceOptions(UserInterfaceOptions userInterfaceOptions) {UserInterfaceOptions = userInterfaceOptions;}
    public SessionOptions getSessionOptions() {
        return SessionOptions;
    }
    public void setSessionOptions(SessionOptions sessionOptions) {
        SessionOptions = sessionOptions;
    }
    public AdvancedOptions getAdvancedOptions() {
        return AdvancedOptions;
    }
    public void setAdvancedOptions(AdvancedOptions advancedOptions) {AdvancedOptions = advancedOptions;}
    public Preferences.CreationOptions getCreationOptions() {return CreationOptions;}
    public void setCreationOptions(Preferences.CreationOptions creationOptions) {CreationOptions = creationOptions;}
    public Preferences.ExportOptions getExportOptions() {return ExportOptions;}
    public void setExportOptions(Preferences.ExportOptions exportOptions) {ExportOptions = exportOptions;}
    public Preferences.PlaybackOptions getPlaybackOptions() {return PlaybackOptions;}
    public void setPlaybackOptions(Preferences.PlaybackOptions playbackOptions) {PlaybackOptions = playbackOptions;}

    // XML Processing
    public void unmarshall() {
        if (OPTIONSXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(Preferences.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                Preferences preferences = (Preferences) unmarshaller.unmarshal(OPTIONSXMLFILE);
                setUserInterfaceOptions(preferences.getUserInterfaceOptions());
                setCreationOptions(preferences.getCreationOptions());
                setExportOptions(preferences.getExportOptions());
                setSessionOptions(preferences.getSessionOptions());
                setPlaybackOptions(preferences.getPlaybackOptions());
                setAdvancedOptions(preferences.getAdvancedOptions());
            } catch (JAXBException ignored) {Platform.runLater(() -> new InformationDialog(this, "Information", "Couldn't Open Preferences", "Check Read File Permissions Of \n" + OPTIONSXMLFILE.getName(), true));}
        } else {resettodefaults();}
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Preferences.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, OPTIONSXMLFILE);
        } catch (JAXBException e) {
            new InformationDialog(this, "Information", "Couldn't Save Preferences", "Check Write File Permissions Of " + OPTIONSXMLFILE.getAbsolutePath(), true);
        }
    }
    public void resettodefaults() {
        UserInterfaceOptions userInterfaceOptions = new UserInterfaceOptions();
        kujiin.xml.Preferences.CreationOptions creationOptions = new CreationOptions();
        kujiin.xml.Preferences.ExportOptions exportOptions = new ExportOptions();
        SessionOptions sessionOptions = new SessionOptions();
        kujiin.xml.Preferences.PlaybackOptions playbackOptions = new PlaybackOptions();
        AdvancedOptions advancedOptions = new AdvancedOptions();
        userInterfaceOptions.setTooltips(DEFAULT_TOOLTIPS_OPTION);
        userInterfaceOptions.setHelpdialogs(DEFAULT_HELP_DIALOGS_OPTION);
        userInterfaceOptions.setIconDisplayType(IconDisplayType.ICONS_AND_TEXT);
        creationOptions.setScrollincrement(DEFAULT_SCROLL_INCREMENT);
        creationOptions.setQuickaddambiencetype(DEFAULT_AMBIENCE_QUICKADD_TYPE);
        sessionOptions.setAlertfunction(DEFAULT_ALERTFUNCTION_OPTION);
        sessionOptions.setAlertfilelocation(DEFAULT_ALERTFILELOCATION);
        sessionOptions.setRampenabled(DEFAULT_RAMP_ENABLED_OPTION);
        sessionOptions.setReferenceoption(DEFAULT_REFERENCE_DISPLAY);
        sessionOptions.setReferencetype(DEFAULT_REFERENCE_TYPE_OPTION);
        playbackOptions.setAnimation_fade_play_enabled(DEFAULT_FADE_PLAY_ENABLED);
        playbackOptions.setAnimation_fade_play_value(DEFAULT_FADE_PLAY_DURATION);
        playbackOptions.setAnimation_fade_stop_enabled(DEFAULT_FADE_STOP_ENABLED);
        playbackOptions.setAnimation_fade_stop_value(DEFAULT_FADE_STOP_DURATION);
        playbackOptions.setAnimation_fade_resume_enabled(DEFAULT_FADE_RESUME_ENABLED);
        playbackOptions.setAnimation_fade_resume_value(DEFAULT_FADE_RESUME_DURATION);
        playbackOptions.setAnimation_fade_pause_enabled(DEFAULT_FADE_PAUSE_ENABLED);
        playbackOptions.setAnimation_fade_pause_value(DEFAULT_FADE_PAUSE_DURATION);
        playbackOptions.setEntrainmentvolume(DEFAULT_ENTRAINMENTVOLUME);
        playbackOptions.setAmbiencevolume(DEFAULT_AMBIENCEVOLUME);
        advancedOptions.setOS(System.getProperty("os.name"));
        advancedOptions.setDebugmode(false);
        setUserInterfaceOptions(userInterfaceOptions);
        setCreationOptions(creationOptions);
        setExportOptions(exportOptions);
        setSessionOptions(sessionOptions);
        setPlaybackOptions(playbackOptions);
        setAdvancedOptions(advancedOptions);
        marshall();
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
        private IconDisplayType iconDisplayType;

        public UserInterfaceOptions() {}

    // Getters And Setters
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
        public IconDisplayType getIconDisplayType() {
            return iconDisplayType;
        }
        public void setIconDisplayType(IconDisplayType iconDisplayType) {
            this.iconDisplayType = iconDisplayType;
        }

    }
    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static class CreationOptions {
        private Integer scrollincrement;
        private QuickAddAmbienceType quickaddambiencetype;

        public CreationOptions() {}

    // Getters And Setters
        public Integer getScrollincrement() {
            return scrollincrement;
        }
        public void setScrollincrement(Integer scrollincrement) {
            this.scrollincrement = scrollincrement;
        }
        public QuickAddAmbienceType getQuickaddambiencetype() {
            return quickaddambiencetype;
        }
        public void setQuickaddambiencetype(QuickAddAmbienceType quickaddambiencetype) {
            this.quickaddambiencetype = quickaddambiencetype;
        }

    }
    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static class ExportOptions {

        public ExportOptions() {}
    }
    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static class SessionOptions {
        private Boolean alertfunction;
        private String alertfilelocation;
        private Boolean rampenabled;
        private Boolean referenceoption;
        private ReferenceType referencetype;

        public SessionOptions() {}

    // Getters And Setters
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

    }
    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static class PlaybackOptions {
        private Boolean animation_fade_play_enabled;
        private Double animation_fade_play_value;
        private Boolean animation_fade_stop_enabled;
        private Double animation_fade_stop_value;
        private Boolean animation_fade_resume_enabled;
        private Double animation_fade_resume_value;
        private Boolean animation_fade_pause_enabled;
        private Double animation_fade_pause_value;
        private Double entrainmentvolume;
        private Double ambiencevolume;

        public PlaybackOptions() {}

    // Getters And Setters
        public Boolean getAnimation_fade_play_enabled() {
            return animation_fade_play_enabled;
        }
        public void setAnimation_fade_play_enabled(Boolean animation_fade_play_enabled) {
            this.animation_fade_play_enabled = animation_fade_play_enabled;
        }
        public Double getAnimation_fade_play_value() {
            return animation_fade_play_value;
        }
        public void setAnimation_fade_play_value(Double animation_fade_play_value) {
            this.animation_fade_play_value = animation_fade_play_value;
        }
        public Boolean getAnimation_fade_stop_enabled() {
            return animation_fade_stop_enabled;
        }
        public void setAnimation_fade_stop_enabled(Boolean animation_fade_stop_enabled) {
            this.animation_fade_stop_enabled = animation_fade_stop_enabled;
        }
        public Double getAnimation_fade_stop_value() {
            return animation_fade_stop_value;
        }
        public void setAnimation_fade_stop_value(Double animation_fade_stop_value) {
            this.animation_fade_stop_value = animation_fade_stop_value;
        }
        public Boolean getAnimation_fade_resume_enabled() {
            return animation_fade_resume_enabled;
        }
        public void setAnimation_fade_resume_enabled(Boolean animation_fade_resume_enabled) {
            this.animation_fade_resume_enabled = animation_fade_resume_enabled;
        }
        public Double getAnimation_fade_resume_value() {
            return animation_fade_resume_value;
        }
        public void setAnimation_fade_resume_value(Double animation_fade_resume_value) {
            this.animation_fade_resume_value = animation_fade_resume_value;
        }
        public Boolean getAnimation_fade_pause_enabled() {
            return animation_fade_pause_enabled;
        }
        public void setAnimation_fade_pause_enabled(Boolean animation_fade_pause_enabled) {
            this.animation_fade_pause_enabled = animation_fade_pause_enabled;
        }
        public Double getAnimation_fade_pause_value() {
            return animation_fade_pause_value;
        }
        public void setAnimation_fade_pause_value(Double animation_fade_pause_value) {
            this.animation_fade_pause_value = animation_fade_pause_value;
        }
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

    }
    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static class AdvancedOptions {
        private boolean debugmode;
        private String OS;

        public AdvancedOptions() {}

    // Getters And Setters
        public boolean isDebugmode() {
            return debugmode;
        }
        public void setDebugmode(boolean debugmode) {
            this.debugmode = debugmode;
        }
        public String getOS() {
            return OS;
        }
        public void setOS(String OS) {
            this.OS = OS;
        }
    }

}
