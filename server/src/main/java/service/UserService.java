package service;

import java.util.UUID;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import request.*;
import result.*;


public class UserService {
	private final UserDAO userDAO;
	private final AuthDAO authDAO;
    public UserService(UserDAO userDAO, AuthDAO authDAO){
		this.userDAO = userDAO;
		this.authDAO = authDAO;
    }
	public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException,BadRequestException,AlreadyTakenException {
		String username = registerRequest.username();
		String password = registerRequest.password();
		String email = registerRequest.email();

		if(username == null || password == null || username.isBlank() || password.isBlank()){
			throw new BadRequestException("Error: bad request");
		}
		if(userDAO.getUser(username)!= null){
			throw new AlreadyTakenException("Error: already taken");
		}
		UserData newUser = new UserData(username, password,email);
		userDAO.insertUser(newUser);

		String authToken = generateToken();
		AuthData authData = new AuthData(authToken, username);

		RegisterResult result = new RegisterResult(username, authToken);

		authDAO.insertAuth(authData);
		

		return result;
	}
	public LoginResult login(LoginRequest loginRequest) throws DataAccessException, BadRequestException, UnauthorizedException {
		String username = loginRequest.username();
		String password = loginRequest.password();
		
		if(username == null || password == null || username.isBlank() || password.isBlank()){
			throw new BadRequestException("Error: bad request");
		}

		UserData userData = userDAO.getUser(username);

		if(userData == null){ // checks for invalid username
			throw new UnauthorizedException("Error: unauthorized");
		}
		
		if(!userData.password().equals(password) ){ // checks for wrong password
			throw new UnauthorizedException("Error: unauthorized");
		}

		String authToken = generateToken();

		AuthData authData = new AuthData(authToken, username);

		authDAO.insertAuth(authData);

		
		return new LoginResult(username, authToken);
	}
	public void logout(LogoutRequest logoutRequest) {
	}

	public static String generateToken() {
    	return UUID.randomUUID().toString();
	}
}
