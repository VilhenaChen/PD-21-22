package pd.tp.servidor.threads;

import pd.tp.comum.NovidadeGRDS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ThreadEnviaAtualizacaoGRDS extends Thread{
    private DatagramSocket ds;
    private DatagramPacket dp;
    private int id;
    private NovidadeGRDS novidadeGRDS;


    public ThreadEnviaAtualizacaoGRDS(DatagramSocket ds, DatagramPacket dp, int id, NovidadeGRDS novidadeGRDS){
        this.ds = ds;
        this.dp = dp;
        this.id = id;
        this.novidadeGRDS = novidadeGRDS;
    }

    @Override
    public void run() {
        try {
            System.out.println("Informei o GRDS da novidade ");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            novidadeGRDS.setIdServidor(id);
            out.writeUnshared(novidadeGRDS);
            out.flush();
            byte[] msgBytes = baos.toByteArray();

            dp.setData(msgBytes);
            dp.setLength(msgBytes.length);
            ds.send(dp);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
