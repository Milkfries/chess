package client;

import static ui.EscapeSequences.*;

import java.io.PrintStream;

import chess.*;
import model.GameData;

public class ScreenDrawing {

    //Chessboard Drawing
    public static void drawGame(PrintStream out, GameData game){
        clearScreen(out);
        setType(out);
        out.print("Chess game: " + game.gameName() + " -- Game: " + game.gameID() + "\n");
        out.print("White pieces: " + game.whiteUsername() + "\nvs\nBlack pieces: " + game.blackUsername());
        out.print("\n");
        setBlack(out);
        drawChessBoard(out, game.game());
        setType(out);
    }
    private static void drawChessBoard(PrintStream out, ChessGame game){
        for(int row = 8; row >0; row--){
            for(int space = 0; space <3; space++){
                for(int col = 1; col < 9; col++){
                    boolean blankRow = true;
                    if(space == 1){
                        blankRow = false;
                    }
                    printPiece(out, game, row, col,blankRow);
                }
                out.print("\n");
            }  
        }
    }
    private static void printPiece(PrintStream out, ChessGame game, int row, int col, boolean blankRow){
        ChessBoard board = game.getBoard();
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(position);

        if((row+col) % 2 == 0){ // determine if the square should be white or black
            setWhiteType(out);
        }
        else{
            setBlueType(out);
        }
        out.print(EMPTY);
        if(blankRow){
            out.print(EMPTY);
        }
        else{
            out.print(getPieceCode(piece));
        }
        out.print(EMPTY);
        setBlack(out);
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

    public static String moveCursor(int x, int y){
        return moveCursorToLocation(x,y);
    }
    public static void setType(PrintStream out){
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }
    public static void setBlueType(PrintStream out){
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_BLACK);
    }
    public static void setWhiteType(PrintStream out){
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    public static void setBlack(PrintStream out){
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    public static void setBlink(PrintStream out){
        out.print(SET_TEXT_BLINKING);
    }

    public static void clearScreen(PrintStream out){
        setBlack(out);
        out.print(ERASE_SCREEN);
        setType(out);
    }
}
