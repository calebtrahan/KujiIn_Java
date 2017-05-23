package kujiin.ui.dialogs;

public class SessionPlaybackOverview {
//    public TableView<kujiin.util.table.SessionItem> SessionItemsTable;
//    public TableColumn<kujiin.util.table.SessionItem, Integer> NumberColumn;
//    public TableColumn<kujiin.util.table.SessionItem, String> NameColumn;
//    public TableColumn<kujiin.util.table.SessionItem, String> DurationColumn;
//    public TableColumn<kujiin.util.table.SessionItem, String> AmbienceColumn;
//    public TableColumn<kujiin.util.table.SessionItem, String> GoalColumn;
//    public Button UpButton;
//    public Button DownButton;
//    public Button CancelButton;
//    public Button AdjustDurationButton;
//    public Button SetGoalButton;
//    public TextField TotalSessionTime;
//    public Button PlaySessionButton;
//    public TextField CompletionTime;
//    public Button SetAmbienceButton;
//    public CheckBox AmbienceSwitch;
//    public ComboBox<String> AmbienceTypeComboBox;
//    public Label StatusBar;
//    private List<SessionItem> alladjustedsessionitems;
//    private SessionItem selectedsessionpart;
//    private ObservableList<kujiin.util.table.SessionItem> tableitems = FXCollections.observableArrayList();
//    private boolean result;
//    private MainController Root;
//    private QuickAddAmbienceType ambiencePlaybackType;
//
//    public SessionPlaybackOverview(MainController Root, Stage parent, boolean minimizeparent, List<SessionItem> itemsinsession) {
//        super(Root, parent, minimizeparent);
//        try {
//            this.Root = Root;
//            alladjustedsessionitems = itemsinsession;
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/SessionPlaybackOverview.fxml"));
//            fxmlLoader.setController(this);
//            Scene defaultscene = new Scene(fxmlLoader.load());
//            setScene(defaultscene);
//            setResizable(false);
//            setOnCloseRequest(event -> {
//
//            });
//            setTitle("Session Playback Overview");
//            NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
//            NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
//            DurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
//            AmbienceColumn.setCellValueFactory(cellData -> cellData.getValue().ambiencesummary);
//            GoalColumn.setCellValueFactory(cellData -> cellData.getValue().goalsummary);
//            SessionItemsTable.setOnMouseClicked(event -> itemselected());
//            AmbienceTypeComboBox.setOnAction(event -> ambiencetypechanged());
//            tableitems = FXCollections.observableArrayList();
//            AmbienceTypeComboBox.setItems(FXCollections.observableArrayList("Repeat", "Shuffle", "Custom"));
//            AmbienceSwitch.setSelected(false);
//            ambiencePlaybackType = Root.getPreferences().getSessionOptions().getAmbiencePlaybackType();
//            if (ambiencePlaybackType != null) {
//                switch (ambiencePlaybackType) {
//                    case Repeat:
//                        AmbienceTypeComboBox.getSelectionModel().select(0);
//                        break;
//                    case Shuffle:
//                        AmbienceTypeComboBox.getSelectionModel().select(1);
//                        break;
//                    case CUSTOM:
//                        AmbienceTypeComboBox.getSelectionModel().select(2);
//                        break;
//                }
//            }
//            AmbienceTypeComboBox.setDisable(true);
//            setupTableColors();
//            populatetable();
//        } catch (IOException ignored) {
//        }
//    }
//
//    public void setupTableColors() {
//        DurationColumn.setCellFactory((column -> new TableCell<kujiin.util.table.SessionItem, String>() {
//            @Override
//            protected void updateItem(String item, boolean empty) {
//                super.updateItem(item, empty);
//                if (item == null | empty) {
//                    setText(null);
//                    setStyle("");
//                } else {
//                    setText(item);
//                    switch (item) {
//                        case "No Duration Set":
//                            setStyle("-fx-text-fill: red");
//                            setText(item);
//                            break;
//                        case "Ramp Only":
//                            setStyle("-fx-text-fill: yellow");
//                            setText(item);
//                            break;
//                        default:
//                            setStyle("-fx-text-fill: white");
//                            setText(item);
//                            break;
//                    }
//                }
//            }
//        }));
//        AmbienceColumn.setCellFactory(column -> new TableCell<kujiin.util.table.SessionItem, String>() {
//            @Override
//            protected void updateItem(String item, boolean empty) {
//                super.updateItem(item, empty);
//                if (item == null | empty) {
//                    setText(null);
//                    setStyle("");
//                } else {
//                    switch (item) {
//                        case "Ambience Not Set":
//                        case "Has No Ambience":
//                            setStyle("-fx-text-fill: red");
//                            setText(item);
//                            break;
//                        case "Will Shuffle":
//                        case "Will Repeat":
//                        case "Disabled":
//                            setStyle("-fx-text-fill: white");
//                            setText(item);
//                            break;
//                    }
//                }
//            }
//        });
//        GoalColumn.setCellFactory(column -> new TableCell<kujiin.util.table.SessionItem, String>() {
//            @Override
//            protected void updateItem(String item, boolean empty) {
//                super.updateItem(item, empty);
//                if (item == null | empty) {
//                    setText(null);
//                    setStyle("");
//                } else {
//                    setText(item);
//                    switch (item) {
//                        case "No Goal Set":
//                            setStyle("-fx-text-fill: yellow");
//                            setText(item);
//                            break;
//                        default:
//                            setStyle("-fx-text-fill: white");
//                            setText(item);
//                            break;
//                    }
//                }
//            }
//        });
//    }
//
//// Getters And Setters
//    public List<SessionItem> getAlladjustedsessionitems() {
//        return alladjustedsessionitems;
//    }
//
//// Table
//    public void itemselected() {
//        int index = SessionItemsTable.getSelectionModel().getSelectedIndex();
//        if (index != -1) {
//            selectedsessionpart = alladjustedsessionitems.get(index);
//        } else {selectedsessionpart = null;}
//        syncbuttons();
//    }
//    public void populatetable() {
//        SessionItemsTable.getItems().removeAll(tableitems);
//        if (alladjustedsessionitems == null) {alladjustedsessionitems = new ArrayList<>();}
//        int count = 1;
//        List<SessionItem> newsessionitems = new ArrayList<>();
//        for (SessionItem x : Root.getAllSessionParts(false)) {
//            if ((alladjustedsessionitems.contains(x)) || (!getwellformedcuts().isEmpty() && x instanceof Cut && (!alladjustedsessionitems.contains(x) && getwellformedcuts().contains(x)))) {
//                String currentgoaltext = "";
//                if (x.goals_getCurrent() == null) {currentgoaltext = "No Goal Set";}
//                else {currentgoaltext = x.goals_getCurrent().getFormattedString(Root.getProgressTracker().getSessions().gettotalpracticedtime(x, false), true, 150.0);}
//                tableitems.add(new kujiin.util.table.SessionItem(count, x.name, x.getdurationasString(true, 150.0), getambiencetext(x), currentgoaltext));
//                newsessionitems.add(x);
//                count++;
//            }
//        }
//        SetGoalButton.setDisable(true);
//        SetAmbienceButton.setDisable(true);
//        AdjustDurationButton.setDisable(true);
//        alladjustedsessionitems = newsessionitems;
//        SessionItemsTable.setItems(tableitems);
//        syncbuttons();
//        calculatetotalduration();
//    }
//    public void syncbuttons() {
//        int index = SessionItemsTable.getSelectionModel().getSelectedIndex();
//        UpButton.setDisable(index < 1);
//        DownButton.setDisable(index == -1 || index == SessionItemsTable.getItems().size() - 1);
//        AdjustDurationButton.setDisable(selectedsessionpart == null);
//        if (selectedsessionpart != null) {
//            SetGoalButton.setDisable(selectedsessionpart.goals_ui_hascurrentgoal());
//            SetAmbienceButton.setDisable(!AmbienceSwitch.isSelected() && ambiencePlaybackType == null);
//            if (AmbienceSwitch.isSelected()) {
//                switch (ambiencePlaybackType) {
//                    case CUSTOM:
//                        SetAmbienceButton.setDisable(index == -1);
//                        if (! selectedsessionpart.getAmbience().hasCustomAmbience()) {SetAmbienceButton.setText("Set Custom Ambience");}
//                        else {SetAmbienceButton.setText("Edit Custom Ambience");}
//                        break;
//                    case Shuffle:
//                    case Repeat:
//                        if (! selectedsessionpart.getAmbience_hasAny() && index != -1) {
//                            SetAmbienceButton.setDisable(false);
//                            SetAmbienceButton.setText("Add Ambience");
//                        } else {SetAmbienceButton.setDisable(true);}
//                        break;
//                    default:
//                        SetAmbienceButton.setDisable(true);
//                        break;
//                }
//            } else {SetAmbienceButton.setDisable(true);}
//            if (selectedsessionpart.goals_getAllCurrent().isEmpty()) {SetGoalButton.setText("Set Current Goal");}
//            else {SetGoalButton.setText("Add Goal");}
//            SetGoalButton.setDisable(index == -1);
//        }
//    }
//
//// Order/Sort Session Parts
//    public void moveitemup() {
//        int selectedindex = SessionItemsTable.getSelectionModel().getSelectedIndex();
//        if (selectedindex == -1) {
//            return;
//        }
//        if (tableitems.getplaybackItemGoals(selectedindex).name.getplaybackItemGoals().equals("Presession") || tableitems.getplaybackItemGoals(selectedindex).name.getplaybackItemGoals().equals("Postsession")) {
//            new InformationDialog(Root.getPreferences(), "Information", "Cannot Move", "Presession And Postsession Cannot Be Moved");
//            return;
//        }
//        if (selectedindex == 0) {
//            return;
//        }
//        SessionItem selecteditem = alladjustedsessionitems.get(selectedindex);
//        SessionItem oneitemup = alladjustedsessionitems.get(selectedindex - 1);
//        if (selecteditem instanceof Cut && oneitemup instanceof Cut) {
//            if (selecteditem.index > oneitemup.index) {
//                new InformationDialog(Root.getPreferences(), "Cannot Move", selecteditem.name + " Cannot Be Moved Before " + oneitemup.name + ". Cuts Would Be Out Of Order", "Cannot Move");
//                return;
//            }
//        }
//        if (oneitemup instanceof Qi_Gong) {
//            new InformationDialog(Root.getPreferences(), "Cannot Move", "Cannot Replace Presession", "Cannot Move");
//            return;
//        }
//        Collections.swap(alladjustedsessionitems, selectedindex, selectedindex - 1);
//        populatetable();
//    }
//    public void moveitemdown() {
//        int selectedindex = SessionItemsTable.getSelectionModel().getSelectedIndex();
//        if (selectedindex == -1) {
//            return;
//        }
//        if (tableitems.getplaybackItemGoals(selectedindex).name.getplaybackItemGoals().equals("Presession") || tableitems.getplaybackItemGoals(selectedindex).name.getplaybackItemGoals().equals("Postsession")) {
//            new InformationDialog(Root.getPreferences(), "Information", "Cannot Move", tableitems.get(selectedindex).name + " Cannot Be Moved");
//            return;
//        }
//        if (selectedindex == tableitems.size() - 1) {
//            return;
//        }
//        SessionItem selecteditem = alladjustedsessionitems.get(selectedindex);
//        SessionItem oneitemdown = alladjustedsessionitems.get(selectedindex + 1);
//        if (selecteditem instanceof Cut && oneitemdown instanceof Cut) {
//            if (selecteditem.index < oneitemdown.index) {
//                new InformationDialog(Root.getPreferences(), "Cannot Move", selecteditem.name + " Cannot Be Moved After " + oneitemdown.name + ". Cuts Would Be Out Of Order", "Cannot Move");
//                return;
//            }
//        }
//        if (oneitemdown instanceof Qi_Gong) {
//            new InformationDialog(Root.getPreferences(), "Cannot Move", "Cannot Replace Postsession", "Cannot Move");
//            return;
//        }
//        Collections.swap(alladjustedsessionitems, selectedindex, selectedindex + 1);
//        populatetable();
//    }
//
//// Session Parts Missing / Out Of Order
//    public List<Cut> getwellformedcuts() {
//        List<Cut> wellformedcuts = new ArrayList<>();
//        for (int i = 0; i < getlastworkingcutindex(); i++) {
//            wellformedcuts.add(Root.getAllCuts().get(i));
//        }
//        return wellformedcuts;
//    }
//    public int getlastworkingcutindex() {
//        int lastcutindex = 0;
//        for (SessionItem i : alladjustedsessionitems) {
//            if (i instanceof Cut && i.getduration().greaterThan(Duration.ZERO)) {
//                lastcutindex = i.index;
//            }
//        }
//        return lastcutindex;
//    }
//
////  Duration
//    public void adjustduration() {
//        if (selectedsessionpart != null) {
//            SessionPlaybackOverview_ChangeDuration changedurationdialog = new SessionPlaybackOverview_ChangeDuration(selectedsessionpart);
//            changedurationdialog.showAndWait();
//            switch (changedurationdialog.result) {
//                case DURATION:
//                    selectedsessionpart.changevalue((int) changedurationdialog.getDuration().toMinutes());
//                    break;
//                case RAMP:
//                    selectedsessionpart.setRamponly();
//                    break;
//                case CANCEL:
//                    break;
//            }
//            populatetable();
//        }
//    }
//    public void calculatetotalduration() {
//        Duration duration = Duration.ZERO;
//        for (SessionItem i : alladjustedsessionitems) {
//            duration = duration.add(i.getduration());
//        }
//        double width = TotalSessionTime.getLayoutBounds().getWidth();
//        if (width > 0.0) {
//            TotalSessionTime.setText(Util.formatdurationtoStringSpelledOut(duration, width));
//        } else {TotalSessionTime.setText(Util.formatdurationtoStringSpelledOut(duration, 100.0));}
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.MILLISECOND, new Double(duration.toMillis()).intValue());
//        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
//        CompletionTime.setText(sdf.format(cal.getTime()));
//    }
//
//// Goal
//    public void setgoal() {
//        if (selectedsessionpart != null) {
//            Root.getProgressTracker().goals_setnew(selectedsessionpart);
//        }
//        populatetable();
//    }
//
//// Ambience
//    public void ambienceswitchtoggled() {
//        AmbienceTypeComboBox.setDisable(! AmbienceSwitch.isSelected());
//        if (ambiencePlaybackType == null) {ambiencePlaybackType = Root.getPreferences().getSessionOptions().getAmbiencePlaybackType();}
//        populatetable();
//    }
//    public String getambiencetext(SessionItem sessionItem) {
//        if (! AmbienceSwitch.isSelected()) {return "Disabled";}
//        else {
//            switch (ambiencePlaybackType) {
//                case Repeat:
//                    if (!sessionItem.getAmbience().hasAnyAmbience()) {
//                        return "Has No Ambience";
//                    } else {
//                        return "Will Repeat";
//                    }
//                case Shuffle:
//                    if (!sessionItem.getAmbience().hasAnyAmbience()) {
//                        return "Has No Ambience";
//                    } else {
//                        return "Will Shuffle";
//                    }
//                case CUSTOM:
//                    if (sessionItem.getAmbience().getAmbience() == null || !sessionItem.getAmbience().getAmbience().isEmpty()) {
//                        return "Ambience Not Set";
//                    } else {
//                        return "Custom Ambience Set";
//                    }
//                default:
//                    return null;
//            }
//        }
//    }
//    public void ambiencetypechanged() {
//        int index = AmbienceTypeComboBox.getSelectionModel().getSelectedIndex();
//        switch (index) {
//            case 0:
//                ambiencePlaybackType = QuickAddAmbienceType.Repeat;
//                break;
//            case 1:
//                ambiencePlaybackType = QuickAddAmbienceType.Shuffle;
//                break;
//            case 2:
//                ambiencePlaybackType = QuickAddAmbienceType.CUSTOM;
//                break;
//        }
//        populatetable();
//    }
//    public void setoraddambience() {
//        switch (SetAmbienceButton.getText()) {
//            case "Set Ambience":
//            case "Set Custom Ambience":
//            case "Edit Custom Ambience":
//                SessionPlaybackOverview_AddCustomAmbience addCustomAmbience = new SessionPlaybackOverview_AddCustomAmbience(selectedsessionpart);
//                addCustomAmbience.showAndWait();
//                if (addCustomAmbience.getResult()) {
//                    List<SoundFile> customambiencelist = addCustomAmbience.getCustomAmbienceList();
//                    selectedsessionpart.getAmbience().setAmbience(customambiencelist);
//                }
//                break;
//            case "Add Ambience":
//                if (Root.getPreferences().getAdvancedOptions().getDefaultambienceeditor().equals("Simple")) {
//                    new AmbienceEditor_Simple(Root, this, false, selectedsessionpart).showAndWait();
//                } else {new AmbienceEditor_Advanced(Root, this, false, selectedsessionpart).showAndWait();}
//                populatetable();
//                break;
//            default:
//                break;
//        }
//    }
////    public void checkambience() {
////        if (isShowing() && AmbienceSwitch.isSelected()) {
////            ArrayList<SessionPart> sessionpartswithnoambience = new ArrayList<>();
////            ArrayList<SessionPart> sessionpartswithreducedambience = new ArrayList<>();
////            Root.getAllSessionParts(false).stream().filter(i -> i.getduration().greaterThan(Duration.ZERO)).forEach(i -> {
////                Root.CreatorStatusBar.setText(String.format("Checking Ambience. Currently Checking %s...", i.name));
////                if (!i.getAvailableAmbience().hasAnyAmbience()) {
////                    sessionpartswithnoambience.add(i);
////                } else if (!i.getAvailableAmbience().hasEnoughAmbience(i.getduration())) {
////                    sessionpartswithreducedambience.add(i);
////                }
////            });
////
//////            if (!sessionpartswithnoambience.isValid()) {
//////                StringBuilder a = new StringBuilder();
//////                for (int i = 0; i < sessionpartswithnoambience.size(); i++) {
//////                    a.append(sessionpartswithnoambience.get(i).name);
//////                    if (i != sessionpartswithnoambience.size() - 1) {
//////                        a.append(", ");
//////                    }
//////                }
//////                if (new ConfirmationDialog(Root.getPreferences(), "Missing Ambience", null, "Missing Ambience For " + a.toString() + ". Ambience Cannot Be Enabled For Session Without At Least One Working Ambience File" +
//////                        " Per Session Part", "Add Ambience", "Disable Ambience").getResult()) {
//////                    if (sessionpartswithnoambience.size() == 1) {
//////                        new AmbienceEditor_Simple(Root, sessionpartswithnoambience.get(0)).showAndWait();
//////                    } else {
//////                        new AmbienceEditor_Simple(Root).showAndWait();
//////                    }
//////                } else {
//////                    AmbienceSwitch.setSelected(false);
//////                }
//////            } else {
////////                    if (! sessionpartswithreducedambience.isValid()) {
////////                        StringBuilder a = new StringBuilder();
////////                        int count = 0;
////////                        for (SessionPart aSessionpartswithreducedambience : sessionpartswithreducedambience) {
////////                            a.append("\n");
////////                            String formattedcurrentduration = Util.formatdurationtoStringSpelledOut(aSessionpartswithreducedambience.getAvailableAmbience().gettotalDuration(), null);
////////                            String formattedexpectedduration = Util.formatdurationtoStringSpelledOut(aSessionpartswithreducedambience.getduration(), null);
////////                            a.append(count + 1).append(". ").append(aSessionpartswithreducedambience.name).append(" >  Current: ").append(formattedcurrentduration).append(" | Needed: ").append(formattedexpectedduration);
////////                            count++;
////////                        }
////////                    if (ambiencePlaybackType == null) {AmbienceSwitch.setSelected(false);}
//////
//////            }
////        }
////    }
//
//// Dialog
//    public boolean getResult() {
//        return result;
//    }
//    public void playsession() {
//        Root.getSessionCreator().setAmbienceenabled(AmbienceSwitch.isSelected());
//        List<Integer> indexesmissingduration = new ArrayList<>();
//        List<Integer> indexesmissinggoals = new ArrayList<>();
//        for (SessionItem i : alladjustedsessionitems) {
//            if (i.getduration() == Duration.ZERO && !i.ramponly) {
//                indexesmissingduration.add(alladjustedsessionitems.indexOf(i));
//            }
//            if (! i.goals_ui_hascurrentgoal()) {
//                indexesmissinggoals.add(alladjustedsessionitems.indexOf(i));
//            }
//        }
//        // Add Ramp Option For Missing Durations
//        if (!indexesmissingduration.isEmpty()) {
//            if (new ConfirmationDialog(Root.getPreferences(), "Confirmation", indexesmissingduration.size() + " Session Parts Are Missing Durations", "Set Ramp Only For The Parts Missing Durations",
//                    "Set Ramp Only", "Cancel Playback").getResult()) {
//                for (int x : indexesmissingduration) {alladjustedsessionitems.get(x).setRamponly();}
//            } else {return;}
//        }
//        // Check Ambience
//        if (AmbienceSwitch.isSelected() && ambiencePlaybackType != null) {
//            Root.getSessionCreator().setAmbiencePlaybackType(ambiencePlaybackType);
//            int count = 0;
//            switch (ambiencePlaybackType) {
//                case Repeat:
//                case Shuffle:
//                    for (SessionItem i : alladjustedsessionitems) {if (! i.getAmbience_hasAny()) {count++;}}
//                    if (count > 0) {
//                        new InformationDialog(Root.getPreferences(), "Information", "Missing Ambience For " + count + " Session Parts", "Please Add Ambience Or Disable Ambience From Session");
//                        return;
//                    }
//                    break;
//                case CUSTOM:
//                    for (SessionItem i : alladjustedsessionitems) {
//                        if (! i.getAmbience().hasCustomAmbience()) {count++;}
//                    }
//                    if (count > 0) {
//                        new InformationDialog(Root.getPreferences(), "Cannot Start Playback",  count + " Session Parts Missing Custom Ambience", "Please Add Custom Ambience, Change Ambience Playback\nType Or Disable Ambience From Session");
//                        return;
//                    }
//                    break;
//            }
//        }
//        // Check AllGoals
//        if (! indexesmissinggoals.isEmpty()) {
//            if (! new ConfirmationDialog(Root.getPreferences(), "Confirmation", indexesmissinggoals.size() + " Session Parts Are Missing AllGoals", "Continue Playing Session Without AllGoals?",
//                    "Yes", "No").getResult()) {
//                return;
//            }
//        }
//        // Check Alert File Needed/Not Needed
//        int longsessionparts = 0;
//        for (SessionItem i : alladjustedsessionitems) {
//            if (i.getduration().greaterThanOrEqualTo(Duration.minutes(Preferences.DEFAULT_LONG_SESSIONPART_DURATION))) {
//                longsessionparts++;
//                break;
//            }
//        }
//        if (longsessionparts > 1 && !Root.getPreferences().getSessionOptions().getAlertfunction()) {
//            switch (new AnswerDialog(Root.getPreferences(), this, "Add Alert File", null, "I've Detected A Long Session. Add Alert File In Between Session Parts?",
//                    "Add Alert File", "Continue Without Alert File", "Cancel Playback").getResult()) {
//                case YES:
//                    new SelectAlertFile(Root, this, false).showAndWait();
//                    break;
//                case CANCEL:
//                    return;
//            }
//        } else if (Root.getPreferences().getSessionOptions().getAlertfunction()) {
//            switch (new AnswerDialog(Root.getPreferences(), this,  "Disable Alert File", null, "I've Detected A Relatively Short Session With Alert File Enabled",
//                    "Disable Alert File", "Leave Alert File Enabled", "Cancel Playback").getResult()) {
//                case YES:
//                    Root.getPreferences().getSessionOptions().setAlertfunction(false);
//                    break;
//                case CANCEL:
//                    return;
//            }
//        }
//        result = true;
//        close();
//    }
//    public void cancel() {
//        alladjustedsessionitems = null;
//        close();
//    }
//
//    public class SessionPlaybackOverview_ChangeDuration extends Stage {
//        public TextField HoursTextField;
//        public TextField MinutesTextField;
//        public CheckBox RampOnlyCheckBox;
//        public Button SetButton;
//        public Button CancelButton;
//        private Duration duration;
//        private ChangeDurationType result = ChangeDurationType.CANCEL;
//
//        public SessionPlaybackOverview_ChangeDuration(SessionItem sessionItem) {
//            try {
//                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/SessionPlaybackOverview_ChangeDuration.fxml"));
//                fxmlLoader.setController(this);
//                Scene defaultscene = new Scene(fxmlLoader.load());
//                setScene(defaultscene);
//                getIcons().clear();
//                getIcons().add(PROGRAM_ICON);
//                String themefile = Root.getPreferences().getUserInterfaceOptions().getThemefile();
//                if (themefile != null) {getScene().getStylesheets().add(themefile);}
//                this.setResizable(false);
//                setTitle("Change " + sessionItem.name + " Duration");
//                HoursTextField.setText("0");
//                MinutesTextField.setText("0");
//                Util.custom_textfield_integer(HoursTextField, 0, 60, 1);
//                Util.custom_textfield_integer(MinutesTextField, 0, 59, 1);
//            } catch (IOException ignored) {
//            }
//        }
//
//        public ChangeDurationType getResult() {
//            return result;
//        }
//        public Duration getDuration() {
//            return duration;
//        }
//        public void setDuration(Duration duration) {
//            this.duration = duration;
//        }
//        public void ramponlyselected() {
//            HoursTextField.setDisable(RampOnlyCheckBox.isSelected());
//            MinutesTextField.setDisable(RampOnlyCheckBox.isSelected());
//        }
//        public void OKButtonPressed() {
//            try {
//                if (!RampOnlyCheckBox.isSelected()) {
//                    Duration duration = Duration.hours(Double.parseDouble(HoursTextField.getText())).add(Duration.minutes(Double.parseDouble(MinutesTextField.getText())));
//                    if (duration.greaterThan(Duration.ZERO)) {
//                        setDuration(duration);
//                        result = ChangeDurationType.DURATION;
//                    } else {
//                        new InformationDialog(Root.getPreferences(), "Information", "Cannot Change Value To 0", null);
//                        return;
//                    }
//                } else {
//                    result = ChangeDurationType.RAMP;
//                }
//                close();
//            } catch (NumberFormatException ignored) {
//            }
//        }
//    }
//    public class SessionPlaybackOverview_AddCustomAmbience extends Stage {
//        public TableView<AmbienceSongWithNumber> AmbienceItemsTable;
//        public TableColumn<AmbienceSongWithNumber, Integer> NumberColumn;
//        public TableColumn<AmbienceSongWithNumber, String> NameColumn;
//        public TableColumn<AmbienceSongWithNumber, String> DurationColumn;
//        public TextField TotalDurationTextField;
//        public Button AcceptButton;
//        public Button CancelButton;
//        public Button RemoveButton;
//        public Button PreviewButton;
//        public MenuButton AddMenuButton;
//        public MenuItem AddAmbience;
//        public MenuItem AddFiles;
//        public MenuButton MoveMenuButton;
//        public MenuItem MoveUp;
//        public MenuItem MoveDown;
//        private AmbienceSongWithNumber selectedtableitem;
//        private ObservableList<AmbienceSongWithNumber> TableItems = FXCollections.observableArrayList();
//        private List<SoundFile> CustomAmbienceList = new ArrayList<>();
//        private SessionItem sessionItem;
//        private boolean result = false;
//        private boolean longenough = false;
//
//        public SessionPlaybackOverview_AddCustomAmbience(SessionItem sessionItem) {
//            try {
//                this.sessionItem = sessionItem;
//                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/SessionPlaybackOverview_AddCustomAmbience.fxml"));
//                fxmlLoader.setController(this);
//                Scene defaultscene = new Scene(fxmlLoader.load());
//                setScene(defaultscene);
//                getIcons().clear();
//                getIcons().add(PROGRAM_ICON);
//                String themefile = Root.getPreferences().getUserInterfaceOptions().getThemefile();
//                if (themefile != null) {getScene().getStylesheets().add(themefile);}
//                this.setResizable(false);
//                setTitle("Set Custom Ambience For " + selectedsessionpart.name);
//                NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
//                NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
//                DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
//                AmbienceItemsTable.getSelectionModel().selectedItemProperty().addListener(
//                        (observable, oldValue, newValue) -> tableselectionchanged());
//                AddAmbience.setOnAction(event -> addambience());
//                AddFiles.setOnAction(event -> addfiles());
//                UpButton.setOnAction(event -> moveupintable());
//                DownButton.setOnAction(event -> movedownintable());
//                if (sessionItem.getAmbience().hasCustomAmbience()) {
//                    int count = 1;
//                    for (SoundFile i : sessionItem.getAmbience().getAmbience()) {
//                        TableItems.add(new AmbienceSongWithNumber(count, i));
//                        count++;
//                    }
//                    AmbienceItemsTable.setItems(TableItems);
//                }
//                calculateduration();
//                syncbuttons();
//            } catch (IOException ignored) {}
//        }
//        private void tableselectionchanged() {
//            int index = AmbienceItemsTable.getSelectionModel().getSelectedIndex();
//            if (index != -1) {selectedtableitem = AmbienceItemsTable.getItems().get(index);}
//            else {selectedtableitem = null;}
//            RemoveButton.setDisable(index == -1);
//            MoveMenuButton.setDisable(index == -1);
//            MoveUp.setDisable(index == -1 || index == 0);
//            MoveDown.setDisable(index == -1 || index == AmbienceItemsTable.getItems().size() - 1);
//            PreviewButton.setDisable(index == -1);
//        }
//        public boolean ambiencealreadyadded(File file) {
//            if (CustomAmbienceList != null) {
//                for (SoundFile i : CustomAmbienceList) {
//                    if (file.equals(i.getFile())) {
//                        return true;
//                    }
//                }
//            }
//            return false;
//        }
//        public void addfiles() {
//            List<File> filesselected = new FileChooser().showOpenMultipleDialog(null);
//            if (filesselected == null || filesselected.isEmpty()) {
//                return;
//            }
//            if (Util.list_hasduplicates(filesselected)) {
//                if (!new ConfirmationDialog(Root.getPreferences(), "Confirmation", "Duplicate Files Detected", "Include Duplicate Files?", "Include", "Discard").getResult()) {
//                    filesselected = Util.list_removeduplicates(filesselected);
//                }
//            }
//            for (File i : filesselected) {
//                if (ambiencealreadyadded(i)) {
//                    continue;
//                }
//                if (Util.audio_isValid(i)) {
//                    MediaPlayer calculatedurationplayer = new MediaPlayer(new Media(i.toURI().toString()));
//                    List<File> finalFilesselected = filesselected;
//                    calculatedurationplayer.setOnReady(() -> {
//                        SoundFile x = new SoundFile(i);
//                        x.setDuration(calculatedurationplayer.getTotalDuration().toMillis());
//                        CustomAmbienceList.add(x);
//                        TableItems.add(new AmbienceSongWithNumber(finalFilesselected.indexOf(i), x));
//                        orderambience();
//                        AmbienceItemsTable.setItems(TableItems);
//                        calculateduration();
//                        calculatedurationplayer.dispose();
//                    });
//                }
//            }
//            syncbuttons();
//        }
//        public void addambience() {
//            SessionPlaybackOverview_SelectAmbience selectAmbience = new SessionPlaybackOverview_SelectAmbience(sessionItem);
//            selectAmbience.showAndWait();
//            if (selectAmbience.getResult()) {
//                TableItems.addAll(selectAmbience.getSoundfiles());
//                orderambience();
//                AmbienceItemsTable.setItems(TableItems);
//                calculateduration();
//            }
//            syncbuttons();
//        }
//        public void orderambience() {
//            int count = 1;
//            for (AmbienceSongWithNumber i : TableItems) {
//                i.setNumber(count);
//                count++;
//            }
//        }
//        public void removeambience() {
//            if (selectedtableitem != null) {
//                if (new ConfirmationDialog(Root.getPreferences(), "Remove Ambience", "Really Remove '" + selectedtableitem.getName() + "'?", "", "Remove", "Cancel").getResult()) {
//                    int index = TableItems.indexOf(selectedtableitem);
//                    TableItems.remove(index);
//                    CustomAmbienceList.remove(index);
//                    orderambience();
//                    AmbienceItemsTable.setItems(TableItems);
//                    calculateduration();
//                }
//            }
//            syncbuttons();
//        }
//        public Duration getcurrenttotal() {
//            Duration duration = Duration.ZERO;
//            for (SoundFile i : CustomAmbienceList) {
//                duration = duration.add(Duration.millis(i.getDuration()));
//            }
//            return duration;
//        }
//        public void syncbuttons() {
//            boolean isemptyorhassingleitem = TableItems.isEmpty() || TableItems.size() == 1;
//            int selectedindex = AmbienceItemsTable.getSelectionModel().getSelectedIndex();
//            boolean noitemselected = selectedindex == -1;
//            MoveMenuButton.setDisable(isemptyorhassingleitem);
//            MoveUp.setDisable(isemptyorhassingleitem || noitemselected || AmbienceItemsTable.getSelectionModel().getSelectedIndex() == 0);
//            MoveDown.setDisable(isemptyorhassingleitem || noitemselected || AmbienceItemsTable.getSelectionModel().getSelectedIndex() == AmbienceItemsTable.getItems().size() - 1);
//            AddMenuButton.setDisable(longenough);
//            AddFiles.setDisable(longenough);
//            AddAmbience.setDisable(longenough);
//            RemoveButton.setDisable(noitemselected);
//            PreviewButton.setDisable(noitemselected);
//            AcceptButton.setDisable(! longenough);
//        }
//        public void calculateduration() {
//            System.out.println(TotalDurationTextField.getLayoutBounds().getWidth());
//            if (getcurrenttotal().lessThan(sessionItem.getduration())) {
//                Duration timeleft = sessionItem.getduration().subtract(getcurrenttotal());
//                TotalDurationTextField.setStyle("-fx-text-fill: red;");
//                TotalDurationTextField.setText(Util.formatdurationtoStringSpelledOut(timeleft, TotalDurationTextField.getLayoutBounds().getWidth()) + " Remaining");
//                longenough = false;
//                AddMenuButton.setDisable(false);
//            } else {
//                TotalDurationTextField.setStyle("-fx-text-fill: white;");
//                TotalDurationTextField.setText("Ambience Is Long Enough");
//                longenough = true;
//                AddMenuButton.setDisable(true);
//            }
//        }
//        public void moveupintable() {
//            int selectedindex = AmbienceItemsTable.getSelectionModel().getSelectedIndex();
//            if (selectedindex > 0) {
//                Collections.swap(TableItems, selectedindex, selectedindex - 1);
//                Collections.swap(CustomAmbienceList, selectedindex, selectedindex - 1);
//                AmbienceItemsTable.setItems(TableItems);
//                calculateduration();
//            }
//        }
//        public void movedownintable() {
//            int selectedindex = AmbienceItemsTable.getSelectionModel().getSelectedIndex();
//            if (selectedindex != -1 && selectedindex != TableItems.size() - 1) {
//                Collections.swap(TableItems, selectedindex, selectedindex + 1);
//                Collections.swap(CustomAmbienceList, selectedindex, selectedindex + 1);
//                AmbienceItemsTable.setItems(TableItems);
//                calculateduration();
//            }
//        }
//        public void preview() {
//            if (selectedtableitem != null) {
//                PreviewFile previewFile = new PreviewFile(selectedtableitem.getFile(), Root, this, false);
//                previewFile.showAndWait();
//            }
//        }
//        public List<SoundFile> getCustomAmbienceList() {
//            return CustomAmbienceList;
//        }
//        public boolean getResult() {
//            return result;
//        }
//        public void accept() {
//            if (getcurrenttotal().lessThan(sessionItem.getduration())) {
//                new InformationDialog(Root.getPreferences(), "Ambience Too Short", "Need At Least " + sessionItem.getdurationasString(false, 50) + " To Set This As Custom Ambience", "");
//            } else {
//                result = true;
//                close();
//            }
//        }
//
//    }
//    public class SessionPlaybackOverview_SelectAmbience extends Stage {
//        public Label TopLabel;
//        public TableView<AmbienceSong> AmbienceTable;
//        public TableColumn<AmbienceSong, String> NameColumn;
//        public TableColumn<AmbienceSong, String> DurationColumn;
//        public Button PreviewButton;
//        public Button AddButton;
//        public Button CancelButton;
//        private boolean result = false;
//        private List<AmbienceSongWithNumber> soundfiles = new ArrayList<>();
//        private ObservableList<AmbienceSong> ambienceSongs = FXCollections.observableArrayList();
//
//        public SessionPlaybackOverview_SelectAmbience(SessionItem SessionItem) {
//            try {
//                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/SelectAmbience.fxml"));
//                fxmlLoader.setController(this);
//                Scene defaultscene = new Scene(fxmlLoader.load());
//                setScene(defaultscene);
//                getIcons().clear();
//                getIcons().add(PROGRAM_ICON);
//                String themefile = Root.getPreferences().getUserInterfaceOptions().getThemefile();
//                if (themefile != null) {getScene().getStylesheets().add(themefile);}
//                setResizable(false);
//                setTitle("Add Ambience");
//                PreviewButton.setDisable(true);
//                AddButton.setDisable(true);
//                NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
//                NameColumn.setStyle( "-fx-alignment: CENTER-LEFT;");
//                DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
//                AmbienceTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> tableselectionchanged(newValue));
//                AmbienceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//                ambienceSongs.addAll(SessionItem.getAmbience().getAvailableAmbience().stream().map(AmbienceSong::new).collect(Collectors.toList()));
//                AmbienceTable.setItems(ambienceSongs);
//            } catch (IOException ignored) {}
//        }
//
//        private void tableselectionchanged(AmbienceSong ambienceSong) {
//            AddButton.setDisable(AmbienceTable.getSelectionModel().getSelectedItems().isEmpty());
//            PreviewButton.setDisable(AmbienceTable.getSelectionModel().getSelectedItems().size() != 1);
//        }
//        public void preview() {
//            if (! PreviewButton.isDisabled()) {
//                File file = ambienceSongs.get(AmbienceTable.getSelectionModel().getSelectedIndex()).getFile();
//                new PreviewFile(file, Root, this, false).showAndWait();
//            }
//        }
//        public void addfiles() {
//            soundfiles.addAll(AmbienceTable.getSelectionModel().getSelectedItems().stream().map(i -> new AmbienceSongWithNumber(AmbienceTable.getSelectionModel().getSelectedItems().indexOf(i), i)).collect(Collectors.toList()));
//            result = true;
//            close();
//        }
//        public boolean getResult() {
//            return result;
//        }
//        public List<AmbienceSongWithNumber> getSoundfiles() {
//            return soundfiles;
//        }
//
//    }
}