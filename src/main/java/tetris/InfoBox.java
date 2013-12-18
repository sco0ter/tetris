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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.concurrent.Callable;


/**
 * @author Christian Schudt
 */
final class InfoBox extends VBox {
    public InfoBox(final GameController gameController) {
        setPadding(new Insets(20, 20, 20, 20));
        setSpacing(10);

        setId("infoBox");

        CheckBox checkBox = new CheckBox();
        checkBox.getStyleClass().add("mute");
        checkBox.setMinSize(64, 64);
        checkBox.setMaxSize(64, 64);
        checkBox.selectedProperty().set(true);
        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                gameController.getBoard().requestFocus();
            }
        });
        gameController.getSoundManager().muteProperty().bind(checkBox.selectedProperty());

        Slider sliderMusicVolume = new Slider();
        sliderMusicVolume.setMin(0);
        sliderMusicVolume.setMax(1);
        sliderMusicVolume.setValue(0.5);
        sliderMusicVolume.setFocusTraversable(false);
        sliderMusicVolume.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                if (!aBoolean2) {
                    gameController.getBoard().requestFocus();
                }
            }
        });
        gameController.getSoundManager().volumeProperty().bind(sliderMusicVolume.valueProperty());

        Slider sliderSoundVolume = new Slider();
        sliderSoundVolume.setMin(0);
        sliderSoundVolume.setMax(1);
        sliderSoundVolume.setValue(0.5);
        sliderSoundVolume.setFocusTraversable(false);
        gameController.getSoundManager().soundVolumeProperty().bind(sliderSoundVolume.valueProperty());
        sliderSoundVolume.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                if (!aBoolean2) {
                    gameController.getBoard().requestFocus();
                }
            }
        });

        final ImageView playImageView = new ImageView(new Image(getClass().getResourceAsStream("/tetris/play.png")));
        final ImageView pauseImageView = new ImageView(new Image(getClass().getResourceAsStream("/tetris/pause.png")));

        Button btnStart = new Button("New Game");
        btnStart.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/tetris/rotate-left.png"))));
        btnStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                gameController.start();
            }
        });

        Button btnStop = new Button("Stop");
        btnStop.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/tetris/stop.png"))));
        btnStop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                gameController.stop();
            }
        });
        btnStop.setMaxWidth(Double.MAX_VALUE);
        btnStop.setAlignment(Pos.CENTER_LEFT);

        btnStart.setMaxWidth(Double.MAX_VALUE);
        btnStart.setAlignment(Pos.CENTER_LEFT);
        Button btnPause = new Button("Pause");
        btnPause.graphicProperty().bind(new ObjectBinding<Node>() {
            {
                super.bind(gameController.pausedProperty());
            }

            @Override
            protected Node computeValue() {
                if (gameController.pausedProperty().get()) {
                    return playImageView;
                } else {
                    return pauseImageView;
                }
            }
        });

        btnPause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (gameController.pausedProperty().get()) {
                    gameController.pausedProperty().set(false);
                } else {
                    gameController.pausedProperty().set(true);

                }
            }
        });
        btnPause.setMaxWidth(Double.MAX_VALUE);
        btnPause.setAlignment(Pos.CENTER_LEFT);
        Preview preview = new Preview(gameController);


        getChildren().add(checkBox);
        getChildren().add(sliderMusicVolume);
        getChildren().add(sliderSoundVolume);

        Label lblPoints = new Label();
        lblPoints.getStyleClass().add("score");
        lblPoints.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return String.valueOf(gameController.getScoreManager().scoreProperty().get());
            }
        }, gameController.getScoreManager().scoreProperty()));
        lblPoints.setAlignment(Pos.CENTER_RIGHT);
        lblPoints.setMaxWidth(Double.MAX_VALUE);
        lblPoints.setEffect(new Reflection());

        getChildren().add(preview);
        getChildren().add(btnStart);
        getChildren().add(btnPause);
        getChildren().add(btnStop);

        Label lblInfo = new Label("Use arrow keys for movement\nand rotating and space for\ndropping the piece.");

        getChildren().add(lblInfo);

        getChildren().addAll(lblPoints);


    }
}
