package kujiin;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import kujiin.util.Element;
import kujiin.util.Qi_Gong;
import kujiin.util.SessionPart;
import kujiin.util.This_Session;
import kujiin.xml.Ambiences;
import kujiin.xml.Entrainments;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main extends Application {
    private MainController Root;
    private StartupChecksDialog startupChecksDialog;

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
        Root.getOptions().setStyle(primaryStage);
        primaryStage.setOnShowing(event -> {
            Root.setEntrainments(new Entrainments(Root));
            Root.setAmbiences(new Ambiences(Root));
            Root.setSession(new This_Session(Root));
            primaryStage.setIconified(false);
            Root.creation_initialize();
            Root.exporter_initialize();
            Root.sessions_initialize();
            Root.goals_initialize();
            Root.preset_initialize();
            StartupChecks startupChecks = new StartupChecks(startupChecksDialog, Root.getSession().getAllSessionParts());
            startupChecksDialog = new StartupChecksDialog();
            startupChecksDialog.setOnShowing(event1 -> startupChecks.run());
            startupChecks.setOnSucceeded(event12 -> System.out.println("Succeeded!"));
            startupChecksDialog.show();
        });
        primaryStage.setOnCloseRequest(event -> {
            if (Root.dialog_getConfirmation("Confirmation", null, "Really Exit?", "Exit", "Cancel")) {Root.close(null);}
            else {event.consume();}
        });
        primaryStage.show();
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

    class StartupChecks extends Task {
        private SessionPart selectedsessionpart;
        private List<SessionPart> sessionPartList;
        private final int[] startupcheck_count = {0, 0, 0};
        private MediaPlayer startupcheckplayer;
        private StartupChecksDialog startupChecksDialog;
        private ArrayList<SessionPart> partswithnoambience = new ArrayList<>();
        private ArrayList<SessionPart> partswithmissingentrainment = new ArrayList<>();

        public StartupChecks(StartupChecksDialog startupChecksDialog, List<SessionPart> allsessionparts) {
            this.startupChecksDialog = startupChecksDialog;
            sessionPartList = allsessionparts;
        }

        @Override
        protected Object call() throws Exception {
            if (selectedsessionpart == null) {selectedsessionpart = getnextsessionpart();}
            startupChecksDialog.LargeProgressLabel.setText("Checking " + selectedsessionpart.name + " (" + sessionPartList.indexOf(selectedsessionpart) + "/" + (sessionPartList.size() - 1) + ")");
            startupChecksDialog.LargeProgress.setProgress(sessionPartList.indexOf(selectedsessionpart) / sessionPartList.size());
            File file = null;
            SoundFile soundFile = null;
            try {
                file = getnextentrainmentfile();
                soundFile = getnextentraimentsoundfile();
                if (soundFile == null) {soundFile = new SoundFile(file);}
                startupChecksDialog.SmallProgressLabel.setText("Checking Entrainment For " + selectedsessionpart.name + " (" + (startupcheck_count[0] + 1) + "/" + selectedsessionpart.partchecker_maxcount());
                startupChecksDialog.SmallProgress.setProgress(startupcheck_count[0] / (selectedsessionpart.partchecker_maxcount() - 1));
            } catch (IndexOutOfBoundsException ignored) {
                try {

                    if (selectedsessionpart.getAmbience().hasAnyAmbience()) {
                        if (startupcheck_count[1] == 0) {
                            selectedsessionpart.getAmbience().startup_addambiencefromdirectory(selectedsessionpart);
                            selectedsessionpart.getAmbience().startup_checkfordeletedfiles();
                        }
                        startupChecksDialog.SmallProgressLabel.setText("Checking Ambience For " + selectedsessionpart.name + " (" + (startupcheck_count[1] + 1) + "/" + selectedsessionpart.getAmbience().getAmbience().size());
                        startupChecksDialog.SmallProgress.setProgress(startupcheck_count[1] / (selectedsessionpart.getAmbience().getAmbience().size() - 1));
                        soundFile = getnextambiencesoundfile();
                        file = soundFile.getFile();
                    } else {
                        partswithnoambience.add(selectedsessionpart);
                        throw new IndexOutOfBoundsException();}
                } catch (IndexOutOfBoundsException ignore) {
                    try {
                        selectedsessionpart.getThisession().Root.getEntrainments().setsessionpartEntrainment(selectedsessionpart.number, selectedsessionpart.getEntrainment());
                        selectedsessionpart.getThisession().Root.getAmbiences().setsessionpartAmbience(selectedsessionpart.number, selectedsessionpart.getAmbience());
                        selectedsessionpart = getnextsessionpart();
                        startupcheck_count[0] = 0;
                        startupcheck_count[1] = 0;
                        call();
                    } catch (IndexOutOfBoundsException e) {
                        // End Of Startup Checks

                        return null;
                    }
                }
            }
            if (file != null && file.exists()) {
                if (soundFile == null) {soundFile = new SoundFile(file);}
                if (soundFile.getDuration() == null || soundFile.getDuration() == 0.0) {
                    System.out.print(selectedsessionpart.name + ": Checking " + soundFile.getName() + "'s Duration...");
                    startupcheckplayer = new MediaPlayer(new Media(file.toURI().toString()));
                    SoundFile finalSoundFile = soundFile;
                    startupcheckplayer.setOnReady(() -> {
                        System.out.print("Done! Duration Is: " + startupcheckplayer.getTotalDuration().toMillis() + "\n");
                        finalSoundFile.setDuration(startupcheckplayer.getTotalDuration().toMillis());
                        startupcheckplayer.dispose();
                        if (startupcheck_count[1] == 0) {
                            if (startupcheck_count[0] == 0) {selectedsessionpart.getEntrainment().setFreq(finalSoundFile);}
                            else {selectedsessionpart.getEntrainment().ramp_add(finalSoundFile);}
                            startupcheck_count[0]++;
                        } else {
                            selectedsessionpart.getAmbience().set(finalSoundFile);
                            startupcheck_count[1]++;
                        }
                        try {call();} catch (Exception ignored) {ignored.printStackTrace();}
                    });
                } else {
                    System.out.println(selectedsessionpart.name + ": " + soundFile.getName() + "'s Duration Is: " + soundFile.getDuration());
                    if (startupcheck_count[0] < selectedsessionpart.partchecker_maxcount()) {
                        if (startupcheck_count[0] == 0) {selectedsessionpart.getEntrainment().setFreq(soundFile);}
                        else {selectedsessionpart.getEntrainment().ramp_add(soundFile);}
                        startupcheck_count[0]++;
                    } else {startupcheck_count[1]++;}
                    try {call();} catch (Exception ignored) {ignored.printStackTrace();}
                }
            } else {
                if (startupcheck_count[0] < selectedsessionpart.partchecker_maxcount()) {
                    if (! partswithmissingentrainment.contains(selectedsessionpart)) {partswithmissingentrainment.add(selectedsessionpart);}
                    startupcheck_count[0]++;
                }
                else {startupcheck_count[1]++;}
                try {call();} catch (Exception ignored) {}
            }
            return null;
        }

        protected SoundFile getnextentraimentsoundfile() throws IndexOutOfBoundsException {
            if (selectedsessionpart instanceof Qi_Gong || selectedsessionpart instanceof Element) {
                try {
                    if (startupcheck_count[0] == 0) {
                        return selectedsessionpart.getEntrainment().getFreq();
                    } else {
                        return selectedsessionpart.getEntrainment().ramp_get(startupcheck_count[0]);
                    }
                } catch (Exception i) {i.printStackTrace(); return null;}
            } else {
                switch (startupcheck_count[0]) {
                    case 0:
                        return selectedsessionpart.getEntrainment().getFreq();
                    case 1:
                        return selectedsessionpart.getEntrainment().ramp_get(0);
                    case 2:
                        return selectedsessionpart.getEntrainment().ramp_get(1);
                    default:
                        throw new IndexOutOfBoundsException();
                }
            }
        }
        protected File getnextentrainmentfile() throws IndexOutOfBoundsException {
            if (selectedsessionpart instanceof Qi_Gong || selectedsessionpart instanceof Element) {
                if (startupcheck_count[0] == 0) {return new File(Options.DIRECTORYENTRAINMENT, selectedsessionpart.getNameForFiles().toUpperCase() + ".mp3");}
                else {return new File(Options.DIRECTORYENTRAINMENT, "ramp/" + selectedsessionpart.getNameForFiles() + "to" + selectedsessionpart.getThisession().getallCutNames().get(startupcheck_count[0] - 1).toLowerCase() + ".mp3");}
            } else {
                switch (startupcheck_count[0]) {
                    case 0:
                       return new File(Options.DIRECTORYENTRAINMENT, selectedsessionpart.getNameForFiles().toUpperCase() + ".mp3");
                    case 1:
                        return new File(Options.DIRECTORYENTRAINMENT, "ramp/" + selectedsessionpart.getNameForFiles() + "to" +
                                selectedsessionpart.getThisession().getallCutNames().get(selectedsessionpart.getThisession().getallCutNames().
                                        indexOf(selectedsessionpart.name) + 1) + ".mp3");
                    case 2:
                        return new File(Options.DIRECTORYENTRAINMENT, "ramp/" + selectedsessionpart.getNameForFiles() + "toqi.mp3");
                    default:
                        throw new IndexOutOfBoundsException();
                }
            }
        }
        protected SoundFile getnextambiencesoundfile() throws IndexOutOfBoundsException {
            return selectedsessionpart.getAmbience().get(startupcheck_count[1]);
        }
        protected SessionPart getnextsessionpart() throws IndexOutOfBoundsException {
            if (selectedsessionpart == null) {return sessionPartList.get(0);}
            else {
                startupcheck_count[2] = sessionPartList.indexOf(selectedsessionpart) + 1;
                return sessionPartList.get(startupcheck_count[2]);
            }
        }

    }
    class StartupChecksDialog extends Stage {
        public Label TopLabel;
        public Label LargeProgressLabel;
        public ProgressBar LargeProgress;
        public Label SmallProgressLabel;
        public ProgressBar SmallProgress;
        public Button CancelButton;

        public StartupChecksDialog() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/StartupChecksDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                setTitle("Performing Startup Checks");
                setResizable(false);
            } catch (IOException e) {}
        }
    }

}
