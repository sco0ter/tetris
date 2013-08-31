package tetris.board;

import javafx.animation.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import tetris.Sound;
import tetris.tetromino.Tetromino;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Christian Schudt
 */
public class Board extends StackPane {
    /**
     * The number of hidden rows, which are located invisible above the board.
     * This is the area where the tetrominos spawn.
     * By default there are 2.
     */
    public static final int HIDDEN_ROWS = 2;

    /**
     * The width and height of a square unit.
     */
    public static final byte SQUARE = 35;

    /**
     * The number of blocks per row. By default this is 10.
     */
    private static final byte BLOCKS_PER_ROW = 10;

    /**
     * The number of blocks per column. By default this is 20.
     */
    private static final byte BLOCKS_PER_COLUMN = 20;

    /**
     * The number of maximal previews.
     */
    private static final short MAX_PREVIEWS = 1;

    /**
     * The move down transition.
     */
    private final TranslateTransition moveDownTransition;

    /**
     * The rotate transition.
     */
    private final RotateTransition rotateTransition;

    /**
     *
     */
    private final SequentialTransition sequentialTransition;

    /**
     * The move down fast transition.
     */
    private final TranslateTransition moveDownFastTransition;

    /**
     * The translate transition for the left/right movement.
     */
    private final TranslateTransition translateTransition;

    private final Set<Animation> runningAnimations = new HashSet<Animation>();

    /**
     * The two-dimensional array, which defines the board.
     */
    private Rectangle[][] matrix = new Rectangle[BLOCKS_PER_COLUMN + HIDDEN_ROWS][BLOCKS_PER_ROW];

    /**
     * Stores if the arrow down key was pressed, to prevent repeated events.
     */
    private boolean movingDown = false;

    private ObservableList<Tetromino> tetrominos = FXCollections.observableArrayList();

    private boolean moving = false;

    private int x = 0, y = 0;

    private boolean isDropping = false;

    private Tetromino currentTetromino;

    /**
     *
     */
    public Board() {
        Group gridPane = new Group();
        setFocusTraversable(true);

        setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent mouseEvent) {
                requestFocus();
            }
        });


        //setStyle("-fx-background-color:linear-gradient(to top, #000000 0%, #333333 100%)");

        setId("board");
        setMinWidth(SQUARE * BLOCKS_PER_ROW);
        setMinHeight(SQUARE * BLOCKS_PER_COLUMN);
        setClip(new Rectangle(SQUARE * BLOCKS_PER_ROW, SQUARE * BLOCKS_PER_COLUMN));

        Lighting lighting = new Lighting(new Light.Distant(225, 50, Color.WHITE));

        for (int i = 0; i < BLOCKS_PER_ROW; i++) {
            for (int j = 0; j < BLOCKS_PER_COLUMN; j++) {
                Rectangle rectangle = new Rectangle();
                rectangle.setWidth(SQUARE);
                rectangle.setHeight(SQUARE);
                rectangle.setX(i * SQUARE);
                rectangle.setY(j * SQUARE);

                rectangle.setArcHeight(3.2);
                rectangle.setArcWidth(rectangle.getArcHeight());
                rectangle.setFill(Color.rgb(0, 0, 0));

                lighting.setSurfaceScale(0.1);

                //lighting.setContentInput(new InnerShadow(23, Color.GRAY));

                rectangle.setEffect(lighting);
                //gridPane.getChildren().add(rectangle);

            }
        }


        setAlignment(Pos.TOP_LEFT);
        getChildren().add(gridPane);

        ObjectProperty<Duration> moveDownDuration = new SimpleObjectProperty<Duration>(Duration.seconds(0.3));


        moveDownTransition = new TranslateTransition(moveDownDuration.get());
        moveDownTransition.durationProperty().bind(moveDownDuration);
        moveDownTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                moving = false;
                y++;
            }
        });


        PauseTransition pauseTransition = new PauseTransition();
        pauseTransition.durationProperty().bind(moveDownDuration);

        sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().addAll(moveDownTransition, pauseTransition);
        sequentialTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                moveDown();
            }
        });
        registerPausableAnimation(sequentialTransition);

        moveDownFastTransition = new TranslateTransition(Duration.seconds(0.1));
        moveDownFastTransition.setInterpolator(Interpolator.EASE_OUT);
        moveDownFastTransition.setOnFinished(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                y++;
                moveDownFast();
            }
        });
        registerPausableAnimation(moveDownFastTransition);

        translateTransition = new TranslateTransition(Duration.seconds(0.1));
        registerPausableAnimation(translateTransition);

        rotateTransition = new RotateTransition(Duration.seconds(0.1));
    }

    public void start() {
        clear();
        requestFocus();
        spawnTetromino();
    }

    private void registerPausableAnimation(final Animation animation) {
        animation.statusProperty().addListener(new ChangeListener<Animation.Status>() {
            @Override
            public void changed(ObservableValue<? extends Animation.Status> observableValue, Animation.Status status, Animation.Status status2) {
                if (status2 == Animation.Status.STOPPED) {
                    runningAnimations.remove(animation);
                } else {
                    runningAnimations.add(animation);
                }
            }
        });
    }

    public void moveDownFast() {
        if (!isDropping) {
            sequentialTransition.stop();
            if (!intersectsWithBoard(currentTetromino.getMatrix(), x, y + 1)) {
                moveDownFastTransition.setToY((y + 1 - Board.HIDDEN_ROWS) * Board.SQUARE);
                moveDownFastTransition.playFromStart();
            } else {

                tetrominoDropped();
            }
        }
    }

    public void pause() {
        for (Animation animation : runningAnimations) {
            animation.pause();
        }

    }

    public void play() {
        for (Animation animation : runningAnimations) {
            animation.play();
        }
        requestFocus();
    }

    public ObservableList<Tetromino> getQueue() {
        return tetrominos;
    }

    /**
     * Spawns a new random tetromino.
     */
    private void spawnTetromino() {

        while (tetrominos.size() <= MAX_PREVIEWS) {
            tetrominos.add(Tetromino.random());
        }

        currentTetromino = tetrominos.remove(0);


        rotateTransition.setNode(currentTetromino);
        rotateTransition.setToAngle(0);

        translateTransition.setNode(currentTetromino);
        moveDownTransition.setNode(currentTetromino);
        moveDownFastTransition.setNode(currentTetromino);
        getChildren().add(currentTetromino);
        // Move it to the correct position
        // Spawn the tetromino in the middle (I, O) or in the left middle (J, L, S, T, Z).
        x = (getMatrix()[0].length - currentTetromino.getMatrix().length) / 2;
        y = 0;
        currentTetromino.setTranslateY((y - Board.HIDDEN_ROWS) * Board.SQUARE);
        currentTetromino.setTranslateX(x * Board.SQUARE);
        translateTransition.setToX(currentTetromino.getTranslateX());

        moveDown();
    }

    /**
     * Gets the matrix of the board.
     *
     * @return The matrix.
     */
    public Rectangle[][] getMatrix() {
        return matrix;
    }

    /**
     * Notification of the tetromino, that it can't move further down.
     */
    public void tetrominoDropped() {
        if (y == 0) {
            // If the piece could not move and we are still in the initial y position, the game is over.
            gameOver();
        } else {
            mergeTetrominoWithBoard();
            Sound.DROPPED.getAudioClip().play();
        }
    }

    /**
     * Merges the tetromino with the board.
     * For each tile, create a rectangle in the board.
     * Eventually removes the tetromino from the board and spawns a new one.
     */
    private void mergeTetrominoWithBoard() {
        int[][] tetrominoMatrix = currentTetromino.getMatrix();

        for (int i = 0; i < tetrominoMatrix.length; i++) {
            for (int j = 0; j < tetrominoMatrix[i].length; j++) {

                int x = this.x + j;
                int y = this.y + i;

                if (tetrominoMatrix[i][j] == 1) {
                    Rectangle rectangle = new Rectangle(SQUARE, SQUARE);
                    rectangle.setFill(currentTetromino.getFill());
                    ((Light.Distant) currentTetromino.getLighting().getLight()).azimuthProperty().set(245);
                    rectangle.setEffect(currentTetromino.getLighting());
                    rectangle.setTranslateX(x * SQUARE);
                    rectangle.setTranslateY((y - HIDDEN_ROWS) * SQUARE);
                    rectangle.setArcHeight(7);
                    rectangle.setArcWidth(7);
                    matrix[y][x] = rectangle;
                    getChildren().add(rectangle);

                }
            }
        }

        getChildren().remove(currentTetromino);


        ParallelTransition fallRowsTransition = new ParallelTransition();
        ParallelTransition deleteRowTransition = new ParallelTransition();
        int fall = 0;

        for (int i = y + currentTetromino.getMatrix().length - 1; i >= 0; i--) {
            if (i < matrix.length) {
                boolean rowComplete = i >= y;

                // Assume the row is complete. Let's prove the opposite.
                if (rowComplete) {
                    for (int j = 0; j < matrix[i].length; j++) {
                        if (matrix[i][j] == null) {
                            rowComplete = false;
                            break;
                        }
                    }
                }
                if (rowComplete) {
                    deleteRowTransition.getChildren().add(deleteRow(i));
                    fall++;
                } else if (fall > 0) {
                    fallRowsTransition.getChildren().add(fallRow(i, fall));
                }

            }
        }
        final int f = fall;
        fallRowsTransition.setOnFinished(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                Sound.DROPPED.getAudioClip().play();
                if (true)return;
                switch (f) {
                    case 1:
                        Sound.SINGLE.getAudioClip().play();
                        break;
                    case 2:
                        Sound.DOUBLE.getAudioClip().play();
                        break;
                    case 3:
                        Sound.TRIPLE.getAudioClip().play();
                        break;
                    case 4:
                        Sound.TETRIS.getAudioClip().play();
                        break;
                }

            }
        });
        if (deleteRowTransition.getChildren().size() > 0) {
            Sound.VANISH.getAudioClip().play();
        }
        final SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().add(deleteRowTransition);
        sequentialTransition.getChildren().add(fallRowsTransition);
        sequentialTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                spawnTetromino();
            }
        });
        registerPausableAnimation(sequentialTransition);
        sequentialTransition.playFromStart();

    }

    /**
     * @param i  The row index.
     * @param by The amount of rows.
     * @return The transition, which animates the falling row.
     */
    private Transition fallRow(int i, int by) {
        ParallelTransition parallelTransition = new ParallelTransition();

        if (by > 0) {
            for (int j = 0; j < matrix[i].length; j++) {
                Rectangle rectangle = matrix[i][j];
                if (rectangle != null) {
                    TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.1), rectangle);
                    translateTransition.setToY((i - HIDDEN_ROWS + by) * SQUARE);
                    parallelTransition.getChildren().add(translateTransition);
                }
                matrix[i + by][j] = rectangle;
            }
        }
        return parallelTransition;
    }

    /**
     * Deletes a row on the board.
     *
     * @param rowIndex The row index.
     * @return The transition, which animates the deleting row.
     */
    private Transition deleteRow(int rowIndex) {

        ParallelTransition parallelTransition = new ParallelTransition();

        for (int i = rowIndex; i >= 0; i--) {
            for (int j = 0; j < BLOCKS_PER_ROW; j++) {
                if (i > 1) {
                    final Rectangle rectangle = matrix[i][j];

                    if (i == rowIndex && rectangle != null) {
                        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.27), rectangle);
                        fadeTransition.setToValue(0);
                        fadeTransition.setCycleCount(3);
                        fadeTransition.setAutoReverse(true);
                        fadeTransition.setOnFinished(new EventHandler<ActionEvent>() {
                            public void handle(ActionEvent actionEvent) {
                                getChildren().remove(rectangle);
                            }
                        });
                        parallelTransition.getChildren().add(fadeTransition);
                    }

                }
            }
        }
        return parallelTransition;
    }

    /**
     * Gets the current tetromino.
     *
     * @return The tetromino.
     */
    public Tetromino getTetromino() {
        return currentTetromino;
    }

    /**
     * The game is over. Clears the board, and restarts the game.
     */
    private void gameOver() {
        clear();
        spawnTetromino();
    }

    private void clear() {
        for (int i = 0; i < BLOCKS_PER_COLUMN + HIDDEN_ROWS; i++) {
            for (int j = 0; j < BLOCKS_PER_ROW; j++) {
                if (matrix[i][j] != null) {
                    getChildren().remove(matrix[i][j]);
                    matrix[i][j] = null;
                }
            }
        }
        getChildren().remove(currentTetromino);
        currentTetromino = null;
    }

    /**
     * Drops the tetromino down to the next possible position.
     */
    public void dropDown() {


        sequentialTransition.stop();
        moveDownFastTransition.stop();

        while (!intersectsWithBoard(currentTetromino.getMatrix(), x, ++y)) ;
        y--;
        isDropping = true;
        final TranslateTransition dropDownTransition = new TranslateTransition(Duration.seconds(0.1), currentTetromino);
        dropDownTransition.setToY((y - Board.HIDDEN_ROWS) * Board.SQUARE);
        dropDownTransition.setOnFinished(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                isDropping = false;
                tetrominoDropped();
            }
        });
        registerPausableAnimation(dropDownTransition);
        dropDownTransition.playFromStart();
    }

    /**
     * Tries to rotate the tetromino.
     *
     * @param direction The horizontal direction.
     * @return True, if the rotation was successful, otherwise false.
     */
    public boolean rotate(final HorizontalDirection direction) {

        int[][] matrix = currentTetromino.getMatrix();

        int[][] newMatrix = new int[matrix.length][matrix.length];


        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (direction == HorizontalDirection.RIGHT) {
                    newMatrix[j][matrix.length - 1 - i] = matrix[i][j];
                } else {
                    newMatrix[matrix[i].length - 1 - j][i] = matrix[i][j];
                }
            }
        }

        if (!intersectsWithBoard(newMatrix, x, y)) {
            currentTetromino.setMatrix(newMatrix);

            int f = direction == HorizontalDirection.RIGHT ? 1 : -1;

            rotateTransition.setToAngle(rotateTransition.getToAngle() + f * 90);

            KeyValue kv = new KeyValue(((Light.Distant) currentTetromino.getLighting().getLight()).azimuthProperty(), 360 - 225 + 90 - rotateTransition.getToAngle());
            KeyFrame keyFrame = new KeyFrame(rotateTransition.getDuration(), kv);
            Timeline lightingAnimation = new Timeline(keyFrame);

            final ParallelTransition parallelTransition = new ParallelTransition(rotateTransition, lightingAnimation);
            registerPausableAnimation(parallelTransition);
            parallelTransition.playFromStart();
            return true;
        }
        return false;
    }

    /**
     * Calculates if the tetromino would intersect with the board,
     * by passing a matrix that the tetromino is going to have.
     * <p/>
     * It intersects either, if it hits another tetromino or if it exceeds the left, right or bottom border.
     *
     * @param targetMatrix The matrix of the tetromino.
     * @param targetX      The target X position.
     * @param targetY      The target Y position.
     * @return True, if it does intersect with the board, otherwise false.
     */
    private boolean intersectsWithBoard(final int[][] targetMatrix, int targetX, int targetY) {
        Rectangle[][] boardMatrix = getMatrix();

        for (int i = 0; i < targetMatrix.length; i++) {
            for (int j = 0; j < targetMatrix[i].length; j++) {

                boolean boardBlocks = false;
                int x = targetX + j;
                int y = targetY + i;

                if (x < 0 || x >= boardMatrix[i].length || y >= boardMatrix.length) {
                    boardBlocks = true;
                } else if (boardMatrix[y][x] != null) {
                    boardBlocks = true;
                }

                if (boardBlocks && targetMatrix[i][j] == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Moves the tetromino to left or right.
     *
     * @param direction The horizontal direction.
     * @return True, if the movement was successful. False, if the movement was blocked by the board.
     */
    public boolean move(final HorizontalDirection direction) {
        int i = direction == HorizontalDirection.RIGHT ? 1 : -1;
        x += i;
        // If it is not moving, only check the current y position.
        // If it is moving, also check the target y position.
        if (!moving && !intersectsWithBoard(currentTetromino.getMatrix(), x, y) || moving && !intersectsWithBoard(currentTetromino.getMatrix(), x, y) && !intersectsWithBoard(currentTetromino.getMatrix(), x, y + 1)) {
            translateTransition.setToX(x * Board.SQUARE);
            translateTransition.playFromStart();
            return true;
        } else {
            x -= i;
            return false;
        }
    }

    /**
     * Moves the tetromino one field down.
     */
    public void moveDown() {
        if (!isDropping) {
            moveDownFastTransition.stop();
            moving = true;

            // If it is able to move to the next y position, do it!
            if (!intersectsWithBoard(currentTetromino.getMatrix(), x, y + 1) && !isDropping) {
                moveDownTransition.setFromY(moveDownTransition.getNode().getTranslateY());
                moveDownTransition.setToY((y + 1 - Board.HIDDEN_ROWS) * Board.SQUARE);
                sequentialTransition.playFromStart();
            } else {
                tetrominoDropped();
            }
        }
    }
}
