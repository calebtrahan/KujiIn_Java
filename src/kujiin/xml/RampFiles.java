package kujiin.xml;

import kujiin.ui.MainController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class RampFiles {
    private List<SoundFile> RampFiles;

    public RampFiles() {}
    public RampFiles(MainController root) {
        unmarshall();
    }

// XML Processing
    public void unmarshall() {
        if (Preferences.RAMPFILESXMLFILE.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(RampFiles.class);
                Unmarshaller createMarshaller = context.createUnmarshaller();
                RampFiles rampfiles = (RampFiles) createMarshaller.unmarshal(Preferences.RAMPFILESXMLFILE);
                RampFiles = rampfiles.getRampFiles();
            } catch (JAXBException ignored) {populatedefaults();}
        } else {populatedefaults();}
    }
    public void marshall() {
        try {
            JAXBContext context = JAXBContext.newInstance(RampFiles.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            createMarshaller.marshal(this, Preferences.RAMPFILESXMLFILE);
        } catch (JAXBException ignored) {ignored.printStackTrace();}
    }

// Getters And Setters
    public List<SoundFile> getRampFiles() {
        return RampFiles;
    }
    public void setRampFiles(List<SoundFile> rampFiles) {
        RampFiles = rampFiles;
    }

// Utility Methods
    public void add(SoundFile soundFile) {RampFiles.add(soundFile);}
    public void remove(int index) {RampFiles.remove(index);}
    public SoundFile getRampFile(Session.PlaybackItem from, Session.PlaybackItem to) {
        StringBuilder name = new StringBuilder();
        if (from instanceof Session.QiGong) {name.append("qi");}
        else {name.append(from.getName());}
        if (to instanceof Session.QiGong) {name.append("qi");}
        else {name.append(to.getName());}
        for (SoundFile i : RampFiles) {if (i.getName().equals(name.toString())) {return i;}}
        return null;
    }
    private void populatedefaults() {
        String[] names = {"rin", "kyo", "toh", "sha", "kai", "jin", "retsu", "zai", "zen", "earth", "air", "fire", "water", "void"};
        ArrayList<SoundFile> filenames = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            for (int x = 0; x < 2; x++) {
                if (x == 0) {filenames.add(new SoundFile(new File(Preferences.DIRECTORYRAMP, names[i] + "to" + "qi.mp3")));}
                else if (i < names.length - 1) {filenames.add(new SoundFile(new File(Preferences.DIRECTORYRAMP, names[i] + "to" + names[i + 1] + ".mp3")));}
            }
        }
        if (RampFiles != null) {RampFiles.clear();} else {RampFiles = new ArrayList<>();}
        RampFiles.addAll(filenames);
    }

}