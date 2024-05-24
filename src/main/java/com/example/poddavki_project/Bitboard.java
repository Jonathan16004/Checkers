package com.example.poddavki_project;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bitboard
{
    // Blacks
    public long player1;
    public long king1;

    // Whites
    public long player2;
    public long king2;

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

    public Bitboard CloneBitboard()
    {
        Bitboard clone = new Bitboard();
        clone.player1 = this.player1;
        clone.player2 = this.player2;
        clone.king1 = this.king1;
        clone.king2 = this.king2;
        return clone;
    }

    public int[] EatPiece(int oldRow, int oldCol, int row, int col)
    {
        int dx = row - oldRow;
        int dy = col - oldCol;

        int jumpX = oldRow + dx / 2;
        int jumpY = oldCol + dy / 2;

        MovePiece(oldRow, oldCol, row, col);
        deletePiece(jumpX, jumpY);
        return new int[]{jumpX, jumpY};
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

    public int[] getFirstBit(long pieces){
        int[] pieceCordination = new int[]{-1, -1};
        int leftestBit = Long.numberOfLeadingZeros(pieces);
        leftestBit = 63 - leftestBit;
        if (leftestBit != -1) {
            pieceCordination[0] = leftestBit / 8;
            pieceCordination[1] = leftestBit % 8;
    }
        long currentPosition = 1L << (pieceCordination[0] * CheckersApplication.HEIGHT + pieceCordination[1]);


        return pieceCordination;
    }
    public Map<Coordinate, List<List<Coordinate>>> generateLegalMoves()
    {

        Map<Coordinate, List<List<Coordinate>>> legalMoves = generateLegalCaptureMoves();

        if(emptyMoves(legalMoves))
        {

            long allPlayers = player1|player2|king1|king2;
            int[] checkExist = (getFirstBit(allPlayers));
            while (checkExist[0] != -1)
            {
                long currentPosition = 1L << (checkExist[0] * CheckersApplication.HEIGHT + checkExist[1]);
                List<List<Coordinate>> pieceLegalMovesCapture = generateLegalMovesForPiece(checkExist[0], checkExist[1], typeOfPiece(checkExist[0],checkExist[1]));
                legalMoves.put(new Coordinate(checkExist[0],checkExist[1]), pieceLegalMovesCapture);
                allPlayers &= ~currentPosition;
                checkExist = getFirstBit(allPlayers);
            }
        }
        return legalMoves;
    }

    // Returns a list of all legal coordinates for each piece
    public Map<Coordinate, List<List<Coordinate>>> generateLegalMovesForType(PieceType type)
    {
        Map<Coordinate, List<List<Coordinate>>> legalMoves = generateLegalCaptureMovesForType(type);

        if(emptyMoves(legalMoves))
        {

            long typePlayers = player1|king1;
            if(type == PieceType.WHITE || type == PieceType.WHITEKING) typePlayers = player2|king2;
            int[] checkExist = (getFirstBit(typePlayers));
            while (checkExist[0] != -1)
            {
                long currentPosition = 1L << (checkExist[0] * CheckersApplication.HEIGHT + checkExist[1]);
                List<List<Coordinate>> pieceLegalMovesCapture = generateLegalMovesForPiece(checkExist[0], checkExist[1], typeOfPiece(checkExist[0],checkExist[1]));
                legalMoves.put(new Coordinate(checkExist[0],checkExist[1]), pieceLegalMovesCapture);
                typePlayers &= ~currentPosition;
                checkExist = getFirstBit(typePlayers);
            }
        }

        return legalMoves;
    }

    // Helper to check if there's any legal moves
    public boolean emptyMovesForPiece(List<List<Coordinate>> legalMoves) {
        for (List<Coordinate> move : legalMoves) {  // Iterate over individual coordinates
            for (Coordinate moveDir : move)
            {
                if (moveDir != null) {  // Check if the coordinate is not null
                    return false;
                }
            }
        }
        return true;
    }

    // Returns a list of all legal capture coordinates for each piece
    public Map<Coordinate, List<List<Coordinate>>> generateLegalCaptureMoves()
    {
        Map<Coordinate, List<List<Coordinate>>> legalMoves = new HashMap<>();

        long allPlayers = player1|player2|king1|king2;
        int[] checkExist = (getFirstBit(allPlayers));
        while (checkExist[0] != -1)
        {
            long currentPosition = 1L << (checkExist[0] * CheckersApplication.HEIGHT + checkExist[1]);
            List<List<Coordinate>> pieceLegalMovesCapture = generateLegalMovesForPieceCapture(checkExist[0], checkExist[1], typeOfPiece(checkExist[0],checkExist[1]));
            legalMoves.put(new Coordinate(checkExist[0],checkExist[1]), pieceLegalMovesCapture);
            allPlayers &= ~currentPosition;
            checkExist = getFirstBit(allPlayers);
        }

        return legalMoves;
    }

    // Checks if the map values is all empty
    public boolean emptyMoves(Map<Coordinate, List<List<Coordinate>>> legalMoves) {
        if (legalMoves == null || legalMoves.isEmpty()) {
            // Handle the case where legalMoves is null or empty (might be necessary depending on your logic)
            return true;
        }

        // Iterate through values manually
        for (List<List<Coordinate>> moveList : legalMoves.values()) {
            for (List<Coordinate> moveListList : moveList) {
                if (!moveListList.isEmpty()) {
                    return false; // If even one inner list has elements, return false
                }
            }
        }

        // If loop completes without finding non-empty list, all are empty
        return true;
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
    public Map<Coordinate, List<List<Coordinate>>> generateLegalCaptureMovesForType(PieceType type)
    {
        Map<Coordinate, List<List<Coordinate>>> legalMoves = new HashMap<>();
        long allPlayers = player1|king1;
        // Loop through each position on the board

        if(type == PieceType.WHITE || type == PieceType.WHITEKING)
        {
            allPlayers = player2|king2;
        }

        int[] checkExist = (getFirstBit(allPlayers));
        while (checkExist[0] != -1)
        {
            long currentPosition = 1L << (checkExist[0] * CheckersApplication.HEIGHT + checkExist[1]);
            List<List<Coordinate>> pieceLegalMovesCapture = generateLegalMovesForPieceCapture(checkExist[0], checkExist[1], typeOfPiece(checkExist[0],checkExist[1]));
            legalMoves.put(new Coordinate(checkExist[0],checkExist[1]), pieceLegalMovesCapture);
            allPlayers &= ~currentPosition;
            checkExist = getFirstBit(allPlayers);
        }
        return legalMoves;
    }


    // Generates legal moves for a specific piece
    public List<List<Coordinate>> generateLegalMovesForPiece(int row, int col, PieceType type) {

        // Get capturing moves
        List<List<Coordinate>> legalMoves = new ArrayList<>(getCapturingMoves(row, col, type, new ArrayList<>()));

        // If there are no capturing moves, check for normal moves
        if (isEmptyMovesListList(legalMoves))
        {
            // PROBLEM IS HERE WTF
            legalMoves.addAll(getNormalMoves(row, col, type));
        }

        return legalMoves;
    }
    // Generates legal moves for a specific piece
    public List<List<Coordinate>> generateLegalMovesForPieceCapture(int row, int col, PieceType type) {
        // Assuming getCapturingMoves returns a list of capturing moves
        return getCapturingMoves(row, col, type, new ArrayList<>());
    }


    // Helper method to get capturing moves for a piece, including consecutive jumps
    public List<List<Coordinate>> getCapturingMoves(int x, int y, PieceType type, List<Coordinate> previousJumps)
    {
        List<List<Coordinate>> allCaptureDir = new ArrayList<>();

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
            // Adding to previousJumps
            allCaptureDir.add(previousJumps);
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
            // Adding to previousJumps
            allCaptureDir.add(previousJumps);
        }
        return allCaptureDir;
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



    public List<List<Coordinate>> getNormalMoves(int x, int y, PieceType type) {
        List<List<Coordinate>> normalMovesAllDir = new ArrayList<>();

        // In case of regular piece
        if (type == PieceType.BLACK || type == PieceType.WHITE) {
            // Add forward moves (depends on player)
            int moveDir = (type == PieceType.BLACK) ? 1 : -1;

            // Check bounds and empty tiles, diagonal left and right forward movement
            if (isValidPosition(x + moveDir, y + 1) && isTileEmpty(x + moveDir, y + 1)) {
                List<Coordinate> normalMove = new ArrayList<>();
                normalMove.add(new Coordinate(x + moveDir, y + 1, x, y));
                normalMovesAllDir.add(normalMove);
            }
            if (isValidPosition(x + moveDir, y - 1) && isTileEmpty(x + moveDir, y - 1)) {
                List<Coordinate> normalMove = new ArrayList<>();
                normalMove.add(new Coordinate(x + moveDir, y - 1, x, y));
                normalMovesAllDir.add(normalMove);
            }
        } else if (type == PieceType.BLACKKING || type == PieceType.WHITEKING) {
            // Check only four diagonals for kings
            for (int dx = -1; dx <= 1; dx += 2) {
                for (int dy = -1; dy <= 1; dy += 2) {
                    if (isValidPosition(x + dx, y + dy) && isTileEmpty(x + dx, y + dy)) {
                        List<Coordinate> normalMove = new ArrayList<>();
                        normalMove.add(new Coordinate(x + dx, y + dy, x, y));
                        normalMovesAllDir.add(normalMove);
                    }
                }
            }
        }
        return normalMovesAllDir;
    }

    public static boolean isEmptyMovesListList(List<List<Coordinate>> moves) {
    // Check if the outer list is empty
    if (moves == null || moves.isEmpty()) {
        return true;
    }

    // Check if any sublist is empty
    for (List<Coordinate> sublist : moves) {
        if (sublist.isEmpty()) {
            return true;
        }
    }

    // No empty sublists found
    return false;
}

    public static boolean containsCoordinateListList(List<List<Coordinate>> listList, int x, int y) {

        System.out.println("Hello");
        for (List<Coordinate> sublist : listList) {
            for(Coordinate subSubList : sublist)
            {
                if (subSubList.getX() == x && subSubList.getY() == y || subSubList.getOldX() == x && subSubList.getOldY() == y) {
                    return true;
                }
            }
        }

        return false;
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

        Map<Coordinate, List<List<Coordinate>>> legalMoves = generateLegalMoves();

        for (Coordinate oldPosition : legalMoves.keySet())
        {
            System.out.println("Legal moves for piece at [" + oldPosition.getX() + "][" + oldPosition.getY() + "]: ");
            for (List<Coordinate> move : legalMoves.get(oldPosition))
            {
                for (Coordinate moveCord : move)
                {
                    System.out.println("[" + moveCord.getX() + "][" + moveCord.getY() + "]");
                }
            }
            System.out.println();
        }
    }
    public void printLegalPieceMoves(int row,int col)
    {
        PieceType type = typeOfPiece(row,col);

        List<List<Coordinate>> legalMoves = generateLegalMovesForPiece(row,col,type);

        System.out.println("Legal moves for " + type +" piece [" + row + "][" + col + "]: ");

        for (List<Coordinate> move : legalMoves)
        {
            for(Coordinate moveCord : move)
            {
                System.out.println("[" + moveCord.getX() + "][" + moveCord.getY() + "] from " + "[" + moveCord.getOldX() + "][" + moveCord.getOldY() + "]");
            }
            System.out.println();
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

// AI

//
    // Launches the app
    public static void main(String[] args) {

        Bitboard board = new Bitboard();
        board.MovePiece(5,4,4,3);
        board.printBoard();


       //---------------------------------------
        System.out.println("BLACK");
        System.out.println(board.player1);
        System.out.println("BLACK KINGS");
        System.out.println(board.king1);
        System.out.println("WHITE");
        System.out.println(board.player2);
        System.out.println("WHITE KINGS");
        System.out.println(board.king2);
//        System.out.println("BLACK");
//        System.out.println(Long.toBinaryString(board.player1));
//        System.out.println("BLACK KINGS");
//        System.out.println(Long.toBinaryString(board.king1));
//        System.out.println("WHITE");
//        System.out.println(Long.toBinaryString(board.player2));
//        System.out.println("WHITE KINGS");
//        System.out.println(Long.toBinaryString(board.king2));
    }
}
