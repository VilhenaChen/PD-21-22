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

    //Funções User
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

    public void updateUserName(String name, String username) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE User SET name='" + name + "'WHERE username='" + username + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();
    }

    public void updateUserUsername(String old_username, String new_username) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE User SET username='" + new_username + "'WHERE username='" + old_username + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();
    }

    public void updateUserPassword(String password, String username) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE User SET password='" + password  + "'WHERE username='" + username + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();
    }

    public void updateUserLogin(String username,int logado) throws SQLException {
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

    public String loginUser(String username, String password) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT password FROM User WHERE username='" + username + "'";
        String DBPassword;

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        if(resultSet.next() == false) {
            System.out.println("O utilizador não existe!");
            resultSet.close();
            statement.close();
            return "UTILIZADOR_INEXISTENTE";
        }
        DBPassword = resultSet.getString("password");
        if(DBPassword.equals(password)){
            System.out.println("Password certa!");
            resultSet.close();
            statement.close();
            return "SUCESSO";
        }
        else{
            System.out.println("Password errada!");
            resultSet.close();
            statement.close();
            return "PASSWORD_ERRADA";
        }
    }

    public boolean verificaExistenciaUser(String username) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT name FROM User WHERE username='" + username + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        if(resultSet.next()==false) {
            resultSet.close();
            statement.close();
            return false;
        }
        else{
            resultSet.close();
            statement.close();
            return true;
        }
    }

    //Funções Grupo


    public String createGroup(String name, String username) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlQuery;
        if(verificaExistenciaUser(username)){
            if (!verificaSeExisteGrupoComNomeEAdmin(name,username)){
                sqlQuery = "INSERT INTO Group (idGroup,name,admin) VALUES ('" + getNextIdGroup() + "','" + name + "','" + username + "')";
                statement.executeUpdate(sqlQuery);
                statement.close();
                return "SUCESSO";
            }
            else {
                statement.close();
                return "NOME_E_ADMIN_JA_EXISTENTES";
            }
        }
        else{
            statement.close();
            return "ADMINISTRADOR_INEXISTENTE";
        }
    }

    public boolean verificaSeExisteGrupoComNomeEAdmin(String name, String admin) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT name FROM Group WHERE admin='" + admin + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        if(!resultSet.next()) {
            resultSet.close();
            statement.close();
            return false;
        }

        while (resultSet.next()){
            if (resultSet.getString("name").equals(name)){
                resultSet.close();
                statement.close();
                return true;
            }
        }
        resultSet.close();
        statement.close();
        return false;
    }

    public int getNextIdGroup() throws SQLException {
        int idAtribuir = -1;
        Statement statement = dbConn.createStatement();
        ResultSet resultSet;
        do{
            idAtribuir++;
            String sqlQuery = "SELECT name FROM Group WHERE id='" + idAtribuir + "'";
            resultSet = statement.executeQuery(sqlQuery);
        }while (resultSet.next());

        resultSet.close();
        statement.close();
        return idAtribuir;
    }


    public String updateGroupName(String name, int idGroup, String admin) throws SQLException{
        Statement statement = dbConn.createStatement();
        if(verificaExistenciaUser(admin))
        {
            if(verificaExistenciaGrupo(idGroup)){
                if(verificaAdminGrupo(idGroup,admin)){
                    if(verificaSeExisteGrupoComNomeEAdmin(name,admin)){
                        String sqlQuery = "UPDATE Group SET name='" + name + "'WHERE idGroup='" + idGroup + "'";
                        statement.executeUpdate(sqlQuery);
                        statement.close();
                        return "SUCESSO";
                    }
                    else {
                        statement.close();
                        return "NOME_E_ADMIN_JA_EXISTENTES";
                    }
                }
                else {
                    statement.close();
                    return "NAO_E_O_ADMIN";
                }
            }
            else {
                statement.close();
                return "GRUPO_INEXISTENTE";
            }
        }
        else {
            statement.close();
            return "ADMINISTRADOR_INEXISTENTE";
        }

    }

    public boolean verificaAdminGrupo(int idGroup, String admin) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT admin FROM Group WHERE idGroup='" + idGroup + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        if(resultSet.getString("admin").equals(admin)) {
            resultSet.close();
            statement.close();
            return false;
        }
        else{
            resultSet.close();
            statement.close();
            return true;
        }
    }

    public boolean verificaExistenciaGrupo(int idGroup) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT name FROM Group WHERE idGroup='" + idGroup + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        if(resultSet.next()==false) {
            resultSet.close();
            statement.close();
            return false;
        }
        else{
            resultSet.close();
            statement.close();
            return true;
        }
    }
}
