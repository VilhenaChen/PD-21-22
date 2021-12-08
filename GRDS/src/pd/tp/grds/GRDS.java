package pd.tp.grds;

import pd.tp.grds.threads.ThreadClientes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class GRDS {

    public static void main(String[] args) throws IOException {
        DatagramSocket ds = new DatagramSocket(9001);

        while(true) {
            DatagramPacket dp = new DatagramPacket(new byte[256], 256);
            ds.receive(dp);
            ThreadClientes tc = new ThreadClientes(dp, ds);
            tc.start();
        }

    }
}
