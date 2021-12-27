package pd.tp.grds.threads;

import java.io.*;
import java.net.*;

public class ThreadMulticastServidores extends Thread{
    private String ip;
    private String porto;
    MulticastSocket ms;
    InetAddress ia;
    public ThreadMulticastServidores(String ip, String porto, MulticastSocket ms, InetAddress ia){
        this.ip = ip;
        this.porto = porto;
        this.ms = ms;
        this.ia = ia;
    }

    @Override
    public void run() {
        StringBuilder stringBuilder = new StringBuilder();
        while (true){
            try {
                DatagramPacket dp = new DatagramPacket(new byte[1024], 1024);
                ms.receive(dp);
                ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
                ObjectInputStream in = new ObjectInputStream(bais);
                String mensagemRecebida = (String) in.readObject();

                if (mensagemRecebida.equals("PEDIDO_COORDENADAS")) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(baos);
                    stringBuilder.append("COORDENADAS,").append(ip).append(",").append(porto);
                    out.writeUnshared(stringBuilder.toString());
                    out.flush();
                    byte[] msgBytes = baos.toByteArray();
                    dp = new DatagramPacket(msgBytes,msgBytes.length,ia, 3030);
                    /*try {
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/ // Para verificar se as 3 tentativas do servidor funcionam
                    ms.send(dp);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
