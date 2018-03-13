package kujiin.ui.progress;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.xml.Preferences;
import kujiin.xml.Sessions;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;

public class ImportSessionFile extends StyledStage {
    public Label TopLabel;
    public TextField SelectedFileTextField;
    public Button OpenFileButton;
    public Button AddSessionsButton;
    public Button CancelButton;
    private Sessions sessionstoadd;
    private Sessions sessions;
    private Preferences preferences;

    public ImportSessionFile(Sessions sessions, Preferences preferences) {
        try {
            this.preferences = preferences;
            this.sessions = sessions;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/progress/ImportSessionsFile.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setTitle("Import Sesssions");
            OpenFileButton.setOnAction(event -> openfile());
            AddSessionsButton.setOnAction(event -> addsessions());
            CancelButton.setOnAction(event -> close());
        } catch (IOException e) { e.printStackTrace(); }
    }

// Getters And Setters
    public Sessions getSessions() {
        return sessions;
    }

// Button Actions
    private void openfile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Sessions File To Import");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("XML Files", ".xml"));
        File tempfile = fileChooser.showOpenDialog(this);
        if (tempfile == null) {return;}
        if (tempfile.getAbsolutePath().endsWith(".xml")) {
            try {
                JAXBContext context = JAXBContext.newInstance(Sessions.class);
                Unmarshaller unmarshaller =  context.createUnmarshaller();
                Sessions tempsessions = (Sessions) unmarshaller.unmarshal(tempfile);
                if (tempsessions.hasSessions()) {
                    sessionstoadd = tempsessions;
                    SelectedFileTextField.setText(tempfile.getName());
                    SelectedFileTextField.setDisable(false);
                    AddSessionsButton.setDisable(false);
                }
            } catch (JAXBException e) {
                new InformationDialog(preferences, "Cannot Add File", tempfile.getName() + " Is Not A Valid Sessions File", null);
                SelectedFileTextField.setText("Select File...");
                SelectedFileTextField.setDisable(false);
                AddSessionsButton.setDisable(true);
            }
        }
    }
    private void addsessions() {
        if (sessionstoadd != null && sessionstoadd.hasSessions()) {
            if (new ConfirmationDialog(preferences, "Confirmation", "This Will Add " + sessionstoadd.getSession().size() + " Sessions To Your Total Progress", "This Cannot Be Undone").getResult()) {
                sessions.add(sessionstoadd);
                close();
            }
        }
    }

}