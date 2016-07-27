package kujiin.xml;

import kujiin.MainController;
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
    private MainController Root;

    public Preset() {}
    public Preset(MainController root) {
        Root = root;
    }

// XML Processing
    public void marshall(File presetfile) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Preset.class);
        Marshaller createMarshaller = context.createMarshaller();
        createMarshaller.marshal(this, presetfile);
    }
    public void unmarshall(File presetfile) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Preset.class);
        Unmarshaller createMarshaller = context.createUnmarshaller();
        Preset loadedsession = (Preset) createMarshaller.unmarshal(presetfile);
        PreTime = loadedsession.PreTime;
        RinTime = loadedsession.PreTime;
        KyoTime = loadedsession.PreTime;
        TohTime = loadedsession.PreTime;
        ShaTime = loadedsession.PreTime;
        KaiTime = loadedsession.PreTime;
        JinTime = loadedsession.PreTime;
        RetsuTime = loadedsession.PreTime;
        ZaiTime = loadedsession.PreTime;
        ZenTime = loadedsession.PreTime;
        EarthTime = loadedsession.PreTime;
        AirTime = loadedsession.PreTime;
        FireTime = loadedsession.PreTime;
        WaterTime = loadedsession.PreTime;
        VoidTime = loadedsession.PreTime;
        PostTime = loadedsession.PreTime;
    }

// Methods
    public boolean savepreset() {
        File presetfile = Util.filechooser_save(Root.getScene(), "Save Preset As", null);
        if (presetfile != null && Util.file_extensioncorrect(Root, "xml", presetfile).getName().endsWith(".xml")) {
            try {
                marshall(presetfile);
                return true;
            } catch (JAXBException e) {return false;}
        } else {return false;}
    }
    public File openpreset() {
        File presetfile = Util.filechooser_single(Root.getScene(), "Load Session Preset", null);
        if (presetfile != null && presetfile.getName().endsWith(".xml")) {
            try {unmarshall(presetfile); return presetfile;}
            catch (JAXBException ignored) {return null;}
        } else {return presetfile;}
    }
    public ArrayList<Double> getpresettimes() {
        return new ArrayList<>(Arrays.asList(PreTime, RinTime, KyoTime, TohTime, ShaTime, KaiTime, JinTime, RetsuTime, ZaiTime, ZenTime, EarthTime, AirTime, FireTime, WaterTime, VoidTime, PostTime));
    }
    public void setpresettimes(ArrayList<Double> creatorvalues) {
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
    public boolean validpreset() {
        for (Double i : getpresettimes()) {if (i == null) {return false;}}
        return true;
    }

}
