package tetris.board;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import tetris.GameController;

/**
 * @author Christian Schudt
 */
public class MainBox extends HBox {
    public MainBox(final GameController gameController) {

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(gameController.getBoard());

        stackPane.getChildren().add(gameController.getPointOverlay());


        getChildren().add(stackPane);
        getChildren().add(new InfoBox(gameController));
    }
}
