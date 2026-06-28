package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{
    private ChessMove move;

    public MakeMoveCommand(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken,gameID);
        this.move = move;
    }

    public ChessMove getChessMove(){
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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        MakeMoveCommand other = (MakeMoveCommand) obj;
        if (move == null) {
            if (other.move != null)
                return false;
        } else if (!move.equals(other.move))
            return false;
        return true;
    }

}
