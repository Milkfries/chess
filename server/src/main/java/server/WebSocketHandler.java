package server;

import org.eclipse.jetty.websocket.api.Session;
import com.google.gson.Gson;

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
import model.UserData;
import server.ConnectionManager.BroadcastType;
import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ServerMessageType;

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
    public void handleMessage(WsMessageContext ctx) throws Exception{
        UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
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

    @Override
    public void handleClose(WsCloseContext ctx){
        System.out.println("Websocket closed");
    }

    private void connect(UserGameCommand command, Session session) throws Exception{
        System.out.println("Join");
        connections.add(command.getGameID(), command.getAuthToken(), session);
        AuthData authData = userService.getUserData(command.getAuthToken());
        ServerMessage msg = new NotificationMessage(authData.username() + ": has joined the game");
        connections.broadcast(command.getGameID(), session, msg, BroadcastType.ALL_OTHERS);
    }

    private void makeMove(MakeMoveCommand command, Session session) throws Exception{
        connections.add(command.getGameID(), command.getAuthToken(), session);
        int gameID = command.getGameID();
        ChessMove move = new ChessMove(new ChessPosition(command.getStartPosition()),new ChessPosition(command.getEndPosition()));
        String authToken = command.getAuthToken();

        System.out.println("Make move called \nGame ID: " + Integer.toString(gameID) +  "\nMove: " + move.toString() + "\nauthToken: " + authToken);
        
        GameData updatedGameData = gameService.updateGame(gameID,move);
        
        ServerMessage msg = new LoadGameMessage(updatedGameData);
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
        ServerMessage msg = new NotificationMessage(authData.username() + ": has resigned the game");

        connections.broadcast(command.getGameID(), session, msg, BroadcastType.ALL_OTHERS);
    }
}
