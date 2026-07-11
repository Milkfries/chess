package client;

import ui.ScreenDrawing;

import static ui.ScreenDrawing.drawGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import model.GameData;
import request.*;
import result.*;
import websocket.messages.ServerMessage;


public class ClientMain implements ServerMessageObserver{
    private ServerFacade serverFacade;
    private WebsocketCommunicator websocket;

    private String currentUser;
    private ClientState currentState;
    private String currentAuthToken;
    private GameData currentGame = new GameData(1, "Dylan", "Cosmo", "Battle of Champions", new ChessGame());;
    private HashMap<Integer,Integer> cachedGames;
    private PrintStream out;
    private Scanner scanner;


    private enum ClientState {
        PRELOGIN,
        POSTLOGIN,
        GAMEPLAY,
        QUIT
    }

    public void main(String[] args) {
        initVariables();
        ScreenDrawing.initStream(out);
        initServer(args[0]);
        mainLoop();
    }
    private void initVariables(){
        out = new PrintStream(System.out, true, StandardCharsets.UTF_16);
        scanner = new Scanner(System.in);
        cachedGames = new HashMap<>();
    }
    private void initServer(String port){
        serverFacade = new ServerFacade("localhost", Integer.parseInt(port));
        websocket = new WebsocketCommunicator("localhost", this);
    }

    public void mainLoop(){
        currentState = ClientState.PRELOGIN;
        currentUser = null;
        currentAuthToken = null;
        ScreenDrawing.clearScreen();
        out.print("Welcome to Dylan Hale's Chess Client - Type help to see Commands\n");
        quitProgram: while(true){
            switch (currentState) { // state machine for UI
                case PRELOGIN:
                    preLoginOutput();
                    break;
                case POSTLOGIN:
                    postLoginOutput();
                    break;
                case QUIT:
                    break quitProgram;
                default:
                    break;
            }
        }
        scanner.close();
    }
    private void preLoginOutput(){
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
    private void postLoginOutput(){
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
    
    private void quitProgram(){
        currentState = ClientState.QUIT;
    }
    private void helpPreLogin() {

        out.print("""
              login <USERNAME> <PASSWORD> - to play
              register <USERNAME> <PASSWORD> <EMAIL> - to create an account
              quit - stop playing
              help - show commands

            """);
    }
    private void helpPostLogin() {

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
    private void notRecognized() {

        out.print("""
                  This command was not recognized, please try again.

                """);
    }
    private void login(String username, String password) {
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
    private void register(String username, String password, String email) {
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

    private void logout(){
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
    private void createGame(String gameName){
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
    private void listGames(){
        try{
            cachedGames.clear();
            ListGameRequest gameRequest = new ListGameRequest(currentAuthToken);
            ListGameResult gameResult = serverFacade.listGames(gameRequest);
            out.print("------------------\n");
            int gameNumber = 1;
            for(GameData game : gameResult.games()){
                out.print("  Game " + gameNumber + ": ");
                out.print(" \"" + game.gameName() + "\" - ");
                out.print("White: {");
                if(game.whiteUsername() != null){
                    out.print(game.whiteUsername());
                }
                else{
                    out.print("empty");
                }
                out.print("} vs Black: {");
                if(game.blackUsername() != null){
                    out.print(game.blackUsername());
                }
                else{
                   out.print("empty");
                }
                out.print("}\n");

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
    private void joinGame(String gameIDString, String color){
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
    private void observeGame(String gameIDString){
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

    @Override
    public void notify(ServerMessage msg){

    }
}
