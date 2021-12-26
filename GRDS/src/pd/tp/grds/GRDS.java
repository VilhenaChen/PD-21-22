package pd.tp.grds;

import pd.tp.grds.servidor.Servidores;
import pd.tp.grds.threads.ThreadComunicacao;
import pd.tp.grds.threads.ThreadHeartbeatServidores;
import pd.tp.grds.threads.ThreadIniciaComunicacao;
import pd.tp.grds.threads.ThreadMulticastServidores;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.Scanner;
import java.util.Timer;

public class GRDS {
    private static final int PORTO = 9001;
    private ThreadMulticastServidores threadMulticastServidores;
    private Scanner scanner;
    private MulticastSocket ms;
    private InetSocketAddress isa;
    NetworkInterface ni;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        GRDS grds = new GRDS();
        Servidores servidores = new Servidores();
        DatagramSocket ds = new DatagramSocket(PORTO);
        System.out.println("---- GRDS INICIADO ----");
        ThreadHeartbeatServidores informaPortoThread = new ThreadHeartbeatServidores(servidores);
        Timer timer = new Timer("StartHeartbeat");
        timer.schedule(informaPortoThread, 0, 1000); //Mudar para 20secondos
        ThreadIniciaComunicacao threadIniciaComunicacao = new ThreadIniciaComunicacao(servidores,ds);
        threadIniciaComunicacao.start();
        grds.IniciaThreadMulticast();
        grds.scanner = new Scanner(System.in);
        while(true){
            System.out.println("Insira 'quit' para sair");
            String comando = grds.scanner.nextLine();
            if(comando.equals("quit")){
                break;
            }
        }
        try {
            threadIniciaComunicacao.join();
            grds.threadMulticastServidores.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        grds.ms.leaveGroup(grds.isa,grds.ni);

    }

    private void IniciaThreadMulticast(){
        try {
            ms = new MulticastSocket(3030);
            InetAddress ia = InetAddress.getByName("230.30.30.30");
            InetAddress myIa = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());
            isa = new InetSocketAddress(ia, 3030);
            ni = NetworkInterface.getByInetAddress(myIa);
            ms.joinGroup(isa,ni);
            threadMulticastServidores = new ThreadMulticastServidores(myIa.getHostAddress(),String.valueOf(PORTO),ms, ia);
            threadMulticastServidores.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
