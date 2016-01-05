package kujiin.widgets;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import kujiin.Root;
import kujiin.This_Session;
import kujiin.Tools;
import kujiin.dialogs.ChangeAllValuesDialog;
import kujiin.util.interfaces.Widget;
import kujiin.util.lib.GuiUtils;
import kujiin.util.states.CreatorState;
import kujiin.util.states.ExporterState;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class CreatorAndExporterWidget implements Widget{
    private Button changeallvaluesbutton;
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
    private ArrayList<Integer> textfieldtimes = new ArrayList<>(11);

    // TODO Encapsulate All Creator And Exporter Logic Here (Maybe Put ChangeAllValues As A Subclass?)
    public CreatorAndExporterWidget(Button changeallvaluesbutton, Button exportButton, Button loadpresetbutton, Button savepresetbutton, CheckBox ambienceswitch,
                                    TextField totalSessionTimeTextField, TextField approximateendtime, TextField preTime, TextField rinTime, TextField kyoTime,
                                    TextField tohTime, TextField shaTime, TextField kaiTime, TextField jinTime,
                                    TextField retsuTime, TextField zaiTime, TextField zenTime, TextField postTime, This_Session this_session) {
        this.changeallvaluesbutton = changeallvaluesbutton;
        ExportButton = exportButton;
        this.loadpresetbutton = loadpresetbutton;
        this.savepresetbutton = savepresetbutton;
        AmbienceSwitch = ambienceswitch;
        TotalSessionTime = totalSessionTimeTextField;
        ApproximateEndTime = approximateendtime;
        PreTime = preTime;
        RinTime = rinTime;
        KyoTime = kyoTime;
        TohTime = tohTime;
        ShaTime = shaTime;
        KaiTime = kaiTime;
        JinTime = jinTime;
        RetsuTime = retsuTime;
        ZaiTime = zaiTime;
        ZenTime = zenTime;
        PostTime = postTime;
        this.this_session = this_session;
        setSessionInformation(this_session);
        creatorState = CreatorState.NOT_CREATED;
        exporterState = ExporterState.IDLE;
        maketextfieldsnumeric();
        textfieldtimes.addAll(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        bindtextfieldstoproperties();
        TotalSessionTime.setOnKeyTyped(Root.noneditabletextfield);
        ApproximateEndTime.setOnKeyTyped(Root.noneditabletextfield);
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
        else {GuiUtils.showerrordialog("Error", "At Least One Cut's Value (Pre + Post Excluded) Must Be Greater Than 0", "Session Not Valid"); return false;}
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
            GuiUtils.showerrordialog("Error", "Cannot Export. Missing FFMpeg", "Please Install FFMpeg To Use The Export Feature");
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
    public void disableeditingfortextfields() {
        TotalSessionTime.setEditable(false);
        PreTime.setEditable(false);
        RinTime.setEditable(false);
        KyoTime.setEditable(false);
        TohTime.setEditable(false);
        ShaTime.setEditable(false);
        KaiTime.setEditable(false);
        JinTime.setEditable(false);
        RetsuTime.setEditable(false);
        ZaiTime.setEditable(false);
        ZenTime.setEditable(false);
        PostTime.setEditable(false);
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
    }
    public boolean gettextfieldtimes() {
        Boolean not_all_zeros = false;
        for (Integer i : textfieldtimes) {if (i > 0) {not_all_zeros = true;}}
        return  not_all_zeros;
    }
    public void checkambience() {
        if (AmbienceSwitch.isSelected()) {
            if (gettextfieldtimes()) {
                this_session.checkifambienceisgood(textfieldtimes);
            } else {
                GuiUtils.showinformationdialog("Information", "All Cut Durations Are Zero", "Please Increase Cut(s) Durations Before Checking This");
                AmbienceSwitch.setSelected(false);
            }
        } else {this_session.setAmbienceenabled(false);}
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
}
