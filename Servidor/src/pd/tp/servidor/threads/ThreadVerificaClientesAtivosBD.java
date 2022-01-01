package pd.tp.servidor.threads;

import pd.tp.comum.NovidadeGRDS;
import pd.tp.comum.Utils;
import pd.tp.servidor.bd.ComunicacaoBD;

import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TimerTask;

public class ThreadVerificaClientesAtivosBD extends TimerTask implements Utils {

    private ComunicacaoBD comBD;
    private NovidadeGRDS novidadeGRDS;
    private DatagramSocket ds;
    private DatagramPacket dp;
    private int id;


    public ThreadVerificaClientesAtivosBD(ComunicacaoBD comBD, DatagramPacket dp, DatagramSocket ds, int id){
        this.comBD = comBD;
        this.dp = dp;
        this.ds = ds;
        this.id = id;
    }
    @Override
    public void run() {
        novidadeGRDS = new NovidadeGRDS();
        ArrayList<String> usersInativos;
        try {
            usersInativos = comBD.verificaUsersInativos();
            if(!usersInativos.isEmpty()) {
                for (String userInativo : usersInativos) {
                    novidadeGRDS.addUserAfetado(userInativo);
                }
                novidadeGRDS.setTipoMsg(UTILIZADORES_INATIVOS);
                ThreadEnviaAtualizacaoGRDS threadEnviaAtualizacaoGRDS = new ThreadEnviaAtualizacaoGRDS(ds, dp, id, novidadeGRDS);
                threadEnviaAtualizacaoGRDS.start();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
