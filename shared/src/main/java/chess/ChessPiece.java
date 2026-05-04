package chess;

import java.util.ArrayList;
import java.util.Collection;

import javax.sound.midi.SysexMessage;

import chess.ChessGame.TeamColor;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private TeamColor color;
    private PieceType type;
    private boolean hasMoved;
    public ChessPiece(TeamColor pieceColor, PieceType type) {
        this.color = pieceColor;
        this.type = type;
        hasMoved = false;
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
    public TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    public void setHasMoved(){
        hasMoved = true;
    }
    public boolean getHasMoved(){
        return hasMoved;
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
            if(!hasMoved){ // Castling Logic
                if(color == TeamColor.WHITE){ 
                    ChessPiece leftCastle = board.getPiece(new ChessPosition(1, 1));
                    ChessPiece rightCastle = board.getPiece(new ChessPosition(1, 8));
                    if(leftCastle != null && leftCastle.getPieceType() == PieceType.ROOK && !leftCastle.getHasMoved()){ // White King long castle
                        boolean spaceClear = true;
                        for(int i = 2; i <= 4; i++){
                            if(board.getPiece(new ChessPosition(1, i)) != null){
                                spaceClear = false;
                                break;
                            }
                        }
                        if(spaceClear){
                            moves.add(new ChessMove(myPosition,new ChessPosition(1, 3)));
                        }
                    }
                    if(rightCastle != null && rightCastle.getPieceType() == PieceType.ROOK && !rightCastle.getHasMoved()){ // White King short castle
                        boolean spaceClear = true;
                        for(int i = 6; i <= 7; i++){
                            if(board.getPiece(new ChessPosition(1, i)) != null){
                                spaceClear = false;
                                break;
                            }
                        }
                        if(spaceClear){
                            moves.add(new ChessMove(myPosition,new ChessPosition(1, 7)));
                        }
                    }
                }
                else{
                    ChessPiece leftCastle = board.getPiece(new ChessPosition(8, 1));
                    ChessPiece rightCastle = board.getPiece(new ChessPosition(8, 8));
                    if(leftCastle != null && leftCastle.getPieceType() == PieceType.ROOK && !leftCastle.getHasMoved()){ // Black King long castle
                        boolean spaceClear = true;
                        for(int i = 2; i <=4; i++){
                            if(board.getPiece(new ChessPosition(8, i)) != null){
                                spaceClear = false;
                                break;
                            }
                        }
                        if(spaceClear){
                            moves.add(new ChessMove(myPosition,new ChessPosition(8, 3)));
                        }
                    }
                    if(rightCastle != null && rightCastle.getPieceType() == PieceType.ROOK && !rightCastle.getHasMoved()){ // Black King short castle
                        boolean spaceClear = true;
                        for(int i = 6; i <= 7; i++){
                            if(board.getPiece(new ChessPosition(8, i)) != null){
                                spaceClear = false;
                                break;
                            }
                        }
                        if(spaceClear){
                            moves.add(new ChessMove(myPosition,new ChessPosition(8, 7)));
                        }
                    }
                }
                
            }
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
            if(!hasMoved){
                if(color == TeamColor.WHITE){
                    ChessPiece whiteKing = board.getPiece(new ChessPosition(1, 5));
                    if(whiteKing != null && !whiteKing.getHasMoved()){
                        if(myPosition == new ChessPosition(1, 1)){ // White Long Castle
                            boolean spaceClear = true;
                            for(int i = 2; i <= 4; i++){
                                if(board.getPiece(new ChessPosition(1, i)) != null){
                                    spaceClear = false;
                                    break;
                                }
                            }
                            if(spaceClear){
                                moves.add(new ChessMove(myPosition, new ChessPosition(1,4)));
                            }
                            
                        }
                        else{ // White Short Castle
                            boolean spaceClear = true;
                            for(int i = 6; i <= 7; i++){
                                if(board.getPiece(new ChessPosition(1, i)) != null){
                                    spaceClear = false;
                                    break;
                                }
                            }
                            if(spaceClear){
                                moves.add(new ChessMove(myPosition, new ChessPosition(1,6)));
                            }
                            
                        }
                    }
                }
                else{
                    ChessPiece blackKing = board.getPiece(new ChessPosition(8, 5));
                    if(blackKing != null && !blackKing.getHasMoved()){
                        if(myPosition == new ChessPosition(8, 1)){ // Black Long Castle
                            boolean spaceClear = true;
                            for(int i = 2; i <= 4; i++){
                                if(board.getPiece(new ChessPosition(8, i)) != null){
                                    spaceClear = false;
                                    break;
                                }
                            }
                            if(spaceClear){
                                moves.add(new ChessMove(myPosition, new ChessPosition(8,4)));
                            }
                            
                        }
                        else{ // Black Short Castle
                            boolean spaceClear = true;
                            for(int i = 6; i <= 7; i++){
                                if(board.getPiece(new ChessPosition(8, i)) != null){
                                    spaceClear = false;
                                    break;
                                }
                            }
                            if(spaceClear){
                                moves.add(new ChessMove(myPosition, new ChessPosition(8,6)));
                            }
                        }
                    }
                }
            }
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
            int passantRow = 5;
            if(color == TeamColor.BLACK){ // Flips the direction if you are black
                flip = -1;       
                startRow = 7;
                promoteRow = 2;    
                passantRow = 4;     
            }
            ChessPosition forwardOnePosition = myPosition.add(new int[]{1*flip,0});
            ChessPosition leftCapturePosition = myPosition.add(new int[]{1*flip,-1});
            ChessPosition rightCapturePosition = myPosition.add(new int[]{1*flip,1});
            
            if(board.getLastMovedPiece() != null && board.getLastMovedPiece().getPieceType() == PieceType.PAWN){
                ChessPosition otherPawnPosition = board.getLastMovedPosition();
                int otherRow = otherPawnPosition.getRow();
                int myRow = myPosition.getRow();
                int otherCol = otherPawnPosition.getColumn();
                int myCol = myPosition.getColumn();
                if(myRow == passantRow && otherRow == myRow){
                    if((myCol + 1 == otherCol) || (myCol -1 == otherCol)){
                        moves.add(new ChessMove(myPosition, otherPawnPosition.add(new int[]{0,flip})));
                    }
                }
            }
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
