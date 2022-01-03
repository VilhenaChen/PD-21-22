package pd.tp.cliente.threads;

import pd.tp.comum.Ficheiro;
import pd.tp.comum.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ThreadEnviaFicheiro extends Thread implements Utils {

    String ipServidor;
    String dir;
    Ficheiro ficheiro;
    String resultado;

    public ThreadEnviaFicheiro(String ipServidor, String dir, Ficheiro ficheiro, String resultado){
        this.ipServidor = ipServidor;
        this.dir = dir;
        this.ficheiro = ficheiro;
        this.resultado = resultado;
    }


    @Override
    public void run() {
        try {
            String [] array = resultado.split(",");
            Socket sCliFile = new Socket(ipServidor, Integer.parseInt(array[1]));

            OutputStream outSendFile = sCliFile.getOutputStream();
            String patch = dir + "\\" + ficheiro.getName();
            FileInputStream file = new FileInputStream(patch);
            while (file.available()!=0){
                byte [] bytesRead = new byte[4000];
                int nBytes = file.read(bytesRead);
                outSendFile.write(bytesRead,0,nBytes);
                outSendFile.flush();
                if(file.available()==0)
                    break;
            }
            outSendFile.close();
            sCliFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
