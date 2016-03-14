package kujiin.interfaces;

import java.util.ArrayList;

public interface Creatable {

    boolean isValid();
    boolean getambienceindirectory();
    boolean hasenoughAmbience(int secondstocheck);
    boolean build(ArrayList<Object> allcutandelementitems, boolean ambienceenabled);
    boolean buildEntrainment();
    boolean buildAmbience();
    void reset();
}
