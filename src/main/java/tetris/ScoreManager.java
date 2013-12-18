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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.HorizontalDirection;

/**
 * Manages the score.
 *
 * @author Christian Schudt
 */
final class ScoreManager implements Board.BoardListener {

    private final IntegerProperty score = new SimpleIntegerProperty();

    private final GameController gameController;

    public ScoreManager(GameController gameController) {
        this.gameController = gameController;
        gameController.getBoard().addBoardListener(this);
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    private void addScore(int score) {
        this.score.set(this.score.get() + score);
    }

    @Override
    public void onDropped() {
    }

    @Override
    public void onRowsEliminated(int rows) {
        switch (rows) {
            case 1:
                addScore(40);
                break;
            case 2:
                addScore(100);
                break;
            case 3:
                addScore(300);
                break;
            case 4:
                addScore(1200);
                break;
        }
    }

    @Override
    public void onGameOver() {
    }

    @Override
    public void onInvalidMove() {
    }

    @Override
    public void onMove(HorizontalDirection horizontalDirection) {
    }

    @Override
    public void onRotate(HorizontalDirection horizontalDirection) {
    }
}
