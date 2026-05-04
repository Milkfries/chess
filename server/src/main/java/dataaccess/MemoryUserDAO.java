package dataaccess;

import java.util.HashMap;
import model.UserData;

public class MemoryUserDAO implements UserDAO{
    private HashMap<String,UserData> users;

    
    public MemoryUserDAO(){
        users = new HashMap<>();
    }
    
    @Override
    public void insertUser(UserData u) throws DataAccessException{
        users.put(u.username(),u);        
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        return users.get(username);
    }

    @Override
    public void clear() throws DataAccessException{
        users.clear();
    }
}
