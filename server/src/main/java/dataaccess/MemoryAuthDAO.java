package dataaccess;

import java.util.HashMap;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO{
    private HashMap<String, AuthData> auths; // Key is authToken

    public MemoryAuthDAO(){
        auths = new HashMap<>();
    }

    @Override
    public void insertAuth(AuthData authData) throws DataAccessException{
        auths.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException{
        return auths.get(authToken);
    }
    @Override
    public void clear() throws DataAccessException{
        auths.clear();
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }
}
