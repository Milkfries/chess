package client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import websocket.commands.UserGameCommand;
import websocket.exceptions.ResponseException;

public class WebsocketCommunicator extends Endpoint{
    private ServerMessageObserver observer;
    private Session session;
    private Gson serializer;

    public WebsocketCommunicator(String hostName, int port, ServerMessageObserver observer) throws Exception{
        serializer = new Gson();
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
    public void sendRequest(UserGameCommand command) throws ResponseException{
        try {
            this.session.getBasicRemote().sendText(serializer.toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        
    }
}
