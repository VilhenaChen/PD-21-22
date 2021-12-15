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


}
