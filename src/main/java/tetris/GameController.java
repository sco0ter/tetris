/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Christian Schudt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package tetris;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * @author Christian Schudt
 */
final class GameController {

    private final SoundManager soundManager;

    private final Board board;

    private final NotificationOverlay notificationOverlay;

    private final ScoreManager scoreManager;

    private final BooleanProperty paused = new SimpleBooleanProperty();

    public GameController() {
        this.board = new Board();
        this.soundManager = new SoundManager(this);
        this.scoreManager = new ScoreManager(this);

        notificationOverlay = new NotificationOverlay(this);
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
        board.start();
        scoreManager.scoreProperty().set(0);
        paused.set(false);
    }

    private void pause() {
        board.pause();
    }

    public void stop() {
        board.clear();
        scoreManager.scoreProperty().set(0);
        paused.set(false);
    }

    public Board getBoard() {
        return board;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public void play() {
        paused.set(false);
        board.play();
    }

    public NotificationOverlay getNotificationOverlay() {
        return notificationOverlay;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }
}
