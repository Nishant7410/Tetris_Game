import java.util.*;
import java.io.IOException;

class Tetris {

    NewShape m[];
    NewShape k;
    Board b;
    Random r;
    Stack<String> stack;
    Stack<String> redoStack;
    Stack<NewState> NewStateUndo;
    Stack<NewState> NewStateRedo;
    boolean status;

    Tetris() {
        m = new NewShape[] { new Line(), new T(), new square(), new Z(), new L() };
        k = m[(int) (Math.random() * 5)];
        b = new Board();
        NewStateUndo = new Stack<NewState>();
        NewStateRedo = new Stack<NewState>();
        stack = new Stack<String>();
        redoStack = new Stack<String>();
    }
    
     void functions(Tetris t) throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);

        char choice = sc.next().charAt(0);

        while (choice != 'e') {

            switch (choice) {
            case 's':
                NewStateRedo = new Stack<NewState>();
                t.redoStack = new Stack<String>();
                t.k.rotateLeft();
                t.stack.push("s");
                t.boardFunctions();
                break;

            case 'w':
                NewStateRedo = new Stack<NewState>();
                t.redoStack = new Stack<String>();
                t.k.rotateRight();
                t.stack.push("w");
                t.boardFunctions();
                break;

            case 'd':
                NewStateRedo = new Stack<NewState>();
                t.redoStack = new Stack<String>();
                t.status = t.k.moveRight(t.b);
                if (t.status) {
                    t.stack.push("d");
                }
                t.boardFunctions();
                break;

            case 'a':
                NewStateRedo = new Stack<NewState>();
                t.redoStack = new Stack<String>();
                t.status = t.k.moveLeft(t.b);
                if (t.status) {
                    t.stack.push("a");
                }
                t.boardFunctions();
                break;

            case 'x':
                NewStateRedo = new Stack<NewState>();
                t.redoStack = new Stack<String>();
                t.k.moveDown();
                t.stack.push("x");
                t.boardFunctions();
                break;

            case 'u':
                handleundo(t);
                break;

            case 'r':
                handleredo(t);
                break;
            }
            choice = sc.next().charAt(0);
        }
        sc.close();
    }

    void handleredo(Tetris t) throws IOException, InterruptedException {
        if (t.redoStack.empty()) {
            t.boardFunctions();
           
        } else {
            if (t.redoStack.peek() == "s") {
                t.stack.push(t.redoStack.pop());
                t.k.rotateLeft();
            } else if (t.redoStack.peek() == "w") {
                t.stack.push(t.redoStack.pop());
                t.k.rotateLeft();
            } else if (t.redoStack.peek() == "d") {
                t.stack.push(t.redoStack.pop());
                t.k.moveRight(t.b);
            } else if (t.redoStack.peek() == "a") {
                t.stack.push(t.redoStack.pop());
                t.k.moveLeft(t.b);
            } else {
                t.stack.push(t.redoStack.pop());
                t.k.moveDown();
            }
            t.boardFunctions();
        }
    }

    void handleundo(Tetris t) throws IOException, InterruptedException {
        if (t.stack.empty()) {
            t.boardFunctions();
            if (t.NewStateUndo.empty()) {
                t.boardFunctions();
            } else {
                for (int i = 0; i < 30; i++) {
                    for (int j = 0; j < 31; j++) {
                        System.out.print(t.NewStateUndo.peek().b.finalBoard[i][j] + " ");
                    }
                    System.out.println("");
                }
                t.b = t.NewStateUndo.peek().b;
                t.k = t.NewStateUndo.peek().k;
                t.stack = t.NewStateUndo.peek().undo;
                t.redoStack = t.NewStateUndo.peek().redo;
                t.handleundo(t);
                t.NewStateRedo.push(t.NewStateUndo.pop());
                t.boardFunctions();
            }
        } else {
            if (t.stack.peek() == "s") {
                t.redoStack.push(t.stack.pop());
                t.k.rotateRight();
            } else if (t.stack.peek() == "w") {
                t.redoStack.push(t.stack.pop());
                t.k.rotateLeft();
            } else if (t.stack.peek() == "d") {
                t.redoStack.push(t.stack.pop());
                t.k.moveLeft(t.b);
            } else if (t.stack.peek() == "a") {
                t.redoStack.push(t.stack.pop());
                t.k.moveRight(t.b);
            } else {
                t.redoStack.push(t.stack.pop());
                t.k.moveUp();
            }
            t.boardFunctions();
        }
    }

    void boardFunctions() throws IOException, InterruptedException {
        b.makeBoard();
        boolean flag = b.insertShape(k.getnowShape());
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        b.printBoard();
        if (flag) {
            NewStateUndo.push(new NewState(b, stack, k, redoStack));

            b = new Board(b);
            b.savepoint(k.getnowShape().getCordinates());
            stack = new Stack<String>();
            redoStack = new Stack<String>();
            m = new NewShape[] { new Line(), new T(), new square(), new Z(), new L() };
            k = m[(int) (Math.random() * 5)];
            boardFunctions();
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        Tetris t = new Tetris();
        t.boardFunctions();
        t.functions(t);
    }

   

}

class NewState {
    Board b;
    Stack<String> undo;
    NewShape k;
    Stack<String> redo;

    NewState(Board b1, Stack<String> undo1, NewShape k1, Stack<String> redo1) {
        b = b1;
        undo = undo1;
        k = k1;
        redo = redo1;
    }

}

class Board {

    public char board[][] = new char[30][31];
    public char finalBoard[][] = new char[30][31];

    Board(Board b) {
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 31; j++) {
                board[i][j] = b.board[i][j];
                finalBoard[i][j] = b.finalBoard[i][j];
            }
        }
    }

    Board() {
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (i == 0 || i == 29 || j == 0 || j == 29) {
                    finalBoard[i][j] = '*';
                } else
                    finalBoard[i][j] = ' ';
            }
        }
        for (int i = 0; i < 30; i++) {
            finalBoard[i][30] = '0';
        }
    }

    public void makeBoard() {
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (i == 0 || i == 29 || j == 0 || j == 29) {
                    board[i][j] = '*';
                } else
                    board[i][j] = ' ';
            }
        }
    }

    void savepoint(int[][] co) {
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (board[i][j] == '#') {
                    finalBoard[i][j] = '$';
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            finalBoard[co[i][0] + 4][30]++;
        }
        for (int i = 0; i < 30; i++) {
            if (finalBoard[i][30] - '0' == 28) {
                for (int m = i; finalBoard[m][30] > '0'; m--) {
                    for (int n = 1; n < 29; n++) {
                        finalBoard[m][n] = finalBoard[m - 1][n];
                    }
                }
            }
        }

    }

    public boolean insertShape(Shape s) {
        int[][] co = s.getCordinates();
        boolean flag = false;
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (board[i][j] != '#')
                    board[i][j] = finalBoard[i][j];
            }
        }
        for (int i = 0; i < 4; i++) {
            if ((board[(co[i][0] + 5)][co[i][1] + 4] == '$' || board[(co[i][0] + 5)][co[i][1] + 4] == '*')) {
                flag = true;
            }
            board[(co[i][0] + 4)][co[i][1] + 4] = '#';
        }
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (board[i][j] == '$')
                    board[i][j] = '#';
            }
        }
        return flag;
    }

    public void printBoard() {
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println("");
        }
    }
}

class Shape {
    int[][] shape = new int[4][2];

    Shape(int[][] ar) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                shape[i][j] = ar[i][j];
            }
        }

    }

    int[][] getCordinates() {
        return shape;
    }

    boolean moveRight(Board b) {

        for (int i = 0; i < 4; i++) {
            if (b.finalBoard[shape[i][0] + 4][shape[i][1] + 5] == '$'
                    || b.finalBoard[shape[i][0] + 4][shape[i][1] + 5] == '*') {
                return false;
            }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 1; j < 2; j++) {
                shape[i][j] += 1;
            }
        }

        return true;
    }

    boolean moveLeft(Board b) {
        for (int i = 0; i < 4; i++) {
            if (b.finalBoard[shape[i][0] + 4][shape[i][1] + 3] == '$'
                    || b.finalBoard[shape[i][0] + 4][shape[i][1] + 3] == '*') {
                return false;
            }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 1; j < 2; j++) {
                shape[i][j] -= 1;
            }
        }
        return true;
    }

    void moveDown() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 1; j++) {
                shape[i][j] += 1;
            }
        }

    }

    void moveUp() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 1; j++) {
                shape[i][j] -= 1;
            }
        }
    }
}

class NewShape {
    Shape main;
    Shape left;
    Shape right;
    Shape invert;
    int now = 0;

    Shape[] states;

    public Shape getnowShape() {
        return states[now];
    }

    public void rotateLeft() {
        if (now < 4 && now != 0) {
            now -= 1;
        } else if (now == 0) {
            now = 3;
        }
    }

    public void rotateRight() {
        if (now < 3) {
            now += 1;
        } else if (now == 3) {
            now = 0;
        }
    }

    boolean moveRight(Board b) {
        for (int i = 0; i < 4; i++) {
            if (!states[i].moveRight(b)) {
                return false;
            }
        }
        return true;
    }

    boolean moveLeft(Board b) {
        for (int i = 0; i < 4; i++) {
            if (!states[i].moveLeft(b)) {
                return false;
            }
        }
        return true;
    }

    void moveDown() {
        for (int i = 0; i < 4; i++) {
            states[i].moveDown();
        }
    }

    void moveUp() {
        for (int i = 0; i < 4; i++) {
            states[i].moveUp();
        }
    }

}

class Line extends NewShape {

    Line() {
        now = 0;
        main = new Shape(new int[][] { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 3, 0 } });
        left = new Shape(new int[][] { { 0, 0 }, { 0, -1 }, { 0, -2 }, { 0, -3 } });
        right = new Shape(new int[][] { { 0, 0 }, { 0, 1 }, { 0, 2 }, { 0, 3 } });
        invert = new Shape(new int[][] { { 0, 0 }, { -1, 0 }, { -2, 0 }, { -3, 0 } });
        states = new Shape[] { main, right, invert, left };
    }

}

class T extends NewShape {

    T() {
        now = 0;
        main = new Shape(new int[][] { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } });
        left = new Shape(new int[][] { { 0, 0 }, { 0, 1 }, { -1, 0 }, { 1, 0 } });
        right = new Shape(new int[][] { { 0, 0 }, { -1, 0 }, { 1, 0 }, { 0, -1 } });
        invert = new Shape(new int[][] { { 0, 0 }, { -1, 0 }, { 0, -1 }, { 0, 1 } });
        states = new Shape[] { main, right, invert, left };
    }

}

class L extends NewShape {
    L() {
        now = 0;
        main = new Shape(new int[][] { { 0, 0 }, { 0, 1 }, { -1, 0 }, { -2, 0 } });
        left = new Shape(new int[][] { { 0, 0 }, { -1, 0 }, { 0, -2 }, { 0, -1 } });
        right = new Shape(new int[][] { { 0, 0 }, { 0, 1 }, { 0, 2 }, { 1, 0 } });
        invert = new Shape(new int[][] { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 0, -1 } });
        states = new Shape[] { main, right, invert, left };
    }
}

class Z extends NewShape {
    Z() {
        now = 0;
        main = new Shape(new int[][] { { 0, 0 }, { -1, -1 }, { 0, -1 }, { 1, 0 } });
        right = new Shape(new int[][] { { 0, 0 }, { -1, 0 }, { -1, 1 }, { 0, -1 } });
        states = new Shape[] { main, right };
    }

    public void rotateLeft() {
        if (now < 2 && now != 0) {
            now -= 1;
        } else if (now == 0) {
            now = 1;
        }
    }

    public void rotateRight() {
        if (now < 1) {
            now += 1;
        } else if (now == 1) {
            now = 0;
        }
    }

    boolean moveRight(Board b) {
        for (int i = 0; i < 2; i++) {
            if (!states[i].moveRight(b)) {
                return false;
            }
        }
        return true;
    }

    boolean moveLeft(Board b) {
        for (int i = 0; i < 2; i++) {
            if (!states[i].moveLeft(b)) {
                return false;
            }
        }
        return true;
    }

    void moveDown() {
        for (int i = 0; i < 2; i++) {
            states[i].moveDown();
        }
    }

    void moveUp() {
        for (int i = 0; i < 2; i++) {
            states[i].moveUp();
        }
    }
}

class square extends NewShape {

    square() {
        now = 0;
        main = new Shape(new int[][] { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 3, 0 } });
        left = new Shape(new int[][] { { 0, 0 }, { 0, -1 }, { 0, -2 }, { 0, -3 } });
        right = new Shape(new int[][] { { 0, 0 }, { 0, 1 }, { 0, 2 }, { 0, 3 } });
        invert = new Shape(new int[][] { { 0, 0 }, { -1, 0 }, { -2, 0 }, { -3, 0 } });
        states = new Shape[] { main, right, invert, left };
    }

}