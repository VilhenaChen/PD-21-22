package pd.tp.cliente.threads;

import pd.tp.cliente.Utilizador;
import pd.tp.comum.AtualizacaoServidor;
import pd.tp.comum.Utils;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ThreadRecebeInformacoesServidor extends Thread implements Utils {

    ObjectInputStream in;
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
                    if(objeto.equals(DESLIGA_SERVIDOR)) {
                        System.out.println("O Servidor vai encerrar!!!!");
                        break;
                    }
                    user.setResultadoComando((String) objeto);
                    user.setRecebiResultado(true);
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
