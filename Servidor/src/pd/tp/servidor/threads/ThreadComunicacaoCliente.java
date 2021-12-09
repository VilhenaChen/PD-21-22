package pd.tp.servidor.threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ThreadComunicacaoCliente extends Thread{
    private Socket sCli;
    private static final String SUCESSO = "SUCESSO";
    private static final String PASSWORD_ERRADA = "PASSWORD_ERRADA";
    private static final String UTILIZADOR_INEXISTENTE = "UTILIZADOR_INEXISTENTE";
    private static final String NOME_REPETIDO = "NOME_REPETIDO";
    private static final String USERNAME_REPETIDO = "USERNAME_REPETIDO";

    public ThreadComunicacaoCliente(Socket sCli) {
        this.sCli = sCli;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(sCli.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(sCli.getInputStream());
            String msgRecebida = (String) in.readObject();

            if(msgRecebida.startsWith("LOGIN")) {
                //Ligacao a BD
                System.out.println(msgRecebida);
                out.writeUnshared(SUCESSO);

            } else if(msgRecebida.startsWith("REGISTO")) {
                //Ligacao a BD
                System.out.println(msgRecebida);
                out.writeUnshared(SUCESSO);
            }

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
