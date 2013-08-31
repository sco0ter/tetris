package tetris;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * @author Christian Schudt
 */
public class SoundManager {

    private final MediaPlayer mediaPlayer1;


    public SoundManager() {
        mediaPlayer1 = new MediaPlayer(new Media(getClass().getResource("/soundtrack.mp3").toExternalForm()));
        mediaPlayer1.setCycleCount(MediaPlayer.INDEFINITE);
    }

    public void pause() {
        mediaPlayer1.pause();
    }

    public void play() {
        mediaPlayer1.play();
    }

    public void playFromStart() {
        mediaPlayer1.stop();
        mediaPlayer1.play();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer1;
    }

}
