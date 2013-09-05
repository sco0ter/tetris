/*
 * Copyright (c) 2013. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package tetris.board;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * @author Christian Schudt
 */
public class PointOverlay extends StackPane {
    private IntegerProperty points = new SimpleIntegerProperty();

    public PointOverlay() {

    }

    public IntegerProperty pointsProperty() {
        return points;
    }

    public void addPoints(int points) {
        this.points.set(this.points.get() + points);
        showPoints(points);
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
}
