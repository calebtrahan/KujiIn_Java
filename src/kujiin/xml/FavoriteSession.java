package kujiin.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class FavoriteSession {
    String name;
    Session session;
    Double duration;

    public FavoriteSession(String name, Session session) {
        this.name = name;
        this.session = session;
        for (Session.PlaybackItem i : session.getPlaybackItems()) {
            duration += i.getDuration();
        }
    }
}
