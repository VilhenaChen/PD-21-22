package pd.tp.servidor;

import pd.tp.cliente.Clientes;
import pd.tp.comum.Utils;
import pd.tp.servidor.bd.ComunicacaoBD;
import pd.tp.servidor.threads.ThreadComunicacaoCliente;
import pd.tp.servidor.threads.ThreadInformaPortoGRDS;
import pd.tp.servidor.threads.ThreadRecebeAtualizacoesGRDS;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.Timer;

public class Servidor implements Utils {

    private String IP_GRDS;
    private int PORTO_GRDS;
    private int ID_SERVIDOR;
    private DatagramSocket ds;
    private DatagramPacket dp;
    private ServerSocket ss;
    private Clientes clientes;

    private void InicioGRDS() throws IOException, ClassNotFoundException {
        String msgTipo = NOVO_SERV + "," + ss.getLocalPort();
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

    private void recebeIPePortoPorMulticast() throws UnknownHostException {
        int cont = 0;
        try{
            MulticastSocket ms = new MulticastSocket(3030);
            InetAddress ia = InetAddress.getByName("230.30.30.30");
            InetAddress myIa = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());
            InetSocketAddress isa = new InetSocketAddress(ia,3030);
            NetworkInterface ni = NetworkInterface.getByInetAddress(myIa);
            ms.joinGroup(isa,ni);

            while(cont<3 && cont != -1) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(baos);
                out.writeUnshared(PEDIDO_COORDENADAS);
                out.flush();
                byte[] msgBytes = baos.toByteArray();
                dp = new DatagramPacket(msgBytes, msgBytes.length, ia, 3030);
                ms.send(dp);
                String mensagemRecebida = "";
                ms.setSoTimeout(5000);
                try{
                    do {
                        DatagramPacket dp = new DatagramPacket(new byte[1024], 1024);
                        ms.receive(dp);
                        ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
                        ObjectInputStream in = new ObjectInputStream(bais);
                        mensagemRecebida = (String) in.readObject();
                        System.out.println("Recebi " + mensagemRecebida);

                        if (mensagemRecebida.startsWith("COORDENADAS")) {
                            String[] array = mensagemRecebida.split(",");
                            IP_GRDS = array[1];
                            PORTO_GRDS = Integer.parseInt(array[2]);
                            cont = -1;
                        }
                    } while (!mensagemRecebida.startsWith("COORDENADAS"));
                }catch (SocketTimeoutException e){
                    cont++;
                    continue;
                }
            }
            System.out.println("A sair do grupo");
            ms.leaveGroup(isa,ni);
            if (cont==3) {
                System.out.println("Erro! Não foi possível obter o IP e o Porto do GRDS!");
                System.exit(1);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        Servidor servidor = new Servidor();

        if(args.length <= 0) {
            System.out.println("Falta de IP e Porto do GRDS por parametros! A Procurar por multicast");
            servidor.recebeIPePortoPorMulticast();
        }
        else {
            servidor.IP_GRDS = args[0];
            try {
                servidor.PORTO_GRDS = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("ERRO!!! O Porto passado não é um Inteiro");
                System.exit(1);
            }
        }
        System.out.println("---- SERVIDOR INICIADO ----");
        servidor.ss = new ServerSocket(0);
        System.out.println("Estou na porta: " + servidor.ss.getLocalPort());
        ComunicacaoBD comBD = new ComunicacaoBD();

        servidor.InicioGRDS();
        ThreadInformaPortoGRDS informaPortoThread = new ThreadInformaPortoGRDS(servidor.ds, servidor.dp, servidor.ss, servidor.ID_SERVIDOR);
        Timer timer = new Timer("InformaPorto");
        timer.schedule(informaPortoThread, 0, 20000); //Mudar para 20secondos
        servidor.clientes = new Clientes();
        ThreadRecebeAtualizacoesGRDS threadRecebeAtualizacoesGRDS = new ThreadRecebeAtualizacoesGRDS(servidor.ds,servidor.clientes);
        threadRecebeAtualizacoesGRDS.start();
        while(true) {
            Socket sCli = servidor.ss.accept();
            ThreadComunicacaoCliente tc = new ThreadComunicacaoCliente(sCli, comBD, servidor.ds, servidor.dp, servidor.ID_SERVIDOR, servidor.clientes);
            tc.start();
        }

    }
}
