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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

/**
 * @author Christian Schudt
 */
public final class Tetris extends HBox {

    /**
     * Stores if the arrow down key was pressed, to prevent repeated events.
     */
    private boolean movingDown = false;


    public Tetris() {

        setId("tetris");

        sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observableValue, Scene scene, Scene scene2) {
                if (scene2 != null) {
                    scene2.getStylesheets().add("tetris/styles.css");
                }
            }
        });
        final GameController gameController = new GameController();

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(gameController.getBoard());

        stackPane.getChildren().add(gameController.getNotificationOverlay());
        stackPane.setAlignment(Pos.TOP_CENTER);

        getChildren().add(stackPane);

        InfoBox infoBox = new InfoBox(gameController);
        infoBox.setMaxHeight(Double.MAX_VALUE);
        //infoBox.maxHeightProperty().bind(gameController.getBoard().heightProperty());

        HBox.setHgrow(infoBox, Priority.ALWAYS);
        getChildren().add(infoBox);

        setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.P) {
                    gameController.pausedProperty().set(!gameController.pausedProperty().get());
                    keyEvent.consume();
                }
            }
        });
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent mouseEvent) {
                gameController.getBoard().requestFocus();

            }
        });

        setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent keyEvent) {

                if (keyEvent.getCode() == KeyCode.LEFT && !gameController.pausedProperty().get()) {
                    gameController.getBoard().move(HorizontalDirection.LEFT);
                    keyEvent.consume();
                }

                if (keyEvent.getCode() == KeyCode.RIGHT && !gameController.pausedProperty().get()) {
                    gameController.getBoard().move(HorizontalDirection.RIGHT);
                    keyEvent.consume();
                }

                if (keyEvent.getCode() == KeyCode.UP && !gameController.pausedProperty().get()) {
                    gameController.getBoard().rotate(HorizontalDirection.LEFT);
                    keyEvent.consume();
                }

                if (keyEvent.getCode() == KeyCode.DOWN) {
                    if (!movingDown) {
                        if (!gameController.pausedProperty().get()) {
                            gameController.getBoard().moveDownFast();
                        }
                        movingDown = true;
                        keyEvent.consume();
                    }
                }
                if (keyEvent.getCode() == KeyCode.SPACE && !gameController.pausedProperty().get()) {
                    gameController.getBoard().dropDown();
                    keyEvent.consume();
                }
            }
        });

        setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.DOWN) {
                    movingDown = false;
                    gameController.getBoard().moveDown();
                }
            }
        });

    }
}
