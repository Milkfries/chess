package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.Locale;

import com.google.gson.Gson;

import request.*;
import result.*;

public class ServerFacade {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private String host;
    private int port;
    private Gson serializer;
    public ServerFacade(String host, int port){
        this.host = host;
        this.port = port;
        this.serializer = new Gson();
    }
    // private void get(String host, int port, String path) throws Exception {
    //     String urlString = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);

    //     HttpRequest request = HttpRequest.newBuilder()
    //             .uri(new URI(urlString))
    //             .timeout(java.time.Duration.ofMillis(5000))
    //             .GET()
    //             .build();

    //     HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    //     if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
    //         System.out.println(httpResponse.body());
    //     } else {
    //         System.out.println("Error: received status code " + httpResponse.statusCode());
    //     }e
    // }
    public RegisterResult register(RegisterRequest registerRequest) throws Exception{
        try{
            // to login - POST /user requires username, password, email (RegisterRequest)
            String bodyJSON = serializer.toJson(registerRequest); // turns request into json for http call
            var httpResponse = sendHTTPRequest("POST","/user",null,bodyJSON); // calls method to send the http request with necessary information
            if(httpResponse.statusCode() != 200){ // checks if the request failed to throw error
                throw new Exception(httpResponse.body());
            }
            return serializer.fromJson(httpResponse.body(), RegisterResult.class); // turns json from request back into an object
        }
        catch(Exception e){
            if(e.getMessage().contains("Error: ")){
                throw new Exception(e.getMessage().substring(e.getMessage().indexOf("Error:"),e.getMessage().length()-2));
            }
            else{
                throw new Exception("An error occured, please try again");
            }
        }
    }
    public LoginResult login(LoginRequest loginRequest)  throws Exception{ 
        try{
            // to login - POST /session requires username, password (LoginRequest)
            String bodyJSON = serializer.toJson(loginRequest);
            var httpResponse = sendHTTPRequest("POST","/session",null,bodyJSON);
            if(httpResponse.statusCode() != 200){
                throw new Exception(httpResponse.body());
            }
            return serializer.fromJson(httpResponse.body(), LoginResult.class);
        }
        catch(Exception e){
            if(e.getMessage().contains("Error: ")){
                throw new Exception(e.getMessage().substring(e.getMessage().indexOf("Error:"),e.getMessage().length()-2));
            }
            else{
                throw new Exception("An error occured, please try again");
            }
        }
    }
    public void logout(LogoutRequest logoutRequest) throws Exception{
        try{
            var httpResponse = sendHTTPRequest("DELETE", "/session", logoutRequest.authToken(), null);
            if(httpResponse.statusCode() != 200){
                throw new Exception(httpResponse.body());
            }
        }
        catch (Exception e){
            if(e.getMessage().contains("Error: ")){
                throw new Exception(e.getMessage().substring(e.getMessage().indexOf("Error:"),e.getMessage().length()-2));
            }
            else{
                throw new Exception("An error occured, please try again");
            }
        }
    }
    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws Exception{
        try{
            String bodyJSON = serializer.toJson(createGameRequest);
            var httpResponse = sendHTTPRequest("POST", "/game", createGameRequest.authToken(), bodyJSON);
            if(httpResponse.statusCode() != 200){
                throw new Exception(httpResponse.body());
            }
            return serializer.fromJson(httpResponse.body(), CreateGameResult.class);
        }
        catch (Exception e){
            if(e.getMessage().contains("Error: ")){
                throw new Exception(e.getMessage().substring(e.getMessage().indexOf("Error:"),e.getMessage().length()-2));
            }
            else{
                throw new Exception("An error occured, please try again");
            }
        }
    }
    public ListGameResult listGames(ListGameRequest listGameRequest) throws Exception{
        try{
            var httpResponse = sendHTTPRequest("GET", "/game", listGameRequest.authToken(), null);
            if(httpResponse.statusCode() != 200){
                throw new Exception(httpResponse.body());
            }
            return serializer.fromJson(httpResponse.body(), ListGameResult.class);
        }
        catch (Exception e){
            if(e.getMessage().contains("Error: ")){
                throw new Exception(e.getMessage().substring(e.getMessage().indexOf("Error:"),e.getMessage().length()-2));
            }
            else{
                throw new Exception("An error occured, please try again");
            }
        }
    } 
    public void joinGame (JoinGameRequest joinGameRequest) throws Exception{
        try{
            String bodyJSON = serializer.toJson(joinGameRequest);
            var httpResponse = sendHTTPRequest("PUT", "/game", joinGameRequest.authToken(), bodyJSON);
            if(httpResponse.statusCode() != 200){
                throw new Exception(httpResponse.body());
            }
        }
        catch (Exception e){
            if(e.getMessage().contains("Error: ")){
                throw new Exception(e.getMessage().substring(e.getMessage().indexOf("Error:"),e.getMessage().length()-2));
            }
            else{
                throw new Exception("An error occured, please try again");
            }
        }
    }
    public void clear() throws Exception{
        try{
            //to clear - DELETE /db
            
            var httpResponse = sendHTTPRequest("DELETE","/db",null,null);

            if(httpResponse.statusCode() != 200){
                throw new Exception(httpResponse.body());
            }
        }
        catch(Exception e){
            if(e.getMessage().contains("Error: ")){
                throw new Exception(e.getMessage().substring(e.getMessage().indexOf("Error:"),e.getMessage().length()-2));
            }
            else{
                throw new Exception("An error occured, please try again");
            }
        }
    }
    private String fullUrl(String host, int port, String path){
        return String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
    }
    private HttpResponse<String> sendHTTPRequest(String method, String path, String authToken, String bodyJSON) throws Exception{
        String url = fullUrl(host, port, path);
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        
        builder.uri(new URI(url)); //URISyntaxExceptionJava

        if(authToken != null){ // add authToken to header
            builder.header("Authorization", authToken);
        }
        builder.timeout(java.time.Duration.ofMillis(5000)); // Do I need this?

        if(method.equals("POST")){
            builder.POST(BodyPublishers.ofString(bodyJSON));
        }
        if(method.equals("PUT")){
            builder.PUT(BodyPublishers.ofString(bodyJSON));
        }
        if(method.equals("GET")){
            builder.GET();
        }
        if(method.equals("DELETE")){
            builder.DELETE();
        }
        // could add HEAD method        

        HttpRequest request = builder.build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()); //IOException, and InterruptedException
    }
}
