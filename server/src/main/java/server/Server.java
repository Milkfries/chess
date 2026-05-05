package server;

import java.util.Map;

import com.google.gson.Gson;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import request.RegisterRequest;
import result.RegisterResult;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final Gson serializer;
    //databases
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    // private final GameDOA gameDAO;

    //services
    private final UserService userService;


    public Server() {


        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        // Register your endpoints and exception handlers here.


        serializer = new Gson();
        
        // serialize to JSON example
        // var json = serializer.toJson(game);

        // // deserialize back to ChessGame example
        // game = serializer.fromJson(json, ChessGame.class);

        //initialize database
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();

        //initialize services
        userService = new UserService(userDAO, authDAO);
        
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        javalin.delete("/db", this::delete);
        javalin.post("/user",this::register);
        javalin.post("/session",this::login);
        javalin.delete("/session",this::logout);
        javalin.get("/game",this::listGames);
        javalin.post("/game",this::createGame);
        javalin.put("/game",this::joinGame);
        
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
    private void delete(Context ctx){

    }
    private void register(Context ctx){
        // var serializer = new Gson();
        try{
            RegisterRequest registerRequest = serializer.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult registerResult = userService.register(registerRequest);

            ctx.status(200);
            ctx.result(new Gson().toJson(registerResult));
        }
        catch(BadRequestException dre){
            errorPage(ctx, 400, dre);
        }
        catch(AlreadyTakenException ate){
            errorPage(ctx, 403, ate);
        }
        catch(Exception e){
            errorPage(ctx, 500, e);
        }
        
    }
    private void login(Context ctx){

    }
    private void logout(Context ctx){

    }
    private void listGames(Context ctx){

    }
    private void createGame(Context ctx){

    }
    private void joinGame(Context ctx){

    }

    private void errorPage(Context ctx, int errorNumber, Exception error){
        ctx.contentType("application/json");
        ctx.status(errorNumber);
        ctx.result(new Gson().toJson(Map.of("message", error.getMessage())));
    }

}
