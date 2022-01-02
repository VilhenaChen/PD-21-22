package pd.tp.servidor.bd;

import pd.tp.comum.Ficheiro;
import pd.tp.comum.Mensagem;
import pd.tp.comum.NovidadeGRDS;
import pd.tp.comum.Utils;

import java.sql.*;
import java.util.ArrayList;

public class ComunicacaoBD implements Utils {

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

    public boolean verificaNomeUserExistente(String name) throws SQLException{
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT username FROM `User` WHERE name='" + name + "'";
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

    public String insereUser(String name, String username, String password, int logado) throws SQLException {
        boolean worked;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                if(verificaExistenciaUser(username)){
                    return USERNAME_REPETIDO;
                }
                if(verificaNomeUserExistente(name)){
                    return NOME_REPETIDO;
                }
                String sqlQuery = "INSERT INTO User (username, name, password, login, lastInteraction) VALUES ('" + username + "','" + name + "','" + password + "','" + logado + "', NOW() )";
                statement.executeUpdate(sqlQuery);
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

        return SUCESSO;
    }

    public String updateUserName(String name, String username) throws SQLException {
        boolean worked;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                if(!verificaExistenciaUser(username)){
                    return UTILIZADOR_INEXISTENTE;
                }
                if(verificaNomeUserExistente(name)){
                    return NOME_REPETIDO;
                }
                String sqlQuery = "UPDATE User SET name='" + name + "'WHERE username='" + username + "'";
                statement.executeUpdate(sqlQuery);
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

        return SUCESSO;
    }

    public String updateUserUsername(String new_username, String old_username) throws SQLException {
        boolean worked;
        do {
            worked = true;
            try {
                Statement statement = dbConn.createStatement();
                dbConn.setAutoCommit(false);
                if (!verificaExistenciaUser(old_username)) {
                    return UTILIZADOR_INEXISTENTE;
                }

                if (verificaExistenciaUser(new_username)) {
                    return USERNAME_REPETIDO;
                }

                String sqlQuery = "SELECT name, password FROM `User` WHERE username='" + old_username + "'";
                ResultSet resultSet = statement.executeQuery(sqlQuery);
                String nome = "";
                String nomeSuplente = "suplente";
                String password = "";
                while (resultSet.next()) {
                    nome = resultSet.getString("name");
                    password = resultSet.getString("password");
                }
                resultSet.close();

                sqlQuery = "INSERT INTO `User` (username, name, password, login) VALUES ('" + new_username + "','" + nomeSuplente + "','" + password + "','" + 1 + "')";
                statement.executeUpdate(sqlQuery);

                sqlQuery = "UPDATE `File` SET sender='" + new_username + "'WHERE sender='" + old_username + "'";
                statement.executeUpdate(sqlQuery);
                sqlQuery = "UPDATE `File` SET user_receiver='" + new_username + "'WHERE user_receiver='" + old_username + "'";
                statement.executeUpdate(sqlQuery);

                sqlQuery = "UPDATE `Msg` SET sender='" + new_username + "'WHERE sender='" + old_username + "'";
                statement.executeUpdate(sqlQuery);
                sqlQuery = "UPDATE `Msg` SET user_receiver='" + new_username + "'WHERE user_receiver='" + old_username + "'";
                statement.executeUpdate(sqlQuery);

                sqlQuery = "UPDATE `Group` SET admin='" + new_username + "'WHERE admin='" + old_username + "'";
                statement.executeUpdate(sqlQuery);
                sqlQuery = "UPDATE `Joins` SET user='" + new_username + "'WHERE user='" + old_username + "'";
                statement.executeUpdate(sqlQuery);

                sqlQuery = "UPDATE `Has_Contact` SET user='" + new_username + "'WHERE user='" + old_username + "'";
                statement.executeUpdate(sqlQuery);
                sqlQuery = "UPDATE `Has_Contact` SET friend='" + new_username + "'WHERE friend='" + old_username + "'";
                statement.executeUpdate(sqlQuery);

                sqlQuery = "DELETE FROM `User` WHERE username='" + old_username + "'";
                statement.executeUpdate(sqlQuery);
                sqlQuery = "UPDATE `User` SET name='" + nome + "'WHERE username='" + new_username + "'";
                statement.executeUpdate(sqlQuery);
                statement.close();
                dbConn.commit();
            }catch (Exception e) {
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);
        return SUCESSO;
    }

    public String updateUserPassword(String password, String username) throws SQLException {
        boolean worked;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                if(!verificaExistenciaUser(username)){
                    return UTILIZADOR_INEXISTENTE;
                }
                String sqlQuery = "UPDATE User SET password='" + password  + "'WHERE username='" + username + "'";
                statement.executeUpdate(sqlQuery);
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

        return SUCESSO;
    }

    public String loginUser(String username, String password) throws SQLException {
        boolean worked=true;
        String DBPassword;
        String DBName = "";
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                String sqlQuery = "SELECT password, login FROM User WHERE username='" + username + "'";


                ResultSet resultSet = statement.executeQuery(sqlQuery);

                if(!resultSet.next()) {
                    System.out.println("O utilizador não existe!");
                    resultSet.close();
                    statement.close();
                    return UTILIZADOR_INEXISTENTE;
                }

                if(resultSet.getInt("login") == 1){
                    resultSet.close();
                    statement.close();
                    return UTILIZADOR_JA_LOGADO;
                }

                DBPassword = resultSet.getString("password");

                if(DBPassword.equals(password)) {
                    int login = 1;
                    sqlQuery = "UPDATE `User` SET login='" + login + "' AND lastInteraction=NOW() WHERE username='" + username + "'";
                    statement.executeUpdate(sqlQuery);
                    sqlQuery = "SELECT name FROM User WHERE username='" + username + "'";


                    resultSet = statement.executeQuery(sqlQuery);

                    resultSet.next();
                    DBName = resultSet.getString("name");

                    resultSet.close();
                    statement.close();

                }
                else{
                    System.out.println("Password errada!");
                    resultSet.close();
                    statement.close();
                    return PASSWORD_ERRADA;
                }
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);
        return SUCESSO + "," + DBName;
    }

    public String logoutUser(String username) throws SQLException {
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
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
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);
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
        if(resultSet.next()) {
            if(resultSet.getInt("login") == 1)
                return true;
            else
                return false;
        }
        return false;
    }

    //Funções Grupo

    public String createGroup(String name, String username, NovidadeGRDS novidadeGRDS) throws SQLException {
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                String sqlQuery;
                if(verificaExistenciaUser(username)) {
                    if (!verificaSeExisteGrupoComNomeEAdmin(name,username)){
                        int idGrupo = getNextIdGroup();
                        sqlQuery = "INSERT INTO `Group` (idGroup,name,admin) VALUES ('" + idGrupo + "','" + name + "','" + username + "')";
                        statement.executeUpdate(sqlQuery);
                        statement.close();
                        novidadeGRDS.setIdGrupo(idGrupo);
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
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);
        return SUCESSO;
    }

    public String joinGroup(int idGrupo, String username) throws SQLException{
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
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
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

        return SUCESSO;
    }

    public String leaveGroup(int idGrupo, String username) throws SQLException{
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                if(!verificaExistenciaGrupo(idGrupo)){
                    return GRUPO_INEXISTENTE;
                }
                if(!verificaMembroGrupo(idGrupo,username)){
                    return NOT_MEMBRO;
                }
                String sqlQuery = "DELETE FROM `Msg` WHERE group_receiver='" + idGrupo + "' AND sender='" + username + "'";
                statement.executeUpdate(sqlQuery);
                sqlQuery = "DELETE FROM `Joins` WHERE user='" + username + "'AND `group`='" + idGrupo +"'";
                statement.executeUpdate(sqlQuery);
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

        return SUCESSO;
    }

    public boolean verificaMembroGrupo(int idGrupo, String username) throws SQLException{
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT user FROM `Joins` WHERE (user='" + username + "'AND `group`='" + idGrupo +"') AND accepted='1'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        if(!resultSet.next()) {
            resultSet.close();
            statement.close();
            return false;
        }
        return true;
    }

    public boolean verificaSePedidoAdesao(int idGrupo, String username) throws SQLException{
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT user FROM `Joins` WHERE (user='" + username + "'AND `group`='" + idGrupo +"') AND accepted='0'";
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
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                String admin = "";
                admin = getAdminFromIDGoup(idGroup);
                if(verificaExistenciaGrupo(idGroup)){
                    if(!verificaSeExisteGrupoComNomeEAdmin(name,admin)){
                        String sqlQuery = "UPDATE `Group` SET name='" + name + "'WHERE idGroup='" + idGroup + "'";
                        statement.executeUpdate(sqlQuery);
                        statement.close();
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
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);
        return SUCESSO;
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
        StringBuilder resultado = new StringBuilder();
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                String sqlQuery;
                boolean vazio = true;
                sqlQuery = "SELECT admin FROM `Group` WHERE `idGroup`='" + idGrupo + "'";
                ResultSet resultSet = statement.executeQuery(sqlQuery);
                if(resultSet.next()) {
                    resultado.append(resultSet.getString("admin")).append(" -> Admin\n");
                    vazio = false;
                }
                else {
                    return EMPTY;
                }
                sqlQuery = "SELECT user FROM `Joins` WHERE accepted='1' AND `group`='" + idGrupo + "'";
                resultSet = statement.executeQuery(sqlQuery);
                while (resultSet.next()) {
                    vazio=false;
                    resultado.append(resultSet.getString("user")).append("\n");
                }
                resultSet.close();
                statement.close();
                if(vazio){
                    return EMPTY;
                }
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

        return resultado.toString();
    }

    public String listaMembrosGrupoPorAceitar(int idGrupo) throws SQLException {
        String resultado = "";
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
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
                    return EMPTY;
                }
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);
        return resultado;
    }

    public String aceitaMembro(String username,int idGrupo) throws SQLException{
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();

                if(!verificaExistenciaUser(username)){
                    return UTILIZADOR_INEXISTENTE;
                }
                if(verificaMembroGrupo(idGrupo,username)){
                    return USERNAME_REPETIDO;
                }
                if (!verificaSePedidoAdesao(idGrupo,username)){
                    return NOT_MEMBRO;
                }
                String sqlQuery = "UPDATE `Joins` SET accepted=1 WHERE `group`='" + idGrupo + "'AND user='" + username + "'";
                statement.executeUpdate(sqlQuery);
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

        return SUCESSO;
    }

    public String rejeitaMembro(String username,int idGrupo) throws SQLException{
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                if(!verificaExistenciaUser(username)){
                    return UTILIZADOR_INEXISTENTE;
                }
                if(verificaMembroGrupo(idGrupo,username)){
                    return USERNAME_REPETIDO;
                }
                if (!verificaSePedidoAdesao(idGrupo,username)){
                    return NOT_MEMBRO;
                }
                String sqlQuery = "DELETE FROM `Joins` WHERE `group`='" + idGrupo + "'AND user='" + username + "'";
                statement.executeUpdate(sqlQuery);
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

        return SUCESSO;
    }

    public String deleteGroup(int idGrupo) throws SQLException{
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                String sqlQuery = "DELETE FROM `Joins` WHERE `group`='" + idGrupo + "'";
                statement.executeUpdate(sqlQuery);
                sqlQuery = "DELETE FROM `Msg` WHERE group_receiver='" + idGrupo + "'";
                statement.executeUpdate(sqlQuery);
                sqlQuery = "DELETE FROM `Group` WHERE idGroup='" + idGrupo + "'";
                statement.executeUpdate(sqlQuery);
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

        return SUCESSO;
        //Falta apagar o historico de ficheiros
    }

    //Funcoes Contactos

    public String adicionaContacto(String username, String friend) throws SQLException {
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                if(!verificaExistenciaUser(friend))
                    return UTILIZADOR_INEXISTENTE;
                if(verificaSeContacto(friend, username) || verificaSePedidoContacto(friend,username)){
                    return USERNAME_REPETIDO;
                }
                Statement statement = dbConn.createStatement();
                String sqlQuery = "INSERT INTO `Has_Contact` (user,friend,accepted) VALUES ('" + username + "','" + friend + "','" + 0 + "')";
                statement.executeUpdate(sqlQuery);
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

        return SUCESSO;
    }

    public boolean verificaSeContacto(String username_amigo, String username_eu) throws SQLException{
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT accepted FROM `Has_Contact` WHERE ((user='" + username_eu + "' AND friend ='" + username_amigo +  "') OR (friend='" + username_eu + "' AND user='" + username_amigo +  "')) AND accepted = '1'";
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
    public boolean verificaSePedidoContacto(String username_amigo, String username_eu) throws SQLException{
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT accepted FROM `Has_Contact` WHERE ((user='" + username_eu + "' AND friend ='" + username_amigo +  "') OR (friend='" + username_eu + "' AND user='" + username_amigo +  "')) AND accepted = '0'";
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
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                if(!verificaExistenciaUser(username_mandou))
                    return UTILIZADOR_INEXISTENTE;
                if (verificaSeContacto(myUsername, username_mandou)){
                    return USERNAME_REPETIDO;
                }
                if(!verificaSePedidoContacto(myUsername,username_mandou)){
                    return NOT_CONTACT;
                }
                Statement statement = dbConn.createStatement();

                String sqlQuery = "UPDATE `Has_Contact` SET accepted=1 WHERE `user`='" + username_mandou + "'AND friend='" + myUsername + "'";
                statement.executeUpdate(sqlQuery);
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

        return SUCESSO;
    }

    public String rejeitaContacto(String username_mandou, String myUsername) throws SQLException {
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                if(!verificaExistenciaUser(username_mandou))
                    return UTILIZADOR_INEXISTENTE;
                if (verificaSeContacto(myUsername, username_mandou)){
                    return USERNAME_REPETIDO;
                }
                if(!verificaSePedidoContacto(myUsername,username_mandou)){
                    return NOT_CONTACT;
                }
                Statement statement = dbConn.createStatement();
                String sqlQuery = "DELETE FROM `Has_Contact` WHERE user='" + username_mandou + "' AND friend='" + myUsername + "'";
                statement.executeUpdate(sqlQuery);
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

        return SUCESSO;
    }

    public String eliminaContacto(String username, String friend) throws SQLException {
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                if(!verificaExistenciaUser(friend))
                    return UTILIZADOR_INEXISTENTE;

                if(!verificaSeContacto(friend,username))
                    return NOT_CONTACT;
                Statement statement = dbConn.createStatement();
                String sqlQuery = "DELETE FROM `Msg` WHERE (sender='" + friend + "'AND user_receiver='" + username + "') OR (sender='" + username + "' AND user_receiver='" + friend + "')" ;
                statement.executeUpdate(sqlQuery);
                sqlQuery = "DELETE FROM `Has_Contact` WHERE (user='" + username + "' AND friend='" + friend + "') OR (user='" + friend + "' AND friend='" + username + "')";
                statement.executeUpdate(sqlQuery);
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

        return SUCESSO;
        //Falta eliminar o historico ficheiros
    }

    public String listarContactos(String username) throws SQLException {
        boolean worked=true;
        StringBuilder resultado = new StringBuilder();
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();


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
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

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

    //MENSAGENS

    public int getNextIdMsg() throws SQLException {
        int idAtribuir = -1;
        Statement statement = dbConn.createStatement();
        ResultSet resultSet;
        do{
            idAtribuir++;
            String sqlQuery = "SELECT subject FROM `Msg` WHERE idMsg='" + idAtribuir + "'";
            resultSet = statement.executeQuery(sqlQuery);
        }while (resultSet.next());

        resultSet.close();
        statement.close();
        return idAtribuir;
    }

    public String recebeMsg(Mensagem msg, NovidadeGRDS novidadeGRDS) throws SQLException {
        int idGrupo;
        String username;
        String sqlQuery;
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                int idMsg = getNextIdMsg();
                try{
                    idGrupo = Integer.parseInt(msg.getReceiver());
                    if(!verificaExistenciaGrupo(idGrupo))
                        return GRUPO_INEXISTENTE;
                    if(!verificaMembroGrupo(idGrupo,msg.getSender())){
                        return NOT_MEMBRO;
                    }
                    sqlQuery = "INSERT INTO `Msg` (idMsg,subject,body,date,viewed,sender,group_receiver) VALUES ('" +  idMsg +  "','" + msg.getAssunto() + "','" + msg.getCorpo() + "','" + Timestamp.valueOf(msg.getDate()) + "','" + 0 + "','" + msg.getSender() + "','" + idGrupo + "')";
                    statement.executeUpdate(sqlQuery);
                }catch (NumberFormatException e){
                    username = msg.getReceiver();
                    if(!verificaExistenciaUser(username))
                        return UTILIZADOR_INEXISTENTE;
                    sqlQuery = "INSERT INTO `Msg` (idMsg,subject,body,date,viewed,sender,user_receiver) VALUES ('" +  idMsg +  "','" + msg.getAssunto() + "','" + msg.getCorpo() + "','" + Timestamp.valueOf(msg.getDate()) + "','" + 0 + "','" + msg.getSender() + "','" + username + "')";
                    statement.executeUpdate(sqlQuery);
                }
                statement.close();
                novidadeGRDS.setIdMsg(idMsg);
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);
        return SUCESSO;

    }

    public ArrayList<Integer> arrayDeGruposUser(String username) throws SQLException{
        Statement statement = dbConn.createStatement();
        ArrayList<Integer> arrayGrupos = new ArrayList<>();
        String sqlQuery = "SELECT `group` FROM `Joins`  WHERE user='" + username + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        while (resultSet.next()){
            arrayGrupos.add(Integer.parseInt(resultSet.getString("idGroup")));
        }
        return arrayGrupos;

    }

    public String listaMensagens(String username) throws SQLException {

        StringBuilder resultado = new StringBuilder();
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                ArrayList<Integer> grupos = arrayDeGruposUser(username);
                Statement statement = dbConn.createStatement();
                String sqlQuery = "SELECT idMsg,subject,`date`,viewed,sender,group_receiver,user_receiver FROM `Msg`  WHERE sender='" + username + "'OR user_receiver='" + username + "'";
                ResultSet resultSet = statement.executeQuery(sqlQuery);

                while(resultSet.next()) {
                    if(Integer.parseInt(resultSet.getString("viewed")) == 1) {
                        if(resultSet.getString("user_receiver") != null)
                            resultado.append(resultSet.getString("idMsg")).append(" - ").append(resultSet.getString("subject")).append(" (Lida)").append("\n\tenvidada as: ").append(resultSet.getString("date")).append(" por ").append(resultSet.getString("sender")).append(" para ").append(resultSet.getString("user_receiver")).append("\n");
                        else
                            resultado.append(resultSet.getString("idMsg")).append(" - ").append(resultSet.getString("subject")).append(" (Lida)").append("\n\tenvidada as: ").append(resultSet.getString("date")).append(" por ").append(resultSet.getString("sender")).append(" para ").append(resultSet.getString("group_receiver")).append("\n");
                    }
                    else {
                        if(resultSet.getString("user_receiver") != null)
                            resultado.append(resultSet.getString("idMsg")).append(" - ").append(resultSet.getString("subject")).append(" (Nao Lida)").append("\n\tenvidada as: ").append(resultSet.getString("date")).append(" por ").append(resultSet.getString("sender")).append(" para ").append(resultSet.getString("user_receiver")).append("\n");
                        else
                            resultado.append(resultSet.getString("idMsg")).append(" - ").append(resultSet.getString("subject")).append(" (Nao Lida)").append("\n\tenvidada as: ").append(resultSet.getString("date")).append(" por ").append(resultSet.getString("sender")).append(" para ").append(resultSet.getString("group_receiver")).append("\n");
                    }
                }

                for(Integer i : grupos) {
                    sqlQuery = "SELECT idMsg,subject,`date`,viewed,sender,group_receiver FROM `Msg`  WHERE group_receiver='" + i + "'AND sender!='" + username + "'";
                    resultSet = statement.executeQuery(sqlQuery);
                    while (resultSet.next()) {
                        if (Integer.parseInt(resultSet.getString("viewed")) == 1) {
                            resultado.append(resultSet.getString("idMsg")).append(" - ").append(resultSet.getString("subject")).append(" Lida").append("\n\tenvidada as: ").append(resultSet.getString("date")).append(" por ").append(resultSet.getString("sender")).append(" para ").append(resultSet.getString("group_receiver")).append("\n");
                        } else {
                            resultado.append(resultSet.getString("idMsg")).append(" - ").append(resultSet.getString("subject")).append(" Nao Lida").append("\n\tenvidada as: ").append(resultSet.getString("date")).append(" por ").append(resultSet.getString("sender")).append(" para ").append(resultSet.getString("group_receiver")).append("\n");
                        }
                    }
                }

                resultSet.close();
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

        return resultado.toString();
    }

    public String listaMensagensParaEliminar(String username) throws SQLException {
        Statement statement = dbConn.createStatement();
        StringBuilder resultado = new StringBuilder();

        String sqlQuery = "SELECT idMsg,subject,`date`,viewed,sender,user_receiver FROM `Msg`  WHERE sender='" + username + "'OR user_receiver='" + username + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next()) {
            if(Integer.parseInt(resultSet.getString("viewed")) == 1) {
                resultado.append(resultSet.getString("idMsg") + " - " + resultSet.getString("subject") + " Lida"
                        + "\n\tenvidada as: " + resultSet.getString("date") + " por " + resultSet.getString("sender") + " para "
                        + resultSet.getString("user_receiver") + "\n");
            }
            else {
                resultado.append(resultSet.getString("idMsg") + " - " + resultSet.getString("subject") + " Nao Lida"
                        + "\n\tenvidada as: " + resultSet.getString("date") + " por " + resultSet.getString("sender") + " para "
                        + resultSet.getString("user_receiver") + "\n");
            }
        }
        resultSet.close();
        statement.close();
        return resultado.toString();
    }

    public boolean verificaExistenciaMensagem(int idMsg) throws SQLException{
        Statement statement = dbConn.createStatement();
        ResultSet resultSet;

        String sqlQuery = "SELECT subject FROM `Msg` WHERE idMsg='" + idMsg + "'";
        resultSet = statement.executeQuery(sqlQuery);
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

    public boolean verificaSenderOrReceiver(int idMsg, String username) throws SQLException{
        Statement statement = dbConn.createStatement();
        ResultSet resultSet;

        String sqlQuery = "SELECT sender, user_receiver FROM `Msg` WHERE idMsg='" + idMsg + "'";
        resultSet = statement.executeQuery(sqlQuery);
        while(resultSet.next()){
            if(resultSet.getString("sender").equals(username) || resultSet.getString("user_receiver").equals(username))
                return true;
            else
                return false;
        }
        return false;
    }

    public String eliminaMsg(int idMsg, String username) throws SQLException{
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                if(!verificaExistenciaMensagem(idMsg)){
                    statement.close();
                    return MSG_INEXISTENTE;
                }
                if(!verificaSenderOrReceiver(idMsg,username)){
                    statement.close();
                    return NOT_YOUR_MSG;
                }
                String sqlQuery = "DELETE FROM `Msg` WHERE `idMsg`='" + idMsg + "'";
                statement.executeUpdate(sqlQuery);
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

        return SUCESSO;
    }

    public String getCorpoMsg(int idMsg, String username) throws SQLException{
        boolean worked=true;
        StringBuilder resultado = new StringBuilder();
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                ResultSet resultSet;

                if(!verificaExistenciaMensagem(idMsg)){
                    statement.close();
                    return resultado.toString();
                }
                String sqlQuery = "SELECT idMsg,subject,body,`date`,viewed,sender,user_receiver, group_receiver FROM `Msg` WHERE `idMsg`='" + idMsg + "'";
                resultSet = statement.executeQuery(sqlQuery);

                while(resultSet.next()) {
                    if(!verificaSenderOrReceiver(idMsg,username) && !verificaMembroGrupo(Integer.parseInt(resultSet.getString("group_receiver")),username)){
                        resultSet.close();
                        statement.close();
                        return resultado.toString();
                    }
                    if(resultSet.getString("user_receiver") != null) {
                        resultado.append("De ").append(resultSet.getString("sender")).append(" para ").append(resultSet.getString("user_receiver")).append(" envidada as: ").append(resultSet.getString("date")).append(" \n").append(resultSet.getString("subject")).append("\n\t").append(resultSet.getString("body"));
                    }
                    else {
                        resultado.append("De ").append(resultSet.getString("sender")).append(" para ").append(resultSet.getString("group_receiver")).append(" envidada as: ").append(resultSet.getString("date")).append(" \n").append(resultSet.getString("subject")).append("\n\t").append(resultSet.getString("body"));
                    }
                }

                sqlQuery = "UPDATE `Msg` SET viewed='1' WHERE idMsg='" + idMsg + "'";
                statement.executeUpdate(sqlQuery);

                resultSet.close();
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

        return resultado.toString();
    }


    //Novidades GRDS

    public void verificaAfetadosUpdateUsername(String old_username, NovidadeGRDS novidadeGRDS) throws SQLException {

        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                ArrayList<Integer> grupos = new ArrayList<>();
                Statement statement = dbConn.createStatement();
                String sqlQuery = "SELECT user FROM `Has_Contact` WHERE friend='" + old_username + "'";
                ResultSet resultSet = statement.executeQuery(sqlQuery);

                while (resultSet.next()){
                    novidadeGRDS.addUserAfetado(resultSet.getString("user"));
                }

                sqlQuery = "SELECT friend FROM `Has_Contact` WHERE user='" + old_username + "'";
                resultSet = statement.executeQuery(sqlQuery);

                while (resultSet.next()){
                    novidadeGRDS.addUserAfetado(resultSet.getString("friend"));
                }

                sqlQuery = "SELECT `group` FROM `Joins` WHERE user='" + old_username + "'";
                resultSet = statement.executeQuery(sqlQuery);

                while (resultSet.next()){
                    grupos.add(resultSet.getInt("group"));
                }

                sqlQuery = "SELECT idGroup FROM `Group` WHERE admin='" + old_username + "'";
                resultSet = statement.executeQuery(sqlQuery);

                while (resultSet.next()){
                    grupos.add(resultSet.getInt("idGroup"));
                }

                for (Integer i : grupos){
                    sqlQuery = "SELECT user FROM `Joins` WHERE `group`='" + i + "' AND user!='" + old_username + "'";
                    resultSet = statement.executeQuery(sqlQuery);

                    while (resultSet.next()){
                        novidadeGRDS.addUserAfetado(resultSet.getString("user"));
                    }

                    sqlQuery = "SELECT admin FROM `Group` WHERE `idGroup`='" + i + "' AND admin!='" + old_username + "'";
                    resultSet = statement.executeQuery(sqlQuery);

                    while (resultSet.next()){
                        novidadeGRDS.addUserAfetado(resultSet.getString("admin"));
                    }
                }
                resultSet.close();
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

    }

    public void verificaAfetadosUpdateGroupName(int idGrupo, NovidadeGRDS novidadeGRDS) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT user FROM `Joins` WHERE `group`='" + idGrupo + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next()){
            novidadeGRDS.addUserAfetado(resultSet.getString("user"));
        }
        resultSet.close();
        statement.close();
    }

    public void verificaAfetadosAdereGrupo(int idGrupo, String username, NovidadeGRDS novidadeGRDS) throws SQLException {
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                String sqlQuery = "SELECT user FROM `Joins` WHERE `group`='" + idGrupo + "' AND user!='" + username + "'";
                ResultSet resultSet = statement.executeQuery(sqlQuery);

                while (resultSet.next()){
                    novidadeGRDS.addUserAfetado(resultSet.getString("user"));
                }

                sqlQuery = "SELECT admin FROM `Group` WHERE `idGroup`='" + idGrupo + "'";
                resultSet = statement.executeQuery(sqlQuery);

                while (resultSet.next()){
                    novidadeGRDS.addUserAfetado(resultSet.getString("admin"));
                }
                resultSet.close();
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

    }

    public void verificaAfetadosLeaveGroup(int idGrupo, String username, NovidadeGRDS novidadeGRDS) throws SQLException {
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                String sqlQuery = "SELECT user FROM `Joins` WHERE `group`='" + idGrupo + "' AND user!='" + username + "'";
                ResultSet resultSet = statement.executeQuery(sqlQuery);

                while (resultSet.next()){
                    novidadeGRDS.addUserAfetado(resultSet.getString("user"));
                }

                sqlQuery = "SELECT admin FROM `Group` WHERE `idGroup`='" + idGrupo + "'";
                resultSet = statement.executeQuery(sqlQuery);

                while (resultSet.next()){
                    novidadeGRDS.addUserAfetado(resultSet.getString("admin"));
                }
                resultSet.close();
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

    }
    public void verificaAfetadosMembroAceite(int idGrupo, NovidadeGRDS novidadeGRDS) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT user FROM `Joins` WHERE `group`='" + idGrupo + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next()){
            novidadeGRDS.addUserAfetado(resultSet.getString("user"));
        }
        novidadeGRDS.addUserAfetado(novidadeGRDS.getUsernameUser());
        resultSet.close();
        statement.close();
    }

    public void verificaAfetadosKickMembro(int idGrupo, NovidadeGRDS novidadeGRDS) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT user FROM `Joins` WHERE `group`='" + idGrupo + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next()){
            novidadeGRDS.addUserAfetado(resultSet.getString("user"));
        }

        resultSet.close();
        statement.close();
    }

    public void verificaAfetadosDeleteGroup(int idGrupo, NovidadeGRDS novidadeGRDS) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlQuery = "SELECT user FROM `Joins` WHERE `group`='" + idGrupo + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next()){
            novidadeGRDS.addUserAfetado(resultSet.getString("user"));
        }

        resultSet.close();
        statement.close();
    }

    public void verificaAfetadosMensagem(String sender, String receiver, NovidadeGRDS novidadeGRDS) throws SQLException {
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                String sqlQuery;
                ResultSet resultSet;
                try{
                    int idGrupo = Integer.parseInt(receiver);

                    sqlQuery = "SELECT user FROM `Joins` WHERE `group`='" + idGrupo + "' AND user!='" + sender + "'";
                    resultSet = statement.executeQuery(sqlQuery);

                    while (resultSet.next()){
                        novidadeGRDS.addUserAfetado(resultSet.getString("user"));
                    }

                    sqlQuery = "SELECT admin FROM `Group` WHERE `idGroup`='" + idGrupo + "' AND admin!='" + sender + "'";
                    resultSet = statement.executeQuery(sqlQuery);

                    while (resultSet.next()){
                        novidadeGRDS.addUserAfetado(resultSet.getString("admin"));
                    }
                    resultSet.close();
                }catch (NumberFormatException e){
                    novidadeGRDS.addUserAfetado(receiver);
                }
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

    }

    public void verificaAfetadosEliminaMensagem(int idMsg, String username, NovidadeGRDS novidadeGRDS) throws SQLException {
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                String sqlQuery = "SELECT sender,user_receiver,group_receiver FROM `Msg` WHERE `idMsg`='" + idMsg + "'";;
                ResultSet resultSet = statement.executeQuery(sqlQuery);

                while (resultSet.next()){
                    if(resultSet.getString("user_receiver")==null){
                        sqlQuery = "SELECT user FROM `Joins` WHERE `group`='" + resultSet.getInt("group_receiver") + "' AND user!='" + username + "'";
                        resultSet = statement.executeQuery(sqlQuery);

                        while (resultSet.next()){
                            novidadeGRDS.addUserAfetado(resultSet.getString("user"));
                        }

                        sqlQuery = "SELECT admin FROM `Group` WHERE `idGroup`='" + resultSet.getInt("group_receiver") + "' AND admin!='" + username + "'";
                        resultSet = statement.executeQuery(sqlQuery);

                        while (resultSet.next()){
                            novidadeGRDS.addUserAfetado(resultSet.getString("admin"));
                        }
                        resultSet.close();
                    }
                    else{
                        if(resultSet.getString("sender").equals(username)){
                            novidadeGRDS.addUserAfetado(resultSet.getString("user_receiver"));
                        }
                        else{
                            novidadeGRDS.addUserAfetado(resultSet.getString("sender"));
                        }
                    }
                }
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);

    }
    public void setNewInteraction(String username) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlQuery = "UPDATE `User` SET lastInteraction=NOW() WHERE username='" + username + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();
    }

    public ArrayList<String> verificaUsersInativos() throws SQLException {
        ArrayList<String> usersInativos = new ArrayList<>();
        try {
            dbConn.setAutoCommit(false);
            Statement statement = dbConn.createStatement();
            String sqlQuery = "SELECT username FROM `User` WHERE TIMESTAMPDIFF(SECOND,lastInteraction,NOW())>=30 AND login=1";
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()){
                usersInativos.add(resultSet.getString("username"));
            }
            sqlQuery = "UPDATE `User` SET login=0 WHERE TIMESTAMPDIFF(SECOND,lastInteraction,NOW())>=30 AND login=1";
            statement.executeUpdate(sqlQuery);
            statement.close();
            dbConn.commit();
        }catch (Exception e){
            dbConn.rollback();
        }
        return usersInativos;
    }

    // FICHEIROS

    public void verificaAfetadosFicheiro(String sender, String receiver, NovidadeGRDS novidade) throws SQLException{
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                String sqlQuery;
                ResultSet resultSet;
                try{
                    int idGrupo = Integer.parseInt(receiver);

                    sqlQuery = "SELECT user FROM `Joins` WHERE `group`='" + idGrupo + "' AND user!='" + sender + "'";
                    resultSet = statement.executeQuery(sqlQuery);

                    while (resultSet.next()){
                        novidade.addUserAfetado(resultSet.getString("user"));
                    }

                    sqlQuery = "SELECT admin FROM `Group` WHERE `idGroup`='" + idGrupo + "' AND admin!='" + sender + "'";
                    resultSet = statement.executeQuery(sqlQuery);

                    while (resultSet.next()){
                        novidade.addUserAfetado(resultSet.getString("admin"));
                    }
                    resultSet.close();
                }catch (NumberFormatException e){
                    novidade.addUserAfetado(receiver);
                }
                statement.close();
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);
    }

    private int getNextIdFile() throws SQLException{
        int idAtribuir = -1;
        Statement statement = dbConn.createStatement();
        ResultSet resultSet;
        do{
            idAtribuir++;
            String sqlQuery = "SELECT name FROM `File` WHERE idFile='" + idAtribuir + "'";
            resultSet = statement.executeQuery(sqlQuery);
        }while (resultSet.next());

        resultSet.close();
        statement.close();
        return idAtribuir;
    }

    public String recebeFicheiro(Ficheiro ficheiro, NovidadeGRDS novidade) throws SQLException{
        int idGrupo;
        String username;
        String sqlQuery;
        boolean worked=true;
        do{
            worked = true;
            try {
                dbConn.setAutoCommit(false);
                Statement statement = dbConn.createStatement();
                int idFile = getNextIdFile();
                try{
                    idGrupo = Integer.parseInt(ficheiro.getReceiver());
                    if(!verificaExistenciaGrupo(idGrupo))
                        return GRUPO_INEXISTENTE;
                    if(!verificaMembroGrupo(idGrupo,ficheiro.getSender())){
                        return NOT_MEMBRO;
                    }
                    sqlQuery = "INSERT INTO `File` (idFile,name,date,viewed,sender,group_receiver) VALUES ('" +  idFile +  "','" + ficheiro.getName() + "','" + Timestamp.valueOf(ficheiro.getDate()) + "','" + 0 + "','" + ficheiro.getSender() + "','" + idGrupo + "')";
                    statement.executeUpdate(sqlQuery);
                }catch (NumberFormatException e){
                    username = ficheiro.getReceiver();
                    if(!verificaExistenciaUser(username))
                        return UTILIZADOR_INEXISTENTE;
                    sqlQuery = "INSERT INTO `File` (idFile,name,date,viewed,sender,user_receiver) VALUES ('" +  idFile +  "','" + ficheiro.getName() + "','" + Timestamp.valueOf(ficheiro.getDate()) + "','" + 0 + "','" + ficheiro.getSender() + "','" + username + "')";
                    statement.executeUpdate(sqlQuery);
                }
                statement.close();
                novidade.setIdFicheiro(idFile);
                dbConn.commit();
            }catch (Exception e){
                dbConn.rollback();
                worked = false;
            }
        }while (!worked);
        return SUCESSO;
    }
}
