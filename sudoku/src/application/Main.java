package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.Timer;

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

							// Check if the new value violates Sudoku rules
					        int newValue = (currentValue % 9) + 1;
					        
					        while(!isValidBoard(finalI, finalJ, newValue)) {
					        	newValue = newValue%9+1;
					        }
					        boolean isItSafe = isValidBoard(finalI, finalJ, newValue);
					        
					     // Update the text and board value only if the new value is valid
					        clickedButton.setText(Integer.toString(newValue));
					        sudoku.board[finalI][finalJ] = newValue;
					        
					        // Update the text and board value only if the new value is valid
					        if (isItSafe) {
					       
					            clickedButton.setStyle("-fx-text-fill: black");
					        } else {
					            clickedButton.setStyle("-fx-text-fill: red");
					        }
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

		Scene scene = new Scene(grid, 1200, 800);
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
				} while (!isSafeBox(row + i - i % 3, col + j - j % 3, num));// change to validgroup
				board[row + i][col + j] = num;
			}
		}
	}

	public boolean isSafeBox(int rowStart, int colStart, int num) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[rowStart + i][colStart + j] == num) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isSafe(int row, int col, int num) {
		for (int x = 0; x < SIZE; x++)
			if (board[row][x] == num)
				return false;

		for (int y = 0; y < SIZE; y++)
			if (board[y][col] == num)
				return false;

		//checking id the number us already present in the 3x3
		int boxRowStart = row - row % 3;
		int boxColStart = col - col % 3;
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (board[boxRowStart + i][boxColStart + j] == num)
					return false;

		return true;
	}

	private boolean isValidBoard(int i, int j, int num) {
		return isSafe(i, j, num) && isSafeBox(i - i % 3, j - j % 3, num);
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
			if (j == (int) (i / 3) * 3)
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
			if (isValidBoard(i, j, num)) {
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
			int i = (int) (Math.random() * SIZE);
			int j = (int) (Math.random() * SIZE);
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