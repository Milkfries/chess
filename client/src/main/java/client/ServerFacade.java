package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
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

    private String fullUrl(String host, int port, String path){
        return String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
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
            // to login - POST /user requires username, password, email
            String bodyJSON = serializer.toJson(registerRequest);
            String url = fullUrl(host, port, "/user");
            HttpRequest.Builder builder = HttpRequest.newBuilder();
            builder.uri(new URI(url)); //URISyntaxExceptionJava
            // request.timeout(java.time.Duration.ofMillis(5000)) Do I want this?
            builder.POST(BodyPublishers.ofString(bodyJSON));

            HttpRequest request = builder.build();
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString()); //IOException, and InterruptedException 
            if(httpResponse.statusCode() != 200){
                throw new Exception(httpResponse.body());
            }
            RegisterResult result = serializer.fromJson(httpResponse.body(), RegisterResult.class);
            return result;
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
        /**  Returns authToken
         *
        */

        try{
            // to login - POST /user requires username, password, email
            String bodyJSON = serializer.toJson(loginRequest);
            String url = fullUrl(host, port, "/session");
            HttpRequest.Builder builder = HttpRequest.newBuilder();
            builder.uri(new URI(url)); //URISyntaxExceptionJava
            // request.timeout(java.time.Duration.ofMillis(5000)) Do I want this?
            builder.POST(BodyPublishers.ofString(bodyJSON));

            HttpRequest request = builder.build();
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString()); //IOException, and InterruptedException 
            if(httpResponse.statusCode() != 200){
                throw new Exception(httpResponse.body());
            }
            LoginResult result = serializer.fromJson(httpResponse.body(), LoginResult.class);
            return result;
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
    
    public void clear() throws Exception{
        try{
            //to clear - DELETE /db
            String url = fullUrl(host, port, "/db");
            HttpRequest.Builder builder = HttpRequest.newBuilder();
            builder.uri(new URI(url)); //URISyntaxExceptionJava
            builder.DELETE();

            HttpRequest request = builder.build();
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString()); //IOException, and InterruptedException 
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
}
