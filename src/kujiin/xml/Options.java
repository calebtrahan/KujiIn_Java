package kujiin.xml;

import javafx.application.Platform;
import javafx.scene.Scene;
import kujiin.MainController;
import kujiin.Tools;
import kujiin.widgets.PlayerWidget;

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

@XmlRootElement(name = "Options")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Options {
// Directory Constants
    public static final File PROJECTROOT = new File(System.getProperty("user.dir"));
    public static final File ROOTDIRECTORY = new File(PROJECTROOT, "src/kujiin/");
    public static final File DIRECTORYREFERENCE = new File(ROOTDIRECTORY, "assets/reference/");
    public static final File LOGFILE = new File(ROOTDIRECTORY, "assets/sessionlog.txt");
    public static final File XMLDIRECTORY = new File(ROOTDIRECTORY, "assets/xml/");
    public static final File OPTIONSXMLFILE = new File(XMLDIRECTORY, "options.xml");
    public static final File GOALSXMLFILE = new File(XMLDIRECTORY, "goals.xml");
    public static final File DIRECTORYSTYLES = new File(ROOTDIRECTORY, "assets/styles/");
    public static final File SESSIONSXMLFILE = new File(XMLDIRECTORY, "sessions.xml");
    public static final File SOUNDDIRECTORY = new File(ROOTDIRECTORY, "assets/sound/");
    public static final File DIRECTORYTEMP = new File(SOUNDDIRECTORY, "temp/");
    public static final File DIRECTORYAMBIENCE = new File(SOUNDDIRECTORY, "ambience/");
    public static final File DIRECTORYENTRAINMENT = new File(SOUNDDIRECTORY, "entrainment/");
    public static final File DIRECTORYRAMPUP = new File(DIRECTORYENTRAINMENT, "ramp/up/");
    public static final File DIRECTORYRAMPDOWN = new File(DIRECTORYENTRAINMENT, "ramp/down/");
    public static final File DIRECTORYTOHRAMP = new File(DIRECTORYENTRAINMENT, "tohramp/");
    public static final File DIRECTORYELEMENTRAMP = new File(DIRECTORYENTRAINMENT, "elementramp/");
    public static final File DIRECTORYMAINCUTS = new File(DIRECTORYENTRAINMENT, "maincuts/");
    public static final ArrayList<String> CUTNAMES = new ArrayList<>(Arrays.asList(
            "Presession", "RIN", "KYO", "TOH", "SHA", "KAI", "JIN", "RETSU", "ZAI", "ZEN", "Postsession"));
    public static final ArrayList<String> ELEMENTNAMES = new ArrayList<>(Arrays.asList("Earth", "Air", "Fire", "Water", "Void"));
    public static final ArrayList<String> RAMPDURATIONS = new ArrayList<>(Arrays.asList("None", "2 Minutes", "3 Minutes", "5 Minutes"));
    public static ArrayList<String> STYLETHEMES = new ArrayList<>();
/// Default Option Values
    private static final Boolean TOOLTIPS = true;
    private static final Boolean HELPDIALOGS = true;
    private static final Double ENTRAINMENTVOLUME = 0.6; // Default Entrainment Volume (Textfield -> In Percentage)
    private static final Double AMBIENCEVOLUME = 1.0; // Default Ambience Volume (Textfield -> In Percentage)
    private static final Double FADEINDURATION = 10.0; // Fade In Duration (Textfield -> In Decimal Seconds)
    private static final Double FADEOUTDURATION = 10.0; // Fade Out Duration (Textfield -> In Decimal Seconds)
    private static final Boolean ALERTFUNCTION = true;
    private static final String ALERTFILELOCATION = null; // (Dialog Selecting A New Alert File)
    private static final String THEMEFILELOCATION = new File(DIRECTORYSTYLES, "dark.css").toURI().toString();
    private static final Boolean RAMPENABLED = true;
    private static final Integer RAMPDURATION = 3;
    private static final PlayerWidget.ReferenceType REFERENCE_TYPE = null;
    private static final Boolean REFERENCEDISPLAY = false;
    private static final Boolean REFERENCEFULLSCREEN = true;
    private ProgramOptions ProgramOptions;
    private SessionOptions SessionOptions;
    private AppearanceOptions AppearanceOptions;
    private MainController Root;

    public Options() {}
    public Options(MainController root) {
        Root = root;
    }

// Getters And Setters
    public Options.ProgramOptions getProgramOptions() {
    return ProgramOptions;
}
    public void setProgramOptions(Options.ProgramOptions programOptions) {
        ProgramOptions = programOptions;
    }
    public Options.SessionOptions getSessionOptions() {
        return SessionOptions;
    }
    public void setSessionOptions(Options.SessionOptions sessionOptions) {
        SessionOptions = sessionOptions;
    }
    public Options.AppearanceOptions getAppearanceOptions() {
        return AppearanceOptions;
    }
    public void setAppearanceOptions(Options.AppearanceOptions appearanceOptions) {
        AppearanceOptions = appearanceOptions;
    }

// XML Processing
    public void unmarshall() {
        if (OPTIONSXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(kujiin.xml.Options.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                kujiin.xml.Options options = (kujiin.xml.Options) unmarshaller.unmarshal(OPTIONSXMLFILE);
                setProgramOptions(options.getProgramOptions());
                setSessionOptions(options.getSessionOptions());
                setAppearanceOptions(options.getAppearanceOptions());
            } catch (JAXBException ignored) {
                Platform.runLater(() -> Tools.showinformationdialog(Root, "Information", "Couldn't Open Options", "Check Read File Permissions Of \n" +
                        OPTIONSXMLFILE.getName()));
            }
        } else {
            resettodefaults();
        }
    }
    public void marshall() {
        try {
            getSessionOptions().setRampenabled(getSessionOptions().getRampduration() > 0);
            JAXBContext context = JAXBContext.newInstance(Options.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, OPTIONSXMLFILE);
        } catch (JAXBException e) {
            Tools.showinformationdialog(Root, "Information", "Couldn't Save Options", "Check Write File Permissions Of " + OPTIONSXMLFILE.getAbsolutePath());
        }
    }
    public void resettodefaults() {
        kujiin.xml.Options.ProgramOptions programOptions = new ProgramOptions();
        programOptions.setTooltips(TOOLTIPS);
        programOptions.setHelpdialogs(HELPDIALOGS);
        setProgramOptions(programOptions);
        kujiin.xml.Options.SessionOptions sessionOptions = new SessionOptions();
        sessionOptions.setFadeoutduration(FADEOUTDURATION);
        sessionOptions.setAmbiencevolume(AMBIENCEVOLUME);
        sessionOptions.setAlertfunction(ALERTFUNCTION);
        sessionOptions.setAlertfilelocation(ALERTFILELOCATION);
        sessionOptions.setFadeinduration(FADEINDURATION);
        sessionOptions.setEntrainmentvolume(ENTRAINMENTVOLUME);
        sessionOptions.setRampenabled(RAMPENABLED);
        sessionOptions.setRampduration(RAMPDURATION);
        sessionOptions.setReferenceoption(REFERENCEDISPLAY);
        sessionOptions.setReferencetype(REFERENCE_TYPE);
        sessionOptions.setReferencefullscreen(REFERENCEFULLSCREEN);
        setSessionOptions(sessionOptions);
        kujiin.xml.Options.AppearanceOptions appearanceOptions = new AppearanceOptions();
        appearanceOptions.setThemefile(THEMEFILELOCATION);
        setAppearanceOptions(appearanceOptions);
        marshall();
    }
    public void setStyle(Scene scene) {
        scene.getStylesheets().clear();
        String themefile = getAppearanceOptions().getThemefile();
        if (themefile != null) {scene.getStylesheets().add(themefile);}
    }

// Subclasses
    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static class ProgramOptions {
        private Boolean tooltips; // Show Tooltips (Checkbox)
        private Boolean helpdialogs; // Show Help Dialogs (Checkbox)

        public ProgramOptions() {}

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
}
    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static class SessionOptions {
        private Double entrainmentvolume;
        private Double ambiencevolume;
        private Double fadeinduration;
        private Double fadeoutduration;
        private String alertfilelocation;
        private Boolean rampenabled;
        private Boolean alertfunction;
        private Integer rampduration;
        private Boolean referenceoption;
        private PlayerWidget.ReferenceType referencetype;
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
        public Integer getRampduration() {
            return rampduration;
        }
        public void setRampduration(Integer rampduration) {
            this.rampduration = rampduration;
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
        public PlayerWidget.ReferenceType getReferencetype() {
            return referencetype;
        }
        public void setReferencetype(PlayerWidget.ReferenceType referencetype) {
            this.referencetype = referencetype;
        }
        public Boolean getReferencefullscreen() {
            return referencefullscreen;
        }
        public void setReferencefullscreen(Boolean referencefullscreen) {
            this.referencefullscreen = referencefullscreen;
        }
    }
    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static class AppearanceOptions {
        private String themefile;

        public AppearanceOptions() {}

        public String getThemefile() {
            return themefile;
        }
        public void setThemefile(String themefile) {
            this.themefile = themefile;
        }
    }
}
