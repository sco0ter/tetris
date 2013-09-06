import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class MemoryLeakTest extends Application {

    public static void main(String[] args) throws Exception {
        launch();
    }
      private List<Node> nodes = new ArrayList<Node>();

    ParallelTransition parallelTransition = new ParallelTransition();

    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox();




        for (int i = 0; i < 10000; i++) {
            Node node = new Label();
            Animation animation = new TranslateTransition(Duration.seconds(1), node);
            animation.statusProperty().addListener(new ChangeListener<Animation.Status>() {
                @Override
                public void changed(ObservableValue<? extends Animation.Status> observableValue, Animation.Status status, Animation.Status status2) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });
            nodes.add(node);
        }

        System.gc();
        System.out.println("Heap use: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 + " kB");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

} 