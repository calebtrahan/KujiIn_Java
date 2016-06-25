package kujiin.xml;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import kujiin.MainController;
import kujiin.ui.PlayerUI;
import kujiin.util.Util;

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
    public static final File DIRECTORYSTYLES = new File(ROOTDIRECTORY, "assets/styles/");
    public static final File SOUNDDIRECTORY = new File(ROOTDIRECTORY, "assets/sound/");
    public static final File DIRECTORYTEMP = new File(SOUNDDIRECTORY, "temp/");
    public static final File DIRECTORYENTRAINMENT = new File(SOUNDDIRECTORY, "entrainment/");
    public static final File DIRECTORYRAMP = new File(DIRECTORYENTRAINMENT, "ramp/");
    // XML File Location Constants
    public static final File OPTIONSXMLFILE = new File(XMLDIRECTORY, "options.xml");
    public static final File DIRECTORYIMAGES = new File(ROOTDIRECTORY, "assets/img/");
    public static final File GOALSXMLFILE = new File(XMLDIRECTORY, "goals.xml");
    public static final File SESSIONSXMLFILE = new File(XMLDIRECTORY, "sessions.xml");
    public static final File ENTRAINMENTXMLFILE = new File(XMLDIRECTORY, "entrainment.xml");
    public static final File AMBIENCEXMLFILE = new File(XMLDIRECTORY, "ambience.xml");

    // Name Constants
    public static final ArrayList<String> QIGONGNAMES = new ArrayList<>(Arrays.asList("Presession", "Postsession"));
    public static final ArrayList<String> CUTNAMES = new ArrayList<>(Arrays.asList("RIN", "KYO", "TOH", "SHA", "KAI", "JIN", "RETSU", "ZAI", "ZEN"));
    public static final ArrayList<String> ELEMENTNAMES = new ArrayList<>(Arrays.asList("Earth", "Air", "Fire", "Water", "Void"));
    public static final ArrayList<String> ALLNAMES = new ArrayList<>(
            Arrays.asList("Presession", "RIN", "KYO", "TOH", "SHA", "KAI", "JIN", "RETSU", "ZAI", "ZEN", "Earth", "Air", "Fire", "Water", "Void", "Postsession")
    );
    public static ArrayList<String> STYLETHEMES = new ArrayList<>();
/// Default Option Values
    public static final Boolean DEFAULT_TOOLTIPS_OPTION = true;
    public static final Boolean DEFAULT_HELP_DIALOGS_OPTION = true;
    public static final Double FADE_VALUE_MAX_DURATION = 60.0;
    public static final Double VOLUME_SLIDER_ADJUSTMENT_INCREMENT = 5.0;
    public static final Double DEFAULT_ENTRAINMENTVOLUME = 0.6;
    public static final Double DEFAULT_AMBIENCEVOLUME = 1.0;
    public static final Double DEFAULT_FADEINDURATION = 10.0;
    public static final Double DEFAULT_FADEOUTDURATION = 10.0;
    public static final Double DEFAULT_FADERESUMEANDPAUSEDURATION = 2.0;
    public static final Boolean DEFAULT_ALERTFUNCTION_OPTION = true;
    public static final String DEFAULT_ALERTFILELOCATION = null; // (Dialog Selecting A New Alert File)
    public static final String DEFAULT_THEMEFILE = new File(DIRECTORYSTYLES, "dark.css").toURI().toString();
    public static final Boolean DEFAULT_RAMP_ENABLED_OPTION = true;
    public static final Integer DEFAULT_RAMP_DURATION = 2;
    public static final PlayerUI.ReferenceType DEFAULT_REFERENCE_TYPE_OPTION = PlayerUI.ReferenceType.html;
    public static final Boolean DEFAULT_REFERENCE_DISPLAY = false;
    public static final Boolean DEFAULT_REFERENCE_FULLSCREEN_OPTION = true;
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
                Platform.runLater(() -> Util.gui_showinformationdialog(Root, "Information", "Couldn't Open Options", "Check Read File Permissions Of \n" +
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
            Util.gui_showinformationdialog(Root, "Information", "Couldn't Save Options", "Check Write File Permissions Of " + OPTIONSXMLFILE.getAbsolutePath());
        }
    }
    public void resettodefaults() {
        kujiin.xml.Options.ProgramOptions programOptions = new ProgramOptions();
        programOptions.setTooltips(DEFAULT_TOOLTIPS_OPTION);
        programOptions.setHelpdialogs(DEFAULT_HELP_DIALOGS_OPTION);
        setProgramOptions(programOptions);
        kujiin.xml.Options.SessionOptions sessionOptions = new SessionOptions();
        sessionOptions.setFadeoutduration(DEFAULT_FADEOUTDURATION);
        sessionOptions.setAmbiencevolume(DEFAULT_AMBIENCEVOLUME);
        sessionOptions.setAlertfunction(DEFAULT_ALERTFUNCTION_OPTION);
        sessionOptions.setAlertfilelocation(DEFAULT_ALERTFILELOCATION);
        sessionOptions.setFadeinduration(DEFAULT_FADEINDURATION);
        sessionOptions.setEntrainmentvolume(DEFAULT_ENTRAINMENTVOLUME);
        sessionOptions.setRampenabled(DEFAULT_RAMP_ENABLED_OPTION);
        sessionOptions.setRampduration(DEFAULT_RAMP_DURATION);
        sessionOptions.setReferenceoption(DEFAULT_REFERENCE_DISPLAY);
        sessionOptions.setReferencetype(DEFAULT_REFERENCE_TYPE_OPTION);
        sessionOptions.setReferencefullscreen(DEFAULT_REFERENCE_FULLSCREEN_OPTION);
        setSessionOptions(sessionOptions);
        kujiin.xml.Options.AppearanceOptions appearanceOptions = new AppearanceOptions();
        appearanceOptions.setThemefile(DEFAULT_THEMEFILE);
        setAppearanceOptions(appearanceOptions);
        marshall();
    }
    public void setStyle(Stage stage) {
        stage.getIcons().clear();
        stage.getIcons().add(new Image(new File(Options.DIRECTORYIMAGES, "icons/mainwinicon.jpg").toURI().toString()));
        String themefile = getAppearanceOptions().getThemefile();
        if (themefile != null) {stage.getScene().getStylesheets().add(themefile);}
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
        private PlayerUI.ReferenceType referencetype;
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
        public PlayerUI.ReferenceType getReferencetype() {
            return referencetype;
        }
        public void setReferencetype(PlayerUI.ReferenceType referencetype) {
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
