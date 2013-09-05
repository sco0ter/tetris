package tetris.tetromino;

import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import tetris.board.Board;

import java.util.Random;

/**
 * @author Christian Schudt
 */
@SuppressWarnings("unused")
public class Tetromino extends Group {

    private static final Random RANDOM = new Random();

    private static final TetrominoDefinition I = new TetrominoDefinition(new int[][]{
            {0, 0, 0, 0},
            {1, 1, 1, 1},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
    },
            //            new double[]{
            //            0.0, 0.0,
            //            0.0, 1.0,
            //            4.0, 1.0,
            //            4.0, 4.0,
            //            4.0, 2.0,
            //            0.0, 2.0},
            Color.CYAN);

    private static final TetrominoDefinition J = new TetrominoDefinition(new int[][]{
            {1, 0, 0},
            {1, 1, 1},
            {0, 0, 0}
    },
            //            new double[]{
            //            0.0, 0.0,
            //            1.0, 0.0,
            //            1.0, 1.0,
            //            3.0, 1.0,
            //            3.0, 3.0,
            //            3.0, 2.0,
            //            0.0, 2.0},
            Color.BLUE);

    private static final TetrominoDefinition L = new TetrominoDefinition(new int[][]{
            {0, 0, 1},
            {1, 1, 1},
            {0, 0, 0}},
            //            new double[]{
            //            3.0, 3.0,
            //            3.0, 0.0,
            //            2.0, 0.0,
            //            2.0, 1.0,
            //            0.0, 1.0,
            //            0.0, 2.0,
            //            3.0, 2.0},
            Color.ORANGE);

    private static final TetrominoDefinition O = new TetrominoDefinition(new int[][]{
            {1, 1},
            {1, 1}
    },
            //            new double[]{
            //            0.0, 0.0,
            //            0.0, 2.0,
            //            2.0, 2.0,
            //            2.0, 0.0},
            Color.YELLOW);

    private static final TetrominoDefinition S = new TetrominoDefinition(new int[][]{
            {0, 1, 1},
            {1, 1, 0},
            {0, 0, 0}
    },
            //            new double[]{
            //            0.0, 1.0,
            //            1.0, 1.0,
            //            1.0, 0.0,
            //            3.0, 0.0,
            //            3.0, 3.0,
            //            3.0, 1.0,
            //            2.0, 1.0,
            //            2.0, 2.0,
            //            0.0, 2.0},
            Color.GREENYELLOW);

    private static final TetrominoDefinition T = new TetrominoDefinition(new int[][]{
            {0, 1, 0},
            {1, 1, 1},
            {0, 0, 0}
    },
            //            new double[]{
            //            0.0, 1.0,
            //            1.0, 1.0,
            //            1.0, 0.0,
            //            2.0, 0.0,
            //            2.0, 1.0,
            //            3.0, 1.0,
            //            3.0, 3.0,
            //            3.0, 2.0,
            //            0.0, 2.0},
            Color.PURPLE);

    private static final TetrominoDefinition Z = new TetrominoDefinition(new int[][]{
            {1, 1, 0},
            {0, 1, 1},
            {0, 0, 0}
    },
            //            new double[]{
            //            0.0, 1.0,
            //            1.0, 1.0,
            //            1.0, 0.0,
            //            3.0, 0.0,
            //            3.0, 3.0,
            //            3.0, 1.0,
            //            2.0, 1.0,
            //            2.0, 2.0,
            //            0.0, 2.0},
            Color.ORANGERED);

    private static final TetrominoDefinition[] TETROMINO_DEFINITIONS = new TetrominoDefinition[]{I, J, L, O, S, T, Z};

    /**
     * The light. This has to be rotated, too, as the tetrominos rotate.
     */
    private Lighting lighting = new Lighting(new Light.Distant(245, 50, Color.WHITE));

    private int[][] matrix;

    private Paint paint;

    private TetrominoDefinition tetrominoDefinition;

    private Tetromino(TetrominoDefinition tetrominoDefinition) {
        this.matrix = tetrominoDefinition.matrix;
        this.tetrominoDefinition = tetrominoDefinition;
        //paint = new LinearGradient(0, 0, 0, 100, false, CycleMethod.NO_CYCLE, new Stop(0, tetrominoDefinition.color.brighter().brighter()), new Stop(1, tetrominoDefinition.color.darker()));
        //paint = new RadialGradient(180, 10, 17, 17, 17, false, CycleMethod.NO_CYCLE, new Stop(0, tetrominoDefinition.color.brighter().brighter()), new Stop(1, tetrominoDefinition.color.darker()));
        paint = tetrominoDefinition.color;

        lighting = new Lighting(new Light.Distant(240, 55, Color.WHITE));


        lighting.setSurfaceScale(0.8);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == 1) {
                    Rectangle rectangle = new Rectangle(Board.SQUARE * 1, Board.SQUARE * 1);
                    rectangle.setTranslateY(Board.SQUARE * (i));
                    rectangle.setTranslateX(Board.SQUARE * (j));
                    rectangle.setEffect(lighting);
                    rectangle.setFill(tetrominoDefinition.color);
                    rectangle.setFill(paint);
                    rectangle.setArcHeight(7);
                    rectangle.setArcWidth(7);
                    getChildren().add(rectangle);
                } else {
                    Rectangle rectangle = new Rectangle(Board.SQUARE * 1, Board.SQUARE * 1);
                    rectangle.setTranslateY(Board.SQUARE * (i));
                    rectangle.setTranslateX(Board.SQUARE * (j));
                    rectangle.setOpacity(0);
                    getChildren().add(rectangle);

                }

            }
        }
        //setFill(new LinearGradient(0, 0, 0, 100, false, CycleMethod.NO_CYCLE, new Stop(0, tetrominoDefinition.color), new Stop(1, tetrominoDefinition.color.darker())));
        //setFill(tetrominoDefinition.color);

        //setEffect(new Lighting(light));

        setCacheHint(CacheHint.SPEED);
        setCache(true);
    }

    public static Tetromino random() {
        TetrominoDefinition tetrominoDefinition = TETROMINO_DEFINITIONS[RANDOM.nextInt(7)];

        return new Tetromino(tetrominoDefinition);
    }

    public Tetromino clone() {
        return new Tetromino(tetrominoDefinition);
    }

    public Paint getFill() {
        return paint;
    }

    public Lighting getLighting() {
        return lighting;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }

    private static class TetrominoDefinition {
        private final Color color;

        private final int[][] matrix;

        private TetrominoDefinition(int[][] matrix, Color color) {
            this.color = color;
            this.matrix = matrix;
        }
    }
}
