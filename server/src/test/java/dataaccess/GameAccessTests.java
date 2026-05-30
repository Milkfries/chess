package dataaccess;

import java.util.Collection;

import org.junit.jupiter.api.*;

import chess.ChessGame;
import model.*;

public class GameAccessTests {
    private static GameDAO gameDAO;
    private static GameData testGame1;
    private static GameData testGame2;
    private static GameData testGame3;

    @BeforeAll
    public static void init() throws DataAccessException{
        gameDAO = new SQLGameDAO();
        gameDAO.clear();
        testGame1 = new GameData(0,null,null, "game1", new ChessGame());
        testGame2 = new GameData(0,null,"SwooptheUte", "game2", new ChessGame());
        testGame3 = new GameData(0,null,null, "game3", new ChessGame());
    }

    @AfterAll
    public static void clear() throws DataAccessException{
        gameDAO.clear();
    }
    @BeforeEach
    public void setup() throws DataAccessException{
        gameDAO.clear();
    }
    @Test
    @DisplayName("Add Get Game")
    public void addGetGame() throws DataAccessException{
        int gameID1 = gameDAO.insertGame(testGame1);
        Assertions.assertNotNull(gameDAO.getGame(gameID1));
        GameData compareGameData = new GameData(gameID1, testGame1.whiteUsername(),testGame1.blackUsername(),testGame1.gameName(),testGame1.game());
        Assertions.assertEquals(compareGameData,gameDAO.getGame(gameID1));
    }
    @Test
    @DisplayName("Clear Database")
    public void clearDatabase() throws DataAccessException{
        int gameID = gameDAO.insertGame(testGame1);
        gameDAO.clear();
        Assertions.assertNull(gameDAO.getGame(gameID));
    }
    @Test
    @DisplayName("Add Get Multiple Games")
    public void addMultipleGames() throws DataAccessException{
        int gameID1 = gameDAO.insertGame(testGame1);
        int gameID2 = gameDAO.insertGame(testGame2);
        int gameID3 = gameDAO.insertGame(testGame3);
        GameData compareGameData1 = new GameData(gameID1, testGame1.whiteUsername(),testGame1.blackUsername(),testGame1.gameName(),testGame1.game());
        GameData compareGameData2 = new GameData(gameID2, testGame2.whiteUsername(),testGame2.blackUsername(),testGame2.gameName(),testGame2.game());
        GameData compareGameData3 = new GameData(gameID3, testGame3.whiteUsername(),testGame3.blackUsername(),testGame3.gameName(),testGame3.game());
        Assertions.assertEquals(compareGameData1, gameDAO.getGame(gameID1));
        Assertions.assertEquals(compareGameData2, gameDAO.getGame(gameID2));
        Assertions.assertEquals(compareGameData3, gameDAO.getGame(gameID3));
    }
    @Test
    @DisplayName("Add null Game")
    public void addNullGame() throws DataAccessException{
        Assertions.assertThrows(DataAccessException.class,()->{
            gameDAO.insertGame(null);
        });
        Assertions.assertThrows(DataAccessException.class,()->{
            gameDAO.insertGame(new GameData(0,null,null,null,null));
        });
    }
    @Test
    @DisplayName("Access Bad Game")
    public void accessBadGame() throws DataAccessException{
        gameDAO.insertGame(testGame2);
        Assertions.assertNull(gameDAO.getGame(1000));
    }
    @Test
    @DisplayName("Update White Username")
    public void replaceWhiteUsername() throws DataAccessException{
        int gameID1 = gameDAO.insertGame(testGame1);
        GameData replaceWhite1= new GameData(gameID1, "Russell",null,testGame1.gameName(),testGame1.game());
        Assertions.assertDoesNotThrow(()->{
            gameDAO.updateGame(replaceWhite1);
        });
        Assertions.assertEquals(gameDAO.getGame(gameID1).whiteUsername(), replaceWhite1.whiteUsername());
    }
    @Test
    @DisplayName("Update White & Black Username")
    public void replaceWhiteBlackUsername() throws DataAccessException{
        int gameID1 = gameDAO.insertGame(testGame1);
        GameData replaceWhite1= new GameData(gameID1, "Russell",null,testGame1.gameName(),testGame1.game());
        GameData replaceBlack1 = new GameData(gameID1, "Russell","Dallin",testGame1.gameName(),testGame1.game());
        Assertions.assertDoesNotThrow(()->{
            gameDAO.updateGame(replaceWhite1);
            gameDAO.updateGame(replaceBlack1);
        });
        Assertions.assertEquals(gameDAO.getGame(gameID1).whiteUsername(), replaceWhite1.whiteUsername());
        Assertions.assertEquals(gameDAO.getGame(gameID1).blackUsername(), replaceBlack1.blackUsername());
    }
    @Test
    @DisplayName("Update Invalid Game")
    public void updateInvalidGame() throws DataAccessException{
        int gameID1 = gameDAO.insertGame(testGame1);

        GameData replaceWhite1= new GameData(gameID1 + 100, "Russell",null,testGame1.gameName(),testGame1.game());

        Assertions.assertThrows(DataAccessException.class,()->{
            gameDAO.updateGame(null);
        });

        Assertions.assertThrows(DataAccessException.class,()->{
            gameDAO.updateGame(replaceWhite1);
        });
    }
    @Test
    @DisplayName("Get All Games")
    public void getAllGames() throws DataAccessException{
        
        gameDAO.insertGame(testGame1);
        gameDAO.insertGame(testGame2);
        gameDAO.insertGame(testGame3);

        Assertions.assertDoesNotThrow(()->{
            gameDAO.getAllGames();
        });
        Collection<GameData> allGames = gameDAO.getAllGames();

        Assertions.assertFalse(allGames.isEmpty());
        Assertions.assertEquals(allGames.size(),3);
    }
    @Test
    @DisplayName("No Games, Get All")
    public void getNoGames() throws DataAccessException{
        gameDAO.insertGame(testGame1);
        gameDAO.clear();
        Assertions.assertDoesNotThrow(()->{
            gameDAO.getAllGames();
        });
        Collection<GameData> allGames = gameDAO.getAllGames();

        Assertions.assertTrue(allGames.isEmpty());
    }
    @Test
    @DisplayName("Delete Game")
    public void deleteGame() throws DataAccessException{
        int gameID = gameDAO.insertGame(testGame1);
        Assertions.assertDoesNotThrow(()->{
            gameDAO.deleteGame(gameID);
        });
        Assertions.assertNull(gameDAO.getGame(gameID));
    }
    @Test
    @DisplayName("Delete Invalid Game")
    public void deleteBadGame() throws DataAccessException{
        int gameID = gameDAO.insertGame(testGame1);
        Assertions.assertDoesNotThrow(()->{
            gameDAO.deleteGame(gameID+100);
        });
        GameData compareGameData = new GameData(gameID,testGame1.whiteUsername(),testGame1.blackUsername(),testGame1.gameName(),testGame1.game());
        Assertions.assertEquals(gameDAO.getGame(gameID),compareGameData);
    }
}

