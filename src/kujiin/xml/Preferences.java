package kujiin.xml;

import javafx.application.Platform;
import javafx.scene.image.Image;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.util.enums.AmbiencePlaybackType;
import kujiin.util.enums.IconDisplayType;
import kujiin.util.enums.ReferenceType;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
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
    public static final File AVAILABLEAMBIENCEXMLFILE = new File(XMLDIRECTORY, "availableambience.xml");
    public static final File FAVORITESESSIONSXMLFILE = new File(XMLDIRECTORY, "favoritesessions.xml");
    public final static File TESTFILE = new File(SOUNDDIRECTORY, "Test.mp3");

    // Name Constants
    public static final ArrayList<String> QIGONGNAMES = new ArrayList<>(Arrays.asList("Presession", "Postsession"));
    public static final ArrayList<String> CUTNAMES = new ArrayList<>(Arrays.asList("RIN", "KYO", "TOH", "SHA", "KAI", "JIN", "RETSU", "ZAI", "ZEN"));
    public static final ArrayList<String> ELEMENTNAMES = new ArrayList<>(Arrays.asList("Earth", "Air", "Fire", "Water", "Void"));
    public static final ArrayList<String> ALLNAMES = new ArrayList<>(Arrays.asList("Presession", "RIN", "KYO", "TOH", "SHA", "KAI", "JIN", "RETSU", "ZAI", "ZEN", "Earth", "Air", "Fire", "Water", "Void", "Postsession"));
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
    private static final File DEFAULT_THEMEFILE = new File(DIRECTORYSTYLES, "default.css");
    public static final File REFERENCE_THEMEFILE = new File(DIRECTORYSTYLES, "referencefile.css");
    private static final Boolean DEFAULT_RAMP_ENABLED_OPTION = true;
    private static final Boolean DEFAULT_PREPOST_RAMP_ENABLED_OPTION = true;
    private static final AmbiencePlaybackType DEFAULT_AMBIENCE_PLAYBACK_TYPE = AmbiencePlaybackType.SHUFFLE;
    public static final ReferenceType DEFAULT_REFERENCE_TYPE_OPTION = ReferenceType.html;
    private static final Boolean DEFAULT_REFERENCE_DISPLAY = false;
    private static final Boolean DEFAULT_REFERENCE_FULLSCREEN_OPTION = true;
    private static final Boolean DEFAULT_AMBIENCE_OPTION = false;
    public static final String[] AMBIENCE_EDITOR_TYPES = {"Simple", "Advanced"};
    public static final Integer DEFAULT_LONG_SESSIONPART_DURATION = 10;
    public static final Double DEFAULT_RAMP_ONLY_RAMP_ANIMATION_DURATION = 5.0;
    public static final String NEWGOALTEXT = "New Goal";
    public static final String GOALPACINGTEXT = "Goal Pacing";
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
                if (! preferences.getAdvancedOptions().getOS().equals(System.getProperty("os.name"))) {
                // Correct Theme File Directory Names
                    ArrayList<String> newfiles = new ArrayList<>();
                    int currentthemeindex = -1;
                    for (String i : preferences.getUserInterfaceOptions().getThemefiles()) {
                        if (i.equals(preferences.getUserInterfaceOptions().getThemefile())) {currentthemeindex = preferences.getUserInterfaceOptions().getThemefiles().indexOf(i);}
                        if (! i.equals("CASPIAN") && ! i.equals("MODENA")) {
                            File newfile = new File(DIRECTORYSTYLES, i.substring(i.lastIndexOf("/")));
                            newfiles.add(newfile.toURI().toString());
                        }
                    }
                    if (currentthemeindex != -1) {preferences.getUserInterfaceOptions().setThemefile(newfiles.get(currentthemeindex));}
                    else {preferences.getUserInterfaceOptions().setThemefile(DEFAULT_THEMEFILE.toURI().toString());}
                    preferences.getAdvancedOptions().setOS(System.getProperty("os.name"));
                }
                setUserInterfaceOptions(preferences.getUserInterfaceOptions());
                setCreationOptions(preferences.getCreationOptions());
                setExportOptions(preferences.getExportOptions());
                setSessionOptions(preferences.getSessionOptions());
                setPlaybackOptions(preferences.getPlaybackOptions());
                setAdvancedOptions(preferences.getAdvancedOptions());
            } catch (JAXBException ignored) {Platform.runLater(() -> new InformationDialog(this, "Information", "Couldn't Open Preferences", "Check Read File Permissions Of \n" + OPTIONSXMLFILE.getName()));}
        } else {resettodefaults();}
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Preferences.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, OPTIONSXMLFILE);
        } catch (JAXBException e) {
            new InformationDialog(this, "Information", "Couldn't Save Preferences", "Check Write File Permissions Of " + OPTIONSXMLFILE.getAbsolutePath());
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
        userInterfaceOptions.setThemefile(DEFAULT_THEMEFILE.toURI().toString());
        userInterfaceOptions.setThemefiles(new ArrayList<>(Arrays.asList(DEFAULT_THEMEFILE.toURI().toString())));
        userInterfaceOptions.setThemefilenames(new ArrayList<>(Arrays.asList("Default")));
        userInterfaceOptions.setIconDisplayType(IconDisplayType.ICONS_AND_TEXT);
        creationOptions.setScrollincrement(DEFAULT_SCROLL_INCREMENT);
        sessionOptions.setAlertfunction(DEFAULT_ALERTFUNCTION_OPTION);
        sessionOptions.setAlertfilelocation(DEFAULT_ALERTFILELOCATION);
        sessionOptions.setRampenabled(DEFAULT_RAMP_ENABLED_OPTION);
        sessionOptions.setPrepostrampenabled(DEFAULT_PREPOST_RAMP_ENABLED_OPTION);
        sessionOptions.setReferenceoption(DEFAULT_REFERENCE_DISPLAY);
        sessionOptions.setReferencetype(DEFAULT_REFERENCE_TYPE_OPTION);
        sessionOptions.setReferencefullscreen(DEFAULT_REFERENCE_FULLSCREEN_OPTION);
        sessionOptions.setAmbienceoption(DEFAULT_AMBIENCE_OPTION);
        sessionOptions.setAmbiencePlaybackType(DEFAULT_AMBIENCE_PLAYBACK_TYPE);
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
        advancedOptions.setDefaultambienceeditor(AMBIENCE_EDITOR_TYPES[0]);
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
    public void addthemefile(String name, String file_location) {
        ArrayList<String> files = getUserInterfaceOptions().getThemefiles();
        ArrayList<String> names =  getUserInterfaceOptions().getThemefilenames();
        if (! files.contains(file_location)) {
            try {
                File newstylefile = new File(DIRECTORYSTYLES, file_location.substring(file_location.lastIndexOf("/")));
                FileUtils.moveFile(new File(file_location), newstylefile);
                files.add(newstylefile.toURI().toString());
                names.add(name);
                getUserInterfaceOptions().setThemefiles(files);
                getUserInterfaceOptions().setThemefilenames(names);
            } catch (IOException e) {e.printStackTrace();}
        } else {}
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
        private String themefile;
        private ArrayList<String> themefiles;
        private ArrayList<String> themefilenames;
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

        public CreationOptions() {}

    // Getters And Setters
        public Integer getScrollincrement() {
            return scrollincrement;
        }
        public void setScrollincrement(Integer scrollincrement) {
            this.scrollincrement = scrollincrement;
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
        private Boolean prepostrampenabled;
        private Boolean referenceoption;
        private ReferenceType referencetype;
        private Boolean referencefullscreen;
        private Boolean ambienceoption;
        private AmbiencePlaybackType ambiencePlaybackType;

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
        public Boolean getAmbienceoption() {
            return ambienceoption;
        }
        public void setAmbienceoption(Boolean ambienceoption) {
            this.ambienceoption = ambienceoption;
        }
        public AmbiencePlaybackType getAmbiencePlaybackType() {
            return ambiencePlaybackType;
        }
        public void setAmbiencePlaybackType(AmbiencePlaybackType ambiencePlaybackType) {
            this.ambiencePlaybackType = ambiencePlaybackType;
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
        private String defaultambienceeditor;
        private boolean debugmode;
        private String OS;

        public AdvancedOptions() {}

    // Getters And Setters
        public String getDefaultambienceeditor() {
            return defaultambienceeditor;
        }
        public void setDefaultambienceeditor(String defaultambienceeditor) {
            this.defaultambienceeditor = defaultambienceeditor;
        }
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
