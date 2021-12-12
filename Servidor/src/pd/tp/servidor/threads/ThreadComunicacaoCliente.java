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
        for(String a : array) {
            System.out.println(a);
        }
    }

    @Override
    public void run() {
        try {
            dadosUser = new HashMap<>();
            ObjectInputStream in = new ObjectInputStream(sCli.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(sCli.getOutputStream());

            String msgRecebida = (String) in.readObject();

            if (msgRecebida.startsWith("LOGIN")) {
                //Ligacao a BD
                System.out.println(msgRecebida);
                separaDadosUser(msgRecebida);
                String login = comBD.loginUser(dadosUser.get("username"), dadosUser.get("password"));
                out.writeObject(login);
                out.flush();

            } else if (msgRecebida.startsWith("REGISTO")) {
                //Ligacao a BD
                separaDadosUser(msgRecebida);
                comBD.insereUser(dadosUser.get("name"), dadosUser.get("username"), dadosUser.get("password"), 0);
                System.out.println(msgRecebida);
                out.writeUnshared(SUCESSO);
                out.flush();
            }


            out.close();
            in.close();

        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();

        }
    }
}
