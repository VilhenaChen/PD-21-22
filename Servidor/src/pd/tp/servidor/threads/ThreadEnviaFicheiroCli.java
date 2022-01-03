package pd.tp.servidor.threads;

import pd.tp.cliente.Ficheiros;
import pd.tp.comum.Utils;
import pd.tp.servidor.bd.ComunicacaoBD;
import java.io.*;
import java.net.*;
import java.sql.SQLException;

public class ThreadEnviaFicheiroCli extends Thread implements Utils {
    Socket sCliFile;
    ObjectOutputStream out;
    int id;
    ComunicacaoBD comBD;
    ServerSocket ss;
    Ficheiros ficheiros;
    int idFile;
    String username;

    public ThreadEnviaFicheiroCli(ObjectOutputStream out, int idServ, int idFile, ComunicacaoBD comBD, Ficheiros ficheiros, String username) {
        this.out = out;
        this.id = idServ;
        this.comBD = comBD;
        try {
            this.ss = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.ficheiros = ficheiros;
        this.idFile = idFile;
        this.username = username;

    }

    @Override
    public void run() {
        try{
            if(!ficheiros.verificaExistenciaFicheiro(idFile)){
                synchronized (out) {
                    out.writeUnshared(FICHEIRO_INEXISTENTE);
                    out.flush();
                }
            }
            if(!comBD.verificaSenderOrReceiverFile(idFile, username)) {
                synchronized (out) {
                    out.writeUnshared(NOT_YOUR_FILE);
                    out.flush();
                }
            }
            else {
                String nameFile = ficheiros.getNomeFicheiro(idFile);
                synchronized (out) {
                    out.writeUnshared(SUCESSO + "," + ss.getLocalPort() + "," + nameFile);
                    out.flush();
                }
                this.sCliFile = ss.accept();
                comBD.atualizaVisualizacaoFicheiro(idFile);

                OutputStream outFile = sCliFile.getOutputStream();

                String patch = "C:\\Ficheiros_Servidor_" + id + "\\" + nameFile;
                FileInputStream ficheiro = new FileInputStream(patch);
                byte[] bytesRead;
                int nBytes;
                while (ficheiro.available()!=0){
                    bytesRead = new byte[MAX_TAM_DATA_FILE];
                    nBytes = ficheiro.read(bytesRead);
                    outFile.write(bytesRead,0,nBytes);
                    outFile.flush();
                    if(ficheiro.available()==0)
                        break;
                }
                System.out.println("Ficheiro enviado para o cliente " + username);
                outFile.close();
                sCliFile.close();

            }

        }catch (IOException | SQLException e){
            e.printStackTrace();
        }
    }
}
