package client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.management.Notification;

import com.google.gson.Gson;

import chess.ChessMove;
import chess.ChessPosition;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;

public class WebsocketCommunicator extends Endpoint{
    private ServerMessageObserver observer;
    private Session session;
    public WebsocketCommunicator(String hostName, String port, ServerMessageObserver observer) throws Exception{
        try {
            String url = "ws://" + hostName + ":" + port + "/ws";
            URI socketURI = new URI(url);
            this.observer = observer;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    observer.notify(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception("Error: " + ex.getMessage());
        }
    }
    
    public void makeMove(String authToken, int gameID, ChessMove chessMove){
        MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, chessMove);
    }

    public void resign(String authToken, int gameID){
        UserGameCommand command = new UserGameCommand(CommandType.RESIGN, null, null);
    }

    public void leave(String authToken, int gameID){

    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        
    }
}
