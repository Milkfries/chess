package websocket.commands;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

public class MakeMoveCommand extends UserGameCommand{
    private ChessMove move;

    public MakeMoveCommand(String authToken, Integer gameID, ChessMove chessMove) {
        super(CommandType.MAKE_MOVE, authToken,gameID);
        this.move = chessMove;
    }
    public ChessPosition getStartPosition(){
        return move.getStartPosition();
    }
    public ChessPosition getEndPosition(){
        return move.getEndPosition();
    }
    public ChessPiece.PieceType getPromotionPiece() {
        return move.getPromotionPiece();
    }
    public ChessMove getMove(){
        return move;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((move == null) ? 0 : move.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object theObj) {
        if (this == theObj){
            return true;
        }
        if (theObj == null){
            return false;
        }
        if (!super.equals(theObj)){
            return false;
        }
        if (getClass() != theObj.getClass()){
            return false;
        }
        MakeMoveCommand other = (MakeMoveCommand) theObj;
        if (move == null) {
            if (other.move != null){
                return false;
            }
        } else if (!move.equals(other.move)){
            return false;
        }
        return true;
    }
}
