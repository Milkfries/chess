package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGameRequest;
import result.CreateGameResult;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO,GameDAO gameDAO){
        this.authDAO=authDAO;
        this.gameDAO=gameDAO;
    }

    public ListGameRequest listGames(ListGameRequest request){

        return null;
    }
    public CreateGameResult createGame(CreateGameRequest request){

        return null;
    }
    public void joinGame(JoinGameRequest request){

    }
    

}
