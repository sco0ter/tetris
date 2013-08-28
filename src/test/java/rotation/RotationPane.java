package rotation;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * @author Christian Schudt
 */
public class RotationPane extends StackPane {

    public RotationPane() {

        sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observableValue, Scene scene, Scene scene1) {
                getScene().setCamera(new PerspectiveCamera());
            }
        });

        selectedIndex.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number1) {
                rotate(number1.intValue());
            }
        });

        getChildren().addListener(new ListChangeListener<Node>() {

            @Override
            public void onChanged(Change<? extends Node> change) {
                while (change.next()) {
                    if (change.wasAdded()) {
                        for (int i = 0; i < change.getAddedSize(); i++) {
                            Node node = change.getAddedSubList().get(i);
                            if (currentChild == null) {
                                currentChild = node;
                            } else {
                                node.setVisible(false);
                                node.setRotationAxis(Rotate.Y_AXIS);
                                node.setRotate(90);
                            }
                        }
                    }
                }
            }
        });
    }

    private Node currentChild;

    private IntegerProperty selectedIndex = new SimpleIntegerProperty();

    public IntegerProperty selectedIndexProperty() {
        return selectedIndex;
    }

    private void rotate(final int index) {
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), currentChild);
        rotateTransition.setFromAngle(0);
        rotateTransition.setToAngle(-90);
        rotateTransition.setInterpolator(Interpolator.EASE_IN);
        rotateTransition.setAxis(Rotate.Y_AXIS);
        rotateTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                currentChild.setVisible(false);
                currentChild = getChildren().get(index);
                currentChild.setVisible(true);
            }
        });
        RotateTransition rotateTransition2 = new RotateTransition(Duration.seconds(1), getChildren().get(index));
        rotateTransition2.setInterpolator(Interpolator.EASE_OUT);
        rotateTransition2.setFromAngle(90);
        rotateTransition2.setToAngle(0);
        rotateTransition2.setAxis(Rotate.Y_AXIS);
        rotateTransition.play();
        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().addAll(rotateTransition, rotateTransition2);
        sequentialTransition.play();
    }

}
