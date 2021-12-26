package pd.tp.cliente.comunicacao;

import pd.tp.comum.Mensagem;
import pd.tp.cliente.Utilizador;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ComunicacaoServidor {
    Socket sCli;
    ObjectOutputStream out;
    ObjectInputStream in;

    public ComunicacaoServidor(Socket sCli, ObjectInputStream in, ObjectOutputStream out) {
        this.sCli = sCli;
        this.in = in;
        this.out = out;
    }

    public String efetuaLogin(Utilizador user) { //Manda o Login do User ao Servidor
        String resultado = "";
        try {
            out.writeObject("LOGIN," + user.getUsername() + "," + user.getPassword());
            out.flush();

            resultado = (String) in.readObject();

        }catch(IOException|ClassNotFoundException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public String efetuaRegisto(Utilizador user) { //Manda o Registo do User ao Servidor
        String resultado = "";
        try {
            out.writeUnshared("REGISTO," + user.getUsername() +"," + user.getPassword() + "," + user.getNome());
            out.flush();

            resultado = (String) in.readObject();

        }catch(IOException|ClassNotFoundException e){
            e.printStackTrace();
        }
        return resultado;
    }

    public void logout(Utilizador user){
        try {
            out.writeObject("LOGOUT," + user.getUsername());
            out.flush();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String trocaNome(Utilizador user, String nome) {
        String resultado = "";
        try {
            out.writeObject("UPDATE_NAME," + user.getUsername() + "," + nome );
            out.flush();

            resultado = (String) in.readObject();

        }catch(IOException|ClassNotFoundException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public String trocaPassword(Utilizador user, String password) {
        String resultado = "";
        try {
            out.writeObject("UPDATE_PASSWORD," + user.getUsername() + "," + password );
            out.flush();

            resultado = (String) in.readObject();

        }catch(IOException|ClassNotFoundException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public String trocaUsername(Utilizador user, String username) {
        String resultado = "";
        try {
            out.writeObject("UPDATE_USERNAME," + user.getUsername() + "," + username );
            out.flush();

            resultado = (String) in.readObject();

        }catch(IOException|ClassNotFoundException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    //GRUPOS
    public String criaGrupo(Utilizador user, String nome) {
        String resultado = "";
        try {
            out.writeObject("NOVO_GRUPO," + user.getUsername() + "," + nome);
            out.flush();

            resultado = (String) in.readObject();

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String adereAGrupo(Utilizador user, int idGrupo){
        String resultado = "";
        try {
            out.writeObject("ADERE_A_GRUPO," + user.getUsername() + "," + idGrupo);
            out.flush();

            resultado = (String) in.readObject();

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String saiDeGrupo(Utilizador user, int idGrupo)
    {
        String resultado = "";
        try {
            out.writeObject("SAI_DE_GRUPO," + user.getUsername() + "," + idGrupo);
            out.flush();

            resultado = (String) in.readObject();

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String trocaNomeGroup(String novo_nome, int idGrupo) {
        String resultado = "";
        try {
            out.writeObject("UPDATE_NOME_GRUPO," + idGrupo + "," + novo_nome);
            out.flush();

            resultado = (String) in.readObject();

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String listaGrupos() {
        String resultado = "";
        try {
            out.writeObject("LISTA_GRUPOS");
            out.flush();

            resultado = (String) in.readObject();

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String listaGruposAdmin(Utilizador user) {
        String resultado = "";
        try {
            out.writeObject("LISTA_GRUPOS_ADMIN," + user.getUsername());
            out.flush();

            resultado = (String) in.readObject();

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String listaMembrosGrupos(int idGrupo) {
        String resultado = "";
        try {
            out.writeObject("LISTA_TODOS_MEMBROS," + idGrupo);
            out.flush();

            resultado = (String) in.readObject();

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String listaMembrosGrupoPorAceitar(int idGrupo) {
        String resultado = "";
        try {
            out.writeObject("LISTA_MEMBROS_GRUPO_POR_ACEITAR," + idGrupo);
            out.flush();

            resultado = (String) in.readObject();

        }catch (IOException | ClassNotFoundException e) {
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
                out.writeObject("ACEITA_MEMBRO," + idGrupo + "," + array[i]);
                out.flush();

                resultado = (String) in.readObject();
                if(!resultado.equals("SUCESSO")){
                    falhas = falhas + "," + array[i];
                }

            }catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(falhas.equals("")){
            return "SUCESSO";
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
                out.writeObject("REJEITA_MEMBRO," + idGrupo + "," + array[i]);
                out.flush();

                resultado = (String) in.readObject();
                if(!resultado.equals("SUCESSO")){
                    falhas = falhas + "," + array[i];
                }

            }catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(falhas.equals("")){
            return "SUCESSO";
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

                out.writeObject("KICK_MEMBRO_GRUPO," + idGrupo + "," + array[i]);
                out.flush();

                resultado = (String) in.readObject();
                if(!resultado.equals("SUCESSO")){
                    falhas = falhas + "," + array[i];
                }

            }catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(falhas.equals("")){
            return "SUCESSO";
        }
        else{
            return "ERRO" + "," + falhas;
        }
    }

    public String eliminaGrupo(int idGrupo){
        String resultado = "";
        try{
            out.writeObject("ELIMINA_GRUPO," + idGrupo);
            out.flush();

            resultado = (String) in.readObject();
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return  resultado;
    }

    //Contactos

    public String listaContactos(Utilizador user) {
        String resultado = "";
        try {
            out.writeObject("LISTA_CONTACTOS," + user.getUsername());
            out.flush();

            resultado = (String) in.readObject();

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String listaContactosPorAceitar(Utilizador user) {
        String resultado = "";
        try {
            out.writeObject("LISTA_POR_ACEITAR_CONTACTOS," + user.getUsername());
            out.flush();

            resultado = (String) in.readObject();

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultado;
    }


    public String adicionaContacto(Utilizador user, String friend) {
        String resultado = "";
        try {
            out.writeObject("NOVO_CONTACTO," + user.getUsername() + "," +friend);
            out.flush();

            resultado = (String) in.readObject();

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String aceitaContactos(Utilizador user, String usernames){
        String resultado = "";
        String[] array = usernames.split(",");
        String falhas = "";

        for(int i = 0; i<array.length; i++){
            try {
                out.writeObject("ACEITA_CONTACTO," + user.getUsername() + "," + array[i]);
                out.flush();

                resultado = (String) in.readObject();
                if(!resultado.equals("SUCESSO")){
                    falhas = falhas + "," + array[i];
                }

            }catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(falhas.equals("")){
            return "SUCESSO";
        }
        else{
            return "ERRO" + "," + falhas;
        }
    }

    public String rejeitaContactos(Utilizador user, String usernames){
        String resultado = "";
        String[] array = usernames.split(",");
        String falhas = "";

        for(int i = 0; i<array.length; i++){
            try {
                out.writeObject("REJEITA_CONTACTO," + user.getUsername() + "," + array[i]);
                out.flush();

                resultado = (String) in.readObject();
                if(!resultado.equals("SUCESSO")){
                    falhas = falhas + "," + array[i];
                }

            }catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(falhas.equals("")){
            return "SUCESSO";
        }
        else{
            return "ERRO" + "," + falhas;
        }
    }

    public String eliminaContactos(Utilizador user, String usernames){
        String resultado = "";
        String[] array = usernames.split(",");
        String falhas = "";

        for(int i = 0; i<array.length; i++){
            try {
                out.writeObject("ELIMINA_CONTACTO," + user.getUsername() + "," + array[i]);
                out.flush();

                resultado = (String) in.readObject();
                if(!resultado.equals("SUCESSO")){
                    falhas = falhas + "," + array[i];
                }

            }catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(falhas.equals("")){
            return "SUCESSO";
        }
        else{
            return "ERRO" + "," + falhas;
        }
    }

    public String pesquisaUsers(String pesquisa) {
        String resultado = "";
        try {
            out.writeObject("PESQUISA_USER," + pesquisa);
            out.flush();

            resultado = (String) in.readObject();

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String listaUsers() {
        String resultado = "";
        try {
            out.writeObject("LISTA_USERS");
            out.flush();

            resultado = (String) in.readObject();

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    //Mensagens

    public String enviaMensagem(Mensagem msg) {
        String resultado = "";
        try {
            out.writeObject(msg);
            out.flush();

            resultado = (String) in.readObject();

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String eliminaMensagens(Utilizador user, String mensagens){
        String resultado = "";
        String[] array = mensagens.split(",");
        String falhas = "";

        for(int i = 0; i<array.length; i++){
            try {
                out.writeObject("ELIMINA_MENSAGEM," + array[i] + "," + user.getUsername());
                out.flush();

                resultado = (String) in.readObject();
                if(!resultado.equals("SUCESSO")){
                    falhas = falhas + "," + array[i];
                }

            }catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(falhas.equals("")){
            return "SUCESSO";
        }
        else{
            return "ERRO" + "," + falhas;
        }
    }

    public String listaMensagens(Utilizador user){
        String resultado = "";
        try {
            out.writeObject("LISTA_MENSAGENS," + user.getUsername());
            out.flush();

            resultado = (String) in.readObject();

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String listaMensagensParaEliminar(Utilizador user){
        String resultado = "";
        try {
            out.writeObject("LISTA_PARA_ELIMINAR_MSG," + user.getUsername());
            out.flush();
            resultado = (String) in.readObject();
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public String getCorpoMensagem(int escolha, Utilizador user) {
        String resultado = "";
        try {
            out.writeObject("GET_CORPO," + escolha + "," + user.getUsername());
            out.flush();
            resultado = (String) in.readObject();
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultado;
    }

}
