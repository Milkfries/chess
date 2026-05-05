package request;

public record JoinGameRequest(String authToken, String teamColor, int gameID) {
    
}
