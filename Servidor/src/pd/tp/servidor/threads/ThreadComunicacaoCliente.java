package pd.tp.servidor.threads;

import pd.tp.comum.Mensagem;
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
            if(login.startsWith(SUCESSO))
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

    private void logoutUser(String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        try {
            String resultado = comBD.logoutUser(username);
            if(resultado.equals(SUCESSO))
                System.out.println("O Utilizador '" + username + "' desligou-se");

        } catch (SQLException e) {
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
        int idGrupo =Integer.parseInt(array[1]);

        try {
            String resultado = comBD.listaMembrosGrupos(idGrupo);
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaMembrosGrupoPorAceitar(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        int idGrupo =Integer.parseInt(array[1]);

        try {
            String resultado = comBD.listaMembrosGrupoPorAceitar(idGrupo);
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

    private void rejeitaMembro(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        int idGrupo = Integer.parseInt(array[1]);
        String username = array[2];
        try{
            String resultado = comBD.rejeitaMembro(username,idGrupo);
            if(resultado.equals(SUCESSO))
                System.out.println("O pedido de adesão do Utilizador '" + username + "' foi rejeitado pelo admin do grupo: " + idGrupo);
            out.writeUnshared(resultado);
            out.flush();

        } catch(IOException | SQLException e){
            e.printStackTrace();
        }
    }

    private void excluiMembroGrupo(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        int idGrupo = Integer.parseInt(array[1]);
        String username = array[2];
        System.out.println("Cheguei à função antes da da bd!");
        try{
            String resultado = comBD.leaveGroup(idGrupo,username);
            if(resultado.equals(SUCESSO))
                System.out.println("O Utilizador '" + username + "' foi excluido do grupo: " + idGrupo);
            out.writeUnshared(resultado);
            out.flush();

        } catch(IOException | SQLException e){
            e.printStackTrace();
        }
    }

    private void eliminaGrupo(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        int idGrupo = Integer.parseInt(array[1]);
        try{
            String resultado = comBD.deleteGroup(idGrupo);
            if(resultado.equals(SUCESSO))
                System.out.println("O Grupo" + idGrupo + "foi eliminado");
            out.writeUnshared(resultado);
            out.flush();

        } catch(IOException | SQLException e){
            e.printStackTrace();
        }
    }

    private void novoContacto(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        String friend = array[2];
        try {
            String resultado = comBD.adicionaContacto(username,friend);
            if(resultado.equals(SUCESSO))
                System.out.println("O Utilizador '" + username + "' enviou um pedido de contacto para o '" + friend + "'" );
            out.writeUnshared(resultado);
            out.flush();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void aceitaContacto(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        String friend = array[2];
        try {
            String resultado = comBD.aceitaContacto(friend,username);
            if(resultado.equals(SUCESSO))
                System.out.println("O Utilizador '" + username + "' aceitou o pedido de contacto do user '" + friend + "'" );
            out.writeUnshared(resultado);
            out.flush();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void rejeitaContacto(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        String friend = array[2];
        try {
            String resultado = comBD.rejeitaContacto(friend,username);
            if(resultado.equals(SUCESSO))
                System.out.println("O Utilizador '" + username + "' rejeitou o pedido de contacto do user '" + friend + "'" );
            out.writeUnshared(resultado);
            out.flush();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaContactos(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        try {
            String resultado = comBD.listarContactos(username);
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaContactosPorAceitar(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        try {
            String resultado = comBD.listarContactosPorAceitar(username);
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void eliminaContacto(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        String contacto = array[2];
        try {
            String resultado = comBD.eliminaContacto(username,contacto);
            if(resultado.equals(SUCESSO))
                System.out.println("O contacto " + contacto + " do user " + username + "foi eliminado com sucesso");
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void pesquisaUsers(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        try {
            String resultado = comBD.pesquisaeListaUsers(username);
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaUsers(ObjectOutputStream out, String msgRecebida) {
        try {
            String resultado = comBD.pesquisaeListaUsers("");
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void recebeMsg(ObjectOutputStream out, Mensagem msg) {
        try {
            String resultado = comBD.recebeMsg(msg);
            if(resultado.equals(SUCESSO)) {
                System.out.println("O Utilizador " + msg.getSender() + " enviou uma menagem para o destinatario " + msg.getReceveiver());
            }
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void eliminaMensagem(ObjectOutputStream out, String msg){
        String[] array = msg.split(",");
        int idMsg = Integer.parseInt(array[1]);
        String username = array[2];
        try {
            String resultado = comBD.eliminaMsg(idMsg, username);
            if(resultado.equals(SUCESSO))
                System.out.println("A mensagem " + idMsg + " do user " + username + "foi eliminada com sucesso");
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaMensagens(ObjectOutputStream out, String msg){
        String[] array = msg.split(",");
        String username = array[1];
        try {
            String resultado = comBD.listaMensagens(username);
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void getCorpoMsg(ObjectOutputStream out, String msg){
        String[] array = msg.split(",");
        int idMsg = Integer.parseInt(array[1]);
        String username = array[2];
        try {
            String resultado = comBD.getCorpoMsg(idMsg,username);
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaMensagensParaEliminar(ObjectOutputStream out, String msg){
        String[] array = msg.split(",");
        String username = array[1];
        try {
            String resultado = comBD.listaMensagensParaEliminar(username);
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
            dadosUser = new HashMap<>();
        try {
            ObjectInputStream in = new ObjectInputStream(sCli.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(sCli.getOutputStream());
            Object objetoRecebido;
            String msgRecebida = "";
            do {
                objetoRecebido = in.readObject();

                if(objetoRecebido instanceof Mensagem) {
                    recebeMsg(out, (Mensagem) objetoRecebido);
                }
                else if(objetoRecebido instanceof String) {
                    msgRecebida = objetoRecebido.toString();

                    if (msgRecebida.startsWith("LOGIN")) {
                        login(out, msgRecebida);
                    } else {
                        if (msgRecebida.startsWith("REGISTO")) {
                            signIn(out, msgRecebida);
                        } else {
                            if (msgRecebida.startsWith("UPDATE_NAME")) {
                                updateName(out, msgRecebida);
                            } else {
                                if (msgRecebida.startsWith("UPDATE_USERNAME")) {
                                    updateUsername(out, msgRecebida);
                                } else {
                                    if (msgRecebida.startsWith("UPDATE_PASSWORD")) {
                                        updatePassword(out, msgRecebida);
                                    } else {
                                        if (msgRecebida.startsWith("NOVO_GRUPO")) {
                                            criaGrupo(out, msgRecebida);
                                        } else {
                                            if (msgRecebida.startsWith("LISTA_GRUPOS_ADMIN")) {
                                                listaGruposAdmin(out, msgRecebida);
                                            } else {
                                                if (msgRecebida.startsWith("LISTA_GRUPOS")) {
                                                    listaGrupos(out, msgRecebida);
                                                } else {
                                                    if (msgRecebida.startsWith("ADERE_A_GRUPO")) {
                                                        adereAGrupo(out, msgRecebida);
                                                    } else {
                                                        if (msgRecebida.startsWith("SAI_DE_GRUPO")) {
                                                            saiDeGrupo(out, msgRecebida);
                                                        } else {
                                                            if (msgRecebida.startsWith("KICK_MEMBRO_GRUPO")) {
                                                                excluiMembroGrupo(out, msgRecebida);
                                                            } else {
                                                                if (msgRecebida.startsWith("LISTA_MEMBROS_GRUPO_POR_ACEITAR")) {
                                                                    listaMembrosGrupoPorAceitar(out, msgRecebida);
                                                                } else {
                                                                    if (msgRecebida.startsWith("ACEITA_MEMBRO")) {
                                                                        aceitaMembro(out, msgRecebida);
                                                                    } else {
                                                                        if (msgRecebida.startsWith("UPDATE_NOME_GRUPO")) {
                                                                            alteraNomeGrupo(out, msgRecebida);
                                                                        } else {
                                                                            if (msgRecebida.startsWith("ELIMINA_GRUPO")) {
                                                                                eliminaGrupo(out, msgRecebida);
                                                                            } else {
                                                                                if (msgRecebida.startsWith("NOVO_CONTACTO")) {
                                                                                    novoContacto(out, msgRecebida);
                                                                                } else {
                                                                                    if (msgRecebida.startsWith("ACEITA_CONTACTO")) {
                                                                                        aceitaContacto(out, msgRecebida);
                                                                                    } else {
                                                                                        if (msgRecebida.startsWith("LISTA_CONTACTOS")) {
                                                                                            listaContactos(out, msgRecebida);
                                                                                        } else {
                                                                                            if (msgRecebida.startsWith("LISTA_POR_ACEITAR_CONTACTOS")) {
                                                                                                listaContactosPorAceitar(out, msgRecebida);
                                                                                            } else {
                                                                                                if (msgRecebida.startsWith("ELIMINA_CONTACTO")) {
                                                                                                    //Eliminar contacto e o historico de troca de msgs e ficheiros entre eles
                                                                                                    eliminaContacto(out, msgRecebida);
                                                                                                } else {
                                                                                                    if (msgRecebida.startsWith("PESQUISA_USER")) {
                                                                                                        pesquisaUsers(out, msgRecebida);
                                                                                                    } else {
                                                                                                        if (msgRecebida.startsWith("LISTA_USERS")) {
                                                                                                            listaUsers(out, msgRecebida);
                                                                                                        } else {
                                                                                                            if (msgRecebida.startsWith("LISTA_TODOS_MEMBROS")) {
                                                                                                                listaMembrosGrupo(out, msgRecebida);
                                                                                                            }
                                                                                                            else{
                                                                                                                if(msgRecebida.startsWith("ELIMINA_MENSAGEM")){
                                                                                                                    eliminaMensagem(out,msgRecebida);
                                                                                                                }
                                                                                                                else {
                                                                                                                    if(msgRecebida.startsWith("LISTA_MENSAGENS")) {
                                                                                                                        listaMensagens(out, msgRecebida);
                                                                                                                    }
                                                                                                                    else{
                                                                                                                        if(msgRecebida.startsWith("LISTA_PARA_ELIMINAR_MSG")){
                                                                                                                            listaMensagensParaEliminar(out,msgRecebida);
                                                                                                                        }
                                                                                                                        else{
                                                                                                                            if(msgRecebida.startsWith("GET_CORPO")){
                                                                                                                                getCorpoMsg(out,msgRecebida);
                                                                                                                            }
                                                                                                                            else{
                                                                                                                                if(msgRecebida.startsWith("REJEITA_CONTACTO")){
                                                                                                                                    rejeitaContacto(out,msgRecebida);
                                                                                                                                }
                                                                                                                                else {
                                                                                                                                    if (msgRecebida.startsWith("REJEITA_MEMBRO"))
                                                                                                                                    {
                                                                                                                                        rejeitaMembro(out,msgRecebida);
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
                            }
                        }
                    }
                }

            }while (!msgRecebida.equals("LOGOUT"));

            logoutUser(msgRecebida);
            out.close();
            in.close();
            sCli.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


}
