package kujiin;

import javafx.concurrent.Service;
import javafx.scene.media.Media;
import javafx.util.Duration;
import kujiin.interfaces.Creatable;
import kujiin.interfaces.Exportable;
import kujiin.widgets.Playable;
import kujiin.xml.Options;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

// TODO Extend Functionality Of Cut Here, Or Put Basic Functionality here And Have Cut Extend This
// TODO Put Add A Japanese Character Symbol Picture (Representing Each Cut) To Creator Cut Labels (With Tooltips Displaying Names)
// TODO Add Tooltips To Cuts Saying A One Word Brief Summary (Rin -> Strength, Kyo -> Control, Toh->Harmony)
// TODO Player On Separate Screen
public class Element extends Playable implements Creatable, Exportable {
    public String name;
    public int number;

    public Element(int number, String name, int duration, This_Session thissession) {
        this.number = number;
        this.name = name;
        super.duration = duration;
        super.thisession = thissession;
        ambiencedirectory = new File(Options.DIRECTORYAMBIENCE, name);
    }

// Getters And Setters


// Creation
    public boolean build(ArrayList<Element> elementstoplay, boolean ambienceenabled) {
        setAmbienceenabled(ambienceenabled);
        setElementstoplay(elementstoplay);
        if (ambienceenabled) {return buildAmbience() && buildEntrainment();}
        else {return buildEntrainment();}
    }
    @Override
    public boolean getambienceindirectory() {
        ambiencefiles = new ArrayList<>();
        ambiencefiledurations = new ArrayList<>();
        try {
            for (File i : ambiencedirectory.listFiles()) {
                double dur = Tools.getaudioduration(i);
                if (dur > 0.0) {
                    ambiencefiles.add(i);
                    ambiencefiledurations.add(dur);
                }
            }
        } catch (NullPointerException e) {new MainController.ExceptionDialog(thisession.Root, e).showAndWait(); return false;}
        return ambiencefiles.size() > 0;
    }
    @Override
    public boolean hasenoughAmbience(int secondstocheck) {
        double a = 0;
        for (Double i : ambiencefiledurations) {a += i;}
        setTotalambienceduration(a);
        return a > (double) secondstocheck;
    }
    @Override
    public boolean buildEntrainment() {
        return false;
    }
    @Override
    public boolean buildAmbience() {
        ambiencelist = new ArrayList<>();
        ambiencemedia = new ArrayList<>();
        Double currentduration = 0.0;
        Double sessionduration = (double) getdurationinseconds();
        // Ambience Is >= Session Duration
        if (hasenoughAmbience(getdurationinseconds())) {
            for (File i : ambiencefiles) {
                if (currentduration < sessionduration) {
                    ambiencelist.add(i);
                    currentduration += ambiencefiledurations.get(ambiencefiles.indexOf(i));
                } else {break;}
            }
            // Shuffle/Loop Ambience Randomly
        } else {
            Random randint = new Random();
            while (currentduration < sessionduration) {
                File tempfile = ambiencefiles.get(randint.nextInt(ambiencefiles.size() - 1));
                double tempduration = ambiencefiledurations.get(ambiencefiles.indexOf(tempfile));
                int size = ambiencelist.size();
                if (size == 0) {
                    ambiencelist.add(tempfile);
                    currentduration += tempduration;
                } else if (size == 1) {
                    ambiencelist.add(tempfile);
                    currentduration += tempduration;
                } else if (size == 2) {
                    if (!tempfile.equals(ambiencelist.get(size - 1))) {
                        ambiencelist.add(tempfile);
                        currentduration += tempduration;
                    }
                } else if (size == 3) {
                    if (!tempfile.equals(ambiencelist.get(size - 1)) && !tempfile.equals(ambiencelist.get(size - 2))) {
                        ambiencelist.add(tempfile);
                        currentduration += tempduration;
                    }
                } else if (size <= 5) {
                    if (!tempfile.equals(ambiencelist.get(size - 1)) && !tempfile.equals(ambiencelist.get(size - 2)) && !tempfile.equals(ambiencelist.get(size - 3))) {
                        ambiencelist.add(tempfile);
                        currentduration += tempduration;
                    }
                } else if (size > 5) {
                    if (!tempfile.equals(ambiencelist.get(size - 1)) && !tempfile.equals(ambiencelist.get(size - 2)) && !tempfile.equals(ambiencelist.get(size - 3)) && !tempfile.equals(ambiencelist.get(size - 4))) {
                        ambiencelist.add(tempfile);
                        currentduration += tempduration;
                    }
                }
            }
        }
        ambiencemedia.addAll(ambiencelist.stream().map(i -> new Media(i.toURI().toString())).collect(Collectors.toList()));
        return ambiencemedia.size() > 0;
    }
    @Override
    public void reset() {
        if (entrainmentlist != null) entrainmentlist.clear();
        if (entrainmentmedia != null) entrainmentmedia.clear();
        if (ambiencelist != null) ambiencelist.clear();
        if (ambiencemedia != null) ambiencemedia.clear();
    }

// Playback
    @Override
    protected void start() {
        super.start();
    }
    @Override
    protected void resume() {
        super.resume();
    }
    @Override
    protected void pause() {
        super.pause();
    }
    @Override
    protected void stop() {
        super.stop();
    }
    @Override
    protected void tick() {
        super.tick();
    }
    @Override
    protected void playnextentrainment() {
        try {
            super.playnextentrainment();
        } catch (IndexOutOfBoundsException ignored) {
            System.out.println("Out Of Bounds In " + name + "'s Ambience List: ");
            for (Media i : ambiencemedia) {System.out.println(i.getSource() + i.getDuration().toSeconds());}
        }
    }
    @Override
    protected void playnextambience() {
        try {
            super.playnextambience();
        } catch (IndexOutOfBoundsException ignored) {
            System.out.println("Out Of Bounds In " + name + "'s Ambience List: ");
            for (Media i : entrainmentmedia) {System.out.println(i.getSource() + i.getDuration().toSeconds());}
        }
    }
    @Override
    protected void startfadeout() {
        super.startfadeout();
    }
    @Override
    protected void cleanup() {
        super.cleanup();
    }
    @Override
    protected Duration getdurationasobject() {
        return super.getdurationasobject();
    }
    @Override
    protected int getdurationinseconds() {
        return super.getdurationinseconds();
    }
    @Override
    protected int getdurationinminutes() {
        return super.getdurationinminutes();
    }
    @Override
    protected Double getdurationindecimalhours() {
        return super.getdurationindecimalhours();
    }
    @Override
    protected String getcurrenttimeformatted() {
        return super.getcurrenttimeformatted();
    }
    @Override
    protected String gettotaltimeformatted() {
        return super.gettotaltimeformatted();
    }
    @Override
    protected void entrainmenterror() {
        super.entrainmenterror();
    }
    @Override
    protected void ambienceerror() {
        super.ambienceerror();
    }

// Export
    @Override
    public Service<Boolean> getexportservice() {
        return null;
    }
    @Override
    public Boolean exportedsuccesfully() {
        return null;
    }
    @Override
    public Boolean cleanuptempfiles() {
        return null;
    }
    @Override
    public File getFinalexportfile() {
        return null;
    }
    @Override
    public Boolean mixentrainmentandambience() {
        return null;
    }
    @Override
    public Boolean sessionreadyforFinalExport() {
        return null;
    }
}
