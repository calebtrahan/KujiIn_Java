package kujiin.ui.progress;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.xml.Session;

import java.io.IOException;

public class EditSessionNotes extends StyledStage {
    public TextArea NotesTextArea;
    public Button SaveNotesButton;
    private Session EditedSession;
    private boolean accepted = false;

    public EditSessionNotes(Session session, String toptext, boolean readonly) {
        try {
            EditedSession = session;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/progress/EditSessionNotes.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            if (readonly) {SaveNotesButton.setVisible(false);}
            NotesTextArea.setEditable(! readonly);
            setTitle(toptext);
            if (session.getNotes() != null) {NotesTextArea.setText(session.getNotes());}
        } catch (IOException e) {e.printStackTrace();}
    }

// Getters And Setters
    public Session getEditedSession() {
        return EditedSession;
    }
    public boolean isAccepted() {
        return accepted;
    }

// Button Actions
    public void savenotesandclose() {
        EditedSession.setNotes(NotesTextArea.getText());
        accepted = true;
        close();
    }

}