package service;


import org.junit.jupiter.api.*;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import request.*;
    import result.*;


    public class GameServiceTests {
        private static UserDAO userDAO;
        private static AuthDAO authDAO;
        private static GameDAO gameDAO;
        private static GameService gameService;
        private static UserService userService;
        private static ClearService clearService;

        private static RegisterResult existingUser1;
        private static RegisterResult existingUser2;

        @BeforeAll
        public static void init() throws DataAccessException{
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
            gameService = new GameService(authDAO, gameDAO);
            userService = new UserService(userDAO, authDAO);
            clearService = new ClearService(userDAO, authDAO, gameDAO);

        }

        @BeforeEach
        public void setup() throws Exception{
            clearService.clear();

            existingUser1 = userService.register(new RegisterRequest("ShaneReese", "gocougs", "number1pres@gmail.com"));

            existingUser2 = userService.register(new RegisterRequest("WendyReese", "shanelover", "wreese@gmail.com"));


        }
        @Test
        @DisplayName("Create Game")
        public void createGame() throws Exception{
            Assertions.assertDoesNotThrow(()->{
                gameService.createGame(new CreateGameRequest(existingUser1.authToken(),"game1"));
            });
        }

        @Test
        @DisplayName("No Game Name")
        public void noGameName() throws Exception{
            Assertions.assertThrows(BadRequestException.class,()->{
                gameService.createGame(new CreateGameRequest(existingUser1.authToken(),null));
            });
        }
        @Test
        @DisplayName("2 Player Join")
        public void twojoin() throws Exception{
            CreateGameResult result = gameService.createGame(new CreateGameRequest(existingUser1.authToken(),"game1"));

            Assertions.assertNotNull(result);
            Assertions.assertDoesNotThrow(()->{
                gameService.joinGame(new JoinGameRequest(existingUser1.authToken(),"WHITE", result.gameID()));
                gameService.joinGame(new JoinGameRequest(existingUser2.authToken(),"BLACK", result.gameID()));
            });      
            Assertions.assertEquals(result.gameID(),1);  
            Assertions.assertEquals(existingUser1.username(), gameDAO.getGame(result.gameID()).whiteUsername());
            Assertions.assertEquals(existingUser2.username(), gameDAO.getGame(result.gameID()).blackUsername());
        }
        @Test
        @DisplayName("Player Color Not Caps")
        public  void playerColorNotCaps() throws Exception{
            CreateGameResult result = gameService.createGame(new CreateGameRequest(existingUser1.authToken(),"game1"));

            Assertions.assertNotNull(result);
            Assertions.assertDoesNotThrow(()->{
                gameService.joinGame(new JoinGameRequest(existingUser1.authToken(),"white", result.gameID()));
                gameService.joinGame(new JoinGameRequest(existingUser2.authToken(),"black", result.gameID()));
            });        
        } 

        @Test
        @DisplayName("Wrong Color")
        public void wrongColor() throws Exception{
            CreateGameResult result = gameService.createGame(new CreateGameRequest(existingUser1.authToken(),"game1"));

            Assertions.assertNotNull(result);
            Assertions.assertThrows(AlreadyTakenException.class,()->{
                gameService.joinGame(new JoinGameRequest(existingUser1.authToken(),"white", result.gameID()));
                gameService.joinGame(new JoinGameRequest(existingUser2.authToken(),"white", result.gameID()));
            });        
        } 

        @Test
        @DisplayName("No Color")
        public void noColor() throws Exception{
            CreateGameResult result = gameService.createGame(new CreateGameRequest(existingUser1.authToken(),"game1"));

            Assertions.assertNotNull(result);
            Assertions.assertThrows(BadRequestException.class,()->{
                gameService.joinGame(new JoinGameRequest(existingUser1.authToken(),"", result.gameID()));
            });        
        } 

        @Test
        @DisplayName("Wrong GameID")
        public void wrongGameID() throws Exception{
            CreateGameResult result = gameService.createGame(new CreateGameRequest(existingUser1.authToken(),"game1"));

            Assertions.assertNotNull(result);
            Assertions.assertThrows(BadRequestException.class,()->{
                gameService.joinGame(new JoinGameRequest(existingUser1.authToken(),"",123));
            });        
        } 

        @Test
        @DisplayName("Multiple Games")
        public void threeGames() throws Exception{
            CreateGameResult result1 = gameService.createGame(new CreateGameRequest(existingUser1.authToken(),"game1"));
            CreateGameResult result2 = gameService.createGame(new CreateGameRequest(existingUser1.authToken(),"game2"));
            CreateGameResult result3 = gameService.createGame(new CreateGameRequest(existingUser1.authToken(),"game3"));
            
            Assertions.assertNotNull(result1);
            Assertions.assertNotNull(result2);
            Assertions.assertNotNull(result3);
            
            Assertions.assertDoesNotThrow(()->{
                gameService.joinGame(new JoinGameRequest(existingUser1.authToken(),"WHITE",result1.gameID()));
                gameService.joinGame(new JoinGameRequest(existingUser2.authToken(),"WHITE",result2.gameID()));
                gameService.joinGame(new JoinGameRequest(existingUser1.authToken(),"WHITE",result3.gameID()));

                gameService.joinGame(new JoinGameRequest(existingUser2.authToken(),"BLACK",result1.gameID()));
                gameService.joinGame(new JoinGameRequest(existingUser1.authToken(),"BLACK",result2.gameID()));
                gameService.joinGame(new JoinGameRequest(existingUser2.authToken(),"BLACK",result3.gameID()));
            });        

            Assertions.assertNotEquals(result2.gameID(), result3.gameID());
            Assertions.assertNotEquals(result1.gameID(), result3.gameID());
            Assertions.assertNotEquals(result1.gameID(), result2.gameID());
        } 

        @Test
        @DisplayName("Same User")   
        public void sameUser() throws Exception{
            CreateGameResult result1 = gameService.createGame(new CreateGameRequest(existingUser1.authToken(),"game1"));
            Assertions.assertDoesNotThrow(()->{
                gameService.joinGame(new JoinGameRequest(existingUser1.authToken(),"WHITE",result1.gameID()));
                
                gameService.joinGame(new JoinGameRequest(existingUser1.authToken(),"BLACK",result1.gameID()));
            });        
        } 

        @Test
        @DisplayName("List Games")   
        public void listGames() throws Exception{
            CreateGameResult result1 = gameService.createGame(new CreateGameRequest(existingUser1.authToken(),"game1"));
            CreateGameResult result2 = gameService.createGame(new CreateGameRequest(existingUser1.authToken(),"game2"));
            CreateGameResult result3 = gameService.createGame(new CreateGameRequest(existingUser1.authToken(),"game3"));
            
            gameService.joinGame(new JoinGameRequest(existingUser1.authToken(),"BLACK",result1.gameID())); // Game 1
            gameService.joinGame(new JoinGameRequest(existingUser2.authToken(),"WHITE",result1.gameID()));

            gameService.joinGame(new JoinGameRequest(existingUser2.authToken(),"WHITE",result2.gameID())); // Game 2

            gameService.joinGame(new JoinGameRequest(existingUser1.authToken(),"BLACK",result3.gameID())); // Game 3
            

            GameData correct1 = new GameData(1, existingUser2.username(), existingUser1.username(), "game1", new ChessGame());
            GameData correct2 = new GameData(2, existingUser2.username(), null, "game2", new ChessGame());
            GameData correct3 = new GameData(3, null, existingUser1.username(), "game3", new ChessGame());

            ListGameResult listGames = gameService.listGames(new ListGameRequest(existingUser1.authToken()));

            Assertions.assertEquals(listGames.games().size(), 3);
            Assertions.assertTrue(listGames.games().contains(correct1));
            Assertions.assertTrue(listGames.games().contains(correct2));
            Assertions.assertTrue(listGames.games().contains(correct3));

        } 

        @Test
        @DisplayName("Invalid Access Create Game")
        public void invalidAccessCreate() throws Exception{
            Assertions.assertThrows(UnauthorizedException.class, ()->{
                gameService.createGame(new CreateGameRequest(UserService.generateToken(),"game1"));
            });
        }

        @Test
        @DisplayName("Invalid Access Join Game")
        public void invalidAccessJoin() throws Exception{
            Assertions.assertThrows(UnauthorizedException.class, ()->{
                CreateGameResult result1 = gameService.createGame(new CreateGameRequest(existingUser1.authToken(),"game1"));    
                gameService.joinGame(new JoinGameRequest(UserService.generateToken(),"BLACK",result1.gameID()));            
            });
        }
        @Test
        @DisplayName("Invalid Access List Games")
        public void invalidAccessList() throws Exception{
            Assertions.assertThrows(UnauthorizedException.class, ()->{
                gameService.listGames(new ListGameRequest(UserService.generateToken()));
            });
        }
    }
