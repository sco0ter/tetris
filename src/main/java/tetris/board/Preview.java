package tetris.board;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import tetris.tetromino.Tetromino;

/**
 * @author Christian Schudt
 */
public class Preview extends StackPane {


    public Preview(final Board board) {

        final ObservableList<Tetromino> tetrominos = board.getQueue();

        tetrominos.addListener(new ListChangeListener<Tetromino>() {
            @Override
            public void onChanged(Change<? extends Tetromino> change) {
                getChildren().clear();
                getChildren().add(tetrominos.get(0));
            }
        });
        setAlignment(Pos.CENTER);

        getChildren().addAll(tetrominos.get(0));
        setPrefSize(100, 100);

    }
}
