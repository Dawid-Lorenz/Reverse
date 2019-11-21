package com.example.reverse;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reverse.Reversi.State;

public class Players extends AppCompatActivity {

    int white, black, empty, available;
    boolean playerFlag;
    State[][] board;

    Reversi engine;

    /*
    Changes the images of the positions given their state in the board array
     */

    private void updateTheBoard(boolean player, int rowNumber, int colNumber, TableRow[] rows)
    {
        // TODO possible error in availability!

        boolean thereAreNoAvs = true;

        int whites = 0;
        int blacks = 0;

        board = engine.updateBoardState(player, rowNumber, colNumber, board).clone();

        for (int row = 0; row < rowNumber; row++)
        {
            for (int col = 0; col < colNumber; col++)
            {
                ImageButton btn = (ImageButton) rows[row].getChildAt(col);

                if (board[row][col] == null) // == State.EMPTY)
                    btn.setImageResource(empty);
                else if (board[row][col] == State.WHITE)
                {
                    whites++;
                    btn.setImageResource(white);
                }
                else if (board[row][col] == State.BLACK)
                {
                    blacks++;
                    btn.setImageResource(black);
                }
                else if ((player && (board[row][col] == State.AV_WHITE))
                        || (!player && (board[row][col] == State.AV_BLACK)))
                {
                    btn.setImageResource(available);
                }
                if (board[row][col] == State.AV_WHITE || board[row][col] == State.AV_BLACK)
                    thereAreNoAvs = false;

            }
        }

        System.out.println(thereAreNoAvs);

        if (thereAreNoAvs) {
            CharSequence message;
            if (whites > blacks)
                message = "White has won!";
            else if (whites < blacks)
                message = "Black has won!";
            else
                message = "It's a tie!";

            new AlertDialog.Builder(Players.this)
                    .setTitle("Game over")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            startActivity(new Intent(Players.this, MainActivity.class));
                        }
                    })
                    .show();

        }

    }
    /*
    Is move from button (row1, col1) to (row2, col2) valid
     */
    /*
    private boolean isValidMove(boolean player, int rowNum, int row1, int col1, int row2, int col2)
    {
        // TODO may need some refactoring - simplified case now!
        State state = player ? State.WHITE : State.BLACK;
//        System.out.println("Move from [" + row1 + ", " + col1 + "] to [" + row2 + ", " + col2 + "] is being checked!");
//        System.out.println("White color is " + white + ", checked is: " + state);
        //out of bounds
        if (row1 >= rowNum || row2 >= rowNum || row1 < 0 || row2 < 0)
        {
//            System.out.println("rows out of bounds");
            return false;
        }
        else {
            int col1Num = 8;
            int col2Num = 8;
            if (col1 >= col1Num || col2 >= col2Num || col1 < 0 || col2 < 0)
            {
//                System.out.println("cols out of bounds");
                return false;
            }

                // the same button
            else if (row1 == row2 && col1 == col2)
                return false;
            else if ((Math.abs(row1 - row2) == Math.abs(col1 - col2)) || ((col1 == col2) || (row1 == row2)))
            {
                if ((   State.EMPTY == board[row1][col1] ||
                        State.AV_BLACK == board[row1][col1] ||
                        State.AV_WHITE == board[row1][col1] ||
                        State.AV_BOTH == board[row1][col1]) && state == board[row2][col2])
                {
                    int dRow;
                    State opposite = player ? State.BLACK : State.WHITE;
                    if (row2 == row1)
                        dRow = 0;
                    else
                        dRow = row2 - row1 > 0 ? 1 : -1;
                    int dCol;
                    if (col1 == col2)
                        dCol = 0;
                    else
                        dCol = col2 - col1 > 0 ? 1 : -1;
                    int currRow = row1 + dRow;
                    int currCol = col1 + dCol;
                    State currState;
                    if (currRow == row2 && currCol == col2) {
//                        System.out.println("Target neighbours source!");
                        return false;
                    }
                    while ((currRow != row2 && currCol != col2) || (currCol != col2 && row1 == row2)
                            || (currRow != row2 && col1 == col2)) {
//                        System.out.println("[" + currRow + ", " + currCol + "]");
                        currState = board[currRow][currCol];
//                        System.out.println(currState);
                        if (opposite != currState) {
//                            System.out.println("not opposite color");
                            return false;
                        }

                        currRow += dRow;
                        currCol += dCol;
                    }
//                    System.out.println("Move from [" + row1 + ", " + col1 + "] to [" + row2 + ", " + col2 + "] is valid");
                    if (State.EMPTY == board[row1][col1])
                        board[row1][col1] = player ? State.AV_WHITE : State.AV_BLACK;
                    else if (State.AV_BLACK == board[row1][col1] && player ||
                            State.AV_WHITE == board[row1][col1] && !player)
                        board[row1][col1] = State.AV_BOTH;
                    return true;
                }
            }
        }
//        System.out.println("unreachable");
        //unreachable statement
        return false;
    }
    */
    private boolean hasPossibleMoves(boolean player, int rowNum, TableRow[] rows)
    {
//        System.out.println("Checking for moves of " + (player ? "white" : "black"));
        boolean hasMoves = false;

        for (int row = 0; row < rowNum; row++)
        {
            int colNum = rows[row].getChildCount();
            for (int col = 0; col < colNum; col++) {
                //System.out.println("Button: [" + row + ", " + col + "]");
//                boolean thisHasMoves = false;

                /*
                    // looping over up-down directions:
                for (int dRow = -1; dRow <= 1; dRow++)
                    for (int dCol = -1; dCol <= 1; dCol++) {
                        if (dCol != 0 || dRow != 0)
                        // loop over the rows and cols multiplier:
                            for (int mult = 1; mult < 8; mult++) {
                                int currRow = row + mult*dRow;
                                int currCol = col + mult*dCol;
                                //System.out.println("Current button: [" + (row + mult * dRow) + ", " + (col + mult * dCol) + "]");
                                if (isValidMove(player, rowNum, row, col, currRow, currCol))
                                {
//                                        System.out.println("Has moves!");
//                                    thisHasMoves = true;
                                    hasMoves = true;
                                }
                                else if (currCol > colNum || currRow > rowNum || currCol < 0 || currRow < 0)
                                    break;
                            }
                    }

//                if (!thisHasMoves)
//                    board[row][col] = State.EMPTY;
                 */
                if(player && board[row][col] == State.AV_WHITE)
                    return true;
                else if(!player && board[row][col] == State.AV_BLACK)
                    return true;

            }
        }
        return hasMoves;
    }

    /*
    ((dCol != 0) || (dRow != 0)) &&
            ((row + (mult * dRow)) < rowNum) && ((row + (mult * dRow)) >= 0) &&
            ((col + (mult * dCol)) < colNum) && ((col + (mult * dCol)) >= 0))
     */

    private void updateText(TextView move, TextView whiteScore, TextView blackScore, int rowNum, TableRow[] rows)
    {
        if (playerFlag)
            move.setText(getString(R.string.white_turn));
        else
            move.setText(getString(R.string.black_turn));

        int whites = 0;
        int blacks = 0;

        for(int i = 0; i < rowNum; i++)
        {
            for (int j = 0; j < rows[i].getChildCount(); j++)
            {
                if (State.WHITE == board[i][j])
                    whites++;
                else if (State.BLACK == board[i][j])
                    blacks++;
            }
        }

        whiteScore.setText("White: " + whites);
        blackScore.setText("Black: " + blacks);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        white = R.drawable.white;
        black = R.drawable.black;
        empty = R.drawable.empty;
        available = R.drawable.available;

        playerFlag = true;

        board = new State[8][8];
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                board[i][j] = null; // State.EMPTY;
        board[3][3] = board[4][4] = State.WHITE;
        board[3][4] = board[4][3] = State.BLACK;

        engine = new Reversi();

        final TableLayout table = findViewById(R.id.table);
        final int rowNumber = table.getChildCount();

        final TextView move = (TextView) findViewById(R.id.move);
        final TextView whiteScore = findViewById(R.id.white_score);
        final TextView blackScore = findViewById(R.id.black_score);

        final TableRow[] rows = new TableRow[rowNumber];
        for (int row = 0; row < rowNumber; row++) {
            rows[row] = (TableRow) table.getChildAt(row);
        }


        updateTheBoard(playerFlag, rowNumber, rowNumber, rows);
        updateText(move, whiteScore, blackScore, rowNumber, rows);

        for (int row = 0; row < rowNumber; row++) {
            final int rowFin = row;
            final int colNumber = rows[row].getChildCount();
            for (int col = 0; col < colNumber; col++) {
                final int colFin = col;
                final ImageButton btn = (ImageButton) rows[row].getChildAt(col);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        State color = playerFlag ? State.WHITE : State.BLACK;
                        boolean hasPossibleMoves = hasPossibleMoves(playerFlag, rowNumber, rows);

                        System.out.println("On click!");
                        System.out.println("hPm = " + hasPossibleMoves);

                        if (!hasPossibleMoves)
                        {
                            CharSequence message = playerFlag ? "White has no possible moves!": "Black has no possible moves!";
                            Toast popup = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT );
                            System.out.println("A: Changing the player!");
                            playerFlag = !playerFlag;
                            updateText(move, whiteScore, blackScore, rowNumber, rows);
                            updateTheBoard(playerFlag, rowNumber, colNumber, rows);
                            popup.show();
                        }
                        else
                        {
                            System.out.println("Button: " + rowFin + ", " + colFin);
                            System.out.println("State: " + board[rowFin][colFin]);
                            State btnState = board[rowFin][colFin];
                            boolean moved = false;
                            if (
                                btnState == State.AV_WHITE && playerFlag ||
                                btnState == State.AV_BLACK && !playerFlag)
                                //|| btnState == State.AV_BOTH)
                            {
                            System.out.println("Button is correct");
                                // looping over up-down directions:
                                for (int dRow = -1; dRow <= 1; dRow++)
                                    for (int dCol = -1; dCol <= 1; dCol++) {
                                        if (dCol != 0 || dRow != 0)
                                        {
                                            System.out.println("Checking...");
                                            //LinkedList<Button> neighbours = new LinkedList<Button>();
                                            // loop over the rows and cols multiplier:
                                            for (int mult = 1; mult < 8; mult++) {
                                                int currRow = rowFin + mult * dRow;
                                                int currCol = colFin + mult * dCol;
                                                if (engine.isValidMove(playerFlag, rowNumber, colNumber, board, rowFin, colFin, currRow, currCol)) {
                                                    //Button current;
                                                    int tempRow = currRow;
                                                    int tempCol = currCol;
                                                    while ((rowFin != tempRow && colFin != tempCol)
                                                            || (rowFin == currRow && colFin != tempCol)
                                                            || (rowFin != tempRow && colFin == currCol)) {
                                                        //current = (Button) rows[tempRow].getChildAt(tempCol);
                                                        moved = true;
//                                                        neighbours.add(current);
                                                        board[tempRow][tempCol] = color;
                                                        tempRow -= dRow;
                                                        tempCol -= dCol;
                                                    }

                                                }
                                            }
//                                            moved = moved || !neighbours.isEmpty();
//                                            for (Button b : neighbours) {
//                                                System.out.println("Changing colors of neighbour!");
//                                                b.setBackgroundColor(color);
//                                            }
                                        }
                                    }
                            }
                            if (moved)
                            {
                                board[rowFin][colFin] = color;
                                System.out.println("B: Changing the player!");
                                playerFlag = !playerFlag;
                                updateText(move, whiteScore, blackScore, rowNumber, rows);
                                updateTheBoard(playerFlag, rowNumber, colNumber, rows);
                            }
                        }

                    }
                });

            }
        }



    }
}
