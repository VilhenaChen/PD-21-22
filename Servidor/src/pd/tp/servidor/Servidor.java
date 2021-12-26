package pd.tp.servidor;

import pd.tp.servidor.bd.ComunicacaoBD;
import pd.tp.servidor.threads.ThreadComunicacaoCliente;
import pd.tp.servidor.threads.ThreadInformaPortoGRDS;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.Timer;

public class Servidor {

    private String IP_GRDS;
    private int PORTO_GRDS;
    private int ID_SERVIDOR;
    private DatagramSocket ds;
    private DatagramPacket dp;
    private ServerSocket ss;

    private void InicioGRDS() throws IOException, ClassNotFoundException {
        String msgTipo = "NOVO_SERV," + ss.getLocalPort();
        ds = new DatagramSocket();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeUnshared(msgTipo);
        out.flush();
        byte[] msgTipoBytes = baos.toByteArray();

        InetAddress ip = InetAddress.getByName(IP_GRDS);
        dp = new DatagramPacket(msgTipoBytes, msgTipoBytes.length, ip, PORTO_GRDS);
        ds.send(dp);

        //Reutilizar o DatagramPacket
        dp.setData(new byte[256]);
        dp.setLength(256);
        ds.receive(dp);

        ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
        ObjectInputStream in = new ObjectInputStream(bais);
        ID_SERVIDOR = (int) in.readObject();
        System.out.println("ID: " + ID_SERVIDOR);

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        Servidor servidor = new Servidor();

        if(args.length <= 0) {
            System.out.println("Falta de IP e Porto do GRDS por parametros");
            System.exit(1);
        }

        servidor.IP_GRDS = args[0];
        try {
            servidor.PORTO_GRDS = Integer.parseInt(args[1]);
        }catch (NumberFormatException e) {
            System.out.println("ERRO!!! O Porto passado não é um Inteiro");
            System.exit(1);
        }

        servidor.ss = new ServerSocket(0);
        System.out.println("Estou na porta: " + servidor.ss.getLocalPort());
        ComunicacaoBD comBD = new ComunicacaoBD();

        servidor.InicioGRDS();
        ThreadInformaPortoGRDS informaPortoThread = new ThreadInformaPortoGRDS(servidor.ds, servidor.dp, servidor.ss, servidor.ID_SERVIDOR);
        Timer timer = new Timer("InformaPorto");
        timer.schedule(informaPortoThread, 0, 20000); //Mudar para 20secondos

        while(true) {
            Socket sCli = servidor.ss.accept();
            ThreadComunicacaoCliente tc = new ThreadComunicacaoCliente(sCli, comBD);
            tc.start();
        }

    }
}
