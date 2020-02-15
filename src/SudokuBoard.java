import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SudokuBoard {
    private int[][] board;
    private final int EMPTY = 0;
    private final int size;
    private final int boxWidth;
    private final int boxHeight;
    private Set<Integer> possibleNumbers;

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

        possibleNumbers = new HashSet<>();
        for (int i = 1; i < size + 1; i++) {
            possibleNumbers.add(i);
        }
    }

    private Set<Integer> getConstraints(int row, int col) {
        Set<Integer> seenNumbers = new HashSet<>();

        for (int i = 0; i < size; i++) {
            int rowValue = board[row][i];
            int colValue = board[i][col];

            if (rowValue != 0) seenNumbers.add(rowValue);
            if (colValue != 0) seenNumbers.add(colValue);
        }

        int rowStart = (row / boxHeight) * boxHeight;
        int colStart = (col / boxWidth) * boxWidth;

        for (int rowIdx = rowStart; rowIdx < rowStart + boxHeight; rowIdx++) {
            for (int colIdx = colStart; colIdx < colStart + boxWidth; colIdx++) {
                int boxValue = board[rowIdx][colIdx];
                if (boxValue != 0) seenNumbers.add(boxValue);
            }
        }
        return seenNumbers;
    }

    private List<GreedyPair> greedySolve() {
        List<GreedyPair> greedySolves = new LinkedList<>();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == EMPTY) {
                    Set<Integer> constraint = getConstraints(row, col);
                    if (constraint.size() == size - 1) {
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

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
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

                if ((col + 1) % boxWidth == 0 && col + 1 != size) {
                    output.append(" ");
                }
            }
        }
        return output.toString();
    }
}

class GreedyPair {
    public final int row;
    public final int col;

    public GreedyPair(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
