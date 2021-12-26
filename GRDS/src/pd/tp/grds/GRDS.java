package pd.tp.grds;

import pd.tp.grds.servidor.Servidor;
import pd.tp.grds.servidor.Servidores;
import pd.tp.grds.threads.ThreadComunicacao;
import pd.tp.grds.threads.ThreadHeartbeatServidores;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Timer;

public class GRDS {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        GRDS grds = new GRDS();
        Servidores servidores = new Servidores();
        DatagramSocket ds = new DatagramSocket(9001);
        System.out.println("---- GRDS INICIADO ----");
        ThreadHeartbeatServidores informaPortoThread = new ThreadHeartbeatServidores(servidores);
        Timer timer = new Timer("StartHeartbeat");
        timer.schedule(informaPortoThread, 0, 1000); //Mudar para 20secondos
        while(true) {
            DatagramPacket dp = new DatagramPacket(new byte[256], 256);
            ds.receive(dp);
            ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
            ObjectInputStream in = new ObjectInputStream(bais);
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
        }

    }
}
