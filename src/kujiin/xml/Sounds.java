package kujiin.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Sounds {
    private String elementorcutname;
    private int elementorcutindex;
    private List<Entrainments> entrainments;
    private List<Ambiences> ambiences;

    public Sounds() {}
    public Sounds(String name, int index) {}

// Getters And Setters
    @XmlElement
    public String getElementorcutname() {
        return elementorcutname;
    }
    public void setElementorcutname(String elementorcutname) {
        this.elementorcutname = elementorcutname;
    }
    @XmlElement
    public int getElementorcutindex() {
        return elementorcutindex;
    }
    public void setElementorcutindex(int elementorcutindex) {
        this.elementorcutindex = elementorcutindex;
    }
    @XmlElement
    public List<Entrainments> getEntrainments() {
        return entrainments;
    }
    public void setEntrainments(List<Entrainments> entrainments) {
        this.entrainments = entrainments;
    }
    @XmlElement
    public List<Ambiences> getAmbiences() {
        return ambiences;
    }
    public void setAmbiences(List<Ambiences> ambiences) {
        this.ambiences = ambiences;
    }

}
