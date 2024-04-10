package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {
	//initializes  the board and sets the dedicated size
    private static final int SIZE = 9;
    private int[][] board;

    @Override
    public void start(Stage primaryStage) {
        Main sudoku = new Main();
        sudoku.fillValues();
        
        //creates the grid enables the gaps for resizing and grid lines
        GridPane grid = new GridPane();
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setGridLinesVisible(false);
        
        // fills the grid with buttons
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
            	//uses the fillRemaining to replace the zero'd values with a button
                if (sudoku.board[i][j] == 0) {
                    Button button = new Button("");
                    button.setPrefWidth(60);
                    button.setPrefHeight(60);
                    button.setStyle("-fx-background: transparent;");
                    button.setStyle("-fx-cursor-color: transparent;");
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
                    	            currentValue = Integer.parseInt(buttonText); // if the button value is empty, set value to 0
                    	        }
                    	        int newValue;
                    	        if (currentValue == 9) {            // allows the number to be removed after 9        	           
                    	            newValue = 0;                   // for an empty button if the player needs to remove the number
                    	            clickedButton.setText("");
                    	        } else {
                    	            newValue = currentValue + 1;    // if the button does not =0 increment by 1 till 9
                    	            clickedButton.setText(Integer.toString(newValue));
                    	        }
                    	        sudoku.board[finalI][finalJ] = newValue; // update the board array
                    	        
                    	        // check if the entered value is correct
                    	        boolean isCorrect = isCorrect(finalI, finalJ, newValue, sudoku);
                    	        
                    	        if (!isCorrect) {
                    	            // allows the green color to get removed if a button change results in an incorrect number
                    	            revertColor(finalI, finalJ, grid, sudoku);
                    	        }
                    	        // uses the completed guide of the puzzle to end the game when all values are filled
                    	        checkCompleted(grid, sudoku);
                    	        if (isComplete(sudoku)) {
                    	            primaryStage.setScene(createWinScene(primaryStage));
                    	        }
                    	    }
                    	});
                    grid.add(button, j, i);
                } else {
                	//if the value is not 0, disables the button and displays the given value 
                    Button button = new Button(Integer.toString(sudoku.board[i][j]));
                    button.setPrefWidth(60);
                    button.setPrefHeight(60);
                    button.setStyle("-fx-background-color: transparent;");
                    button.setStyle("-fx-boreder: transparent;");
                    button.setDisable(true); 
                    grid.add(button, j, i);
                }
            }
        }
        //builds the scene and title for the puzzle to display on 
        Scene scene = new Scene(grid, 540, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sudoku Puzzle");
        primaryStage.show();
    }

    public Main() {
        this.board = new int[SIZE][SIZE]; // takes the size to create a grid for the board
    }

    public void fillValues() {
        fillDiagonalBoxes();
        fillRemaining(0, 3);
        removeDigits(30); // use to change the number of zeros present 
    }

    public void fillDiagonalBoxes() {
        for (int i = 0; i < SIZE; i += 3) {
            fillBox(i, i);
        }
    }

    public int randomGenerator(int num) {
        return (int) Math.floor((Math.random() * num + 1));
    }

    public void fillBox(int row, int col) {
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

    public boolean isSafe(int i, int j, int num) {
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

    public boolean fillRemaining(int i, int j) {
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
            if (isSafe(i, j, num)) {
                board[i][j] = num;
                if (fillRemaining(i, j + 1))
                    return true;
                board[i][j] = 0;
            }
        }
        return false;
    }

    public void removeDigits(int count) {
        while (count != 0) {
            int i = (int) (Math.random() * SIZE);
            int j = (int) (Math.random() * SIZE);
            if (board[i][j] != 0) {
                count--;
                board[i][j] = 0;
            }
        }
    }

    public void checkCompleted(GridPane grid, Main sudoku) {
    	// uses the row checker to turn any completed row green 
        for (int i = 0; i < SIZE; i++) {
            if (isCompletedRow(i, sudoku)) {
                for (int j = 0; j < SIZE; j++) {
                    Button button = (Button) getNodeFromGridPane(grid, j, i);
                    button.setStyle("-fx-background-color: lightgreen;");
                } 
            }
         // uses the column checker to turn any completed column green 
            if (isCompletedColumn(i, sudoku)) {
                for (int j = 0; j < SIZE; j++) {
                    Button button = (Button) getNodeFromGridPane(grid, i, j);
                    button.setStyle("-fx-background-color: lightgreen;");
                }
            }
        }
     // uses the box checker to turn any completed box green 
        for (int i = 0; i < SIZE; i += 3) {
            for (int j = 0; j < SIZE; j += 3) {
                if (isCompletedBox(i, j, sudoku)) {
                    for (int k = 0; k < 3; k++) {
                        for (int l = 0; l < 3; l++) {
                            Button button = (Button) getNodeFromGridPane(grid, j + l, i + k);
                            button.setStyle("-fx-background-color: lightgreen;");
                        } 
                    }
                }
            }
        }
    }

    public boolean isCompletedRow(int row, Main sudoku) {
        boolean[] nums = new boolean[SIZE];
        // goes through the row and checks if the number input is seen before in the row
        for (int i = 0; i < SIZE; i++) {        
            int num = sudoku.board[row][i];
            if (num != 0 && !nums[num - 1]) {
                nums[num - 1] = true;
            } else if (num == 0) {
                return false; // If there's a missing value, row is not complete
            } else {
                return false; // If duplicate value found, row is not complete
            }
        }
        return true;
    }
 // goes through the column and checks if the number input is seen before in the column
    public boolean isCompletedColumn(int col, Main sudoku) {
        boolean[] nums = new boolean[SIZE];
        for (int i = 0; i < SIZE; i++) {
            int num = sudoku.board[i][col];
            if (num != 0 && !nums[num - 1]) {
                nums[num - 1] = true;
            } else if (num == 0) {
                return false; // If there's a missing value, column is not complete
            } else {
                return false; // If duplicate value found, column is not complete
            }
        }
        return true;
    }
 // goes through the box and checks if the number input is seen before in the box
    public boolean isCompletedBox(int row, int col, Main sudoku) {
        boolean[] nums = new boolean[SIZE];
        for (int i = row; i < row + 3; i++) {
            for (int j = col; j < col + 3; j++) {
                int num = sudoku.board[i][j];
                if (num != 0 && !nums[num - 1]) {
                    nums[num - 1] = true;
                } else if (num == 0) {
                    return false; // If there's a missing value, box is not complete
                } else {
                    return false; // If duplicate value found, box is not complete
                }
            }
        }
        return true;
    }

    public boolean isComplete(Main sudoku) {
    	// determines if the game is completed 
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (!isCompletedRow(i, sudoku) && !isCompletedColumn(j, sudoku) && !isCompletedBox(i - i % 3, j - j % 3, sudoku)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isCorrect(int i, int j, int num, Main sudoku) {
    	//used to revert the color if a number is changed in the sequence
        return isCompletedRow(i, sudoku) && isCompletedColumn(j, sudoku) && isCompletedBox(i - i % 3, j - j % 3, sudoku);
    }
    
    public void revertColor(int i, int j, GridPane grid, Main sudoku) {
    	// changes the color of the row is the button was previously green
        for (int k = 0; k < SIZE; k++) {
            Button rowButton = (Button) getNodeFromGridPane(grid, k, i);
            rowButton.setStyle("-fx-background-color: transparent;");
            rowButton.setStyle("-fx-cursor-color: transparent;");
         // changes the color of the column is the button was previously green
            Button colButton = (Button) getNodeFromGridPane(grid, j, k);
            colButton.setStyle("-fx-background-color: transparent;");
            colButton.setStyle("-fx-cursor-color: transparent;");           
        }
        
        int boxRowStart = i - i % 3;
        int boxColStart = j - j % 3;
        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 3; l++) {
            	// changes the color of the box is the button was previously green
                Button boxButton = (Button) getNodeFromGridPane(grid, boxColStart + l, boxRowStart + k);
                boxButton.setStyle("-fx-background-color: transparent;");
                boxButton.setStyle("-fx-cursor-color: transparent;");
            }
        }
    }

    public Scene createWinScene(Stage primaryStage) {
    	// displays the win screen when isComplete returns true 
        StackPane winPane = new StackPane();
        Text winText = new Text("You Win!");
        winText.setStyle("-fx-font-size: 24;");
        winPane.getChildren().add(winText);
        return new Scene(winPane, 200, 100);
    }
    
    public javafx.scene.Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null) {
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                    return node;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}