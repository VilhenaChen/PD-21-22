package pd.tp.servidor.threads;

import pd.tp.cliente.Clientes;
import pd.tp.cliente.Ficheiros;
import pd.tp.servidor.bd.ComunicacaoBD;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class ThreadIniciaComunicacaoClientes extends Thread{

    private ServerSocket ss;
    private ComunicacaoBD comBD;
    private DatagramSocket ds;
    private DatagramPacket dp;
    private int ID_SERVIDOR;
    private Clientes clientes;
    private ArrayList<ThreadComunicacaoCliente> threads;
    private Ficheiros ficheiros;

    public ThreadIniciaComunicacaoClientes(ServerSocket ss, ComunicacaoBD comBD, DatagramSocket ds, DatagramPacket dp, int ID_SERVIDOR, Clientes clientes, Ficheiros ficheiros) {
        this.ss = ss;
        this.comBD = comBD;
        this.ds = ds;
        this.dp = dp;
        this.ID_SERVIDOR = ID_SERVIDOR;
        this.clientes = clientes;
        this.threads = new ArrayList<>();
        this.ficheiros = ficheiros;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Socket sCli = ss.accept();
                ThreadComunicacaoCliente tc = new ThreadComunicacaoCliente(sCli, comBD, ds, dp, ID_SERVIDOR, clientes,ficheiros);
                threads.add(tc);
                tc.start();
            }catch (IOException e) {
                return;
            }
        }
    }

    @Override
    public void interrupt() {
        for(ThreadComunicacaoCliente t : threads) {
            t.interrupt();
        }
        super.interrupt();
    }
}
