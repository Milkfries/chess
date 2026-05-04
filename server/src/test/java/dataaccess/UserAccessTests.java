package dataaccess;

import model.UserData;

import org.junit.jupiter.api.*;


public class UserAccessTests {
    private static UserDAO userDataAccess;
    private static UserData testUser1;
    private static UserData testUser2;
    private static UserData testUser3;

    @BeforeAll
    public static void init(){
        userDataAccess = new MemoryUserDAO();
        testUser1 = new UserData("Bill", "pass1234","billgates@gmail.com");
        testUser2 = new UserData("Steve", "mypassword","jobsteve@gmail.com");
        testUser3 = new UserData("Mark", "ilovemymom","markzuck@gmail.com");
    }
    @BeforeEach
    public void setup() throws DataAccessException {
        userDataAccess.clear();
    }
    @Test
    @DisplayName("Add & Get User")
    public void addGetUser() throws DataAccessException{
        userDataAccess.insertUser(testUser1);
        UserData accessUser = userDataAccess.getUser(testUser1.username());
        Assertions.assertEquals(accessUser,testUser1);
    }
    @Test
    @DisplayName("Add & Get Multiple Users")
    public void addGetUserMultiple() throws DataAccessException{
        userDataAccess.insertUser(testUser1);
        userDataAccess.insertUser(testUser2);
        userDataAccess.insertUser(testUser3);
        UserData accessUser1 = userDataAccess.getUser(testUser1.username());
        UserData accessUser2 = userDataAccess.getUser(testUser2.username());
        UserData accessUser3 = userDataAccess.getUser(testUser3.username());
        Assertions.assertEquals(accessUser1,testUser1);
        Assertions.assertEquals(accessUser2,testUser2);
        Assertions.assertEquals(accessUser3,testUser3);
    }
    @Test
    @DisplayName("Get Nonexistant User")
    public void getNullUser() throws DataAccessException{
        userDataAccess.insertUser(testUser1);
        UserData accessUser = userDataAccess.getUser(testUser2.username());
        Assertions.assertNull(accessUser);
    }
    @Test
    @DisplayName("Clear User Database")
    public void clearUser() throws DataAccessException{
        userDataAccess.insertUser(testUser1);
        userDataAccess.insertUser(testUser2);
        userDataAccess.insertUser(testUser3);
        userDataAccess.clear();
        UserData accessUser1 = userDataAccess.getUser(testUser1.username());
        UserData accessUser2 = userDataAccess.getUser(testUser2.username());
        UserData accessUser3 = userDataAccess.getUser(testUser3.username());
        Assertions.assertNull(accessUser1);
        Assertions.assertNull(accessUser2);
        Assertions.assertNull(accessUser3);
    }
    @Test
    @DisplayName("Overwrite User")
    public void overwriteUser() throws DataAccessException{
        userDataAccess.insertUser(testUser1);
        UserData replaceUser = new UserData(testUser1.username(), "saferPass123", "billmicrosoft@gmail.com");
        userDataAccess.insertUser(replaceUser);
        UserData accessUser1 = userDataAccess.getUser(testUser1.username());
        UserData accessUser2 = userDataAccess.getUser(replaceUser.username());
        Assertions.assertEquals(accessUser1, accessUser2);
        Assertions.assertEquals(accessUser1, replaceUser);
    }
    @Test
    @DisplayName("Case Sensativity")
    public void caseSensativeUser() throws DataAccessException{
        
        UserData firstUser = new UserData(testUser1.username().toLowerCase(), "security1234", "billygoats@gmail.com");
        UserData similarUser = new UserData(testUser1.username().toUpperCase(), "saferPass123", "billmicrosoft@gmail.com");
        userDataAccess.insertUser(firstUser);
        userDataAccess.insertUser(similarUser);
        UserData accessUser1 = userDataAccess.getUser(firstUser.username());
        UserData accessUser2 = userDataAccess.getUser(similarUser.username());
        Assertions.assertNotEquals(accessUser1, accessUser2);
        Assertions.assertEquals(accessUser1, firstUser);
        Assertions.assertEquals(accessUser2, similarUser);
    }
}
