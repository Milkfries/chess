package chess;

import java.util.ArrayList;
import java.util.Collection;

import javax.sound.midi.SysexMessage;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor color;
    private ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        if(type == PieceType.KING){ // Movement options for KING
            int[][] kingVectors = {{1,1},{1,0},{1,-1},{0,1},{0,-1},{-1,1},{-1,0},{-1,-1}};
            for(int i = 0; i < kingVectors.length;i++){
                ChessPosition newPosition = myPosition.add(kingVectors[i]);
                if(!newPosition.inBoard()){
                    continue;
                }
                ChessPiece pieceAtNew = board.getPiece(newPosition);
                if(pieceAtNew == null){
                    moves.add(new ChessMove(myPosition,newPosition));
                }
                else if(pieceAtNew.getTeamColor() != color){
                    moves.add(new ChessMove(myPosition,newPosition));
                }
                else if(pieceAtNew.getTeamColor() == color){
                }
                else{
                    throw new RuntimeException("King Move Error"); 
                }
            }
        }
        else if(type == PieceType.QUEEN){ // Movement options for QUEEN
            int[][] queenVectors = {{1,1},{1,0},{1,-1},{0,1},{0,-1},{-1,1},{-1,0},{-1,-1}};
            for(int i = 0; i < queenVectors.length;i++){
                for(int j = 1; j < 8; j++){
                    ChessPosition newPosition = myPosition.add(queenVectors[i],j);
                    if(!newPosition.inBoard()){
                        continue;
                    }
                    ChessPiece pieceAtNew = board.getPiece(newPosition);
                    if(pieceAtNew == null){
                        moves.add(new ChessMove(myPosition,newPosition));
                    }
                    else if(pieceAtNew.getTeamColor() != color){
                        moves.add(new ChessMove(myPosition,newPosition));
                        break;
                    }
                    else if(pieceAtNew.getTeamColor() == color){
                        break;
                    }
                    else{
                        throw new RuntimeException("Queen Move Error"); 
                    }
                }
                
            }
        }
        else if(type == PieceType.ROOK){ // Movement options for ROOK (not castling)
            int[][] rookVectors = {{1,0},{0,1},{0,-1},{-1,0}};
            for(int i = 0; i < rookVectors.length;i++){
                for(int j = 1; j < 8; j++){
                    ChessPosition newPosition = myPosition.add(rookVectors[i],j);
                    if(!newPosition.inBoard()){
                        continue;
                    }
                    ChessPiece pieceAtNew = board.getPiece(newPosition);
                    if(pieceAtNew == null){
                        moves.add(new ChessMove(myPosition,newPosition));
                    }
                    else if(pieceAtNew.getTeamColor() != color){
                        moves.add(new ChessMove(myPosition,newPosition));
                        break;
                    }
                    else if(pieceAtNew.getTeamColor() == color){
                        break;
                    }
                    else{
                        throw new RuntimeException("Rook Move Error"); 
                    }
                }
            }
        }
        else if(type == PieceType.BISHOP){ // Movement options for BISHOP
            int[][] bishopVectors = {{1,1},{-1,1},{1,-1},{-1,-1}};
            for(int i = 0; i < bishopVectors.length;i++){
                for(int j = 1; j < 8; j++){
                    ChessPosition newPosition = myPosition.add(bishopVectors[i],j);
                    if(!newPosition.inBoard()){
                        continue;
                    }
                    ChessPiece pieceAtNew = board.getPiece(newPosition);
                    if(pieceAtNew == null){
                        moves.add(new ChessMove(myPosition,newPosition));
                    }
                    else if(pieceAtNew.getTeamColor() != color){
                        moves.add(new ChessMove(myPosition,newPosition));
                        break;
                    }
                    else if(pieceAtNew.getTeamColor() == color){
                        break;
                    }
                    else{
                        throw new RuntimeException("Bishop Move Error"); 
                    }
                }
                
            }
        }
        else if(type == PieceType.KNIGHT){ // Movement options for KNIGHT
            int[][] knightVectors = {{2,1},{1,2},{-2,1},{-1,2},{2,-1},{1,-2},{-2,-1},{-1,-2}};
            for(int i = 0; i < knightVectors.length;i++){
                ChessPosition newPosition = myPosition.add(knightVectors[i]);
                if(!newPosition.inBoard()){
                    continue;
                }
                ChessPiece pieceAtNew = board.getPiece(newPosition);
                if(pieceAtNew == null){
                    moves.add(new ChessMove(myPosition,newPosition));
                }
                else if(pieceAtNew.getTeamColor() != color){
                    moves.add(new ChessMove(myPosition,newPosition));
                }
                else if(pieceAtNew.getTeamColor() == color){
                }
                else{
                    throw new RuntimeException("Knight Move Error"); 
                }
            }
        }
        else if(type == PieceType.PAWN){ // Movement options for PAWN (ignores En passant)
            int flip = 1;
            int startRow = 2;
            int promoteRow = 7;
            if(color == ChessGame.TeamColor.BLACK){ // Flips the direction if you are black
                flip = -1;       
                startRow = 7;
                promoteRow = 2;         
            }
            ChessPosition forwardOnePosition = myPosition.add(new int[]{1*flip,0});
            ChessPosition leftCapturePosition = myPosition.add(new int[]{1*flip,-1});
            ChessPosition rightCapturePosition = myPosition.add(new int[]{1*flip,1});
            if(forwardOnePosition.inBoard() && board.getPiece(forwardOnePosition) == null){ // Normal go forward 1 move
                if(myPosition.getRow() == promoteRow){
                    pawnPromotionMoves(moves,myPosition,forwardOnePosition);
                }
                else{
                    moves.add(new ChessMove(myPosition, forwardOnePosition));
                }
                if(myPosition.getRow() == startRow){ // Check if you on start square
                    ChessPosition forwardTwoPosition = myPosition.add(new int[]{2*flip,0});
                    if(board.getPiece(forwardTwoPosition) == null){
                        moves.add(new ChessMove(myPosition, forwardTwoPosition));
                    }
                }
            }
            //Checks if you can capture left or right

            if(leftCapturePosition.inBoard() && board.getPiece(leftCapturePosition) != null && board.getPiece(leftCapturePosition).getTeamColor() != color){
                if(myPosition.getRow() == promoteRow){
                    pawnPromotionMoves(moves,myPosition,leftCapturePosition);
                }
                else{
                    moves.add(new ChessMove(myPosition, leftCapturePosition));
                }  
            }
            if(rightCapturePosition.inBoard() && board.getPiece(rightCapturePosition) != null && board.getPiece(rightCapturePosition).getTeamColor() != color){
                if(myPosition.getRow() == promoteRow){
                    pawnPromotionMoves(moves,myPosition,rightCapturePosition);
                }
                else{
                    moves.add(new ChessMove(myPosition, rightCapturePosition));
                }  
            }
        }
        return moves;
    }
    
    public void pawnPromotionMoves(Collection<ChessMove> moves, ChessPosition myPosition, ChessPosition newPosition){
        moves.add(new ChessMove(myPosition, newPosition,ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(myPosition, newPosition,ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(myPosition, newPosition,ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(myPosition, newPosition,ChessPiece.PieceType.QUEEN));
    }           
                                
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        ChessPiece other = (ChessPiece) obj;
        if (color != other.color)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ChessPiece [color=" + color + ", type=" + type + "]";
    }
}
