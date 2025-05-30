import java.util.*;
import java.io.*;

public class Schach {
    static Stack<char[][]> history = new Stack<>();

    public static void main(String[] args) {
        char[][] board = createBoard();
        Scanner scanner = new Scanner(System.in);
        boolean whiteTurn = true;

        while (true) {
            showBoard(board);
            if (isCheckmate(board, whiteTurn)) {
                System.out.println("Schachmatt! " + (whiteTurn ? "Schwarz" : "Weiß") + " gewinnt.");
                break;
            }

            System.out.println((whiteTurn ? "Weiß" : "Schwarz") + " ist am Zug.");
            System.out.print("Von (z.B. E2 oder 'undo'/'save'/'load'): ");
            String from = scanner.next().toUpperCase();

            if (from.equals("UNDO")) {
                if (!history.isEmpty()) board = history.pop();
                else System.out.println("Keine Züge zum Rückgängig machen.");
                continue;
            } else if (from.equals("SAVE")) {
                saveBoard(board);
                continue;
            } else if (from.equals("LOAD")) {
                board = loadBoard();
                continue;
            }

            System.out.print("Nach (z.B. E4): ");
            String to = scanner.next().toUpperCase();

            if (!isValidInput(from) || !isValidInput(to)) {
                System.out.println("Ungültiges Format!");
                continue;
            }

            char fromCol = from.charAt(0);
            int fromRow = Character.getNumericValue(from.charAt(1));
            char toCol = to.charAt(0);
            int toRow = Character.getNumericValue(to.charAt(1));

            if (isValidMove(board, fromCol, fromRow, toCol, toRow, whiteTurn)) {
                history.push(copyBoard(board));
                move(board, fromCol, fromRow, toCol, toRow);
                if (isInCheck(board, whiteTurn)) {
                    System.out.println("Achtung: Dein König ist in Schach!");
                }
                whiteTurn = !whiteTurn;
            } else {
                System.out.println("Ungültiger Zug.");
            }
        }
    }

    public static boolean isValidInput(String input) {
        if (input.length() != 2) return false;
        char col = input.charAt(0);
        char row = input.charAt(1);
        return col >= 'A' && col <= 'H' && row >= '1' && row <= '8';
    }

    public static char[][] createBoard() {
        char[][] board = new char[8][8];
        board[0] = new char[] {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'};
        board[1] = new char[] {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'};
        for (int i = 2; i < 6; i++) Arrays.fill(board[i], '0');
        board[6] = new char[] {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'};
        board[7] = new char[] {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'};
        return board;
    }

    public static void showBoard(char[][] board) {
        Map<Character, String> emoji = Map.ofEntries(
                Map.entry('R', "♖"), Map.entry('N', "♘"), Map.entry('B', "♗"), Map.entry('Q', "♕"),
                Map.entry('K', "♔"), Map.entry('P', "♙"), Map.entry('r', "♜"), Map.entry('n', "♞"),
                Map.entry('b', "♝"), Map.entry('q', "♛"), Map.entry('k', "♚"), Map.entry('p', "♟"),
                Map.entry('0', "·")
        );
        System.out.println("  A B C D E F G H");
        for (int i = 0; i < 8; i++) {
            System.out.print((8 - i) + " ");
            for (int j = 0; j < 8; j++) {
                System.out.print(emoji.getOrDefault(board[i][j], "?") + " ");
            }
            System.out.println(8 - i);
        }
        System.out.println("  A B C D E F G H");
    }

    public static void move(char[][] board, char fromCol, int fromRow, char toCol, int toRow) {
        int fx = fromCol - 'A';
        int fy = 8 - fromRow;
        int tx = toCol - 'A';
        int ty = 8 - toRow;
        board[ty][tx] = board[fy][fx];
        board[fy][fx] = '0';
    }

    public static char[][] copyBoard(char[][] board) {
        char[][] copy = new char[8][8];
        for (int i = 0; i < 8; i++) copy[i] = board[i].clone();
        return copy;
    }

    public static void saveBoard(char[][] board) {
        try (PrintWriter pw = new PrintWriter("spielstand.txt")) {
            for (int i = 0; i < 8; i++) pw.println(new String(board[i]));
            System.out.println("Spielstand gespeichert.");
        } catch (IOException e) {
            System.out.println("Fehler beim Speichern: " + e.getMessage());
        }
    }

    public static char[][] loadBoard() {
        char[][] board = new char[8][8];
        try (BufferedReader br = new BufferedReader(new FileReader("spielstand.txt"))) {
            for (int i = 0; i < 8; i++) board[i] = br.readLine().toCharArray();
            System.out.println("Spielstand geladen.");
        } catch (IOException e) {
            System.out.println("Fehler beim Laden: " + e.getMessage());
        }
        return board;
    }

    public static boolean isCheckmate(char[][] board, boolean whiteTurn) {
        if (!isInCheck(board, whiteTurn)) return false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char piece = board[i][j];
                if (piece == '0') continue;
                if (whiteTurn && !Character.isUpperCase(piece)) continue;
                if (!whiteTurn && !Character.isLowerCase(piece)) continue;
                for (int x = 0; x < 8; x++) {
                    for (int y = 0; y < 8; y++) {
                        char fromCol = (char) ('A' + j);
                        int fromRow = 8 - i;
                        char toCol = (char) ('A' + y);
                        int toRow = 8 - x;
                        if (!isValidMove(board, fromCol, fromRow, toCol, toRow, whiteTurn)) continue;
                        char[][] test = copyBoard(board);
                        test[x][y] = test[i][j];
                        test[i][j] = '0';
                        if (!isInCheck(test, whiteTurn)) return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isInCheck(char[][] board, boolean whiteTurn) {
        int kx = -1, ky = -1;
        char king = whiteTurn ? 'K' : 'k';
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (board[i][j] == king) {
                    ky = i;
                    kx = j;
                }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char attacker = board[i][j];
                if (attacker == '0') continue;
                if (whiteTurn && Character.isLowerCase(attacker)) {
                    if (isValidMove(board, (char) ('A' + j), 8 - i, (char) ('A' + kx), 8 - ky, false))
                        return true;
                }
                if (!whiteTurn && Character.isUpperCase(attacker)) {
                    if (isValidMove(board, (char) ('A' + j), 8 - i, (char) ('A' + kx), 8 - ky, true))
                        return true;
                }
            }
        }
        return false;
    }

    public static boolean isValidMove(char[][] board, char fromCol, int fromRow, char toCol, int toRow, boolean whiteTurn) {
        int fx = fromCol - 'A', fy = 8 - fromRow;
        int tx = toCol - 'A', ty = 8 - toRow;
        if (fx < 0 || fy < 0 || fx >= 8 || fy >= 8 || tx < 0 || ty < 0 || tx >= 8 || ty >= 8) return false;

        char piece = board[fy][fx];
        char target = board[ty][tx];
        if (piece == '0') return false;
        if (whiteTurn && !Character.isUpperCase(piece)) return false;
        if (!whiteTurn && !Character.isLowerCase(piece)) return false;
        if ((Character.isUpperCase(piece) && Character.isUpperCase(target)) ||
                (Character.isLowerCase(piece) && Character.isLowerCase(target))) return false;

        int dx = tx - fx, dy = ty - fy;

        switch (Character.toLowerCase(piece)) {
            case 'p':
                int dir = whiteTurn ? -1 : 1;
                if (dx == 0 && dy == dir && target == '0') return true;
                if (dx == 0 && dy == 2 * dir && ((whiteTurn && fy == 6) || (!whiteTurn && fy == 1)) &&
                        board[fy + dir][fx] == '0' && target == '0') return true;
                if (Math.abs(dx) == 1 && dy == dir && target != '0' &&
                        ((whiteTurn && Character.isLowerCase(target)) || (!whiteTurn && Character.isUpperCase(target))))
                    return true;
                break;
            case 'r':
                if (dx == 0 || dy == 0) return isPathClear(board, fx, fy, tx, ty);
                break;
            case 'n':
                if ((Math.abs(dx) == 2 && Math.abs(dy) == 1) || (Math.abs(dx) == 1 && Math.abs(dy) == 2)) return true;
                break;
            case 'b':
                if (Math.abs(dx) == Math.abs(dy)) return isPathClear(board, fx, fy, tx, ty);
                break;
            case 'q':
                if (dx == 0 || dy == 0 || Math.abs(dx) == Math.abs(dy)) return isPathClear(board, fx, fy, tx, ty);
                break;
            case 'k':
                if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1) return true;
                break;
        }

        return false;
    }

    public static boolean isPathClear(char[][] board, int fx, int fy, int tx, int ty) {
        int dx = Integer.compare(tx, fx);
        int dy = Integer.compare(ty, fy);
        int x = fx + dx, y = fy + dy;
        while (x != tx || y != ty) {
            if (board[y][x] != '0') return false;
            x += dx;
            y += dy;
        }
        return true;
    }
}
