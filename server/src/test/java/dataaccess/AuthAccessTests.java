package dataaccess;

import model.AuthData;

import service.UserService;

import org.junit.jupiter.api.*;


public class AuthAccessTests {
    private static AuthDAO authDAO;
    private static AuthData testAuth1;
    private static AuthData testAuth2;
    private static AuthData testAuth3;

    @BeforeAll
    public static void init() throws DataAccessException{
        authDAO = new SQLAuthDAO();
    }
    @AfterAll
    public static void clear() throws DataAccessException{
        authDAO.clear();
    }
    @BeforeEach
    public void setup() throws DataAccessException {
        testAuth1 = new AuthData(UserService.generateToken(), "Bill");
        testAuth2 = new AuthData(UserService.generateToken(), "Jill");
        testAuth3 = new AuthData(UserService.generateToken(), "Will");
        authDAO.clear();
    }
    @Test
    @DisplayName("Add & Get Auth")
    public void addGetAuth() throws DataAccessException{
        authDAO.insertAuth(testAuth1);
        AuthData accessAuth = authDAO.getAuth(testAuth1.authToken());
        Assertions.assertEquals(accessAuth,testAuth1);
    }
    @Test
    @DisplayName("Add & Get Multiple Auths")
    public void addGetAuthMultiple() throws DataAccessException{
        authDAO.insertAuth(testAuth1);
        authDAO.insertAuth(testAuth2);
        authDAO.insertAuth(testAuth3);
        AuthData accessAuth1 = authDAO.getAuth(testAuth1.authToken());
        AuthData accessAuth2 = authDAO.getAuth(testAuth2.authToken());
        AuthData accessAuth3 = authDAO.getAuth(testAuth3.authToken());
        Assertions.assertEquals(accessAuth1,testAuth1);
        Assertions.assertEquals(accessAuth2,testAuth2);
        Assertions.assertEquals(accessAuth3,testAuth3);
    }
    @Test
    @DisplayName("Get Nonexistant Auth")
    public void getNullAuth() throws DataAccessException{
        authDAO.insertAuth(testAuth1);
        AuthData accessAuth = authDAO.getAuth(testAuth2.authToken());
        Assertions.assertNull(accessAuth);
    }
    @Test
    @DisplayName("Clear Auth Database")
    public void clearAuth() throws DataAccessException{
        authDAO.insertAuth(testAuth1);
        authDAO.insertAuth(testAuth2);
        authDAO.insertAuth(testAuth3);
        authDAO.clear();
        AuthData accessAuth1 = authDAO.getAuth(testAuth1.authToken());
        AuthData accessAuth2 = authDAO.getAuth(testAuth2.authToken());
        AuthData accessAuth3 = authDAO.getAuth(testAuth3.authToken());
        Assertions.assertNull(accessAuth1);
        Assertions.assertNull(accessAuth2);
        Assertions.assertNull(accessAuth3);
    }
    @Test
    @DisplayName("Overwrite Auth")
    public void overwriteAuth() throws DataAccessException{
        authDAO.insertAuth(testAuth1);
        AuthData replaceAuth = new AuthData(testAuth1.authToken(),testAuth1.username().toUpperCase());
        Assertions.assertThrows(DataAccessException.class,()->{
            authDAO.insertAuth(replaceAuth);
        });
    }
    @Test
    @DisplayName("Same Username")
    public void sameUsername() throws DataAccessException{
        
        AuthData firstAuth = new AuthData(UserService.generateToken(), "Joe");
        AuthData similarAuth = new AuthData(UserService.generateToken(), "Joe");
        authDAO.insertAuth(firstAuth);
        authDAO.insertAuth(similarAuth);
        AuthData accessAuth1 = authDAO.getAuth(firstAuth.authToken());
        AuthData accessAuth2 = authDAO.getAuth(similarAuth.authToken());
        Assertions.assertNotEquals(accessAuth1, accessAuth2);
    }
    @Test
    @DisplayName("Remove Auth")
    public void removeAuth() throws DataAccessException{
        authDAO.insertAuth(testAuth1);
        authDAO.deleteAuth(testAuth1.authToken());
        AuthData accessAuth1 = authDAO.getAuth(testAuth1.authToken());
        Assertions.assertNull(accessAuth1);
    }
    @Test
    @DisplayName("Remove Non Existant Auth")
    public void removeNotRealAuth() throws DataAccessException{
        Assertions.assertDoesNotThrow(()->{
            authDAO.deleteAuth(testAuth1.authToken());
        });
    }
}
