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
        primaryStage.getIcons().clear();
        primaryStage.getIcons().add(PROGRAM_ICON);
        String themefile = root.getPreferences().getUserInterfaceOptions().getThemefile();
        if (themefile != null) {primaryStage.getScene().getStylesheets().add(themefile);}
        root.setFavoriteSessions(new FavoriteSessions());
        root.setSessions(new Sessions(root));
        root.setAvailableAmbiences(new AvailableAmbiences(root));
        root.setAvailableEntrainments(new AvailableEntrainments(root));
        root.setRampFiles(new RampFiles(root));
        root.setGoals(new Goals(root));
        AudioChecksDialog audioChecksDialog = new AudioChecksDialog(root);
        audioChecksDialog.initModality(Modality.APPLICATION_MODAL);
        audioChecksDialog.initOwner(primaryStage);
        audioChecksDialog.showAndWait();
        primaryStage.setOnCloseRequest(event -> {
            if (root.getProgramState() == ProgramState.IDLE &&
                    new ConfirmationDialog(root.getPreferences(), "Confirmation", null, "Really Exit?", "Exit", "Cancel").getResult()) {
                root.close();
            } else {event.consume();}
        });
        primaryStage.show();
        test();
    }

    public void test() {

    }
}