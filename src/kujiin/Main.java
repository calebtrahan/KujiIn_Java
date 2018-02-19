package kujiin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kujiin.ui.MainController;
import kujiin.ui.dialogs.AudioChecksDialog;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.util.enums.ProgramState;
import kujiin.xml.*;

import static kujiin.xml.Preferences.*;

// TODO Add Concurrent Program Check

// TODO Design Tutorial ( displaytutorial() )

// TODO Ambience Files Found In Directories... (PlaybackItem Folder)...Add?

// TODO Move Ambience/Reference To User's Home Directory
// TODO Fixing Timing Display On:
    // Playlist (Shows 1 second Before)

// TODO Reverse Order Practiced Sessions (Most Recent At Top)

// TODO After End Of Session -> Return To Player -> Clear And Unselect Reference

// TODO Enable/Disable Ambience On Playback Item Play
    // If Has Available Ambience.....Enable But Don't Play
    // If Not Has Preset Ambience....Set This Session Ambience Invisible
    // Turn Ambience Volume On/Off At Will

// Move Ambience Volume Controls Inside Ambience Tab
// Remove "Entrainment" And "Ambience" Labels From Volume Controls


// TODO Add Recent Activity Tab


// TODO Change Sessions To Store Online?
// TODO Redesign Goals Completed (Use Bar Chart To Show Completed And Next With Practiced Time)
// TODO On Session Complete Chart, If Less Than Expected Map Out Expected Duration With Different Color
// TODO Set Tooltips For Playlist Ambience Names & Progress Bar And Buttons
// TODO Set Up Goal Overview Mouse Pressed Listeners
    // Set New Goal On Current Goal Column
    // Goals Completed Detail On Goals Completed Column

// TODO Style/Color Completed/Not Completed Goals Text On Goal Details Pane
// TODO Instead Of 100% On Completed Goals, Display √
// TODO Set Tables On Practiced Sessions Pane To Constricted Resize

// Maybe Add Goals In Bar Chart To Session Complete?
// Add Goals Completed Label Into Bottom Of Goals Tab On Player

// Rename "Available" Ambience?

// Bugs To Fix
    // TODO Fix Session Complete Dialog So It Can Exit Program Or Return To Program (Return To Player Works)

// Additional Features To Add
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
        checkfiles();
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
                    new ConfirmationDialog(root.getPreferences(), "Exit Kuji-In Program", "Really Exit?", null, "Exit", "Cancel").getResult()) {
                root.close();
            } else {event.consume();}
        });
        primaryStage.show();
    }

    private void checkfiles() {
        if (! XMLDIRECTORY.exists()) {
            if (! XMLDIRECTORY.mkdirs()) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText("Cannot Access " + USERDIRECTORY + "! Cannot Save Needed Files And Cannot Continue");
                a.setContentText("Please Check Permissions And Run The Program Again");
                a.showAndWait();
                System.exit(0);
            }
        }
    }
    private void test() {

    }
}