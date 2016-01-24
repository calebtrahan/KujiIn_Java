package kujiin.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "options")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Options {
    private Integer defaultinvocationtime;
    private Boolean helpdialogsenabled;
    private Boolean tooltipsdisabled;

    // Getters And Setters
    public Integer getDefaultinvocationtime() {
        return defaultinvocationtime;
    }
    public void setDefaultinvocationtime(Integer defaultinvocationtime) {
        this.defaultinvocationtime = defaultinvocationtime;
    }
    public Boolean getHelpdialogsenabled() {
        return helpdialogsenabled;
    }
    public void setHelpdialogsenabled(Boolean helpdialogsenabled) {
        this.helpdialogsenabled = helpdialogsenabled;
    }
    public Boolean getTooltipsdisabled() {
        return tooltipsdisabled;
    }
    public void setTooltipsdisabled(Boolean tooltipsdisabled) {
        this.tooltipsdisabled = tooltipsdisabled;
    }

}
