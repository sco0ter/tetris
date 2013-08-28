import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MemoryLeakTest extends Application {

    public static void main(String[] args) throws Exception {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox();

        final TreeItem<String> rootItem = new TreeItem<String>();
        final TreeView<String> treeView = new TreeView<String>(rootItem);
        treeView.setShowRoot(false);



        Button button = new Button("clear + add");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                for (int j = 0; j < 1; j++) {
                    treeView.getRoot().getChildren().clear();
                    for (int i = 0; i < 500; i++) {
                        TreeItem<String> item = new TreeItem<String>();
                        item.setValue(Integer.toString(i));
                        treeView.getRoot().getChildren().add(item);
                    }
                }



                System.out.println(treeView.getFocusModel().getFocusedIndex());

                System.gc();
                long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
                long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
                System.out.println("Heap use: " + (totalMemory - freeMemory) + " / " + totalMemory + " kB");
            }
        });
        TextField textField = new TextField();

        Button btnFocus = new Button("Fokus");
        btnFocus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                treeView.getSelectionModel().selectNext();
                System.out.println(treeView.getFocusModel().getFocusedIndex());

            }
        });

        root.getChildren().add(button);
        root.getChildren().add(btnFocus);

        root.getChildren().add(treeView);

        Stage stage2 = new Stage();
        //stage.setScene(new Scene(treeView));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
} 