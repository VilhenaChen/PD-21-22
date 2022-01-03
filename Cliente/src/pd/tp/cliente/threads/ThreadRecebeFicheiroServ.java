package pd.tp.cliente.threads;

import pd.tp.comum.Utils;
import java.io.*;
import java.net.Socket;

public class ThreadRecebeFicheiroServ extends Thread implements Utils {
    Socket sCli;
    String username;
    String nameFile;

    public ThreadRecebeFicheiroServ(String ipServidor, String resultado, String username){
        String[] array = resultado.split(",");
        try {
            sCli = new Socket(ipServidor,Integer.parseInt(array[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.username = username;
        this.nameFile = array[2];
    }


    @Override
    public void run() {
        try{
            InputStream in = sCli.getInputStream();
            String patch = "C:\\Ficheiros_Cliente_" + username;
            File diretorio = new File(patch);
            diretorio.mkdirs();
            patch = patch + "\\" + nameFile;
            FileOutputStream ficheiro = new FileOutputStream(patch);

            while (true){
                byte[] leitura = new byte[MAX_TAM_DATA_FILE];
                int nBytes = in.read(leitura);
                if (nBytes==-1){
                    break;
                }
                ficheiro.write(leitura,0,nBytes);
            }
            System.out.println("Recebi o Ficheiro " + nameFile + " que está na pasta: " + patch);
            in.close();
            sCli.close();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
