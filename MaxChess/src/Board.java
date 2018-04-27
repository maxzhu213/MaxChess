import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Max on 2018-04-25.
 */
class Board {

    static final int SIZE = 8;
    private final int SQUARE_SIZE = 80;
    private final int X_POS = 10;
    private final int Y_POS = 10;

    private boolean whiteMove;

    private int selected;

    private ArrayList<Piece> pieces;

    Board() {
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

    void draw(Graphics g) {
        boolean checkmated = false;
        boolean stalemated = false;

        for (Piece piece: pieces) {
            if (piece.isWhite == whiteMove) {
                if (piece.inCheckMate(pieces)) {
                    checkmated = true;
                    break;
                } else if (piece.inStaleMate(pieces)) {
                    stalemated = true;
                    break;
                }
            }
        }

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                g.setColor((i + j) % 2 == 1 ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                g.fillRect(X_POS + i * SQUARE_SIZE, Y_POS + j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }

        if (checkmated) {
            int kingX = -1, kingY = -1;
            for (Piece piece: pieces) {
                if (piece.type.equals("King") && piece.isWhite == whiteMove) {
                    kingX = piece.x;
                    kingY = piece.y;
                }
            }
            g.setColor(Color.RED);
            g.fillRect(X_POS + kingX * SQUARE_SIZE,
                    Y_POS + kingY * SQUARE_SIZE,
                    SQUARE_SIZE,
                    SQUARE_SIZE);
        } else if (stalemated) {
            int kingX1 = -1, kingY1 = -1, kingX2 = -1, kingY2 = -1;
            for (Piece piece: pieces) {
                if (piece.type.equals("King")) {
                    if (piece.isWhite == whiteMove) {
                        kingX1 = piece.x;
                        kingY1 = piece.y;
                    } else {
                        kingX2 = piece.x;
                        kingY2 = piece.y;
                    }
                }
            }

            g.setColor(Color.YELLOW);
            g.fillRect(X_POS + kingX1 * SQUARE_SIZE,
                    Y_POS + kingY1 * SQUARE_SIZE,
                    SQUARE_SIZE,
                    SQUARE_SIZE);
            g.fillRect(X_POS + kingX2 * SQUARE_SIZE,
                    Y_POS + kingY2 * SQUARE_SIZE,
                    SQUARE_SIZE,
                    SQUARE_SIZE);
        }

        if (selected > -1) {
            g.setColor(Color.GREEN);
            g.fillRect(X_POS + pieces.get(selected).x * SQUARE_SIZE,
                    Y_POS + pieces.get(selected).y * SQUARE_SIZE,
                    SQUARE_SIZE,
                    SQUARE_SIZE);

            for (int x = 0; x < SIZE; x++) {
                for (int y = 0; y < SIZE; y++) {
                    if (pieces.get(selected).legalMove(x, y, pieces)) {
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

    void onClick(int x, int y) {
        int a[] = clickedSquare(x, y);

        Piece selPiece = selected > -1 ? pieces.get(selected) : new Piece();

        if (selected == -1) {
            for (int i = 0; i < pieces.size(); i++) {
                if (pieces.get(i).x == a[0] && pieces.get(i).y == a[1] && pieces.get(i).isWhite == whiteMove) {
                    selected = i;
                    return;
                }
            }
        } else if (a[0] > -1 && selPiece.legalMove(a[0], a[1], pieces)) {

            if  (a[0] == selPiece.x && a[1] == selPiece.y) {
                selected = -1;
                return;
            }

            pieces.stream().filter(piece -> piece.x == a[0] && piece.y == a[1]).forEach(piece -> {
                piece.x = SIZE;
                piece.y = piece.isWhite ? 7 : 0;
                piece.type = "null";
            });

            selPiece.x = a[0];
            selPiece.y = a[1];
            selPiece.moves++;

            if (selPiece.type.equals("Pawn") && selPiece.y == (selPiece.isWhite ? 0 : 7)) {
                pieces.remove(selPiece);
                pieces.add(new Piece("Queen", selPiece.x, selPiece.y, selPiece.isWhite));
            }

            if (selPiece.type.equals("King")) {
                Piece rook = new Piece();
                if (selPiece.moves == 1 && selPiece.x == 6) {
                    for (Piece piece: pieces) {
                        if (piece.x == 7 && piece.type.equals("Rook") && piece.isWhite == whiteMove) {
                            rook = piece;
                            break;
                        }
                    }

                    rook.x = 5;
                    rook.moves++;
                } else if (selPiece.moves == 1 && selPiece.x == 2) {
                    for (Piece piece: pieces) {
                        if (piece.x == 0 && piece.type.equals("Rook") && piece.isWhite == whiteMove) {
                            rook = piece;
                            break;
                        }
                    }

                    rook.x = 3;
                    rook.moves++;
                }
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

    static int[][] pieceLocations(ArrayList<Piece> pieces_) {
        int a[][] = new int[SIZE][SIZE];

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                a[x][y] = 0;
            }
        }

        pieces_.stream().filter(piece -> !piece.type.equals("null")).forEach(piece -> {
            try {
                a[piece.x][piece.y] = piece.isWhite ? 1 : -1;
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }
        });

        return a;
    }

    private boolean isIn(int x, int y, Rectangle r) {
        return  r.x <= x && x <= r.x + r.width &&
                r.y <= y && y <= r.y + r.height;
    }
}
