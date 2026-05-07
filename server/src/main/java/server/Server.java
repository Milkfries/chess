package server;

import java.util.Map;

import com.google.gson.Gson;


import io.javalin.*;
import io.javalin.http.Context;
import request.*;
import result.*;
import service.*;
import dataaccess.*;

public class Server {

    private final Javalin javalin;
    private final Gson serializer;
    //databases
    private AuthDAO authDAO;
    private UserDAO userDAO;
    private GameDAO gameDAO;

    //services
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;


    public Server(){


        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        // Register your endpoints and exception handlers here.


        serializer = new Gson();
        
        // serialize to JSON example
        // var json = serializer.toJson(game);

        // // deserialize back to ChessGame example
        // game = serializer.fromJson(json, ChessGame.class);

        //initialize database
        try{
            authDAO = new SQLAuthDAO();
            userDAO = new SQLUserDAO();
        }
        catch(Exception e){
            // authDAO = new MemoryAuthDAO();
            System.out.println(e.getMessage());
        }
        
        
        
        gameDAO = new MemoryGameDAO();

        //initialize services
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        
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
        try{
            clearService.clear();
            ctx.status(200);
            ctx.result("{}");
        }
        catch(Exception e){
            errorPage(ctx, 500, e);
        }
        
    }
    private void register(Context ctx){
        try{
            RegisterRequest registerRequest = serializer.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult registerResult = userService.register(registerRequest);

            ctx.status(200);
            ctx.result(serializer.toJson(registerResult));
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
        try{
            LoginRequest loginRequest = serializer.fromJson(ctx.body(), LoginRequest.class);
            LoginResult loginResult = userService.login(loginRequest);

            ctx.status(200);
            ctx.result(serializer.toJson(loginResult));
        }
        catch (BadRequestException bre){
            errorPage(ctx, 400, bre);
        }
        catch (UnauthorizedException ue){
            errorPage(ctx, 401, ue);
        }
        catch (Exception e){
            errorPage(ctx, 500, e);
        }
    }
    private void logout(Context ctx){
        try{
            String authToken = ctx.header("Authorization");
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            userService.logout(logoutRequest);
            ctx.status(200);
            ctx.result("{}");
        }
        catch (UnauthorizedException ue){
            errorPage(ctx, 401, ue);
        }
        catch(Exception e){
            errorPage(ctx, 500, e);
        }
    }
    private void listGames(Context ctx){
        try{
            String authToken = ctx.header("Authorization");
            ListGameRequest listGameRequest = new ListGameRequest(authToken);
            ListGameResult listGameResult = gameService.listGames(listGameRequest);

            ctx.status(200);
            ctx.result(serializer.toJson(listGameResult));
        }
        catch(UnauthorizedException ue){
            errorPage(ctx, 401, ue);
        }
        catch(Exception e){
            errorPage(ctx, 500, e);
        }
    }
    private void createGame(Context ctx){
        try{
            String authToken = ctx.header("Authorization");
            CreateGameRequest temp_createGameRequest = serializer.fromJson(ctx.body(),CreateGameRequest.class);
            CreateGameRequest createGameRequest = new CreateGameRequest(authToken, temp_createGameRequest.gameName());
            CreateGameResult createGameResult = gameService.createGame(createGameRequest);

            ctx.status(200);
            ctx.result(serializer.toJson(createGameResult));
        }
        catch(BadRequestException bre){
            errorPage(ctx, 400, bre);
        }
        catch(UnauthorizedException ue){
            errorPage(ctx, 401, ue);
        }
        catch(Exception e){
            errorPage(ctx, 500, e);
        }
    }
    private void joinGame(Context ctx){
        try{
            String authToken = ctx.header("Authorization");
            JoinGameRequest temp_joinGameRequest = serializer.fromJson(ctx.body(),JoinGameRequest.class);
            JoinGameRequest joinGameRequest = new JoinGameRequest(authToken,temp_joinGameRequest.playerColor(),temp_joinGameRequest.gameID());
            gameService.joinGame(joinGameRequest);

            ctx.status(200);
            ctx.result("{}");
        }
        catch(BadRequestException bre){
            errorPage(ctx, 400, bre);
        }
        catch(UnauthorizedException ue){
            errorPage(ctx, 401, ue);
        }
        catch(AlreadyTakenException ate){
            errorPage(ctx, 403, ate);
        }
        catch(Exception e){
            errorPage(ctx, 500, e);
        }
    }

    private void errorPage(Context ctx, int errorNumber, Exception error){
        ctx.contentType("application/json");
        ctx.status(errorNumber);
        ctx.result(new Gson().toJson(Map.of("message", error.getMessage())));
    }

}
