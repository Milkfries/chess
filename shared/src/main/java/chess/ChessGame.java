package chess;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessPiece.PieceType;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        Collection<ChessMove> possibleMoves = possibleMoves(startPosition);
        ChessPiece startPiece = board.getPiece(startPosition);

        Collection<ChessMove> legalMoves = new ArrayList<>();

        if(startPiece == null){
            return legalMoves;
        }
        ChessGame.TeamColor color = startPiece.getTeamColor();        
        
        
        for(ChessMove move : possibleMoves){
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece endPiece = board.getPiece(endPosition);
            
            ChessPiece lastPiece = null;
            boolean passantFlag = isEnPassant(move);


            if(passantFlag){ // Needs to remove pawn in en passant or move second piece in castling
                ChessPosition lastPosition = board.getLastMove().getEndPosition();
                lastPiece = board.getPiece(lastPosition);
                board.addPiece(lastPosition, null); 
                
            }
            makeMoveOnBoard(move);
            if(!isInCheck(color)){
                legalMoves.add(move);
            }
            if(passantFlag){ // needs to be able to undo en Passant capture
                
                board.addPiece(board.getLastMove().getEndPosition(), lastPiece);
            }
            board.addPiece(startPosition, startPiece);
            board.addPiece(endPosition, endPiece);

        }
        

        return legalMoves;
    }

    public Collection<ChessMove> possibleMoves(ChessPosition startPosition){
        ChessPiece piece = board.getPiece(startPosition);
        
        if(piece == null){
            return new ArrayList<ChessMove>();
        }

        return piece.pieceMoves(board,startPosition);
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        boolean passantFlag = isEnPassant(move);
        ChessMove castleMove = isCastling(move);
        boolean castleFlag = castleMove == null? false : true;
        
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPosition);

        ChessPosition endPosition = move.getEndPosition();
        ChessPiece endPiece = board.getPiece(endPosition);
        ChessPiece lastPiece = null;
        if(board.getLastMove()!= null){
            board.getPiece(board.getLastMove().getEndPosition());
        }
        

        if(isInCheckmate(teamTurn)){
            throw new InvalidMoveException("Cannot Move - Game Over");
        }
        isValidPiece(piece); // checks if the piece exists and is the right color

        Collection<ChessMove> moves = validMoves(startPosition);

        if (moves.contains(move)){
            if(passantFlag){ // Needs to remove pawn in en passant or move second piece in castling
                board.addPiece(board.getLastMove().getEndPosition(), null); 
            }
            if(castleFlag){
                makeMoveOnBoard(castleMove);
            }
            makeMoveOnBoard(move);
            
        }
        else{
            throw new InvalidMoveException("Move Not Valid");
        }

        piece.setHasMoved();
        board.setLastMove(move);
        if(teamTurn == TeamColor.BLACK){
            setTeamTurn(TeamColor.WHITE);
        }
        else{
            setTeamTurn(TeamColor.BLACK);
        }
    }
    private ChessMove isCastling(ChessMove move) {
        
        // ChessPosition startPosition = move.getStartPosition();
        // ChessPosition endPosition = move.getEndPosition();
        // ChessPiece piece = board.getPiece(startPosition);

        // if(piece.getPieceType() == PieceType.ROOK){

        // }
        // else if(piece.getPieceType() == PieceType.KING){
            
        // }
        
        return null;
    }
    private boolean isEnPassant(ChessMove move){

        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);

        int[] passantDirection = startPosition.difference(endPosition); // gets the vector the piece is trying to move in
        
        //checks that the move is capturing diagonally and that it is moving into a square with no piece which is only possible in en passant
        
        if(piece != null && piece.getPieceType() == PieceType.PAWN && Math.abs(passantDirection[0]) == 1 && Math.abs(passantDirection[1]) == 1 && board.getPiece(endPosition) == null ){  
            return true;
        }
        else{
            return false;
        }
        
    }
    public void makeMoveOnBoard(ChessMove move){
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);

        board.addPiece(startPosition, null);
        PieceType promotionPiece = move.getPromotionPiece();
        if(promotionPiece == null){
            board.addPiece(endPosition, piece);
        }   
        else{
            board.addPiece(endPosition, new ChessPiece(teamTurn, promotionPiece));
        }
    }
    
    public void isValidPiece(ChessPiece piece) throws InvalidMoveException{
        if(piece == null){
            throw new InvalidMoveException("No Piece to Move");
        }
        if(piece.getTeamColor() != teamTurn){
            throw new InvalidMoveException("Move Out of Turn");
        }
        return;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);
        if(kingPosition == null){
            return false;
        }
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition position = new ChessPosition(i, j);
                Collection<ChessMove> moves = possibleMoves(position);
                for(ChessMove move : moves){
                    ChessPosition endPosition = move.getEndPosition();
                    if(endPosition.equals(kingPosition)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            ChessPosition kingPosition = getKingPosition(teamColor);
            if(kingPosition == null){
                return false;
            }
            Collection<ChessMove> allValidMoves = getAllTeamMoves(teamColor);
        
            for(ChessMove move: allValidMoves){
                ChessPosition startPosition = move.getStartPosition();
                ChessPiece startPiece = board.getPiece(startPosition);

                ChessPosition endPosition = move.getEndPosition();
                ChessPiece endPiece = board.getPiece(endPosition);
                

                makeMoveOnBoard(move);

                if(!isInCheck(teamColor)){
                    board.addPiece(startPosition, startPiece);
                    board.addPiece(endPosition, endPiece);
                    return false;
                }
                board.addPiece(startPosition, startPiece);
                board.addPiece(endPosition, endPiece);
            }
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(!isInCheck(teamColor) && getAllTeamMoves(teamColor).size()==0){
            return true;
        }
        else{
            return false;
        }
    }


    ChessPosition getKingPosition(TeamColor teamColor){
        for(int i = 1; i <= 8; i++){
                for(int j = 1; j <= 8; j++){
                    ChessPosition position = new ChessPosition(i, j);
                    ChessPiece piece = board.getPiece(position);
                    if(piece == null){
                        continue;
                    }
                    if(piece.getPieceType() == PieceType.KING && piece.getTeamColor() == teamColor){
                        return position;
                    }
                }
            }
        return null;
    }

    Collection<ChessMove> getAllTeamMoves(TeamColor teamColor){
        Collection<ChessMove> allMoves = new ArrayList<>();
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition position = new ChessPosition(i, j);
                Collection<ChessMove> moves = validMoves(position);
                for(ChessMove move : moves){
                    if(board.getPiece(move.getStartPosition()).getTeamColor()==teamColor){
                        allMoves.add(move);
                    }
                    
                }
            }
        }
        return allMoves;
    }
    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((teamTurn == null) ? 0 : teamTurn.hashCode());
        result = prime * result + ((board == null) ? 0 : board.hashCode());
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
        ChessGame other = (ChessGame) obj;
        if (teamTurn != other.teamTurn)
            return false;
        if (board == null) {
            if (other.board != null)
                return false;
        } else if (!board.equals(other.board))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ChessGame [teamTurn=" + teamTurn + ", board=" + board + "]";
    }
    
}
