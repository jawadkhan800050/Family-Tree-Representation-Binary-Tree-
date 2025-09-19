import javafx.animation.FillTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FamilyTreePane extends Pane {
    private FamilyTree tree;
    private String highlightName = null;
    private Map<person, Shape> nodeShapes = new HashMap<>();

    private double scaleValue = 1.0;
    private double translateX = 0;
    private double translateY = 0;
    private double lastX, lastY;

    private Group nodeGroup = new Group();

    public FamilyTreePane(FamilyTree tree) {
        this.tree = tree;
        setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #e6f7ff);");
        getChildren().add(nodeGroup);

        // Zoom
        this.addEventFilter(ScrollEvent.SCROLL, e -> {
            double delta = e.getDeltaY() < 0 ? 1 / 1.1 : 1.1;
            scaleValue *= delta;
            scaleValue = Math.max(0.2, Math.min(scaleValue, 3.0));
            nodeGroup.setScaleX(scaleValue);
            nodeGroup.setScaleY(scaleValue);
            e.consume();
        });

        // Pan
        this.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> { lastX = e.getX(); lastY = e.getY(); });
        this.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            double dx = e.getX() - lastX;
            double dy = e.getY() - lastY;
            translateX += dx / scaleValue;
            translateY += dy / scaleValue;
            nodeGroup.setTranslateX(translateX);
            nodeGroup.setTranslateY(translateY);
            lastX = e.getX();
            lastY = e.getY();
        });

        drawTreeAsync();
    }

    public void setHighlight(String name) {
        this.highlightName = name;
        drawTreeAsync();
    }

    private void drawTreeAsync() {
        Platform.runLater(() -> {
            nodeGroup.getChildren().clear();
            nodeShapes.clear();
            if (tree != null && tree.root != null) {
                double width = getWidth() > 0 ? getWidth() : 1600;
                drawNode(tree.root, width / 2, 50, 200);
            }
        });
    }

    private int getSubtreeWidth(person p) {
        if (p == null) return 0;
        if (p.leftchild == null && p.rightchild == null) return 1;
        return Math.max(getSubtreeWidth(p.leftchild), 1) + Math.max(getSubtreeWidth(p.rightchild), 1);
    }

    private void drawNode(person p, double x, double y, double spacing) {
        if (p == null) return;

        double yStep = 150;

        int leftWidth = getSubtreeWidth(p.leftchild);
        int rightWidth = getSubtreeWidth(p.rightchild);

        if (p.leftchild != null) {
            double childX = x - spacing * rightWidth / 2;
            double childY = y + yStep;
            Line line = new Line(x, y, childX, childY);
            line.setStroke(Color.BLACK);
            nodeGroup.getChildren().add(line);
            drawNode(p.leftchild, childX, childY, spacing);
        }

        if (p.rightchild != null) {
            double childX = x + spacing * leftWidth / 2;
            double childY = y + yStep;
            Line line = new Line(x, y, childX, childY);
            line.setStroke(Color.BLACK);
            nodeGroup.getChildren().add(line);
            drawNode(p.rightchild, childX, childY, spacing);
        }

        // Draw node
        Shape shape;
        if ("Male".equalsIgnoreCase(p.gender)) {
            shape = new Circle(x, y, 40, Color.LIGHTBLUE);
        } else {
            shape = new Rectangle(x - 45, y - 20, 90, 40);
            ((Rectangle) shape).setFill(Color.PINK);
        }
        shape.setStroke(Color.BLACK);
        if (highlightName != null && p.name.equalsIgnoreCase(highlightName)) {
            shape.setStroke(Color.RED);
            shape.setStrokeWidth(3);
        }

        nodeGroup.getChildren().add(shape);
        nodeShapes.put(p, shape);

        Text text = new Text(x - p.name.length() * 3, y + 5, p.name);
        nodeGroup.getChildren().add(text);
    }

    public void highlightPathAnimated(List<String> path) {
        if (path == null || path.isEmpty()) return;

        new Thread(() -> {
            try {
                for (String name : path) {
                    Shape shape = null;
                    for (person p : nodeShapes.keySet()) {
                        if (p.name.equalsIgnoreCase(name)) {
                            shape = nodeShapes.get(p);
                            break;
                        }
                    }

                    if (shape != null) {
                        Shape finalShape = shape;
                        Platform.runLater(() -> {
                            Color originalColor = finalShape instanceof Circle ?
                                    (Color) ((Circle) finalShape).getFill() :
                                    (Color) ((Rectangle) finalShape).getFill();

                            FillTransition ft = new FillTransition(Duration.millis(500), finalShape, originalColor, Color.ORANGE);
                            ft.setAutoReverse(true);
                            ft.setCycleCount(2);
                            ft.play();
                        });
                        Thread.sleep(300);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
