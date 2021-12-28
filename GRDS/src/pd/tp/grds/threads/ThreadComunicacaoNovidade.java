package pd.tp.grds.threads;

import pd.tp.comum.NovidadeGRDS;
import pd.tp.comum.Utils;
import pd.tp.grds.servidor.Servidores;
import java.net.DatagramSocket;

public class ThreadComunicacaoNovidade extends Thread implements Utils {

    DatagramSocket ds;
    Servidores servidores;
    NovidadeGRDS novidadeGRDS;

    public ThreadComunicacaoNovidade(DatagramSocket ds, Servidores servidores, NovidadeGRDS novidadeGRDS){
        this.ds = ds;
        this.servidores = servidores;
        this.novidadeGRDS = novidadeGRDS;
    }

    @Override
    public void run() {
        servidores.enviaNovidadeAosServidores(ds, novidadeGRDS);
    }
}
