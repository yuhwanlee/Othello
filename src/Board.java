import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Board extends JPanel {
    Game game;
    Piece[][] pieces;

    public Board(Game game) {
        this.game = game;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(5, 90, 0));
        g.setColor(Color.black);
        int scale = 100;
        for (int i = 0; i < 9; i++) {
            g.drawLine(i * scale, 0, i * scale, scale * 8);
        }
        for (int i = 0; i < 9; i++) {
            g.drawLine(0, i * scale, scale * 8, i * scale);
        }
        if (game.initialized()) {
            pieces = game.getPieces();
            for (int i = 0; i < pieces[0].length; i++) {
                for (int j = 0; j < pieces.length; j++) {
                    if (pieces[i][j] != null) {
                        g.setColor(pieces[i][j].getColor());
                        g.fillOval(i * 100 + 20, j * 100 + 20, 60, 60);
                    }
                }
            }
            if (game.getHighlightX() != -1) {
                g.setColor(new Color(game.getHighlightPiece().getColor().getRed(),
                        game.getHighlightPiece().getColor().getGreen(), game.getHighlightPiece().getColor().getBlue(),
                        150));
                g.fillOval(game.getHighlightX() * 100 + 20, game.getHighlightY() * 100 + 20, 60, 60);
            }
        }
    }
}


