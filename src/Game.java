import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

public class Game {
    JFrame frame;
    Board panel;
    Piece[][] pieces;
    String turn = "black";
    int highlightX, highlightY = -1;
    boolean gameEnd = false;
    int AILevel = 2;
    boolean initialized = false;

    public Game() {
        frame = new JFrame("Othello");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new Board(this);
        int screenHeight = 800;
        int screenWidth = 800;
        frame.add(panel);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(false);
        panel.setPreferredSize(new Dimension(screenWidth, screenHeight));
        panel.setSize(screenWidth, screenHeight);
        frame.setSize(807, 836);
        frame.setVisible(true);
        addListeners();
        createPieces();
    }

    private void createPieces() {
        pieces = new Piece[8][8];
        pieces[3][3] = new Piece("white");
        pieces[4][4] = new Piece("white");
        pieces[3][4] = new Piece("black");
        pieces[4][3] = new Piece("black");
        initialized = true;
    }

    public boolean initialized() {
        return initialized;
    }

    public Piece[][] getPieces() {
        return pieces;
    }

    private void addListeners() {
        frame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
            }

            @Override
            public void mouseExited(MouseEvent arg0) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (turn.equals("black") || AILevel == 0) {
                    int xCoord = (e.getX() - 3) / 100;
                    int yCoord = (e.getY() - 32) / 100;
                    if (pieces[xCoord][yCoord] == null && validMove(xCoord, yCoord, turn, pieces) != null) {
                        pieces[xCoord][yCoord] = new Piece(turn);
                        switchPieces(validMove(xCoord, yCoord, turn, pieces));
                        turn = opposite(turn);
                        highlightX = -1;
                    }
                    panel.repaint();
                } else {
                    makeMove(turn);
                }
                if (noValidMoves(turn, pieces)) {
                    System.out.println(turn + " couldnt play");
                    turn = opposite(turn);
                    if (noValidMoves(turn, pieces)) {
                        System.out.println("no one can play");
                        gameEnd = true;
                    }
                }
                if (gameEnd) {
                    displayScores();
                }
            }

            @Override
            public void mouseReleased(MouseEvent arg0) {
            }
        });
        frame.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (turn.equals("black") || AILevel == 0) {
                    int xCoord = (e.getX() - 3) / 100;
                    int yCoord = (e.getY() - 32) / 100;
                    if (inBounds(xCoord) && inBounds(yCoord)) {
                        if (pieces[xCoord][yCoord] == null && validMove(xCoord, yCoord, turn, pieces) != null) {
                            highlightX = xCoord;
                            highlightY = yCoord;
                            panel.repaint();
                        } else {
                            highlightX = -1;
                            highlightY = -1;
                            panel.repaint();
                        }
                    }
                }
            }
        });
    }

    private void displayScores() {
        int whiteScore = 0;
        int blackScore = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j] != null) {
                    if (pieces[i][j].colorName.equals("white")) {
                        whiteScore++;
                    } else {
                        blackScore++;
                    }
                }
            }
        }
        System.out.println("Black score: " + blackScore);
        System.out.println("White score: " + whiteScore);
        if (blackScore > whiteScore) {
            System.out.println("Black wins!");
        } else if (whiteScore > blackScore) {
            System.out.println("White wins!");
        } else {
            System.out.println("It's a tie!");
        }
    }

    private ArrayList<Coordinate> possibleMoves(String color, Piece[][] board) {
        ArrayList<Coordinate> possibleMoves = new ArrayList<Coordinate>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == null && validMove(i, j, color, board) != null) {
                    possibleMoves.add(new Coordinate(i, j));
                }
            }
        }
        return possibleMoves;
    }

    private void makeMove(String color) {
        ArrayList<Coordinate> possibleMoves = possibleMoves(color, pieces);
        if (AILevel == 1) {
            Random random = new Random();
            Coordinate pos = possibleMoves.get(random.nextInt(possibleMoves.size()));
            pieces[pos.x()][pos.y()] = new Piece(color);
            switchPieces(validMove(pos.x(), pos.y(), color, pieces));
            turn = opposite(turn);
            System.out.println(pos);
            panel.repaint();
        } else if (AILevel == 2) {
            float bestValue = 0;
            Coordinate bestPos = null;
            Piece[][] newBoard;
            if (possibleMoves.size() == 1) {
                bestPos = possibleMoves.get(0);
            } else {
                for (Coordinate pos : possibleMoves) {
                    newBoard = copyArray(pieces);
                    float value = (validMove(pos.x(), pos.y(), color, newBoard).size());
                    newBoard[pos.x()][pos.y()] = new Piece(color);
                    switchPieces(validMove(pos.x(), pos.y(), color, newBoard));
                    Coordinate bestEnemy = chooseBest(newBoard, opposite(color),
                            possibleMoves(opposite(color), newBoard));
                    if (noValidMoves(opposite(color), newBoard)) {
                        System.out.println("\nFOUND PASS MOVE");
                        value *= 5;
                    } else {
                        value /= validMove(bestEnemy.x(), bestEnemy.y(), opposite(color), newBoard).size();
                    }
                    System.out.println(pos + ": " + value);
                    if (value > bestValue) {
                        bestValue = value;
                        bestPos = pos;
                    }
                }
            }
            System.out.println(bestPos);
            pieces[bestPos.x()][bestPos.y()] = new Piece(color);
            switchPieces(validMove(bestPos.x(), bestPos.y(), color, pieces));
            turn = opposite(turn);
            panel.repaint();
        }
    }

    private Piece[][] copyArray(Piece[][] board) {
        Piece[][] newArray = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    newArray[i][j] = new Piece(board[i][j].colorName);
                }
            }
        }
        return newArray;
    }

    private Coordinate chooseBest(Piece[][] board, String color, ArrayList<Coordinate> moves) {
        int best = 0;
        int current = 0;
        Coordinate bestPos = null;
        for (Coordinate pos : moves) {
            current = validMove(pos.x(), pos.y(), color, board).size();
            if (current > best) {
                best = current;
                bestPos = pos;
            }
        }
        return bestPos;
    }

    private boolean noValidMoves(String color, Piece[][] board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j] == null && validMove(i, j, color, board) != null) {
                    return false;
                }
            }
        }
        return true;
    }

    public Piece getHighlightPiece() {
        return new Piece(turn);
    }

    public int getHighlightX() {
        return highlightX;
    }

    public int getHighlightY() {
        return highlightY;
    }

    private ArrayList<Piece> validMove(int x, int y, String color, Piece[][] board) {
        ArrayList<Piece> allPieces = new ArrayList<Piece>();
        if (checkLeft(x, y, color, board) != null) {
            allPieces.addAll(checkLeft(x, y, color, board));
        }
        if (checkRight(x, y, color, board) != null) {
            allPieces.addAll(checkRight(x, y, color, board));
        }
        if (checkBottom(x, y, color, board) != null) {
            allPieces.addAll(checkBottom(x, y, color, board));
        }
        if (checkTop(x, y, color, board) != null) {
            allPieces.addAll(checkTop(x, y, color, board));
        }
        if (checkBottomRight(x, y, color, board) != null) {
            allPieces.addAll(checkBottomRight(x, y, color, board));
        }
        if (checkBottomLeft(x, y, color, board) != null) {
            allPieces.addAll(checkBottomLeft(x, y, color, board));
        }
        if (checkTopRight(x, y, color, board) != null) {
            allPieces.addAll(checkTopRight(x, y, color, board));
        }
        if (checkTopLeft(x, y, color, board) != null) {
            allPieces.addAll(checkTopLeft(x, y, color, board));
        }
        if (allPieces.size() != 0) {
            return allPieces;
        }
        return null;
    }

    private void switchPieces(ArrayList<Piece> piecesToSwitch) {
        // System.out.println(piecesToSwitch);
        for (Piece piece : piecesToSwitch) {
            piece.switchColor();
        }
    }

    private ArrayList<Piece> checkTopRight(int x, int y, String color, Piece[][] board) {
        boolean checking = true;
        boolean canPlace = false;
        ArrayList<Piece> piecesToSwitch = new ArrayList<Piece>();
        int i = 1;
        while (checking) {
            if (inBounds(x + i) && inBounds(y - i)) {
                Piece piece = board[x + i][y - i];
                if (piece != null && piece.toString() == opposite(color)) {
                    piecesToSwitch.add(piece);
                    i++;
                } else if (piece != null && piece.toString() == color) {
                    if (i == 1) {
                        checking = false;
                        canPlace = false;
                    } else if (i > 1) {
                        checking = false;
                        canPlace = true;
                    }
                } else {
                    checking = false;
                    canPlace = false;
                }
            } else {
                checking = false;
                canPlace = false;
            }
        }
        if (canPlace) {
            return piecesToSwitch;
        }
        return null;
    }

    private ArrayList<Piece> checkRight(int x, int y, String color, Piece[][] board) {
        boolean checking = true;
        boolean canPlace = false;
        ArrayList<Piece> piecesToSwitch = new ArrayList<Piece>();
        int i = 1;
        while (checking) {
            if (inBounds(x + i) && inBounds(y)) {
                Piece piece = board[x + i][y];
                if (piece != null && piece.toString() == opposite(color)) {
                    piecesToSwitch.add(piece);
                    i++;
                } else if (piece != null && piece.toString() == color) {
                    if (i == 1) {
                        checking = false;
                        canPlace = false;
                    } else if (i > 1) {
                        checking = false;
                        canPlace = true;
                    }
                } else if (piece == null) {
                    checking = false;
                    canPlace = false;
                }
            } else {
                checking = false;
                canPlace = false;
            }
        }
        if (canPlace) {
            return piecesToSwitch;
        }
        return null;
    }

    private ArrayList<Piece> checkLeft(int x, int y, String color, Piece[][] board) {
        boolean checking = true;
        boolean canPlace = false;
        ArrayList<Piece> piecesToSwitch = new ArrayList<Piece>();
        int i = 1;
        while (checking) {
            if (inBounds(x - i) && inBounds(y)) {
                Piece piece = board[x - i][y];
                if (piece != null && piece.toString() == opposite(color)) {
                    piecesToSwitch.add(piece);
                    i++;
                } else if (piece != null && piece.toString() == color) {
                    if (i == 1) {
                        checking = false;
                        canPlace = false;
                    } else if (i > 1) {
                        checking = false;
                        canPlace = true;
                    }
                } else if (piece == null) {
                    checking = false;
                    canPlace = false;
                }
            } else {
                checking = false;
                canPlace = false;
            }
        }
        if (canPlace) {
            return piecesToSwitch;
        }
        return null;
    }

    private ArrayList<Piece> checkBottom(int x, int y, String color, Piece[][] board) {
        boolean checking = true;
        boolean canPlace = false;
        ArrayList<Piece> piecesToSwitch = new ArrayList<Piece>();
        int i = 1;
        while (checking) {
            if (inBounds(x) && inBounds(y + i)) {
                Piece piece = board[x][y + i];
                if (piece != null && piece.toString() == opposite(color)) {
                    piecesToSwitch.add(piece);
                    i++;
                } else if (piece != null && piece.toString() == color) {
                    if (i == 1) {
                        checking = false;
                        canPlace = false;
                    } else if (i > 1) {
                        checking = false;
                        canPlace = true;
                    }
                } else if (piece == null) {
                    checking = false;
                    canPlace = false;
                }
            } else {
                checking = false;
                canPlace = false;
            }
        }
        if (canPlace) {
            return piecesToSwitch;
        }
        return null;
    }

    private ArrayList<Piece> checkTop(int x, int y, String color, Piece[][] board) {
        boolean checking = true;
        boolean canPlace = false;
        ArrayList<Piece> piecesToSwitch = new ArrayList<Piece>();
        int i = 1;
        while (checking) {
            if (inBounds(x) && inBounds(y - i)) {
                Piece piece = board[x][y - i];
                if (piece != null && piece.toString() == opposite(color)) {
                    piecesToSwitch.add(piece);
                    i++;
                } else if (piece != null && piece.toString() == color) {
                    if (i == 1) {
                        checking = false;
                        canPlace = false;
                    } else if (i > 1) {
                        checking = false;
                        canPlace = true;
                    }
                } else if (piece == null) {
                    checking = false;
                    canPlace = false;
                }
            } else {
                checking = false;
                canPlace = false;
            }
        }
        if (canPlace) {
            return piecesToSwitch;
        }
        return null;
    }

    private ArrayList<Piece> checkTopLeft(int x, int y, String color, Piece[][] board) {
        boolean checking = true;
        boolean canPlace = false;
        ArrayList<Piece> piecesToSwitch = new ArrayList<Piece>();
        int i = 1;
        while (checking) {
            if (inBounds(x - i) && inBounds(y - i)) {
                Piece piece = board[x - i][y - i];
                if (piece != null && piece.toString() == opposite(color)) {
                    piecesToSwitch.add(piece);
                    i++;
                } else if (piece != null && piece.toString() == color) {
                    if (i == 1) {
                        checking = false;
                        canPlace = false;
                    } else if (i > 1) {
                        checking = false;
                        canPlace = true;
                    }
                } else if (piece == null) {
                    checking = false;
                    canPlace = false;
                }
            } else {
                checking = false;
                canPlace = false;
            }
        }
        if (canPlace) {
            return piecesToSwitch;
        }
        return null;
    }

    private ArrayList<Piece> checkBottomRight(int x, int y, String color, Piece[][] board) {
        boolean checking = true;
        boolean canPlace = false;
        ArrayList<Piece> piecesToSwitch = new ArrayList<Piece>();
        int i = 1;
        while (checking) {
            if (inBounds(x + i) && inBounds(y + i)) {
                Piece piece = board[x + i][y + i];
                if (piece != null && piece.toString() == opposite(color)) {
                    piecesToSwitch.add(piece);
                    i++;
                } else if (piece != null && piece.toString() == color) {
                    if (i == 1) {
                        checking = false;
                        canPlace = false;
                    } else if (i > 1) {
                        checking = false;
                        canPlace = true;
                    }
                } else if (piece == null) {
                    checking = false;
                    canPlace = false;
                }
            } else {
                checking = false;
                canPlace = false;
            }
        }
        if (canPlace) {
            return piecesToSwitch;
        }
        return null;
    }

    private ArrayList<Piece> checkBottomLeft(int x, int y, String color, Piece[][] board) {
        boolean checking = true;
        boolean canPlace = false;
        ArrayList<Piece> piecesToSwitch = new ArrayList<Piece>();
        int i = 1;
        while (checking) {
            if (inBounds(x - i) && inBounds(y + i)) {
                Piece piece = board[x - i][y + i];
                if (piece != null && piece.toString() == opposite(color)) {
                    piecesToSwitch.add(piece);
                    i++;
                } else if (piece != null && piece.toString() == color) {
                    if (i == 1) {
                        checking = false;
                        canPlace = false;
                    } else if (i > 1) {
                        checking = false;
                        canPlace = true;
                    }
                } else if (piece == null) {
                    checking = false;
                    canPlace = false;
                }
            } else {
                checking = false;
                canPlace = false;
            }
        }
        if (canPlace) {
            return piecesToSwitch;
        }
        return null;
    }

    private String opposite(String color) {
        if (color.equals("white")) {
            return "black";
        } else {
            return "white";
        }
    }

    private boolean inBounds(int x) {
        return (x > -1 && x < 8);
    }

    private void printBoard(Piece[][] board) {
        for (int j = 0; j < 8; j++) {
            System.out.println();
            for (int i = 0; i < 8; i++) {
                if (board[i][j] == null) {
                    System.out.print("      ");
                } else {
                    System.out.print(board[i][j] + " ");
                }
            }
        }
        System.out.println("\n--------------------------------");
    }

    public static void main(String args[]) {
        Game game = new Game();

    }
}

