package pd.tp.servidor.threads;


import pd.tp.cliente.Ficheiros;
import pd.tp.comum.Ficheiro;
import pd.tp.comum.Utils;
import pd.tp.servidor.bd.ComunicacaoBD;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class ThreadRecebeFicheiroServ extends Thread implements Utils {

    Socket sServ;
    int idFicheiro;
    Ficheiros ficheiros;
    String nameFile;
    int idServ;

    public ThreadRecebeFicheiroServ(String ipServ, int porto, int idFicheiro, String nameFile, Ficheiros ficheiros, int idServ) {
        try {
            sServ = new Socket(ipServ, porto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.idFicheiro = idFicheiro;
        this.nameFile = nameFile;
        this.ficheiros = ficheiros;
        this.idServ = idServ;

    }

    @Override
    public void run() {
        try{
            InputStream in = sServ.getInputStream();
            OutputStream out = sServ.getOutputStream();

            byte[] idFile = String.valueOf(idFicheiro).getBytes();
            out.write(idFile);
            out.flush();


            String patch = "C:\\Ficheiros_Servidor_" + idServ;
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
            System.out.println("Recebi Ficheiro!");
            out.close();
            in.close();
            sServ.close();
            ficheiros.addFicheiro(new Ficheiro(idFicheiro,nameFile));

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
