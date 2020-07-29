import java.awt.Color;

public class Piece {
    Color color;
    String colorName;

    public Piece(String str) {
        colorName = str;
        if (colorName.equals("white")) {
            this.color = Color.white;
        } else if (colorName.equals("black")) {
            this.color = Color.black;
        }
    }

    public void switchColor() {
        if (colorName.equals("white")) {
            color = Color.black;
            colorName = "black";
        } else if (colorName.equals("black")) {
            color = Color.white;
            colorName = "white";
        }
    }

    public Color getColor() {
        return color;
    }

    public String toString() {
        return colorName;
    }
}


