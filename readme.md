## SudokuBoard
Sudoku solver for Java using backtracking. Finds correct size based on input. Call solve to solve board.

```java
class SolveBoard {
    public static void main(String[] args) {
        // Takes input as double list
        int[][] input = {
                {0, 1, 0, 0},
                {0, 2, 0, 0},
                {0, 0, 3, 0},
                {0, 0, 1, 0}
        };
        
        // Initialize and solve
        SudokuBoard board = new SudokuBoard(input);
        board.solve();
        
        // Print board
        System.out.println(board);
    }
}
```

Output
```text
41 23 
32 41 

14 32 
23 14
```

## Future improvements
* Generate set for constraining numbers
* Try greedy solves before backtracking
