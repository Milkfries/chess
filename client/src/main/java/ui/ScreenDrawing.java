package ui;

import static ui.EscapeSequences.*;

import java.io.PrintStream;

import chess.*;
import chess.ChessGame.TeamColor;
import model.GameData;

public class ScreenDrawing {
    private final String[] BOARD_LETTERS = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
    private PrintStream out;

    // Pass in print stream from client
    public ScreenDrawing(PrintStream ps){
        out = ps;
    }
    public void clearScreen(){
        setBlack();
        out.print(ERASE_SCREEN);
        setType();
    }
    //Chessboard Drawing
    public void drawGame(GameData game, ChessGame.TeamColor color){
        clearScreen();
        setType();
        out.print("Chess Game: " + game.gameName() + "\n\n");
        out.print("  White pieces: " + game.whiteUsername() + "\n          vs\n  Black pieces: " + game.blackUsername());
        out.print("\n");
        setBlack();
        drawChessBoard(game.game(),color == TeamColor.WHITE ? false : true);
        setType();
    }
    private void drawChessBoard(ChessGame game, boolean flipBoard){// Printing
        int rowStart = flipBoard ? 1 : 8;
        int rowEnd = flipBoard ? 8 : 1;
        int rowStep = flipBoard ? 1 : -1;

        printEdge(flipBoard);
        // Flips the direction it reads board if from black's perspective
        for(int row = rowStart; flipBoard ? (row <= rowEnd) : (row >= rowEnd) ; row+=rowStep){ 
            for(int space = 0; space < 3; space++){
                boolean blankRow = space != 1 ? true : false;
                printEdgeBox(row, row, blankRow, flipBoard);
                for(int col = 8; col > 0; col--){
                    printPiece(game, row, col,blankRow);
                }
                printEdgeBox(row, 0, blankRow, flipBoard);
                out.print("\n");
            }  
        }
        printEdge(flipBoard);

        setType();
    }
    private void printPiece(ChessGame game, int row, int col, boolean blankRow){
        ChessBoard board = game.getBoard();
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(position);

        if((row+col) % 2 == 1){ // determine if the square should be white or black
            setWhiteType();
        }
        else{
            setBlueType();
        }
        out.print(EMPTY);
        if(blankRow){
            out.print(EMPTY_BIG);
        }
        else{
            out.print(getPieceCode(piece));
            setRegular();
        }
        out.print(EMPTY);
        setBlack();
    }
    private String getPieceCode(ChessPiece piece){
        if(piece == null){
            return EMPTY_BIG;
        }
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            setBold();
            switch (piece.getPieceType()) {
                case ChessPiece.PieceType.PAWN:
                    return WHITE_PAWN;
                case ChessPiece.PieceType.ROOK:
                    return WHITE_ROOK;
                case ChessPiece.PieceType.KNIGHT:
                    return WHITE_KNIGHT;
                case ChessPiece.PieceType.BISHOP:
                    return WHITE_BISHOP;
                case ChessPiece.PieceType.QUEEN:
                    return WHITE_QUEEN;
                case ChessPiece.PieceType.KING:
                    return WHITE_KING;
                default:
                    return null;
            }
        }
        else{
            setBold();
            switch (piece.getPieceType()) {
                case ChessPiece.PieceType.PAWN:
                    return BLACK_PAWN;
                case ChessPiece.PieceType.ROOK:
                    return BLACK_ROOK;
                case ChessPiece.PieceType.KNIGHT:
                    return BLACK_KNIGHT;
                case ChessPiece.PieceType.BISHOP:
                    return BLACK_BISHOP;
                case ChessPiece.PieceType.QUEEN:
                    return BLACK_QUEEN;
                case ChessPiece.PieceType.KING:
                    return BLACK_KING;
                default:
                    return null;
            }
        }
    }
    private void printEdge(boolean flipBoard){
        int rowStart = flipBoard ? 8 : -1;
        int rowEnd = flipBoard ? -1 : 8;
        int rowStep = flipBoard ? -1 : 1;

        for(int j = 0; j < 3; j++){
            for(int i = rowStart; flipBoard ? (i >= rowEnd) : (i <= rowEnd); i+=rowStep){
                boolean blankRow = j != 1 ? true : false; 
                printEdgeBox(0,i,blankRow,flipBoard);
            }
            out.print("\n");
        }
    }
    private void printEdgeBox(int row, int col, boolean blankRow, boolean flipBoard){
        setGreyType();
        out.print(EMPTY);
        if(!blankRow){
            setBold();
            if(row == 0){
                try{
                    out.print(BOARD_LETTERS[col]);
                }
                catch (Exception e){
                    out.print(EMPTY_BIG);
                }
                
            }
            else{
                out.print(" " + row + " ");
            }
            setRegular();
        }
        else{
            out.print(EMPTY_BIG);
        }
        out.print(EMPTY);
        setBlack();
    }

    // Basic drawing functions
    private void setType(){
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }
    private void setBlueType(){
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_BLACK);
    }
    private void setGreyType(){
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }
    private void setWhiteType(){
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);
    }
    private void setBold(){
        out.print(SET_TEXT_BOLD);
    }
    private void setRegular(){
        out.print(RESET_TEXT_BOLD_FAINT);
    }
    private void setBlack(){
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}
