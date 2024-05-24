package com.example.poddavki_project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ai {
    public Model model;
    public Presenter app;
    public static HashMap<String, List<Coordinate>> openingBook;
    public boolean usedOpening = false;

    static
    {
        openingBook = new HashMap<>(); // Initialize the HashMap

        // Case 1
        List<Coordinate> resultMoves = new ArrayList<>(); // Create a list of moves
        resultMoves.add(new Coordinate(3, 2,2, 3)); // Add the moves to the list
        openingBook.put("6172838606831353856,11163050", resultMoves); // Add the opening and its moves to the book

        // Case 2
        resultMoves = new ArrayList<>(); // Create a list of moves
        resultMoves.add(new Coordinate(3, 2, 2, 3)); // Add the moves to the list
        openingBook.put("6172835308296470528,11163050", resultMoves); // Add the opening and its moves to the book

        // Case 3
        resultMoves = new ArrayList<>(); // Create a list of moves
        resultMoves.add(new Coordinate(3, 4,2, 5)); // Add the moves to the list
        openingBook.put("6172835334066274304,11163050", resultMoves); // Add the opening and its moves to the book

        // Case 4
        resultMoves = new ArrayList<>(); // Create a list of moves
        resultMoves.add(new Coordinate(3, 4, 2, 5)); // Add the moves to the list
        openingBook.put("6172822139926740992,11163050", resultMoves); // Add the opening and its moves to the book

        // Case 5
        resultMoves = new ArrayList<>(); // Create a list of moves
        resultMoves.add(new Coordinate(3, 6, 2, 7)); // Add the moves to the list
        openingBook.put("6172822243005956096,11163050", resultMoves); // Add the opening and its moves to the book

        // Case 6
        resultMoves = new ArrayList<>(); // Create a list of moves
        resultMoves.add(new Coordinate(3, 6, 2, 7)); // Add the moves to the list
        openingBook.put("6172769466447822848,11163050", resultMoves); // Add the opening and its moves to the book

        // Case 7 HERE
        resultMoves = new ArrayList<>(); // Create a list of moves
        resultMoves.add(new Coordinate(3, 6, 2, 5)); // Add the moves to the list
        openingBook.put("6172769878764683264,11163050", resultMoves); // Add the opening and its moves to the book
    }

    public Ai(Model board, Presenter app) {
        this.model = board;
        this.app = app;
    }


    public int evaluate(PieceType type, List<Coordinate> coords) {
        type = PieceType.BLACK;

        // CLONES
            Bitboard cloneBoard = model.board.CloneBitboard();
        //

        int evaluateMove = 0;
        PieceType typeTest = cloneBoard.typeOfPiece(coords.getFirst().getOldX(), coords.getFirst().getOldY());
        for (Coordinate coord : coords) {
            if (Math.abs(coord.getOldX() - coord.getX()) > 1){
                // Adding 8 points for killing a piece
                evaluateMove += 8;
                // Adding another 8 points for killing a king
                int [] eatenPiece = cloneBoard.EatPiece(coord.getOldX(), coord.getOldY(), coord.getX(), coord.getY());
                if (cloneBoard.checkKing(eatenPiece[0],eatenPiece[1],cloneBoard.typeOfPiece(eatenPiece[0],eatenPiece[1]))) {
                    evaluateMove += 8;
                }
            } else if (Math.abs(coord.getOldX() - coord.getX()) == 1) {
                evaluateMove += getCloseEnemy(type, coord.getX(), coord.getY());
                // check next white move and see if it can eat a piece
            }
            else evaluateMove = Integer.MAX_VALUE;
            // Wining move gets the lowest score which is -1
            if (cloneBoard.checkWin(type)) return -1;

            // Makes a piece become a king
            if (cloneBoard.checkKing(coord.getX(), coord.getY(), type)) {
                evaluateMove += 16;
            }
        }
        return evaluateMove;
    }

    public int getCloseEnemy(PieceType type, int x, int y) {
        type = PieceType.BLACK;
        int closeEnemy = Integer.MAX_VALUE;
        long enemyPlayers = model.board.player2 | model.board.king2;
        // Loop through each position on the board

        if (type == PieceType.WHITE || type == PieceType.WHITEKING) {
            enemyPlayers = model.board.player1 | model.board.king1;
        }

        int[] checkExist = (model.board.getFirstBit(enemyPlayers));

        while (checkExist[0] != -1) {
            long currentPosition = 1L << (checkExist[0] * CheckersApplication.HEIGHT + checkExist[1]);

            if (closeEnemy > (Math.abs(checkExist[0] - x) + Math.abs(checkExist[1] - y)) - 1) {
                closeEnemy = (Math.abs(checkExist[0] - x) + Math.abs(checkExist[1] - y)) - 1;
            }

            enemyPlayers &= ~currentPosition;
            checkExist = model.board.getFirstBit(enemyPlayers);
        }
        return closeEnemy;
    }

    public List<Coordinate> getBestMovesForType(PieceType type) {

        if(!usedOpening)
        {
            usedOpening = true;
            return openingBook.get(model.board.player2 + "," + model.board.player1);
        }

        type = PieceType.BLACK;
        Map<Coordinate, List<List<Coordinate>>> legalMoves = model.board.generateLegalMovesForType(PieceType.BLACK);
        int score = Integer.MAX_VALUE;
        List<Coordinate> bestMove = new ArrayList<>();
        for (Coordinate oldPosition : legalMoves.keySet())
        {
            for (List<Coordinate> move : legalMoves.get(oldPosition))
            {
                if(!move.isEmpty())
                {
                    while(model.board.typeOfPiece(move.getFirst().getOldX(), move.getFirst().getOldY()) == PieceType.WHITE)
                    {
                        model.board.printBoard();
                        System.out.println("WTF");
                    }
                        if(score > evaluate(PieceType.BLACK,move))
                    {
                        score = evaluate(PieceType.BLACK,move);
                        bestMove = move;
                    }
                }
            }
        }
        return bestMove;
    }
}
