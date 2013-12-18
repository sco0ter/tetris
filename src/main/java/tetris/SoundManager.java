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
