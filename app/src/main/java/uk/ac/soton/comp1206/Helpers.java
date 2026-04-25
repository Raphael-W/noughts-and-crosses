package uk.ac.soton.comp1206;

import javafx.geometry.Bounds;
import javafx.scene.control.Button;

public class Helpers {
    private Helpers() {}

    static public double calculateButtonSize(double width, double height, int padding) {
        return (Math.min(width, height - 70) - (2 * padding)) / 3;
    }

    static public String calculateBorderStyle(int x, int y) {
        String top  = y > 0 ? "line-color" : "transparent";
        String right = x < 2 ? "line-color" : "transparent";
        String bottom = y < 2 ? "line-color" : "transparent";
        String left = x > 0 ? "line-color" : "transparent";

        return "-fx-border-color: " + top + " " + right + " " + bottom + " " + left + ";";
    }

    static public int[] compareCells(Character[][] grid, int x1, int y1, int x2, int y2, int x3, int y3) {
        Character a = grid[x1][y1];
        Character b = grid[x2][y2];
        Character c = grid[x3][y3];

        if (a != null && b != null && c != null) {
            if (a.equals(b) && b.equals(c)) {
                return new int[]{x1,y1, x2,y2, x3,y3};
            }
        }
        return null;
    }

    static public Character roundToSymbol(int turn) {
        return (turn % 2 == 0 ? 'x' : 'o');
    }

    static public double getGlobalX(Button button) {
        Bounds toBounds = button.localToScene(button.getBoundsInLocal());
        return toBounds.getCenterX();
    }

    static public double getGlobalY(Button button) {
        Bounds toBounds = button.localToScene(button.getBoundsInLocal());
        return toBounds.getCenterY();
    }

    static public double[] extendLine(double x1, double y1, double x2, double y2, int extension) {
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
