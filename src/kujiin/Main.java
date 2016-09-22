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
import kujiin.util.*;
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Called Start Method");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/Main.fxml"));
        Scene Scene = new Scene(fxmlLoader.load());
        Root = fxmlLoader.getController();
        primaryStage.setTitle("Kuji-In");
        primaryStage.setScene(Scene);
        primaryStage.setResizable(false);
        Root.setScene(Scene);
        Root.setStage(primaryStage);
        Root.getOptions().setStyle(primaryStage);
        Root.setEntrainments(new Entrainments(Root));
        Root.setAmbiences(new Ambiences(Root));
        Root.setSession(new This_Session(Root));
        primaryStage.setOnShowing(event -> {
            primaryStage.setIconified(false);
            Root.creation_initialize();
            Root.exporter_initialize();
            Root.sessions_initialize();
            Root.goals_initialize();
            Root.preset_initialize();
            StartupChecks startupChecks = new StartupChecks(Root.getSession().getAllSessionParts());
            startupChecks.run();
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
        // [entrainment part count, ambience checker part count, sessionpart count;]
        private final int[] startupcheck_count = {0, 0, 0};
        private MediaPlayer startupcheckplayer;
        private StartupChecksDialog startupChecksDialog;
        private ArrayList<File> ambiencechecker_soundfilestoadd;

        public StartupChecks(List<SessionPart> allsessionparts) {
            sessionPartList = allsessionparts;
        }

        @Override
        protected Object call() throws Exception {
            if (selectedsessionpart == null) {selectedsessionpart = getnextsessionpart();}
            File file = null;
            SoundFile soundFile = null;
            try {
                file = getnextentrainmentfile();
                System.out.println("Next Entrainment File For " + selectedsessionpart.name + " Is: " + file.getAbsolutePath());
                soundFile = getnextentraimentsoundfile();
                if (soundFile == null) {soundFile = new SoundFile(file);}
            } catch (IndexOutOfBoundsException ignored) {
                try {
                    soundFile = getnextambiencesoundfile();
                    System.out.println("Next Ambience File For " + selectedsessionpart.name + " Is: " + file.getAbsolutePath());
                    file = soundFile.getFile();
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
                    System.out.println("Checking " + soundFile.getName() + "'s Duration...");
                    startupcheckplayer = new MediaPlayer(new Media(file.toURI().toString()));
                    SoundFile finalSoundFile = soundFile;
                    startupcheckplayer.setOnReady(() -> {
                        System.out.println(finalSoundFile.getName() + "'s Duration Is: " + startupcheckplayer.getTotalDuration().toMillis());
                        finalSoundFile.setDuration(startupcheckplayer.getTotalDuration().toMillis());
                        startupcheckplayer.dispose();
                        if (startupcheck_count[1] == 0) {
                            if (startupcheck_count[0] == 0) {selectedsessionpart.getEntrainment().setFreq(finalSoundFile);}
                            else {selectedsessionpart.getEntrainment().ramp_add(finalSoundFile);}
                            startupcheck_count[0]++;
                        } else {
                            selectedsessionpart.getAmbience().add(finalSoundFile);
                            startupcheck_count[1]++;
                        }
                        try {call();} catch (Exception ignored) {}
                    });
                }
            } else {
                if (startupcheck_count[1] == 0) {startupcheck_count[0]++;}
                else {startupcheck_count[1]++;}
                try {call();} catch (Exception ignored) {}
            }
            return null;
        }

        protected void populateambience() {
            try {
                File ambiencedirectory = new File(Options.DIRECTORYAMBIENCE, selectedsessionpart.name);
                for (File i : ambiencedirectory.listFiles()) {
                    if (Util.audio_isValid(i)) {
                        if (! selectedsessionpart.getAmbience().getAmbienceFiles().contains(i)) {
                            ambiencechecker_soundfilestoadd.add(i);}
                        else {
                            try {
                                Double duration = selectedsessionpart.getAmbience().getAmbience().get(selectedsessionpart.getAmbience().getAmbienceFiles().indexOf(i)).getDuration();
                                if (duration == null || duration == 0.0) {
                                    ambiencechecker_soundfilestoadd.add(i);}
                            } catch (ArrayIndexOutOfBoundsException ignored) {ambiencechecker_soundfilestoadd.add(i);}
                        }
                    }
                }
                if (ambiencechecker_soundfilestoadd.isEmpty()) {}
            } catch (NullPointerException ignored) {
                // TODO Change This To Reflect No Ambience Files In Directory
            }
        }
        protected SoundFile getnextentraimentsoundfile() throws IndexOutOfBoundsException {
            if (selectedsessionpart instanceof Qi_Gong || selectedsessionpart instanceof Element) {
                if (startupcheck_count[0] == 0) {return selectedsessionpart.getEntrainment().getFreq();}
                else {return selectedsessionpart.getEntrainment().ramp_get(startupcheck_count[0]);}
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
                else {return new File(Options.DIRECTORYENTRAINMENT, "ramp/" + selectedsessionpart.getNameForFiles() + "to" + selectedsessionpart.entrainmentchecker_partcutnames.get(startupcheck_count[0] - 1) + ".mp3");}
            } else {
                switch (startupcheck_count[0]) {
                    case 0:
                       return new File(Options.DIRECTORYENTRAINMENT, selectedsessionpart.getNameForFiles().toUpperCase() + ".mp3");
                    case 1:
                        return new File(Options.DIRECTORYENTRAINMENT, "ramp/" + selectedsessionpart.getNameForFiles() + "to" +
                                selectedsessionpart.entrainmentchecker_partcutnames.get(selectedsessionpart.entrainmentchecker_partcutnames.
                                        indexOf(selectedsessionpart.getNameForFiles()) + 1) + ".mp3");
                    case 2:
                        return new File(Options.DIRECTORYENTRAINMENT, "ramp/" + selectedsessionpart.getNameForFiles() + "toqi.mp3");
                    default:
                        throw new IndexOutOfBoundsException();
                }
            }
        }
        protected SoundFile getnextambiencesoundfile() throws IndexOutOfBoundsException {
            if (startupcheck_count[1] == 0) {populateambience();}
            return selectedsessionpart.getAmbience().get(startupcheck_count[1]);
        }
        protected SessionPart getnextsessionpart() throws IndexOutOfBoundsException {
            if (selectedsessionpart == null) {return sessionPartList.get(0);}
            else {
                startupcheck_count[2] = sessionPartList.indexOf(selectedsessionpart) + 1;
                return sessionPartList.get(startupcheck_count[2]);
            }
        }

        @Override
        protected void succeeded() {
            super.succeeded();
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
                setTitle("Starting Up The Program");
                setResizable(false);
            } catch (IOException e) {}
        }
    }
}
