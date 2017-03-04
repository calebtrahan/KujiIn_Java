package kujiin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kujiin.ui.MainController;
import kujiin.xml.AvailableAmbiences;
import kujiin.xml.FavoriteSessions;
import kujiin.xml.Sessions;

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
        root.setAvailableAmbiences(new AvailableAmbiences(root));
        root.setFavoriteSessions(new FavoriteSessions());
        root.setSessions(new Sessions(root));
        primaryStage.setOnCloseRequest(event -> {
//        if (Root.getProgramState() == ProgramState.IDLE &&
//                new ConfirmationDialog(Root.getPreferences(), "Confirmation", null, "Really Exit?", "Exit", "Cancel").getResult()) {
//            Root.close();
//        } else {event.consume();}
        });
        primaryStage.show();
    }
    @Override
    public void stop() throws Exception {
//        if (Root.cleanup()) {
//            super.stop();
//            System.exit(0);
//        }
    }

    public void test() {}

}