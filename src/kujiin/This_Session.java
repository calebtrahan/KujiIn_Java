package kujiin;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.CheckBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.dialogs.SimpleTextDialogWithCancelButton;
import kujiin.widgets.CreatorAndExporterWidget;
import kujiin.widgets.Meditatable;
import kujiin.widgets.PlayerWidget;
import kujiin.widgets.ProgressAndGoalsWidget;
import kujiin.xml.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO Theme The Reference Files With A Dark Theme
// TODO Double Check That Reference Files Switch On And Off And Work With The Options Being In XML
// TODO Work On Completed Goals
// TODO If Ramp Disabled (And No Pre/PostSession Set) Ask User If They Want TO Add A Ramp Into 1st Practiced Cut (2/3/5) Min, Then Update UI And Create Session
// TODO Preferences Dialog Doesn't Initially Populate With Options From XML (Check If It Saves As Well?)

public class This_Session {
    private Qi_Gong Presession;
    private Cut Rin;
    private Cut Kyo;
    private Cut Toh;
    private Cut Sha;
    private Cut Jin;
    private Cut Kai;
    private Cut Retsu;
    private Cut Zai;
    private Cut Zen;
    private Qi_Gong Postsession;
    private Element Earth;
    private Element Air;
    private Element Fire;
    private Element Water;
    private Element Void;
    private PlayerWidget.PlayerState playerState;
    private Meditatable currentcutorelement;
    private Timeline updateuitimeline;
    private Sessions sessions;
    private int totalsecondselapsed;
    private int totalsecondsinsession;
    private int cutorelementcount;
    private PlayerWidget.DisplayReference displayReference;
    public MainController Root;
    public List<Goals.Goal> GoalsCompletedThisSession;
    private PlayerWidget playerWidget;
    private List<Meditatable> itemsinsession;
    private Entrainments entrainments;
    private Ambiences ambiences;

    public This_Session(MainController mainController) {
        Root = mainController;
        this.sessions = Root.getProgressTracker().getSessions();
        setPlayerState(PlayerWidget.PlayerState.IDLE);
        entrainments = new Entrainments(Root);
        entrainments.unmarshall();
        ambiences = new Ambiences(Root);
        ambiences.unmarshall();
        Presession =  new Qi_Gong(0, "Presession", 0, "Gather Qi Before The Session Starts", this, Root.PreSwitch, Root.PreTime);
        Rin = new Cut(1, "RIN", 0, "Meet (Spirit & Invite Into The Body)", this, Root.RinSwitch, Root.RinTime);
        Kyo = new Cut(2, "KYO", 0, "Troops (Manage Internal Strategy/Tools)", this, Root.KyoSwitch, Root.KyoTime);
        Toh = new Cut(3, "TOH", 0, "Fighting (Against Myself To Attain Harmony)", this, Root.TohSwitch, Root.TohTime);
        Sha = new Cut(4, "SHA", 0, "Person (Meet & Become Person We Met In RIN)", this, Root.ShaSwitch, Root.ShaTime);
        Kai = new Cut(5, "KAI", 0, "All/Everything (Feeling Love & Compassion For Absolutely Everything)", this, Root.KaiSwitch, Root.KaiTime);
        Jin = new Cut(6, "JIN", 0, "Understanding", this, Root.JinSwitch, Root.JinTime);
        Retsu = new Cut(7, "RETSU", 0, "Dimension", this, Root.RetsuSwitch, Root.RetsuTime);
        Zai = new Cut(8, "ZAI", 0, "Creation", this, Root.ZaiSwitch, Root.ZaiTime);
        Zen = new Cut(9, "ZEN", 0, "Perfection", this, Root.ZenSwitch, Root.ZenTime);
        Earth = new Element(10, "Earth", 0, "", this, Root.EarthSwitch, Root.EarthTime);
        Air = new Element(11, "Air", 0, "", this, Root.AirSwitch, Root.AirTime);
        Fire = new Element(12, "Fire", 0, "", this, Root.FireSwitch, Root.FireTime);
        Water = new Element(13, "Water", 0, "", this, Root.WaterSwitch, Root.WaterTime);
        Void = new Element(14, "Void", 0, "", this, Root.VoidSwitch, Root.VoidTime);
        Postsession = new Qi_Gong(15, "Postsession", 0, "Gather Qi After The Session Ends", this, Root.PostSwitch, Root.PostTime);
    }

// Getters And Setters
    public void setPlayerState(PlayerWidget.PlayerState playerState) {this.playerState = playerState;}
    public PlayerWidget.PlayerState getPlayerState() {return playerState;}
    public boolean isValid() {
        int totaltime = 0;
        for (Object i : getallCutsAndElements()) {totaltime += ((Meditatable) i).getdurationinminutes();}
        return totaltime > 0;
    }
    public PlayerWidget getPlayerWidget() {
        return playerWidget;
    }
    public void setPlayerWidget(PlayerWidget playerWidget) {
        this.playerWidget = playerWidget;
    }
    public ArrayList<Meditatable> getallCutsAndElements() {return new ArrayList<>(Arrays.asList(Presession, Rin, Kyo, Toh, Sha, Kai, Jin, Retsu, Zai, Zen, Earth, Air, Fire, Water, Void, Postsession));}
    public ArrayList<Cut> getallCuts()  {return new ArrayList<>(Arrays.asList(Rin, Kyo, Toh, Sha, Kai, Jin, Retsu, Zai, Zen));}
    public ArrayList<Element> getallElements() {return new ArrayList<>(Arrays.asList(Earth, Air, Fire, Water, Void));}
    public List<Meditatable> getallitemsinSession() {
        return itemsinsession;
    }
    public void setItemsinsession(List<Meditatable> itemsinsession) {
        this.itemsinsession = itemsinsession;
    }
    public ArrayList<Cut> getCutsinSession() {
        ArrayList<Cut> cutinsession = new ArrayList<>();
        for (Object i : itemsinsession) {
            if (i instanceof Cut) {cutinsession.add((Cut) i);}
        }
        return cutinsession;
    }
    public ArrayList<Element> getElementsinSession() {
        ArrayList<Element> elementsinsession = new ArrayList<>();
        for (Object i : itemsinsession) {
            if (i instanceof Element) {elementsinsession.add((Element) i);}
        }
        return elementsinsession;
    }
    public Entrainments getEntrainments() {
        return entrainments;
    }
    public void setEntrainments(Entrainments entrainments) {
        this.entrainments = entrainments;
    }
    public Ambiences getAmbiences() {
        return ambiences;
    }
    public void setAmbiences(Ambiences ambiences) {
        this.ambiences = ambiences;
    }

    // Cut And Element Getters
    public Qi_Gong getPresession() {
        return Presession;
    }
    public Cut getRin() {
        return Rin;
    }
    public Cut getKyo() {
        return Kyo;
    }
    public Cut getToh() {
        return Toh;
    }
    public Cut getSha() {
        return Sha;
    }
    public Cut getJin() {
        return Jin;
    }
    public Cut getKai() {
        return Kai;
    }
    public Cut getRetsu() {
        return Retsu;
    }
    public Cut getZai() {
        return Zai;
    }
    public Cut getZen() {
        return Zen;
    }
    public Qi_Gong getPostsession() {
        return Postsession;
    }
    public Element getEarth() {
        return Earth;
    }
    public Element getAir() {
        return Air;
    }
    public Element getFire() {
        return Fire;
    }
    public Element getWater() {
        return Water;
    }
    public Element getVoid() {
        return Void;
    }

    // GUI
    public void setDuration(int elementorcutindex, int duration) {
        if (elementorcutindex == 0) {Presession.setDuration(duration);}
        if (elementorcutindex == 1) {Rin.setDuration(duration);}
        if (elementorcutindex == 2) {Kyo.setDuration(duration);}
        if (elementorcutindex == 3) {Toh.setDuration(duration);}
        if (elementorcutindex == 4) {Sha.setDuration(duration);}
        if (elementorcutindex == 5) {Kai.setDuration(duration);}
        if (elementorcutindex == 6) {Jin.setDuration(duration);}
        if (elementorcutindex == 7) {Retsu.setDuration(duration);}
        if (elementorcutindex == 8) {Zai.setDuration(duration);}
        if (elementorcutindex == 9) {Zen.setDuration(duration);}
        if (elementorcutindex == 10) {Postsession.setDuration(duration);}
        if (elementorcutindex == 11) {Earth.setDuration(duration);}
        if (elementorcutindex == 12) {Air.setDuration(duration);}
        if (elementorcutindex == 13) {Fire.setDuration(duration);}
        if (elementorcutindex == 14) {Water.setDuration(duration);}
        if (elementorcutindex == 15) {Void.setDuration(duration);}
    }
    public int getDuration(int elementorcutindex) {
        if (elementorcutindex == 0) {return Presession.getdurationinminutes();}
        if (elementorcutindex == 1) {return Rin.getdurationinminutes();}
        if (elementorcutindex == 2) {return Kyo.getdurationinminutes();}
        if (elementorcutindex == 3) {return Toh.getdurationinminutes();}
        if (elementorcutindex == 4) {return Sha.getdurationinminutes();}
        if (elementorcutindex == 5) {return Kai.getdurationinminutes();}
        if (elementorcutindex == 6) {return Jin.getdurationinminutes();}
        if (elementorcutindex == 7) {return Retsu.getdurationinminutes();}
        if (elementorcutindex == 8) {return Zai.getdurationinminutes();}
        if (elementorcutindex == 9) {return Zen.getdurationinminutes();}
        if (elementorcutindex == 10) {return Postsession.getdurationinminutes();}
        if (elementorcutindex == 11) {return Earth.getdurationinminutes();}
        if (elementorcutindex == 12) {return Air.getdurationinminutes();}
        if (elementorcutindex == 13) {return Fire.getdurationinminutes();}
        if (elementorcutindex == 14) {return Water.getdurationinminutes();}
        if (elementorcutindex == 15) {return Void.getdurationinminutes();}
        return 0;
    }
    public ArrayList<Integer> getallsessionvalues() {
        ArrayList<Integer> values = new ArrayList<>();
        for (int i=0; i<=15; i++) {values.add(getDuration(i));}
        return values;
    }
    public ArrayList<Integer> getcutsessionvalues(boolean includepreandpost) {
        if (includepreandpost) {return new ArrayList<>(getallsessionvalues().subList(0, 11));}
        else {return new ArrayList<>(getallsessionvalues().subList(1, 10));}
    }
    public ArrayList<Integer> getelementvalues() {
        return new ArrayList<>(getallsessionvalues().subList(11, 15));
    }

// Creation Methods
    public boolean sessionvaluesok() {
        for (Object i : getallCutsAndElements()) {
            Meditatable cutorelement = (Meditatable) i;
            if (cutorelement.getdurationinminutes() > 0) {return true;}
        }
        return false;
    }
    public void checkambience(CheckBox ambiencecheckbox) {
        if (sessionvaluesok()) {
            ArrayList<Meditatable> cutsorelementswithnoambience = new ArrayList<>();
            ArrayList<Meditatable> cutsorelementswithreducedambience = new ArrayList<>();
            Service<Void> ambiencecheckerservice = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                for (Meditatable i : getallCutsAndElements()) {
                                    updateMessage(String.format("Currently Checking %s...", i.name));
                                    if (! i.getAmbience().hasAnyAmbience()) {cutsorelementswithnoambience.add(i);}
                                    else if (! i.getAmbience().hasEnoughAmbience(i.getdurationinseconds())) {cutsorelementswithreducedambience.add(i);}
                                }
                                updateMessage("Done Checking Ambience");
                                return null;
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        }
                    };
                }
            };
            final SimpleTextDialogWithCancelButton[] cad = new SimpleTextDialogWithCancelButton[1];
            ambiencecheckerservice.setOnRunning(event -> {
                cad[0] = new SimpleTextDialogWithCancelButton(Root, "Checking Ambience", "Checking Ambience", "");
                cad[0].Message.textProperty().bind(ambiencecheckerservice.messageProperty());
                cad[0].CancelButton.setOnAction(ev -> ambiencecheckerservice.cancel());
                cad[0].showAndWait();
            });
            ambiencecheckerservice.setOnSucceeded(event -> {
                cad[0].close();
                if (cutsorelementswithnoambience.size() > 0) {
                    StringBuilder a = new StringBuilder();
                    for (int i = 0; i < cutsorelementswithnoambience.size(); i++) {
                        a.append(cutsorelementswithnoambience.get(i).name);
                        if (i != cutsorelementswithnoambience.size() - 1) {a.append(", ");}
                    }
                    if (cutsorelementswithnoambience.size() > 1) {
                        Tools.gui_showerrordialog(Root, "Error", String.format("%s Have No Ambience At All", a.toString()), "Cannot Add Ambience");
                        if (Tools.gui_getconfirmationdialog(Root, "Add Ambience", a.toString() + " Needs Ambience", "Open The Ambience Editor?")) {
                            MainController.AdvancedAmbienceEditor ambienceEditor = new MainController.AdvancedAmbienceEditor(Root, Root.getSession().getAmbiences());
                            ambienceEditor.showAndWait();
                        }
                    } else {
                        Tools.gui_showerrordialog(Root, "Error", String.format("%s Have No Ambience At All", a.toString()), "Cannot Add Ambience");
                        if (Tools.gui_getconfirmationdialog(Root, "Add Ambience", a.toString() + " Need Ambience", "Open The Ambience Editor?")) {
                            MainController.AdvancedAmbienceEditor ambienceEditor = new MainController.AdvancedAmbienceEditor(Root, Root.getSession().getAmbiences(), cutsorelementswithnoambience.get(0).name);
                            ambienceEditor.showAndWait();
                        }
                    }
                    ambiencecheckbox.setSelected(false);
                } else {
                    if (cutsorelementswithreducedambience.size() > 0) {
                        StringBuilder a = new StringBuilder();
                        int count = 1;
                        for (int i = 0; i < cutsorelementswithreducedambience.size(); i++) {
                            a.append("\n");
                            Meditatable thiscut = cutsorelementswithreducedambience.get(i);
                            String formattedcurrentduration = Tools.format_minstohrsandmins_short((int) ((thiscut.getAmbience().gettotalActualDuration() / 1000) / 60));
                            String formattedexpectedduration = Tools.format_minstohrsandmins_short(cutsorelementswithreducedambience.get(i).getdurationinminutes());
                            a.append(count).append(". ").append(thiscut.name).append(" >  Current: ").append(formattedcurrentduration).append(" | Needed: ").append(formattedexpectedduration);
                            count++;
                        }
                        if (cutsorelementswithreducedambience.size() == 1) {
                            ambiencecheckbox.setSelected(Tools.gui_getconfirmationdialog(Root, "Confirmation", String.format("The Following Cut's Ambience Isn't Long Enough: %s ", a.toString()), "Shuffle And Loop Ambience For This Cut?"));
                        } else {
                            ambiencecheckbox.setSelected(Tools.gui_getconfirmationdialog(Root, "Confirmation", String.format("The Following Cuts' Ambience Aren't Long Enough: %s ", a.toString()), "Shuffle And Loop Ambience For These Cuts?"));
                        }
                    } else {
                        ambiencecheckbox.setSelected(true);
                    }
                }
            });
            ambiencecheckerservice.setOnCancelled(event -> {
                cad[0].close();
                ambiencecheckbox.setSelected(false);
            });
            ambiencecheckerservice.setOnFailed(event -> {
                System.out.println("Failed!!");
                cad[0].close();
                Tools.gui_showerrordialog(Root, "Error", "Couldn't Check Ambience", "Check Ambience Folder Read Permissions");
                ambiencecheckbox.setSelected(false);
            });
            ambiencecheckerservice.start();
        } else {Tools.gui_showinformationdialog(Root, "Information", "Cannot Check Ambience", "No Cuts Have > 0 Values, So I Don't Know Which Ambience To Check");}
    }
    public boolean checksessionwellformedness(ArrayList<Integer> textfieldtimes) {
        int lastcutindex = 0;
        for (int i = 0; i < textfieldtimes.size(); i++) {
            if (textfieldtimes.get(i) > 0) {lastcutindex = i;}
        }
        // Get NonSequential Cuts
        ArrayList<Integer> indexestochange = new ArrayList<>();
        for (int i = 0; i < lastcutindex; i++) {
            if (i > 0) {if (textfieldtimes.get(i) == 0) {indexestochange.add(i);}}
        }
        if (indexestochange.size() > 0) {
            ArrayList<String> cutsmissinglist = new ArrayList<>();
            for (Integer x : indexestochange) {cutsmissinglist.add(Options.CUTNAMES.get(x));}
            StringBuilder cutsmissingtext = new StringBuilder();
            for (int i = 0; i < cutsmissinglist.size(); i++) {
                cutsmissingtext.append(cutsmissinglist.get(i));
                if (i != cutsmissinglist.size() - 1) {cutsmissingtext.append(", ");}
            }
            CreatorAndExporterWidget.SessionNotWellformedDialog notWellformedDialog = new CreatorAndExporterWidget.SessionNotWellformedDialog(Root, textfieldtimes, cutsmissingtext.toString(), lastcutindex);
            notWellformedDialog.showAndWait();
            if (notWellformedDialog.isCreatesession()) {
                int invocationduration = notWellformedDialog.getInvocationduration();
                for (int i : indexestochange) {textfieldtimes.set(i, invocationduration);}
                return true;
            } else {return false;}
        }
        return true;
    }
    public boolean setupcutsinsession() {
        itemsinsession = new ArrayList<>();
        if (Presession.getdurationinminutes() != 0) {itemsinsession.add(Presession);}
        if (Rin.getdurationinminutes() != 0) {itemsinsession.add(Rin);}
        if (Kyo.getdurationinminutes() != 0) {itemsinsession.add(Kyo);}
        if (Toh.getdurationinminutes() != 0) {itemsinsession.add(Toh);}
        if (Sha.getdurationinminutes() != 0) {itemsinsession.add(Sha);}
        if (Kai.getdurationinminutes() != 0) {itemsinsession.add(Kai);}
        if (Jin.getdurationinminutes() != 0) {itemsinsession.add(Jin);}
        if (Retsu.getdurationinminutes() != 0) {itemsinsession.add(Retsu);}
        if (Zai.getdurationinminutes() != 0) {itemsinsession.add(Zai);}
        if (Zen.getdurationinminutes() != 0) {itemsinsession.add(Zen);}
        if (Earth.getdurationinminutes() != 0) {itemsinsession.add(Earth);}
        if (Air.getdurationinminutes() != 0) {itemsinsession.add(Air);}
        if (Fire.getdurationinminutes() != 0) {itemsinsession.add(Fire);}
        if (Water.getdurationinminutes() != 0) {itemsinsession.add(Water);}
        if (Void.getdurationinminutes() != 0) {itemsinsession.add(Void);}
        if (Postsession.getdurationinminutes() != 0) {itemsinsession.add(Postsession);}
        setItemsinsession(itemsinsession);
        return getallitemsinSession().size() > 0;
    }
    public boolean create(ArrayList<Integer> textfieldtimes) {
        if (checksessionwellformedness(textfieldtimes)) {
            setupcutsinsession();
            boolean haselements = false;
            boolean hascuts = false;
            for (Object i : getallitemsinSession()) {if (i instanceof Element) haselements = true;}
            for (Object i : getallitemsinSession()) {if (i instanceof Cut) hascuts = true;}
            if (haselements && hascuts || haselements && ! hascuts) {
                CreatorAndExporterWidget.SortSessionItems sortSessionItems = new CreatorAndExporterWidget.SortSessionItems(Root, getallitemsinSession());
                sortSessionItems.showAndWait();
                if (sortSessionItems.getorderedsessionitems() == null) {return false;}
                else {setItemsinsession(sortSessionItems.getorderedsessionitems());}
            }
            for (Object i : getallitemsinSession()) {
                if (i instanceof Cut) {if (! ((Cut) i).build(getallitemsinSession(), Root.AmbienceSwitch.isSelected())) {return false;}}
                if (i instanceof Element) {if (! ((Element) i).build(getallitemsinSession(), Root.AmbienceSwitch.isSelected())) {return false;}}
                if (i instanceof Qi_Gong) {if (! ((Qi_Gong) i).build(getallitemsinSession(), Root.AmbienceSwitch.isSelected())) {return false;}}
            }
            return true;
        } else {return false;}
    }
    public void resetcreateditems() {
        for (Object i : getallCutsAndElements()) {
            if (i instanceof Element) {((Element) i).resetCreation();}
            if (i instanceof Cut) {((Cut) i).resetCreation();}
            if (i instanceof  Qi_Gong) {((Qi_Gong) i).resetCreation();}
        }
    }

// Export
    public Service<Boolean> getsessionexporter() {
//        CreatorAndExporterWidget.ExportingSessionDialog exportingSessionDialog = new CreatorAndExporterWidget.ExportingSessionDialog(this);
        return new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        updateTitle("Finalizing Session");
//                        int taskcount = cutsinsession.size() + 2;
//                        // TODO Mix Entrainment And Ambience
//                        for (Cut i : cutsinsession) {
//                            updateMessage("Combining Entrainment And Ambience For " + i.name);
//                            if (! i.mixentrainmentandambience()) {cancel();}
//                            if (isCancelled()) {return false;}
//                            updateProgress((double) (cutsinsession.indexOf(i) / taskcount), 1.0);
//                            updateMessage("Finished Combining " + i.name);
//                        }
                        updateMessage("Creating Final Session File (May Take A While)");
                        export();
                        if (isCancelled()) {return false;}
//                        updateProgress(taskcount - 1, 1.0);
                        updateMessage("Double-Checking Final Session File");
                        boolean success = testexportfile();
                        if (isCancelled()) {return false;}
                        updateProgress(1.0, 1.0);
                        return success;
                    }
                };
            }
        };
//        exportingSessionDialog.creatingsessionProgressBar.progressProperty().bind(exporterservice.progressProperty());
//        exportingSessionDialog.creatingsessionTextStatusBar.textProperty().bind(exporterservice.messageProperty());
//        exportingSessionDialog.CancelButton.setOnAction(event -> exporterservice.cancel());
//        exporterservice.setOnSucceeded(event -> {
//            if (exporterservice.getValue()) {Tools.gui_showinformationdialog("Information", "Export Succeeded", "File Saved To: ");}
//            else {Tools.gui_showerrordialog("Error", "Errors Occured During Export", "Please Try Again Or Contact Me For Support");}
//            exportingSessionDialog.close();
//        });
//        exporterservice.setOnFailed(event -> {
//            String v = exporterservice.getException().getMessage();
//            Tools.gui_showerrordialog("Error", "Errors Occured While Trying To Create The This_Session. The Main Exception I Encoured Was " + v,
//                    "Please Try Again Or Contact Me For Support");
//            This_Session.deleteprevioussession();
//            exportingSessionDialog.close();
//        });
//        exporterservice.setOnCancelled(event -> {
//            Tools.gui_showinformationdialog("Cancelled", "Export Cancelled", "You Cancelled Export");
//            This_Session.deleteprevioussession();
//            exportingSessionDialog.close();
//        });
//        return false;
    }
    public void getnewexportsavefile() {
//        File tempfile = Tools.filechooser_save(Root.getScene(), "Save Export File As", null);
//        if (tempfile != null && Tools.audio_isValid(tempfile)) {
//            setExportfile(tempfile);
//        } else {
//            if (tempfile == null) {return;}
//            if (Tools.gui_getconfirmationdialog(Root, "Confirmation", "Invalid Audio File Extension", "Save As .mp3?")) {
//                String file = tempfile.getAbsolutePath();
//                int index = file.lastIndexOf(".");
//                String firstpart = file.substring(0, index - 1);
//                setExportfile(new File(firstpart.concat(".mp3")));
//            }
//        }
    }
    public boolean export() {
        ArrayList<File> filestoexport = new ArrayList<>();
//        for (int i=0; i < cutsinsession.size(); i++) {
//            filestoexport.add(cutsinsession.get(i).getFinalexportfile());
//            if (i != cutsinsession.size() - 1) {
//                filestoexport.add(new File(Root.getOptions().getSessionOptions().getAlertfilelocation()));
//            }
//        }
        return filestoexport.size() != 0;
    }
    public boolean testexportfile() {
//        try {
//            MediaPlayer test = new MediaPlayer(new Media(getExportfile().toURI().toString()));
//            test.setOnReady(test::dispose);
//            return true;
//        } catch (MediaException ignored) {return false;}
        return false;
    }
    public static void deleteprevioussession() {
        ArrayList<File> folders = new ArrayList<>();
        folders.add(new File(Options.DIRECTORYTEMP, "Ambience"));
        folders.add(new File(Options.DIRECTORYTEMP, "Entrainment"));
        folders.add(new File(Options.DIRECTORYTEMP, "txt"));
        folders.add(new File(Options.DIRECTORYTEMP, "Export"));
        for (File i : folders) {
            try {
                for (File x : i.listFiles()) {x.delete();}
            } catch (NullPointerException ignored) {}
        }
        try {
            for (File x : Options.DIRECTORYTEMP.listFiles()) {
                if (! x.isDirectory()) {x.delete();}
            }
        } catch (NullPointerException ignored) {}
    }

// Playback
    public void startplayback() {
        totalsecondselapsed = 0;
        totalsecondsinsession = 0;
        for (Object i : itemsinsession) {totalsecondsinsession += ((Meditatable) i).getdurationinseconds();}
        getPlayerWidget().TotalTotalLabel.setText(Tools.format_secondsforplayerdisplay(totalsecondsinsession));
        updateuitimeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> updateplayerui()));
        updateuitimeline.setCycleCount(Animation.INDEFINITE);
        updateuitimeline.play();
        cutorelementcount = 0;
        currentcutorelement = itemsinsession.get(cutorelementcount);
        playthiscut();
        sessions.createnewsession();
    }
    public String play(PlayerWidget playerWidget) {
        if (playerState == PlayerWidget.PlayerState.IDLE) {
            setPlayerWidget(playerWidget);
            startplayback();
            return "Playing Session...";
        }
        else if(playerState == PlayerWidget.PlayerState.PAUSED) {
            updateuitimeline.play();
            currentcutorelement.resume();
            setPlayerState(PlayerWidget.PlayerState.PLAYING);
            return "Resuming Session...";
        }
        else if(playerState == PlayerWidget.PlayerState.STOPPED) {
            setPlayerWidget(playerWidget);
            startplayback();
            return "Playing Session...";
        }
        else if(playerState == PlayerWidget.PlayerState.PLAYING) {
            return "Already Playing";
        }
        else if(playerState == PlayerWidget.PlayerState.TRANSITIONING) {
            return "Transistioning To The Next Cut";
        } else {
            return "";
        }
    }
    public String pause() {
        if (playerState == PlayerWidget.PlayerState.PLAYING) {
            currentcutorelement.pause();
            updateuitimeline.pause();
            setPlayerState(PlayerWidget.PlayerState.PAUSED);
            return "Session Paused";
        } else if (playerState == PlayerWidget.PlayerState.PAUSED) {
            return "Already Paused";
        } else if (playerState == PlayerWidget.PlayerState.TRANSITIONING) {
            return "Currently Transitioning To The Next Cut. Please Wait Till At The Next Cut To Pause";
        } else {
            return "No Session Playing";
        }
    }
    public String stop() {
        if (playerState == PlayerWidget.PlayerState.PLAYING) {
            currentcutorelement.stop();
            updateuitimeline.stop();
            setPlayerState(PlayerWidget.PlayerState.STOPPED);
            resetthissession();
            return "Session Stopped";
        } else if (playerState == PlayerWidget.PlayerState.PAUSED) {
            currentcutorelement.stop();
            updateuitimeline.stop();
            setPlayerState(PlayerWidget.PlayerState.STOPPED);
            resetthissession();
            return "Session Stopped";
        } else if (playerState == PlayerWidget.PlayerState.IDLE) {
            return "No Session Playing, Cannot Stop";
        } else if (playerState == PlayerWidget.PlayerState.TRANSITIONING) {
            return "Currently Transitioning To The Next Cut. Please Wait Till At The Next Cut To Stop";
        } else {
            return "";
        }
    }
    public void updateplayerui() {
        try {
            if (playerState == PlayerWidget.PlayerState.PLAYING) {
                totalsecondselapsed++;
                Float currentprogress;
                Float totalprogress;
                if (currentcutorelement.getSecondselapsed() != 0) {currentprogress = (float) currentcutorelement.getSecondselapsed() / (float) currentcutorelement.getdurationinseconds();}
                else {currentprogress = (float) 0.0;}
                if (totalsecondselapsed != 0) {totalprogress = (float) totalsecondselapsed / (float) totalsecondsinsession;}
                else {totalprogress = (float) 0.0;}
                getPlayerWidget().CurrentCutProgress.setProgress(currentprogress);
                getPlayerWidget().TotalProgress.setProgress(totalprogress);
                currentprogress *= 100;
                totalprogress *= 100;
                getPlayerWidget().CurrentCutTopLabel.setText(String.format("%s (%d", currentcutorelement.name, currentprogress.intValue()) + "%)");
                getPlayerWidget().TotalSessionLabel.setText(String.format("Session (%d", totalprogress.intValue()) + "%)");
                getPlayerWidget().CutCurrentLabel.setText(currentcutorelement.getcurrenttimeformatted());
                getPlayerWidget().CutTotalLabel.setText(currentcutorelement.gettotaltimeformatted());
                getPlayerWidget().TotalCurrentLabel.setText(Tools.format_secondsforplayerdisplay(totalsecondselapsed));
                getPlayerWidget().StatusBar.setText("Session Playing. Currently Practicing " + currentcutorelement.name + "...");
                Root.getProgressTracker().updaterootgoalsui();
                Root.getProgressTracker().updateprogressui();
            } else if (playerState == PlayerWidget.PlayerState.TRANSITIONING) {
                getPlayerWidget().CurrentCutProgress.setProgress(1.0);
                getPlayerWidget().CurrentCutTopLabel.setText(currentcutorelement.name + " Completed");
                if (! currentcutorelement.name.equals("Postsession")) {getPlayerWidget().StatusBar.setText("Prepare For " + getallitemsinSession().get(currentcutorelement.number + 1).name);}
                getPlayerWidget().CutCurrentLabel.setText(currentcutorelement.gettotaltimeformatted());
                getPlayerWidget().CutTotalLabel.setText(currentcutorelement.gettotaltimeformatted());
            } else if (playerState == PlayerWidget.PlayerState.PAUSED) {
                getPlayerWidget().StatusBar.setText("Session Paused");
            } else if (playerState == PlayerWidget.PlayerState.STOPPED) {
                getPlayerWidget().StatusBar.setText("Session Stopped");
            }
        } catch (Exception e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
    }
    public void playthiscut() {
        try {
            if (Root.getOptions().getSessionOptions().getReferenceoption() != null && Root.getOptions().getSessionOptions().getReferenceoption()) {displayreferencefile();}
            Duration cutduration = new Duration(currentcutorelement.getdurationinmillis());
            currentcutorelement.start();
            Timeline timeline = new Timeline(new KeyFrame(cutduration, ae -> progresstonextcut()));
            timeline.setOnFinished(event -> timeline.stop());
            timeline.play();
            setPlayerState(PlayerWidget.PlayerState.PLAYING);
        } catch (Exception e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
    }
    public void progresstonextcut() {
        try {
            if (playerState == PlayerWidget.PlayerState.TRANSITIONING) {
//                System.out.println(TimeUtils.getformattedtime() + "> Clause 1");
                try {
                    List<Goals.Goal> completedgoals = Root.getProgressTracker().getGoal().completecutgoals(currentcutorelement.number,
                            Tools.convert_minstodecimalhours(Root.getProgressTracker().getSessions().getpracticedtimeinminutesforallsessions(currentcutorelement.number, false), 2));
                    if (completedgoals.size() > 0) {GoalsCompletedThisSession.addAll(completedgoals);}
                    currentcutorelement.cleanup();
                    cutorelementcount++;
                    currentcutorelement = getallitemsinSession().get(cutorelementcount);
                    playthiscut();
                } catch (IndexOutOfBoundsException ignored) {
                    currentcutorelement.cleanup(); endofsession();}
            } else if (playerState == PlayerWidget.PlayerState.PLAYING) {transition();}
        } catch (Exception e) {new MainController.ExceptionDialog(Root, e).show();}
    }
    public void endofsession() {
        getPlayerWidget().CurrentCutTopLabel.setText(currentcutorelement.name + " Completed");
        getPlayerWidget().TotalSessionLabel.setText("Session Completed");
        closereferencefile();
        updateuitimeline.stop();
        setPlayerState(PlayerWidget.PlayerState.STOPPED);
        sessions.deletenonvalidsessions();
        // TODO Some Animation Is Still Running At End Of Session. Find It And Stop It Then Change Session Finsished Dialog To Showandwait
        PlayerWidget.SessionFinishedDialog sess = new PlayerWidget.SessionFinishedDialog(Root);
        sess.show();
        sess.setOnHidden(event -> {
            System.out.println("Session Finished Dialog Is Closed/Hidden");
            if (GoalsCompletedThisSession != null && GoalsCompletedThisSession.size() == 1) {
                Goals.Goal i = GoalsCompletedThisSession.get(0);
                int cutindex = new ArrayList<>(Arrays.asList(ProgressAndGoalsWidget.GOALCUTNAMES)).indexOf(i.getCutName());
                double currentpracticedhours = Tools.convert_minstodecimalhours(Root.getProgressTracker().getSessions().getpracticedtimeinminutesforallsessions(cutindex, false), 2);
                new ProgressAndGoalsWidget.SingleGoalCompletedDialog(Root, i, currentpracticedhours);
            } else if (GoalsCompletedThisSession != null && GoalsCompletedThisSession.size() > 1) {
                new ProgressAndGoalsWidget.MultipleGoalsCompletedDialog(Root, GoalsCompletedThisSession).showAndWait();
            }
        });
        // TODO Prompt For Export
//        if (Tools.gui_getconfirmationdialog(Root, "Confirmation", "Session Completed", "Export This Session For Later Use?")) {
//            getsessionexporter();}
        Root.getProgressTracker().updaterootgoalsui();
    }
    public void resetthissession() {
        updateuitimeline = null;
        sessions.deletenonvalidsessions();
        cutorelementcount = 0;
        totalsecondselapsed = 0;
        totalsecondsinsession = 0;
    }
    public void transition() {
        closereferencefile();
        Session currentsession = sessions.getsession(sessions.totalsessioncount() - 1);
        currentsession.updatecutduration(currentcutorelement.number, currentcutorelement.getdurationinminutes());
        sessions.marshall();
        Root.getProgressTracker().updaterootgoalsui();
        currentcutorelement.stop();
        if (currentcutorelement.name.equals("Postsession")) {setPlayerState(PlayerWidget.PlayerState.TRANSITIONING); progresstonextcut();}
        else {
            if (Root.getOptions().getSessionOptions().getAlertfunction()) {
                Media alertmedia = new Media(Root.getOptions().getSessionOptions().getAlertfilelocation());
                MediaPlayer alertplayer = new MediaPlayer(alertmedia);
                alertplayer.play();
                setPlayerState(PlayerWidget.PlayerState.TRANSITIONING);
                alertplayer.setOnEndOfMedia(() -> {
                    alertplayer.stop();
                    alertplayer.dispose();
                    progresstonextcut();
                });
                alertplayer.setOnError(() -> {
                    if (Tools.gui_getconfirmationdialog(Root, "Confirmation", "An Error Occured While Playing Alert File" +
                            alertplayer.getMedia().getSource() + "'", "Retry Playing Alert File? (Pressing Cancel " +
                            "Will Progress To The Next Cut)")) {
                        alertplayer.stop();
                        alertplayer.play();
                    } else {
                        alertplayer.stop();
                        alertplayer.dispose();
                        progresstonextcut();
                    }
                });
            } else {
                setPlayerState(PlayerWidget.PlayerState.TRANSITIONING);
                progresstonextcut();
            }
        }
    }
    public void error_endplayback() {

    }

// Reference Files
    public void choosereferencetype() {
        if (! Root.getOptions().getSessionOptions().getReferenceoption() && Root.getOptions().getSessionOptions().getReferencetype() == null) {
            PlayerWidget.ReferenceTypeDialog reftype = new PlayerWidget.ReferenceTypeDialog(Root);
            reftype.showAndWait();
            Root.getOptions().getSessionOptions().setReferencetype(reftype.getReferenceType());
            Root.getOptions().getSessionOptions().setReferencefullscreen(reftype.getFullscreen());
            Root.getOptions().getSessionOptions().setReferenceoption(reftype.getEnabled());
        }
    }
    public void togglereferencedisplay(CheckBox ReferenceFileCheckbox) {
        if (ReferenceFileCheckbox.isSelected()) {choosereferencetype();}
        ReferenceFileCheckbox.setSelected(Root.getOptions().getSessionOptions().getReferenceoption());
        if (ReferenceFileCheckbox.isSelected()  && playerState == PlayerWidget.PlayerState.PLAYING) {displayreferencefile();}
        else {
            Root.getOptions().getSessionOptions().setReferenceoption(false);
            Root.getOptions().getSessionOptions().setReferencetype(null);
            closereferencefile();
        }
    }
    public void displayreferencefile() {
        displayReference = new PlayerWidget.DisplayReference(Root, currentcutorelement);
        displayReference.show();
    }
    public void closereferencefile() {
        if (displayReference != null && displayReference.isShowing()) {
            displayReference.close();
            displayReference = null;
        }
    }

}