package com.example.poddavki_project;

import javafx.scene.Scene;

import java.util.List;
import java.util.Map;

public class Presenter implements IPresenter
{
    public View view;
    public Model model;

    public Ai ai;
    public Presenter(View view)
    {
        model = new Model();
        this.view = view;
        this.ai = new Ai(model, this);
    }

    // Creates a new object of a piece
    public Piece makePiece(PieceType type, int x, int y)
    {
        Piece piece = new Piece(type, x, y);
        piece.setOnMouseReleased(e ->
        {
            System.out.println("TURN: " + view.currentPlayer);
            movePiece(piece);
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
                List<Coordinate> bestMove = ai.getBestMovesForType(PieceType.BLACK);    // Gets the best move for the AI
                moveAi(bestMove);
                if(model.board.checkWin(PieceType.WHITE))
                {
                    System.out.println("WHITE WON");
                    Scene whiteWon = new Scene(view.whiteWinScene());
                    view.stageWon.setScene(whiteWon);
                    view.stageWon.show();
                    view.won = true;
                }
            }
        });
        return piece;
    }

    private void moveAi(List<Coordinate> bestMove)
    {
        for(Coordinate bestMoveCoord : bestMove)
        {
            moveAiPiece(view.boardVisual[bestMoveCoord.getOldY()][bestMoveCoord.getOldX()].getPiece(), bestMoveCoord);
            System.out.println("AI MOVED X: " + bestMoveCoord.getX() + " Y: " + bestMoveCoord.getY());
        }
    }

    public void movePiece(Piece piece)
    {
        System.out.println("MOVING " + view.currentPlayer);
        model.board.generateLegalCaptureMovesForType(view.currentPlayer);
        int newX = view.toBoard(piece.getLayoutX());
        int newY = view.toBoard(piece.getLayoutY());

        MoveResult result = tryMove(piece, newX, newY);
        int x0 = view.toBoard(piece.getOldX());
        int y0 = view.toBoard(piece.getOldY());
        Map<Coordinate, List<List<Coordinate>>> legalMoves = model.board.generateLegalCaptureMovesForType(view.currentPlayer);
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
    }

    public void moveAiPiece(Piece piece, Coordinate coord)
    {
        System.out.println("MOVING " + view.currentPlayer);
        model.board.generateLegalCaptureMovesForType(view.currentPlayer);
        int newX = coord.getY(); /*col*/
        int newY = coord.getX(); /*row*/

        // Problem is here
        MoveResult result = tryMove(piece, newX, newY);
        int x0 = coord.getOldY(); /*col*/
        int y0 = coord.getOldX(); /*row*/

        if(Math.abs(newY - y0) > 1)
        {
            piece.move(newX, newY);
            view.boardVisual[x0][y0].setPiece(null);
            view.boardVisual[newX][newY].setPiece(piece);
            int[] eatenPiece = model.board.EatPiece(y0, x0, newY, newX);
           // model.board.MovePiece(y0, x0, newY, newX);
            //Piece otherPiece = result.getPiece();
            Piece otherPiece = view.boardVisual[eatenPiece[1]][eatenPiece[0]].getPiece();

            if(otherPiece == null)
            {
                System.out.println("ERROR");
            }
            view.boardVisual[view.toBoard(otherPiece.getOldX())][view.toBoard(otherPiece.getOldY())].setPiece(null);
            //model.board.deletePiece(view.toBoard(otherPiece.getOldY()), view.toBoard(otherPiece.getOldX()));
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
        if(Math.abs(newY - y0) == 1)
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
            System.out.println("ERROR");
        }
    }

    public MoveResult tryMove(Piece piece, int newCol /*Col*/, int newRow /*Row*/)
    {
        // row: newY y0 x, col: newX x0 y.
        int oldCol = view.toBoard(piece.getOldX()); //col
        int oldRow = view.toBoard(piece.getOldY()); //row
        PieceType checkTurn = piece.getType();
        System.out.println("THE TYPE OF THE PIECE IS " + model.board.typeOfPiece(oldRow,oldCol));
        // PROBLEM IS HERE FOR SOME REASON
        List<List<Coordinate>> movesForPiece = model.board.generateLegalMovesForPiece(oldRow,oldCol,piece.getType());
        model.board.printLegalPieceMoves(oldRow,oldCol);
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
            for (List<Coordinate> move : movesForPiece)
            {
                for(Coordinate moveDir : move)
                {
                    if(moveDir.getX() == newRow && moveDir.getY() == newCol)
                    {
                        // In case of a normal move
                        if(Math.abs(newRow - oldRow) == 1)
                        {
                            System.out.println("Normal MOVE!");
                            return new MoveResult(MoveType.NORMAL);
                        }

                        else if (Math.abs(newRow - oldRow) == 2/* && newY - y0 == piece.getType().moveDir * 2*/) {
                            int x1;
                            int y1;

                            x1 = oldCol + (newCol - oldCol) / 2;
                            y1 = oldRow + (newRow - oldRow) / 2;

                            if (view.boardVisual[x1][y1].hasPiece() && view.boardVisual[x1][y1].getPiece().getType() != piece.getType())
                            {
                                System.out.println("KILL MOVE!");
                                return new MoveResult(MoveType.KILL, view.boardVisual[x1][y1].getPiece());
                            }
                        }
                    }
                }
            }
        }
        System.out.println("No Move");
        return new MoveResult(MoveType.NONE);
    }
}
