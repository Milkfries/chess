package dataaccess;

import model.UserData;

public interface UserDAO {

    public void insertUser(UserData u) throws DataAccessException;
    public UserData getUser(String username) throws DataAccessException;
    public void clear() throws DataAccessException;
}
