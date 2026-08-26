package client;

import ui.ScreenDrawing;


import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

import com.google.gson.Gson;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;
import model.GameData;
import request.*;
import result.*;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ServerMessageType;


public class Client implements ServerMessageObserver{
    private ServerFacade serverFacade;
    private WebsocketCommunicator websocket;
    private String port;
    private String hostName;
    private String currentUser;
    private ClientState currentState;
    private ChessGame.TeamColor currentColor;
    private String currentAuthToken;
    private GameData currentGame;
    private int currentGameID;
    private HashMap<Integer,GameData> cachedGames;
    private PrintStream out;
    private Scanner scanner;
    private ScreenDrawing screenDraw;
    private Gson serializer;


    private enum ClientState {
        PRELOGIN,
        POSTLOGIN,
        GAMEPLAY,
        QUIT
    }
    public Client(String hostName, String port) {
        this.port = port;
        this.hostName = hostName;
        initVariables();
        initServer();
    }
    public void run(){
        mainLoop();
    }
    private void initVariables(){
        this.serializer = new Gson();
        out = new PrintStream(System.out, true, StandardCharsets.UTF_16);
        screenDraw = new ScreenDrawing(out);
        scanner = new Scanner(System.in);
        cachedGames = new HashMap<>();
    }
    private void initServer(){

        serverFacade = new ServerFacade(hostName, Integer.parseInt(port));
        try{
            websocket = new WebsocketCommunicator(hostName, port, this);
        }
        catch(Exception e){
            out.print("  -- Websocket Error --\n  - ");
            out.print(e.getMessage());
            out.print(" -\n");
        }
        
    }

    public void mainLoop(){
        currentState = ClientState.PRELOGIN;
        currentGame = null;
        currentUser = null;
        currentColor = null;
        currentAuthToken = null;
        screenDraw.clearScreen();
        out.print("Welcome to Dylan Hale's Chess Client - Type help to see Commands\n");
        quitProgram: while(true){
            switch (currentState) { // state machine for UI
                case PRELOGIN:
                    preLoginOutput();
                    break;
                case POSTLOGIN:
                    postLoginOutput();
                    break;
                case GAMEPLAY:
                    gameplay();
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
    private void gameplay(){
        out.print("[" + currentUser + "] >> ");
        String line = scanner.nextLine().toLowerCase();
        String[] command = line.split("\\s+");
        int commandCount = command.length;

        if(command[0].equals("help") && commandCount == 1){
            helpGameplay();
        }
        else if(command[0].equals("redraw") && commandCount == 1){
            redrawBoard();
        }
        else if(command[0].equals("move") && commandCount == 3){
            String startPosition = command[1];
            String endPosition = command[2];
            makeMove(startPosition,endPosition);
        }
        else if(command[0].equals("show") && commandCount == 2){
            String piecePosition = command[1];
            showMoves(piecePosition);
        }
        else if(command[0].equals("leave") && commandCount == 1){
            leaveGame();
        }
        else if(command[0].equals("resign") && commandCount == 1){
            resignGame();
        }
        else if(command[0].equals("quit") && commandCount == 1){
            leaveGame();
            logout();
            quitProgram();
        }
        else{
            notRecognized();
        }
    }
    private void initGameplay(){

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
    private void helpGameplay(){
        out.print("""
              redraw - redraw the chessboard
              move <STARTSQUARE*> <ENDSQUARE*> - move a piece from a square to another square
              show <SQUARE*> - show all legal moves of a piece
              leave - leave the game
              resign - resign from the game
              help - show commands

              * SQUAREs should be formatted as a number and a letter to represent the square
              on the board i.e. a1 d6 h8 *

            """);
    }
    private void notRecognized() {
        out.print("""
                  This command was not recognized, type help to see commands.

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

                cachedGames.put(gameNumber,game);
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
    private void joinGame(String gameNumberString, String color){
        TeamColor tempColor;
        try{
            if(color.equals("white")){
                tempColor = TeamColor.WHITE;
            }
            else if(color.equals("black")){
                tempColor = TeamColor.BLACK;
            }
            else {
                throw new Exception("Error: Color must be WHITE or BLACK");
            }
            
            int gameID = 0;
            GameData game = null;
            try{
                game = cachedGames.get(Integer.parseInt(gameNumberString));
                gameID = game.gameID();
            }
            catch(Exception e){
                throw new Exception("Error: GameID must be an integer");
            }
            if(cachedGames.containsKey(gameID)){
                JoinGameRequest request = new JoinGameRequest(currentAuthToken,color.toLowerCase(),gameID);
                serverFacade.joinGame(request);
                currentState = ClientState.GAMEPLAY;
                currentColor = tempColor;
                currentGame = game;
                currentGameID = gameID;
                screenDraw.drawGame(currentGame, currentColor);
                initGameplay();
            }
            else{
                throw new Exception("Error: must call list games to see games first");
            }
        }
        catch(Exception e){
            out.print("-- FAILED TO JOIN GAME --\n- ");
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
                screenDraw.drawGame(currentGame,TeamColor.WHITE);
            }
            else{
                throw new Exception("Error: must call list games to see games first");
            }
        }
        catch(Exception e){
            out.print("-- FAILED TO OBSERVE GAME --\n- ");
            out.print(e.getMessage());
            out.print(" -\n");
        }
    }
    private void redrawBoard(){
        screenDraw.drawGame(currentGame,currentColor);
    }
    private void makeMove(String startPositionString, String endPositionString){
        try{
            ChessPosition startPosition = createChessPosition(startPositionString);
            ChessPosition endPosition = createChessPosition(endPositionString);
            ChessMove chessMove = new ChessMove(startPosition, endPosition);
            out.print(chessMove);
            websocket.makeMove(currentAuthToken,currentGame.gameID(),chessMove);
            // TODO implement make move actions
        }
        catch(Exception e){
            out.print("-- FAILED TO MAKE MOVE --\n- ");
            out.print(e.getMessage());
            out.print(" -\n");
        }
        
    }
    private void showMoves(String piecePositionString){
        try{
            ChessPosition piecePosition = createChessPosition(piecePositionString);
            Collection<ChessMove> possibleMoves = currentGame.game().possibleMoves(piecePosition);
            possibleMoves.add(new ChessMove(piecePosition, null));
            screenDraw.drawGame(currentGame,currentColor, possibleMoves);
            // call get moves from chess directly, no call to websocket
        }
        catch (Exception e){
            out.print("-- FAILED TO SHOW MOVES --\n- ");
            out.print(e.getMessage());
            out.print(" -\n");
        }
        
    }
    private void resignGame(){
        try{
            websocket.resign(currentAuthToken,currentGame.gameID());
            // TODO implement resign actions (screen saying you lost? idk)
        }
        catch(Exception e){
            out.print("-- FAILED TO RESIGN --\n- ");
            out.print(e.getMessage());
            out.print(" -\n");
        }
    }
    private void leaveGame(){
        try{
            websocket.leave(currentAuthToken, currentGame.gameID());
            currentGame = null;
            currentColor = null;
            currentState = ClientState.POSTLOGIN;
        }
        catch(Exception e){
            out.print("-- FAILED TO LEAVE --\n- ");
            out.print(e.getMessage());
            out.print(" -\n");
        }
    }

    private ChessPosition createChessPosition(String positionString) throws Exception{
        if(positionString.length() != 2){
            throw new Exception("Error: Not a valid position");
        }
        int row = (int) positionString.charAt(1) - '0'; 
        int col = (int) positionString.charAt(0) - 'a' + 1;
        if (row >= 1 && row <= 8 && row >= 1 && row <= 8){
            return new ChessPosition(row, col);
        }
        else{
            throw new Exception("Error: Not a valid position");
        }
    }
    @Override
    public void notify(String msg){
        ServerMessage notification = serializer.fromJson(msg, ServerMessage.class);

        ServerMessageType messageType = notification.getServerMessageType();
        if(messageType.equals(ServerMessageType.LOAD_GAME)){
            LoadGameMessage loadGameMessage = serializer.fromJson(msg, LoadGameMessage.class);
            currentGame = loadGameMessage.getGameData();
        }
    }
}
