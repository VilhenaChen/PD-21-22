package pd.tp.servidor.threads;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.TimerTask;

public class ThreadInformaPortoGRDS extends TimerTask {
    DatagramSocket ds;
    DatagramPacket dp;
    int porto, id;

    public ThreadInformaPortoGRDS(DatagramSocket ds, DatagramPacket dp, int porto, int id) {
        this.ds = ds;
        this.dp = dp;
        this.porto = porto;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            System.out.println("Estou a dar");
            String msg = "INFO_PORT," + id + "," + porto;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeUnshared(msg);
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
