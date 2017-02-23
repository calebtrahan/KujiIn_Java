package kujiin.ui.dialogs;

import javafx.stage.Stage;

public class CreateNewSessionOld extends Stage {
//    public ToggleButton PreSwitch;
//    public ToggleButton RinSwitch;
//    public ToggleButton KyoSwitch;
//    public ToggleButton TohSwitch;
//    public ToggleButton ShaSwitch;
//    public ToggleButton KaiSwitch;
//    public ToggleButton JinSwitch;
//    public ToggleButton RetsuSwitch;
//    public ToggleButton ZaiSwitch;
//    public ToggleButton ZenSwitch;
//    public ToggleButton EarthSwitch;
//    public ToggleButton AirSwitch;
//    public ToggleButton FireSwitch;
//    public ToggleButton WaterSwitch;
//    public ToggleButton VoidSwitch;
//    public ToggleButton PostSwitch;
//    public TextField PreTime;
//    public TextField RinTime;
//    public TextField KyoTime;
//    public TextField TohTime;
//    public TextField ShaTime;
//    public TextField KaiTime;
//    public TextField JinTime;
//    public TextField RetsuTime;
//    public TextField ZaiTime;
//    public TextField ZenTime;
//    public TextField EarthTime;
//    public TextField AirTime;
//    public TextField FireTime;
//    public TextField WaterTime;
//    public TextField VoidTime;
//    public TextField PostTime;
//    public TextField TotalSessionTime;
//    public Button ChangeAllCutsButton;
//    public Button ChangeAllElementsButton;
//    public Button ResetCreatorButton;
//    public Button OKButton;
//    public Button CancelButton;
//    private Session Session;
//
//    public CreateNewSessionOld() {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/CreateNewSession.fxml"));
//            fxmlLoader.setController(this);
//            Scene scene = new Scene(fxmlLoader.load());
//            setScene(scene);
//            Session = new Session();
//        } catch (IOException e) {e.printStackTrace();}
//    }
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        Util.custom_textfield_integer(PreTime, PreSwitch, 0, 999 ,1);
//        Util.custom_textfield_integer(RinTime, RinSwitch, 0, 999 ,1);
//        Util.custom_textfield_integer(KyoTime, KyoSwitch, 0, 999 ,1);
//        Util.custom_textfield_integer(TohTime, TohSwitch, 0, 999 ,1);
//        Util.custom_textfield_integer(ShaTime, ShaSwitch, 0, 999 ,1);
//        Util.custom_textfield_integer(KaiTime, KaiSwitch, 0, 999 ,1);
//        Util.custom_textfield_integer(JinTime, JinSwitch, 0, 999 ,1);
//        Util.custom_textfield_integer(RetsuTime, RetsuSwitch, 0, 999 ,1);
//        Util.custom_textfield_integer(ZaiTime, ZaiSwitch, 0, 999 ,1);
//        Util.custom_textfield_integer(ZenTime, ZenSwitch, 0, 999 ,1);
//        Util.custom_textfield_integer(EarthTime, EarthSwitch, 0, 999 ,1);
//        Util.custom_textfield_integer(AirTime, AirSwitch, 0, 999 ,1);
//        Util.custom_textfield_integer(FireTime, FireSwitch, 0, 999 ,1);
//        Util.custom_textfield_integer(WaterTime, WaterSwitch, 0, 999 ,1);
//        Util.custom_textfield_integer(VoidTime, VoidSwitch, 0, 999 ,1);
//        Util.custom_textfield_integer(PostTime, PostSwitch, 0, 999 ,1);
//        setupupdateduration(PreSwitch, PreTime, 0);
//        PreSwitch.setOnAction(event -> toggleswitch(PreSwitch, PreTime, "Presession"));
//        setupupdateduration(RinSwitch, RinTime, 1);
//        RinSwitch.setOnAction(event -> toggleswitch(RinSwitch, RinTime, "Rin"));
//        setupupdateduration(KyoSwitch, KyoTime, 2);
//        KyoSwitch.setOnAction(event -> toggleswitch(KyoSwitch, KyoTime, "Kyo"));
//        setupupdateduration(TohSwitch, TohTime, 3);
//        TohSwitch.setOnAction(event -> toggleswitch(TohSwitch, TohTime, "Toh"));
//        setupupdateduration(ShaSwitch, ShaTime, 4);
//        ShaSwitch.setOnAction(event -> toggleswitch(ShaSwitch, ShaTime, "Sha"));
//        setupupdateduration(KaiSwitch, KaiTime, 5);
//        KaiSwitch.setOnAction(event -> toggleswitch(KaiSwitch, KaiTime, "Kai"));
//        setupupdateduration(JinSwitch, JinTime, 6);
//        JinSwitch.setOnAction(event -> toggleswitch(JinSwitch, JinTime, "Jin"));
//        setupupdateduration(RetsuSwitch, RetsuTime, 7);
//        RetsuSwitch.setOnAction(event -> toggleswitch(RetsuSwitch, RetsuTime, "Retsu"));
//        setupupdateduration(ZaiSwitch, ZaiTime, 8);
//        ZaiSwitch.setOnAction(event -> toggleswitch(ZaiSwitch, ZaiTime, "Zai"));
//        setupupdateduration(ZenSwitch, ZenTime, 9);
//        ZenSwitch.setOnAction(event -> toggleswitch(ZenSwitch, ZenTime, "Zen"));
//        setupupdateduration(EarthSwitch, EarthTime, 10);
//        EarthSwitch.setOnAction(event -> toggleswitch(EarthSwitch, EarthTime, "Earth"));
//        setupupdateduration(AirSwitch, AirTime, 11);
//        AirSwitch.setOnAction(event -> toggleswitch(AirSwitch, AirTime, "Air"));
//        setupupdateduration(FireSwitch, FireTime, 12);
//        FireSwitch.setOnAction(event -> toggleswitch(FireSwitch, FireTime, "Fire"));
//        setupupdateduration(WaterSwitch, WaterTime, 13);
//        WaterSwitch.setOnAction(event -> toggleswitch(WaterSwitch, WaterTime, "Water"));
//        setupupdateduration(VoidSwitch, VoidTime, 14);
//        VoidSwitch.setOnAction(event -> toggleswitch(VoidSwitch, VoidTime, "Void"));
//        setupupdateduration(PostSwitch, PostTime, 15);
//        PostSwitch.setOnAction(event -> toggleswitch(PostSwitch, PostTime, "Postsession"));
//    }
//
//// Getters And Setters
//    public kujiin.xml.Session getSession() {
//        return Session;
//    }
//
//// Utility Methods
//    public void updatetotalduration() {
//        if (Session.gettotalsessionduration().greaterThan(Duration.ZERO)) {
//            TotalSessionTime.setText(Util.formatdurationtoStringSpelledOut(Session.gettotalsessionduration(), TotalSessionTime.getLayoutBounds().getWidth()));
//        } else {TotalSessionTime.setText("-");}
//    }
//    public void setupupdateduration(ToggleButton toggleButton, TextField textField, int sessionpartnumber) {
//        toggleButton.textProperty().addListener((observable, oldValue, newValue) -> {
//            try {
//                Session.updateduration(sessionpartnumber, Duration.minutes(Integer.parseInt(textField.getText())));
//                updatetotalduration();
//            } catch (NumberFormatException ignored) {Session.updateduration(sessionpartnumber, Duration.ZERO);}
//        });
//    }
//    public void toggleswitch(ToggleButton toggleButton, TextField textField, String name) {
//        if (toggleButton.isSelected()) {
//            textField.setText("0");
//            textField.setDisable(false);
//            textField.setTooltip(new Tooltip("Practice Time For " + name + " (In Minutes)"));
//        } else {
//            textField.setText("0");
//            textField.setDisable(true);
//            textField.setTooltip(new Tooltip(name + " Is Disabled. Click " + name + " Button Above To Enable"));
//        }
//    }
//
//// Subclasses
//    public class SelectPresetAmbience {}
//    public class SetSessionOrder {}

}