package pd.tp.cliente.comunicacao;

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

    public void logout(){
        try {
            out.writeObject("LOGOUT");
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

    public String trocaNomeGroup(Utilizador user, String novo_nome, int idGrupo) {
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
            out.writeObject("LISTA_MEMBROS_GRUPO," + idGrupo);
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

}
