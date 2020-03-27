package com.example.reverse;

import java.util.ArrayList;



public class GameTree {
    public Node root;
    public Reversi.State[][] board;

    public GameTree(Reversi.State[][] rootData) {
        root = new Node();
        board = copyArray(rootData);
        root.children = new ArrayList<Node>();
    }

    private static Reversi.State[][] copyArray(Reversi.State[][] source)
    {
        Reversi.State[][] target = new Reversi.State[source.length][source[0].length];
        for (int i = 0; i < source.length; i++)
            for (int j = 0; j < source[0].length; j++)
                target[i][j] = source[i][j];

        return target;
    }

    public static class Node {
        public byte[] prevMove = new byte[2];
        public byte[] bestMove = {-1, -1};
        public int score;
        protected byte childCount;
        private ArrayList<Node> children;
        private static int[][] weights = {{30, -3, 11, 8, 8, 11, -3, 30},
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

        public Node(Node n)
        {
            prevMove[0] = n.prevMove[0];
            prevMove[1] = n.prevMove[1];
            bestMove[0] = n.bestMove[0];
            bestMove[1] = n.bestMove[1];
            score = n.score;
            children = new ArrayList<>(n.children);
        }

        public ArrayList<Node> getChildren()
        {
            return children;
        }

        public void add(Node node)
        {
            children.add(node);
        }

        public static int staticEvaluation(boolean player, Reversi.State[][] staticBoard)
        {
            int staticscore = 0;
            double whites = 0.0;
            double blacks = 0.0;
            int tempScore = 0;
            for (int i = 0; i < 8; i++)
                for (int j = 0; j < 8; j++) {
                    if (staticBoard[i][j] == Reversi.State.WHITE)
                    {
                        whites++;
                        tempScore += weights[i][j];
                    }
                    else if (staticBoard[i][j] == Reversi.State.BLACK)
                    {
                        blacks++;
                        tempScore -= weights[i][j];
                    }
                }

            if (whites + blacks == 64)
                staticscore = (int) (whites - blacks);
            else
                staticscore = tempScore;

            staticscore += (int) 100.0*(whites - blacks)/(whites + blacks);

            return staticscore;
        }

        public int getDepth()
        {
            if (children.isEmpty())
                return 1;
            else
            {
                int depth = 1;
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
