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
import kujiin.dialogs.SimpleTextDialogWithCancelButton;
import kujiin.ui.CreatorAndExporterUI;
import kujiin.ui.PlayerUI;
import kujiin.ui.ProgressAndGoalsUI;
import kujiin.xml.*;

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
    private PlayerUI.PlayerState playerState;
    private Meditatable currentcutorelement;
    private Timeline updateuitimeline;
    private Sessions sessions;
    private int totalsecondselapsed;
    private int totalsecondsinsession;
    private int cutorelementcount;
    private PlayerUI.DisplayReference displayReference;
    public MainController Root;
    public List<Goals.Goal> GoalsCompletedThisSession;
    private PlayerUI playerUI;
    private List<Meditatable> itemsinsession;
    private Entrainments entrainments;
    private Ambiences ambiences;

    public This_Session(MainController mainController) {
        Root = mainController;
        setPlayerState(PlayerUI.PlayerState.IDLE);
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
    public void setPlayerState(PlayerUI.PlayerState playerState) {this.playerState = playerState;}
    public PlayerUI.PlayerState getPlayerState() {return playerState;}
    public boolean isValid() {
        int totaltime = 0;
        for (Object i : getAllMeditatables()) {totaltime += ((Meditatable) i).getdurationinminutes();}
        return totaltime > 0;
    }
    public void setSessions() {
        sessions = Root.getProgressTracker().getSessions();
    }
    public PlayerUI getPlayerUI() {
        return playerUI;
    }
    public void setPlayerUI(PlayerUI playerUI) {
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
    public Meditatable getCurrentcutorelement() {
        return currentcutorelement;
    }
    public int getCurrentindexofplayingelement() {
        try {return getallitemsinSession().indexOf(currentcutorelement);}
        catch (NullPointerException | IndexOutOfBoundsException ignored) {return -1;}
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
    public PlayerUI.DisplayReference getDisplayReference() {
        return displayReference;
    }
    public void setDisplayReference(PlayerUI.DisplayReference displayReference) {
        this.displayReference = displayReference;
    }
    public int getTotalsecondselapsed() {
        return totalsecondselapsed;
    }
    public void setTotalsecondselapsed(int totalsecondselapsed) {
        this.totalsecondselapsed = totalsecondselapsed;
    }
    public int getTotalsecondsinsession() {
        return totalsecondsinsession;
    }
    public void setTotalsecondsinsession(int totalsecondsinsession) {
        this.totalsecondsinsession = totalsecondsinsession;
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
        if (elementorcutindex == 0) {
            int value = Presession.getdurationinminutes();
            if (Root.getOptions().getSessionOptions().getRampenabled()) {value -= Root.getOptions().getSessionOptions().getRampduration();}
            return value;
        }
        if (elementorcutindex == 1) {return Rin.getdurationinminutes();}
        if (elementorcutindex == 2) {return Kyo.getdurationinminutes();}
        if (elementorcutindex == 3) {return Toh.getdurationinminutes();}
        if (elementorcutindex == 4) {return Sha.getdurationinminutes();}
        if (elementorcutindex == 5) {return Kai.getdurationinminutes();}
        if (elementorcutindex == 6) {return Jin.getdurationinminutes();}
        if (elementorcutindex == 7) {return Retsu.getdurationinminutes();}
        if (elementorcutindex == 8) {return Zai.getdurationinminutes();}
        if (elementorcutindex == 9) {return Zen.getdurationinminutes();}
        if (elementorcutindex == 10) {return Earth.getdurationinminutes();}
        if (elementorcutindex == 11) {return Air.getdurationinminutes();}
        if (elementorcutindex == 12) {return Fire.getdurationinminutes();}
        if (elementorcutindex == 13) {return Water.getdurationinminutes();}
        if (elementorcutindex == 14) {return Void.getdurationinminutes();}
        if (elementorcutindex == 15) {
            int value = Postsession.getdurationinminutes();
            if (Root.getOptions().getSessionOptions().getRampenabled()) {value -= Root.getOptions().getSessionOptions().getRampduration();}
            return value;
        }
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
        for (Object i : getAllMeditatables()) {
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
                                for (Meditatable i : getAllMeditatables()) {
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
                    if (cutsorelementswithreducedambience.size() > 0) {
                        StringBuilder a = new StringBuilder();
                        int count = 1;
                        for (int i = 0; i < cutsorelementswithreducedambience.size(); i++) {
                            a.append("\n");
                            Meditatable thiscut = cutsorelementswithreducedambience.get(i);
                            String formattedcurrentduration = Util.format_minstohrsandmins_short((int) ((thiscut.getAmbience().gettotalActualDuration() / 1000) / 60));
                            String formattedexpectedduration = Util.format_minstohrsandmins_short(cutsorelementswithreducedambience.get(i).getdurationinminutes());
                            a.append(count).append(". ").append(thiscut.name).append(" >  Current: ").append(formattedcurrentduration).append(" | Needed: ").append(formattedexpectedduration);
                            count++;
                        }
                        if (cutsorelementswithreducedambience.size() == 1) {
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
    public Util.AnswerType checkcutsinorder() {
        int workingcutcount = 0;
        for (Meditatable i : getAllMeditatables()) {if (i instanceof Cut) {if (i.getdurationinminutes() > 0) workingcutcount++;}}
        if (workingcutcount < getallCuts().size()) {
            CreatorAndExporterUI.CutsMissingDialog cutsMissingDialog = new CreatorAndExporterUI.CutsMissingDialog(Root, getallCuts());
            cutsMissingDialog.showAndWait();
            itemsinsession.clear();
            setupcutsinsession();
            return cutsMissingDialog.getResult();
        } else {return Util.AnswerType.YES;}
    }
    public Util.AnswerType checkgoals() {
        ArrayList<Meditatable> meditatableswithoutlongenoughgoals = Root.getProgressTracker().getmeditatableswithoutlongenoughgoals(getallitemsinSession());
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
                notgooddurations.add(i.getdurationinminutes());
            }
            StringBuilder notgoodtext = new StringBuilder();
            if (presessionmissinggoals) {notgoodtext.append("Presession\n");}
            if (cutcount > 0) {notgoodtext.append(cutcount).append(" Cut(s)\n");}
            if (elementcount > 0) {notgoodtext.append(elementcount).append(" Element(s)\n");}
            if (postsessionmissinggoals) {notgoodtext.append("Postsession\n");}
            switch (Util.gui_getyesnocancelconfirmationdialog(Root, "Confirmation", "Goals Are Missing/Not Long Enough For \n" + notgoodtext.toString(), "Set A Goal Before Playback?", "Set Goal", "Continue Anyway", "Cancel Playback")) {
                case YES:
                    // Was last parameter-> Util.list_getmaxintegervalue(notgooddurations)
                    /*ProgressAndGoalsUI.SetANewGoalForMultipleCutsOrElements s = new ProgressAndGoalsUI.SetANewGoalForMultipleCutsOrElements(Root, meditatableswithoutlongenoughgoals);
                    s.showAndWait();
                    if (s.isAccepted()) {
                        List<Integer> cutindexes = s.getSelectedCutIndexes();
                        Double goalhours = s.getGoalhours();
                        LocalDate goaldate = s.getGoaldate();
                        boolean goalssetsuccessfully = true;
                        // TODO Only Add Goals If They Are Higher (And A Later Date) Than All Other Goals For That Cut/Element
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
                case NO:
                    break;
                case CANCEL:
                    // TODO Find A Way To Cancel Playback And Creation Here
                    break;
            }
        }
        return null;
    }
    public boolean setupcutsinsession() {
        itemsinsession = new ArrayList<>();
        if (Presession.getdurationinminutes() != 0 || Root.getOptions().getSessionOptions().getRampenabled()) {itemsinsession.add(Presession);}
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
        if (Postsession.getdurationinminutes() != 0 || Root.getOptions().getSessionOptions().getRampenabled()) {itemsinsession.add(Postsession);}
        setItemsinsession(itemsinsession);
        return getallitemsinSession().size() > 0;
    }
    public boolean wellformedsessionquickcheck() {
        boolean wellformed = true;
        ArrayList<Cut> cutsinsession = getallitemsinSession().stream().filter(i -> i instanceof Cut).map(i -> (Cut) i).collect(Collectors.toCollection(ArrayList::new));
        int cutcount = 1;
        for (Cut i : cutsinsession) {
            if (i.number != cutcount) {wellformed = false;}
            cutcount++;
        }
        return wellformed;
    }
    public boolean checkallreferencefilesforsession(PlayerUI.ReferenceType referenceType, boolean enableprompt) {
        int invalidcutcount = 0;
        for (Meditatable i : getallitemsinSession()) {
            if (!i.referencefilevalid(referenceType)) invalidcutcount++;
        }
        if (invalidcutcount > 0 && enableprompt) {
            return Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "There Are " + invalidcutcount + " Cuts/Elements With Empty/Invalid Reference Files", "Enable Reference Anyways?");
        } else {return invalidcutcount == 0;}
    }
    public boolean create() {
        if (setupcutsinsession()) {
            int cutcount = 0;
            int elementcount = 0;
            for (Meditatable i : getallitemsinSession()) {if (i instanceof Element) elementcount++;}
            for (Meditatable i : getallitemsinSession()) {if (i instanceof Cut) cutcount++;}
            if (cutcount > 0) {if (checkcutsinorder() == Util.AnswerType.CANCEL) return false;}
            if (elementcount > 0 || cutcount > 0) {
                if (cutcount > 0 && ! wellformedsessionquickcheck()) {
                    CreatorAndExporterUI.SortSessionItems sortSessionItems = new CreatorAndExporterUI.SortSessionItems(Root, getallitemsinSession());
                    sortSessionItems.showAndWait();
                    if (sortSessionItems.getResult() != null && sortSessionItems.getResult() == Util.AnswerType.CANCEL) {return false;}
                    if (sortSessionItems.getorderedsessionitems() != null) {setItemsinsession(sortSessionItems.getorderedsessionitems());}
                }
            }
            for (Meditatable i : getallitemsinSession()) {
                if (! i.build(getallitemsinSession(), Root.AmbienceSwitch.isSelected())) {return false;}
            }
            checkgoals();
            return true;
        } else {return false;}
    }
    public void resetcreateditems() {
        for (Object i : getAllMeditatables()) {
            if (i instanceof Element) {((Element) i).resetCreation();}
            if (i instanceof Cut) {((Cut) i).resetCreation();}
            if (i instanceof  Qi_Gong) {((Qi_Gong) i).resetCreation();}
        }
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
    public void play(PlayerUI playerUI) {
        switch (playerState) {
            case IDLE:
            case STOPPED:
                setPlayerUI(playerUI);
                totalsecondselapsed = 0;
                totalsecondsinsession = 0;
                for (Meditatable i : itemsinsession) {totalsecondsinsession += i.getdurationinseconds();}
                getPlayerUI().TotalTotalLabel.setText(Util.format_secondsforplayerdisplay(totalsecondsinsession));
                updateuitimeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> updateplayerui()));
                updateuitimeline.setCycleCount(Animation.INDEFINITE);
                updateuitimeline.play();
                cutorelementcount = 0;
                currentcutorelement = itemsinsession.get(cutorelementcount);
                sessions.createnewsession();
                currentcutorelement.start();
                break;
            case PAUSED:
                updateuitimeline.play();
                currentcutorelement.resume();
                break;
        }
    }
    public void pause() {
        if (playerState == PlayerUI.PlayerState.PLAYING) {
            currentcutorelement.pause();
            updateuitimeline.pause();
        }
    }
    public void stop() {
        if (! endsessionprematurely()) {return;}
        currentcutorelement.stop();
        updateuitimeline.stop();
        resetthissession();
    }
    public void updateplayerui() {
        try {
//            try {
                // TODO Debug Entrainment Player Being Off And On Here
//                System.out.println(currentcutorelement.name + " (" + Util.format_secondsforplayerdisplay(currentcutorelement.secondselapsed) +
//                        ") -> Entrainment Player Is : " + currentcutorelement.getCurrentEntrainmentPlayer().getStatus().toString() +
//                " And Ambience Player Is: " + currentcutorelement.getCurrentAmbiencePlayer().getStatus().toString());
//            } catch (NullPointerException ignored) {}
            totalsecondselapsed++;
            currentcutorelement.secondselapsed++;
            Float currentprogress;
            Float totalprogress;
            if (currentcutorelement.secondselapsed != 0) {currentprogress = (float) currentcutorelement.secondselapsed / (float) currentcutorelement.getdurationinseconds();}
            else {currentprogress = (float) 0.0;}
            if (totalsecondselapsed != 0) {totalprogress = (float) totalsecondselapsed / (float) totalsecondsinsession;}
            else {totalprogress = (float) 0.0;}
            getPlayerUI().CurrentCutProgress.setProgress(currentprogress);
            getPlayerUI().TotalProgress.setProgress(totalprogress);
            currentprogress *= 100;
            totalprogress *= 100;
            getPlayerUI().CurrentCutTopLabel.setText(String.format("%s (%d", currentcutorelement.name, currentprogress.intValue()) + "%)");
            getPlayerUI().TotalSessionLabel.setText(String.format("Session (%d", totalprogress.intValue()) + "%)");
            getPlayerUI().CutCurrentLabel.setText(currentcutorelement.getcurrenttimeformatted());
            boolean displaynormaltime = getPlayerUI().displaynormaltime;
            if (displaynormaltime) {getPlayerUI().CutTotalLabel.setText(currentcutorelement.gettotaltimeformatted());}
            else {getPlayerUI().CutTotalLabel.setText(Util.format_secondsleftforplayerdisplay(currentcutorelement.secondselapsed, currentcutorelement.getdurationinseconds()));}
            getPlayerUI().TotalCurrentLabel.setText(Util.format_secondsforplayerdisplay(totalsecondselapsed));
            if (displaynormaltime) {getPlayerUI().TotalTotalLabel.setText(Util.format_secondsforplayerdisplay(getTotalsecondsinsession()));}
            else {getPlayerUI().TotalTotalLabel.setText(Util.format_secondsleftforplayerdisplay(totalsecondselapsed, getTotalsecondsinsession()));}
            try {
                if (getDisplayReference() != null && getDisplayReference().isShowing()) {
                    getDisplayReference().CurrentProgress.setProgress(currentprogress / 100);
                    getDisplayReference().CurrentPercentage.setText(currentprogress.intValue() + "%");
                    getDisplayReference().TotalProgress.setProgress(totalprogress / 100);
                    getDisplayReference().TotalPercentage.setText(totalprogress.intValue() + "%");
                    getDisplayReference().CurrentName.setText(currentcutorelement.name);
                }
            } catch (NullPointerException ignored) {}
            Root.getProgressTracker().updategoalsui();
            Root.getProgressTracker().updatesessionsprogressui();
            currentcutorelement.tick();
        } catch (Exception e) {e.printStackTrace();
//            new MainController.ExceptionDialog(Root, e).show();
        }
    }
    public void progresstonextcut() {
        try {
            switch (playerState) {
                case TRANSITIONING:
                    try {
                        currentcutorelement.transition_goalscheck();
                        currentcutorelement.cleanupPlayersandAnimations();
                        cutorelementcount++;
                        currentcutorelement = getallitemsinSession().get(cutorelementcount);
                        currentcutorelement.start();
                    } catch (IndexOutOfBoundsException ignored) {
                        setPlayerState(PlayerUI.PlayerState.IDLE);
                        currentcutorelement.cleanupPlayersandAnimations();
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
        getPlayerUI().CurrentCutTopLabel.setText(currentcutorelement.name + " Completed");
        getPlayerUI().TotalSessionLabel.setText("Session Completed");
        updateuitimeline.stop();
        setPlayerState(PlayerUI.PlayerState.STOPPED);
        sessions.deletenonvalidsessions();
        // TODO Some Animation Is Still Running At End Of Session. Find It And Stop It Then Change Session Finsished Dialog To Showandwait
        MainController.SessionDetails sessionDetails = new MainController.SessionDetails(Root, getallitemsinSession());
        sessionDetails.show();
        sessionDetails.setOnHidden(event -> {
            if (GoalsCompletedThisSession != null && GoalsCompletedThisSession.size() == 1) {
                Goals.Goal i = GoalsCompletedThisSession.get(0);
                int index = getAllMeditablesincludingTotalNames().indexOf(i.getMeditatableName());
                Meditatable x = getAllMeditatablesincludingTotalforTracking().get(index);
                double currentpracticedhours = Util.convert_minstodecimalhours(x.getTotalMinutesPracticed(), 2);
                new ProgressAndGoalsUI.SingleGoalCompletedDialog(Root, i, currentpracticedhours);
            } else if (GoalsCompletedThisSession != null && GoalsCompletedThisSession.size() > 1) {
                new ProgressAndGoalsUI.MultipleGoalsCompletedDialog(Root, GoalsCompletedThisSession).showAndWait();
            }
        });
//        PlayerUI.SessionFinishedDialog sess = new PlayerUI.SessionFinishedDialog(Root);
//        sess.show();
//        sess.setOnHidden(event -> {
//            if (GoalsCompletedThisSession != null && GoalsCompletedThisSession.size() == 1) {
//                Goals.Goal i = GoalsCompletedThisSession.get(0);
//                int cutindex = new ArrayList<>(Arrays.asList(ProgressAndGoalsUI.GOALCUTNAMES)).indexOf(i.getMeditatableName());
//                double currentpracticedhours = Util.convert_minstodecimalhours(Root.getProgressTracker().getSessions().sessioninformation_getallsessiontotals(cutindex, false), 2);
//                new ProgressAndGoalsUI.SingleGoalCompletedDialog(Root, i, currentpracticedhours);
//            } else if (GoalsCompletedThisSession != null && GoalsCompletedThisSession.size() > 1) {
//                new ProgressAndGoalsUI.MultipleGoalsCompletedDialog(Root, GoalsCompletedThisSession).showAndWait();
//            }
//        });
        // TODO Prompt For Export
//        if (Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "Session Completed", "Export This Session For Later Use?")) {
//            getsessionexporter();}
        Root.getProgressTracker().updategoalsui();
        resetthissession();
    }
    public void resetthissession() {
        updateuitimeline = null;
        sessions.deletenonvalidsessions();
        cutorelementcount = 0;
        totalsecondselapsed = 0;
        totalsecondsinsession = 0;
        getPlayerUI().reset();
    }
    public void transition() {
        Session currentsession = sessions.sessioninformation_getspecificsession(sessions.sessioninformation_totalsessioncount() - 1);
        currentsession.updatecutduration(currentcutorelement.number, currentcutorelement.getdurationinminutes());
        sessions.marshall();
        Root.getProgressTracker().updategoalsui();
        currentcutorelement.stop();
        if (currentcutorelement.name.equals("Postsession")) {setPlayerState(PlayerUI.PlayerState.TRANSITIONING); progresstonextcut();}
        else {
            if (Root.getOptions().getSessionOptions().getAlertfunction()) {
                Media alertmedia = new Media(Root.getOptions().getSessionOptions().getAlertfilelocation());
                MediaPlayer alertplayer = new MediaPlayer(alertmedia);
                alertplayer.play();
                setPlayerState(PlayerUI.PlayerState.TRANSITIONING);
                alertplayer.setOnEndOfMedia(() -> {
                    alertplayer.stop();
                    alertplayer.dispose();
                    progresstonextcut();
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
                        progresstonextcut();
                    }
                });
            } else {
                setPlayerState(PlayerUI.PlayerState.TRANSITIONING);
                progresstonextcut();
            }
        }
    }
    public void error_endplayback() {

    }
    public boolean endsessionprematurely() {
        if (getPlayerState() == PlayerUI.PlayerState.PLAYING || getPlayerState() == PlayerUI.PlayerState.PAUSED || getPlayerState() == PlayerUI.PlayerState.TRANSITIONING) {
            pause();
            if (Util.gui_getokcancelconfirmationdialog(Root, "End Session Early", "End Session Prematurely?", "Really End Session Prematurely")) {return true;}
            else {play(Root.getPlayer()); return false;}
        } else {return true;}
    }
    public void togglevolumebinding() {
        if (getPlayerState() != PlayerUI.PlayerState.IDLE && getPlayerState() != PlayerUI.PlayerState.STOPPED) {
            currentcutorelement.volume_rebindentrainment();
            if (currentcutorelement.getAmbienceenabled()) {currentcutorelement.volume_rebindambience();}
        }
    }

// Reference Files
    public void choosereferencetype() {
        if (! Root.getOptions().getSessionOptions().getReferenceoption() && Root.getOptions().getSessionOptions().getReferencetype() == null) {
            PlayerUI.ReferenceTypeDialog reftype = new PlayerUI.ReferenceTypeDialog(Root);
            reftype.showAndWait();
            Root.getOptions().getSessionOptions().setReferencetype(reftype.getReferenceType());
            Root.getOptions().getSessionOptions().setReferencefullscreen(reftype.getFullscreen());
            Root.getOptions().getSessionOptions().setReferenceoption(reftype.getEnabled());
        }
    }
//    public void togglereferencedisplay(CheckBox ReferenceFileCheckbox) {
//        if (ReferenceFileCheckbox.isSelected()) {choosereferencetype();}
//        ReferenceFileCheckbox.setSelected(Root.getOptions().getSessionOptions().getReferenceoption());
//        if (ReferenceFileCheckbox.isSelected()  && playerState == PlayerUI.PlayerState.PLAYING) {displayreferencefile();}
//        else {
//            Root.getOptions().getSessionOptions().setReferenceoption(false);
//            Root.getOptions().getSessionOptions().setReferencetype(null);
//            closereferencefile();
//        }
//    }
    public void displayreferencefile() {
        boolean notalreadyshowing = displayReference == null || ! displayReference.isShowing();
        boolean referenceenabledwithvalidtype = Root.getOptions().getSessionOptions().getReferenceoption() &&
                (Root.getOptions().getSessionOptions().getReferencetype() == PlayerUI.ReferenceType.html || Root.getOptions().getSessionOptions().getReferencetype() == PlayerUI.ReferenceType.txt);
        if (notalreadyshowing && referenceenabledwithvalidtype) {
            displayReference = new PlayerUI.DisplayReference(Root, currentcutorelement);
            displayReference.show();
            displayReference.setOnHidden(event -> {
                currentcutorelement.volume_rebindentrainment();
                if (currentcutorelement.ambienceenabled) {
                    currentcutorelement.volume_rebindambience();
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