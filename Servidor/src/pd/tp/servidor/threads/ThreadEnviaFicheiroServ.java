package pd.tp.servidor.threads;

import pd.tp.cliente.Ficheiros;
import pd.tp.comum.Ficheiro;
import pd.tp.comum.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ThreadEnviaFicheiroServ extends Thread implements Utils {

    int idServ;
    String dir;
    Ficheiros ficheiros;
    String resultado;
    Socket sServ;

    public ThreadEnviaFicheiroServ(Socket sServ, int idServ, Ficheiros ficheiros){
        this.sServ = sServ;
        this.idServ = idServ;
        this.ficheiros = ficheiros;
    }


    @Override
    public void run() {
        try {


            InputStream in = sServ.getInputStream();
            OutputStream out = sServ.getOutputStream();

            byte[] bytesRead = new byte[256];
            int nBytes = in.read(bytesRead);
            String msgReceived = new String(bytesRead,0,nBytes);
            int idFile = Integer.parseInt(msgReceived);

            if(!ficheiros.verificaExistenciaFicheiro(idFile)){
                out.close();
                in.close();
                sServ.close();
            }
            else {
                String nameFile = ficheiros.getNomeFicheiro(idFile);
                String patch = "C:\\Ficheiros_Servidor_" + idServ + "\\" + nameFile;
                FileInputStream ficheiro = new FileInputStream(patch);
                while (ficheiro.available()!=0){
                    bytesRead = new byte[MAX_TAM_DATA_FILE];
                    nBytes = ficheiro.read(bytesRead);
                    out.write(bytesRead,0,nBytes);
                    out.flush();
                    if(ficheiro.available()==0)
                        break;
                }
                System.out.println("Ficheiro enviado!");
                out.close();
                in.close();
                sServ.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
