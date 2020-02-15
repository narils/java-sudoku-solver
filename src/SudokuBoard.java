import org.jetbrains.annotations.NotNull;

public class SudokuBoard {
    private int[][] board;
    private final int EMPTY = 0;
    private final int size;
    private final int boxWidth;
    private final int boxHeight;

    private int[] getBoxBounds() {
        int[] boxSizes = {0, 0};

        // find highest int first, this should be set as the width.
        int startInt = (int) Math.ceil(Math.sqrt(size));
        for (int i = 0; i < size - startInt; i++) {
            int width = startInt + i;
            if (size % width == 0) {
                boxSizes[0] = width;
                boxSizes[1] = size / width;
                return boxSizes;
            }
        }
        throw new IllegalArgumentException("Could not set box bounds for boardsize " + size + "x" + size);
    }

    public SudokuBoard(@NotNull int[][] board) throws IllegalArgumentException {
        this.board = board;
        size = board.length;
        int[] boxSizes = getBoxBounds();
        boxWidth = boxSizes[0];
        boxHeight = boxSizes[1];
    }

    private boolean containsInRow(int row, int n) {
        for (int i = 0; i < size; i++) {
            if (board[row][i] == n) {
                return true;
            }
        }
        return false;
    }

    private boolean containsInCol(int col, int n) {
        for (int i = 0; i < size; i++) {
            if (board[i][col] == n) {
                return true;
            }
        }
        return false;
    }

    private boolean containsInBox(int row, int col, int n) {
        int rowStart = (row / boxHeight) * boxHeight;
        int colStart = (col / boxWidth) * boxWidth;

        for (int rowOffset = 0; rowOffset < boxHeight; rowOffset++) {
            for (int colOffset = 0; colOffset < boxWidth; colOffset++) {
                int value = board[rowStart + rowOffset][colStart + colOffset];
                if (value == n) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAllowed(int row, int col, int n) {
        return !containsInRow(row, n) && !containsInCol(col, n) && !containsInBox(row, col, n);
    }

    public boolean solve() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == EMPTY) {
                    for (int i = 1; i < size + 1; i++) {
                        if (isAllowed(row, col, i)) {
                            board[row][col] = i;
                            if (!solve()) {
                                board[row][col] = EMPTY;
                            }
                        }
                    }
                    if (board[row][col] == EMPTY) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        for (int row = 0; row < size; row++) {
            if (row > 0) {
                output.append('\n');
            }

            if (row != 0 && row % boxHeight == 0) {
                output.append('\n');
            }

            for (int col = 0; col < size; col++) {
                int val = board[row][col];
                output.append(val == EMPTY ? "_" : val);

                if ((col + 1) % boxWidth == 0) {
                    output.append(" ");
                }
            }
        }
        return output.toString();
    }
}
