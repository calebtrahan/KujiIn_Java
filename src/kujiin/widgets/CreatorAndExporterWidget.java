package kujiin.widgets;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import kujiin.ChangeSessionValues;
import kujiin.Cut;
import kujiin.This_Session;
import kujiin.util.interfaces.Widget;
import kujiin.util.states.CreatorState;
import kujiin.util.states.ExporterState;

import java.util.ArrayList;

public class CreatorAndExporterWidget implements Widget{
    private Button CreateButton;
    private Button ExportButton;
    private Button loadpresetbutton;
    private Button savepresetbutton;
    private TextField AmbienceEnabledTextField;
    private TextField TotalSessionTimeTextField;
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
    private CreatorState creatorState;
    private ExporterState exporterState;
    private ChangeSessionValues changeSessionValues;
    private This_Session this_session;

    public CreatorAndExporterWidget(Button createButton, Button exportButton, Button loadpresetbutton, Button savepresetbutton, TextField ambienceEnabledTextField,
                                    TextField totalSessionTimeTextField, TextField preTime, TextField rinTime, TextField kyoTime,
                                    TextField tohTime, TextField shaTime, TextField kaiTime, TextField jinTime,
                                    TextField retsuTime, TextField zaiTime, TextField zenTime, TextField postTime, This_Session this_session) {
        CreateButton = createButton;
        ExportButton = exportButton;
        this.loadpresetbutton = loadpresetbutton;
        this.savepresetbutton = savepresetbutton;
        AmbienceEnabledTextField = ambienceEnabledTextField;
        TotalSessionTimeTextField = totalSessionTimeTextField;
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
        disableeditingfortextfields();
    }

// Getters And Setters
    public void setCreatorState(CreatorState creatorState) {this.creatorState = creatorState;}
    public CreatorState getCreatorState() {return creatorState;}

// Button Actions
    public void createsession() {
        if (changeSessionValues == null) {
            changeSessionValues = new ChangeSessionValues(this_session);}
        changeSessionValues.showAndWait();
    }
    public void exportsession() {}
    public void setSessionInformation(This_Session session) {
        ArrayList<Cut> cutsinsession = session.getallCuts();
        PreTime.setText(Integer.toString(cutsinsession.get(0).duration));
        RinTime.setText(Integer.toString(cutsinsession.get(1).duration));
        KyoTime.setText(Integer.toString(cutsinsession.get(2).duration));
        TohTime.setText(Integer.toString(cutsinsession.get(3).duration));
        ShaTime.setText(Integer.toString(cutsinsession.get(4).duration));
        KaiTime.setText(Integer.toString(cutsinsession.get(5).duration));
        JinTime.setText(Integer.toString(cutsinsession.get(6).duration));
        RetsuTime.setText(Integer.toString(cutsinsession.get(7).duration));
        ZaiTime.setText(Integer.toString(cutsinsession.get(8).duration));
        ZenTime.setText(Integer.toString(cutsinsession.get(9).duration));
        PostTime.setText(Integer.toString(cutsinsession.get(10).duration));
        if (session.isValid()) {
            if (session.getAmbienceenabled()) {AmbienceEnabledTextField.setText("Yes");}
            else {AmbienceEnabledTextField.setText("No");}
            TotalSessionTimeTextField.setText(session.gettotalsessionduration());
            enable();
        } else {
            AmbienceEnabledTextField.setText("Not A Valid Session");
            TotalSessionTimeTextField.setText("Not A Valid Session");
            disable();
        }
    }
    public void disableeditingfortextfields() {
        AmbienceEnabledTextField.setEditable(false);
        TotalSessionTimeTextField.setEditable(false);
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

// Widget Implementation
    @Override
    public void disable() {
        AmbienceEnabledTextField.setDisable(true);
        TotalSessionTimeTextField.setDisable(true);
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
        CreateButton.setDisable(true);
        ExportButton.setDisable(true);
        loadpresetbutton.setDisable(true);
        savepresetbutton.setDisable(true);
    }
    @Override
    public void enable() {
        AmbienceEnabledTextField.setDisable(false);
        TotalSessionTimeTextField.setDisable(false);
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
        CreateButton.setDisable(false);
        ExportButton.setDisable(false);
        loadpresetbutton.setDisable(false);
        savepresetbutton.setDisable(false);
    }
    @Override
    public void resetallvalues() {
        AmbienceEnabledTextField.setText("No Session Created");
        TotalSessionTimeTextField.setText("No Session Created");
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
