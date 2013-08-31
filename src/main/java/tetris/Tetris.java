package tetris;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HorizontalDirection;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import tetris.board.MainBox;

public class Tetris extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        VBox group = new VBox();

        final GameController gameController = new GameController();

        final MainBox mainBox = new MainBox(gameController);

        MenuBar bar = new MenuBar();

        Menu menu = new Menu("Tetris");

        MenuItem startNewGameMenuItem = new MenuItem("Start new Game");
        startNewGameMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                gameController.start();
            }
        });

        CheckMenuItem pauseMenuItem = new CheckMenuItem("Pause");

        pauseMenuItem.selectedProperty().bindBidirectional(gameController.pausedProperty());

        bar.getMenus().add(menu);
        menu.getItems().addAll(startNewGameMenuItem);
        menu.getItems().add(pauseMenuItem);
        bar.setUseSystemMenuBar(true);
        group.getChildren().add(bar);

        group.getChildren().add(mainBox);
        primaryStage.setTitle("Tetris");
        Scene scene = new Scene(group);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.P) {
                    gameController.pausedProperty().set(!gameController.pausedProperty().get());
                }
            }
        });


        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent keyEvent) {

                if (keyEvent.getCode() == KeyCode.LEFT && !gameController.pausedProperty().get()) {
                    if (gameController.getBoard().move(HorizontalDirection.LEFT)) {
                        Sound.MOVE_LEFT.getAudioClip().play();
                    } else {
                        Sound.INVALID_MOVE.getAudioClip().play();
                    }
                }

                if (keyEvent.getCode() == KeyCode.RIGHT && !gameController.pausedProperty().get()) {
                    if (gameController.getBoard().move(HorizontalDirection.RIGHT)) {
                        Sound.MOVE_RIGHT.getAudioClip().play();
                    } else {
                        Sound.INVALID_MOVE.getAudioClip().play();
                    }

                }
                if (keyEvent.getCode() == KeyCode.UP && !gameController.pausedProperty().get()) {
                    if (gameController.getBoard().rotate(HorizontalDirection.LEFT)) {
                        Sound.ROTATE_RIGHT.getAudioClip().play();
                    } else {
                        Sound.INVALID_MOVE.getAudioClip().play();
                    }
                }

                if (keyEvent.getCode() == KeyCode.DOWN) {
                    //if (!movingDown) {
                        if (!gameController.pausedProperty().get()) {
                            gameController.getBoard().moveDownFast();
                        }
                        //movingDown = true;
                    //}
                    /*if (getTetromino().rotate(HorizontalDirection.RIGHT)) {
                        Sound.ROTATE_RIGHT.getAudioClip().play();
                    } else {
                        Sound.INVALID_MOVE.getAudioClip().play();
                    } */
                }
                if (keyEvent.getCode() == KeyCode.SPACE && !gameController.pausedProperty().get()) {
                    gameController.getBoard().dropDown();
                }
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.DOWN) {
                    //movingDown = false;
                    gameController.getBoard().moveDown();
                }
            }
        });


        scene.getStylesheets().add("styles.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}