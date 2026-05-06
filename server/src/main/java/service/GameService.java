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
        String teamColor = request.playerColor();
        int gameID = request.gameID();


        AuthData authData = authDAO.getAuth(authToken);

        if(authData == null){
            throw new UnauthorizedException("Error: unauthorized");
        }

        if(teamColor == null || teamColor.isBlank() || gameID == 0){
            System.out.println("This one");
            throw new BadRequestException("Error: bad request");
        }

        GameData currentGame = gameDAO.getGame(gameID);

        if(currentGame == null){
            throw new BadRequestException("Error: bad request");

        }

        String whiteUsername = currentGame.whiteUsername();
        String blackUsername = currentGame.blackUsername();
        String gameName = currentGame.gameName();
        ChessGame chessGame = currentGame.game();
        
        if(teamColor.toUpperCase().equals("WHITE")){
            if(whiteUsername == null){
                whiteUsername = authData.username();
            }
            else{
                throw new AlreadyTakenException("Error: already taken");
            }
            
        }
        else if(teamColor.toUpperCase().equals("BLACK")){
            if(blackUsername == null){
                blackUsername = authData.username();
            }
            else{
                throw new AlreadyTakenException("Error: already taken");
            }
        }
        else{
            throw new BadRequestException("Error: bad request");
        }

        GameData newGame = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);

        gameDAO.updateGame(newGame);
    }
    

}
