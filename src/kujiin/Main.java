package kujiin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// TODO Add A Feature To Add Ambience To The This_Session (When They Check 'add ambience to session')
    // Use addCustomMusic (CustomMusicWidget) From Sleep Machine As A Template
    // Select Ambience File(s) To Add Into A List
    // Detect Ambience File(s) In Each Cut List (And Inform The User)
    // User Choice -> Just For This This_Session (Just Append Filename to Ambience List) || Always (Copy Files To The Program's Ambience Directory)

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("assets/fxml/Main.fxml"));
        primaryStage.setTitle("Kuji-In");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> System.exit(0));
    }


    public static void main(String[] args) {
        launch(args);
    }

}
