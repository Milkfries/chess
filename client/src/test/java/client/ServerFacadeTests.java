package client;

import org.junit.jupiter.api.*;

import model.GameData;
import request.*;
import result.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static RegisterRequest testRegisterRequest1;
    private static RegisterRequest testRegisterRequest2;
    private static RegisterRequest testRegisterRequest3;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("localhost", port);

        testRegisterRequest1 = new RegisterRequest("User1", "SecurePass","NormalEmail@gmail.com" );
        testRegisterRequest2 = new RegisterRequest("SecondUser", "1234Word","testing@yahoo.com" );
        testRegisterRequest3 = new RegisterRequest("FinalUse", "DontHaxMe","regularDude@byu.edu");
    }
    @BeforeEach
    public void clear(){
        try{
            facade.clear();
        }
        catch (Exception e){
            return;
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerUsers() throws Exception{
        RegisterResult result1 = facade.register(testRegisterRequest1);
        RegisterResult result2 = facade.register(testRegisterRequest2);

        Assertions.assertEquals(result1.username(),testRegisterRequest1.username());
        Assertions.assertNotEquals(result1.authToken(), "");
        Assertions.assertNotEquals(result1.authToken(), testRegisterRequest1.username());
        Assertions.assertNotEquals(result1.authToken(), testRegisterRequest1.password());

        Assertions.assertNotEquals(result1.authToken(), result2.authToken());
    }

    @Test
    public void registerBadUser() throws Exception{
        RegisterRequest request = new RegisterRequest(null,null,null);
        
        try{
            facade.register(request);
            Assertions.assertThrows(Exception.class, ()->{});
        }
        catch (Exception e){
            Assertions.assertEquals(e.getMessage(), "Error: bad request");
        }
    }

    @Test
    public void registerRepeatUser() throws Exception{       
        try{
            facade.register(testRegisterRequest1);
            facade.register(testRegisterRequest1);
            Assertions.assertThrows(Exception.class, ()->{});
        }
        catch (Exception e){
            Assertions.assertEquals(e.getMessage(), "Error: already taken");
        }
    }

    @Test
    public void clearDatabase() throws Exception{
        Assertions.assertDoesNotThrow(()->{
            facade.register(testRegisterRequest3);
            facade.clear();
            facade.register(testRegisterRequest3);
        });
    }

    @Test
    public void loginUser() throws Exception{
        LoginRequest loginRequest = new LoginRequest(testRegisterRequest2.username(),testRegisterRequest2.password());

        RegisterResult registerResult = facade.register(testRegisterRequest2);
        LoginResult loginResult = facade.login(loginRequest);

        Assertions.assertNotEquals(registerResult.authToken(), loginResult.authToken());
        Assertions.assertEquals(registerResult.username(), loginResult.username());
        Assertions.assertEquals(loginRequest.username(),loginResult.username());
    }

    @Test
    public void loginWrongPassword() throws Exception{
        LoginRequest loginRequest = new LoginRequest(testRegisterRequest3.username(),testRegisterRequest3.password() + "1234");

        facade.register(testRegisterRequest3);

        try{
            facade.login(loginRequest);
            Assertions.assertThrows(Exception.class, ()->{});
        }
        catch (Exception e){
            Assertions.assertEquals(e.getMessage(), "Error: unauthorized");
        }
    }

    @Test
    public void loginBeforeRegister() throws Exception{
        LoginRequest loginRequest = new LoginRequest(testRegisterRequest2.username(),testRegisterRequest2.password());
        try{
            facade.login(loginRequest);
            Assertions.assertThrows(Exception.class, ()->{});
        }
        catch (Exception e){
            Assertions.assertEquals(e.getMessage(), "Error: unauthorized");
        }
    }

    @Test
    public void loginNullInfo() throws Exception{
        LoginRequest loginRequest = new LoginRequest(null,null);
        try{
            facade.login(loginRequest);
            Assertions.assertThrows(Exception.class, ()->{});
        }
        catch (Exception e){
            Assertions.assertEquals(e.getMessage(), "Error: bad request");
        }
    }
    
    @Test
    public void logoutUser() throws Exception{
        Assertions.assertDoesNotThrow(()->{
            RegisterResult registerResult = facade.register(testRegisterRequest2);
            LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
            facade.logout(logoutRequest);
        });
    }

    @Test
    public void logoutUserBeforeLogin() throws Exception{
        try{
            LogoutRequest logoutRequest = new LogoutRequest(null);
            facade.logout(logoutRequest);
            Assertions.assertThrows(Exception.class, ()->{});
        }
        catch (Exception e){
            Assertions.assertEquals(e.getMessage(), "Error: unauthorized");
        }
    }

    @Test
    public void createGames() throws Exception{
        RegisterResult result = facade.register(testRegisterRequest3);
        CreateGameRequest createGameRequest1 = new CreateGameRequest(result.authToken(),"Game1");
        CreateGameRequest createGameRequest2 = new CreateGameRequest(result.authToken(),"Game2");

        CreateGameResult createGameResult1 = facade.createGame(createGameRequest1);
        CreateGameResult createGameResult2 = facade.createGame(createGameRequest2);

        Assertions.assertNotEquals(createGameResult1.gameID(), createGameResult2.gameID());
    }

    @Test
    public void createBadGames() throws Exception{
        RegisterResult result = facade.register(testRegisterRequest3);
        CreateGameRequest createGameRequest1 = new CreateGameRequest(result.authToken(),"");
        CreateGameRequest createGameRequest2 = new CreateGameRequest("asdfajklsdhfasv","Game1");
        try{
            facade.createGame(createGameRequest1);
            Assertions.assertThrows(Exception.class, ()->{});
        }
        catch (Exception e){
            Assertions.assertEquals(e.getMessage(), "Error: bad request");
        }
        try{
            facade.createGame(createGameRequest2);
            Assertions.assertThrows(Exception.class, ()->{});
        }
        catch (Exception e){
            Assertions.assertEquals(e.getMessage(), "Error: unauthorized");
        }
    }

    @Test
    public void joinGame() throws Exception{
        RegisterResult result = facade.register(testRegisterRequest1);

        CreateGameRequest createGameRequest1 = new CreateGameRequest(result.authToken(),"Game1");
        CreateGameResult createGameResult1 = facade.createGame(createGameRequest1);
        JoinGameRequest joinGameRequest = new JoinGameRequest(result.authToken(), "WHITE", createGameResult1.gameID());

        Assertions.assertDoesNotThrow(()->{
            facade.joinGame(joinGameRequest);
        });
    }

    @Test
    public void joinGameAgain() throws Exception{
        RegisterResult result = facade.register(testRegisterRequest2);

        CreateGameRequest createGameRequest1 = new CreateGameRequest(result.authToken(),"Game1");
        CreateGameResult createGameResult1 = facade.createGame(createGameRequest1);
        JoinGameRequest joinGameRequest = new JoinGameRequest(result.authToken(), "WHITE", createGameResult1.gameID());

       try{
            facade.joinGame(joinGameRequest);
            facade.joinGame(joinGameRequest);
            Assertions.assertThrows(Exception.class, ()->{});
        }
        catch (Exception e){
            Assertions.assertEquals(e.getMessage(), "Error: already taken");
        }
    }
    
    @Test
    public void listGames() throws Exception{
        RegisterResult result = facade.register(testRegisterRequest3);
        CreateGameRequest createGameRequest1 = new CreateGameRequest(result.authToken(),"Game1");
        CreateGameRequest createGameRequest2 = new CreateGameRequest(result.authToken(),"Game2");
        CreateGameResult createGameResult1 = facade.createGame(createGameRequest1);
        CreateGameResult createGameResult2 = facade.createGame(createGameRequest2);

        JoinGameRequest joinGameRequest = new JoinGameRequest(result.authToken(),"WHITE",createGameResult1.gameID());

        facade.joinGame(joinGameRequest);
        ListGameRequest listGameRequest = new ListGameRequest(result.authToken());
        ListGameResult listGameResult = facade.listGames(listGameRequest);

        Assertions.assertEquals(listGameResult.games().size(), 2);
        GameData[] games = listGameResult.games().toArray(new GameData[2]);
        
        Assertions.assertEquals(games[0].gameID(),createGameResult1.gameID());
        Assertions.assertEquals(games[1].gameID(),createGameResult2.gameID());

        Assertions.assertEquals(games[0].gameName(),createGameRequest1.gameName());
        Assertions.assertEquals(games[1].gameName(),createGameRequest2.gameName());

        Assertions.assertEquals(games[0].whiteUsername(),testRegisterRequest3.username());
        Assertions.assertEquals(games[1].blackUsername(),null);
    }
    @Test
    public void listGamesNoAuth() throws Exception{
        ListGameRequest listGameRequest = new ListGameRequest("af02jf030");

        try{
            facade.listGames(listGameRequest);
            Assertions.assertThrows(Exception.class, ()->{});
        }
        catch (Exception e){
            Assertions.assertEquals(e.getMessage(), "Error: unauthorized");
        }

    }
}
