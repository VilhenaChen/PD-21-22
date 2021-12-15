package pd.tp.servidor.bd;

import java.sql.*;

public class ComunicacaoBD {

    private final String BD_URL = "jdbc:mysql://localhost:13306/sgbd";
    private final String USERNAME = "sgbd_user";
    private final String PASSWORD = "c4s#fk9bpw2x";
    private static final String SUCESSO = "SUCESSO";
    private static final String PASSWORD_ERRADA = "PASSWORD_ERRADA";
    private static final String UTILIZADOR_INEXISTENTE = "UTILIZADOR_INEXISTENTE";
    private static final String NOME_REPETIDO = "NOME_REPETIDO";
    private static final String USERNAME_REPETIDO = "USERNAME_REPETIDO";
    private static final String NOME_E_ADMIN_JA_EXISTENTES = "NOME_E_ADMIN_JA_EXISTENTES";
    private static final String ADMIN_INEXISTENTE = "ADMIN_INEXISTENTE";
    private static final String NOT_ADMIN = "NOT_ADMIN";
    private static final String GRUPO_INEXISTENTE = "GRUPO_INEXISTENTE";

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

    public String insereUser(String name, String username, String password, int logado) throws SQLException {
        Statement statement = dbConn.createStatement();
        if(verificaExistenciaUser(username)){
            return USERNAME_REPETIDO;
        }
        String sqlQuery = "INSERT INTO User (username, name, password, login) VALUES ('" + username + "','" + name + "','" + password + "','" + logado + "')";
        statement.executeUpdate(sqlQuery);
        statement.close();
        return SUCESSO;
    }

    public String updateUserName(String name, String username) throws SQLException {
        Statement statement = dbConn.createStatement();
        if(!verificaExistenciaUser(username)){
            return UTILIZADOR_INEXISTENTE;
        }
        String sqlQuery = "UPDATE User SET name='" + name + "'WHERE username='" + username + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();
        return SUCESSO;
    }

    public String updateUserUsername(String new_username, String old_username) throws SQLException {
        Statement statement = dbConn.createStatement();
        if(!verificaExistenciaUser(old_username)){
            return UTILIZADOR_INEXISTENTE;
        }

        if(verificaExistenciaUser(new_username)){
            return USERNAME_REPETIDO;
        }
        String sqlQuery = "UPDATE User SET username='" + new_username + "'WHERE username='" + old_username + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();
        return SUCESSO;
    }

    public String updateUserPassword(String password, String username) throws SQLException {
        Statement statement = dbConn.createStatement();
        if(!verificaExistenciaUser(username)){
            return UTILIZADOR_INEXISTENTE;
        }
        String sqlQuery = "UPDATE User SET password='" + password  + "'WHERE username='" + username + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();
        return SUCESSO;
    }

    public String deleteUser(String username) throws SQLException {
        Statement statement = dbConn.createStatement();
        if(!verificaExistenciaUser(username)){
            return UTILIZADOR_INEXISTENTE;
        }
        String sqlQuery = "DELETE FROM User WHERE username='" + username + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();
        return SUCESSO;
    }

    public String loginUser(String username, String password) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT password FROM User WHERE username='" + username + "'";
        String DBPassword;

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        if(!resultSet.next()) {
            System.out.println("O utilizador não existe!");
            resultSet.close();
            statement.close();
            return UTILIZADOR_INEXISTENTE;
        }

        DBPassword = resultSet.getString("password");

        if(DBPassword.equals(password)) {
            System.out.println("Password certa!");
            int login = 1;
            sqlQuery = "UPDATE User SET login='" + login + "' WHERE username='" + username + "'";
            statement.executeUpdate(sqlQuery);
            sqlQuery = "SELECT name FROM User WHERE username='" + username + "'";
            String DBName;

            resultSet = statement.executeQuery(sqlQuery);

            resultSet.next();
            DBName = resultSet.getString("name");

            resultSet.close();
            statement.close();
            return SUCESSO + "," + DBName;
        }
        else{
            System.out.println("Password errada!");
            resultSet.close();
            statement.close();
            return PASSWORD_ERRADA;
        }
    }

    public String logoutUser(String username) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT login FROM User WHERE username='" + username + "'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);
        if(!resultSet.next()) {
            System.out.println("O utilizador não existe!");
            resultSet.close();
            statement.close();
            return UTILIZADOR_INEXISTENTE;
        }
        int logout = 0;
        sqlQuery = "UPDATE User SET login='" + logout + "'WHERE username='" + username + "'";
        statement.executeUpdate(sqlQuery);
        resultSet.close();
        statement.close();
        return SUCESSO;
    }

    public boolean verificaExistenciaUser(String username) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT name FROM User WHERE username='" + username + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        if(!resultSet.next()) {
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
                return NOME_E_ADMIN_JA_EXISTENTES;
            }
        }
        else{
            statement.close();
            return ADMIN_INEXISTENTE;
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
                        return SUCESSO;
                    }
                    else {
                        statement.close();
                        return NOME_E_ADMIN_JA_EXISTENTES;
                    }
                }
                else {
                    statement.close();
                    return NOT_ADMIN;
                }
            }
            else {
                statement.close();
                return GRUPO_INEXISTENTE;
            }
        }
        else {
            statement.close();
            return ADMIN_INEXISTENTE;
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
