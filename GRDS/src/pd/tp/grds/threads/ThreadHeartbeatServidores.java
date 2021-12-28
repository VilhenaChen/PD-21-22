package pd.tp.grds.threads;

import pd.tp.grds.servidor.Servidor;
import pd.tp.grds.servidor.Servidores;

import java.util.ArrayList;
import java.util.TimerTask;

public class ThreadHeartbeatServidores extends TimerTask {
    Servidores servidores;

    public ThreadHeartbeatServidores(Servidores servidores) {
        this.servidores = servidores;;
    }


    @Override
    public void run() {
        servidores.aumentaHeartbeat();
        servidores.removeServidoresInativos();
        /*if(!servidores.verificaVazio())
            System.out.println(servidores);*/
    }
}
