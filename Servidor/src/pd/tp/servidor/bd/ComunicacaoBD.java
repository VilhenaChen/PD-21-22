package pd.tp.servidor.bd;

import javax.swing.plaf.nimbus.State;
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
    private static final String NOT_MEMBRO = "NOT_MEMBRO";
    private static final String NOT_CONTACT = "NOT_CONTACT";
    private static final String JA_PERTENCE = "JA_PERTENCE";
    private static final String EMPTY = "EMPTY";

    private Connection dbConn;

    public ComunicacaoBD() throws SQLException {
        dbConn = DriverManager.getConnection(BD_URL, USERNAME, PASSWORD);
    }

    public void close() throws SQLException {
        if(dbConn != null)
            dbConn.close();
    }

    //Funções User
    public String pesquisaeListaUsers(String username) throws SQLException {
        Statement statement = dbConn.createStatement();
        StringBuilder resultado = new StringBuilder();
        String sqlQuery;

        if(!username.isEmpty()) {
            sqlQuery = "SELECT username, login FROM User WHERE username='" + username + "";
        }
        else
            sqlQuery = "SELECT username, login FROM User";

            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while(resultSet.next()) {
                if(Integer.parseInt(resultSet.getString("login")) == 1)
                    resultado.append(resultSet.getString("username")).append("[Online]\n");
                else
                    resultado.append(resultSet.getString("username")).append("[Offline]\n");
            }
            resultSet.close();
            statement.close();
            return resultado.toString();
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

    public boolean isuserOnline(String username) throws SQLException {
        if(!verificaExistenciaUser(username))
            return false;
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT login FROM User WHERE username='" + username + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        if(!resultSet.next()) {
            if(resultSet.getInt("login") == 1)
                return true;
            else
                return false;
        }
        return false;
    }

    //Funções Grupo

    public String createGroup(String name, String username) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlQuery;
        if(verificaExistenciaUser(username)) {
            if (!verificaSeExisteGrupoComNomeEAdmin(name,username)){
                sqlQuery = "INSERT INTO `Group` (idGroup,name,admin) VALUES ('" + getNextIdGroup() + "','" + name + "','" + username + "')";
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

    public String joinGroup(int idGrupo, String username) throws SQLException{
        Statement statement = dbConn.createStatement();
        if(!verificaExistenciaGrupo(idGrupo)){
            return GRUPO_INEXISTENTE;
        }
        if (verificaMembroGrupo(idGrupo, username)){
            return JA_PERTENCE;
        }

        String sqlQuery = "INSERT INTO `Joins` (user,`group`,accepted) VALUES ('" + username + "','" + idGrupo + "','" + 0 + "')";
        statement.executeUpdate(sqlQuery);
        statement.close();
        return SUCESSO;
    }

    public String leaveGroup(int idGrupo, String username) throws SQLException{
        Statement statement = dbConn.createStatement();
        if(!verificaExistenciaGrupo(idGrupo)){
            return GRUPO_INEXISTENTE;
        }
        if(!verificaMembroGrupo(idGrupo,username)){
            return NOT_MEMBRO;
        }
        String sqlQuery = "DELETE FROM `Joins` WHERE user='" + username + "'AND `group`='" + idGrupo +"'";
        statement.executeUpdate(sqlQuery);
        statement.close();
        return SUCESSO;
    }

    public boolean verificaMembroGrupo(int idGrupo, String username) throws SQLException{
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT user FROM `Joins` WHERE user='" + username + "'AND `group`='" + idGrupo +"'";
        System.out.println(sqlQuery);
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        if(!resultSet.next()) {
            resultSet.close();
            statement.close();
            return false;
        }
        return true;
    }

    public boolean verificaSeExisteGrupoComNomeEAdmin(String name, String admin) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT name FROM `Group` WHERE admin='" + admin + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

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
            String sqlQuery = "SELECT name FROM `Group` WHERE idGroup='" + idAtribuir + "'";
            resultSet = statement.executeQuery(sqlQuery);
        }while (resultSet.next());

        resultSet.close();
        statement.close();
        return idAtribuir;
    }


    public String updateGroupName(String name, int idGroup) throws SQLException{
        Statement statement = dbConn.createStatement();
        String admin = "";
        admin = getAdminFromIDGoup(idGroup);
        if(verificaExistenciaGrupo(idGroup)){
                if(!verificaSeExisteGrupoComNomeEAdmin(name,admin)){
                    String sqlQuery = "UPDATE `Group` SET name='" + name + "'WHERE idGroup='" + idGroup + "'";
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
            return GRUPO_INEXISTENTE;
        }
    }

    public String getAdminFromIDGoup(int idGrupo) throws SQLException{
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT admin FROM `Group` WHERE idGroup='" + idGrupo + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        if(resultSet.next()){
            String admin = resultSet.getString("admin");
            resultSet.close();
            statement.close();
            return admin;
        }
        resultSet.close();
        statement.close();
        return GRUPO_INEXISTENTE;

    }

    public boolean verificaAdminGrupo(int idGroup, String admin) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT admin FROM `Group` WHERE idGroup='" + idGroup + "'";
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
        String sqlQuery = "SELECT name FROM `Group` WHERE idGroup='" + idGroup + "'";
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

    public String listaGrupos() throws SQLException {
        String resultado = "";
        Statement statement = dbConn.createStatement();
        String sqlQuery;
        sqlQuery = "SELECT name, idGroup FROM `Group`";
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        while(resultSet.next()) {
            resultado = resultado + resultSet.getString("idGroup") + " - " + resultSet.getString("name") + "\n";
        }
        statement.close();
        return resultado;
    }

    public String listaGruposAdmin(String username) throws SQLException {
        String resultado = "";
        int count = 0;
        Statement statement = dbConn.createStatement();
        String sqlQuery;
        sqlQuery = "SELECT name, idGroup FROM `Group` WHERE admin='" +  username + "'" ;
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        while(resultSet.next()) {
            count++;
            resultado = resultado + resultSet.getString("idGroup") + "," + resultSet.getString("name") + ",";
        }
        resultado = count + "," + resultado;
        resultSet.close();
        statement.close();
        return resultado;
    }

    public String listaMembrosGrupos(int idGrupo) throws SQLException {
        String resultado = "";
        Statement statement = dbConn.createStatement();
        String sqlQuery;
        boolean vazio = true;
        sqlQuery = "SELECT user FROM `Joins` WHERE accepted='1' AND `group`='" + idGrupo + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        while (resultSet.next()) {
            vazio=false;
            resultado = resultado + resultSet.getString("user") + "\n";
        }
        resultSet.close();
        statement.close();
        if(vazio){
            System.out.println("Não existem membros neste grupo!");
            return EMPTY;
        }
        return resultado;
    }

    public String listaMembrosGrupoPorAceitar(int idGrupo) throws SQLException {
        String resultado = "";
        Statement statement = dbConn.createStatement();
        String sqlQuery;
        boolean vazio = true;
        sqlQuery = "SELECT user FROM `Joins` WHERE accepted='0' AND `group`='" + idGrupo + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next()) {
            vazio = false;
            resultado = resultado + resultSet.getString("user") + "\n";
        }
        resultSet.close();
        statement.close();
        if(vazio){
            System.out.println("Não existem membros por aceitar!");
            return EMPTY;
        }
        return resultado;
    }

    public String aceitaMembro(String username,int idGrupo) throws SQLException{
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT user FROM `Joins` WHERE `group`='" + idGrupo + "' AND user='" + username + "'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);
        if(!resultSet.next()) {
            System.out.println("O utilizador não existe!");
            resultSet.close();
            statement.close();
            return UTILIZADOR_INEXISTENTE;
        }
        sqlQuery = "UPDATE `Joins` SET accepted=1 WHERE `group`='" + idGrupo + "'AND user='" + username + "'";
        statement.executeUpdate(sqlQuery);
        resultSet.close();
        statement.close();
        return SUCESSO;
    }

    public String deleteGroup(int idGrupo) throws SQLException{
        Statement statement = dbConn.createStatement();
        String sqlQuery = "DELETE FROM `Joins` WHERE `group`='" + idGrupo + "'";
        statement.executeUpdate(sqlQuery);
        sqlQuery = "DELETE FROM `Group` WHERE idGroup='" + idGrupo + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();
        return SUCESSO;
    }

    //Funcoes Contactos

    public String adicionaContacto(String username, String friend) throws SQLException {
        if(!verificaExistenciaUser(friend))
            return UTILIZADOR_INEXISTENTE;
        if(verificaSeContacto(friend, username)){
            return USERNAME_REPETIDO;
        }
        Statement statement = dbConn.createStatement();
        String sqlQuery = "INSERT INTO `Has_Contact` (user,friend,accepted) VALUES ('" + username + "','" + friend + "','" + 0 + "')";
        statement.executeUpdate(sqlQuery);
        statement.close();
        return SUCESSO;
    }

    public boolean verificaSeContacto(String username_amigo, String username_eu) throws SQLException{
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT accepted FROM `Has_Contact` WHERE (user='" + username_eu + "' AND friend ='" + username_amigo +  "') OR (friend='" + username_eu + "' AND user='" + username_amigo +  "')";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        if(resultSet.next()){
            resultSet.close();
            statement.close();
            return true;
        }
        else{
            resultSet.close();
            statement.close();
            return false;
        }
    }

    public String aceitaContacto(String username_mandou, String myUsername) throws SQLException {
        if(!verificaExistenciaUser(username_mandou))
            return UTILIZADOR_INEXISTENTE;
        if (verificaSeContacto(myUsername, username_mandou)){
            return USERNAME_REPETIDO;
        }
        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE `Has_Contact` SET accepted=1 WHERE `user`='" + username_mandou + "'AND friend='" + myUsername + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();
        return SUCESSO;
    }

    public String eliminaContacto(String username, String friend) throws SQLException {
        if(!verificaExistenciaUser(friend))
            return UTILIZADOR_INEXISTENTE;

        if(!verificaSeContacto(friend,username))
            return NOT_CONTACT;
        Statement statement = dbConn.createStatement();

        String sqlQuery = "DELETE FROM `Has_Contact` WHERE (user='" + username + "' AND friend='" + friend + "') OR (user='" + friend + "' AND friend='" + username + "')";
        statement.executeUpdate(sqlQuery);
        statement.close();
        return SUCESSO;
        //Falta eliminar o historico de msgs e de ficheiros
    }

    public String listarContactos(String username) throws SQLException {
        Statement statement = dbConn.createStatement();
        StringBuilder resultado = new StringBuilder();

        String sqlQuery = "SELECT friend FROM `Has_Contact` WHERE accepted=1" + " AND user='" + username + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        while(resultSet.next()) {
            if(isuserOnline(resultSet.getString("friend")))
                resultado.append(resultSet.getString("friend")).append("[Online]\n");
            else
                resultado.append(resultSet.getString("friend")).append("[Offline]\n");
        }

        sqlQuery = "SELECT user FROM `Has_Contact` WHERE accepted=1" + " AND friend='" + username + "'";
        resultSet = statement.executeQuery(sqlQuery);
        while(resultSet.next()) {
            if(isuserOnline(resultSet.getString("user")))
                resultado.append(resultSet.getString("user")).append("[Online]");
            else
                resultado.append(resultSet.getString("user")).append("[Offline]");
        }

        resultSet.close();
        statement.close();
        return resultado.toString();
    }

    public String listarContactosPorAceitar(String username) throws SQLException {
        Statement statement = dbConn.createStatement();
        StringBuilder resultado = new StringBuilder();

        String sqlQuery = "SELECT user FROM `Has_Contact` WHERE accepted=0" + " AND friend='" + username + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next()) {
            System.out.println(resultSet.getString("user"));
            resultado.append(resultSet.getString("user"));
        }

        resultSet.close();
        statement.close();
        return resultado.toString();
    }

 }
