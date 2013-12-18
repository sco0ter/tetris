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

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HorizontalDirection;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * @author Christian Schudt
 */
final class NotificationOverlay extends StackPane implements Board.BoardListener {

    public NotificationOverlay(GameController gameController) {
        gameController.getBoard().addBoardListener(this);
        gameController.getScoreManager().scoreProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                if (number2.doubleValue() != 0) {
                    showPoints(number2.intValue() - number.intValue());
                }
            }
        });
    }

    private void showPoints(int points) {
        final Label label = new Label("+" + String.valueOf(points));
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-weight:bold;-fx-font-size:2em");
        label.setOpacity(0);
        getChildren().add(label);

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(3), label);
        translateTransition.setByY(-100);
        translateTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                getChildren().remove(label);
            }
        });
        translateTransition.setInterpolator(Interpolator.EASE_OUT);
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(label);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setAutoReverse(true);
        fadeTransition.setCycleCount(2);
        fadeTransition.setDuration(translateTransition.getDuration().divide(fadeTransition.getCycleCount()));

        translateTransition.playFromStart();
        fadeTransition.playFromStart();
    }

    private void showGameOver() {
        final Label label = new Label("Game Over");
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-weight:bold;-fx-font-size:4em");
        label.setOpacity(0);
        getChildren().add(label);

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(6), label);
        translateTransition.setByY(-50);
        translateTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                getChildren().remove(label);
            }
        });
        translateTransition.setInterpolator(Interpolator.EASE_OUT);
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(label);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setAutoReverse(true);
        fadeTransition.setCycleCount(2);
        fadeTransition.setDuration(translateTransition.getDuration().divide(fadeTransition.getCycleCount()));

        translateTransition.playFromStart();
        fadeTransition.playFromStart();
    }

    @Override
    public void onDropped() {
    }

    @Override
    public void onRowsEliminated(int rows) {

    }

    @Override
    public void onGameOver() {
        showGameOver();
        //setStyle("-fx-background-color: rgba(51, 51, 51, 0.6)");
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
