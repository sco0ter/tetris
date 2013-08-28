package tetris.board;

import javafx.scene.layout.HBox;
import tetris.Tetris;

/**
 * @author Christian Schudt
 */
public class MainBox extends HBox {
    public MainBox(final Tetris tetris) {

        Board board = new Board();
        getChildren().add(board);



        getChildren().add(new InfoBox(tetris, board));
    }
}
