package kujiin.dialogs;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import kujiin.Cut;
import kujiin.This_Session;

import java.io.IOException;
import java.util.ArrayList;

public class CreatingSessionDialog extends Stage {
    public ProgressBar creatingsessionProgressBar;
    public Label creatingsessionTextStatusBar;
    public Button CancelButton;
    private int sessionparts;
    private int currentpartcount;
    This_Session thisSession;
    ArrayList<Cut> cutsinsesession;

    public CreatingSessionDialog(Parent parent, This_Session thisSession) {
//        this.cutsinsesession = cutsinsesession;
//        percent = sessionpartialpercent / 100;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/CreatingSessionDialog.fxml"));
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
//                // TODO Dialog -> "This_Session Successfully Created!"
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
