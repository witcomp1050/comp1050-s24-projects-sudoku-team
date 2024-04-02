package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
    private static final int SIZE = 9;
    private int[][] board;

    @Override
    public void start(Stage primaryStage) {
        Main sudoku = new Main();
        sudoku.fillValues();

        GridPane grid = new GridPane();
        grid.setHgap(6);
        grid.setVgap(6);

        // Fill the grid with values and buttons for zeros
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (sudoku.board[i][j] == 0) {
                    Button button = new Button("");
                    button.setPrefWidth(60);
                    button.setPrefHeight(60);
                    button.setStyle("-fx-border-color: black;");
                    int finalI = i;
                    int finalJ = j;            
                    button.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            // what happens when the button is clicked
                        	 Button clickedButton = (Button) event.getSource();
                             String buttonText = clickedButton.getText();
                             int currentValue = 0;
                             if (!buttonText.isEmpty()) {
                                 currentValue = Integer.parseInt(buttonText);
                             }
                             int newValue = (currentValue % 9) + 1;
                             clickedButton.setText(Integer.toString(newValue));
                             sudoku.board[finalI][finalJ] = newValue; // Update the board array
                         }
                     });
                    grid.add(button, j, i);
                } else {
                    Button button = new Button(Integer.toString(sudoku.board[i][j]));
                    button.setPrefWidth(60);
                    button.setPrefHeight(60);
                    button.setStyle("-fx-border-color: black;");
                    button.setDisable(true); // Disable buttons for non-zero cells
                    grid.add(button, j, i);
                }
            }
        }

        Scene scene = new Scene(grid, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sudoku Puzzle");
        primaryStage.show();
    }

    public Main() {
        this.board = new int[SIZE][SIZE];
    }

    public void fillValues() {
        fillDiagonalBoxes();
        fillRemaining(0, 3);
        removeDigits(45); // changes the number of zeros present for difficulty
    }

    private void fillDiagonalBoxes() {
        for (int i = 0; i < SIZE; i += 3) {
            fillBox(i, i);
        }
    }

    private int randomGenerator(int num) {
        return (int) Math.floor((Math.random() * num + 1));
    }

    private void fillBox(int row, int col) {
        int num;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                do {
                    num = randomGenerator(SIZE);
                } while (!isSafeBox(row, col, num));
                board[row + i][col + j] = num;
            }
        }
    }

    private boolean isSafeBox(int rowStart, int colStart, int num) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[rowStart + i][colStart + j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isSafe(int i, int j, int num) {
        for (int x = 0; x < SIZE; x++)
            if (board[i][x] == num)
                return false;

        for (int x = 0; x < SIZE; x++)
            if (board[x][j] == num)
                return false;

        int boxRowStart = i - i % 3;
        int boxColStart = j - j % 3;
        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 3; y++)
                if (board[boxRowStart + x][boxColStart + y] == num)
                    return false;

        return true;
    }

    private boolean fillRemaining(int i, int j) {
        if (j >= SIZE && i < SIZE - 1) {
            i = i + 1;
            j = 0;
        }
        if (i >= SIZE && j >= SIZE)
            return true;

        if (i < 3) {
            if (j < 3)
                j = 3;
        } else if (i < SIZE - 3) {
            if (j == (int)(i / 3) * 3)
                j = j + 3;
        } else {
            if (j == SIZE - 3) {
                i = i + 1;
                j = 0;
                if (i >= SIZE)
                    return true;
            }
        }

        for (int num = 1; num <= SIZE; num++) {
            if (isSafe(i, j, num)) {
                board[i][j] = num;
                if (fillRemaining(i, j + 1))
                    return true;
                board[i][j] = 0;
            }
        }
        return false;
    }

    private void removeDigits(int count) {
        while (count != 0) {
            int i = (int)(Math.random() * SIZE);
            int j = (int)(Math.random() * SIZE);
            if (board[i][j] != 0) {
                count--;
                board[i][j] = 0;
            }
        }
    }
  
    public static void main(String[] args) {
        launch(args);
    }
}