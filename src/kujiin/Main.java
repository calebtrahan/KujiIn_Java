package kujiin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import kujiin.ui.MainController;
import kujiin.xml.AvailableAmbiences;
import kujiin.xml.FavoriteSessions;
import kujiin.xml.Preferences;
import kujiin.xml.Sessions;

import java.io.File;

import static kujiin.xml.Preferences.PROGRAM_ICON;


public class Main extends Application {
    private MainController Root;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/Main.fxml"));
        Scene Scene = new Scene(fxmlLoader.load());
        Root = fxmlLoader.getController();
        primaryStage.setTitle("Kuji-In");
        primaryStage.setScene(Scene);
        primaryStage.setResizable(false);
        Root.setScene(Scene);
        Root.setStage(primaryStage);
        primaryStage.getIcons().clear();
        primaryStage.getIcons().add(PROGRAM_ICON);
        String themefile = Root.getPreferences().getUserInterfaceOptions().getThemefile();
        if (themefile != null) {primaryStage.getScene().getStylesheets().add(themefile);}
        Root.setAvailableAmbiences(new AvailableAmbiences(Root));
        Root.setFavoriteSessions(new FavoriteSessions());
        Root.setSessions(new Sessions(Root));
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

    public void test() {
        File file = new File(Preferences.SOUNDDIRECTORY, "Test.mp3");
        Media media = new Media(file.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        mediaPlayer.setOnHalted(() -> System.out.println("Error"));
    }

}