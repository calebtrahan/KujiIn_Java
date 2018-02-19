//package kujiin.ui.dialogs;
//
//import javafx.animation.KeyFrame;
//import javafx.animation.Timeline;
//import javafx.beans.property.IntegerProperty;
//import javafx.beans.property.SimpleIntegerProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.property.StringProperty;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.stage.Modality;
//import javafx.util.Duration;
//import kujiin.ui.boilerplate.StyledStage;
//import kujiin.ui.creation.CustomizeAmbience;
//import kujiin.ui.creation.QuickAddAmbience;
//import kujiin.ui.creation.SetDurationWithAmbienceOption;
//import kujiin.ui.dialogs.alerts.ConfirmationDialog;
//import kujiin.ui.dialogs.alerts.InformationDialog;
//import kujiin.ui.table.TableItem_Number_Name_Duration_Ambience;
//import kujiin.util.Util;
//import kujiin.util.enums.QuickAddAmbienceType;
//import kujiin.xml.PlaybackItem;
//import kujiin.xml.Preferences;
//import kujiin.xml.Session;
//import kujiin.xml.Sessions;
//
//import java.io.IOException;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class AddMissedSessions extends StyledStage {
//    private final Preferences preferences;
//    private final Sessions sessions;
//// TableView
//    public TableView<MissedSessionTableItem> MissedSessionTableView;
//    public TableColumn<MissedSessionTableItem, Integer> MissedSessionNumberColumn;
//    public TableColumn<MissedSessionTableItem, String> MissedSessionNameColumn;
//    public TableColumn<MissedSessionTableItem, String> MissedSessionDurationColumn;
//// TableView Controls
//    public MenuButton AddItemsMenu;
//    public Menu AddAllMenu;
//    public MenuItem AddAllKujiInMenuItem;
//    public MenuItem AddAllElementsMenuItem;
//    public MenuItem AddQiGongMenuItem;
//    public MenuItem AddRinMenuItem;
//    public MenuItem AddKyoMenuItem;
//    public MenuItem AddTohMenuItem;
//    public MenuItem AddShaMenuItem;
//    public MenuItem AddKaiMenuItem;
//    public MenuItem addJinMenuItem;
//    public MenuItem AddRetsuMenuItem;
//    public MenuItem AddZaiMenuItem;
//    public MenuItem AddZenMenuItem;
//    public MenuItem AddEarthMenuItem;
//    public MenuItem AddAirMenuItem;
//    public MenuItem AddFireMenuItem;
//    public MenuItem AddWaterMenuItem;
//    public MenuItem AddVoidMenuItem;
//    public MenuButton DurationMenu;
//    public MenuItem Duration_SetRampOnlyMenuItem;
//    public MenuItem Duration_EditDurationMenuItem;
//    public Button MoveUpButton;
//    public Button MoveDownButton;
//    public Button RemoveButton;
//// Dialog Controls
//    public Label SessionDurationLabel;
//    public TextField SessionDurationTextField;
//    public Button AddSessionButton;
//    public Button CloseButton;
//// My Fields
//    private Session createdsession;
//    boolean accepted = false;
//
//    public AddMissedSessions(Preferences preferences, Sessions sessions) {
//        this.preferences = preferences;
//        this.sessions = sessions;
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/progress/AddMissingSessions.fxml"));
//            fxmlLoader.setController(this);
//            Scene scene = new Scene(fxmlLoader.load());
//            setScene(scene);
//            setResizable(false);
//        } catch (IOException ignored) { }
//    }
//
//// Getters And Setters
//    public boolean isAccepted() {
//        return accepted;
//    }
//
//
//// Table
//    private void populatetable() {
//        calculatedurationandestimatedcompletion();
//        if (createdtableplaybackitems != null) {createdtableplaybackitems.clear();}
//        else {createdtableplaybackitems = new ArrayList<>();}
//        if (! createdtableitems.isEmpty()) {createdtableitems.clear();}
//        if (createdsession.getPlaybackItems() != null && ! createdsession.getPlaybackItems().isEmpty()) {
//            createdtableplaybackitems.addAll(createdsession.getPlaybackItems());
//            int number = 1;
//            for (PlaybackItem i : createdtableplaybackitems) {
//                createdtableitems.add(new TableItem_Number_Name_Duration_Ambience(number, i.getName(), i.getdurationasString(), i.getAmbienceasString()));
//                number++;
//            }
//            CreatedTableView.setItems(createdtableitems);
//        }
//        createdtableselecteditem = null;
//        syncbuttons();
//    }
//    private void syncbuttons() {
//        boolean nosessionloaded = createdsession == null;
//        boolean tableempty = CreatedTableView.getItems().isEmpty();
//        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
//        AddItemsMenu.setDisable(nosessionloaded);
//        DurationMenu.setDisable(nosessionloaded || tableempty || selectedindex == -1);
//        AmbienceMenu.setDisable(nosessionloaded || tableempty || selectedindex == -1);
//        PlayButton.setDisable(nosessionloaded || tableempty);
//        SaveAsFileButton.setDisable(nosessionloaded || tableempty);
//        ExportButton.setDisable(nosessionloaded || tableempty);
//        RemoveButton.setDisable(nosessionloaded || tableempty || selectedindex == -1);
//        MoveUpButton.setDisable(nosessionloaded || tableempty || selectedindex == -1 || selectedindex == 0);
//        MoveDownButton.setDisable(nosessionloaded || tableempty || selectedindex == -1 || selectedindex == CreatedTableView.getItems().size() -1);
//        AddToFavoritesButton.setDisable(nosessionloaded || tableempty);
//        ClearButton.setDisable(nosessionloaded || tableempty);
//        SessionSummary_CompletionTime_Label.setDisable(nosessionloaded || tableempty);
//        SessionSummary_CompletionTime.setDisable(nosessionloaded || tableempty);
//        SessionSummary_Duration.setDisable(nosessionloaded || tableempty);
//        SessionSummary_Duration_Label.setDisable(nosessionloaded || tableempty);
//        if (nosessionloaded) {CreatedTableView.setPlaceholder(new Label("Please Create Or Load A Session"));}
//        else {CreatedTableView.setPlaceholder(new Label("Session Is Empty"));}
//    }
//    private void calculatedurationandestimatedcompletion() {
//        if (createdsession != null && ! createdsession.getPlaybackItems().isEmpty()) {
//            SessionSummary_Duration.setText(Util.formatdurationtoStringSpelledOutShort(createdsession.getExpectedSessionDuration(), false));
//            LocalTime now = LocalTime.now();
//            LocalTime completiontime = now.plusMinutes((int) createdsession.getExpectedSessionDuration().toMinutes());
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
//            String completiontext = completiontime.format(formatter);
//            SessionSummary_CompletionTime.setText(completiontext);
//            SessionSummary_CompletionTime.setTooltip(new Tooltip("Completion Time If You Start Now"));
//            updatecompletiontime = null;
//            new Timeline(new KeyFrame(Duration.millis(5000), ae -> calculatedurationandestimatedcompletion())).play();
//        } else {
//            SessionSummary_CompletionTime.setText(null);
//            SessionSummary_CompletionTime.setTooltip(null);
//            SessionSummary_Duration.setText(null);
//            SessionSummary_Duration.setTooltip(null);
//        }
//    }
//    // Table Button Actions
//    private void add(int availableambienceindex) {
//        PlaybackItem playbackItem = createdsession.getplaybackitem(availableambienceindex);
//        PlaybackItem.PlaybackItemType playbackItemType;
//        if (availableambienceindex == 0) {playbackItemType = PlaybackItem.PlaybackItemType.QIGONG;}
//        else if (availableambienceindex > 0 && availableambienceindex < 10) {playbackItemType = PlaybackItem.PlaybackItemType.CUT;}
//        else {playbackItemType = PlaybackItem.PlaybackItemType.ELEMENT;}
//        playbackItem.setPlaybackItemType(playbackItemType);
//        SetDurationWithAmbienceOption adjustDuration = new SetDurationWithAmbienceOption(preferences, availableAmbiences, Collections.singletonList(playbackItem), false);
//        adjustDuration.initModality(Modality.APPLICATION_MODAL);
//        adjustDuration.showAndWait();
//        if (adjustDuration.isAccepted()) {
//            int startindex;
//            if (MissedSessionTableView.getSelectionModel().getSelectedIndex() != -1) {
//                startindex = MissedSessionTableView.getSelectionModel().getSelectedIndex();
//                createdsession.addplaybackitems(startindex, Collections.singletonList(playbackItem)); createdsession.calculateexpectedduration();
//            } else {
//                startindex = createdsession.getPlaybackItems().size() - 1;
//                createdsession.addplaybackitems(null, Collections.singletonList(playbackItem)); createdsession.calculateexpectedduration();
//            }
//            if (MissedSessionTableView.getSelectionModel().getSelectedIndex() != -1 && createdsession.getplaybackitem(startindex).getCreationindex() == playbackItem.getCreationindex()) {
//                mergeitems(createdsession.getplaybackitem(startindex), createdsession.getplaybackitem(startindex + 1));
//                populatetable();
//                return;
//            }
//        }
//        populatetable();
//    }
//    private void add(int[] availableambienceindexes) {
//        List<PlaybackItem> items = new ArrayList<>();
//        for (int i : availableambienceindexes) {
//            PlaybackItem playbackItem = createdsession.getplaybackitem(i);
//            if (i == 0) {playbackItem.setPlaybackItemType(PlaybackItem.PlaybackItemType.QIGONG);}
//            else if (i > 0 && i < 10) {playbackItem.setPlaybackItemType(PlaybackItem.PlaybackItemType.CUT);}
//            else {playbackItem.setPlaybackItemType(PlaybackItem.PlaybackItemType.ELEMENT);}
//            items.add(playbackItem);
//        }
//        SetDurationWithAmbienceOption adjustDuration = new SetDurationWithAmbienceOption(preferences, availableAmbiences, items, true);
//        adjustDuration.initModality(Modality.APPLICATION_MODAL);
//        adjustDuration.showAndWait();
//        if (adjustDuration.isAccepted()) {
//            items = adjustDuration.getPlaybackItemList();
//            if (adjustDuration.isQuickaddambience()) {
//                switch (adjustDuration.getQuickAddAmbienceType()) {
//                    case 0:
//                        items = quickaddambience(QuickAddAmbienceType.REPEAT, items);
//                        break;
//                    case 1:
//                        items = quickaddambience(QuickAddAmbienceType.SHUFFLE, items);
//                        break;
//                }
//            }
//            int startindex;
//            if (CreatedTableView.getSelectionModel().getSelectedIndex() != -1) {
//                startindex = CreatedTableView.getSelectionModel().getSelectedIndex();
//                createdsession.addplaybackitems(startindex, items); createdsession.calculateexpectedduration();
//            } else {
//                startindex = createdsession.getPlaybackItems().size() - 1;
//                createdsession.addplaybackitems(null, items); createdsession.calculateexpectedduration();
//            }
//            if (CreatedTableView.getSelectionModel().getSelectedIndex() != -1 && createdsession.getplaybackitem(startindex).getCreationindex() == items.get(0).getCreationindex()) {
//                mergeitems(createdsession.getplaybackitem(startindex), createdsession.getplaybackitem(startindex + 1));
//                populatetable();
//                return;
//            }
//        }
//        populatetable();
//    }
//    public void addallitems_kujiin() {
//    int[] indexes = {1, 2, 3, 4, 5, 6, 7, 8, 9};
//    add(indexes);
//}
//    public void addallitems_elements() {
//        int[] indexes = {10, 11, 12, 13, 14};
//        add(indexes);
//    }
//    public void add_QiGong() {add(0);}
//    public void addRin() {add(1);}
//    public void addKyo() {add(2);}
//    public void addToh() {add(3);}
//    public void addSha() {add(4);}
//    public void addKai() {add(5);}
//    public void addJin() {add(6);}
//    public void addRetsu() {add(7);}
//    public void addZai() {add(8);}
//    public void addZen() {add(9);}
//    public void addEarth() {add(10);}
//    public void addAir() {add(11);}
//    public void addFire() {add(12);}
//    public void addWater() {add(13);}
//    public void addVoid() {add(14);}
//    public void moveupincreatortable() {
//        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
//        if (selectedindex > 0) {
//            ArrayList<PlaybackItem> itemsinsession = createdsession.getPlaybackItems();
//            PlaybackItem currentitem = createdsession.getPlaybackItems().get(selectedindex);
//            PlaybackItem oneitemup = createdsession.getPlaybackItems().get(selectedindex - 1);
//            if (currentitem.getPlaybackItemType() == PlaybackItem.PlaybackItemType.CUT && oneitemup.getPlaybackItemType() == PlaybackItem.PlaybackItemType.CUT) {
//                if (currentitem.getCreationindex() > oneitemup.getCreationindex()) {
//                    if (! new ConfirmationDialog(preferences, "Confirmation", "This Will Place Cuts Out Of Order", "This Is Not Recommended", "Proceed Anyway", "Cancel").getResult()) {return;}
//                }
//            }
//            if (selectedindex > 1 ) {
//                PlaybackItem twoitemsup = createdsession.getPlaybackItems().get(selectedindex - 2);
//                if (twoitemsup.getCreationindex() == currentitem.getCreationindex()) {mergeitems(twoitemsup, currentitem); }
//                else {Collections.swap(itemsinsession, selectedindex, selectedindex - 1);}
//            } else {Collections.swap(itemsinsession, selectedindex, selectedindex - 1);}
//            createdsession.setPlaybackItems(itemsinsession);
//            populatetable();
//        }
//    }
//    public void movedownincreatortable() {
//        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
//        if (selectedindex != -1 && selectedindex < createdtableplaybackitems.size() - 1) {
//            ArrayList<PlaybackItem> itemsinsession = createdsession.getPlaybackItems();
//            PlaybackItem currentitem = createdsession.getPlaybackItems().get(selectedindex);
//            PlaybackItem oneitemdown = createdsession.getPlaybackItems().get(selectedindex + 1);
//            if (currentitem.getPlaybackItemType() == PlaybackItem.PlaybackItemType.CUT && oneitemdown.getPlaybackItemType() == PlaybackItem.PlaybackItemType.CUT) {
//                if (currentitem.getCreationindex() < oneitemdown.getCreationindex()) {
//                    if (! new ConfirmationDialog(preferences, "Confirmation", "This Will Place Cuts Out Of Order", "This Is Not Recommended", "Proceed Anyway", "Cancel").getResult()) {return;}
//                }
//            }
//            if (selectedindex < createdtableplaybackitems.size() - 2) {
//                PlaybackItem twoitemsdown = createdsession.getPlaybackItems().get(selectedindex + 2);
//                if (twoitemsdown.getCreationindex() == currentitem.getCreationindex()) {mergeitems(twoitemsdown, currentitem); }
//                else {Collections.swap(itemsinsession, selectedindex, selectedindex + 1);}
//            } else {Collections.swap(itemsinsession, selectedindex, selectedindex + 1);}
//            createdsession.setPlaybackItems(itemsinsession);
//            populatetable();
//        }
//
//    }
//    public void removefromcreatortable() {
//        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
//        if (selectedindex != -1 && createdtableitems != null) {
//            createdsession.removeplaybackitem(selectedindex);
//            populatetable();
//        }
//    }
//    private void mergeitems(PlaybackItem item1, PlaybackItem item2) {
//        if (new ConfirmationDialog(preferences, "Confirmation", "Duplicate Playback Item  + '" + item1.getName() + "' Detected", "Merge? (This Will Clear Ambience For Merged Items)").getResult()) {
//            ArrayList<PlaybackItem> itemsinsession = createdsession.getPlaybackItems();
//            int item1index = itemsinsession.indexOf(item1);
//            itemsinsession.remove(item2);
//            boolean ambiencewasenabled = item1.getAmbience().isEnabled() || item2.getAmbience().isEnabled();
//            Duration newduration = new Duration(item1.getExpectedDuration());
//            newduration = newduration.add(new Duration(item2.getExpectedDuration()));
//            item1.setExpectedDuration(newduration.toMillis());
//            item1.getAmbience().setAmbience(new ArrayList<>());
//            item1.getAmbience().setEnabled(false);
//            if (ambiencewasenabled) {
//                Duration ambienceduration = Duration.ZERO;
//                if (item1.getAmbience().isEnabled()) {ambienceduration = ambienceduration.add(item1.getAmbience().gettotalDuration());}
//                if (item2.getAmbience().isEnabled()) {ambienceduration = ambienceduration.add(item2.getAmbience().gettotalDuration());}
//                if (ambienceduration.lessThan(newduration)) {
//                    String[] options = {"Quick Add Ambience", "Customize Ambience", "Don't Add Ambience"};
//                    ChoiceDialog<String> addambiencechoicedialog = new ChoiceDialog<>(options[0], options);
//                    addambiencechoicedialog.setHeaderText("Ambience Was Cleared But Previously Enabled For One Or Both Of These Playback Items");
//                    addambiencechoicedialog.setContentText("Select How You Would Like To Add Ambience");
//                    addambiencechoicedialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(addambiencechoicedialog.selectedItemProperty().isNull());
//                    if (addambiencechoicedialog.showAndWait().isPresent()) {
//                        System.out.println(addambiencechoicedialog.getSelectedItem());
//                        switch (addambiencechoicedialog.getSelectedItem()) {
//                            case "Quick Add Ambience":
//                                QuickAddAmbience quickAddAmbience = new QuickAddAmbience(preferences, availableAmbiences, Collections.singletonList(item1));
//                                quickAddAmbience.initModality(Modality.APPLICATION_MODAL);
//                                quickAddAmbience.showAndWait();
//                                if (quickAddAmbience.isAccepted()) { item1 = quickAddAmbience.getPlaybackItemList().get(0); }
//                                break;
//                            case "Customize Ambience":
//                                CustomizeAmbience customizeAmbience = new CustomizeAmbience(preferences, item1, availableAmbiences);
//                                customizeAmbience.showAndWait();
//                                if (customizeAmbience.isAccepted()) { item1 = customizeAmbience.getPlaybackItem(); }
//                                break;
//                            case "Don't Add Ambience":
//                                break;
//                        }
//                    }
//                }
//            }
//            itemsinsession.set(item1index, item1);
//            createdsession.setPlaybackItems(itemsinsession);
//        }
//    }
//    public void editduration() {
//        int selectedindex = CreatedTableView.getSelectionModel().getSelectedIndex();
//        if (selectedindex != -1 && createdtableselecteditem != null) {
//            SetDurationWithAmbienceOption adjustDuration = new SetDurationWithAmbienceOption(preferences, availableAmbiences, Collections.singletonList(createdtableselecteditem), false);
//            adjustDuration.initOwner(getStage());
//            adjustDuration.showAndWait();
//            if (adjustDuration.isAccepted()) {
//                createdtableplaybackitems.set(selectedindex, adjustDuration.getPlaybackItemList().get(0));
//                if (createdtableselecteditem.getAmbience().isEnabled() && createdtableselecteditem.getAmbience().getCurrentAmbienceDuration().lessThan(new Duration(createdtableplaybackitems.get(selectedindex).getExpectedDuration()))) {
//                    if (new ConfirmationDialog(preferences, "Set Ambience", "Ambience Not Long Enough To Match New Duration", "Please Set More Ambience Or Disable Ambience", "Add Ambience", "Disable Ambience").getResult()) {
//                        customizeambience();
//                        PlaybackItem createdtableselecteditem = createdtableplaybackitems.get(selectedindex);
//                        if (createdtableselecteditem.getAmbience().getCurrentAmbienceDuration().lessThan(new Duration(createdtableplaybackitems.get(selectedindex).getExpectedDuration()))) {
//                            createdtableselecteditem.getAmbience().clearambience();
//                            createdtableselecteditem.getAmbience().setEnabled(false);
//                        }
//                    } else {
//                        createdtableselecteditem.getAmbience().clearambience();
//                        createdtableselecteditem.getAmbience().setEnabled(false);
//                    }
//                }
//                createdsession.calculateexpectedduration();
//                populatetable();
//                syncbuttons();
//            }
//        } else {new InformationDialog(preferences, "Cannot Edit Duration", "Select A Single Table Item To Edit Duration", null, true);}
//    }
//
//    // Dialog
//    private void addsession() {
//        // Confirm Add Session & Ask For Date Practiced
//            // Add Session
//        // Ask If Want To Add Another Session || Close Dialog
//    }
//    @Override
//    public void close() {
//        // Check If Session Loaded But Not Added
//            // Add Session
//        super.close();
//    }
//
//
//    class MissedSessionTableItem {
//        IntegerProperty number;
//        StringProperty name;
//        StringProperty duration;
//
//        public MissedSessionTableItem(int number, String name, String duration) {
//            this.number = new SimpleIntegerProperty(number);
//            this.name = new SimpleStringProperty(name);
//            this.duration = new SimpleStringProperty(duration);
//        }
//    }
//}