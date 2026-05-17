package client;

import static client.ScreenDrawing.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import chess.*;
import model.GameData;
import request.*;
import result.*;
import server.Server;


public class ClientMain {
    private static ServerFacade serverFacade;
    private static Server server;

    private static String currentUser;
    private static ClientState currentState;
    private static String currentAuthToken;

    

    private static final GameData testGameData = new GameData(1, "Dylan", "Cosmo", "Battle of Champions", new ChessGame());

    private enum ClientState {
        PRELOGIN,
        POSTLOGIN,
        GAMEPLAY,
        QUIT
    }


    public static void main(String[] args) {
        

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        
        initServer();
        mainLoop();
    }
    private static void initServer(){
        server = new Server();
        var port = server.run(8080);
        serverFacade = new ServerFacade("localhost", port);
    }

    public static void mainLoop(){
        currentState = ClientState.PRELOGIN;
        currentUser = null;
        currentAuthToken = null;
        
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_16);
        String line = null;
        // drawGame(out, gameData);

        try(Scanner scanner = new Scanner(System.in)){
            
            clearScreen(out);
            out.print("Welcome to Dylan Hale's Chess Client - Type Help to see Commands\n");
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
                            // helpPreLogin(out);
                            drawGame(out, testGameData);
                        }
                        else if(line.equals("login")){
                            login(out,scanner);
                        }
                        else if(line.equals("register")){
                            register(out, scanner);
                        }
                        else if(line.equals("quit")){
                            quitProgram(out);
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
                        else if(line.equals("quit")){
                            logout(out);
                            quitProgram(out);
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
                    case QUIT:
                        break quitProgram;
                    default:
                        break;
                }
            }
        }
    }

private static void quitProgram(PrintStream out) {
    server.stop();
    clearScreen(out);
    currentState = ClientState.QUIT;
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
        try{
            currentUser = username.substring(0,1).toUpperCase() + username.substring(1).toLowerCase();
        }
        catch(Exception e){
            currentUser = username;
        }
        out.print(moveCursor(11,4));  
        String password = scanner.nextLine();
        clearScreen(out);
        out.print("""
                -- Logging In --

                """);
        try{
            LoginRequest loginRequest = new LoginRequest(username, password);
            LoginResult result = serverFacade.login(loginRequest);
            currentAuthToken = result.authToken();

            clearScreen(out);
            out.print("SUCCESS - [" + currentUser+"] has logged in\n\n");

            currentState = ClientState.POSTLOGIN;
        }
        catch(Exception e){
            clearScreen(out);
            out.print("-- FAILED TO LOGIN --\n- ");
            out.print(e.getMessage());
            out.print(" -\n\n"); 
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

        clearScreen(out);
        out.print("""

                -- Registering --

                """);

        RegisterRequest request = new RegisterRequest(username,password,email);
        try{
            RegisterResult result = serverFacade.register(request);
            currentAuthToken = result.authToken();
            clearScreen(out);
            setType(out);
            out.print("SUCCESS - [" + currentUser+"] has been registered and logged in\n\n");

            currentState = ClientState.POSTLOGIN;
        }
        catch(Exception e){
            clearScreen(out);
            out.print("-- FAILED TO REGISTER --\n- ");
            out.print(e.getMessage());
            out.print(" -\n\n");
        }
    }

    private static void logout(PrintStream out){
        LogoutRequest request = new LogoutRequest(currentAuthToken);
        try{
            serverFacade.logout(request);
            clearScreen(out);
            out.print("SUCCESS - [" + currentUser+"] has been logged out\n\n");
            currentUser = null;
            currentAuthToken = null;
            currentState = ClientState.PRELOGIN;
        }
        catch(Exception e){
            clearScreen(out);
            out.print("-- FAILED TO LOGOUT --\n- ");
            out.print(e.getMessage());
            out.print(" -\n\n");
        }

        

    }
    private static void createGame(PrintStream out, Scanner scanner){ScreenDrawing.clearScreen(out);
        ScreenDrawing.setType(out);

        out.print("""
                -- Create Game Page --

                """);
        out.print("Game Name: ");

        String gameName = scanner.nextLine();

        clearScreen(out);
        out.print("""

                -- CREATING GAME --

                """);

        CreateGameRequest createGameRequest = new CreateGameRequest(currentAuthToken, gameName);
        try{
            CreateGameResult createGameResult = serverFacade.createGame(createGameRequest);
            clearScreen(out);
            setType(out);
            int gameID = createGameResult.gameID();
            out.print("SUCCESS - Game: [" + gameName +"] has been created with Game ID: <" + gameID + ">\n\n");
        }
        catch(Exception e){
            clearScreen(out);
            out.print("-- FAILED TO CREATE GAME --\n- ");
            out.print(e.getMessage());
            out.print(" -\n\n");
        }
    }
    private static void listGames(PrintStream out,Scanner scanner){
        ListGameRequest gameRequest = new ListGameRequest(currentAuthToken);

        try{
            ListGameResult gameResult = serverFacade.listGames(gameRequest);
            out.print("------------------");
            out.println();
            for(GameData game : gameResult.games()){
                out.println("Game ID: " + game.gameID());
                out.println("Game: " + game.gameName());
                out.println("White Pieces: " + game.whiteUsername());
                out.println("Black Pieces: " + game.blackUsername());
                out.println("------------------");
            }
        }
        catch(Exception e){
            clearScreen(out);
            out.print("-- FAILED TO GET GAMES --\n- ");
            out.print(e.getMessage());
            out.print(" -\n\n");
        }
    }
    private static void joinGame(PrintStream out,Scanner scanner){
        ScreenDrawing.clearScreen(out);
        ScreenDrawing.setType(out);
        out.print("""
                -- Join Game Page --

                """);
        out.print("Game ID: \n");
        out.print("Color (White/Black): ");

        out.print(moveCursor(10,3));
        int gameID = Integer.parseInt(scanner.nextLine());
        out.print(moveCursor(22,4));  
        String color = scanner.nextLine();

        clearScreen(out);
        out.print("""

                -- JOINING GAME --

                """);

        JoinGameRequest request = new JoinGameRequest(currentAuthToken,color.toLowerCase(),gameID);
        try{
            serverFacade.joinGame(request);
            clearScreen(out);
            setType(out);
            out.print("SUCCESS - [" + currentUser+"] has joined Chess Game [" + gameID + "] as [" + color.toUpperCase() + "]\n\n");

            currentState = ClientState.POSTLOGIN;
        }
        catch(Exception e){
            clearScreen(out);
            out.print("-- FAILED TO REGISTER --\n- ");
            out.print(e.getMessage());
            out.print(" -\n\n");
        }
    }
    
    private static void observeGame(PrintStream out,Scanner scanner){

    }
}
