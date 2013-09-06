package tetris;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.Light;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.EventListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class represents the main board.
 *
 * @author Christian Schudt
 */
final class Board extends StackPane {

    /**
     * The number of hidden rows, which are located invisible above the board.
     * This is the area where the tetrominos spawn.
     * By default there are 2.
     */
    private static final byte HIDDEN_ROWS = 2;

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
    private static final byte MAX_PREVIEWS = 1;

    /**
     * The move down transition.
     */
    private final TranslateTransition moveDownTransition;

    /**
     * The rotate transition.
     */
    private final RotateTransition rotateTransition;

    /**
     * The sequential transition, which consists of the {@link #moveDownTransition} and a {@link PauseTransition}.
     */
    private final SequentialTransition moveTransition;

    /**
     * The transition, which let's a piece move down fast.
     */
    private final TranslateTransition moveDownFastTransition;

    /**
     * The translate transition for the left/right movement.
     */
    private final TranslateTransition translateTransition;

    /**
     * A set of running transitions. All running transitions are paused, when the game is paused.
     */
    private final Set<Animation> runningAnimations = new HashSet<Animation>();

    /**
     * The two-dimensional array, which defines the board. If an element is null in the matrix, it is empty, otherwise it is occupied.
     */
    private final Rectangle[][] matrix = new Rectangle[BLOCKS_PER_COLUMN + HIDDEN_ROWS][BLOCKS_PER_ROW];

    /**
     * The list of tetrominos, which are coming next.
     */
    private final ObservableList<Tetromino> waitingTetrominos = FXCollections.observableArrayList();

    /**
     * The very fast drop down transition.
     */
    private final TranslateTransition dropDownTransition;

    /**
     * Stores, if the down key is pressed. As long as this is the case, the {@link #moveDownFastTransition} is played.
     */
    private boolean moving = false;

    /**
     * The current x and y position with the matrix of the current tetromino.
     */
    private int x = 0, y = 0;

    /**
     * True, while the tetromino is dropped (with the space key).
     */
    private boolean isDropping = false;

    /**
     * The current tetromino, which is falling.
     */
    private Tetromino currentTetromino;

    /**
     * Holds the board listeners.
     */
    private List<BoardListener> boardListeners = new CopyOnWriteArrayList<BoardListener>();

    private DoubleProperty squareSize = new SimpleDoubleProperty();

    /**
     * Creates the board.
     */
    public Board() {
        setFocusTraversable(true);


        setId("board");
        setMinWidth(35 * BLOCKS_PER_ROW);
        setMinHeight(35 * BLOCKS_PER_COLUMN);

        maxWidthProperty().bind(minWidthProperty());
        maxHeightProperty().bind(minHeightProperty());
        //setStyle("-fx-border-color:red");
        //        minHeightProperty().bind(new DoubleBinding() {
        //            {
        //                super.bind(widthProperty());
        //            }
        //
        //            @Override
        //            protected double computeValue() {
        //                return getWidth() * BLOCKS_PER_COLUMN / BLOCKS_PER_ROW;
        //            }
        //        });
        //        maxHeightProperty().bind(minHeightProperty());

        clipProperty().bind(new ObjectBinding<Node>() {
            {
                super.bind(widthProperty(), heightProperty());
            }

            @Override
            protected Node computeValue() {
                return new Rectangle(getWidth(), getHeight());
            }
        });

        setAlignment(Pos.TOP_LEFT);

        // Initialize move down transition.
        moveDownTransition = new TranslateTransition(Duration.seconds(0.3));
        moveDownTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                moving = false;
                y++;
            }
        });

        // After the piece has moved down, wait shortly until it moves again.
        PauseTransition pauseTransition = new PauseTransition();
        pauseTransition.durationProperty().bind(moveDownTransition.durationProperty());

        moveTransition = new SequentialTransition();
        moveTransition.getChildren().addAll(moveDownTransition, pauseTransition);
        moveTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                moveDown();
            }
        });

        // This movement should be pausable.
        registerPausableAnimation(moveTransition);

        // Moves the piece down fast.
        moveDownFastTransition = new TranslateTransition(Duration.seconds(0.08));
        // To make it look more smoothly, use a linear interpolator.
        moveDownFastTransition.setInterpolator(Interpolator.LINEAR);
        moveDownFastTransition.setOnFinished(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                y++;
                moveDownFast();
            }
        });
        registerPausableAnimation(moveDownFastTransition);

        // Moves the piece left and right.
        translateTransition = new TranslateTransition(Duration.seconds(0.1));
        registerPausableAnimation(translateTransition);

        // Rotates the piece.
        rotateTransition = new RotateTransition(Duration.seconds(0.1));
        dropDownTransition = new TranslateTransition(Duration.seconds(0.1));
        dropDownTransition.setInterpolator(Interpolator.EASE_IN);

        squareSize.bind(new DoubleBinding() {
            {
                super.bind(widthProperty());
            }

            @Override
            protected double computeValue() {
                return getWidth() / BLOCKS_PER_ROW;
            }
        });
    }

    /**
     * Registers an animation, which is added to the list of running animations, if it is running, and is removed again, if it is stopped.
     * When the game pauses, all running animations are paused.
     *
     * @param animation The animation.
     */
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

    /**
     * Spawns a new random tetromino.
     */
    private void spawnTetromino() {

        // Fill the queue of waiting tetrominos, if it's empty.
        while (waitingTetrominos.size() <= MAX_PREVIEWS) {
            waitingTetrominos.add(Tetromino.random(squareSize));
        }

        // Remove the first from the queue and spawn it.
        currentTetromino = waitingTetrominos.remove(0);

        // Reset all transitions.
        rotateTransition.setNode(currentTetromino);
        rotateTransition.setToAngle(0);

        translateTransition.setNode(currentTetromino);
        moveDownTransition.setNode(currentTetromino);
        moveDownFastTransition.setNode(currentTetromino);

        // Add the current tetromino to the board.
        getChildren().add(currentTetromino);

        // Move it to the correct position
        // Spawn the tetromino in the middle (I, O) or in the left middle (J, L, S, T, Z).
        x = (matrix[0].length - currentTetromino.getMatrix().length) / 2;
        y = 0;
        // Translate the tetromino to its starting position.
        currentTetromino.setTranslateY((y - Board.HIDDEN_ROWS) * getSquareSize());
        currentTetromino.setTranslateX(x * getSquareSize());

        //translateTransition.setToX(currentTetromino.getTranslateX());

        // Start to move it.
        moveDown();
    }

    /**
     * Notification of the tetromino, that it can't move further down.
     */
    private void tetrominoDropped() {
        if (y == 0) {
            // If the piece could not move and we are still in the initial y position, the game is over.
            currentTetromino = null;
            waitingTetrominos.clear();
            notifyGameOver();
        } else {
            mergeTetrominoWithBoard();
        }
    }

    /**
     * Notifies the listener, that a piece has dropped.
     */
    private void notifyOnDropped() {
        for (BoardListener boardListener : boardListeners) {
            boardListener.onDropped();
        }
    }

    /**
     * Notifies the listener, that the game is over.
     */
    private void notifyGameOver() {
        for (BoardListener boardListener : boardListeners) {
            boardListener.onGameOver();
        }
    }

    /**
     *
     */
    private void notifyOnMove(HorizontalDirection horizontalDirection) {

        for (BoardListener boardListener : boardListeners) {
            boardListener.onMove(horizontalDirection);
        }
    }

    /**
     * Notifies the listeners, that rows were eliminated.
     *
     * @param rows The number of rows.
     */
    private void notifyOnRowsEliminated(int rows) {
        for (BoardListener boardListener : boardListeners) {
            boardListener.onRowsEliminated(rows);
        }
    }

    /**
     * Notifies the listeners, that an invalid move was tried.
     */
    private void notifyInvalidMove() {
        for (BoardListener boardListener : boardListeners) {
            boardListener.onInvalidMove();
        }
    }

    /**
     * Notifies the listeners, that an invalid move was tried.
     */
    private void notifyRotate(HorizontalDirection horizontalDirection) {
        for (BoardListener boardListener : boardListeners) {
            boardListener.onRotate(horizontalDirection);
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

                final int x = this.x + j;
                final int y = this.y + i;

                if (tetrominoMatrix[i][j] == 1 && y < BLOCKS_PER_COLUMN + HIDDEN_ROWS && x < BLOCKS_PER_ROW) {
                    final Rectangle rectangle = new Rectangle();

                    ChangeListener<Number> changeListener = new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                            rectangle.setWidth(number2.doubleValue());
                            rectangle.setHeight(number2.doubleValue());
                            rectangle.setTranslateX(number2.doubleValue() * x);
                            rectangle.setTranslateY(number2.doubleValue() * ((Integer) rectangle.getProperties().get("y")));
                        }
                    };
                    squareSize.addListener(new WeakChangeListener<Number>(changeListener));
                    rectangle.setUserData(changeListener);
                    rectangle.getProperties().put("y", y - HIDDEN_ROWS);
                    rectangle.setWidth(squareSize.doubleValue());
                    rectangle.setHeight(squareSize.doubleValue());
                    rectangle.setTranslateX(squareSize.doubleValue() * x);
                    rectangle.setTranslateY(squareSize.doubleValue() * ((Integer) rectangle.getProperties().get("y")));

                    rectangle.setFill(currentTetromino.getFill());
                    ((Light.Distant) currentTetromino.getLighting().getLight()).azimuthProperty().set(225);
                    rectangle.setEffect(currentTetromino.getLighting());

                    rectangle.setArcHeight(7);
                    rectangle.setArcWidth(7);
                    // Assign a rectangle to the board matrix.
                    matrix[y][x] = rectangle;
                    getChildren().add(rectangle);
                }
            }
        }

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
                notifyOnDropped();
            }
        });

        // If at least one row has been eliminated.
        if (f > 0) {
            notifyOnRowsEliminated(f);
        }
        final SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().add(deleteRowTransition);
        sequentialTransition.getChildren().add(fallRowsTransition);
        sequentialTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //sequentialTransition.getChildren().clear();
                spawnTetromino();
            }
        });
        // Cached nodes leak memory
        // https://javafx-jira.kenai.com/browse/RT-32733
        //currentTetromino.setCache(false);
        getChildren().remove(currentTetromino);
        currentTetromino = null;
        registerPausableAnimation(sequentialTransition);
        sequentialTransition.playFromStart();
        notifyOnDropped();
    }

    /**
     * @param i  The row index.
     * @param by The amount of rows.
     * @return The transition, which animates the falling row.
     */
    private Transition fallRow(final int i, final int by) {
        ParallelTransition parallelTransition = new ParallelTransition();

        if (by > 0) {
            for (int j = 0; j < matrix[i].length; j++) {
                final Rectangle rectangle = matrix[i][j];

                if (rectangle != null) {
                    // Unbind the original y position, to allow the rectangle to move to its new one.
                    //rectangle.translateYProperty().unbind();
                    final TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.1), rectangle);
                    rectangle.getProperties().put("y", i - HIDDEN_ROWS + by);

                    translateTransition.toYProperty().bind(squareSize.multiply(i - HIDDEN_ROWS + by));
                    translateTransition.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            translateTransition.toYProperty().unbind();

                            //rectangle.translateYProperty().bind(squareSize.multiply(i - HIDDEN_ROWS + by));
                        }
                    });
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
     * Clears the board and the waiting tetrominos.
     */
    private void clear() {
        for (int i = 0; i < BLOCKS_PER_COLUMN + HIDDEN_ROWS; i++) {
            for (int j = 0; j < BLOCKS_PER_ROW; j++) {
                matrix[i][j] = null;
            }
        }
        getChildren().clear();
        getChildren().remove(currentTetromino);
        currentTetromino = null;
        waitingTetrominos.clear();
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
        Rectangle[][] boardMatrix = matrix;

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
     * Starts the board by spawning a new tetromino.
     */
    public void start() {
        clear();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                requestFocus();
            }
        });
        spawnTetromino();
    }

    /**
     * Drops the tetromino down to the next possible position.
     */
    public void dropDown() {
        if (currentTetromino == null) {
            return;
        }

        moveTransition.stop();
        moveDownFastTransition.stop();
        dropDownTransition.stop();

        do {
            y++;
        }
        while (!intersectsWithBoard(currentTetromino.getMatrix(), x, y));
        y--;
        isDropping = true;
        dropDownTransition.setNode(currentTetromino);
        dropDownTransition.toYProperty().bind(squareSize.multiply(y - Board.HIDDEN_ROWS));
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
        boolean result = false;
        if (currentTetromino == null) {
            result = false;
        } else {
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
                parallelTransition.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        // clear, because otherwise parallelTransition won't be gc'ed because it has reference to rotateTransition.
                        parallelTransition.getChildren().clear();
                    }
                });
                parallelTransition.playFromStart();
                result = true;
            }
        }

        if (!result) {
            notifyInvalidMove();
        } else {
            notifyRotate(direction);
        }
        return result;
    }

    /**
     * Moves the tetromino to left or right.
     *
     * @param direction The horizontal direction.
     * @return True, if the movement was successful. False, if the movement was blocked by the board.
     */
    public boolean move(final HorizontalDirection direction) {
        boolean result;
        if (currentTetromino == null || isDropping) {
            result = false;
        } else {
            int i = direction == HorizontalDirection.RIGHT ? 1 : -1;
            x += i;
            // If it is not moving, only check the current y position.
            // If it is moving, also check the target y position.
            if (!moving && !intersectsWithBoard(currentTetromino.getMatrix(), x, y) || moving && !intersectsWithBoard(currentTetromino.getMatrix(), x, y) && !intersectsWithBoard(currentTetromino.getMatrix(), x, y + 1)) {
                translateTransition.toXProperty().unbind();
                translateTransition.toXProperty().bind(squareSize.multiply(x));
                translateTransition.playFromStart();
                result = true;
            } else {
                x -= i;
                result = false;
            }
        }
        if (!result) {
            notifyInvalidMove();
        } else {
            notifyOnMove(direction);
        }
        return result;
    }

    /**
     * Moves the tetromino one field down.
     */
    public void moveDown() {
        if (!isDropping && currentTetromino != null) {
            moveDownFastTransition.stop();
            moving = true;

            // If it is able to move to the next y position, do it!
            if (!intersectsWithBoard(currentTetromino.getMatrix(), x, y + 1) && !isDropping) {
                //moveDownTransition.setFromY(moveDownTransition.getNode().getTranslateY());
                moveDownTransition.toYProperty().unbind();
                moveDownTransition.toYProperty().bind(squareSize.multiply(y + 1 - Board.HIDDEN_ROWS));
                moveTransition.playFromStart();
            } else {
                tetrominoDropped();
            }
        }
    }

    /**
     * Moves the current tetromino down fast, if it not already dropping.
     */
    public void moveDownFast() {
        if (!isDropping) {

            // Stop the normal move transition.
            moveTransition.stop();
            // Then check, if the next position, would not intersect with the board.
            if (!intersectsWithBoard(currentTetromino.getMatrix(), x, y + 1)) {
                // If it can move, move it!
                moveDownFastTransition.toYProperty().unbind();
                moveDownFastTransition.toYProperty().bind(squareSize.multiply(y + 1 - Board.HIDDEN_ROWS));
                moveDownFastTransition.playFromStart();
            } else {
                // Otherwise it has reached ground.
                tetrominoDropped();
            }
        }
    }

    /**
     * Pauses the board.
     *
     * @see #play()
     */
    public void pause() {
        for (Animation animation : runningAnimations) {
            if (animation.getStatus() == Animation.Status.RUNNING) {
                animation.pause();
            }
        }
    }

    /**
     * Plays the board again, after it has been paused.
     *
     * @see #pause()
     */
    public void play() {
        for (Animation animation : runningAnimations) {
            if (animation.getStatus() == Animation.Status.PAUSED) {
                animation.play();
            }
        }
        requestFocus();
    }

    /**
     * Gets the waiting tetrominos, which are about to be spawned.
     * <p/>
     * The first element will be spawned next.
     *
     * @return The list of queued tetrominos.
     */
    public ObservableList<Tetromino> getWaitingTetrominos() {
        return waitingTetrominos;
    }

    public double getSquareSize() {
        return squareSize.get();
    }

    /**
     * Adds a listener to the board, which gets notified for certain events.
     *
     * @param boardListener The listener.
     */
    public void addBoardListener(BoardListener boardListener) {
        boardListeners.add(boardListener);
    }

    /**
     * Removes a listener, which was previously added by {@link #addBoardListener(tetris.Board.BoardListener)}
     *
     * @param boardListener The listener.
     */
    public void removeBoardListener(BoardListener boardListener) {
        boardListeners.remove(boardListener);
    }

    /**
     * Allows to listen for certain board events.
     */
    public static interface BoardListener extends EventListener {


        /**
         * Called, when a tetromino is dropped or a complete row is dropped after some rows were eliminated.
         */
        void onDropped();

        /**
         * Called, when one or more rows are full and therefore get eliminated.
         *
         * @param rows The number of rows.
         */
        void onRowsEliminated(int rows);

        /**
         * Called when the game is over.
         */
        void onGameOver();

        /**
         * Called when an invalid was made.
         */
        void onInvalidMove();

        /**
         * Called when a piece was moved.
         *
         * @param horizontalDirection The direction.
         */
        void onMove(HorizontalDirection horizontalDirection);

        /**
         * Called when a piece was rotated.
         *
         * @param horizontalDirection The direction.
         */
        void onRotate(HorizontalDirection horizontalDirection);
    }
}
