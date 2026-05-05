package dataaccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import model.GameData;

public class MemoryGameDAO implements GameDAO{
    private HashMap<Integer, GameData> games; // Key is gameID - int
    private static int gameID;

    public MemoryGameDAO(){
        games = new HashMap<>();
        gameID = 0;
    }
    @Override
    public void insertGame(GameData gameData) throws DataAccessException {
        games.put(++gameID,gameData);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
        games.remove(gameID);
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }
    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(),gameData);
    }

}