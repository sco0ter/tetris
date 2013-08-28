package tetris;

import javafx.scene.media.AudioClip;

/**
 * @author Christian Schudt
 */
public enum Sound {

    ROTATE_LEFT("cartoon019.mp3"),
    ROTATE_RIGHT("cartoon130.mp3"),
    SINGLE("cartoon015.mp3"),
    DOUBLE("cartoon034.mp3"),
    TRIPLE("cartoon092.mp3"),
    TETRIS("cartoon040.mp3"),
    DROPPED("cartoon035.mp3"),
    INVALID_MOVE("cartoon155.mp3"),
    MOVE_LEFT("cartoon136.mp3"),
    MOVE_RIGHT("cartoon136.mp3");

    private Sound(String name) {
        audioClip = new AudioClip(getClass().getResource("/" + name).toExternalForm());

    }

    private AudioClip audioClip;

    public AudioClip getAudioClip() {
        return audioClip;
    }

}
