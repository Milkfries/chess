package dataaccess;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;

import chess.ChessGame;
import model.GameData;

public class SQLGameDAO implements GameDAO{
    public SQLGameDAO() throws DataAccessException{
        DatabaseManager.createDatabase();
    }
    @Override
    public int insertGame(GameData gameData) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            String whiteUsername = gameData.whiteUsername();
            String blackUsername = gameData.blackUsername();
            String gameName = gameData.gameName();
            String gameJSON = new Gson().toJson(gameData.game());
            var statement = "INSERT INTO gameData (whiteUsername,blackUsername,gameName,game) VALUES (?,?,?,?)";

            try(var preparedStatement = conn.prepareStatement(statement,Statement.RETURN_GENERATED_KEYS)){
                preparedStatement.setString(1,whiteUsername);
                preparedStatement.setString(2,blackUsername);
                preparedStatement.setString(3,gameName);
                preparedStatement.setString(4,gameJSON);
                preparedStatement.executeUpdate();
                ResultSet rs = preparedStatement.getGeneratedKeys();
                if(rs.next()){
                    int test = rs.getInt(1);
                    return test;    
                }
                throw new DataAccessException("error: failed to insert gameData");
            }

        }
        catch (Exception e){
            throw new DataAccessException("error: failed to insert gameData");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameData WHERE gameID = ?";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setInt(1,gameID);
                ResultSet rs = preparedStatement.executeQuery();
                if(rs.next()){
                    return readGameData(rs);
                }
            }
            
        }
        catch(Exception e){
            throw new DataAccessException("error: failed to get gameData");
        }

        return null;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            String whiteUsername = gameData.whiteUsername();
            String blackUsername = gameData.blackUsername();
            String gameJSON = new Gson().toJson(gameData.game());
            int gameID = gameData.gameID();
            var statement = "UPDATE gameData SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?";

            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1,whiteUsername);
                preparedStatement.setString(2,blackUsername);
                preparedStatement.setString(3,gameJSON);
                preparedStatement.setInt(4,gameID);
                preparedStatement.executeUpdate();
            }

        }
        catch (Exception e){
            throw new DataAccessException("error: failed to update gameData");
        }
    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameData";
            try(var preparedStatement = conn.prepareStatement(statement)){
                ResultSet rs = preparedStatement.executeQuery();
                while(rs.next()){
                    games.add(readGameData(rs));
                }
            }
            
        }
        catch(Exception e){
            throw new DataAccessException("error: failed to get games");
        }

        return games;
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "DELETE FROM gameData WHERE gameID = ?";

            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setInt(1,gameID);
                preparedStatement.executeUpdate();
            }

        }
        catch (Exception e){
            throw new DataAccessException("error: failed to delete gameData");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "TRUNCATE gameData";

            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }

        }
        catch (Exception e){
            throw new DataAccessException("error: failed to clear gameData");
        }
    }
    private GameData readGameData(ResultSet rs) throws Exception{
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        var gameJSON = rs.getString("game");
        ChessGame game = new Gson().fromJson(gameJSON, ChessGame.class);
        return new GameData(gameID,whiteUsername,blackUsername,gameName,game);
    }
}
