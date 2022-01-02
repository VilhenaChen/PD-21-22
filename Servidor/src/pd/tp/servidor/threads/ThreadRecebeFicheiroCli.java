package pd.tp.servidor.threads;

import pd.tp.comum.Ficheiro;
import pd.tp.comum.NovidadeGRDS;
import pd.tp.comum.Utils;
import pd.tp.servidor.bd.ComunicacaoBD;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class ThreadRecebeFicheiroCli extends Thread implements Utils {

    Socket sCliFile;
    ObjectOutputStream out;
    Ficheiro ficheiro;
    DatagramSocket ds;
    DatagramPacket dp;
    int id;
    ComunicacaoBD comBD;
    ServerSocket ss;
    public ThreadRecebeFicheiroCli(ObjectOutputStream out, Ficheiro ficheiro, DatagramSocket ds, DatagramPacket dp, int id, ComunicacaoBD comBD) {
        try {
            this.ss = new ServerSocket(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.out = out;
        this.ficheiro = ficheiro;
        this.ds = ds;
        this.dp = dp;
        this.id = id;

        this.comBD = comBD;

    }

    @Override
    public void run() {
        try{
            NovidadeGRDS novidade = new NovidadeGRDS();
            novidade.setTipoMsg(FICHEIRO);
            novidade.setUsernameUser(ficheiro.getSender());
            novidade.setReceiver(ficheiro.getReceiver());
            novidade.setIpFicheiro("IPFICHEIRO");
            novidade.setPortoFicheiro(1998);
            comBD.verificaAfetadosFicheiro(ficheiro.getSender(),ficheiro.getReceiver(),novidade);
            String resultado = comBD.recebeFicheiro(ficheiro, novidade);

            if(resultado.equals(SUCESSO)){
                resultado = resultado + "," + ss.getLocalPort();
            }
            synchronized (out){
                out.writeUnshared(resultado);
                out.flush();
            }
            this.sCliFile = ss.accept();


            if(!resultado.startsWith(SUCESSO)){
                return;
            }

            String patch = "C:\\FicheirosServidor_" + id;
            File diretorio = new File(patch);
            diretorio.mkdirs();
            FileOutputStream file = new FileOutputStream(patch + "\\" + ficheiro.getName());
            InputStream in = sCliFile.getInputStream();
            while (true) {
                byte[] leitura = new byte[MAX_TAM_DATA_FILE];
                int nBytes = in.read(leitura);
                if (nBytes == -1) {
                    break;
                }
                file.write(leitura, 0, nBytes);
            }
            in.close();
            sCliFile.close();

            System.out.println("O Utilizador " + ficheiro.getSender() + " enviou um ficheiro para o destinatario " + ficheiro.getReceiver());
            ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds,dp,id,novidade);
            threadEnviaAtualizacaoGRDS.start();

        }catch (IOException | SQLException e){
            e.printStackTrace();
        }
    }
}
