package pd.tp.servidor.bd;

import java.sql.*;

public class ComunicacaoBD {
    private final String BD_URL = "jdbc:mysql://localhost:13306/sgbd";
    private final String USERNAME = "sgbd_user";
    private final String PASSWORD = "c4s#fk9bpw2x";

    private Connection dbConn;

    public ComunicacaoBD() throws SQLException {
        dbConn = DriverManager.getConnection(BD_URL, USERNAME, PASSWORD);
    }

    public void close() throws SQLException {
        if(dbConn != null)
            dbConn.close();
    }

    public void listaUsers() throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT username, name, password FROM User";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next()) {
            String username = resultSet.getString("username");
            String nome = resultSet.getString("name");
            String password = resultSet.getString("password");
            System.out.println("Username: " + username + " Nome: " + nome + " Password: " + password);
        }

        resultSet.close();
        statement.close();
    }

    public void insereUser(String name, String username, String password, int logado) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "INSERT INTO User (username, name, password, login) VALUES ('" + username + "','" + name + "','" + password + "','" + logado + "')";
        statement.executeUpdate(sqlQuery);
        statement.close();
    }

    public void updateUserName(String name, String username) throws SQLException{
        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE User SET name='" + name + "'WHERE username='" + username + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();
    }

    public void updateUserUsername(String old_username, String new_username) throws SQLException{
        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE User SET username='" + new_username + "'WHERE username='" + old_username + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();
    }

    public void updateUserPassword(String password, String username) throws SQLException{
        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE User SET password='" + password  + "'WHERE username='" + username + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();
    }

    public void updateUserLogin(String username,int logado) throws SQLException{
        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE User SET login='" + logado + "'WHERE username='" + username + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();
    }

    public void deleteUser(String username) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "DELETE FROM User WHERE username='" + username + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();
    }
}
