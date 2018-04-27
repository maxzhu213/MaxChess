import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by Max on 2018-04-25.
 */

public class Piece {

    boolean isWhite;

    int x;
    int y;
    int moves;

    String type;

    private BufferedImage img;

    Piece() {
        type = "null";
        x = -1;
        y = -1;
        isWhite = true;
        moves = 0;
    }

    Piece(String type, int x, int y, boolean isWhite) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.isWhite = isWhite;

        String path = System.getProperty("user.dir") + "\\src\\res\\" + (type.equals("null") ? "" : (isWhite ? "white" : "black")) + type + ".png";

        try {
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Error: " + type + " image not found");
            System.out.println("Looked in: " + path);
        }
    }

    private Piece(Piece piece) {
        this.type = piece.type;
        this.x = piece.x;
        this.y = piece.y;
        this.isWhite = piece.isWhite;
        this.img = piece.img;
    }

    private boolean canMove(int x_, int y_, ArrayList<Piece> pieces) {
        int b[][] = Board.pieceLocations(pieces);

        // No piece can ever go where a friendly piece is, or stay still
        if (b[x_][y_] == (isWhite ? 1 : -1) || (x == x_ && y == y_)) {
            return false;
        }

        switch (type) {
            case "Pawn":
                if (y_ == y + (isWhite ? -2 : 2)) { // On the first move, pawns can go forward 2 squares
                    return b[x][y_] == 0 && b[x][y + (isWhite ? -1 : 1)] == 0 && x == x_ && (y == 1 || y == 6);
                } else if (y_ == y + (isWhite ? -1 : 1)) {  // Else, it can only go forward 1 square
                    if (x == x_) {
                        return b[x][y_] == 0;   // Must move to empty space, if going straight forward
                    } else {    // Must be capturing enemy piece, if moving diagonally
                        return (x + 1 == x_ || x - 1 == x_) && b[x_][y_] == (isWhite ? -1 : 1);
                    }
                } else return false;
            case "Knight":
                return (Math.abs(x_ - x) == 2 && Math.abs(y_ - y) == 1) || // Knights go in an L shape
                        (Math.abs(x_ - x) == 1 && Math.abs(y_ - y) == 2);
            case "Bishop": {
                if (Math.abs(x_ - x) != Math.abs(y_ - y)) { // Bishops move diagonally
                    return false;
                }

                int dist = Math.abs(x_ - x);
                int xDir = (int) Math.signum(x_ - x);
                int yDir = (int) Math.signum(y_ - y);

                // Checking for pieces in between the Bishop and its target square
                for (int i = 1; i < dist; i++) {
                    if (b[x + i * xDir][y + i * yDir] != 0) {
                        return false;
                    }
                }

                return true;
            }
            case "Rook": {
                if (x != x_ && y != y_) {   // Rooks move horizontally or vertically
                    return false;
                }

                int dist = Math.abs(x_ - x + y_ - y);
                int xDir = (int) Math.signum(x_ - x);
                int yDir = (int) Math.signum(y_ - y);

                // Checking for pieces in between the Rook and its target square
                for (int i = 1; i < dist; i++) {
                    if ((xDir != 0 && b[x + i * xDir][y] != 0) ||
                            (yDir != 0 && b[x][y + i * yDir] != 0)) {
                        return false;
                    }
                }

                return true;
            }
            case "Queen": {
                if (Math.abs(x_ - x) != Math.abs(y_ - y) && (x != x_ && y != y_)) { // Queens move like a Rook or Bishop
                    return false;
                }

                int dist = Math.max(Math.abs(x_ - x), Math.abs(y_ - y));
                int xDir = (int) Math.signum(x_ - x);
                int yDir = (int) Math.signum(y_ - y);

                // Checking for pieces in between the Queen and its target square
                if (xDir * yDir != 0) {
                    for (int i = 1; i < dist; i++) {
                        if (b[x + i * xDir][y + i * yDir] != 0) {
                            return false;
                        }
                    }
                } else {
                    for (int i = 1; i < dist; i++) {
                        if ((xDir != 0 && b[x + i * xDir][y] != 0) ||
                                (yDir != 0 && b[x][y + i * yDir] != 0)) {
                            return false;
                        }
                    }
                }

                return true;
            }
            case "King":
                if (Math.abs(x_ - x) < 2 && Math.abs(y_ - y) < 2) {
                    return true;
                }
                return canCastle(pieces, x_, y_);
        }
        return false;
    }

    boolean legalMove(int x_, int y_, ArrayList<Piece> pieces) {
        ArrayList<Piece> pieces_ = pieces.stream().map(Piece::new).collect(Collectors.toCollection(ArrayList::new));

        for (Piece piece : pieces_) {
            if (piece.x == x_ && piece.y == y_) {
                piece.type = "null";
                break;
            }
        }

        for (Piece piece : pieces_) {
            if (this.x == piece.x && this.y == piece.y) {
                piece.moves++;
                piece.x = x_;
                piece.y = y_;
                break;
            }
        }

        return canMove(x_, y_, pieces) && !inCheck(pieces_);
    }

    boolean inCheckMate(ArrayList<Piece> pieces) {
        return inCheck(pieces) && inMate(pieces);
    }

    boolean inStaleMate(ArrayList<Piece> pieces) {
        return !inCheck(pieces) && inMate(pieces);
    }

    private boolean inMate(ArrayList<Piece> pieces) {
        for (Piece piece : pieces) {
            for (int x = 0; x < Board.SIZE; x++) {
                for (int y = 0; y < Board.SIZE; y++) {
                    if (piece.isWhite == this.isWhite && piece.legalMove(x, y, pieces)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean inCheck(ArrayList<Piece> pieces) {
        int kingX = -1;
        int kingY = -1;

        for (Piece piece : pieces) {
            if (this.isWhite == piece.isWhite && piece.type.equals("King")) {
                kingX = piece.x;
                kingY = piece.y;
                break;
            }
        }

        for (Piece piece : pieces) {
            if (this.isWhite != piece.isWhite && piece.canMove(kingX, kingY, pieces)) {
                return true;
            }
        }

        return false;
    }

    private boolean canCastle(ArrayList<Piece> pieces, int x_, int y_) {
        if (moves > 0 || Math.abs(x_ - x) != 2 || y_ != y || inCheck(pieces)) return false;

        int b[][] = Board.pieceLocations(pieces);

        if (b[x + (int) Math.signum(x_ - x)][y] != 0 || b[x_][y] != 0) return false;

        Piece rook = new Piece();
        ArrayList<Piece> pieces_ = pieces.stream().map(Piece::new).collect(Collectors.toCollection(ArrayList::new));

        for (Piece piece : pieces) {
            if (piece.x == Math.signum(x_ - x + 2) * 7) {
                rook = piece;
                break;
            }
        }

        if (rook.moves > 0 || !rook.type.equals("Rook")) return false;

        for (Piece piece : pieces_) {
            if (this.x == piece.x && this.y == piece.y) {
                piece.moves++;
                piece.x += Math.signum(x_ - x);
                break;
            }
        }

        if (inCheck(pieces_)) return false;

        return true;
    }

    void draw(Graphics g, int x, int y, int w, int h) {
        img = resize(img, h, w);
        try {
            g.drawImage(img, x, y, new Color(0, 0, 0, 0), null);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
}
