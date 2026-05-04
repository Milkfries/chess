package dataaccess;

import model.AuthData;

public interface AuthDAO {
    public void insertAuth(AuthData authData) throws DataAccessException;
    public AuthData getAuth(String authToken) throws DataAccessException;
    public void clear() throws DataAccessException;
}
