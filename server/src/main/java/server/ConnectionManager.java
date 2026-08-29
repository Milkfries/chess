package server;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import com.google.gson.Gson;

import websocket.messages.ServerMessage;
public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String,Session>> connections = new ConcurrentHashMap<>(); // game id - > username (maybe authToken) - > Session
    public Gson serializer = new Gson();
    public void add(int gameID, String authToken, Session session) {
        ConcurrentHashMap<String,Session> gameConnections = connections.get(gameID);
        if(gameConnections == null){
            gameConnections = new ConcurrentHashMap<>();
        }
        
        gameConnections.put(authToken,session);
        connections.put(gameID, gameConnections);
    }

    public void remove(int gameID, String authToken) {
        connections.get(gameID).remove(authToken);
    }

    public void broadcast(int gameID, Session currentSession, ServerMessage serverMsg, boolean includeCurrent) throws IOException {
        String msg = serializer.toJson(serverMsg);
        for (Session c : connections.get(gameID).values()) {
            if (c.isOpen()) {
                if (!c.equals(currentSession)) {
                    c.getRemote().sendString(msg);
                }
                else if(includeCurrent){
                    c.getRemote().sendString(msg);
                }

            }
        }
    }
}
