package client;

import static ui.EscapeSequences.*;

import java.io.PrintStream;

import chess.*;
import chess.ChessGame.TeamColor;
import model.GameData;

public class ScreenDrawing {
    private static PrintStream out;

    // Pass in print stream from client
    public static void initStream(PrintStream ps){
        out = ps;
    }
    //Chessboard Drawing
    public static void drawGame(GameData game, ChessGame.TeamColor color){
        // TODO Make drawGame have border that gives chess coordinates, also flip board if you are black
        clearScreen();
        setType();
        out.print("Chess Game: " + game.gameName() + "\n\n");
        out.print("  White pieces: " + game.whiteUsername() + "\n          vs\n  Black pieces: " + game.blackUsername());
        out.print("\n");
        setBlack();
        drawChessBoard(game.game(),color == TeamColor.WHITE ? false : true);
        setType();
    }
    private static void drawChessBoard(ChessGame game, boolean flipBoard){
        for(int row = 8; row > 0; row--){
            for(int space = 0; space <3; space++){
                for(int col = 1; col < 9; col++){
                    boolean blankRow = true;
                    if(space == 1){
                        blankRow = false;
                    }
                    printPiece(game, row, col,blankRow);
                }
                out.print("\n");
            }  
        }
        setType();
    }
    private static void printPiece(ChessGame game, int row, int col, boolean blankRow){
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
            out.print(EMPTY);
        }
        else{
            out.print(getPieceCode(piece));
        }
        out.print(EMPTY);
        setBlack();
    }
    private static String getPieceCode(ChessPiece piece){
        if(piece == null){
            return EMPTY;
        }
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
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

    // Basic drawing functions

    // public static String moveCursor(int x, int y){
    //     return moveCursorToLocation(x,y);
    // }
    public static void setType(){
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }
    public static void setBlueType(){
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_BLACK);
    }
    public static void setWhiteType(){
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    public static void setBlack(){
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    public static void setBlink(){
        out.print(SET_TEXT_BLINKING);
    }

    public static void clearScreen(){
        setBlack();
        out.print(ERASE_SCREEN);
        setType();
    }
}
