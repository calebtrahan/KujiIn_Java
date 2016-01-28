package kujiin.xml;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import kujiin.This_Session;
import kujiin.Tools;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;

@XmlRootElement(name = "Options")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Options {
    /// Default Values
    private static final Boolean TOOLTIPS = true;
    private static final Boolean HELPDIALOGS = true;
    private static final Double ENTRAINMENTVOLUME = 0.6; // Default Entrainment Volume (Textfield -> In Percentage)
    private static final Double AMBIENCEVOLUME = 1.0; // Default Ambience Volume (Textfield -> In Percentage)
    private static final Double FADEINDURATION = 10.0; // Fade In Duration (Textfield -> In Decimal Seconds)
    private static final Double FADEOUTDURATION = 10.0; // Fade Out Duration (Textfield -> In Decimal Seconds)
    private static final Boolean PREMATUREENDINGS = true; // Premature Endings Reason Dialog (Checkbox)
    private static final String ALERTFILELOCATION = null; // (Dialog Selecting A New Alert File)
    private static final String THEMEFILELOCATION = null;
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
        if (This_Session.optionsxmlfile.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(kujiin.xml.Options.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                kujiin.xml.Options options = (kujiin.xml.Options) unmarshaller.unmarshal(This_Session.optionsxmlfile);
                setProgramOptions(options.getProgramOptions());
                setSessionOptions(options.getSessionOptions());
                setAppearanceOptions(options.getAppearanceOptions());
            } catch (JAXBException e) {
                e.printStackTrace();
                Platform.runLater(() -> Tools.showinformationdialog("Information", "Couldn't Open Options", "Check Read File Permissions Of \n" +
                        This_Session.optionsxmlfile.getName()));
            }
        } else {
            kujiin.xml.Options.ProgramOptions programOptions = new ProgramOptions();
            programOptions.setTooltips(TOOLTIPS);
            programOptions.setHelpdialogs(HELPDIALOGS);
            setProgramOptions(programOptions);
            kujiin.xml.Options.SessionOptions sessionOptions = new SessionOptions();
            sessionOptions.setPrematureendings(PREMATUREENDINGS);
            sessionOptions.setFadeoutduration(FADEOUTDURATION);
            sessionOptions.setAmbiencevolume(AMBIENCEVOLUME);
            sessionOptions.setAlertfilelocation(ALERTFILELOCATION);
            sessionOptions.setFadeinduration(FADEINDURATION);
            sessionOptions.setEntrainmentvolume(ENTRAINMENTVOLUME);
            setSessionOptions(sessionOptions);
            kujiin.xml.Options.AppearanceOptions appearanceOptions = new AppearanceOptions();
            appearanceOptions.setThemefile(THEMEFILELOCATION);
            setAppearanceOptions(appearanceOptions);
            marshall();
        }
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(Options.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, This_Session.optionsxmlfile);
        } catch (JAXBException e) {
            Tools.showinformationdialog("Information", "Couldn't Save Options", "Check Write File Permissions Of " + This_Session.optionsxmlfile.getAbsolutePath());
        }
    }
    public void getnewAlertFile() {
        File newalertfile = new FileChooser().showOpenDialog(null);
        if (newalertfile == null) {return;}
        boolean validaudiofile = Tools.validaudiofile(newalertfile);
        if (validaudiofile) {
//            setAlertfile(newalertfile);
            marshall();
        } else {Tools.showinformationdialog("Information", "Not A Valid Audio File", "Supported Audio Formats: " + Tools.supportedaudiotext());}
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
        private Boolean prematureendings; // Premature Endings Reason Dialog (Checkbox)
        private String alertfilelocation; // (Dialog Selecting A New Alert File)

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
        public Boolean getPrematureendings() {
            return prematureendings;
        }
        public void setPrematureendings(Boolean prematureendings) {
            this.prematureendings = prematureendings;
        }
        public String getAlertfilelocation() {
            return alertfilelocation;
        }
        public void setAlertfilelocation(String alertfilelocation) {
            this.alertfilelocation = alertfilelocation;
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
