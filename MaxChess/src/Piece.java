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

    public boolean isWhite;

    public char letter;

    public int x;
    public int y;
    public int moves;

    public String type;

    BufferedImage img;

    public Piece() {
        type = "";
        x = -1;
        y = -1;
        isWhite = true;
        moves = 0;
    }

    public Piece(String type, int x, int y, boolean isWhite) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.isWhite = isWhite;

        if (type.equalsIgnoreCase("Pawn")) {
            this.letter = 'P';
        } else if (type.equalsIgnoreCase("Knight")) {
            this.letter = 'N';
        } else if (type.equalsIgnoreCase("Bishop")) {
            this.letter = 'B';
        } else if (type.equalsIgnoreCase("Rook")) {
            this.letter = 'R';
        } else if (type.equalsIgnoreCase("Queen")) {
            this.letter = 'Q';
        } else if (type.equalsIgnoreCase("King")) {
            this.letter = 'K';
        } else {
            this.letter = 'F';
        }

        String path = System.getProperty("user.dir") + "\\src\\res\\" + (type.equals("null") ? "" : (isWhite ? "white" : "black")) + type + ".png";

        try {
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Error: " + type + " image not found");
            System.out.println("Looked in: " + path);
        }
    }

    public Piece (Piece piece) {
        this.type = piece.type;
        this.x = piece.x;
        this.y = piece.y;
        this.isWhite = piece.isWhite;
        this.img = piece.img;
    }

    public boolean canMove(int x_, int y_, int b[][], ArrayList<Piece> pieces) {
        System.out.println(x + ", " + y);
        System.out.println(x_ + ", " + y_);

        // No piece can ever go where a friendly piece is, or stay still
        if (b[x_][y_] == (isWhite ? 1 : -1) || (x == x_ && y == y_)) {
            return false;
        }

        if (type.equals("Pawn")) {
            if (y_ == y + (isWhite ? -2 : 2)) { // On the first move, pawns can go forward 2 squares
                return b[x][y_] == 0 && b[x][y + (isWhite ? -1 : 1)] == 0 && x == x_ && (y == 1 || y == 6);
            } else if (y_ == y + (isWhite ? -1 : 1)) {  // Else, it can only go forward 1 square
                if (x == x_) {
                    return b[x][y_] == 0;   // Must move to empty space, if going straight forward
                } else if (x + 1 == x_ || x - 1 == x_) {
                    return b[x_][y_] == (isWhite ? -1 : 1); // Must be capturing enemy piece, if moving diagonally
                } else return false;
            } else return false;
        } else if (type.equals("Knight")) {
            return (Math.abs(x_ - x) == 2 && Math.abs(y_ - y) == 1) || // Knights go in an L shape
                    (Math.abs(x_ - x) == 1 && Math.abs(y_ - y) == 2);
        } else if (type.equals("Bishop")) {
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
        } else if (type.equals("Rook")) {
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
        } else if (type.equals("Queen")) {
            if (Math.abs(x_ - x) != Math.abs(y_ - y) && (x != x_ && y != y_)) { // Queens move like a Rook or Bishop
                return false;
            }

            int dist = Math.max(Math.abs(x_ - x), Math.abs(y_ - y));
            int xDir = (int) Math.signum(x_ - x);
            int yDir = (int) Math.signum(y_ - y);

            // Checking for pieces in between the Queen and its target square
            if (xDir * yDir != 0) {
                for (int i = 1; i < dist; i++) {
                    System.out.println(x + i * xDir + ", " + y + i * yDir);
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
        } else if (type.equals("King")) {
            if (Math.abs(x_ - x) < 2 && Math.abs(y_ - y) < 2) {
                return true;
            }
            if (moves > 0) {
                return false;
            }
            if (x + 2 == x_){
                int rook = -1;

                //for (Piece piece: pieces)
            }
        }
        return false;
    }

    public boolean legalMove(int x_, int y_, ArrayList<Piece> pieces, int b[][]) {
        ArrayList<Piece> pieces_ = pieces.stream().map(Piece::new).collect(Collectors.toCollection(ArrayList::new));

        for (Piece piece: pieces_) {
            if (piece.x == x_ && piece.y == y_) {
                piece.type = "null";
            }

            if (this.x == piece.x && this.y == piece.y) {
                piece.moves++;
                piece.x = x_;
                piece.y = y_;
                break;
            }
        }

        return canMove(x_, y_, b, pieces) && !inCheck(pieces_);
    }

    private boolean inCheck(ArrayList<Piece> pieces) {
        int b[][] = Board.pieceLocations(pieces);
        int kingX = -1;
        int kingY = -1;

        for (Piece piece : pieces) {
            System.out.println(this.isWhite + " & " + piece.isWhite);
            if (this.isWhite == piece.isWhite && piece.type.equals("King")) {
                kingX = piece.x;
                kingY = piece.y;
                break;
            }
        }

        System.out.println("King: " + kingX + ", " + kingY);

        for (Piece piece : pieces) {
            if (this.isWhite != piece.isWhite && piece.canMove(kingX, kingY, b, pieces)) {
                return true;
            }
        }

        return false;
    }

    public void draw(Graphics g, int x, int y, int w, int h) {
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
