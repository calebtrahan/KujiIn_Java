package kujiin.util.lib;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.Optional;

public class GuiUtils {
    public static boolean getanswerdialog(String titletext, String headertext, String contenttext) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(titletext);
        a.setHeaderText(headertext);
        a.setContentText(contenttext);
        Optional<ButtonType> answer = a.showAndWait();
        return answer.isPresent() && answer.get() == ButtonType.OK;
    }

    public static void showinformationdialog(String titletext, String headertext, String contexttext) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titletext);
        a.setHeaderText(headertext);
        a.setContentText(contexttext);
        a.showAndWait();
    }

    public static void showerrordialog(String titletext, String headertext, String contenttext) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titletext);
        a.setHeaderText(headertext);
        a.setContentText(contenttext);
        a.showAndWait();
    }

    public static void showtimedmessage(Label label, String text, double millis) {
        label.setText(text);
        new Timeline(new KeyFrame(Duration.millis(millis), ae -> label.setText(""))).play();
    }
}
