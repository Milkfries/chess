package dataaccess;

import java.util.Collection;

import model.GameData;

public interface GameDAO {
    public void insertGame(GameData gameData) throws DataAccessException;
    public GameData getGame(int gameID) throws DataAccessException;
    public void updateGame(GameData gameData) throws DataAccessException;
    public Collection<GameData> getAllGames() throws DataAccessException;
    public void deleteGame(int gameID) throws DataAccessException;
    public void clear() throws DataAccessException;
}
