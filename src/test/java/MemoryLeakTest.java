import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class MemoryLeakTest extends Application {

    private List<Node> nodes = new ArrayList<Node>();

    private ObjectProperty<Locale> locale = new SimpleObjectProperty<Locale>(Locale.ENGLISH);

    public static void main(String[] args) throws Exception {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox();

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.gc();
                System.out.println("Heap use: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 + " kB");
            }
        }, 0, 1, TimeUnit.SECONDS);

        for (int i = 0; i < 100000; i++) {
            final Label label = new Label();
            ChangeListener<Locale> changeListener = new ChangeListener<Locale>() {
                @Override
                public void changed(ObservableValue<? extends Locale> observableValue, Locale oldLocale, Locale newLocale) {
                    label.setText(newLocale.toString());
                }
            };
            // Keep a hard reference to the changeListener.
            label.setUserData(changeListener);
            // Keep a reference to the node.
            nodes.add(label);
            locale.addListener(new WeakChangeListener<Locale>(changeListener));
        }
        // Clear all references. At this point all WeakReferences can be freed. But it won't be done until the locale changes.
        nodes.clear();

        Button button = new Button("Switch locale");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (locale.get().equals(Locale.ENGLISH)) {
                    locale.set(Locale.GERMAN);
                } else {
                    locale.set(Locale.ENGLISH);
                }
            }
        });
        root.getChildren().add(button);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
} 