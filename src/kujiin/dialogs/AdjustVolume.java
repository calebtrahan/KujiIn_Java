package kujiin.dialogs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import kujiin.Cut;
import kujiin.Root;

import java.io.IOException;

public class AdjustVolume extends Stage {
    public Slider EntrainmentSlider;
    public Slider AmbienceSlider;

    public AdjustVolume(Cut currentcut) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/AdjustSessionVolume.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Adjust Session Volume");}
        catch (IOException e) {e.printStackTrace();}
        if (currentcut.getCurrentAmbiencePlayer() != null) {currentcut.getCurrentAmbiencePlayer().volumeProperty().bind(AmbienceSlider.valueProperty());}
        else {AmbienceSlider.setDisable(true);}
        if (currentcut.getCurrentEntrainmentPlayer() != null) {currentcut.getCurrentEntrainmentPlayer().volumeProperty().bind(EntrainmentSlider.valueProperty());}
        else {EntrainmentSlider.setDisable(true);}
        AmbienceSlider.setValue(Root.AMBIENCEVOLUME);
        EntrainmentSlider.setValue(Root.ENTRAINMENTVOLUME);
    }

    public Double getEntrainmentVolume() {return EntrainmentSlider.getValue();}
    public Double getAmbienceVolume() {return AmbienceSlider.getValue();}

}
