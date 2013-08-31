package tetris.board;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Shadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tetris.GameController;
import tetris.tetromino.Tetromino;

/**
 * @author Christian Schudt
 */
public class Preview extends VBox {


    public Preview(GameController gameController) {

        final ObservableList<Tetromino> tetrominos = gameController.getBoard().getQueue();

        tetrominos.addListener(new ListChangeListener<Tetromino>() {
            @Override
            public void onChanged(Change<? extends Tetromino> change) {
                getChildren().clear();
                Group group = new Group();
                DropShadow dropShadow = new DropShadow();
                dropShadow.setColor(Color.DARKGREY);
                //dropShadow.setOffsetX(20);
                dropShadow.setRadius(30);
                //dropShadow.setSpread(0.01);
                group.setEffect(dropShadow);

                group.getChildren().addAll(tetrominos.get(0));
                getChildren().add(group);
            }
        });

        setMinHeight(Board.SQUARE * 9);
        setPrefHeight(Board.SQUARE*9);

        setAlignment(Pos.CENTER);

        if (!tetrominos.isEmpty()) {
            getChildren().addAll(tetrominos.get(0));
        }
        setPrefSize(100, 100);

    }
}
