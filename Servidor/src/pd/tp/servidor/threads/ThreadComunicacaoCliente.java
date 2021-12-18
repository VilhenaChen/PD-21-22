package pd.tp.servidor.threads;

import pd.tp.servidor.bd.ComunicacaoBD;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;

public class ThreadComunicacaoCliente extends Thread{
    private Socket sCli;
    private ComunicacaoBD comBD;
    private static final String SUCESSO = "SUCESSO";
    private static final String PASSWORD_ERRADA = "PASSWORD_ERRADA";
    private static final String UTILIZADOR_INEXISTENTE = "UTILIZADOR_INEXISTENTE";
    private static final String NOME_REPETIDO = "NOME_REPETIDO";
    private static final String USERNAME_REPETIDO = "USERNAME_REPETIDO";
    private static final String NOME_E_ADMIN_JA_EXISTENTES = "NOME_E_ADMIN_JA_EXISTENTES";
    private static final String ADMIN_INEXISTENTE = "ADMIN_INEXISTENTE";
    private static final String NOT_ADMIN = "NOT_ADMIN";
    private static final String GRUPO_INEXISTENTE = "GRUPO_INEXISTENTE";
    private HashMap<String, String> dadosUser;

    public ThreadComunicacaoCliente(Socket sCli, ComunicacaoBD comBD) {
        this.sCli = sCli;
        this.comBD = comBD;
    }


    private void separaDadosUser(String msg) {
        String[] array = msg.split(",");
        dadosUser.put("username", array[1]);
        dadosUser.put("password", array[2]);
        if(array.length > 3)
            dadosUser.put("name", array[3]);
    }

    private void login(ObjectOutputStream out, String msgRecebida){
        separaDadosUser(msgRecebida);
        try {
            String login = comBD.loginUser(dadosUser.get("username"), dadosUser.get("password"));
            if(login.equals(SUCESSO))
                System.out.println("O Utilizador '" + dadosUser.get("username") + "' efetou login com Sucesso");
            out.writeUnshared(login);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

    }

    private void signIn(ObjectOutputStream out, String msgRecebida){
        separaDadosUser(msgRecebida);
        try {
            String resultado = comBD.insereUser(dadosUser.get("name"), dadosUser.get("username"), dadosUser.get("password"),0);
            if(resultado.equals(SUCESSO))
                System.out.println("O Utilizador '" + dadosUser.get("username") + "' registou-se com Sucesso");
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateName(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        String username = array[1];
        String name = array[2];
        try {
            String resultado = comBD.updateUserName(name, username);
            if(resultado.equals(SUCESSO))
                System.out.println("O Utilizador '" + dadosUser.get("username") + "' mudou o seu nome com Sucesso");
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateUsername(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        String old_username = array[1];
        String new_username = array[2];
        try {
            String resultado = comBD.updateUserUsername(new_username, old_username);
            if(resultado.equals(SUCESSO))
                System.out.println("O Utilizador '" + old_username + "' alterou o seu username para '" + new_username + "'");
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePassword(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        String username = array[1];
        String password = array[2];
        try {
            String resultado = comBD.updateUserPassword(password, username);
            if(resultado.equals(SUCESSO))
                System.out.println("O Utilizador '" + username + "' mudou a sua password com sucesso");
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    //Grupos

    private void criaGrupo(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        String nomeGrupo = array[2];

        try {
            String resultado = comBD.createGroup(nomeGrupo, username);
            if(resultado.equals(SUCESSO))
                System.out.println("O Utilizador " + username + " criou o grupo " + nomeGrupo);
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void alteraNomeGrupo(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        int idGrupo = Integer.parseInt(array[1]);
        String novoNome = array[2];

        try{
            String resultado = comBD.updateGroupName(novoNome,idGrupo);
            if(resultado.equals(SUCESSO)){
                System.out.println("O nome do grupo " + idGrupo + " foi trocado para: " + novoNome);
            }
            out.writeUnshared(resultado);
            out.flush();
        }catch (IOException | SQLException e){
            e.printStackTrace();
        }
    }

    private void adereAGrupo(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        String username = array[1];
        int idGrupo = Integer.parseInt(array[2]);

        try {
            String resultado = comBD.joinGroup(idGrupo, username);
            if(resultado.equals(SUCESSO))
                System.out.println("O Utilizador " + username + " pediu para aderir ao grupo com o id: " + idGrupo);
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void saiDeGrupo(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        String username = array[1];
        int idGrupo = Integer.parseInt(array[2]);

        try {
            String resultado = comBD.leaveGroup(idGrupo, username);
            if(resultado.equals(SUCESSO))
                System.out.println("O Utilizador " + username + " saiu do grupo " + idGrupo);
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaMembrosGrupo(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String idGrupo = array[2];

        try {
            String resultado = comBD.listaMembrosGrupos(Integer.parseInt(idGrupo));
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaMembrosGrupoPorAceitar(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        String idGrupo = array[1];

        try {
            String resultado = comBD.listaMembrosGrupoPorAceitar(Integer.parseInt(idGrupo));
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaGruposAdmin(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        try {
            String resultado = comBD.listaGruposAdmin(username);
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaGrupos(ObjectOutputStream out, String msgRecebida) {
        try {
            String resultado = comBD.listaGrupos();
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void aceitaMembro(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        int idGrupo = Integer.parseInt(array[1]);
        String username = array[2];
        try{
            String resultado = comBD.aceitaMembro(username,idGrupo);
            if(resultado.equals(SUCESSO))
                System.out.println("O Utilizador '" + username + "' foi aceite no grupo: " + idGrupo);
            out.writeUnshared(resultado);
            out.flush();

        } catch(IOException | SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
            dadosUser = new HashMap<>();
        try {
            ObjectInputStream in = new ObjectInputStream(sCli.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(sCli.getOutputStream());
            String msgRecebida;

            do {
                msgRecebida = (String) in.readObject();

                if (msgRecebida.startsWith("LOGIN")) {
                    login(out,msgRecebida);
                } else {
                    if (msgRecebida.startsWith("REGISTO")) {
                        signIn(out,msgRecebida);
                    }
                    else{
                        if (msgRecebida.startsWith("UPDATE_NAME")) {
                            updateName(out,msgRecebida);
                        }
                        else{
                            if (msgRecebida.startsWith("UPDATE_USERNAME")) {
                                updateUsername(out, msgRecebida);
                            }
                            else{
                                if (msgRecebida.startsWith("UPDATE_PASSWORD")) {
                                    updatePassword(out,msgRecebida);
                                }
                                else {
                                    if(msgRecebida.startsWith("NOVO_GRUPO")) {
                                        criaGrupo(out, msgRecebida);
                                    }
                                    else {
                                        if(msgRecebida.startsWith("LISTA_GRUPOS_ADMIN")) {
                                            listaGruposAdmin(out, msgRecebida);
                                        }
                                        else {
                                            if(msgRecebida.startsWith("LISTA_GRUPOS")) {
                                                listaGrupos(out, msgRecebida);
                                            }
                                            else{
                                                if(msgRecebida.startsWith("ADERE_A_GRUPO")){
                                                    adereAGrupo(out,msgRecebida);
                                                }
                                                else{
                                                    if(msgRecebida.startsWith("SAI_DE_GRUPO")){
                                                        saiDeGrupo(out,msgRecebida);
                                                    }
                                                    else{
                                                        if(msgRecebida.startsWith("LISTA_MEMBROS_GRUPO_POR_ACEITAR")) {
                                                            listaMembrosGrupoPorAceitar(out, msgRecebida);
                                                        }
                                                        else{
                                                            if(msgRecebida.startsWith("ACEITA_MEMBRO")){
                                                                aceitaMembro(out,msgRecebida);
                                                            }
                                                            else{
                                                                if(msgRecebida.startsWith("UPDATE_NOME_GRUPO")){
                                                                    alteraNomeGrupo(out,msgRecebida);
                                                                }
                                                           }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


            }while (!msgRecebida.equals("LOGOUT"));

            comBD.logoutUser(dadosUser.get("username"));
            out.close();
            in.close();
            sCli.close();
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }
}
