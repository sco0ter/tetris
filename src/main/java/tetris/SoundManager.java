package tetris;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.HorizontalDirection;
import javafx.scene.media.AudioClip;

/**
 * @author Christian Schudt
 */
final class SoundManager implements Board.BoardListener {

    private DoubleProperty volume = new SimpleDoubleProperty();

    private DoubleProperty soundVolume = new SimpleDoubleProperty();

    private BooleanProperty mute = new SimpleBooleanProperty();

    public SoundManager(GameController gameController) {
        gameController.getBoard().addBoardListener(this);

        for (Sound sound : Sound.values()) {
            sound.getAudioClip().volumeProperty().bind(SoundManager.this.soundVolumeProperty());
        }
    }

    public DoubleProperty volumeProperty() {
        return volume;
    }

    public DoubleProperty soundVolumeProperty() {
        return soundVolume;
    }

    public BooleanProperty muteProperty() {
        return mute;
    }

    public void onDropped() {
        if (!mute.get()) {
            Sound.DROPPED.getAudioClip().play();
        }
    }

    @Override
    public void onRowsEliminated(int rows) {
        if (!mute.get()) {
            if (rows < 4) {
                Sound.VANISH.getAudioClip().play();
            } else {
                Sound.TETRIS.getAudioClip().play();
            }
        }
    }

    @Override
    public void onGameOver() {
        if (!mute.get()) {
            Sound.GAME_OVER.getAudioClip().play();
        }
    }

    @Override
    public void onInvalidMove() {
        if (!mute.get()) {
            Sound.INVALID_MOVE.getAudioClip().play();
        }
    }

    @Override
    public void onMove(HorizontalDirection horizontalDirection) {
        if (!mute.get()) {
            Sound.MOVE.getAudioClip().play();
        }
    }

    @Override
    public void onRotate(HorizontalDirection horizontalDirection) {
        if (!mute.get()) {
            Sound.ROTATE.getAudioClip().play();
        }
    }

    private enum Sound {

        ROTATE("tetris/cartoon130.mp3"),
        TETRIS("tetris/cartoon034.mp3"),
        DROPPED("tetris/cartoon035.mp3"),
        INVALID_MOVE("tetris/cartoon155.mp3"),
        MOVE("tetris/cartoon136.mp3"),
        VANISH("tetris/cartoon017.mp3"),
        GAME_OVER("tetris/cartoon014.mp3");

        private AudioClip audioClip;

        private Sound(String name) {
            audioClip = new AudioClip(getClass().getResource("/" + name).toExternalForm());
        }

        public AudioClip getAudioClip() {
            return audioClip;
        }

    }
}
