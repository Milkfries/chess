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
	public RegisterResult register(RegisterRequest registerRequest) throws ServiceException,DataAccessException {
		String username = registerRequest.username();
		String password = registerRequest.password();
		String email = registerRequest.email();

		if(username == null || password == null || username.isBlank() || password.isBlank()){
			throw new ServiceException("Error: bad request");
		}
		if(userDAO.getUser(username)!= null){
			throw new ServiceException("Error: already taken");
		}
		UserData newUser = new UserData(username, password,email);
		userDAO.insertUser(newUser);

		String authToken = generateToken();
		AuthData authData = new AuthData(authToken, username);

		RegisterResult result = new RegisterResult(username, authToken);

		authDAO.insertAuth(authData);
		

		return result;
	}
	public LoginResult login(LoginRequest loginRequest) {
		return null;
	}
	public void logout(LogoutRequest logoutRequest) {
	}

	public static String generateToken() {
    	return UUID.randomUUID().toString();
	}
}
