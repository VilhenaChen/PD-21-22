package pd.tp.servidor.threads;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.TimerTask;

public class ThreadInformaPortoGRDS extends TimerTask {
    DatagramSocket ds;
    DatagramPacket dp;
    ServerSocket ss;
    int id;

    public ThreadInformaPortoGRDS(DatagramSocket ds, DatagramPacket dp, ServerSocket ss, int id) {
        this.ds = ds;
        this.dp = dp;
        this.ss = ss;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            System.out.println("Informei o GRDS do Porto " + ss.getLocalPort());
            String msg = "INFO_PORT," + id + "," + ss.getLocalPort();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeUnshared(msg);
            out.flush();
            byte[] msgBytes = baos.toByteArray();

            synchronized (dp){
                dp.setData(msgBytes);
                dp.setLength(msgBytes.length);
                ds.send(dp);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
