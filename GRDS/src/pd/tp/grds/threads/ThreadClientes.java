package pd.tp.grds.threads;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ThreadClientes extends Thread {

    DatagramPacket dp;
    DatagramSocket ds;

    public ThreadClientes(DatagramPacket dp, DatagramSocket ds) {
        this.dp = dp;
        this.ds = ds;
    }

    @Override
    public void run() {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
            ObjectInputStream in = new ObjectInputStream(bais);
            String msgRecebida = (String) in.readObject();

            if(msgRecebida.startsWith("NOVO_CLI")) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(baos);
                String IP_SERVER = new String("192.168.1.1");
                out.writeUnshared(IP_SERVER);
                out.flush();
                byte[] msgBytes = baos.toByteArray();

                dp.setData(msgBytes);
                dp.setLength(msgBytes.length);
                ds.send(dp);

            }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
    }
}
