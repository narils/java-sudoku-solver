import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SudokuBoard {
    private int[][] board;
    private final int EMPTY = 0;
    private int SIZE, BOX_WIDTH, BOX_HEIGHT;
    private Set<Integer> possibleNumbers;

    private void setBoxBounds() {
        // find highest int first, this should be set as the width.
        int startInt = (int) Math.ceil(Math.sqrt(SIZE));
        for (int i = 0; i < SIZE - startInt; i++) {
            int width = startInt + i;
            if (SIZE % width == 0) {
                BOX_WIDTH = width;
                BOX_HEIGHT = SIZE / width;
                return;
            }
        }
        throw new IllegalArgumentException("Could not set box bounds for boardsize " + SIZE + "x" + SIZE);
    }

    public SudokuBoard() {
    }

    public SudokuBoard(@NotNull int[][] board) throws IllegalArgumentException {
        setBoard(board);
    }

    public void setBoard(@NotNull int[][] board) throws IllegalArgumentException {
        this.board = board;
        setup(board);
    }

    public int[][] getBoard() {
        return board;
    }

    private void setup(@NotNull int[][] board) {
        SIZE = board.length;

        for (int rowIdx = 0; rowIdx < SIZE; rowIdx++) {
            int[] colArray = board[rowIdx];
            if (colArray.length != SIZE) {
                throw new IllegalArgumentException("Row " + (rowIdx + 1) + " is not of proper size (" + SIZE + ")");
            }
        }

        setBoxBounds();
        possibleNumbers = new HashSet<>();
        for (int i = 1; i < SIZE + 1; i++) {
            possibleNumbers.add(i);
        }
    }

    private Set<Integer> getConstraints(int row, int col) {
        Set<Integer> seenNumbers = new HashSet<>();

        for (int i = 0; i < SIZE; i++) {
            int rowValue = board[row][i];
            int colValue = board[i][col];

            if (rowValue != 0) seenNumbers.add(rowValue);
            if (colValue != 0) seenNumbers.add(colValue);
        }

        int rowStart = row - row % BOX_HEIGHT;
        int colStart = col - col % BOX_WIDTH;

        for (int rowIdx = rowStart; rowIdx < rowStart + BOX_HEIGHT; rowIdx++) {
            for (int colIdx = colStart; colIdx < colStart + BOX_WIDTH; colIdx++) {
                int boxValue = board[rowIdx][colIdx];
                if (boxValue != 0) seenNumbers.add(boxValue);
            }
        }
        return seenNumbers;
    }

    private List<GreedyPair> greedySolve() {
        List<GreedyPair> greedySolves = new LinkedList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == EMPTY) {
                    Set<Integer> constraint = getConstraints(row, col);
                    if (constraint.size() == SIZE - 1) {
                        Set<Integer> numbers = new HashSet<>(possibleNumbers);
                        numbers.removeAll(constraint);
                        for (int num : numbers) {
                            board[row][col] = num;
                            greedySolves.add(new GreedyPair(row, col));
                        }
                    }
                }
            }
        }
        return greedySolves;
    }

    private void undoGreedy(List<GreedyPair> undoList) {
        for (GreedyPair position : undoList) {
            board[position.row][position.col] = EMPTY;
        }
    }

    public boolean solve() {
        List<GreedyPair> greedySolves = greedySolve();

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == EMPTY) {
                    Set<Integer> constraint = getConstraints(row, col);
                    Set<Integer> numbers = new HashSet<>(possibleNumbers);
                    numbers.removeAll(constraint);

                    for (Integer num : numbers) {
                        board[row][col] = num;
                        if (!solve()) {
                            board[row][col] = EMPTY;
                        } else {
                            break;
                        }
                    }
                    if (board[row][col] == EMPTY) {
                        undoGreedy(greedySolves);
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

        for (int row = 0; row < SIZE; row++) {
            if (row > 0) {
                output.append('\n');
            }

            if (row != 0 && row % BOX_HEIGHT == 0) {
                output.append('\n');
            }

            for (int col = 0; col < SIZE; col++) {
                int val = board[row][col];
                output.append(val == EMPTY ? "_" : val);

                if ((col + 1) % BOX_WIDTH == 0 && col + 1 != SIZE) {
                    output.append(" ");
                }
            }
        }
        return output.toString();
    }
}

class GreedyPair {
    public final int row, col;

    public GreedyPair(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
