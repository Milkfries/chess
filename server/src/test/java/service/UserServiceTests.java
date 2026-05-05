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
    private static RegisterRequest registerForgotUsername;
    private static RegisterRequest registerForgotPassword;
    private static RegisterRequest registerForgotEmail;

    private static LoginRequest loginRequest1;
    private static LoginRequest loginRequest2;
    private static LoginRequest loginForgotUsername;
    private static LoginRequest loginForgotPassword;


    @BeforeAll
    public static void init(){
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);

        registerRequest1 = new RegisterRequest("Michael", "kingofpop123", "jackson1@gmail.com");
        registerRequest2 = new RegisterRequest("Elvis", "rockandroller", "therealpres@gmail.com");
        registerForgotUsername = new RegisterRequest(null, "bestbeatle", "fabfour@gmail.com");
        registerForgotPassword = new RegisterRequest("bobdylan", null, "completeunknown@gmail.com");
        registerForgotEmail = new RegisterRequest("EltonJohn","ogpianoman",null);

        loginRequest1 = new LoginRequest("Michael","kingofpop123");
        loginRequest2 = new LoginRequest("Elvis","rockandroller");
        loginForgotUsername = new LoginRequest(null, "bestbeatle");
        loginForgotPassword = new LoginRequest("bobdylan", null);
        

    }

    @BeforeEach
    public void setup() throws Exception {
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    @DisplayName("Register User")
    public void registerUser() throws Exception{ 
        RegisterResult registerResult = userService.register(registerRequest1);
        Assertions.assertNotNull(registerResult);
        Assertions.assertEquals(registerRequest1.username(), registerResult.username());
        Assertions.assertNotNull(registerResult.authToken());
        Assertions.assertFalse(registerResult.authToken().isBlank());
    }

    @Test
    @DisplayName("Register Multiple Users")
    public void registerMultiUser() throws Exception{ 
        RegisterResult registerResult1 = userService.register(registerRequest1);
        RegisterResult registerResult2 = userService.register(registerRequest2);
        Assertions.assertNotEquals(registerResult1,registerResult2);
    }

    @Test
    @DisplayName("Register No Username")
    public void noUsername() throws Exception{
        BadRequestException exception = Assertions.assertThrows(BadRequestException.class,()->{
            userService.register(registerForgotUsername);
            });
        Assertions.assertEquals("Error: bad request", exception.getMessage());
    }
    @Test
    @DisplayName("Register No Password")
    public void noPassword() throws Exception{
        BadRequestException exception = Assertions.assertThrows(BadRequestException.class,()->{
            userService.register(registerForgotPassword);
            });

        Assertions.assertEquals("Error: bad request", exception.getMessage());

    }
    @Test
    @DisplayName("Register No Email")
    public void noEmail() throws Exception{
        Assertions.assertDoesNotThrow(()->{
            userService.register(registerForgotEmail);
            });
    }
    @Test
    @DisplayName("Register Duplicate Username")
    public void duplicateUsername() throws Exception{
        RegisterRequest duplicateRequest = new RegisterRequest(registerRequest1.username(), "saferPassword", "dontspamme@gmail.com");

        userService.register(registerRequest1);

        AlreadyTakenException exception = Assertions.assertThrows(AlreadyTakenException.class,()->{
            userService.register(duplicateRequest);
        });

        Assertions.assertEquals("Error: already taken", exception.getMessage());

    }

    @Test
    @DisplayName("Login")
    public void loginOnce() throws Exception{
        Assertions.assertDoesNotThrow(()->{
            userService.register(registerRequest1);
            userService.login(loginRequest1);   
        });
    }
    @Test
    @DisplayName("Login Verified")
    public void loginCheck() throws Exception{
        RegisterResult registerResult = userService.register(registerRequest1);
        LoginResult loginResult = userService.login(loginRequest1);  
        Assertions.assertEquals(authDAO.getAuth(loginResult.authToken()).username(),registerRequest1.username());
        Assertions.assertNotEquals(registerResult.authToken(), loginResult.authToken());
    }

    @Test
    @DisplayName("Login Multiple")
    public void loginMultiple() throws Exception{
        Assertions.assertDoesNotThrow(()->{
            userService.register(registerRequest1);
            userService.register(registerRequest2);
            userService.login(loginRequest1);   
            userService.login(loginRequest2);
        });
    }

    @Test
    @DisplayName("Login Repeat")
    public void loginRepeat() throws Exception{
        userService.register(registerRequest1);
        LoginResult loginResult1 = userService.login(loginRequest1);   
        LoginResult loginResult2 = userService.login(loginRequest1);

        Assertions.assertNotEquals(loginResult1.authToken(), loginResult2.authToken());
        Assertions.assertEquals(loginResult1.username(),loginResult2.username());
    }

    @Test
    @DisplayName("Login Multiple Verify")
    public void loginMultipleCheck() throws Exception{
        userService.register(registerRequest1);
        userService.register(registerRequest2);
        LoginResult loginResult1 = userService.login(loginRequest1);   
        LoginResult loginResult2 = userService.login(loginRequest2);
        Assertions.assertNotEquals(loginResult1.authToken(), loginResult2.authToken());
        Assertions.assertEquals(loginResult1.username(), registerRequest1.username());
        Assertions.assertEquals(loginResult2.username(), registerRequest2.username());
    }

    @Test
    @DisplayName("Login before Register")
    public void loginNoRegister() throws Exception{
        Assertions.assertThrows(UnauthorizedException.class, ()->{
            userService.login(loginRequest1);
        });
    }

    @Test
    @DisplayName("Login wrong Password")
    public void loginWrongPassword() throws Exception{
        userService.register(registerRequest1);
        LoginRequest loginWrongPassword = new LoginRequest(loginRequest1.username(), "iforgot");
        Assertions.assertThrows(UnauthorizedException.class, ()->{
            userService.login(loginWrongPassword);
        });
    }

    @Test
    @DisplayName("Login no username")
    public void loginNoUsername() throws Exception{
        Assertions.assertThrows(BadRequestException.class, ()->{
            userService.login(loginForgotUsername);
        });
    }

    @Test
    @DisplayName("Login no Password")
    public void loginNoPassword() throws Exception{
        Assertions.assertThrows(BadRequestException.class, ()->{
            userService.login(loginForgotPassword);
        });
    }

    @Test
    @DisplayName("Login Case Sensative")
    public void loginCaseSensative() throws Exception{
        userService.register(registerRequest2);
        LoginRequest login1 = new LoginRequest(loginRequest2.username().toLowerCase(),loginRequest2.password());
        LoginRequest login2 = new LoginRequest(loginRequest2.username().toUpperCase(),loginRequest2.password());
        Assertions.assertThrows(UnauthorizedException.class, ()->{
            userService.login(login1);
        });
        Assertions.assertThrows(UnauthorizedException.class, ()->{
            userService.login(login2);
        });
    }

    
}

