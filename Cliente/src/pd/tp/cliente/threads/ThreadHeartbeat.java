package pd.tp.cliente.threads;

import pd.tp.cliente.comunicacao.ComunicacaoServidor;

import java.util.TimerTask;

public class ThreadHeartbeat extends TimerTask {

    ComunicacaoServidor cs;

    public ThreadHeartbeat(ComunicacaoServidor cs){
        this.cs = cs;
    }

    @Override
    public void run() {
        cs.heartbeat();
    }
}
