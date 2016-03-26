package kujiin.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@XmlRootElement(name = "Audio")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Audio {
    @XmlElement
    public List<Sounds> Sounds;

    public Audio() {}

    public List<kujiin.xml.Sounds> getSounds() {
        return Sounds;
    }
    public void setSounds(List<kujiin.xml.Sounds> sounds) {
        Sounds = sounds;
    }

    // XML Processing
    public void marshall() {}
    public void unmarshall() {}

// Sound Getters
    public Sounds getcutorelementsounds(String cutorelementname) {
        if (getSounds() == null) {return null;}
        for (Sounds i : getSounds()) {
            if (i.getElementorcutname().equals(cutorelementname)) {return i;}
        }
        return null;
    }
    public Sounds getcutorelementsounds(int cutorelementindex) {
        if (getSounds() == null) {return null;}
        for (Sounds i : getSounds()) {
            if (i.getElementorcutindex() == cutorelementindex) {return i;}
        }
        return null;
    }

    // > Sound Class
        // > Name
        // > List<Entrainment>
        // > List<Ambience>
}
