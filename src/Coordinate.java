
public class Coordinate {
    private int x, y, value;
    private boolean evaluated = false;

    public Coordinate(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
        evaluated = true;
    }

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int value() {
        return value;
    }

    public void setValue(int x) {
        value = x;
    }

    public String toString() {
        if (evaluated) {
            return "(" + x + ", " + y + "): " + value;
        } else {
            return "(" + x + ", " + y + ")";
        }
    }
}


