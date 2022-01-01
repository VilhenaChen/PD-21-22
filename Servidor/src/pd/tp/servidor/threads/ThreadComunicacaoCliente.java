package pd.tp.servidor.threads;

import pd.tp.cliente.Clientes;
import pd.tp.comum.Mensagem;
import pd.tp.comum.NovidadeGRDS;
import pd.tp.comum.Utils;
import pd.tp.servidor.bd.ComunicacaoBD;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Timer;

public class ThreadComunicacaoCliente extends Thread implements Utils {
    private Socket sCli;
    private ComunicacaoBD comBD;
    private HashMap<String, String> dadosUser;
    private Clientes clientes;
    DatagramSocket ds;
    DatagramPacket dp;
    ThreadEnviaAtualizacoesCliente threadEnviaAtualizacoesCliente;
    ThreadVerificaClientesAtivosBD threadVerificaClientesAtivosBD;
    Timer timer;
    int id;

    public ThreadComunicacaoCliente(Socket sCli, ComunicacaoBD comBD, DatagramSocket ds, DatagramPacket dp, int id, Clientes clientes) {
        this.sCli = sCli;
        this.comBD = comBD;
        this.clientes = clientes;
        this.ds = ds;
        this.dp = dp;
        this.id = id;
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
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(LOGIN);
            novidade.setUsernameUser(dadosUser.get("username"));
            String login = comBD.loginUser(dadosUser.get("username"), dadosUser.get("password"));
            if(login.startsWith(SUCESSO)){
                System.out.println("O Utilizador '" + dadosUser.get("username") + "' efetou login com Sucesso");
                clientes.addCli(dadosUser.get("username"));

                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
            out.writeUnshared(login);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

    }

    private void signIn(ObjectOutputStream out, String msgRecebida){
        separaDadosUser(msgRecebida);
        try {
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(REGISTO);
            novidade.setUsernameUser(dadosUser.get("username"));
            String resultado = comBD.insereUser(dadosUser.get("name"), dadosUser.get("username"), dadosUser.get("password"),0);
            if(resultado.equals(SUCESSO)){
                System.out.println("O Utilizador '" + dadosUser.get("username") + "' registou-se com Sucesso");

                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
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
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(UPDATE_NAME);
            novidade.setUsernameUser(dadosUser.get("username"));
            String resultado = comBD.updateUserName(name, username);
            if(resultado.equals(SUCESSO)){
                dadosUser.replace("name", name);
                System.out.println("O Utilizador '" + dadosUser.get("username") + "' mudou o seu nome com Sucesso");
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
            synchronized (out) {
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateUsername(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        String old_username = array[1];
        String new_username = array[2];
        try {
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(UPDATE_USERNAME);
            novidade.setUsernameUser(old_username);
            novidade.setNovoUsernameUser(new_username);
            comBD.verificaAfetadosUpdateUsername(old_username,novidade);
            String resultado = comBD.updateUserUsername(new_username, old_username);
            if(resultado.equals(SUCESSO)){
                dadosUser.replace("username", new_username);
                threadEnviaAtualizacoesCliente.setUsername(new_username);
                System.out.println("O Utilizador '" + old_username + "' alterou o seu username para '" + new_username + "'");
                clientes.updateUsernameCli(new_username); //Ve se esta bem que isto apaga demasiadas coisas
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePassword(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        String username = array[1];
        String password = array[2];
        try {
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(UPDATE_PASSWORD);
            novidade.setUsernameUser(dadosUser.get("username"));

            String resultado = comBD.updateUserPassword(password, username);
            if(resultado.equals(SUCESSO)) {
                System.out.println("O Utilizador '" + username + "' mudou a sua password com sucesso");
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void logoutUser(String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        try {
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(LOGOUT);
            novidade.setUsernameUser(username);
            String resultado = comBD.logoutUser(username);
            novidade.setTipoMsg("LOGOUT");
            novidade.setUsernameUser(dadosUser.get("username"));
            if(resultado.equals(SUCESSO)){
                System.out.println("O Utilizador '" + username + "' desligou-se");
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }

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
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(NOVO_GRUPO);
            novidade.setUsernameUser(username);
            String resultado = comBD.createGroup(nomeGrupo, username, novidade);
            if(resultado.equals(SUCESSO))
                System.out.println("O Utilizador " + username + " criou o grupo " + nomeGrupo);
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void alteraNomeGrupo(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        int idGrupo = Integer.parseInt(array[1]);
        String novoNome = array[2];

        try{
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(UPDATE_NOME_GRUPO);
            novidade.setUsernameUser(dadosUser.get("username"));
            novidade.setIdGrupo(idGrupo);
            novidade.setNovoNomeGrupo(novoNome);
            comBD.verificaAfetadosUpdateGroupName(idGrupo,novidade);
            String resultado = comBD.updateGroupName(novoNome,idGrupo);
            if(resultado.equals(SUCESSO)){
                System.out.println("O nome do grupo " + idGrupo + " foi trocado para: " + novoNome);
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        }catch (IOException | SQLException e){
            e.printStackTrace();
        }
    }

    private void adereAGrupo(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        String username = array[1];
        int idGrupo = Integer.parseInt(array[2]);

        try {
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(ADERE_A_GRUPO);
            novidade.setUsernameUser(username);
            novidade.setIdGrupo(idGrupo);
            comBD.verificaAfetadosAdereGrupo(idGrupo,username,novidade);
            String resultado = comBD.joinGroup(idGrupo, username);
            if(resultado.equals(SUCESSO)){
                System.out.println("O Utilizador " + username + " pediu para aderir ao grupo com o id: " + idGrupo);
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void saiDeGrupo(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        String username = array[1];
        int idGrupo = Integer.parseInt(array[2]);

        try {
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(SAI_DE_GRUPO);
            novidade.setIdGrupo(idGrupo);
            novidade.setUsernameUser(username);
            comBD.verificaAfetadosLeaveGroup(idGrupo,username,novidade);
            String resultado = comBD.leaveGroup(idGrupo, username);
            if(resultado.equals(SUCESSO)) {
                System.out.println("O Utilizador " + username + " saiu do grupo " + idGrupo);
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaMembrosGrupo(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        int idGrupo =Integer.parseInt(array[1]);

        try {
            String resultado = comBD.listaMembrosGrupos(idGrupo);
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaMembrosGrupoPorAceitar(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        int idGrupo =Integer.parseInt(array[1]);

        try {
            String resultado = comBD.listaMembrosGrupoPorAceitar(idGrupo);
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaGruposAdmin(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        try {
            String resultado = comBD.listaGruposAdmin(username);
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaGrupos(ObjectOutputStream out, String msgRecebida) {
        try {
            String resultado = comBD.listaGrupos();
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void aceitaMembro(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        int idGrupo = Integer.parseInt(array[1]);
        String username = array[2];

        try{
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(ACEITA_MEMBRO);
            novidade.setIdGrupo(idGrupo);
            novidade.setUsernameUser(username);
            comBD.verificaAfetadosMembroAceite(idGrupo,novidade);
            String resultado = comBD.aceitaMembro(username,idGrupo);
            if(resultado.equals(SUCESSO)) {
                System.out.println("O Utilizador '" + username + "' foi aceite no grupo: " + idGrupo);
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }

        } catch(IOException | SQLException e){
            e.printStackTrace();
        }
    }

    private void rejeitaMembro(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        int idGrupo = Integer.parseInt(array[1]);
        String username = array[2];
        try{
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(REJEITA_MEMBRO);
            novidade.setUsernameUser(username);
            novidade.setIdGrupo(idGrupo);
            novidade.addUserAfetado(username);
            String resultado = comBD.rejeitaMembro(username,idGrupo);
            if(resultado.equals(SUCESSO)) {
                System.out.println("O pedido de adesão do Utilizador '" + username + "' foi rejeitado pelo admin do grupo: " + idGrupo);
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }

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
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(KICK_MEMBRO_GRUPO);
            novidade.setIdGrupo(idGrupo);
            novidade.setUsernameUser(username);
            comBD.verificaAfetadosKickMembro(idGrupo,novidade);
            String resultado = comBD.leaveGroup(idGrupo,username);
            if(resultado.equals(SUCESSO)) {
                System.out.println("O Utilizador '" + username + "' foi excluido do grupo: " + idGrupo);
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }

        } catch(IOException | SQLException e){
            e.printStackTrace();
        }
    }

    private void eliminaGrupo(ObjectOutputStream out, String msgRecebida){
        String[] array = msgRecebida.split(",");
        int idGrupo = Integer.parseInt(array[1]);
        try{
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(ELIMINA_GRUPO);
            novidade.setIdGrupo(idGrupo);
            comBD.verificaAfetadosDeleteGroup(idGrupo,novidade);
            String resultado = comBD.deleteGroup(idGrupo);
            if(resultado.equals(SUCESSO)) {
                System.out.println("O Grupo" + idGrupo + "foi eliminado");
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }

        } catch(IOException | SQLException e){
            e.printStackTrace();
        }
    }

    private void getMembrosGrupo(ObjectOutputStream out,String msgRecebida) {
        String[] array = msgRecebida.split(",");
        int idGrupo = Integer.parseInt(array[1]);
        try {
            String resultado = comBD.listaMembrosGrupos(idGrupo);
            out.writeUnshared(resultado);
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void novoContacto(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        String friend = array[2];
        try {
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(NOVO_CONTACTO);
            novidade.setUsernameUser(username);
            novidade.setFriend(friend);
            novidade.addUserAfetado(friend);
            String resultado = comBD.adicionaContacto(username,friend);
            if(resultado.equals(SUCESSO)){
                System.out.println("O Utilizador '" + username + "' enviou um pedido de contacto para o '" + friend + "'" );
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void aceitaContacto(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        String friend = array[2];
        try {
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(ACEITA_CONTACTO);
            novidade.setFriend(friend);
            novidade.setUsernameUser(username);
            novidade.addUserAfetado(friend);
            String resultado = comBD.aceitaContacto(friend,username);
            if(resultado.equals(SUCESSO)){
                System.out.println("O Utilizador '" + username + "' aceitou o pedido de contacto do user '" + friend + "'" );
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void rejeitaContacto(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        String friend = array[2];
        try {
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(REJEITA_CONTACTO);
            novidade.setUsernameUser(username);
            novidade.setFriend(friend);
            novidade.addUserAfetado(friend);
            String resultado = comBD.rejeitaContacto(friend,username);
            if(resultado.equals(SUCESSO)) {
                System.out.println("O Utilizador '" + username + "' rejeitou o pedido de contacto do user '" + friend + "'");
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaContactos(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        try {
            String resultado = comBD.listarContactos(username);
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaContactosPorAceitar(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        try {
            String resultado = comBD.listarContactosPorAceitar(username);
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void eliminaContacto(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        String contacto = array[2];
        try {
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(ELIMINA_CONTACTO);
            novidade.setUsernameUser(username);
            novidade.setFriend(contacto);
            novidade.addUserAfetado(contacto);
            String resultado = comBD.eliminaContacto(username,contacto);
            if(resultado.equals(SUCESSO)) {
                System.out.println("O contacto " + contacto + " do user " + username + "foi eliminado com sucesso");
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void pesquisaUsers(ObjectOutputStream out, String msgRecebida) {
        String[] array = msgRecebida.split(",");
        String username = array[1];
        try {
            String resultado = comBD.pesquisaeListaUsers(username);
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaUsers(ObjectOutputStream out, String msgRecebida) {
        try {
            String resultado = comBD.pesquisaeListaUsers("");
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }



    private void recebeMsg(ObjectOutputStream out, Mensagem msg) {
        try {
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(MENSAGEM);
            novidade.setUsernameUser(msg.getSender());
            novidade.setReceiver(msg.getReceveiver());
            comBD.verificaAfetadosMensagem(msg.getSender(),msg.getReceveiver(),novidade);
            String resultado = comBD.recebeMsg(msg, novidade);
            if(resultado.equals(SUCESSO)) {
                System.out.println("O Utilizador " + msg.getSender() + " enviou uma menagem para o destinatario " + msg.getReceveiver());
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void eliminaMensagem(ObjectOutputStream out, String msg){
        String[] array = msg.split(",");
        int idMsg = Integer.parseInt(array[1]);
        String username = array[2];
        try {
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(ELIMINA_MENSAGEM);
            novidade.setIdMsg(idMsg);
            novidade.setUsernameUser(username);
            comBD.verificaAfetadosEliminaMensagem(idMsg, username, novidade);
            String resultado = comBD.eliminaMsg(idMsg, username);
            if(resultado.equals(SUCESSO)){
                System.out.println("A mensagem " + idMsg + " do user " + username + "foi eliminada com sucesso");
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
                threadEnviaAtualizacaoGRDS.start();
            }
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaMensagens(ObjectOutputStream out, String msg){
        String[] array = msg.split(",");
        String username = array[1];
        try {
            String resultado = comBD.listaMensagens(username);
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
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
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void listaMensagensParaEliminar(ObjectOutputStream out, String msg){
        String[] array = msg.split(",");
        String username = array[1];
        try {
            String resultado = comBD.listaMensagensParaEliminar(username);
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void novaInteracao(){
        try {
            comBD.setNewInteraction(dadosUser.get("username"));
            //System.out.println("Atualizei a ultima interação com o cliente " + dadosUser.get("username"));
        } catch (SQLException e) {
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

                    if (msgRecebida.startsWith(LOGIN)) {
                        login(out, msgRecebida);
                        threadEnviaAtualizacoesCliente = new ThreadEnviaAtualizacoesCliente(out,clientes,dadosUser.get("username"));
                        threadEnviaAtualizacoesCliente.start();
                    } else {
                        if (msgRecebida.startsWith(REGISTO)) {
                            novaInteracao();
                            signIn(out, msgRecebida);
                        } else {
                            if (msgRecebida.startsWith(UPDATE_NAME)) {
                                novaInteracao();
                                updateName(out, msgRecebida);
                            } else {
                                if (msgRecebida.startsWith(UPDATE_USERNAME)) {
                                    novaInteracao();
                                    updateUsername(out, msgRecebida);
                                } else {
                                    if (msgRecebida.startsWith(UPDATE_PASSWORD)) {
                                        novaInteracao();
                                        updatePassword(out, msgRecebida);
                                    } else {
                                        if (msgRecebida.startsWith(NOVO_GRUPO)) {
                                            novaInteracao();
                                            criaGrupo(out, msgRecebida);
                                        } else {
                                            if (msgRecebida.startsWith(LISTA_GRUPOS_ADMIN)) {
                                                novaInteracao();
                                                listaGruposAdmin(out, msgRecebida);
                                            } else {
                                                if (msgRecebida.startsWith(LISTA_GRUPOS)) {
                                                    novaInteracao();
                                                    listaGrupos(out, msgRecebida);
                                                } else {
                                                    if (msgRecebida.startsWith(ADERE_A_GRUPO)) {
                                                        novaInteracao();
                                                        adereAGrupo(out, msgRecebida);
                                                    } else {
                                                        if (msgRecebida.startsWith(SAI_DE_GRUPO)) {
                                                            novaInteracao();
                                                            saiDeGrupo(out, msgRecebida);
                                                        } else {
                                                            if (msgRecebida.startsWith(KICK_MEMBRO_GRUPO)) {
                                                                novaInteracao();
                                                                excluiMembroGrupo(out, msgRecebida);
                                                            } else {
                                                                if (msgRecebida.startsWith(LISTA_MEMBROS_GRUPO_POR_ACEITAR)) {
                                                                    novaInteracao();
                                                                    listaMembrosGrupoPorAceitar(out, msgRecebida);
                                                                } else {
                                                                    if (msgRecebida.startsWith(ACEITA_MEMBRO)) {
                                                                        novaInteracao();
                                                                        aceitaMembro(out, msgRecebida);
                                                                    } else {
                                                                        if (msgRecebida.startsWith(UPDATE_NOME_GRUPO)) {
                                                                            novaInteracao();
                                                                            alteraNomeGrupo(out, msgRecebida);
                                                                        } else {
                                                                            if (msgRecebida.startsWith(ELIMINA_GRUPO)) {
                                                                                novaInteracao();
                                                                                eliminaGrupo(out, msgRecebida);
                                                                            } else {
                                                                                if (msgRecebida.startsWith(NOVO_CONTACTO)) {
                                                                                    novaInteracao();
                                                                                    novoContacto(out, msgRecebida);
                                                                                } else {
                                                                                    if (msgRecebida.startsWith(ACEITA_CONTACTO)) {
                                                                                        novaInteracao();
                                                                                        aceitaContacto(out, msgRecebida);
                                                                                    } else {
                                                                                        if (msgRecebida.startsWith(LISTA_CONTACTOS)) {
                                                                                            novaInteracao();
                                                                                            listaContactos(out, msgRecebida);
                                                                                        } else {
                                                                                            if (msgRecebida.startsWith(LISTA_POR_ACEITAR_CONTACTOS)) {
                                                                                                novaInteracao();
                                                                                                listaContactosPorAceitar(out, msgRecebida);
                                                                                            } else {
                                                                                                if (msgRecebida.startsWith(ELIMINA_CONTACTO)) {
                                                                                                    novaInteracao();
                                                                                                    //Eliminar contacto e o historico de troca de msgs e ficheiros entre eles
                                                                                                    eliminaContacto(out, msgRecebida);
                                                                                                } else {
                                                                                                    if (msgRecebida.startsWith(PESQUISA_USER)) {
                                                                                                        novaInteracao();
                                                                                                        pesquisaUsers(out, msgRecebida);
                                                                                                    } else {
                                                                                                        if (msgRecebida.startsWith(LISTA_USERS)) {
                                                                                                            novaInteracao();
                                                                                                            listaUsers(out, msgRecebida);
                                                                                                        } else {
                                                                                                            if (msgRecebida.startsWith(LISTA_TODOS_MEMBROS)) {
                                                                                                                novaInteracao();
                                                                                                                listaMembrosGrupo(out, msgRecebida);
                                                                                                            }
                                                                                                            else{
                                                                                                                if(msgRecebida.startsWith(ELIMINA_MENSAGEM)){
                                                                                                                    novaInteracao();
                                                                                                                    eliminaMensagem(out,msgRecebida);
                                                                                                                }
                                                                                                                else {
                                                                                                                    if(msgRecebida.startsWith(LISTA_MENSAGENS)) {
                                                                                                                        novaInteracao();
                                                                                                                        listaMensagens(out, msgRecebida);
                                                                                                                    }
                                                                                                                    else{
                                                                                                                        if(msgRecebida.startsWith(LISTA_PARA_ELIMINAR_MSG)){
                                                                                                                            novaInteracao();
                                                                                                                            listaMensagensParaEliminar(out,msgRecebida);
                                                                                                                        }
                                                                                                                        else{
                                                                                                                            if(msgRecebida.startsWith(GET_CORPO)){
                                                                                                                                novaInteracao();
                                                                                                                                getCorpoMsg(out,msgRecebida);
                                                                                                                            }
                                                                                                                            else{
                                                                                                                                if(msgRecebida.startsWith(REJEITA_CONTACTO)){
                                                                                                                                    novaInteracao();
                                                                                                                                    rejeitaContacto(out,msgRecebida);
                                                                                                                                }
                                                                                                                                else {
                                                                                                                                    if (msgRecebida.startsWith(REJEITA_MEMBRO)) {
                                                                                                                                        novaInteracao();
                                                                                                                                        rejeitaMembro(out,msgRecebida);
                                                                                                                                    }
                                                                                                                                    else {
                                                                                                                                        if(msgRecebida.startsWith(GET_MEMBROS_GRUPO)) {
                                                                                                                                            novaInteracao();
                                                                                                                                            getMembrosGrupo(out,msgRecebida);
                                                                                                                                        }
                                                                                                                                        else{
                                                                                                                                            if(msgRecebida.equals(HEARTBEAT_CLI)){
                                                                                                                                                novaInteracao();
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
                    }
                }
            }while (!msgRecebida.startsWith("LOGOUT"));
            logoutUser(msgRecebida);
            threadEnviaAtualizacoesCliente.join();
            out.close();
            in.close();
            sCli.close();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
