package tetris.board;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import tetris.Tetris;


/**
 * @author Christian Schudt
 */
public class InfoBox extends VBox {
    public InfoBox(final Tetris tetris, final Board board) {

        setPadding(new Insets(20, 20, 20, 20));
        setSpacing(10);
        Label label = new Label("Sound");
        label.getStyleClass().add("header");

        getChildren().add(label);
        getChildren().add(new Separator());

        CheckBox checkBox = new CheckBox("Stumm schalten");
        checkBox.selectedProperty().set(true);
        //tetris.getMediaPlayer().muteProperty().bind(checkBox.selectedProperty());

        Label lblVolume = new Label("Hintergrundmusik:");

        getChildren().add(lblVolume);

        Slider sliderVolume = new Slider();
        sliderVolume.setMin(0);
        sliderVolume.setMax(1);
        sliderVolume.setValue(0.2);
        sliderVolume.setTooltip(new Tooltip("Lautstärke"));
        //tetris.getMediaPlayer().volumeProperty().bind(sliderVolume.valueProperty());

        Slider sliderBalance = new Slider();
        sliderBalance.setMin(-1);
        sliderBalance.setMax(1);
        sliderBalance.setValue(0);
        sliderBalance.setTooltip(new Tooltip("Balance"));
        //tetris.getMediaPlayer().balanceProperty().bind(sliderBalance.valueProperty());


        Button btnPause = new Button("Pause");
        btnPause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                board.pause();
            }
        });

        Button btnResume = new Button("Resume");
        btnResume.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                board.resume();
            }
        });

        Preview preview = new Preview(board);


        getChildren().add(checkBox);
        getChildren().add(sliderVolume);
        getChildren().add(sliderBalance);

        getChildren().add(preview);

        getChildren().add(btnPause);
        getChildren().addAll(btnResume);

    }
}
