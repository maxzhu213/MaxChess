import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Max on 2018-04-25.
 */
public class Board {

    private static final int SIZE = 8;
    private final int SQUARE_SIZE = 80;
    private final int X_POS = 10;
    private final int Y_POS = 10;

    private boolean whiteMove;

    private int selected;

    private ArrayList<Piece> pieces;

    public Board() {
        whiteMove = true;
        selected = -1;
        pieces = new ArrayList<>();

        for (int i = 0; i < 16; i++) {  //Adding pawns
            pieces.add(i, new Piece("Pawn", i % SIZE, i < SIZE ? 6 : 1, i < SIZE));
        }
        for (int i = 0; i < 16; i++) {  //Adding pieces
            if (i % SIZE == 0 || i % SIZE == 7) {
                pieces.add(i, new Piece("Rook", i % SIZE, i < SIZE ? 7 : 0, i < SIZE));
            } else if (i % SIZE == 1 || i % SIZE == 6) {
                pieces.add(i, new Piece("Knight", i % SIZE, i < SIZE ? 7 : 0, i < SIZE));
            } else if (i % SIZE == 2 || i % SIZE == 5) {
                pieces.add(i, new Piece("Bishop", i % SIZE, i < SIZE ? 7 : 0, i < SIZE));
            } else if (i % SIZE == 3) {
                pieces.add(i, new Piece("Queen", i % SIZE, i < SIZE ? 7 : 0, i < SIZE));
            } else if (i % SIZE == 4) {
                pieces.add(i, new Piece("King", i % SIZE, i < SIZE ? 7 : 0, i < SIZE));
            }
        }

    }

    public void draw(Graphics g) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                g.setColor((i + j) % 2 == 1 ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                g.fillRect(X_POS + i * SQUARE_SIZE, Y_POS + j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }

        if (selected > -1) {
            g.setColor(Color.GREEN);
            g.fillRect(X_POS + pieces.get(selected).x * SQUARE_SIZE,
                    Y_POS + pieces.get(selected).y * SQUARE_SIZE,
                    SQUARE_SIZE,
                    SQUARE_SIZE);

            for (int x = 0; x < SIZE; x++) {
                for (int y = 0; y < SIZE; y++) {
                    if (pieces.get(selected).legalMove(x, y, pieces, pieceLocations())) {
                        g.setColor(new Color(0, 255, 255, 50));
                        g.fillOval(X_POS + x * SQUARE_SIZE, Y_POS + y * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                    }
                }
            }
        }

        for (Piece piece : pieces) {
            piece.draw(g, X_POS + piece.x * SQUARE_SIZE, Y_POS + piece.y * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
        }
    }

    public void onClick(int x, int y) {
        int a[] = clickedSquare(x, y);

        Piece selPiece = selected > -1 ? pieces.get(selected) : new Piece();

        if (selected == -1) {
            for (int i = 0; i < pieces.size(); i++) {
                if (pieces.get(i).x == a[0] && pieces.get(i).y == a[1] && pieces.get(i).isWhite == whiteMove) {
                    selected = i;
                    return;
                }
            }
        } else if (a[0] > -1 && selPiece.legalMove(a[0], a[1], pieces, pieceLocations())) {

            if  (a[0] == selPiece.x && a[1] == selPiece.y) {
                selected = -1;
                return;
            }

            for (int i = 0; i < pieces.size(); i++) {
                if (pieces.get(i).x == a[0] && pieces.get(i).y == a[1]) {
                    pieces.get(i).x = SIZE;
                    pieces.get(i).y = pieces.get(i).isWhite ? 7 : 0;
                    pieces.get(i).type = "null";
                }
            }

            selPiece.x = a[0];
            selPiece.y = a[1];
            selPiece.moves++;

            if (selPiece.type.equals("Pawn") && selPiece.y == (selPiece.isWhite ? 0 : 7)) {
                pieces.remove(selPiece);
                pieces.add(new Piece("Queen", selPiece.x, selPiece.y, selPiece.isWhite));
                System.out.println("PROMOTION!");
            }

            selected = -1;
            whiteMove = !whiteMove;
        } else {
            selected = -1;
        }
    }

    private int[] clickedSquare(int x, int y) {
        int a[] = {-1, -1};
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (isIn(x, y, new Rectangle(X_POS + i * SQUARE_SIZE, Y_POS + j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE))) {
                    a[0] = i;
                    a[1] = j;
                    break;
                }
            }
        }
        return a;
    }

    public static int[][] pieceLocations(ArrayList<Piece> pieces_) {
        int a[][] = new int[SIZE][SIZE];

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                a[x][y] = 0;
            }
        }

        for (Piece piece: pieces_) {
            try {
                a[piece.x][piece.y] = piece.isWhite ? 1 : -1;
            } catch (ArrayIndexOutOfBoundsException e) {

            }
        }

        return a;
    }

    private int[][] pieceLocations() {
        return pieceLocations(this.pieces);
    }

    private boolean isIn(int x, int y, Rectangle r) {
        return  r.x <= x && x <= r.x + r.width &&
                r.y <= y && y <= r.y + r.height;
    }
}
