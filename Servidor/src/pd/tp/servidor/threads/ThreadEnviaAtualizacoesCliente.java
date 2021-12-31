package pd.tp.servidor.threads;

import pd.tp.cliente.Clientes;
import pd.tp.comum.AtualizacaoServidor;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ThreadEnviaAtualizacoesCliente extends Thread{
    ObjectOutputStream out;
    Clientes clientes;
    ArrayList<AtualizacaoServidor> atualizacoesServidor;
    String username;

    public ThreadEnviaAtualizacoesCliente(ObjectOutputStream out, Clientes clientes, String username){
        this.out = out;
        this.clientes = clientes;
        this.username = username;
    }

    @Override
    public void run() {
        while (true){
            atualizacoesServidor = clientes.getNovidadesCli(username);
            if(atualizacoesServidor == null){
                for (AtualizacaoServidor at : atualizacoesServidor){
                    try {
                        out.writeUnshared(at);
                        out.flush();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            clientes.removeNovidadesCli(username);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
