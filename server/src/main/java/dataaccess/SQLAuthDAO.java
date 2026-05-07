package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO {

    @Override
    public void insertAuth(AuthData authData) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            String authToken = authData.authToken();
            String username = authData.username();
            var statement = "INSERT INTO authData (authToken, username) VALUES (?,?)";
            
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1,authToken);
                preparedStatement.setString(2,username);
                preparedStatement.executeUpdate();
            }

        }
        catch (Exception e){
            throw new DataAccessException("Failed to insert authData");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAuth'");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAuth'");
    }

    @Override
    public void clear() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clear'");
    }
    
}
