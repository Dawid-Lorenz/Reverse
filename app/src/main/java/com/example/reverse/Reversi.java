package com.example.reverse;

import android.widget.ImageButton;

import java.util.ArrayList;

public class Reversi
{

    protected GameTree tree;

    /*
    0 - empty
    1 - white
    2 - black
    3 - available for white
    4 - available for black
    5 - available for both
     */

    enum State
    {
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
        else
        {
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
                if ((null == board[row1][col1] ||
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
                    if (currRow == row2 && currCol == col2)
                    {
//                        System.out.println("Target neighbours source!");
                        return false;
                    }
                    while ((currRow != row2 && currCol != col2) || (currCol != col2 && row1 == row2)
                            || (currRow != row2 && col1 == col2))
                    {
//                        System.out.println("[" + currRow + ", " + currCol + "]");
                        currState = board[currRow][currCol];
//                        System.out.println(currState);
                        if (opposite != currState)
                        {
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

        for (int row = 0; row < rowNumber; row++)
        {
            for (int col = 0; col < colNumber; col++)
            {
                if (board[row][col] != State.WHITE && board[row][col] != State.BLACK)
                    board[row][col] = null; // State.EMPTY
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

    /*
    protected GameTree.Node evaluationAdding(boolean player, GameTree.Node parent, int maxDepth)
    {
        return evaluationAdding(player, parent, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
    } */

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


    /*
    protected GameTree.Node evaluationAdding(boolean player, GameTree.Node parent, Reversi.State[][] board, int maxDepth, int alpha, int beta)
    {
        // TODO try gererating the tree each move!
        GameTree.Node child;
        if (parent.getDepth() <= 1 && maxDepth > 0)
        {
            ArrayList<int[]> possibleMoves = listPossibleMoves(player, parent);

            State[][] tempBoard;

            if (!possibleMoves.isEmpty())
            {

                if (player)
                {
                    parent.score = Integer.MIN_VALUE;
                }
                else
                {
                    parent.score = Integer.MAX_VALUE;
                }
                for (int i = 0; i < possibleMoves.size(); i++)
                {
                    child = new GameTree.Node();
                    tempBoard = copyArray(board);

                    for (int dRow = -1; dRow < 2; dRow++)
                        for (int dCol = -1; dCol < 2; dCol++)
                        {
                            if (dRow != 0 || dCol != 0)
                                for (int mult = 1; mult < 8; mult++)
                                {
                                    int currRow = possibleMoves.get(i)[0] + dRow * mult;
                                    int currCol = possibleMoves.get(i)[1] + dCol * mult;

                                    if (isValidMove(player, 8, 8, tempBoard,
                                            possibleMoves.get(i)[0], possibleMoves.get(i)[1], currRow, currCol))
                                    {
                                        int tempRow = currRow;
                                        int tempCol = currCol;
                                        while ((possibleMoves.get(i)[0] != tempRow && possibleMoves.get(i)[1] != tempCol)
                                                || (possibleMoves.get(i)[0] == currRow && possibleMoves.get(i)[1] != tempCol)
                                                || (possibleMoves.get(i)[0] != tempRow && possibleMoves.get(i)[1] == currCol))
                                        {
                                            tempBoard[tempRow][tempCol] = player ? State.WHITE : State.BLACK;
                                            tempRow -= dRow;
                                            tempCol -= dCol;
                                        }
                                    }
                                }
                        }

                    tempBoard[possibleMoves.get(i)[0]][possibleMoves.get(i)[1]] = player ? State.WHITE : State.BLACK;
                    tempBoard = copyArray(updateBoardState(player, tempBoard.length, tempBoard[0].length, tempBoard));

                    child.prevMove[0] = (byte) possibleMoves.get(i)[0];
                    child.prevMove[1] = (byte) possibleMoves.get(i)[1];

                    if (player) // TODO take out of the loop
                    {
                        child = evaluationAdding(false, parent.getChildren().get(i), tempBoard, maxDepth - 1, alpha, beta); // TODO child rather parent.get(i) ???

                        if (child.score > parent.score)
                        {
                            parent.bestMove[0] = child.prevMove[0];
                            parent.bestMove[1] = child.prevMove[1];
                            parent.score = child.score;
                        }

                        alpha = alpha > child.score ? alpha : child.score;

                        if (alpha >= beta)
                            break;
                    }
                    else
                    {
                        child = evaluationAdding(true, parent.getChildren().get(i), tempBoard, maxDepth - 1, alpha, beta);

                        if (child.score < parent.score)
                        {
                            parent.bestMove[0] = child.prevMove[0];
                            parent.bestMove[1] = child.prevMove[1];
                            parent.score = child.score;
                        }

                        beta = beta < child.score ? beta : child.score;

                        if (alpha >= beta)
                            break;
                    }

                    parent.add(child);

                }

                /*
                if (player)
                {
                    parent.score = Integer.MIN_VALUE;
                    for (int i = 0; i < parent.getChildren().size(); i++)
                    {
                        child = evaluationAdding(false, parent.getChildren().get(i), maxDepth - 1, alpha, beta);

                        if (child.score > parent.score)
                        {
                            parent.bestMove[0] = child.prevMove[0];
                            parent.bestMove[1] = child.prevMove[1];
                            parent.score = child.score;
                        }

                        alpha = alpha > child.score ? alpha : child.score;

                        if (alpha >= beta)
                            break;
                    }

                }
                else
                {
                    parent.score = Integer.MAX_VALUE;
                    for (int i = 0; i < parent.getChildren().size(); i++)
                    {
                        child = evaluationAdding(true, parent.getChildren().get(i), maxDepth - 1, alpha, beta);

                        if (child.score < parent.score)
                        {
                            parent.bestMove[0] = child.prevMove[0];
                            parent.bestMove[1] = child.prevMove[1];
                            parent.score = child.score;
                        }

                        beta = beta < child.score ? beta : child.score;

                        if (alpha >= beta)
                            break;
                    }
                }
                 */
                    /*
                    GameTree.Node tempNode = new GameTree.Node(child); // <- redundant???
                    int[] answer;
                    tempNode = new GameTree.Node(evaluationAdding(!player, tempNode, maxDepth - 1, alpha, beta));
                    if (player) {
                        tempNode.score = Integer.MIN_VALUE;
                        for (int j = 0; j < tempNode.getChildren().size(); j++) {

                            // THIS CALL MIGHT BE PROBLEMATIC!!!
                            answer = tempNode.getChildren().get(j).alphaBeta(!player, maxDepth - 1, alpha, beta);
                            if (answer[2] > tempNode.score) {
                                tempNode.score = answer[2];
                                tempNode.bestMove[0] = (byte) answer[0]; // = tempNode.getChildren().get(j).prevMove[0];
                                tempNode.bestMove[1] = (byte) answer[1]; // = tempNode.getChildren().get(j).prevMove[1];
                            }
                            alpha = alpha > tempNode.score ? alpha : tempNode.score;
                            if (beta <= alpha) {
                                break;
                            } else {
                                child.add(tempNode.getChildren().get(j));
                                child.score = tempNode.score;
                                child.bestMove[0] = tempNode.prevMove[0];
                                child.bestMove[1] = tempNode.prevMove[1];
                            }
                        }

                    } else {
                        tempNode.score = Integer.MAX_VALUE;
                        for (int j = 0; j < tempNode.getChildren().size(); j++) {
                            answer = tempNode.getChildren().get(j).alphaBeta(!player, maxDepth - 1, alpha, beta);
                            if (answer[2] < tempNode.score) {
                                tempNode.score = answer[2];
                                tempNode.bestMove[0] = tempNode.getChildren().get(j).prevMove[0];
                                tempNode.bestMove[1] = tempNode.getChildren().get(j).prevMove[1];
                            }
                            beta = beta < tempNode.score ? beta : tempNode.score;
                            if (beta <= alpha) {
                                break;
                            } else {
                                child.add(tempNode.getChildren().get(j));
                                child.score = tempNode.score;
                                child.bestMove[0] = tempNode.prevMove[0];
                                child.bestMove[1] = tempNode.prevMove[1];
                            }
                        }
                    }


            }
            else
            {
                child = new GameTree.Node();
                tempBoard = copyArray(updateBoardState(!player, parent.board.length, parent.board[0].length, parent.board));
                child.prevMove[0] = parent.bestMove[0] = -1;
                child.prevMove[1] = parent.bestMove[1] = -1;
                parent.add(child);
            }
        }
        else if (maxDepth <= 0)
        {
            parent.score = GameTree.Node.staticEvaluation(player, board);
        }
        else
        {
            for (int i = 0; i < parent.getChildren().size(); i++)
                evaluationAdding(!player, parent.getChildren().get(i), maxDepth - 1, alpha, beta);
            //TODO update alpha and beta here depending on the player!!!
        }


        return parent;
    }

    protected GameTree.Node addChildrenRecursively(boolean player, GameTree.Node parent, int maxDepth)
    {

        if (parent.getDepth() == 1)// && maxDepth > 0)
        {
            ArrayList<int[]> possibleMoves = listPossibleMoves(player, parent);

            GameTree.Node node;
            State[][] tempBoard;

            if (!possibleMoves.isEmpty())
                for (int i = 0; i < possibleMoves.size(); i++)
                {
                    node = new GameTree.Node();
                    tempBoard = copyArray(parent.board);

                    for (int dRow = -1; dRow < 2; dRow++)
                        for (int dCol = -1; dCol < 2; dCol++)
                        {
                            if (dRow != 0 || dCol != 0)
                                for (int mult = 1; mult < 8; mult++)
                                {
                                    int currRow = possibleMoves.get(i)[0] + dRow * mult;
                                    int currCol = possibleMoves.get(i)[1] + dCol * mult;

                                    if (isValidMove(player, 8, 8, tempBoard,
                                            possibleMoves.get(i)[0], possibleMoves.get(i)[1], currRow, currCol))
                                    {
                                        int tempRow = currRow;
                                        int tempCol = currCol;
                                        while ((possibleMoves.get(i)[0] != tempRow && possibleMoves.get(i)[1] != tempCol)
                                                || (possibleMoves.get(i)[0] == currRow && possibleMoves.get(i)[1] != tempCol)
                                                || (possibleMoves.get(i)[0] != tempRow && possibleMoves.get(i)[1] == currCol))
                                        {
                                            tempBoard[tempRow][tempCol] = player ? State.WHITE : State.BLACK;
                                            tempRow -= dRow;
                                            tempCol -= dCol;
                                        }
                                    }
                                }
                        }

                    tempBoard[possibleMoves.get(i)[0]][possibleMoves.get(i)[1]] = player ? State.WHITE : State.BLACK;
                    tempBoard = copyArray(updateBoardState(!player, tempBoard.length, tempBoard[0].length, tempBoard));

                    node.prevMove[0] = (byte) possibleMoves.get(i)[0];
                    node.prevMove[1] = (byte) possibleMoves.get(i)[1];
                    node.board = copyArray(tempBoard);
                    parent.add(node);
                }
            else
            {
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
        if (maxDepth != 0)
        {
            parent.childCount = 0;
            for (int i = 0; i < parent.getChildren().size(); i++)
            {
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

*/

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

    protected void initialiseGameTree(boolean player, State[][] board, int maxDepth)
    {
//        tree = new GameTree(board);

//        tree.root = addChildrenRecursively(player, tree.root, maxDepth);

//        tree.root = evaluationAdding(player, tree.root, maxDepth);
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
//        return tree.root.bestMove;
    }


    /*
    protected boolean madeMove(boolean player, int row, int col, int maxDepth)
    {


        if (tree.root.getDepth() <= maxDepth)
        {
//            tree.root = addChildrenRecursively(player, tree.root, maxDepth);
            tree.root = evaluationAdding(player, tree.root, maxDepth);
        }

        for (int i = 0; i < tree.root.getChildren().size(); i++)
        {
            if (tree.root.getChildren().get(i).prevMove[0] == row
                    && tree.root.getChildren().get(i).prevMove[1] == col)
            {
                tree.root = tree.root.getChildren().get(i);
                return true;
            }
        }
        return false;
    }
*/
}
