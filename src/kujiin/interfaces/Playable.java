package kujiin.interfaces;

import javafx.scene.media.MediaPlayer;

public interface Playable {
    MediaPlayer getCurrentEntrainmentPlayer();
    MediaPlayer getCurrentAmbiencePlayer();
    void start();
    void resume();
    void pause();
    void stop();
    void tick();
    void playnextentrainment();
    void playnextambience();
    void startfadeout();
    void entrainmenterror();
    void ambienceerror();
    void cleanup();
}
