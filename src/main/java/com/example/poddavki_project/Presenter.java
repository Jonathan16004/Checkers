package com.example.poddavki_project;

import javafx.scene.Scene;

import java.util.List;
import java.util.Map;

public class Presenter implements IPresenter
{
    private View view;
    private Model model;
    public Presenter(View view)
    {
        model = new Model();
        this.view = view;
    }
    // Creates a new object of a piece
    public void aiMove(int x0, int y0, int newX, int newY, Piece piece)
    {

        MoveResult result = tryMove(piece, newX, newY);
        // PROBLEM IS HERE
        Map<Coordinate, List<Coordinate>> legalMoves = model.board.generateLegalCaptureMovesForType(view.currentPlayer);
        for (int i = 0; i < legalMoves.size(); i++)
        {
            List<Coordinate> movesForPiece = legalMoves.get(i);
            System.out.println("Legal moves for piece number " + i + ": ");
            for (Coordinate move : movesForPiece)
            {
                System.out.println("[" + move.getX() + "][" + move.getY() + "]");
            }
            System.out.println();
        }
        if(model.board.emptyMoves(legalMoves))
        {
            if (result.getType() == MoveType.NONE)
            {
                piece.abortMove();
            }
            else if (result.getType() == MoveType.NORMAL)
            {
                piece.getType();
                piece.move(newX, newY);
                view.boardVisual[x0][y0].setPiece(null);
                view.boardVisual[newX][newY].setPiece(piece);
                model.board.MovePiece(y0, x0, newY, newX);
                // Checks if a player is now king
                if(model.board.checkKing(newY,newX,view.currentPlayer))
                {
                    piece.setKing();
                    model.board.kingify(newY,newX);
                    System.out.println("NEW KING " + model.board.typeOfPiece(newY, newX));
                }
                view.currentPlayer = view.currentPlayer == PieceType.BLACK ? PieceType.WHITE : PieceType.BLACK;
                System.out.println("TURN: " + view.currentPlayer);
            }
            else
            {
                piece.abortMove();
            }
        }
        else if (result.getType() == MoveType.KILL)
        {
            piece.move(newX, newY);
            view.boardVisual[x0][y0].setPiece(null);
            view.boardVisual[newX][newY].setPiece(piece);
            model.board.MovePiece(y0, x0, newY, newX);
            Piece otherPiece = result.getPiece();
            view.boardVisual[view.toBoard(otherPiece.getOldX())][view.toBoard(otherPiece.getOldY())].setPiece(null);
            model.board.deletePiece(view.toBoard(otherPiece.getOldY()), view.toBoard(otherPiece.getOldX()));
            view.pieceGroup.getChildren().remove(otherPiece);
            if(model.board.checkKing(newY,newX,view.currentPlayer))
            {
                piece.setKing();
                model.board.kingify(newY,newX);
                System.out.println("NEW KING " + model.board.typeOfPiece(newY, newX));
            }
            if(model.board.emptyMovesForPiece(model.board.generateLegalMovesForPieceCapture(newY, newX, piece.getType())))
            {
                view.currentPlayer = view.currentPlayer == PieceType.BLACK ? PieceType.WHITE : PieceType.BLACK;
            }
        }
        else
        {
            piece.abortMove();
        }
        // White won
        if(model.board.checkWin(PieceType.WHITE))
        {
            System.out.println("WHITE WON");
            Scene whiteWon = new Scene(view.whiteWinScene());
            view.stageWon.setScene(whiteWon);
            view.stageWon.show();
            view.won = true;
        }
        // Black won
        else if(model.board.checkWin(PieceType.BLACK))
        {
            System.out.println("BLACK WON");
            Scene blackWon = new Scene(view.blackWinScene());
            view.stageWon.setScene(blackWon);
            view.stageWon.show();
            view.won = true;
        }
        // Tie
        else if (model.board.checkWin(PieceType.BLACK) && model.board.checkWin(PieceType.WHITE))
        {
            System.out.println("Tie");
            Scene tie = new Scene(view.tieScene());
            view.stageWon.setScene(tie);
            view.stageWon.show();
            view.won = true;
        }
        model.board.printBoard();
        System.out.println("AI");
    }

    // Creates a new object of a piece
    public Piece makePiece(PieceType type, int x, int y)
    {
        Piece piece = new Piece(type, x, y);
        piece.setOnMouseReleased(e ->
        {
            System.out.println("TURN: " + view.currentPlayer);
            model.board.generateLegalCaptureMovesForType(view.currentPlayer);
            int newX = view.toBoard(piece.getLayoutX());
            int newY = view.toBoard(piece.getLayoutY());

            MoveResult result = tryMove(piece, newX, newY);
            int x0 = view.toBoard(piece.getOldX());
            int y0 = view.toBoard(piece.getOldY());
            Map<Coordinate, List<Coordinate>> legalMoves = model.board.generateLegalCaptureMovesForType(view.currentPlayer);
            if(model.board.emptyMoves(legalMoves))
            {
                if (result.getType() == MoveType.NONE)
                {
                    piece.abortMove();
                }
                else if (result.getType() == MoveType.NORMAL)
                {
                    piece.move(newX, newY);
                    view.boardVisual[x0][y0].setPiece(null);
                    view.boardVisual[newX][newY].setPiece(piece);
                    model.board.MovePiece(y0, x0, newY, newX);
                    // Checks if a player is now king
                    if(model.board.checkKing(newY,newX,view.currentPlayer))
                    {
                        piece.setKing();
                        model.board.kingify(newY,newX);
                        System.out.println("NEW KING " + model.board.typeOfPiece(newY, newX));
                    }
                    view.currentPlayer = view.currentPlayer == PieceType.BLACK ? PieceType.WHITE : PieceType.BLACK;
                }
                else
                {
                    piece.abortMove();
                }
            }
            else if (result.getType() == MoveType.KILL)
            {
                piece.move(newX, newY);
                view.boardVisual[x0][y0].setPiece(null);
                view.boardVisual[newX][newY].setPiece(piece);
                model.board.MovePiece(y0, x0, newY, newX);
                Piece otherPiece = result.getPiece();
                view.boardVisual[ view.toBoard(otherPiece.getOldX())][ view.toBoard(otherPiece.getOldY())].setPiece(null);
                model.board.deletePiece( view.toBoard(otherPiece.getOldY()),  view.toBoard(otherPiece.getOldX()));
                view.pieceGroup.getChildren().remove(otherPiece);
                if(model.board.checkKing(newY,newX, view.currentPlayer))
                {
                    piece.setKing();
                    model.board.kingify(newY,newX);
                    System.out.println("NEW KING " + model.board.typeOfPiece(newY, newX));
                }
                if(model.board.emptyMovesForPiece(model.board.generateLegalMovesForPieceCapture(newY, newX, piece.getType())))
                {
                    view.currentPlayer = view.currentPlayer == PieceType.BLACK ? PieceType.WHITE : PieceType.BLACK;
                }
            }
            else
            {
                piece.abortMove();
            }
            // White won
            if(model.board.checkWin(PieceType.WHITE))
            {
                System.out.println("WHITE WON");
                Scene whiteWon = new Scene(view.whiteWinScene());
                view.stageWon.setScene(whiteWon);
                view.stageWon.show();
                view.won = true;
            }
            // Black won
            else if(model.board.checkWin(PieceType.BLACK))
            {
                System.out.println("BLACK WON");
                Scene blackWon = new Scene(view.blackWinScene());
                view.stageWon.setScene(blackWon);
                view.stageWon.show();
                view.won = true;
            }
            // Tie
            else if (model.board.checkWin(PieceType.BLACK) && model.board.checkWin(PieceType.WHITE))
            {
                System.out.println("Tie");
                Scene tie = new Scene(view.tieScene());
                view.stageWon.setScene(tie);
                view.stageWon.show();
                view.won = true;
                // :)
            }
            model.board.printBoard();
            while (view.ai && view.currentPlayer == PieceType.BLACK && !view.won)
            {
                int fromRow=0;
                int fromCol=0;
                int row=0;
                int col=0;
                Piece pieceAi = null;
                legalMoves = model.board.generateLegalCaptureMovesForType(PieceType.BLACK);
                if(model.board.emptyMoves(legalMoves))
                {
                    legalMoves = model.board.generateLegalMovesForType(PieceType.BLACK);
                    if(model.board.emptyMoves(legalMoves))
                    {
                        System.out.println("No suitable moves.");
                        break;
                    }
                    for (int i = 0; i < legalMoves.size(); i++)
                    {
                        List<Coordinate> movesForPiece = legalMoves.get(i);
                        System.out.println("Legal moves for piece number " + i + ": ");
                        for (Coordinate move : movesForPiece)
                        {
                            System.out.println("[" + move.getX() + "][" + move.getY() + "] from " + "[" + move.getOldX() + "][" + move.getOldY() + "]");
                            if(model.board.typeOfPiece(move.getOldX(),move.getOldY()) == PieceType.BLACK ||  model.board.typeOfPiece(move.getOldX(),move.getOldY()) == PieceType.BLACKKING ) {
                                row = move.getX();
                                col = move.getY();
                                fromRow = move.getOldX();
                                fromCol = move.getOldY();
                                pieceAi = view.boardVisual[fromCol][fromRow].getPiece();
                                break;
                            }
                        }
                    }
                    try {
                        aiMove(fromCol, fromRow, col, row, pieceAi);
                    } catch (Exception E) {
                        System.out.println("An error occurred: " + E.getMessage());
                    }
                }
                else
                {
                    for (int i = 0; i < legalMoves.size(); i++)
                    {
                        List<Coordinate> movesForPiece = legalMoves.get(i);
                        System.out.println("Legal moves for piece number " + i + ": ");
                        for (Coordinate move : movesForPiece)
                        {
                            System.out.println("[" + move.getX() + "][" + move.getY() + "] from " + "[" + move.getOldX() + "][" + move.getOldY() + "]");
                            if(model.board.typeOfPiece(move.getOldX(),move.getOldY()) == PieceType.BLACK || model.board.typeOfPiece(move.getOldX(),move.getOldY()) == PieceType.BLACKKING ) {
                                row = move.getX();
                                col = move.getY();
                                fromRow = move.getOldX();
                                fromCol = move.getOldY();
                                pieceAi = view.boardVisual[fromCol][fromRow].getPiece();
                                if(pieceAi != null) break;
                            }
                        }
                    }
                    try {
                        aiMove(fromCol,fromRow,col,row,pieceAi);
                    } catch (Exception E) {
                        System.out.println("An error occurred: " + E.getMessage());
                    }
                }
            }
        });
        return piece;
    }
    // Checks if a piece can move to a destination
    public MoveResult tryMove(Piece piece, int newX, int newY)
    {
        int x0 = view.toBoard(piece.getOldX());
        int y0 = view.toBoard(piece.getOldY());
        PieceType checkTurn = piece.getType();
        System.out.println("THE TYPE OF THE PIECE IS " + model.board.typeOfPiece(y0,x0));
        // PROBLEM IS HERE FOR SOME REASON
        List<Coordinate> movesForPiece = model.board.generateLegalMovesForPiece(y0,x0,piece.getType());
        model.board.printLegalPieceMoves(y0,x0);
        if(piece.getType() == PieceType.WHITEKING)
        {
            checkTurn = PieceType.WHITE;
        }
        else if(piece.getType() == PieceType.BLACKKING)
        {
            checkTurn = PieceType.BLACK;
        }
        if(checkTurn == view.currentPlayer && !movesForPiece.isEmpty())
        {
            for (Coordinate move : movesForPiece)
            {
                if(move.getX() == newY && move.getY() == newX)
                {
                    // In case of a normal move
                    if(Math.abs(newX - x0) == 1)
                    {
                        System.out.println("Normal MOVE!");
                        return new MoveResult(MoveType.NORMAL);
                    }

                    else if (Math.abs(newX - x0) == 2/* && newY - y0 == piece.getType().moveDir * 2*/) {
                        int x1;
                        int y1;

                        x1 = x0 + (newX - x0) / 2;
                        y1 = y0 + (newY - y0) / 2;

                        if (view.boardVisual[x1][y1].hasPiece() && view.boardVisual[x1][y1].getPiece().getType() != piece.getType())
                        {
                            System.out.println("KILL MOVE!");
                            return new MoveResult(MoveType.KILL, view.boardVisual[x1][y1].getPiece());
                        }
                    }
                }
            }
        }
        System.out.println("No Move");
        return new MoveResult(MoveType.NONE);
    }

}
