package tetris.board;

import javafx.scene.layout.HBox;
import tetris.GameController;
import tetris.Tetris;

/**
 * @author Christian Schudt
 */
public class MainBox extends HBox {
    public MainBox(final GameController gameController) {

        getChildren().add(gameController.getBoard());



        getChildren().add(new InfoBox(gameController));
    }
}
