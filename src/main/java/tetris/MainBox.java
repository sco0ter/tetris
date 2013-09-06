package tetris;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

/**
 * @author Christian Schudt
 */
final class MainBox extends HBox {
    public MainBox(final GameController gameController) {

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(gameController.getBoard());

        stackPane.getChildren().add(gameController.getNotificationOverlay());
        stackPane.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(stackPane, Priority.ALWAYS);
        getChildren().add(stackPane);
        getChildren().add(new InfoBox(gameController));
    }
}
