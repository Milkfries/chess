package dataaccess;

import java.util.Collection;

import com.google.gson.Gson;

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

            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1,whiteUsername);
                preparedStatement.setString(2,blackUsername);
                preparedStatement.setString(3,gameName);
                preparedStatement.setString(4,gameJSON);
                return preparedStatement.executeUpdate();
            }

        }
        catch (Exception e){
            throw new DataAccessException("Failed to insert authData");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGame'");
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        deleteGame(gameData.gameID());
        insertGame(gameData);
    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllGames'");
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
            throw new DataAccessException("Failed to delete gameData");
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
            throw new DataAccessException("Failed to delete gameData");
        }
    }
    
}
