package kujiin.ui.dialogs;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import kujiin.ui.MainController;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.ui.dialogs.alerts.ErrorDialog;
import kujiin.ui.dialogs.alerts.ExceptionDialog;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.util.enums.QuickAddAmbienceType;
import kujiin.util.enums.ReferenceType;
import kujiin.xml.Preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ChangeProgramOptions extends StyledStage {
// User Interface & Creation Tab
    public CheckBox TooltipsCheckBox;
    public CheckBox HelpDialogsCheckBox;
    public ChoiceBox<String> AmbienceTypeChoiceBox;
    public Spinner<Integer> ScrollIncrement;
// Session Tab
    public CheckBox AlertFileSwitch;
    public CheckBox RampSwitch;
    public CheckBox ReferenceSwitch;
    public ChoiceBox<String> ReferenceTypeChoiceBox;
// Playback Tab
    public CheckBox FadeAnimation_PlaySwitch;
    public Spinner<Double> FadeAnimation_PlayValue;
    public CheckBox FadeAnimation_StopSwitch;
    public Spinner<Double> FadeAnimation_StopValue;
    public CheckBox FadeAnimation_ResumeSwitch;
    public Spinner<Double> FadeAnimation_ResumeValue;
    public CheckBox FadeAnimation_PauseSwitch;
    public Spinner<Double> FadeAnimation_PauseValue;
    public Spinner<Integer> EntrainmentVolumePercentage;
    public Spinner<Integer> AmbienceVolumePercentage;
// Advanced Tab
    public Button DeleteAllGoalsButton;
    public Button DeleteAllSessionsProgressButton;
    public CheckBox DebugModeCheckbox;
// Gerneral Dialog Controls
    public Button CloseButton;
    public Button DefaultsButton;
    public Label DescriptionBoxTopLabel;
    public TextArea DescriptionTextField;
// Fields
    private Preferences Preferences;
    private ArrayList<ItemWithDescription> descriptionitems = new ArrayList<>();
    private MainController Root;

    public ChangeProgramOptions(MainController Root) {
        try {
            this.Root = Root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/ChangeProgramOptions.fxml"));
            fxmlLoader.setController(this);
            Preferences = Root.getPreferences();
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            setTitle("Preferences");
            setuplisteners();
            setuptooltips();
            setupdescriptions();
            populatefromxml();
            referencetoggled();
        } catch (IOException e) {new ExceptionDialog(Preferences, e).showAndWait();}
    }

// Setup Methods
    public void populatefromxml() {
    // User Interface Tab
        TooltipsCheckBox.setSelected(Preferences.getUserInterfaceOptions().getTooltips());
        HelpDialogsCheckBox.setSelected(Preferences.getUserInterfaceOptions().getHelpdialogs());
    // Creation Tab
        ScrollIncrement.getValueFactory().setValue(Preferences.getCreationOptions().getScrollincrement());
    // Session Tab
        if (Preferences.getSessionOptions().getAlertfunction()) {
            AlertFileSwitch.setSelected(Preferences.hasValidAlertFile());
            if (! AlertFileSwitch.isSelected()) {
                Preferences.getSessionOptions().setAlertfunction(false); Preferences.getSessionOptions().setAlertfilelocation(null);}
        } else {AlertFileSwitch.setSelected(false);}
        RampSwitch.setSelected(Preferences.getSessionOptions().getRampenabled());
        ReferenceSwitch.setSelected(Preferences.getSessionOptions().getReferenceoption());
        ReferenceTypeChoiceBox.setItems(FXCollections.observableArrayList(Arrays.asList("HTML", "TEXT")));
        switch (Preferences.getSessionOptions().getReferencetype()) {
            case html:
                ReferenceTypeChoiceBox.getSelectionModel().select(0);
                break;
            case txt:
                ReferenceTypeChoiceBox.getSelectionModel().select(1);
                break;
        }
        AmbienceTypeChoiceBox.setItems(FXCollections.observableArrayList(Arrays.asList("Repeat", "Shuffle")));
        switch (Preferences.getCreationOptions().getQuickaddambiencetype()) {
            case REPEAT:
                AmbienceTypeChoiceBox.getSelectionModel().select(0);
                break;
            case SHUFFLE:
                AmbienceTypeChoiceBox.getSelectionModel().select(1);
                break;
        }
    // Playback Tab
        FadeAnimation_PlaySwitch.setSelected(Preferences.getPlaybackOptions().getAnimation_fade_play_enabled());
        FadeAnimation_PlayValue.getValueFactory().setValue(Preferences.getPlaybackOptions().getAnimation_fade_play_value());
        FadeAnimation_PlayValue.setDisable(FadeAnimation_PlaySwitch.isSelected());
        toggleplayfade();
        FadeAnimation_StopSwitch.setSelected(Preferences.getPlaybackOptions().getAnimation_fade_stop_enabled());
        FadeAnimation_StopValue.getValueFactory().setValue(Preferences.getPlaybackOptions().getAnimation_fade_stop_value());
        FadeAnimation_StopValue.setDisable(FadeAnimation_StopSwitch.isSelected());
        togglestopfade();
        FadeAnimation_ResumeSwitch.setSelected(Preferences.getPlaybackOptions().getAnimation_fade_resume_enabled());
        FadeAnimation_ResumeValue.getValueFactory().setValue(Preferences.getPlaybackOptions().getAnimation_fade_resume_value());
        FadeAnimation_ResumeValue.setDisable(FadeAnimation_ResumeSwitch.isSelected());
        toggleresumefade();
        FadeAnimation_PauseSwitch.setSelected(Preferences.getPlaybackOptions().getAnimation_fade_pause_enabled());
        FadeAnimation_PauseValue.getValueFactory().setValue(Preferences.getPlaybackOptions().getAnimation_fade_pause_value());
        FadeAnimation_PauseValue.setDisable(FadeAnimation_PauseSwitch.isSelected());
        togglepausefade();
    // Advanced Tab
        DebugModeCheckbox.setSelected(Preferences.getAdvancedOptions().isDebugmode());
    }
    public void setuptooltips() {
//        TooltipsCheckBox.setTooltip(new Tooltip("Display Messages Like These When Hovering Over Program Controls"));
//        HelpDialogsCheckBox.setTooltip(new Tooltip("Display Help Dialogs"));
//        AlertFileSwitch.setTooltip(new Tooltip("Alert File Is A Sound File Played In Between Different Session Parts"));
//        RampSwitch.setTooltip(new Tooltip("Enable A Ramp In Between Session Parts To Smooth Mental Transition"));
//        FadeInValue.setTooltip(new Tooltip("Seconds To Fade In Audio Into Session Part"));
//        FadeOutValue.setTooltip(new Tooltip("Seconds To Fade Out Audio Out Of Session Part"));
//        EntrainmentVolumePercentage.setTooltip(new Tooltip("Default Volume Percentage For PlaybackItemEntrainment (Changeable In Session)"));
//        AmbienceVolumePercentage.setTooltip(new Tooltip("Default Volume Percentage For Ambience (Changeable In Session)"));
//        DeleteAllGoalsButton.setTooltip(new Tooltip("Delete ALL Goals Past, Present And Completed (This CANNOT Be Undone)"));
//        DeleteAllSessionsProgressButton.setTooltip((new Tooltip("Delete ALL Sessions Past, Present And Completed (This CANNOT Be Undone)")));
    }
    public void setuplisteners() {
    // User Interface Tab
        TooltipsCheckBox.setOnAction(event -> Preferences.getUserInterfaceOptions().setTooltips(TooltipsCheckBox.isSelected()));
        HelpDialogsCheckBox.setOnAction(event -> Preferences.getUserInterfaceOptions().setHelpdialogs(HelpDialogsCheckBox.isSelected()));
    // Creation Tab
        ScrollIncrement.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, Preferences.getCreationOptions().getScrollincrement()));
        ScrollIncrement.setOnScroll(event -> {
            Integer newvalue = ScrollIncrement.getValue();
            if (event.getDeltaY() < 0) {newvalue -= 1;} else {newvalue += 1;}
            ScrollIncrement.getValueFactory().setValue(newvalue);
        });
    // Session Tab
        AlertFileSwitch.setOnAction(event -> alertfiletoggled());
        RampSwitch.setOnAction(event -> toggleramp());
        ReferenceSwitch.setOnMouseClicked(event -> referencetoggled());
        ReferenceTypeChoiceBox.setOnAction(event -> referencetypetoggled());
        AmbienceTypeChoiceBox.setOnAction(event -> ambiencetypechanged());
    // Playback Tab
        FadeAnimation_PlayValue.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 30.0, Preferences.getPlaybackOptions().getAnimation_fade_play_value(), 0.5));
        FadeAnimation_PlayValue.setOnScroll(event -> {
            Double newvalue = FadeAnimation_PlayValue.getValue();
            if (event.getDeltaY() < 0) {newvalue -= 0.5;} else {newvalue += 0.5;}
            FadeAnimation_PlayValue.getValueFactory().setValue(newvalue);
        });
        FadeAnimation_PlaySwitch.setOnAction(event -> toggleplayfade());
        FadeAnimation_StopValue.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 30.0, Preferences.getPlaybackOptions().getAnimation_fade_stop_value(), 0.5));
        FadeAnimation_StopValue.setOnScroll(event -> {
            Double newvalue = FadeAnimation_StopValue.getValue();
            if (event.getDeltaY() < 0) {newvalue -= 0.5;} else {newvalue += 0.5;}
            FadeAnimation_StopValue.getValueFactory().setValue(newvalue);
        });
        FadeAnimation_StopSwitch.setOnAction(event -> togglestopfade());
        FadeAnimation_PauseValue.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10.0, Preferences.getPlaybackOptions().getAnimation_fade_pause_value(), 0.5));
        FadeAnimation_PauseValue.setOnScroll(event -> {
            Double newvalue = FadeAnimation_PauseValue.getValue();
            if (event.getDeltaY() < 0) {newvalue -= 0.5;} else {newvalue += 0.5;}
            FadeAnimation_PauseValue.getValueFactory().setValue(newvalue);
        });
        FadeAnimation_PauseSwitch.setOnAction(event -> togglepausefade());
        FadeAnimation_ResumeValue.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10.0, Preferences.getPlaybackOptions().getAnimation_fade_resume_value(), 0.5));
        FadeAnimation_ResumeValue.setOnScroll(event -> {
            Double newvalue = FadeAnimation_ResumeValue.getValue();
            if (event.getDeltaY() < 0) {newvalue -= 0.5;} else {newvalue += 0.5;}
            FadeAnimation_ResumeValue.getValueFactory().setValue(newvalue);
        });
        FadeAnimation_ResumeSwitch.setOnAction(event -> toggleresumefade());
        EntrainmentVolumePercentage.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, new Double(Preferences.getPlaybackOptions().getEntrainmentvolume() * 100).intValue()));
        EntrainmentVolumePercentage.setOnScroll(event -> {
            Integer newvalue = EntrainmentVolumePercentage.getValue();
            if (event.getDeltaY() < 0) {newvalue -= 1;} else {newvalue += 1;}
            EntrainmentVolumePercentage.getValueFactory().setValue(newvalue);
        });
        AmbienceVolumePercentage.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, new Double(Preferences.getPlaybackOptions().getAmbiencevolume() * 100).intValue()));
        AmbienceVolumePercentage.setOnScroll(event -> {
            Integer newvalue = AmbienceVolumePercentage.getValue();
            if (event.getDeltaY() < 0) {newvalue -= 1;} else {newvalue += 1;}
            AmbienceVolumePercentage.getValueFactory().setValue(newvalue);
        });
    // Advanced Tab
        DeleteAllSessionsProgressButton.setOnAction(event -> deleteallsessions());
        DeleteAllGoalsButton.setOnAction(event -> deleteallgoals());
        DebugModeCheckbox.setOnAction(event -> Preferences.getAdvancedOptions().setDebugmode(DebugModeCheckbox.isSelected()));
    // Button Actions
        DefaultsButton.setOnAction(event -> resettodefaults());
        CloseButton.setOnAction(event -> close());
    }
    @Override
    public void close() {
        Preferences.getCreationOptions().setScrollincrement(ScrollIncrement.getValue());
        if (FadeAnimation_PlaySwitch.isSelected()) {Preferences.getPlaybackOptions().setAnimation_fade_play_value(FadeAnimation_PlayValue.getValue());}
        if (FadeAnimation_StopSwitch.isSelected()) {Preferences.getPlaybackOptions().setAnimation_fade_stop_value(FadeAnimation_StopValue.getValue());}
        if (FadeAnimation_ResumeSwitch.isSelected()) {Preferences.getPlaybackOptions().setAnimation_fade_resume_value(FadeAnimation_ResumeValue.getValue());}
        if (FadeAnimation_PauseSwitch.isSelected()) {Preferences.getPlaybackOptions().setAnimation_fade_pause_value(FadeAnimation_PauseValue.getValue());}
        Preferences.getPlaybackOptions().setEntrainmentvolume((double) (EntrainmentVolumePercentage.getValue() / 100));
        Preferences.getPlaybackOptions().setAmbiencevolume((double) AmbienceVolumePercentage.getValue() / 100);
        Preferences.marshall();
        super.close();
    }

// User Interface & Creation Tab Tab
    public void ambiencetypechanged() {
    switch (AmbienceTypeChoiceBox.getSelectionModel().getSelectedIndex()) {
        case 0:
            Preferences.getCreationOptions().setQuickaddambiencetype(QuickAddAmbienceType.REPEAT);
            break;
        case 1:
            Preferences.getCreationOptions().setQuickaddambiencetype(QuickAddAmbienceType.SHUFFLE);
            break;
    }
}

// Session Tab
    // Alert File
    public void alertfiletoggled() {
        if (AlertFileSwitch.isSelected()) {
            SelectAlertFile selectAlertFile = new SelectAlertFile(Root);
            selectAlertFile.initModality(Modality.APPLICATION_MODAL);
            selectAlertFile.initOwner(this);
            selectAlertFile.showAndWait();
            AlertFileSwitch.setSelected(Preferences.getSessionOptions().getAlertfunction() && Preferences.hasValidAlertFile());
        }
    }
    // Ramp
    public void toggleramp() {
        Preferences.getSessionOptions().setRampenabled(RampSwitch.isSelected());
    }
    // Reference
    public void referencetoggled() {
//        Preferences.getSessionOptions().setReferenceoption(ReferenceSwitch.isSelected());
//        if (ReferenceSwitch.isSelected()) {
//            SelectReferenceType selectReferenceType = new SelectReferenceType(Root, this, false, Root.getAllSessionParts(false));
//            selectReferenceType.showAndWait();
//            if (selectReferenceType.getResult()) {
//                Preferences.getSessionOptions().setReferencetype(selectReferenceType.getReferenceType());
//                switch (Preferences.getSessionOptions().getReferencetype()) {
//                    case html:
//                        ReferenceTypeChoiceBox.getSelectionModel().select(0);
//                        break;
//                    case txt:
//                        ReferenceTypeChoiceBox.getSelectionModel().select(1);
//                        break;
//                }
//                Preferences.getSessionOptions().setReferencefullscreen(selectReferenceType.getFullScreen());
//                ReferenceFullScreenCheckBox.setSelected(selectReferenceType.getFullScreen());
//            } else {
//                Preferences.getSessionOptions().setReferenceoption(false);
//                ReferenceSwitch.setSelected(false);
//                ReferenceFullScreenCheckBox.setDisable(true);
//                ReferenceTypeChoiceBox.setDisable(true);
//            }
//        }
    }
    public void referencetypetoggled() {
        switch (ReferenceTypeChoiceBox.getSelectionModel().getSelectedIndex()) {
            case 0:
                Preferences.getSessionOptions().setReferencetype(ReferenceType.html);
                break;
            case 1:
                Preferences.getSessionOptions().setReferencetype(ReferenceType.txt);
                break;
        }
    }

// Playback Tab
    // Fade Animations
    public void toggleplayfade() {
        Preferences.getPlaybackOptions().setAnimation_fade_play_enabled(FadeAnimation_PlaySwitch.isSelected());
        FadeAnimation_PlayValue.setDisable(! FadeAnimation_PlaySwitch.isSelected());
    }
    public void togglestopfade() {
        Preferences.getPlaybackOptions().setAnimation_fade_stop_enabled(FadeAnimation_StopSwitch.isSelected());
        FadeAnimation_StopValue.setDisable(! FadeAnimation_StopSwitch.isSelected());
    }
    public void toggleresumefade() {
        Preferences.getPlaybackOptions().setAnimation_fade_resume_enabled(FadeAnimation_ResumeSwitch.isSelected());
        FadeAnimation_ResumeValue.setDisable(! FadeAnimation_ResumeSwitch.isSelected());
    }
    public void togglepausefade() {
        Preferences.getPlaybackOptions().setAnimation_fade_pause_enabled(FadeAnimation_PauseSwitch.isSelected());
        FadeAnimation_PauseValue.setDisable(! FadeAnimation_PauseSwitch.isSelected());
    }

// Advanced Tab
    public void deleteallsessions() {
    if (new ConfirmationDialog(Preferences, "Confirmation", null, "This Will Permanently And Irreversible Delete All Sessions Progress And Reset The Progress Tracker", "Delete?", "Cancel").getResult()) {
        if (! kujiin.xml.Preferences.SESSIONSXMLFILE.delete()) {new ErrorDialog(Preferences, "Error", "Couldn't Delete Sessions File", "Check File Permissions For: " + kujiin.xml.Preferences.SESSIONSXMLFILE.getAbsolutePath());}
        else {new InformationDialog(Preferences, "Success", null, "Successfully Delete Sessions And Reset All Progress");}
    }
}
    public void deleteallgoals() {
        if (new ConfirmationDialog(Preferences, "Confirmation", null, "This Will Permanently And Irreversible Delete All Goals Completed And Current", "Delete", "Cancel").getResult()) {
            if (! kujiin.xml.Preferences.SESSIONSXMLFILE.delete()) {new ErrorDialog(Preferences, "Error", "Couldn't Delete Goals File", "Check File Permissions For: " + kujiin.xml.Preferences.GOALSXMLFILE.getAbsolutePath());}
            else {new InformationDialog(Preferences, "Success", null, "Successfully Deleted All Goals");}
        }
    }

// General Dialog Controls
    // Description Box
    public void setupdescriptions() {
        descriptionitems.add(new ItemWithDescription("Tool Tips Checkbox", "Display Description Messages When Hovering Over Program Controls"));
        descriptionitems.add(new ItemWithDescription("Help Dialogs Checkbox", "Display Additional Dialogs Explaining Various Features Of The Program"));
        descriptionitems.add(new ItemWithDescription("Appearance Selection", "List Of The Available Appearance Themes For The Program"));
        descriptionitems.add(new ItemWithDescription("Add New Theme Button", "Add A New Theme To The List Of Available Themes"));
        descriptionitems.add(new ItemWithDescription("Scroll Increment", "The Amount The Value Will Be Incremented/Decremented On Mouse Scroll"));
        descriptionitems.add(new ItemWithDescription("Alert File", "An Alert File Is An Optional Sound File Played In Between Session Elements\n(Will Prompt For Long Session Playback)"));
        descriptionitems.add(new ItemWithDescription("Ramp Checkbox", "Enable/Disable A Ramp In Session Parts To Smooth Mental Transition"));
        descriptionitems.add(new ItemWithDescription("Pre/Post Ramp Checkbox", "Add A Ramp For Pre And Post Even If They Are Not In Session"));
        descriptionitems.add(new ItemWithDescription("Display Reference", "Default To Display Reference Files During Session Playback\n(Can Be Toggled In Session Player)"));
        descriptionitems.add(new ItemWithDescription("Reference Type", "Select Which Reference Type You Want To Display During Session Playaback\n(Can Be Toggled In Session Player)"));
        descriptionitems.add(new ItemWithDescription("Reference Full Screen", "Display Reference Files Full Screen"));
        descriptionitems.add(new ItemWithDescription("Ambience", "Default To Enable/Disable Ambience Playback During Session"));
        descriptionitems.add(new ItemWithDescription("Ambience Type", "Default Ambience Quick Add Type In Session Creator"));
        descriptionitems.add(new ItemWithDescription("Playback Fade Animation", "Enable/Disable Fade In Audio When Playing"));
        descriptionitems.add(new ItemWithDescription("Playback Fade Animation", "Seconds To Fade In To Playback From Silent When Playing"));
        descriptionitems.add(new ItemWithDescription("Stop Fade Animation", "Enable/Disable Fade Out Audio When When Stopping"));
        descriptionitems.add(new ItemWithDescription("Stop Fade Animation", "Seconds To Fade Out To Silent From Playback When Stopping"));
        descriptionitems.add(new ItemWithDescription("Resume Fade Animation", "Enable/Disable Fade In Audio When When Resuming"));
        descriptionitems.add(new ItemWithDescription("Resume Fade Animation", "Seconds To Fade In From Silent Into Playback When Resuming"));
        descriptionitems.add(new ItemWithDescription("Pause Fade Animation", "Enable/Disable Fade Out Audio When When Pausing"));
        descriptionitems.add(new ItemWithDescription("Pause Fade Animation", "Seconds To Fade Out To Silent From Playback Playback When Pausing"));
        descriptionitems.add(new ItemWithDescription("PlaybackItemEntrainment Volume", "Default Volume Percentage For PlaybackItemEntrainment\n(Can Be Adjusted In Session)"));
        descriptionitems.add(new ItemWithDescription("Ambience Volume", "Default Volume Percentage For Ambience\n(Can Be Adjusted In Session)"));
        descriptionitems.add(new ItemWithDescription("Ambience Editor Default", "The Default Ambience Editor To Open When Adding/Editing Ambiences"));
        descriptionitems.add(new ItemWithDescription("Delete Session Button", "This Button Will Permanently Delete All Session Progress And Reset All Cut/Elements Progress"));
        descriptionitems.add(new ItemWithDescription("Delete Goal Button", "This Button Will Permanently Delete All Current And Completed Goals"));
        descriptionitems.add(new ItemWithDescription("Debug Mode", "For People Who Want To Help Me Find Bugs In My Program, This Will Display Alot Of Programming Jargon In The Console"));
        TooltipsCheckBox.setOnMouseEntered(event -> populatedescriptionbox(0));
        TooltipsCheckBox.setOnMouseExited(event -> cleardescription());
        HelpDialogsCheckBox.setOnMouseEntered(event -> populatedescriptionbox(1));
        HelpDialogsCheckBox.setOnMouseExited(event -> cleardescription());
        ScrollIncrement.setOnMouseEntered(event -> populatedescriptionbox(4));
        ScrollIncrement.setOnMouseExited(event -> cleardescription());
        AlertFileSwitch.setOnMouseEntered(event -> populatedescriptionbox(5));
        AlertFileSwitch.setOnMouseExited(event -> cleardescription());
        RampSwitch.setOnMouseEntered(event -> populatedescriptionbox(6));
        RampSwitch.setOnMouseExited(event -> cleardescription());
        ReferenceSwitch.setOnMouseEntered(event -> populatedescriptionbox(8));
        ReferenceSwitch.setOnMouseExited(event -> cleardescription());
        ReferenceTypeChoiceBox.setOnMouseEntered(event -> populatedescriptionbox(9));
        ReferenceTypeChoiceBox.setOnMouseExited(event -> cleardescription());
        AmbienceTypeChoiceBox.setOnMouseEntered(event -> populatedescriptionbox(12));
        AmbienceTypeChoiceBox.setOnMouseExited(event -> cleardescription());
        FadeAnimation_PlaySwitch.setOnMouseEntered(event -> populatedescriptionbox(13));
        FadeAnimation_PlaySwitch.setOnMouseExited(event -> cleardescription());
        FadeAnimation_PlayValue.setOnMouseEntered(event -> populatedescriptionbox(14));
        FadeAnimation_PlayValue.setOnMouseExited(event -> cleardescription());
        FadeAnimation_StopSwitch.setOnMouseEntered(event -> populatedescriptionbox(15));
        FadeAnimation_StopSwitch.setOnMouseExited(event -> cleardescription());
        FadeAnimation_StopValue.setOnMouseEntered(event -> populatedescriptionbox(16));
        FadeAnimation_StopValue.setOnMouseExited(event -> cleardescription());
        FadeAnimation_ResumeSwitch.setOnMouseEntered(event -> populatedescriptionbox(17));
        FadeAnimation_ResumeSwitch.setOnMouseExited(event -> cleardescription());
        FadeAnimation_ResumeValue.setOnMouseEntered(event -> populatedescriptionbox(18));
        FadeAnimation_ResumeValue.setOnMouseExited(event -> cleardescription());
        FadeAnimation_PauseSwitch.setOnMouseEntered(event -> populatedescriptionbox(19));
        FadeAnimation_PauseSwitch.setOnMouseExited(event -> cleardescription());
        FadeAnimation_PauseValue.setOnMouseEntered(event -> populatedescriptionbox(20));
        FadeAnimation_PauseValue.setOnMouseExited(event -> cleardescription());
        EntrainmentVolumePercentage.setOnMouseEntered(event -> populatedescriptionbox(21));
        EntrainmentVolumePercentage.setOnMouseExited(event -> cleardescription());
        AmbienceVolumePercentage.setOnMouseEntered(event -> populatedescriptionbox(22));
        AmbienceVolumePercentage.setOnMouseExited(event -> cleardescription());
        DeleteAllSessionsProgressButton.setOnMouseEntered(event -> populatedescriptionbox(24));
        DeleteAllSessionsProgressButton.setOnMouseExited(event -> cleardescription());
        DeleteAllGoalsButton.setOnMouseEntered(event -> populatedescriptionbox(25));
        DeleteAllGoalsButton.setOnMouseExited(event -> cleardescription());
        DebugModeCheckbox.setOnMouseEntered(event -> populatedescriptionbox(26));
        DebugModeCheckbox.setOnMouseExited(event -> cleardescription());
    }
    public void populatedescriptionbox(int index) {
        ItemWithDescription item = descriptionitems.get(index);
        DescriptionBoxTopLabel.setText(item.getName());
        DescriptionTextField.setText(item.getDescription());
    }
    public void cleardescription() {
        DescriptionBoxTopLabel.setText("Description");
        DescriptionTextField.setText("");
    }
    // Button Actions
    public void resettodefaults() {
        if (new ConfirmationDialog(Preferences, "Reset To Defaults", null, "Reset All Values To Defaults? You Will Lose Any Unsaved Changes", "Reset", "Cancel").getResult()) {
            Preferences.resettodefaults();
            populatefromxml();
        }
    }

    class ItemWithDescription {
        private final String name;
        private final String description;

        public ItemWithDescription(String name, String description) {
            this.name = name + " Description";
            this.description = description;
        }

        public String getName() {
            return name;
        }
        public String getDescription() {
            return description;
        }
    }
}
