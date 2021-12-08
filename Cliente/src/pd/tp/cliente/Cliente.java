package pd.tp.cliente;

import pd.tp.cliente.ui.UiTexto;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Cliente {
    private Scanner scanner;
    private String username;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if(args.length <= 0) {
            System.out.println("Falta de IP e Porto do GRDS por parametros");
            System.exit(1);
        }
        String msgEnviada = "NOVO_CLI";
        DatagramSocket ds = new DatagramSocket();
        //ds.setSoTimeout(5000);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeUnshared(msgEnviada);
        out.flush();
        byte[] msgEnBytes = baos.toByteArray();

        //InetAddress ip = InetAddress.getByName("127.0.0.1");
        InetAddress ip = InetAddress.getByName(args[0]);
        DatagramPacket dp = new DatagramPacket(msgEnBytes, msgEnBytes.length, ip, Integer.parseInt(args[1]));
        ds.send(dp);

        dp.setData(new byte[256]);
        dp.setLength(256);
        ds.receive(dp);

        ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
        ObjectInputStream in = new ObjectInputStream(bais);
        String msgRecebida = (String) in.readObject();
        System.out.println(msgRecebida);

        String IP_GRDS = args[0];
        String PORTO_GRDS = args[1];
        System.out.println("IP = " + IP_GRDS + " PORTO = " + PORTO_GRDS);
        UiTexto ui = new UiTexto();
        ui.run();
        System.exit(0);
    }
}
