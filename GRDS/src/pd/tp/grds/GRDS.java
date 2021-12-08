package pd.tp.grds;

import pd.tp.grds.servidor.Servidor;
import pd.tp.grds.threads.ThreadComunicacao;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class GRDS {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DatagramSocket ds = new DatagramSocket(9001);
        System.out.println("---- GRDS INICIADO ----");
        ArrayList<Servidor> servidores = new ArrayList<>();
        int indiceUltimoServidorAtribuido = -1;
        while(true) {
            DatagramPacket dp = new DatagramPacket(new byte[256], 256);
            ds.receive(dp);
            ThreadComunicacao tc = new ThreadComunicacao(dp,ds,servidores,indiceUltimoServidorAtribuido);
            tc.start();
        }

    }
}
