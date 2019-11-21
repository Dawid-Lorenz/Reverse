package com.example.reverse;

import java.util.ArrayList;

public class Reversi {

    protected GameTree tree;

    /*
    0 - empty
    1 - white
    2 - black
    3 - available for white
    4 - available for black
    5 - available for both
     */

    enum State {
        //EMPTY,
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
            if (col1 >= colNum || col2 >= colNum || col1 < 0 || col2 < 0)
            {
//                System.out.println("cols out of bounds");
                return false;
            }

            // the same button
            else if (row1 == row2 && col1 == col2)
                return false;
            else if ((Math.abs(row1 - row2) == Math.abs(col1 - col2)) || ((col1 == col2) || (row1 == row2)))
            {
                if ((   null == board[row1][col1] ||
                        //State.EMPTY == board[row1][col1] ||
                        State.AV_BLACK == board[row1][col1] ||
                        State.AV_WHITE == board[row1][col1]) && state == board[row2][col2])
                // ||
                //                        State.AV_BOTH == board[row1][col1]
                //      Possibly other AV states as well
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
                    if (null == board[row1][col1])
                            //State.EMPTY == board[row1][col1])
                        board[row1][col1] = player ? State.AV_WHITE : State.AV_BLACK;
                    /*
                    else if (State.AV_BLACK == board[row1][col1] && player ||
                            State.AV_WHITE == board[row1][col1] && !player)
                        board[row1][col1] = State.AV_BOTH; */
                    return true;
                }
            }
        }
//        System.out.println("unreachable");
        //unreachable statement
        return false;
    }

    protected State[][] updateBoardState(boolean player, int rowNumber, int colNumber, State[][] board)
    {

        for (int row = 0; row < rowNumber; row++) {
            for (int col = 0; col < colNumber; col++) {
                if (board[row][col] != State.WHITE && board[row][col] != State.BLACK)
                    board[row][col] = null; // State.EMPTY
            }
        }

        for (int row = 0; row < rowNumber; row++)
        {
            for (int col = 0; col < colNumber; col++)
            {
                for (int dRow = -1; dRow <= 1; dRow++)
                    for (int dCol = -1; dCol <= 1; dCol++) {
                        if (dCol != 0 || dRow != 0)
                            // loop over the rows and cols multiplier:
                            for (int mult = 1; mult < 8; mult++) {
                                int currRow = row + mult*dRow;
                                int currCol = col + mult*dCol;
                                //System.out.println("Current button: [" + (row + mult * dRow) + ", " + (col + mult * dCol) + "]");
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

    protected GameTree.Node evaluationAdding(boolean player, GameTree.Node parent, int maxDepth, int alpha, int beta)
    {
        if (parent.getDepth() == 1)
        {
            ArrayList<int[]> possibleMoves = listPossibleMoves(player, parent);

            GameTree.Node node;
            State[][] tempBoard;

            if (!possibleMoves.isEmpty())
                for (int i = 0; i < possibleMoves.size(); i++) {
                    node = new GameTree.Node();
                    tempBoard = copyArray(parent.board);

                    for (int dRow = -1; dRow < 2; dRow++)
                        for (int dCol = -1; dCol < 2; dCol++) {
                            if (dRow != 0 || dCol != 0)
                                for (int mult = 1; mult < 8; mult++) {
                                    int currRow = possibleMoves.get(i)[0] + dRow * mult;
                                    int currCol = possibleMoves.get(i)[1] + dCol * mult;

                                    if (isValidMove(player, 8, 8, tempBoard,
                                            possibleMoves.get(i)[0], possibleMoves.get(i)[1], currRow, currCol))
                                    {
                                        int tempRow = currRow;
                                        int tempCol = currCol;
                                        while ((possibleMoves.get(i)[0] != tempRow && possibleMoves.get(i)[1] != tempCol)
                                                || (possibleMoves.get(i)[0] == currRow && possibleMoves.get(i)[1] != tempCol)
                                                || (possibleMoves.get(i)[0] != tempRow && possibleMoves.get(i)[1] == currCol)) {
                                            tempBoard[tempRow][tempCol] = player ? State.WHITE : State.BLACK;
                                            tempRow -= dRow;
                                            tempCol -= dCol;
                                        }
                                    }
                                }
                        }

                    tempBoard[possibleMoves.get(i)[0]][possibleMoves.get(i)[1]] = player ? State.WHITE : State.BLACK;
                    tempBoard = copyArray(updateBoardState(!player, tempBoard.length, tempBoard[0].length, tempBoard));

                    node.prevMove[0] = (byte)possibleMoves.get(i)[0];
                    node.prevMove[1] = (byte)possibleMoves.get(i)[1];
                    node.board = copyArray(tempBoard);
                    if (maxDepth != 0) {
                        node = evaluationAdding(!player, node, maxDepth - 1, alpha, beta);
                        int[] answer = node.alphaBeta(!player, maxDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
                        node.bestMove[0] = (byte)answer[0];
                        node.bestMove[1] = (byte)answer[1];

                        // TODO prune off here!

                    }
                    parent.add(node);
                }
            else {
                node = new GameTree.Node();
                tempBoard = copyArray(updateBoardState(player, parent.board.length, parent.board[0].length, parent.board));
                node.prevMove[0] = -1;
                node.prevMove[1] = -1;
                node.board = copyArray(tempBoard);
                parent.add(node);
            }

//            parent.board = null;
            //        ArrayList<GameTree.Node> newChildren = new ArrayList<>();

        }



        return parent;
    }

    protected GameTree.Node addChildrenRecursively(boolean player, GameTree.Node parent, int maxDepth)
    {

        if (parent.getDepth() == 1)
        {
            ArrayList<int[]> possibleMoves = listPossibleMoves(player, parent);

            GameTree.Node node;
            State[][] tempBoard;

            if (!possibleMoves.isEmpty())
                for (int i = 0; i < possibleMoves.size(); i++) {
                    node = new GameTree.Node();
                    tempBoard = copyArray(parent.board);

                    for (int dRow = -1; dRow < 2; dRow++)
                        for (int dCol = -1; dCol < 2; dCol++) {
                            if (dRow != 0 || dCol != 0)
                                for (int mult = 1; mult < 8; mult++) {
                                    int currRow = possibleMoves.get(i)[0] + dRow * mult;
                                    int currCol = possibleMoves.get(i)[1] + dCol * mult;

                                    if (isValidMove(player, 8, 8, tempBoard,
                                            possibleMoves.get(i)[0], possibleMoves.get(i)[1], currRow, currCol))
                                    {
                                        int tempRow = currRow;
                                        int tempCol = currCol;
                                        while ((possibleMoves.get(i)[0] != tempRow && possibleMoves.get(i)[1] != tempCol)
                                                || (possibleMoves.get(i)[0] == currRow && possibleMoves.get(i)[1] != tempCol)
                                                || (possibleMoves.get(i)[0] != tempRow && possibleMoves.get(i)[1] == currCol)) {
                                            tempBoard[tempRow][tempCol] = player ? State.WHITE : State.BLACK;
                                            tempRow -= dRow;
                                            tempCol -= dCol;
                                        }
                                    }
                                }
                        }

                    tempBoard[possibleMoves.get(i)[0]][possibleMoves.get(i)[1]] = player ? State.WHITE : State.BLACK;
                    tempBoard = copyArray(updateBoardState(!player, tempBoard.length, tempBoard[0].length, tempBoard));

                    node.prevMove[0] = (byte)possibleMoves.get(i)[0];
                    node.prevMove[1] = (byte)possibleMoves.get(i)[1];
                    node.board = copyArray(tempBoard);
                    parent.add(node);
                }
            else {
                node = new GameTree.Node();
                tempBoard = copyArray(updateBoardState(player, parent.board.length, parent.board[0].length, parent.board));
                node.prevMove[0] = -1;
                node.prevMove[1] = -1;
                node.board = copyArray(tempBoard);
                parent.add(node);
            }

//            parent.board = null;
    //        ArrayList<GameTree.Node> newChildren = new ArrayList<>();

        }
        if (maxDepth != 0) {
            parent.childCount = 0;
            for (int i = 0; i < parent.getChildren().size(); i++) {
                if (maxDepth < 0)
                    parent.getChildren().set(i, addChildrenRecursively(!player, parent.getChildren().get(i), maxDepth));

                else if (maxDepth > 0)
                    if (parent.getChildren().size() <= 9 || maxDepth == 1)
                        parent.getChildren().set(i, addChildrenRecursively(!player, parent.getChildren().get(i), maxDepth - 1));
                    else
                        parent.getChildren().set(i, addChildrenRecursively(!player, parent.getChildren().get(i), 1));

                parent.childCount += parent.getChildren().get(i).getChildren().size() + 1;
            }
        }


        return parent;
    }


    protected void initialiseGameTree(boolean player, State[][] board, int maxDepth)
    {
        tree = new GameTree(board);

        tree.root = addChildrenRecursively(player, tree.root, maxDepth);

    }

    protected ArrayList<int[]> listPossibleMoves(boolean player, GameTree.Node node)
    {
        ArrayList<int []> possibleMoves = new ArrayList<>();

        node.board = copyArray(updateBoardState(player, 8, 8, node.board));

        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                int[] pair = {row, col};
                if (player && node.board[row][col] == State.AV_WHITE)
                    possibleMoves.add(pair);
                else if (!player && node.board[row][col] == State.AV_BLACK)
                    possibleMoves.add(pair);

            }
        }

        return possibleMoves;
    }


    protected byte[] chooseTheBestMove(boolean player, int maxDepth)
    {
        /*
        int[] returned = tree.root.alphaBeta(player, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);

        int[] coords = {returned[0], returned[1]}; */
        return tree.root.bestMove;
    }

    protected boolean madeMove(boolean player, int row, int col, int maxDepth)
    {
        if (tree.root.getDepth() <= maxDepth)
        {
            tree.root = addChildrenRecursively(player, tree.root, maxDepth);
        }

        for (int i = 0; i < tree.root.getChildren().size(); i++)
        {
            if (tree.root.getChildren().get(i).prevMove[0] == row
            &&  tree.root.getChildren().get(i).prevMove[1] == col)
            {
                tree.root = tree.root.getChildren().get(i);
                return true;
            }
        }
        return false;
    }

}
