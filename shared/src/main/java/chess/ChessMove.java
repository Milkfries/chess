package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private ChessPosition start;
    private ChessPosition end;
    private ChessPiece.PieceType promotionType;
    private ChessMove otherMove;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.start = startPosition;
        this.end = endPosition;
        this.promotionType = promotionPiece;
        this.otherMove = null;
    }
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this.start = startPosition;
        this.end = endPosition;
        this.promotionType = null;
        this.otherMove = null;
    }
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessMove otherMove) {
        this.start = startPosition;
        this.end = endPosition;
        this.promotionType = null;
        this.otherMove = otherMove;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return start;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return end;
    }
    
    public ChessMove getOtherMove(){
        return otherMove;
    }
    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((promotionType == null) ? 0 : promotionType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChessMove other = (ChessMove) obj;
        if (start == null) {
            if (other.start != null)
                return false;
        } else if (!start.equals(other.start))
            return false;
        if (end == null) {
            if (other.end != null)
                return false;
        } else if (!end.equals(other.end))
            return false;
        if (promotionType != other.promotionType)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ChessMove [start=" + start + ", end=" + end + ", promotionType=" + promotionType + "]";
    }
    

    
}
