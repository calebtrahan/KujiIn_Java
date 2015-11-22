package kujiin.widgets;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import kujiin.util.interfaces.Widget;
import kujiin.util.states.CreatorState;
import kujiin.util.states.ExporterState;

public class CreatorAndExporterWidget implements Widget{
    private Button CreateButton;
    private Button ExportButton;
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

    public CreatorAndExporterWidget(Button createButton, Button exportButton, TextField ambienceEnabledTextField,
                                    TextField totalSessionTimeTextField, TextField preTime, TextField rinTime, TextField kyoTime,
                                    TextField tohTime, TextField shaTime, TextField kaiTime, TextField jinTime,
                                    TextField retsuTime, TextField zaiTime, TextField zenTime, TextField postTime) {
        CreateButton = createButton;
        ExportButton = exportButton;
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
        creatorState = CreatorState.IDLE;
        exporterState = ExporterState.IDLE;
    }

// Button Actions
    public void createsession() {}
    public void exportsession() {}

// Widget Implementation
    @Override
    public void disable() {
        CreateButton.setDisable(true);
        ExportButton.setDisable(true);
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
    @Override
    public void enable() {
        CreateButton.setDisable(false);
        ExportButton.setDisable(false);
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
