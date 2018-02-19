package kujiin.ui.dialogs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.Duration;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.xml.SoundFile;

import java.io.IOException;
import java.time.LocalDate;

public class SelectDate extends StyledStage {
    public DatePicker DateSelector;
    public Button AcceptButton;
    public Button CancelButton;
    private boolean Accepted;

    public SelectDate(String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/SelectDate.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            setTitle(title);
            Accepted = false;
            DateSelector.setValue(LocalDate.now());
            AcceptButton.setOnAction(event -> {
                Accepted = true;
                close();
            });
            CancelButton.setOnAction(event -> {
                Accepted = false;
                close();
            });
        } catch (IOException e) {e.printStackTrace();}
    }

    public LocalDate getDate() {return DateSelector.getValue();}
    public boolean isAccepted() {
        return Accepted;
    }

}