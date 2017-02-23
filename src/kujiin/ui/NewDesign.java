package kujiin.ui;

import javafx.scene.control.*;
import javafx.stage.Stage;

public class NewDesign extends Stage {
// High Level Controls
    public Accordion PlayExportAccordion;
    public TitledPane CreatePane;
    public TitledPane PlayPane;
    public TitledPane ExportPane;
// Play/Export Pane
    // Create/Load Session Tab
        // Top Toolbar
    public Button CreateNewSessionButton;
    public Button RecentPracticedButton;
    public Button FavoritesButton;
    public Button OpenFileButton;
        // Session Information Pane
    public TextField LoadedSessionName;
    public TextField LoadedSessionDuration;
    public CheckBox LoadedSessionHasAmbienceCheckbox;
    public Button LoadedSessionPlayButton;
    public Button LoadedSessionExportButton;
    public Button LoadedSessionSaveAsFileButton;
    public Button LoadedSessionDetailsButton;
    // Play Tab
    // Export Tab

// Progress Pane

// Goals Pane


    public NewDesign() {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/NewDesign.fxml"));
//            fxmlLoader.setController(this);
//            Scene defaultscene = new Scene(fxmlLoader.load());
//            setScene(defaultscene);
//            setResizable(false);
//            PlayExportAccordion.setExpandedPane(CreatePane);
//            PlayPane.setDisable(true);
//            ExportPane.setDisable(true);
//            PlayPane.setTooltip(new Tooltip("Load A Session And Push 'Play'  To Access The Player"));
//            ExportPane.setTooltip(new Tooltip("Load A Session And Push 'Export'  To Access The Exporter"));
//        } catch (IOException e) {e.printStackTrace();}
    }
}
