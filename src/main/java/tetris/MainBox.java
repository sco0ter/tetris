package tetris;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * @author Christian Schudt
 */
final class MainBox extends HBox {
    public MainBox(final GameController gameController) {

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(gameController.getBoard());

        stackPane.getChildren().add(gameController.getNotificationOverlay());


        getChildren().add(stackPane);
        getChildren().add(new InfoBox(gameController));
    }
}
