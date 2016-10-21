package kujiin.ui.dialogs;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import kujiin.ui.MainController;
import kujiin.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ChangeProgramOptions extends Stage {
    public CheckBox TooltipsCheckBox;
    public CheckBox HelpDialogsCheckBox;
    public CheckBox FadeSwitch;
    public TextField FadeInValue;
    public TextField FadeOutValue;
    public TextField EntrainmentVolumePercentage;
    public TextField AmbienceVolumePercentage;
    public ChoiceBox<String> ProgramThemeChoiceBox;
    public Button CloseButton;
    public Button DeleteAllGoalsButton;
    public Button DeleteAllSessionsProgressButton;
    public Button DefaultsButton;
    public CheckBox ReferenceSwitch;
    public CheckBox RampSwitch;
    public Button AddNewThemeButton;
    public Label DescriptionBoxTopLabel;
    public TextArea DescriptionTextField;
    public CheckBox AlertFileSwitch;
    public CheckBox PrePostRamp;
    public TextField ScrollIncrement;
    private kujiin.xml.Options Options;
    private ArrayList<ItemWithDescription> descriptionitems = new ArrayList<>();
    private MainController Root;

    public ChangeProgramOptions(MainController Root) {
        try {
            this.Root = Root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/ChangeProgramOptions.fxml"));
            fxmlLoader.setController(this);
            Options = Root.getOptions();
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            Options.setStyle(this);
            setResizable(false);
            setTitle("Preferences");
            setuplisteners();
            setuptooltips();
            setupdescriptions();
            populatefromxml();
            referencetoggle();
        } catch (IOException e) {new ExceptionDialog(Options, e).showAndWait();}
    }

    // Setup Methods
    public void populatefromxml() {
        // User Interface Options
        TooltipsCheckBox.setSelected(Options.getUserInterfaceOptions().getTooltips());
        HelpDialogsCheckBox.setSelected(Options.getUserInterfaceOptions().getHelpdialogs());
        ScrollIncrement.setText(Options.getUserInterfaceOptions().getScrollincrement().toString());
        populateappearancecheckbox();
        // Session Options
        if (Options.getSessionOptions().getAlertfunction()) {
            AlertFileSwitch.setSelected(Options.hasValidAlertFile());
            if (! AlertFileSwitch.isSelected()) {Options.getSessionOptions().setAlertfunction(false); Options.getSessionOptions().setAlertfilelocation(null);}
        } else {AlertFileSwitch.setSelected(false);}
        AlertFileSwitch.setOnAction(event -> alertfiletoggled());
        RampSwitch.setSelected(Options.getSessionOptions().getRampenabled());
        PrePostRamp.setSelected(Options.getSessionOptions().getPrepostrampenabled());
        FadeSwitch.setSelected(Options.getSessionOptions().getFadeenabled());
        FadeInValue.setText(String.format("%.2f", Options.getSessionOptions().getFadeinduration()));
        FadeInValue.setDisable(! FadeSwitch.isSelected());
        FadeOutValue.setText(String.format("%.2f", Options.getSessionOptions().getFadeoutduration()));
        FadeOutValue.setDisable(! FadeSwitch.isSelected());
        EntrainmentVolumePercentage.setText(String.valueOf(new Double(Options.getSessionOptions().getEntrainmentvolume() * 100).intValue()));
        AmbienceVolumePercentage.setText(String.valueOf(new Double(Options.getSessionOptions().getAmbiencevolume() * 100).intValue()));

    }
    public void setuptooltips() {
        TooltipsCheckBox.setTooltip(new Tooltip("Display Messages Like These When Hovering Over Program Controls"));
        HelpDialogsCheckBox.setTooltip(new Tooltip("Display Help Dialogs"));
        AlertFileSwitch.setTooltip(new Tooltip("Alert File Is A Sound File Played In Between Different Session Parts"));
        RampSwitch.setTooltip(new Tooltip("Enable A Ramp In Between Session Parts To Smooth Mental Transition"));
        FadeInValue.setTooltip(new Tooltip("Seconds To Fade In Audio Into Session Part"));
        FadeOutValue.setTooltip(new Tooltip("Seconds To Fade Out Audio Out Of Session Part"));
        EntrainmentVolumePercentage.setTooltip(new Tooltip("Default Volume Percentage For Entrainment (Changeable In Session)"));
        AmbienceVolumePercentage.setTooltip(new Tooltip("Default Volume Percentage For Ambience (Changeable In Session)"));
        DeleteAllGoalsButton.setTooltip(new Tooltip("Delete ALL Goals Past, Present And Completed (This CANNOT Be Undone)"));
        DeleteAllSessionsProgressButton.setTooltip((new Tooltip("Delete ALL Sessions Past, Present And Completed (This CANNOT Be Undone)")));
    }
    public void setuplisteners() {
        Util.custom_textfield_double(FadeInValue, 0.0, kujiin.xml.Options.FADE_VALUE_MAX_DURATION, 1, 1);
        Util.custom_textfield_double(FadeOutValue, 0.0, kujiin.xml.Options.FADE_VALUE_MAX_DURATION, 1, 1);
        Util.custom_textfield_integer(EntrainmentVolumePercentage, 1, 100, 5);
        Util.custom_textfield_integer(EntrainmentVolumePercentage, 1, 100, 5);
        Util.custom_textfield_integer(ScrollIncrement, 1, 10, 1);
        ProgramThemeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectnewtheme());
        FadeSwitch.setOnAction(event -> togglefade());
        RampSwitch.setOnAction(event -> toggleramp());
        PrePostRamp.setOnAction(event -> toggleprepostramp());
        ReferenceSwitch.setOnMouseClicked(event -> referencetoggle());
        DeleteAllSessionsProgressButton.setOnAction(event -> deleteallsessions());
        DeleteAllGoalsButton.setOnAction(event -> deleteallgoals());
        AddNewThemeButton.setOnAction(event -> addnewtheme());
        DefaultsButton.setOnAction(event -> resettodefaults());
        CloseButton.setOnAction(event -> close());
    }

    // Description Box Methods
    public void setupdescriptions() {
        descriptionitems.add(new ItemWithDescription("Tool Tips Checkbox", "Display/Don't Display Description Messages When Hovering Over Program Controls"));
        descriptionitems.add(new ItemWithDescription("Help Dialogs Checkbox", "Display/Don't Display Additional Dialogs Explaining Various Features Of The Program"));
        descriptionitems.add(new ItemWithDescription("Fade Checkbox", "Fade In/Out Volume Of Each Session Part To Make For A Smoother Playback Experience"));
        descriptionitems.add(new ItemWithDescription("Fade In", "Seconds To Fade In From Silent Into Each Session Part"));
        descriptionitems.add(new ItemWithDescription("Fade Out", "Seconds To Fade Out To Silent Into Each Session Part"));
        descriptionitems.add(new ItemWithDescription("Entrainment Volume", "Default Volume Percentage For Entrainment\n(Can Be Adjusted In Session)"));
        descriptionitems.add(new ItemWithDescription("Ambience Volume", "Default Volume Percentage For Ambience\n(Can Be Adjusted In Session)"));
        descriptionitems.add(new ItemWithDescription("Alert File", "An Alert File Is An Optional Sound File Played In Between Session Elements"));
        descriptionitems.add(new ItemWithDescription("Display Reference", "Default To Display/Don't Display Reference Files During Session Playback\n(Can Be Changed In Session)"));
        descriptionitems.add(new ItemWithDescription("Ramp Checkbox", "Enable/Disable A Ramp In Session Parts To Smooth Mental Transition"));
        descriptionitems.add(new ItemWithDescription("Pre/Post Ramp Checkbox", "Add A Ramp For Pre And Post Even If They Are Not In Session"));
        descriptionitems.add(new ItemWithDescription("Delete Session Button", "This Button Will Permanently Delete All Session Progress And Reset All Cut/Elements Progress"));
        descriptionitems.add(new ItemWithDescription("Delete Goal Button", "This Button Will Permanently Delete All Current And Completed Goals"));
        descriptionitems.add(new ItemWithDescription("Appearance Selection", "List Of The Available Appearance Themes For The Program"));
        descriptionitems.add(new ItemWithDescription("Add New Theme Button", "Add A New Theme To The List Of Available Themes"));
        TooltipsCheckBox.setOnMouseEntered(event -> populatedescriptionbox(0));
        TooltipsCheckBox.setOnMouseExited(event -> cleardescription());
        HelpDialogsCheckBox.setOnMouseEntered(event -> populatedescriptionbox(1));
        HelpDialogsCheckBox.setOnMouseExited(event -> cleardescription());
        FadeSwitch.setOnMouseEntered(event -> populatedescriptionbox(2));
        FadeSwitch.setOnMouseExited(event -> cleardescription());
        FadeInValue.setOnMouseEntered(event -> populatedescriptionbox(3));
        FadeInValue.setOnMouseExited(event -> cleardescription());
        FadeOutValue.setOnMouseEntered(event -> populatedescriptionbox(4));
        FadeOutValue.setOnMouseExited(event -> cleardescription());
        EntrainmentVolumePercentage.setOnMouseEntered(event -> populatedescriptionbox(5));
        EntrainmentVolumePercentage.setOnMouseExited(event -> cleardescription());
        AmbienceVolumePercentage.setOnMouseEntered(event -> populatedescriptionbox(6));
        AmbienceVolumePercentage.setOnMouseExited(event -> cleardescription());
        AlertFileSwitch.setOnMouseEntered(event -> populatedescriptionbox(6));
        AlertFileSwitch.setOnMouseExited(event -> cleardescription());
        ReferenceSwitch.setOnMouseEntered(event -> populatedescriptionbox(8));
        ReferenceSwitch.setOnMouseExited(event -> cleardescription());
        RampSwitch.setOnMouseEntered(event -> populatedescriptionbox(9));
        RampSwitch.setOnMouseExited(event -> cleardescription());
        PrePostRamp.setOnMouseEntered(event -> populatedescriptionbox(10));
        PrePostRamp.setOnMouseExited(event -> cleardescription());
        DeleteAllSessionsProgressButton.setOnMouseEntered(event -> populatedescriptionbox(11));
        DeleteAllSessionsProgressButton.setOnMouseExited(event -> cleardescription());
        DeleteAllGoalsButton.setOnMouseEntered(event -> populatedescriptionbox(12));
        DeleteAllGoalsButton.setOnMouseExited(event -> cleardescription());
        ProgramThemeChoiceBox.setOnMouseEntered(event -> populatedescriptionbox(13));
        ProgramThemeChoiceBox.setOnMouseExited(event -> cleardescription());
        AddNewThemeButton.setOnMouseEntered(event -> populatedescriptionbox(14));
        AddNewThemeButton.setOnMouseExited(event -> cleardescription());
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

    // Alert File Methods
    public void alertfiletoggled() {
        if (AlertFileSwitch.isSelected()) {
            SelectAlertFile selectAlertFile = new SelectAlertFile(Root);
            selectAlertFile.showAndWait();
            AlertFileSwitch.setSelected(Options.getSessionOptions().getAlertfunction() && Options.hasValidAlertFile());
        }
    }

    // Reference Methods
    public void referencetoggle() {
        Options.getSessionOptions().setReferenceoption(ReferenceSwitch.isSelected());
        if (ReferenceSwitch.isSelected()) {
            SelectReferenceType selectReferenceType = new SelectReferenceType(Root);
            selectReferenceType.showAndWait();
            if (selectReferenceType.getResult()) {
                Options.getSessionOptions().setReferencetype(selectReferenceType.getReferenceType());
                Options.getSessionOptions().setReferencefullscreen(selectReferenceType.getFullScreen());
            }
        }
    }

    // Ramp Methods
    public void toggleramp() {
        PrePostRamp.setSelected(RampSwitch.isSelected());
        Options.getSessionOptions().setRampenabled(RampSwitch.isSelected());
        if (RampSwitch.isSelected()) {toggleprepostramp();}
    }
    public void toggleprepostramp() {
        Options.getSessionOptions().setPrepostrampenabled(PrePostRamp.isSelected());
    }

    // Fade Methods
    public void togglefade() {
        Options.getSessionOptions().setFadeenabled(FadeSwitch.isSelected());
        FadeInValue.setDisable(! FadeSwitch.isSelected());
        FadeOutValue.setDisable(! FadeSwitch.isSelected());
    }

    // Appearance Methods
    public void populateappearancecheckbox() {
        ProgramThemeChoiceBox.setItems(FXCollections.observableArrayList(Options. getUserInterfaceOptions().getThemefilenames()));
        try {
            int index = Options.getUserInterfaceOptions().getThemefiles().indexOf(Options.getUserInterfaceOptions().getThemefile());
            ProgramThemeChoiceBox.getSelectionModel().select(index);
        } catch (Exception ignored) {}
    }
    public void addnewtheme() {
        File newfile = new FileChooser().showOpenDialog(this);
        if (newfile == null) {return;}
        Options.addthemefile(newfile.getName().substring(0, newfile.getName().lastIndexOf(".")), newfile.toURI().toString());
        populateappearancecheckbox();
    }
    public void selectnewtheme() {
        int index = ProgramThemeChoiceBox.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            Options. getUserInterfaceOptions().setThemefile(Options.getUserInterfaceOptions().getThemefiles().get(index));
            getScene().getStylesheets().clear();
            getScene().getStylesheets().add(Options. getUserInterfaceOptions().getThemefile());
        }
    }

    // Button Actions
    public void resettodefaults() {
        if (new ConfirmationDialog(Options, "Reset To Defaults", null, "Reset All Values To Defaults? You Will Lose Any Unsaved Changes", "Reset", "Cancel").getResult()) {
            Options.resettodefaults();
            populatefromxml();
        }
    }
    public void deleteallsessions() {
        if (new ConfirmationDialog(Options, "Confirmation", null, "This Will Permanently And Irreversible Delete All Sessions Progress And Reset The Progress Tracker", "Delete?", "Cancel").getResult()) {
            if (! kujiin.xml.Options.SESSIONSXMLFILE.delete()) {new ErrorDialog(Options, "Error", "Couldn't Delete Sessions File", "Check File Permissions For: " + kujiin.xml.Options.SESSIONSXMLFILE.getAbsolutePath());}
            else {new InformationDialog(Options, "Success", null, "Successfully Delete Sessions And Reset All Progress");}
        }
    }
    public void deleteallgoals() {
        if (new ConfirmationDialog(Options, "Confirmation", null, "This Will Permanently And Irreversible Delete All Goals Completed And Current", "Delete", "Cancel").getResult()) {
            if (! kujiin.xml.Options.SESSIONSXMLFILE.delete()) {new ErrorDialog(Options, "Error", "Couldn't Delete Goals File", "Check File Permissions For: " + kujiin.xml.Options.GOALSXMLFILE.getAbsolutePath());}
            else {new InformationDialog(Options, "Success", null, "Successfully Deleted All Goals");}
        }
    }

    @Override
    public void close() {
        Options.getUserInterfaceOptions().setTooltips(TooltipsCheckBox.isSelected());
        Options.getUserInterfaceOptions().setScrollincrement(Integer.parseInt(ScrollIncrement.getText()));
        Options.getUserInterfaceOptions().setHelpdialogs(HelpDialogsCheckBox.isSelected());
        Options.getSessionOptions().setEntrainmentvolume(new Double(EntrainmentVolumePercentage.getText()) / 100);
        Options.getSessionOptions().setAmbiencevolume(new Double(AmbienceVolumePercentage.getText()) / 100);
        Options.getSessionOptions().setFadeoutduration(new Double(FadeOutValue.getText()));
        Options.getSessionOptions().setFadeinduration(new Double(FadeInValue.getText()));
        Options.getSessionOptions().setRampenabled(RampSwitch.isSelected());
        Options.getSessionOptions().setReferenceoption(ReferenceSwitch.isSelected());
        Options.marshall();
        super.close();
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
