package kujiin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kujiin.ui.MainController;
import kujiin.ui.dialogs.AudioChecksDialog;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.util.enums.ProgramState;
import kujiin.xml.*;

import static kujiin.xml.Preferences.PROGRAM_ICON;

// Maybe Add Goals In Bar Chart To Session Complete?
// Add Goals Completed Label Into Bottom Of Goals Tab On Player

// Rename "Available" Ambience?


// Bugs To Fix
    // TODO Customize Ambience Does Not Set Ambience Enabled
        // Customize Ambience > Populate Session Creator Table Deletes All Playback Items
    // TODO Fix Session Complete Dialog So It Can Exit Program Or Return To Program (Return To Player Works)

// Additional Features To Add
    // TODO Implement Start Time/Stop Time/Breaks Session Functionality Into Player

    // TODO Get Merge Logic On Add Items Functional
    // TODO Dialog At End To Set Entrainment/Ambience Volume As Default?
    // TODO Add Users Function, Tracking Everything To Each User
    // TODO Add Logging (And Write To Log File) For Troubleshooting
    // TODO Create Goal Progress Similar To Session Details And Add To Session Details Dialog
    // TODO Exporter
    // TODO Welcome Dialog

// Additional Features (Optional)
    // TODO Refactor Freq Files So There Can Be 2 or 3 Different Frequency Octaves For The Same Session Part (Use enum FreqType)

// Mind Workstation
    // TODO Add Low (And Possibly Medium) Variations Of All Session Parts
    // TODO Add Ramps To Connect Low (And Possibly Medium) Variations Of Session Parts With Each Other

// Startup Wizard (Welcome Dialog)
    // Welcome To The Program ......(Description Of Program). This A Short Tutorial To Teach You About Some Of The Features Of This Program
    // Session Creation
    // Ambience & Playback
    // Ambience

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/Main.fxml"));
        Scene Scene = new Scene(fxmlLoader.load());
        MainController root = fxmlLoader.getController();
        primaryStage.setTitle("Kuji-In");
        primaryStage.setScene(Scene);
        primaryStage.setResizable(false);
        root.setScene(Scene);
        root.setStage(primaryStage);
        if ( ! System.getProperty("os.name").equals(root.getPreferences().getAdvancedOptions().getOS())) {
            Preferences.AMBIENCEXMLFILE.delete();
            Preferences.ENTRAINMENTXMLFILE.delete();
            Preferences.RAMPFILESXMLFILE.delete();
        }
        primaryStage.getIcons().clear();
        primaryStage.getIcons().add(PROGRAM_ICON);
        root.getScene().getStylesheets().add(Preferences.DEFAULTSTYLESHEET.toURI().toString());
        root.setFavoriteSessions(new FavoriteSessions(root));
        root.setSessions(new Sessions(root));
        root.setAvailableAmbiences(new AvailableAmbiences(root));
        root.setAvailableEntrainments(new AvailableEntrainments(root));
        root.setRampFiles(new RampFiles(root));
        root.setAllGoals(new AllGoals(root));
        AudioChecksDialog audioChecksDialog = new AudioChecksDialog(root);
        audioChecksDialog.initModality(Modality.APPLICATION_MODAL);
        audioChecksDialog.initOwner(primaryStage);
        audioChecksDialog.showAndWait();
        primaryStage.setOnCloseRequest(event -> {
            if (root.getProgramState() == ProgramState.IDLE &&
                    new ConfirmationDialog(root.getPreferences(), "Exit Kuji-In Program", null, "Really Exit?", "Exit", "Cancel").getResult()) {
                root.close();
            } else {event.consume();}
        });
        primaryStage.show();
    }

    public void test() {

    }
}