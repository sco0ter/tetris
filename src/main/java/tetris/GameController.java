package tetris;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import tetris.board.Board;
import tetris.board.PointOverlay;

/**
 * @author Christian Schudt
 */
public class GameController {

    private final SoundManager soundManager;

    private final Board board;

    private BooleanProperty paused = new SimpleBooleanProperty();

    private PointOverlay pointOverlay;

    public GameController() {
        this.soundManager = new SoundManager();
        this.board = new Board(this);
        pointOverlay = new PointOverlay();
        paused.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                if (aBoolean2) {
                    pause();
                } else {
                    play();
                }
            }
        });
    }

    public BooleanProperty pausedProperty() {
        return paused;
    }

    public void start() {
        soundManager.playFromStart();
        board.start();
        paused.set(false);
    }

    private void pause() {
        soundManager.pause();
        board.pause();
    }

    public Board getBoard() {
        return board;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public void play() {
        paused.set(false);
        soundManager.play();
        board.play();
    }

    public PointOverlay getPointOverlay() {
        return pointOverlay;
    }
}
