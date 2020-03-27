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
        boolean thereAreNoAvs = true;

        int whites = 0;
        int blacks = 0;

        board = engine.updateBoardState(player, rowNumber, colNumber, board).clone();

        for (int row = 0; row < rowNumber; row++)
        {
            for (int col = 0; col < colNumber; col++)
            {
                ImageButton btn = (ImageButton) rows[row].getChildAt(col);

                if (board[row][col] == null)
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
    private boolean hasPossibleMoves(boolean player, int rowNum, TableRow[] rows)
    {
        boolean hasMoves = false;

        for (int row = 0; row < rowNum; row++)
        {
            int colNum = rows[row].getChildCount();
            for (int col = 0; col < colNum; col++) {
                if(player && board[row][col] == State.AV_WHITE)
                    return true;
                else if(!player && board[row][col] == State.AV_BLACK)
                    return true;

            }
        }
        return hasMoves;
    }

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
                            {
                            System.out.println("Button is correct");
                                // looping over up-down directions:
                                for (int dRow = -1; dRow <= 1; dRow++)
                                    for (int dCol = -1; dCol <= 1; dCol++) {
                                        if (dCol != 0 || dRow != 0)
                                        {
                                            System.out.println("Checking...");
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
