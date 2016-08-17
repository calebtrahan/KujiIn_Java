package kujiin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kujiin.util.This_Session;
import kujiin.xml.Ambiences;
import kujiin.xml.Entrainments;


public class Main extends Application {
    private MainController Root;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/Main.fxml"));
        Scene Scene = new Scene(fxmlLoader.load());
        Root = fxmlLoader.getController();
        primaryStage.setTitle("Kuji-In");
        primaryStage.setScene(Scene);
        primaryStage.setResizable(false);
        primaryStage.setOnShowing(event -> {
            primaryStage.setIconified(false);
            Root.setScene(Scene);
            Root.setStage(primaryStage);
            Root.getOptions().setStyle(primaryStage);
            Root.setEntrainments(new Entrainments(Root));
            Root.setAmbiences(new Ambiences(Root));
            Root.setSession(new This_Session(Root));
            Root.preset_initialize();
            Root.creation_initialize();
            Root.exporter_initialize();
            Root.sessions_initialize();
            Root.goals_initialize();
        });
        primaryStage.setOnCloseRequest(event -> {
            if (Root.dialog_YesNoConfirmation("Confirmation", "Really Exit?", "")) {System.exit(0);}
            else {event.consume();}
        });
        primaryStage.show();
//        test();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        if (Root.cleanup()) {
            super.stop();
            System.exit(0);
        }
    }
    public void test() {
        for (int x = 1; x > 0; x--) {
            System.out.println("Looping " + x);
        }
    }

}
