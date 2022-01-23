package pd.tp.grds.threads;

import pd.tp.grds.servidor.Servidor;
import pd.tp.grds.servidor.Servidores;
import pd.tp.rmi.GestaoRMI;

import java.util.ArrayList;
import java.util.TimerTask;

public class ThreadHeartbeatServidores extends TimerTask {
    Servidores servidores;
    GestaoRMI gestaoRMI;

    public ThreadHeartbeatServidores(Servidores servidores, GestaoRMI gestaoRMI) {
        this.servidores = servidores;
        this.gestaoRMI = gestaoRMI;
    }


    @Override
    public void run() {
        servidores.aumentaHeartbeat();
        servidores.removeServidoresInativos(gestaoRMI);
        /*if(!servidores.verificaVazio())
            System.out.println(servidores);*/
    }
}
