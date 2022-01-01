package pd.tp.grds.threads;

import pd.tp.comum.NovidadeGRDS;
import pd.tp.comum.Utils;
import pd.tp.grds.servidor.Servidores;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ThreadIniciaComunicacao extends Thread implements Utils {
    Servidores servidores;
    DatagramSocket ds;
    public ThreadIniciaComunicacao(Servidores servidores, DatagramSocket ds){
        this.servidores = servidores;
        this.ds = ds;
    }
    @Override
    public void run() {
        while(true) {
            try{
                DatagramPacket dp = new DatagramPacket(new byte[1024], 1024);
                ds.receive(dp);
                ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
                ObjectInputStream in = new ObjectInputStream(bais);
                Object objeto = in.readObject();
                if(objeto instanceof String){
                    String tipo = (String) objeto;
                    if(tipo.startsWith(NOVO_CLI)){
                        do{
                            servidores.getNovoindiceUltimoServidorAtribuido();
                        }while(!servidores.verificaServidorAtivoParaAtribuir(servidores.getIndiceUltimoServidorAtribuido()));
                    }
                    ThreadComunicacao tc = new ThreadComunicacao(dp,ds,servidores, tipo);
                    tc.start();
                }
                else {
                    NovidadeGRDS novidadeGRDS = (NovidadeGRDS) objeto;
                    ThreadComunicacaoNovidade tcn = new ThreadComunicacaoNovidade(ds,servidores,novidadeGRDS);
                    tcn.start();
                }

                in.close();
                bais.close();
            } catch (IOException | ClassNotFoundException e) {
                return;
            }
        }
    }
}
