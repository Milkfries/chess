package client;

import org.junit.jupiter.api.*;

import request.*;
import result.*;


public class ServerFacadeTests {

    private static ServerFacade server;

    @BeforeAll
    public static void init() {
        server = new ServerFacade("localhost");
    }

    @AfterAll
    static void stopServer() {
        server.stopServer();
    }


    // @Test
    // public void sampleTest() {
    //     LoginRequest request = new LoginRequest();
    //     String result = facade.login(request);
    //     Assertions.assertTrue(true);
    // }

}
