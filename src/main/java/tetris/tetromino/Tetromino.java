package tetris.tetromino;

import javafx.scene.CacheHint;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import tetris.board.Board;

import java.util.Random;

/**
 * @author Christian Schudt
 */
@SuppressWarnings("unused")
public class Tetromino extends Polygon {

    private static final Random RANDOM = new Random();

    private static final TetrominoDefinition I = new TetrominoDefinition(new int[][]{
            {0, 0, 0, 0},
            {1, 1, 1, 1},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
    }, new double[]{
            0.0, 0.0,
            0.0, 1.0,
            4.0, 1.0,
            4.0, 4.0,
            4.0, 2.0,
            0.0, 2.0},
            Color.CYAN);

    private static final TetrominoDefinition J = new TetrominoDefinition(new int[][]{
            {1, 0, 0},
            {1, 1, 1},
            {0, 0, 0}
    }, new double[]{
            0.0, 0.0,
            1.0, 0.0,
            1.0, 1.0,
            3.0, 1.0,
            3.0, 3.0,
            3.0, 2.0,
            0.0, 2.0},
            Color.BLUE);

    private static final TetrominoDefinition L = new TetrominoDefinition(new int[][]{
            {0, 0, 1},
            {1, 1, 1},
            {0, 0, 0}}, new double[]{
            3.0, 3.0,
            3.0, 0.0,
            2.0, 0.0,
            2.0, 1.0,
            0.0, 1.0,
            0.0, 2.0,
            3.0, 2.0},
            Color.ORANGE);

    private static final TetrominoDefinition O = new TetrominoDefinition(new int[][]{
            {1, 1},
            {1, 1}
    }, new double[]{
            0.0, 0.0,
            0.0, 2.0,
            2.0, 2.0,
            2.0, 0.0},
            Color.YELLOW);

    private static final TetrominoDefinition S = new TetrominoDefinition(new int[][]{
            {0, 1, 1},
            {1, 1, 0},
            {0, 0, 0}
    }, new double[]{
            0.0, 1.0,
            1.0, 1.0,
            1.0, 0.0,
            3.0, 0.0,
            3.0, 3.0,
            3.0, 1.0,
            2.0, 1.0,
            2.0, 2.0,
            0.0, 2.0},
            Color.GREEN);

    private static final TetrominoDefinition T = new TetrominoDefinition(new int[][]{
            {0, 1, 0},
            {1, 1, 1},
            {0, 0, 0}
    }, new double[]{
            0.0, 1.0,
            1.0, 1.0,
            1.0, 0.0,
            2.0, 0.0,
            2.0, 1.0,
            3.0, 1.0,
            3.0, 3.0,
            3.0, 2.0,
            0.0, 2.0},
            Color.PURPLE);

    private static final TetrominoDefinition Z = new TetrominoDefinition(new int[][]{
            {0, 1, 0},
            {1, 1, 1},
            {0, 0, 0}
    }, new double[]{
            0.0, 1.0,
            1.0, 1.0,
            1.0, 0.0,
            2.0, 0.0,
            2.0, 1.0,
            3.0, 1.0,
            3.0, 3.0,
            3.0, 2.0,
            0.0, 2.0},
            Color.PURPLE);

    private static final TetrominoDefinition[] TETROMINO_DEFINITIONS = new TetrominoDefinition[]{I, J, L, O, S, T, Z};

    private int[][] matrix;

    /**
     * The light. This has to be rotated, too, as the tetrominos rotate.
     */
    private final Light.Distant light = new Light.Distant(225, 50, Color.WHITE);

    private Tetromino(TetrominoDefinition tetrominoDefinition) {
        this.matrix = tetrominoDefinition.matrix;

        for (double d : tetrominoDefinition.points) {
            getPoints().add(d * Board.SQUARE);
        }
        setFill(tetrominoDefinition.color);
        setEffect(new Lighting(light));

        setCacheHint(CacheHint.SPEED);
        setCache(true);
    }

    public static Tetromino random() {
        TetrominoDefinition tetrominoDefinition = TETROMINO_DEFINITIONS[RANDOM.nextInt(7)];

        return new Tetromino(tetrominoDefinition);
    }

    public Light.Distant getLight() {
        return light;
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

        private final double[] points;

        private TetrominoDefinition(int[][] matrix, double[] points, Color color) {
            this.color = color;
            this.matrix = matrix;
            this.points = points;
        }
    }
}
