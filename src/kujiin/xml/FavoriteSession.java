package kujiin.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.FIELD)
public class FavoriteSession {
    private UUID id;
    private String name;
    private Session session;

    public FavoriteSession() {}
    public FavoriteSession(String name, Session session) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.session = session;
    }

// Getters And Setters
    public String getName() {
        return name;
    }
    public Session getSession() {
        return session;
    }
    public UUID getId() {
        return id;
    }

}