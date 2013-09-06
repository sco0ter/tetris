package tetris;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.concurrent.Callable;


/**
 * @author Christian Schudt
 */
final class InfoBox extends StackPane {
    public InfoBox(final GameController gameController) {

        VBox root = new VBox();

        root.setPadding(new Insets(20, 20, 20, 20));
        root.setSpacing(10);

        setId("infoBox");

        CheckBox checkBox = new CheckBox();
        checkBox.getStyleClass().add("mute");
        checkBox.setMinSize(64, 64);
        checkBox.setMaxSize(64, 64);
        checkBox.selectedProperty().set(true);
        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                gameController.getBoard().requestFocus();
            }
        });
        gameController.getSoundManager().muteProperty().bind(checkBox.selectedProperty());

        Slider sliderMusicVolume = new Slider();
        sliderMusicVolume.setMin(0);
        sliderMusicVolume.setMax(1);
        sliderMusicVolume.setValue(0.5);
        sliderMusicVolume.setFocusTraversable(false);
        sliderMusicVolume.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                if (!aBoolean2) {
                    gameController.getBoard().requestFocus();
                }
            }
        });
        gameController.getSoundManager().volumeProperty().bind(sliderMusicVolume.valueProperty());

        Slider sliderSoundVolume = new Slider();
        sliderSoundVolume.setMin(0);
        sliderSoundVolume.setMax(1);
        sliderSoundVolume.setValue(0.5);
        sliderSoundVolume.setFocusTraversable(false);
        gameController.getSoundManager().soundVolumeProperty().bind(sliderSoundVolume.valueProperty());
        sliderSoundVolume.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                if (!aBoolean2) {
                    gameController.getBoard().requestFocus();
                }
            }
        });

        final ImageView playImageView = new ImageView(new Image(getClass().getResourceAsStream("/tetris/play.png")));
        final ImageView pauseImageView = new ImageView(new Image(getClass().getResourceAsStream("/tetris/pause.png")));

        Button btnStart = new Button("New Game");
        btnStart.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/tetris/rotate-left.png"))));
        btnStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                gameController.start();
            }
        });

        btnStart.setMaxWidth(Double.MAX_VALUE);
        btnStart.setAlignment(Pos.CENTER_LEFT);
        Button btnPause = new Button("Pause");
        btnPause.graphicProperty().bind(new ObjectBinding<Node>() {
            {
                super.bind(gameController.pausedProperty());
            }

            @Override
            protected Node computeValue() {
                if (gameController.pausedProperty().get()) {
                    return playImageView;
                } else {
                    return pauseImageView;
                }
            }
        });

        btnPause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (gameController.pausedProperty().get()) {
                    gameController.pausedProperty().set(false);
                } else {
                    gameController.pausedProperty().set(true);

                }
            }
        });
        btnPause.setMaxWidth(Double.MAX_VALUE);
        btnPause.setAlignment(Pos.CENTER_LEFT);
        Preview preview = new Preview(gameController);


        root.getChildren().add(checkBox);
        root.getChildren().add(sliderMusicVolume);
        root.getChildren().add(sliderSoundVolume);

        Label lblPoints = new Label();
        lblPoints.getStyleClass().add("points");
        lblPoints.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return String.valueOf(gameController.getPointManager().pointsProperty().get());
            }
        }, gameController.getPointManager().pointsProperty()));
        lblPoints.setAlignment(Pos.CENTER_RIGHT);
        lblPoints.setMaxWidth(Double.MAX_VALUE);
        lblPoints.setEffect(new Reflection());

        root.getChildren().add(preview);
        root.getChildren().add(btnStart);
        root.getChildren().add(btnPause);

        Label lblInfo = new Label("Use arrow keys for movement\nand rotating and space for\ndropping the piece.");

        root.getChildren().add(lblInfo);

        root.getChildren().addAll(lblPoints);


        getChildren().add(root);

    }
}
