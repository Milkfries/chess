package dataaccess;

import java.sql.ResultSet;

import model.UserData;

public class SQLUserDAO implements UserDAO{
    public SQLUserDAO() throws DataAccessException{
        DatabaseManager.createDatabase();
    }
    @Override
    public void insertUser(UserData userData) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            String username = userData.username();
            String password = userData.password();
            String email = userData.email();
            if(email == null){
                email = "";
            }
            var statement = "INSERT INTO userData (username, password, email) VALUES (?,?,?)";

            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1,username);
                preparedStatement.setString(2,password);
                preparedStatement.setString(3,email);
                preparedStatement.executeUpdate();
            }

        }
        catch (Exception e){
            throw new DataAccessException("error: failed to insert userData");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT username, password, email FROM userData WHERE username = ?";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1,username);
                ResultSet rs = preparedStatement.executeQuery();
                if(rs.next()){
                    String usernameResult = rs.getString("username");
                    String passwordResult = rs.getString("password");
                    String emailResult = rs.getString("email");
                    return new UserData(usernameResult,passwordResult,emailResult);
                }
            }
            
        }
        catch(Exception e){
            throw new DataAccessException("error: failed to get userData");
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "TRUNCATE userData";

            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }

        }
        catch (Exception e){
            throw new DataAccessException("error: failed to delete userData");
        }
    }

}
