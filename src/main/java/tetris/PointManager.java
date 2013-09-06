package tetris;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.HorizontalDirection;

/**
 * @author Christian Schudt
 */
final class PointManager implements Board.BoardListener {

    private final IntegerProperty points = new SimpleIntegerProperty();

    private final GameController gameController;

    public PointManager(GameController gameController) {
        this.gameController = gameController;
        gameController.getBoard().addBoardListener(this);
    }

    public IntegerProperty pointsProperty() {
        return points;
    }

    private void addPoints(int points) {
        this.points.set(this.points.get() + points);
    }

    @Override
    public void onDropped() {
    }

    @Override
    public void onRowsEliminated(int rows) {
        switch (rows) {
            case 1:
                addPoints(40);
                break;
            case 2:
                addPoints(100);
                break;
            case 3:
                addPoints(300);
                break;
            case 4:
                addPoints(1200);
                break;
        }
    }

    @Override
    public void onGameOver() {
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
