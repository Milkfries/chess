package client;

import org.junit.jupiter.api.*;

import request.*;
import result.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("localhost", port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    // @Test
    // public void sampleTest() {
    //     LoginRequest request = new LoginRequest();
    //     String result = facade.login(request);
    //     Assertions.assertTrue(true);
    // }

}
