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
            atualizacoesServidor = clientes.getNovidadesCli(getUsername());
            if (atualizacoesServidor != null) {
                for (AtualizacaoServidor at : atualizacoesServidor) {
                    if(at.isEnviada()){
                        try {
                            synchronized (out){
                                out.writeUnshared(at);
                                out.flush();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            clientes.removeNovidadesCli(getUsername());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    private String getUsername(){
        synchronized (this.username){
            return this.username;
        }
    }

    public void setUsername(String username) {
        synchronized (this.username) {
            this.username = username;
        }
    }
}
