package com.example.reverse;

import java.util.ArrayList;



public class GameTree {
    public Node root;

    public GameTree(Reversi.State[][] rootData) {
        root = new Node();
        root.board = copyArray(rootData);
        root.children = new ArrayList<Node>();
        root.depth = 0;
    }

    protected Reversi.State[][] copyArray(Reversi.State[][] source)
    {
        Reversi.State[][] target = new Reversi.State[source.length][source[0].length];
        for (int i = 0; i < source.length; i++)
            for (int j = 0; j < source[0].length; j++)
                target[i][j] = source[i][j];

        return target;
    }

    public static class Node {
        public Reversi.State[][] board;
        public byte[] prevMove = new byte[2];
        public byte[] bestMove = {-1, -1};
        private int score;
        private byte depth;
        protected byte childCount;
//        private Node parent;
        private ArrayList<Node> children;
        private int[][] weights = {{30, -3, 11, 8, 8, 11, -3, 30},
            {-3, -7, -4, 1, 1, -4, -7, -3},
            {11, -4, 2, 2, 2, 2, -4, 11},
            {8, 1, 2, -3, -3, 2, 1, 8},
            {8, 1, 2, -3, -3, 2, 1, 8},
            {11, -4, 2, 2, 2, 2, -4, 11},
            {-3, -7, -4, 1, 1, -4, -7, -3},
            {30, -3, 11, 8, 8, 11, -3, 30}};;

        public Node()
        {
            childCount = 0;
            children = new ArrayList<>();
//            weights = new int[8][8];
            /*
            for (int i = 0; i < 8; i++)
                for (int j = 0; j < 8; j++)
                    if ((i == 0 || i == 7) && j == 0 ||
                            (i == 0 || i == 7) && j == 7)
                        weights[i][j] = 100;
                    else if ((i == 0 || i == 7) && (j == 1 || j == 6) ||
                            (i == 1 || i == 6) && (j <= 1 || j >= 6))
                        weights[i][j] = -50;
                    else if (i == 0 || j == 0 || i == 7 || j == 7)
                        weights[i][j] = 2;
                    else
                        weights[i][j] = 0;

             */
        }

        public ArrayList<Node> getChildren()
        {
            return children;
        }

        public void add(Node node)
        {
            children.add(node);
        }


        // ESSENTIALLY MINIMAX HERE!!!
        public int[] getScore(boolean player, int maxDepth)
        {
            int[] returned;
            int[] coords = {-1, -1};
            if (maxDepth == 0 || children.isEmpty())
            {
                score = 0;
                double whites = 0.0;
                double blacks = 0.0;
                int tempScore = 0;
                boolean isEnd = true;
                for (int i = 0; i < 8; i++)
                    for (int j = 0; j < 8; j++) {
                        if (board[i][j] == Reversi.State.WHITE)
                        {
                            whites++;
                            if (player)
                                tempScore += weights[i][j];
                            else
                                tempScore -= weights[i][j];
                        }
                        else if (board[i][j] == Reversi.State.BLACK)
                        {
                            blacks++;
                            if (player)
                                tempScore -= weights[i][j];
                            else
                                tempScore += weights[i][j];
                        }
                    }

                if (whites + blacks == 64)
                    if (player)
                        score = (int) (whites - blacks);
                    else
                        score = (int) (blacks - whites);
                else
                    score = tempScore;

                if (player)
                    score += (int) 100.0*(whites - blacks)/(whites + blacks);
                else
                    score += (int) 100.0*(blacks - whites)/(whites + blacks);
                coords[0] = prevMove[0];
                coords[1] = prevMove[1];
            }
            else if (maxDepth > 0)
            {
                returned = children.get(0).getScore(!player, maxDepth - 1);
                score = returned[2];
                coords[0] = children.get(0).prevMove[0];
                coords[1] = children.get(0).prevMove[1];
                for (int i = 1; i < children.size(); i++)
                {
                    returned = children.get(i).getScore(!player, maxDepth - 1);
                    if (player && returned[2] > score || !player && returned[2] < score) {
                        score = returned[2];
                        coords[0] = children.get(i).prevMove[0];
                        coords[1] = children.get(i).prevMove[1];
                    }
                }
            }
            int[] answer = {coords[0], coords[1], score};
            return answer;
        }

        // TODO finish changing to Alfa Beta pruning:
        public int[] alphaBeta(boolean player, int maxDepth, int alpha, int beta)
        {
            int[] returned;
            int[] coords = {-1, -1};
            if (maxDepth == 0 || children.isEmpty())
            {
                score = 0;
                double whites = 0.0;
                double blacks = 0.0;
                int tempScore = 0;
                for (int i = 0; i < 8; i++)
                    for (int j = 0; j < 8; j++) {
                        if (board[i][j] == Reversi.State.WHITE)
                        {
                            whites++;
                            tempScore += weights[i][j];
                        }
                        else if (board[i][j] == Reversi.State.BLACK)
                        {
                            blacks++;
                            tempScore -= weights[i][j];
                        }
                    }

                if (whites + blacks == 64.0) // TODO the game doesn't always end here!
                    score = (int) (whites - blacks);
                else {
                    score = tempScore;

                    if (player)
                        score += (int) 100.0 * (whites - blacks) / (whites + blacks);
                    else
                        score += (int) 100.0 * (blacks - whites) / (whites + blacks);

                    coords[0] = prevMove[0];
                    coords[1] = prevMove[1];
                }
            }
            else if (maxDepth > 0)
            {
                if (player) {
                   score = Integer.MIN_VALUE;
                    for (int i = 0; i < children.size(); i++) {
                        returned = children.get(i).alphaBeta(!player, maxDepth - 1, alpha, beta);
                        if (returned[2] > score) {
                            score = returned[2];
                            coords[0] = children.get(i).prevMove[0];
                            coords[1] = children.get(i).prevMove[1];
                        }
                        alpha = alpha > score ? alpha : score;
                        if (beta <= alpha)
                            return returned;
                    }
                }
                else
                {
                    score = Integer.MAX_VALUE;
                    for (int i = 0; i < children.size(); i++) {
                        returned = children.get(i).alphaBeta(!player, maxDepth - 1, alpha, beta);
                        if (returned[2] < score) {
                            score = returned[2];
                            coords[0] = children.get(i).prevMove[0];
                            coords[1] = children.get(i).prevMove[1];
                        }
                        beta = beta < score ? beta : score;
                        if (beta <= alpha)
                            return returned;
                    }
                }
            }
            int[] answer = {coords[0], coords[1], score};
            return answer;
        }

        public byte getDepth()
        {
            if (children.isEmpty())
                return 1;
            else
            {
                depth = 1;
                int maxDepth = 0;
                for (int i = 0; i < children.size(); i++)
                    if (maxDepth < children.get(i).getDepth())
                        maxDepth = children.get(i).getDepth();

                depth += maxDepth;
                return depth;
            }
        }


    }


}
