package uk.ac.soton.comp1206;

import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class Helpers {
    private Helpers() {}

    static public double getGlobalX(Button button, Pane parent) {
        Bounds sceneBounds = button.localToScene(button.getBoundsInLocal());
        return parent.sceneToLocal(sceneBounds).getCenterX();
    }

    static public double getGlobalY(Button button, Pane parent) {
        Bounds sceneBounds = button.localToScene(button.getBoundsInLocal());
        return parent.sceneToLocal(sceneBounds).getCenterY();
    }

    static public double[] extendLine(double x1, double y1, double x2, double y2, double extension) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);

        dx /= length;
        dy /= length;

        return new double[]{
                x1 - (dx * extension),
                y1 - (dy * extension),
                x2 + (dx * extension),
                y2 + (dy * extension)};
    }
}
