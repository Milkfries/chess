package client;

import static client.ScreenDrawing.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import server.Server;
import chess.*;
import model.GameData;


public class ClientMain {
    private static Server server;
    private static ServerFacade serverFacade;
    private static String currentUser;
    private static ClientState currentState;

    private static final GameData testGameData = new GameData(1, "Dylan", "Cosmo", "Battle of Champions", new ChessGame());

    private enum ClientState {
        PRELOGIN,
        POSTLOGIN,
        GAMEPLAY
    }


    public static void main(String[] args) {
        

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        initServer();
        // server.init();

        mainLoop();
    }
    private static void initServer(){
        // server = new ServerFacade("localhost", "3000");
        // server.run();
    }
    public static void mainLoop(){
        currentState = ClientState.PRELOGIN;
        currentUser = null;
        
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_16);
        String line = null;
        // drawGame(out, gameData);

        try(Scanner scanner = new Scanner(System.in)){
            
            clearScreen(out);
            quitProgram: while(true){
                setType(out);
                switch (currentState) { // state machine for UI
                    case PRELOGIN:
                        // 
                        // START PRELOGIN SCREEN
                        //

                        out.print("Chess Client >> ");
                        line = scanner.nextLine().toLowerCase();

                        if(line.equals("help")){
                            helpPreLogin(out);
                        }
                        else if(line.equals("login")){
                            login(out,scanner);
                        }
                        else if(line.equals("register")){
                            register(out, scanner);
                        }
                        else if(line.equals("quit")){
                            break quitProgram;
                        }
                        else{
                            notRecognized(out);
                        }
                        


                        // 
                        // END PRELOGIN SCREEN
                        //
                        break;
                    case POSTLOGIN:
                        // 
                        // START POSTLOGIN SCREEN
                        //

                        out.print("[" + currentUser + "] >> ");
                        line = scanner.nextLine().toLowerCase();

                        if(line.equals("help")){
                            helpPostLogin(out);
                        }
                        else if(line.equals("logout")){
                            logout(out);
                        }
                        else if(line.equals("create game") || line.equals("cg")){
                            createGame(out,scanner);
                        }
                        else if(line.equals("list games") || line.equals("lg")){
                            listGames(out,scanner);
                        }
                        else if(line.equals("join game") || line.equals("jg")){
                            joinGame(out,scanner);
                        }
                        else if(line.equals("observe game") || line.equals("og")){
                            observeGame(out,scanner);
                        }
                        else{
                            notRecognized(out);
                        }

                        // 
                        // END POSTLOGIN SCREEN
                        //
                        break;

                    case GAMEPLAY:
                        drawGame(out, testGameData);
                        break;

                    default:
                        break;
                }
            }
        }
    }

    private static void helpPreLogin(PrintStream out) {

        out.print("""

                -- Valid commands --

                Help - Displays commands you can run
                Quit - Ends the program
                Login - Logs into the chess server
                Register - Make a new account in the server

                """);
    }
    private static void helpPostLogin(PrintStream out) {

        out.print("""

                -- Valid commands --

                Help - Displays commands you can run
                Logout - Logs out of the server
                Create Game - Create a new chess game
                List Games - See a list of all current chess games
                Join Game - Join an existing chess game
                Observe Game - Watch a current chess game

                """);
    }
    private static void notRecognized(PrintStream out) {

        out.print("""

                This command was not recognized, please try again.

                """);
    }
    private static void login(PrintStream out, Scanner scanner) {
        clearScreen(out);

        out.print("""
                -- Login Page --

                """);

        out.print("Username: \n");
        out.print("Password: ");

        out.print(moveCursor(11,3));
        String username = scanner.nextLine();
        currentUser = username.substring(0,1).toUpperCase() + username.substring(1).toLowerCase();

        out.print(moveCursor(11,4));  
        String password = scanner.nextLine();

        setBlink(out);
        out.print("""

                -- Logging In --

                """);
        setType(out);
        scanner.nextLine();
        


        boolean loginSuccess = true;  // Call the http with username and password
        
        // TODO change this to switch based on the error code
        if(loginSuccess){
            clearScreen(out);
            out.print("SUCCESS - [" + currentUser+"] has logged in\n\n");

            currentState = ClientState.POSTLOGIN;
        }
        else{
            out.print("""

                -- FAILED TO LOGIN --

                """);
        }
    }
    private static void register(PrintStream out, Scanner scanner) {
        ScreenDrawing.clearScreen(out);
        ScreenDrawing.setType(out);

        out.print("""
                -- Registration Page --

                """);
        out.print("Username: \n");
        out.print("Password: \n");
        out.print("Email: ");
        out.print(moveCursor(11,3));
        String username = scanner.nextLine();
        currentUser = username.substring(0,1).toUpperCase() + username.substring(1).toLowerCase();
        out.print(moveCursor(11,4));  
        String password = scanner.nextLine();
        out.print(moveCursor(8,5));  
        String email = scanner.nextLine();

        setBlink(out);
        out.print("""

                -- Registering --

                """);

        scanner.nextLine();


        boolean registerSuccess = true;  // Call the http with username and password
        
        // TODO change this to switch based on the error code
        if(registerSuccess){
            clearScreen(out);
            setType(out);
            out.print("SUCCESS - [" + currentUser+"] has been registered and logged in\n\n");

            currentState = ClientState.POSTLOGIN;
        }
        else{
            out.print("""

                -- FAILED TO REGISTER --

                """);
        }
    }

    private static void logout(PrintStream out){
        //TODO make this connect to https and fail with error codes

        try{
            clearScreen(out);
            out.print("SUCCESS - [" + currentUser+"] has been logged out\n\n");
            currentUser = null;
            currentState = ClientState.PRELOGIN;
        }
        catch(Exception e){
            out.print("""

                -- FAILED TO LOGOUT --

                """);
        }

        

    }
    private static void createGame(PrintStream out, Scanner scanner){

    }
    private static void listGames(PrintStream out,Scanner scanner){
    
    }
    private static void joinGame(PrintStream out,Scanner scanner){

    }
    
    private static void observeGame(PrintStream out,Scanner scanner){

    }
}
