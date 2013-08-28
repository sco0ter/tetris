import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.effect.ReflectionBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author Christian Schudt
 */
public class PersectiveTransformTest extends Application {

    public static void main(String[] args) throws Exception {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        HBox g = new HBox();

        Button button = new Button("Ein kleiner Testbutton");
        g.getChildren().add(button);

        bindNode(button, angle);

        Button btnStart = new Button("Start");
        btnStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Timeline timeline = new Timeline();
                timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), new KeyValue(angle, -45)));
                timeline.play();
            }
        });
        g.getChildren().add(btnStart);
        Scene scene = new Scene(g);
        stage.setScene(scene);
        stage.show();
    }

    private DoubleProperty angle = new SimpleDoubleProperty(90);

    private void bindNode(final Node node, final DoubleProperty angle) {

        final PerspectiveTransform transform = new PerspectiveTransform();
        transform.ulxProperty().bind(new DoubleBinding() {
            {
                super.bind(angle, node.layoutBoundsProperty());
            }

            @Override
            protected double computeValue() {
                double radius = node.getLayoutBounds().getWidth() / 2;
                //return radius - Math.sin(Math.toRadians(angle.get()) * radius - 1);
                return -2;
            }
        });
        transform.ulyProperty().bind(new DoubleBinding() {
            {
                super.bind(angle, node.layoutBoundsProperty());
            }

            @Override
            protected double computeValue() {
                //return -Math.cos(Math.toRadians(angle.get())) * node.getLayoutBounds().getWidth() / 20;
                return -2;
            }
        });
        transform.urxProperty().bind(new DoubleBinding() {
            {
                super.bind(angle, node.layoutBoundsProperty());
            }

            @Override
            protected double computeValue() {
                double radius = node.getLayoutBounds().getWidth() / 2;
                return radius + Math.sin(Math.toRadians(angle.get())) * radius;
            }
        });
        transform.uryProperty().bind(new DoubleBinding() {
            {
                super.bind(angle, node.layoutBoundsProperty());
            }
            @Override
            protected double computeValue() {
                   return -Math.cos(Math.toRadians(angle.get())) * node.getLayoutBounds().getWidth() / -10 -2;
            }
        });
        transform.lrxProperty().bind(transform.urxProperty());
        transform.lryProperty().bind(new DoubleBinding() {
            {
                super.bind(angle, transform.ulyProperty(), node.layoutBoundsProperty());
            }

            @Override
            protected double computeValue() {
                return  node.layoutBoundsProperty().get().getHeight()+2;
            }
        });
        transform.llxProperty().bind(transform.ulxProperty());
        transform.llyProperty().bind(new DoubleBinding() {
            {
                super.bind(angle, transform.uryProperty(), node.layoutBoundsProperty());
            }

            @Override
            protected double computeValue() {
                //return transform.uryProperty().get() + node.layoutBoundsProperty().get().getHeight();
                return node.layoutBoundsProperty().get().getHeight()+2;
            }
        });
        node.setEffect(transform);
    }

    public static class PerspectiveImage extends Parent {
        private static final double REFLECTION_SIZE = 0.25;
        private static final double WIDTH = 200;
        private static final double HEIGHT = WIDTH + (WIDTH * REFLECTION_SIZE);
        private static final double RADIUS_H = WIDTH / 2;
        private static final double BACK = WIDTH / 10;
        private PerspectiveTransform transform = new PerspectiveTransform();
        /**
         * Angle Property
         */
        private final DoubleProperty angle = new SimpleDoubleProperty(45) {
            @Override
            protected void invalidated() {
                // when angle changes calculate new transform
                double lx = (RADIUS_H - Math.sin(Math.toRadians(angle.get())) * RADIUS_H - 1);
                double rx = (RADIUS_H + Math.sin(Math.toRadians(angle.get())) * RADIUS_H + 1);
                double uly = (-Math.cos(Math.toRadians(angle.get())) * BACK);
                double ury = -uly;
                transform.setUlx(lx);
                transform.setUly(uly);
                transform.setUrx(rx);
                transform.setUry(ury);
                transform.setLrx(rx);
                transform.setLry(HEIGHT + uly);
                transform.setLlx(lx);
                transform.setLly(HEIGHT + ury);
            }
        };

        public final double getAngle() {
            return angle.getValue();
        }

        public final void setAngle(double value) {
            angle.setValue(value);
        }

        public final DoubleProperty angleModel() {
            return angle;
        }

        public PerspectiveImage(Image image) {
            ImageView imageView = new ImageView(image);
            imageView.setEffect(ReflectionBuilder.create().fraction(REFLECTION_SIZE).build());
            setEffect(transform);
            getChildren().addAll(imageView);
        }
    }
}
