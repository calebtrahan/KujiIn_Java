package kujiin.widgets;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import kujiin.Cut;
import kujiin.MainController;
import kujiin.This_Session;
import kujiin.Tools;
import kujiin.interfaces.Widget;
import kujiin.xml.Session;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ResourceBundle;

public class CreatorAndExporterWidget implements Widget {
    private MainController Root;
    private Button changeallvaluesbutton;
    private Button exportButton;
    private Button ExportButton;
    private Button loadpresetbutton;
    private Button savepresetbutton;
    private CheckBox AmbienceSwitch;
    private TextField TotalSessionTime;
    private TextField ApproximateEndTime;
    private TextField PreTime;
    private TextField RinTime;
    private TextField KyoTime;
    private TextField TohTime;
    private TextField ShaTime;
    private TextField KaiTime;
    private TextField JinTime;
    private TextField RetsuTime;
    private TextField ZaiTime;
    private TextField ZenTime;
    private TextField PostTime;
    private IntegerProperty PresessionValue = new SimpleIntegerProperty(0);
    private IntegerProperty RinValue = new SimpleIntegerProperty(0);
    private IntegerProperty KyoValue = new SimpleIntegerProperty(0);
    private IntegerProperty TohValue = new SimpleIntegerProperty(0);
    private IntegerProperty ShaValue = new SimpleIntegerProperty(0);
    private IntegerProperty KaiValue = new SimpleIntegerProperty(0);
    private IntegerProperty JinValue = new SimpleIntegerProperty(0);
    private IntegerProperty RetsuValue = new SimpleIntegerProperty(0);
    private IntegerProperty ZaiValue = new SimpleIntegerProperty(0);
    private IntegerProperty ZenValue = new SimpleIntegerProperty(0);
    private IntegerProperty PostsessionValue = new SimpleIntegerProperty(0);
    private CreatorState creatorState;
    private ExporterState exporterState;
    private This_Session this_session;
    private Label StatusBar;
    private ArrayList<Integer> textfieldtimes = new ArrayList<>(11);

    public CreatorAndExporterWidget(MainController mainController) {
        Root = mainController;
        this.exportButton = Root.ExportButton;
        this.loadpresetbutton = Root.LoadPresetButton;
        this.savepresetbutton = Root.SavePresetButton;
        this.changeallvaluesbutton = Root.ChangeValuesButton;
        ExportButton = Root.ExportButton;
        AmbienceSwitch = Root.AmbienceSwitch;
        TotalSessionTime = Root.TotalSessionTime;
        ApproximateEndTime = Root.ApproximateEndTime;
        PreTime = Root.PreTime;
        RinTime = Root.RinTime;
        KyoTime = Root.KyoTime;
        TohTime = Root.TohTime;
        ShaTime = Root.ShaTime;
        KaiTime = Root.KaiTime;
        JinTime = Root.JinTime;
        RetsuTime = Root.RetsuTime;
        ZaiTime = Root.ZaiTime;
        ZenTime = Root.ZenTime;
        PostTime = Root.PostTime;
        this_session = Root.getThis_session();
        StatusBar = Root.StatusBar;
        setSessionInformation(this_session);
        creatorState = CreatorState.NOT_CREATED;
        exporterState = ExporterState.IDLE;
        maketextfieldsnumeric();
        textfieldtimes.addAll(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        bindtextfieldstoproperties();
        TotalSessionTime.setOnKeyTyped(MainController.noneditabletextfield);
        ApproximateEndTime.setOnKeyTyped(MainController.noneditabletextfield);
    }

// Getters And Setters
    public void setCreatorState(CreatorState creatorState) {this.creatorState = creatorState;}
    public CreatorState getCreatorState() {return creatorState;}
    public ExporterState getExporterState() {
        return exporterState;
    }
    public void setExporterState(ExporterState exporterState) {
        this.exporterState = exporterState;
    }

// Button Actions
    public boolean createsession() {
        if (gettextfieldtimes()) {
            if (Tools.sessionwellformednesschecks(textfieldtimes)) {
                this_session.setAmbienceenabled(AmbienceSwitch.isSelected());
                this_session.create(textfieldtimes);
                return true;
            } else {return false;}
        }
        else {Tools.showerrordialog("Error", "At Least One Cut's Value (Pre + Post Excluded) Must Be Greater Than 0", "Session Not Valid"); return false;}
    }
    public boolean checkforffmpeg() {
        boolean good = false;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList("ffmpeg", "-version"));
            final Process checkforffmpeg = processBuilder.start();
            checkforffmpeg.waitFor();
            good = checkforffmpeg.exitValue() == 0;
        } catch (IOException ignored) {} catch (InterruptedException e) {
            e.printStackTrace();
            good = false;
        }
        return good;
    }
    public void exportsession() {
        if (checkforffmpeg()) {
            createsession();
        } else {
            Tools.showerrordialog("Error", "Cannot Export. Missing FFMpeg", "Please Install FFMpeg To Use The Export Feature");
        }
    }

// Other Methods
    public void setSessionInformation(This_Session session) {
//        ArrayList<Cut> cutsinsession = session.getallCuts();
//        PreTime.setText(Integer.toString(cutsinsession.get(0).duration));
//        RinTime.setText(Integer.toString(cutsinsession.get(1).duration));
//        KyoTime.setText(Integer.toString(cutsinsession.get(2).duration));
//        TohTime.setText(Integer.toString(cutsinsession.get(3).duration));
//        ShaTime.setText(Integer.toString(cutsinsession.get(4).duration));
//        KaiTime.setText(Integer.toString(cutsinsession.get(5).duration));
//        JinTime.setText(Integer.toString(cutsinsession.get(6).duration));
//        RetsuTime.setText(Integer.toString(cutsinsession.get(7).duration));
//        ZaiTime.setText(Integer.toString(cutsinsession.get(8).duration));
//        ZenTime.setText(Integer.toString(cutsinsession.get(9).duration));
//        PostTime.setText(Integer.toString(cutsinsession.get(10).duration));
//        if (session.isValid()) {
//            if (session.getAmbienceenabled()) {AmbienceEnabledTextField.setText("Yes");}
//            else {AmbienceEnabledTextField.setText("No");}
//            TotalSessionTime.setText(session.gettotalsessionduration());
//            enable();
//        } else {
//            AmbienceEnabledTextField.setText("Not A Valid Session");
//            TotalSessionTime.setText("Not A Valid Session");
//            disable();
//        }
    }
    public void bindtextfieldstoproperties() {
        PreTime.textProperty().addListener((observable, oldValue, newValue) -> {PresessionValue.set(Integer.valueOf(newValue)); textfieldtimes.set(0, PresessionValue.get()); updatecreatorui();});
        RinTime.textProperty().addListener((observable, oldValue, newValue) -> {RinValue.set(Integer.valueOf(newValue)); textfieldtimes.set(1, RinValue.get()); updatecreatorui();});
        KyoTime.textProperty().addListener((observable, oldValue, newValue) -> {KyoValue.set(Integer.valueOf(newValue)); textfieldtimes.set(2, KyoValue.get()); updatecreatorui();});
        TohTime.textProperty().addListener((observable, oldValue, newValue) -> {TohValue.set(Integer.valueOf(newValue)); textfieldtimes.set(3, TohValue.get()); updatecreatorui();});
        ShaTime.textProperty().addListener((observable, oldValue, newValue) -> {ShaValue.set(Integer.valueOf(newValue)); textfieldtimes.set(4, ShaValue.get()); updatecreatorui();});
        KaiTime.textProperty().addListener((observable, oldValue, newValue) -> {KaiValue.set(Integer.valueOf(newValue)); textfieldtimes.set(5, KaiValue.get()); updatecreatorui();});
        JinTime.textProperty().addListener((observable, oldValue, newValue) -> {JinValue.set(Integer.valueOf(newValue)); textfieldtimes.set(6, JinValue.get()); updatecreatorui();});
        RetsuTime.textProperty().addListener((observable, oldValue, newValue) -> {RetsuValue.set(Integer.valueOf(newValue)); textfieldtimes.set(7, RetsuValue.get()); updatecreatorui();});
        ZaiTime.textProperty().addListener((observable, oldValue, newValue) -> {ZaiValue.set(Integer.valueOf(newValue)); textfieldtimes.set(8, ZaiValue.get()); updatecreatorui();});
        ZenTime.textProperty().addListener((observable, oldValue, newValue) -> {ZenValue.set(Integer.valueOf(newValue)); textfieldtimes.set(9, ZenValue.get()); updatecreatorui();});
        PostTime.textProperty().addListener((observable, oldValue, newValue) -> {PostsessionValue.set(Integer.valueOf(newValue)); textfieldtimes.set(10, PostsessionValue.get()); updatecreatorui();});
    }
    public void maketextfieldsnumeric() {
        Tools.numericTextField(PreTime);
        Tools.numericTextField(RinTime);
        Tools.numericTextField(KyoTime);
        Tools.numericTextField(TohTime);
        Tools.numericTextField(ShaTime);
        Tools.numericTextField(KaiTime);
        Tools.numericTextField(JinTime);
        Tools.numericTextField(RetsuTime);
        Tools.numericTextField(ZaiTime);
        Tools.numericTextField(ZenTime);
        Tools.numericTextField(PostTime);
    }
    public void updatecreatorui() {
        if (gettextfieldtimes()) {
            Integer totalsessiontime = 0;
            for (Integer i : textfieldtimes) {totalsessiontime += i;}
            TotalSessionTime.setText(Tools.minutestoformattedhoursandmins(totalsessiontime));
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, totalsessiontime);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            ApproximateEndTime.setText(sdf.format(cal.getTime()));
        }
        if (AmbienceSwitch.isSelected()) {
            AmbienceSwitch.setSelected(false);
            this_session.resetambience();
            Tools.showinformationdialog("Information", "Unselected Ambience Because Values Are Changed", "Set All Desired Session Values First, Then Check The Ambience Box Last");
        }
    }
    public boolean gettextfieldtimes() {
        Boolean not_all_zeros = false;
        for (Integer i : textfieldtimes) {if (i > 0) {not_all_zeros = true;}}
        return  not_all_zeros;
    }
    public void checkambience() {
        if (AmbienceSwitch.isSelected()) {
            if (gettextfieldtimes()) {
                this_session.checkifambienceisgood(textfieldtimes, AmbienceSwitch);
            } else {
                Tools.showinformationdialog("Information", "All Cut Durations Are Zero", "Please Increase Cut(s) Durations Before Checking This");
                AmbienceSwitch.setSelected(false);
            }
        } else {
            this_session.resetambience();
            this_session.setAmbienceenabled(false);
        }
    }
    public void changeallvalues() {
        ChangeAllValuesDialog changevaluesdialog = new ChangeAllValuesDialog();
        changevaluesdialog.showAndWait();
        if (changevaluesdialog.getAccepted()) {
            Integer min = changevaluesdialog.getminutes();
            String minutes = min.toString();
            RinTime.setText(minutes);
            KyoTime.setText(minutes);
            TohTime.setText(minutes);
            ShaTime.setText(minutes);
            KaiTime.setText(minutes);
            JinTime.setText(minutes);
            RetsuTime.setText(minutes);
            ZaiTime.setText(minutes);
            ZenTime.setText(minutes);
            if (changevaluesdialog.getincludepresession()) {PreTime.setText(minutes);}
            if (changevaluesdialog.getincludepostsession()) {PostTime.setText(minutes);}
        }
    }

// Widget Implementation
    @Override
    public void disable() {
        changeallvaluesbutton.setDisable(true);
        AmbienceSwitch.setDisable(true);
        ApproximateEndTime.setDisable(true);
        TotalSessionTime.setDisable(true);
        PreTime.setDisable(true);
        RinTime.setDisable(true);
        KyoTime.setDisable(true);
        TohTime.setDisable(true);
        ShaTime.setDisable(true);
        KaiTime.setDisable(true);
        JinTime.setDisable(true);
        RetsuTime.setDisable(true);
        ZaiTime.setDisable(true);
        ZenTime.setDisable(true);
        PostTime.setDisable(true);
    }
    public void disablebuttons() {
        ExportButton.setDisable(true);
        loadpresetbutton.setDisable(true);
        savepresetbutton.setDisable(true);
    }
    @Override
    public void enable() {
        changeallvaluesbutton.setDisable(false);
        AmbienceSwitch.setDisable(false);
        ApproximateEndTime.setDisable(false);
        TotalSessionTime.setDisable(false);
        PreTime.setDisable(false);
        RinTime.setDisable(false);
        KyoTime.setDisable(false);
        TohTime.setDisable(false);
        ShaTime.setDisable(false);
        KaiTime.setDisable(false);
        JinTime.setDisable(false);
        RetsuTime.setDisable(false);
        ZaiTime.setDisable(false);
        ZenTime.setDisable(false);
        PostTime.setDisable(false);
    }
    public void enablebuttons() {
        ExportButton.setDisable(false);
        loadpresetbutton.setDisable(false);
        savepresetbutton.setDisable(false);
    }
    @Override
    public void resetallvalues() {
        AmbienceSwitch.setText("No Session Created");
        TotalSessionTime.setText("No Session Created");
        PreTime.setText("-");
        RinTime.setText("-");
        KyoTime.setText("-");
        TohTime.setText("-");
        ShaTime.setText("-");
        KaiTime.setText("-");
        JinTime.setText("-");
        RetsuTime.setText("-");
        ZaiTime.setText("-");
        ZenTime.setText("-");
        PostTime.setText("-");
    }

// Presets
    public void loadpreset() {
    File xmlfile = new FileChooser().showOpenDialog(null);
    if (xmlfile != null) {
        try {
            JAXBContext context = JAXBContext.newInstance(Session.class);
            Unmarshaller createMarshaller = context.createUnmarshaller();
            Session loadedsession = (Session) createMarshaller.unmarshal(xmlfile);
            if (loadedsession != null) {
                this_session.setupcutsinsession(loadedsession.getallcuttimes());
                Tools.showinformationdialog("Information", "Preset Loaded", "Your Preset Was Successfully Loaded");
            }
        } catch (JAXBException e) {
            Tools.showerrordialog("Error", "Not A Valid Preset File", "Please Select A Valid Preset File");}
    }
}
    public void saveaspreset(Session session) {
        File xmlfile = new FileChooser().showSaveDialog(null);
        if (xmlfile != null) {
            try {
                JAXBContext context = JAXBContext.newInstance(Session.class);
                Marshaller createMarshaller = context.createMarshaller();
                createMarshaller.marshal(session, xmlfile);
                Tools.showinformationdialog("Information", "Preset Saved", "Your Preset Was Successfully Saved");
            } catch (JAXBException e) {
                Tools.showerrordialog("Error", "Couldn't Save Preset", "Your Preset Could Not Be Saved, Do You Have Write Access To That Directory?");}
        } else {Tools.showtimedmessage(StatusBar, "Canceled Saving Preset", 2000);}
    }

// Subclasses/Dialogs
    public static class ChangeAllValuesDialog extends Stage implements Initializable {
        public Button AcceptButton;
        public Button CancelButton;
        public TextField changeAllValuesMinutesTextField;
        public CheckBox PresessionCheckbox;
        public CheckBox PostsessionCheckBox;
        private Boolean accepted;

        public ChangeAllValuesDialog() {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ChangeAllValuesDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Change All Values To:");}
            catch (IOException e) {e.printStackTrace();}
            setAccepted(false);
        }

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            Tools.numericTextField(changeAllValuesMinutesTextField);
        }
    // Getters And Setters
        public Boolean getAccepted() {
        return accepted;
    }
        public void setAccepted(Boolean accepted) {
            this.accepted = accepted;
        }

    // Button Actions
        public void acceptbuttonpressed(Event event) {setAccepted(true); this.close();}
        public void cancelbuttonpressed(Event event) {this.close();}
        public boolean getincludepresession() {return PresessionCheckbox.isSelected();}
        public boolean getincludepostsession() {return PostsessionCheckBox.isSelected();}
        public Integer getminutes() {
            try {return Integer.parseInt(changeAllValuesMinutesTextField.getText());}
            catch (NumberFormatException e) {return 0;}
        }

    }
    public static class ExportingSessionDialog extends Stage {
        public ProgressBar creatingsessionProgressBar;
        public Label creatingsessionTextStatusBar;
        public Button CancelButton;
        private int sessionparts;
        private int currentpartcount;
        This_Session thisSession;
        ArrayList<Cut> cutsinsesession;

        public ExportingSessionDialog(This_Session thisSession) {
    //        this.cutsinsesession = cutsinsesession;
    //        percent = sessionpartialpercent / 100;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ExportingSessionDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Creating This_Session");}
            catch (IOException e) {e.printStackTrace();}
    //        creatingsessionProgressBar.setProgress(0.0);
            this.thisSession = thisSession;
            currentpartcount = 0;
        }

        public void setSessionparts(int sessionparts) {this.sessionparts = sessionparts;}

        public void updateprogress() {
            currentpartcount += 1;
            Platform.runLater(() -> {
    //            System.out.println("Current Part Count: " + currentpartcount);
    //            System.out.println("Total This_Session Parts: " + sessionparts);
                double percent = currentpartcount / sessionparts;
    //            creatingsessionProgressBar.setProgress(percent);
                testifdone();
            });
        }

        public void testifdone() {
    //        if (currentpartcount == sessionparts) {
    //            if (thisSession.getCreated()) {
    //                Alert completed = new Alert(Alert.AlertType.INFORMATION);
    //                completed.setTitle("This_Session Created");
    //                completed.setHeaderText("Completed!");
    //                completed.setContentText("This_Session Creation Complete With No Errors");
    //                completed.showAndWait();
    //                this.close();
    //            } else {
    //                Alert completed = new Alert(Alert.AlertType.ERROR);
    //                completed.setTitle("This_Session Creation Failed");
    //                completed.setHeaderText("Failed!");
    //                completed.setContentText("This_Session Creation Failed");
    //                completed.showAndWait();
    //                this.close();
    //            }
    //        }
        }

        public void displaymessage(String text) {
    //        Platform.runLater(() -> creatingsessionTextStatusBar.setText(text));
        }
    }
    public static class SessionNotWellformedDialog extends Stage {
        public Button returntoCreatorButton;
        public Button addmissingCutsButton;
        public ListView<Text> sessionlistview;
        public Label sessionmissingcutsLabel;
        public Button CreateAnywayButton;
        public Label explanationLabel;
        private ArrayList<Integer> textfieldvalues;
        private int lastcutindex;
        private int invocationduration;
        private boolean createsession;

        public SessionNotWellformedDialog(Parent parent, ArrayList<Integer> textfieldvalues, String cutsmissingtext, int lastcutindex) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionNotWellformedDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Creating This_Session");}
            catch (IOException e) {e.printStackTrace();}
            this.textfieldvalues = textfieldvalues;
            this.lastcutindex = lastcutindex;
            sessionmissingcutsLabel.setText(cutsmissingtext);
            populatelistview();
            explanationLabel.setText(("Your Practiced Cuts Do Not Connect! Due To The Nature Of The Kuji-In I Recommend " +
                    "Connecting All Cuts From RIN All The Way To Your Last Cut (") + This_Session.allnames.get(lastcutindex) +
                    ") Or Your This_Session Might Not Have The Energy It Needs");
            setCreatesession(false);
        }

        public void populatelistview() {
            ArrayList<String> items = new ArrayList<>();
            ObservableList<Text> sessionitems = FXCollections.observableArrayList();
            int count = 0;
            boolean thisitemmissing;
            for (int i = 0; i < textfieldvalues.size(); i++) {
                String name = This_Session.allnames.get(i);
                String minutes;
                if (i <= lastcutindex || i == textfieldvalues.size() - 1) {
                    thisitemmissing = false;
                    if (i == 0 || i == 10) {
                        if (textfieldvalues.get(i) == 0) {
                            minutes = "Ramp Only";
                        } else {
                            String time = Tools.minutestoformattedhoursandmins(textfieldvalues.get(i));
                            minutes = String.format("%s + Ramp", time);
                        }
                    } else {
                        if (textfieldvalues.get(i) == 0) {
                            thisitemmissing = true;
                            minutes = " Missing Value! ";
                        }
                        else {minutes = Tools.minutestoformattedhoursandmins(textfieldvalues.get(i));}
                    }
                    String txt = String.format("%d: %s (%s )", count + 1, name, minutes);
                    Text item = new Text();
                    item.setText(txt);
                    if (thisitemmissing) item.setStyle("-fx-font-weight:bold; -fx-font-style: italic;");
                    sessionitems.add(item);
                    count++;
                }
            }
            sessionlistview.setItems(sessionitems);
        }

        public void returntoCreator(Event event) {this.close();}

        public void addmissingcutstoSession(Event event) {
            CutInvocationDialog cutdurationdialog = new CutInvocationDialog(null);
            cutdurationdialog.showAndWait();
            setInvocationduration(cutdurationdialog.getCutinvocationduration());
            setCreatesession(true);
            this.close();
        }

        public void createSessionwithoutmissingcuts(Event event) {
            if (Tools.getanswerdialog("Confirmation", "Session Not Well-Formed", "Really Create Anyway?")) {
                setCreatesession(true);
                this.close();
            }
        }

        public int getInvocationduration() {
            return invocationduration;
        }

        public void setInvocationduration(int invocationduration) {
            this.invocationduration = invocationduration;
        }

        public boolean isCreatesession() {
            return createsession;
        }

        public void setCreatesession(boolean createsession) {this.createsession = createsession;}
    }
    public static class CutInvocationDialog extends Stage implements Initializable{
        public Button CancelButton;
        public Button OKButton;
        public TextField cutinvocationminutesTextField;
        private int cutinvocationduration;

        CutInvocationDialog(Parent parent) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/CutInvocationDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Creating This_Session");}
            catch (IOException e) {e.printStackTrace();}
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            Tools.numericTextField(cutinvocationminutesTextField);
        }

        public int getCutinvocationduration() {
            return cutinvocationduration;
        }

        public void setCutinvocationduration(int cutinvocationduration) {
            this.cutinvocationduration = cutinvocationduration;
        }

        public void CancelButtonPressed(Event event) {
            setCutinvocationduration(0);
            this.close();
        }

        public void OKButtonPressed(Event event) {
            try {
                int value = Integer.parseInt(cutinvocationminutesTextField.getText());
                if (value != 0) {
                    setCutinvocationduration(value);
                    this.close();
                } else {
                    if (Tools.getanswerdialog("Confirmation", "Cut Invocation Value Is 0", "Continue With Zero Value (These Cuts Won't Be Included)" )) {
                        setCutinvocationduration(0);
                        this.close();
                    }
                }
            } catch (NumberFormatException e) {Tools.showerrordialog("Error", "Value Is Empty", "Enter A Numeric Value Then Press OK");}
        }
    }

// Enums
    public enum CreatorState {
        NOT_CREATED, CREATION_IN_PROGRESS, CREATED
    }
    public enum ExporterState {
        IDLE, EXPORT_IN_PROGRESS, EXPORTED
    }

}
