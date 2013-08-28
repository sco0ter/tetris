package rotation;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class TestApp extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void start(final Stage stage) throws Exception {

        VBox root = new VBox();

        final Button button = new Button("I am a Button");
        button.setRotationAxis(Rotate.Y_AXIS);
        button.setRotate(0);

        final RotationPane rotationPane = new RotationPane();
        rotationPane.getChildren().addAll(new Button("Hallooo !!!!"), button, new TextField("Blabla"), VBoxBuilder.create().prefHeight(300).prefWidth(300).style("-fx-background-color:red").build());

        Button button2 = new Button();
        button2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                rotationPane.selectedIndexProperty().set((rotationPane.selectedIndexProperty().get() + 1) % rotationPane.getChildren().size());
            }
        });

        root.getChildren().addAll(rotationPane, button2);
        Scene scene = new Scene(root);
        PerspectiveCamera camera = new PerspectiveCamera();
        new RotationPane();
        scene.setCamera(camera);
        stage.setScene(scene);
        stage.show();
    }
}