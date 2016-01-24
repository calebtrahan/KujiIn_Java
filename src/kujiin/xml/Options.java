package kujiin.xml;

import kujiin.This_Session;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "options")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Options {
    private Integer defaultinvocationtime;
    private Boolean helpdialogs;
    private Boolean tooltips;
    private Boolean prematureendings;

    public Options() {
        try {unmarshall();} catch (JAXBException ignored) {}
    }

    // Getters And Setters
    public Integer getDefaultinvocationtime() {
        return defaultinvocationtime;
    }
    public void setDefaultinvocationtime(Integer defaultinvocationtime) {
        this.defaultinvocationtime = defaultinvocationtime;
    }
    public Boolean getHelpdialogs() {
        return helpdialogs;
    }
    public void setHelpdialogs(Boolean helpdialogs) {
        this.helpdialogs = helpdialogs;
    }
    public Boolean getTooltips() {
        return tooltips;
    }
    public void setTooltips(Boolean tooltips) {
        this.tooltips = tooltips;
    }
    public Boolean getPrematureendings() {
        return prematureendings;
    }
    public void setPrematureendings(Boolean prematureendings) {
        this.prematureendings = prematureendings;
    }

    public void unmarshall() throws JAXBException {
        if (This_Session.optionsxmlfile.exists()) {
            JAXBContext context = JAXBContext.newInstance(Options.class);
            Unmarshaller createMarshaller = context.createUnmarshaller();
            Options options = (Options) createMarshaller.unmarshal(This_Session.optionsxmlfile);
            if (options != null) {
                setDefaultinvocationtime(options.getDefaultinvocationtime());
                setHelpdialogs(options.getHelpdialogs());
                setTooltips(options.getTooltips());
                setPrematureendings(options.getPrematureendings());
            }
        } else {
            setDefaultinvocationtime(3);
            setHelpdialogs(true);
            setTooltips(true);
            setPrematureendings(true);
            marshall();
        }
    }
    public void marshall() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Options.class);
        Marshaller createMarshaller = context.createMarshaller();
        createMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        createMarshaller.marshal(this, This_Session.optionsxmlfile);
    }
}
