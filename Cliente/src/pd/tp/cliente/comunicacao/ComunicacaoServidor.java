package pd.tp.cliente.comunicacao;

import pd.tp.cliente.threads.ThreadEnviaFicheiro;
import pd.tp.cliente.threads.ThreadRecebeFicheiroServ;
import pd.tp.comum.Ficheiro;
import pd.tp.comum.Mensagem;
import pd.tp.cliente.Utilizador;
import pd.tp.comum.Utils;

import java.io.*;
import java.net.Socket;

public class ComunicacaoServidor implements Utils {
    private Socket sCli;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Utilizador user;
    private String ipServidor;

    public ComunicacaoServidor(Socket sCli, ObjectInputStream in, ObjectOutputStream out, String ipServidor) {
        this.sCli = sCli;
        this.in = in;
        this.out = out;
        this.ipServidor = ipServidor;
    }

    public void setUser(Utilizador user){
        this.user = user;
        System.out.println(user.toString());
    }

    public String efetuaLogin(Utilizador utilizador) { //Manda o Login do User ao Servidor
        String resultado = "";
        try {
            synchronized (out){
                out.writeObject(LOGIN+"," + utilizador.getUsername() + "," + utilizador.getPassword());
                out.flush();
            }

            resultado = (String) in.readObject();

        }catch(IOException|ClassNotFoundException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public String efetuaRegisto(Utilizador utilizador) { //Manda o Registo do User ao Servidor
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(REGISTO + "," + utilizador.getUsername() +"," + utilizador.getPassword() + "," + utilizador.getNome());
                out.flush();
            }
            resultado = (String) in.readObject();

        }catch(IOException|ClassNotFoundException e){
            e.printStackTrace();
        }
        return resultado;
    }

    public void logout(){
        try {
            synchronized (out){
                out.writeUnshared(LOGOUT + "," + user.getUsername());
                out.flush();
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String trocaNome(String nome) {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(UPDATE_NAME + "," + user.getUsername() + "," + nome );
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

        }catch(IOException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public String trocaPassword(String password) {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(UPDATE_PASSWORD + "," + user.getUsername() + "," + password );
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

        }catch(IOException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public String trocaUsername(String username) {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(UPDATE_USERNAME + "," + user.getUsername() + "," + username );
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

        }catch(IOException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    //GRUPOS
    public String criaGrupo(String nome) {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(NOVO_GRUPO + "," + user.getUsername() + "," + nome);
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

        }catch (IOException  e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String adereAGrupo(int idGrupo){
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(ADERE_A_GRUPO + "," + user.getUsername() + "," + idGrupo);
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String saiDeGrupo(int idGrupo)
    {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(SAI_DE_GRUPO + "," + user.getUsername() + "," + idGrupo);
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String trocaNomeGroup(String novo_nome, int idGrupo) {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(UPDATE_NOME_GRUPO + "," + idGrupo + "," + novo_nome);
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String listaGrupos() {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(LISTA_GRUPOS);
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String listaGruposAdmin() {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(LISTA_GRUPOS_ADMIN + "," + user.getUsername());
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String listaMembrosGrupos(int idGrupo) {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(LISTA_TODOS_MEMBROS + "," + idGrupo);
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String listaMembrosGrupoPorAceitar(int idGrupo) {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(LISTA_MEMBROS_GRUPO_POR_ACEITAR + "," + idGrupo);
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String aceitaMembros(String usernames, int idGrupo){
        String resultado = "";
        String[] array = usernames.split(",");
        String falhas = "";

        for(int i = 0; i<array.length; i++){
            try {
                synchronized (out){
                    out.writeUnshared(ACEITA_MEMBRO + "," + idGrupo + "," + array[i]);
                    out.flush();
                }

                while(true){
                    if (user.isRecebiResultado()){
                        resultado = user.getResultadoComando();
                        user.eraseResultadoComando();
                        user.setRecebiResultado(false);
                        break;
                    }
                }

                if(!resultado.equals(SUCESSO)){
                    falhas = falhas + "," + array[i];
                }

            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(falhas.equals("")){
            return SUCESSO;
        }
        else{
            return "ERRO" + "," + falhas;
        }
    }

    public String rejeitaMembros(String usernames, int idGrupo){
        String resultado = "";
        String[] array = usernames.split(",");
        String falhas = "";

        for(int i = 0; i<array.length; i++){
            try {
                synchronized (out){
                    out.writeUnshared(REJEITA_MEMBRO + "," + idGrupo + "," + array[i]);
                    out.flush();
                }

                while(true){
                    if (user.isRecebiResultado()){
                        resultado = user.getResultadoComando();
                        user.eraseResultadoComando();
                        user.setRecebiResultado(false);
                        break;
                    }
                }
                if(!resultado.equals(SUCESSO)){
                    falhas = falhas + "," + array[i];
                }

            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(falhas.equals("")){
            return SUCESSO;
        }
        else{
            return "ERRO" + "," + falhas;
        }
    }

    public String excluiMembros(String usernames_excluir, int idGrupo) {
        String resultado = "";
        String[] array = usernames_excluir.split(",");
        String falhas = "";

        for(int i = 0; i<array.length; i++){
            try {
                synchronized (out){
                    out.writeUnshared(KICK_MEMBRO_GRUPO + "," + idGrupo + "," + array[i]);
                    out.flush();
                }

                while(true){
                    if (user.isRecebiResultado()){
                        resultado = user.getResultadoComando();
                        user.eraseResultadoComando();
                        user.setRecebiResultado(false);
                        break;
                    }
                }
                if(!resultado.equals(SUCESSO)){
                    falhas = falhas + "," + array[i];
                }

            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(falhas.equals("")){
            return SUCESSO;
        }
        else{
            return "ERRO" + "," + falhas;
        }
    }

    public String eliminaGrupo(int idGrupo){
        String resultado = "";
        try{
            synchronized (out){
                out.writeUnshared(ELIMINA_GRUPO + "," + idGrupo);
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return  resultado;
    }

    public String getListaMembrosGrupo(int escolha) {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(GET_MEMBROS_GRUPO + "," + escolha);
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    //Contactos

    public String listaContactos() {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(LISTA_CONTACTOS + "," + user.getUsername());
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String listaContactosPorAceitar() {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(LISTA_POR_ACEITAR_CONTACTOS + "," + user.getUsername());
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }


    public String adicionaContacto(String friend) {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(NOVO_CONTACTO + "," + user.getUsername() + "," +friend);
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String aceitaContactos(String usernames){
        String resultado = "";
        String[] array = usernames.split(",");
        String falhas = "";

        for(int i = 0; i<array.length; i++){
            try {
                synchronized (out){
                    out.writeUnshared(ACEITA_CONTACTO + "," + user.getUsername() + "," + array[i]);
                    out.flush();
                }

                while(true){
                    if (user.isRecebiResultado()){
                        resultado = user.getResultadoComando();
                        user.eraseResultadoComando();
                        user.setRecebiResultado(false);
                        break;
                    }
                }
                if(!resultado.equals(SUCESSO)){
                    falhas = falhas + "," + array[i];
                }

            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(falhas.equals("")){
            return SUCESSO;
        }
        else{
            return "ERRO" + "," + falhas;
        }
    }

    public String rejeitaContactos(String usernames){
        String resultado = "";
        String[] array = usernames.split(",");
        String falhas = "";

        for(int i = 0; i<array.length; i++){
            try {
                synchronized (out){
                    out.writeUnshared(REJEITA_CONTACTO + "," + user.getUsername() + "," + array[i]);
                    out.flush();
                }

                while(true){
                    if (user.isRecebiResultado()){
                        resultado = user.getResultadoComando();
                        user.eraseResultadoComando();
                        user.setRecebiResultado(false);
                        break;
                    }
                }
                if(!resultado.equals(SUCESSO)){
                    falhas = falhas + "," + array[i];
                }

            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(falhas.equals("")){
            return SUCESSO;
        }
        else{
            return "ERRO" + "," + falhas;
        }
    }

    public String eliminaContactos(String usernames){
        String resultado = "";
        String[] array = usernames.split(",");
        String falhas = "";

        for(int i = 0; i<array.length; i++){
            try {
                synchronized (out){
                    out.writeUnshared(ELIMINA_CONTACTO + "," + user.getUsername() + "," + array[i]);
                    out.flush();
                }

                while(true){
                    if (user.isRecebiResultado()){
                        resultado = user.getResultadoComando();
                        user.eraseResultadoComando();
                        user.setRecebiResultado(false);
                        break;
                    }
                }
                if(!resultado.equals(SUCESSO)){
                    falhas = falhas + "," + array[i];
                }

            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(falhas.equals("")){
            return SUCESSO;
        }
        else{
            return "ERRO" + "," + falhas;
        }
    }

    public String pesquisaUsers(String pesquisa) {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(PESQUISA_USER + "," + pesquisa);
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String listaUsers() {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(LISTA_USERS);
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    //Mensagens

    public String enviaMensagem(Mensagem msg) {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(msg);
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String eliminaMensagens(String mensagens){
        String resultado = "";
        String[] array = mensagens.split(",");
        String falhas = "";

        for(int i = 0; i<array.length; i++){
            try {
                synchronized (out){
                    out.writeUnshared(ELIMINA_MENSAGEM + "," + array[i] + "," + user.getUsername());
                    out.flush();
                }

                while(true){
                    if (user.isRecebiResultado()){
                        resultado = user.getResultadoComando();
                        user.eraseResultadoComando();
                        user.setRecebiResultado(false);
                        break;
                    }
                }
                if(!resultado.equals(SUCESSO)){
                    falhas = falhas + "," + array[i];
                }

            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(falhas.equals("")){
            return SUCESSO;
        }
        else{
            return "ERRO" + "," + falhas;
        }
    }

    public String listaMensagens(){
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(LISTA_MENSAGENS + "," + user.getUsername());
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String listaMensagensParaEliminar(){
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(LISTA_PARA_ELIMINAR_MSG + "," + user.getUsername());
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String getCorpoMensagem(int escolha) {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(GET_CORPO + "," + escolha + "," + user.getUsername());
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public void heartbeat(){
        try {
            synchronized (out){
                out.writeUnshared(HEARTBEAT_CLI);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Ficheiros

    public String enviaFicheiro(Ficheiro ficheiro, String dir) {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(ficheiro);
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }
            if(!resultado.startsWith(SUCESSO)){
                return resultado;
            }

            ThreadEnviaFicheiro threadEnviaFicheiro = new ThreadEnviaFicheiro(ipServidor,dir,ficheiro,resultado);
            threadEnviaFicheiro.start();

        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String listaFicheiros() {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(LISTA_FICHEIROS + "," + user.getUsername());
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String getFicheiro(int escolha) {
        String resultado = "";
        try {
            synchronized (out){
                out.writeUnshared(GET_FICHEIRO + "," + escolha + "," + user.getUsername());
                out.flush();
            }

            while(true){
                if (user.isRecebiResultado()){
                    resultado = user.getResultadoComando();
                    user.eraseResultadoComando();
                    user.setRecebiResultado(false);
                    break;
                }
            }

            if(!resultado.startsWith(SUCESSO)){
                return resultado;
            }

            ThreadRecebeFicheiroServ threadRecebeFicheiroServ = new ThreadRecebeFicheiroServ(ipServidor,resultado,user.getUsername());
            threadRecebeFicheiroServ.start();

        }catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;
    }
}
