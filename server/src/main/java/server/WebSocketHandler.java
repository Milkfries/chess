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
            ServerMessage msg = new NotificationMessage(e.getMessage());
            connections.broadcast(command.getGameID(), ctx.session, msg, BroadcastType.ALL_OTHERS);
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
        ServerMessage msg;
        connections.add(command.getGameID(), command.getAuthToken(), session);
        int gameID = command.getGameID();
        ChessMove move = new ChessMove(new ChessPosition(command.getStartPosition()),new ChessPosition(command.getEndPosition()));
        String authToken = command.getAuthToken();

        System.out.println("Make move called \nGame ID: " + Integer.toString(gameID) +  "\nMove: " + move.toString() + "\nauthToken: " + authToken);
        GameData updatedGameData = gameService.getGameData(gameID);
        if(updatedGameData.game().getWinner() != null){
            msg = new NotificationMessage("Game is completed, no more moves allowed");
            connections.broadcast(command.getGameID(), session, msg, BroadcastType.ONLY_SELF);
            return;
        }
        updatedGameData.game().makeMove(move);
        System.out.println("Move was made");
         // TODO Catch errors here

        gameService.updateGame(updatedGameData);
        System.out.println("Move was saved");
        
        msg = new LoadGameMessage(updatedGameData);
        connections.broadcast(updatedGameData.gameID(),session,msg,BroadcastType.ALL);

        if(updatedGameData.game().isInCheckmate(ChessGame.TeamColor.WHITE)){
            msg = new NotificationMessage("CHECKMATE! BLACK - " + updatedGameData.blackUsername() + " wins the game");
            connections.broadcast(command.getGameID(), session, msg, BroadcastType.ALL_OTHERS);
            updatedGameData.game().setWinner(ChessGame.TeamColor.BLACK);
            gameService.updateGame(updatedGameData);
            msg = new LoadGameMessage(updatedGameData);
            connections.broadcast(command.getGameID(), session, msg, BroadcastType.ALL);
        }
        else if(updatedGameData.game().isInCheckmate(ChessGame.TeamColor.BLACK)){
            msg = new NotificationMessage("CHECKMATE! WHITE - " + updatedGameData.whiteUsername() + " wins the game");
            connections.broadcast(command.getGameID(), session, msg, BroadcastType.ALL_OTHERS);
            updatedGameData.game().setWinner(ChessGame.TeamColor.WHITE);
            gameService.updateGame(updatedGameData);
            msg = new LoadGameMessage(updatedGameData);
            connections.broadcast(command.getGameID(), session, msg, BroadcastType.ALL);
        }
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

        ServerMessage msg = new NotificationMessage(authData.username() + ": has resigned the game");
        connections.broadcast(command.getGameID(), session, msg, BroadcastType.ALL_OTHERS);

        msg = new LoadGameMessage(gameData);
        connections.broadcast(command.getGameID(), session, msg, BroadcastType.ALL);
    }
}
