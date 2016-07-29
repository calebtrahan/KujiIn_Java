package kujiin.util;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.CheckBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import kujiin.MainController;
import kujiin.xml.Ambiences;
import kujiin.xml.Entrainments;
import kujiin.xml.Options;
import kujiin.xml.Session;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// TODO Work On Completed Goals
// TODO If Ramp Disabled (And No Pre/PostSession Set) Ask User If They Want TO Add A Ramp Into 1st Practiced Cut (2/3/5) Min, Then Update UI And Create Session
// TODO Preferences Dialog Doesn't Initially Populate With Options From XML (Check If It Saves As Well?)

// TODO Redesign Goals Completed Dialog Using Bar Charts/Graphs


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
    private Meditatable Total;
    private MainController.PlayerState playerState;
    private Meditatable currentmeditatable;
    private Timeline updateuitimeline;
    private Duration totalsessiondurationelapsed;
    private Duration totalsessionduration;
    private int meditatablecount;
    private MainController.PlayerUI.DisplayReference displayReference;
    public MainController Root;
    public List<Meditatable> MeditatableswithGoalsCompletedThisSession;
    private MainController.PlayerUI playerUI;
    private List<Meditatable> itemsinsession;
    private Entrainments entrainments;
    private Ambiences ambiences;

    public This_Session(MainController mainController) {
        Root = mainController;
        setPlayerState(MainController.PlayerState.IDLE);
        entrainments = new Entrainments(Root);
        entrainments.unmarshall();
        ambiences = new Ambiences(Root);
        ambiences.unmarshall();
        Presession =  new Qi_Gong(0, "Presession", 0, "Gather Qi Before The Session Starts", this, Root.PreSwitch, Root.PreTime);
        Rin = new Cut(1, "RIN", 0, "Meet (Spirit & Invite Into The Body)", this, Root.RinSwitch, Root.RinTime);
        Kyo = new Cut(2, "KYO", 0, "Troops (Manage Internal Strategy/Util)", this, Root.KyoSwitch, Root.KyoTime);
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
        Total = new Total(16, "Total", 0, "", this, null, null);
    }

// Getters And Setters
    public MainController getRoot() {return Root;}
    public void setPlayerState(MainController.PlayerState playerState) {this.playerState = playerState;}
    public MainController.PlayerState getPlayerState() {return playerState;}
    public boolean isValid() {
        Duration totaltime = Duration.ZERO;
        for (Meditatable i : getAllMeditatables()) {totaltime.add(i.getduration());}
        return totaltime.greaterThan(Duration.ZERO);
    }
    public MainController.PlayerUI getPlayerUI() {
        return playerUI;
    }
    public void setPlayerUI(MainController.PlayerUI playerUI) {
        this.playerUI = playerUI;
    }
    public ArrayList<Meditatable> getAllMeditatables() {return new ArrayList<>(Arrays.asList(Presession, Rin, Kyo, Toh, Sha, Kai, Jin, Retsu, Zai, Zen, Earth, Air, Fire, Water, Void, Postsession));}
    public ArrayList<String> getAllMeditatablesNames() {
        ArrayList<String> allmeditatablesnames = getAllMeditatables().stream().map(i -> i.name).collect(Collectors.toCollection(ArrayList::new));
        return allmeditatablesnames;
    }
    public ArrayList<Meditatable> getAllMeditatablesincludingTotalforTracking() {return new ArrayList<>(Arrays.asList(Presession, Rin, Kyo, Toh, Sha, Kai, Jin, Retsu, Zai, Zen, Earth, Air, Fire, Water, Void, Postsession, Total));}
    public ArrayList<String> getAllMeditablesincludingTotalNames() {
        return getAllMeditatablesincludingTotalforTracking().stream().map(i -> i.name).collect(Collectors.toCollection(ArrayList::new));
    }
    public ArrayList<Cut> getallCuts()  {return new ArrayList<>(Arrays.asList(Rin, Kyo, Toh, Sha, Kai, Jin, Retsu, Zai, Zen));}
    public ArrayList<Element> getallElements() {return new ArrayList<>(Arrays.asList(Earth, Air, Fire, Water, Void));}
    public List<Meditatable> getallitemsinSession() {
        return itemsinsession;
    }
    public Meditatable getCurrentmeditatable() {
        return currentmeditatable;
    }
    public int getCurrentindexofplayingelement() {
        try {return getallitemsinSession().indexOf(currentmeditatable);}
        catch (NullPointerException | IndexOutOfBoundsException ignored) {return -1;}
    }
    public void setItemsinsession(List<Meditatable> itemsinsession) {
        this.itemsinsession = itemsinsession;
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
    public MainController.PlayerUI.DisplayReference getDisplayReference() {
        return displayReference;
    }
    public void setDisplayReference(MainController.PlayerUI.DisplayReference displayReference) {
        this.displayReference = displayReference;
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
    public ArrayList<Integer> getallsessionvalues() {
        ArrayList<Integer> values = getAllMeditatables().stream().map(i -> new Double(i.getduration().toMinutes()).intValue()).collect(Collectors.toCollection(ArrayList::new));
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
    public boolean createsession() {
    if (sessionhasvalidvalues()) {
        populateitemsinsession();
        switch (checksessionwellformed()) {
            case YES:
                break;
            case NO:
                break;
            case CANCEL:
                return false;
        }
        for (Meditatable i : getallitemsinSession()) {
            if (! i.build(getallitemsinSession(), Root.AmbienceSwitch.isSelected())) {return false;}
        }
        switch (checkgoals()) {
            case YES:
                break;
            case NO:
                break;
            case CANCEL:
                break;
        }
        return true;
    } else {return false;}
}
    // Get Session Values
    public List<Cut> getCutsInSession() {return getallitemsinSession().stream().filter(i -> i instanceof Cut).map(i -> (Cut) i).collect(Collectors.toCollection(ArrayList::new));}
    public List<Element> getElementsInSession() {return getallitemsinSession().stream().filter(i -> i instanceof Element).map(i -> (Element) i).collect(Collectors.toCollection(ArrayList::new));}
    // Session Values Validation
    public void populateitemsinsession() {
        itemsinsession = new ArrayList<>();
        for (Meditatable i : getAllMeditatables()) {
            if (i instanceof Qi_Gong) {
                if (i.getduration().greaterThan(Duration.ZERO) || Root.getOptions().getSessionOptions().getRampenabled()) {itemsinsession.add(i);}
            }
            else if (i.getduration().greaterThan(Duration.ZERO)) {itemsinsession.add(i);}
        }
    }
    public boolean sessionhasvalidvalues() {
        boolean sessionhasvalidvalues = false;
        for (Meditatable i : getAllMeditatables()) {if (i.getduration().greaterThan(Duration.ZERO)) {sessionhasvalidvalues = true;}}
        return sessionhasvalidvalues;
    }
    // Well Formed Session Validation
    public boolean firstcutconnectedtorin(List<Cut> cutsinsession) {
        try {
            return cutsinsession.get(0).number == 1;
        } catch (Exception e) {return false;}
    }
    public boolean firstandlastcutsconnect(List<Cut> cutsinsession) {
        if (cutsinsession.size() == 1) {return true;}
        try {
            int count = cutsinsession.get(0).number;
            for (Cut i : cutsinsession) {
                if (i.number != count) {return false;}
                count++;
            }
            return true;
        } catch (NullPointerException | IndexOutOfBoundsException ignored) {return false;}
    }
    public Util.AnswerType checksessionwellformed() {
        List<Cut> cutsinsession = getCutsInSession();
        List<Element> elementsinsession = getElementsInSession();
        if (cutsinsession != null && cutsinsession.size() > 0) {
            boolean rinnotstartpoint = firstcutconnectedtorin(cutsinsession);
            if (! rinnotstartpoint) {rinnotstartpoint = ! Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "Cuts In Session Not Connected To RIN, And May Lack The Energy They Need To Get Results",
                    "Connect " + cutsinsession.get(0).name + " Back To RIN?");}
            if (! rinnotstartpoint || ! firstandlastcutsconnect(cutsinsession)) {
                MainController.CutsMissingDialog cutsMissingDialog = new MainController.CutsMissingDialog(Root, cutsinsession);
                cutsMissingDialog.showAndWait();
                switch (cutsMissingDialog.getResult()) {
                    case YES:
                        populateitemsinsession();
                        cutsinsession = getCutsInSession();
                        break;
                    case NO:
                        break;
                    case CANCEL:
                        return Util.AnswerType.CANCEL;
                    default:
                        break;
                }
            }
        }
        if (cutsinsession != null && elementsinsession != null && cutsinsession.size() > 0 && elementsinsession.size() > 0) {
            MainController.SortSessionItems sortSessionItems = new MainController.SortSessionItems(Root, getallitemsinSession());
            sortSessionItems.showAndWait();
            switch (sortSessionItems.getResult()) {
                case YES:
                    setItemsinsession(sortSessionItems.getorderedsessionitems());
                    break;
                case NO:
                    break;
                case CANCEL:
                    return Util.AnswerType.CANCEL;
                default:
                    break;
            }
            // Sort Session Parts
        }
        return Util.AnswerType.YES;
    }
    // Goals Validation
    public Util.AnswerType checkgoals() {
        ArrayList<Meditatable> meditatableswithoutlongenoughgoals = Root.goals_util_getmeditatableswithoutlongenoughgoals(getallitemsinSession());
        List<Integer>  notgooddurations = new ArrayList<>();
        if (! meditatableswithoutlongenoughgoals.isEmpty()) {
            boolean presessionmissinggoals = false;
            int cutcount = 0;
            int elementcount = 0;
            boolean postsessionmissinggoals = false;
            for (Meditatable i : meditatableswithoutlongenoughgoals) {
                if (i instanceof Cut) {cutcount++;}
                if (i instanceof Element) {elementcount++;}
                else {
                    if (i.name.equals("Presession")) {presessionmissinggoals = true;}
                    if (i.name.equals("Postsession")) {postsessionmissinggoals = true;}
                }
                notgooddurations.add(new Double(i.getduration().toMinutes()).intValue());
            }
            StringBuilder notgoodtext = new StringBuilder();
            if (presessionmissinggoals) {notgoodtext.append("Presession\n");}
            if (cutcount > 0) {notgoodtext.append(cutcount).append(" Cut(s)\n");}
            if (elementcount > 0) {notgoodtext.append(elementcount).append(" Element(s)\n");}
            if (postsessionmissinggoals) {notgoodtext.append("Postsession\n");}
            switch (Util.gui_getyesnocancelconfirmationdialog(Root, "Confirmation", "Goals Are Missing/Not Long Enough For \n" + notgoodtext.toString(), "Set A Goal Before Playback?", "Set Goal", "Continue Anyway", "Cancel Playback")) {
                case YES:
                    // TODO Make A Goal Set Dialog Before Playback Here
                    // Was last parameter-> Util.list_getmaxintegervalue(notgooddurations)
                    /*ProgressAndGoalsUI.SetANewGoalForMultipleCutsOrElements s = new ProgressAndGoalsUI.SetANewGoalForMultipleCutsOrElements(Root, meditatableswithoutlongenoughgoals);
                    s.showAndWait();
                    if (s.isAccepted()) {
                        List<Integer> cutindexes = s.getSelectedCutIndexes();
                        Double goalhours = s.getGoalhours();
                        LocalDate goaldate = s.getGoaldate();
                        boolean goalssetsuccessfully = true;
                        for (Integer i : cutindexes) {
                            try {
                                Meditatable x = getAllMeditatablesincludingTotalforTracking().get(i);
                                x.addGoal(new Goals.Goal(goalhours, x.name));
                            } catch (JAXBException ignored) {
                                goalssetsuccessfully = false;
                                Util.gui_showerrordialog(Root, "Error", "Couldn't Add Goal For " + getAllMeditatablesincludingTotalforTracking().get(i).name, "Check File Permissions");
                            }
                        }
                        if (goalssetsuccessfully) {
                            Util.gui_showinformationdialog(Root, "Information", "Goals For " + notgoodtext.toString() + "Set Successfully", "Session Will Now Be Created");
                        }
                    }
                    break;*/
                    break;
                case NO:
                    break;
                case CANCEL:
                    return Util.AnswerType.CANCEL;
            }
            return Util.AnswerType.YES;
        } else {return Util.AnswerType.YES;}
    }
    // Ambience Validation
    public void checkambience(CheckBox ambiencecheckbox) {
        if (sessionhasvalidvalues()) {
            ArrayList<Meditatable> cutsorelementswithnoambience = new ArrayList<>();
            ArrayList<Meditatable> meditatableswithreducedambience = new ArrayList<>();
            Service<Void> ambiencecheckerservice = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                for (Meditatable i : getAllMeditatables()) {
                                    updateMessage(String.format("Currently Checking %s...", i.name));
                                    if (! i.getAmbience().hasAnyAmbience()) {cutsorelementswithnoambience.add(i);}
                                    else if (! i.getAmbience().hasEnoughAmbience(i.getduration())) {meditatableswithreducedambience.add(i);}
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
            final MainController.SimpleTextDialogWithCancelButton[] cad = new MainController.SimpleTextDialogWithCancelButton[1];
            ambiencecheckerservice.setOnRunning(event -> {
                cad[0] = new MainController.SimpleTextDialogWithCancelButton(Root, "Checking Ambience", "Checking Ambience", "");
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
                        Util.gui_showerrordialog(Root, "Error", String.format("%s Have No Ambience At All", a.toString()), "Cannot Add Ambience");
                        if (Util.gui_getokcancelconfirmationdialog(Root, "Add Ambience", a.toString() + " Needs Ambience", "Open The Ambience Editor?")) {
                            MainController.AdvancedAmbienceEditor ambienceEditor = new MainController.AdvancedAmbienceEditor(Root, Root.getSession().getAmbiences());
                            ambienceEditor.showAndWait();
                        }
                    } else {
                        Util.gui_showerrordialog(Root, "Error", String.format("%s Have No Ambience At All", a.toString()), "Cannot Add Ambience");
                        if (Util.gui_getokcancelconfirmationdialog(Root, "Add Ambience", a.toString() + " Need Ambience", "Open The Ambience Editor?")) {
                            MainController.AdvancedAmbienceEditor ambienceEditor = new MainController.AdvancedAmbienceEditor(Root, Root.getSession().getAmbiences(), cutsorelementswithnoambience.get(0).name);
                            ambienceEditor.showAndWait();
                        }
                    }
                    ambiencecheckbox.setSelected(false);
                } else {
                    if (meditatableswithreducedambience.size() > 0) {
                        StringBuilder a = new StringBuilder();
                        int count = 1;
                        for (int i = 0; i < meditatableswithreducedambience.size(); i++) {
                            a.append("\n");
                            Meditatable thismeditatable = meditatableswithreducedambience.get(i);
                            String formattedcurrentduration = Util.formatdurationtoStringSpelledOut(thismeditatable.getAmbience().gettotalActualDuration(), null);
                            String formattedexpectedduration = Util.formatdurationtoStringSpelledOut(thismeditatable.getduration(), null);
                            a.append(count).append(". ").append(thismeditatable.name).append(" >  Current: ").append(formattedcurrentduration).append(" | Needed: ").append(formattedexpectedduration);
                            count++;
                        }
                        System.out.println(a.toString());
                        if (meditatableswithreducedambience.size() == 1) {
                            ambiencecheckbox.setSelected(Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", String.format("The Following Cut's Ambience Isn't Long Enough: %s ", a.toString()), "Shuffle And Loop Ambience For This Cut?"));
                        } else {
                            ambiencecheckbox.setSelected(Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", String.format("The Following Cuts' Ambience Aren't Long Enough: %s ", a.toString()), "Shuffle And Loop Ambience For These Cuts?"));
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
                Util.gui_showerrordialog(Root, "Error", "Couldn't Check Ambience", "Check Ambience Folder Read Permissions");
                ambiencecheckbox.setSelected(false);
            });
            ambiencecheckerservice.start();
        } else {
            Util.gui_showinformationdialog(Root, "Information", "Cannot Check Ambience", "No Cuts Have > 0 Values, So I Don't Know Which Ambience To Check");}
    }
    // Reference Files Validation
    public boolean checkallreferencefilesforsession(MainController.ReferenceType referenceType, boolean enableprompt) {
        int invalidcutcount = 0;
        for (Meditatable i : getallitemsinSession()) {
            if (!i.referencefilevalid(referenceType)) invalidcutcount++;
        }
        if (invalidcutcount > 0 && enableprompt) {
            return Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "There Are " + invalidcutcount + " Cuts/Elements With Empty/Invalid Reference Files", "Enable Reference Anyways?");
        } else {return invalidcutcount == 0;}
    }
    // Reset
    public void resetcreateditems() {
        getAllMeditatables().forEach(Meditatable::resetCreation);
    }

// Export
    public Service<Boolean> getsessionexporter() {
//        CreatorAndExporterUI.ExportingSessionDialog exportingSessionDialog = new CreatorAndExporterUI.ExportingSessionDialog(this);
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
//            if (exporterservice.getValue()) {Util.gui_showinformationdialog("Information", "Export Succeeded", "File Saved To: ");}
//            else {Util.gui_showerrordialog("Error", "Errors Occured During Export", "Please Try Again Or Contact Me For Support");}
//            exportingSessionDialog.close();
//        });
//        exporterservice.setOnFailed(event -> {
//            String v = exporterservice.getException().getMessage();
//            Util.gui_showerrordialog("Error", "Errors Occured While Trying To Create The This_Session. The Main Exception I Encoured Was " + v,
//                    "Please Try Again Or Contact Me For Support");
//            This_Session.deleteprevioussession();
//            exportingSessionDialog.close();
//        });
//        exporterservice.setOnCancelled(event -> {
//            Util.gui_showinformationdialog("Cancelled", "Export Cancelled", "You Cancelled Export");
//            This_Session.deleteprevioussession();
//            exportingSessionDialog.close();
//        });
//        return false;
    }
    public void getnewexportsavefile() {
//        File tempfile = Util.filechooser_save(Root.getScene(), "Save Export File As", null);
//        if (tempfile != null && Util.audio_isValid(tempfile)) {
//            setExportfile(tempfile);
//        } else {
//            if (tempfile == null) {return;}
//            if (Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "Invalid Audio File Extension", "Save As .mp3?")) {
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
    public void play(MainController.PlayerUI playerUI) {
        switch (playerState) {
            case IDLE:
            case STOPPED:
                MeditatableswithGoalsCompletedThisSession = new ArrayList<>();
                setPlayerUI(playerUI);
                totalsessiondurationelapsed = Duration.ZERO;
                totalsessionduration = Duration.ZERO;
                for (Meditatable i : itemsinsession) {totalsessionduration = totalsessionduration.add(i.getduration());}
                getPlayerUI().TotalTotalLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessionduration));
                updateuitimeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> updateplayerui()));
                updateuitimeline.setCycleCount(Animation.INDEFINITE);
                updateuitimeline.play();
                meditatablecount = 0;
                currentmeditatable = itemsinsession.get(meditatablecount);
                Root.getSessions().createnewsession();
                currentmeditatable.start();
                break;
            case PAUSED:
                updateuitimeline.play();
                currentmeditatable.resume();
                break;
        }
    }
    public void pause() {
        if (playerState == MainController.PlayerState.PLAYING) {
            currentmeditatable.pause();
            updateuitimeline.pause();
        }
    }
    public void stop() {
        if (! endsessionprematurely()) {return;}
        currentmeditatable.stop();
        updateuitimeline.stop();
        resetthissession();
    }
    public void updateplayerui() {
        try {
            totalsessiondurationelapsed = totalsessiondurationelapsed.add(Duration.seconds(1.0));
            try {
                currentmeditatable.elapsedtime = currentmeditatable.elapsedtime.add(Duration.seconds(1.0));
            } catch (NullPointerException ignored) {}
            Float currentprogress;
            Float totalprogress;
            try {
                if (currentmeditatable.elapsedtime.greaterThan(Duration.ZERO)) {currentprogress = (float) currentmeditatable.elapsedtime.toMillis() / (float) currentmeditatable.getduration().toMillis();}
                else {currentprogress = (float) 0;}
            } catch (NullPointerException ignored) {currentprogress = (float) 0;}
            if (totalsessiondurationelapsed.greaterThan(Duration.ZERO)) {
                totalprogress = (float) totalsessiondurationelapsed.toMillis()
                        / (float) totalsessionduration.toMillis();}
            else {totalprogress = (float) 0.0;}
            getPlayerUI().CurrentCutProgress.setProgress(currentprogress);
            getPlayerUI().TotalProgress.setProgress(totalprogress);
            currentprogress *= 100;
            totalprogress *= 100;
            getPlayerUI().CurrentCutTopLabel.setText(String.format("%s (%d", currentmeditatable.name, currentprogress.intValue()) + "%)");
            getPlayerUI().TotalSessionLabel.setText(String.format("Session (%d", totalprogress.intValue()) + "%)");
            try {getPlayerUI().CutCurrentLabel.setText(Util.formatdurationtoStringDecimalWithColons(currentmeditatable.elapsedtime));}
            catch (NullPointerException ignored) {getPlayerUI().CutCurrentLabel.setText(Util.formatdurationtoStringDecimalWithColons(Duration.ZERO));}
            getPlayerUI().TotalCurrentLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessiondurationelapsed));
            boolean displaynormaltime = getPlayerUI().displaynormaltime;
            if (displaynormaltime) {getPlayerUI().CutTotalLabel.setText(Util.formatdurationtoStringDecimalWithColons(currentmeditatable.getduration()));}
            else {getPlayerUI().CutTotalLabel.setText(Util.formatdurationtoStringDecimalWithColons(currentmeditatable.getduration().subtract(currentmeditatable.elapsedtime)));}
            if (displaynormaltime) {getPlayerUI().TotalTotalLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessionduration));}
            else {getPlayerUI().TotalTotalLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessionduration.subtract(totalsessiondurationelapsed)));}
            try {
                if (getDisplayReference() != null && getDisplayReference().isShowing()) {
                    getDisplayReference().CurrentProgress.setProgress(currentprogress / 100);
                    getDisplayReference().CurrentPercentage.setText(currentprogress.intValue() + "%");
                    getDisplayReference().TotalProgress.setProgress(totalprogress / 100);
                    getDisplayReference().TotalPercentage.setText(totalprogress.intValue() + "%");
                    getDisplayReference().CurrentName.setText(currentmeditatable.name);
                }
            } catch (NullPointerException ignored) {}
            Root.goals_gui_updateui();
            Root.sessions_gui_updateui();
            currentmeditatable.tick();
        } catch (Exception e) {e.printStackTrace();
//            new MainController.ExceptionDialog(Root, e).show();
        }
    }
    public void progresstonextmeditatable() {
        try {
            switch (playerState) {
                case TRANSITIONING:
                    try {
                        currentmeditatable.transition_goalscheck();
                        currentmeditatable.cleanupPlayersandAnimations();
                        meditatablecount++;
                        currentmeditatable = getallitemsinSession().get(meditatablecount);
                        currentmeditatable.start();
                    } catch (IndexOutOfBoundsException ignored) {
                        setPlayerState(MainController.PlayerState.IDLE);
                        currentmeditatable.cleanupPlayersandAnimations();
                        endofsession();
                    }
                    break;
                case PLAYING:
                    closereferencefile();
                    transition();
                    break;
            }
        } catch (Exception e) {new MainController.ExceptionDialog(Root, e).show();}
    }
    public void endofsession() {
        getPlayerUI().CurrentCutTopLabel.setText(currentmeditatable.name + " Completed");
        getPlayerUI().TotalSessionLabel.setText("Session Completed");
        updateuitimeline.stop();
        setPlayerState(MainController.PlayerState.STOPPED);
        Root.getSessions().deletenonvalidsessions();
        // TODO Some Animation Is Still Running At End Of Session. Find It And Stop It Then Change Session Finsished Dialog To Showandwait
        MainController.SessionDetails sessionDetails = new MainController.SessionDetails(Root, getallitemsinSession());
        sessionDetails.show();
        // TODO Prompt For Export
//        if (Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "Session Completed", "Export This Session For Later Use?")) {
//            getsessionexporter();}
        Root.sessions_gui_updateui();
        Root.goals_gui_updateui();
        resetthissession();
    }
    public void resetthissession() {
        updateuitimeline = null;
        Root.getSessions().deletenonvalidsessions();
        meditatablecount = 0;
        totalsessiondurationelapsed = Duration.ZERO;
        totalsessionduration = Duration.ZERO;
        getPlayerUI().reset();
    }
    public void transition() {
        Session currentsession =  Root.getSessions().sessioninformation_getspecificsession( Root.getSessions().sessioninformation_totalsessioncount() - 1);
        currentsession.updatecutduration(currentmeditatable.number, new Double(currentmeditatable.getduration().toMinutes()).intValue());
        Root.getSessions().marshall();
        Root.goals_gui_updateui();
        currentmeditatable.stop();
        if (currentmeditatable.name.equals("Postsession")) {setPlayerState(MainController.PlayerState.TRANSITIONING); progresstonextmeditatable();}
        else {
            if (Root.getOptions().getSessionOptions().getAlertfunction()) {
                Media alertmedia = new Media(Root.getOptions().getSessionOptions().getAlertfilelocation());
                MediaPlayer alertplayer = new MediaPlayer(alertmedia);
                alertplayer.play();
                setPlayerState(MainController.PlayerState.TRANSITIONING);
                alertplayer.setOnEndOfMedia(() -> {
                    alertplayer.stop();
                    alertplayer.dispose();
                    progresstonextmeditatable();
                });
                alertplayer.setOnError(() -> {
                    if (Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "An Error Occured While Playing Alert File" +
                            alertplayer.getMedia().getSource() + "'", "Retry Playing Alert File? (Pressing Cancel " +
                            "Will Progress To The Next Cut)")) {
                        alertplayer.stop();
                        alertplayer.play();
                    } else {
                        alertplayer.stop();
                        alertplayer.dispose();
                        progresstonextmeditatable();
                    }
                });
            } else {
                setPlayerState(MainController.PlayerState.TRANSITIONING);
                progresstonextmeditatable();
            }
        }
    }
    public void error_endplayback() {

    }
    public boolean endsessionprematurely() {
        if (getPlayerState() == MainController.PlayerState.PLAYING || getPlayerState() == MainController.PlayerState.PAUSED || getPlayerState() == MainController.PlayerState.TRANSITIONING) {
            pause();
            if (Util.gui_getokcancelconfirmationdialog(Root, "End Session Early", "End Session Prematurely?", "Really End Session Prematurely")) {return true;}
            else {play(Root.getPlayer()); return false;}
        } else {return true;}
    }
    public void togglevolumebinding() {
        if (getPlayerState() != MainController.PlayerState.IDLE && getPlayerState() != MainController.PlayerState.STOPPED) {
            currentmeditatable.volume_rebindentrainment();
            if (currentmeditatable.getAmbienceenabled()) {
                currentmeditatable.volume_rebindambience();}
        }
    }
    public void displayreferencefile() {
        boolean notalreadyshowing = displayReference == null || ! displayReference.isShowing();
        boolean referenceenabledwithvalidtype = Root.getOptions().getSessionOptions().getReferenceoption() &&
                (Root.getOptions().getSessionOptions().getReferencetype() == MainController.ReferenceType.html || Root.getOptions().getSessionOptions().getReferencetype() == MainController.ReferenceType.txt);
        if (notalreadyshowing && referenceenabledwithvalidtype) {
            displayReference = new MainController.PlayerUI.DisplayReference(Root, currentmeditatable);
            displayReference.show();
            displayReference.setOnHidden(event -> {
                currentmeditatable.volume_rebindentrainment();
                if (currentmeditatable.ambienceenabled) {
                    currentmeditatable.volume_rebindambience();
                }
            });
        }
    }
    public void closereferencefile() {
        if (referencecurrentlyDisplayed()) {
            displayReference.close();
        }
    }
    public boolean referencecurrentlyDisplayed() {
        return displayReference != null && displayReference.isShowing() && displayReference.EntrainmentVolumeSlider != null;
    }

}