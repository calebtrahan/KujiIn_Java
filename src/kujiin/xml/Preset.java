package kujiin.xml;

import kujiin.MainController;
import kujiin.Util;

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

@XmlRootElement(name = "Preset")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Preset {
    private Integer PreTime;
    private Integer RinTime;
    private Integer KyoTime;
    private Integer TohTime;
    private Integer ShaTime;
    private Integer KaiTime;
    private Integer JinTime;
    private Integer RetsuTime;
    private Integer ZaiTime;
    private Integer ZenTime;
    private Integer PostTime;
    private MainController Root;

    public Preset() {}
    public Preset(MainController root) {
        Root = root;
    }

    public Integer getPreTime() {
        return PreTime;
    }
    public void setPreTime(Integer preTime) {
        PreTime = preTime;
    }
    public Integer getRinTime() {
        return RinTime;
    }
    public void setRinTime(Integer rinTime) {
        RinTime = rinTime;
    }
    public Integer getKyoTime() {
        return KyoTime;
    }
    public void setKyoTime(Integer kyoTime) {
        KyoTime = kyoTime;
    }
    public Integer getTohTime() {
        return TohTime;
    }
    public void setTohTime(Integer tohTime) {
        TohTime = tohTime;
    }
    public Integer getShaTime() {
        return ShaTime;
    }
    public void setShaTime(Integer shaTime) {
        ShaTime = shaTime;
    }
    public Integer getKaiTime() {
        return KaiTime;
    }
    public void setKaiTime(Integer kaiTime) {
        KaiTime = kaiTime;
    }
    public Integer getJinTime() {
        return JinTime;
    }
    public void setJinTime(Integer jinTime) {
        JinTime = jinTime;
    }
    public Integer getRetsuTime() {
        return RetsuTime;
    }
    public void setRetsuTime(Integer retsuTime) {
        RetsuTime = retsuTime;
    }
    public Integer getZaiTime() {
        return ZaiTime;
    }
    public void setZaiTime(Integer zaiTime) {
        ZaiTime = zaiTime;
    }
    public Integer getZenTime() {
        return ZenTime;
    }
    public void setZenTime(Integer zenTime) {
        ZenTime = zenTime;
    }
    public Integer getPostTime() {
        return PostTime;
    }
    public void setPostTime(Integer postTime) {
        PostTime = postTime;
    }

    public void marshall(File presetfile) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Preset.class);
        Marshaller createMarshaller = context.createMarshaller();
        createMarshaller.marshal(this, presetfile);
    }
    public void unmarshall(File presetfile) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Preset.class);
        Unmarshaller createMarshaller = context.createUnmarshaller();
        Preset loadedsession = (Preset) createMarshaller.unmarshal(presetfile);
        setPreTime(loadedsession.getPreTime());
        setRinTime(loadedsession.getRinTime());
        setKyoTime(loadedsession.getKyoTime());
        setTohTime(loadedsession.getTohTime());
        setShaTime(loadedsession.getShaTime());
        setKaiTime(loadedsession.getKaiTime());
        setJinTime(loadedsession.getJinTime());
        setRetsuTime(loadedsession.getRetsuTime());
        setZaiTime(loadedsession.getZaiTime());
        setZenTime(loadedsession.getZenTime());
        setPostTime(loadedsession.getPostTime());
    }
    public boolean savepreset() {
        File presetfile = Util.filechooser_save(Root.getScene(), "Save Preset As", null);
        if (presetfile != null && Util.file_extensioncorrect(Root, "xml", presetfile).getName().endsWith(".xml")) {
            try {
                marshall(null);
                return true;
            } catch (JAXBException e) {
                return false;
            }
        } else {return false;}
    }
    public boolean openpreset() {
        File presetfile = Util.filechooser_single(Root.getScene(), "Load Session Preset", null);
        if (presetfile != null && presetfile.getName().endsWith(".xml")) {
            try {unmarshall(presetfile); return true;}
            catch (JAXBException ignored) {return false;}
        } else {return false;}
    }
    public ArrayList<Integer> getpresettimes() {
        return new ArrayList<>(Arrays.asList(
                getPreTime(), getRinTime(), getKyoTime(), getTohTime(), getShaTime(), getKaiTime(),
                getJinTime(), getRetsuTime(), getZaiTime(), getZenTime(), getPostTime()));
    }
    public void setpresettimes(ArrayList<Integer> creatorvalues) {
        try {
            setPreTime(creatorvalues.get(0));
            setRinTime(creatorvalues.get(1));
            setKyoTime(creatorvalues.get(2));
            setTohTime(creatorvalues.get(3));
            setShaTime(creatorvalues.get(4));
            setKaiTime(creatorvalues.get(5));
            setJinTime(creatorvalues.get(6));
            setRetsuTime(creatorvalues.get(7));
            setZaiTime(creatorvalues.get(8));
            setZenTime(creatorvalues.get(9));
            setPostTime(creatorvalues.get(10));
        } catch (ArrayIndexOutOfBoundsException ignored) {}
    }
    public boolean validpreset() {
        for (Integer i : getpresettimes()) {if (i == null) {return false;}}
        return true;
    }
}
