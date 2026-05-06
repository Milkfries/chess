package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import request.*;
import result.*;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO,GameDAO gameDAO){
        this.authDAO=authDAO;
        this.gameDAO=gameDAO;
    }

    public ListGameResult listGames(ListGameRequest request) throws UnauthorizedException, DataAccessException{
        String authToken = request.authToken();

        AuthData authData = authDAO.getAuth(authToken);

        if(authData == null){
            throw new UnauthorizedException("Error: unauthorized");
        }

        ListGameResult listGameResult = new ListGameResult(gameDAO.getAllGames());

        return listGameResult;
    }
    public CreateGameResult createGame(CreateGameRequest request) throws UnauthorizedException, BadRequestException, DataAccessException{
        String authToken = request.authToken();
        String gameName = request.gameName();


        if(gameName == null || gameName.isBlank()){
            throw new BadRequestException("Error: bad request");
        }

        AuthData authData = authDAO.getAuth(authToken);
        
        if(authData == null){
            throw new UnauthorizedException("Error: unauthorized");
        }

        ChessGame newGame = new ChessGame();
        GameData gameData = new GameData(0,null,null,gameName,newGame);
        int gameID = gameDAO.insertGame(gameData);

        CreateGameResult createGameResult = new CreateGameResult(gameID);
        return createGameResult;
    }
    public void joinGame(JoinGameRequest request) throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException{
        String authToken = request.authToken();
        String teamColor = request.teamColor();
        int gameID = request.gameID();


        AuthData authData = authDAO.getAuth(authToken);

        if(authData == null){
            throw new UnauthorizedException("Error: unauthorized");
        }

        if(teamColor == null || teamColor.isBlank() || gameID == 0 ){
            throw new BadRequestException("Error: bad request");
        }

        GameData currentGame = gameDAO.getGame(gameID);
        String whiteUsername = currentGame.whiteUsername();
        String blackUsername = currentGame.blackUsername();
        String gameName = currentGame.gameName();
        ChessGame chessGame = currentGame.game();

        if(teamColor.toUpperCase() == "WHITE"){
            whiteUsername = authData.username();
        }
        else if(teamColor.toUpperCase() == "BLACK"){
            blackUsername = authData.username();
        }
        else{
            throw new BadRequestException("Error: bad request");
        }

        GameData newGame = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);

        gameDAO.updateGame(newGame);
    }
    

}
