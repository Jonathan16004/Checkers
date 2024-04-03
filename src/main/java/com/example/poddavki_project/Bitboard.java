package com.example.poddavki_project;

import java.util.ArrayList;
import java.util.List;

public class Bitboard
{
    // Blacks
    private long player1;
    private long king1;

    // Whites
    private long player2;
    private long king2;

    public Bitboard()
    {
        long mask = 1;

        player1 = 0;
        player2 = 0;
        king1 = 0;
        king2 = 0;

        // Initializes both players
        for (int row = 0; row < CheckersApplication.HEIGHT; row++)
        {
            for (int col = 0; col < CheckersApplication.WIDTH; col++)
            {
                // Checks if it's the upper half of the board and the tiles are not a white tile
                if (row <= 2 && (row + col) % 2 != 0)
                {
                    // player 1 add with bitwise
                    player1 |= mask;
                }

                // Checks if it's the lower half of the board and the tile are white tile
                if (row >= 5 && (row + col) % 2 != 0)
                {
                    // player 2 add with bitwise
                    player2 |= mask;
                }

                mask <<= 1;
            }
        }
    }

    // Moves a piece on the bitboard
    public void MovePiece(int oldRow, int oldCol, int newRow, int newCol)
    {
        PieceType type = typeOfPiece(oldRow,oldCol);

        // Calculate the bit position of the old and new positions
        long newPosition = 1L << (newRow * CheckersApplication.HEIGHT + newCol);

        // Determine which player's bitboard to update based on the piece type
        if (type == PieceType.BLACK)
        {
            // Clear the old position for player 1
            deletePiece(oldRow, oldCol);
            // Set the new position for player 1
            player1 |= newPosition;
        }

        else if (type == PieceType.BLACKKING)
        {
            deletePiece(oldRow, oldCol);
            king1 |= newPosition;
        }

        else if (type == PieceType.WHITE)
        {
            // Clear the old position for player 2
            deletePiece(oldRow, oldCol);
            // Set the new position for player 2
            player2 |= newPosition;
        }

        else if (type == PieceType.WHITEKING)
        {
            deletePiece(oldRow, oldCol);
            king2 |= newPosition;
        }

        else
        {
            System.out.println("Can't Move the piece type: " + type);
        }
    }

    // Deletes a piece
    public void deletePiece(int row, int col)
    {
        PieceType type = typeOfPiece(row,col);
        // Calculate the bit position of the old and new positions
        long position = 1L << (row * CheckersApplication.HEIGHT + col);

        // Determine which player's bitboard to update based on the piece type
        if (type == PieceType.BLACK || type == PieceType.BLACKKING)
        {
            // Clear the position for player 1
            player1 &= ~position;
            king1 &= ~position;
        }
        else if (type == PieceType.WHITE || type == PieceType.WHITEKING)
        {
            // Clear the old position for player 2
            player2 &= ~position;
            king2 &= ~position;
        }
        else
        {
            System.out.println("Can't delete the piece type: " + type);
        }
    }

    // kingify
    public void kingify(int row, int col)
    {
        PieceType type = typeOfPiece(row,col);

        long position = 1L << (row * CheckersApplication.HEIGHT + col);

        // Determine which player's bitboard to update based on the piece type
        if (type == PieceType.BLACK || type == PieceType.BLACKKING)
        {
            deletePiece(row, col);
            // Clear the position for player 1
            king1 |= position;
        }

        else if(type == PieceType.WHITE || type == PieceType.WHITEKING)
        {
            deletePiece(row, col);
            // Clear the old position for player 2
            king2 |= position;
        }
        else
        {
            System.out.println("Can't kingify the piece type: " + type);
        }
    }

    // Returns a list of all legal coordinates for each piece
    public List<List<Coordinate>> generateLegalMoves()
    {
        List<List<Coordinate>> legalMoves = generateLegalCaptureMoves();

        if(emptyMoves(legalMoves))
        {
            // Loop through each position on the board
            for (int x = 0; x < CheckersApplication.WIDTH; x++)
            {
                for (int y = 0; y < CheckersApplication.HEIGHT; y++)
                {
                    PieceType type = typeOfPiece(x, y);
                    long currentPosition = 1L << (x * CheckersApplication.HEIGHT + y);

                    // If the current position contains any type of piece
                    if (((player1 | king1 | player2 | king2) & currentPosition) != 0)
                    {
                        List<Coordinate> pieceLegalMovesCapture = generateLegalMovesForPiece(x, y, type);
                        legalMoves.add(pieceLegalMovesCapture);
                    }
                }
            }
        }
        return legalMoves;
    }

    // Returns a list of all legal coordinates for each piece
    public List<List<Coordinate>> generateLegalMovesForType(PieceType type)
    {
        List<List<Coordinate>> legalMoves = generateLegalCaptureMoves();

        if(emptyMoves(legalMoves))
        {
            // Loop through each position on the board
            for (int x = 0; x < CheckersApplication.WIDTH; x++)
            {
                for (int y = 0; y < CheckersApplication.HEIGHT; y++)
                {
                    long currentPosition = 1L << (x * CheckersApplication.HEIGHT + y);

                    if(type == PieceType.BLACK || type == PieceType.BLACKKING)
                    {
                        // If the current position contains any type of piece
                        if (((player1 | king1) & currentPosition) != 0)
                        {
                            List<Coordinate> pieceLegalMovesCapture = generateLegalMovesForPiece(x, y, type);
                            legalMoves.add(pieceLegalMovesCapture);
                        }
                    }
                    else
                    {
                        // If the current position contains any type of piece
                        if (((player2 | king2) & currentPosition) != 0)
                        {
                            List<Coordinate> pieceLegalMovesCapture = generateLegalMovesForPiece(x, y, type);
                            legalMoves.add(pieceLegalMovesCapture);
                        }
                    }
                }
            }
        }
        return legalMoves;
    }

    // Helper to check if there's any legal moves
    public boolean emptyMoves(List<List<Coordinate>> legalMoves)
    {
        for (List<Coordinate> pieceMoves : legalMoves) {
            if (!pieceMoves.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    // Helper to check if there's any legal moves
    public boolean emptyMovesForPiece(List<Coordinate> legalMoves) {
        for (Coordinate move : legalMoves) {  // Iterate over individual coordinates
            if (move != null) {  // Check if the coordinate is not null
                return false;
            }
        }
        return true;
    }

    // Returns a list of all legal capture coordinates for each piece
    public List<List<Coordinate>> generateLegalCaptureMoves()
    {
        List<List<Coordinate>> legalMoves = new ArrayList<>();

        // Loop through each position on the board
        for (int x = 0; x < CheckersApplication.WIDTH; x++)
        {
            for (int y = 0; y < CheckersApplication.HEIGHT; y++)
            {
                PieceType type = typeOfPiece(x, y);
                long currentPosition = 1L << (x * CheckersApplication.HEIGHT + y);

                // If the current position contains any type of piece
                if (((player1 | king1 | player2 | king2) & currentPosition) != 0)
                {
                    List<Coordinate> pieceLegalMovesCapture = generateLegalMovesForPieceCapture(x, y, type);
                    legalMoves.add(pieceLegalMovesCapture);
                }
            }
        }
        return legalMoves;
    }

    public boolean hasPieces(PieceType type)
    {
        if(type == PieceType.BLACK || type == PieceType.BLACKKING)
        {
            return (player1|king1) != 0;
        }
        return (player2|king2) != 0;
    }

    public boolean checkWin(PieceType type)
    {
        return emptyMoves(generateLegalMovesForType(type)) || !hasPieces(type);
    }

    // Check if a player is king
    public boolean checkKing(int row, int col, PieceType type) {
        // King-making rows for black and white pieces
        int kingRowBlack = CheckersApplication.HEIGHT - 1;
        int kingRowWhite = 0;

        // Check for king-making position based on piece type and row
        if ((type == PieceType.BLACK && row == kingRowBlack) ||
                (type == PieceType.WHITE && row == kingRowWhite)) {
            kingify(row, col); // Promote piece to king if on king-making row
            return true;
        }

        return false; // Not a king-making location
    }
    // Returns a list of all legal capture coordinates for a kind of piece
    public List<List<Coordinate>> generateLegalCaptureMovesForType(PieceType type)
    {
        List<List<Coordinate>> legalMoves = new ArrayList<>();

        // Loop through each position on the board
        for (int x = 0; x < CheckersApplication.WIDTH; x++)
        {
            for (int y = 0; y < CheckersApplication.HEIGHT; y++)
            {
                long currentPosition = 1L << (x * CheckersApplication.HEIGHT + y);

                if(type == PieceType.BLACK || type == PieceType.BLACKKING)
                {
                    // If the current position contains any type of piece
                    if (((player1 | king1) & currentPosition) != 0)
                    {
                        List<Coordinate> pieceLegalMovesCapture = generateLegalMovesForPieceCapture(x, y, typeOfPiece(x,y));
                        legalMoves.add(pieceLegalMovesCapture);
                    }
                }
                else
                {
                    // If the current position contains any type of piece
                    if (((player2 | king2) & currentPosition) != 0)
                    {
                        List<Coordinate> pieceLegalMovesCapture = generateLegalMovesForPieceCapture(x, y, typeOfPiece(x,y));
                        legalMoves.add(pieceLegalMovesCapture);
                    }
                }
            }
        }
        return legalMoves;
    }


    // Generates legal moves for a specific piece
    public List<Coordinate> generateLegalMovesForPiece(int row, int col, PieceType type) {

        // Get capturing moves
        List<Coordinate> legalMoves = new ArrayList<>(getCapturingMoves(row, col, type, new ArrayList<>()));

        // If there are no capturing moves, check for normal moves
        if (legalMoves.isEmpty())
        {
            legalMoves.addAll(getNormalMoves(row, col, type));
        }

        return legalMoves;
    }
    // Generates legal moves for a specific piece
    public List<Coordinate> generateLegalMovesForPieceCapture(int row, int col, PieceType type) {

        // Get capturing moves

        return new ArrayList<>(getCapturingMoves(row, col, type, new ArrayList<>()));
    }

    // Helper method to get capturing moves for a piece, including consecutive jumps
    private List<Coordinate> getCapturingMoves(int x, int y, PieceType type, List<Coordinate> previousJumps) {

        // Check capturing moves in all 4 diagonal directions
        if(!previousJumps.isEmpty() || type == PieceType.BLACKKING || type == PieceType.WHITEKING)
        {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 && dy != 0) { // Avoid same square or straight moves

                        int jumpX = x + dx;
                        int jumpY = y + dy;

                        // Check if jump is within bounds and captures opponent's piece
                        if (isValidPosition(jumpX, jumpY) && isOpponentPiece(jumpX, jumpY, type)) {

                            // Check if there's an empty space after the jump
                            int afterJumpX = jumpX + dx;
                            int afterJumpY = jumpY + dy;

                            if (isValidPosition(afterJumpX, afterJumpY) && isTileEmpty(afterJumpX, afterJumpY) && !containsCoordinate(previousJumps,afterJumpX,afterJumpY))
                            {
                                // Adds the jumped coordinate
                                previousJumps.add(new Coordinate(afterJumpX, afterJumpY, x, y));
                                getCapturingMoves(afterJumpX, afterJumpY, type, previousJumps);

                            }
                        }
                    }
                }
            }
        }
        else
        {
            int moveDirY; // Determine move direction for normal pieces
            if (type == PieceType.BLACK) {
                moveDirY = 1; // Black moves down
            } else {
                moveDirY = -1; // White moves up
            }

            for (int dx = -1; dx <= 1; dx++) {
                if (dx != 0) { // Avoid same square (dx = 0)
                    int jumpX = x + moveDirY;
                    int jumpY = y + dx;

                    // Check if jump is within bounds and captures opponent's piece
                    if (isValidPosition(jumpX, jumpY) && isOpponentPiece(jumpX, jumpY, type)) {

                        // Check if there's an empty space after the jump
                        int afterJumpX = jumpX + moveDirY;
                        int afterJumpY = jumpY + dx;

                        if (isValidPosition(afterJumpX, afterJumpY) && isTileEmpty(afterJumpX, afterJumpY) && !containsCoordinate(previousJumps,afterJumpX,afterJumpY)) {
                            // Adds the jumped coordinate
                            previousJumps.add(new Coordinate(afterJumpX, afterJumpY, x, y));
                            getCapturingMoves(afterJumpX, afterJumpY, type, previousJumps);
                        }
                    }
                }
            }
        }
        return previousJumps;
    }

    // Helper method to check if a list of coordinates contains a specific coordinate
    private boolean containsCoordinate(List<Coordinate> coordinates, int x, int y) {
        for (Coordinate coordinate : coordinates) {
            if (coordinate.getX() == x && coordinate.getY() == y) {
                return true;
            }
        }
        return false;
    }



    // Helper method to get normal moves for a piece
    private List<Coordinate> getNormalMoves(int x, int y, PieceType type)
    {
        List<Coordinate> normalMoves = new ArrayList<>();

        // In case of regular piece
        if (type == PieceType.BLACK || type == PieceType.WHITE)
        {
            // Add forward moves (depends on player)
            int moveDir = (type == PieceType.BLACK) ? 1 : -1;

            // Check bounds and empty tiles, diagonal left and right forward movement
            if (isValidPosition(x + moveDir, y + 1) && isTileEmpty(x + moveDir, y + 1))
            {
                normalMoves.add(new Coordinate(x + moveDir, y + 1, x, y));
            }
            if (isValidPosition(x + moveDir, y - 1) && isTileEmpty(x + moveDir, y - 1)) {
                normalMoves.add(new Coordinate(x + moveDir, y - 1, x, y));
            }
        }
        else if (type == PieceType.BLACKKING || type == PieceType.WHITEKING)
        {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 && dy != 0) { // Avoid moving to the same position
                        int distance = 1;
                        while (isValidPosition(x + dx * distance, y + dy * distance) &&
                                isTileEmpty(x + dx * distance, y + dy * distance)) {
                            normalMoves.add(new Coordinate(x + dx * distance, y + dy * distance, x, y));
                            distance++; // Check further in the same diagonal direction
                        }
                    }
                }
            }
        }
        return normalMoves;
    }

    // Helper method to check if a position is within the board bounds
    private boolean isValidPosition(int x, int y)
    {
        return x >= 0 && x < CheckersApplication.WIDTH && y >= 0 && y < CheckersApplication.HEIGHT;
    }

    // Helper method to check if a tile is empty
    private boolean isTileEmpty(int x, int y)
    {
        long position = 1L << (x * CheckersApplication.HEIGHT + y);
        return ((player1 | player2 | king2 | king1) & position) == 0;
    }

    // Helper method to check if the tile contains an opponent's piece
    public boolean isOpponentPiece(int x, int y, PieceType currentPlayerType)
    {
        long position = 1L << (x * CheckersApplication.HEIGHT + y);
        // In case of a White opponent
        if (currentPlayerType == PieceType.BLACK || currentPlayerType == PieceType.BLACKKING)
        {
            // If there's a white piece in that place
            return ((player2 | king2) & position) != 0;
        }
        // In case of a black opponent
        else
        {
            // If there's a black piece in that place
            return ((player1 | king1) & position) != 0;
        }
    }

    public PieceType typeOfPiece(int row, int col)
    {
        long position = 1L << (row * CheckersApplication.HEIGHT + col);

        if ((player1 & position) != 0)
        {
            return PieceType.BLACK;
        }
        else if ((player2 & position) != 0)
        {
            return PieceType.WHITE;
        }
        else if ((king1 & position) != 0)
        {
            return PieceType.BLACKKING;
        }
        else if ((king2 & position) != 0)
        {
            return PieceType.WHITEKING;
        }

        return PieceType.NONE;
    }

    // Prints legal moves
    public void printLegalMoves()
    {
        List<List<Coordinate>> legalMoves = generateLegalMoves();

        for (int i = 0; i < legalMoves.size(); i++)
        {
            List<Coordinate> movesForPiece = legalMoves.get(i);
            System.out.println("Legal moves for piece number " + i + ": ");
            for (Coordinate move : movesForPiece)
            {
                System.out.println("[" + move.getX() + "][" + move.getY() + "] from " + "[" + move.getOldX() + "][" + move.getOldY() + "]");
            }
            System.out.println();
        }
    }
    public void printLegalPieceMoves(int row,int col)
    {
        PieceType type = typeOfPiece(row,col);

        List<Coordinate> legalMoves = generateLegalMovesForPiece(row,col,type);

        System.out.println("Legal moves for " + type +" piece [" + row + "][" + col + "]: ");

        for (Coordinate move : legalMoves)
        {
            System.out.println("[" + move.getX() + "][" + move.getY() + "] from " + "[" + move.getOldX() + "][" + move.getOldY() + "]");
        }

    }

    // Prints legal moves
    public void printBoard() {
        PieceType[][] boardPrint = bitToMatrix();

        for (int x = 0; x < CheckersApplication.WIDTH; x++) {
            for (int y = 0; y < CheckersApplication.HEIGHT; y++) {
                System.out.print(boardPrint[x][y] + " ");
            }
            System.out.println();
        }
    }

    // Converts the bitboard to matrix
    public PieceType[][] bitToMatrix() {
        PieceType[][] board = new PieceType[CheckersApplication.WIDTH][CheckersApplication.HEIGHT];
        long mask = 1;

        for (int x = 0; x < CheckersApplication.WIDTH; x++) {
            for (int y = 0; y < CheckersApplication.HEIGHT; y++) {
                if ((player1 & mask) == mask) {
                    board[x][y] = PieceType.BLACK;
                } else if ((player2 & mask) == mask) {
                    board[x][y] = PieceType.WHITE;
                } else if ((king1 & mask) == mask) {
                    board[x][y] = PieceType.BLACKKING;
                } else if ((king2 & mask) == mask) {
                    board[x][y] = PieceType.WHITEKING;
                } else {
                    board[x][y] = PieceType.NONE;
                }
                mask <<= 1;
            }
        }
        return board;
    }

    // Launches the app
    public static void main(String[] args) {
        Bitboard board = new Bitboard();

        board.MovePiece(5,4,3,4);
        board.MovePiece(5,2,3,2);
        board.deletePiece(6,7);

        board.deletePiece(2,3);
        board.deletePiece(3,4);
        board.MovePiece(2,1,4,1);
        board.MovePiece(0,1,3,4);
        board.kingify(5,6);
        board.MovePiece(5,6,4,5);

        board.printLegalMoves();

        board.printLegalPieceMoves(4,5);
        board.printBoard();

        //---------------------------------------
        System.out.println("BLACK");
        System.out.println(Long.toBinaryString(board.player1));
        System.out.println("BLACK KINGS");
        System.out.println(Long.toBinaryString(board.king1));
        System.out.println("WHITE");
        System.out.println(Long.toBinaryString(board.player2));
        System.out.println("WHITE KINGS");
        System.out.println(Long.toBinaryString(board.king2));
    }
}
