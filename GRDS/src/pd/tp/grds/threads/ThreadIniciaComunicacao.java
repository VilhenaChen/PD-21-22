package pd.tp.grds.threads;

import pd.tp.grds.servidor.Servidores;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ThreadIniciaComunicacao extends Thread{
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
                DatagramPacket dp = new DatagramPacket(new byte[256], 256);
                ds.receive(dp);
                ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
                ObjectInputStream in = null;
                in = new ObjectInputStream(bais);
                String tipo = (String) in.readObject();
                if(tipo.startsWith("NOVO_CLI")){
                    do{
                        servidores.getNovoindiceUltimoServidorAtribuido();
                    }while(servidores.verificaServidorAtivoParaAtribuir(servidores.getIndiceUltimoServidorAtribuido()));
                }
                ThreadComunicacao tc = new ThreadComunicacao(dp,ds,servidores, tipo);
                tc.start();

                in.close();
                bais.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
