package server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.core.WebSocketComponents;

import com.google.gson.Gson;

import chess.ChessMove;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
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
        System.out.println("Connect");
        connections.add(session);
        ServerMessage msg = new ServerMessage(ServerMessageType.NOTIFICATION);
        connections.broadcast(session, msg);
    }

    private void makeMove(MakeMoveCommand command, Session session) throws Exception{
        int gameID = command.getGameID();
        ChessMove move = command.getChessMove();
        String authToken = command.getAuthToken();

        System.out.println("Make move called \nGame ID: " + Integer.toString(gameID) +  "\nMove: " + move.toString() + "\nauthToken: " + authToken);

        ServerMessage msg = new ServerMessage(ServerMessageType.LOAD_GAME);
        connections.broadcast(session, msg);
    }

    private void leave(UserGameCommand command, Session session) throws Exception{
        System.out.println("Leave");
        connections.remove(session);

        ServerMessage msg = new ServerMessage(ServerMessageType.ERROR);
        connections.broadcast(session, msg);
    }

    private void resign(UserGameCommand command, Session session){
        System.out.println("Resign");
        connections.remove(session);
    }
}
