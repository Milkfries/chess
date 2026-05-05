package service;

import com.mysql.cj.exceptions.UnableToConnectException;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGameRequest;
import result.CreateGameResult;
import result.ListGameResult;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO,GameDAO gameDAO){
        this.authDAO=authDAO;
        this.gameDAO=gameDAO;
    }

    public ListGameResult listGames(ListGameRequest request) throws UnauthorizedException{

        return null;
    }
    public CreateGameResult createGame(CreateGameRequest request) throws UnauthorizedException, BadRequestException{

        return null;
    }
    public void joinGame(JoinGameRequest request) throws UnauthorizedException, BadRequestException, AlreadyTakenException{

    }
    

}
