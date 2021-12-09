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

    public ComunicacaoServidor(Socket sCli) {
        this.sCli = sCli;
    }

    public String efetuaLogin(Utilizador user){ //Manda o Login do User ao Servidor
        String resultado = "";
        try {
            out = new ObjectOutputStream(sCli.getOutputStream());
            in = new ObjectInputStream(sCli.getInputStream());
            out.writeUnshared("LOGIN|" + user.getUsername() + "|" + user.getPassword());
            out.flush();

            resultado = (String) in.readObject();

            in.close();
            out.close();

        }catch(IOException|ClassNotFoundException e){
            e.printStackTrace();
        }
        return resultado;
    }

    public String efetuaRegisto(Utilizador user) { //Manda o Registo do User ao Servidor
                String resultado = "";
        try {
            out = new ObjectOutputStream(sCli.getOutputStream());
            in = new ObjectInputStream(sCli.getInputStream());
            out.writeUnshared("REGISTO|" + user.getUsername() +"|" + user.getPassword() + "|" + user.getNome());
            out.flush();

            resultado = (String) in.readObject();

            in.close();
            out.close();
        }catch(IOException|ClassNotFoundException e){
            e.printStackTrace();
        }
        return resultado;
    }

}
