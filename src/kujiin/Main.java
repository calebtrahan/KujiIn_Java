package kujiin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


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
            Root.setScene(Scene);
            Root.setStage(primaryStage);
            Root.getOptions().setStyle(primaryStage);
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

    }

}
