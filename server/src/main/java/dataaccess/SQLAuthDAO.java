package dataaccess;

import java.sql.ResultSet;

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
        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT authData, username FROM authData WHERE authData = ?";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1,authToken);
                ResultSet rs = preparedStatement.executeQuery();
                if(rs.next()){
                    String authTokenResult = rs.getString("authToken");
                    String usernameResult = rs.getString("username");
                    return new AuthData(authTokenResult,usernameResult);
                }
            }
            
        }
        catch(Exception e){
            throw new DataAccessException("Failed to get authData");
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "DELETE FROM authData WHERE authToken = ?";

            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1,authToken);
                preparedStatement.executeUpdate();
            }

        }
        catch (Exception e){
            throw new DataAccessException("Failed to delete authData");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "TRUNCATE authData";

            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }

        }
        catch (Exception e){
            throw new DataAccessException("Failed to delete authData");
        }
    }
    
}
