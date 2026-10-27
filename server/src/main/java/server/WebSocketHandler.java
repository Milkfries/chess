package server;

import org.eclipse.jetty.websocket.api.Session;
import com.google.gson.Gson;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import server.ConnectionManager.BroadcastType;
import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler{
    private final ConnectionManager connections = new ConnectionManager();
    
    private UserService userService;
    private GameService gameService;

    public WebSocketHandler(UserService userService, GameService gameService){
        this.userService = userService;
        this.gameService = gameService;
    }
    
    @Override
    public void handleConnect(WsConnectContext ctx){
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx){
        UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        try{
            switch (command.getCommandType()){
                case CONNECT:
                    connect(command, ctx.session);
                    break;
                case MAKE_MOVE:
                    MakeMoveCommand moveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(moveCommand, ctx.session);
                    break;
                case LEAVE:
                    leave(command, ctx.session);
                    break;
                case RESIGN:
                    resign(command, ctx.session);
                    break;
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            ServerMessage msg = new ErrorMessage(e.getMessage().toString());
            connections.broadcast(command.getGameID(), ctx.session, msg, BroadcastType.ONLY_SELF);
        }
        
    }

    @Override
    public void handleClose(WsCloseContext ctx){
        System.out.println("Websocket closed");
    }

    private void connect(UserGameCommand command, Session session) throws Exception{
        System.out.println("Join");
        connections.add(command.getGameID(), command.getAuthToken(), session);
        AuthData authData = userService.getUserData(command.getAuthToken());
        GameData gameData = gameService.getGameData(command.getGameID());
        String msg = authData.username() + ": has joined the game";
        ServerMessage userMessage = new LoadGameMessage(gameData,msg);
        connections.broadcast(command.getGameID(), session, userMessage,BroadcastType.ALL);
    }

    private void makeMove(MakeMoveCommand command, Session session) throws Exception{
        ServerMessage msg;
        //connections.add(command.getGameID(), command.getAuthToken(), session);
        int gameID = command.getGameID();
        ChessMove move = new ChessMove(new ChessPosition(command.getStartPosition()),new ChessPosition(command.getEndPosition()));
        String authToken = command.getAuthToken();
        AuthData authData = userService.getUserData(authToken);
        System.out.println("Make move called \nGame ID: " + Integer.toString(gameID) +  "\nMove: " + move.toString() + "\nauthToken: " + authToken);
        GameData updatedGameData = gameService.getGameData(gameID);
        if(updatedGameData.game().getWinner() != null){
            msg = new NotificationMessage("Game is completed, no more moves allowed");
            connections.broadcast(command.getGameID(), session, msg, BroadcastType.ONLY_SELF);
            return;
        }
        
        boolean userIsWhite = updatedGameData.whiteUsername().equals(authData.username());
        boolean userIsBlack = updatedGameData.blackUsername().equals(authData.username());
        boolean whiteToMove = updatedGameData.game().getTeamTurn().equals(ChessGame.TeamColor.WHITE);
        
        if(whiteToMove && userIsWhite){
            updatedGameData.game().makeMove(move);
        }
        else if(!whiteToMove && userIsBlack){
            updatedGameData.game().makeMove(move);
        }
        else{
            throw new Exception("Error: Observers cannot make moves");
        }
        
        
        System.out.println("Move was made");


        gameService.updateGame(updatedGameData);
        System.out.println("Move was saved");
        
        
        String textMessage = null;
        if(updatedGameData.game().isInCheck(ChessGame.TeamColor.WHITE)){
            textMessage = "WHITE PLAYER - " + updatedGameData.whiteUsername().toUpperCase() + " IS IN CHECK";
        }
        else if(updatedGameData.game().isInCheck(ChessGame.TeamColor.BLACK)){
            textMessage = "BLACK PLAYER - " + updatedGameData.blackUsername().toUpperCase() + " IS IN CHECK";
        }
        if(updatedGameData.game().isInCheckmate(ChessGame.TeamColor.WHITE)){
            textMessage = "WHITE HAS BEEN CHECKMATEd!\n BLACK - " + updatedGameData.blackUsername().toUpperCase() + " WINS THE GAME";
            updatedGameData.game().setWinner(ChessGame.TeamColor.BLACK);
            gameService.updateGame(updatedGameData);
        }
        else if(updatedGameData.game().isInCheckmate(ChessGame.TeamColor.BLACK)){
            textMessage = "BLACK HAS BEEN CHECKMATEd!\\nCHECKMATE! WHITE - " + updatedGameData.whiteUsername().toUpperCase() + " WINS THE GAME";
            updatedGameData.game().setWinner(ChessGame.TeamColor.WHITE);
            gameService.updateGame(updatedGameData);
        }
        msg = new LoadGameMessage(updatedGameData,textMessage);
        connections.broadcast(updatedGameData.gameID(),session,msg,BroadcastType.ALL);
    }

    private void leave(UserGameCommand command, Session session) throws Exception{
        System.out.println("Leave");
        AuthData authData = userService.getUserData(command.getAuthToken());
        ServerMessage msg = new NotificationMessage(authData.username() + ": has left the game");
        connections.broadcast(command.getGameID(), session, msg, BroadcastType.ALL_OTHERS);
        connections.remove(command.getGameID(),command.getAuthToken());
    }

    private void resign(UserGameCommand command, Session session) throws Exception{
        System.out.println("Resign");
        AuthData authData = userService.getUserData(command.getAuthToken());
        GameData gameData = gameService.getGameData(command.getGameID());
        ChessGame.TeamColor winningColor;
        if(authData.username().equals(gameData.blackUsername())){
            winningColor = ChessGame.TeamColor.WHITE;
        }
        else if(authData.username().equals(gameData.whiteUsername())){
            winningColor = ChessGame.TeamColor.BLACK;
        }
        else{
            throw new Exception("Resigning User is not playing");
        }
        gameData.game().setWinner(winningColor);
        gameService.updateGame(gameData);

        String textMsg = authData.username() + ": has resigned the game";

        ServerMessage msg = new LoadGameMessage(gameData,textMsg);
        connections.broadcast(command.getGameID(), session, msg, BroadcastType.ALL);
    }
}
