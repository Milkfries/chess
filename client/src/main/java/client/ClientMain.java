package client;

import static client.ScreenDrawing.clearScreen;
import static client.ScreenDrawing.drawGame;
import static client.ScreenDrawing.initStream;
import static client.ScreenDrawing.moveCursor;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGameRequest;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGameResult;
import result.LoginResult;
import result.RegisterResult;


public class ClientMain {
    private static ServerFacade serverFacade;

    private static String currentUser;
    private static ClientState currentState;
    private static String currentAuthToken;
    private static GameData currentGame = new GameData(1, "Dylan", "Cosmo", "Battle of Champions", new ChessGame());;
    private static HashMap<Integer,Integer> cachedGames;
    private static PrintStream out;
    private static Scanner scanner;


    private enum ClientState {
        PRELOGIN,
        POSTLOGIN,
        GAMEPLAY,
        QUIT
    }


    public static void main(String[] args) {
        initServer();
        initVariables();
        initStream(out);
        mainLoop();
    }
    private static void initVariables(){
        out = new PrintStream(System.out, true, StandardCharsets.UTF_16);
        scanner = new Scanner(System.in);
        cachedGames = new HashMap<>();
    }
    private static void initServer(){
        serverFacade = new ServerFacade("localhost", 0);
    }

    public static void mainLoop(){
        currentState = ClientState.PRELOGIN;
        currentUser = null;
        currentAuthToken = null;
        clearScreen();
        out.print("Welcome to Dylan Hale's Chess Client - Type help to see Commands\n");
        quitProgram: while(true){
            switch (currentState) { // state machine for UI
                case PRELOGIN:
                    preLoginOutput();
                    break;
                case POSTLOGIN:
                    postLoginOutput();
                    break;
                // case GAMEPLAY:
                //     gameplayOutput();
                //     break;
                case QUIT:
                    break quitProgram;
                default:
                    break;
            }
        }
        scanner.close();
    }
    private static void preLoginOutput(){
        out.print("[chess_client] >> ");
        String line = scanner.nextLine().toLowerCase();
        String[] command = line.split("\\s+");
        int commandCount = command.length;

        if(command[0].equals("help") && commandCount == 1){
            helpPreLogin();
        }
        else if(command[0].equals("login") && commandCount == 3){
            String username = command[1];
            String password = command[2];
            login(username,password);
        }
        else if(command[0].equals("register") && commandCount == 4){
            String username = command[1];
            String password = command[2];
            String email = command[3];
            register(username,password,email);
        }
        else if(command[0].equals("quit") && commandCount == 1){
            quitProgram();
        }
        else{
            notRecognized();
        }
    }
    private static void postLoginOutput(){
        out.print("[" + currentUser + "] >> ");
        String line = scanner.nextLine().toLowerCase();
        String[] command = line.split("\\s+");
        int commandCount = command.length;

        if(command[0].equals("help") && commandCount == 1){
            helpPostLogin();
        }
        else if(command[0].equals("logout") && commandCount == 1){
            logout();
        }
        else if(command[0].equals("create") && commandCount == 2){
            String gameName = command[1];
            createGame(gameName);
        }
        else if(command[0].equals("list") && commandCount == 1){
            listGames();
        }
        else if(command[0].equals("join") && commandCount == 3){
            String gameID = command[1];
            String gameColor = command[2];
            joinGame(gameID,gameColor);
        }
        else if(command[0].equals("observe") && commandCount == 2){
            String gameID = command[1];
            observeGame(gameID);
        }
        else if(command[0].equals("quit") && commandCount == 1){
            logout();
            quitProgram();
        }
        else{
            notRecognized();
        }
    }
    
    private static void quitProgram(){
        currentState = ClientState.QUIT;
    }
    private static void helpPreLogin() {

        out.print("""
              login <USERNAME> <PASSWORD> - to play
              register <USERNAME> <PASSWORD> <EMAIL> - to create an account
              quit - stop playing
              help - show commands

            """);
    }
    private static void helpPostLogin() {

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
    private static void notRecognized() {

        out.print("""
                  This command was not recognized, please try again.

                """);
    }
    private static void login(String username, String password) {
        try{
            LoginRequest loginRequest = new LoginRequest(username, password);
            LoginResult result = serverFacade.login(loginRequest);
            currentAuthToken = result.authToken();
            currentUser = username;
            out.print("  SUCCESS - [" + currentUser+"] has logged in\n");

            currentState = ClientState.POSTLOGIN;
        }
        catch(Exception e){
            out.print("  -- FAILED TO LOGIN --\n  - ");
            out.print(e.getMessage());
            out.print(" -\n"); 
        }
    }
    private static void register(String username, String password, String email) {
        try{
            RegisterRequest request = new RegisterRequest(username,password,email);
            RegisterResult result = serverFacade.register(request);
            currentAuthToken = result.authToken();
            currentUser = username;
            out.print("  SUCCESS - [" + currentUser+"] has been registered and logged in\n\n");

            currentState = ClientState.POSTLOGIN;
        }
        catch(Exception e){
            out.print("  -- FAILED TO REGISTER --\n  - ");
            out.print(e.getMessage());
            out.print(" -\n");
        }
    }

    private static void logout(){
        LogoutRequest request = new LogoutRequest(currentAuthToken);
        try{
            serverFacade.logout(request);
            out.print("  SUCCESS - [" + currentUser+"] has been logged out\n\n");
            currentUser = null;
            currentAuthToken = null;
            currentState = ClientState.PRELOGIN;
        }
        catch(Exception e){
            out.print("  -- FAILED TO LOGOUT --\n  - ");
            out.print(e.getMessage());
            out.print(" -\n");
        }

        

    }
    private static void createGame(String gameName){
        try{
            CreateGameRequest createGameRequest = new CreateGameRequest(currentAuthToken, gameName);
            CreateGameResult createGameResult = serverFacade.createGame(createGameRequest);
            createGameResult.gameID();
            out.print("  SUCCESS - Game: [" + gameName +"] has been created\n");
        }
        catch(Exception e){
            out.print("-- FAILED TO CREATE GAME --\n  - ");
            out.print(e.getMessage());
            out.print(" -\n");
        }
    }
    private static void listGames(){
        try{
            cachedGames.clear();
            ListGameRequest gameRequest = new ListGameRequest(currentAuthToken);
            ListGameResult gameResult = serverFacade.listGames(gameRequest);
            out.print("------------------\n");
            int gameNumber = 1;
            for(GameData game : gameResult.games()){
                out.print("  Game " + gameNumber + ": ");
                out.print(game.gameName() + " (");
                out.print("White: " + game.whiteUsername());
                out.print(", Black: " + game.blackUsername() + ")\n");

                cachedGames.put(gameNumber,game.gameID());
                gameNumber++;
            }
            out.println("------------------");
        }
        catch(Exception e){
            out.print("  -- FAILED TO GET GAMES --\n  - ");
            out.print(e.getMessage());
            out.print(" -\n");
        }
    }
    private static void joinGame(String gameIDString, String color){
        try{
            int gameID = 0;
            try{
                gameID = Integer.parseInt(gameIDString);
            }
            catch(Exception e){
                throw new Exception("Error: GameID must be an integer");
            }
            if(cachedGames.containsKey(gameID)){
                JoinGameRequest request = new JoinGameRequest(currentAuthToken,color.toLowerCase(),gameID);
                serverFacade.joinGame(request);
                // currentState = ClientState.GAMEPLAY;
                if(color.equals("white")){
                    drawGame(currentGame,TeamColor.WHITE);
                }
                else{
                    drawGame(currentGame,TeamColor.BLACK);
                }
                
            }
            else{
                throw new Exception("Error: must call list games to see games first");
            }
        }
        catch(Exception e){
            out.print("-- FAILED TO REGISTER --\n- ");
            out.print(e.getMessage());
            out.print(" -\n");
        }
    }
    private static void observeGame(String gameIDString){
        try{
            int gameID = 0;
            try{
                gameID = Integer.parseInt(gameIDString);
            }
            catch(Exception e){
                throw new Exception("Error: GameID must be an integer");
            }
            if(cachedGames.containsKey(gameID)){
                // currentState = ClientState.GAMEPLAY;
                drawGame(currentGame,TeamColor.WHITE);
            }
            else{
                throw new Exception("Error: must call list games to see games first");
            }
        }
        catch(Exception e){
            out.print("-- FAILED TO REGISTER --\n- ");
            out.print(e.getMessage());
            out.print(" -\n");
        }
    }
}
