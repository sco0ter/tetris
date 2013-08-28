package tetris;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import tetris.board.MainBox;

public class Tetris extends Application {

    private AudioClip clip = new AudioClip(getClass().getResource("/soundtrack.mp3").toExternalForm());
    private MediaPlayer mediaPlayer1 = new MediaPlayer(new Media(getClass().getResource("/soundtrack.mp3").toExternalForm()));

    @Override
    public void start(Stage primaryStage) throws Exception {

        Group group = new Group();

        final MainBox mainBox = new MainBox(this);

        mediaPlayer1.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer1.play();

        //clip.setCycleCount(AudioClip.INDEFINITE);
        //clip.play();

        MenuBar bar = new MenuBar();

        Menu menu = new Menu("test");
        MenuItem item = new MenuItem("Item");
        item.setDisable(true);
        bar.getMenus().add(menu);
        menu.getItems().add(item);
//        item.setGraphic(new ImageView(new Image("")));
        bar.setUseSystemMenuBar(true);
        group.getChildren().add(bar);


        group.getChildren().add(mainBox);
        primaryStage.setTitle("Tetris");
        Scene scene = new Scene(group);
        scene.getStylesheets().add("styles.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}