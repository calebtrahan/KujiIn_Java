package kujiin.xml;

import kujiin.ui.MainController;
import kujiin.ui.dialogs.ConfirmationDialog;
import kujiin.util.Util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

@XmlRootElement(name = "Preset")
@XmlAccessorType(XmlAccessType.FIELD)
public class Preset {
    private Double PreTime;
    private Double RinTime;
    private Double KyoTime;
    private Double TohTime;
    private Double ShaTime;
    private Double KaiTime;
    private Double JinTime;
    private Double RetsuTime;
    private Double ZaiTime;
    private Double ZenTime;
    private Double EarthTime;
    private Double AirTime;
    private Double FireTime;
    private Double WaterTime;
    private Double VoidTime;
    private Double PostTime;
    @XmlTransient
    private File presetfile;
    @XmlTransient
    private MainController Root;

    public Preset() {}
    public Preset(MainController Root) {this.Root = Root;}

// XML Processing
    public boolean marshall(File presetfile) {
        try {
            JAXBContext context = JAXBContext.newInstance(Preset.class);
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.marshal(this, presetfile);
            return presetfile.exists();
        } catch (JAXBException ignored) {
            if (presetfile.exists()) {presetfile.delete();}
            return false;
        }
    }
    public boolean unmarshall(File presetfile) {
        try {
            JAXBContext context = JAXBContext.newInstance(Preset.class);
            Unmarshaller createMarshaller = context.createUnmarshaller();
            Preset loadedsession = (Preset) createMarshaller.unmarshal(presetfile);
            PreTime = loadedsession.PreTime;
            RinTime = loadedsession.RinTime;
            KyoTime = loadedsession.KyoTime;
            TohTime = loadedsession.TohTime;
            ShaTime = loadedsession.ShaTime;
            KaiTime = loadedsession.KaiTime;
            JinTime = loadedsession.JinTime;
            RetsuTime = loadedsession.RetsuTime;
            ZaiTime = loadedsession.ZaiTime;
            ZenTime = loadedsession.ZenTime;
            EarthTime = loadedsession.EarthTime;
            AirTime = loadedsession.AirTime;
            FireTime = loadedsession.FireTime;
            WaterTime = loadedsession.WaterTime;
            VoidTime = loadedsession.VoidTime;
            PostTime = loadedsession.PostTime;
            return true;
        } catch (JAXBException ignored) {return false;}
    }

// Methods
    public boolean save() {
        presetfile = Util.filechooser_save(Root.getScene(), "Save Preset As", null);
        if (presetfile == null) {return false;}
        if (! presetfile.getName().endsWith(".xml")) {
            if (! presetfile.getName().contains(".")) {
                // No File Extension
                if (new ConfirmationDialog(Root.getPreferences(), "Confirmation", null, "No File Extension", "Save As .xml", "Save As Is").getResult()) {
                    presetfile = new File(presetfile.getAbsolutePath().concat(".xml"));
                } else {
                    if (! new ConfirmationDialog(Root.getPreferences(), "Confirmation", null, "Save Preset As Invalid XML File? You May Not Be Able To Load This Preset",
                            "Save Anyway", "Cancel").getResult()) {
                        presetfile = null;
                        return false;
                    }
                }
            } else {
                // Invalid File Extension
                String extension = presetfile.getName().substring(presetfile.getName().lastIndexOf("."));
                if (new ConfirmationDialog(Root.getPreferences(), "Confirmation", null, "Invalid Extension " + extension + " Rename As .xml?", "Rename", "Keep Extension").getResult()) {
                    String filewithoutextension = presetfile.getAbsolutePath().substring(0, presetfile.getName().lastIndexOf("."));
                    presetfile = new File(filewithoutextension.concat(".xml"));
                } else {
                    if (! new ConfirmationDialog(Root.getPreferences(), "Confirmation", null, "Really Save Preset With ." + extension + " Extension? You May Not Be Able To Load This Preset",
                            "Save Anyway", "Cancel").getResult()) {
                        presetfile = null;
                        return false;
                    }
                }
            }
        }
        return marshall(presetfile);
    }
    public File open() {
        presetfile = Util.filechooser_single(Root.getScene(), "Load Session Preset", null);
        if (presetfile != null && presetfile.getName().endsWith(".xml")) {
            if (unmarshall(presetfile)) {return presetfile;}
            else {return null;}
        } else {return null;}
    }
    public ArrayList<Double> gettimes() {
        return new ArrayList<>(Arrays.asList(PreTime, RinTime, KyoTime, TohTime, ShaTime, KaiTime, JinTime, RetsuTime, ZaiTime, ZenTime, EarthTime, AirTime, FireTime, WaterTime, VoidTime, PostTime));
    }
    public void settimes(ArrayList<Double> creatorvalues) {
        try {
            PreTime = (creatorvalues.get(0));
            RinTime = (creatorvalues.get(1));
            KyoTime = (creatorvalues.get(2));
            TohTime = (creatorvalues.get(3));
            ShaTime = (creatorvalues.get(4));
            KaiTime = (creatorvalues.get(5));
            JinTime = (creatorvalues.get(6));
            RetsuTime = (creatorvalues.get(7));
            ZaiTime = (creatorvalues.get(8));
            ZenTime = (creatorvalues.get(9));
            EarthTime = (creatorvalues.get(10));
            AirTime = (creatorvalues.get(11));
            FireTime = (creatorvalues.get(12));
            WaterTime = (creatorvalues.get(13));
            VoidTime = (creatorvalues.get(14));
            PostTime = (creatorvalues.get(15));
        } catch (ArrayIndexOutOfBoundsException ignored) {}
    }
    public boolean hasvalidValues() {
        for (Double i : gettimes()) {if (i == null) {return false;}}
        return true;
    }

}
