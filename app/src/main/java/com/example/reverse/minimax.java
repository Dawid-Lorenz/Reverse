package com.example.reverse;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class minimax extends AppCompatActivity {

    int white, black, empty, available;
    int movesAhead = 2;
    boolean playerFlag;
    Reversi.State[][] board;

    Reversi engine;


    private void updateTheBoard(boolean player, int rowNumber, int colNumber, TableRow[] rows)
    {

                // change UI elements here

            board = engine.updateBoardState(player, rowNumber, colNumber, board).clone();

            for (int row = 0; row < rowNumber; row++)
            {
                for (int col = 0; col < colNumber; col++)
                {
                    ImageButton btn = (ImageButton) rows[row].getChildAt(col);

                    if (board[row][col] == null) // == Reversi.State.EMPTY)
                        btn.setImageResource(empty);

                    else if (board[row][col] == Reversi.State.WHITE)
                    {
                        btn.setImageResource(white);
                    }
                    else if (board[row][col] == Reversi.State.BLACK)
                    {
                        btn.setImageResource(black);
                    }
                    else if ((player && (board[row][col] == Reversi.State.AV_WHITE))
                            || (!player && (board[row][col] == Reversi.State.AV_BLACK)))
                    {
                        btn.setImageResource(available);
                    }

                }
            }


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
                if (Reversi.State.WHITE == board[i][j])
                    whites++;
                else if (Reversi.State.BLACK == board[i][j])
                    blacks++;
            }
        }

        whiteScore.setText("White: " + whites);
        blackScore.setText("Black: " + blacks);

    }

    private boolean hasPossibleMoves(boolean player, int rowNum, TableRow[] rows)
    {
//        System.out.println("Checking for moves of " + (player ? "white" : "black"));
        boolean hasMoves = false;

        for (int row = 0; row < rowNum; row++)
        {
            int colNum = rows[row].getChildCount();
            for (int col = 0; col < colNum; col++) {
                if(player && board[row][col] == Reversi.State.AV_WHITE)
                    return true;
                else if(!player && board[row][col] == Reversi.State.AV_BLACK)
                    return true;

            }
        }
        return hasMoves;
    }

    private boolean makeMove(int prevRow, int prevCol, int rowNum, int colNum, TableRow[] rows)
    {
//        if(!engine.madeMove(playerFlag, prevRow, prevCol, movesAhead))
//            System.err.println("That move: " + prevRow + ", " + prevCol + " seemed illegal!");
        byte[] move = engine.chooseTheBestMove(false, board, movesAhead);
        if (move[0] == -1 && move[1] == -1)
        {
//            engine.madeMove(false, move[0], move[1], movesAhead);
            return false;
        }
        else
        {
            boolean moved = false;
            for(int dRow = -1; dRow <= 1; dRow++)
                for (int dCol = -1; dCol <= 1; dCol++)
                    if (dRow != 0 || dCol != 0)
                        for (int mult = 1; mult < 8; mult++)
                        {
                            int currRow = move[0] + mult * dRow;
                            int currCol = move[1] + mult * dCol;
                            if (engine.isValidMove(false, rowNum, colNum, board, move[0], move[1], currRow, currCol)) {
                                //Button current;
                                int tempRow = currRow;
                                int tempCol = currCol;
                                while ((move[0] != tempRow && move[1] != tempCol)
                                        || (move[0] == currRow && move[1] != tempCol)
                                        || (move[0] != tempRow && move[1] == currCol)) {
                                    moved = true;
//                                                        neighbours.add(current);
                                    board[tempRow][tempCol] = Reversi.State.BLACK;
                                    tempRow -= dRow;
                                    tempCol -= dCol;
                                }


                            }
                        }

            if (moved)
                board[move[0]][move[1]] = Reversi.State.BLACK;
//            if(!engine.madeMove(false, move[0], move[1], movesAhead))
//                System.err.println("That move: " + move[0] + ", " + move[1] + " seemed illegal!");
            return true;
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        white = R.drawable.white;
        black = R.drawable.black;
        empty = R.drawable.empty;
        available = R.drawable.available;

        playerFlag = true;

        board = new Reversi.State[8][8];
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                board[i][j] = null; // Reversi.State.EMPTY;
        board[3][3] = board[4][4] = Reversi.State.WHITE;
        board[3][4] = board[4][3] = Reversi.State.BLACK;

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


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("How many moves ahead? (8 is optimal");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Put actions for OK button here
                movesAhead = Integer.parseInt(input.getText().toString());
            }
        });

        alert.show();


        engine.initialiseGameTree(playerFlag, board, movesAhead);
        for(int row = 0; row < rowNumber; row++){
            final int rowFin = row;
            final int colNumber = rows[row].getChildCount();
            for (int col = 0; col < colNumber; col++) {
                final int colFin = col;
                final ImageButton btn = (ImageButton) rows[row].getChildAt(col);


                //TODO refactor into this:
                btn.setOnTouchListener(new View.OnTouchListener() {

                boolean moved = false;
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Reversi.State color = playerFlag ? Reversi.State.WHITE : Reversi.State.BLACK;
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            boolean hasPossibleMoves = hasPossibleMoves(playerFlag, rowNumber, rows);

                            if (!hasPossibleMoves) {
                                CharSequence message = "White has no possible moves!";
                                Toast popup = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                                updateText(move, whiteScore, blackScore, rowNumber, rows);
                                updateTheBoard(!playerFlag, rowNumber, colNumber, rows);
                                popup.show();
                                if (!makeMove(-1, -1, rowNumber, colNumber, rows)) {

                                    int whites = 0;
                                    int blacks = 0;
                                    for (int i = 0; i < rowNumber; i++)
                                        for (int j = 0; j < colNumber; j++)
                                            if (board[i][j] == Reversi.State.WHITE)
                                                whites++;
                                            else if (board[i][j] == Reversi.State.BLACK)
                                                blacks++;

                                    if (whites > blacks)
                                        message = "White has won!";
                                    else if (whites < blacks)
                                        message = "Black has won!";
                                    else
                                        message = "It's a tie!";

                                    new AlertDialog.Builder(minimax.this)
                                            .setTitle("Game over")
                                            .setMessage(message)
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    startActivity(new Intent(minimax.this, MainActivity.class));
                                                }
                                            })
                                            .show();

                                } else {
                                    updateText(move, whiteScore, blackScore, rowNumber, rows);
                                    updateTheBoard(playerFlag, rowNumber, colNumber, rows);
                                }
                            } else {
//                                System.out.println("Button: " + rowFin + ", " + colFin);
//                                System.out.println("State: " + board[rowFin][colFin]);
                                Reversi.State btnState = board[rowFin][colFin];
                                if (
                                        btnState == Reversi.State.AV_WHITE && playerFlag ||
                                                btnState == Reversi.State.AV_BLACK && !playerFlag)
                                   // || btnState == Reversi.State.AV_BOTH)
                                {
//                                    System.out.println("Button is correct");
                                    // looping over up-down directions:
                                    for (int dRow = -1; dRow <= 1; dRow++)
                                        for (int dCol = -1; dCol <= 1; dCol++) {
                                            if (dCol != 0 || dRow != 0) {
//                                                System.out.println("Checking...");
                                                //LinkedList<Button> neighbours = new LinkedList<Button>();
                                                // loop over the rows and cols multiplier:
                                                for (int mult = 1; mult < 8; mult++) {
                                                    int currRow = rowFin + mult * dRow;
                                                    int currCol = colFin + mult * dCol;
                                                    if (engine.isValidMove(playerFlag, rowNumber, colNumber, board, rowFin, colFin, currRow, currCol)) {
                                                        //Button current;
                                                        int tempRow = currRow;
                                                        int tempCol = currCol;
                                                        moved = true;
                                                        while ((rowFin != tempRow && colFin != tempCol)
                                                                || (rowFin == currRow && colFin != tempCol)
                                                                || (rowFin != tempRow && colFin == currCol)) {
                                                            //current = (Button) rows[tempRow].getChildAt(tempCol);
//                                                        neighbours.add(current);
                                                            board[tempRow][tempCol] = color;
                                                            tempRow -= dRow;
                                                            tempCol -= dCol;
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                }

                            }
                        }
                        else if (event.getAction() == MotionEvent.ACTION_UP)
                        {
                            if (moved)
                            {
                                board[rowFin][colFin] = color;
//                                System.out.println("B: Changing the player!");
//                                playerFlag = !playerFlag;
                                updateText(move, whiteScore, blackScore, rowNumber, rows);
                                updateTheBoard(!playerFlag, rowNumber, colNumber, rows);

                                if(!makeMove(rowFin, colFin, rowNumber, colNumber, rows)) {
                                    CharSequence message = "Black has no moves!";
                                    Toast popup = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                                    popup.show();
                                }
                                updateText(move, whiteScore, blackScore, rowNumber, rows);
                                updateTheBoard(playerFlag, rowNumber, colNumber, rows);

                            }
                            moved = false;

                        }
                        return true;
                    }


               });

                /*
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Reversi.State color = playerFlag ? Reversi.State.WHITE : Reversi.State.BLACK;
                        boolean hasPossibleMoves = hasPossibleMoves(playerFlag, rowNumber, rows);

                        System.out.println("On click!");
                        System.out.println("hPm = " + hasPossibleMoves);

                        if (!hasPossibleMoves)
                        {
//                            CharSequence message = playerFlag ? "White has no possible moves!": "Black has no possible moves!";
                            CharSequence message = "White has no possible moves!";
                            Toast popup = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT );
//                            System.out.println("A: Changing the player!");
//                            playerFlag = !playerFlag;
                            updateText(move, whiteScore, blackScore, rowNumber, rows);
                            updateTheBoard(!playerFlag, rowNumber, colNumber, rows);
                            popup.show();
                            if(!makeMove(-1, -1,rowNumber, colNumber, rows))
                            {

                                int whites = 0; int blacks = 0;
                                for (int i = 0; i < rowNumber; i++)
                                    for (int j = 0; j < colNumber; j++)
                                        if (board[i][j] == Reversi.State.WHITE)
                                            whites++;
                                        else if (board[i][j] == Reversi.State.BLACK)
                                            blacks++;

                                if (whites > blacks)
                                    message = "White has won!";
                                else if (whites < blacks)
                                    message = "Black has won!";
                                else
                                    message = "It's a tie!";

                                new AlertDialog.Builder(minimax.this)
                                        .setTitle("Game over")
                                        .setMessage(message)
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Continue with delete operation
                                                startActivity(new Intent(minimax.this, MainActivity.class));
                                            }
                                        })
                                        .show();

                            }
                            else
                            {
                                updateText(move, whiteScore, blackScore, rowNumber, rows);
                                updateTheBoard(playerFlag, rowNumber, colNumber, rows);
                            }
                        }
                        else
                        {
                            System.out.println("Button: " + rowFin + ", " + colFin);
                            System.out.println("State: " + board[rowFin][colFin]);
                            Reversi.State btnState = board[rowFin][colFin];
                            boolean moved = false;
                            if (
                                    btnState == Reversi.State.AV_WHITE && playerFlag ||
                                            btnState == Reversi.State.AV_BLACK && !playerFlag ||
                                            btnState == Reversi.State.AV_BOTH)
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
                                        }
                                    }
                            }
                            if (moved)
                            {
                                board[rowFin][colFin] = color;
//                                System.out.println("B: Changing the player!");
//                                playerFlag = !playerFlag;
                                updateText(move, whiteScore, blackScore, rowNumber, rows);
                                updateTheBoard(!playerFlag, rowNumber, colNumber, rows);

                                CharSequence message = "Thinking...";
                                Toast thinkToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT );
                                thinkToast.show();

                                if(!makeMove(rowFin, colFin, rowNumber, colNumber, rows)) {
                                    message = "Black has no moves!";
                                    Toast popup = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                                    popup.show();
                                }
                                updateText(move, whiteScore, blackScore, rowNumber, rows);
                                updateTheBoard(playerFlag, rowNumber, colNumber, rows);

                            }
                        }

                    }
                });

                */
            }
        }

    }
}
