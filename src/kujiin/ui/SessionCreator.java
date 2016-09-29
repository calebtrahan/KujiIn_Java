package kujiin.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import kujiin.ui.dialogs.*;
import kujiin.util.*;
import kujiin.util.enums.*;
import kujiin.util.interfaces.UI;
import kujiin.util.table.AmbienceSong;
import kujiin.util.table.AmbienceSongWithNumber;
import kujiin.util.table.SessionItem;
import kujiin.xml.Options;
import kujiin.xml.Preset;
import kujiin.xml.SoundFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static kujiin.util.enums.PlayerState.TRANSITIONING;

public class SessionCreator implements UI {
    private Button LoadPresetButton;
    private Button SavePresetButton;
    private TextField ApproximateEndTime;
    private TextField TotalSessionTime;
    private Button ChangeAllCutsButton;
    private Button ChangeAllElementsButton;
    private Button ResetButton;
    private Button PlayButton;
    private Button ExportButton;
    private Label StatusBar;
    private Preset Preset;
    private Timeline updateuitimeline;
    private Options options;
    private List<SessionPart> AllSessionParts;
    private MainController Root;
    private Player Player;
    private ProgressTracker progressTracker;
    private PlayerState playerState;
    private CreatorState creatorState = CreatorState.NOT_CREATED;
    private ExporterState exporterState;
    private SessionPlaybackOverview sessionPlaybackOverview;
    private DisplayReference displayReference;
    private ReferenceType referenceType;
    private AmbiencePlaybackType ambiencePlaybackType;
    private List<SessionPart> itemsinsession;
    private boolean ambienceenabled = false;

    public SessionCreator(MainController Root) {
        this.Root = Root;
        Preset = new Preset(Root);
        LoadPresetButton = Root.LoadPresetButton;
        SavePresetButton = Root.SavePresetButton;
        ApproximateEndTime = Root.ApproximateEndTime;
        TotalSessionTime = Root.TotalSessionTime;
        ChangeAllCutsButton = Root.ChangeAllCutsButton;
        ChangeAllElementsButton = Root.ChangeAllElementsButton;
        ResetButton = Root.ResetCreatorButton;
        PlayButton = Root.PlayButton;
        ExportButton = Root.ExportButton;
        StatusBar = Root.CreatorStatusBar;
        options = Root.getOptions();
        AllSessionParts = Root.getAllSessionParts(false);
        updateuitimeline = new Timeline(new KeyFrame(Duration.seconds(10), ae -> updategui()));
        updateuitimeline.setCycleCount(Animation.INDEFINITE);
    }

    public void setupListeners() {
        LoadPresetButton.setOnAction(event -> loadPreset());
        SavePresetButton.setOnAction(event -> savePreset());
        ChangeAllCutsButton.setOnAction(event -> changeallcutvalues());
        ChangeAllElementsButton.setOnAction(event -> changeallelementvalues());
        ResetButton.setOnAction(event -> reset(true));
        PlayButton.setOnAction(event -> playsession());
    }
    public void setupTooltips() {
        if (options.getProgramOptions().getTooltips()) {
            TotalSessionTime.setTooltip(new Tooltip("Total Session Time (Not Including Presession + Postsession Ramp, And Alert File)"));
            ApproximateEndTime.setTooltip(new Tooltip("Approximate Finish Time For This Session (Assuming You Start Now)"));
            ChangeAllCutsButton.setTooltip(new Tooltip("Change All Cut Values Simultaneously"));
            ChangeAllElementsButton.setTooltip(new Tooltip("Change All Element Values Simultaneously"));
            LoadPresetButton.setTooltip(new Tooltip("Load A Saved Preset"));
            SavePresetButton.setTooltip(new Tooltip("Save This Session As A Preset"));
            ExportButton.setTooltip(new Tooltip("Export This Session To .mp3 For Use Without The Program"));
        } else {
            TotalSessionTime.setTooltip(null);
            ApproximateEndTime.setTooltip(null);
            ChangeAllCutsButton.setTooltip(null);
            ChangeAllElementsButton.setTooltip(null);
            LoadPresetButton.setTooltip(null);
            SavePresetButton.setTooltip(null);
            ExportButton.setTooltip(null);
        }
    }
    public void setDisable(boolean disabled) {
        LoadPresetButton.setDisable(disabled);
        SavePresetButton.setDisable(disabled);
        ApproximateEndTime.setDisable(disabled);
        TotalSessionTime.setDisable(disabled);
        ChangeAllCutsButton.setDisable(disabled);
        ChangeAllElementsButton.setDisable(disabled);
        PlayButton.setDisable(disabled);
        ExportButton.setDisable(disabled);
        ResetButton.setDisable(disabled);
        for (SessionPart i : AllSessionParts) {i.gui_setDisable(disabled);}
        if (disabled) {updateuitimeline.stop();
        } else {updateuitimeline.play();}
    }
    public void setDisable(boolean disabled, String statusbarmsg) {
        setDisable(disabled);
        StatusBar.setText(statusbarmsg);
    }
    public boolean cleanup() {return true;}

// Getters And Setters
    public ReferenceType getReferenceType() {
        return referenceType;
    }
    public boolean isAmbienceenabled() {
        return ambienceenabled;
    }
    public AmbiencePlaybackType getAmbiencePlaybackType() {
        return ambiencePlaybackType;
    }
    public PlayerState getPlayerState() {
        return playerState;
    }
    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }
    public SessionCreator.Player getPlayer() {
        return Player;
    }
    public DisplayReference getDisplayReference() {
        return displayReference;
    }
    public ArrayList<Integer> getallsessionvalues() {
        return AllSessionParts.stream().map(i -> new Double(i.getduration().toMinutes()).intValue()).collect(Collectors.toCollection(ArrayList::new));
    }
    public boolean allvaluesnotzero() {
        for (SessionPart i : AllSessionParts) {if (i.hasValidValue()) {return true;}}
        return false;
    }

// Creation
    public void updategui() {
        boolean notallzero = false;
        try {for (Integer i : getallsessionvalues()) {if (i > 0) {notallzero = true;}}}
        catch (NullPointerException ignored) {}
        if (notallzero) {
            Duration totalsessiontime = Duration.ZERO;
            for (SessionPart i : AllSessionParts) {totalsessiontime = totalsessiontime.add(i.getduration());}
            TotalSessionTime.setText(Util.formatdurationtoStringSpelledOut(totalsessiontime, TotalSessionTime.getLayoutBounds().getWidth()));
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MILLISECOND, new Double(totalsessiontime.toMillis()).intValue());
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            ApproximateEndTime.setText(sdf.format(cal.getTime()));
        } else {
            TotalSessionTime.setText("-");
            ApproximateEndTime.setText("-");
        }
    }
    public void changeallcutvalues() {
        ChangeAllValuesDialog changevaluesdialog = new ChangeAllValuesDialog("Change All Cut Values To: ");
        changevaluesdialog.showAndWait();
        if (changevaluesdialog.getAccepted()) {
            Integer min = changevaluesdialog.getMinutes();
            for (Cut i : Root.getAllCuts()) {i.changevalue(min);}
            if (changevaluesdialog.getincludepresession()) {
                Root.getPresession().changevalue(min);
            }
            if (changevaluesdialog.getincludepostsession()) {
                Root.getPostsession().changevalue(min);
            }
        }
    }
    public void changeallelementvalues() {
        ChangeAllValuesDialog changevaluesdialog = new ChangeAllValuesDialog("Change All Element Values To: ");
        changevaluesdialog.showAndWait();
        if (changevaluesdialog.getAccepted()) {
            Integer min = changevaluesdialog.getMinutes();
            for (Element i : Root.getAllElements()) {i.changevalue(min);}
            if (changevaluesdialog.getincludepresession()) {
                Root.getPresession().changevalue(min);
            }
            if (changevaluesdialog.getincludepostsession()) {
                Root.getPostsession().changevalue(min);
            }
        }
    }
    public void populateitemsinsession() {
        itemsinsession = new ArrayList<>();
        for (SessionPart i : Root.getAllSessionParts(false)) {
            if (i.getduration().greaterThan(Duration.ZERO) || i.ramponly) {itemsinsession.add(i);}
            else if (i instanceof Qi_Gong && Root.getOptions().getSessionOptions().getPrepostrampenabled()) {i.setRamponly(true); itemsinsession.add(i);}
        }
    }
    public boolean creationprechecks() {
        if (Root.getProgramState() == ProgramState.IDLE) {
            if (!allvaluesnotzero()) {
                new ErrorDialog(options, "Error Creating Session", "At Least One SessionPart's Value Must Not Be 0", "Cannot Create Session");
                return false;
            }
            populateitemsinsession();
            setDisable(true, "Creator Disabled While Confirming Session");
            sessionPlaybackOverview = new SessionPlaybackOverview();
            sessionPlaybackOverview.showAndWait();
            setDisable(false, "");
            if (sessionPlaybackOverview.getResult()) {return true;}
            else {reset(false); return false;}
        } else {return false;}
    }
    public boolean create() {
        for (SessionPart i : itemsinsession) {
            if (! i.creation_build(itemsinsession)) {reset(false); return false;}
        }
        return true;
    }
    public void reset(boolean setvaluetozero) {
        itemsinsession.clear();
        for (SessionPart i : AllSessionParts) {i.creation_reset(setvaluetozero);}
    }

// Playback
    public void playsession() {
        if (creationprechecks() && create()) {
            setDisable(true, "Creator Disabled During Session Playback");
            Player = new Player();
            Player.setOnShowing(event -> Root.getStage().setIconified(true));
            Player.showAndWait();
            if (Root.getStage().isIconified()) {Root.getStage().setIconified(false);}
            progressTracker.displaysessiondetails(itemsinsession);
            setDisable(false, "");
        }
    }
    public boolean checkreferencefiles(boolean enableprompt) {
        int invalidsessionpartcount = 0;
        for (SessionPart i : AllSessionParts) {
            if (!i.reference_filevalid(referenceType)) invalidsessionpartcount++;
        }
        if (invalidsessionpartcount > 0 && enableprompt) {
            return new ConfirmationDialog(options, "Confirmation", null, "There Are " + invalidsessionpartcount + " Session Parts With Empty/Invalid Reference Files", "Enable Reference", "Disable Reference").getResult();
        } else {return invalidsessionpartcount == 0;}
    }

// Export

// Preset Methods
    public void loadPreset() {
        File presetfile = Preset.open();
        if (presetfile != null && Preset.hasvalidValues()) {
            preset_changecreationvaluestopreset(Preset.gettimes());
        } else {
            if (presetfile != null)
                new InformationDialog(options, "Invalid Preset File", "Invalid Preset File", "Cannot Load File");
        }
    }
    public void savePreset() {
        ArrayList<Double> creatorvaluesinminutes = new ArrayList<>();
        boolean validsession = false;
        for (SessionPart i : AllSessionParts) {
            creatorvaluesinminutes.add(i.getduration().toMinutes());
            if (i.getduration().greaterThan(Duration.ZERO)) {
                validsession = true;
            }
        }
        if (validsession) {
            Preset.settimes(creatorvaluesinminutes);
            if (Preset.save()) {
                Util.gui_showtimedmessageonlabel(StatusBar, "Preset Successfully Saved", 1500);
            } else {
                new ErrorDialog(options, "Error", "Couldn't Save Preset", "Your Preset Could Not Be Saved, Do You Have Write Access To That Directory?");
            }
        } else {
            new InformationDialog(options, "Information", "Cannot Save Preset", "All Values Are 0");
        }
    }
    public void preset_changecreationvaluestopreset(ArrayList<Double> presetvalues) {
        try {
            for (int i = 0; i < AllSessionParts.size(); i++) {
                AllSessionParts.get(i).changevalue(presetvalues.get(i).intValue());
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
            new ErrorDialog(options, "Error", "Couldn't Change Creator Values To Preset", "Try Reloaded Preset");
        }
    }

    public class ChangeAllValuesDialog extends Stage {
        public Button AcceptButton;
        public Button CancelButton;
        public TextField MinutesTextField;
        public CheckBox PresessionCheckbox;
        public CheckBox PostsessionCheckBox;
        private Boolean accepted;
        private int minutes;

        public ChangeAllValuesDialog(String toptext) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ChangeAllValuesDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                options.setStyle(this);
                this.setResizable(false);
                setTitle(toptext);
                setAccepted(false);
                MinutesTextField.setText("0");
                Util.custom_textfield_integer(MinutesTextField, 0, 600, 1);
                MinutesTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                    try {setMinutes(Integer.parseInt(MinutesTextField.getText()));}
                    catch (NumberFormatException ignored) {setMinutes(0);}
                });
            } catch (IOException e) {}
        }

        // Getters And Setters
        public Boolean getAccepted() {
            return accepted;
        }
        public void setAccepted(Boolean accepted) {
            this.accepted = accepted;
        }
        public int getMinutes() {
            return minutes;
        }
        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        // Button Actions
        public void acceptbuttonpressed(ActionEvent event) {setAccepted(true); close();}
        public void cancelbuttonpressed(ActionEvent event) {setAccepted(false); close();}
        public boolean getincludepresession() {return PresessionCheckbox.isSelected();}
        public boolean getincludepostsession() {return PostsessionCheckBox.isSelected();}
    }
    public class SessionPlaybackOverview extends Stage {
        public TableView<SessionItem> SessionItemsTable;
        public TableColumn<SessionItem, Integer> NumberColumn;
        public TableColumn<SessionItem, String> NameColumn;
        public TableColumn<SessionItem, String> DurationColumn;
        public TableColumn<SessionItem, String> AmbienceColumn;
        public TableColumn<SessionItem, String> GoalColumn;
        public Button UpButton;
        public Button DownButton;
        public Button CancelButton;
        public Button AdjustDurationButton;
        public Button SetGoalButton;
        public TextField TotalSessionTime;
        public Button PlaySessionButton;
        public TextField CompletionTime;
        public Button SetAmbienceButton;
        public CheckBox AmbienceSwitch;
        public ComboBox<String> AmbienceTypeComboBox;
        public Label StatusBar;
        private List<SessionPart> alladjustedsessionitems;
        private SessionPart selectedsessionpart;
        private ObservableList<SessionItem> tableitems = FXCollections.observableArrayList();
        private final ObservableList<String> ambiencetypes = FXCollections.observableArrayList("Repeat", "Shuffle", "Custom");
        private boolean result;

        public SessionPlaybackOverview() {
            try {
                alladjustedsessionitems = itemsinsession;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionPlaybackOverview.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                setResizable(false);
                setOnCloseRequest(event -> {});
                setTitle("Session Playback Overview");
                NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
                NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
                DurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
                DurationColumn.setCellFactory(new Callback<TableColumn<SessionItem, String>, TableCell<SessionItem, String>>() {
                    @Override
                    public TableCell<SessionItem, String> call(TableColumn<SessionItem, String> param) {
                        return new TableCell<SessionItem, String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (! isEmpty()) {
                                    if (item.equals("No Duration Set")) {setTextFill(Color.RED);}
                                    setText(item);
                                }
                            }
                        };
                    }
                });
                AmbienceColumn.setCellValueFactory(cellData -> cellData.getValue().ambiencesummary);
                AmbienceColumn.setCellFactory(new Callback<TableColumn<SessionItem, String>, TableCell<SessionItem, String>>() {
                    @Override
                    public TableCell<SessionItem, String> call(TableColumn<SessionItem, String> param) {
                        return new TableCell<SessionItem, String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (! isEmpty()) {
                                    if (item.equals("Ambience Not Set") || item.equals("Has No Ambience")) {
                                        System.out.println("Should Be Red");
                                        setTextFill(Color.RED);}
                                    setText(item);

                                }
                            }
                        };
                    }
                });
                GoalColumn.setCellValueFactory(cellData -> cellData.getValue().goalsummary);
                GoalColumn.setCellFactory(new Callback<TableColumn<SessionItem, String>, TableCell<SessionItem, String>>() {
                    @Override
                    public TableCell<SessionItem, String> call(TableColumn<SessionItem, String> param) {
                        return new TableCell<SessionItem, String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (! isEmpty()) {
                                    if (item.equals("No Goal Set")) {this.setTextFill(Color.YELLOW);}
                                    setText(item);
                                }
                            }
                        };
                    }
                });
                SessionItemsTable.setOnMouseClicked(event -> itemselected());
                tableitems = FXCollections.observableArrayList();
                AmbienceTypeComboBox.setItems(ambiencetypes);
                AmbienceSwitch.setSelected(false);
                if (Root.getOptions().getSessionOptions().getAmbiencePlaybackType() != null) {
                    switch (Root.getOptions().getSessionOptions().getAmbiencePlaybackType()) {
                        case REPEAT: AmbienceTypeComboBox.getSelectionModel().select(0); break;
                        case SHUFFLE: AmbienceTypeComboBox.getSelectionModel().select(1); break;
                        case CUSTOM: AmbienceTypeComboBox.getSelectionModel().select(2); break;
                    }
                }
                AmbienceTypeComboBox.setDisable(true);
                populatetable();
            } catch (IOException ignored) {}
        }

        // Table Methods
        public void itemselected() {
            int index = SessionItemsTable.getSelectionModel().getSelectedIndex();
            if (index != -1) {selectedsessionpart = alladjustedsessionitems.get(index);}
            syncbuttons();
        }
        public void populatetable() {
            SessionItemsTable.getItems().clear();
            tableitems.clear();
            if (alladjustedsessionitems == null) {alladjustedsessionitems = new ArrayList<>();}
            int count = 1;
            List<SessionPart> newsessionitems = new ArrayList<>();
            for (SessionPart x : AllSessionParts) {
                if ((alladjustedsessionitems.contains(x)) || (!getwellformedcuts().isEmpty() && x instanceof Cut && (!alladjustedsessionitems.contains(x) && getwellformedcuts().contains(x)))) {
                    tableitems.add(new SessionItem(count, x.name, x.getdurationasString(true, 150.0), getambiencetext(x), x.goals_getCurrentAsString(true, 150.0)));
                    newsessionitems.add(x);
                    count++;
                }
            }
            alladjustedsessionitems = newsessionitems;
            SessionItemsTable.setItems(tableitems);
            syncbuttons();
            calculatetotalduration();
        }
        public void syncbuttons() {
            int index = SessionItemsTable.getSelectionModel().getSelectedIndex();
            boolean itemselected = index != -1;
            UpButton.setDisable(index < 1);
            DownButton.setDisable(! itemselected && index != SessionItemsTable.getItems().size() - 1);
            AdjustDurationButton.setDisable(selectedsessionpart == null);
            if (selectedsessionpart != null) {
                SetGoalButton.setDisable(selectedsessionpart.goals_ui_currentgoalisset());
                SetAmbienceButton.setDisable(ambiencePlaybackType == null || ambiencePlaybackType != AmbiencePlaybackType.CUSTOM);
            }
        }

        // Order/Sort Session Parts
        public void moveitemup(ActionEvent actionEvent) {
            int selectedindex = SessionItemsTable.getSelectionModel().getSelectedIndex();
            if (selectedindex == -1) {return;}
            if (tableitems.get(selectedindex).name.get().equals("Presession") || tableitems.get(selectedindex).name.get().equals("Postsession")) {
                new InformationDialog(options, "Information", "Cannot Move", "Presession And Postsession Cannot Be Moved");
                return;
            }
            if (selectedindex == 0) {return;}
            SessionPart selecteditem = alladjustedsessionitems.get(selectedindex);
            SessionPart oneitemup = alladjustedsessionitems.get(selectedindex - 1);
            if (selecteditem instanceof Cut && oneitemup instanceof Cut) {
                if (selecteditem.number > oneitemup.number) {
                    new InformationDialog(options, "Cannot Move", selecteditem.name + " Cannot Be Moved Before " + oneitemup.name + ". Cuts Would Be Out Of Order", "Cannot Move");
                    return;
                }
            }
            if (oneitemup instanceof Qi_Gong) {
                new InformationDialog(options, "Cannot Move", "Cannot Replace Presession", "Cannot Move");
                return;
            }
            Collections.swap(alladjustedsessionitems, selectedindex, selectedindex - 1);
            populatetable();
        }
        public void moveitemdown(ActionEvent actionEvent) {
            int selectedindex = SessionItemsTable.getSelectionModel().getSelectedIndex();
            if (selectedindex == -1) {return;}
            if (tableitems.get(selectedindex).name.get().equals("Presession") || tableitems.get(selectedindex).name.get().equals("Postsession")) {
                new InformationDialog(options, "Information", "Cannot Move", tableitems.get(selectedindex).name + " Cannot Be Moved");
                return;
            }
            if (selectedindex == tableitems.size() - 1) {return;}
            SessionPart selecteditem = alladjustedsessionitems.get(selectedindex);
            SessionPart oneitemdown = alladjustedsessionitems.get(selectedindex + 1);
            if (selecteditem instanceof Cut && oneitemdown instanceof Cut) {
                if (selecteditem.number < oneitemdown.number) {
                    new InformationDialog(options, "Cannot Move", selecteditem.name + " Cannot Be Moved After " + oneitemdown.name + ". Cuts Would Be Out Of Order", "Cannot Move");
                    return;
                }
            }
            if (oneitemdown instanceof Qi_Gong) {
                new InformationDialog(options, "Cannot Move", "Cannot Replace Postsession", "Cannot Move");
                return;
            }
            Collections.swap(alladjustedsessionitems, selectedindex, selectedindex + 1);
            populatetable();
        }

        // Session Parts Missing / Out Of Order
        public List<Cut> getwellformedcuts() {
            List<Cut> wellformedcuts = new ArrayList<>();
            for (int i=0; i<getlastworkingcutindex(); i++) {
                wellformedcuts.add(Root.getAllCuts().get(i));
            }
            return wellformedcuts;
        }
        public int getlastworkingcutindex() {
            int lastcutindex = 0;
            for (SessionPart i : itemsinsession) {
                if (i instanceof Cut && i.getduration().greaterThan(Duration.ZERO)) {lastcutindex = i.number;}
            }
            return lastcutindex;
        }

        //  Duration Methods
        public void adjustduration(ActionEvent actionEvent) {
            if (selectedsessionpart != null) {
                SessionPlaybackOverview_ChangeDuration changedurationdialog = new SessionPlaybackOverview_ChangeDuration(selectedsessionpart);
                changedurationdialog.showAndWait();
                switch (changedurationdialog.result) {
                    case DURATION:
                        selectedsessionpart.changevalue((int) changedurationdialog.getDuration().toMinutes());
                        break;
                    case RAMP:
                        selectedsessionpart.setRamponly(true);
                        break;
                    case CANCEL:
                        break;
                }
                populatetable();
            }
        }
        public void calculatetotalduration() {
            Duration duration = Duration.ZERO;
            for (SessionPart i : alladjustedsessionitems) {
                duration = duration.add(i.getduration());
            }
            TotalSessionTime.setText(Util.formatdurationtoStringSpelledOut(duration, TotalSessionTime.getLayoutBounds().getWidth()));
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MILLISECOND, new Double(duration.toMillis()).intValue());
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            CompletionTime.setText(sdf.format(cal.getTime()));
        }

        // Dialog Methods
        public boolean getResult() {
            return result;
        }
        public void playsession(ActionEvent actionEvent) {
            List<Integer> indexesmissingduration = new ArrayList<>();
            List<Integer>  indexesmissinggoals = new ArrayList<>();
            for (SessionPart i : alladjustedsessionitems) {
                if (i.getduration() == Duration.ZERO && ! i.ramponly) {indexesmissingduration.add(alladjustedsessionitems.indexOf(i));}
                if (! i.goals_ui_currentgoalisset()) {indexesmissinggoals.add(alladjustedsessionitems.indexOf(i));}
            }
            // Add Ramp Option For Missing Durations
            if (! indexesmissingduration.isEmpty()) {
                if (new ConfirmationDialog(options, "Confirmation", indexesmissingduration.size() + " Session Parts Are Missing Durations", "Set Ramp Only For The Parts Missing Durations",
                        "Set Ramp Only", "Cancel Playback").getResult()) {
                    for (int x : indexesmissingduration) {alladjustedsessionitems.get(x).setRamponly(true);}
                } else {return;}
            }
            // Check Goals
            if (! indexesmissinggoals.isEmpty()) {
                if (! new ConfirmationDialog(options, "Confirmation", indexesmissinggoals.size() + " Session Parts Are Missing Goals", "Continue Playing Session Without Goals?",
                        "Yes", "No").getResult()) {return;}
            }
            // Check Alert File Needed/Not Needed
            boolean longsession = false;
            for (SessionPart i : alladjustedsessionitems) {
                if (i.getduration().greaterThanOrEqualTo(Duration.minutes(kujiin.xml.Options.DEFAULT_LONG_SESSIONPART_DURATION))) {
                    longsession = true;
                    break;
                }
            }
            if (longsession && ! Root.getOptions().getSessionOptions().getAlertfunction()) {
                switch (new AnswerDialog(options, "Add Alert File", null, "I've Detected A Long Session. Add Alert File In Between Session Parts?",
                        "Add Alert File", "Continue Without Alert File", "Cancel Playback").getResult()) {
                    case YES: new SelectAlertFile(Root).showAndWait(); break;
                    case CANCEL: return;
                }
            } else if (Root.getOptions().getSessionOptions().getAlertfunction()) {
                switch (new AnswerDialog(options, "Disable Alert File", null, "I've Detected A Relatively Short Session With Alert File Enabled",
                        "Disable Alert File", "Leave Alert File Enabled", "Cancel Playback").getResult()) {
                    case YES: Root.getOptions().getSessionOptions().setAlertfunction(false); break;
                    case CANCEL: return;
                }
            }
            // Accepted
            result = true;
            itemsinsession = alladjustedsessionitems;
            close();
        }
        public void cancel(ActionEvent actionEvent) {
            alladjustedsessionitems = null;
            close();
        }

        // Goal Methods
        public void setgoal(ActionEvent actionEvent) {
            if (selectedsessionpart != null) {Root.getProgressTracker().setnewgoal(selectedsessionpart);}
            populatetable();
        }

        // Ambience Methods
        public void ambienceswitchtoggled(ActionEvent actionEvent) {
            checkambience();
            ambienceenabled = AmbienceSwitch.isSelected();
            AmbienceTypeComboBox.setDisable(! AmbienceSwitch.isSelected());
            populatetable();
        }
        public String getambiencetext(SessionPart sessionPart) {
            if (! isAmbienceenabled()) {return "Disabled";}
            else {
                if (! sessionPart.getAmbience().hasAnyAmbience()) {return "Has No Ambience";}
                switch (ambiencePlaybackType) {
                    case REPEAT:
                        return "Will Repeat";
                    case SHUFFLE:
                        return "Will Shuffle";
                    case CUSTOM:
                        if (sessionPart.getAmbience().getCustomAmbience() == null || ! sessionPart.getAmbience().getCustomAmbience().isEmpty()) {return "Ambience Not Set";}
                        else {return "Custom Ambience Set";}
                    default:
                        return null;
                }
            }
        }
        public void ambiencetypechanged(ActionEvent actionEvent) {
            int index = AmbienceTypeComboBox.getSelectionModel().getSelectedIndex();
            switch (index) {
                case 0: ambiencePlaybackType = AmbiencePlaybackType.REPEAT; break;
                case 1: ambiencePlaybackType = AmbiencePlaybackType.SHUFFLE; break;
                case 2: ambiencePlaybackType = AmbiencePlaybackType.CUSTOM; break;
            }
            populatetable();
        }
        public void setambience(ActionEvent actionEvent) {
            SessionPlaybackOverview_AddCustomAmbience addCustomAmbience = new SessionPlaybackOverview_AddCustomAmbience(selectedsessionpart);
            addCustomAmbience.showAndWait();
            if (addCustomAmbience.getResult()) {
                List<SoundFile> customambiencelist = addCustomAmbience.getCustomAmbienceList();
                selectedsessionpart.getAmbience().setCustomAmbience(customambiencelist);
            }
        }
        public void checkambience() {
            if (sessionPlaybackOverview != null && sessionPlaybackOverview.isShowing() && sessionPlaybackOverview.AmbienceSwitch.isSelected()) {
                ArrayList<SessionPart> sessionpartswithnoambience = new ArrayList<>();
                ArrayList<SessionPart> sessionpartswithreducedambience = new ArrayList<>();
                AllSessionParts.stream().filter(i -> i.getduration().greaterThan(Duration.ZERO)).forEach(i -> {
                    Root.CreatorStatusBar.setText(String.format("Checking Ambience. Currently Checking %s...", i.name));
                    if (!i.getAmbience().hasAnyAmbience()) {sessionpartswithnoambience.add(i);}
                    else if (!i.getAmbience().hasEnoughAmbience(i.getduration())) {sessionpartswithreducedambience.add(i);}
                });
                Root.CreatorStatusBar.setText("");
                if (! sessionpartswithnoambience.isEmpty()) {
                    StringBuilder a = new StringBuilder();
                    for (int i = 0; i < sessionpartswithnoambience.size(); i++) {
                        a.append(sessionpartswithnoambience.get(i).name);
                        if (i != sessionpartswithnoambience.size() - 1) {a.append(", ");}
                    }
                    if (new ConfirmationDialog(options, "Missing Ambience", null, "Missing Ambience For " + a.toString() + ". Ambience Cannot Be Enabled For Session Without At Least One Working Ambience File" +
                            " Per Session Part", "Add Ambience", "Disable Ambience").getResult()) {
                        if (sessionpartswithnoambience.size() == 1) {new AmbienceEditor_Simple(Root, sessionpartswithnoambience.get(0)).showAndWait();}
                        else {new AmbienceEditor_Simple(Root).showAndWait();}
                    } else {AmbienceSwitch.setSelected(false);}
                } else if (! sessionpartswithreducedambience.isEmpty()) {
                    StringBuilder a = new StringBuilder();
                    int count = 0;
                    for (SessionPart aSessionpartswithreducedambience : sessionpartswithreducedambience) {
                        a.append("\n");
                        String formattedcurrentduration = Util.formatdurationtoStringSpelledOut(aSessionpartswithreducedambience.getAmbience().gettotalDuration(), null);
                        String formattedexpectedduration = Util.formatdurationtoStringSpelledOut(aSessionpartswithreducedambience.getduration(), null);
                        a.append(count + 1).append(". ").append(aSessionpartswithreducedambience.name).append(" >  Current: ").append(formattedcurrentduration).append(" | Needed: ").append(formattedexpectedduration);
                        count++;
                    }
                    if (ambiencePlaybackType == null) {AmbienceSwitch.setSelected(false);}
                } else {
                    AmbienceSwitch.setSelected(true);
                    ambiencePlaybackType = Root.getOptions().getSessionOptions().getAmbiencePlaybackType();
                }
            }
        }


        class SessionPlaybackOverview_ChangeDuration extends Stage {
            public TextField HoursTextField;
            public TextField MinutesTextField;
            public CheckBox RampOnlyCheckBox;
            public Button SetButton;
            public Button CancelButton;
            private Duration duration;
            private ChangeDurationType result = ChangeDurationType.CANCEL;

            public SessionPlaybackOverview_ChangeDuration(SessionPart sessionPart) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionPlaybackOverview_ChangeDuration.fxml"));
                    fxmlLoader.setController(this);
                    Scene defaultscene = new Scene(fxmlLoader.load());
                    setScene(defaultscene);
                    options.setStyle(this);
                    this.setResizable(false);
                    setTitle("Change " + sessionPart.name + " Duration");
                    HoursTextField.setText("0");
                    MinutesTextField.setText("0");
                    Util.custom_textfield_integer(HoursTextField, 0, 60, 1);
                    Util.custom_textfield_integer(MinutesTextField, 0, 59, 1);
                } catch (IOException ignored) {}
            }

            public ChangeDurationType getResult() {
                return result;
            }
            public Duration getDuration() {
                return duration;
            }
            public void setDuration(Duration duration) {
                this.duration = duration;
            }
            public void ramponlyselected(ActionEvent actionEvent) {
                HoursTextField.setDisable(RampOnlyCheckBox.isSelected());
                MinutesTextField.setDisable(RampOnlyCheckBox.isSelected());
            }
            public void OKButtonPressed(ActionEvent actionEvent) {
                try {
                    if (! RampOnlyCheckBox.isSelected()) {
                        Duration duration = Duration.hours(Double.parseDouble(HoursTextField.getText())).add(Duration.minutes(Double.parseDouble(MinutesTextField.getText())));
                        if (duration.greaterThan(Duration.ZERO)) {
                            setDuration(duration);
                            result = ChangeDurationType.DURATION;
                        } else {new InformationDialog(options, "Information", "Cannot Change Value To 0", null); return;}
                    } else {result = ChangeDurationType.RAMP;}
                    close();
                } catch (NumberFormatException ignored) {}
            }
        }
        class SessionPlaybackOverview_AddCustomAmbience extends Stage {
            public TableView<AmbienceSongWithNumber> AmbienceItemsTable;
            public TableColumn<AmbienceSongWithNumber, Integer> NumberColumn;
            public TableColumn<AmbienceSongWithNumber, String> NameColumn;
            public TableColumn<AmbienceSongWithNumber, String> DurationColumn;
            public TextField TotalDurationTextField;
            public Button AcceptButton;
            public Button CancelButton;
            public Button RemoveButton;
            public Button MoveUpButton;
            public Button MoveDownButton;
            public Button PreviewButton;
            public Button AddFilesButton;
            public Button AddAmbienceButton;
            private AmbienceSongWithNumber selectedtableitem;
            private ObservableList<AmbienceSongWithNumber> TableItems = FXCollections.observableArrayList();
            private List<SoundFile> CustomAmbienceList = new ArrayList<>();
            private SessionPart sessionPart;
            private boolean result = false;
            private boolean longenough = false;

            public SessionPlaybackOverview_AddCustomAmbience(SessionPart sessionPart) {
                try {
                    this.sessionPart = sessionPart;
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionPlaybackOverview_AddCustomAmbience.fxml"));
                    fxmlLoader.setController(this);
                    Scene defaultscene = new Scene(fxmlLoader.load());
                    setScene(defaultscene);
                    options.setStyle(this);
                    this.setResizable(false);
                    RemoveButton.setDisable(true);
                    MoveUpButton.setDisable(true);
                    MoveDownButton.setDisable(true);
                    setTitle("Set Custom Ambience");
                    NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
                    NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
                    DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
                    AmbienceItemsTable.getSelectionModel().selectedItemProperty().addListener(
                            (observable, oldValue, newValue) -> tableselectionchanged(newValue));
                    calculatetotal();
                } catch (IOException ignored) {}
            }

            private void tableselectionchanged(AmbienceSongWithNumber newValue) {
                int index = AmbienceItemsTable.getSelectionModel().getSelectedIndex();
                if (index != -1) {selectedtableitem = AmbienceItemsTable.getItems().get(index);}
                else {selectedtableitem = null;}
                RemoveButton.setDisable(index == -1);
                MoveUpButton.setDisable(index == -1 || index == 0);
                MoveDownButton.setDisable(index == -1 || index == AmbienceItemsTable.getItems().size() - 1);
                PreviewButton.setDisable(index == -1);
            }
            public boolean ambiencealreadyadded(File file) {
                if (CustomAmbienceList != null) {
                    for (SoundFile i : CustomAmbienceList) {if (file.equals(i.getFile())) {return true;}}
                }
                return false;
            }
            public void addfiles(ActionEvent actionEvent) {
                List<File> filesselected = new FileChooser().showOpenMultipleDialog(null);
                if (filesselected == null || filesselected.isEmpty()) {return;}
                if (Util.list_hasduplicates(filesselected)) {
                    if (! new ConfirmationDialog(options, "Confirmation", "Duplicate Files Detected", "Include Duplicate Files?", "Include", "Discard").getResult()) {
                        filesselected = Util.list_removeduplicates(filesselected);
                    }
                }
                for (File i : filesselected) {
                    if (ambiencealreadyadded(i)) {continue;}
                    if (Util.audio_isValid(i)) {
                        MediaPlayer calculatedurationplayer = new MediaPlayer(new Media(i.toURI().toString()));
                        List<File> finalFilesselected = filesselected;
                        calculatedurationplayer.setOnReady(() -> {
                            SoundFile x = new SoundFile(i);
                            x.setDuration(calculatedurationplayer.getTotalDuration().toMillis());
                            CustomAmbienceList.add(x);
                            TableItems.add(new AmbienceSongWithNumber(finalFilesselected.indexOf(i), x));
                            orderambience();
                            AmbienceItemsTable.setItems(TableItems);
                            calculatetotal();
                            calculatedurationplayer.dispose();
                        });
                    }
                }
            }
            public void addambience(ActionEvent actionEvent) {
                SessionPlaybackOverview_SelectAmbience selectAmbience = new SessionPlaybackOverview_SelectAmbience(sessionPart);
                selectAmbience.showAndWait();
                if (selectAmbience.getResult()) {
                    TableItems.addAll(selectAmbience.getSoundfiles());
                    orderambience();
                    AmbienceItemsTable.setItems(TableItems);
                    calculatetotal();
                }
            }
            public void orderambience() {
                int count = 1;
                for (AmbienceSongWithNumber i : TableItems) {
                    i.setNumber(count);
                    count++;
                }
            }
            public void removeambience(ActionEvent actionEvent) {
                if (selectedtableitem != null) {
                    if (new ConfirmationDialog(options, "Remove Ambience", "Really Remove '" + selectedtableitem.getName() + "'?", "", "Remove", "Cancel").getResult()) {
                        int index = TableItems.indexOf(selectedtableitem);
                        TableItems.remove(index);
                        CustomAmbienceList.remove(index);
                        orderambience();
                        AmbienceItemsTable.setItems(TableItems);
                        calculatetotal();
                    }
                }
            }

            public Duration getcurrenttotal() {
                Duration duration = Duration.ZERO;
                for (SoundFile i : CustomAmbienceList) {
                    duration = duration.add(Duration.millis(i.getDuration()));
                }
                return duration;
            }
            public void calculatetotal() {
                if (getcurrenttotal().lessThan(sessionPart.getduration())) {
                    Duration timeleft = sessionPart.getduration().subtract(getcurrenttotal());
                    TotalDurationTextField.setStyle("-fx-text-fill: red;");
                    String str = "Ambience Is Not Long Enough (" +
                            Util.formatdurationtoStringSpelledOut(timeleft, TotalDurationTextField.getLayoutBounds().getWidth()) +
                            " Left)";
                    TotalDurationTextField.setText(str);
                    longenough = false;
                }
                else {
                    TotalDurationTextField.setStyle("-fx-text-fill: white;");
                    TotalDurationTextField.setText("Ambience Is Long Enough");
                    longenough = true;
                }
            }
            public void moveupintable(ActionEvent actionEvent) {
                int selectedindex = AmbienceItemsTable.getSelectionModel().getSelectedIndex();
                if (selectedindex > 0) {
                    Collections.swap(TableItems, selectedindex, selectedindex - 1);
                    Collections.swap(CustomAmbienceList, selectedindex, selectedindex - 1);
                    AmbienceItemsTable.setItems(TableItems);
                    calculatetotal();
                }
            }
            public void movedownintable(ActionEvent actionEvent) {
                int selectedindex = AmbienceItemsTable.getSelectionModel().getSelectedIndex();
                if (selectedindex != -1 && selectedindex != TableItems.size() - 1) {
                    Collections.swap(TableItems, selectedindex, selectedindex + 1);
                    Collections.swap(CustomAmbienceList, selectedindex, selectedindex + 1);
                    AmbienceItemsTable.setItems(TableItems);
                    calculatetotal();
                }
            }
            public void preview(ActionEvent actionEvent) {
                if (selectedtableitem != null) {
                    PreviewFile previewFile = new PreviewFile(selectedtableitem.getFile(), Root);
                    previewFile.showAndWait();
                }
            }

            public List<SoundFile> getCustomAmbienceList() {
                return CustomAmbienceList;
            }
            public boolean getResult() {
                return result;
            }

            public void accept(ActionEvent actionEvent) {
                if (getcurrenttotal().lessThan(sessionPart.getduration())) {
                    new InformationDialog(options, "Ambience Too Short", "Need At Least " + sessionPart.getdurationasString(false, 50) + " To Set This As Custom Ambience", "");
                } else {result = true; close();}
            }
        }
        class SessionPlaybackOverview_SelectAmbience extends Stage {
            public Label TopLabel;
            public TableView<AmbienceSong> AmbienceTable;
            public TableColumn<AmbienceSong, String> NameColumn;
            public TableColumn<AmbienceSong, String>  DurationColumn;
            public Button PreviewButton;
            public Button AddButton;
            public Button CancelButton;
            private SessionPart sessionPart;
            private boolean result = false;
            private List<AmbienceSongWithNumber> soundfiles = new ArrayList<>();
            private ObservableList<AmbienceSong> ambienceSongs = FXCollections.observableArrayList();

            public SessionPlaybackOverview_SelectAmbience(SessionPart SessionPart) {
                try {
                    sessionPart = SessionPart;
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SelectAmbience.fxml"));
                    fxmlLoader.setController(this);
                    Scene defaultscene = new Scene(fxmlLoader.load());
                    setScene(defaultscene);
                    options.setStyle(this);
                    setResizable(false);
                    setTitle("Set Custom Ambience");
                    PreviewButton.setDisable(true);
                    AddButton.setDisable(true);
                    NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
                    DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
                    AmbienceTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> tableselectionchanged(newValue));
                    AmbienceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                    ambienceSongs.addAll(SessionPart.getAmbience().getAmbience().stream().map(AmbienceSong::new).collect(Collectors.toList()));
                    AmbienceTable.setItems(ambienceSongs);
                } catch (IOException ignored) {}
            }

            private void tableselectionchanged(AmbienceSong ambienceSong) {
                AddButton.setDisable(AmbienceTable.getSelectionModel().getSelectedItems().isEmpty());
                PreviewButton.setDisable(AmbienceTable.getSelectionModel().getSelectedItems().size() != 1);
            }
            public void preview(ActionEvent actionEvent) {
                if (! PreviewButton.isDisabled()) {
                    File file = ambienceSongs.get(AmbienceTable.getSelectionModel().getSelectedIndex()).getFile();
                    new PreviewFile(file, Root).showAndWait();
                }
            }
            public void addfiles(ActionEvent actionEvent) {
                soundfiles.addAll(AmbienceTable.getSelectionModel().getSelectedItems().stream().map(i -> new AmbienceSongWithNumber(0, i)).collect(Collectors.toList()));
                result = true;
                close();
            }
            public boolean getResult() {
                return result;
            }
            public List<AmbienceSongWithNumber> getSoundfiles() {
                return soundfiles;
            }
        }
    }
    public class DisplayReference extends Stage {
        public ScrollPane ContentPane;
        public Slider EntrainmentVolumeSlider;
        public Label EntrainmentVolumePercentage;
        public Slider AmbienceVolumeSlider;
        public Label AmbienceVolumePercentage;
        public Button PlayButton;
        public Button PauseButton;
        public Button StopButton;
        public ProgressBar TotalProgress;
        public ProgressBar CurrentProgress;
        public Label CurrentName;
        public Label CurrentPercentage;
        public Label TotalPercentage;
        private Boolean fullscreenoption;
        private Scene scene;
        private ReferenceType referenceType;

        public DisplayReference(SessionPart sessionPart) {
            try {
                referenceType = options.getSessionOptions().getReferencetype();
                fullscreenoption = options.getSessionOptions().getReferencefullscreen();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferenceDisplay.fxml"));
                fxmlLoader.setController(this);
                scene = new Scene(fxmlLoader.load());
                setScene(scene);
                options.setStyle(this);
//                this.setResizable(false);
                setTitle(sessionPart.name + "'s Reference");
                setsizing();
                loadcontent(sessionPart.reference_getFile());
                AmbienceVolumeSlider.setValue(getPlayer().AmbienceVolume.getValue());
                AmbienceVolumePercentage.setText(getPlayer().AmbienceVolumePercentage.getText());
                EntrainmentVolumeSlider.setValue(getPlayer().EntrainmentVolume.getValue());
                EntrainmentVolumePercentage.setText(getPlayer().EntrainmentVolumePercentage.getText());
                setOnCloseRequest(event -> untoggleplayerreference());
                if (itemsinsession.indexOf(sessionPart) == 0) {
                    setFullScreenExitHint("Press F11 To Toggle Fullscreen, ESC To Hide Reference");
                } else {setFullScreenExitHint("");}
                addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    switch (event.getCode()) {
                        case ESCAPE:
//                                hide();
//                                untoggleplayerreference();
//                                break;
                        case F11:
                            if (playerState == PlayerState.PLAYING) {
                                boolean fullscreen = this.isFullScreen();
                                fullscreenoption = !fullscreen;
                                Root.getOptions().getSessionOptions().setReferencefullscreen(fullscreenoption);
                                setsizing();
                                if (!fullscreen) {setFullScreenExitHint("");}
                                break;
                            }
                    }
                });
            } catch (IOException ignored) {}
        }
        public DisplayReference(String htmlcontent) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ReferencePreview.fxml"));
            fxmlLoader.setController(this);
            try {
                scene = new Scene(fxmlLoader.load());
                setScene(scene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                setTitle("Reference File Preview");
                fullscreenoption = false;
                setsizing();
                WebView browser = new WebView();
                WebEngine webEngine = browser.getEngine();
                webEngine.setUserStyleSheetLocation(new File(kujiin.xml.Options.DIRECTORYSTYLES, "referencefile.css").toURI().toString());
                webEngine.loadContent(htmlcontent);
                ContentPane.setContent(browser);
            } catch (IOException ignored) {}
        }

        public void setsizing() {
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            double height = primaryScreenBounds.getHeight();
            double width = primaryScreenBounds.getWidth();
            if (! fullscreenoption) {height -= 100; width -= 100;}
            this.setFullScreen(fullscreenoption);
            this.setHeight(height);
            this.setWidth(width);
            this.centerOnScreen();
            ContentPane.setFitToWidth(true);
            ContentPane.setFitToHeight(true);
            ContentPane.setStyle("-fx-background-color: #212526");
        }
        public void loadcontent(File referencefile) {
            if (referencefile != null) {
                switch (referenceType) {
                    case txt:
                        StringBuilder sb = new StringBuilder();
                        try (FileInputStream fis = new FileInputStream(referencefile);
                             BufferedInputStream bis = new BufferedInputStream(fis)) {
                            while (bis.available() > 0) {
                                sb.append((char) bis.read());
                            }
                        } catch (Exception ignored) {}
                        TextArea ta = new TextArea();
                        ta.setText(sb.toString());
                        ta.setWrapText(true);
                        ContentPane.setContent(ta);
                        Root.getOptions().setStyle(this);
                        break;
                    case html:
                        WebView browser = new WebView();
                        WebEngine webEngine = browser.getEngine();
                        webEngine.load(referencefile.toURI().toString());
                        webEngine.setUserStyleSheetLocation(Options.REFERENCE_THEMEFILE.toURI().toString());
                        ContentPane.setContent(browser);
                        break;
                    default:
                        break;
                }
            } else {System.out.println("Reference File Is Null");}
        }
        public void untoggleplayerreference() {
            getPlayer().ReferenceCheckBox.setSelected(false);
            getPlayer().togglereference(null);
        }

        public void play(ActionEvent actionEvent) {getPlayer().play();}
        public void pause(ActionEvent actionEvent) {getPlayer().pause();}
        public void stop(ActionEvent actionEvent) {getPlayer().stop();}

    }
    public class Exporter extends Stage {
        public Button CancelButton;
        public ProgressBar TotalProgress;
        public Label StatusBar;
        public ProgressBar CurrentProgress;
        public Label TotalLabel;
        public Label CurrentLabel;
        private File finalexportfile;
        private File tempentrainmenttextfile;
        private File tempambiencetextfile;
        private File tempentrainmentfile;
        private File tempambiencefile;
        private File finalentrainmentfile;
        private File finalambiencefile;

        public Exporter() {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ExportingSessionDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                options.setStyle(this);
                this.setResizable(false);
            } catch (IOException ignored) {}
            setTitle("Exporting Session");
        }

        public void unbindproperties() {
            TotalProgress.progressProperty().unbind();
            CurrentProgress.progressProperty().unbind();
            StatusBar.textProperty().unbind();
            CurrentLabel.textProperty().unbind();
        }

    }
    public class Player extends Stage {
        public Button PlayButton;
        public Button PauseButton;
        public Button StopButton;
        public Slider EntrainmentVolume;
        public Label EntrainmentVolumePercentage;
        public Slider AmbienceVolume;
        public Label AmbienceVolumePercentage;
        public Label CurrentSessionPartTopLabel;
        public Label SessionPartCurrentTimeLabel;
        public ProgressBar CurrentSessionPartProgress;
        public Label SessionPartTotalTimeLabel;
        public Label TotalCurrentTimeLabel;
        public ProgressBar TotalProgress;
        public Label TotalTotalTimeLabel;
        public Label TotalSessionLabel;
        public Label GoalTopLabel;
        public ProgressBar GoalProgressBar;
        public Label GoalPercentageLabel;
        public Boolean displaynormaltime = true;
        public CheckBox ReferenceCheckBox;
        public ComboBox<String> ReferenceTypeComboBox;
        private Timeline player_updateuitimeline;
        // Playback Fields
        private Double currententrainmentvolume;
        private Double currentambiencevolume;
        public SessionPart currentsessionpart;
        public Duration totalsessiondurationelapsed;
        public Duration totalsessionduration;
        public int sessionpartcount;
        public List<SessionPart> sessionpartswithGoalsCompletedThisSession;


        public Player() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionPlayerDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                options.setStyle(this);
                setTitle("Session Player");
                reset(false);
                boolean referenceoption = Root.getOptions().getSessionOptions().getReferenceoption();
                if (referenceoption && referenceType != null) {ReferenceCheckBox.setSelected(true);}
                else {ReferenceCheckBox.setSelected(false);}
                togglereference(null);
                ReferenceCheckBox.setSelected(Root.getOptions().getSessionOptions().getReferenceoption());
                setResizable(false);
                SessionPartTotalTimeLabel.setOnMouseClicked(event -> displaynormaltime = ! displaynormaltime);
                TotalTotalTimeLabel.setOnMouseClicked(event -> displaynormaltime = ! displaynormaltime);
                ObservableList<String> referencetypes = FXCollections.observableArrayList();
                for (ReferenceType i : ReferenceType.values()) {referencetypes.add(i.toString());}
                referencetypes.add("Help");
                ReferenceTypeComboBox.setItems(referencetypes);
                ReferenceTypeComboBox.setOnAction(event -> {
                    int index = ReferenceTypeComboBox.getSelectionModel().getSelectedIndex();
                    if (index == 0) {Root.getOptions().getSessionOptions().setReferencetype(ReferenceType.html);}
                    else if (index == 1) {Root.getOptions().getSessionOptions().setReferencetype(ReferenceType.txt);}
                });
                if (referenceType != null) {
                    switch (referenceType) {
                        case html:
                            ReferenceTypeComboBox.getSelectionModel().select(0);
                            break;
                        case txt:
                            ReferenceTypeComboBox.getSelectionModel().select(1);
                            break;
                    }
                }
                setOnCloseRequest(event -> {
                    if (playerState == PlayerState.PLAYING || playerState == PlayerState.STOPPED || playerState == PlayerState.PAUSED || playerState == PlayerState.IDLE) {
                        if (endsessionprematurely()) {close();} else {play(); event.consume();}
                    } else {
//                        Util.gui_showtimedmessageonlabel(StatusBar, "Cannot Close Player During Fade Animation", 400);
                        new Timeline(new KeyFrame(Duration.millis(400), ae -> currentsessionpart.toggleplayerbuttons()));
                        event.consume();
                    }
                });
            } catch (IOException ignored) {}
        }

    // Getters And Setters
        public Double getCurrententrainmentvolume() {
            return currententrainmentvolume;
        }
        public Double getCurrentambiencevolume() {
            return currentambiencevolume;
        }
        public void setCurrententrainmentvolume(Double currententrainmentvolume) {
            this.currententrainmentvolume = currententrainmentvolume;
        }
        public void setCurrentambiencevolume(Double currentambiencevolume) {
            this.currentambiencevolume = currentambiencevolume;
        }

    // Playback
        public void play() {
            switch (playerState) {
                case IDLE:
                case STOPPED:
                    sessionpartswithGoalsCompletedThisSession = new ArrayList<>();
                    totalsessiondurationelapsed = Duration.ZERO;
                    totalsessionduration = Duration.ZERO;
                    for (SessionPart i : itemsinsession) {totalsessionduration = totalsessionduration.add(i.getduration());}
                    TotalTotalTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessionduration));
                    player_updateuitimeline = new Timeline(new KeyFrame(Duration.millis(100), ae -> updateui()));
                    player_updateuitimeline.setCycleCount(Animation.INDEFINITE);
                    player_updateuitimeline.play();
                    sessionpartcount = 0;
                    currentsessionpart = itemsinsession.get(sessionpartcount);
                    Root.getProgressTracker().getSessions().createnew();
                    currententrainmentvolume = Root.getOptions().getSessionOptions().getEntrainmentvolume();
                    currentambiencevolume = Root.getOptions().getSessionOptions().getAmbiencevolume();
                    currentsessionpart.start();
                    displayreferencefile();
                    break;
                case PAUSED:
                    updateuitimeline.play();
                    currentsessionpart.resume();
                    break;
            }
        }
        public void pause() {
            if (playerState == PlayerState.PLAYING) {
                currentsessionpart.pause();
                updateuitimeline.pause();
            }
        }
        public void stop() {
            try {
                currentsessionpart.stop();
                updateuitimeline.stop();
            } catch (NullPointerException ignored) {}
            reset(false);
        }
        public void updateui() {
            try {
                totalsessiondurationelapsed = totalsessiondurationelapsed.add(Duration.millis(100));
                try {
                    currentsessionpart.elapsedtime = currentsessionpart.elapsedtime.add(Duration.millis(100));} catch (NullPointerException ignored) {}
                Float currentprogress;
                Float totalprogress;
                try {
                    if (currentsessionpart.elapsedtime.greaterThan(Duration.ZERO)) {currentprogress = (float) currentsessionpart.elapsedtime.toMillis() / (float) currentsessionpart.getduration().toMillis();}
                    else {currentprogress = (float) 0;}
                } catch (NullPointerException ignored) {currentprogress = (float) 0;}
                if (totalsessiondurationelapsed.greaterThan(Duration.ZERO)) {
                    totalprogress = (float) totalsessiondurationelapsed.toMillis()
                            / (float) totalsessionduration.toMillis();}
                else {totalprogress = (float) 0.0;}
                CurrentSessionPartProgress.setProgress(currentprogress);
                TotalProgress.setProgress(totalprogress);
                currentprogress *= 100;
                totalprogress *= 100;
                CurrentSessionPartTopLabel.setText(String.format("%s (%d", currentsessionpart.name, currentprogress.intValue()) + "%)");
                TotalSessionLabel.setText(String.format("Session (%d", totalprogress.intValue()) + "%)");
                try {SessionPartCurrentTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(currentsessionpart.elapsedtime));}
                catch (NullPointerException ignored) {SessionPartCurrentTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(Duration.ZERO));}
                TotalCurrentTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessiondurationelapsed));
                boolean displaynormaltime = false;
                if (displaynormaltime) {SessionPartTotalTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(currentsessionpart.getduration()));}
                else {SessionPartTotalTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(currentsessionpart.getduration().subtract(currentsessionpart.elapsedtime)));}
                if (displaynormaltime) {TotalTotalTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessionduration));}
                else {TotalTotalTimeLabel.setText(Util.formatdurationtoStringDecimalWithColons(totalsessionduration.subtract(totalsessiondurationelapsed)));}
                try {
                    if (displayReference != null && displayReference.isShowing()) {
                        displayReference.CurrentProgress.setProgress(currentprogress / 100);
                        displayReference.CurrentPercentage.setText(currentprogress.intValue() + "%");
                        displayReference.TotalProgress.setProgress(totalprogress / 100);
                        displayReference.TotalPercentage.setText(totalprogress.intValue() + "%");
                        displayReference.CurrentName.setText(currentsessionpart.name);
                    }
                } catch (NullPointerException ignored) {}
                progressTracker.updateui_goals(this);
                progressTracker.updateui_sessions();
                currentsessionpart.tick();
            } catch (Exception ignored) {}
        }
        public void progresstonextsessionpart() {
            try {
                switch (playerState) {
                    case TRANSITIONING:
                        try {
                            currentsessionpart.goals_transitioncheck();
                            currentsessionpart.cleanupPlayersandAnimations();
                            sessionpartcount++;
                            currentsessionpart = itemsinsession.get(sessionpartcount);
                            currentsessionpart.start();
                        } catch (IndexOutOfBoundsException ignored) {
                            playerState = PlayerState.IDLE;
                            currentsessionpart.cleanupPlayersandAnimations();
                            endofsession();
                        }
                        break;
                    case PLAYING:
                        closereferencefile();
                        transition();
                        break;
                }
            } catch (Exception ignored) {}
        }
        public void transition() {
            kujiin.xml.Session currentsession =  Root.getProgressTracker().getSessions().getspecificsession( Root.getProgressTracker().getSessions().totalsessioncount() - 1);
            currentsession.updatesessionpartduration(currentsessionpart.number, new Double(currentsessionpart.getduration().toMinutes()).intValue());
            Root.getProgressTracker().getSessions().marshall();
            progressTracker.updateui_goals(this);
            currentsessionpart.stop();
            if (Root.getOptions().getSessionOptions().getAlertfunction()) {
                Media alertmedia = new Media(Root.getOptions().getSessionOptions().getAlertfilelocation());
                MediaPlayer alertplayer = new MediaPlayer(alertmedia);
                alertplayer.play();
                playerState = TRANSITIONING;
                alertplayer.setOnEndOfMedia(() -> {
                    alertplayer.stop();
                    alertplayer.dispose();
                    progresstonextsessionpart();
                });
//                alertplayer.setOnError(() -> {
//                    if (new ConfirmationDialog(options, "Alert File Playback Error", null, "An Error Occured While Playing The Alert File.",
//                            "Retry", "Skip")) {
//                        alertplayer.stop();
//                        alertplayer.play();
//                    } else {
//                        alertplayer.stop();
//                        alertplayer.dispose();
//                        player_progresstonextsessionpart();
//                    }
//                });
            } else {
                playerState = TRANSITIONING;
                progresstonextsessionpart();
            }
        }
        public void reset(boolean endofsession) {
            SessionPartCurrentTimeLabel.setText("--:--");
            CurrentSessionPartProgress.setProgress(0.0);
            SessionPartTotalTimeLabel.setText("--:--");
            TotalCurrentTimeLabel.setText("--:--");
            TotalProgress.setProgress(0.0);
            TotalTotalTimeLabel.setText("--:--");
            EntrainmentVolume.setDisable(true);
            EntrainmentVolume.setValue(0.0);
            EntrainmentVolumePercentage.setText("0%");
            AmbienceVolume.setDisable(true);
            AmbienceVolume.setValue(0.0);
            AmbienceVolumePercentage.setText("0%");
            // TODO Reset Goal UI Here
            if (endofsession) {PlayButton.setText("Replay");}
            else {PlayButton.setText("Start");}
            PauseButton.setDisable(true);
            StopButton.setDisable(true);
        }
        public void endofsession() {
            CurrentSessionPartTopLabel.setText(currentsessionpart.name + " Completed");
            TotalSessionLabel.setText("Session Completed");
            player_updateuitimeline.stop();
            PlayButton.setText("Replay");
            playerState = PlayerState.STOPPED;
            itemsinsession.forEach(SessionPart::cleanupPlayersandAnimations);
            // TODO Prompt For Export
            progressTracker.updateui_sessions();
            progressTracker.updateui_goals(this);
            reset(true);
        }
        public boolean endsessionprematurely() {
            if (playerState == PlayerState.PLAYING || playerState == PlayerState.PAUSED || playerState == TRANSITIONING) {
                currentsessionpart.pausewithoutanimation();
                updateuitimeline.pause();
                if (new ConfirmationDialog(options, "End Session Early", "Session Is Not Completed.", "End Session Prematurely?", "End Session", "Continue").getResult()) {return true;}
                else {play(); return false;}
            } else {return true;}
        }
        public void togglevolumebinding() {
            if (currentsessionpart != null && playerState == PlayerState.IDLE || playerState == PlayerState.STOPPED) {
                currentsessionpart.volume_rebindentrainment();
                if (ambienceenabled) {currentsessionpart.volume_rebindambience();}
            }
        }

    // Reference
        public void togglereference(ActionEvent actionEvent) {
            boolean buttontoggled = ReferenceCheckBox.isSelected();
            Root.getOptions().getSessionOptions().setReferenceoption(buttontoggled);
            if (! buttontoggled) {
                closereferencefile();
                togglevolumebinding();
            } else {
                if (Root.getOptions().getSessionOptions().getReferencetype() == null || ReferenceTypeComboBox.getSelectionModel().getSelectedIndex() == -1) {
                    Root.getOptions().getSessionOptions().setReferencetype(kujiin.xml.Options.DEFAULT_REFERENCE_TYPE_OPTION);
                    SelectReferenceType selectReferenceType = new SelectReferenceType(Root);
                    selectReferenceType.show();
                    selectReferenceType.setOnHidden(event -> {
                        if (selectReferenceType.getResult()) {
                            if (! checkreferencefiles(true)) {ReferenceCheckBox.setSelected(false);}
                            else {
                                switch (Root.getOptions().getSessionOptions().getReferencetype()) {
                                    case html:
                                        ReferenceTypeComboBox.getSelectionModel().select(0);
                                        break;
                                    case txt:
                                        ReferenceTypeComboBox.getSelectionModel().select(1);
                                        break;
                                }
                            }
                            if (playerState == PlayerState.PLAYING) {displayreferencefile(); togglevolumebinding();}
                        } else {
                            switch (Root.getOptions().getSessionOptions().getReferencetype()) {
                                case html:
                                    ReferenceTypeComboBox.getSelectionModel().select(0);
                                    break;
                                case txt:
                                    ReferenceTypeComboBox.getSelectionModel().select(1);
                                    break;
                            }
                        }
                    });
                } else {
                    if (playerState == PlayerState.PLAYING) {displayreferencefile(); togglevolumebinding();}
                }
            }
        }
        public void displayreferencefile() {
            boolean notalreadyshowing = displayReference == null || ! displayReference.isShowing();
            boolean referenceenabledwithvalidtype = Root.getOptions().getSessionOptions().getReferenceoption() &&
                    (Root.getOptions().getSessionOptions().getReferencetype() == ReferenceType.html || Root.getOptions().getSessionOptions().getReferencetype() == ReferenceType.txt);
            if (notalreadyshowing && referenceenabledwithvalidtype) {
                displayReference = new DisplayReference(currentsessionpart);
                displayReference.show();
                displayReference.setOnHidden(event -> {
                    currentsessionpart.volume_rebindentrainment();
                    if (ambienceenabled) {currentsessionpart.volume_rebindambience();}
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

        @Override
        public void close() {
            super.close();
            reset(false);
        }
    }
}
