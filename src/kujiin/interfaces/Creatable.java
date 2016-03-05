package kujiin.interfaces;

public interface Creatable {
    boolean isValid();
    boolean getambienceindirectory();
    boolean hasenoughAmbience(int secondstocheck);
    boolean buildEntrainment();
    boolean buildAmbience();
    void reset();
}
