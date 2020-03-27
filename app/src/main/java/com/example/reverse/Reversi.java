package com.example.reverse;

import android.widget.ImageButton;

import java.util.ArrayList;

public class Reversi
{

    protected GameTree tree;

    enum State
    {
        WHITE,
        BLACK,
        AV_WHITE,
        AV_BLACK
    }

    public Reversi()
    {

    }

    protected State[][] copyArray(State[][] source)
    {
        State[][] target = new State[source.length][source[0].length];
        for (int i = 0; i < source.length; i++)
            for (int j = 0; j < source[0].length; j++)
                target[i][j] = source[i][j];

        return target;
    }

    protected boolean isValidMove(boolean player, int rowNum, int colNum, State[][] board,
                                  int row1, int col1, int row2, int col2)
    {
        State state = player ? State.WHITE : State.BLACK;
        if (row1 >= rowNum || row2 >= rowNum || row1 < 0 || row2 < 0)
        {
            return false;
        }
        else
        {
            if (col1 >= colNum || col2 >= colNum || col1 < 0 || col2 < 0)
            {
                return false;
            }

            // the same button
            else if (row1 == row2 && col1 == col2)
                return false;
            else if ((Math.abs(row1 - row2) == Math.abs(col1 - col2)) || ((col1 == col2) || (row1 == row2)))
            {
                if ((null == board[row1][col1] ||
                        State.AV_BLACK == board[row1][col1] ||
                        State.AV_WHITE == board[row1][col1]) && state == board[row2][col2])
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
                    if (currRow == row2 && currCol == col2)
                    {
                        return false;
                    }
                    while ((currRow != row2 && currCol != col2) || (currCol != col2 && row1 == row2)
                            || (currRow != row2 && col1 == col2))
                    {
                        currState = board[currRow][currCol];
                        if (opposite != currState)
                        {
                            return false;
                        }

                        currRow += dRow;
                        currCol += dCol;
                    }
                    if (null == board[row1][col1])
                        board[row1][col1] = player ? State.AV_WHITE : State.AV_BLACK;
                    return true;
                }
            }
        }
        //unreachable statement
        return false;
    }

    protected State[][] updateBoardState(boolean player, int rowNumber, int colNumber, State[][] board)
    {

        for (int row = 0; row < rowNumber; row++)
        {
            for (int col = 0; col < colNumber; col++)
            {
                if (board[row][col] != State.WHITE && board[row][col] != State.BLACK)
                    board[row][col] = null;
            }
        }

        for (int row = 0; row < rowNumber; row++)
        {
            for (int col = 0; col < colNumber; col++)
            {
                for (int dRow = -1; dRow <= 1; dRow++)
                    for (int dCol = -1; dCol <= 1; dCol++)
                    {
                        if (dCol != 0 || dRow != 0)
                            // loop over the rows and cols multiplier:
                            for (int mult = 1; mult < 8; mult++)
                            {
                                int currRow = row + mult * dRow;
                                int currCol = col + mult * dCol;
                                if (isValidMove(player, rowNumber, colNumber, board, row, col, currRow, currCol))
                                {
                                    // possible moves based on white and black pos'
                                    if (board[row][col] == null) // == State.EMPTY)
                                        if (player)
                                            board[row][col] = State.AV_WHITE;
                                        else
                                            board[row][col] = State.AV_BLACK;
                                    else
                                        break;
                                }
                                else if (currCol > colNumber || currRow > rowNumber || currCol < 0 || currRow < 0)
                                    break;
                            }
                    }

            }
        }

        return board;
    }

    private State[][] applyMove(boolean player, State[][] board, int x, int y)
    {
        board = copyArray(board);

        for (int dRow = -1; dRow < 2; dRow++)
            for (int dCol = -1; dCol < 2; dCol++)
            {
                if (dRow != 0 || dCol != 0)
                    for (int mult = 1; mult < 8; mult++)
                    {
                        int currRow = x + dRow * mult;
                        int currCol = y + dCol * mult;

                        if (isValidMove(player, 8, 8, board,
                                x, y, currRow, currCol))
                        {
                            int tempRow = currRow;
                            int tempCol = currCol;
                            while ((x != tempRow && y != tempCol)
                                    || (x == currRow && y != tempCol)
                                    || (x != tempRow && y == currCol))
                            {
                                board[tempRow][tempCol] = player ? State.WHITE : State.BLACK;
                                tempRow -= dRow;
                                tempCol -= dCol;
                            }
                        }
                    }
            }

        board[x][y] = player ? State.WHITE : State.BLACK;

        return board;
    }

    private int[] alphaBeta(boolean player, Reversi.State[][] board, int maxDepth, int alpha, int beta)
    {
        if (maxDepth == 0)
        {
            int[] answer = {-1, -1, GameTree.Node.staticEvaluation(player, board)};
            return answer;
        }
        else if (player)
        {

            ArrayList<int[]> moves = listPossibleMoves(player, board);
            int size = moves.size();

            int[] answer = {-1, -1, Integer.MIN_VALUE};
            int[] returned;

            for (int[] move: moves)
            {
                Reversi.State[][] nextBoard = applyMove(player, board, move[0], move[1]);
                if (maxDepth * size > 50)
                    returned = alphaBeta(!player, nextBoard, maxDepth/2, alpha, beta);
                else
                    returned = alphaBeta(!player, nextBoard, maxDepth - 1, alpha, beta);
                if (answer[2] < returned[2])
                {
                    answer[0] = move[0];
                    answer[1] = move[1];
                    answer[2] = returned[2];
                }

                if (alpha < returned[2])
                    alpha = returned[2];

                if (alpha >= beta)
                    break;
            }

            return answer;
        }
        else
        {
            ArrayList<int[]> moves = listPossibleMoves(player, board);
            int size = moves.size();

            int[] answer = {-1, -1, Integer.MAX_VALUE};
            int[] returned;

            for (int[] move: moves)
            {
                Reversi.State[][] nextBoard = applyMove(player, board, move[0], move[1]);
                if (maxDepth * size > 50)
                    returned = alphaBeta(!player, nextBoard, maxDepth/2, alpha, beta);
                else
                    returned = alphaBeta(!player, nextBoard, maxDepth - 1, alpha, beta);
                if (answer[2] > returned[2])
                {
                    answer[0] = move[0];
                    answer[1] = move[1];
                    answer[2] = returned[2];
                }

                if (beta > returned[2])
                    beta = returned[2];

                if (alpha >= beta)
                    break;
            }

            return answer;
        }
    }

    protected ArrayList<int[]> listPossibleMoves(boolean player, Reversi.State[][] board)
    {
        ArrayList<int[]> possibleMoves = new ArrayList<>();

        board = copyArray(updateBoardState(player, 8, 8, board));

        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                int[] pair = {row, col};
                if (player && board[row][col] == State.AV_WHITE)
                    possibleMoves.add(pair);
                else if (!player && board[row][col] == State.AV_BLACK)
                    possibleMoves.add(pair);

            }
        }

        return possibleMoves;
    }


    protected byte[] chooseTheBestMove(boolean player, Reversi.State[][] board, int maxDepth)
    {

        int[] returned = alphaBeta(player, board, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);

        byte[] coords = {(byte)returned[0], (byte)returned[1]};

        return coords;
    }
}
