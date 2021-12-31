package pd.tp.cliente.threads;

import pd.tp.cliente.Utilizador;
import pd.tp.comum.AtualizacaoServidor;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ThreadRecebeInformacoesServidor extends Thread{

    ObjectInputStream in;
    String mensagemRecebida;
    Utilizador user;
    Object objeto;

    public ThreadRecebeInformacoesServidor(ObjectInputStream in, Utilizador user){
        this.in=in;
        this.user = user;
    }

    @Override
    public void run() {
        while(true){
            try {
                objeto = in.readObject();
                if(objeto instanceof String){
                    user.setResultadoComando((String) objeto);
                }
                else{
                    AtualizacaoServidor atualizacaoServidor = (AtualizacaoServidor) objeto;
                    System.out.println(atualizacaoServidor.toString());
                }
            } catch (IOException | ClassNotFoundException e) {
                break;
                //e.printStackTrace();
            }
        }
    }
}
