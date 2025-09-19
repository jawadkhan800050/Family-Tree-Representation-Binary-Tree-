import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.io.File;
import java.util.List;
import java.util.function.Supplier;

public class MainFX extends Application {

    private FamilyTree tree;
    private FamilyTreePane panel;
    private TextArea out;

    @Override
    public void start(Stage primaryStage) {

        // --- Large Tree (4 generations) ---
        tree = new FamilyTree("GreatGrandfather", "Male");

        tree.addChild("GreatGrandfather", "Grandfather1", "Male", true);
        tree.addChild("GreatGrandfather", "Grandfather2", "Male", false);

        tree.addChild("Grandfather1", "Father1", "Male", true);
        tree.addChild("Grandfather1", "Aunt1", "Female", false);
        tree.addChild("Grandfather2", "Uncle1", "Male", true);
        tree.addChild("Grandfather2", "Aunt2", "Female", false);

        tree.addChild("Father1", "Me", "Male", true);
        tree.addChild("Father1", "Sister", "Female", false);
        tree.addChild("Aunt1", "Cousin1", "Male", true);
        tree.addChild("Aunt1", "Cousin2", "Female", false);
        tree.addChild("Uncle1", "Cousin3", "Male", true);
        tree.addChild("Uncle1", "Cousin4", "Female", false);
        tree.addChild("Aunt2", "Cousin5", "Male", true);
        tree.addChild("Aunt2", "Cousin6", "Female", false);

        tree.addChild("Me", "Son", "Male", true);
        tree.addChild("Me", "Daughter", "Female", false);
        tree.addChild("Sister", "Nephew1", "Male", true);
        tree.addChild("Sister", "Niece1", "Female", false);
        tree.addChild("Cousin1", "Child1", "Male", true);
        tree.addChild("Cousin1", "Child2", "Female", false);
        tree.addChild("Cousin3", "Child3", "Male", true);
        tree.addChild("Cousin4", "Child4", "Female", false);

        panel = new FamilyTreePane(tree);
        panel.setPrefSize(1600, 900);

        // --- Controls ---
        TextField searchField = new TextField();
        searchField.setPrefWidth(100);

        Button searchBtn = new Button("Search & Highlight");
        Button clearHighlight = new Button("Clear Highlight");
        Button preorderBtn = new Button("Preorder");
        Button inorderBtn = new Button("Inorder");
        Button postorderBtn = new Button("Postorder");
        Button saveBtn = new Button("Save Default");
        Button loadBtn = new Button("Load Default");

        out = new TextArea();
        out.setPrefRowCount(6);
        out.setEditable(false);

        HBox controls = new HBox(8);
        controls.setPadding(new Insets(8));
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.getChildren().addAll(
                new Label("Search name:"), searchField, searchBtn, clearHighlight,
                preorderBtn, inorderBtn, postorderBtn,
                saveBtn, loadBtn
        );

        VBox root = new VBox(5);
        root.getChildren().addAll(controls, panel, out);

        // --- Button actions ---
        searchBtn.setOnAction(e -> {
            String name = searchField.getText().trim();
            if (!name.isEmpty()) {
                runBackground(() -> {
                    boolean found = tree.search(tree.root, name) != null;
                    Platform.runLater(() -> {
                        if (found) {
                            panel.setHighlight(name);
                            List<String> path = tree.getAncestryPath(name);
                            panel.highlightPathAnimated(path);
                            appendText("Found and highlighted: " + name);
                        } else {
                            appendText(name + " not found.");
                        }
                    });
                });
            }
        });

        clearHighlight.setOnAction(e -> panel.setHighlight(null));
        preorderBtn.setOnAction(e -> runTraversal(() -> tree.preorder(tree.root), "Preorder"));
        inorderBtn.setOnAction(e -> runTraversal(() -> tree.inorder(tree.root), "Inorder"));
        postorderBtn.setOnAction(e -> runTraversal(() -> tree.postorder(tree.root), "Postorder"));


        saveBtn.setOnAction(e -> {
            new Thread(() -> {
                File f = new File("familytree.ser");
                boolean success = FileHandler.saveTree(tree, f.getAbsolutePath());
                Platform.runLater(() -> appendText(success ? "Saved to " + f.getAbsolutePath() : "Save failed"));
            }).start();
        });

        loadBtn.setOnAction(e -> runBackground(() -> {
            File f = new File("familytree.ser");
            FamilyTree loaded = FileHandler.loadTree(f.getAbsolutePath());
            Platform.runLater(() -> {
                if (loaded != null) {
                    tree = loaded;
                    panel = new FamilyTreePane(tree);
                    root.getChildren().set(1, panel); // replace old panel
                    appendText("Loaded from " + f.getAbsolutePath());
                } else {
                    appendText("Load failed");
                }
            });
        }));


        Scene scene = new Scene(root, 1600, 1000);
        primaryStage.setTitle("Advanced Family Tree");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void appendText(String msg) { out.appendText(msg + "\n"); }

    private void runBackground(Runnable task) {
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void runTraversal(Supplier<String> traversalMethod, String type) {
        runBackground(() -> {
            String result = traversalMethod.get();
            Platform.runLater(() -> appendText(type + ": " + result));
        });
    }

    public static void main(String[] args) { launch(args); }
}
