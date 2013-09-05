package tetris;

import javafx.geometry.HorizontalDirection;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * @author Christian Schudt
 */
final class SoundManager implements Board.BoardListener {

    private final MediaPlayer mediaPlayer1;

    public SoundManager(GameController gameController) {
        mediaPlayer1 = new MediaPlayer(new Media(getClass().getResource("/soundtrack.mp3").toExternalForm()));
        mediaPlayer1.setCycleCount(MediaPlayer.INDEFINITE);
        gameController.getBoard().addBoardListener(this);
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

    public void onDropped() {
        Sound.DROPPED.getAudioClip().play();
    }

    @Override
    public void onRowsEliminated(int rows) {
        if (rows < 4) {
            Sound.VANISH.getAudioClip().play();
        } else {
            Sound.TETRIS.getAudioClip().play();
        }
    }

    @Override
    public void onGameOver() {

    }

    @Override
    public void onInvalidMove() {
        Sound.INVALID_MOVE.getAudioClip().play();
    }

    @Override
    public void onMove(HorizontalDirection horizontalDirection) {
        Sound.MOVE.getAudioClip().play();
    }

    @Override
    public void onRotate(HorizontalDirection horizontalDirection) {
        Sound.ROTATE.getAudioClip().play();
    }

    private enum Sound {

        ROTATE("cartoon130.mp3"),
        TETRIS("cartoon040.mp3"),
        DROPPED("cartoon035.mp3"),
        INVALID_MOVE("cartoon155.mp3"),
        MOVE("cartoon136.mp3"),
        VANISH("cartoon017.mp3");

        private AudioClip audioClip;

        private Sound(String name) {
            audioClip = new AudioClip(getClass().getResource("/" + name).toExternalForm());

        }

        public AudioClip getAudioClip() {
            return audioClip;
        }

    }
}
