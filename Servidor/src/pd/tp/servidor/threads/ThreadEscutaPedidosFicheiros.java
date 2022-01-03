package pd.tp.servidor.threads;

import pd.tp.cliente.Clientes;
import pd.tp.cliente.Ficheiros;
import pd.tp.comum.Ficheiro;
import pd.tp.servidor.bd.ComunicacaoBD;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ThreadEscutaPedidosFicheiros extends Thread{

    private int ID_SERVIDOR;
    ServerSocket ss;
    Ficheiros ficheiros;

    public ThreadEscutaPedidosFicheiros(ServerSocket ss, int ID_SERVIDOR, Ficheiros ficheiros) {
        this.ID_SERVIDOR = ID_SERVIDOR;
        this.ss = ss;
        this.ficheiros = ficheiros;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Socket sServ = ss.accept();
                ThreadEnviaFicheiroServ threadEnviaFicheiroServ = new ThreadEnviaFicheiroServ(sServ, ID_SERVIDOR, ficheiros);
                threadEnviaFicheiroServ.start();
            }catch (IOException e) {
                return;
            }
        }
    }

    @Override
    public void interrupt() {

        super.interrupt();
    }
}
