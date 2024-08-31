import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

public class SudokuSolverGUI extends JFrame {

    private static final int SIZE = 9;
    private JTextField[][] cells = new JTextField[SIZE][SIZE];
    private int[][] board = new int[SIZE][SIZE];
    private HashSet<Integer>[] rows = new HashSet[SIZE];
    private HashSet<Integer>[] cols = new HashSet[SIZE];
    private HashSet<Integer>[] subgrids = new HashSet[SIZE];

    // Define colors
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color CELL_BACKGROUND = new Color(200, 220, 240);
    private static final Color FIXED_NUMBER_COLOR = new Color(0, 0, 100);
    private static final Color SOLVED_NUMBER_COLOR = new Color(0, 100, 0);
    private static final Color BUTTON_COLOR = new Color(100, 150, 200);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;

    public SudokuSolverGUI() {
        setTitle("Sudoku Solver");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(SIZE, SIZE));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridPanel.setBackground(BACKGROUND_COLOR);

        for (int row = 0; row < SIZE; row++) {
            rows[row] = new HashSet<>();
            cols[row] = new HashSet<>();
            subgrids[row] = new HashSet<>();
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Arial", Font.BOLD, 20));
                cells[row][col].setBackground(CELL_BACKGROUND);
                cells[row][col].setBorder(BorderFactory.createLineBorder(Color.GRAY));
                gridPanel.add(cells[row][col]);
            }
        }
        add(gridPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton loadButton = createStyledButton("Load Puzzle");
        loadButton.addActionListener(e -> loadPuzzle());
        buttonPanel.add(loadButton);

        JButton solveButton = createStyledButton("Solve");
        solveButton.addActionListener(e -> new Thread(this::solvePuzzle).start());
        buttonPanel.add(solveButton);

        JButton clearButton = createStyledButton("Clear");
        clearButton.addActionListener(e -> clearBoard());
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
    }

    private void loadPuzzle() {
        int[][] puzzle = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col] = puzzle[row][col];
                if (puzzle[row][col] != 0) {
                    cells[row][col].setText(String.valueOf(puzzle[row][col]));
                    cells[row][col].setEditable(false);
                    cells[row][col].setForeground(FIXED_NUMBER_COLOR);
                    rows[row].add(puzzle[row][col]);
                    cols[col].add(puzzle[row][col]);
                    subgrids[(row / 3) * 3 + col / 3].add(puzzle[row][col]);
                } else {
                    cells[row][col].setText("");
                    cells[row][col].setEditable(true);
                    cells[row][col].setForeground(SOLVED_NUMBER_COLOR);
                }
            }
        }
    }

    private void solvePuzzle() {
        if (solve()) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(SudokuSolverGUI.this, "Sudoku Solved!", "Success", JOptionPane.INFORMATION_MESSAGE);
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(SudokuSolverGUI.this, "No solution exists for the given Sudoku board.", "Error", JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    private void clearBoard() {
        for (int row = 0; row < SIZE; row++) {
            rows[row].clear();
            cols[row].clear();
            subgrids[row].clear();
            for (int col = 0; col < SIZE; col++) {
                cells[row][col].setText("");
                cells[row][col].setEditable(true);
                cells[row][col].setForeground(SOLVED_NUMBER_COLOR);
                board[row][col] = 0;
            }
        }
    }

    private boolean isValid(int row, int col, int num) {
        return !rows[row].contains(num) && !cols[col].contains(num) && !subgrids[(row / 3) * 3 + col / 3].contains(num);
    }

    private boolean solve() {
        int[] empty = findEmptyCell();
        if (empty == null) {
            return true;
        }
        int row = empty[0];
        int col = empty[1];

        for (int num = 1; num <= SIZE; num++) {
            if (isValid(row, col, num)) {
                board[row][col] = num;
                rows[row].add(num);
                cols[col].add(num);
                subgrids[(row / 3) * 3 + col / 3].add(num);
                updateGUI(row, col, num);
                delay(50); // Delay to visualize steps
                if (solve()) {
                    return true;
                }
                board[row][col] = 0;
                rows[row].remove(num);
                cols[col].remove(num);
                subgrids[(row / 3) * 3 + col / 3].remove(num);
                updateGUI(row, col, 0);
                delay(50); // Delay to visualize steps
            }
        }
        return false;
    }

    private int[] findEmptyCell() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    private void updateGUI(int row, int col, int num) {
        SwingUtilities.invokeLater(() -> {
            cells[row][col].setText(num == 0 ? "" : String.valueOf(num));
            cells[row][col].setForeground(SOLVED_NUMBER_COLOR);
        });
    }

    private void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SudokuSolverGUI solver = new SudokuSolverGUI();
            solver.setVisible(true);
        });
    }
}