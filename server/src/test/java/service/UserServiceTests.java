package service;

import org.junit.jupiter.api.*;

import dataaccess.*;
import request.*;
import result.*;

public class UserServiceTests {
    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static UserService userService;

    private static RegisterRequest registerRequest1;
    private static RegisterRequest registerRequest2;
    private static RegisterRequest forgotUsername;
    private static RegisterRequest forgotPassword;
    private static RegisterRequest forgotEmail;

    @BeforeAll
    public static void init(){
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);

        registerRequest1 = new RegisterRequest("Michael", "kingofpop123", "jackson1@gmail.com");
        registerRequest2 = new RegisterRequest("Elvis", "rockandroller", "therealpres@gmail.com");
        forgotUsername = new RegisterRequest(null, "bestbeatle", "fabfour@gmail.com");
        forgotPassword = new RegisterRequest("bobdylan", null, "completeunknown@gmail.com");
        forgotEmail = new RegisterRequest("EltonJohn","ogpianoman",null);
        
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    @DisplayName("Register User")
    public void registerUser() throws DataAccessException, ServiceException{ 
        RegisterResult registerResult = userService.register(registerRequest1);
        Assertions.assertNotNull(registerResult);
        Assertions.assertEquals(registerRequest1.username(), registerResult.username());
        Assertions.assertNotNull(registerResult.authToken());
        Assertions.assertFalse(registerResult.authToken().isBlank());
    }

    @Test
    @DisplayName("Register Multiple Users")
    public void registerMultiUser() throws DataAccessException, ServiceException{ 
        RegisterResult registerResult1 = userService.register(registerRequest1);
        RegisterResult registerResult2 = userService.register(registerRequest2);
        Assertions.assertNotEquals(registerResult1,registerResult2);
    }

    @Test
    @DisplayName("No Username")
    public void noUsername() throws DataAccessException, ServiceException{
        ServiceException exception = Assertions.assertThrows(ServiceException.class,()->{
            userService.register(forgotUsername);
            });
        Assertions.assertEquals("Error: bad request", exception.getMessage());
    }
    @Test
    @DisplayName("No Password")
    public void noPassword() throws DataAccessException, ServiceException{
        ServiceException exception = Assertions.assertThrows(ServiceException.class,()->{
            userService.register(forgotPassword);
            });

        Assertions.assertEquals("Error: bad request", exception.getMessage());

    }
    @Test
    @DisplayName("No Email")
    public void noEmail() throws DataAccessException, ServiceException{
        Assertions.assertDoesNotThrow(()->{
            userService.register(forgotEmail);
            });
    }
    @Test
    @DisplayName("Duplicate Username")
    public void duplicateUsername() throws DataAccessException, ServiceException{
        RegisterRequest duplicateRequest = new RegisterRequest(registerRequest1.username(), "saferPassword", "dontspamme@gmail.com");

        userService.register(registerRequest1);

        ServiceException exception = Assertions.assertThrows(ServiceException.class,()->{
            userService.register(duplicateRequest);
        });

        Assertions.assertEquals("Error: already taken", exception.getMessage());

    }
}
