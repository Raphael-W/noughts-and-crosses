package noughtsandcrosses;

public class Coord {
    public int x;
    public int y;

    // Used to represent a position on the grid, and removes need to pass in an x and y as parameters (or int[])
    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Used to debug
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
