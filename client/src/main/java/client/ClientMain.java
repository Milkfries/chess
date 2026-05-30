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
        String[] command = null;
        int commandCount = 0;
        // drawGame(out, gameData);

        try(Scanner scanner = new Scanner(System.in)){
            
            clearScreen(out);
            out.print("Welcome to Dylan Hale's Chess Client - Type help to see Commands\n");
            quitProgram: while(true){
                setType(out);
                switch (currentState) { // state machine for UI
                    case PRELOGIN:
                        // 
                        // START PRELOGIN SCREEN
                        //

                        out.print("[chess_client] >> ");
                        line = scanner.nextLine().toLowerCase();
                        command = line.split("\\s+");
                        commandCount = command.length;

                        if(command[0].equals("help") && commandCount == 1){
                            helpPreLogin(out);
                        }
                        else if(command[0].equals("login") && commandCount == 3){
                            String username = command[1];
                            String password = command[2];
                            login(out,username,password);
                        }
                        else if(command[0].equals("register") && commandCount == 4){
                            String username = command[1];
                            String password = command[2];
                            String email = command[3];
                            register(out,username,password,email);
                        }
                        else if(command[0].equals("quit") && commandCount == 1){
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
                            quitLogoutProgram(out);
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
    currentState = ClientState.QUIT;
}
private static void quitLogoutProgram(PrintStream out) {
    logout(out);
    server.stop();
    currentState = ClientState.QUIT;
}
 private static void helpPreLogin(PrintStream out) {

        out.print("""
            login <USERNAME> <PASSWORD> - to play
            register <USERNAME> <PASSWORD> <EMAIL> - to create an account
            quit - stop playing
            help - show commands

            """);
    }
    private static void helpPostLogin(PrintStream out) {

        out.print("""
            create <NAME> - make new game
            list - see all games
            join <ID> <WHITE|BLACK> - join game as white or black
            observe <ID> - watch a game
            logout - log out of account
            quit - stop playing
            help - show commands

            """);
    }
    private static void notRecognized(PrintStream out) {

        out.print("""
                This command was not recognized, please try again.

                """);
    }
    private static void login(PrintStream out, String username, String password) {
        try{
            LoginRequest loginRequest = new LoginRequest(username, password);
            LoginResult result = serverFacade.login(loginRequest);
            currentAuthToken = result.authToken();
            currentUser = username;
            out.print("SUCCESS - [" + currentUser+"] has logged in\n");

            currentState = ClientState.POSTLOGIN;
        }
        catch(Exception e){
            out.print("-- FAILED TO LOGIN --\n- ");
            out.print(e.getMessage());
            out.print(" -\n"); 
        }
    }
    private static void register(PrintStream out, String username, String password, String email) {
        try{
            RegisterRequest request = new RegisterRequest(username,password,email);
            RegisterResult result = serverFacade.register(request);
            currentAuthToken = result.authToken();
            currentUser = username;
            out.print("SUCCESS - [" + currentUser+"] has been registered and logged in\n\n");

            currentState = ClientState.POSTLOGIN;
        }
        catch(Exception e){
            out.print("-- FAILED TO REGISTER --\n- ");
            out.print(e.getMessage());
            out.print(" -\n");
        }
    }

    private static void logout(PrintStream out){
        LogoutRequest request = new LogoutRequest(currentAuthToken);
        try{
            serverFacade.logout(request);
            currentUser = null;
            out.print("SUCCESS - [" + currentUser+"] has been logged out\n\n");
            currentUser = null;
            currentAuthToken = null;
            currentState = ClientState.PRELOGIN;
        }
        catch(Exception e){
            out.print("-- FAILED TO LOGOUT --\n- ");
            out.print(e.getMessage());
            out.print(" -\n");
        }

        

    }
    private static void createGame(PrintStream out, Scanner scanner){
        

        out.print("""
                -- Create Game Page --

                """);
        out.print("Game Name: ");

        String gameName = scanner.nextLine();

        out.print("-- CREATING GAME --");

        CreateGameRequest createGameRequest = new CreateGameRequest(currentAuthToken, gameName);
        try{
            CreateGameResult createGameResult = serverFacade.createGame(createGameRequest);
            setType(out);
            int gameID = createGameResult.gameID();
            out.print("SUCCESS - Game: [" + gameName +"] has been created with Game ID: <" + gameID + ">\n\n");
        }
        catch(Exception e){
            out.print("-- FAILED TO CREATE GAME --\n- ");
            out.print(e.getMessage());
            out.print(" -\n");
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
            out.print("-- FAILED TO GET GAMES --\n- ");
            out.print(e.getMessage());
            out.print(" -\n");
        }
    }
    private static void joinGame(PrintStream out,Scanner scanner){
        ScreenDrawing.setType(out);
        out.print("-- Join Game Page --");
        out.print("Game ID: \n");
        out.print("Color (White/Black): ");

        out.print(moveCursor(10,3));
        int gameID = Integer.parseInt(scanner.nextLine());
        out.print(moveCursor(22,4));  
        String color = scanner.nextLine();

        out.print("-- JOINING GAME --");

        JoinGameRequest request = new JoinGameRequest(currentAuthToken,color.toLowerCase(),gameID);
        try{
            serverFacade.joinGame(request);
            setType(out);
            out.print("SUCCESS - [" + currentUser+"] has joined Chess Game [" + gameID + "] as [" + color.toUpperCase() + "]\n\n");

            currentState = ClientState.POSTLOGIN;
        }
        catch(Exception e){
            out.print("-- FAILED TO REGISTER --\n- ");
            out.print(e.getMessage());
            out.print(" -\n");
        }
    }
    
    private static void observeGame(PrintStream out,Scanner scanner){

    }
}
