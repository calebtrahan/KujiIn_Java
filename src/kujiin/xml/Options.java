package kujiin.xml;

import javafx.application.Platform;
import kujiin.Tools;

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
    public static final File projectroot = new File(System.getProperty("user.dir"));
    public static final File rootdirectory = new File(projectroot, "src/kujiin/");
    public static final File directoryreference = new File(rootdirectory, "assets/reference/");
    public static final File logfile = new File(rootdirectory, "assets/sessionlog.txt");
    public static final File xmldirectory = new File(rootdirectory, "assets/xml/");
    public static final File optionsxmlfile = new File(xmldirectory, "options.xml");
    public static final File goalsxmlfile = new File(xmldirectory, "goals.xml");
    public static final File sessionsxmlfile = new File(xmldirectory, "sessions.xml");
    public static final File sounddirectory = new File(rootdirectory, "assets/sound/");
    public static final File alertfile = new File(sounddirectory, "Alert.mp3");
    public static final File directorytemp = new File(sounddirectory, "temp/");
    public static final File directoryambience = new File(sounddirectory, "ambience/");
    public static final File directoryentrainment = new File(sounddirectory, "entrainment/");
    public static final File directoryrampup = new File(directoryentrainment, "ramp/up/");
    public static final File directoryrampdown = new File(directoryentrainment, "ramp/down/");
    public static final File directorytohramp = new File(directoryentrainment, "tohramp/");
    public static final File directorymaincuts = new File(directoryentrainment, "maincuts/");
    public static final ArrayList<String> allnames = new ArrayList<>(Arrays.asList(
            "Presession", "RIN", "KYO", "TOH", "SHA", "KAI", "JIN", "RETSU", "ZAI", "ZEN", "Postsession"));
    public static final ArrayList<String> RAMPDURATIONS = new ArrayList<>(Arrays.asList("2 Minutes", "3 Minutes", "5 Minutes"));
/// Default Option Values
    private static final Boolean TOOLTIPS = true;
    private static final Boolean HELPDIALOGS = true;
    private static final Double ENTRAINMENTVOLUME = 0.6; // Default Entrainment Volume (Textfield -> In Percentage)
    private static final Double AMBIENCEVOLUME = 1.0; // Default Ambience Volume (Textfield -> In Percentage)
    private static final Double FADEINDURATION = 10.0; // Fade In Duration (Textfield -> In Decimal Seconds)
    private static final Double FADEOUTDURATION = 10.0; // Fade Out Duration (Textfield -> In Decimal Seconds)
    private static final String ALERTFILELOCATION = null; // (Dialog Selecting A New Alert File)
    private static final String THEMEFILELOCATION = null;
    private static final Boolean RAMPENABLED = true;
    private static final Integer RAMPDURATION = 3;
    private ProgramOptions ProgramOptions;
    private SessionOptions SessionOptions;
    private AppearanceOptions AppearanceOptions;

    public Options() {}

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
        if (optionsxmlfile.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(kujiin.xml.Options.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                kujiin.xml.Options options = (kujiin.xml.Options) unmarshaller.unmarshal(optionsxmlfile);
                setProgramOptions(options.getProgramOptions());
                setSessionOptions(options.getSessionOptions());
                setAppearanceOptions(options.getAppearanceOptions());
            } catch (JAXBException e) {
                e.printStackTrace();
                Platform.runLater(() -> Tools.showinformationdialog("Information", "Couldn't Open Options", "Check Read File Permissions Of \n" +
                        optionsxmlfile.getName()));
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
            createMarshaller.marshal(this, optionsxmlfile);
        } catch (JAXBException e) {
            Tools.showinformationdialog("Information", "Couldn't Save Options", "Check Write File Permissions Of " + optionsxmlfile.getAbsolutePath());
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
        sessionOptions.setAlertfilelocation(ALERTFILELOCATION);
        sessionOptions.setFadeinduration(FADEINDURATION);
        sessionOptions.setEntrainmentvolume(ENTRAINMENTVOLUME);
        sessionOptions.setRampenabled(RAMPENABLED);
        sessionOptions.setRampduration(RAMPDURATION);
        setSessionOptions(sessionOptions);
        kujiin.xml.Options.AppearanceOptions appearanceOptions = new AppearanceOptions();
        appearanceOptions.setThemefile(THEMEFILELOCATION);
        setAppearanceOptions(appearanceOptions);
        marshall();
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
        private Double entrainmentvolume; // Default Entrainment Volume (Textfield -> In Percentage)
        private Double ambiencevolume; // Default Ambience Volume (Textfield -> In Percentage)
        private Double fadeinduration; // Fade In Duration (Textfield -> In Decimal Seconds)
        private Double fadeoutduration; // Fade Out Duration (Textfield -> In Decimal Seconds)
        private String alertfilelocation; // (Dialog Selecting A New Alert File)
        private Boolean rampenabled;
        private Integer rampduration;

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
