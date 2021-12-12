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

        if (sCli.isClosed())
            System.out.println("Fechado aqui dentro ComServ!");
    }

    public String efetuaLogin(Utilizador user){
        if (sCli.isClosed())
            System.out.println("Fechado aqui EfetuaLogin 1!");
        //Manda o Login do User ao Servidor
        String resultado = "";
        try {
            if (sCli.isClosed())
                System.out.println("Fechado aqui EfetuaLogin 2!");
            out.writeObject("LOGIN," + user.getUsername() + "," + user.getPassword());
            out.flush();
            if (sCli.isClosed())
                System.out.println("Fechado aqui EfetuaLogin 3!");


            resultado = (String) in.readObject();



            if (sCli.isClosed())
                System.out.println("Fechado aqui EfetuaLogin 4!");



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

            if(in.readObject()==null){
                System.out.println("cenas horriveis");
            }
            //resultado = (String) in.readObject();



        }catch(IOException|ClassNotFoundException e){
            e.printStackTrace();
        }
        return resultado;
    }


}
