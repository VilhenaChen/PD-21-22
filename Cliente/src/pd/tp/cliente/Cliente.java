package pd.tp.cliente;

import pd.tp.cliente.ui.UiTexto;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

public class Cliente {
    private String IP_GRDS;
    private int PORTO_GRDS;
    private HashMap<String, String> infoServidor;

    private void pedeIPGRDS() throws IOException, ClassNotFoundException {
        String msgEnviada = "NOVO_CLI";
        DatagramSocket ds = new DatagramSocket();
        //ds.setSoTimeout(5000);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeUnshared(msgEnviada);
        out.flush();
        byte[] msgEnBytes = baos.toByteArray();

        InetAddress ip = InetAddress.getByName(IP_GRDS);
        DatagramPacket dp = new DatagramPacket(msgEnBytes, msgEnBytes.length, ip, PORTO_GRDS);
        ds.send(dp);

        //Reutilizar o DatagramPacket
        dp.setData(new byte[256]);
        dp.setLength(256);
        ds.receive(dp);

        ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
        ObjectInputStream in = new ObjectInputStream(bais);
        infoServidor = (HashMap<String,String>) in.readObject();
        System.out.println(infoServidor.get("IP"));

        ds.close();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Cliente cli = new Cliente();
        cli.infoServidor = new HashMap<>();
        if(args.length <= 0) {
            System.out.println("Falta de IP e Porto do GRDS por parametros");
            System.exit(1);
        }

        cli.IP_GRDS = args[0];
        try {
            cli.PORTO_GRDS = Integer.parseInt(args[1]);
        }catch (NumberFormatException e) {
            System.out.println("ERRO!!! O Porto passado não é um Inteiro");
            System.exit(1);
        }
        System.out.println("IP = " + cli.IP_GRDS + " PORTO = " + cli.PORTO_GRDS);

        cli.pedeIPGRDS();

        Socket sCli = new Socket(cli.infoServidor.get("IP"), Integer.parseInt(cli.infoServidor.get("PORTO")));

        UiTexto ui = new UiTexto(sCli);
        ui.run();
        System.exit(0);
    }
}
