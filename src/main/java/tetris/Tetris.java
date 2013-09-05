package tetris;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HorizontalDirection;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class Tetris extends Application {

    /**
     * Stores if the arrow down key was pressed, to prevent repeated events.
     */
    private boolean movingDown = false;

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
                    gameController.getBoard().move(HorizontalDirection.LEFT);
                }

                if (keyEvent.getCode() == KeyCode.RIGHT && !gameController.pausedProperty().get()) {
                    gameController.getBoard().move(HorizontalDirection.RIGHT);
                }

                if (keyEvent.getCode() == KeyCode.UP && !gameController.pausedProperty().get()) {
                    gameController.getBoard().rotate(HorizontalDirection.LEFT);
                }

                if (keyEvent.getCode() == KeyCode.DOWN) {
                    if (!movingDown) {
                        if (!gameController.pausedProperty().get()) {
                            gameController.getBoard().moveDownFast();
                        }
                        movingDown = true;
                    }
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
                    movingDown = false;
                    gameController.getBoard().moveDown();
                }
            }
        });


        scene.getStylesheets().add("styles.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}